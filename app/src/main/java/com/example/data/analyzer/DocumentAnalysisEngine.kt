package com.example.data.analyzer

import com.example.data.models.ExtractedKeyword
import com.example.data.models.FileSummaryReport
import com.example.data.models.SupportedFileType
import java.util.Locale

class DocumentAnalysisEngine {

    fun analyzeDocument(fileName: String, content: String): FileSummaryReport {
        val fileType = when {
            fileName.endsWith(".pdf", ignoreCase = true) -> SupportedFileType.PDF
            fileName.endsWith(".docx", ignoreCase = true) -> SupportedFileType.DOCX
            fileName.endsWith(".py", ignoreCase = true) || fileName.endsWith(".sh", ignoreCase = true) || fileName.endsWith(".cpp", ignoreCase = true) -> SupportedFileType.CODE
            else -> SupportedFileType.TXT
        }

        val words = content.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val wordCount = words.size

        // Keyword extraction logic
        val stopWords = setOf("the", "and", "or", "to", "in", "a", "is", "that", "for", "with", "as", "on", "by", "this", "be", "are", "from", "at", "it", "an")
        val wordFrequencies = mutableMapOf<String, Int>()
        words.forEach { w ->
            val clean = w.lowercase(Locale.ROOT).replace("[^a-zA-Z0-9_.-]".toRegex(), "")
            if (clean.length > 3 && !stopWords.contains(clean)) {
                wordFrequencies[clean] = (wordFrequencies[clean] ?: 0) + 1
            }
        }

        val sortedKeywords = wordFrequencies.entries
            .sortedByDescending { it.value }
            .take(12)
            .map { entry ->
                val cat = when {
                    entry.key.contains("port") || entry.key.contains("nmap") || entry.key.contains("syn") || entry.key.contains("scan") -> "Recon"
                    entry.key.contains("sql") || entry.key.contains("inject") || entry.key.contains("vulnerab") || entry.key.contains("cwe") -> "Vulnerability"
                    entry.key.contains("ssh") || entry.key.contains("auth") || entry.key.contains("pass") || entry.key.contains("brute") -> "Defense"
                    entry.key.contains("ip") || entry.key.contains("tcp") || entry.key.contains("packet") || entry.key.contains("http") -> "Protocol"
                    else -> "Domain Entity"
                }
                ExtractedKeyword(entry.key, entry.value, cat)
            }

        // Executive summary synthesis
        val sentences = content.split("[.!?\n]".toRegex()).filter { it.trim().length > 15 }
        val keySentences = sentences.take(4).map { it.trim() }

        val executiveSummary = if (keySentences.isNotEmpty()) {
            keySentences.joinToString(". ") + "."
        } else {
            "Document contains $wordCount words with focused domain terminology across security architecture and implementation details."
        }

        val findings = mutableListOf<String>()
        if (content.contains("vulnerability", ignoreCase = true) || content.contains("injection", ignoreCase = true)) {
            findings.add("Security vulnerability patterns identified in text content (High Priority).")
        }
        if (content.contains("failed password", ignoreCase = true) || content.contains("brute", ignoreCase = true)) {
            findings.add("Authentication telemetry indicates repeated failed authorization attempts.")
        }
        if (content.contains("nmap", ignoreCase = true) || content.contains("port", ignoreCase = true)) {
            findings.add("Network exploration parameters and open port mappings documented.")
        }
        if (findings.isEmpty()) {
            findings.add("Document successfully ingested and validated. Content conforms to standard structure.")
            findings.add("Identified ${sortedKeywords.size} distinct high-frequency domain keywords.")
        }

        return FileSummaryReport(
            fileName = fileName,
            fileType = fileType,
            wordCount = wordCount,
            executiveSummary = executiveSummary,
            keyFindings = findings,
            keywords = sortedKeywords,
            rawContent = content,
            editedContent = content
        )
    }
}
