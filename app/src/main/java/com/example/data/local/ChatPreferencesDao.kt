package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Room database DAO interface for storing chat history and user preferences locally
 * to complement the DataStore configuration.
 *
 * Provides reactive [Flow] queries for UI updates and suspend functions for
 * asynchronous mutations in accordance with Android Architecture & Room guidelines.
 */
@Dao
interface ChatPreferencesDao {

    // =========================================================================
    // Chat History Operations
    // =========================================================================

    /**
     * Observes the full chat conversation history in ascending chronological order.
     */
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatHistory(): Flow<List<ChatEntity>>

    /**
     * Searches chat messages containing the given query string in message text or tags.
     */
    @Query("SELECT * FROM chat_messages WHERE text LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchChatHistory(query: String): Flow<List<ChatEntity>>

    /**
     * Retrieves recent chat history with pagination support.
     */
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    fun getRecentChatHistory(limit: Int = 50, offset: Int = 0): Flow<List<ChatEntity>>

    /**
     * Retrieves a single chat message by unique ID.
     */
    @Query("SELECT * FROM chat_messages WHERE id = :id LIMIT 1")
    suspend fun getChatMessageById(id: String): ChatEntity?

    /**
     * Observes the total number of chat messages currently stored.
     */
    @Query("SELECT COUNT(*) FROM chat_messages")
    fun getChatMessageCount(): Flow<Int>

    /**
     * Inserts or replaces a chat message record.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatEntity)

    /**
     * Inserts or replaces multiple chat message records in a batch.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessages(messages: List<ChatEntity>)

    /**
     * Deletes a specific chat message by ID.
     */
    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteChatMessageById(id: String)

    /**
     * Clears all chat history records.
     */
    @Query("DELETE FROM chat_messages")
    suspend fun clearAllChatHistory()

    // =========================================================================
    // User Preferences Operations (Complementing DataStore)
    // =========================================================================

    /**
     * Observes the user preferences reactively.
     */
    @Query("SELECT * FROM user_preferences WHERE id = :id LIMIT 1")
    fun getUserPreferencesFlow(id: String = "default_user_prefs"): Flow<UserPreferencesEntity?>

    /**
     * Synchronously/one-shot retrieves the current user preferences.
     */
    @Query("SELECT * FROM user_preferences WHERE id = :id LIMIT 1")
    suspend fun getUserPreferences(id: String = "default_user_prefs"): UserPreferencesEntity?

    /**
     * Inserts or replaces user preferences.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(preferences: UserPreferencesEntity)

    /**
     * Updates existing user preferences.
     */
    @Update
    suspend fun updatePreferences(preferences: UserPreferencesEntity)

    /**
     * Updates language preference.
     */
    @Query("UPDATE user_preferences SET selectedLanguageCode = :langCode, lastUpdated = :now WHERE id = :id")
    suspend fun updateLanguage(langCode: String, id: String = "default_user_prefs", now: Long = System.currentTimeMillis())

    /**
     * Updates offline sandbox mode preference.
     */
    @Query("UPDATE user_preferences SET isOfflineSandboxMode = :offline, lastUpdated = :now WHERE id = :id")
    suspend fun updateOfflineMode(offline: Boolean, id: String = "default_user_prefs", now: Long = System.currentTimeMillis())

    /**
     * Updates autonomous Termux auto-dispatch preference.
     */
    @Query("UPDATE user_preferences SET isAutoDispatchTermux = :autoDispatch, lastUpdated = :now WHERE id = :id")
    suspend fun updateTermuxAutoDispatch(autoDispatch: Boolean, id: String = "default_user_prefs", now: Long = System.currentTimeMillis())

    /**
     * Updates voice-activated web search preference.
     */
    @Query("UPDATE user_preferences SET isVoiceAutoSearchEnabled = :voiceEnabled, lastUpdated = :now WHERE id = :id")
    suspend fun updateVoiceAutoSearch(voiceEnabled: Boolean, id: String = "default_user_prefs", now: Long = System.currentTimeMillis())

    /**
     * Updates 0ms high-speed model cache preference.
     */
    @Query("UPDATE user_preferences SET isHighSpeedCacheEnabled = :cacheEnabled, lastUpdated = :now WHERE id = :id")
    suspend fun updateHighSpeedCache(cacheEnabled: Boolean, id: String = "default_user_prefs", now: Long = System.currentTimeMillis())

    /**
     * Clears stored local user preferences.
     */
    @Query("DELETE FROM user_preferences WHERE id = :id")
    suspend fun clearPreferences(id: String = "default_user_prefs")

    // =========================================================================
    // Combined Transactional Operations
    // =========================================================================

    /**
     * Atomic transaction to reset both chat history and user preferences to defaults.
     */
    @Transaction
    suspend fun resetAllUserData() {
        clearAllChatHistory()
        clearPreferences()
        insertOrUpdatePreferences(UserPreferencesEntity())
    }
}
