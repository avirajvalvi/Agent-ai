package com.example.data.knowledge

data class FactualConcept(
    val title: String,
    val summary: String,
    val technicalDetails: String,
    val commands: List<String>,
    val defensiveRemediation: String,
    val relatedConcepts: List<String>
)

object ExpansiveKnowledgeBase {

    val concepts = listOf(
        FactualConcept(
            title = "TCP SYN Half-Open Stealth Scanning (-sS)",
            summary = "A port scanning technique that sends SYN packets and does not complete the 3-way TCP handshake.",
            technicalDetails = "1. Client sends TCP SYN.\n2. Open port replies with SYN/ACK.\n3. Client immediately responds with RST rather than ACK to tear down connection before application layers log it.\n4. Closed port replies with RST directly.",
            commands = listOf(
                "nmap -sS -p 1-1024 192.168.1.1",
                "hping3 -S -p 80 192.168.1.1 -c 1"
            ),
            defensiveRemediation = "Deploy stateful firewalls (nftables) with synproxy or rate limiting. Configure Snort rule: alert tcp any any -> \$HOME_NET any (flags:S; threshold:type both, track by_src, count 20, seconds 5; msg:\"SYN Scan Detected\";)",
            relatedConcepts = listOf("TCP 3-Way Handshake", "RST Packet", "Firewall Rules", "Nmap Flags")
        ),
        FactualConcept(
            title = "SSH Key-Based Authentication & Cryptographic Curves",
            summary = "Asymmetric cryptography standard replacing vulnerable password authentication with Ed25519 or RSA-4096 keys.",
            technicalDetails = "Ed25519 utilizes Edwards-curve Digital Signature Algorithm (EdDSA) over Curve25519. Provides high performance, resistance to side-channel timing attacks, and collision resilience compared to legacy RSA.",
            commands = listOf(
                "ssh-keygen -t ed25519 -C \"admin@secmentor.local\"",
                "ssh-copy-id -i ~/.ssh/id_ed25519.pub user@host"
            ),
            defensiveRemediation = "Always enforce 'PasswordAuthentication no' in /etc/ssh/sshd_config and protect private keys with strong passphrases (PBKDF2/Argon2).",
            relatedConcepts = listOf("Ed25519", "sshd_config", "Public Key Cryptography", "Fail2ban")
        ),
        FactualConcept(
            title = "OWASP A03: SQL Injection (SQLi) Deep Dive",
            summary = "Vulnerability where untrusted user input alters database query structure and execution.",
            technicalDetails = "Occurs when string concatenation or string interpolation forms dynamic queries: SELECT * FROM users WHERE name = '' + input + ''. An input of ' OR '1'='1 bypasses authentication.",
            commands = listOf(
                "sqlmap -u \"http://testphp.vulnweb.com/artists.php?artist=1\" --risk=1 --level=1",
                "curl -G \"http://localhost:8080/api/users\" --data-urlencode \"id=1 UNION SELECT 1,username,password FROM users--\""
            ),
            defensiveRemediation = "Use parameterized queries (PreparedStatements) exclusively: 'SELECT * FROM users WHERE name = ?'. Never concatenate user inputs into SQL strings.",
            relatedConcepts = listOf("Parameterized Queries", "ORM Security", "Input Sanitization", "WAF")
        ),
        FactualConcept(
            title = "Android APK Decompilation & Reverse Engineering",
            summary = "Static inspection of Android bytecode (DEX/Smali) and manifest configurations using JADX and Apktool.",
            technicalDetails = "Android APKs package compiled Java/Kotlin bytecode as classes.dex. JADX decompiles Dalvik bytecode back into readable Java sources. Apktool extracts resources, drawables, and AndroidManifest.xml.",
            commands = listOf(
                "jadx-gui target_app.apk",
                "apktool d target_app.apk -o unpacked_app/",
                "apksigner verify --verbose target_app.apk"
            ),
            defensiveRemediation = "Enable ProGuard/R8 code shrinking and obfuscation. Implement tamper detection and root detection using Play Integrity API.",
            relatedConcepts = listOf("Smali", "DEX", "JADX", "Frida", "Play Integrity")
        ),
        FactualConcept(
            title = "Frida Dynamic Instrumentation & Runtime Hooking",
            summary = "Injecting custom JavaScript into running native and Android processes to inspect memory and bypass protections.",
            technicalDetails = "Frida connects to frida-server on Android via adb or ptrace. It dynamically intercepts native symbols in libc.so, libart.so, and Java runtime methods via the Java.perform bridge.",
            commands = listOf(
                "frida-ps -Uai",
                "frida -U -f com.example.app -l ssl_unpin.js --no-pause"
            ),
            defensiveRemediation = "Check for debuggable flags, inspect ptrace status (/proc/self/status TracerPid), scan for frida-server named pipes in memory, and implement native integrity checks.",
            relatedConcepts = listOf("SSL Pinning", "Reverse Engineering", "Dynamic Analysis", "ptrace")
        ),
        FactualConcept(
            title = "Linux Log Forensic Analysis (/var/log/auth.log)",
            summary = "Examining authentication events to identify brute force patterns, privilege escalations, and persistence mechanisms.",
            technicalDetails = "PAM (Pluggable Authentication Modules) logs sshd authentication failures, sudo execution, and cron events into /var/log/auth.log (Debian/Ubuntu) or /var/log/secure (RHEL/CentOS).",
            commands = listOf(
                "grep \"Failed password\" /var/log/auth.log | awk '{print \$(NF-3)}' | sort | uniq -c | sort -nr",
                "grep \"sudo:\" /var/log/auth.log | grep \"COMMAND=\"",
                "lastb -n 20"
            ),
            defensiveRemediation = "Forward syslog events to a centralized SIEM (Elasticsearch/Splunk) via rsyslog with TLS to prevent attacker local log tampering.",
            relatedConcepts = listOf("Syslog", "Fail2ban", "SIEM", "Incident Response")
        )
    )

    fun queryKnowledge(prompt: String): FactualConcept? {
        val q = prompt.lowercase()
        return concepts.firstOrNull { concept ->
            concept.title.lowercase().contains(q) ||
            concept.relatedConcepts.any { q.contains(it.lowercase()) } ||
            q.split(Regex("\\s+")).any { concept.title.lowercase().contains(it) }
        }
    }
}
