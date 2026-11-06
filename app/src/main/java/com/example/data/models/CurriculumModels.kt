package com.example.data.models

/**
 * Data structures representing the 60-90 Day Structured Ethical Hacking Curriculum,
 * sequential module locking, and real-time learner progress tracking.
 */

enum class ModuleLockStatus {
    LOCKED,
    UNLOCKED,
    IN_PROGRESS,
    COMPLETED
}

data class HandsOnLabCommand(
    val command: String,
    val expectedOutputHint: String,
    val targetEnvironment: String = "Linux Terminal / Sandbox",
    val defensiveMitigation: String
)

data class LabQuizQuestion(
    val question: String,
    val questionHindi: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class CurriculumModule(
    val id: String,
    val dayRange: String,
    val orderIndex: Int,
    val title: String,
    val titleHindi: String,
    val summary: String,
    val learningObjectives: List<String>,
    val handsOnLab: HandsOnLabCommand,
    val quizQuestion: LabQuizQuestion? = null,
    val xpReward: Int = 100,
    val estimatedHours: Int = 3,
    val prerequisiteModuleId: String? = null,
    val status: ModuleLockStatus = ModuleLockStatus.LOCKED
)

data class CurriculumPhase(
    val id: String,
    val phaseNumber: Int,
    val title: String,
    val titleHindi: String,
    val durationDays: String,
    val description: String,
    val modules: List<CurriculumModule>,
    val prerequisitePhaseId: String? = null
)

data class CurriculumProgress(
    val completedModuleIds: Set<String> = emptySet(),
    val totalXp: Int = 0,
    val streakDays: Int = 1,
    val isInstructorBypassEnabled: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
