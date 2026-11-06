package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.theme.TerracottaPrimary
import com.example.ui.viewmodels.FileAnalyzerViewModel

@Composable
fun FileAnalyzerScreen(
    fileAnalyzerViewModel: FileAnalyzerViewModel,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val report by fileAnalyzerViewModel.currentReport.collectAsState()
    val userSettings by fileAnalyzerViewModel.userSettings.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Summary", "Keywords (${report.keywords.size})", "Content Editor")

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "Uploaded_Document.txt"
            fileAnalyzerViewModel.ingestUserFile(uri, fileName)
            Toast.makeText(context, "Analyzing $fileName...", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181716))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("file_analyzer_screen")
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
                        text = "File & Doc Analyzer",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            color = Color(0xFFFAF8F5)
                        )
                    )
                }

                Button(
                    onClick = { filePickerLauncher.launch("*/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload File", style = MaterialTheme.typography.labelSmall)
                }
            }

            // DataStore-backed Offline Mode Toggle & Cached Cybersecurity Analysis Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (userSettings.isOfflineMode) Color(0xFF2E2218) else Color(0xFF22201E),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (userSettings.isOfflineMode) TerracottaPrimary.copy(alpha = 0.5f) else Color(0xFF383531)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (userSettings.isOfflineMode) Icons.Default.CloudOff else Icons.Default.Cached,
                                contentDescription = null,
                                tint = if (userSettings.isOfflineMode) TerracottaPrimary else CyberEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (userSettings.isOfflineMode) "Offline Mode (Disconnected Cache Active)" else "Offline Mode",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFFFAF8F5),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = if (userSettings.isOfflineMode) "Preserving last processed cybersecurity analysis" else "Auto-caches cybersecurity analysis when disconnected",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA8A29E), fontSize = 11.sp)
                                )
                            }
                        }

                        Switch(
                            checked = userSettings.isOfflineMode,
                            onCheckedChange = { checked ->
                                fileAnalyzerViewModel.toggleOfflineMode(checked)
                                Toast.makeText(
                                    context,
                                    if (checked) "Offline Mode Enabled: Cybersecurity analysis cached in DataStore" else "Offline Mode Disabled",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = TerracottaPrimary,
                                uncheckedThumbColor = Color(0xFFA8A29E),
                                uncheckedTrackColor = Color(0xFF383531)
                            ),
                            modifier = Modifier.testTag("offline_mode_toggle")
                        )
                    }

                    userSettings.cachedAnalysis?.let { cached ->
                        Spacer(modifier = Modifier.height(8.dp))
                        val dateFormatted = if (cached.timestamp > 0) {
                            SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(cached.timestamp))
                        } else "Recent"

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF141312),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Security,
                                        contentDescription = null,
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Cached Analysis: ${cached.title} ($dateFormatted)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyberEmerald,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Surface(
                                    onClick = {
                                        fileAnalyzerViewModel.loadCachedAnalysis(cached)
                                        Toast.makeText(context, "Loaded cached analysis: ${cached.title}", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    color = TerracottaPrimary
                                ) {
                                    Text(
                                        text = "Load",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Sample Pills (Matching Screenshot 6: DOC, PDF, CODE)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = { fileAnalyzerViewModel.loadSample("pdf") },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF262422),
                    border = if (report.fileName.endsWith(".pdf")) androidx.compose.foundation.BorderStroke(1.dp, TerracottaPrimary) else null
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFDC2626)) {
                            Text("PDF", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(3.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Security_Whitepaper.pdf", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }

                Surface(
                    onClick = { fileAnalyzerViewModel.loadSample("docx") },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF262422),
                    border = if (report.fileName.endsWith(".docx")) androidx.compose.foundation.BorderStroke(1.dp, TerracottaPrimary) else null
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF2563EB)) {
                            Text("DOC", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(3.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Incident_Playbook.docx", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }

                Surface(
                    onClick = { fileAnalyzerViewModel.loadSample("code") },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF262422),
                    border = if (report.fileName.endsWith(".py")) androidx.compose.foundation.BorderStroke(1.dp, TerracottaPrimary) else null
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(4.dp), color = CyberEmerald) {
                            Text("CODE", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(3.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ethical_Scanner.py", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }

            // Tab Navigation
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF1E1D1B),
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
                    // Summary Tab
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            // File Metadata Card
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF22201E))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = report.fileName,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFFAF8F5)
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${report.fileType.displayName} • ${report.wordCount} words",
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E))
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = CyberEmerald.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "ANALYZED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = CyberEmerald,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            // Executive Summary Card
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF22201E))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TextSnippet, contentDescription = null, tint = TerracottaPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Executive Summary",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFFAF8F5)
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = report.executiveSummary,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFFFAF8F5),
                                            lineHeight = 22.sp
                                        )
                                    )
                                }
                            }
                        }

                        item {
                            // Key Findings
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF22201E))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Key Takeaways & Findings",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFFAF8F5)
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    report.keyFindings.forEach { finding ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text("•", color = TerracottaPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                                            Text(
                                                text = finding,
                                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFD6D3D1))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Keywords Tab
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Extracted Keywords & Entity Tags",
                                style = MaterialTheme.typography.labelLarge.copy(color = Color(0xFFA8A29E)),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        items(report.keywords) { kw ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF22201E),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Key, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = kw.keyword,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF2E2C29)
                                        ) {
                                            Text(
                                                text = kw.category,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = when (kw.category) {
                                                        "Recon" -> CyberEmerald
                                                        "Vulnerability" -> CyberRose
                                                        "Defense" -> CyberCyan
                                                        else -> CyberAmber
                                                    }
                                                ),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${kw.count}x",
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Content Editor Tab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "In-App Content Editor:",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFAF8F5)
                                )
                            )

                            Row {
                                Button(
                                    onClick = {
                                        fileAnalyzerViewModel.applyAiImprovement()
                                        Toast.makeText(context, "AI annotations applied!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2A28)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(end = 6.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TerracottaPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("AI Polish", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                }

                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(report.editedContent))
                                        Toast.makeText(context, "Saved & Downloaded ${report.fileName}", Toast.LENGTH_LONG).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Download", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = report.editedContent,
                            onValueChange = { fileAnalyzerViewModel.updateEditedContent(it) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .testTag("file_content_editor"),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = Color(0xFFFAF8F5),
                                lineHeight = 19.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF131211),
                                unfocusedContainerColor = Color(0xFF131211),
                                focusedBorderColor = Color(0xFF383531),
                                unfocusedBorderColor = Color(0xFF2C2A28),
                                cursorColor = TerracottaPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}
