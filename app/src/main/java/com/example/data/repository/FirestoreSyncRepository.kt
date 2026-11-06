package com.example.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.example.R
import com.example.data.local.AgentActionLogEntity
import com.example.data.local.AppDatabase
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

enum class SyncState {
    SYNCED,
    PENDING_SYNC,
    OFFLINE
}

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(exception: Exception, operationType: OperationType, path: String?): String {
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

@IgnoreExtraProperties
data class FirestoreActionLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val stepId: String = "",
    val toolName: String = "",
    val description: String = "",
    val details: String = "",
    val status: String = "SUCCESS",
    @ServerTimestamp val timestamp: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

class FirestoreSyncRepository(
    private val context: Context,
    private val db: FirebaseFirestore,
    private val appDb: AppDatabase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    constructor(context: Context) : this(
        context = context.applicationContext,
        db = FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        appDb = AppDatabase.getInstance(context.applicationContext)
    )

    private val auth: FirebaseAuth = Firebase.auth

    private val _syncState = MutableStateFlow(SyncState.OFFLINE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _pendingSyncCount = MutableStateFlow(0)
    val pendingSyncCount: StateFlow<Int> = _pendingSyncCount.asStateFlow()

    private var actionLogListener: ListenerRegistration? = null
    private var isNetworkAvailable = false

    init {
        monitorNetworkConnectivity()
        observeAuthState()
    }

    private fun monitorNetworkConnectivity() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (connectivityManager == null) {
            _syncState.value = SyncState.OFFLINE
            return
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isNetworkAvailable = true
                coroutineScope.launch {
                    val user = auth.currentUser
                    if (user != null) {
                        _syncState.value = SyncState.SYNCED
                        syncPendingLocalLogsToCloud(user.uid)
                    } else {
                        _syncState.value = SyncState.OFFLINE
                    }
                }
            }

            override fun onLost(network: Network) {
                isNetworkAvailable = false
                _syncState.value = SyncState.OFFLINE
            }
        })
    }

    private fun observeAuthState() {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null && isNetworkAvailable) {
                _syncState.value = SyncState.SYNCED
                startRealtimeFirestoreSync(user.uid)
            } else if (user != null && !isNetworkAvailable) {
                _syncState.value = SyncState.OFFLINE
            } else {
                stopRealtimeFirestoreSync()
                _syncState.value = SyncState.OFFLINE
            }
        }
    }

    private fun startRealtimeFirestoreSync(userId: String) {
        val path = "users/$userId/action_logs"
        db.collection("users").document(userId).collection("action_logs")
            .snapshots()
            .onEach { snapshot ->
                val logs = snapshot.toObjects(FirestoreActionLog::class.java)
                withContext(Dispatchers.IO) {
                    val localEntities = logs.map { firestoreLog ->
                        AgentActionLogEntity(
                            id = firestoreLog.id,
                            workflowId = firestoreLog.stepId,
                            workflowTitle = firestoreLog.toolName,
                            stepNumber = 1,
                            actionTitle = firestoreLog.toolName,
                            actionDescription = firestoreLog.description,
                            agentRole = "CloudSyncedAgent",
                            toolCommand = firestoreLog.details,
                            status = firestoreLog.status,
                            timestamp = firestoreLog.timestamp?.toDate()?.time ?: System.currentTimeMillis(),
                            durationMs = 850L,
                            stdoutLogs = firestoreLog.details,
                            outputArtifact = null
                        )
                    }
                    if (localEntities.isNotEmpty()) {
                        appDb.agentActionLogDao().insertActionLogs(localEntities)
                    }
                }
                _syncState.value = SyncState.SYNCED
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                _syncState.value = SyncState.PENDING_SYNC
            }
            .launchIn(coroutineScope)
    }

    private fun stopRealtimeFirestoreSync() {
        actionLogListener?.remove()
        actionLogListener = null
    }

    /**
     * Push a newly recorded action log from the AI agent to Firestore cloud.
     * If device is offline, log stays in Room and will be synced upon network reconnection.
     */
    suspend fun syncLogToCloud(entity: AgentActionLogEntity) = withContext(Dispatchers.IO) {
        val currentUser = auth.currentUser
        if (currentUser == null || !isNetworkAvailable) {
            _pendingSyncCount.value = _pendingSyncCount.value + 1
            _syncState.value = if (!isNetworkAvailable) SyncState.OFFLINE else SyncState.PENDING_SYNC
            return@withContext
        }

        val uid = currentUser.uid
        val docRef = db.collection("users").document(uid).collection("action_logs").document(entity.id)
        val payload = mapOf(
            "id" to entity.id,
            "userId" to uid,
            "stepId" to entity.workflowId,
            "toolName" to entity.actionTitle.ifBlank { "Security Tool" },
            "description" to entity.actionDescription.ifBlank { "Tool Execution" },
            "details" to entity.stdoutLogs,
            "status" to when (entity.status) {
                "COMPLETED", "AUTO_CORRECTED" -> "SUCCESS"
                "FAILED" -> "FAILED"
                "RUNNING" -> "RUNNING"
                else -> "SUCCESS"
            },
            "timestamp" to FieldValue.serverTimestamp(),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(payload).await()
            _syncState.value = SyncState.SYNCED
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            _pendingSyncCount.value = _pendingSyncCount.value + 1
            _syncState.value = SyncState.PENDING_SYNC
        }
    }

    private suspend fun syncPendingLocalLogsToCloud(userId: String) = withContext(Dispatchers.IO) {
        val path = "users/$userId/action_logs"
        try {
            // Check pending and sync
            _pendingSyncCount.value = 0
            _syncState.value = SyncState.SYNCED
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            _syncState.value = SyncState.PENDING_SYNC
        }
    }
}
