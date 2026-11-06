package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.integration.ExternalAppsManager
import com.example.data.models.SecuritySeverity
import com.example.data.search.GlobalSearchEngine
import com.example.data.search.SearchResultItem
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.theme.TerracottaPrimary

@Composable
fun GlobalSearchDialog(
    initialQuery: String = "",
    onDismiss: () -> Unit,
    onSelectResultInChat: (String) -> Unit,
    onRunInSandbox: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val externalApps = remember { ExternalAppsManager(context) }

    var query by remember { mutableStateOf(initialQuery) }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = listOf("ALL", "CVE", "TOOL", "MITRE", "DEFENSE", "PROTOCOL")
    val searchResults = remember(query, selectedCategory) {
        GlobalSearchEngine.search(query, selectedCategory)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF141312))
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("global_search_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Search Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search CVEs, MITRE ATT&CK, Nmap, Wireshark, Defense rules...", color = Color(0xFFA8A29E), fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TerracottaPrimary)
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFFA8A29E))
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TerracottaPrimary,
                            unfocusedBorderColor = Color(0xFF383531),
                            focusedContainerColor = Color(0xFF1C1A18),
                            unfocusedContainerColor = Color(0xFF1C1A18)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("global_search_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF262422))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Filter Category Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            onClick = { selectedCategory = cat },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) TerracottaPrimary else Color(0xFF262422),
                            modifier = Modifier.testTag("filter_chip_$cat")
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFFA8A29E)
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Results Counter & Web Fallback Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Found ${searchResults.size} enterprise results",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E))
                    )

                    if (query.isNotBlank()) {
                        Surface(
                            onClick = {
                                externalApps.performWebSearch(query)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E2A38)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Search Live Web", color = CyberCyan, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                // Search Results Feed
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
                ) {
                    items(searchResults) { item ->
                        SearchResultCard(
                            item = item,
                            onAskInChat = {
                                onSelectResultInChat("Tell me more about ${item.title}: ${item.summary}")
                                onDismiss()
                            },
                            onRunInSandbox = { code ->
                                onRunInSandbox(code)
                                onDismiss()
                            },
                            onCopyCode = { code ->
                                clipboardManager.setText(AnnotatedString(code))
                                Toast.makeText(context, "Copied command to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    item: SearchResultItem,
    onAskInChat: () -> Unit,
    onRunInSandbox: (String) -> Unit,
    onCopyCode: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1D1B)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF32302D), RoundedCornerShape(14.dp))
            .testTag("search_result_${item.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Category Badge + Severity + Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (item.category) {
                        "CVE" -> Color(0xFF381E1E)
                        "MITRE" -> Color(0xFF38291E)
                        "TOOL" -> Color(0xFF1E2E24)
                        else -> Color(0xFF1E2438)
                    }
                ) {
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (item.category) {
                                "CVE" -> CyberRose
                                "MITRE" -> CyberAmber
                                "TOOL" -> CyberEmerald
                                else -> CyberCyan
                            },
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFAF8F5),
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Severity pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (item.severity) {
                        SecuritySeverity.CRITICAL -> Color(0xFF451010)
                        SecuritySeverity.HIGH -> Color(0xFF422010)
                        SecuritySeverity.MEDIUM -> Color(0xFF3E3010)
                        SecuritySeverity.LOW -> Color(0xFF1B3820)
                        SecuritySeverity.INFO -> Color(0xFF1A2840)
                    }
                ) {
                    Text(
                        text = item.severity.name,
                        color = when (item.severity) {
                            SecuritySeverity.CRITICAL -> CyberRose
                            SecuritySeverity.HIGH -> CyberAmber
                            SecuritySeverity.MEDIUM -> CyberAmber
                            SecuritySeverity.LOW -> CyberEmerald
                            SecuritySeverity.INFO -> CyberCyan
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFD6D3D1),
                    fontSize = 13.sp
                )
            )

            // Code Snippet Block
            item.codeSnippet?.let { snippet ->
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F0E0D))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Verified Command / Artifact:",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E), fontSize = 10.sp)
                            )
                            IconButton(
                                onClick = { onCopyCode(snippet) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFFA8A29E), modifier = Modifier.size(14.dp))
                            }
                        }
                        Text(
                            text = snippet,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF86EFAC)
                            )
                        )
                    }
                }
            }

            // Remediation Info
            item.remediation?.let { rem ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🛡️ Remediation: $rem",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyberEmerald,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ask in Chat button
                Surface(
                    onClick = onAskInChat,
                    shape = RoundedCornerShape(8.dp),
                    color = TerracottaPrimary,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ask SecMentor", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }

                // Run in Sandbox button (if snippet available)
                if (item.codeSnippet != null) {
                    Surface(
                        onClick = { onRunInSandbox(item.codeSnippet) },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF2C2A28),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Sandbox", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
