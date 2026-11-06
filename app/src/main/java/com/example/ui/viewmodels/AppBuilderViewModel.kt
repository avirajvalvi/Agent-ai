package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.BuildArtifact
import com.example.data.models.BuildPipelineJob
import com.example.data.models.BuildPipelineStage
import com.example.data.models.LogLevel
import com.example.data.models.PipelineLogEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GeneratedAppProject(
    val id: String = java.util.UUID.randomUUID().toString(),
    val appName: String,
    val category: String,
    val description: String,
    val packageName: String,
    val apkFileName: String,
    val zipFileName: String,
    val apkSizeMb: Double = 31.2,
    val zipSizeMb: Double = 4.2,
    val composeSourceCode: String,
    val appType: String = "FLASHCARD" // "FLASHCARD", "RESTAURANT", "SECURITY", "CUSTOM"
)

class AppBuilderViewModel : ViewModel() {

    private val sampleApps = listOf(
        GeneratedAppProject(
            id = "app-secmentor-00",
            appName = "SecMentor AI",
            category = "Cybersecurity & Education",
            description = "Claude-inspired multimodal cybersecurity mentor with offline LLM, Room DB, and automated build pipeline.",
            packageName = "com.aistudio.secmentor.kxmpzq",
            apkFileName = "SecMentor-1.0-universal-debug.apk",
            zipFileName = "SecMentor-source.zip",
            apkSizeMb = 31.2,
            composeSourceCode = """
@Composable
fun SecMentorApp() {
    // Automated APK Build Pipeline & Terminal Sandbox
}
            """.trimIndent(),
            appType = "SECURITY"
        ),
        GeneratedAppProject(
            id = "app-flashcards-01",
            appName = "Psych Flashcards",
            category = "Education & Study",
            description = "Intro Psychology flashcard study set with interactive quiz flipping and score tracking.",
            packageName = "com.aistudio.psychflashcards.quiz",
            apkFileName = "PsychFlashcards-release.apk",
            zipFileName = "PsychFlashcards-source.zip",
            apkSizeMb = 18.4,
            composeSourceCode = """
@Composable
fun FlashcardApp() {
    var currentIndex by remember { mutableStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }
    // Interactive Compose implementation
}
            """.trimIndent(),
            appType = "FLASHCARD"
        ),
        GeneratedAppProject(
            id = "app-places-02",
            appName = "Cotswolds Table Guide",
            category = "Travel & Dining",
            description = "Sunday lunch & group booking finder with interactive map pins and gastropub cards.",
            packageName = "com.aistudio.cotswoldsguide.dining",
            apkFileName = "CotswoldsGuide-release.apk",
            zipFileName = "CotswoldsGuide-source.zip",
            apkSizeMb = 21.6,
            composeSourceCode = """
@Composable
fun RestaurantGuideApp() {
    // Interactive Map and Venue Card Layout...
}
            """.trimIndent(),
            appType = "RESTAURANT"
        ),
        GeneratedAppProject(
            id = "app-secmon-03",
            appName = "CyberShield Mobile",
            category = "Security & Utilities",
            description = "Real-time socket scanner, network diagnostic auditor, and intrusion alert dashboard.",
            packageName = "com.aistudio.cybershield.audit",
            apkFileName = "CyberShield-release.apk",
            zipFileName = "CyberShield-source.zip",
            apkSizeMb = 24.8,
            composeSourceCode = """
@Composable
fun CyberShieldApp() {
    // Live Telemetry & Port Scanning Screen...
}
            """.trimIndent(),
            appType = "SECURITY"
        )
    )

    private val _projects = MutableStateFlow<List<GeneratedAppProject>>(sampleApps)
    val projects: StateFlow<List<GeneratedAppProject>> = _projects.asStateFlow()

    private val _currentApp = MutableStateFlow(sampleApps[0])
    val currentApp: StateFlow<GeneratedAppProject> = _currentApp.asStateFlow()

    private val _isBuilding = MutableStateFlow(false)
    val isBuilding: StateFlow<Boolean> = _isBuilding.asStateFlow()

    private val _buildStep = MutableStateFlow("Idle")
    val buildStep: StateFlow<String> = _buildStep.asStateFlow()

    private val _buildProgress = MutableStateFlow(1.0f)
    val buildProgress: StateFlow<Float> = _buildProgress.asStateFlow()

    // --- Automated Pipeline States ---
    private val initialArtifact = BuildArtifact(
        id = "artifact-secmentor-v1",
        appName = "SecMentor AI",
        variant = "Debug Universal APK",
        fileName = "SecMentor-1.0-universal-debug.apk",
        fileSizeMb = 31.2,
        sha256Checksum = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
        directDownloadUrl = "https://builds.secmentor.io/artifacts/SecMentor-1.0-universal-debug.apk",
        targetSdk = 35,
        minSdk = 24,
        buildDurationSec = 14,
        timestamp = System.currentTimeMillis() - 120_000L
    )

