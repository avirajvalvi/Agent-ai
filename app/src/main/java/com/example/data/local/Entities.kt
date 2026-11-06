package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long,
    val attachedCode: String?,
    val codeLanguage: String?,
    val attachedFileName: String?,
    val securitySeverity: String?,
    val tags: String // Comma-separated
)

@Entity(tableName = "security_projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val lastUpdated: Long,
    val scriptCount: Int,
    val sampleSnippet: String
)

@Entity(tableName = "custom_tools")
data class CustomToolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val language: String,
    val description: String,
    val code: String,
    val usageExample: String
)

@Entity(tableName = "autonomous_action_logs")
data class AgentActionLogEntity(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val workflowId: String,
    val workflowTitle: String,
    val stepNumber: Int,
    val actionTitle: String,
    val actionDescription: String,
    val agentRole: String,
    val toolCommand: String,
    val status: String, // "COMPLETED", "AUTO_CORRECTED", "RUNNING", "FAILED"
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long = 1200L,
    val stdoutLogs: String,
    val outputArtifact: String? = null
)

@Entity(tableName = "model_cache")
data class ModelCacheEntity(
    @PrimaryKey val key: String,
    val taskType: String, // "AST_SCAN", "AI_INFERENCE", "PROMPT_EMBEDDING"
    val cachedPayload: String,
    val latencySavedMs: Long = 850L,
    val hitCount: Int = 1,
    val lastAccessed: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey val id: String = "default_user_prefs",
    val selectedLanguageCode: String = "hi",
    val isOfflineSandboxMode: Boolean = false,
    val isAutoDispatchTermux: Boolean = true,
    val isVoiceAutoSearchEnabled: Boolean = true,
    val isHighSpeedCacheEnabled: Boolean = true,
    val themeMode: String = "SYSTEM",
    val defaultTargetHost: String = "127.0.0.1",
    val maxChatHistoryRetained: Int = 500,
    val lastUpdated: Long = System.currentTimeMillis()
)
