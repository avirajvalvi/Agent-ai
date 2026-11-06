package com.example.data.search

import com.example.data.models.SecuritySeverity

data class SearchResultItem(
    val id: String,
    val title: String,
    val category: String, // "CVE", "MITRE", "TOOL", "DEFENSE", "PROTOCOL", "SCRIPT"
    val summary: String,
    val details: String,
    val severity: SecuritySeverity = SecuritySeverity.INFO,
    val codeSnippet: String? = null,
    val remediation: String? = null,
    val referenceSource: String = "SecMentor Enterprise Knowledge Base",
    val tags: List<String> = emptyList()
)

object GlobalSearchEngine {

    private val enterpriseCatalog: List<SearchResultItem> = listOf(
        SearchResultItem(
            id = "cve-2024-6387",
            title = "CVE-2024-6387 (regreSSHion)",
            category = "CVE",
            summary = "Signal handler race condition in OpenSSH's server (sshd) on glibc-based Linux systems resulting in unauthenticated remote code execution as root.",
            details = "A regression of CVE-2006-5051 in sshd where SIGALRM is handled asynchronously without async-signal-safety. An attacker timing the LoginGraceTime can overwrite memory pointers.",
            severity = SecuritySeverity.CRITICAL,
            codeSnippet = "# Verify your OpenSSH version:\nsshd -V\n# Temporary Mitigation in /etc/ssh/sshd_config:\nLoginGraceTime 0",
            remediation = "Upgrade openssh-server to 9.8p1 or newer immediately. Enforce firewall rules limiting port 22 access.",
            referenceSource = "NIST NVD / Qualys Security Advisory",
            tags = listOf("OpenSSH", "RCE", "Critical", "CVE-2024-6387", "Linux")
        ),
        SearchResultItem(
            id = "cve-2024-3094",
            title = "CVE-2024-3094 (XZ Utils Supply Chain Backdoor)",
            category = "CVE",
            summary = "Malicious backdoor embedded in upstream tarballs of XZ Utils (liblzma) versions 5.6.0 and 5.6.1 intercepting OpenSSH RSA authentication.",
            details = "The backdoor injected an IFUNC hook into libcrypto RSA_public_decrypt during sshd build processes, allowing remote authentication bypass by authorized signature.",
            severity = SecuritySeverity.CRITICAL,
            codeSnippet = "# Check liblzma library version:\nxz --version\nstrings \$(which xz) | grep -i 5.6.",
            remediation = "Downgrade xz to version 5.4.6 or update to sanitized distribution releases.",
            referenceSource = "Red Hat Security Advisory / Openwall",
            tags = listOf("XZ", "Supply Chain", "Backdoor", "CVE-2024-3094", "sshd")
        ),
        SearchResultItem(
            id = "cve-2021-44228",
            title = "CVE-2021-44228 (Log4Shell)",
            category = "CVE",
            summary = "JNDI lookup injection vulnerability in Apache Log4j2 allowing remote code execution via LDAP/RMI payloads.",
            details = "Log4j's message lookup parser evaluated \${jndi:ldap://...} strings in logs without sanitization, triggering arbitrary remote Java class loading.",
            severity = SecuritySeverity.CRITICAL,
            codeSnippet = "# System property mitigation:\nJAVA_OPTS=\"-Dlog4j2.formatMsgNoLookups=true\"",
            remediation = "Upgrade Log4j to 2.17.1 or higher. Strip JndiLookup.class from classpath jars.",
            referenceSource = "Apache Software Foundation / NIST",
            tags = listOf("Log4j", "Java", "JNDI", "RCE", "CVE-2021-44228")
        ),
        SearchResultItem(
            id = "mitre-t1046",
            title = "MITRE ATT&CK T1046: Network Service Discovery",
            category = "MITRE",
            summary = "Adversaries attempt to get a listing of services and open ports on networked systems using port scanners like Nmap and Masscan.",
            details = "Tactic: Discovery (TA0007). Monitoring connection bursts and half-open TCP SYN flags identifies active recon prior to exploitation.",
            severity = SecuritySeverity.MEDIUM,
            codeSnippet = "# Detect SYN scan bursts with iptables logging:\niptables -A INPUT -p tcp --tcp-flags ALL SYN -m limit --limit 5/m -j LOG --log-prefix \"[SYN_SCAN_DETECTED] \"",
            remediation = "Use network intrusion detection systems (Snort/Suricata) and stateful firewalls blocking unneeded inbound ports.",
            referenceSource = "MITRE ATT&CK Framework",
            tags = listOf("MITRE", "Discovery", "Nmap", "T1046", "SYN Scan")
        ),
        SearchResultItem(
            id = "mitre-t1190",
            title = "MITRE ATT&CK T1190: Exploit Public-Facing Application",
            category = "MITRE",
            summary = "Adversaries exploit software vulnerabilities or configuration bugs in Internet-facing programs (web servers, databases, SSH).",
            details = "Tactic: Initial Access (TA0001). Commonly involves SQL injection, deserialization flaws, buffer overflows, and unauthenticated remote code execution.",
            severity = SecuritySeverity.HIGH,
            codeSnippet = "# Install ModSecurity WAF on Nginx/Apache:\napt install libapache2-mod-security2 -y\n# Enable OWASP Core Rule Set (CRS)",
            remediation = "Deploy Web Application Firewalls (WAF), perform automated weekly vulnerability scanning, and implement strict least privilege.",
            referenceSource = "MITRE ATT&CK Framework",
            tags = listOf("MITRE", "Initial Access", "WAF", "T1190", "Vulnerability")
        ),
        SearchResultItem(
            id = "tool-nmap",
            title = "Nmap (Network Mapper) CLI Engine",
            category = "TOOL",
            summary = "De facto standard open-source utility for network discovery, port scanning, OS detection, and vulnerability audit scripting (NSE).",
            details = "Supports stealth SYN scans (-sS), UDP audits (-sU), service version interrogation (-sV), and default defensive NSE scripts (--script=default,vuln).",
            severity = SecuritySeverity.INFO,
            codeSnippet = "# Comprehensive defensive host audit in sandbox:\nnmap -sV -sC -O -T4 -p 1-1000 127.0.0.1 -oN sandbox_scan.txt",
            remediation = "Always secure written authorization before scanning production infrastructure.",
            referenceSource = "Nmap.org Reference Guide",
            tags = listOf("Nmap", "CLI", "Port Scan", "NSE", "Auditing")
        ),
        SearchResultItem(
            id = "tool-wireshark",
            title = "Wireshark & Tshark Packet Analyzer",
            category = "TOOL",
            summary = "World's foremost network protocol analyzer for packet capture, traffic inspection, and deep protocol dissection.",
            details = "Provides live inspection of Ethernet, 802.11, IP, TCP, UDP, TLS, DNS, HTTP/2, and HTTP/3 packets with granular display filters.",
            severity = SecuritySeverity.INFO,
            codeSnippet = "# Headless packet capture with tshark:\ntshark -i eth0 -f \"tcp port 443\" -w capture.pcap\n# Filter failed TLS handshakes:\ntshark -r capture.pcap -Y \"tls.alert_message\"",
            remediation = "Filter and redact sensitive payloads when archiving PCAPs.",
            referenceSource = "Wireshark Foundation Documentation",
            tags = listOf("Wireshark", "tshark", "PCAP", "Sniffer", "Packets")
        ),
        SearchResultItem(
            id = "tool-sqlmap",
            title = "SQLmap Automated Penetration Testing Tool",
            category = "TOOL",
            summary = "Open-source tool that automates the detection and exploitation of SQL injection flaws and database takeover.",
            details = "Supports Boolean-based blind, Error-based, UNION query-based, Stacked queries, and Time-based blind SQL injection techniques across 30+ DBMS.",
            severity = SecuritySeverity.HIGH,
            codeSnippet = "# Audit authorized web application parameter:\nsqlmap -u \"http://localhost:8080/search.php?id=1\" --batch --dbs",
            remediation = "Use Prepared Statements (Parameterized Queries) and ORMs across all database access layers.",
            referenceSource = "sqlmap.org Project",
            tags = listOf("SQLmap", "SQLi", "Web Security", "OWASP", "Database")
        ),
        SearchResultItem(
            id = "tool-frida",
            title = "Frida Dynamic Instrumentation Toolkit",
            category = "TOOL",
            summary = "Dynamic code instrumentation toolkit for developers, reverse engineers, and security researchers on Android, iOS, and Linux.",
            details = "Injects JavaScript into native apps to inspect memory, hook functions in real time, bypass SSL Pinning, and trace cryptographic calls.",
            severity = SecuritySeverity.HIGH,
            codeSnippet = "// JavaScript SSL Pinning bypass hook snippet for Android:\nJava.perform(function() {\n  var X509TrustManager = Java.use('javax.net.ssl.X509TrustManager');\n  // Override checkServerTrusted method\n});",
            remediation = "Implement root/hook detection (SafetyNet / Play Integrity API) and obfuscate critical native logic with ProGuard/DexGuard.",
            referenceSource = "Frida.re Documentation",
            tags = listOf("Frida", "Dynamic Analysis", "Android", "Hooking", "Reverse Engineering")
        ),
        SearchResultItem(
            id = "defense-ssh-hardening",
            title = "Linux SSH Server Hardening Standard",
            category = "DEFENSE",
            summary = "Enterprise baseline configuration for /etc/ssh/sshd_config to eliminate brute force and credential-based unauthorized access.",
            details = "Disables password authentication, limits access to ed25519 public keys, changes default port, and binds strictly to authorized network interfaces.",
            severity = SecuritySeverity.MEDIUM,
            codeSnippet = "# Recommended /etc/ssh/sshd_config hardened lines:\nPermitRootLogin no\nPasswordAuthentication no\nPubkeyAuthentication yes\nMaxAuthTries 3\nClientAliveInterval 300\nClientAliveCountMax 2",
            remediation = "Restart sshd with 'systemctl restart sshd' and confirm access in a separate terminal before closing session.",
            referenceSource = "CIS Linux Benchmarks / OpenSSH Guide",
            tags = listOf("SSH", "Hardening", "CIS Benchmark", "Linux", "Defense")
        ),
        SearchResultItem(
            id = "defense-iptables-fail2ban",
            title = "Fail2ban & Stateful Iptables Intrusion Prevention",
            category = "DEFENSE",
            summary = "Automated log auditing daemon that scans log files (/var/log/auth.log) and dynamically inserts firewall drop rules.",
            details = "Monitors failed SSH, HTTP, and FTP authentication bursts and bans attacker IPs for customizable ban times (e.g. 24 hours).",
            severity = SecuritySeverity.LOW,
            codeSnippet = "# Fail2ban jail.local configuration:\n[sshd]\nenabled = true\nport = ssh\nfilter = sshd\nlogpath = /var/log/auth.log\nmaxretry = 3\nbantime = 86400",
            remediation = "Enable fail2ban service: 'systemctl enable --now fail2ban' and monitor with 'fail2ban-client status sshd'.",
            referenceSource = "Debian Security Hardening Manual",
            tags = listOf("Fail2ban", "iptables", "Intrusion Prevention", "Logs")
        ),
        SearchResultItem(
            id = "proto-tls13",
            title = "TLS 1.3 Protocol Security Standard (RFC 8446)",
            category = "PROTOCOL",
            summary = "Modern transport security specification removing legacy weak ciphers (RC4, DES, 3DES, MD5, SHA-1) and enabling 0-RTT handshakes.",
            details = "Mandates forward secrecy (PFS) using Ephemeral Diffie-Hellman (ECDHE/DHE) and eliminates static RSA key exchange to prevent retro-active decryption.",
            severity = SecuritySeverity.INFO,
            codeSnippet = "# Test TLS 1.3 support on server:\nopenssl s_client -connect localhost:443 -tls1_3",
            remediation = "Disable TLS 1.0 and 1.1 in web server and reverse proxy configs. Enable TLS 1.2 and 1.3 exclusively.",
            referenceSource = "IETF RFC 8446",
            tags = listOf("TLS 1.3", "Cryptography", "RFC 8446", "PFS", "HTTPS")
        ),
        SearchResultItem(
            id = "owasp-top10",
            title = "OWASP Top 10 Web Application Security Risks",
            category = "DEFENSE",
            summary = "Authoritative consensus document representing the most critical security risks to web applications and APIs.",
            details = "Covers: A01 Broken Access Control, A02 Cryptographic Failures, A03 Injection, A04 Insecure Design, A05 Security Misconfiguration, A06 Vulnerable Components, A07 Auth Failures, A08 Integrity Failures, A09 Logging Failures, A10 SSRF.",
            severity = SecuritySeverity.HIGH,
            codeSnippet = "// Defense: Always validate authorization checks server-side on every request:\nif (!currentUser.hasRole(Role.ADMIN)) throw ForbiddenException();",
            remediation = "Integrate SAST and DAST scanning into CI/CD build pipelines and enforce continuous developer security training.",
            referenceSource = "OWASP Foundation (owasp.org)",
            tags = listOf("OWASP", "Top 10", "Web Security", "SSRF", "Injection")
        )
    )

