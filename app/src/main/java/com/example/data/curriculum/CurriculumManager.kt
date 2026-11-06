package com.example.data.curriculum

import android.content.Context
import com.example.data.models.CurriculumModule
import com.example.data.models.CurriculumPhase
import com.example.data.models.CurriculumProgress
import com.example.data.models.HandsOnLabCommand
import com.example.data.models.LabQuizQuestion
import com.example.data.models.ModuleLockStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the 60-90 Day Ethical Hacking learning curriculum,
 * sequential module locking mechanics, and persistent progress tracking.
 */
class CurriculumManager(context: Context) {

    private val prefs = context.getSharedPreferences("curriculum_progress_prefs", Context.MODE_PRIVATE)

    private val _progress = MutableStateFlow(loadProgress())
    val progress: StateFlow<CurriculumProgress> = _progress.asStateFlow()

    private val basePhases: List<CurriculumPhase> = listOf(
        CurriculumPhase(
            id = "phase_1",
            phaseNumber = 1,
            title = "Linux, Networking & Bash Automation",
            titleHindi = "लिनक्स, नेटवर्किंग प्रोटोकॉल्स और बैश ऑटोमेशन",
            durationDays = "दिन 1 - 15",
            description = "लिनक्स कर्नेल, प्रोसेस इनटर्नल्स, टीसीपी/आईपी फ्लैग्स, और वाई-शार्क पैकेट विश्लेषण की बुनियादी समझ।",
            modules = listOf(
                CurriculumModule(
                    id = "mod_1_1",
                    dayRange = "Day 1-3",
                    orderIndex = 1,
                    title = "Linux Internals, Permissions & SUID Auditing",
                    titleHindi = "लिनक्स डायरेक्ट्री स्ट्रक्चर व SUID प्रिविलेज ऑडिट",
                    summary = "Learn /etc/passwd, /etc/shadow, chmod numeric bits, SUID/SGID elevation risk, and process lifecycle with ps/top.",
                    learningObjectives = listOf(
                        "Explain POSIX permissions (Read, Write, Execute)",
                        "Identify dangerous SUID binaries that allow privilege escalation",
                        "Audit running processes for unauthorized service daemon handles"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "find / -perm -4000 2>/dev/null ; ps aux --sort=-%mem | head -n 10",
                        expectedOutputHint = "Lists setuid root binaries and memory-intensive system processes",
                        targetEnvironment = "Linux Terminal Sandbox",
                        defensiveMitigation = "Remove SUID bit using 'chmod u-s /path/to/binary' on unneeded utilities"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "Which chmod permission mode grants owner full access, and read/execute to group and others?",
                        questionHindi = "कौन सा chmod मोड ओनर को फुल एक्सेस और बाकियों को रीड/एक्ज़ीक्यूट देता है?",
                        options = listOf("755", "777", "644", "700"),
                        correctIndex = 0,
                        explanation = "755 translates to rwxr-xr-x (Owner=7=rwx, Group=5=r-x, Others=5=r-x)."
                    ),
                    xpReward = 100,
                    estimatedHours = 3,
                    prerequisiteModuleId = null
                ),
                CurriculumModule(
                    id = "mod_1_2",
                    dayRange = "Day 4-7",
                    orderIndex = 2,
                    title = "Networking Protocols: TCP 3-Way Handshake, DNS & ARP",
                    titleHindi = "TCP 3-वे हैंडशेक, डीएनएस रिज़ॉल्यूशन व एआरपी टेबल",
                    summary = "Analyze TCP SYN, SYN-ACK, ACK sequence flags, ARP poisoning defense, and DNS header lookups.",
                    learningObjectives = listOf(
                        "Trace full TCP 3-way handshake packet sequence",
                        "Understand ARP spoofing attack vectors and static ARP mitigation",
                        "Audit socket listeners using ss and netstat"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "ss -tulpn ; ip neigh show ; tcpdump -i any -c 5 'tcp[tcpflags] & tcp-syn != 0'",
                        expectedOutputHint = "Shows active listening TCP/UDP ports, ARP neighbor table, and inbound SYN flags",
                        targetEnvironment = "Network Sandbox",
                        defensiveMitigation = "Deploy static ARP bindings or enable Dynamic ARP Inspection (DAI) on switches"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "What flag combination does a server return in step 2 of a TCP 3-Way Handshake?",
                        questionHindi = "TCP 3-वे हैंडशेक के दूसरे चरण में सर्वर कौन सा फ्लैग भेजता है?",
                        options = listOf("SYN + ACK", "FIN + ACK", "RST", "ACK only"),
                        correctIndex = 0,
                        explanation = "The server responds with SYN + ACK to acknowledge the client SYN and initiate its own connection."
                    ),
                    xpReward = 120,
                    estimatedHours = 4,
                    prerequisiteModuleId = "mod_1_1"
                ),
                CurriculumModule(
                    id = "mod_1_3",
                    dayRange = "Day 8-11",
                    orderIndex = 3,
                    title = "Bash Automation & Defensive Log Parsing",
                    titleHindi = "बैश स्क्रिप्टिंग और SSH लॉग्स पार्सिंग",
                    summary = "Automate detection of brute-force password guessing from /var/log/auth.log with AWK, Grep, and regex.",
                    learningObjectives = listOf(
                        "Write automated log parsers for unauthorized authentication events",
                        "Aggregate IP addresses with frequency counting",
                        "Generate automatic iptables drop scripts"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "grep 'Failed password' /var/log/auth.log | awk '{print $(NF-3)}' | sort | uniq -c | sort -nr | head -n 5",
                        expectedOutputHint = "Outputs top 5 attacking IP addresses with failed attempt counts",
                        targetEnvironment = "Linux Sandbox",
                        defensiveMitigation = "Install and configure Fail2ban to ban offending IPs after 3 consecutive failures"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "Which utility automatically bans IP addresses that show malicious signs in authentication logs?",
                        questionHindi = "कौन सा टूल ऑथेंटिकेशन लॉग्स में संदेहास्पद गतिविधियों पर IP को ऑटोमैटिक बैन करता है?",
                        options = listOf("Fail2ban", "Apache", "NFS", "Cron"),
                        correctIndex = 0,
                        explanation = "Fail2ban monitors log files and updates firewall rules to block IPs after repeated failed attempts."
                    ),
                    xpReward = 150,
                    estimatedHours = 4,
                    prerequisiteModuleId = "mod_1_2"
                ),
                CurriculumModule(
                    id = "mod_1_4",
                    dayRange = "Day 12-15",
                    orderIndex = 4,
                    title = "Wireshark CLI (Tshark) & Network Packet Sniffing",
                    titleHindi = "टी-शार्क और पैकेट कैप्चरिंग का व्यावहारिक अभ्यास",
                    summary = "Capture live sandbox packets, filter HTTP GET/POST headers, and inspect cleartext credentials.",
                    learningObjectives = listOf(
                        "Operate tshark from command line without GUI overhead",
                        "Extract HTTP headers and POST request bodies",
                        "Identify unencrypted protocol usage (Telnet, FTP, HTTP)"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "tshark -i any -Y 'http.request.method == \"POST\"' -T fields -e ip.src -e http.host -c 10",
                        expectedOutputHint = "Captures 10 live HTTP POST requests showing source IP and target host header",
                        targetEnvironment = "Packet Capture Sandbox",
                        defensiveMitigation = "Enforce TLS 1.3 encryption across all communication endpoints"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "What is the primary command-line equivalent of Wireshark for headless servers?",
                        questionHindi = "हेडलेस सर्वर के लिए Wireshark का मुख्य कमांड-लाइन विकल्प कौन सा है?",
                        options = listOf("Tshark", "Grep", "Curl", "Ping"),
                        correctIndex = 0,
                        explanation = "Tshark is the official terminal-based network protocol analyzer version of Wireshark."
                    ),
                    xpReward = 150,
                    estimatedHours = 4,
                    prerequisiteModuleId = "mod_1_3"
                )
            )
        ),
        CurriculumPhase(
            id = "phase_2",
            phaseNumber = 2,
            title = "Reconnaissance & Vulnerability Auditing",
            titleHindi = "नेटवर्क स्कैनिंग, Nmap मास्टरी और OSINT",
            durationDays = "दिन 16 - 30",
            description = "Nmap पोर्ट ऑडिटिंग, स्टील्थ SYN स्कैनिंग, Nmap स्क्रिप्टिंग इंजन (NSE), और OSINT सबडोमेन खोज।",
            modules = listOf(
                CurriculumModule(
                    id = "mod_2_1",
                    dayRange = "Day 16-20",
                    orderIndex = 5,
                    title = "Nmap Stealth SYN Scan & Banner Grabbing",
                    titleHindi = "Nmap स्टील्थ स्कैनिंग और सर्विस डिटेक्शन",
                    summary = "Master -sS, -sV, -O, -p- and timing templates (-T0 to -T5) with defensive IDS evasion understanding.",
                    learningObjectives = listOf(
                        "Differentiate full connect (-sT) from half-open stealth (-sS)",
                        "Extract exact software daemon version banners",
                        "Map open ports against known vulnerability databases"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "nmap -sS -sV -T4 -p 22,80,443,3306 127.0.0.1 -oN scan_report.txt",
                        expectedOutputHint = "Discovers open ports with service version strings and saves clean report",
                        targetEnvironment = "Nmap Audit Sandbox",
                        defensiveMitigation = "Close unused listening ports and configure firewall state tracking (conntrack)"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "Why is an Nmap TCP SYN scan (-sS) often called a 'half-open' scan?",
                        questionHindi = "Nmap TCP SYN स्कैन (-sS) को 'हाफ-ओपन' स्कैन क्यों कहा जाता है?",
                        options = listOf(
                            "It sends a RST packet instead of ACK to avoid establishing connection",
                            "It only scans half of the total ports",
                            "It uses UDP instead of TCP",
                            "It requires two network cards"
                        ),
                        correctIndex = 0,
                        explanation = "Nmap sends RST immediately after receiving SYN-ACK, never completing the 3-way handshake."
                    ),
                    xpReward = 180,
                    estimatedHours = 5,
                    prerequisiteModuleId = "mod_1_4"
                ),
                CurriculumModule(
                    id = "mod_2_2",
                    dayRange = "Day 21-25",
                    orderIndex = 6,
                    title = "Nmap Scripting Engine (NSE) Vulnerability Auditing",
                    titleHindi = "Nmap NSE द्वारा सुरक्षित वल्नरेबिलिटी स्कैनिंग",
                    summary = "Run safe vulnerability assessment scripts from Nmap's default, vuln, and safe NSE script categories.",
                    learningObjectives = listOf(
                        "Execute specific CVE detection scripts safely",
                        "Understand Lua script execution within NSE framework",
                        "Formulate automated remediation recommendations"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "nmap --script \"vuln and safe\" -p 80,443 127.0.0.1",
                        expectedOutputHint = "Reports detected web application vulnerabilities and known CVE status",
                        targetEnvironment = "NSE Sandbox",
                        defensiveMitigation = "Regularly patch web servers and apply security headers"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "In what programming language are Nmap Scripting Engine (NSE) scripts written?",
                        questionHindi = "Nmap स्क्रिप्टिंग इंजन (NSE) की स्क्रिप्ट्स किस भाषा में लिखी जाती हैं?",
                        options = listOf("Lua", "Python", "C++", "Java"),
                        correctIndex = 0,
                        explanation = "NSE scripts are written in Lua, a lightweight, embeddable scripting language."
                    ),
                    xpReward = 200,
                    estimatedHours = 5,
                    prerequisiteModuleId = "mod_2_1"
                ),
                CurriculumModule(
                    id = "mod_2_3",
                    dayRange = "Day 26-30",
                    orderIndex = 7,
                    title = "OSINT, Subdomain Enumeration & Shodan APIs",
                    titleHindi = "ओपन सोर्स इंटेलिजेंस (OSINT) और सबडोमेन ब्रूटफोर्स",
                    summary = "Leverage Certificate Transparency logs, Amass, whois, and passive search APIs for reconnaissance.",
                    learningObjectives = listOf(
                        "Query crt.sh certificate logs for publicly exposed subdomains",
                        "Extract WHOIS registrar and nameserver telemetry",
                        "Identify shadow IT and forgotten staging subdomains"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "curl -s \"https://crt.sh/?q=%.example.com&output=json\" | grep -o '\"name_value\": \"[^\"]*' | cut -d'\"' -f4 | sort -u | head -n 10",
                        expectedOutputHint = "Enumerates validated SSL/TLS subdomains from public certificate transparency logs",
                        targetEnvironment = "OSINT Sandbox",
                        defensiveMitigation = "Decommission obsolete staging servers and remove orphaned DNS records"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "What public log system makes newly issued SSL/TLS certificates publicly auditable?",
                        questionHindi = "कौन सा पब्लिक लॉग सिस्टम नए जारी SSL/TLS सर्टिफिकेट्स को सार्वजनिक रूप से देखने योग्य बनाता है?",
                        options = listOf("Certificate Transparency (CT)", "DNSSEC", "BGP Table", "Whois Database"),
                        correctIndex = 0,
                        explanation = "Certificate Transparency logs record every issued TLS certificate in append-only cryptographic ledgers."
                    ),
                    xpReward = 200,
                    estimatedHours = 5,
                    prerequisiteModuleId = "mod_2_2"
                )
            )
        ),
        CurriculumPhase(
            id = "phase_3",
            phaseNumber = 3,
            title = "Web Application Security (OWASP Top 10)",
            titleHindi = "वेब एप्लिकेशन सिक्योरिटी (OWASP Top 10)",
            durationDays = "दिन 31 - 50",
            description = "बर्प सूट प्रॉक्सी, SQL इंजेक्शन, XSS, CSRF, SSRF, और एक्सेस कंट्रोल प्रिवेंशन।",
            modules = listOf(
                CurriculumModule(
                    id = "mod_3_1",
                    dayRange = "Day 31-35",
                    orderIndex = 8,
                    title = "Burp Suite Setup & HTTP Request Tampering",
                    titleHindi = "बर्प सूट इंटरसेप्शन, रिपीटर और इंट्रूडर मास्टरी",
                    summary = "Intercept HTTP/S payloads, replay altered parameters with Repeater, and analyze cookie flags.",
                    learningObjectives = listOf(
                        "Install local Burp CA certificate in browser",
                        "Intercept and modify HTTP GET/POST queries",
                        "Analyze session management tokens and anti-CSRF headers"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "curl -x http://127.0.0.1:8080 -k -i -X GET \"http://localhost:3000/api/user\"",
                        expectedOutputHint = "Proxies request through local Burp interceptor displaying raw HTTP headers",
                        targetEnvironment = "Web Proxy Lab",
                        defensiveMitigation = "Set Secure and HttpOnly flags on all authentication cookies"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "Which cookie attribute prevents client-side JavaScript from accessing session tokens?",
                        questionHindi = "कौन सा कुकी एट्रिब्यूट क्लाइंट-साइड जावास्क्रिप्ट को सेशन टोकन पढ़ने से रोकता है?",
                        options = listOf("HttpOnly", "SameSite", "Secure", "Domain"),
                        correctIndex = 0,
                        explanation = "The HttpOnly flag blocks document.cookie access in JavaScript, mitigating XSS session theft."
                    ),
                    xpReward = 220,
                    estimatedHours = 5,
                    prerequisiteModuleId = "mod_2_3"
                ),
                CurriculumModule(
                    id = "mod_3_2",
                    dayRange = "Day 36-40",
                    orderIndex = 9,
                    title = "SQL Injection (SQLi) & Parameterized Queries",
                    titleHindi = "एसक्यूएल इंजेक्शन विश्लेषण और पैरामीटराइज्ड क्वेरी रोकथाम",
                    summary = "Identify SQL injection points and apply Prepared Statements (PDO / parameterized queries) to permanently remediate.",
                    learningObjectives = listOf(
                        "Differentiate in-band, error-based, and blind SQL injection",
                        "Explain database abstraction with prepared statements",
                        "Verify fix using sqlmap automated tester"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "sqlmap -u \"http://localhost:3000/products?id=1\" --batch --risk=1 --level=1",
                        expectedOutputHint = "Audits parameter id for SQL injection vulnerabilities and reports database backend",
                        targetEnvironment = "Web App Sandbox",
                        defensiveMitigation = "Never concatenate strings into SQL; always bind parameters with PreparedStatement"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "What is the single most effective defense against SQL Injection vulnerabilities?",
                        questionHindi = "SQL इंजेक्शन वल्नरेबिलिटी के विरुद्ध सबसे प्रभावी बचाव क्या है?",
                        options = listOf(
                            "Parameterized Queries (Prepared Statements)",
                            "Web Application Firewall (WAF) alone",
                            "Storing passwords in plain text",
                            "Changing database port"
                        ),
                        correctIndex = 0,
                        explanation = "Prepared statements separate query structure from user data, preventing arbitrary command interpretation."
                    ),
                    xpReward = 250,
                    estimatedHours = 6,
                    prerequisiteModuleId = "mod_3_1"
                ),
                CurriculumModule(
                    id = "mod_3_3",
                    dayRange = "Day 41-45",
                    orderIndex = 10,
                    title = "Cross-Site Scripting (XSS) & Content Security Policy",
                    titleHindi = "XSS (रिफ्लेक्टेड, स्टोर्ड) और CSP सुरक्षा हेडर",
                    summary = "Audit stored, reflected, and DOM-based XSS vectors and configure rigid Content-Security-Policy headers.",
                    learningObjectives = listOf(
                        "Trace unescaped HTML reflection in DOM",
                        "Deploy strict Content-Security-Policy (CSP) headers",
                        "Implement context-aware HTML entity encoding"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "curl -I -X GET \"http://localhost:3000\" | grep -i \"Content-Security-Policy\"",
                        expectedOutputHint = "Inspects response for CSP headers (default-src 'self'; script-src 'self')",
                        targetEnvironment = "Web App Sandbox",
                        defensiveMitigation = "Configure Content-Security-Policy: default-src 'self' and encode user-supplied text"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "Which type of XSS is permanently stored in the application's database?",
                        questionHindi = "किस प्रकार का XSS एप्लिकेशन के डेटाबेस में स्थायी रूप से स्टोर हो जाता है?",
                        options = listOf("Stored XSS", "Reflected XSS", "DOM-based XSS", "Blind SSRF"),
                        correctIndex = 0,
                        explanation = "Stored XSS embeds malicious payloads into the server database, executing when any user views the content."
                    ),
                    xpReward = 250,
                    estimatedHours = 6,
                    prerequisiteModuleId = "mod_3_2"
                )
            )
        ),
        CurriculumPhase(
            id = "phase_4",
            phaseNumber = 4,
            title = "System Hardening & Android Mobile Auditing",
            titleHindi = "सिस्टम हार्डनिंग और एंड्रॉइड मोबाइल ऑडिटिंग",
            durationDays = "दिन 51 - 70",
            description = "लिनक्स प्रिविलेज एस्केलेशन, APK रिवर्स इंजीनियरिंग (JADX, Apktool) और Frida रनटाइम हुकिंग।",
            modules = listOf(
                CurriculumModule(
                    id = "mod_4_1",
                    dayRange = "Day 51-60",
                    orderIndex = 11,
                    title = "Linux Privilege Escalation: Sudo, Cron & Capabilities",
                    titleHindi = "लिनक्स प्रिविलेज एस्केलेशन ऑडिटिंग (Cron, Sudo, Capabilities)",
                    summary = "Audit misconfigured sudo rights (sudo -l), writable cron scripts, and Linux capabilities (getcap).",
                    learningObjectives = listOf(
                        "Analyze sudoers configuration using sudo -l",
                        "Identify writable cron job executable scripts",
                        "Audit process capabilities (cap_setuid, cap_net_raw)"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "sudo -l ; getcap -r / 2>/dev/null",
                        expectedOutputHint = "Prints authorized sudo commands and binaries with granted Linux capabilities",
                        targetEnvironment = "Privilege Audit Sandbox",
                        defensiveMitigation = "Follow Principle of Least Privilege in /etc/sudoers and remove wildcard commands"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "What utility displays the sudo privileges granted to the current logged-in user?",
                        questionHindi = "वर्तमान लॉग-इन यूज़र के sudo प्रिविलेजेस देखने के लिए कौन सा कमांड प्रयोग होता है?",
                        options = listOf("sudo -l", "sudo -v", "whoami", "id -g"),
                        correctIndex = 0,
                        explanation = "'sudo -l' lists the allowed and forbidden commands for the invoking user."
                    ),
                    xpReward = 300,
                    estimatedHours = 7,
                    prerequisiteModuleId = "mod_3_3"
                ),
                CurriculumModule(
                    id = "mod_4_2",
                    dayRange = "Day 61-70",
                    orderIndex = 12,
                    title = "Android APK Decompilation & Dynamic Frida Hooking",
                    titleHindi = "APK डीकंपाइलेशन और Frida रनटाइम हुकिंग",
                    summary = "Inspect AndroidManifest.xml for exported components and write Frida JavaScript scripts to trace runtime methods.",
                    learningObjectives = listOf(
                        "Decompile APK resources and manifest using Apktool & JADX",
                        "Identify exported activities, services, and content providers",
                        "Hook Java methods at runtime using Frida API"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "apktool d app.apk -o output_dir ; grep -rn \"android:exported=\\\"true\\\"\" output_dir/AndroidManifest.xml",
                        expectedOutputHint = "Decodes APK and lists all exported components vulnerable to unauthorized Intent injection",
                        targetEnvironment = "Android Reverse Engineering Sandbox",
                        defensiveMitigation = "Set android:exported=\"false\" unless an external application explicitly requires access"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "Which attribute in AndroidManifest.xml makes a component accessible to other installed applications?",
                        questionHindi = "AndroidManifest.xml का कौन सा एट्रिब्यूट घटक को बाहरी ऐप्स के लिए सुलभ बनाता है?",
                        options = listOf("android:exported=\"true\"", "android:debuggable", "android:allowBackup", "android:theme"),
                        correctIndex = 0,
                        explanation = "android:exported=\"true\" allows other applications on the device to launch or access the component."
                    ),
                    xpReward = 350,
                    estimatedHours = 8,
                    prerequisiteModuleId = "mod_4_1"
                )
            )
        ),
        CurriculumPhase(
            id = "phase_5",
            phaseNumber = 5,
            title = "Autonomous AI Agents & n8n CI/CD Deployment",
            titleHindi = "ऑटोनॉमस AI एजेंट्स, n8n वर्कफ़्लो व गिटहब CI/CD",
            durationDays = "दिन 71 - 90",
            description = "n8n सेल्फ-होस्टिंग, LangChain/Gemini AI नोड्स, स्वचालित CVE ट्राइएजिंग और GitHub Actions डिप्लॉयमेंट।",
            modules = listOf(
                CurriculumModule(
                    id = "mod_5_1",
                    dayRange = "Day 71-80",
                    orderIndex = 13,
                    title = "Self-Hosted n8n AI Agent Pipeline Architecture",
                    titleHindi = "n8n डॉकर परिनियोजन व AI एजेंट वर्कफ़्लो",
                    summary = "Deploy self-hosted n8n in Docker, configure secure webhooks, credential encryption, and error triggers.",
                    learningObjectives = listOf(
                        "Deploy and persist n8n with Docker volumes",
                        "Build webhook listener nodes with HMAC authentication",
                        "Integrate Gemini / LangChain LLM nodes for automated triage"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "docker run -d --name n8n -p 5678:5678 -v ~/.n8n:/home/node/.n8n docker.n8n.io/n8nio/n8n",
                        expectedOutputHint = "Initializes autonomous n8n automation engine on http://localhost:5678",
                        targetEnvironment = "Docker / VPS Sandbox",
                        defensiveMitigation = "Enforce TLS and set N8N_BASIC_AUTH_ACTIVE=true with strong credentials"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "What is the primary role of a Webhook Node in an n8n AI Agent workflow?",
                        questionHindi = "n8n AI एजेंट वर्कफ़्लो में वेबहुक नोड का मुख्य कार्य क्या होता है?",
                        options = listOf(
                            "To listen for incoming HTTP POST events from external security systems",
                            "To compile Kotlin code",
                            "To clean docker caches",
                            "To act as a firewall"
                        ),
                        correctIndex = 0,
                        explanation = "Webhook nodes expose HTTP endpoints that trigger workflow execution upon receiving event data."
                    ),
                    xpReward = 400,
                    estimatedHours = 8,
                    prerequisiteModuleId = "mod_4_2"
                ),
                CurriculumModule(
                    id = "mod_5_2",
                    dayRange = "Day 81-90",
                    orderIndex = 14,
                    title = "GitHub Actions CI/CD for Autonomous Security Workflows",
                    titleHindi = "GitHub Actions CI/CD द्वारा वर्कफ़्लो ऑटोमेशन और अलर्टिंग",
                    summary = "Build a complete GitHub Actions pipeline that triggers n8n agents, lints Python/Bash security scripts, and posts security reports.",
                    learningObjectives = listOf(
                        "Configure .github/workflows/ai-security-agent.yml",
                        "Inject secrets via GitHub Secrets vault",
                        "Dispatch automated alerts and remediation PRs"
                    ),
                    handsOnLab = HandsOnLabCommand(
                        command = "cat .github/workflows/ai-security-agent.yml",
                        expectedOutputHint = "Displays configured multi-step CI/CD security audit pipeline",
                        targetEnvironment = "GitHub Actions Sandbox",
                        defensiveMitigation = "Never store plain-text secrets in repository code; always use GitHub Secrets"
                    ),
                    quizQuestion = LabQuizQuestion(
                        question = "Where should API keys and webhook tokens be stored for GitHub Actions workflows?",
                        questionHindi = "GitHub Actions के लिए API कीज़ और वेबहुक टोकन्स कहाँ स्टोर करने चाहिए?",
                        options = listOf("GitHub Repository Secrets", "README.md", "Public git commit", "Workflow YAML directly"),
                        correctIndex = 0,
                        explanation = "GitHub Secrets securely encrypts credentials and exposes them only during runner execution."
                    ),
                    xpReward = 500,
                    estimatedHours = 10,
                    prerequisiteModuleId = "mod_5_1"
                )
            )
        )
    )

