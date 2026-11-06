package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.userSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "secmentor_user_settings")

data class UserSettings(
    val selectedLanguageCode: String = "hi",
    val isOfflineSandboxMode: Boolean = false,
    val isAutoDispatchTermux: Boolean = true,
    val isVoiceAutoSearchEnabled: Boolean = true,
    val isHighSpeedCacheEnabled: Boolean = true,
    val lastActiveProjectName: String = "SecMentorTool"
)

class UserSettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language_code")
        val OFFLINE_SANDBOX_MODE = booleanPreferencesKey("offline_sandbox_mode")
        val AUTO_DISPATCH_TERMUX = booleanPreferencesKey("auto_dispatch_termux")
        val VOICE_AUTO_SEARCH_ENABLED = booleanPreferencesKey("voice_auto_search_enabled")
        val HIGH_SPEED_CACHE_ENABLED = booleanPreferencesKey("high_speed_cache_enabled")
        val LAST_ACTIVE_PROJECT_NAME = stringPreferencesKey("last_active_project_name")
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
            UserSettings(
                selectedLanguageCode = preferences[PreferencesKeys.SELECTED_LANGUAGE] ?: "hi",
                isOfflineSandboxMode = preferences[PreferencesKeys.OFFLINE_SANDBOX_MODE] ?: false,
                isAutoDispatchTermux = preferences[PreferencesKeys.AUTO_DISPATCH_TERMUX] ?: true,
                isVoiceAutoSearchEnabled = preferences[PreferencesKeys.VOICE_AUTO_SEARCH_ENABLED] ?: true,
                isHighSpeedCacheEnabled = preferences[PreferencesKeys.HIGH_SPEED_CACHE_ENABLED] ?: true,
                lastActiveProjectName = preferences[PreferencesKeys.LAST_ACTIVE_PROJECT_NAME] ?: "SecMentorTool"
            )
        }

    suspend fun updateLanguage(languageCode: String) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LANGUAGE] = languageCode
        }
    }

    suspend fun setOfflineSandboxMode(enabled: Boolean) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.OFFLINE_SANDBOX_MODE] = enabled
        }
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
