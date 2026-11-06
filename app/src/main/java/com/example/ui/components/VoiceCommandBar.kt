package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.voice.VoiceCommand
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.TerracottaPrimary

@Composable
fun VoiceCommandBar(
    isListening: Boolean,
    transcript: String,
    rmsVolume: Float,
    detectedCommand: VoiceCommand?,
    onStopListening: () -> Unit,
    onSubmitCommand: (String, VoiceCommand) -> Unit,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        color = Color(0xFF22201D),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(1.5.dp, TerracottaPrimary.copy(alpha = if (isListening) pulseAlpha else 0.4f), RoundedCornerShape(22.dp))
            .testTag("voice_command_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Status indicator + Live Audio Waveform + Cancel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isListening) TerracottaPrimary else Color(0xFFA8A29E))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isListening) "Listening to your voice..." else "Voice input captured",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFAF8F5)
                        )
                    )
                }

                // Dynamic Sound Wave Bars responding to rmsVolume
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val heights = listOf(10f, 18f, 26f, 20f, 12f)
                    heights.forEachIndexed { i, h ->
                        val dynamicMultiplier = if (isListening) {
                            (0.3f + rmsVolume * 0.9f + (i % 2) * 0.15f)
                        } else {
                            0.25f
                        }
                        val barH = (h * dynamicMultiplier).coerceIn(6f, 30f).dp

                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(barH)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isListening) TerracottaPrimary else Color(0xFF78716C))
                        )
                    }
                }

                IconButton(
                    onClick = onCancel,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel Voice Mode",
                        tint = Color(0xFFA8A29E),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Live Transcript Display
            Text(
                text = if (transcript.isBlank()) "Speak commands like \"Clear chat\", \"Run nmap\", or ask a question..." else "\"$transcript\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (transcript.isBlank()) Color(0xFFA8A29E) else Color(0xFFFAF8F5),
                    fontSize = 15.sp,
                    fontWeight = if (transcript.isNotBlank()) FontWeight.Medium else FontWeight.Normal
                ),
                maxLines = 3
            )

            // Detected Command Badge
            detectedCommand?.let { cmd ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (cmd) {
                        is VoiceCommand.ClearChat -> {
                            VoiceIntentPill(
                                icon = Icons.Default.Close,
                                label = "Voice Action: Clear Chat",
                                color = CyberAmber
                            )
                        }
                        is VoiceCommand.RunTerminal -> {
                            VoiceIntentPill(
                                icon = Icons.Default.Terminal,
                                label = "Voice Action: Run \"${cmd.command}\" in Sandbox",
                                color = CyberEmerald
                            )
                        }
                        is VoiceCommand.OpenTerminal -> {
                            VoiceIntentPill(
                                icon = Icons.Default.Terminal,
                                label = "Voice Action: Open Terminal Sandbox",
                                color = CyberEmerald
                            )
                        }
                        is VoiceCommand.BuildApk -> {
                            VoiceIntentPill(
                                icon = Icons.Default.PlayArrow,
                                label = "Voice Action: Trigger Termux APK Build",
                                color = CyberCyan
                            )
                        }
                        is VoiceCommand.SearchWebOrCve -> {
                            VoiceIntentPill(
                                icon = Icons.Default.Search,
                                label = "Voice Action: Web/CVE Search for \"${cmd.query}\"",
                                color = CyberCyan
                            )
                        }
                        is VoiceCommand.GeneralQuery -> {
                            VoiceIntentPill(
                                icon = Icons.Default.Mic,
                                label = "Query: Ask SecMentor AI",
                                color = TerracottaPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions Row: Stop / Resume listening + Send to Assistant
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onStopListening,
                    shape = RoundedCornerShape(14.dp),
                    color = if (isListening) Color(0xFF332F2A) else TerracottaPrimary,
                    modifier = Modifier.testTag("voice_toggle_listening_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isListening) "Pause Listening" else "Resume Speaking",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                if (transcript.isNotBlank()) {
                    Surface(
                        onClick = {
                            val cmd = detectedCommand ?: VoiceCommand.GeneralQuery(transcript)
                            onSubmitCommand(transcript, cmd)
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = TerracottaPrimary,
                        modifier = Modifier.testTag("voice_submit_command_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Execute Command",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Execute",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceIntentPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, color = color, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold))
        }
    }
}