    private fun loadProgress(): CurriculumProgress {
        val completed = prefs.getStringSet("completed_modules", emptySet()) ?: emptySet()
        val xp = prefs.getInt("total_xp", 0)
        val bypass = prefs.getBoolean("instructor_bypass", false)
        return CurriculumProgress(
            completedModuleIds = completed,
            totalXp = xp,
            isInstructorBypassEnabled = bypass
        )
    }

    fun getComputedPhases(): List<CurriculumPhase> {
        val currentProgress = _progress.value
        val completedSet = currentProgress.completedModuleIds
        val isBypass = currentProgress.isInstructorBypassEnabled

        return basePhases.mapIndexed { phaseIdx, phase ->
            val computedModules = phase.modules.mapIndexed { modIdx, mod ->
                val isCompleted = completedSet.contains(mod.id)
                val status = when {
                    isCompleted -> ModuleLockStatus.COMPLETED
                    isBypass -> ModuleLockStatus.UNLOCKED
                    // First module of first phase is unlocked by default
                    phaseIdx == 0 && modIdx == 0 -> ModuleLockStatus.UNLOCKED
                    // Otherwise, unlocked only if prerequisite is completed
                    mod.prerequisiteModuleId != null && completedSet.contains(mod.prerequisiteModuleId) -> ModuleLockStatus.UNLOCKED
                    // If first module of subsequent phase, unlocked if last module of prior phase is completed
                    mod.prerequisiteModuleId == null && phaseIdx > 0 -> {
                        val priorPhase = basePhases[phaseIdx - 1]
                        val lastPriorMod = priorPhase.modules.lastOrNull()
                        if (lastPriorMod != null && completedSet.contains(lastPriorMod.id)) {
                            ModuleLockStatus.UNLOCKED
                        } else {
                            ModuleLockStatus.LOCKED
                        }
                    }
                    else -> ModuleLockStatus.LOCKED
                }
                mod.copy(status = status)
            }
            phase.copy(modules = computedModules)
        }
    }

    fun markModuleCompleted(moduleId: String) {
        val current = _progress.value
        if (current.completedModuleIds.contains(moduleId)) return

        val newCompleted = current.completedModuleIds + moduleId
        // Find module XP reward
        val modXp = basePhases.flatMap { it.modules }.firstOrNull { it.id == moduleId }?.xpReward ?: 100
        val newXp = current.totalXp + modXp

        prefs.edit()
            .putStringSet("completed_modules", newCompleted)
            .putInt("total_xp", newXp)
            .apply()

        _progress.value = current.copy(
            completedModuleIds = newCompleted,
            totalXp = newXp,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun toggleInstructorBypass(enabled: Boolean) {
        prefs.edit().putBoolean("instructor_bypass", enabled).apply()
        _progress.value = _progress.value.copy(
            isInstructorBypassEnabled = enabled,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun resetCurriculumProgress() {
        prefs.edit().clear().apply()
        _progress.value = CurriculumProgress()
    }
}
