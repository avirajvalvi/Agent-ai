package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ConversationalEngine
import com.example.data.models.ChatMessage
import com.example.data.models.SecuritySeverity
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ChatViewModel that uses the Room database to render and display
 * the AI assistant's conversation history in real-time.
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChatRepository.getInstance(application)
    private val conversationalEngine = ConversationalEngine(application)

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _isVoiceListening = MutableStateFlow(false)
    val isVoiceListening: StateFlow<Boolean> = _isVoiceListening.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _streamingMessage = MutableStateFlow<ChatMessage?>(null)
    val streamingMessage: StateFlow<ChatMessage?> = _streamingMessage.asStateFlow()

    private val _followUpSuggestions = MutableStateFlow<List<String>>(
        listOf(
            "What is an Nmap SYN scan?",
            "How to patch CVE-2024-6387 regreSSHion?",
            "Audit Linux SSH /var/log/auth.log",
            "Show OWASP Top 10 defenses"
        )
    )
    val followUpSuggestions: StateFlow<List<String>> = _followUpSuggestions.asStateFlow()

    /**
     * Real-time Room database count of stored conversation turns.
     */
    val messageCount: StateFlow<Int> = repository.messageCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    /**
     * Real-time Room database flow of chat messages.
     * Filtered in real-time if a search query is active.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val displayedMessages: StateFlow<List<ChatMessage>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.realTimeChatHistory
            } else {
                repository.searchHistory(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Backward-compatibility alias for screens observing `messages`
    val messages: StateFlow<List<ChatMessage>> = displayedMessages

    init {
        seedInitialMessageIfNeeded()
    }

    private fun seedInitialMessageIfNeeded() {
        viewModelScope.launch {
            // Check if Room has any messages, if not insert welcome mentor message
            repository.realTimeChatHistory.collect { list ->
                if (list.isEmpty()) {
                    val welcomeMsg = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        text = "नमस्ते! मैं आपका **SecMentor AI** हूँ। GitHub के ओपन-सोर्स साइबर सुरक्षा टूल्स (Nmap, Wireshark, SQLMap) का विश्लेषण, सैंडबॉक्स टेस्टिंग और कस्टम टूल डेवलपमेंट सीखने के लिए मुझसे कोई भी सवाल पूछें।",
                        isUser = false,
                        tags = listOf("Welcome", "Defensive Security", "Room Database")
                    )
                    repository.insertMessage(welcomeMsg)
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }

    fun sendMessage(
        userText: String,
        attachedCode: String? = null,
        attachedFileName: String? = null
    ) {
        if (userText.isBlank() && attachedCode.isNullOrBlank()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = userText,
            isUser = true,
            attachedCode = attachedCode,
            attachedFileName = attachedFileName
        )

        _isGenerating.value = true

        viewModelScope.launch {
            // 1. Immediately persist to Room database (triggers real-time UI emission)
            repository.insertMessage(userMessage)

            // 2. Synthesize factual response via ConversationalEngine
            val synthesis = conversationalEngine.processTurn(userText, attachedCode)
            _followUpSuggestions.value = synthesis.followUpSuggestions

            val aiMessageId = UUID.randomUUID().toString()
            var currentAiText = ""

            val placeholderAiMessage = ChatMessage(
                id = aiMessageId,
                text = "",
                isUser = false,
                securitySeverity = synthesis.severity,
                tags = synthesis.matchedSearchItem?.let { listOf(it.category, it.title) } ?: listOf("Defensive Security", "Room DB")
            )
            _streamingMessage.value = placeholderAiMessage

            // 3. Stream response tokens with continuous low latency
            conversationalEngine.streamResponse(synthesis.fullResponse).collect { chunk ->
                currentAiText = chunk
                _streamingMessage.value = placeholderAiMessage.copy(text = currentAiText)
            }

            _isGenerating.value = false
            _streamingMessage.value = null

            // 4. Save completed response to Room database
            val finalAiMessage = placeholderAiMessage.copy(text = synthesis.fullResponse)
            repository.insertMessage(finalAiMessage)
        }
    }

    fun deleteMessage(id: String) {
        viewModelScope.launch {
            repository.deleteMessage(id)
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearAllMessages()
            // Re-seed welcome prompt
            val welcomeMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                text = "नमस्ते! मैं आपका **SecMentor AI** हूँ। GitHub के ओपन-सोर्स साइबर सुरक्षा टूल्स (Nmap, Wireshark, SQLMap) का विश्लेषण, सैंडबॉक्स टेस्टिंग और कस्टम टूल डेवलपमेंट सीखने के लिए मुझसे कोई भी सवाल पूछें।",
                isUser = false,
                tags = listOf("Welcome", "Defensive Security", "Room Database")
            )
            repository.insertMessage(welcomeMsg)
        }
    }

    fun setVoiceListening(listening: Boolean) {
        _isVoiceListening.value = listening
    }
}
