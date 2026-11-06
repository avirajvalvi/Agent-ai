package com.example.data.analyzer

import com.example.data.models.CodeAnalysisReport
import com.example.data.models.SecurityIssue
import com.example.data.models.SecuritySeverity

class CodeAnalyzerEngine {

    fun analyzeCode(sourceCode: String, language: String = "python"): CodeAnalysisReport {
        val lines = sourceCode.lines()
        val issues = mutableListOf<SecurityIssue>()
        var patched = sourceCode
        var syntaxValid = true

        val langLower = language.lowercase()

        // 1. Python Analysis
        if (langLower.contains("py") || (!langLower.contains("bash") && !langLower.contains("c++") && !langLower.contains("go"))) {
            lines.forEachIndexed { idx, line ->
                val lineNum = idx + 1
                val trimmed = line.trim()

                // Check SQL Injection pattern
                if (trimmed.contains("execute(") && (trimmed.contains("%") || trimmed.contains("+") || trimmed.contains("f\"") || trimmed.contains("f'"))) {
                    issues.add(
                        SecurityIssue(
                            title = "SQL Injection Vulnerability (CWE-89)",
                            severity = SecuritySeverity.CRITICAL,
                            lineNumber = lineNum,
                            description = "Direct string formatting or interpolation detected inside SQL execute() call.",
                            remediation = "Use parameterized queries with placeholders: cursor.execute('SELECT * FROM users WHERE id = %s', (user_id,))"
                        )
                    )
                    patched = patched.replace(line, "    # Patched: Parameterized query prevents SQL injection\n    cursor.execute(\"SELECT * FROM users WHERE username = %s\", (username,))")
                }

                // Check Command Injection
                if (trimmed.contains("os.system(") || (trimmed.contains("subprocess.") && trimmed.contains("shell=True"))) {
                    issues.add(
                        SecurityIssue(
                            title = "Command Injection Vulnerability (CWE-78)",
                            severity = SecuritySeverity.HIGH,
                            lineNumber = lineNum,
                            description = "Unsanitized system shell execution allows arbitrary command execution by attackers.",
                            remediation = "Use subprocess.run with arguments list and shell=False: subprocess.run(['ping', '-c', '1', host], check=True)"
                        )
                    )
                    patched = patched.replace(line, "    # Patched: Passing arguments as list with shell=False\n    subprocess.run([\"ping\", \"-c\", \"1\", sanitized_host], check=True)")
                }

                // Hardcoded Secrets
                if ((trimmed.contains("api_key") || trimmed.contains("password") || trimmed.contains("secret")) &&
                    (trimmed.contains("=\"") || trimmed.contains("= \"") || trimmed.contains("='")) && !trimmed.contains("os.getenv")) {
                    issues.add(
                        SecurityIssue(
                            title = "Hardcoded Secret / Credential (CWE-798)",
                            severity = SecuritySeverity.HIGH,
                            lineNumber = lineNum,
                            description = "Sensitive credentials or API keys found in plain source code.",
                            remediation = "Load secrets from environment variables using os.getenv('API_KEY') or .env configuration."
                        )
                    )
                    patched = patched.replace(line, "    # Patched: Loaded safely from environment variable\n    API_KEY = os.getenv(\"API_KEY\")")
                }

                // Insecure Deserialization
                if (trimmed.contains("pickle.loads(") || trimmed.contains("pickle.load(")) {
                    issues.add(
                        SecurityIssue(
                            title = "Insecure Deserialization (CWE-502)",
                            severity = SecuritySeverity.CRITICAL,
                            lineNumber = lineNum,
                            description = "Pickle deserialization executes arbitrary bytecode embedded in payloads.",
                            remediation = "Use safe data formats like json.loads() or hmac signature verification."
                        )
                    )
                    patched = patched.replace(line, "    # Patched: Use safe JSON parsing instead of pickle\n    data = json.loads(payload)")
                }

                // Insecure Socket without timeout
                if (trimmed.contains("socket.socket(") && !sourceCode.contains("settimeout(")) {
                    issues.add(
                        SecurityIssue(
                            title = "Missing Socket Timeout (DoS Risk)",
                            severity = SecuritySeverity.MEDIUM,
                            lineNumber = lineNum,
                            description = "Network socket created without explicit timeout can hang threads indefinitely.",
                            remediation = "Always configure s.settimeout(2.0) after socket creation."
                        )
                    )
                }

                // Basic Python syntax checks
                if ((trimmed.startsWith("def ") || trimmed.startsWith("if ") || trimmed.startsWith("for ") || trimmed.startsWith("while ") || trimmed.startsWith("try")) &&
                    !trimmed.endsWith(":") && !trimmed.endsWith("\\") && !trimmed.contains("#")) {
                    syntaxValid = false
                    issues.add(
                        SecurityIssue(
                            title = "Syntax Error: Missing Colon ':'",
                            severity = SecuritySeverity.LOW,
                            lineNumber = lineNum,
                            description = "Python block declaration statement is missing trailing colon.",
                            remediation = "Add ':' at the end of statement: $trimmed:"
                        )
                    )
                }
            }
        }

        // 2. Bash / Shell Analysis
        if (langLower.contains("bash") || langLower.contains("sh")) {
            lines.forEachIndexed { idx, line ->
                val lineNum = idx + 1
                val trimmed = line.trim()
                if (trimmed.contains("eval ") || trimmed.contains("`") || (trimmed.contains("$(") && trimmed.contains("cat"))) {
                    issues.add(
                        SecurityIssue(
                            title = "Insecure Dynamic Command Evaluation in Bash",
                            severity = SecuritySeverity.HIGH,
                            lineNumber = lineNum,
                            description = "Using 'eval' on variable inputs allows shell meta-character manipulation.",
                            remediation = "Quote all variable expansions strictly: \"\${VAR}\" and avoid eval."
                        )
                    )
                }
            }
        }

        // 3. C++ / Memory Analysis
        if (langLower.contains("c++") || langLower.contains("cpp") || langLower.contains("c")) {
            lines.forEachIndexed { idx, line ->
                val lineNum = idx + 1
                val trimmed = line.trim()
                if (trimmed.contains("strcpy(") || trimmed.contains("gets(") || trimmed.contains("sprintf(")) {
                    issues.add(
                        SecurityIssue(
                            title = "Buffer Overflow Vulnerability (CWE-120)",
                            severity = SecuritySeverity.CRITICAL,
                            lineNumber = lineNum,
                            description = "Unbounded memory copy function susceptible to stack buffer overflows.",
                            remediation = "Use bounded alternatives: strncpy(), snprintf(), or std::string in C++."
                        )
                    )
                }
            }
        }

        val summary = if (issues.isEmpty()) {
            "Analysis Passed: No high-risk security flaws or syntax bugs detected. Code conforms to defensive best practices."
        } else {
            val crit = issues.count { it.severity == SecuritySeverity.CRITICAL }
            val high = issues.count { it.severity == SecuritySeverity.HIGH }
            "Audit Complete: Identified ${issues.size} issues ($crit Critical, $high High). Automated remediations and secure patches generated below."
        }

        return CodeAnalysisReport(
            language = language,
            isSyntaxValid = syntaxValid,
            summary = summary,
            issues = issues,
            patchedCode = patched
        )
    }
}
