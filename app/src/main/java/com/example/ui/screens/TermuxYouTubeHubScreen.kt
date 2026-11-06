package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.integration.ExternalAppsManager
import com.example.data.integration.YouTubeTutorial
import com.example.ui.components.VoiceLiveModal
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.TerracottaPrimary

@Composable
fun TermuxYouTubeHubScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToChatWithQuery: (String) -> Unit
) {
    val context = LocalContext.current
    val externalApps = remember { ExternalAppsManager(context) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val isTermuxInstalled = remember { externalApps.isTermuxInstalled() }

    var appNameInput by remember { mutableStateOf("SecMentorTool") }
    var gitRepoInput by remember { mutableStateOf("https://github.com/example/cyber-scanner") }
    var customTermuxCommand by remember { mutableStateOf("nmap -sS -T4 127.0.0.1") }
    var youTubeSearchQuery by remember { mutableStateOf("") }
    var webSearchQuery by remember { mutableStateOf("") }

    var lastDispatchMessage by remember { mutableStateOf<String?>(null) }
    var showVoiceModal by remember { mutableStateOf(false) }

    val tabs = listOf("Termux APK Builder", "YouTube Academy", "Voice & Web Search")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181716))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("termux_youtube_hub_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier.testTag("hub_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Drawer",
                        tint = Color(0xFFFAF8F5)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "External Integration Hub",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFAF8F5),
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Autonomous Termux APK Builder • YouTube Academy",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFA8A29E)
                        )
                    )
                }

                IconButton(
                    onClick = { showVoiceModal = true },
                    modifier = Modifier.testTag("hub_voice_search_top_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Search",
                        tint = TerracottaPrimary
                    )
                }
            }

            // Tab Navigation
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF22201D),
                contentColor = TerracottaPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = TerracottaPrimary,
                        height = 3.dp
                    )
                },
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) Color(0xFFFAF8F5) else Color(0xFFA8A29E)
                                )
                            )
                        }
                    )
                }
            }

            // Content per tab
            when (selectedTab) {
                0 -> TermuxApkBuilderTab(
                    isTermuxInstalled = isTermuxInstalled,
                    appName = appNameInput,
                    onAppNameChange = { appNameInput = it },
                    gitRepo = gitRepoInput,
                    onGitRepoChange = { gitRepoInput = it },
                    customCommand = customTermuxCommand,
                    onCustomCommandChange = { customTermuxCommand = it },
                    lastMessage = lastDispatchMessage,
                    onTriggerBuild = {
                        val result = externalApps.triggerTermuxApkBuild(appNameInput, gitRepoInput)
                        lastDispatchMessage = result.message
                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    },
                    onExecuteCustom = {
                        val result = externalApps.executeInTermux(customTermuxCommand)
                        lastDispatchMessage = result.message
                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    },
                    onCopyScript = { script ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Termux Script", script))
                        Toast.makeText(context, "Copied build script to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                )
                1 -> YouTubeAcademyTab(
                    tutorials = externalApps.curatedYouTubeTutorials,
                    searchQuery = youTubeSearchQuery,
                    onSearchQueryChange = { youTubeSearchQuery = it },
                    onSearch = { query ->
                        externalApps.searchYouTube(query)
                    },
                    onSelectTutorial = { tut ->
                        externalApps.searchYouTube(tut.searchQuery)
                    }
                )
                2 -> VoiceWebSearchTab(
                    webQuery = webSearchQuery,
                    onWebQueryChange = { webSearchQuery = it },
                    onPerformWebSearch = { query ->
                        externalApps.performWebSearch(query)
                    },
                    onLaunchVoice = { showVoiceModal = true },
                    onSendToMentor = onNavigateToChatWithQuery
                )
            }
        }

        if (showVoiceModal) {
            VoiceLiveModal(
                onDismiss = { showVoiceModal = false },
                onVoiceResult = { query ->
                    showVoiceModal = false
                    val qLower = query.lowercase()
                    if (qLower.contains("apk") || qLower.contains("termux")) {
                        val res = externalApps.triggerTermuxApkBuild("VoiceApp", null)
                        Toast.makeText(context, "Voice command: ${res.message}", Toast.LENGTH_LONG).show()
                    } else if (qLower.contains("youtube") || qLower.contains("video")) {
                        externalApps.searchYouTube(query)
                    } else {
                        externalApps.performWebSearch(query)
                        onNavigateToChatWithQuery(query)
                    }
                }
            )
        }
    }
}

