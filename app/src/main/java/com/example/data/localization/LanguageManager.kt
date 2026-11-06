package com.example.data.localization

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिन्दी"),
    SPANISH("es", "Spanish", "Español")
}

class LanguageManager private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("secmentor_locale_prefs", Context.MODE_PRIVATE)

    private val _currentLanguage = MutableStateFlow(loadSavedLanguage())
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private fun loadSavedLanguage(): AppLanguage {
        val code = prefs.getString("selected_lang_code", AppLanguage.HINDI.code) ?: AppLanguage.HINDI.code
        return AppLanguage.values().firstOrNull { it.code == code } ?: AppLanguage.HINDI
    }

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString("selected_lang_code", lang.code).apply()
        _currentLanguage.value = lang
    }

    fun getString(key: String): String {
        val lang = _currentLanguage.value
        return translations[lang]?.get(key) ?: translations[AppLanguage.ENGLISH]?.get(key) ?: key
    }

    companion object {
        @Volatile
        private var INSTANCE: LanguageManager? = null

        fun getInstance(context: Context): LanguageManager {
            return INSTANCE ?: synchronized(this) {
                val instance = LanguageManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }

        private val translations = mapOf(
            AppLanguage.ENGLISH to mapOf(
                "app_title" to "SecMentor AI",
                "home" to "Home",
                "chats" to "Chats",
                "autonomous_agent" to "Autonomous Agent",
                "ai_app_builder" to "Build Pipeline & APK",
                "files_docs" to "Files & Docs",
                "code_sandbox" to "Code & Sandbox",
                "video_studio" to "Video Studio",
                "projects" to "Projects",
                "artifacts" to "Artifacts",
                "new_chat" to "New chat",
                "recents" to "Recents",
                "ready_when_you_are" to "Ready when you are",
                "message_placeholder" to "Message Claude / SecMentor...",
                "sign_in_google" to "Sign in with Google",
                "cloud_synced" to "Cloud Synced",
                "offline_mode" to "Offline Mode",
                "pending_sync" to "Pending Sync",
                "sync_now" to "Sync Now",
                "cache_speed" to "Dual-Tier Cache",
                "language" to "Language",
                "sign_out" to "Sign Out",
                "guest_offline" to "Offline Sandbox Mode",
                "external_hub" to "Termux & YouTube Hub",
                "hacking_academy" to "60-90 Day Academy & n8n Hub",
                "sec_mentor_subtitle" to "Open-Source Ethical Hacking Mentor & Autonomous Agent"
            ),
            AppLanguage.HINDI to mapOf(
                "app_title" to "SecMentor AI",
                "home" to "होम",
                "chats" to "चैट्स",
                "autonomous_agent" to "ऑटोनॉमस एजेंट",
                "ai_app_builder" to "बिल्ड पाइपलाइन व APK",
                "files_docs" to "फाइल्स व डॉक्स",
                "code_sandbox" to "कोड व सैंडबॉक्स",
                "video_studio" to "वीडियो स्टूडियो",
                "projects" to "प्रोजेक्ट्स",
                "artifacts" to "आर्टिफैक्ट्स",
                "new_chat" to "नई चैट",
                "recents" to "हालिया गतिविधियाँ",
                "ready_when_you_are" to "तैयार, जब भी आप कहें",
                "message_placeholder" to "क्लाउड / सेकमेंटर को संदेश भेजें...",
                "sign_in_google" to "Google के साथ साइन इन करें",
                "cloud_synced" to "क्लाउड सिंक संपन्न",
                "offline_mode" to "ऑफ़लाइन मोड",
                "pending_sync" to "सिंक प्रतीक्षारत",
                "sync_now" to "अभी सिंक करें",
                "cache_speed" to "द्वि-स्तरीय त्वरित कैश",
                "language" to "भाषा",
                "sign_out" to "साइन आउट",
                "guest_offline" to "ऑफ़लाइन सैंडबॉक्स मोड",
                "external_hub" to "टर्मक्स व यूट्यूब हब",
                "hacking_academy" to "60-90 दिन अकादमी व n8n हब",
                "sec_mentor_subtitle" to "ओपन-सोर्स एथिकल हैकिंग मेंटर और ऑटोनॉमस टास्क एजेंट"
            ),
            AppLanguage.SPANISH to mapOf(
                "app_title" to "SecMentor AI",
                "home" to "Inicio",
                "chats" to "Chats",
                "autonomous_agent" to "Agente Autónomo",
                "ai_app_builder" to "Creador de Apps IA",
                "files_docs" to "Archivos y Docs",
                "code_sandbox" to "Código y Sandbox",
                "video_studio" to "Estudio de Video",
                "projects" to "Proyectos",
                "artifacts" to "Artefactos",
                "new_chat" to "Nuevo chat",
                "recents" to "Recientes",
                "ready_when_you_are" to "Listo cuando tú lo estés",
                "message_placeholder" to "Mensaje a Claude / SecMentor...",
                "sign_in_google" to "Iniciar sesión con Google",
                "cloud_synced" to "Sincronizado en la Nube",
                "offline_mode" to "Modo Sin Conexión",
                "pending_sync" to "Sincronización Pendiente",
                "sync_now" to "Sincronizar Ahora",
                "cache_speed" to "Caché de Doble Nivel",
                "language" to "Idioma",
                "sign_out" to "Cerrar Sesión",
                "guest_offline" to "Modo Sandbox Offline",
                "external_hub" to "Hub Termux y YouTube",
                "hacking_academy" to "Academia 60-90 Días y Hub n8n",
                "sec_mentor_subtitle" to "Mentor de Hacking Ético y Agente Autónomo de Seguridad"
            )
        )
    }
}
