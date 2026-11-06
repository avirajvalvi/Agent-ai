package com.example.ui.screens

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import com.example.ui.components.GlobalSearchDialog
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.VoiceLiveModal

import com.example.data.localization.LanguageManager
import com.example.data.repository.SyncState
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.ui.platform.LocalContext

import androidx.compose.material.icons.filled.Terminal
import com.example.data.integration.ExternalAppsManager

@Composable
fun ClaudeHomeScreen(
    persistentLogCount: Int,
    syncState: SyncState = SyncState.SYNCED,
    pendingSyncCount: Int = 0,
    cacheHits: Int = 18,
    cacheLatencySavedMs: Long = 1420L,
    onNavigateToChats: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToCode: () -> Unit,
    onNavigateToArtifacts: () -> Unit,
    onNavigateToAppBuilder: () -> Unit,
    onNavigateToVideoStudio: () -> Unit,
    onNavigateToPersistentLogs: () -> Unit,
    onNavigateToExternalHub: () -> Unit = {},
    onNavigateToAcademy: () -> Unit = {},
    onSelectRecentTopic: (String) -> Unit,
    onNewChat: () -> Unit,
    onOpenLanguageSelector: () -> Unit = {},
    onOpenAuth: () -> Unit = {}
) {
    val context = LocalContext.current
    val langManager = remember { LanguageManager.getInstance(context) }
    val externalApps = remember { ExternalAppsManager(context) }

    // Exact recents list from user's screenshot
    val recentsList = listOf(
        "ऑडियो बुक्स की ऑनलाइन बुक स्टोर",
        "बिजनेसमैन रोलप्ले",
        "Build command termax setup",
        "क्लॉकआउट कोड निर्माण",
        "है क्लाउड",
        "Client project setup",
        "फंडिंग वेबसाइटें"
    )

    // Warm ivory / parchment background matching Screenshot 1
    val backgroundColor = Color(0xFFF7F5F0)
    val textPrimary = Color(0xFF1F1E1B)
    val dividerColor = Color(0xFFE5E2DA)
    val coralAvatar = Color(0xFFD97757)

    var promptInput by remember { mutableStateOf("") }
    var showLiveVoiceModal by remember { mutableStateOf(false) }
    var showGlobalSearchDialog by remember { mutableStateOf(false) }

    // Subtle spinner rotation
    val infiniteTransition = rememberInfiniteTransition(label = "spinner")
    val spinnerAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("claude_home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // "Claude" Header Title in Serif Font (matching screenshot 1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Claude",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 38.sp,
                            color = textPrimary,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    // Subtle refresh arc from screenshot
                    Canvas(
                        modifier = Modifier
                            .size(26.dp)
                            .rotate(spinnerAngle)
                    ) {
                        drawArc(
                            color = Color(0xFF5A5751),
                            startAngle = 45f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Global Search Button
                    Surface(
                        onClick = { showGlobalSearchDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFEAE6DE),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("home_global_search_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Enterprise Search",
                                tint = Color(0xFF5A5751),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Search",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Language Switcher Button
                    Surface(
                        onClick = onOpenLanguageSelector,
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFEAE6DE),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("language_switch_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Language",
                                tint = Color(0xFF5A5751),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = langManager.currentLanguage.value.code.uppercase(java.util.Locale.ROOT),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // AI Live Voice & Vision Multimodal Trigger (from screenshot 2)
                    Surface(
                        onClick = { showLiveVoiceModal = true },
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFEAE6DE),
                        modifier = Modifier.testTag("home_live_ai_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 4-point rainbow star
                            Canvas(modifier = Modifier.size(16.dp)) {
                                val w = size.width
                                val h = size.height
                                val midX = w / 2f
                                val midY = h / 2f
                                val starPath = Path().apply {
                                    moveTo(midX, 0f)
                                    cubicTo(midX, midY * 0.5f, midX + w * 0.25f, midY, w, midY)
                                    cubicTo(midX + w * 0.25f, midY, midX, midY * 1.5f, midX, h)
                                    cubicTo(midX, midY * 1.5f, midX - w * 0.25f, midY, 0f, midY)
                                    cubicTo(midX - w * 0.25f, midY, midX, midY * 0.5f, midX, 0f)
                                    close()
                                }
                                drawPath(
                                    path = starPath,
                                    brush = Brush.linearGradient(
                                        listOf(Color(0xFF60A5FA), Color(0xFF34D399), Color(0xFFFBBF24), Color(0xFFF43F5E))
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live AI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            // Cloud Sync & Model Cache Status Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cloud Sync Pill
                Surface(
                    onClick = onOpenAuth,
                    shape = RoundedCornerShape(12.dp),
                    color = when (syncState) {
                        SyncState.SYNCED -> Color(0xFFE8F5E9)
                        SyncState.PENDING_SYNC -> Color(0xFFFFF8E1)
                        SyncState.OFFLINE -> Color(0xFFEEEEEE)
                    },
                    modifier = Modifier.testTag("cloud_sync_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (syncState) {
                                SyncState.SYNCED -> Icons.Default.CloudDone
                                SyncState.PENDING_SYNC -> Icons.Default.CloudSync
                                SyncState.OFFLINE -> Icons.Default.WifiOff
                            },
                            contentDescription = null,
                            tint = when (syncState) {
                                SyncState.SYNCED -> Color(0xFF2E7D32)
                                SyncState.PENDING_SYNC -> Color(0xFFF57F17)
                                SyncState.OFFLINE -> Color(0xFF616161)
                            },
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (syncState) {
                                SyncState.SYNCED -> langManager.getString("cloud_synced")
                                SyncState.PENDING_SYNC -> "${langManager.getString("pending_sync")} ($pendingSyncCount)"
                                SyncState.OFFLINE -> langManager.getString("offline_mode")
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = when (syncState) {
                                    SyncState.SYNCED -> Color(0xFF1B5E20)
                                    SyncState.PENDING_SYNC -> Color(0xFFE65100)
                                    SyncState.OFFLINE -> Color(0xFF424242)
                                },
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Dual-Tier Cache Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEDE7F6),
                    modifier = Modifier.testTag("model_cache_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color(0xFF5E35B1),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "0ms AST Cache • ${cacheHits} hits",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF4527A0),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Primary Navigation Items matching Screenshot 1
            ClaudeHomeNavItem(
                icon = Icons.AutoMirrored.Outlined.Chat,
                title = langManager.getString("chats"),
                textColor = textPrimary,
                onClick = onNavigateToChats
            )
            ClaudeHomeNavItem(
                icon = Icons.Outlined.FolderSpecial,
                title = langManager.getString("projects"),
                textColor = textPrimary,
                onClick = onNavigateToProjects
            )
            ClaudeHomeNavItem(
                icon = Icons.Outlined.Code,
                title = langManager.getString("code_sandbox"),
                textColor = textPrimary,
                onClick = onNavigateToCode
            )
            ClaudeHomeNavItem(
                icon = Icons.Outlined.Description,
                title = langManager.getString("artifacts"),
                textColor = textPrimary,
                onClick = onNavigateToArtifacts
            )

            // Autonomous Action Logs (New Persistent UI Component Entry)
            ClaudeHomeNavItem(
                icon = Icons.Default.History,
                title = "${langManager.getString("autonomous_agent")} ($persistentLogCount)",
                textColor = textPrimary,
                accentBadge = "Cloud Sync",
                onClick = onNavigateToPersistentLogs
            )

            // Automated APK Build Pipeline & App Builder Quick Access
            ClaudeHomeNavItem(
                icon = Icons.Default.AutoAwesome,
                title = "Automated Build Pipeline (Direct APK)",
                textColor = textPrimary,
                accentBadge = "APK Ready",
                onClick = onNavigateToAppBuilder
            )
            ClaudeHomeNavItem(
                icon = Icons.Default.Movie,
                title = "Video Studio (30-60m Generated)",
                textColor = textPrimary,
                accentBadge = "HD",
                onClick = onNavigateToVideoStudio
            )
            ClaudeHomeNavItem(
                icon = Icons.Default.Terminal,
                title = "Termux APK Builder & YouTube Hub",
                textColor = textPrimary,
                accentBadge = "Intents",
                onClick = onNavigateToExternalHub
            )
            ClaudeHomeNavItem(
                icon = Icons.Default.School,
                title = "60-90 Day Ethical Hacking Academy & n8n Hub",
                textColor = textPrimary,
                accentBadge = "Curriculum",
                onClick = onNavigateToAcademy
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = dividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // "Recents" Header (matching screenshot 1)
            Text(
                text = "Recents",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6B6862),
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Recents Feed (matching screenshot 1 exactly)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(recentsList) { recentItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clickable { onSelectRecentTopic(recentItem) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            tint = Color(0xFF78756F),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = recentItem,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = textPrimary,
                                fontSize = 15.sp
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Pinned Bottom Bar: Avatar 'A', Prompt input / placeholder, and Floating "+ New chat" Button
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = 0f),
                            backgroundColor.copy(alpha = 0.95f),
                            backgroundColor
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coral Avatar 'A' (matching screenshot 1)
            Surface(
                shape = CircleShape,
                color = coralAvatar,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "A",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }
            }

            // Quick Prompt / Input Bar matching Screenshot 1 ("Install omnigent via cu...")
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFEDE9E1),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            color = textPrimary,
                            fontSize = 14.sp
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (promptInput.isEmpty()) {
                                Text(
                                    text = "Install omnigent via cu...",
                                    style = TextStyle(
                                        color = Color(0xFF9C988F),
                                        fontSize = 14.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (promptInput.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                val query = promptInput
                                promptInput = ""
                                onSelectRecentTopic(query)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Black "+ New chat" Pill Button (matching screenshot 1)
            Surface(
                onClick = onNewChat,
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF181716),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("home_new_chat_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New chat",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "New chat",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }

        // Gemini / Claude Live Multimodal Voice & Vision Modal (Screenshot 2)
        if (showLiveVoiceModal) {
            VoiceLiveModal(
                onDismiss = { showLiveVoiceModal = false },
                onVoiceResult = { voiceQuery ->
                    showLiveVoiceModal = false
                    val qLower = voiceQuery.lowercase()
                    if (qLower.contains("apk") || qLower.contains("termux")) {
                        externalApps.triggerTermuxApkBuild("VoiceApp", null)
                    } else if (qLower.contains("youtube") || qLower.contains("video")) {
                        externalApps.searchYouTube(voiceQuery)
                    } else if (qLower.contains("search") || qLower.contains("web") || qLower.contains("google")) {
                        externalApps.performWebSearch(voiceQuery)
                    }
                    onSelectRecentTopic(voiceQuery)
                }
            )
        }

        // Enterprise Global Search Dialog
        if (showGlobalSearchDialog) {
            GlobalSearchDialog(
                onDismiss = { showGlobalSearchDialog = false },
                onSelectResultInChat = { queryText ->
                    showGlobalSearchDialog = false
                    onSelectRecentTopic(queryText)
                },
                onRunInSandbox = { cmd ->
                    showGlobalSearchDialog = false
                    onNavigateToCode()
                }
            )
        }
    }
}

@Composable
fun ClaudeHomeNavItem(
    icon: ImageVector,
    title: String,
    textColor: Color,
    accentBadge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Normal,
                color = textColor,
                fontSize = 17.sp
            ),
            modifier = Modifier.weight(1f)
        )

        accentBadge?.let { badge ->
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFE2DDD3)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF5A5751),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
