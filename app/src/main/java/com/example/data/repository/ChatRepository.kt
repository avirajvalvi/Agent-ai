package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.ChatDao
import com.example.data.local.ChatEntity
import com.example.data.local.ChatPreferencesDao
import com.example.data.models.ChatMessage
import com.example.data.models.SecuritySeverity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Repository layer adhering to Room Database Integration architectural standards.
 * Abstracts SQLite data access from ViewModels and provides reactive real-time Flow streams.
 */
class ChatRepository(
    private val chatDao: ChatDao,
    private val chatPreferencesDao: ChatPreferencesDao
) {
    /**
     * Observes real-time conversation history directly from the Room database.
     * Automatically emits whenever a new message is inserted, updated, or removed.
     */
    val realTimeChatHistory: Flow<List<ChatMessage>> = chatDao.getAllMessages().map { entities ->
        entities.map { entity ->
            ChatMessage(
                id = entity.id,
                text = entity.text,
                isUser = entity.isUser,
                timestamp = entity.timestamp,
                attachedCode = entity.attachedCode,
                codeLanguage = entity.codeLanguage,
                attachedFileName = entity.attachedFileName,
                securitySeverity = entity.securitySeverity?.let {
                    try {
                        SecuritySeverity.valueOf(it)
                    } catch (e: Exception) {
                        null
                    }
                },
                tags = entity.tags.split(",").filter { it.isNotBlank() }
            )
        }
    }

    /**
     * Observes real-time message count stored in Room database.
     */
    val messageCount: Flow<Int> = chatPreferencesDao.getChatMessageCount()

    /**
     * Searches stored chat conversation history in real-time by text query or tags.
     */
    fun searchHistory(query: String): Flow<List<ChatMessage>> =
        chatPreferencesDao.searchChatHistory(query).map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id,
                    text = entity.text,
                    isUser = entity.isUser,
                    timestamp = entity.timestamp,
                    attachedCode = entity.attachedCode,
                    codeLanguage = entity.codeLanguage,
                    attachedFileName = entity.attachedFileName,
                    securitySeverity = entity.securitySeverity?.let {
                        try {
                            SecuritySeverity.valueOf(it)
                        } catch (e: Exception) {
                            null
                        }
                    },
                    tags = entity.tags.split(",").filter { it.isNotBlank() }
                )
            }
        }

    /**
     * Inserts a user or AI message into Room asynchronously.
     */
    suspend fun insertMessage(message: ChatMessage) = withContext(Dispatchers.IO) {
        val entity = ChatEntity(
            id = message.id,
            text = message.text,
            isUser = message.isUser,
            timestamp = message.timestamp,
            attachedCode = message.attachedCode,
            codeLanguage = message.codeLanguage,
            attachedFileName = message.attachedFileName,
            securitySeverity = message.securitySeverity?.name,
            tags = message.tags.joinToString(",")
        )
        chatDao.insertMessage(entity)
    }

    /**
     * Updates an existing message in Room (e.g., during live streaming token generation).
     */
    suspend fun updateMessage(message: ChatMessage) = withContext(Dispatchers.IO) {
        val entity = ChatEntity(
            id = message.id,
            text = message.text,
            isUser = message.isUser,
            timestamp = message.timestamp,
            attachedCode = message.attachedCode,
            codeLanguage = message.codeLanguage,
            attachedFileName = message.attachedFileName,
            securitySeverity = message.securitySeverity?.name,
            tags = message.tags.joinToString(",")
        )
        chatDao.insertMessage(entity)
    }

    /**
     * Deletes a specific message by ID from Room.
     */
    suspend fun deleteMessage(id: String) = withContext(Dispatchers.IO) {
        chatPreferencesDao.deleteChatMessageById(id)
    }

    /**
     * Clears all chat messages from Room.
     */
    suspend fun clearAllMessages() = withContext(Dispatchers.IO) {
        chatDao.clearMessages()
    }

    companion object {
        @Volatile
        private var INSTANCE: ChatRepository? = null

        fun getInstance(context: Context): ChatRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val instance = ChatRepository(db.chatDao(), db.chatPreferencesDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
