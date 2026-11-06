package com.example.data.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class VoiceCommand {
    data class GeneralQuery(val query: String) : VoiceCommand()
    data object ClearChat : VoiceCommand()
    data class RunTerminal(val command: String) : VoiceCommand()
    data class SearchWebOrCve(val query: String) : VoiceCommand()
    data class BuildApk(val appName: String) : VoiceCommand()
    data object OpenTerminal : VoiceCommand()
}

class VoiceCommanderService(private val context: Context) : RecognitionListener, TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _liveTranscript = MutableStateFlow("")
    val liveTranscript: StateFlow<String> = _liveTranscript.asStateFlow()

    private val _rmsVolume = MutableStateFlow(0f)
    val rmsVolume: StateFlow<Float> = _rmsVolume.asStateFlow()

    private val _detectedCommand = MutableStateFlow<VoiceCommand?>(null)
    val detectedCommand: StateFlow<VoiceCommand?> = _detectedCommand.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var onSpeechResultCallback: ((String, VoiceCommand) -> Unit)? = null

    private var isContinuousMode = false
    private val _isContinuous = MutableStateFlow(false)
    val isContinuous: StateFlow<Boolean> = _isContinuous.asStateFlow()

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("VoiceCommander", "Failed to initialize TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.getDefault())
            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED

            textToSpeech?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    if (isContinuousMode) {
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            startListening(onSpeechResultCallback ?: { _, _ -> })
                        }
                    }
                }
                override fun onError(utteranceId: String?) {
                    if (isContinuousMode) {
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            startListening(onSpeechResultCallback ?: { _, _ -> })
                        }
                    }
                }
            })
        }
    }

    fun setContinuousMode(enabled: Boolean) {
        isContinuousMode = enabled
        _isContinuous.value = enabled
        if (!enabled) {
            stopListening()
            stopSpeaking()
        }
    }

    fun startListening(onResult: (String, VoiceCommand) -> Unit) {
        onSpeechResultCallback = onResult
        _errorMessage.value = null
        _liveTranscript.value = ""
        _detectedCommand.value = null

        try {
            stopListening()
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer = recognizer
            recognizer.setRecognitionListener(this)

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                // Instruct Android speech recognizer to prefer local offline on-device acoustic models
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            }

            recognizer.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            Log.e("VoiceCommander", "Error starting speech recognizer", e)
            _isListening.value = false
            _errorMessage.value = "Speech recognition unavailable: ${e.localizedMessage}"
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Log.e("VoiceCommander", "Error stopping speech recognizer", e)
        } finally {
            _isListening.value = false
            _rmsVolume.value = 0f
        }
    }

    fun speak(text: String, onDoneSpeaking: (() -> Unit)? = null) {
        if (!isTtsReady || textToSpeech == null) {
            onDoneSpeaking?.invoke()
            return
        }
        val cleanText = text
            .replace(Regex("[#*`>]"), "")
            .replace(Regex("```[\\s\\S]*?```"), "Code block provided in chat.")
            .take(300)

        val utteranceId = "tts_utterance_${System.currentTimeMillis()}"
        textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
    }

    fun parseVoiceCommand(transcript: String): VoiceCommand {
        val q = transcript.trim().lowercase()

        return when {
            q.contains("clear chat") || q.contains("delete chat") || q.contains("चैट साफ") || q.contains("reset chat") -> {
                VoiceCommand.ClearChat
            }
            q.contains("open terminal") || q.contains("go to terminal") || q.contains("open sandbox") || q.contains("टर्मिनल") -> {
                VoiceCommand.OpenTerminal
            }
            q.contains("run nmap") || q.contains("scan localhost") || q.contains("port scan") -> {
                VoiceCommand.RunTerminal("nmap -sS -T4 127.0.0.1")
            }
            q.contains("run tshark") || q.contains("capture packets") -> {
                VoiceCommand.RunTerminal("tshark -i eth0 -c 10")
            }
            q.contains("build apk") || q.contains("termux build") || q.contains("compile app") -> {
                VoiceCommand.BuildApk("SecMentorApp")
            }
            q.startsWith("search cve") || q.contains("search vulnerability") || q.contains("search web for") -> {
                val term = transcript.substringAfter("for", transcript.substringAfter("cve", transcript)).trim()
                VoiceCommand.SearchWebOrCve(term.ifEmpty { transcript })
            }
            else -> {
                VoiceCommand.GeneralQuery(transcript)
            }
        }
    }

    // RecognitionListener Callbacks
    override fun onReadyForSpeech(params: Bundle?) {
        _isListening.value = true
    }

    override fun onBeginningOfSpeech() {
        _isListening.value = true
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Convert RMS dB to normalized value 0.0 to 1.0
        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.05f, 1.0f)
        _rmsVolume.value = normalized
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        _isListening.value = false
        _rmsVolume.value = 0f
    }

    override fun onError(error: Int) {
        _isListening.value = false
        _rmsVolume.value = 0f
        val msg = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Tap mic to speak again."
            SpeechRecognizer.ERROR_NETWORK -> "Network issue for cloud speech. Retrying locally."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
            else -> "Voice recognition stopped."
        }
        _errorMessage.value = msg
    }

    override fun onResults(results: Bundle?) {
        _isListening.value = false
        _rmsVolume.value = 0f
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val recognized = matches[0]
            _liveTranscript.value = recognized
            val command = parseVoiceCommand(recognized)
            _detectedCommand.value = command
            onSpeechResultCallback?.invoke(recognized, command)
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val partial = matches[0]
            _liveTranscript.value = partial
            _detectedCommand.value = parseVoiceCommand(partial)
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    fun destroy() {
        stopListening()
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (e: Exception) {}
    }
}
