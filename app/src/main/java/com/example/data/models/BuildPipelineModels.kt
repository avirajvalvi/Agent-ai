package com.example.data.models

import java.util.UUID

enum class BuildPipelineStage(
    val title: String,
    val description: String,
    val estimatedWeight: Float
) {
    IDLE("Idle", "Pipeline standby, ready to compile", 0.0f),
    LINT_CHECK("1. Lint & Static Analysis", "Analyzing Compose semantics and Android Lint checks", 0.15f),
    KSP_PROCESSING("2. KSP Symbol Processing", "Generating Room DB schemas and type-safe navigators", 0.32f),
    COMPOSE_COMPILATION("3. Compose Bytecode Compilation", "Compiling Kotlin AST into Android DEX bytecode with optimizations", 0.55f),
    DEX_OPTIMIZATION("4. D8 / R8 Code Shrinking", "Shrinking unused classes and creating classes.dex tables", 0.72f),
    APK_PACKAGING("5. AAPT2 Resource Packaging", "Assembling resources, binary XML, and compiled binaries into APK container", 0.88f),
    SIGNING_AND_CHECKSUM("6. Signing & Checksum Generation", "V2/V3 APK signing scheme and computing SHA-256 fingerprint", 0.98f),
    SUCCESS("Build Succeeded", "APK compiled successfully and ready for direct download", 1.0f),
    FAILED("Build Failed", "Pipeline encountered an error during compilation", 1.0f)
}

enum class LogLevel {
    INFO,
    TASK,
    SUCCESS,
    WARNING,
    ERROR
}

data class PipelineLogEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String,
    val taskName: String,
    val message: String,
    val level: LogLevel = LogLevel.INFO
)

data class BuildArtifact(
    val id: String = UUID.randomUUID().toString(),
    val appName: String,
    val variant: String, // "Debug Universal", "Release Signed"
    val fileName: String,
    val fileSizeMb: Double,
    val sha256Checksum: String,
    val directDownloadUrl: String,
    val targetSdk: Int = 35,
    val minSdk: Int = 24,
    val buildDurationSec: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class BuildPipelineJob(
    val id: String = UUID.randomUUID().toString(),
    val projectName: String,
    val variant: String = "Debug Universal APK",
    val stage: BuildPipelineStage = BuildPipelineStage.IDLE,
    val progress: Float = 0.0f,
    val logs: List<PipelineLogEntry> = emptyList(),
    val artifact: BuildArtifact? = null,
    val startedAt: Long = 0L,
    val isRunning: Boolean = false,
    val errorMessage: String? = null
)
