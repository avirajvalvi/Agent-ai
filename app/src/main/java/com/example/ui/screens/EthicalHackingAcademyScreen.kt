package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.OnDeviceOfflineLlmEngine
import com.example.data.curriculum.CurriculumManager
import com.example.data.models.CurriculumModule
import com.example.data.models.CurriculumPhase
import com.example.data.models.CurriculumProgress
import com.example.data.voice.VoiceCommand
import com.example.data.voice.VoiceCommanderService
import com.example.ui.components.CurriculumModuleCard
import com.example.ui.components.CurriculumPhaseHeader
import com.example.ui.components.CurriculumProgressDashboard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.theme.TerracottaPrimary
import kotlinx.coroutines.launch

@Composable
fun EthicalHackingAcademyScreen(
    onOpenDrawer: () -> Unit,
    onRunInTerminal: (String) -> Unit,
    onAskInChat: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        "60-90 Day Roadmap",
        "n8n AI Agents & Prompts",
        "GitHub Workflows & Deploy",
        "Offline Voice Assistant"
    )

    // Dedicated Curriculum Manager for progress tracking and module locking
    val curriculumManager = remember { CurriculumManager(context) }
    val curriculumProgress by curriculumManager.progress.collectAsState()
    val computedPhases = remember(curriculumProgress) { curriculumManager.getComputedPhases() }

    // Voice Commander instance for continuous offline dialogue
    val voiceCommander = remember { VoiceCommanderService(context) }
    val isListening by voiceCommander.isListening.collectAsState()
    val isContinuous by voiceCommander.isContinuous.collectAsState()
    val liveTranscript by voiceCommander.liveTranscript.collectAsState()
    val rmsVolume by voiceCommander.rmsVolume.collectAsState()
    var offlineVoiceAnswer by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            voiceCommander.destroy()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181716))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("ethical_hacking_academy_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier.testTag("academy_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Drawer",
                        tint = Color(0xFFFAF8F5)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "SecMentor Academy",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            color = Color(0xFFFAF8F5),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "60-90 Days Roadmap & n8n AI Agents",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFA8A29E)
                        )
                    )
                }
            }

            // Quick Voice Trigger Pill
            Surface(
                onClick = {
                    if (isListening) {
                        voiceCommander.stopListening()
                    } else {
                        voiceCommander.setContinuousMode(true)
                        voiceCommander.startListening { query, _ ->
                            coroutineScope.launch {
                                val res = OnDeviceOfflineLlmEngine.generateResponse(query)
                                offlineVoiceAnswer = res.answer
                                voiceCommander.speak(res.answer)
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(20.dp),
                color = if (isListening) TerracottaPrimary else Color(0xFF262422),
                modifier = Modifier.testTag("academy_quick_voice_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Offline Voice",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isListening) "Listening..." else "Offline Voice",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                    )
                }
            }
        }

        // Horizontal Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF1E1D1B),
            contentColor = TerracottaPrimary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = TerracottaPrimary,
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) Color(0xFFFAF8F5) else Color(0xFFA8A29E)
                            )
                        )
                    }
                )
            }
        }

        // Active Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedTabIndex) {
                0 -> RoadmapTabContent(
                    phases = computedPhases,
                    progress = curriculumProgress,
                    onToggleInstructorBypass = { enabled ->
                        curriculumManager.toggleInstructorBypass(enabled)
                    },
                    onResetProgress = {
                        curriculumManager.resetCurriculumProgress()
                    },
                    onRunCommand = onRunInTerminal,
                    onAskAi = onAskInChat,
                    onCompleteModule = { modId ->
                        curriculumManager.markModuleCompleted(modId)
                    }
                )
                1 -> N8nAiAgentsTabContent(
                    onRunTerminal = onRunInTerminal,
                    onAskAi = onAskInChat
                )
                2 -> GitHubWorkflowsTabContent(
                    onRunTerminal = onRunInTerminal,
                    onCopyCode = { code ->
                        clipboardManager.setText(AnnotatedString(code))
                        Toast.makeText(context, "GitHub Workflow YAML Copied!", Toast.LENGTH_SHORT).show()
                    }
                )
                3 -> OfflineVoiceAssistantTabContent(
                    voiceCommander = voiceCommander,
                    isListening = isListening,
                    isContinuous = isContinuous,
                    liveTranscript = liveTranscript,
                    rmsVolume = rmsVolume,
                    offlineAnswer = offlineVoiceAnswer,
                    onAnswerGenerated = { ans -> offlineVoiceAnswer = ans },
                    onRunCommand = onRunInTerminal
                )
            }
        }
    }
}

