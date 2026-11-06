package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.data.analyzer.CodeAnalyzerEngine
import com.example.data.models.CodeAnalysisReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CodeStudioViewModel : ViewModel() {

    private val analyzerEngine = CodeAnalyzerEngine()

    private val defaultVulnerableScript = """
# Sample Tool with Vulnerabilities for Audit Testing
import os
import sqlite3

def query_user(username):
    conn = sqlite3.connect("database.db")
    cursor = conn.cursor()
    # Flaw 1: Direct SQL concatenation
    query = f"SELECT * FROM users WHERE username = '{username}'"
    cursor.execute(query)
    return cursor.fetchall()

def ping_host(target_host):
    # Flaw 2: Direct Command Injection
    os.system("ping -c 1 " + target_host)

api_key = "AIzaSyD-981249128-SECRET"  # Flaw 3: Hardcoded Secret
    """.trimIndent()

    private val _sourceCode = MutableStateFlow(defaultVulnerableScript)
    val sourceCode: StateFlow<String> = _sourceCode.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("Python")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _analysisReport = MutableStateFlow<CodeAnalysisReport?>(null)
    val analysisReport: StateFlow<CodeAnalysisReport?> = _analysisReport.asStateFlow()

    init {
        // Run initial analysis on default template
        runAnalysis()
    }

    fun updateCode(code: String) {
        _sourceCode.value = code
    }

    fun selectLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun runAnalysis() {
        val report = analyzerEngine.analyzeCode(_sourceCode.value, _selectedLanguage.value)
        _analysisReport.value = report
    }

    fun applyPatchedCode() {
        _analysisReport.value?.patchedCode?.let {
            _sourceCode.value = it
            runAnalysis()
        }
    }

    fun loadTemplate(templateType: String) {
        when (templateType) {
            "sqli" -> {
                _selectedLanguage.value = "Python"
                _sourceCode.value = defaultVulnerableScript
            }
            "scanner" -> {
                _selectedLanguage.value = "Python"
                _sourceCode.value = """
import socket

def audit_ports(host, ports):
    print(f"Auditing host: {host}")
    for p in ports:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.settimeout(1.0)
        res = s.connect_ex((host, p))
        if res == 0:
            print(f"Port {p} is OPEN")
        s.close()

audit_ports("127.0.0.1", [22, 80, 443])
                """.trimIndent()
            }
            "bash" -> {
                _selectedLanguage.value = "Bash"
                _sourceCode.value = """
#!/bin/bash
# Network Audit Script
TARGET=${'$'}1
echo "Checking target ${'$'}TARGET"
eval "ping -c 2 ${'$'}TARGET"
                """.trimIndent()
            }
        }
        runAnalysis()
    }
}
