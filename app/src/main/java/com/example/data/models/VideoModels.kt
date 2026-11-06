package com.example.data.models

enum class VideoStyle(val displayName: String, val description: String) {
    ANIMATED_EXPLAINER("Animated 2D/3D", "Stylized illustrated motion graphics with fluid character and diagram transitions."),
    CINEMATIC_AI("Cinematic Hyper-Realistic", "Photorealistic cinematic lighting, volumetric fog, and slow-motion camera moves."),
    CYBER_SECURITY("Cyber Tech & Terminal", "Futuristic HUD telemetry, live code flows, terminal typography, and digital network grids."),
    DOCUMENTARY_LONGFORM("Long-form Documentary", "Editorial documentary pacing, archival visuals, deep-dive chapter narration (30-60 min).")
}

data class VideoScene(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sceneNumber: Int,
    val title: String,
    val durationSeconds: Int = 180, // Default 3 min per scene for long-form
    val scriptNarration: String,
    val visualPrompt: String,
    val visualTheme: String = "Cinematic volumetric atmospheric lighting",
    val status: String = "Ready",
    val subtitleText: String = ""
)

data class VideoProject(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val targetDurationMinutes: Int = 30, // 30 min to 60 min long-form
    val style: VideoStyle = VideoStyle.CYBER_SECURITY,
    val scriptSourceType: String = "Text & Audio Script",
    val rawScript: String = "",
    val scenes: List<VideoScene> = emptyList(),
    val totalScenes: Int = 10,
    val resolution: String = "1080p 60fps",
    val isRendering: Boolean = false,
    val renderProgress: Float = 1.0f,
    val voiceoverVoice: String = "Nova Deep (Bilingual Hindi/English)",
    val backgroundMusicTrack: String = "Ambient Cyber Synth (Defensive Echoes)"
)
