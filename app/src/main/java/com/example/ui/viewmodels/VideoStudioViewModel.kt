package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.VideoProject
import com.example.data.models.VideoScene
import com.example.data.models.VideoStyle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoStudioViewModel : ViewModel() {

    private val sampleLongFormProjects = listOf(
        VideoProject(
            id = "vid-proj-45min",
            title = "Complete Open-Source Security Masterclass (45 Min)",
            targetDurationMinutes = 45,
            style = VideoStyle.CYBER_SECURITY,
            scriptSourceType = "Text & Audio Script",
            rawScript = "Introduction to Ethical Hacking and GitHub Security Tools. Chapter 1: Reconnaissance & Nmap Architecture. Chapter 2: Wireshark Packet Inspection & Forensics. Chapter 3: SQL Injection Vulnerability Exploitation & Parameterized Defense. Chapter 4: Building Custom Python Port Scanners in Linux Sandbox. Chapter 5: Automated Log Forensics with Fail2ban.",
            scenes = listOf(
                VideoScene(
                    sceneNumber = 1,
                    title = "01: Cyber Intelligence & Threat Landscape",
                    durationSeconds = 300, // 5 min
                    scriptNarration = "Welcome to the 45-minute comprehensive open-source security masterclass. Today we explore foundational defense mechanisms, open-source licensing, and sandboxing.",
                    visualPrompt = "Hyper-realistic moving clouds, soft volumetric fog, dreamy atmosphere, cinematic lighting, slow motion effect overlooking a glowing digital cyber metropolis.",
                    visualTheme = "Cinematic volumetric atmospheric lighting",
                    subtitleText = "Welcome to the 45-minute security masterclass."
                ),
                VideoScene(
                    sceneNumber = 2,
                    title = "02: Nmap Deep Architecture & SYN Scanning",
                    durationSeconds = 480, // 8 min
                    scriptNarration = "Examining TCP SYN 3-way handshake mechanics. Nmap sends SYN packets without completing the ACK handshake to identify listening daemons silently.",
                    visualPrompt = "3D animated packet flows across network switch blades, holographic IP packet headers, binary streams in emerald and cyan.",
                    visualTheme = "3D Animated Schematic Flow",
                    subtitleText = "Nmap sends SYN packets without completing the ACK handshake."
                ),
                VideoScene(
                    sceneNumber = 3,
                    title = "03: Wireshark & Packet Dissection in CLI",
                    durationSeconds = 420, // 7 min
                    scriptNarration = "Capturing PCAP streams using TShark. Inspecting TCP window sizes, banner exchanges, and spotting anomalous ARP poisoning attempts.",
                    visualPrompt = "Cinematic close-up of dark terminal console with glowing neon hexadecimal packet dumps scrolling smoothly.",
                    visualTheme = "Cyber Terminal HUD Telemetry",
                    subtitleText = "Capturing PCAP streams using TShark."
                ),
                VideoScene(
                    sceneNumber = 4,
                    title = "04: Web Defense: SQLi & Command Injection",
                    durationSeconds = 600, // 10 min
                    scriptNarration = "Analyzing untrusted user input paths. Demonstrating why string concatenation in database queries leads to complete authentication bypass, and implementing parameterized queries.",
                    visualPrompt = "Animated split-screen comparing vulnerable SQL query execution against prepared statement execution in real-time.",
                    visualTheme = "Animated 2D/3D Explainer",
                    subtitleText = "Demonstrating parameterized query remediation."
                ),
                VideoScene(
                    sceneNumber = 5,
                    title = "05: Custom Defensive Tool Development",
                    durationSeconds = 540, // 9 min
                    scriptNarration = "Writing a multi-threaded Python port scanner and auth.log brute force analyzer from scratch. Testing inside the isolated Linux sandbox.",
                    visualPrompt = "Live code compilation visualization, socket connection thread visualizer, green checkmarks on audited ports.",
                    visualTheme = "Animated Code Architecture",
                    subtitleText = "Writing multi-threaded Python port scanner from scratch."
                ),
                VideoScene(
                    sceneNumber = 6,
                    title = "06: Incident Response & Log Hardening",
                    durationSeconds = 360, // 6 min
                    scriptNarration = "Automating defensive containment using Fail2ban and iptables. Extracting top offending IP addresses from Linux server logs.",
                    visualPrompt = "Volumetric cinematic camera tracking through a digital firewall perimeter deflecting unauthorized brute force login bursts.",
                    visualTheme = "Cinematic Volumetric Lighting",
                    subtitleText = "Automating defensive containment using Fail2ban."
                )
            ),
            totalScenes = 6,
            resolution = "4K 60fps Ultra HD",
            isRendering = false,
            renderProgress = 1.0f
        ),
        VideoProject(
            id = "vid-proj-30min",
            title = "Autonomous AI Multimodal Architecture (30 Min)",
            targetDurationMinutes = 30,
            style = VideoStyle.CINEMATIC_AI,
            scriptSourceType = "Audio & Script",
            rawScript = "Deep exploration of Multimodal AI Agents with live camera perception, real-time voice synthesis, dynamic screen perception, and self-correcting coding sandboxes.",
            scenes = listOf(
                VideoScene(
                    sceneNumber = 1,
                    title = "01: The Multimodal Paradigm Shift",
                    durationSeconds = 360,
                    scriptNarration = "High resolution, hyper-realistic moving clouds, soft volumetric fog, dreamy atmosphere, cinematic lighting, slow motion effect illustrating emerging intelligence.",
                    visualPrompt = "Hyper-realistic moving clouds, soft volumetric fog, dreamy atmosphere, cinematic lighting, slow motion effect.",
                    visualTheme = "Hyper-realistic Volumetric Clouds",
                    subtitleText = "Emerging multimodal intelligence in real-time."
                ),
                VideoScene(
                    sceneNumber = 2,
                    title = "02: Real-Time Audio & Vision Grounding",
                    durationSeconds = 540,
                    scriptNarration = "Processing continuous 16kHz audio streams and low-latency camera frames to ground model reasoning in real physical environments.",
                    visualPrompt = "Dynamic glowing neural orb pulsating with human speech frequency waves and camera frame ingestion.",
                    visualTheme = "Neural Voice Wave Simulation",
                    subtitleText = "Processing audio streams and camera frames in real time."
                ),
                VideoScene(
                    sceneNumber = 3,
                    title = "03: Autonomous Code Execution Sandboxes",
                    durationSeconds = 480,
                    scriptNarration = "Evaluating how agents write, run, and self-correct code within sandboxed Linux runtimes without human intervention.",
                    visualPrompt = "3D holographic terminal running compiler cycles and auto-repairing broken syntax blocks.",
                    visualTheme = "3D Code Holograph",
                    subtitleText = "Self-correcting code in sandboxed runtimes."
                ),
                VideoScene(
                    sceneNumber = 4,
                    title = "04: Long-Form Synthesis & Video Production",
                    durationSeconds = 420,
                    scriptNarration = "Generating full 30 to 60 minute cohesive video narratives with consistent character keyframes and multi-track audio.",
                    visualPrompt = "Film reel timeline expanding across multiple tracks with cinematic color grading curves.",
                    visualTheme = "Cinematic Film Editing Suite",
                    subtitleText = "Producing full 30 to 60 minute cohesive video narratives."
                )
            ),
            totalScenes = 4,
            resolution = "1080p 60fps",
            isRendering = false,
            renderProgress = 1.0f
        )
    )

    private val _projects = MutableStateFlow<List<VideoProject>>(sampleLongFormProjects)
    val projects: StateFlow<List<VideoProject>> = _projects.asStateFlow()

    private val _currentProject = MutableStateFlow(sampleLongFormProjects[0])
    val currentProject: StateFlow<VideoProject> = _currentProject.asStateFlow()

    private val _selectedSceneIndex = MutableStateFlow(0)
    val selectedSceneIndex: StateFlow<Int> = _selectedSceneIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlaybackSecond = MutableStateFlow(0)
    val currentPlaybackSecond: StateFlow<Int> = _currentPlaybackSecond.asStateFlow()

    private val _isRendering = MutableStateFlow(false)
    val isRendering: StateFlow<Boolean> = _isRendering.asStateFlow()

    private val _renderProgress = MutableStateFlow(1.0f)
    val renderProgress: StateFlow<Float> = _renderProgress.asStateFlow()

    private var playbackJob: Job? = null

    fun selectProject(project: VideoProject) {
        _currentProject.value = project
        _selectedSceneIndex.value = 0
        _currentPlaybackSecond.value = 0
        pausePlayback()
    }

    fun selectScene(index: Int) {
        if (index in _currentProject.value.scenes.indices) {
            _selectedSceneIndex.value = index
            val scene = _currentProject.value.scenes[index]
            var accumulated = 0
            for (i in 0 until index) {
                accumulated += _currentProject.value.scenes[i].durationSeconds
            }
            _currentPlaybackSecond.value = accumulated
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val totalSec = _currentProject.value.scenes.sumOf { it.durationSeconds }
            while (_isPlaying.value && _currentPlaybackSecond.value < totalSec) {
                delay(1000)
                _currentPlaybackSecond.value += 1

                // Update selected scene based on time
                var timeAcc = 0
                _currentProject.value.scenes.forEachIndexed { idx, sc ->
                    timeAcc += sc.durationSeconds
                    if (_currentPlaybackSecond.value < timeAcc) {
                        _selectedSceneIndex.value = idx
                        return@forEachIndexed
                    }
                }
            }
            if (_currentPlaybackSecond.value >= totalSec) {
                _isPlaying.value = false
                _currentPlaybackSecond.value = 0
            }
        }
    }

    private fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun seekToSecond(second: Int) {
        _currentPlaybackSecond.value = second
    }

    fun updateSceneNarration(newText: String) {
        val proj = _currentProject.value
        val idx = _selectedSceneIndex.value
        if (idx in proj.scenes.indices) {
            val updatedScenes = proj.scenes.toMutableList()
            updatedScenes[idx] = updatedScenes[idx].copy(
                scriptNarration = newText,
                subtitleText = newText.take(60)
            )
            _currentProject.value = proj.copy(scenes = updatedScenes)
        }
    }

    fun updateSceneVisualPrompt(newPrompt: String) {
        val proj = _currentProject.value
        val idx = _selectedSceneIndex.value
        if (idx in proj.scenes.indices) {
            val updatedScenes = proj.scenes.toMutableList()
            updatedScenes[idx] = updatedScenes[idx].copy(visualPrompt = newPrompt)
            _currentProject.value = proj.copy(scenes = updatedScenes)
        }
    }

    fun trimSceneDuration(deltaSeconds: Int) {
        val proj = _currentProject.value
        val idx = _selectedSceneIndex.value
        if (idx in proj.scenes.indices) {
            val updatedScenes = proj.scenes.toMutableList()
            val newDur = (updatedScenes[idx].durationSeconds + deltaSeconds).coerceAtLeast(30)
            updatedScenes[idx] = updatedScenes[idx].copy(durationSeconds = newDur)
            val newTotalMin = updatedScenes.sumOf { it.durationSeconds } / 60
            _currentProject.value = proj.copy(
                scenes = updatedScenes,
                targetDurationMinutes = newTotalMin
            )
        }
    }

    fun setVideoStyle(style: VideoStyle) {
        _currentProject.value = _currentProject.value.copy(style = style)
    }

    fun generateNewLongFormVideo(
        title: String,
        targetDurationMinutes: Int,
        style: VideoStyle,
        rawScriptText: String
    ) {
        _isRendering.value = true
        _renderProgress.value = 0.05f

        viewModelScope.launch {
            // Split script into 30 to 60 min chapter scenes
            val sceneCount = (targetDurationMinutes / 5).coerceIn(6, 12)
            val secPerScene = (targetDurationMinutes * 60) / sceneCount

            val generatedScenes = (1..sceneCount).map { num ->
                VideoScene(
                    sceneNumber = num,
                    title = "Chapter $num: Concept & Analysis",
                    durationSeconds = secPerScene,
                    scriptNarration = "Chapter $num narrative audio script: Analyzing core structural logic, algorithmic flow, and real-time execution in style ${style.displayName}.",
                    visualPrompt = "High resolution, hyper-realistic moving clouds, soft volumetric fog, dreamy atmosphere, cinematic lighting, slow motion effect in scene $num.",
                    visualTheme = style.displayName,
                    subtitleText = "Chapter $num: Exploring deep architecture."
                )
            }

            // Simulate multi-pass render engine progression
            for (step in 1..10) {
                delay(300)
                _renderProgress.value = (step / 10f)
            }

            val newProject = VideoProject(
                title = title,
                targetDurationMinutes = targetDurationMinutes,
                style = style,
                rawScript = rawScriptText,
                scenes = generatedScenes,
                totalScenes = sceneCount,
                isRendering = false,
                renderProgress = 1.0f
            )

            _projects.value = listOf(newProject) + _projects.value
            _currentProject.value = newProject
            _selectedSceneIndex.value = 0
            _isRendering.value = false
        }
    }
}
