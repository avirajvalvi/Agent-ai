package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.VideoStyle
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.TerracottaPrimary
import com.example.ui.viewmodels.VideoStudioViewModel

@Composable
fun VideoStudioScreen(
    videoStudioViewModel: VideoStudioViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val currentProject by videoStudioViewModel.currentProject.collectAsState()
    val selectedSceneIndex by videoStudioViewModel.selectedSceneIndex.collectAsState()
    val isPlaying by videoStudioViewModel.isPlaying.collectAsState()
    val currentPlayTimeSec by videoStudioViewModel.currentPlaybackSecond.collectAsState()
    val isRendering by videoStudioViewModel.isRendering.collectAsState()
    val renderProgress by videoStudioViewModel.renderProgress.collectAsState()

    var showNewVideoDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("Autonomous Cybersecurity Deep-Dive") }
    var targetDuration by remember { mutableStateOf(45) } // 30 or 60 min
    var selectedStyle by remember { mutableStateOf(VideoStyle.CINEMATIC_AI) }
    var scriptInput by remember { mutableStateOf("Text & Audio narration for complete 45-minute modular walkthrough on ethical defense and code sandboxing...") }

    val activeScene = currentProject.scenes.getOrNull(selectedSceneIndex)
    val totalProjectSeconds = currentProject.scenes.sumOf { it.durationSeconds }.coerceAtLeast(1)

    // Cinematic moving clouds & volumetric fog animation
    val infiniteTransition = rememberInfiniteTransition(label = "fogAnimation")
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloudDrift"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141312))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("video_studio_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Drawer",
                            tint = Color(0xFFFAF8F5)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Video Studio",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            color = Color(0xFFFAF8F5)
                        )
                    )
                }

                Button(
                    onClick = { showNewVideoDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Video (30-60m)", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Rendering Progress Banner
            if (isRendering) {
                Surface(
                    color = Color(0xFF262422),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Rendering Long-Form Video Engine...",
                                style = MaterialTheme.typography.labelSmall.copy(color = TerracottaPrimary)
                            )
                            Text(
                                text = "${(renderProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { renderProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = TerracottaPrimary,
                            trackColor = Color(0xFF383531)
                        )
                    }
                }
            }

            // Video Player Preview Canvas (Simulating Volumetric Fog, Cinematic Lighting & Animation)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black)
                    .border(1.dp, Color(0xFF2E2C29), RoundedCornerShape(14.dp))
            ) {
                // Procedural Canvas Visualizer matching user prompt:
                // "High resolution, hyper-realistic moving clouds, soft volumetric fog, dreamy atmosphere, cinematic lighting, slow motion effect."
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Deep atmospheric background gradient
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E293B), // Deep slate blue
                                Color(0xFF0F172A), // Midnight
                                Color(0xFF020617)  // Deep shadow
                            )
                        )
                    )

                    // Cinematic volumetric light rays
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFED7AA).copy(alpha = 0.35f), // Warm gold ray
                                Color(0xFFFB923C).copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.7f, h * 0.2f),
                            radius = w * 0.6f
                        )
                    )

                    // Soft volumetric moving clouds simulation
                    val cloudShift = cloudOffset * w
                    for (i in 0..4) {
                        val cx = ((i * 220f + cloudShift) % (w + 400f)) - 200f
                        val cy = h * (0.35f + (i % 3) * 0.15f)
                        val radius = 120f + (i % 2) * 60f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.08f),
                                    Color(0xFF94A3B8).copy(alpha = 0.04f),
                                    Color.Transparent
                                ),
                                center = Offset(cx, cy),
                                radius = radius
                            )
                        )
                    }

                    // Cyber HUD or animated overlays if in Cyber or Animated style
                    if (currentProject.style == VideoStyle.CYBER_SECURITY) {
                        drawLine(
                            color = CyberEmerald.copy(alpha = 0.4f),
                            start = Offset(0f, h * 0.82f),
                            end = Offset(w, h * 0.82f),
                            strokeWidth = 1.5f
                        )
                    }
                }

                // Top Left Overlay: Scene Index & Duration
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = null, tint = TerracottaPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Scene ${selectedSceneIndex + 1}/${currentProject.scenes.size} • ${currentProject.targetDurationMinutes} Min Long-Form",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 11.sp)
                        )
                    }
                }

                // Subtitle Overlay at Bottom of Video Canvas
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Text(
                            text = activeScene?.subtitleText ?: activeScene?.scriptNarration?.take(60) ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFFAF8F5),
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Video Playback Controls Bar
            Surface(
                color = Color(0xFF1B1A18),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                    // Scrubber Slider
                    Slider(
                        value = currentPlayTimeSec.toFloat(),
                        onValueChange = { videoStudioViewModel.seekToSecond(it.toInt()) },
                        valueRange = 0f..totalProjectSeconds.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = TerracottaPrimary,
                            activeTrackColor = TerracottaPrimary,
                            inactiveTrackColor = Color(0xFF383531)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Current time display (formatted min:sec)
                        val curMin = currentPlayTimeSec / 60
                        val curSec = currentPlayTimeSec % 60
                        val totMin = totalProjectSeconds / 60
                        val totSec = totalProjectSeconds % 60
                        Text(
                            text = String.format("%02d:%02d / %02d:%02d", curMin, curSec, totMin, totSec),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFA8A29E)
                            )
                        )

                        // Transport Buttons: Rewind, Play/Pause, Fast Forward
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    videoStudioViewModel.seekToSecond((currentPlayTimeSec - 15).coerceAtLeast(0))
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.FastRewind, contentDescription = "Rewind", tint = Color.White)
                            }

                            Surface(
                                onClick = { videoStudioViewModel.togglePlayPause() },
                                shape = CircleShape,
                                color = TerracottaPrimary,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    videoStudioViewModel.seekToSecond((currentPlayTimeSec + 15).coerceAtMost(totalProjectSeconds))
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.FastForward, contentDescription = "Forward", tint = Color.White)
                            }
                        }

                        // Style indicator
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF262422)
                        ) {
                            Text(
                                text = currentProject.style.displayName.take(15),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberCyan,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Timeline Clips Track & Active Scene Editor
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Sequence Timeline (${currentProject.scenes.size} Scenes):",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFFA8A29E),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Horizontal Timeline Clips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currentProject.scenes.forEachIndexed { idx, scene ->
                            val isSelected = idx == selectedSceneIndex
                            Surface(
                                onClick = { videoStudioViewModel.selectScene(idx) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF2E2B27) else Color(0xFF1E1D1B),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, TerracottaPrimary) else null,
                                modifier = Modifier.width(135.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Scene ${scene.sceneNumber}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) TerracottaPrimary else Color(0xFFA8A29E),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = scene.title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.5.sp
                                        ),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${scene.durationSeconds / 60}m ${scene.durationSeconds % 60}s",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = CyberEmerald,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Active Scene Editor Card
                activeScene?.let { scene ->
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF201E1C))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Edit Scene ${scene.sceneNumber}: ${scene.title}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFFAF8F5),
                                            fontSize = 15.sp
                                        )
                                    )

                                    // Trim Buttons
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            onClick = { videoStudioViewModel.trimSceneDuration(-30) },
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF2C2A28),
                                            modifier = Modifier.padding(end = 4.dp)
                                        ) {
                                            Text("-30s", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                        }
                                        Surface(
                                            onClick = { videoStudioViewModel.trimSceneDuration(30) },
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF2C2A28)
                                        ) {
                                            Text("+30s", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Audio Script / Narration Input
                                Text(
                                    text = "Audio Script & Voiceover Narration:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E))
                                )
                                OutlinedTextField(
                                    value = scene.scriptNarration,
                                    onValueChange = { videoStudioViewModel.updateSceneNarration(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF141312),
                                        unfocusedContainerColor = Color(0xFF141312),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    maxLines = 3
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Visual Prompt & Volumetric Atmosphere Input
                                Text(
                                    text = "Visual Generation & Volumetric Lighting Prompt:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E))
                                )
                                OutlinedTextField(
                                    value = scene.visualPrompt,
                                    onValueChange = { videoStudioViewModel.updateSceneVisualPrompt(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF141312),
                                        unfocusedContainerColor = Color(0xFF141312),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }

                // Export Full Long-Form Video Action
                item {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Rendering ${currentProject.targetDurationMinutes}-minute master video...", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Export ${currentProject.targetDurationMinutes}-Minute Master Video (1080p)",
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Generate Long-form Video Modal Dialog (30 to 60 Minutes)
        if (showNewVideoDialog) {
            AlertDialog(
                onDismissRequest = { showNewVideoDialog = false },
                title = { Text("Generate Long-Form Video (30 - 60 Min)", color = Color.White) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Video Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        // Target Duration Selector (30 Min vs 45 Min vs 60 Min)
                        Text("Target Duration:", style = MaterialTheme.typography.labelSmall, color = Color(0xFFA8A29E))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(30, 45, 60).forEach { mins ->
                                Surface(
                                    onClick = { targetDuration = mins },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (targetDuration == mins) TerracottaPrimary else Color(0xFF2C2A28)
                                ) {
                                    Text(
                                        text = "$mins Minutes",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Style Selector
                        Text("Visual Style:", style = MaterialTheme.typography.labelSmall, color = Color(0xFFA8A29E))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            VideoStyle.values().forEach { st ->
                                Surface(
                                    onClick = { selectedStyle = st },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectedStyle == st) CyberCyan.copy(alpha = 0.3f) else Color(0xFF2C2A28),
                                    border = if (selectedStyle == st) androidx.compose.foundation.BorderStroke(1.dp, CyberCyan) else null
                                ) {
                                    Text(
                                        text = st.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Text & Audio Script
                        OutlinedTextField(
                            value = scriptInput,
                            onValueChange = { scriptInput = it },
                            label = { Text("Text / Audio Script") },
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            videoStudioViewModel.generateNewLongFormVideo(
                                title = newTitle,
                                targetDurationMinutes = targetDuration,
                                style = selectedStyle,
                                rawScriptText = scriptInput
                            )
                            showNewVideoDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                    ) {
                        Text("Generate Video")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNewVideoDialog = false }) {
                        Text("Cancel", color = Color(0xFFA8A29E))
                    }
                },
                containerColor = Color(0xFF242220)
            )
        }
    }
}
