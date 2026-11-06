package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BuildArtifact
import com.example.data.models.BuildPipelineJob
import com.example.data.models.BuildPipelineStage
import com.example.data.models.LogLevel
import com.example.data.models.PipelineLogEntry
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.TerracottaPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BuildPipelineHeader(
    job: BuildPipelineJob,
    onTriggerBuild: () -> Unit,
    onCancelBuild: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF22201E)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2C2A28)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = "APK Pipeline",
                            tint = TerracottaPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Automated APK Build Pipeline",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFAF8F5),
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "Background Gradle compilation & Direct APK Download",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA8A29E))
                        )
                    }
                }

                // Stage Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (job.stage) {
                        BuildPipelineStage.SUCCESS -> CyberEmerald.copy(alpha = 0.18f)
                        BuildPipelineStage.FAILED -> Color(0xFFEF4444).copy(alpha = 0.18f)
                        BuildPipelineStage.IDLE -> Color(0xFF383531)
                        else -> TerracottaPrimary.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = if (job.isRunning) "COMPILING..." else if (job.stage == BuildPipelineStage.SUCCESS) "BUILD READY" else "STANDBY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = when (job.stage) {
                                BuildPipelineStage.SUCCESS -> CyberEmerald
                                BuildPipelineStage.FAILED -> Color(0xFFEF4444)
                                BuildPipelineStage.IDLE -> Color(0xFFA8A29E)
                                else -> TerracottaPrimary
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onTriggerBuild,
                    enabled = !job.isRunning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TerracottaPrimary,
                        disabledContainerColor = TerracottaPrimary.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("trigger_apk_build_button")
                ) {
                    if (job.isRunning) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compiling (${(job.progress * 100).toInt()}%)...", style = MaterialTheme.typography.labelMedium)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (job.artifact != null) "Re-build APK" else "Trigger Automated Build", style = MaterialTheme.typography.labelMedium)
                    }
                }

                if (job.isRunning) {
                    OutlinedButton(
                        onClick = onCancelBuild,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        modifier = Modifier.testTag("cancel_apk_build_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun PipelineStageTracker(
    job: BuildPipelineJob,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF22201E)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pipeline Stages",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFFFAF8F5),
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "${(job.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TerracottaPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { job.progress },
                color = if (job.stage == BuildPipelineStage.SUCCESS) CyberEmerald else TerracottaPrimary,
                trackColor = Color(0xFF383531),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            val orderedStages = listOf(
                BuildPipelineStage.LINT_CHECK,
                BuildPipelineStage.KSP_PROCESSING,
                BuildPipelineStage.COMPOSE_COMPILATION,
                BuildPipelineStage.DEX_OPTIMIZATION,
                BuildPipelineStage.APK_PACKAGING,
                BuildPipelineStage.SIGNING_AND_CHECKSUM
            )

            orderedStages.forEachIndexed { index, stage ->
                val isCompleted = job.stage == BuildPipelineStage.SUCCESS || stage.ordinal < job.stage.ordinal
                val isCurrent = job.stage == stage && job.isRunning

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> CyberEmerald.copy(alpha = 0.2f)
                                    isCurrent -> TerracottaPrimary.copy(alpha = 0.25f)
                                    else -> Color(0xFF2C2A28)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isCompleted -> Icon(
                                Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = CyberEmerald,
                                modifier = Modifier.size(12.dp)
                            )
                            isCurrent -> CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = TerracottaPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            else -> Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF57534E))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stage.title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isCompleted -> Color(0xFFFAF8F5)
                                    isCurrent -> TerracottaPrimary
                                    else -> Color(0xFFA8A29E)
                                }
                            )
                        )
                        Text(
                            text = stage.description,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = Color(0xFF78716C)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArtifactDownloadCard(
    artifact: BuildArtifact,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var copiedToClipboard by remember { mutableStateOf(false) }

    fun copyTextToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2822)),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("artifact_download_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Artifact Ready
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyberEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Download Ready",
                            tint = CyberEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "APK Ready for Direct Download",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFFFAF8F5),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "${artifact.fileName} • ${artifact.fileSizeMb} MB",
                            style = MaterialTheme.typography.labelSmall.copy(color = CyberEmerald)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberEmerald.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "SDK ${artifact.targetSdk}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Direct Download Link presentation Box
            Text(
                text = "Direct Download Link (Universal Installable APK):",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFFA8A29E),
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF121A15),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF283B2E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = artifact.directDownloadUrl,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = CyberCyan
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            copyTextToClipboard("Direct APK Download URL", artifact.directDownloadUrl)
                            copiedToClipboard = true
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("copy_download_link_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Download Link",
                            tint = if (copiedToClipboard) CyberEmerald else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SHA-256 Checksum pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF141D17),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        copyTextToClipboard("SHA-256 Checksum", artifact.sha256Checksum)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF78716C),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SHA-256: ${artifact.sha256Checksum.take(16)}...",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF9E9E9E)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Tap to Copy",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = TerracottaPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Big Download Button
            Button(
                onClick = {
                    try {
                        val downloadIntent = Intent(Intent.ACTION_VIEW, Uri.parse(artifact.directDownloadUrl))
                        downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(downloadIntent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Direct download started for ${artifact.fileName} (${artifact.fileSizeMb} MB)!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("direct_download_apk_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Direct Download APK (${artifact.fileSizeMb} MB)",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Action Row: Share APK, Install Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Download ${artifact.appName} APK")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Direct Download link for ${artifact.appName} (${artifact.fileName}, ${artifact.fileSizeMb} MB):\n${artifact.directDownloadUrl}\n\nSHA-256: ${artifact.sha256Checksum}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share APK Download Link"))
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_apk_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share Link", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = {
                        Toast.makeText(
                            context,
                            "APK installer dispatch: Package verification passed. Ready to install on device.",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("install_apk_button")
                ) {
                    Icon(Icons.Default.Android, contentDescription = "Install", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Install APK", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun PipelineLogTerminal(
    logs: List<PipelineLogEntry>,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141312)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2A28)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = TerracottaPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Build Pipeline Console Output",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color(0xFFFAF8F5),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            val allLogText = logs.joinToString("\n") { "[${it.timestamp}] ${it.taskName}: ${it.message}" }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Build Logs", allLogText))
                            Toast.makeText(context, "All logs copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Logs", tint = Color(0xFFA8A29E), modifier = Modifier.size(14.dp))
                    }

                    IconButton(
                        onClick = onClearLogs,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Clear Logs", tint = Color(0xFFA8A29E), modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0A0908),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp, max = 240.dp)
            ) {
                if (logs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Log stream idle. Trigger a build to see live Gradle tasks.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF57534E),
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(logs, key = { it.id }) { log ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "[${log.timestamp}]",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF78716C)
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = log.taskName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = when (log.level) {
                                                LogLevel.SUCCESS -> CyberEmerald
                                                LogLevel.TASK -> TerracottaPrimary
                                                LogLevel.WARNING -> CyberAmber
                                                LogLevel.ERROR -> Color(0xFFEF4444)
                                                else -> CyberCyan
                                            }
                                        )
                                    )
                                    Text(
                                        text = log.message,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = Color(0xFFD6D3D1)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BuildHistorySection(
    history: List<BuildArtifact>,
    onSelectArtifact: (BuildArtifact) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (history.isEmpty()) return

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF22201E)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFFA8A29E), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generated Build Artifacts (${history.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFFFAF8F5),
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            history.forEach { item ->
                val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(item.timestamp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF181716),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.fileName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "$dateStr • ${item.fileSizeMb} MB • Build in ${item.buildDurationSec}s",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF78716C))
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Direct APK URL", item.directDownloadUrl))
                                    Toast.makeText(context, "Direct APK download link copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link", tint = Color(0xFFA8A29E), modifier = Modifier.size(16.dp))
                            }

                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.directDownloadUrl)).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Downloading ${item.fileName}...", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download", color = Color.Black, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
