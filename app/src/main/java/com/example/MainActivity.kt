package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppLanguage
import com.example.data.AspirantProfile
import com.example.data.UserRole
import com.example.ui.BottomNavTab
import com.example.ui.WbpViewModel
import com.example.ui.screens.AdminControlPanelScreen
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.AspirantProfileDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DualModeAuthScreen
import com.example.ui.screens.MockTestScreen
import com.example.ui.screens.PetTrackerScreen
import com.example.ui.screens.SyllabusScreen
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonWrong
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val wbpViewModel: WbpViewModel = viewModel()
            val isDark by wbpViewModel.isDarkMode.collectAsStateWithLifecycle()
            val view = LocalView.current

            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as? Activity)?.window ?: return@SideEffect
                    val insetsController = WindowCompat.getInsetsController(window, view)
                    insetsController.isAppearanceLightStatusBars = !isDark
                    insetsController.isAppearanceLightNavigationBars = !isDark
                }
            }

            MyApplicationTheme(darkTheme = isDark, dynamicColor = false) {
                WbpPrepApp(wbpViewModel = wbpViewModel)
            }
        }
    }
}

@Composable
fun WbpPrepApp(wbpViewModel: WbpViewModel) {
    val language by wbpViewModel.language.collectAsStateWithLifecycle()
    val isDark by wbpViewModel.isDarkMode.collectAsStateWithLifecycle()
    val selectedExam by wbpViewModel.selectedExam.collectAsStateWithLifecycle()
    val dailyStreak by wbpViewModel.dailyStreak.collectAsStateWithLifecycle()
    val currentTab by wbpViewModel.currentTab.collectAsStateWithLifecycle()

    val userRole by wbpViewModel.userRole.collectAsStateWithLifecycle()
    val currentAspirant by wbpViewModel.currentAspirant.collectAsStateWithLifecycle()
    val registeredAspirants by wbpViewModel.registeredAspirants.collectAsStateWithLifecycle()
    val authErrorMessage by wbpViewModel.authErrorMessage.collectAsStateWithLifecycle()
    val adminPasskey by wbpViewModel.adminPasskey.collectAsStateWithLifecycle()
    val cloudDbConfig by wbpViewModel.cloudDbConfig.collectAsStateWithLifecycle()
    val isSyncingCloud by wbpViewModel.isSyncingCloud.collectAsStateWithLifecycle()

    val syllabusTopics by wbpViewModel.syllabusTopics.collectAsStateWithLifecycle()
    val mockResults by wbpViewModel.mockResults.collectAsStateWithLifecycle()
    val petRunLogs by wbpViewModel.petRunLogs.collectAsStateWithLifecycle()
    val chatMessages by wbpViewModel.chatMessages.collectAsStateWithLifecycle()
    val mistakeNotebook by wbpViewModel.mistakeNotebook.collectAsStateWithLifecycle()
    val bookmarkedQuestionIds by wbpViewModel.bookmarkedQuestionIds.collectAsStateWithLifecycle()
    val questionVault by wbpViewModel.questionVault.collectAsStateWithLifecycle()
    val seenQuestionIds by wbpViewModel.seenQuestionIds.collectAsStateWithLifecycle()
    val bulkGeneratingTarget by wbpViewModel.bulkGeneratingTarget.collectAsStateWithLifecycle()
    val isImportingPyqBook by wbpViewModel.isImportingPyqBook.collectAsStateWithLifecycle()
    val scannerCurrentPage by wbpViewModel.scannerCurrentPage.collectAsStateWithLifecycle()
    val scannerTotalPages by wbpViewModel.scannerTotalPages.collectAsStateWithLifecycle()
    val scannerAddedCount by wbpViewModel.scannerAddedCount.collectAsStateWithLifecycle()
    val scannerExtractedCount by wbpViewModel.scannerExtractedCount.collectAsStateWithLifecycle()
    val scannerStep by wbpViewModel.scannerStep.collectAsStateWithLifecycle()
    val scannerErrorMessage by wbpViewModel.scannerErrorMessage.collectAsStateWithLifecycle()
    val scannerVerificationReport by wbpViewModel.scannerVerificationReport.collectAsStateWithLifecycle()
    val scannerToastMessage by wbpViewModel.scannerToastMessage.collectAsStateWithLifecycle()
    val isGeneratingExplanation by wbpViewModel.isGeneratingExplanation.collectAsStateWithLifecycle()

    val questions by wbpViewModel.questions.collectAsStateWithLifecycle()
    val currentQuestionIndex by wbpViewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val selectedAnswers by wbpViewModel.selectedAnswers.collectAsStateWithLifecycle()
    val markedForReview by wbpViewModel.markedForReview.collectAsStateWithLifecycle()
    val questionTimeSpent by wbpViewModel.questionTimeSpent.collectAsStateWithLifecycle()
    val isTestSubmitted by wbpViewModel.isTestSubmitted.collectAsStateWithLifecycle()
    val isLiveExamActive by wbpViewModel.isLiveExamActive.collectAsStateWithLifecycle()
    val isExamPaused by wbpViewModel.isExamPaused.collectAsStateWithLifecycle()
    val activeTestTitleEn by wbpViewModel.activeTestTitleEn.collectAsStateWithLifecycle()
    val activeTestTitleBn by wbpViewModel.activeTestTitleBn.collectAsStateWithLifecycle()
    val completedTestScores by wbpViewModel.completedTestScores.collectAsStateWithLifecycle()
    val generatingSubjectSectional by wbpViewModel.generatingSubjectSectional.collectAsStateWithLifecycle()
    val remainingSeconds by wbpViewModel.remainingSeconds.collectAsStateWithLifecycle()
    val totalDurationSeconds by wbpViewModel.totalDurationSeconds.collectAsStateWithLifecycle()
    val isGeneratingAiMock by wbpViewModel.isGeneratingAiMock.collectAsStateWithLifecycle()
    val mockStatusBanner by wbpViewModel.mockStatusBanner.collectAsStateWithLifecycle()
    val isAiTutorLoading by wbpViewModel.isAiTutorLoading.collectAsStateWithLifecycle()
    val selectedDistrict by wbpViewModel.selectedDistrict.collectAsStateWithLifecycle()
    val interviewTurns by wbpViewModel.interviewTurns.collectAsStateWithLifecycle()
    val latestInterviewScore by wbpViewModel.latestInterviewScore.collectAsStateWithLifecycle()
    val isInterviewLoading by wbpViewModel.isInterviewLoading.collectAsStateWithLifecycle()
    val revisedPocketCardIds by wbpViewModel.revisedPocketCardIds.collectAsStateWithLifecycle()

    var openAiTutorInInterviewMode by remember { mutableStateOf(false) }
    var openSyllabusInFlashcardsMode by remember { mutableStateOf(false) }
    var showAspirantProfileDialog by remember { mutableStateOf(false) }

    // 1. DUAL-MODE AUTHENTICATION GATE: Show Login / Sign-Up screen when unauthenticated
    if (userRole == UserRole.UNAUTHENTICATED) {
        DualModeAuthScreen(
            authErrorMessage = authErrorMessage,
            onClearError = { wbpViewModel.clearAuthError() },
            onLoginAspirant = { email, password ->
                wbpViewModel.loginAspirant(email, password)
            },
            onSignUpAspirant = { fullName, email, password, category, targetExam ->
                wbpViewModel.signUpAspirant(fullName, email, password, category, targetExam)
            },
            onQuickDemoAspirantLogin = {
                wbpViewModel.quickDemoAspirantLogin()
            },
            onLoginAdmin = { email, passkey ->
                wbpViewModel.loginAdmin(email, passkey)
            },
            onSignInWithGoogleIdToken = { idToken, displayName, email, category, targetExam ->
                wbpViewModel.signInWithGoogleIdToken(idToken, displayName, email, category, targetExam)
            }
        )
        return
    }

    val isAdmin = userRole == UserRole.ADMIN
    val isBn = language == AppLanguage.BENGALI
    val isFullScreenExamOverlay = currentTab == BottomNavTab.MOCK_TEST && isLiveExamActive

    // Aspirant Profile Edit / Logout Modal (with 1-Tap Switch to Admin Studio)
    if (showAspirantProfileDialog && currentAspirant != null) {
        AspirantProfileDialog(
            aspirant = currentAspirant!!,
            onDismiss = { showAspirantProfileDialog = false },
            onUpdateProfile = { name, cat, exam ->
                wbpViewModel.updateCurrentAspirantProfile(name, cat, exam)
            },
            onSwitchToAdminOneTap = {
                showAspirantProfileDialog = false
                wbpViewModel.switchRoleOneTap()
            },
            onLogout = {
                showAspirantProfileDialog = false
                wbpViewModel.logoutUser()
            }
        )
    }

    // Mandatory BackHandler for custom tab navigation
    if (currentTab != BottomNavTab.HOME && !isFullScreenExamOverlay) {
        BackHandler {
            wbpViewModel.selectTab(BottomNavTab.HOME)
        }
    }

    val visibleNavTabs = remember(isAdmin) {
        if (isAdmin) {
            BottomNavTab.entries
        } else {
            BottomNavTab.entries.filter { it != BottomNavTab.ADMIN_PANEL }
        }
    }

    // Calculate bottom gesture-bar safe-area padding: pb-6 (min 24.dp) + pt-2 (8.dp)
    val navBarBottomInset = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
        .coerceAtLeast(24.dp)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (!isFullScreenExamOverlay) {
                WbpTopHeader(
                    isDark = isDark,
                    isAdmin = isAdmin,
                    aspirantProfile = currentAspirant,
                    onOpenProfile = { showAspirantProfileDialog = true },
                    onOpenAdminPanel = { wbpViewModel.selectTab(BottomNavTab.ADMIN_PANEL) },
                    onSwitchRoleOneTap = { wbpViewModel.switchRoleOneTap() },
                    onLogout = { wbpViewModel.logoutUser() },
                    onToggleDark = { wbpViewModel.toggleDarkMode() }
                )
            }
        },
        bottomBar = {
            if (!isFullScreenExamOverlay) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                    tonalElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = navBarBottomInset)
                    ) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                        )
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                            tonalElevation = 0.dp,
                            windowInsets = WindowInsets(0, 0, 0, 0)
                        ) {
                            visibleNavTabs.forEach { tab ->
                                val isSelected = currentTab == tab
                                val icon = when (tab) {
                                    BottomNavTab.HOME -> if (isSelected) Icons.Default.Home else Icons.Outlined.Home
                                    BottomNavTab.MOCK_TEST -> Icons.AutoMirrored.Outlined.Assignment
                                    BottomNavTab.AI_TUTOR -> if (isSelected) Icons.Default.AutoAwesome else Icons.Outlined.AutoAwesome
                                    BottomNavTab.PET_TRACKER -> if (isSelected) Icons.AutoMirrored.Filled.DirectionsRun else Icons.AutoMirrored.Outlined.DirectionsRun
                                    BottomNavTab.SYLLABUS -> Icons.AutoMirrored.Outlined.MenuBook
                                    BottomNavTab.ADMIN_PANEL -> if (isSelected) Icons.Default.AdminPanelSettings else Icons.Outlined.AdminPanelSettings
                                }
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        if (tab == BottomNavTab.AI_TUTOR) openAiTutorInInterviewMode = false
                                        if (tab == BottomNavTab.SYLLABUS) openSyllabusInFlashcardsMode = false
                                        wbpViewModel.selectTab(tab)
                                    },
                                    modifier = Modifier.testTag("nav_${tab.route}"),
                                    icon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = tab.labelEn,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = if (isBn) tab.labelBn else tab.labelEn,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = if (visibleNavTabs.size >= 6) 10.sp else 11.sp,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                            ),
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 460.dp)
            ) {
                when (currentTab) {
                    BottomNavTab.HOME -> DashboardScreen(
                        language = language,
                        selectedExam = selectedExam,
                        dailyStreak = dailyStreak,
                        syllabusTopics = syllabusTopics,
                        mockResults = mockResults,
                        mistakeNotebook = mistakeNotebook,
                        questionVault = questionVault,
                        seenQuestionIds = seenQuestionIds,
                        bulkGeneratingTarget = bulkGeneratingTarget,
                        isAdmin = isAdmin,
                        aspirantProfile = currentAspirant,
                        onOpenProfileDialog = { showAspirantProfileDialog = true },
                        onLogoutUser = { wbpViewModel.logoutUser() },
                        onSelectExam = { wbpViewModel.selectExam(it) },
                        onStartQuickQuiz = { wbpViewModel.startQuickDailyQuiz() },
                        onStartRetryMistakes = { wbpViewModel.startRetryMistakesQuiz() },
                        onRemoveMistake = { wbpViewModel.removeFromMistakeNotebook(it) },
                        onStartSubjectPractice = { wbpViewModel.startFullPracticeSet(it) },
                        onBulkAdd50Questions = { subjectId ->
                            wbpViewModel.bulkAdd50QuestionsToVault(subjectId, null)
                        },
                        onOpenSyllabus = {
                            openSyllabusInFlashcardsMode = false
                            wbpViewModel.selectTab(BottomNavTab.SYLLABUS)
                        },
                        onOpenFlashcards = {
                            openSyllabusInFlashcardsMode = true
                            wbpViewModel.selectTab(BottomNavTab.SYLLABUS)
                        },
                        onOpenMockInterview = {
                            openAiTutorInInterviewMode = true
                            wbpViewModel.selectTab(BottomNavTab.AI_TUTOR)
                        },
                        onOpenPetStopwatch = {
                            wbpViewModel.selectTab(BottomNavTab.PET_TRACKER)
                        }
                    )

                    BottomNavTab.MOCK_TEST -> MockTestScreen(
                        language = language,
                        questions = questions,
                        currentIndex = currentQuestionIndex,
                        selectedAnswers = selectedAnswers,
                        markedForReview = markedForReview,
                        bookmarkedIds = bookmarkedQuestionIds,
                        mistakeNotebook = mistakeNotebook,
                        questionTimeSpent = questionTimeSpent,
                        isSubmitted = isTestSubmitted,
                        isLiveExamActive = isLiveExamActive,
                        isExamPaused = isExamPaused,
                        activeTestTitleEn = activeTestTitleEn,
                        activeTestTitleBn = activeTestTitleBn,
                        completedTestScores = completedTestScores,
                        generatingSubjectSectional = generatingSubjectSectional,
                        remainingSeconds = remainingSeconds,
                        totalDurationSeconds = totalDurationSeconds,
                        isGeneratingAiMock = isGeneratingAiMock,
                        statusBanner = mockStatusBanner,
                        questionVault = questionVault,
                        seenQuestionIds = seenQuestionIds,
                        bulkGeneratingTarget = bulkGeneratingTarget,
                        isImportingPyqBook = isImportingPyqBook,
                        scannerCurrentPage = scannerCurrentPage,
                        scannerTotalPages = scannerTotalPages,
                        scannerAddedCount = scannerAddedCount,
                        scannerExtractedCount = scannerExtractedCount,
                        scannerStep = scannerStep,
                        scannerErrorMessage = scannerErrorMessage,
                        scannerVerificationReport = scannerVerificationReport,
                        scannerToastMessage = scannerToastMessage,
                        isAdmin = isAdmin,
                        aspirantProfile = currentAspirant,
                        getPaletteStatus = { wbpViewModel.getPaletteStatus(it) },
                        onSelectQuestion = { wbpViewModel.goToQuestion(it) },
                        onSelectOption = { qId, opt -> wbpViewModel.selectOmrOption(qId, opt) },
                        onClearOption = { wbpViewModel.clearOmrOption(it) },
                        onToggleReview = { wbpViewModel.toggleMarkForReview(it) },
                        onToggleBookmark = { wbpViewModel.toggleBookmark(it) },
                        onSaveAndNext = { wbpViewModel.saveAndNextQuestion() },
                        onStartTestSeriesItem = { wbpViewModel.startTestSeriesItem(it) },
                        onViewSolutionsForTestSeriesItem = { wbpViewModel.viewSolutionsForTestSeriesItem(it) },
                        onGenerateAiSectionalTest = { wbpViewModel.generateAiSectionalTest(it) },
                        onBulkAdd50Questions = { subjectId, chapterId, chapterTitle ->
                            wbpViewModel.bulkAdd50QuestionsToVault(subjectId, chapterId, chapterTitle)
                        },
                        onImportPyqBookContent = { rawText, base64Doc, docMime, subjectHint ->
                            wbpViewModel.importPyqBookContent(rawText, base64Doc, docMime, subjectHint)
                        },
                        onScanSmartBatch = { rawTextOrUrl, attachments, declaredPageCount, subjectHint ->
                            wbpViewModel.scanAndIngestSmartQuestionsBatch(
                                rawTextOrUrl = rawTextOrUrl,
                                attachments = attachments,
                                declaredPageCount = declaredPageCount,
                                subjectHint = subjectHint
                            )
                        },
                        onClearScannerToast = { wbpViewModel.clearScannerToastMessage() },
                        onClearScannerError = { wbpViewModel.clearScannerErrorMessage() },
                        onNotifyScannerInputLoaded = { wbpViewModel.notifyScannerInputLoaded() },
                        onUpdateUploadedQuestion = { wbpViewModel.updateUploadedQuestion(it) },
                        onDeleteUploadedQuestion = { wbpViewModel.deleteUploadedQuestion(it) },
                        onStartUploadedQuestionsTest = { uploadedList ->
                            wbpViewModel.startUploadedQuestionsQuiz(uploadedList)
                        },
                        isGeneratingExplanation = isGeneratingExplanation,
                        onSaveManualBatch = { batchList ->
                            wbpViewModel.saveManualQuestionsBatch(batchList)
                        },
                        onExportDatabaseJson = { callback ->
                            wbpViewModel.exportDatabaseBackupJson(callback)
                        },
                        onImportDatabaseJson = { rawJson ->
                            wbpViewModel.importDatabaseBackupJson(rawJson)
                        },
                        onAutoGenerateExplanation = { subject, chapter, questionBn, optionsBn, correctIndex, onResult ->
                            wbpViewModel.autoGenerateBengaliExplanation(
                                subject = subject,
                                chapter = chapter,
                                questionText = questionBn,
                                options = optionsBn,
                                correctIndex = correctIndex,
                                onResult = onResult
                            )
                        },
                        onStartChapterTest = { wbpViewModel.startChapterPracticeTest(it) },
                        onStartExamGroupTest = { group, subjectFilter ->
                            wbpViewModel.startPyqExamGroupTest(group.id, group.labelEn, group.labelBn, subjectFilter)
                        },
                        onResetSeenHistory = { wbpViewModel.resetSeenQuestionsHistory() },
                        onToggleExamPause = { wbpViewModel.toggleExamPause() },
                        onExitLiveExamToHub = { wbpViewModel.exitLiveExamToHub() },
                        onResumeActiveLiveExam = { wbpViewModel.resumeOrOpenActiveLiveExam() },
                        onRetryMistakes = { wbpViewModel.startRetryMistakesQuiz() },
                        onRemoveMistake = { wbpViewModel.removeFromMistakeNotebook(it) },
                        onSubmitTest = { wbpViewModel.submitMockTest() },
                        onGenerateAiMock = { wbpViewModel.generateNewAiMockTest() },
                        onRestartFullMock = { wbpViewModel.startFullPracticeSet(null) },
                        onStartWeakChapterDrill = { wbpViewModel.startWeakChapterRecoveryDrill() },
                        onAskAiWeakTopic = { prompt ->
                            openAiTutorInInterviewMode = false
                            wbpViewModel.sendTutorQuestion(prompt)
                            wbpViewModel.selectTab(BottomNavTab.AI_TUTOR)
                        }
                    )

                    BottomNavTab.AI_TUTOR -> AiTutorScreen(
                        language = language,
                        messages = chatMessages,
                        isLoading = isAiTutorLoading,
                        onSendPrompt = { wbpViewModel.sendTutorQuestion(it) },
                        onSendPhotoPrompt = { bmp, caption -> wbpViewModel.sendTutorPhotoQuestion(bmp, caption) },
                        onClearChat = { wbpViewModel.clearTutorChat() },
                        onOpenMockTestTab = { wbpViewModel.selectTab(BottomNavTab.MOCK_TEST) },
                        selectedDistrict = selectedDistrict,
                        interviewTurns = interviewTurns,
                        latestInterviewScore = latestInterviewScore,
                        isInterviewLoading = isInterviewLoading,
                        onSelectDistrict = { wbpViewModel.selectHomeDistrict(it) },
                        onSubmitInterviewAnswer = { wbpViewModel.submitInterviewAnswer(it) },
                        onResetInterview = { wbpViewModel.resetInterviewForDistrict() },
                        initialInterviewMode = openAiTutorInInterviewMode,
                        onOpenPetTrackerTab = { wbpViewModel.selectTab(BottomNavTab.PET_TRACKER) }
                    )

                    BottomNavTab.PET_TRACKER -> PetTrackerScreen(
                        language = language,
                        petRunLogs = petRunLogs,
                        aspirantProfile = currentAspirant,
                        onSaveRun = { post, gender, dist, time, target, qual, pace ->
                            wbpViewModel.savePetRun(post, gender, dist, time, target, qual, pace)
                        },
                        onDeleteRun = { wbpViewModel.deletePetRun(it) }
                    )

                    BottomNavTab.SYLLABUS -> SyllabusScreen(
                        language = language,
                        syllabusTopics = syllabusTopics,
                        onToggleTopic = { wbpViewModel.toggleSyllabusItem(it) },
                        persistedRevisedIds = revisedPocketCardIds,
                        onToggleCardRevised = { wbpViewModel.togglePocketCardRevised(it) },
                        initialFlashcardsMode = openSyllabusInFlashcardsMode,
                        questionVault = questionVault,
                        seenQuestionIds = seenQuestionIds,
                        bulkGeneratingTarget = bulkGeneratingTarget,
                        isAdmin = isAdmin,
                        onStartChapterTest = { chapter ->
                            wbpViewModel.startChapterPracticeTest(chapter)
                        },
                        onBulkAdd50ForChapter = { chapter ->
                            wbpViewModel.bulkAdd50QuestionsToVault(chapter.subject, chapter.chapterId, chapter.titleEn)
                        }
                    )

                    BottomNavTab.ADMIN_PANEL -> AdminControlPanelScreen(
                        questionVault = questionVault,
                        registeredAspirants = registeredAspirants,
                        cloudDbConfig = cloudDbConfig,
                        isSyncingCloud = isSyncingCloud,
                        adminPasskey = adminPasskey,
                        isImportingPyqBook = isImportingPyqBook,
                        scannerCurrentPage = scannerCurrentPage,
                        scannerTotalPages = scannerTotalPages,
                        scannerAddedCount = scannerAddedCount,
                        scannerExtractedCount = scannerExtractedCount,
                        scannerStep = scannerStep,
                        scannerErrorMessage = scannerErrorMessage,
                        scannerVerificationReport = scannerVerificationReport,
                        scannerToastMessage = scannerToastMessage,
                        isGeneratingExplanation = isGeneratingExplanation,
                        onClearScannerToast = { wbpViewModel.clearScannerToastMessage() },
                        onClearScannerError = { wbpViewModel.clearScannerErrorMessage() },
                        onNotifyScannerInputLoaded = { wbpViewModel.notifyScannerInputLoaded() },
                        onUpdateUploadedQuestion = { wbpViewModel.updateUploadedQuestion(it) },
                        onDeleteUploadedQuestion = { wbpViewModel.deleteUploadedQuestion(it) },
                        onStartUploadedQuestionsTest = { uploadedList ->
                            wbpViewModel.startUploadedQuestionsQuiz(uploadedList)
                            wbpViewModel.selectTab(BottomNavTab.MOCK_TEST)
                        },
                        onImportPyqBookContent = { rawText, base64Doc, docMime, subjectHint ->
                            wbpViewModel.importPyqBookContent(rawText, base64Doc, docMime, subjectHint)
                        },
                        onScanSmartBatch = { rawTextOrUrl, attachments, declaredPageCount, subjectHint ->
                            wbpViewModel.scanAndIngestSmartQuestionsBatch(
                                rawTextOrUrl = rawTextOrUrl,
                                attachments = attachments,
                                declaredPageCount = declaredPageCount,
                                subjectHint = subjectHint
                            )
                        },
                        onSaveManualBatch = { batchList ->
                            wbpViewModel.saveManualQuestionsBatch(batchList)
                        },
                        onAutoGenerateExplanation = { subject, chapter, questionBn, optionsBn, correctIndex, onResult ->
                            wbpViewModel.autoGenerateBengaliExplanation(
                                subject = subject,
                                chapter = chapter,
                                questionText = questionBn,
                                options = optionsBn,
                                correctIndex = correctIndex,
                                onResult = onResult
                            )
                        },
                        onExportDatabaseJson = { callback ->
                            wbpViewModel.exportDatabaseBackupJson(callback)
                        },
                        onImportDatabaseJson = { rawJson ->
                            wbpViewModel.importDatabaseBackupJson(rawJson)
                        },
                        onSaveCloudConfig = { cfg ->
                            wbpViewModel.saveCloudDbConfig(cfg)
                        },
                        onTriggerCloudSync = {
                            wbpViewModel.triggerCloudDatabaseSync()
                        },
                        onUpdateAdminPasskey = { newPass ->
                            wbpViewModel.updateAdminPasskey(newPass)
                        },
                        onDeleteAspirant = { email ->
                            wbpViewModel.deleteAspirantByEmail(email)
                        },
                        onLogoutAdmin = {
                            wbpViewModel.logoutUser()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WbpTopHeader(
    isDark: Boolean,
    isAdmin: Boolean,
    aspirantProfile: AspirantProfile?,
    onOpenProfile: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    onSwitchRoleOneTap: () -> Unit,
    onLogout: () -> Unit,
    onToggleDark: () -> Unit
) {
    // Top safe-area offset: max(statusBarInset, 40.dp) -> pt-10 pb-3 px-4
    val topSafeClearance = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()
        .coerceAtLeast(40.dp)

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = topSafeClearance,
                        bottom = 12.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Minimal App Logo & Title (100% English UI)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(34.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "WBP Prep Logo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = "WBP & KP Prep",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Right: Role Badge + 1-Tap Role Switcher + Dark Mode Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isAdmin) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { onOpenAdminPanel() }
                                .testTag("header_admin_mode_badge"),
                            color = AmberWarning.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier
                                    .border(1.dp, AmberWarning.copy(alpha = 0.55f), RoundedCornerShape(50))
                                    .padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Studio",
                                    tint = AmberWarning,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "ADMIN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberWarning
                                )
                            }
                        }

                        // 1-Tap Switch to Aspirant Mode
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { onSwitchRoleOneTap() }
                                .testTag("header_one_tap_role_switcher"),
                            color = EmeraldSuccess.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "1-Tap Aspirant",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess,
                                modifier = Modifier
                                    .border(1.dp, EmeraldSuccess.copy(alpha = 0.45f), RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .clickable { onLogout() }
                                .testTag("header_logout_button"),
                            shape = CircleShape,
                            color = CrimsonWrong.copy(alpha = 0.12f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Logout Admin",
                                    tint = CrimsonWrong,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else if (aspirantProfile != null) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { onOpenProfile() }
                                .testTag("header_aspirant_profile_chip"),
                            color = EmeraldSuccess.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier
                                    .border(1.dp, EmeraldSuccess.copy(alpha = 0.45f), RoundedCornerShape(50))
                                    .padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Aspirant Profile",
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${aspirantProfile.fullName.substringBefore(" ")} • ${aspirantProfile.resolvedCasteCategory.shortBadge}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                            }
                        }

                        // 1-Tap Switch to Admin Studio
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { onSwitchRoleOneTap() }
                                .testTag("header_one_tap_role_switcher"),
                            color = AmberWarning.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "🛠️ Admin",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AmberWarning,
                                modifier = Modifier
                                    .border(1.dp, AmberWarning.copy(alpha = 0.5f), RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onToggleDark() }
                            .testTag("theme_toggle_button"),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                .padding(7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Dark/Light Theme",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.65f)
        )
    }
}
