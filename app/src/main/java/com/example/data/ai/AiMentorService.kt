package com.example.data.ai

import android.util.Log
import com.example.data.knowledge.SecurityKnowledgeBase
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiMentorService {

    private val model: GenerativeModel? by lazy {
        try {
            Firebase.ai.generativeModel("gemini-3.8-flash")
        } catch (e: Exception) {
            Log.e("AiMentorService", "Firebase AI not configured yet, using local mentor knowledge", e)
            null
        }
    }

    suspend fun generateSecurityAdvice(userPrompt: String): String = withContext(Dispatchers.IO) {
        val prompt = userPrompt.trim()
        val qLower = prompt.lowercase()

        // Enforce safety guidelines first
        if (qLower.contains("hack") && (qLower.contains("wifi") || qLower.contains("bank") || qLower.contains("ddos") || qLower.contains("steal"))) {
            return@withContext SecurityKnowledgeBase.getMentorResponse(prompt)
        }

        try {
            val genModel = model
            if (genModel != null) {
                val systemInstruction = """
You are SecMentor AI, an expert open-source cybersecurity mentor, ethical hacking instructor, and code auditor.
Guide the user strictly on defensive security, sandbox testing, GitHub tools (Nmap, Wireshark, SQLmap), and custom tool development.
Provide responses in clear Hindi or English matching the user's language.
If asked about malicious attacks, redirect firmly to defensive security auditing.
                """.trimIndent()

                val response = genModel.generateContent("$systemInstruction\n\nUser Question: $prompt")
                val text = response.text
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            }
        } catch (e: Exception) {
            Log.e("AiMentorService", "Inference error or App Check not initialized", e)
        }

        return@withContext SecurityKnowledgeBase.getMentorResponse(prompt)
    }
}
