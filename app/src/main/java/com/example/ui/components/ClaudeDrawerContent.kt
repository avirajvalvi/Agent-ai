package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TerracottaDark
import com.example.ui.theme.TerracottaPrimary

import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.data.localization.LanguageManager
import androidx.compose.material.icons.filled.School
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

enum class AppDestination(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.AutoMirrored.Outlined.Chat),
    CHATS("Chats", Icons.AutoMirrored.Outlined.Chat),
    HACKING_ACADEMY("60-90 Day Academy", Icons.Default.School),
    EXTERNAL_HUB("Termux & YouTube", Icons.Default.AutoAwesome),
    AGENTIC("Autonomous Agent", Icons.Default.AccountTree),
    APP_BUILDER("AI App Builder", Icons.Default.AutoAwesome),
    FILE_ANALYZER("Files & Docs", Icons.Outlined.Description),
    CODE("Code & Sandbox", Icons.Outlined.Code),
    VIDEO("Video Studio", Icons.Default.Movie),
    PROJECTS("Projects", Icons.Outlined.FolderSpecial),
    ARTIFACTS("Artifacts", Icons.Outlined.Description)
}

@Composable
fun ClaudeDrawerContent(
    currentDestination: AppDestination,
    onSelectDestination: (AppDestination) -> Unit,
    onSelectRecentTopic: (String) -> Unit,
    onNewChat: () -> Unit,
    onOpenLanguageSelector: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val context = LocalContext.current
    val langManager = remember { LanguageManager.getInstance(context) }
    val currentUser = Firebase.auth.currentUser

    val sampleRecents = listOf(
        "Nmap TCP SYN Stealth Scan Lab",
        "SSH auth.log Brute-Force Detection",
        "Python Socket Port Scanner",
        "Build command termux setup",
        "ऑडियो बुक्स की ऑनलाइन बुक स्टोर",
        "बिजनेसमैन रोलप्ले",
        "Client project setup"
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(310.dp)
            .background(Color(0xFF181716))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 24.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
            .testTag("claude_drawer")
    ) {
        // App Title in Serif + Language Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SecMentor AI",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 28.sp,
                    color = Color(0xFFFAF8F5)
                ),
                modifier = Modifier.clickable { onSelectDestination(AppDestination.HOME) }
            )

            Surface(
                onClick = onOpenLanguageSelector,
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF262422),
                modifier = Modifier.testTag("drawer_language_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = Color(0xFFD6D3D1),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = langManager.currentLanguage.value.code.uppercase(java.util.Locale.ROOT),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFFAF8F5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Navigation Sections matching Screenshot 3
        AppDestination.values().forEach { destination ->
            val isSelected = currentDestination == destination
            val label = when (destination) {
                AppDestination.HOME -> langManager.getString("home")
                AppDestination.CHATS -> langManager.getString("chats")
                AppDestination.HACKING_ACADEMY -> langManager.getString("hacking_academy")
                AppDestination.EXTERNAL_HUB -> langManager.getString("external_hub")
                AppDestination.AGENTIC -> langManager.getString("autonomous_agent")
                AppDestination.APP_BUILDER -> langManager.getString("ai_app_builder")
                AppDestination.FILE_ANALYZER -> langManager.getString("files_docs")
                AppDestination.CODE -> langManager.getString("code_sandbox")
                AppDestination.VIDEO -> langManager.getString("video_studio")
                AppDestination.PROJECTS -> langManager.getString("projects")
                AppDestination.ARTIFACTS -> langManager.getString("artifacts")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        color = if (isSelected) Color(0xFF262422) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelectDestination(destination) }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = label,
                    tint = if (isSelected) TerracottaPrimary else Color(0xFFD6D3D1),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFFFAF8F5) else Color(0xFFE7E5E4)
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0xFF2C2A28), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // Recents Header
        Text(
            text = "Recents",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFA8A29E),
                fontSize = 13.sp
            ),
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        // Recents List
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(sampleRecents) { recentTitle ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clickable { onSelectRecentTopic(recentTitle) }
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Recent chat",
                        tint = Color(0xFF78716C),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = recentTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFD6D3D1)
                        ),
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Row: User Avatar / Sign Out and "+ New chat" Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Avatar (showing user initial, e.g. 'A' or first letter of email)
            val userInitial = currentUser?.email?.firstOrNull()?.uppercase() ?: "A"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    if (currentUser != null) {
                        onSignOut()
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(TerracottaDark, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userInitial,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if (currentUser != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Sign out",
                        tint = Color(0xFFA8A29E),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Floating "+ New chat" pill
            Surface(
                onClick = onNewChat,
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF242220),
                modifier = Modifier
                    .height(44.dp)
                    .testTag("new_chat_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New chat",
                        tint = Color(0xFFFAF8F5),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = langManager.getString("new_chat"),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color(0xFFFAF8F5),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
