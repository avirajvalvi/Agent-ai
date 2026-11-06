package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.agent.AutonomousTaskEngine
import com.example.data.local.AgentActionLogEntity
import com.example.data.local.AppDatabase
import com.example.data.models.AgenticWorkflow
import com.example.data.models.StepStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.data.repository.AgentActionLogRepository

data class AgenticNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val body: String,
    val timestamp: String = "Just now",
    val workflowId: String
)

class AgenticWorkflowViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val syncRepository = com.example.data.repository.FirestoreSyncRepository(application)
    private val repository = AgentActionLogRepository(db.agentActionLogDao(), syncRepository)

    private val taskEngine = AutonomousTaskEngine()

    private val initialWorkflow = taskEngine.createWorkflowForGoal(
        "Autonomously scan sandbox webapp cluster, patch vulnerabilities, and verify zero regressions in sandbox."
    )

    private val _workflows = MutableStateFlow<List<AgenticWorkflow>>(listOf(initialWorkflow))
    val workflows: StateFlow<List<AgenticWorkflow>> = _workflows.asStateFlow()

    private val _currentWorkflow = MutableStateFlow(initialWorkflow)
    val currentWorkflow: StateFlow<AgenticWorkflow> = _currentWorkflow.asStateFlow()

    private val _persistentLogs = MutableStateFlow<List<AgentActionLogEntity>>(emptyList())
    val persistentLogs: StateFlow<List<AgentActionLogEntity>> = _persistentLogs.asStateFlow()
    val actionLogs: StateFlow<List<AgentActionLogEntity>> = persistentLogs

    private val _notifications = MutableStateFlow<List<AgenticNotification>>(
        listOf(
            AgenticNotification(
                title = "SecMentor responded",
                body = "Report's done. Executive summary, full analysis, and a slide-ready version are in your Drive folder. 12 pages, 3 charts.",
                timestamp = "9:41 AM",
                workflowId = "initial"
            ),
            AgenticNotification(
                title = "SecMentor responded",
                body = "Finished scanning Q1 network audits and press releases for Notion, Linear, and Asana. A few interesting signals in their security shifts.",
                timestamp = "9:02 AM",
                workflowId = "initial"
            ),
            AgenticNotification(
                title = "SecMentor responded",
                body = "On it — pulling together competitive landscape for Thursday's meeting; Starting with pricing, positioning, and recent product moves.",
                timestamp = "Yesterday",
                workflowId = "initial"
            )
        )
    )
    val notifications: StateFlow<List<AgenticNotification>> = _notifications.asStateFlow()

    private var executionJob: Job? = null

    init {
        observePersistentLogs()
        startAutonomousExecution()
    }

    private fun observePersistentLogs() {
        viewModelScope.launch {
            db.agentActionLogDao().getAllActionLogs().collect { entities ->
                if (entities.isEmpty()) {
                    // Seed initial completed historical action logs for instant review
                    seedInitialLogs()
                } else {
                    _persistentLogs.value = entities
                }
            }
        }
    }

    private suspend fun seedInitialLogs() {
        val initialLogs = listOf(
            AgentActionLogEntity(
                workflowId = "hist-01",
                workflowTitle = "Autonomous Security Audit & Self-Healing Pipeline",
                stepNumber = 1,
                actionTitle = "Autonomous Subnet Discovery & Reconnaissance",
                actionDescription = "Scanned subnet 192.168.1.0/24 with TCP SYN stealth parameters. Identified 4 active hosts.",
                agentRole = "Reconnaissance Agent",
                toolCommand = "nmap -sS -sV -T4 192.168.1.100",
                status = "COMPLETED",
                timestamp = System.currentTimeMillis() - 3600000L,
                durationMs = 840L,
                stdoutLogs = "Host 192.168.1.100 up (0.0004s). Ports: 22/tcp (SSH), 80/tcp (HTTP), 443/tcp (HTTPS), 8080/tcp (Proxy).",
                outputArtifact = "nmap_audit_192.168.1.100.txt"
            ),
            AgentActionLogEntity(
                workflowId = "hist-01",
                workflowTitle = "Autonomous Security Audit & Self-Healing Pipeline",
                stepNumber = 2,
                actionTitle = "Static Code & AST Vulnerability Inspection",
                actionDescription = "Inspected /home/secbox/labs source tree. Flagged SQL concatenation CWE-89 on line 12 of webapp.py.",
                agentRole = "Autonomous Code Auditor",
                toolCommand = "ast_analyzer.py --scan-owasp /home/secbox/labs",
                status = "COMPLETED",
                timestamp = System.currentTimeMillis() - 3400000L,
                durationMs = 1120L,
                stdoutLogs = "CWE-89 SQL Injection detected in query_user(). Query formatted directly from request.args.get('username').",
                outputArtifact = "vulnerability_report.json"
            ),
            AgentActionLogEntity(
                workflowId = "hist-01",
                workflowTitle = "Autonomous Security Audit & Self-Healing Pipeline",
                stepNumber = 3,
                actionTitle = "Autonomous Patch Synthesis & Self-Healing",
                actionDescription = "Generated parameterized query cursor.execute(query, (username,)). Overwrote vulnerable line and regenerated AST.",
                agentRole = "Self-Healing Patch Engineer",
                toolCommand = "remediation_engine --auto-patch --target app.py",
                status = "AUTO_CORRECTED",
                timestamp = System.currentTimeMillis() - 3100000L,
                durationMs = 1450L,
                stdoutLogs = "[AUTONOMOUS HEAL] Vulnerability eliminated. Replaced with parameterized cursor placeholder. Regression test: PASS.",
                outputArtifact = "webapp_patched.py"
            ),
            AgentActionLogEntity(
                workflowId = "hist-02",
                workflowTitle = "Autonomous App Generation & APK Packaging",
                stepNumber = 4,
                actionTitle = "Release Packaging (APK & Source ZIP)",
                actionDescription = "Executed Gradle build pipeline, signed debug and release APKs, bundled full zip source.",
                agentRole = "Gradle & APK Packager",
                toolCommand = "gradle assembleRelease && zip -r project.zip",
                status = "COMPLETED",
                timestamp = System.currentTimeMillis() - 1800000L,
                durationMs = 2800L,
                stdoutLogs = "BUILD SUCCESSFUL in 14s. 38 actionable tasks: 38 executed. Output: app-release.apk (18.4 MB).",
                outputArtifact = "app-release.apk"
            )
        )

        initialLogs.forEach { db.agentActionLogDao().insertActionLog(it) }
    }

    fun startAutonomousExecution() {
        val workflow = _currentWorkflow.value
        if (workflow.isRunning) return

        executionJob?.cancel()
        executionJob = viewModelScope.launch {
            var updatedWorkflow = workflow.copy(
                isRunning = true,
                liveTerminalLogs = workflow.liveTerminalLogs + "[ENGINE] Initiating autonomous task execution pipeline..."
            )
            _currentWorkflow.value = updatedWorkflow

            val steps = updatedWorkflow.steps.toMutableList()

            for (i in steps.indices) {
                // Mark step RUNNING
                steps[i] = steps[i].copy(status = StepStatus.RUNNING)
                val logEntry = "[${steps[i].agentRole.displayName}] Executing '${steps[i].toolUsed}'..."
                updatedWorkflow = updatedWorkflow.copy(
                    steps = steps.toList(),
                    currentStepIndex = i,
                    progress = (i.toFloat() / steps.size),
                    liveTerminalLogs = updatedWorkflow.liveTerminalLogs + logEntry
                )
                _currentWorkflow.value = updatedWorkflow
                delay(1200)

                // Simulate Autonomous Self-Correction on Step 3
                var wasSelfHealed = false
                if (i == 2) {
                    wasSelfHealed = true
                    steps[i] = steps[i].copy(
                        status = StepStatus.AUTO_CORRECTING,
                        autoCorrectionAttempts = 1
                    )
                    val selfCorrectionLog = "[SELF-CORRECTION] Detected unquoted shell variable in target script. Autonomously applying parameterization patch..."
                    updatedWorkflow = updatedWorkflow.copy(
                        steps = steps.toList(),
                        liveTerminalLogs = updatedWorkflow.liveTerminalLogs + selfCorrectionLog
                    )
                    _currentWorkflow.value = updatedWorkflow
                    delay(1400)
                }

                // Complete Step
                steps[i] = steps[i].copy(
                    status = StepStatus.COMPLETED,
                    logs = "Execution succeeded. Tool output validated against security constraints.",
                    outputArtifact = "artifact_step_${i + 1}.json"
                )
                val compLog = "[COMPLETED] Step ${i + 1}: ${steps[i].title} finished with exit code 0."
                updatedWorkflow = updatedWorkflow.copy(
                    steps = steps.toList(),
                    progress = ((i + 1).toFloat() / steps.size),
                    liveTerminalLogs = updatedWorkflow.liveTerminalLogs + compLog
                )
                _currentWorkflow.value = updatedWorkflow

                // Persist this completed autonomous action into Room Database
                val persistentLog = AgentActionLogEntity(
                    workflowId = updatedWorkflow.id,
                    workflowTitle = updatedWorkflow.title,
                    stepNumber = i + 1,
                    actionTitle = steps[i].title,
                    actionDescription = steps[i].description,
                    agentRole = steps[i].agentRole.displayName,
                    toolCommand = steps[i].toolUsed,
                    status = if (wasSelfHealed) "AUTO_CORRECTED" else "COMPLETED",
                    timestamp = System.currentTimeMillis(),
                    durationMs = if (wasSelfHealed) 2600L else 1200L,
                    stdoutLogs = compLog,
                    outputArtifact = "artifact_step_${i + 1}.json"
                )
                db.agentActionLogDao().insertActionLog(persistentLog)

                delay(600)
            }

            // Pipeline finished completely without manual intervention!
            val finalLog = "[PIPELINE FINISHED] All ${steps.size} steps autonomously completed and verified."
            val completedWorkflow = updatedWorkflow.copy(
                isRunning = false,
                progress = 1.0f,
                completedAt = System.currentTimeMillis(),
                liveTerminalLogs = updatedWorkflow.liveTerminalLogs + finalLog
            )
            _currentWorkflow.value = completedWorkflow

            // Dispatch notification (matching screenshot 5)
            val newNotif = AgenticNotification(
                title = "SecMentor responded",
                body = completedWorkflow.notificationBody ?: "Autonomous workflow '${completedWorkflow.title}' successfully completed all steps.",
                timestamp = "Just now",
                workflowId = completedWorkflow.id
            )
            _notifications.value = listOf(newNotif) + _notifications.value

            // Update in workflows list
            _workflows.value = _workflows.value.map {
                if (it.id == completedWorkflow.id) completedWorkflow else it
            }
        }
    }

    fun submitNewGoal(goal: String) {
        if (goal.isBlank()) return
        val newWorkflow = taskEngine.createWorkflowForGoal(goal)
        _workflows.value = listOf(newWorkflow) + _workflows.value
        _currentWorkflow.value = newWorkflow
        startAutonomousExecution()
    }

    fun clearActionLogs() {
        viewModelScope.launch {
            db.agentActionLogDao().clearActionLogs()
            _persistentLogs.value = emptyList()
        }
    }

    fun selectWorkflow(wf: AgenticWorkflow) {
        _currentWorkflow.value = wf
    }
}