@Composable
fun RoadmapTabContent(
    phases: List<CurriculumPhase>,
    progress: CurriculumProgress,
    onToggleInstructorBypass: (Boolean) -> Unit,
    onResetProgress: () -> Unit,
    onRunCommand: (String) -> Unit,
    onAskAi: (String) -> Unit,
    onCompleteModule: (String) -> Unit
) {
    val totalModules = remember(phases) { phases.sumOf { it.modules.size } }
    val completedModules = progress.completedModuleIds.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            CurriculumProgressDashboard(
                progress = progress,
                totalModulesCount = totalModules,
                completedModulesCount = completedModules,
                onToggleInstructorBypass = onToggleInstructorBypass,
                onResetProgress = onResetProgress
            )
        }

        phases.forEach { phase ->
            item(key = "header_${phase.id}") {
                CurriculumPhaseHeader(phase = phase)
            }

            items(phase.modules, key = { it.id }) { module ->
                CurriculumModuleCard(
                    module = module,
                    onRunInTerminal = onRunCommand,
                    onAskAi = onAskAi,
                    onCompleteModule = onCompleteModule
                )
            }
        }
    }
}

@Composable
fun N8nAiAgentsTabContent(
    onRunTerminal: (String) -> Unit,
    onAskAi: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val n8nWorkflowJson = """
{
  "name": "Autonomous Security Triage AI Agent",
  "nodes": [
    {
      "parameters": {
        "httpMethod": "POST",
        "path": "security-event",
        "responseMode": "onReceived"
      },
      "name": "Webhook Trigger",
      "type": "n8n-nodes-base.webhook",
      "position": [250, 300]
    },
    {
      "parameters": {
        "model": "gemini-1.5-flash",
        "prompt": "You are an autonomous defensive security analyst. Analyze the following security alert:\n\n{{ ${'$'}json.body }}\n\nProvide root cause analysis, severity assessment, and exact remediation commands in JSON format."
      },
      "name": "LangChain Gemini AI Node",
      "type": "@n8n/n8n-nodes-langchain.agent",
      "position": [480, 300]
    },
    {
      "parameters": {
        "conditions": {
          "string": [
            {
              "value1": "={{ ${'$'}json.severity }}",
              "value2": "CRITICAL"
            }
          ]
        }
      },
      "name": "Severity Filter",
      "type": "n8n-nodes-base.if",
      "position": [700, 300]
    },
    {
      "parameters": {
        "owner": "my-org",
        "repository": "security-sandbox",
        "title": "🚨 [ALERT] {{ ${'$'}json.alertTitle }}",
        "body": "={{ ${'$'}json.aiAnalysis }}\n\n**Remediation:**\n```bash\n{{ ${'$'}json.remediationCommand }}\n```"
      },
      "name": "Create GitHub Issue",
      "type": "n8n-nodes-base.github",
      "position": [920, 200]
    }
  ]
}
    """.trimIndent()

    val masterPrompts = listOf(
        Pair(
            "1. Security Auditor & CVE Triaging Agent Prompt",
            """
You are SecAuditor, an autonomous level-3 cybersecurity analyst and code reviewer.
Your objective:
1. Ingest raw vulnerability reports, CVE identifiers, and network scan logs.
2. Filter out false positives by cross-referencing with official MITRE ATT&CK and NVD catalogs.
3. Formulate strict, defensive shell commands (iptables, UFW, systemctl patch updates, or Suricata rules) to neutralize the threat.
4. Output strictly structured JSON with fields: { "cveId", "severity", "threatVector", "mitigationCommands": [], "falsePositiveProbability": 0.0-1.0 }.
            """.trimIndent()
        ),
        Pair(
            "2. Automated GitHub PR Code Reviewer Agent Prompt",
            """
You are SecCodeReviewer, an automated DevSecOps bot running inside GitHub Actions.
Your objective:
1. Inspect git diffs for hardcoded API keys, JWT secrets, insecure deserialization, SQL injection, and buffer overflows.
2. Flag any non-parameterized queries or missing input validation.
3. Recommend safe refactored code snippets following OWASP Secure Coding Practices.
4. If a critical secret or vulnerability is detected, output: { "status": "FAIL", "reason": "...", "suggestedFix": "..." }.
            """.trimIndent()
        ),
        Pair(
            "3. Threat Intelligence & Honeypot Watcher Agent",
            """
You are HoneypotWatcher, an autonomous telemetry agent monitoring unauthorized probe attempts.
Your objective:
1. Parse live SSH / HTTP connection attempts from honeypot webhooks.
2. Extract source IPs, user-agent signatures, and brute-force passwords.
3. Automatically generate temporary null-route iptables drop rules for offending IPs exceeding 5 failed attempts per minute.
            """.trimIndent()
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF262422)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚀 n8n ऑटोनॉमस AI एजेंट आर्किटेक्चर",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFFFAF8F5),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "n8n एक शक्तिशाली नो-कोड/लो-कोड वर्कफ़्लो ऑटोमेशन इंजन है। यहाँ आप LangChain और Gemini AI नोड्स का उपयोग करके ऑटोनॉमस सिक्योरिटी एजेंट्स बना सकते हैं जो 24/7 वेबहुक्स, लॉग्स और अलर्ट्स को प्रोसेस करते हैं।",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD6D3D1))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            onClick = {
                                onRunTerminal("docker run -d --name n8n -p 5678:5678 -v ~/.n8n:/home/node/.n8n docker.n8n.io/n8nio/n8n")
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E2D24),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Docker n8n Run", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            onClick = {
                                clipboard.setText(AnnotatedString(n8nWorkflowJson))
                                Toast.makeText(context, "n8n Workflow JSON Copied!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF2C2A28),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy n8n JSON", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "🎯 प्रोडक्शन-रेडी AI एजेंट प्रॉम्प्ट्स (Master Prompts)",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        items(masterPrompts) { (title, promptContent) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1D1B)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TerracottaPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(promptContent))
                                Toast.makeText(context, "Prompt copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Prompt",
                                tint = Color(0xFFA8A29E),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF121110),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = promptContent,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFD6D3D1),
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GitHubWorkflowsTabContent(
    onRunTerminal: (String) -> Unit,
    onCopyCode: (String) -> Unit
) {
    val workflowYaml = """
name: Autonomous Security Agent CI/CD Pipeline

on:
  push:
    branches: [ "main", "dev" ]
  pull_request:
    branches: [ "main" ]
  schedule:
    # Run daily vulnerability and CVE audit at 02:00 UTC
    - cron: '0 2 * * *'
  workflow_dispatch:

jobs:
  security-audit-and-n8n-trigger:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up Python 3.11
        uses: actions/setup-python@v5
        with:
          python-version: '3.11'

      - name: Install Defensive Security Tools
        run: |
          python -m pip install --upgrade pip
          pip install bandit safety flake8

      - name: Static Security Code Analysis (SAST)
        run: |
          echo "Running Bandit SAST audit on Python codebase..."
          bandit -r . -f json -o bandit-report.json || true
          cat bandit-report.json

      - name: Dependency Vulnerability Check
        run: |
          echo "Checking third-party packages for known CVEs..."
          safety check --output json > safety-report.json || true

      - name: Dispatch Alert to Autonomous n8n AI Agent
        env:
          N8N_WEBHOOK_URL: ${'$'}{{ secrets.N8N_WEBHOOK_URL }}
          N8N_AUTH_TOKEN: ${'$'}{{ secrets.N8N_AUTH_TOKEN }}
        run: |
          if [ -n "${'$'}N8N_WEBHOOK_URL" ]; then
            echo "Sending security audit telemetry to n8n AI agent webhook..."
            curl -X POST "${'$'}N8N_WEBHOOK_URL" \
              -H "Content-Type: application/json" \
              -H "Authorization: Bearer ${'$'}N8N_AUTH_TOKEN" \
              -d @bandit-report.json
          else
            echo "N8N_WEBHOOK_URL secret not configured; skipping remote dispatch."
          fi
    """.trimIndent()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF262422)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🛠️ गिटहब वर्कफ़्लो और ऑटोनॉमस CI/CD डिप्लॉयमेंट",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFFFAF8F5),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "इस वर्कफ़्लो फ़ाइल को अपनी रीपॉज़िटरी में `.github/workflows/ai-security-agent.yml` के रूप में सेव करें। यह हर पुश/PR पर ऑटोमैटिक स्टैटिक सिक्योरिटी एनालिसिस चलाएगा और रिज़ल्ट्स सीधे आपके n8n AI एजेंट को भेजेगा।",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD6D3D1))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            onClick = { onCopyCode(workflowYaml) },
                            shape = RoundedCornerShape(10.dp),
                            color = TerracottaPrimary,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy Workflow YAML", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            onClick = { onRunTerminal("mkdir -p .github/workflows && cat << 'EOF' > .github/workflows/ai-security-agent.yml\n$workflowYaml\nEOF") },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E2D24),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create in Sandbox", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF141312),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = ".github/workflows/ai-security-agent.yml",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            color = CyberCyan
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = workflowYaml,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun OfflineVoiceAssistantTabContent(
    voiceCommander: VoiceCommanderService,
    isListening: Boolean,
    isContinuous: Boolean,
    liveTranscript: String,
    rmsVolume: Float,
    offlineAnswer: String?,
    onAnswerGenerated: (String) -> Unit,
    onRunCommand: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF262422)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🎙️ 100% ऑफ़लाइन ऑन-डिवाइस वॉइस चैट",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFFFAF8F5),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "इंटरनेट के बिना निरंतर बातचीत (Continuous Voice Dialogue)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberEmerald
                            )
                        )
                    }

                    // Continuous mode toggle
                    Surface(
                        onClick = {
                            val nextState = !isContinuous
                            voiceCommander.setContinuousMode(nextState)
                            if (nextState) {
                                voiceCommander.startListening { query, _ ->
                                    coroutineScope.launch {
                                        val res = OnDeviceOfflineLlmEngine.generateResponse(query)
                                        onAnswerGenerated(res.answer)
                                        voiceCommander.speak(res.answer)
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isContinuous) TerracottaPrimary else Color(0xFF383532)
                    ) {
                        Text(
                            text = if (isContinuous) "Continuous ON" else "Continuous OFF",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "यह इंजन सीधे Android के ऑन-डिवाइस Speech-to-Text मॉडल और SecMentor ऑन-डिवाइस LLM रीज़निंग इंजन का उपयोग करता है। आप बिना वाईफ़ाई/डेटा के लगातार हैकिंग टूल्स, CVEs और n8n एजेंट्स के बारे में बोलकर सवाल पूछ सकते हैं।",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD6D3D1))
                )
            }
        }

        // Live Voice Visualizer Orb
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(
                    if (isListening) Brush.radialGradient(
                        colors = listOf(TerracottaPrimary, Color(0xFF262422))
                    ) else Brush.radialGradient(
                        colors = listOf(Color(0xFF383532), Color(0xFF1E1D1B))
                    )
                )
                .clickable {
                    if (isListening) {
                        voiceCommander.stopListening()
                    } else {
                        voiceCommander.setContinuousMode(true)
                        voiceCommander.startListening { query, _ ->
                            coroutineScope.launch {
                                val res = OnDeviceOfflineLlmEngine.generateResponse(query)
                                onAnswerGenerated(res.answer)
                                voiceCommander.speak(res.answer)
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                contentDescription = "Microphone",
                tint = Color.White,
                modifier = Modifier.size(42.dp)
            )
        }

        Text(
            text = if (isListening) "Listening... (RMS: ${(rmsVolume * 100).toInt()}%)" else "Tap Orb to Start Voice Chat",
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (isListening) CyberCyan else Color(0xFFA8A29E)
            )
        )

        if (liveTranscript.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E2836),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🗣️ \"$liveTranscript\"",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFFAF8F5),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        offlineAnswer?.let { answer ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1D1B)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(modifier = Modifier.padding(14.dp)) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💡 ऑन-डिवाइस ऑफ़लाइन उत्तर (Offline AI Answer)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = CyberEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            IconButton(
                                onClick = { voiceCommander.speak(answer) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Speak", tint = Color(0xFFA8A29E))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = answer,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
