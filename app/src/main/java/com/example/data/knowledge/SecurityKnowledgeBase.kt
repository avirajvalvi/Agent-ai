package com.example.data.knowledge

import com.example.data.models.CustomSecurityTool
import com.example.data.models.SecurityProject

object SecurityKnowledgeBase {

    val sampleProjects = listOf(
        SecurityProject(
            id = "proj-recon-01",
            title = "Network Reconnaissance & Port Auditing",
            description = "Nmap scan logs, SYN stealth analysis, and active service fingerprinting on internal subnets.",
            category = "Reconnaissance",
            scriptCount = 3,
            sampleSnippet = "nmap -sS -p 21,22,80,443,8080 -T4 192.168.1.100"
        ),
        SecurityProject(
            id = "proj-defense-02",
            title = "SSH Brute-Force Log Detection & Fail2ban",
            description = "Parsing /var/log/auth.log with Python regex to detect failed credential bursts and create iptables blocks.",
            category = "Defense & Incident Response",
            scriptCount = 2,
            sampleSnippet = "python3 labs/log_parser.py"
        ),
        SecurityProject(
            id = "proj-web-03",
            title = "HTTP Security Headers & OWASP Audit",
            description = "Analyzing Content-Security-Policy, HSTS, X-Frame-Options, and SSL/TLS configuration.",
            category = "Web Application Security",
            scriptCount = 2,
            sampleSnippet = "curl -I https://sandbox-webapp.local"
        ),
        SecurityProject(
            id = "proj-custom-04",
            title = "Custom Multi-Threaded Port Scanner",
            description = "Python socket-based custom port scanner with configurable timeout and banner grabbing.",
            category = "Custom Tool Development",
            scriptCount = 1,
            sampleSnippet = "python3 labs/port_scanner.py"
        )
    )

    val sampleCustomTools = listOf(
        CustomSecurityTool(
            id = "tool-port-scanner",
            name = "Ethical Port Scanner",
            category = "Recon",
            language = "Python",
            description = "Fast socket-based TCP scanner designed for authorized network infrastructure auditing.",
            code = """
import socket
import concurrent.futures

def scan_port(target_ip, port):
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.settimeout(0.6)
            if s.connect_ex((target_ip, port)) == 0:
                return port, True
    except Exception:
        pass
    return port, False

def audit_host(ip, port_list):
    print(f"[*] Auditing {ip}...")
    with concurrent.futures.ThreadPoolExecutor(maxWorkers=10) as executor:
        futures = [executor.submit(scan_port, ip, p) for p in port_list]
        for f in concurrent.futures.as_completed(futures):
            p, is_open = f.result()
            if is_open:
                print(f"[+] OPEN: Port {p}")
            """.trimIndent(),
            usageExample = "audit_host('127.0.0.1', [22, 80, 443, 3306, 8080])"
        ),
        CustomSecurityTool(
            id = "tool-log-parser",
            name = "Auth.log Intrusion Parser",
            category = "Defense",
            language = "Python",
            description = "Detects rapid failed authentication bursts in Linux logs and correlates attacker source IPs.",
            code = """
import re
from collections import Counter

def parse_auth_log(filepath):
    failed_ips = []
    with open(filepath, 'r') as f:
        for line in f:
            if 'Failed password' in line:
                m = re.search(r'from\s+(\d+\.\d+\.\d+\.\d+)', line)
                if m:
                    failed_ips.append(m.group(1))
    
    top = Counter(failed_ips).most_common(5)
    for ip, count in top:
        print(f"[ALERT] Attacker IP {ip}: {count} failed attempts")
            """.trimIndent(),
            usageExample = "parse_auth_log('/var/log/auth.log')"
        ),
        CustomSecurityTool(
            id = "tool-header-audit",
            name = "HTTP Security Headers Inspector",
            category = "Web Defense",
            language = "Python",
            description = "Validates presence of vital protective headers (CSP, HSTS, X-Content-Type, X-Frame-Options).",
            code = """
import urllib.request

REQUIRED_HEADERS = [
    "Strict-Transport-Security",
    "Content-Security-Policy",
    "X-Frame-Options",
    "X-Content-Type-Options"
]

def check_headers(url):
    req = urllib.request.Request(url, headers={'User-Agent': 'SecMentor-Audit/1.0'})
    with urllib.request.urlopen(req) as resp:
        headers = dict(resp.getheaders())
        for rh in REQUIRED_HEADERS:
            status = "PASS" if rh in headers else "FAIL (MISSING)"
            print(f"[{status}] {rh}")
            """.trimIndent(),
            usageExample = "check_headers('https://example.com')"
        )
    )

