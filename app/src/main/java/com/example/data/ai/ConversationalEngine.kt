package com.example.data.ai

import android.content.Context
import android.util.Log
import com.example.data.knowledge.ExpansiveKnowledgeBase
import com.example.data.knowledge.SecurityKnowledgeBase
import com.example.data.models.ChatMessage
import com.example.data.models.SecuritySeverity
import com.example.data.search.GlobalSearchEngine
import com.example.data.search.SearchResultItem
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

data class ConversationalTurn(
    val userQuery: String,
    val aiResponse: String,
    val matchedSearchItem: SearchResultItem? = null,
    val followUpSuggestions: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class SynthesisResult(
    val fullResponse: String,
    val matchedSearchItem: SearchResultItem? = null,
    val severity: SecuritySeverity = SecuritySeverity.INFO,
    val followUpSuggestions: List<String> = emptyList()
)

class ConversationalEngine(private val context: Context) {

    private val conversationHistory = mutableListOf<ConversationalTurn>()

    private val generativeModel: GenerativeModel? by lazy {
        try {
            Firebase.ai.generativeModel("gemini-3.8-flash")
        } catch (e: Exception) {
            Log.e("ConversationalEngine", "Firebase AI not configured or unavailable, using local factual synthesis", e)
            null
        }
    }

    /**
     * Synthesizes a factual, natural response with enterprise search grounding and low latency.
     */
    suspend fun processTurn(
        userPrompt: String,
        attachedCode: String? = null
    ): SynthesisResult = withContext(Dispatchers.IO) {
        val cleanPrompt = userPrompt.trim()
        val qLower = cleanPrompt.lowercase()

        // Step 1: Enterprise Global Search Engine lookup for instant factual grounding
        val searchMatch = GlobalSearchEngine.findFactualMatch(cleanPrompt)
        val knowledgeConcept = ExpansiveKnowledgeBase.queryKnowledge(cleanPrompt)

        // Step 2: Formulate dynamic follow-up suggestions for natural dialogue
        val followUps = mutableListOf<String>()
        if (searchMatch != null) {
            when (searchMatch.category) {
                "CVE" -> {
                    followUps.add("How do I patch ${searchMatch.title}?")
                    followUps.add("Can you show the detection script?")
                    followUps.add("What are the network mitigation rules?")
                }
                "TOOL" -> {
                    followUps.add("Run this command in the sandbox terminal")
                    followUps.add("Explain all command flags step-by-step")
                    followUps.add("Show how to build this in Termux")
                }
                "MITRE" -> {
                    followUps.add("What detection rules identify this technique?")
                    followUps.add("Show Fail2ban / iptables prevention config")
                }
                else -> {
                    followUps.add("Can we write a custom Python audit script for this?")
                    followUps.add("Explain the defensive packet analysis in Wireshark")
                }
            }
        } else {
            followUps.add("How does this relate to OWASP Top 10?")
            followUps.add("Show hands-on terminal command example")
            followUps.add("Explain defensive hardening best practices")
        }

        // Step 3: Conversational Prompt Augmentation
        val previousContext = conversationHistory.takeLast(3).joinToString("\n") { turn ->
            "User: ${turn.userQuery}\nAssistant: ${turn.aiResponse.take(150)}..."
        }

        val factualGroundingSnippet = buildString {
            if (searchMatch != null) {
                append("\n[ENTERPRISE SEARCH FACTUAL CONTEXT]\n")
                append("Title: ${searchMatch.title}\n")
                append("Category: ${searchMatch.category} (Severity: ${searchMatch.severity})\n")
                append("Summary: ${searchMatch.summary}\n")
                append("Technical Details: ${searchMatch.details}\n")
                searchMatch.codeSnippet?.let { append("Verified Code/Command:\n$it\n") }
                searchMatch.remediation?.let { append("Remediation: $it\n") }
            }
            if (knowledgeConcept != null && knowledgeConcept.title != searchMatch?.title) {
                append("\n[EXPANSIVE KNOWLEDGE BASE CONCEPT]\n")
                append("Concept: ${knowledgeConcept.title}\n")
                append("Details: ${knowledgeConcept.technicalDetails}\n")
                append("Defensive Remediation: ${knowledgeConcept.defensiveRemediation}\n")
            }
        }

        var finalResponse = ""
        val model = generativeModel

        if (model != null) {
            try {
                val systemPrompt = """
You are SecMentor AI, an enterprise-grade ethical hacking mentor, code analyzer, and continuous conversational companion.
Maintain a warm, engaging, natural human-like interactive dialogue (mirroring Claude's articulate, respectful persona).
Support fluent Hindi (हिन्दी), English, and Spanish matching the user's inquiry language.
Ground your response strictly in the factual cybersecurity knowledge provided below.
Focus exclusively on defensive security, sandbox testing, educational vulnerability analysis, and code auditing. Never generate unauthorized attack instructions.

Recent Conversation History:
$previousContext

Factual Grounding Reference:
$factualGroundingSnippet
                """.trimIndent()

                val promptWithCode = if (attachedCode != null) {
                    "$cleanPrompt\n\nAnalyzed Code:\n```\n$attachedCode\n```"
                } else {
                    cleanPrompt
                }

                val result = model.generateContent("$systemPrompt\n\nUser: $promptWithCode\nAssistant:")
                val text = result.text
                if (!text.isNullOrBlank()) {
                    finalResponse = text
                }
            } catch (e: Exception) {
                Log.w("ConversationalEngine", "Model generation error, falling back to instant factual synthesizer", e)
            }
        }

        // Fallback: Low-latency instant factual response synthesis
        if (finalResponse.isBlank()) {
            finalResponse = if (searchMatch != null) {
                buildString {
                    append("### ${searchMatch.title}\n\n")
                    append("${searchMatch.summary}\n\n")
                    append("**Technical Analysis:**\n${searchMatch.details}\n\n")
                    if (searchMatch.codeSnippet != null) {
                        append("**Verified Command / Code:**\n```bash\n${searchMatch.codeSnippet}\n```\n\n")
                    }
                    if (searchMatch.remediation != null) {
                        append("**Defensive Remediation:**\n${searchMatch.remediation}\n\n")
                    }
                    append("💡 *Factual Source:* ${searchMatch.referenceSource}")
                }
            } else if (knowledgeConcept != null) {
                buildString {
                    append("### ${knowledgeConcept.title}\n\n")
                    append("${knowledgeConcept.summary}\n\n")
                    append("**Technical Details:**\n${knowledgeConcept.technicalDetails}\n\n")
                    append("**Standard Commands:**\n```bash\n${knowledgeConcept.commands.joinToString("\n")}\n```\n\n")
                    append("**Defensive Remediation:**\n${knowledgeConcept.defensiveRemediation}")
                }
            } else {
                SecurityKnowledgeBase.getMentorResponse(cleanPrompt)
            }
        }

        // Record conversational turn for continuous context
        val turn = ConversationalTurn(
            userQuery = cleanPrompt,
            aiResponse = finalResponse,
            matchedSearchItem = searchMatch,
            followUpSuggestions = followUps
        )
        conversationHistory.add(turn)

        SynthesisResult(
            fullResponse = finalResponse,
            matchedSearchItem = searchMatch,
            severity = searchMatch?.severity ?: SecuritySeverity.INFO,
            followUpSuggestions = followUps
        )
    }

    /**
     * Streams the synthesized response word-by-word with ultra-low latency for human-like typing effect.
     */
    fun streamResponse(fullText: String): Flow<String> = flow {
        val words = fullText.split(" ")
        val sb = StringBuilder()
        for (i in words.indices) {
            sb.append(words[i]).append(" ")
            if (i % 3 == 0 || i == words.size - 1) {
                emit(sb.toString().trim())
                delay(20) // 20ms human-like conversational typing cadence
            }
        }
    }.flowOn(Dispatchers.Default)

    fun clearHistory() {
        conversationHistory.clear()
    }
}
