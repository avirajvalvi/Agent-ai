package com.example.data.repository

import com.example.data.local.AgentActionLogDao
import com.example.data.local.AgentActionLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository pattern implementation for persisting and retrieving
 * autonomous AI agent action history logs locally via Room Database.
 */
class AgentActionLogRepository(
    private val actionLogDao: AgentActionLogDao,
    private val syncRepository: FirestoreSyncRepository? = null
) {
    /**
     * Flow of all action logs ordered by timestamp descending for reactive UI updates.
     */
    val allActionLogs: Flow<List<AgentActionLogEntity>> = actionLogDao.getAllActionLogs()

    /**
     * Retrieve action logs for a specific workflow by workflowId.
     */
    fun getLogsByWorkflow(workflowId: String): Flow<List<AgentActionLogEntity>> {
        return actionLogDao.getActionLogsByWorkflow(workflowId)
    }

    /**
     * Persist a new autonomous action log entry to the Room database and sync to cloud.
     */
    suspend fun insertActionLog(log: AgentActionLogEntity) {
        actionLogDao.insertActionLog(log)
        syncRepository?.syncLogToCloud(log)
    }

    /**
     * Batch insert action logs into the Room database.
     */
    suspend fun insertActionLogs(logs: List<AgentActionLogEntity>) {
        actionLogDao.insertActionLogs(logs)
    }

    /**
     * Delete a single action log by its ID.
     */
    suspend fun deleteActionLogById(id: String) {
        actionLogDao.deleteActionLogById(id)
    }

    /**
     * Clear all recorded action logs from the local database.
     */
    suspend fun clearActionLogs() {
        actionLogDao.clearActionLogs()
    }
}