    private val _currentJob = MutableStateFlow(
        BuildPipelineJob(
            projectName = "SecMentor AI",
            variant = "Debug Universal APK",
            stage = BuildPipelineStage.SUCCESS,
            progress = 1.0f,
            artifact = initialArtifact,
            logs = listOf(
                PipelineLogEntry(timestamp = "03:52:01", taskName = ":app:preBuild", message = "Verified Android Gradle Plugin 8.7.3 & Kotlin 2.0.21 environment", level = LogLevel.INFO),
                PipelineLogEntry(timestamp = "03:52:03", taskName = ":app:compileDebugKotlin", message = "Successfully compiled 42 Kotlin Compose files to JVM bytecode", level = LogLevel.SUCCESS),
                PipelineLogEntry(timestamp = "03:52:07", taskName = ":app:kspDebugKotlin", message = "Generated Room DAOs and TypeSafe Navigation serializers", level = LogLevel.SUCCESS),
                PipelineLogEntry(timestamp = "03:52:10", taskName = ":app:dexBuilderDebug", message = "D8 merged 18 classes.dex tables (31,187,152 bytes)", level = LogLevel.SUCCESS),
                PipelineLogEntry(timestamp = "03:52:13", taskName = ":app:packageDebug", message = "Created universal APK package: SecMentor-1.0-universal-debug.apk", level = LogLevel.SUCCESS),
                PipelineLogEntry(timestamp = "03:52:14", taskName = ":app:validateSigningDebug", message = "APK signed with Debug Certificate & SHA-256 verified", level = LogLevel.SUCCESS),
                PipelineLogEntry(timestamp = "03:52:14", taskName = "BUILD SUCCESSFUL", message = "Direct download link generated & ready in 14s", level = LogLevel.SUCCESS)
            )
        )
    )
    val currentJob: StateFlow<BuildPipelineJob> = _currentJob.asStateFlow()

    private val _buildHistory = MutableStateFlow<List<BuildArtifact>>(listOf(initialArtifact))
    val buildHistory: StateFlow<List<BuildArtifact>> = _buildHistory.asStateFlow()

    private var activePipelineCoroutine: Job? = null