    /**
     * Executes enterprise fuzzy and semantic keyword search across the catalog.
     */
    fun search(query: String, filterCategory: String? = null): List<SearchResultItem> {
        val q = query.trim().lowercase()
        if (q.isBlank()) {
            return if (filterCategory != null && filterCategory != "ALL") {
                enterpriseCatalog.filter { it.category.equals(filterCategory, ignoreCase = true) }
            } else {
                enterpriseCatalog
            }
        }

        val tokens = q.split(Regex("\\s+")).filter { it.isNotBlank() }

        val scored = enterpriseCatalog.mapNotNull { item ->
            if (filterCategory != null && filterCategory != "ALL" && !item.category.equals(filterCategory, ignoreCase = true)) {
                return@mapNotNull null
            }

            var score = 0
            val fullText = "${item.title} ${item.summary} ${item.details} ${item.remediation ?: ""} ${item.tags.joinToString(" ")}".lowercase()

            if (item.title.lowercase().contains(q)) score += 100
            if (item.tags.any { it.lowercase().contains(q) }) score += 60
            if (item.summary.lowercase().contains(q)) score += 40
            if (item.details.lowercase().contains(q)) score += 20

            tokens.forEach { token ->
                if (fullText.contains(token)) score += 15
            }

            if (score > 0) Pair(item, score) else null
        }

        return scored.sortedByDescending { it.second }.map { it.first }
    }

    /**
     * Finds exact or highest-relevance factual answer for a natural language user inquiry.
     */
    fun findFactualMatch(userPrompt: String): SearchResultItem? {
        val results = search(userPrompt)
        return results.firstOrNull()
    }
}
