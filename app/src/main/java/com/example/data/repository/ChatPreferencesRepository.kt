package com.example.data.repository

import com.example.data.local.ChatEntity
import com.example.data.local.ChatPreferencesDao
import com.example.data.local.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository abstracting Room database operations for local chat history
 * and persistent user preferences, complementing the DataStore settings.
 */
class ChatPreferencesRepository(private val dao: ChatPreferencesDao) {

    // Chat History Flows
    val allChatHistory: Flow<List<ChatEntity>> = dao.getAllChatHistory()
    val chatMessageCount: Flow<Int> = dao.getChatMessageCount()

    // User Preferences Flows
    val userPreferences: Flow<UserPreferencesEntity?> = dao.getUserPreferencesFlow()

    // Chat History Actions
    fun searchChat(query: String): Flow<List<ChatEntity>> = dao.searchChatHistory(query)

    fun getRecentChat(limit: Int = 50, offset: Int = 0): Flow<List<ChatEntity>> =
        dao.getRecentChatHistory(limit, offset)

    suspend fun getMessageById(id: String): ChatEntity? = dao.getChatMessageById(id)

    suspend fun saveMessage(message: ChatEntity) = dao.insertChatMessage(message)

    suspend fun saveMessages(messages: List<ChatEntity>) = dao.insertChatMessages(messages)

    suspend fun deleteMessage(id: String) = dao.deleteChatMessageById(id)

    suspend fun clearHistory() = dao.clearAllChatHistory()

    // User Preferences Actions
    suspend fun getPreferences(): UserPreferencesEntity =
        dao.getUserPreferences() ?: UserPreferencesEntity()

    suspend fun savePreferences(preferences: UserPreferencesEntity) =
        dao.insertOrUpdatePreferences(preferences)

    suspend fun setLanguage(languageCode: String) =
        dao.updateLanguage(languageCode)

    suspend fun setOfflineMode(isOffline: Boolean) =
        dao.updateOfflineMode(isOffline)

    suspend fun setAutoDispatchTermux(autoDispatch: Boolean) =
        dao.updateTermuxAutoDispatch(autoDispatch)

    suspend fun setVoiceAutoSearch(voiceEnabled: Boolean) =
        dao.updateVoiceAutoSearch(voiceEnabled)

    suspend fun setHighSpeedCache(cacheEnabled: Boolean) =
        dao.updateHighSpeedCache(cacheEnabled)

    suspend fun resetUserData() = dao.resetAllUserData()
}
