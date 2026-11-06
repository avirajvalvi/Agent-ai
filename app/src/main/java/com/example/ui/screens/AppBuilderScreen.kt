package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
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
import androidx.compose.material3.Surface
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
import com.example.ui.components.ArtifactDownloadCard
import com.example.ui.components.BuildHistorySection
import com.example.ui.components.BuildPipelineHeader
import com.example.ui.components.PipelineLogTerminal
import com.example.ui.components.PipelineStageTracker
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.TerracottaPrimary
import com.example.ui.viewmodels.AppBuilderViewModel

enum class BuilderTab {
    PIPELINE,
    SYNTHESIZER
}

@Composable
fun AppBuilderScreen(
    appBuilderViewModel: AppBuilderViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val currentApp by appBuilderViewModel.currentApp.collectAsState()
    val isBuilding by appBuilderViewModel.isBuilding.collectAsState()
    val buildStep by appBuilderViewModel.buildStep.collectAsState()
    val buildProgress by appBuilderViewModel.buildProgress.collectAsState()

    val currentJob by appBuilderViewModel.currentJob.collectAsState()
    val buildHistory by appBuilderViewModel.buildHistory.collectAsState()

    var selectedTab by remember { mutableStateOf(BuilderTab.PIPELINE) }
    var promptInput by remember { mutableStateOf("Create a flashcard game to help my daughter study for her Psych 101 final") }

    val quickAppPrompts = listOf(
        "Create a flashcard game for Psych 101",
        "Cotswolds Sunday lunch options for 20",
        "Cyber security port auditor & scanner",
        "Expense and budget tracker with charts"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181716))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("app_builder_screen")
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
                        text = "Build Pipeline & APK",
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
                    color = Color(0xFF2C2A28)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Android, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AGP 8.7 • Gradle 8.9", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }

            // Tab Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF22201E))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedTab == BuilderTab.PIPELINE) TerracottaPrimary else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = BuilderTab.PIPELINE }
                        .testTag("pipeline_tab_button")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            tint = if (selectedTab == BuilderTab.PIPELINE) Color.White else Color(0xFFA8A29E),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Build Pipeline",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedTab == BuilderTab.PIPELINE) Color.White else Color(0xFFA8A29E)
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedTab == BuilderTab.SYNTHESIZER) TerracottaPrimary else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = BuilderTab.SYNTHESIZER }
                        .testTag("synthesizer_tab_button")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (selectedTab == BuilderTab.SYNTHESIZER) Color.White else Color(0xFFA8A29E),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI Synthesizer",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedTab == BuilderTab.SYNTHESIZER) Color.White else Color(0xFFA8A29E)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Content Area
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (selectedTab == BuilderTab.PIPELINE) {
                    // === Automated Build Pipeline View ===

                    // 1. Pipeline Trigger & Control Header
                    item {
                        BuildPipelineHeader(
                            job = currentJob,
                            onTriggerBuild = {
                                appBuilderViewModel.triggerAutomatedBuild(
                                    projectName = currentApp.appName,
                                    variant = "Debug Universal APK"
                                )
                            },
                            onCancelBuild = {
                                appBuilderViewModel.cancelBuild()
                            }
                        )
                    }

                    // 2. Direct Download Card (When Artifact is Available)
                    currentJob.artifact?.let { artifact ->
                        item {
                            ArtifactDownloadCard(artifact = artifact)
                        }
                    }

                    // 3. Stage Progress Tracker
                    item {
                        PipelineStageTracker(job = currentJob)
                    }

                    // 4. Live Terminal Console Logs
                    item {
                        PipelineLogTerminal(
                            logs = currentJob.logs,
                            onClearLogs = { appBuilderViewModel.clearLogs() }
                        )
                    }

                    // 5. Build Artifact History
                    item {
                        BuildHistorySection(
                            history = buildHistory,
                            onSelectArtifact = {}
                        )
                    }
                } else {
                    // === AI Synthesizer & App Generator View ===

                    // Prompt Builder Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF22201E))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Describe Any App to Build Automatically:",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFFAF8F5),
                                        fontSize = 15.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = promptInput,
                                    onValueChange = { promptInput = it },
                                    placeholder = { Text("e.g. Create a flashcard study app for biology...", color = Color(0xFF78716C)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF141312),
                                        unfocusedContainerColor = Color(0xFF141312),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        cursorColor = TerracottaPrimary
                                    ),
                                    maxLines = 3
                                )

                                Spacer(modifier = Modifier.height(10.dp))

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
                                        quickAppPrompts.forEach { p ->
                                            Surface(
                                                onClick = { promptInput = p },
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF2C2A28)
                                            ) {
                                                Text(
                                                    text = p.take(18) + "...",
                                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFD6D3D1), fontSize = 11.sp),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            if (promptInput.isNotBlank()) {
                                                appBuilderViewModel.buildNewApp(promptInput)
                                                selectedTab = BuilderTab.PIPELINE
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Build App", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                    }

                    // Download Options Card for Current Generated App
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
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
                                            text = currentApp.appName,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFAF8F5),
                                                fontSize = 17.sp
                                            )
                                        )
                                        Text(
                                            text = "${currentApp.packageName} • ${currentApp.category}",
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E))
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = CyberEmerald.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "READY TO INSTALL",
                                            style = MaterialTheme.typography.labelSmall.copy(color = CyberEmerald, fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            Toast.makeText(
                                                context,
                                                "Downloading ${currentApp.apkFileName} (${currentApp.apkSizeMb} MB)... Ready to install on Android device!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Android, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Download APK (${currentApp.apkSizeMb} MB)", style = MaterialTheme.typography.labelMedium)
                                    }

                                    Button(
                                        onClick = {
                                            Toast.makeText(
                                                context,
                                                "Downloading ${currentApp.zipFileName} (${currentApp.zipSizeMb} MB)... Full Android Studio project archive ready!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2C29)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.FolderZip, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Source ZIP (${currentApp.zipSizeMb} MB)", style = MaterialTheme.typography.labelMedium)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        val externalApps = com.example.data.integration.ExternalAppsManager(context)
                                        val res = externalApps.triggerTermuxApkBuild(currentApp.appName, null)
                                        Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2D24)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("app_builder_termux_trigger")
                                ) {
                                    Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Dispatch Autonomous Build to Termux", color = Color.White, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }

                    // Interactive Live App Preview
                    item {
                        Text(
                            text = "Interactive Live Preview (Simulated In-App):",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color(0xFFA8A29E),
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF121110))
                                .border(1.dp, Color(0xFF383531), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            when (currentApp.appType) {
                                "FLASHCARD" -> InteractiveFlashcardPreview()
                                "RESTAURANT" -> InteractiveRestaurantPreview()
                                else -> InteractiveCyberPreview()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveFlashcardPreview() {
    var cardIndex by remember { mutableStateOf(0) }
    var isRevealed by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }

    val flashcards = listOf(
        "What is a neuron?" to "The basic building block of the nervous system. It's a specialized cell that receives, processes, and transmits information through electrical and chemical signals.",
        "What is the cerebral cortex?" to "The outer layer of neural tissue of the cerebrum of the brain in humans. It plays a key role in memory, thinking, learning, and consciousness.",
        "What is dopamine?" to "A neurotransmitter that plays several important roles in the brain and body, including movement, memory, pleasurable reward, and motivation."
    )

    val currentCard = flashcards[cardIndex % flashcards.size]

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Psych flashcards",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 17.sp
                    )
                )
                Text(
                    text = "Intro Psychology study set",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E))
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E2D24)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$correctCount correct", style = MaterialTheme.typography.labelSmall, color = CyberEmerald)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Card ${cardIndex + 1} of ${flashcards.size}", style = MaterialTheme.typography.labelSmall, color = Color(0xFFA8A29E))
        }

        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (cardIndex + 1f) / flashcards.size },
            color = Color.White,
            trackColor = Color(0xFF2C2A28),
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242220)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF4F46E5)
                    ) {
                        Text(
                            text = if (isRevealed) "ANSWER" else "QUESTION",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text("Q${cardIndex + 1}", style = MaterialTheme.typography.labelSmall, color = Color(0xFFA8A29E))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentCard.first,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 19.sp
                    )
                )

                if (isRevealed) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentCard.second,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFD6D3D1),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isRevealed = !isRevealed },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF383531)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isRevealed) "Hide Answer" else "Reveal Answer", color = Color.White)
                    }

                    Button(
                        onClick = {
                            correctCount += 1
                            isRevealed = false
                            cardIndex = (cardIndex + 1) % flashcards.size
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Next Card", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveRestaurantPreview() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Cotswolds Sunday Lunch Options for 20",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242220))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("The Plough & Feather", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E2A20)) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("4.7", color = CyberAmber, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Gastropub • Private Dining Room for up to 24 guests • Cotswolds", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA8A29E)))
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Book Table for 20")
                }
            }
        }
    }
}

@Composable
fun InteractiveCyberPreview() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("CyberShield Diagnostic Console", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(6.dp))
        Text("Audit target: 192.168.1.0/24 | Active interfaces: 2", style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan, fontFamily = FontFamily.Monospace))
    }
}