    fun getMentorResponse(query: String): String {
        val q = query.lowercase()

        // Safety filter: Malicious / live attack payloads
        if (q.contains("hack wifi") || q.contains("ddos") || q.contains("steal password") || q.contains("carding") || q.contains("exploit bank")) {
            return """
⚠️ **सुरक्षा और नीति सूचना (Safety & Legal Notice)**:
SecMentor केवल रक्षात्मक सुरक्षा (Defensive Security), सिस्टम ऑडिटिंग और लीगल सैंडबॉक्स एनवायरनमेंट में शिक्षण का समर्थन करता है। 

हम किसी वास्तविक लक्ष्य पर अनधिकृत अटैक या दुर्भावनापूर्ण स्क्रिप्ट्स को अनुमति नहीं देते।

इसके बजाय, आप सैंडबॉक्स में यह सीख सकते हैं:
1. नेटवर्क पर अपनी संपत्तियों को सुरक्षित कैसे रखें (`nmap` डिफेंसिव ऑडिट)।
2. अनधिकृत एक्सेस को कैसे डिटेक्ट और ब्लॉक करें (`auth.log` पार्सिंग व Fail2ban)।
3. अपने कोड में वल्नरेबिलिटीज़ को कैसे पैच करें।
            """.trimIndent()
        }

        if (q.contains("nmap") || q.contains("port scan") || q.contains("स्कैन")) {
            return """
🔍 **Nmap (Network Mapper) - ओपन सोर्स टूल विश्लेषण**:

**Nmap क्या है और कैसे काम करता है?**
Nmap एक ओपन-सोर्स नेटवर्क डिस्कवरी और वल्नरेबिलिटी स्कैनर है। यह टारगेट होस्ट्स को रॉ (raw) IP पैकेट भेजकर यह पता लगाता है कि कौन से पोर्ट्स ओपन हैं, कौन सी सर्विसेज (Web server, SSH, DB) चल रही हैं, और उनका ओएस क्या है।

**मुख्य Nmap स्कैन्स:**
- **TCP SYN Scan (`nmap -sS <target>`)**: इसे 'Stealth Scan' भी कहते हैं क्योंकि यह पूरा 3-Way Handshake पूरा नहीं करता (केवल SYN भेजता है और SYN-ACK मिलते ही RST भेज देता है)।
- **Service Version Detection (`nmap -sV <target>`)**: पोर्ट पर चल रहे सॉफ्टवेयर के सटीक वर्जन का पता लगाता है।
- **Default Scripts (`nmap -sC <target>`)**: Nmap Scripting Engine (NSE) के जरिए आम सुरक्षा खामियों को डिटेक्ट करता है।

**सैंडबॉक्स में टेस्ट करें:**
हमारे Terminal Sandbox में जाकर टाइप करें:
```bash
nmap -sV 192.168.1.100
```
यह सैंडबॉक्स वेब सर्वर पर पोर्ट 22, 80, 443 और 8080 का लाइव स्टेटस दिखाएगा!
            """.trimIndent()
        }

        if (q.contains("wireshark") || q.contains("packet") || q.contains("पैकेट") || q.contains("tcpdump")) {
            return """
📡 **Wireshark & CLI Packet Analysis (TShark / TCPDump)**:

**आंतरिक लॉजिक (Internal Working):**
Wireshark नेटवर्क इंटरफ़ेस कार्ड (NIC) को 'Promiscuous Mode' में डालकर नेटवर्क सेगमेंट से गुजरने वाले हर ईथरनेट फ्रेम को कैप्चर करता है। यह PCAP (Packet Capture) फॉर्मेट में स्टोर होता है।

**महत्वपूर्ण फिल्टर्स:**
- `tcp.flags.syn == 1 and tcp.flags.ack == 0` : नए TCP कनेक्शन अनुरोध ढूंढें।
- `http.request.method == "POST"` : फॉर्म सबमिशन और क्रेडेंशियल्स का ट्रैफिक देखें।
- `ip.src == 192.168.1.105` : संदिग्ध आईपी का पूरा कम्युनिकेशन ट्रैक करें।

**सैंडबॉक्स कमांड:**
टर्मिनल में चलाएं:
```bash
tshark -c 5 -i eth0
```
यह TCP Handshake और SSH बैनर एक्सचेंज के 5 पैकेट्स का रीयल-टाइम डिकोड दिखाएगा।
            """.trimIndent()
        }

        if (q.contains("sqlmap") || q.contains("sql injection") || q.contains("sqli")) {
            return """
🛡️ **SQL Injection & SQLMap रक्षात्मक विश्लेषण**:

**SQL Injection कैसे होता है?**
जब यूजर इनपुट को बिना सैनिटाइज किए सीधे डेटाबेस क्वेरी में जोड़ दिया जाता है:
```python
# Vulnerable Code:
cursor.execute("SELECT * FROM users WHERE user = '" + input_name + "'")
```
अगर हमलावर `' OR '1'='1` इनपुट देता है, तो पूरी टेबल का एक्सेस मिल जाता है।

**सुरक्षित उपाय (Remediation):**
हमेशा **Parameterized Queries (Prepared Statements)** का उपयोग करें:
```python
# Secure Patched Code:
cursor.execute("SELECT * FROM users WHERE user = %s", (input_name,))
```
Code Studio टैब में अपना Python कोड पेस्ट करके ऑटोमैटिक वल्नरेबिलिटी स्कैनर चलाएं!
            """.trimIndent()
        }

        if (q.contains("custom tool") || q.contains("टूल बना") || q.contains("python scanner")) {
            return """
🛠️ **कस्टम सिक्योरिटी टूल डेवलपमेंट (Custom Tool Building)**:

आप Python के `socket` मॉड्यूल का उपयोग करके खुद का पोर्ट स्कैनर बना सकते हैं।

**कोड उदाहरण (Port Scanner):**
```python
import socket

def scan(ip, port):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.settimeout(0.5)
    result = s.connect_ex((ip, port))
    if result == 0:
        print(f"[+] Port {port} is OPEN")
    s.close()

scan("127.0.0.1", 80)
```

**लाइव सैंडबॉक्स टेस्ट:**
टर्मिनल में जाकर सीधे रन करें:
```bash
python3 labs/port_scanner.py
```
आप Code Studio में इस कोड को एडिट और कस्टमाइज़ भी कर सकते हैं!
            """.trimIndent()
        }

        if (q.contains("git clone") || q.contains("github") || q.contains("गिटहब")) {
            return """
🐙 **GitHub Open-Source Security Tools का सुरक्षित उपयोग**:

जब आप GitHub से कोई सिक्योरिटी टूल क्लोन करते हैं:
1. **लाइसेंस जांचें**: GPL, MIT, Apache 2.0 का पालन करें।
2. **डिपेंडेंसी ऑडिट**: `requirements.txt` या `package.json` में दुर्भावनापूर्ण पैकेज (Typosquatting) न हों।
3. **सैंडबॉक्स में रन करें**: किसी भी अज्ञात स्क्रिप्ट को सीधे होस्ट मशीन पर चलाने के बजाय हमेशा आइसोलेटेड वर्चुअल एनवायरनमेंट में चलाएं।

**सैंडबॉक्स में टेस्ट करें:**
```bash
git clone https://github.com/nmap/nmap
ls -la nmap
```
            """.trimIndent()
        }

        return """
नमस्ते! मैं आपका **SecMentor AI** हूँ — आपका समर्पित ओपन-सोर्स सुरक्षा विशेषज्ञ और कोडिंग मेंटर।

मैं आपकी निम्नलिखित में मदद कर सकता हूँ:
1. 🔍 **GitHub Tools Mastery**: Nmap, Wireshark, SQLMap, Gobuster का आंतरिक आर्किटेक्चर समझना।
2. 💻 **Linux Terminal Sandbox**: सैंडबॉक्स में `nmap`, `python3`, `git clone`, और `grep` का लाइव अभ्यास।
3. 🛡️ **Code Analyzer**: Python/C++/Go/Bash कोड में SQLi, Command Injection, और बफर ओवरफ्लो ढूंढना व सुधारना।
4. ⚙️ **Custom Tool Building**: अपने खुद के नेटवर्क स्कैनर व लॉग पार्सर टूल तैयार करना।

शुरू करने के लिए नीचे दिए गए किसी भी प्रॉम्प्ट को टैप करें या अपना सवाल पूछें!
        """.trimIndent()
    }
}
