package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppDestination
import com.example.ui.components.ClaudeDrawerContent
import com.example.ui.screens.AgenticWorkflowScreen
import com.example.ui.screens.AppBuilderScreen
import com.example.ui.screens.ArtifactsScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ClaudeHomeScreen
import com.example.ui.screens.CodeStudioScreen
import com.example.ui.screens.EthicalHackingAcademyScreen
import com.example.ui.screens.FileAnalyzerScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.TerminalScreen
import com.example.ui.screens.TermuxYouTubeHubScreen
import com.example.ui.screens.VideoStudioScreen
import com.example.ui.theme.SecMentorTheme
import com.example.ui.viewmodels.AgenticWorkflowViewModel
import com.example.ui.viewmodels.AppBuilderViewModel
import com.example.ui.viewmodels.ChatViewModel
import com.example.ui.viewmodels.CodeStudioViewModel
import com.example.ui.viewmodels.FileAnalyzerViewModel
import com.example.ui.viewmodels.ProjectsViewModel
import com.example.ui.viewmodels.TerminalViewModel
import com.example.ui.viewmodels.VideoStudioViewModel
import kotlinx.coroutines.launch

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.example.data.ai.ModelCacheManager
import com.example.data.localization.LanguageManager
import com.example.data.preferences.UserSettingsRepository
import com.example.data.repository.FirestoreSyncRepository
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.screens.AuthScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private val chatViewModel: ChatViewModel by viewModels()
    private val agenticWorkflowViewModel: AgenticWorkflowViewModel by viewModels()
    private val terminalViewModel: TerminalViewModel by viewModels()
    private val codeStudioViewModel: CodeStudioViewModel by viewModels()
    private val projectsViewModel: ProjectsViewModel by viewModels()
    private val videoStudioViewModel: VideoStudioViewModel by viewModels()
    private val fileAnalyzerViewModel: FileAnalyzerViewModel by viewModels()
    private val appBuilderViewModel: AppBuilderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SecMentorTheme {
                SecMentorApp(
                    chatViewModel = chatViewModel,
                    agenticWorkflowViewModel = agenticWorkflowViewModel,
                    terminalViewModel = terminalViewModel,
                    codeStudioViewModel = codeStudioViewModel,
                    projectsViewModel = projectsViewModel,
                    videoStudioViewModel = videoStudioViewModel,
                    fileAnalyzerViewModel = fileAnalyzerViewModel,
                    appBuilderViewModel = appBuilderViewModel
                )
            }
        }
    }
}