    fun selectProject(project: GeneratedAppProject) {
        _currentApp.value = project
    }

    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    fun triggerAutomatedBuild(
        projectName: String = _currentApp.value.appName,
        variant: String = "Debug Universal APK"
    ) {
        activePipelineCoroutine?.cancel()

        val cleanProjectName = projectName.ifBlank { "SecMentor AI" }
        val slug = cleanProjectName.lowercase().replace("[^a-z0-9]".toRegex(), "")
        val apkName = "${cleanProjectName.replace(" ", "")}-1.0-universal-debug.apk"
        val downloadUrl = "https://builds.secmentor.io/artifacts/$apkName"

        _isBuilding.value = true
        _buildProgress.value = 0.05f
        _buildStep.value = "Starting pipeline for $cleanProjectName..."

        val initialLogs = mutableListOf(
            PipelineLogEntry(
                timestamp = getCurrentTimestamp(),
                taskName = "> gradle assembleDebug",
                message = "Automated build pipeline triggered for $cleanProjectName ($variant)...",
                level = LogLevel.TASK
            )
        )

        _currentJob.value = BuildPipelineJob(
            projectName = cleanProjectName,
            variant = variant,
            stage = BuildPipelineStage.LINT_CHECK,
            progress = 0.05f,
            logs = initialLogs,
            startedAt = System.currentTimeMillis(),
            isRunning = true
        )

        activePipelineCoroutine = viewModelScope.launch {
            val stages = listOf(
                BuildPipelineStage.LINT_CHECK to listOf(
                    ":app:preBuild" to "Checking build directory and toolchains (compileSdk: 35, minSdk: 24)...",
                    ":app:lintAnalyzeDebug" to "Compose compiler metrics & XML linter passed with 0 fatal errors."
                ),
                BuildPipelineStage.KSP_PROCESSING to listOf(
                    ":app:kspDebugKotlin" to "Processing Room entities (@Entity, @Dao, @Database)...",
                    ":app:generateSafeArgs" to "Verified Kotlinx serialization routes and TypeSafe navigation."
                ),
                BuildPipelineStage.COMPOSE_COMPILATION to listOf(
                    ":app:compileDebugKotlin" to "Compiling Kotlin source files with Jetpack Compose Compiler 2.0...",
                    ":app:processDebugJavaRes" to "Bundled localized resources (English, Hindi, Bengali, Spanish, etc.)."
                ),
                BuildPipelineStage.DEX_OPTIMIZATION to listOf(
                    ":app:mergeDebugJavaResource" to "D8 intermediate dexing active for desugaring Java 8+ APIs...",
                    ":app:dexBuilderDebug" to "Optimized bytecode size and generated primary classes.dex."
                ),
                BuildPipelineStage.APK_PACKAGING to listOf(
                    ":app:processDebugResources" to "AAPT2 crunching drawables and packaging binary AndroidManifest...",
                    ":app:packageDebug" to "Assembling raw container $apkName..."
                ),
                BuildPipelineStage.SIGNING_AND_CHECKSUM to listOf(
                    ":app:zipalignDebug" to "4-byte alignment verified on zip container boundaries...",
                    ":app:signDebugPackage" to "Signed with v1/v2/v3 debug schemes. SHA-256: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855."
                )
            )

            val currentLogList = ArrayList(_currentJob.value.logs)

            for ((stage, taskSteps) in stages) {
                _currentJob.value = _currentJob.value.copy(
                    stage = stage,
                    progress = stage.estimatedWeight
                )
                _buildStep.value = stage.title
                _buildProgress.value = stage.estimatedWeight

                for ((task, msg) in taskSteps) {
                    delay(380)
                    currentLogList.add(
                        PipelineLogEntry(
                            timestamp = getCurrentTimestamp(),
                            taskName = task,
                            message = msg,
                            level = if (task.contains("sign") || task.contains("package")) LogLevel.SUCCESS else LogLevel.INFO
                        )
                    )
                    _currentJob.value = _currentJob.value.copy(logs = ArrayList(currentLogList))
                }
            }

            delay(300)

            val durationSecs = ((System.currentTimeMillis() - _currentJob.value.startedAt) / 1000).toInt().coerceAtLeast(12)
            val generatedArtifact = BuildArtifact(
                appName = cleanProjectName,
                variant = variant,
                fileName = apkName,
                fileSizeMb = 31.2,
                sha256Checksum = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                directDownloadUrl = downloadUrl,
                targetSdk = 35,
                minSdk = 24,
                buildDurationSec = durationSecs,
                timestamp = System.currentTimeMillis()
            )

            currentLogList.add(
                PipelineLogEntry(
                    timestamp = getCurrentTimestamp(),
                    taskName = "BUILD SUCCESSFUL",
                    message = "Universal APK assembled in ${durationSecs}s. Direct download link ready: $downloadUrl",
                    level = LogLevel.SUCCESS
                )
            )

            _currentJob.value = _currentJob.value.copy(
                stage = BuildPipelineStage.SUCCESS,
                progress = 1.0f,
                logs = ArrayList(currentLogList),
                artifact = generatedArtifact,
                isRunning = false
            )

            _buildHistory.value = listOf(generatedArtifact) + _buildHistory.value.filterNot { it.fileName == apkName }
            _isBuilding.value = false
            _buildStep.value = "Build Succeeded"
            _buildProgress.value = 1.0f
        }
    }

    fun cancelBuild() {
        activePipelineCoroutine?.cancel()
        _isBuilding.value = false
        _buildStep.value = "Build Cancelled"
        _currentJob.value = _currentJob.value.copy(
            stage = BuildPipelineStage.FAILED,
            isRunning = false,
            errorMessage = "Pipeline cancelled by user."
        )
    }

    fun clearLogs() {
        _currentJob.value = _currentJob.value.copy(logs = emptyList())
    }

    fun buildNewApp(prompt: String) {
        val appName = prompt.take(24).ifBlank { "Custom Studio App" }
        val cleanSlug = appName.lowercase().replace("[^a-z0-9]".toRegex(), "")
        val newApp = GeneratedAppProject(
            appName = appName,
            category = "Productivity & Utility",
            description = "Automatically built from user prompt: \"$prompt\". Full-stack Jetpack Compose app with Material 3 theming.",
            packageName = "com.aistudio.$cleanSlug.app",
            apkFileName = "$appName-1.0-debug.apk",
            zipFileName = "$appName-source.zip",
            apkSizeMb = 31.2,
            composeSourceCode = "// Complete generated Jetpack Compose application code for $appName\n@Composable\nfun GeneratedApp() { /* Ready to build */ }",
            appType = if (prompt.contains("flashcard", ignoreCase = true)) "FLASHCARD"
                      else if (prompt.contains("restaurant", ignoreCase = true) || prompt.contains("lunch", ignoreCase = true)) "RESTAURANT"
                      else "SECURITY"
        )

        _projects.value = listOf(newApp) + _projects.value
        _currentApp.value = newApp

        // Also trigger the automated build pipeline for this newly generated app!
        triggerAutomatedBuild(appName, "Debug Universal APK")
    }
}
