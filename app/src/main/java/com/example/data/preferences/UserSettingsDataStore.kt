package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.userSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "secmentor_user_settings")

data class CachedCyberAnalysis(
    val title: String,
    val summary: String,
    val content: String,
    val timestamp: Long,
    val severity: String = "HIGH",
    val category: String = "Defensive Security Audit"
)

data class UserSettings(
    val selectedLanguageCode: String = "hi",
    val isOfflineMode: Boolean = false,
    val isOfflineSandboxMode: Boolean = false,
    val isAutoDispatchTermux: Boolean = true,
    val isVoiceAutoSearchEnabled: Boolean = true,
    val isHighSpeedCacheEnabled: Boolean = true,
    val lastActiveProjectName: String = "SecMentorTool",
    val cachedAnalysis: CachedCyberAnalysis? = null
)

class UserSettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language_code")
        val OFFLINE_MODE = booleanPreferencesKey("offline_mode_enabled")
        val OFFLINE_SANDBOX_MODE = booleanPreferencesKey("offline_sandbox_mode")
        val AUTO_DISPATCH_TERMUX = booleanPreferencesKey("auto_dispatch_termux")
        val VOICE_AUTO_SEARCH_ENABLED = booleanPreferencesKey("voice_auto_search_enabled")
        val HIGH_SPEED_CACHE_ENABLED = booleanPreferencesKey("high_speed_cache_enabled")
        val LAST_ACTIVE_PROJECT_NAME = stringPreferencesKey("last_active_project_name")

        // Cached cybersecurity analysis when disconnected
        val CACHED_ANALYSIS_TITLE = stringPreferencesKey("cached_analysis_title")
        val CACHED_ANALYSIS_SUMMARY = stringPreferencesKey("cached_analysis_summary")
        val CACHED_ANALYSIS_CONTENT = stringPreferencesKey("cached_analysis_content")
        val CACHED_ANALYSIS_TIMESTAMP = longPreferencesKey("cached_analysis_timestamp")
        val CACHED_ANALYSIS_SEVERITY = stringPreferencesKey("cached_analysis_severity")
        val CACHED_ANALYSIS_CATEGORY = stringPreferencesKey("cached_analysis_category")
    }

    val userSettingsFlow: Flow<UserSettings> = context.userSettingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val cachedTitle = preferences[PreferencesKeys.CACHED_ANALYSIS_TITLE]
            val cachedAnalysis = if (!cachedTitle.isNullOrBlank()) {
                CachedCyberAnalysis(
                    title = cachedTitle,
                    summary = preferences[PreferencesKeys.CACHED_ANALYSIS_SUMMARY] ?: "Cached security analysis report",
                    content = preferences[PreferencesKeys.CACHED_ANALYSIS_CONTENT] ?: "",
                    timestamp = preferences[PreferencesKeys.CACHED_ANALYSIS_TIMESTAMP] ?: 0L,
                    severity = preferences[PreferencesKeys.CACHED_ANALYSIS_SEVERITY] ?: "HIGH",
                    category = preferences[PreferencesKeys.CACHED_ANALYSIS_CATEGORY] ?: "Cybersecurity Audit"
                )
            } else null

            UserSettings(
                selectedLanguageCode = preferences[PreferencesKeys.SELECTED_LANGUAGE] ?: "hi",
                isOfflineMode = preferences[PreferencesKeys.OFFLINE_MODE] ?: false,
                isOfflineSandboxMode = preferences[PreferencesKeys.OFFLINE_SANDBOX_MODE] ?: false,
                isAutoDispatchTermux = preferences[PreferencesKeys.AUTO_DISPATCH_TERMUX] ?: true,
                isVoiceAutoSearchEnabled = preferences[PreferencesKeys.VOICE_AUTO_SEARCH_ENABLED] ?: true,
                isHighSpeedCacheEnabled = preferences[PreferencesKeys.HIGH_SPEED_CACHE_ENABLED] ?: true,
                lastActiveProjectName = preferences[PreferencesKeys.LAST_ACTIVE_PROJECT_NAME] ?: "SecMentorTool",
                cachedAnalysis = cachedAnalysis
            )
        }

    suspend fun updateLanguage(languageCode: String) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LANGUAGE] = languageCode
        }
    }

    suspend fun setOfflineMode(enabled: Boolean) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.OFFLINE_MODE] = enabled
        }
    }

    suspend fun setOfflineSandboxMode(enabled: Boolean) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.OFFLINE_SANDBOX_MODE] = enabled
        }
    }

    suspend fun cacheCybersecurityAnalysis(
        title: String,
        summary: String,
        content: String,
        severity: String = "HIGH",
        category: String = "Cybersecurity Audit"
    ) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.CACHED_ANALYSIS_TITLE] = title
            preferences[PreferencesKeys.CACHED_ANALYSIS_SUMMARY] = summary
            preferences[PreferencesKeys.CACHED_ANALYSIS_CONTENT] = content
            preferences[PreferencesKeys.CACHED_ANALYSIS_TIMESTAMP] = System.currentTimeMillis()
            preferences[PreferencesKeys.CACHED_ANALYSIS_SEVERITY] = severity
            preferences[PreferencesKeys.CACHED_ANALYSIS_CATEGORY] = category
        }
    }

    suspend fun clearCachedAnalysis() {
        context.userSettingsDataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.CACHED_ANALYSIS_TITLE)
            preferences.remove(PreferencesKeys.CACHED_ANALYSIS_SUMMARY)
            preferences.remove(PreferencesKeys.CACHED_ANALYSIS_CONTENT)
            preferences.remove(PreferencesKeys.CACHED_ANALYSIS_TIMESTAMP)
            preferences.remove(PreferencesKeys.CACHED_ANALYSIS_SEVERITY)
            preferences.remove(PreferencesKeys.CACHED_ANALYSIS_CATEGORY)
        }
    }

    suspend fun getCachedAnalysis(): CachedCyberAnalysis? {
        return userSettingsFlow.first().cachedAnalysis
    }

    suspend fun setAutoDispatchTermux(enabled: Boolean) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_DISPATCH_TERMUX] = enabled
        }
    }

    suspend fun setVoiceAutoSearchEnabled(enabled: Boolean) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.VOICE_AUTO_SEARCH_ENABLED] = enabled
        }
    }

    suspend fun setHighSpeedCacheEnabled(enabled: Boolean) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.HIGH_SPEED_CACHE_ENABLED] = enabled
        }
    }

    suspend fun setLastActiveProjectName(projectName: String) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_ACTIVE_PROJECT_NAME] = projectName
        }
    }
}
