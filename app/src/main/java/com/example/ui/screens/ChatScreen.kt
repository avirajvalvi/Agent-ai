package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlobalSearchDialog
import com.example.ui.components.VoiceCommandBar
import com.example.data.voice.VoiceCommand
import com.example.data.voice.VoiceCommanderService
import com.example.data.integration.ExternalAppsManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ChatMessage
import com.example.ui.components.VoiceLiveModal
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.TerracottaPrimary
import com.example.ui.viewmodels.ChatViewModel

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateToTerminal: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Real-time Room database streams
    val messages by chatViewModel.displayedMessages.collectAsStateWithLifecycle()
    val messageCount by chatViewModel.messageCount.collectAsStateWithLifecycle()
    val searchQuery by chatViewModel.searchQuery.collectAsStateWithLifecycle()
    val streamingMessage by chatViewModel.streamingMessage.collectAsStateWithLifecycle()

    val isGenerating by chatViewModel.isGenerating.collectAsStateWithLifecycle()
    val followUpSuggestions by chatViewModel.followUpSuggestions.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    var attachedFile by remember { mutableStateOf<String?>(null) }
    var attachedCode by remember { mutableStateOf<String?>(null) }
    var showVoiceModal by remember { mutableStateOf(false) }
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var showGlobalSearchDialog by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }

    val externalApps = remember { ExternalAppsManager(context) }
    val voiceCommander = remember { VoiceCommanderService(context) }
    val isVoiceListening by voiceCommander.isListening.collectAsState()
    val voiceTranscript by voiceCommander.liveTranscript.collectAsState()
    val voiceRms by voiceCommander.rmsVolume.collectAsState()
    val voiceDetectedCommand by voiceCommander.detectedCommand.collectAsState()
    var isVoiceBarActive by remember { mutableStateOf(false) }

    fun executeVoiceCommand(text: String, command: VoiceCommand) {
        when (command) {
            is VoiceCommand.ClearChat -> {
                chatViewModel.clearChat()
                Toast.makeText(context, "Voice Command: Chat Cleared", Toast.LENGTH_SHORT).show()
                voiceCommander.speak("Chat history cleared")
            }
            is VoiceCommand.OpenTerminal -> {
                onNavigateToTerminal("")
                voiceCommander.speak("Opening terminal sandbox")
            }
            is VoiceCommand.RunTerminal -> {
                onNavigateToTerminal(command.command)
                voiceCommander.speak("Running command in sandbox")
            }
            is VoiceCommand.BuildApk -> {
                val res = externalApps.triggerTermuxApkBuild(command.appName, null)
                Toast.makeText(context, "Voice Command: ${res.message}", Toast.LENGTH_LONG).show()
                voiceCommander.speak("Dispatched APK build to Termux")
            }
            is VoiceCommand.SearchWebOrCve -> {
                externalApps.performWebSearch(command.query)
                chatViewModel.sendMessage(text)
            }
            is VoiceCommand.GeneralQuery -> {
                chatViewModel.sendMessage(command.query)
            }
        }
        isVoiceBarActive = false
        voiceCommander.stopListening()
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isVoiceBarActive = true
            voiceCommander.startListening { text, command ->
                executeVoiceCommand(text, command)
            }
        } else {
            Toast.makeText(context, "Microphone permission required for voice commands", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            voiceCommander.destroy()
        }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val promptSuggestions = listOf(
        "Nmap TCP SYN scan logic",
        "Python port scanner code",
        "SSH auth.log brute-force analysis",
        "SQLi vs Parameterized queries",
        "Wireshark packet capture analysis"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181716))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("chat_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Drawer",
                            tint = Color(0xFFFAF8F5)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SecMentor",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            color = Color(0xFFFAF8F5)
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Room Database Real-Time Status Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E2820),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .testTag("room_database_status_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(CyberEmerald, shape = CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Room DB: $messageCount msgs",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    // Room History Search Toggle Button
                    IconButton(
                        onClick = {
                            isSearchActive = !isSearchActive
                            if (!isSearchActive) {
                                chatViewModel.clearSearchQuery()
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("toggle_chat_search_button")
                    ) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search Room History",
                            tint = if (isSearchActive) TerracottaPrimary else Color(0xFFD6D3D1),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { showGlobalSearchDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("chat_global_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "Enterprise Global Search",
                            tint = TerracottaPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { chatViewModel.clearChat() },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("clear_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = Color(0xFFA8A29E),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Real-Time Room Database Search Bar
            AnimatedVisibility(visible = isSearchActive) {
                Surface(
                    color = Color(0xFF22201E),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFA8A29E), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { chatViewModel.setSearchQuery(it) },
                            textStyle = TextStyle(color = Color(0xFFFAF8F5), fontSize = 13.sp),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("room_search_input"),
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty()) {
                                    Text("Search conversation history in Room DB...", color = Color(0xFF78716C), fontSize = 13.sp)
                                }
                                inner()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { chatViewModel.clearSearchQuery() }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFFA8A29E), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // Real-Time Room Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages) { msg ->
                    ChatMessageItem(
                        message = msg,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(it))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        onSendToSandbox = { cmd ->
                            onNavigateToTerminal(cmd)
                        },
                        onSpeak = { textToSpeak ->
                            voiceCommander.speak(textToSpeak)
                        },
                        onDelete = { id ->
                            chatViewModel.deleteMessage(id)
                            Toast.makeText(context, "Deleted from Room DB", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Streaming message from Room / In-Flight generation
                streamingMessage?.let { streamMsg ->
                    item {
                        ChatMessageItem(
                            message = streamMsg,
                            onCopy = {},
                            onSendToSandbox = onNavigateToTerminal,
                            onSpeak = {},
                            onDelete = {}
                        )
                    }
                }

                if (isGenerating && streamingMessage == null) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = TerracottaPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "SecMentor is analyzing security parameters...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFA8A29E))
                            )
                        }
                    }
                }
            }

            // Quick Prompt Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(promptSuggestions) { prompt ->
                    Surface(
                        onClick = { chatViewModel.sendMessage(prompt) },
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF262422),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color(0xFFD6D3D1)
                                )
                            )
                        }
                    }
                }
            }

            // Attachment Banner (if file or code attached)
            AnimatedVisibility(visible = attachedFile != null || attachedCode != null) {
                Surface(
                    color = Color(0xFF2B2824),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Attached file",
                                tint = TerracottaPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = attachedFile ?: "Attached Code Snippet",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFFFAF8F5),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        IconButton(
                            onClick = {
                                attachedFile = null
                                attachedCode = null
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove attachment",
                                tint = Color(0xFFA8A29E),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Attachment Picker Dropdown Sheet
            AnimatedVisibility(visible = showAttachmentMenu) {
                Surface(
                    color = Color(0xFF242220),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Add Security Artifact to Analyze:",
                            style = MaterialTheme.typography.labelLarge.copy(color = Color(0xFFFAF8F5)),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Surface(
                                onClick = {
                                    attachedFile = "port_scanner.py"
                                    attachedCode = """
import socket
target = "127.0.0.1"
for p in [22, 80, 443]:
    s = socket.socket()
    if s.connect_ex((target, p)) == 0:
        print(f"Port {p} OPEN")
    s.close()
                                    """.trimIndent()
                                    showAttachmentMenu = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF33302C),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Code, contentDescription = "Python", tint = CyberEmerald)
                                    Text("Python Code", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }

                            Surface(
                                onClick = {
                                    attachedFile = "auth.log"
                                    attachedCode = "Nov 3 04:12:01 sshd: Failed password for admin from 192.168.1.105"
                                    showAttachmentMenu = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF33302C),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = "Logs", tint = Color(0xFF38BDF8))
                                    Text("Linux Log", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }

                            Surface(
                                onClick = {
                                    attachedFile = "packet_dump.pcap"
                                    attachedCode = "TCP SYN 192.168.1.105:44210 -> 192.168.1.100:22 [SYN] Len=0"
                                    showAttachmentMenu = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF33302C),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = "PCAP", tint = TerracottaPrimary)
                                    Text("Packet Dump", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Continuous Conversational Follow-Up Suggestions
            if (followUpSuggestions.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(followUpSuggestions) { suggestion ->
                        Surface(
                            onClick = { chatViewModel.sendMessage(suggestion) },
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF262422),
                            modifier = Modifier.testTag("follow_up_chip_${suggestion.take(12)}")
                        ) {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFE7E5E4),
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Live Voice Command Bar
            AnimatedVisibility(visible = isVoiceBarActive) {
                VoiceCommandBar(
                    isListening = isVoiceListening,
                    transcript = voiceTranscript,
                    rmsVolume = voiceRms,
                    detectedCommand = voiceDetectedCommand,
                    onStopListening = {
                        if (isVoiceListening) {
                            voiceCommander.stopListening()
                        } else {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    onSubmitCommand = { text, cmd ->
                        executeVoiceCommand(text, cmd)
                    },
                    onCancel = {
                        voiceCommander.stopListening()
                        isVoiceBarActive = false
                    }
                )
            }

            // Input Bar matching Claude mobile app (Screenshot 6)
            Surface(
                color = Color(0xFF22201E),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attachment button `+`
                    IconButton(
                        onClick = { showAttachmentMenu = !showAttachmentMenu },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Attach code/log",
                            tint = Color(0xFFFAF8F5)
                        )
                    }

                    // Model Badge Pill
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF2E2C29),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = "Flash 2.5",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFD6D3D1),
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Input Text Field
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Ask about tools, code, or sandbox...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF78716C),
                                    fontSize = 14.sp
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color(0xFFFAF8F5),
                            unfocusedTextColor = Color(0xFFFAF8F5),
                            cursorColor = TerracottaPrimary
                        ),
                        singleLine = false,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank() || attachedCode != null) {
                                    chatViewModel.sendMessage(inputText, attachedCode, attachedFile)
                                    inputText = ""
                                    attachedFile = null
                                    attachedCode = null
                                }
                            }
                        )
                    )

                    // Speech-to-Text Voice Command Button
                    IconButton(
                        onClick = {
                            if (isVoiceBarActive) {
                                voiceCommander.stopListening()
                                isVoiceBarActive = false
                            } else {
                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("chat_voice_mic_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice mode",
                            tint = if (isVoiceBarActive) TerracottaPrimary else Color(0xFFD6D3D1)
                        )
                    }

                    // Terracotta Send Action Pill
                    Surface(
                        onClick = {
                            if (inputText.isNotBlank() || attachedCode != null) {
                                chatViewModel.sendMessage(inputText, attachedCode, attachedFile)
                                inputText = ""
                                attachedFile = null
                                attachedCode = null
                            }
                        },
                        shape = CircleShape,
                        color = TerracottaPrimary,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("send_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Multimodal Live Voice Overlay (Triggered by Mic button)
        if (showVoiceModal) {
            VoiceLiveModal(
                onDismiss = { showVoiceModal = false },
                onVoiceResult = { voiceText ->
                    chatViewModel.sendMessage(voiceText)
                }
            )
        }

        // Enterprise Global Search Dialog
        if (showGlobalSearchDialog) {
            GlobalSearchDialog(
                onDismiss = { showGlobalSearchDialog = false },
                onSelectResultInChat = { queryText ->
                    showGlobalSearchDialog = false
                    chatViewModel.sendMessage(queryText)
                },
                onRunInSandbox = { cmd ->
                    showGlobalSearchDialog = false
                    onNavigateToTerminal(cmd)
                }
            )
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onCopy: (String) -> Unit,
    onSendToSandbox: (String) -> Unit,
    onSpeak: (String) -> Unit = {},
    onDelete: (String) -> Unit = {}
) {
    val isUser = message.isUser
    val timeFormatted = remember(message.timestamp) {
        try {
            java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                .format(java.util.Date(message.timestamp))
        } catch (e: Exception) {
            ""
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (isUser) {
            // User message bubble (matching Claude warm dark style)
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF2E2C29),
                    modifier = Modifier
                        .padding(start = 48.dp)
                        .testTag("user_chat_bubble_${message.id}")
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFFFAF8F5),
                                fontSize = 15.sp
                            )
                        )

                        message.attachedFileName?.let { fname ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E1D1B)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = TerracottaPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(fname, style = MaterialTheme.typography.labelSmall, color = Color(0xFFD6D3D1))
                                }
                            }
                        }
                    }
                }

                // Timestamp & Delete button for user message
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp, end = 4.dp)
                ) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF78716C), fontSize = 10.sp)
                    )
                    IconButton(
                        onClick = { onDelete(message.id) },
                        modifier = Modifier
                            .size(22.dp)
                            .testTag("delete_message_button_${message.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFF78716C),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        } else {
            // AI Mentor response card stored in Room
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF22201E),
                modifier = Modifier
                    .padding(end = 24.dp)
                    .fillMaxWidth()
                    .testTag("ai_chat_card_${message.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header with avatar / status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(TerracottaPrimary, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "SecMentor",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "SecMentor AI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFAF8F5),
                                    fontSize = 14.sp
                                )
                            )
                            if (timeFormatted.isNotBlank()) {
                                Text(
                                    text = "$timeFormatted • Room DB",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyberEmerald,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            onClick = { onSpeak(message.text) },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Read Aloud",
                                tint = Color(0xFFA8A29E),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        IconButton(
                            onClick = { onCopy(message.text) },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Response",
                                tint = Color(0xFFA8A29E),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        IconButton(
                            onClick = { onDelete(message.id) },
                            modifier = Modifier
                                .size(30.dp)
                                .testTag("delete_message_button_${message.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete from Room",
                                tint = Color(0xFF78716C),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Stored Tags Chips
                    if (message.tags.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            message.tags.take(3).forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF191816)
                                ) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFD6D3D1),
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Formatted Text
                    FormattedSecurityText(
                        rawText = message.text,
                        onSendToSandbox = onSendToSandbox
                    )
                }
            }
        }
    }
}

