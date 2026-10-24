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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DirectionsRun
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
import androidx.compose.material3.minimumInteractiveComponentSize
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
import com.example.ui.BottomNavTab
import com.example.ui.WbpViewModel
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MockTestScreen
import com.example.ui.screens.PetTrackerScreen
import com.example.ui.screens.SyllabusScreen
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

    val isBn = language == AppLanguage.BENGALI
    val isFullScreenExamOverlay = currentTab == BottomNavTab.MOCK_TEST && isLiveExamActive

    // Mandatory BackHandler for custom tab navigation
    if (currentTab != BottomNavTab.HOME && !isFullScreenExamOverlay) {
        BackHandler {
            wbpViewModel.selectTab(BottomNavTab.HOME)
        }
    }

    // Calculate bottom gesture-bar safe-area padding: max(env(safe-area-inset-bottom, 0px), 16.dp)
    val navBarBottomInset = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
        .coerceAtLeast(16.dp)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (!isFullScreenExamOverlay) {
                WbpTopHeader(
                    language = language,
                    isDark = isDark,
                    onToggleLanguage = { wbpViewModel.toggleLanguage() },
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
                            .padding(top = 6.dp, bottom = navBarBottomInset)
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
                            BottomNavTab.entries.forEach { tab ->
                                val isSelected = currentTab == tab
                                val icon = when (tab) {
                                    BottomNavTab.HOME -> if (isSelected) Icons.Default.Home else Icons.Outlined.Home
                                    BottomNavTab.MOCK_TEST -> Icons.AutoMirrored.Outlined.Assignment
                                    BottomNavTab.AI_TUTOR -> if (isSelected) Icons.Default.AutoAwesome else Icons.Outlined.AutoAwesome
                                    BottomNavTab.PET_TRACKER -> if (isSelected) Icons.Default.DirectionsRun else Icons.Outlined.DirectionsRun
                                    BottomNavTab.SYLLABUS -> Icons.AutoMirrored.Outlined.MenuBook
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
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                            )
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
                    .widthIn(max = 500.dp)
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
                        initialInterviewMode = openAiTutorInInterviewMode
                    )

                    BottomNavTab.PET_TRACKER -> PetTrackerScreen(
                        language = language,
                        petRunLogs = petRunLogs,
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
                        initialFlashcardsMode = openSyllabusInFlashcardsMode
                    )
                }
            }
        }
    }
}

@Composable
private fun WbpTopHeader(
    language: AppLanguage,
    isDark: Boolean,
    onToggleLanguage: () -> Unit,
    onToggleDark: () -> Unit
) {
    // Top safe-area offset: max(statusBarInset, 40.dp) so header items sit cleanly below the Android status bar
    val topSafeClearance = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()
        .coerceAtLeast(40.dp)

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = topSafeClearance,
                        bottom = 12.dp,
                        start = 16.dp,
                        end = 20.dp // 4.dp extra inward clearance from the right curved screen edge
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Minimal App Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                        text = if (language == AppLanguage.BENGALI) "WBP ও KP প্রস্তুতি" else "WBP & KP Prep",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Right: Enlarged Touch Targets for বাংলা / EN Pill Toggle + Dark Mode Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(50))
                            .clickable { onToggleLanguage() }
                            .testTag("language_toggle_button"),
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "বাংলা",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (language == AppLanguage.BENGALI) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (language == AppLanguage.BENGALI) FontWeight.Bold else FontWeight.Medium
                            )
                            Text(
                                text = "/",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "EN",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (language == AppLanguage.ENGLISH) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (language == AppLanguage.ENGLISH) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { onToggleDark() }
                            .testTag("theme_toggle_button"),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Dark/Light Theme",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
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
