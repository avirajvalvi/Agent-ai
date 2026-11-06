package com.example.ui.components

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.integration.ExternalAppsManager
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.TerracottaPrimary
import java.util.Locale

@Composable
fun VoiceLiveModal(
    onDismiss: () -> Unit,
    onVoiceResult: (String) -> Unit
) {
    val context = LocalContext.current
    val externalApps = remember { ExternalAppsManager(context) }

    var isListening by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("Listening to your voice... Speak your security inquiry") }
    var currentQuery by remember { mutableStateOf("") }
    var rmsVolume by remember { mutableFloatStateOf(0f) }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    var showPerceptionModal by remember { mutableStateOf(false) }
    var perceptionMode by remember { mutableStateOf(PerceptionMode.LIVE_CAMERA) }
    var lastAutonomousAction by remember { mutableStateOf<String?>(null) }

    fun startListeningInternal(recognizer: SpeechRecognizer?) {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            recognizer?.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            recognizedText = "Tap quick queries or mic to speak."
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isListening = true
            try {
                val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer = recognizer

                recognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        recognizedText = "Ready! Speak your question..."
                    }
                    override fun onBeginningOfSpeech() {
                        recognizedText = "Hearing your voice..."
                    }
                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize RMS dB typically -2dB to 10dB
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1.0f)
                        rmsVolume = normalized
                    }
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        isListening = false
                    }
                    override fun onError(error: Int) {
                        isListening = false
                        if (currentQuery.isEmpty()) {
                            recognizedText = "Voice ready. Speak or tap any action below."
                        }
                    }
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val query = matches[0]
                            currentQuery = query
                            recognizedText = "\"$query\""

                            // Autonomous Intent Processing:
                            val qLower = query.lowercase()
                            if (qLower.contains("build apk") || qLower.contains("termux") || qLower.contains("टर्मक्स")) {
                                lastAutonomousAction = "Triggered Termux APK build autonomously!"
                                externalApps.triggerTermuxApkBuild("VoiceApp", null)
                                Toast.makeText(context, "Termux build script dispatched!", Toast.LENGTH_SHORT).show()
                            } else if (qLower.contains("youtube") || qLower.contains("video") || qLower.contains("tutorial") || qLower.contains("वीडियो")) {
                                lastAutonomousAction = "Launched YouTube video exploration for \"$query\""
                                externalApps.searchYouTube(query)
                            } else if (qLower.contains("search") || qLower.contains("web") || qLower.contains("google") || qLower.contains("cve") || qLower.contains("खोज")) {
                                lastAutonomousAction = "Executing real-time web search for \"$query\""
                                externalApps.performWebSearch(query)
                            }
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val partial = matches[0]
                            currentQuery = partial
                            recognizedText = partial
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                startListeningInternal(recognizer)
            } catch (e: Exception) {
                recognizedText = "Voice ready: Tap quick prompts or mic."
            }
        } else {
            recognizedText = "Microphone access denied. You can still use quick action buttons."
        }
    }

    DisposableEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        onDispose {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.destroy()
            } catch (e: Exception) {}
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Full screen overlay matching screenshot 2
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF151413),
                        Color(0xFF1E1D1B),
                        Color(0xFF262422)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("voice_live_modal")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("voice_modal_back_button")
            ) {
                Box(modifier = Modifier.size(24.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawLine(
                            color = Color(0xFFD6D3D1),
                            start = Offset(0f, 6.dp.toPx()),
                            end = Offset(size.width, 6.dp.toPx()),
                            strokeWidth = 2.dp.toPx()
                        )
                        drawLine(
                            color = Color(0xFFD6D3D1),
                            start = Offset(0f, 16.dp.toPx()),
                            end = Offset(size.width, 16.dp.toPx()),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        perceptionMode = PerceptionMode.SCREEN_ANALYSIS
                        showPerceptionModal = true
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ScreenShare,
                        contentDescription = "Screen Perception",
                        tint = Color(0xFFD6D3D1)
                    )
                }

                IconButton(
                    onClick = {},
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = Color(0xFFD6D3D1)
                    )
                }
            }
        }

        // Center Content: Colorful 4-pointed Gemini/Claude Star and "Ready when you are"
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Multimodal 4-pointed Star Canvas
            Canvas(
                modifier = Modifier
                    .size((74 * scale).dp)
                    .clickable {
                        // Quick demo voice trigger
                        val demoQuery = "Search web for latest Nmap CVE vulnerability exploits"
                        currentQuery = demoQuery
                        recognizedText = demoQuery
                        externalApps.performWebSearch(demoQuery)
                        onVoiceResult(demoQuery)
                        onDismiss()
                    }
            ) {
                val w = size.width
                val h = size.height
                val midX = w / 2f
                val midY = h / 2f

                val path = Path().apply {
                    moveTo(midX, 0f)
                    cubicTo(midX, midY * 0.5f, midX + w * 0.25f, midY, w, midY)
                    cubicTo(midX + w * 0.25f, midY, midX, midY * 1.5f, midX, h)
                    cubicTo(midX, midY * 1.5f, midX - w * 0.25f, midY, 0f, midY)
                    cubicTo(midX - w * 0.25f, midY, midX, midY * 0.5f, midX, 0f)
                    close()
                }

                drawPath(
                    path = path,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF60A5FA), // Blue
                            Color(0xFF34D399), // Emerald
                            Color(0xFFFBBF24), // Amber
                            Color(0xFFF43F5E)  // Coral
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(w, h)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ready when you are",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 32.sp,
                    color = Color(0xFFFAF8F5)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = recognizedText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isListening) CyberCyan else Color(0xFFA8A29E),
                    fontSize = 15.sp
                ),
                maxLines = 3
            )

            // Autonomous Action Feedback Banner
            lastAutonomousAction?.let { actionMsg ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E2D24),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚡ $actionMsg",
                        color = CyberEmerald,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Triggers when query is captured
            AnimatedVisibility(visible = currentQuery.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Web Search Trigger
                    Surface(
                        onClick = {
                            externalApps.performWebSearch(currentQuery)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E2A38),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("voice_trigger_web_search")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Web Search", color = Color(0xFFFAF8F5), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Termux Build Trigger
                    Surface(
                        onClick = {
                            val res = externalApps.triggerTermuxApkBuild("VoiceBuiltApp", null)
                            Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E2D24),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("voice_trigger_termux_build")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Android, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Termux Build", color = Color(0xFFFAF8F5), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    // YouTube Trigger
                    Surface(
                        onClick = {
                            externalApps.searchYouTube(currentQuery)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF381E1E),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("voice_trigger_youtube")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("YouTube", color = Color(0xFFFAF8F5), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick suggestion chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    onClick = {
                        val q = "Search web for Nmap TCP SYN scan options and defense"
                        currentQuery = q
                        externalApps.performWebSearch(q)
                        onVoiceResult(q)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF2C2A28),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = "🌐 Web Search: Nmap SYN",
                        color = Color(0xFFFAF8F5),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                Surface(
                    onClick = {
                        val q = "Autonomous Termux APK build setup"
                        currentQuery = q
                        externalApps.triggerTermuxApkBuild("SecMentorApk", null)
                        Toast.makeText(context, "Termux build script dispatched!", Toast.LENGTH_SHORT).show()
                        onVoiceResult(q)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF2C2A28),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = "🤖 Build APK in Termux",
                        color = Color(0xFFFAF8F5),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Bottom Navigation Bar matching the pill controls in Screenshot 2
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live Camera Perception Button
            Surface(
                onClick = {
                    perceptionMode = PerceptionMode.LIVE_CAMERA
                    showPerceptionModal = true
                },
                shape = CircleShape,
                color = Color(0xFF2B2A27),
                modifier = Modifier
                    .size(54.dp)
                    .testTag("voice_camera_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Live Camera",
                        tint = Color(0xFFFAF8F5),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Document / Script Upload Button
            Surface(
                onClick = {
                    recognizedText = "Analyzing code file (Python, C++, Bash, PCAP)..."
                    val q = "Explain network packet sniffer script logic in Python"
                    currentQuery = q
                    onVoiceResult(q)
                    onDismiss()
                },
                shape = CircleShape,
                color = Color(0xFF2B2A27),
                modifier = Modifier
                    .size(54.dp)
                    .testTag("voice_upload_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = "Upload Document",
                        tint = Color(0xFFFAF8F5),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Glowing Animated Voice Pill with Real-Time RMS Waveform
            Surface(
                onClick = {
                    if (isListening) {
                        try {
                            speechRecognizer?.stopListening()
                        } catch (e: Exception) {}
                        isListening = false
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFFFAF8F5),
                modifier = Modifier
                    .height(54.dp)
                    .width(110.dp)
                    .testTag("voice_wave_pill")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFE2E8F0)
                                )
                            )
                        )
                ) {
                    // Animated Sound Wave bars reacting to live RMS volume
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val baseHeights = listOf(14f, 26f, 34f, 22f, 12f)
                        baseHeights.forEachIndexed { i, bh ->
                            val dynamicMultiplier = if (isListening) {
                                (0.4f + rmsVolume * 0.8f + (i % 2) * 0.15f)
                            } else {
                                0.35f
                            }
                            val barHeight = (bh * dynamicMultiplier).coerceIn(8f, 40f).dp

                            Box(
                                modifier = Modifier
                                    .width(3.5.dp)
                                    .height(barHeight)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(TerracottaPrimary)
                            )
                        }
                    }
                }
            }

            // Mic Toggle
            Surface(
                onClick = {
                    if (isListening) {
                        try {
                            speechRecognizer?.stopListening()
                        } catch (e: Exception) {}
                        isListening = false
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                shape = CircleShape,
                color = if (isListening) TerracottaPrimary else Color(0xFF2B2A27),
                modifier = Modifier
                    .size(54.dp)
                    .testTag("voice_mic_toggle_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Toggle Mic",
                        tint = Color(0xFFFAF8F5),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Close 'X' Button
            Surface(
                onClick = onDismiss,
                shape = CircleShape,
                color = Color(0xFF2B2A27),
                modifier = Modifier
                    .size(54.dp)
                    .testTag("voice_close_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Voice Mode",
                        tint = Color(0xFFFAF8F5),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        if (showPerceptionModal) {
            RealtimePerceptionModal(
                initialMode = perceptionMode,
                onDismiss = { showPerceptionModal = false },
                onPerceptionCaptured = { finding ->
                    showPerceptionModal = false
                    onVoiceResult(finding)
                    onDismiss()
                }
            )
        }
    }
}
