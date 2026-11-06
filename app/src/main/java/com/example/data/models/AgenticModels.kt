package com.example.data.models

enum class StepStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    AUTO_CORRECTING,
    FAILED
}

enum class AgentRole(val displayName: String, val badgeColorHex: Long) {
    CHIEF_PLANNER("Lead Planning Agent", 0xFF60A5FA),
    RECON_SPECIALIST("Reconnaissance Agent", 0xFF34D399),
    CODE_AUDITOR("Autonomous Code Auditor", 0xFFF59E0B),
    PATCH_ENGINEER("Self-Healing Patch Engineer", 0xFFD97757),
    BUILD_PACKAGER("Gradle & APK Packager", 0xFFA78BFA),
    VERIFICATION_AGENT("Independent Verifier", 0xFF10B981)
}

data class WorkflowStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val agentRole: AgentRole,
    val toolUsed: String,
    val status: StepStatus = StepStatus.PENDING,
    val logs: String = "",
    val outputArtifact: String? = null,
    val autoCorrectionAttempts: Int = 0
)

data class AgenticWorkflow(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val primaryGoal: String,
    val isRunning: Boolean = false,
    val progress: Float = 0f,
    val steps: List<WorkflowStep>,
    val currentStepIndex: Int = 0,
    val liveTerminalLogs: List<String> = emptyList(),
    val completedAt: Long? = null,
    val notificationTitle: String? = null,
    val notificationBody: String? = null
)
