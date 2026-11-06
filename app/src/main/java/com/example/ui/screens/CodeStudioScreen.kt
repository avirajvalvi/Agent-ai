package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
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
import com.example.data.models.SecurityIssue
import com.example.data.models.SecuritySeverity
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.theme.TerracottaPrimary
import com.example.ui.viewmodels.CodeStudioViewModel

@Composable
fun CodeStudioScreen(
    codeStudioViewModel: CodeStudioViewModel,
    onOpenDrawer: () -> Unit,
    onSendToSandbox: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val sourceCode by codeStudioViewModel.sourceCode.collectAsState()
    val report by codeStudioViewModel.analysisReport.collectAsState()
    val selectedLang by codeStudioViewModel.selectedLanguage.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Editor & Templates", "Audit Report (${report?.issues?.size ?: 0})", "Patched Code")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181716))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("code_studio_screen")
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
                        text = "Code Analyzer & Studio",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = Color(0xFFFAF8F5)
                        )
                    )
                }

                Button(
                    onClick = {
                        codeStudioViewModel.runAnalysis()
                        selectedTabIndex = 1
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Audit", style = MaterialTheme.typography.labelMedium)
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
                    // Editor & Templates Tab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Templates Quick Bar
                        Text(
                            text = "Sample Lab Templates:",
                            style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFFA8A29E)),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = { codeStudioViewModel.loadTemplate("sqli") },
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF2C2A28)
                            ) {
                                Text(
                                    text = "SQLi & Injection Sample",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberRose,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            Surface(
                                onClick = { codeStudioViewModel.loadTemplate("scanner") },
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF2C2A28)
                            ) {
                                Text(
                                    text = "Custom Port Scanner",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberEmerald,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            Surface(
                                onClick = { codeStudioViewModel.loadTemplate("bash") },
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF2C2A28)
                            ) {
                                Text(
                                    text = "Bash Recon Script",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberCyan,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Code Editor Box
                        OutlinedTextField(
                            value = sourceCode,
                            onValueChange = { codeStudioViewModel.updateCode(it) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .testTag("code_editor_field"),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = Color(0xFFFAF8F5),
                                lineHeight = 18.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF131211),
                                unfocusedContainerColor = Color(0xFF131211),
                                focusedBorderColor = Color(0xFF383531),
                                unfocusedBorderColor = Color(0xFF2C2A28),
                                cursorColor = TerracottaPrimary
                            ),
                            placeholder = {
                                Text("Paste Python, C++, Go, or Bash script to analyze...", color = Color(0xFF78716C))
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Lines: ${sourceCode.lines().size} | Lang: $selectedLang",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E))
                            )

                            Button(
                                onClick = {
                                    val scriptCmd = "python3 -c \"${sourceCode.replace("\"", "\\\"").take(150)}\""
                                    onSendToSandbox(scriptCmd)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262422)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Test in Sandbox", color = Color(0xFFFAF8F5), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                1 -> {
                    // Audit Report Tab
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            // Summary Card
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF23211F))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if ((report?.issues?.size ?: 0) == 0) Icons.Default.CheckCircle else Icons.Default.BugReport,
                                            contentDescription = null,
                                            tint = if ((report?.issues?.size ?: 0) == 0) CyberEmerald else CyberAmber,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Automated Security Audit Findings",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFFAF8F5)
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = report?.summary ?: "No audit generated yet.",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFD6D3D1))
                                    )
                                }
                            }
                        }

                        val issues = report?.issues ?: emptyList()
                        if (issues.isEmpty()) {
                            item {
                                Surface(
                                    color = Color(0xFF1B2E24),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Great job! No high-risk vulnerability patterns detected.",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                                        )
                                    }
                                }
                            }
                        } else {
                            items(issues) { issue ->
                                SecurityIssueCard(issue = issue)
                            }
                        }
                    }
                }

                2 -> {
                    // Patched Code Tab
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
                                text = "Auto-Remediated Code:",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFAF8F5)
                                )
                            )

                            Button(
                                onClick = {
                                    codeStudioViewModel.applyPatchedCode()
                                    selectedTabIndex = 0
                                    Toast.makeText(context, "Patched code applied to editor!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Apply Patch", color = Color.Black, style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = Color(0xFF121110),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp)
                            ) {
                                item {
                                    Text(
                                        text = report?.patchedCode ?: sourceCode,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp,
                                            color = CyberEmerald,
                                            lineHeight = 18.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(report?.patchedCode ?: sourceCode))
                                    Toast.makeText(context, "Patched code copied", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262422)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy Patched Code", color = Color.White, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityIssueCard(issue: SecurityIssue) {
    val severityColor = when (issue.severity) {
        SecuritySeverity.CRITICAL -> CyberRose
        SecuritySeverity.HIGH -> CyberRose
        SecuritySeverity.MEDIUM -> CyberAmber
        SecuritySeverity.LOW -> CyberCyan
        SecuritySeverity.INFO -> Color(0xFF94A3B8)
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
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = severityColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${issue.severity.name} SEVERITY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = severityColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "Line ${issue.lineNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA8A29E))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = issue.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFAF8F5),
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = issue.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFD6D3D1),
                    fontSize = 13.5.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFF2E2C29))
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Remediation & Secure Fix:",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TerracottaPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = issue.remediation,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = CyberEmerald,
                    fontSize = 12.sp
                )
            )
        }
    }
}
