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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.theme.TerracottaPrimary
import com.example.ui.viewmodels.TerminalViewModel

@Composable
fun TerminalScreen(
    terminalViewModel: TerminalViewModel,
    onOpenDrawer: () -> Unit,
    onAskAiAboutOutput: (String) -> Unit
) {
    val context = LocalContext.current
    val history by terminalViewModel.history.collectAsState()
    val promptPrefix by terminalViewModel.currentPrompt.collectAsState()

    var inputCommand by remember { mutableStateOf("") }
    var showHelpDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    val quickCommands = listOf(
        "ls -la",
        "cat README.md",
        "python3 labs/port_scanner.py",
        "python3 labs/log_parser.py",
        "nmap -sV 192.168.1.100",
        "grep \"Failed\" /var/log/auth.log",
        "tshark -c 5 -i eth0",
        "curl -I https://sandbox-webapp.local",
        "git clone https://github.com/nmap/nmap",
        "clear"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E0D))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("terminal_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Terminal Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161514))
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
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Linux Sandbox CLI",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberEmerald,
                            fontSize = 16.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showHelpDialog = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Help Guide",
                            tint = Color(0xFFA8A29E)
                        )
                    }

                    IconButton(
                        onClick = { terminalViewModel.clearTerminal() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Terminal",
                            tint = Color(0xFFA8A29E)
                        )
                    }
                }
            }

            // Terminal Console Buffer
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(history) { entry ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Prompt + Command line
                        if (entry.command != "boot") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "secbox@mentor:${entry.directory}$ ",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = CyberCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = entry.command,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFFFAF8F5),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }

                        // Output Body
                        if (entry.output.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = entry.output,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = if (entry.isError) CyberRose else Color(0xFFD6D3D1),
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.sp
                                )
                            )

                            // Quick Action to Ask AI about this output
                            if (entry.command != "boot" && entry.output.lines().size > 2) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    onClick = {
                                        val prompt = "Please analyze this sandbox terminal output for '${entry.command}':\n\n${entry.output}"
                                        onAskAiAboutOutput(prompt)
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF1E1D1B)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QuestionAnswer,
                                            contentDescription = null,
                                            tint = TerracottaPrimary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Ask SecMentor to analyze this scan",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TerracottaPrimary,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Command Toolbar (Horizontal Scroll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161514))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickCommands.forEach { cmd ->
                    Surface(
                        onClick = {
                            terminalViewModel.runCommand(cmd)
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF262422)
                    ) {
                        Text(
                            text = cmd,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = CyberEmerald,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Direct Command Input Row
            Surface(
                color = Color(0xFF121110),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$ ",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )

                    OutlinedTextField(
                        value = inputCommand,
                        onValueChange = { inputCommand = it },
                        placeholder = {
                            Text(
                                text = "Enter command (e.g., nmap, python3, ls)...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF6B7280),
                                    fontSize = 13.sp
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("terminal_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color(0xFFFAF8F5),
                            unfocusedTextColor = Color(0xFFFAF8F5),
                            cursorColor = CyberEmerald
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.5.sp
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputCommand.isNotBlank()) {
                                    terminalViewModel.runCommand(inputCommand)
                                    inputCommand = ""
                                }
                            }
                        )
                    )

                    Surface(
                        onClick = {
                            if (inputCommand.isNotBlank()) {
                                terminalViewModel.runCommand(inputCommand)
                                inputCommand = ""
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1F2937),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Run Command",
                                tint = CyberEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Help Modal Dialog
        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                title = {
                    Text(
                        text = "SecMentor Sandbox CLI Guide",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "This terminal is a virtual defensive Linux environment designed to safely practice CLI commands and analyze open-source cyber tools.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFD6D3D1))
                        )
                        Text(
                            text = "Key Lab Workflows:\n• nmap 192.168.1.100 : Scan simulated webapp\n• python3 labs/port_scanner.py : Run custom scanner\n• grep 'Failed' /var/log/auth.log : SSH brute-force forensics\n• git clone <url> : Practice cloning repos safely",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = CyberEmerald
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHelpDialog = false }) {
                        Text("Got It", color = TerracottaPrimary)
                    }
                },
                containerColor = Color(0xFF242220)
            )
        }
    }
}
