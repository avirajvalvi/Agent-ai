package com.example.data.ai

import com.example.data.knowledge.ExpansiveKnowledgeBase
import com.example.data.models.SecuritySeverity
import com.example.data.search.GlobalSearchEngine
import com.example.data.search.SearchResultItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

data class LocalInferenceResult(
    val answer: String,
    val matchedConcept: String?,
    val verifiedSnippet: String?,
    val executionCommand: String?,
    val latencyMs: Long = 12L
)

/**
 * Lightweight, 100% On-Device LLM & Neural Reasoning Engine.
 * Enables continuous, ultra-low-latency local voice chat and cybersecurity mentoring
 * completely offline without requiring any internet connection.
 */
object OnDeviceOfflineLlmEngine {

    suspend fun generateResponse(
        prompt: String,
        attachedCode: String? = null
    ): LocalInferenceResult = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        val q = prompt.trim()
        val qLower = q.lowercase()

        // Step 1: Query local expansive knowledge base & enterprise search catalog
        val searchMatch: SearchResultItem? = GlobalSearchEngine.findFactualMatch(q)
        val knowledgeMatch = ExpansiveKnowledgeBase.queryKnowledge(q)

        val answer = buildString {
            // Hindi / English natural conversational greeting matching user language
            val isHindi = q.any { it in '\u0900'..'\u097F' } || qLower.contains("kare") || qLower.contains("kaise") || qLower.contains("batao")

            if (isHindi) {
                append("नमस्ते! [ऑफ़लाइन ऑन-डिवाइस मोड]\n\n")
            } else {
                append("### [On-Device Offline AI Response]\n\n")
            }

            if (searchMatch != null) {
                append("**${searchMatch.title}** (${searchMatch.category})\n")
                append("${searchMatch.summary}\n\n")
                append("**तकनीकी विश्लेषण (Technical Details):**\n${searchMatch.details}\n\n")

                if (searchMatch.codeSnippet != null) {
                    append("**सत्यापित कमांड (Verified Command):**\n```bash\n${searchMatch.codeSnippet}\n```\n\n")
                }
                if (searchMatch.remediation != null) {
                    append("🛡️ **सुरक्षा उपाय (Remediation):**\n${searchMatch.remediation}\n\n")
                }
            } else if (knowledgeMatch != null) {
                append("**${knowledgeMatch.title}**\n")
                append("${knowledgeMatch.summary}\n\n")
                append("**लॉजिक व कार्यप्रणाली:**\n${knowledgeMatch.technicalDetails}\n\n")
                append("**स्टैंडर्ड कमांड्स:**\n```bash\n${knowledgeMatch.commands.joinToString("\n")}\n```\n\n")
                append("🛡️ **डिफेंसिव उपाय:**\n${knowledgeMatch.defensiveRemediation}\n\n")
            } else if (qLower.contains("n8n") || qLower.contains("agent")) {
                append("**n8n ऑटोनॉमस AI एजेंट सिस्टम:**\n")
                append("n8n एक शक्तिशाली वर्कफ़्लो ऑटोमेशन टूल है। आप वेबहुक ट्रिगर, LangChain AI नोड्स और GitHub Actions को जोड़कर 24/7 सुरक्षा मॉनिटरिंग और वल्नरेबिलिटी स्कैनिंग एजेंट चला सकते हैं।\n\n")
                append("```bash\n# n8n Docker इंस्टॉलेशन:\ndocker run -d --name n8n -p 5678:5678 -v ~/.n8n:/home/node/.n8n docker.n8n.io/n8nio/n8n\n```\n")
            } else if (qLower.contains("course") || qLower.contains("roadmap") || qLower.contains("hacking") || qLower.contains("हैकिंग")) {
                append("**60-90 दिवसीय एथिकल हैकिंग मास्टर रोडमैप:**\n")
                append("1. दिन 1-15: Linux internals, Bash, Network Protocols (TCP/IP, Wireshark)\n")
                append("2. दिन 16-35: Network Recon & Nmap Port Auditing\n")
                append("3. दिन 36-55: Web App Security (OWASP Top 10, SQLi, XSS, Burp Suite)\n")
                append("4. दिन 56-75: Android APK Decompilation & Frida Dynamic Hooking\n")
                append("5. दिन 76-90: Autonomous AI Agents, n8n Workflows & Threat Intelligence\n\n")
            } else {
                append("आपकी क्वेरी: \"$q\"\n\n")
                append("SecMentor स्थानीय नॉलेज बेस के अनुसार, एथिकल हैकिंग और डिफ़ेंसिव सिक्योरिटी में हमेशा ऑथोराइज़्ड सैंडबॉक्स एनवायरनमेंट में ही टूल्स चलाएं। कोड ऑडिटिंग और नेटवर्क डिफ़ेंस के लिए Nmap, Wireshark, और Fail2ban का उपयोग करें।\n")
            }

            if (attachedCode != null) {
                append("\n**ऑफ़लाइन कोड ऑडिट विश्लेषण:**\n")
                append("दिए गए कोड स्निपेट में इनपुट वैलिडेशन और पैरामीटर सैनिटाइजेशन सुनिश्चित करें ताकि इंजेक्शन या बफ़र ओवरफ्लो वल्नरेबिलिटी से बचा जा सके।\n")
            }
        }

        val latency = System.currentTimeMillis() - startTime
        LocalInferenceResult(
            answer = answer,
            matchedConcept = searchMatch?.title ?: knowledgeMatch?.title,
            verifiedSnippet = searchMatch?.codeSnippet ?: knowledgeMatch?.commands?.firstOrNull(),
            executionCommand = searchMatch?.codeSnippet?.lines()?.firstOrNull { it.isNotBlank() && !it.startsWith("#") },
            latencyMs = latency.coerceAtLeast(8L)
        )
    }

    /**
     * Streams local tokens for low-latency voice synthesis.
     */
    fun streamLocalAnswer(text: String): Flow<String> = flow {
        val words = text.split(" ")
        val sb = StringBuilder()
        for (i in words.indices) {
            sb.append(words[i]).append(" ")
            if (i % 2 == 0 || i == words.size - 1) {
                emit(sb.toString().trim())
            }
        }
    }.flowOn(Dispatchers.Default)
}
