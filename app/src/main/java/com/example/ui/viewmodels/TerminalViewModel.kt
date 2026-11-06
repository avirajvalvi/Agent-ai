package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.data.models.TerminalEntry
import com.example.data.sandbox.TerminalSandboxEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TerminalViewModel : ViewModel() {

    private val sandboxEngine = TerminalSandboxEngine()

    private val _history = MutableStateFlow<List<TerminalEntry>>(
        listOf(
            TerminalEntry(
                command = "boot",
                output = "SecMentor Linux Sandbox (v6.8.0)\nType 'help' to see available commands and cybersecurity tools.\nTarget subnet: 192.168.1.0/24 (Simulated Lab)",
                exitCode = 0,
                directory = "~"
            )
        )
    )
    val history: StateFlow<List<TerminalEntry>> = _history.asStateFlow()

    private val _currentPrompt = MutableStateFlow(sandboxEngine.getCurrentPrompt())
    val currentPrompt: StateFlow<String> = _currentPrompt.asStateFlow()

    fun runCommand(cmd: String) {
        val entry = sandboxEngine.execute(cmd)
        if (entry.output == "\u000C") {
            // Clear command
            _history.value = emptyList()
        } else {
            _history.value = _history.value + entry
        }
        _currentPrompt.value = sandboxEngine.getCurrentPrompt()
    }

    fun clearTerminal() {
        _history.value = emptyList()
    }
}
