package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CurriculumModule
import com.example.data.models.CurriculumPhase
import com.example.data.models.CurriculumProgress
import com.example.data.models.ModuleLockStatus
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.TerracottaDark
import com.example.ui.theme.TerracottaPrimary

/**
 * High-level Dashboard displaying overall curriculum completion metrics, XP, and module locking controls.
 */
@Composable
fun CurriculumProgressDashboard(
    progress: CurriculumProgress,
    totalModulesCount: Int,
    completedModulesCount: Int,
    onToggleInstructorBypass: (Boolean) -> Unit,
    onResetProgress: () -> Unit
) {
    val progressFraction = if (totalModulesCount > 0) {
        completedModulesCount.toFloat() / totalModulesCount.toFloat()
    } else 0f

    val percentage = (progressFraction * 100).toInt()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF242220)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("curriculum_progress_dashboard")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with title and XP badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "एथिकल हैकिंग अकादमी प्रगति",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFFFAF8F5),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Text(
                        text = "60-90 Day Structured Learning Path",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E))
                    )
                }

                // Total XP Badge
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF382C18),
                    modifier = Modifier.testTag("curriculum_xp_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "XP",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${progress.totalXp} XP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFFDE68A),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar and Metric
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$completedModulesCount / $totalModulesCount मॉड्यूल्स पूर्ण ($percentage%)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (percentage == 100) CyberEmerald else TerracottaPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = if (progress.isInstructorBypassEnabled) "🔓 बाईपास मोड चालू" else "🔒 क्रमिक लॉकिंग सक्रिय",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (progress.isInstructorBypassEnabled) Color(0xFFFBBF24) else Color(0xFF94A3B8)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = TerracottaPrimary,
                trackColor = Color(0xFF383532)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Instructor Bypass Switch and Reset Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = progress.isInstructorBypassEnabled,
                        onCheckedChange = onToggleInstructorBypass,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TerracottaPrimary,
                            uncheckedThumbColor = Color(0xFFA8A29E),
                            uncheckedTrackColor = Color(0xFF383532)
                        ),
                        modifier = Modifier.testTag("instructor_bypass_switch")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "सभी मॉड्यूल्स अनलॉक करें (ऑडिट मोड)",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFD6D3D1))
                    )
                }

                IconButton(
                    onClick = onResetProgress,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("reset_progress_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset Progress",
                        tint = Color(0xFF78716C),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Phase Header grouping modules with duration and completion status.
 */
@Composable
fun CurriculumPhaseHeader(phase: CurriculumPhase) {
    val totalInPhase = phase.modules.size
    val completedInPhase = phase.modules.count { it.status == ModuleLockStatus.COMPLETED }
    val isAllCompleted = completedInPhase == totalInPhase && totalInPhase > 0

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isAllCompleted) Color(0xFF1E2D24) else Color(0xFF1E2836),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("curriculum_phase_header_${phase.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "फेज़ ${phase.phaseNumber}: ${phase.titleHindi}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = if (isAllCompleted) CyberEmerald else CyberCyan,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${phase.durationDays} • ${phase.description}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    ),
                    maxLines = 2
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF141A22)
            ) {
                Text(
                    text = "$completedInPhase / $totalInPhase",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isAllCompleted) CyberEmerald else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Composable representing an individual curriculum module with locked/unlocked state,
 * hands-on terminal command lab, interactive quiz check, and completion triggers.
 */
@Composable
fun CurriculumModuleCard(
    module: CurriculumModule,
    onRunInTerminal: (String) -> Unit,
    onAskAi: (String) -> Unit,
    onCompleteModule: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var showQuiz by remember { mutableStateOf(false) }
    var selectedOptionIndex by remember { mutableIntStateOf(-1) }
    var quizAnsweredCorrectly by remember { mutableStateOf(false) }

    val isLocked = module.status == ModuleLockStatus.LOCKED
    val isCompleted = module.status == ModuleLockStatus.COMPLETED

    val borderColor by animateColorAsState(
        targetValue = when (module.status) {
            ModuleLockStatus.COMPLETED -> CyberEmerald.copy(alpha = 0.5f)
            ModuleLockStatus.UNLOCKED, ModuleLockStatus.IN_PROGRESS -> TerracottaPrimary.copy(alpha = 0.6f)
            ModuleLockStatus.LOCKED -> Color(0xFF2C2A28)
        },
        label = "moduleBorderColor"
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = when (module.status) {
                ModuleLockStatus.COMPLETED -> Color(0xFF1B241E)
                ModuleLockStatus.UNLOCKED, ModuleLockStatus.IN_PROGRESS -> Color(0xFF22201E)
                ModuleLockStatus.LOCKED -> Color(0xFF191816)
            }
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .testTag("curriculum_module_${module.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Day Range, Title, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                when (module.status) {
                                    ModuleLockStatus.COMPLETED -> Color(0xFF1E3827)
                                    ModuleLockStatus.UNLOCKED, ModuleLockStatus.IN_PROGRESS -> TerracottaDark
                                    ModuleLockStatus.LOCKED -> Color(0xFF262422)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (module.status) {
                                ModuleLockStatus.COMPLETED -> Icons.Default.CheckCircle
                                ModuleLockStatus.UNLOCKED, ModuleLockStatus.IN_PROGRESS -> Icons.Default.PlayArrow
                                ModuleLockStatus.LOCKED -> Icons.Default.Lock
                            },
                            contentDescription = module.status.name,
                            tint = when (module.status) {
                                ModuleLockStatus.COMPLETED -> CyberEmerald
                                ModuleLockStatus.UNLOCKED, ModuleLockStatus.IN_PROGRESS -> Color.White
                                ModuleLockStatus.LOCKED -> Color(0xFF78716C)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "${module.dayRange}: ${module.titleHindi}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = if (isLocked) Color(0xFFA8A29E) else Color(0xFFFAF8F5),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = module.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isLocked) Color(0xFF78716C) else TerracottaPrimary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // XP & Status Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (module.status) {
                        ModuleLockStatus.COMPLETED -> Color(0xFF1E3827)
                        ModuleLockStatus.UNLOCKED -> Color(0xFF2C2A28)
                        ModuleLockStatus.LOCKED -> Color(0xFF22201E)
                        ModuleLockStatus.IN_PROGRESS -> Color(0xFF382C18)
                    }
                ) {
                    Text(
                        text = when (module.status) {
                            ModuleLockStatus.COMPLETED -> "✓ +${module.xpReward} XP"
                            ModuleLockStatus.UNLOCKED -> "+${module.xpReward} XP"
                            ModuleLockStatus.LOCKED -> "🔒 Locked"
                            ModuleLockStatus.IN_PROGRESS -> "In Progress"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = when (module.status) {
                                ModuleLockStatus.COMPLETED -> CyberEmerald
                                ModuleLockStatus.UNLOCKED -> Color(0xFFD6D3D1)
                                ModuleLockStatus.LOCKED -> Color(0xFF78716C)
                                ModuleLockStatus.IN_PROGRESS -> Color(0xFFFBBF24)
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = module.summary,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isLocked) Color(0xFF78716C) else Color(0xFFD6D3D1)
                )
            )

            // Learning Objectives Bullet Points
            if (!isLocked) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    module.learningObjectives.forEach { objective ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text("• ", color = TerracottaPrimary, style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = objective,
                                color = Color(0xFFA8A29E),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Hands-on Command Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF121110),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💻 ${module.handsOnLab.targetEnvironment}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TerracottaPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Row {
                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(module.handsOnLab.command))
                                        Toast.makeText(context, "Command copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Command",
                                        tint = Color(0xFFA8A29E),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onRunInTerminal(module.handsOnLab.command) },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .testTag("run_lab_button_${module.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Run Command",
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onAskAi("Explain this ethical hacking command and its defense: ${module.handsOnLab.command}") },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = "Ask AI",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "$ ${module.handsOnLab.command}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = CyberEmerald,
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "🛡️ डिफ़ेंसिव उपाय: ${module.handsOnLab.defensiveMitigation}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Interactive Knowledge Check / Quiz Button
                module.quizQuestion?.let { quiz ->
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        onClick = { showQuiz = !showQuiz },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E2632),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (showQuiz) "क्विज़ छिपाएं (Hide Quiz)" else "ज्ञान परीक्षण क्विज़ (Knowledge Check)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyberCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                text = if (quizAnsweredCorrectly) "✓ सत्यापित" else "1 प्रश्न",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (quizAnsweredCorrectly) CyberEmerald else Color(0xFFA8A29E)
                                )
                            )
                        }
                    }

                    AnimatedVisibility(visible = showQuiz) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .background(Color(0xFF161514), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = quiz.questionHindi,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color(0xFFFAF8F5),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            quiz.options.forEachIndexed { optIndex, optionText ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedOptionIndex = optIndex
                                            quizAnsweredCorrectly = optIndex == quiz.correctIndex
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedOptionIndex == optIndex,
                                        onClick = {
                                            selectedOptionIndex = optIndex
                                            quizAnsweredCorrectly = optIndex == quiz.correctIndex
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = TerracottaPrimary,
                                            unselectedColor = Color(0xFF78716C)
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = optionText,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (selectedOptionIndex == optIndex) {
                                                if (optIndex == quiz.correctIndex) CyberEmerald else Color(0xFFF87171)
                                            } else Color(0xFFD6D3D1)
                                        )
                                    )
                                }
                            }

                            if (selectedOptionIndex != -1) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (quizAnsweredCorrectly) "✓ सही उत्तर! ${quiz.explanation}" else "❌ गलत उत्तर। पुनः प्रयास करें।",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (quizAnsweredCorrectly) CyberEmerald else Color(0xFFF87171)
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Module Completion Trigger
                if (!isCompleted) {
                    Surface(
                        onClick = { onCompleteModule(module.id) },
                        shape = RoundedCornerShape(12.dp),
                        color = TerracottaPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("complete_lab_button_${module.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "लैब पूर्ण करें और अगला मॉड्यूल अनलॉक करें (+${module.xpReward} XP)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✓ मॉड्यूल संपन्न और सत्यापित",
                            style = MaterialTheme.typography.labelSmall.copy(color = CyberEmerald, fontWeight = FontWeight.Bold)
                        )

                        Surface(
                            onClick = { onRunInTerminal(module.handsOnLab.command) },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF262422)
                        ) {
                            Text(
                                text = "पुनः अभ्यास करें",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFD6D3D1)),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            } else {
                // Locked state helper
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF78716C), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "यह मॉड्यूल लॉक है। इसे अनलॉक करने के लिए पिछला मॉड्यूल पूर्ण करें।",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF78716C),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