@Composable
fun FormattedSecurityText(
    rawText: String,
    onSendToSandbox: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Check for code blocks delimited by ```
    val parts = rawText.split("```")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                // Code block
                val firstLineBreak = part.indexOf("\n")
                val lang = if (firstLineBreak != -1) part.substring(0, firstLineBreak).trim() else ""
                val code = if (firstLineBreak != -1) part.substring(firstLineBreak + 1) else part

                Surface(
                    color = Color(0xFF121110),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang.isNotBlank()) lang.uppercase() else "BASH / SCRIPT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TerracottaPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Row {
                                Surface(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(code.trim()))
                                        Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                                    },
                                    color = Color(0xFF262422),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.padding(end = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                    }
                                }

                                Surface(
                                    onClick = {
                                        val firstCmd = code.lines().firstOrNull { it.isNotBlank() && !it.startsWith("#") }?.trim() ?: "nmap 192.168.1.100"
                                        onSendToSandbox(firstCmd)
                                    },
                                    color = Color(0xFF1E3A2F),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Run in Sandbox", style = MaterialTheme.typography.labelSmall, color = CyberEmerald)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = code.trim(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = CyberEmerald,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            } else {
                // Regular markdown text
                if (part.isNotBlank()) {
                    Text(
                        text = part.trim(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFFFAF8F5),
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }
    }
}
