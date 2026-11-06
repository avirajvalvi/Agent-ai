package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AgentActionLogEntity
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.theme.TerracottaPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PersistentActionLogComponent(
    logs: List<AgentActionLogEntity>,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filterOptions = listOf("ALL", "COMPLETED", "AUTO_CORRECTED", "RECON", "CODE", "BUILD")

    val filteredLogs = logs.filter { log ->
        val matchesSearch = searchQuery.isBlank() ||
                log.actionTitle.contains(searchQuery, ignoreCase = true) ||
                log.agentRole.contains(searchQuery, ignoreCase = true) ||
                log.toolCommand.contains(searchQuery, ignoreCase = true) ||
                log.workflowTitle.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "ALL" -> true
            "COMPLETED" -> log.status == "COMPLETED"
            "AUTO_CORRECTED" -> log.status == "AUTO_CORRECTED"
            "RECON" -> log.agentRole.contains("Recon", ignoreCase = true)
            "CODE" -> log.agentRole.contains("Code", ignoreCase = true) || log.agentRole.contains("Patch", ignoreCase = true)
            "BUILD" -> log.agentRole.contains("Build", ignoreCase = true) || log.agentRole.contains("Packager", ignoreCase = true)
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("persistent_action_log_component")
    ) {
        // Control Bar: Search & Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = TerracottaPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Persistent Autonomous Audit Log",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFAF8F5),
                            fontSize = 16.sp
                        )
                    )
                }
                Text(
                    text = "${logs.size} actions recorded across sessions in Room DB",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E))
                )
            }

            Row {
                IconButton(
                    onClick = {
                        val allLogText = logs.joinToString("\n---\n") {
                            "[${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(it.timestamp))}] " +
                                    "${it.agentRole} | ${it.actionTitle} (${it.status})\nTool: ${it.toolCommand}\nLogs: ${it.stdoutLogs}"
                        }
                        clipboardManager.setText(AnnotatedString(allLogText))
                        Toast.makeText(context, "Exported ${logs.size} action logs to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Export Logs", tint = Color.White, modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = onClearLogs,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ClearAll, contentDescription = "Clear Logs", tint = Color(0xFFA8A29E), modifier = Modifier.size(18.dp))
                }
            }
        }

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search autonomous actions, commands, or agents...", color = Color(0xFF78716C), fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFA8A29E), modifier = Modifier.size(18.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF141312),
                unfocusedContainerColor = Color(0xFF141312),
                focusedBorderColor = Color(0xFF383531),
                unfocusedBorderColor = Color(0xFF2C2A28),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = TerracottaPrimary
            ),
            singleLine = true
        )

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filterOptions.forEach { filter ->
                val isSelected = selectedFilter == filter
                Surface(
                    onClick = { selectedFilter = filter },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) TerracottaPrimary else Color(0xFF262422)
                ) {
                    Text(
                        text = filter,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) Color.White else Color(0xFFD6D3D1),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Log Items List
        if (filteredLogs.isEmpty()) {
            Surface(
                color = Color(0xFF1B1A18),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier.padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No autonomous actions match current search.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFA8A29E))
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredLogs.forEach { logItem ->
                    ActionLogItemCard(log = logItem)
                }
            }
        }
    }
}

@Composable
fun ActionLogItemCard(log: AgentActionLogEntity) {
    var isExpanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val dateStr = SimpleDateFormat("MMM d, HH:mm:ss", Locale.US).format(Date(log.timestamp))

    val statusColor = when (log.status) {
        "COMPLETED" -> CyberEmerald
        "AUTO_CORRECTED" -> CyberAmber
        "RUNNING" -> CyberCyan
        else -> CyberRose
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF201E1C)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Agent Role, Status & Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF2C2A28)
                    ) {
                        Text(
                            text = log.agentRole,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TerracottaPrimary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (log.status == "AUTO_CORRECTED") {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("SELF-HEALED", style = MaterialTheme.typography.labelSmall.copy(color = CyberAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold))
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("SUCCESS", style = MaterialTheme.typography.labelSmall.copy(color = CyberEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF78716C), fontSize = 10.5.sp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFFA8A29E),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Title
            Text(
                text = "Step ${log.stepNumber}: ${log.actionTitle}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFAF8F5),
                    fontSize = 14.5.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = log.actionDescription,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFD6D3D1),
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tool Execution Command Line
            Surface(
                color = Color(0xFF131211),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.toolCommand,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = CyberEmerald,
                            fontSize = 11.5.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            // Expanded Execution Logs & Artifact Details
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = Color(0xFF2C2A28))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Detailed Output & Telemetry (${log.durationMs}ms):",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E), fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = Color(0xFF0F0E0D),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = log.stdoutLogs,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFFAF8F5),
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    log.outputArtifact?.let { artifact ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Artifact: $artifact",
                                style = MaterialTheme.typography.labelSmall.copy(color = CyberCyan, fontFamily = FontFamily.Monospace)
                            )
                            Surface(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(log.stdoutLogs))
                                    Toast.makeText(context, "Copied log telemetry", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF2C2A28)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy Log", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
