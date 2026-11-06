package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.StepStatus
import com.example.data.models.WorkflowStep
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.components.PersistentActionLogComponent
import com.example.ui.theme.TerracottaPrimary
import com.example.ui.viewmodels.AgenticNotification
import com.example.ui.viewmodels.AgenticWorkflowViewModel

@Composable
fun AgenticWorkflowScreen(
    agenticWorkflowViewModel: AgenticWorkflowViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val currentWorkflow by agenticWorkflowViewModel.currentWorkflow.collectAsState()
    val notifications by agenticWorkflowViewModel.notifications.collectAsState()
    val actionLogs by agenticWorkflowViewModel.actionLogs.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Autonomous Graph", "Tasks (${notifications.size})", "Action Logs (${actionLogs.size})")

    var customGoalInput by remember { mutableStateOf("Autonomously scan cluster, patch vulnerabilities, and verify zero regressions") }

    val presetGoals = listOf(
        "End-to-End Pentest & Self-Healing",
        "Autonomous App Build & Release APK",
        "45-Min Multimodal Video Production",
        "Continuous Network Reconnaissance"
    )

    // Pulse animation for running state
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181716))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("agentic_workflow_screen")
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
                        text = "Autonomous Agent Engine",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            color = Color(0xFFFAF8F5)
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (currentWorkflow.isRunning) Color(0xFF1E3A2F) else Color(0xFF2C2A28)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(if (currentWorkflow.isRunning) CyberEmerald else Color(0xFFA8A29E), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentWorkflow.isRunning) "Autonomous Loop Active" else "Ready",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 11.sp)
                        )
                    }
                }
            }

            // Tab Navigation
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF1F1D1B),
                contentColor = TerracottaPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = TerracottaPrimary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTabIndex == index) Color(0xFFFAF8F5) else Color(0xFFA8A29E)
                                )
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> {
                    // Autonomous Execution Graph View
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // Autonomous Goal Input Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF22201E))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Assign Autonomous Multi-Step Goal:",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFFAF8F5),
                                            fontSize = 15.sp
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = customGoalInput,
                                        onValueChange = { customGoalInput = it },
                                        placeholder = { Text("Describe complex goal to plan & execute autonomously...", color = Color(0xFF78716C)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFF141312),
                                            unfocusedContainerColor = Color(0xFF141312),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            cursorColor = TerracottaPrimary
                                        ),
                                        maxLines = 2
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Presets & Launch Button
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            presetGoals.forEach { preset ->
                                                Surface(
                                                    onClick = { customGoalInput = preset },
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFF2C2A28)
                                                ) {
                                                    Text(
                                                        text = preset,
                                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFD6D3D1), fontSize = 11.sp),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Button(
                                            onClick = {
                                                agenticWorkflowViewModel.submitNewGoal(customGoalInput)
                                                Toast.makeText(context, "Autonomous engine launched!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Run", style = MaterialTheme.typography.labelMedium)
                                        }
                                    }
                                }
                            }
                        }

                        // Active Workflow Overview Header
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF22201E))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = currentWorkflow.title,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFAF8F5),
                                                    fontSize = 16.sp
                                                )
                                            )
                                            Text(
                                                text = "Goal: ${currentWorkflow.primaryGoal}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E)),
                                                maxLines = 1
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (currentWorkflow.isRunning) CyberAmber.copy(alpha = 0.2f) else CyberEmerald.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = if (currentWorkflow.isRunning) "EXECUTING" else "ALL STEPS VERIFIED",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (currentWorkflow.isRunning) CyberAmber else CyberEmerald,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Progress bar
                                    LinearProgressIndicator(
                                        progress = { currentWorkflow.progress },
                                        color = if (currentWorkflow.progress >= 1f) CyberEmerald else TerracottaPrimary,
                                        trackColor = Color(0xFF383531),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                    )
                                }
                            }
                        }

                        // Steps Graph Execution List
                        item {
                            Text(
                                text = "Autonomous Multi-Step Execution Plan:",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color(0xFFA8A29E),
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        items(currentWorkflow.steps) { step ->
                            WorkflowStepCard(step = step)
                        }

                        // Live Autonomous Execution Logs Terminal
                        item {
                            Text(
                                text = "Live Sub-Agent Execution Terminal:",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color(0xFFA8A29E),
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        item {
                            Surface(
                                color = Color(0xFF100F0E),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFF2C2A28), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    currentWorkflow.liveTerminalLogs.takeLast(10).forEach { logLine ->
                                        Text(
                                            text = logLine,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                color = when {
                                                    logLine.contains("[SELF-CORRECTION]") -> CyberAmber
                                                    logLine.contains("[COMPLETED]") || logLine.contains("[PIPELINE FINISHED]") -> CyberEmerald
                                                    else -> Color(0xFFD6D3D1)
                                                },
                                                fontSize = 11.5.sp,
                                                lineHeight = 16.sp
                                            ),
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Background Task Notifications View (Matching Screenshot 5)
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            // Editorial Headline matching Screenshot 5
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = "Keep tasks running while you're away",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 28.sp,
                                        color = Color(0xFFFAF8F5)
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "SecMentor's autonomous engines plan, compile, audit, and render in the background without needing your browser tab open.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFA8A29E))
                                )
                            }
                        }

                        items(notifications) { notif ->
                            NotificationCard(notification = notif)
                        }
                    }
                }

                2 -> {
                    // Persistent Action Logs View
                    PersistentActionLogComponent(
                        logs = actionLogs,
                        onClearLogs = { agenticWorkflowViewModel.clearActionLogs() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun WorkflowStepCard(step: WorkflowStep) {
    val statusColor = when (step.status) {
        StepStatus.COMPLETED -> CyberEmerald
        StepStatus.RUNNING -> CyberCyan
        StepStatus.AUTO_CORRECTING -> CyberAmber
        StepStatus.FAILED -> CyberRose
        StepStatus.PENDING -> Color(0xFF78716C)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF22201E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(statusColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.stepNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF2C2A28)
                    ) {
                        Text(
                            text = step.agentRole.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(step.agentRole.badgeColorHex),
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // Step Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (step.status) {
                            StepStatus.COMPLETED -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(12.dp))
                            StepStatus.RUNNING -> CircularProgressIndicator(color = CyberCyan, strokeWidth = 1.5.dp, modifier = Modifier.size(12.dp))
                            StepStatus.AUTO_CORRECTING -> Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(12.dp))
                            else -> Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFF78716C), modifier = Modifier.size(12.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (step.status) {
                                StepStatus.AUTO_CORRECTING -> "SELF-HEALING"
                                else -> step.status.name
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFAF8F5),
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFD6D3D1),
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tool Invocations Box
            Surface(
                color = Color(0xFF131211),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tool: ",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E))
                    )
                    Text(
                        text = step.toolUsed,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = CyberEmerald,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: AgenticNotification) {
    // Matching Screenshot 5: Lock-screen style push notification cards
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF22201E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Orange Claude sun icon matching screenshot 5
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(TerracottaPrimary, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFAF8F5),
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = notification.timestamp,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF78716C),
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFD6D3D1),
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp
                    )
                )
            }
        }
    }
}
