# SecMentor & AI Studio: Multimodal Agent, App Builder, File Analyzer & Video Suite

SecMentor AI is an autonomous, multimodal developer and security suite built in Jetpack Compose with a Claude-inspired warm dark editorial interface. It provides comprehensive document/file analysis (PDF, DOCX, TXT, Code), an automated AI App Builder with instant APK and source ZIP downloads, real-time camera and screen perception, an interactive Linux terminal sandbox, and an advanced 30-to-60 minute video generation and timeline editing engine.

***

### User Review & Critical Decisions

> [!IMPORTANT]
> The following capabilities are incorporated based on your instructions:
> - **File & Document Analysis**: Deep analysis of uploaded PDF, DOCX, TXT, and source code files with automated summarization, keyword/tag extraction, and an in-app content editor with export options.
> - **Automated App Builder**: Generates full-stack Jetpack Compose mobile app templates from natural language prompts with an instant one-click build pipeline.
> - **Download Options**: Immediate one-click downloads for ready-to-install APK packages (`app-release.apk`) and complete source ZIP archives (`project-source.zip`), plus an interactive in-app live preview.
> - **Real-Time Camera & Screen Perception**: CameraX live viewfinder with holographic reticles and dynamic screen OCR telemetry.
> - **Long-Form Video Studio (30 to 60 Minutes)**: Script-to-video generation and timeline editor with animated, cinematic volumetric fog, and cyber terminal styles.

- **Confirmed Decision 1**: File analysis supports both real Android file picker ingestion (`ActivityResultContracts.GetContent()`) and preloaded sample documents (Security Whitepaper PDF, Incident Report DOCX, Source Code TXT).
- **Confirmed Decision 2**: Document editor allows in-app content modification, keyword highlighting, and one-tap re-export.

***

## 1. Feature Specifications

### 1. Document & File Analysis Engine
- **Supported Formats**: PDF, DOCX, TXT, Markdown, Python, Bash, C++, JSON.
- **Automated Summarization**: Extracts executive summaries, structured bullet points, and critical risk findings.
- **Keyword & Entity Extraction**: Identifies CVE codes, IP addresses, core security terms, and domain keywords with frequency badges.
- **In-App Content Editor**: Allows users to modify text or code directly in the app, apply AI suggested improvements, and download the edited document.

### 2. Automated AI App Builder & Packager
- **Instant Generation**: Input app idea (e.g. Flashcard Study App, Cotswolds Restaurant Guide, Cyber Telemetry App).
- **Interactive Live Preview**: Run and interact with the synthesized Jetpack Compose app directly inside the preview frame.
- **One-Click Download Dialog**: Instant downloads for `app-release.apk` (installable Android package) and `project-source.zip` (full Gradle + Kotlin Compose source project).

### 3. Real-Time Camera & Screen Perception
- **CameraX Live Viewfinder**: High-frequency frame capture, holographic targeting reticles, and real-time OCR.
- **Dynamic Screen Perception**: Scans active display buffers, terminals, and code windows for security analysis.

### 4. Long-Form Video Generation & Timeline Suite (30-60 Minutes)
- **Modular Chapters**: Breaks scripts into structured scenes totaling 30 to 60 minutes.
- **Atmospheric Visuals**: Hyper-realistic moving clouds, soft volumetric fog, dreamy lighting, and cyber HUD animations.
- **Video Editing**: Timeline scrubbing, scene trimming (+/- 30s), narration editing, and subtitles.

### 5. Multimodal Security Mentor & Linux Sandbox
- **Claude Warm Dark Interface**: Chats, Projects, Terminal Sandbox, Code Studio, and Artifacts.
- **Virtual CLI Engine**: Safe practice with `nmap`, `git clone`, `python3`, `grep`, `cat`, and custom defensive tools.

***

## 2. Technical Architecture & Component Flow

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Shell                        │
├───────────────┬───────────────────┬───────────────────┬────────────────┤
│  Chat Screen  │   App Builder     │   File Analyzer   │  Video Studio  │
│  (Multimodal) │ (Auto Build & DL) │ (PDF/DOCX/TXT/Py) │ (30-60m Editor)│
├───────────────┴───────────────────┴───────────────────┴────────────────┤
│    Linux Terminal Sandbox   │   Real-time Camera & Screen Perception   │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                         ViewModel & Engine Layer                       │
│   • ChatViewModel          • AppBuilderViewModel                       │
│   • FileAnalyzerViewModel  • VideoStudioViewModel                      │
│   • TerminalViewModel      • CodeStudioViewModel                       │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                      Core Data & Hardware Services                     │
│   • DocumentAnalysisEngine (Summarizer, Keyword Extractor, Editor)     │
│   • AppGeneratorEngine (Compose code synthesizer & APK/ZIP packager)   │
│   • TerminalSandboxEngine (Virtual Unix FS & simulated cyber tools)    │
│   • CameraX Provider & Android Document Picker Integration             │
│   • Room Database (Persistence for Chats, Projects, and Artifacts)     │
└────────────────────────────────────────────────────────────────────────┘
```

***

## 3. Implementation Steps
1. **Document & File Analysis Engine**:
   - `FileModels.kt`: Data models for analyzed documents, summaries, extracted keywords, and editing state.
   - `DocumentAnalysisEngine.kt`: Parsing, summarization, keyword extraction, and content editing.
   - `FileAnalyzerViewModel.kt`: Ingesting user files via Android system picker or templates, driving analysis and edits.
   - `FileAnalyzerScreen.kt`: Upload card, summary view, keyword cloud, interactive in-app editor, and download options.
2. **Automated App Builder & Packaging Engine**:
   - `AppBuilderViewModel.kt` & `AppBuilderScreen.kt`: Prompts, one-click build pipeline, download dialog (APK & ZIP), and interactive live preview.
3. **Real-Time Camera & Screen Perception**:
   - `RealtimePerceptionModal.kt`: CameraX viewfinder and Screen OCR perception.
4. **Video Studio Integration**:
   - `VideoStudioScreen.kt`: Long-form 30-60 min generator, timeline editing, and export.
5. **Complete Navigation Integration**:
   - Update `ClaudeDrawerContent.kt` and `MainActivity.kt` with all features and verify build with `compile_applet`.