@Composable
fun SecMentorApp(
    chatViewModel: ChatViewModel,
    agenticWorkflowViewModel: AgenticWorkflowViewModel,
    terminalViewModel: TerminalViewModel,
    codeStudioViewModel: CodeStudioViewModel,
    projectsViewModel: ProjectsViewModel,
    videoStudioViewModel: VideoStudioViewModel,
    fileAnalyzerViewModel: FileAnalyzerViewModel,
    appBuilderViewModel: AppBuilderViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val syncRepository = remember { FirestoreSyncRepository(context) }
    val modelCacheManager = remember { ModelCacheManager(context) }
    val langManager = remember { LanguageManager.getInstance(context) }
    val userSettingsRepo = remember { UserSettingsRepository(context) }

    val syncState by syncRepository.syncState.collectAsStateWithLifecycle()
    val pendingCount by syncRepository.pendingSyncCount.collectAsStateWithLifecycle()
    val cacheStats by modelCacheManager.stats.collectAsStateWithLifecycle()
    val currentLang by langManager.currentLanguage.collectAsStateWithLifecycle()

    val persistentLogs by agenticWorkflowViewModel.actionLogs.collectAsStateWithLifecycle()
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
    var showCodeStudioSubView by remember { mutableStateOf(false) }

    var showAuthScreen by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(authListener)
        onDispose {
            Firebase.auth.removeAuthStateListener(authListener)
        }
    }

    if (showLanguageDialog) {
        LanguageSelectorDialog(
            currentLanguage = currentLang,
            onSelectLanguage = { selected ->
                langManager.setLanguage(selected)
                coroutineScope.launch {
                    userSettingsRepo.updateLanguage(selected.code)
                }
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showAuthScreen) {
        AuthScreen(
            onAuthSuccess = {
                showAuthScreen = false
            },
            onContinueOffline = {
                showAuthScreen = false
            }
        )
        return
    }

    // Android predictive/system back handling: return to Claude Home screen
    BackHandler(enabled = drawerState.isOpen || currentDestination != AppDestination.HOME || showCodeStudioSubView) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (showCodeStudioSubView) {
            showCodeStudioSubView = false
        } else {
            currentDestination = AppDestination.HOME
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ClaudeDrawerContent(
                currentDestination = currentDestination,
                onSelectDestination = { dest ->
                    currentDestination = dest
                    showCodeStudioSubView = false
                    coroutineScope.launch { drawerState.close() }
                },
                onSelectRecentTopic = { topic ->
                    currentDestination = AppDestination.CHATS
                    chatViewModel.sendMessage(topic)
                    coroutineScope.launch { drawerState.close() }
                },
                onNewChat = {
                    currentDestination = AppDestination.CHATS
                    chatViewModel.clearChat()
                    coroutineScope.launch { drawerState.close() }
                },
                onOpenLanguageSelector = {
                    showLanguageDialog = true
                    coroutineScope.launch { drawerState.close() }
                },
                onSignOut = {
                    Firebase.auth.signOut()
                    coroutineScope.launch {
                        try {
                            CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
                        } catch (e: Exception) {
                            // Ignored
                        }
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        when (currentDestination) {
            AppDestination.HOME -> {
                ClaudeHomeScreen(
                    persistentLogCount = persistentLogs.size,
                    syncState = syncState,
                    pendingSyncCount = pendingCount,
                    cacheHits = cacheStats.totalHits,
                    cacheLatencySavedMs = cacheStats.estimatedLatencySavedMs,
                    onNavigateToChats = { currentDestination = AppDestination.CHATS },
                    onNavigateToProjects = { currentDestination = AppDestination.PROJECTS },
                    onNavigateToCode = { currentDestination = AppDestination.CODE },
                    onNavigateToArtifacts = { currentDestination = AppDestination.ARTIFACTS },
                    onNavigateToAppBuilder = { currentDestination = AppDestination.APP_BUILDER },
                    onNavigateToVideoStudio = { currentDestination = AppDestination.VIDEO },
                    onNavigateToPersistentLogs = { currentDestination = AppDestination.AGENTIC },
                    onNavigateToExternalHub = { currentDestination = AppDestination.EXTERNAL_HUB },
                    onNavigateToAcademy = { currentDestination = AppDestination.HACKING_ACADEMY },
                    onOpenLanguageSelector = { showLanguageDialog = true },
                    onOpenAuth = { showAuthScreen = true },
                    onSelectRecentTopic = { topic ->
                        currentDestination = AppDestination.CHATS
                        chatViewModel.sendMessage(topic)
                    },
                    onNewChat = {
                        currentDestination = AppDestination.CHATS
                        chatViewModel.clearChat()
                    }
                )
            }
            AppDestination.CHATS -> {
                ChatScreen(
                    chatViewModel = chatViewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onNavigateToTerminal = { cmd ->
                        currentDestination = AppDestination.CODE
                        terminalViewModel.runCommand(cmd)
                    }
                )
            }

            AppDestination.HACKING_ACADEMY -> {
                EthicalHackingAcademyScreen(
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onRunInTerminal = { cmd ->
                        currentDestination = AppDestination.CODE
                        terminalViewModel.runCommand(cmd)
                    },
                    onAskInChat = { prompt ->
                        currentDestination = AppDestination.CHATS
                        chatViewModel.sendMessage(prompt)
                    }
                )
            }

            AppDestination.EXTERNAL_HUB -> {
                TermuxYouTubeHubScreen(
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onNavigateToChatWithQuery = { query ->
                        currentDestination = AppDestination.CHATS
                        chatViewModel.sendMessage(query)
                    }
                )
            }

            AppDestination.AGENTIC -> {
                AgenticWorkflowScreen(
                    agenticWorkflowViewModel = agenticWorkflowViewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
            }

            AppDestination.APP_BUILDER -> {
                AppBuilderScreen(
                    appBuilderViewModel = appBuilderViewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
            }

            AppDestination.FILE_ANALYZER -> {
                FileAnalyzerScreen(
                    fileAnalyzerViewModel = fileAnalyzerViewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
            }

            AppDestination.CODE -> {
                if (showCodeStudioSubView) {
                    CodeStudioScreen(
                        codeStudioViewModel = codeStudioViewModel,
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onSendToSandbox = { cmd ->
                            showCodeStudioSubView = false
                            terminalViewModel.runCommand(cmd)
                        }
                    )
                } else {
                    TerminalScreen(
                        terminalViewModel = terminalViewModel,
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onAskAiAboutOutput = { prompt ->
                            currentDestination = AppDestination.CHATS
                            chatViewModel.sendMessage(prompt)
                        }
                    )
                }
            }

            AppDestination.VIDEO -> {
                VideoStudioScreen(
                    videoStudioViewModel = videoStudioViewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
            }

            AppDestination.PROJECTS -> {
                ProjectsScreen(
                    projectsViewModel = projectsViewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onOpenLabInTerminal = { snippet ->
                        currentDestination = AppDestination.CODE
                        terminalViewModel.runCommand(snippet)
                    }
                )
            }

            AppDestination.ARTIFACTS -> {
                ArtifactsScreen(
                    projectsViewModel = projectsViewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onRunInSandbox = { cmd ->
                        currentDestination = AppDestination.CODE
                        terminalViewModel.runCommand(cmd)
                    }
                )
            }
        }
    }
}
