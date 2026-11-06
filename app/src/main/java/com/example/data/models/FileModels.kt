package com.example.data.models

enum class SupportedFileType(val extension: String, val displayName: String) {
    PDF("pdf", "PDF Document"),
    DOCX("docx", "Word Document"),
    TXT("txt", "Plain Text / Markdown"),
    CODE("py", "Source Code (Python/Bash/C++)")
}

data class ExtractedKeyword(
    val keyword: String,
    val count: Int,
    val category: String // "Security", "Entity", "Technology", "Protocol"
)

data class FileSummaryReport(
    val fileName: String,
    val fileType: SupportedFileType,
    val wordCount: Int,
    val executiveSummary: String,
    val keyFindings: List<String>,
    val keywords: List<ExtractedKeyword>,
    val rawContent: String,
    val editedContent: String
)
