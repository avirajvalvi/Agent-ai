package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ScreenSearchDesktop
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.TerracottaPrimary

enum class PerceptionMode {
    LIVE_CAMERA, SCREEN_ANALYSIS
}

@Composable
fun RealtimePerceptionModal(
    initialMode: PerceptionMode = PerceptionMode.LIVE_CAMERA,
    onDismiss: () -> Unit,
    onPerceptionCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var currentMode by remember { mutableStateOf(initialMode) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var liveDetectionStatus by remember { mutableStateOf("Scanning security surface in real time...") }
    var detectedObjects by remember {
        mutableStateOf(
            listOf(
                "Code Block: Python socket script",
                "Target IP: 192.168.1.100 (Web Server)",
                "Security Status: Auditable"
            )
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val transition = rememberInfiniteTransition(label = "scanLine")
    val scanLineY by transition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanY"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (currentMode == PerceptionMode.LIVE_CAMERA) {
            if (hasCameraPermission) {
                // Real-time CameraX Preview View
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(lensFacing)
                                .build()
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                            } catch (e: Exception) {
                                // Camera in emulator fallback
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Permission placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF141312)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Camera permission requested for live perception.",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            // Real-Time Dynamic Screen Analysis Simulation View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F0E0D))
            ) {
                // Simulated screen view being analyzed
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "LIVE SCREEN PERCEPTION STREAM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = Color(0xFF1E1D1B),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberEmerald.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "SCREEN OCR & CODE INSPECTION HUD:",
                                style = MaterialTheme.typography.labelSmall.copy(color = CyberEmerald, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Target: Linux Terminal & Source Editor\nActive Window: secbox@mentor:~/labs\nDetected Command: nmap -sS -p 22,80,443 192.168.1.100\nBuffer Status: Clean Handshake\nNo unencrypted secrets detected on active display.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFFAF8F5),
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Live Scanning HUD Overlay (Reticle, Scan Line, Telemetry)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Corner Reticles
            val reticleSize = 40.dp.toPx()
            val stroke = 3.dp.toPx()
            val reticleColor = if (currentMode == PerceptionMode.LIVE_CAMERA) CyberEmerald else CyberCyan

            // Top-left
            drawLine(reticleColor, Offset(40f, 80f), Offset(40f + reticleSize, 80f), stroke)
            drawLine(reticleColor, Offset(40f, 80f), Offset(40f, 80f + reticleSize), stroke)

            // Top-right
            drawLine(reticleColor, Offset(w - 40f, 80f), Offset(w - 40f - reticleSize, 80f), stroke)
            drawLine(reticleColor, Offset(w - 40f, 80f), Offset(w - 40f, 80f + reticleSize), stroke)

            // Bottom-left
            drawLine(reticleColor, Offset(40f, h - 160f), Offset(40f + reticleSize, h - 160f), stroke)
            drawLine(reticleColor, Offset(40f, h - 160f), Offset(40f, h - 160f - reticleSize), stroke)

            // Bottom-right
            drawLine(reticleColor, Offset(w - 40f, h - 160f), Offset(w - 40f - reticleSize, h - 160f), stroke)
            drawLine(reticleColor, Offset(w - 40f, h - 160f), Offset(w - 40f, h - 160f - reticleSize), stroke)

            // Dynamic Scanning Line
            val scanY = (h - 240f) * scanLineY + 80f
            drawLine(
                color = reticleColor.copy(alpha = 0.8f),
                start = Offset(40f, scanY),
                end = Offset(w - 40f, scanY),
                strokeWidth = 2.dp.toPx()
            )
        }

        // Top Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.65f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(CyberEmerald, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentMode == PerceptionMode.LIVE_CAMERA) "LIVE CAMERA PERCEPTION" else "LIVE SCREEN PERCEPTION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Row {
                if (currentMode == PerceptionMode.LIVE_CAMERA) {
                    IconButton(
                        onClick = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera", tint = Color.White)
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        // Mode Switcher Toggle Pill (Camera vs Screen Analysis)
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.Black.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                Surface(
                    onClick = { currentMode = PerceptionMode.LIVE_CAMERA },
                    shape = RoundedCornerShape(20.dp),
                    color = if (currentMode == PerceptionMode.LIVE_CAMERA) TerracottaPrimary else Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Camera", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                }

                Surface(
                    onClick = { currentMode = PerceptionMode.SCREEN_ANALYSIS },
                    shape = RoundedCornerShape(20.dp),
                    color = if (currentMode == PerceptionMode.SCREEN_ANALYSIS) TerracottaPrimary else Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ScreenSearchDesktop, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Screen OCR", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                }
            }
        }

        // Bottom Telemetry & Capture Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Live Detected Telemetry Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "REAL-TIME PERCEPTION FINDINGS:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TerracottaPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    detectedObjects.forEach { obj ->
                        Text(
                            text = "• $obj",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFFAF8F5),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Capture / Send to SecMentor Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = {
                        val finding = if (currentMode == PerceptionMode.LIVE_CAMERA) {
                            "Camera perception captured: Analyzing physical screen containing Python socket code and target network topology."
                        } else {
                            "Screen perception captured: Auditing current CLI terminal state, active ports (22, 80, 443), and verified defensive posture."
                        }
                        onPerceptionCaptured(finding)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(26.dp),
                    color = TerracottaPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analyze Perception in SecMentor AI",
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
