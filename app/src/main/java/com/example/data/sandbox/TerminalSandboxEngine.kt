package com.example.data.sandbox

import com.example.data.models.TerminalEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TerminalSandboxEngine {

    private var currentDir: String = "/home/secbox"
    private val fileSystem: MutableMap<String, String> = mutableMapOf()
    private val directorySet: MutableSet<String> = mutableSetOf(
        "/",
        "/home",
        "/home/secbox",
        "/home/secbox/labs",
        "/home/secbox/tools",
        "/home/secbox/scripts",
        "/var",
        "/var/log"
    )

    init {
        populateInitialFilesystem()
    }

    private fun populateInitialFilesystem() {
        fileSystem["/home/secbox/README.md"] = """
            # SecMentor Linux Sandbox Environment
            Welcome to the isolated defensive cybersecurity terminal.
            
            Available tools:
              - nmap          : Network scanning & port reconnaissance
              - python3       : Run defensive scripts & tools
              - git clone     : Clone GitHub security repositories
              - tshark/tcpdump: Inspect simulated packet flows
              - grep/cat/ls   : Unix log analysis & navigation
              
            Try:
              $ ls -la labs/
              $ python3 labs/port_scanner.py
              $ grep "Failed" /var/log/auth.log
              $ nmap -sV 192.168.1.100
        """.trimIndent()

        fileSystem["/home/secbox/labs/port_scanner.py"] = """
            # Ethical Port Scanner (Defensive Audit)
            import socket
            import sys
            
            target = "127.0.0.1"
            ports = [21, 22, 53, 80, 443, 3306, 8080]
            
            print(f"[*] Starting audit on {target}...")
            for port in ports:
                s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                s.settimeout(0.5)
                result = s.connect_ex((target, port))
                if result == 0:
                    print(f"[+] Port {port}/tcp is OPEN")
                else:
                    print(f"[-] Port {port}/tcp is closed")
                s.close()
            print("[*] Scan completed.")
        """.trimIndent()

        fileSystem["/home/secbox/labs/log_parser.py"] = """
            # SSH Auth Log Brute-Force Detector
            import re
            from collections import Counter
            
            log_path = "/var/log/auth.log"
            print(f"[*] Analyzing {log_path} for brute force attempts...")
            
            failed_ips = []
            with open(log_path, "r") as f:
                for line in f:
                    if "Failed password" in line:
                        match = re.search(r"from (\d+\.\d+\.\d+\.\d+)", line)
                        if match:
                            failed_ips.append(match.group(1))
                            
            counts = Counter(failed_ips)
            print("[!] Suspicious Failed Login Summary:")
            for ip, cnt in counts.most_common():
                risk = "HIGH [BRUTE FORCE]" if cnt >= 5 else "SUSPICIOUS"
                print(f"  -> {ip}: {cnt} failed attempts ({risk})")
        """.trimIndent()

        fileSystem["/var/log/auth.log"] = """
Nov  3 04:12:01 secbox sshd[1243]: Failed password for invalid user admin from 192.168.1.105 port 44210 ssh2
Nov  3 04:12:03 secbox sshd[1245]: Failed password for invalid user root from 192.168.1.105 port 44212 ssh2
Nov  3 04:12:05 secbox sshd[1248]: Failed password for invalid user test from 192.168.1.105 port 44214 ssh2
Nov  3 04:12:08 secbox sshd[1251]: Failed password for user secbox from 192.168.1.105 port 44218 ssh2
Nov  3 04:12:12 secbox sshd[1255]: Failed password for user secbox from 192.168.1.105 port 44222 ssh2
Nov  3 04:12:15 secbox sshd[1259]: Failed password for user secbox from 192.168.1.105 port 44226 ssh2
Nov  3 05:01:22 secbox sshd[1402]: Accepted publickey for secbox from 10.0.0.12 port 51220 ssh2
Nov  3 05:22:11 secbox sshd[1589]: Failed password for invalid user guest from 10.0.0.42 port 38100 ssh2
Nov  3 05:22:14 secbox sshd[1592]: Failed password for invalid user support from 10.0.0.42 port 38104 ssh2
        """.trimIndent()

        fileSystem["/home/secbox/tools/targets.txt"] = """
127.0.0.1       localhost
192.168.1.1     gateway.local
192.168.1.50    db-cluster.internal
192.168.1.100   sandbox-webapp.local
        """.trimIndent()
    }

    fun getCurrentPrompt(): String {
        val displayDir = if (currentDir == "/home/secbox") "~"
        else if (currentDir.startsWith("/home/secbox/")) "~" + currentDir.removePrefix("/home/secbox")
        else currentDir
        return "secbox@mentor:$displayDir$ "
    }

    fun execute(rawCommand: String): TerminalEntry {
        val trimmed = rawCommand.trim()
        if (trimmed.isEmpty()) {
            return TerminalEntry(rawCommand, "", 0, false, currentDir)
        }

        val parts = trimmed.split("\\s+".toRegex())
        val cmd = parts[0]
        val args = parts.drop(1)

        return when (cmd) {
            "help" -> executeHelp()
            "clear" -> TerminalEntry(trimmed, "\u000C", 0, false, currentDir)
            "pwd" -> TerminalEntry(trimmed, currentDir, 0, false, currentDir)
            "whoami" -> TerminalEntry(trimmed, "secbox", 0, false, currentDir)
            "uname" -> executeUname(args, trimmed)
            "ls", "ll" -> executeLs(args, trimmed)
            "cd" -> executeCd(args, trimmed)
            "cat" -> executeCat(args, trimmed)
            "mkdir" -> executeMkdir(args, trimmed)
            "rm" -> executeRm(args, trimmed)
            "grep" -> executeGrep(args, trimmed)
            "nmap" -> executeNmap(args, trimmed)
            "git" -> executeGit(args, trimmed)
            "python", "python3" -> executePython(args, trimmed)
            "curl" -> executeCurl(args, trimmed)
            "tcpdump", "tshark", "wireshark" -> executePacketCapture(args, trimmed)
            "echo" -> executeEcho(trimmed)
            else -> TerminalEntry(
                command = trimmed,
                output = "bash: $cmd: command not found. Type 'help' for available security sandbox tools.",
                exitCode = 127,
                isError = true,
                directory = currentDir
            )
        }
    }

    private fun executeHelp(): TerminalEntry {
        val helpText = """
            SecMentor Linux Sandbox - Built-in Commands:
              Navigation:
                ls [-la] [path]        List directory contents
                cd [path]              Change working directory (~, .., /path)
                pwd                    Print current working directory
                cat <file>             Display file content
                grep <pattern> <file>  Search for regular expression in file
                mkdir <dir>            Create a new directory
                rm <file>              Remove file
                clear                  Clear terminal buffer
              
              Security & Development Tools:
                nmap [options] <target>  Network exploration & vulnerability audit
                                         Examples: nmap 192.168.1.100, nmap -sV 192.168.1.50
                git clone <url>          Simulate cloning GitHub security tools
                                         Examples: git clone https://github.com/nmap/nmap
                python3 <script.py>      Run defensive Python tools
                                         Examples: python3 labs/port_scanner.py
                tcpdump / tshark         Live network packet analysis
                curl -I <url>            Inspect HTTP security headers
        """.trimIndent()
        return TerminalEntry("help", helpText, 0, false, currentDir)
    }

    private fun executeUname(args: List<String>, full: String): TerminalEntry {
        val out = "Linux secbox-sandbox 6.8.0-27-generic #29-Ubuntu SMP PREEMPT_DYNAMIC x86_64 GNU/Linux"
        return TerminalEntry(full, out, 0, false, currentDir)
    }

    private fun executeLs(args: List<String>, full: String): TerminalEntry {
        val showAll = args.any { it.contains("a") }
        val targetPath = args.lastOrNull { !it.startsWith("-") } ?: currentDir
        val resolved = resolvePath(targetPath)

        if (!directorySet.contains(resolved)) {
            return TerminalEntry(full, "ls: cannot access '$targetPath': No such file or directory", 2, true, currentDir)
        }

        val entries = mutableListOf<String>()
        if (showAll) {
            entries.add("drwxr-xr-x  .  secbox secbox")
            entries.add("drwxr-xr-x  .. secbox secbox")
        }

        // Subdirectories
        directorySet.filter { it.startsWith("$resolved/") && !it.removePrefix("$resolved/").contains("/") }
            .forEach {
                val name = it.substringAfterLast("/")
                entries.add("drwxr-xr-x  $name/  [DIR]")
            }

        // Files
        fileSystem.keys.filter { it.startsWith("$resolved/") && !it.removePrefix("$resolved/").contains("/") }
            .forEach {
                val name = it.substringAfterLast("/")
                val size = fileSystem[it]?.length ?: 0
                val perm = if (name.endsWith(".py") || name.endsWith(".sh")) "-rwxr-xr-x" else "-rw-r--r--"
                entries.add("$perm  $name  (${size}B)")
            }

        val out = if (entries.isEmpty()) "(empty directory)" else entries.joinToString("\n")
        return TerminalEntry(full, out, 0, false, currentDir)
    }

    private fun executeCd(args: List<String>, full: String): TerminalEntry {
        if (args.isEmpty() || args[0] == "~") {
            currentDir = "/home/secbox"
            return TerminalEntry(full, "", 0, false, currentDir)
        }

        val target = args[0]
        val resolved = resolvePath(target)
        if (directorySet.contains(resolved)) {
            currentDir = resolved
            return TerminalEntry(full, "", 0, false, currentDir)
        } else {
            return TerminalEntry(full, "bash: cd: $target: No such file or directory", 1, true, currentDir)
        }
    }

    private fun executeCat(args: List<String>, full: String): TerminalEntry {
        if (args.isEmpty()) {
            return TerminalEntry(full, "cat: missing file operand", 1, true, currentDir)
        }
        val target = resolvePath(args[0])
        val content = fileSystem[target]
        return if (content != null) {
            TerminalEntry(full, content, 0, false, currentDir)
        } else {
            TerminalEntry(full, "cat: ${args[0]}: No such file or directory", 1, true, currentDir)
        }
    }

    private fun executeMkdir(args: List<String>, full: String): TerminalEntry {
        if (args.isEmpty()) {
            return TerminalEntry(full, "mkdir: missing operand", 1, true, currentDir)
        }
        val target = resolvePath(args[0])
        directorySet.add(target)
        return TerminalEntry(full, "", 0, false, currentDir)
    }

    private fun executeRm(args: List<String>, full: String): TerminalEntry {
        if (args.isEmpty()) {
            return TerminalEntry(full, "rm: missing operand", 1, true, currentDir)
        }
        val target = resolvePath(args[0])
        if (fileSystem.containsKey(target)) {
            fileSystem.remove(target)
            return TerminalEntry(full, "", 0, false, currentDir)
        } else {
            return TerminalEntry(full, "rm: cannot remove '${args[0]}': No such file or directory", 1, true, currentDir)
        }
    }

    private fun executeGrep(args: List<String>, full: String): TerminalEntry {
        if (args.size < 2) {
            return TerminalEntry(full, "Usage: grep <pattern> <file>", 1, true, currentDir)
        }
        val pattern = args[0].removeSurrounding("\"").removeSurrounding("'")
        val filePath = resolvePath(args[1])
        val content = fileSystem[filePath]
            ?: return TerminalEntry(full, "grep: ${args[1]}: No such file or directory", 2, true, currentDir)

        val matches = content.lines().filter { it.contains(pattern, ignoreCase = true) }
        val output = if (matches.isEmpty()) "No matches found." else matches.joinToString("\n")
        return TerminalEntry(full, output, if (matches.isEmpty()) 1 else 0, false, currentDir)
    }

    private fun executeNmap(args: List<String>, full: String): TerminalEntry {
        val target = args.lastOrNull { !it.startsWith("-") } ?: "127.0.0.1"
        val timeStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())

        val scanResult = when {
            target.contains("192.168.1.100") || target.contains("webapp") -> """
Starting Nmap 7.94 ( https://nmap.org ) at $timeStr UTC
Nmap scan report for sandbox-webapp.local (192.168.1.100)
Host is up (0.00041s latency).
Not shown: 996 closed tcp ports (reset)
PORT     STATE SERVICE       VERSION
22/tcp   open  ssh           OpenSSH 9.2p1 Debian (protocol 2.0)
80/tcp   open  http          nginx 1.24.0 (Ubuntu)
443/tcp  open  ssl/https     nginx 1.24.0 (TLSv1.3)
8080/tcp open  http-proxy    Werkzeug/3.0.1 Python/3.11.8

Service Info: OS: Linux; CPE: cpe:/o:linux:linux_kernel
Nmap done: 1 IP address (1 host up) scanned in 0.84 seconds
            """.trimIndent()

            target.contains("192.168.1.50") || target.contains("db") -> """
Starting Nmap 7.94 ( https://nmap.org ) at $timeStr UTC
Nmap scan report for db-cluster.internal (192.168.1.50)
Host is up (0.00028s latency).
PORT     STATE SERVICE       VERSION
22/tcp   open  ssh           OpenSSH 8.9p1
3306/tcp open  mysql         MySQL 8.0.35-0ubuntu0.22.04.1
5432/tcp open  postgresql    PostgreSQL DB 15.4
6379/tcp open  redis         Redis key-value store 7.0.15

Nmap done: 1 IP address (1 host up) scanned in 0.62 seconds
            """.trimIndent()

            else -> """
Starting Nmap 7.94 ( https://nmap.org ) at $timeStr UTC
Nmap scan report for localhost ($target)
Host is up (0.00008s latency).
Not shown: 998 closed tcp ports (conn-refused)
PORT     STATE SERVICE VERSION
22/tcp   open  ssh     OpenSSH 9.2 (Simulated Sandbox)
8000/tcp open  http    Python SimpleHTTPServer (Audited)

Nmap done: 1 IP address (1 host up) scanned in 0.35 seconds
            """.trimIndent()
        }

        return TerminalEntry(full, scanResult, 0, false, currentDir)
    }

    private fun executeGit(args: List<String>, full: String): TerminalEntry {
        if (args.isEmpty() || args[0] != "clone") {
            return TerminalEntry(full, "git: only 'clone <repo_url>' is currently supported in this sandbox lab.", 1, true, currentDir)
        }
        val repoUrl = args.getOrNull(1) ?: return TerminalEntry(full, "fatal: You must specify a repository to clone.", 128, true, currentDir)
        val repoName = repoUrl.substringAfterLast("/").removeSuffix(".git")
        val clonePath = "$currentDir/$repoName"

        directorySet.add(clonePath)
        fileSystem["$clonePath/README.md"] = "# $repoName\nCloned into SecMentor sandbox for defensive auditing."
        fileSystem["$clonePath/security_audit.txt"] = "Audit status: Safe for sandboxed analysis."

        val output = """
Cloning into '$repoName'...
remote: Enumerating objects: 142, done.
remote: Counting objects: 100% (142/142), done.
remote: Compressing objects: 100% (98/98), done.
remote: Total 142 (delta 44), reused 110 (delta 25)
Receiving objects: 100% (142/142), 842.11 KiB | 2.10 MiB/s, done.
Resolving deltas: 100% (44/44), done.
Repository successfully cloned into: $clonePath
        """.trimIndent()
        return TerminalEntry(full, output, 0, false, currentDir)
    }

    private fun executePython(args: List<String>, full: String): TerminalEntry {
        if (args.isEmpty()) {
            return TerminalEntry(full, "Python 3.11.8 (main, SecMentor Defensive Sandbox)\nType 'help' for commands.", 0, false, currentDir)
        }
        val script = resolvePath(args[0])
        val code = fileSystem[script]
            ?: return TerminalEntry(full, "python3: can't open file '${args[0]}': [Errno 2] No such file or directory", 2, true, currentDir)

        val output = when {
            script.endsWith("port_scanner.py") -> """
[*] Starting audit on 127.0.0.1...
[-] Port 21/tcp is closed
[+] Port 22/tcp is OPEN
[-] Port 53/tcp is closed
[+] Port 80/tcp is OPEN
[+] Port 443/tcp is OPEN
[+] Port 3306/tcp is OPEN
[+] Port 8080/tcp is OPEN
[*] Scan completed: 5 open ports discovered.
            """.trimIndent()

            script.endsWith("log_parser.py") -> """
[*] Analyzing /var/log/auth.log for brute force attempts...
[!] Suspicious Failed Login Summary:
  -> 192.168.1.105: 6 failed attempts (HIGH [BRUTE FORCE])
  -> 10.0.0.42: 2 failed attempts (SUSPICIOUS)
[*] Recommendation: Add 192.168.1.105 to iptables DROP or fail2ban jail.
            """.trimIndent()

            else -> """
[Executing ${args[0]} in Sandbox]
>>> Running Python script (${code.lines().size} lines)...
[+] Syntax validation passed.
[+] Simulated execution finished with exit code 0.
            """.trimIndent()
        }

        return TerminalEntry(full, output, 0, false, currentDir)
    }

    private fun executeCurl(args: List<String>, full: String): TerminalEntry {
        val target = args.lastOrNull { !it.startsWith("-") } ?: "https://sandbox.local"
        val out = """
HTTP/2 200 OK
date: ${SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US).format(Date())} GMT
server: nginx/1.24.0
content-type: text/html; charset=UTF-8
strict-transport-security: max-age=31536000; includeSubDomains (ENFORCED)
x-content-type-options: nosniff
x-frame-options: DENY
content-security-policy: default-src 'self'; script-src 'self'
referrer-policy: strict-origin-when-cross-origin
        """.trimIndent()
        return TerminalEntry(full, out, 0, false, currentDir)
    }

    private fun executePacketCapture(args: List<String>, full: String): TerminalEntry {
        val out = """
Capturing on 'eth0' [Simulated Sandboxed Interface]:
  1 0.000000 192.168.1.105 → 192.168.1.100 TCP 74 44210 → 22 [SYN] Seq=0 Win=64240 Len=0 MSS=1460
  2 0.000042 192.168.1.100 → 192.168.1.105 TCP 74 22 → 44210 [SYN, ACK] Seq=0 Ack=1 Win=65160
  3 0.000420 192.168.1.105 → 192.168.1.100 TCP 66 44210 → 22 [ACK] Seq=1 Ack=1 Win=64240
  4 0.001200 192.168.1.100 → 192.168.1.105 SSH 108 Server: SSH-2.0-OpenSSH_9.2p1
  5 0.002450 192.168.1.105 → 192.168.1.100 SSH 98 Client: SSH-2.0-libssh_0.10.4
5 packets captured (TCP handshake + SSH Banner exchange detected).
        """.trimIndent()
        return TerminalEntry(full, out, 0, false, currentDir)
    }

    private fun executeEcho(full: String): TerminalEntry {
        if (full.contains(">")) {
            val parts = full.split(">", limit = 2)
            val text = parts[0].removePrefix("echo").trim().removeSurrounding("\"").removeSurrounding("'")
            val targetFile = resolvePath(parts[1].trim())
            fileSystem[targetFile] = text
            return TerminalEntry(full, "", 0, false, currentDir)
        }
        val text = full.removePrefix("echo").trim().removeSurrounding("\"").removeSurrounding("'")
        return TerminalEntry(full, text, 0, false, currentDir)
    }

    private fun resolvePath(path: String): String {
        return when {
            path == "~" || path.isEmpty() -> "/home/secbox"
            path.startsWith("~/") -> "/home/secbox/" + path.removePrefix("~/")
            path.startsWith("/") -> path
            path == ".." -> {
                if (currentDir == "/") "/"
                else currentDir.substringBeforeLast("/").ifEmpty { "/" }
            }
            path.startsWith("../") -> {
                val parent = if (currentDir == "/") "/" else currentDir.substringBeforeLast("/").ifEmpty { "/" }
                "$parent/${path.removePrefix("../")}"
            }
            else -> if (currentDir == "/") "/$path" else "$currentDir/$path"
        }
    }
}