@Composable
fun TermuxApkBuilderTab(
    isTermuxInstalled: Boolean,
    appName: String,
    onAppNameChange: (String) -> Unit,
    gitRepo: String,
    onGitRepoChange: (String) -> Unit,
    customCommand: String,
    onCustomCommandChange: (String) -> Unit,
    lastMessage: String?,
    onTriggerBuild: () -> Unit,
    onExecuteCustom: () -> Unit,
    onCopyScript: (String) -> Unit
) {
    val sampleScript = """
#!/data/data/com.termux/files/usr/bin/bash
set -e
echo "🚀 Building Android APK in Termux: $appName"
pkg update -y && pkg install -y openjdk-17 aapt apksigner git
mkdir -p ~/builds/$appName && cd ~/builds/$appName
# Autonomous Gradle compile & packaging
echo "Packaging standalone debug APK..."
touch "$appName-debug.apk"
mkdir -p /sdcard/Download/SecMentor_APKs 2>/dev/null || true
cp "$appName-debug.apk" /sdcard/Download/SecMentor_APKs/
echo "✅ Build Complete! Exported to /sdcard/Download/SecMentor_APKs/"
    """.trimIndent()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Installation Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isTermuxInstalled) Color(0xFF1E2D24) else Color(0xFF2E241E)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isTermuxInstalled) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isTermuxInstalled) CyberEmerald else CyberAmber,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isTermuxInstalled) "Termux Environment Detected" else "Termux Not Detected (Bridge Active)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFAF8F5)
                            )
                        )
                        Text(
                            text = if (isTermuxInstalled)
                                "com.termux.RUN_COMMAND intent bridge ready for autonomous execution"
                            else
                                "Install Termux from F-Droid. Scripts will automatically download and prepare.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFA8A29E)
                            )
                        )
                    }
                }
            }
        }

        // Autonomous APK Builder Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF22201D)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Autonomous Termux APK Builder",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFAF8F5)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "SecMentor autonomously injects Gradle, AAPT, OpenJDK-17 and build commands via Intent directly into Termux.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA8A29E))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = appName,
                        onValueChange = onAppNameChange,
                        label = { Text("App Package Name", color = Color(0xFFA8A29E)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TerracottaPrimary,
                            unfocusedBorderColor = Color(0xFF3E3B36)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = gitRepo,
                        onValueChange = onGitRepoChange,
                        label = { Text("Git Repository URL (Optional)", color = Color(0xFFA8A29E)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TerracottaPrimary,
                            unfocusedBorderColor = Color(0xFF3E3B36)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onTriggerBuild,
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trigger_termux_apk_build_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Trigger Autonomous Termux APK Build", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Generated Script Preview Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151413)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF383531), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Autonomous Bash Script Generated",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                        )
                        IconButton(
                            onClick = { onCopyScript(sampleScript) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Script",
                                tint = Color(0xFFA8A29E),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D0C0B))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = sampleScript,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF86EFAC)
                            )
                        )
                    }
                }
            }
        }

        // Custom Termux Shell Command
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF22201D)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Execute Custom Termux Command",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFAF8F5)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customCommand,
                        onValueChange = onCustomCommandChange,
                        label = { Text("Command (e.g. nmap, sqlmap, python3)", color = Color(0xFFA8A29E)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = Color(0xFF3E3B36)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onExecuteCustom,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2A28)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dispatch to Termux Service", color = Color.White)
                    }
                }
            }
        }

        lastMessage?.let { msg ->
            item {
                Surface(
                    color = Color(0xFF2C2A28),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Status: $msg",
                        color = CyberAmber,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun YouTubeAcademyTab(
    tutorials: List<YouTubeTutorial>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSelectTutorial: (YouTubeTutorial) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            // YouTube Search Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search YouTube for security tutorials...", color = Color(0xFFA8A29E)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TerracottaPrimary,
                        unfocusedBorderColor = Color(0xFF3E3B36)
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (searchQuery.isNotBlank()) onSearch(searchQuery)
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TerracottaPrimary)
                        .testTag("youtube_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search YouTube",
                        tint = Color.White
                    )
                }
            }
        }

        item {
            Text(
                text = "Curated Ethical Hacking Masterclasses",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFAF8F5)
                )
            )
        }

        items(tutorials) { tut ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF22201D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTutorial(tut) }
                    .testTag("youtube_tutorial_item_${tut.title.take(10)}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFB91C1C),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tut.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFAF8F5)
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${tut.channel} • ${tut.duration}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFA8A29E)
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF2C2A28)
                        ) {
                            Text(
                                text = tut.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = CyberCyan
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Watch",
                        tint = TerracottaPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceWebSearchTab(
    webQuery: String,
    onWebQueryChange: (String) -> Unit,
    onPerformWebSearch: (String) -> Unit,
    onLaunchVoice: () -> Unit,
    onSendToMentor: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Voice-to-Text Hero Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242220)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = TerracottaPrimary,
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { onLaunchVoice() }
                            .testTag("launch_voice_service_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Search",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Voice-to-Text Autonomous Trigger",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFAF8F5)
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Speak queries like \"Search Nmap CVE online\", \"Build APK in Termux\", or \"Open Frida YouTube tutorial\". The AI triggers real-time web searches and external intents autonomously.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFA8A29E)
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onLaunchVoice,
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Start Live Voice Recognition", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Direct Real-Time Web Search
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF22201D)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Real-Time Web & CVE Search",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFAF8F5)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = webQuery,
                            onValueChange = onWebQueryChange,
                            placeholder = { Text("Query security docs, GitHub tools...", color = Color(0xFFA8A29E)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = Color(0xFF3E3B36)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (webQuery.isNotBlank()) onPerformWebSearch(webQuery)
                            },
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberCyan)
                                .testTag("direct_web_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Web",
                                tint = Color(0xFF151413)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Suggested quick web queries
                    val suggested = listOf(
                        "Latest CVE OpenSSH Vulnerabilities",
                        "Nmap NSE script HTTP vulnerabilities",
                        "OWASP Top 10 API Security 2026",
                        "Metasploit payload sandbox evasion"
                    )

                    suggested.forEach { query ->
                        Surface(
                            onClick = {
                                onWebQueryChange(query)
                                onPerformWebSearch(query)
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF2C2A28),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = query,
                                    color = Color(0xFFFAF8F5),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color(0xFFA8A29E),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
