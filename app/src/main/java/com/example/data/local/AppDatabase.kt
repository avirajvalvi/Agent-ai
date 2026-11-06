package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearMessages()
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM security_projects ORDER BY lastUpdated DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Query("DELETE FROM security_projects WHERE id = :id")
    suspend fun deleteProject(id: String)
}

@Dao
interface CustomToolDao {
    @Query("SELECT * FROM custom_tools ORDER BY name ASC")
    fun getAllTools(): Flow<List<CustomToolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTool(tool: CustomToolEntity)

    @Query("DELETE FROM custom_tools WHERE id = :id")
    suspend fun deleteTool(id: String)
}

/**
 * Data Access Object for persisting and querying autonomous AI agent action history.
 */
@Dao
interface AgentActionLogDao {
    @Query("SELECT * FROM autonomous_action_logs ORDER BY timestamp DESC")
    fun getAllActionLogs(): Flow<List<AgentActionLogEntity>>

    @Query("SELECT * FROM autonomous_action_logs WHERE workflowId = :workflowId ORDER BY stepNumber ASC")
    fun getActionLogsByWorkflow(workflowId: String): Flow<List<AgentActionLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActionLog(log: AgentActionLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActionLogs(logs: List<AgentActionLogEntity>)

    @Query("DELETE FROM autonomous_action_logs WHERE id = :id")
    suspend fun deleteActionLogById(id: String)

    @Query("DELETE FROM autonomous_action_logs")
    suspend fun clearActionLogs()
}

@Dao
interface ModelCacheDao {
    @Query("SELECT * FROM model_cache WHERE `key` = :key LIMIT 1")
    suspend fun getCache(key: String): ModelCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: ModelCacheEntity)

    @Query("UPDATE model_cache SET hitCount = hitCount + 1, lastAccessed = :now WHERE `key` = :key")
    suspend fun recordHit(key: String, now: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM model_cache")
    fun getCacheCount(): Flow<Int>

    @Query("DELETE FROM model_cache WHERE `key` NOT IN (SELECT `key` FROM model_cache ORDER BY lastAccessed DESC LIMIT 500)")
    suspend fun evictOldEntries()

    @Query("DELETE FROM model_cache")
    suspend fun clearCache()
}

@Database(
    entities = [
        ChatEntity::class,
        ProjectEntity::class,
        CustomToolEntity::class,
        AgentActionLogEntity::class,
        ModelCacheEntity::class,
        UserPreferencesEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun chatPreferencesDao(): ChatPreferencesDao
    abstract fun projectDao(): ProjectDao
    abstract fun customToolDao(): CustomToolDao
    abstract fun agentActionLogDao(): AgentActionLogDao
    abstract fun modelCacheDao(): ModelCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "secmentor_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
