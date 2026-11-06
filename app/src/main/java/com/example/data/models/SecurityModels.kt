package com.example.data.models

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val attachedCode: String? = null,
    val codeLanguage: String? = null,
    val attachedFileName: String? = null,
    val securitySeverity: SecuritySeverity? = null,
    val tags: List<String> = emptyList()
)

enum class SecuritySeverity {
    INFO, LOW, MEDIUM, HIGH, CRITICAL
}

data class SecurityIssue(
    val title: String,
    val severity: SecuritySeverity,
    val lineNumber: Int,
    val description: String,
    val remediation: String
)

data class CodeAnalysisReport(
    val language: String,
    val isSyntaxValid: Boolean,
    val summary: String,
    val issues: List<SecurityIssue>,
    val patchedCode: String
)

data class SecurityProject(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val category: String,
    val lastUpdated: Long = System.currentTimeMillis(),
    val scriptCount: Int = 1,
    val sampleSnippet: String = ""
)

data class CustomSecurityTool(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val language: String,
    val description: String,
    val code: String,
    val usageExample: String
)

data class TerminalEntry(
    val command: String,
    val output: String,
    val exitCode: Int = 0,
    val isError: Boolean = false,
    val directory: String = "~"
)
