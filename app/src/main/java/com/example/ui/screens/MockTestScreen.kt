package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.MistakeNotebookEntity
import com.example.data.QuestionPaletteStatus
import com.example.data.QuizQuestion
import com.example.data.SubjectCategory
import com.example.data.TestSeriesFilter
import com.example.data.TestSeriesItem
import com.example.data.WbpSampleData
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonWrong
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PurpleReview
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

private enum class SolutionFilter(val labelEn: String, val labelBn: String) {
    ALL("All", "সব (All)"),
    INCORRECT("Incorrect", "ভুল উত্তর (Incorrect)"),
    UNATTEMPTED("Unattempted", "অনুত্তরিত (Unattempted)"),
    BOOKMARKED("Bookmarked", "বুকমার্ক করা (Bookmarked)")
}

@Composable
fun MockTestScreen(
    language: AppLanguage,
    questions: List<QuizQuestion>,
    questionVault: List<QuizQuestion> = emptyList(),
    seenQuestionIds: Set<String> = emptySet(),
    bulkGeneratingTarget: String? = null,
    isAdmin: Boolean = false,
    aspirantProfile: com.example.data.AspirantProfile? = null,
    isImportingPyqBook: Boolean = false,
    scannerCurrentPage: Int = 0,
    scannerTotalPages: Int = 0,
    scannerAddedCount: Int = 0,
    scannerExtractedCount: Int = 0,
    scannerStep: Int = 0,
    scannerErrorMessage: String? = null,
    scannerVerificationReport: com.example.data.ScanVerificationReport? = null,
    scannerToastMessage: String? = null,
    currentIndex: Int,
    selectedAnswers: Map<String, Int>,
    markedForReview: Set<String>,
    bookmarkedIds: Set<String>,
    mistakeNotebook: List<MistakeNotebookEntity>,
    questionTimeSpent: Map<String, Int>,
    isSubmitted: Boolean,
    isLiveExamActive: Boolean = false,
    isExamPaused: Boolean = false,
    activeTestTitleEn: String = "WBP Constable Full Length Mock Test",
    activeTestTitleBn: String = "WBP কনস্টেবল ফুল লেন্থ মক টেস্ট",
    completedTestScores: Map<String, Pair<Double, Int>> = emptyMap(),
    generatingSubjectSectional: String? = null,
    remainingSeconds: Int,
    totalDurationSeconds: Int,
    isGeneratingAiMock: Boolean,
    statusBanner: String?,
    getPaletteStatus: (String) -> QuestionPaletteStatus,
    onSelectQuestion: (Int) -> Unit,
    onSelectOption: (String, Int) -> Unit,
    onClearOption: (String) -> Unit,
    onToggleReview: (String) -> Unit,
    onToggleBookmark: (QuizQuestion) -> Unit,
    onSaveAndNext: () -> Unit = {},
    onStartTestSeriesItem: (TestSeriesItem) -> Unit = {},
    onViewSolutionsForTestSeriesItem: (TestSeriesItem) -> Unit = {},
    onGenerateAiSectionalTest: (String) -> Unit = {},
    onBulkAdd50Questions: (String, String?, String?) -> Unit = { _, _, _ -> },
    onImportPyqBookContent: (String, String?, String?, String) -> Unit = { _, _, _, _ -> },
    onScanSmartBatch: (String, List<com.example.network.GeminiClient.ScannerInlineAttachment>, Int, String?) -> Unit = { _, _, _, _ -> },
    onClearScannerToast: () -> Unit = {},
    onClearScannerError: () -> Unit = {},
    onNotifyScannerInputLoaded: () -> Unit = {},
    onUpdateUploadedQuestion: (QuizQuestion) -> Unit = {},
    onDeleteUploadedQuestion: (String) -> Unit = {},
    onStartUploadedQuestionsTest: (List<QuizQuestion>) -> Unit = {},
    isGeneratingExplanation: Boolean = false,
    onSaveManualBatch: (List<QuizQuestion>) -> Unit = {},
    onExportDatabaseJson: ((String) -> Unit) -> Unit = {},
    onImportDatabaseJson: (String) -> Unit = {},
    onAutoGenerateExplanation: (String, String, String, List<String>, Int, (String) -> Unit) -> Unit = { _, _, _, _, _, _ -> },
    onStartChapterTest: (com.example.data.WbpChapterSpec) -> Unit = {},
    onStartExamGroupTest: (com.example.data.WbpExamHistoryGroup, String?) -> Unit = { _, _ -> },
    onResetSeenHistory: () -> Unit = {},
    onToggleExamPause: () -> Unit = {},
    onExitLiveExamToHub: () -> Unit = {},
    onResumeActiveLiveExam: () -> Unit = {},
    onRetryMistakes: () -> Unit,
    onRemoveMistake: (String) -> Unit,
    onSubmitTest: () -> Unit,
    onGenerateAiMock: () -> Unit,
    onRestartFullMock: () -> Unit,
    onStartWeakChapterDrill: () -> Unit = {},
    onAskAiWeakTopic: (String) -> Unit = {}
) {
    if (isLiveExamActive) {
        TestbookLiveExamView(
            language = language,
            questions = questions,
            currentIndex = currentIndex,
            selectedAnswers = selectedAnswers,
            markedForReview = markedForReview,
            bookmarkedIds = bookmarkedIds,
            mistakeNotebook = mistakeNotebook,
            questionTimeSpent = questionTimeSpent,
            isSubmitted = isSubmitted,
            isExamPaused = isExamPaused,
            activeTestTitleEn = activeTestTitleEn,
            activeTestTitleBn = activeTestTitleBn,
            remainingSeconds = remainingSeconds,
            totalDurationSeconds = totalDurationSeconds,
            statusBanner = statusBanner,
            aspirantProfile = aspirantProfile,
            getPaletteStatus = getPaletteStatus,
            onSelectQuestion = onSelectQuestion,
            onSelectOption = onSelectOption,
            onClearOption = onClearOption,
            onToggleReview = onToggleReview,
            onToggleBookmark = onToggleBookmark,
            onSaveAndNext = onSaveAndNext,
            onToggleExamPause = onToggleExamPause,
            onExitLiveExamToHub = onExitLiveExamToHub,
            onRetryMistakes = onRetryMistakes,
            onSubmitTest = onSubmitTest,
            onRestartFullMock = onRestartFullMock,
            onStartWeakChapterDrill = onStartWeakChapterDrill,
            onAskAiWeakTopic = onAskAiWeakTopic
        )
    } else {
        TestSeriesHubView(
            language = language,
            questionVault = questionVault,
            seenQuestionIds = seenQuestionIds,
            bulkGeneratingTarget = bulkGeneratingTarget,
            isAdmin = isAdmin,
            aspirantProfile = aspirantProfile,
            isImportingPyqBook = isImportingPyqBook,
            scannerCurrentPage = scannerCurrentPage,
            scannerTotalPages = scannerTotalPages,
            scannerAddedCount = scannerAddedCount,
            scannerExtractedCount = scannerExtractedCount,
            scannerStep = scannerStep,
            scannerErrorMessage = scannerErrorMessage,
            scannerVerificationReport = scannerVerificationReport,
            scannerToastMessage = scannerToastMessage,
            mistakeNotebook = mistakeNotebook,
            completedTestScores = completedTestScores,
            isGeneratingAiMock = isGeneratingAiMock,
            generatingSubjectSectional = generatingSubjectSectional,
            statusBanner = statusBanner,
            activeTestTitleEn = activeTestTitleEn,
            activeTestTitleBn = activeTestTitleBn,
            remainingSeconds = remainingSeconds,
            isSubmitted = isSubmitted,
            onStartTestSeriesItem = onStartTestSeriesItem,
            onViewSolutionsForTestSeriesItem = onViewSolutionsForTestSeriesItem,
            onGenerateAiMock = onGenerateAiMock,
            onGenerateAiSectionalTest = onGenerateAiSectionalTest,
            onBulkAdd50Questions = onBulkAdd50Questions,
            onImportPyqBookContent = onImportPyqBookContent,
            onScanSmartBatch = onScanSmartBatch,
            onClearScannerToast = onClearScannerToast,
            onClearScannerError = onClearScannerError,
            onNotifyScannerInputLoaded = onNotifyScannerInputLoaded,
            onUpdateUploadedQuestion = onUpdateUploadedQuestion,
            onDeleteUploadedQuestion = onDeleteUploadedQuestion,
            onStartUploadedQuestionsTest = onStartUploadedQuestionsTest,
            isGeneratingExplanation = isGeneratingExplanation,
            onSaveManualBatch = onSaveManualBatch,
            onExportDatabaseJson = onExportDatabaseJson,
            onImportDatabaseJson = onImportDatabaseJson,
            onAutoGenerateExplanation = onAutoGenerateExplanation,
            onStartChapterTest = onStartChapterTest,
            onStartExamGroupTest = onStartExamGroupTest,
            onResetSeenHistory = onResetSeenHistory,
            onResumeActiveLiveExam = onResumeActiveLiveExam,
            onRetryMistakes = onRetryMistakes,
            onRemoveMistake = onRemoveMistake
        )
    }
}

/**
 * Part 2: Subject-Wise Test Series Hub & WBPRB Official PYQ Vault (2005–Present) (Testbook App Style)
 * Includes horizontal filter pills: [All Tests], [Full Mock (85M)], [Subject-Wise], [PYQ Vault (2005–Present)], [Chapter-Wise Practice], [🗂️ Manage Question Database], [Mistake Retry]
 */
@Composable
private fun TestSeriesHubView(
    language: AppLanguage,
    questionVault: List<QuizQuestion>,
    seenQuestionIds: Set<String>,
    bulkGeneratingTarget: String?,
    isAdmin: Boolean = false,
    aspirantProfile: com.example.data.AspirantProfile? = null,
    isImportingPyqBook: Boolean,
    scannerCurrentPage: Int,
    scannerTotalPages: Int,
    scannerAddedCount: Int,
    scannerExtractedCount: Int,
    scannerStep: Int,
    scannerErrorMessage: String?,
    scannerVerificationReport: com.example.data.ScanVerificationReport?,
    scannerToastMessage: String?,
    mistakeNotebook: List<MistakeNotebookEntity>,
    completedTestScores: Map<String, Pair<Double, Int>>,
    isGeneratingAiMock: Boolean,
    generatingSubjectSectional: String?,
    statusBanner: String?,
    activeTestTitleEn: String,
    activeTestTitleBn: String,
    remainingSeconds: Int,
    isSubmitted: Boolean,
    onStartTestSeriesItem: (TestSeriesItem) -> Unit,
    onViewSolutionsForTestSeriesItem: (TestSeriesItem) -> Unit,
    onGenerateAiMock: () -> Unit,
    onGenerateAiSectionalTest: (String) -> Unit,
    onBulkAdd50Questions: (String, String?, String?) -> Unit,
    onImportPyqBookContent: (String, String?, String?, String) -> Unit,
    onScanSmartBatch: (String, List<com.example.network.GeminiClient.ScannerInlineAttachment>, Int, String?) -> Unit,
    onClearScannerToast: () -> Unit,
    onClearScannerError: () -> Unit,
    onNotifyScannerInputLoaded: () -> Unit,
    onUpdateUploadedQuestion: (QuizQuestion) -> Unit,
    onDeleteUploadedQuestion: (String) -> Unit,
    onStartUploadedQuestionsTest: (List<QuizQuestion>) -> Unit,
    isGeneratingExplanation: Boolean,
    onSaveManualBatch: (List<QuizQuestion>) -> Unit,
    onExportDatabaseJson: ((String) -> Unit) -> Unit,
    onImportDatabaseJson: (String) -> Unit,
    onAutoGenerateExplanation: (String, String, String, List<String>, Int, (String) -> Unit) -> Unit,
    onStartChapterTest: (com.example.data.WbpChapterSpec) -> Unit,
    onStartExamGroupTest: (com.example.data.WbpExamHistoryGroup, String?) -> Unit,
    onResetSeenHistory: () -> Unit,
    onResumeActiveLiveExam: () -> Unit,
    onRetryMistakes: () -> Unit,
    onRemoveMistake: (String) -> Unit
) {
    val isBn = language == AppLanguage.BENGALI
    var selectedFilter by remember { mutableStateOf(TestSeriesFilter.ALL) }
    var showPyqImporterModal by remember { mutableStateOf(false) }

    val allSeries = WbpSampleData.testSeriesItems
    val fullMockSeries = remember(allSeries) { allSeries.filter { it.isFullMock } }
    val sectionalSeries = remember(allSeries) { allSeries.filter { !it.isFullMock } }
    val totalVaultCount = questionVault.size
    val visibleFilters = remember(isAdmin) {
        if (isAdmin) TestSeriesFilter.entries
        else TestSeriesFilter.entries.filterNot { it == TestSeriesFilter.MY_UPLOADED }
    }

    if (showPyqImporterModal && isAdmin) {
        PyqBookImporterDialog(
            isBn = isBn,
            isImporting = isImportingPyqBook,
            scannerCurrentPage = scannerCurrentPage,
            scannerTotalPages = scannerTotalPages,
            scannerAddedCount = scannerAddedCount,
            scannerExtractedCount = scannerExtractedCount,
            scannerStep = scannerStep,
            scannerErrorMessage = scannerErrorMessage,
            scannerVerificationReport = scannerVerificationReport,
            scannerToastMessage = scannerToastMessage,
            questionVault = questionVault,
            isGeneratingExplanation = isGeneratingExplanation,
            onClearToastMessage = onClearScannerToast,
            onClearErrorMessage = onClearScannerError,
            onNotifyInputLoaded = onNotifyScannerInputLoaded,
            onUpdateUploadedQuestion = onUpdateUploadedQuestion,
            onDeleteUploadedQuestion = onDeleteUploadedQuestion,
            onStartUploadedQuestionsTest = onStartUploadedQuestionsTest,
            onDismiss = { showPyqImporterModal = false },
            onImportBookContent = onImportPyqBookContent,
            onScanSmartBatch = onScanSmartBatch,
            onSaveManualBatch = onSaveManualBatch,
            onAutoGenerateExplanation = onAutoGenerateExplanation,
            onExportDatabaseJson = onExportDatabaseJson,
            onImportDatabaseJson = onImportDatabaseJson
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Horizontal Filter Pill Bar (Manage Question Database hidden for Aspirants)
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visibleFilters) { filter ->
                    val isSelected = selectedFilter == filter
                    val baseLabel = if (isBn) filter.labelBn else filter.labelEn
                    val pillLabel = if (filter == TestSeriesFilter.MY_UPLOADED) {
                        "$baseLabel ($totalVaultCount)"
                    } else {
                        baseLabel
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { selectedFilter = filter }
                            .testTag("test_filter_${filter.id}"),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pillLabel,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Top Question Vault Master Bar ("✍️ Manual Question Studio" button visible only in Admin Mode)
        item {
            QuestionVaultMasterBar(
                isBn = isBn,
                questionVault = questionVault,
                seenQuestionIds = seenQuestionIds,
                isImportingBook = isImportingPyqBook,
                scannerCurrentPage = scannerCurrentPage,
                scannerTotalPages = scannerTotalPages,
                scannerAddedCount = scannerAddedCount,
                isAdmin = isAdmin,
                onOpenImporterModal = { showPyqImporterModal = true },
                onResetSeenHistory = onResetSeenHistory
            )
        }

        // Status / AI Generation Banner + Quick Open Live OMR Strip
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBn) "WBPRB টেস্ট সিরিজ হাব (বাংলা মাধ্যম)" else "WBPRB TEST SERIES HUB",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.7.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBn) activeTestTitleBn else activeTestTitleEn,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onResumeActiveLiveExam() }
                                .testTag("resume_live_exam_button"),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isSubmitted) {
                                        if (isBn) "ফলাফল দেখুন" else "Scorecard"
                                    } else {
                                        val m = remainingSeconds / 60
                                        val s = remainingSeconds % 60
                                        if (isBn) "চালু করুন (${m}m ${s}s)" else "Resume (${m}m)"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onGenerateAiMock,
                            enabled = !isGeneratingAiMock,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("generate_ai_mock_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            if (isGeneratingAiMock && generatingSubjectSectional == null) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBn) "তৈরি হচ্ছে..." else "Generating...",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBn) "⚡ নতুন AI ফুল মক" else "⚡ New AI Full Mock",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onRetryMistakes,
                            enabled = mistakeNotebook.isNotEmpty(),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("retry_mistakes_mock_button"),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = null,
                                tint = CrimsonWrong,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn) "ভুল রি-টেস্ট (${mistakeNotebook.size})" else "Retry Mistakes (${mistakeNotebook.size})",
                                style = MaterialTheme.typography.labelLarge,
                                color = CrimsonWrong,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (!statusBanner.isNullOrBlank()) {
                        Text(
                            text = statusBanner,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // SECTION 0: MANAGE QUESTION DATABASE (Admin Mode Only)
        if (isAdmin && (selectedFilter == TestSeriesFilter.MY_UPLOADED || selectedFilter == TestSeriesFilter.ALL)) {
            item {
                MyUploadedQuestionsVaultSection(
                    isBn = isBn,
                    uploadedQuestions = questionVault,
                    onStartUploadedTest = onStartUploadedQuestionsTest,
                    onOpenScannerModal = { showPyqImporterModal = true },
                    onUpdateUploadedQuestion = onUpdateUploadedQuestion,
                    onDeleteUploadedQuestion = onDeleteUploadedQuestion,
                    onExportDatabaseJson = onExportDatabaseJson,
                    onImportDatabaseJson = onImportDatabaseJson,
                    initiallyExpandedList = selectedFilter == TestSeriesFilter.MY_UPLOADED
                )
            }
        }

        // SECTION 1: FULL MOCK (85M) SERIES
        if (selectedFilter == TestSeriesFilter.ALL || selectedFilter == TestSeriesFilter.FULL_MOCK) {
            item {
                Text(
                    text = if (isBn) "ফুল লেন্থ মক টেস্ট সিরিজ (৮৫ নম্বর • ৬০ মিনিট)" else "FULL LENGTH MOCK TESTS (85 MARKS • 60 MINS)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
            }

            items(fullMockSeries, key = { it.id }) { testItem ->
                val savedInVault = questionVault.size
                val unseenInVault = questionVault.count { it.id !in seenQuestionIds }
                TestbookSeriesCard(
                    isBn = isBn,
                    item = testItem,
                    savedVaultCount = savedInVault,
                    unseenVaultCount = unseenInVault,
                    targetVaultCapacity = 4000,
                    completedScore = completedTestScores[testItem.id],
                    isGeneratingSectional = false,
                    isBulkAdding50 = false,
                    onStartTest = { onStartTestSeriesItem(testItem) },
                    onViewSolutions = { onViewSolutionsForTestSeriesItem(testItem) },
                    onGenerateAiSectional = null,
                    onBulkAdd50ToVault = null
                )
            }
        }

        // SECTION 2: SUBJECT-WISE SECTIONAL TEST SERIES (All 4 WBP/KP Subjects)
        if (selectedFilter == TestSeriesFilter.ALL || selectedFilter == TestSeriesFilter.SUBJECT_WISE) {
            item {
                Text(
                    text = if (isBn)
                        "বিষয়ভিত্তিক সেকশনাল টেস্ট সিরিজ (SUBJECT-WISE TEST SERIES)"
                    else
                        "SUBJECT-WISE SECTIONAL TEST SERIES (4 WBP/KP SUBJECTS)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
            }

            items(sectionalSeries, key = { it.id }) { testItem ->
                val subCode = testItem.subjectFilter ?: "GK"
                val subQuestions = questionVault.filter { it.subject.equals(subCode, ignoreCase = true) }
                val subSaved = subQuestions.size
                val subUnseen = subQuestions.count { it.id !in seenQuestionIds }
                TestbookSeriesCard(
                    isBn = isBn,
                    item = testItem,
                    savedVaultCount = subSaved,
                    unseenVaultCount = subUnseen,
                    targetVaultCapacity = 1000,
                    completedScore = completedTestScores[testItem.id],
                    isGeneratingSectional = generatingSubjectSectional.equals(subCode, ignoreCase = true),
                    isBulkAdding50 = bulkGeneratingTarget.equals(subCode, ignoreCase = true),
                    onStartTest = { onStartTestSeriesItem(testItem) },
                    onViewSolutions = { onViewSolutionsForTestSeriesItem(testItem) },
                    onGenerateAiSectional = { onGenerateAiSectionalTest(subCode) },
                    onBulkAdd50ToVault = if (isAdmin) {
                        { onBulkAdd50Questions(subCode, null, testItem.titleEn) }
                    } else null
                )
            }
        }

        // SECTION 3: PYQ VAULT (2005–PRESENT) ARCHIVE
        if (selectedFilter == TestSeriesFilter.ALL || selectedFilter == TestSeriesFilter.PYQ_VAULT) {
            item {
                PyqVaultArchiveSection(
                    isBn = isBn,
                    questionVault = questionVault,
                    seenQuestionIds = seenQuestionIds,
                    bulkGeneratingTarget = bulkGeneratingTarget,
                    isAdmin = isAdmin,
                    onStartExamGroupTest = onStartExamGroupTest,
                    onBulkAdd50Questions = onBulkAdd50Questions
                )
            }
        }

        // SECTION 4: CHAPTER-WISE PRACTICE (55 OFFICIAL WBP & KP CHAPTERS ACROSS GK, MATH, REASONING, ENGLISH)
        if (selectedFilter == TestSeriesFilter.ALL || selectedFilter == TestSeriesFilter.CHAPTER_WISE) {
            item {
                WbpChapterWiseSection(
                    isBn = isBn,
                    questionVault = questionVault,
                    seenQuestionIds = seenQuestionIds,
                    bulkGeneratingTarget = bulkGeneratingTarget,
                    isAdmin = isAdmin,
                    onStartChapterTest = onStartChapterTest,
                    onBulkAdd50ForChapter = { chap ->
                        onBulkAdd50Questions(chap.subject, chap.id, chap.titleEn)
                    }
                )
            }
        }

        // SECTION 5: MISTAKE RETRY & BOOKMARKS NOTEBOOK
        if (selectedFilter == TestSeriesFilter.ALL || selectedFilter == TestSeriesFilter.MISTAKE_RETRY) {
            item {
                MistakeNotebookSectionCard(
                    isBn = isBn,
                    mistakeNotebook = mistakeNotebook,
                    onRetryMistakes = onRetryMistakes,
                    onRemoveMistake = onRemoveMistake,
                    retryButtonTestTag = "retry_mistakes_mock_section_button",
                    initiallyExpanded = selectedFilter == TestSeriesFilter.MISTAKE_RETRY
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun TestbookSeriesCard(
    isBn: Boolean,
    item: TestSeriesItem,
    savedVaultCount: Int,
    unseenVaultCount: Int,
    targetVaultCapacity: Int = 1000,
    completedScore: Pair<Double, Int>?,
    isGeneratingSectional: Boolean,
    isBulkAdding50: Boolean,
    onStartTest: () -> Unit,
    onViewSolutions: () -> Unit,
    onGenerateAiSectional: (() -> Unit)?,
    onBulkAdd50ToVault: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("test_series_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Tag Row: Bengali Medium Tag + Previous Score Pill (if completed)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isBn) item.mediumTagBn else item.mediumTagEn,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (item.subjectFilter != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = item.subjectFilter,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                if (completedScore != null) {
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = String.format(
                                Locale.US,
                                if (isBn) "স্কোর: %.2f / %d" else "Score: %.2f / %d",
                                completedScore.first,
                                completedScore.second
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Test Title & Subtitle
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (isBn) item.titleBn else item.titleEn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isBn) item.subtitleBn else item.subtitleEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Live Counter Badge: [Saved in Vault: X / 1000 Questions • Unseen: Y]
            VaultCounterBadge(
                savedCount = savedVaultCount,
                unseenCount = unseenVaultCount,
                targetCapacity = targetVaultCapacity,
                modifier = Modifier.testTag("vault_counter_${item.id}")
            )

            // Questions • Marks • Time Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TestSpecPill(
                    text = if (isBn) "${item.questionCount}টি প্রশ্ন" else "${item.questionCount} Questions"
                )
                TestSpecPill(
                    text = if (isBn) "${item.totalMarks} নম্বর" else "${item.totalMarks} Marks"
                )
                TestSpecPill(
                    text = if (isBn) "${item.durationMinutes} মিনিট" else "${item.durationMinutes} Mins"
                )
            }

            // Action Buttons: Start Test OR (Re-attempt / View Solutions)
            if (completedScore == null) {
                Button(
                    onClick = onStartTest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("start_test_${item.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "Start Test (পরীক্ষা শুরু করুন)" else "Start Test",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onStartTest,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("start_test_${item.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "Re-attempt" else "Re-attempt",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = onViewSolutions,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("view_solutions_${item.id}"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isBn) "View Solutions" else "View Solutions",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // "⚡ Bulk Add +50 New PYQ-Pattern Questions to Vault" button inside every Subject-Wise Test card
            if (onBulkAdd50ToVault != null) {
                BulkAdd50VaultButton(
                    isBn = isBn,
                    isLoading = isBulkAdding50,
                    testTag = "bulk_add_50_${item.subjectFilter}",
                    onClick = onBulkAdd50ToVault
                )
            }

            // "⚡ Generate New AI Sectional Test" button inside each Subject-Wise Test card
            if (onGenerateAiSectional != null) {
                OutlinedButton(
                    onClick = onGenerateAiSectional,
                    enabled = !isGeneratingSectional,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("generate_ai_sectional_${item.subjectFilter}"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    if (isGeneratingSectional) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "AI নতুন সেকশনাল প্রশ্নপত্র তৈরি করছে..." else "Generating AI Sectional Test...",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn)
                                "⚡ Generate New AI Sectional Test (আনলিমিটেড নতুন সেট)"
                            else
                                "⚡ Generate New AI Sectional Test",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TestSpecPill(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        )
    }
}

/**
 * Part 3: Real Testbook-Style Live Exam Interface & Post-Test Solutions View
 * Distraction-free full-screen view with proper top (40.dp min) and bottom (16.dp min) safe-area padding.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun TestbookLiveExamView(
    language: AppLanguage,
    questions: List<QuizQuestion>,
    currentIndex: Int,
    selectedAnswers: Map<String, Int>,
    markedForReview: Set<String>,
    bookmarkedIds: Set<String>,
    mistakeNotebook: List<MistakeNotebookEntity>,
    questionTimeSpent: Map<String, Int>,
    isSubmitted: Boolean,
    isExamPaused: Boolean,
    activeTestTitleEn: String,
    activeTestTitleBn: String,
    remainingSeconds: Int,
    totalDurationSeconds: Int,
    statusBanner: String?,
    aspirantProfile: com.example.data.AspirantProfile? = null,
    getPaletteStatus: (String) -> QuestionPaletteStatus,
    onSelectQuestion: (Int) -> Unit,
    onSelectOption: (String, Int) -> Unit,
    onClearOption: (String) -> Unit,
    onToggleReview: (String) -> Unit,
    onToggleBookmark: (QuizQuestion) -> Unit,
    onSaveAndNext: () -> Unit,
    onToggleExamPause: () -> Unit,
    onExitLiveExamToHub: () -> Unit,
    onRetryMistakes: () -> Unit,
    onSubmitTest: () -> Unit,
    onRestartFullMock: () -> Unit,
    onStartWeakChapterDrill: () -> Unit = {},
    onAskAiWeakTopic: (String) -> Unit
) {
    val isBn = language == AppLanguage.BENGALI
    val currentQuestion = questions.getOrNull(currentIndex) ?: return

    var showPaletteDrawer by remember { mutableStateOf(false) }
    var showSubmitConfirmDialog by remember { mutableStateOf(false) }
    var selectedSolutionFilter by remember { mutableStateOf(SolutionFilter.ALL) }

    BackHandler {
        onExitLiveExamToHub()
    }

    val topSafeClearance = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()
        .coerceAtLeast(40.dp)

    val bottomSafeClearance = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
        .coerceAtLeast(16.dp)

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeText = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    // 42-Second Per-Question Speed Pacer calculation for current question
    val currentQSeconds = questionTimeSpent[currentQuestion.id] ?: 0
    val pacerColor = when {
        currentQSeconds <= 42 -> EmeraldSuccess
        currentQSeconds in 43..89 -> AmberWarning
        else -> CrimsonWrong
    }

    // 5-State Counts for Palette & Submit Confirmation Modal
    val statusCounts = remember(questions, selectedAnswers, markedForReview, currentIndex) {
        var answered = 0
        var notAnswered = 0
        var reviewUnanswered = 0
        var answeredAndReview = 0
        var notVisited = 0
        questions.forEach { q ->
            when (getPaletteStatus(q.id)) {
                QuestionPaletteStatus.ANSWERED -> answered++
                QuestionPaletteStatus.UNANSWERED -> notAnswered++
                QuestionPaletteStatus.MARKED_FOR_REVIEW -> reviewUnanswered++
                QuestionPaletteStatus.ANSWERED_AND_MARKED_FOR_REVIEW -> answeredAndReview++
                QuestionPaletteStatus.NOT_VISITED -> notVisited++
            }
        }
        FiveStateCounts(
            answered = answered,
            notAnswered = notAnswered,
            markedForReview = reviewUnanswered,
            answeredAndMarkedForReview = answeredAndReview,
            notVisited = notVisited
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            // Testbook Top Exam Bar with Top Safe-Area Padding
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = topSafeClearance,
                                bottom = 10.dp,
                                start = 12.dp,
                                end = 16.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Pause / Exit Button + Test Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = onExitLiveExamToHub,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("exit_exam_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Exit to Test Hub",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            if (!isSubmitted) {
                                IconButton(
                                    onClick = onToggleExamPause,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .testTag("pause_exam_button")
                                ) {
                                    Icon(
                                        imageVector = if (isExamPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                        contentDescription = if (isExamPaused) "Resume Exam" else "Pause Exam",
                                        tint = if (isExamPaused) AmberWarning else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBn) activeTestTitleBn else activeTestTitleEn,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (isSubmitted) {
                                        if (isBn) "স্কোরকার্ড ও সমাধান" else "Scorecard & Solutions"
                                    } else if (isExamPaused) {
                                        if (isBn) "বিরতি (Paused)" else "Exam Paused"
                                    } else {
                                        if (isBn) "বাংলা মাধ্যম • লাইভ পরীক্ষা" else "Bengali Medium • Live Exam"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isExamPaused) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Right: Live Countdown Timer (MM:SS) + Grid Icon for Slide-Out Question Palette
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = if (remainingSeconds < 60 && !isSubmitted)
                                    CrimsonWrong.copy(alpha = 0.14f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "Countdown Timer",
                                        modifier = Modifier.size(14.dp),
                                        tint = if (remainingSeconds < 60 && !isSubmitted)
                                            CrimsonWrong
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isSubmitted) (if (isBn) "সমাপ্ত" else "DONE") else timeText,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (remainingSeconds < 60 && !isSubmitted)
                                            CrimsonWrong
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (!isSubmitted) {
                                IconButton(
                                    onClick = { showPaletteDrawer = true },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .testTag("open_palette_grid_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GridView,
                                        contentDescription = "Open Question Palette",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Horizontal Testbook Subject Section Switcher Bar: [GK (25)], [Math (25)], [Reasoning (25)], [English (10)]
                    val sectionTabs = remember(questions) {
                        val order = listOf(
                            "GK" to "GK",
                            "MATH" to "Math",
                            "REASONING" to "Reasoning",
                            "ENGLISH" to "English"
                        )
                        order.mapNotNull { (code, label) ->
                            val firstIdx = questions.indexOfFirst { it.subject.equals(code, ignoreCase = true) }
                            val count = questions.count { it.subject.equals(code, ignoreCase = true) }
                            if (firstIdx >= 0 && count > 0) Triple(code, "$label ($count)", firstIdx) else null
                        }
                    }

                    if (!isSubmitted && sectionTabs.size > 1) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .testTag("live_exam_section_tabs"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(sectionTabs, key = { it.first }) { (subCode, tabLabel, firstQuestionIdx) ->
                                val isCurrentSection = currentQuestion.subject.equals(subCode, ignoreCase = true)
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onSelectQuestion(firstQuestionIdx) }
                                        .testTag("section_tab_${subCode.lowercase()}"),
                                    color = if (isCurrentSection) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = tabLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isCurrentSection) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCurrentSection) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Horizontal Question Number Strip (Swipeable numbered pills 1, 2, 3, 4... right below header)
                    if (!isSubmitted) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            itemsIndexed(questions) { idx, q ->
                                val status = getPaletteStatus(q.id)
                                val isCurrent = idx == currentIndex
                                TestbookQuestionNumberPill(
                                    questionNumber = idx + 1,
                                    status = status,
                                    isCurrent = isCurrent,
                                    onClick = { onSelectQuestion(idx) },
                                    modifier = Modifier.testTag("palette_q_${idx + 1}")
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                    )
                }
            }
        },
        bottomBar = {
            // Fixed Testbook Bottom Action Bar (with bottom safe-area padding)
            if (!isSubmitted) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                    tonalElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = bottomSafeClearance)
                    ) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.75f)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left side: Mark for Review (Purple accent) & Clear Response
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val isMarked = currentQuestion.id in markedForReview
                                OutlinedButton(
                                    onClick = { onToggleReview(currentQuestion.id) },
                                    modifier = Modifier
                                        .height(42.dp)
                                        .testTag("mark_review_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isMarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Mark for Review",
                                        tint = PurpleReview,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isMarked) {
                                            if (isBn) "রিভিউতে আছে" else "Marked"
                                        } else {
                                            if (isBn) "Mark Review" else "Mark Review"
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PurpleReview
                                    )
                                }

                                OutlinedButton(
                                    onClick = { onClearOption(currentQuestion.id) },
                                    enabled = selectedAnswers.containsKey(currentQuestion.id),
                                    modifier = Modifier
                                        .height(42.dp)
                                        .testTag("clear_response_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "Clear" else "Clear",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (selectedAnswers.containsKey(currentQuestion.id))
                                            CrimsonWrong
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Right side: Save & Next primary button (or Submit on last question)
                            Button(
                                onClick = {
                                    if (currentIndex < questions.lastIndex) {
                                        onSaveAndNext()
                                    } else {
                                        onSaveAndNext()
                                        showSubmitConfirmDialog = true
                                    }
                                },
                                modifier = Modifier
                                    .height(42.dp)
                                    .testTag("save_and_next_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (currentIndex < questions.lastIndex) {
                                        if (isBn) "Save & Next" else "Save & Next"
                                    } else {
                                        if (isBn) "Save & Submit" else "Save & Submit"
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!statusBanner.isNullOrBlank()) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = statusBanner,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (!isSubmitted) {
                // Active Exam Question Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                                .padding(18.dp)
                        ) {
                            // Question Info Header: Q. 1/25, +1.0 | -0.25 marking pills, live 42s Speed Timer badge, Bookmark/Star icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Q. ${currentIndex + 1}/${questions.size}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    // +1.0 | -0.25 Marking Pill
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "+1.0 | -0.25",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                        )
                                    }

                                    // Live 42s Speed Timer Badge
                                    Surface(
                                        modifier = Modifier.testTag("question_pacer_badge"),
                                        color = pacerColor.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .border(1.dp, pacerColor.copy(alpha = 0.35f), RoundedCornerShape(50))
                                                .padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Speed,
                                                contentDescription = "Question Time Spent",
                                                tint = pacerColor,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "${currentQSeconds}s / 42s",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = pacerColor,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                // Bookmark / Star Icon
                                val isBookmarked = currentQuestion.id in bookmarkedIds
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onToggleBookmark(currentQuestion) }
                                        .testTag("bookmark_star_button"),
                                    color = if (isBookmarked) AmberWarning.copy(alpha = 0.15f) else Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "Bookmark Question",
                                            tint = if (isBookmarked) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // Gentle 90+ seconds alert banner
                            AnimatedVisibility(visible = currentQSeconds >= 90) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    color = CrimsonWrong.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.WarningAmber,
                                                contentDescription = null,
                                                tint = CrimsonWrong,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Time warning: Skip and return later!",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = CrimsonWrong,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        if (currentIndex < questions.lastIndex) {
                                            Text(
                                                text = if (isBn) "স্কিপ →" else "Skip →",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = CrimsonWrong,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.clickable {
                                                    onToggleReview(currentQuestion.id)
                                                    onSelectQuestion(currentIndex + 1)
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val effectiveYearTag = currentQuestion.effectivePyqBadge().ifBlank {
                                currentQuestion.examYearTag.ifBlank { "WBPRB Expected Pattern" }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${currentQuestion.subject} • ${currentQuestion.effectiveChapterName()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Surface(
                                    color = AmberWarning.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "[$effectiveYearTag]",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberWarning,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // Question Text — ALWAYS in Bengali for GK, Math, Reasoning; English for English Grammar
                            Text(
                                text = currentQuestion.displayQuestionText(),
                                style = MaterialTheme.typography.titleMedium,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Reasoning Question Figure / Diagram (with Tap-to-Zoom Full-Screen Preview)
                            if (currentQuestion.hasQuestionImage()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                QuestionDiagramCard(
                                    base64Image = currentQuestion.questionImage,
                                    modifier = Modifier.testTag("live_exam_question_diagram")
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Clean MCQ Option Rows — ALWAYS in Bengali for GK, Math, Reasoning; English for English Grammar
                            // Supports Reasoning Option Images inside each Option A/B/C/D card
                            val options = currentQuestion.displayOptions()
                            val chosenOption = selectedAnswers[currentQuestion.id]
                            val bubbleLetters = listOf("A", "B", "C", "D")

                            options.forEachIndexed { optIdx, optText ->
                                val isSelected = chosenOption == optIdx
                                val optImgBase64 = currentQuestion.optionImageAt(optIdx)
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSelectOption(currentQuestion.id, optIdx) }
                                        .testTag("omr_option_$optIdx"),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                                    else MaterialTheme.colorScheme.surface
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isSelected) MaterialTheme.colorScheme.primary
                                                        else MaterialTheme.colorScheme.surfaceVariant
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = bubbleLetters.getOrElse(optIdx) { "" },
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Text(
                                                text = optText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        if (optImgBase64.isNotBlank()) {
                                            OptionDiagramThumbnail(
                                                base64Image = optImgBase64,
                                                modifier = Modifier
                                                    .padding(start = 40.dp)
                                                    .testTag("live_exam_option_diagram_$optIdx")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Submit Test Card Button (also accessible directly below question card)
                item {
                    OutlinedButton(
                        onClick = { showSubmitConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("submit_omr_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "Submit Test (পরীক্ষা জমা দিন)" else "Submit Test & View Scorecard",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldSuccess
                        )
                    }
                }
            } else {
                // POST-TEST SCORECARD & SOLUTIONS VIEW
                item {
                    ScorecardSummaryCard(
                        isBn = isBn,
                        questions = questions,
                        selectedAnswers = selectedAnswers,
                        questionTimeSpent = questionTimeSpent,
                        timeTakenSeconds = max(1, totalDurationSeconds - remainingSeconds),
                        defaultCasteCategory = aspirantProfile?.resolvedCasteCategory,
                        onRetake = onRestartFullMock,
                        onBackToHub = onExitLiveExamToHub,
                        onRetryMistakes = onRetryMistakes,
                        onStartWeakChapterDrill = onStartWeakChapterDrill,
                        hasMistakes = mistakeNotebook.isNotEmpty(),
                        onAskAiWeakTopic = onAskAiWeakTopic
                    )
                }

                // Solutions Filter Bar: [All], [Incorrect], [Unattempted]
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isBn)
                                "বিস্তারিত সমাধান ও শর্টকাট ট্রিকস (বাংলায় ব্যাখ্যা)"
                            else
                                "SOLUTIONS & SHORTCUT TRICKS (BENGALI EXPLANATIONS)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(SolutionFilter.entries) { filter ->
                                val isSel = selectedSolutionFilter == filter
                                val count = when (filter) {
                                    SolutionFilter.ALL -> questions.size
                                    SolutionFilter.INCORRECT -> questions.count { q ->
                                        val c = selectedAnswers[q.id]
                                        c != null && c != q.correctIndex
                                    }
                                    SolutionFilter.UNATTEMPTED -> questions.count { q ->
                                        selectedAnswers[q.id] == null
                                    }
                                    SolutionFilter.BOOKMARKED -> questions.count { q ->
                                        q.id in bookmarkedIds
                                    }
                                }
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .clickable { selectedSolutionFilter = filter }
                                        .testTag("solutions_filter_${filter.name}"),
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "${if (isBn) filter.labelBn else filter.labelEn} ($count)",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Medium,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                val filteredSolutions = questions.mapIndexed { idx, q -> idx to q }.filter { (_, q) ->
                    val userChoice = selectedAnswers[q.id]
                    when (selectedSolutionFilter) {
                        SolutionFilter.ALL -> true
                        SolutionFilter.INCORRECT -> userChoice != null && userChoice != q.correctIndex
                        SolutionFilter.UNATTEMPTED -> userChoice == null
                        SolutionFilter.BOOKMARKED -> q.id in bookmarkedIds
                    }
                }

                items(filteredSolutions, key = { (_, q) -> q.id }) { (idx, q) ->
                    val userChoice = selectedAnswers[q.id]
                    val isCorrect = userChoice == q.correctIndex
                    val isBookmarked = q.id in bookmarkedIds
                    val options = q.displayOptions()
                    val qSec = questionTimeSpent[q.id] ?: 0
                    val qTimeColor = when {
                        qSec <= 42 -> EmeraldSuccess
                        qSec in 43..89 -> AmberWarning
                        else -> CrimsonWrong
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = when {
                                        userChoice == null -> MaterialTheme.colorScheme.outline
                                        isCorrect -> EmeraldSuccess.copy(alpha = 0.6f)
                                        else -> CrimsonWrong.copy(alpha = 0.6f)
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Q${idx + 1} • ${q.subject}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (q.examYearTag.isNotBlank()) {
                                        Surface(
                                            color = AmberWarning.copy(alpha = 0.14f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "[${q.examYearTag}]",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = AmberWarning,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Surface(
                                        color = qTimeColor.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "${qSec}s",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = qTimeColor,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val markBadge = when {
                                        userChoice == null -> if (isBn) "অনুত্তরিত (0)" else "Unattempted (0)"
                                        isCorrect -> "+1.00"
                                        else -> if (isBn) "-0.25 • ভুল খাতায় সেভড" else "-0.25 • Saved"
                                    }
                                    Text(
                                        text = markBadge,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = when {
                                            userChoice == null -> MaterialTheme.colorScheme.onSurfaceVariant
                                            isCorrect -> EmeraldSuccess
                                            else -> CrimsonWrong
                                        }
                                    )
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Bookmark Question",
                                        tint = if (isBookmarked) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable { onToggleBookmark(q) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Question text (Bengali for GK/Math/Reasoning, English for English Grammar)
                            Text(
                                text = q.displayQuestionText(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (q.hasQuestionImage()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                QuestionDiagramCard(
                                    base64Image = q.questionImage
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (userChoice != null && !isCorrect) {
                                Text(
                                    text = (if (isBn) "✕ আপনার উত্তর: " else "✕ Your Answer: ") +
                                        options.getOrElse(userChoice) { "" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CrimsonWrong,
                                    fontWeight = FontWeight.Medium
                                )
                                val chosenOptImg = q.optionImageAt(userChoice)
                                if (chosenOptImg.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OptionDiagramThumbnail(base64Image = chosenOptImg)
                                }
                            }

                            Text(
                                text = (if (isBn) "✓ সঠিক উত্তর: " else "✓ Correct Answer: ") +
                                    options.getOrElse(q.correctIndex) { "" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                            val correctOptImg = q.optionImageAt(q.correctIndex)
                            if (correctOptImg.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                OptionDiagramThumbnail(base64Image = correctOptImg)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Explanation ALWAYS in Bengali (`explanation_bn`)
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (isBn) "বিস্তারিত ব্যাখ্যা (বাংলায়):" else "Step-by-Step Solution (বাংলায়):",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = q.displayExplanationBn(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    val trick = q.displayShortcutBn()
                                    if (trick.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Lightbulb,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = trick,
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Slide-Over / Modal Question Palette Sheet (Testbook 5-Color Legend + Full Question Grid + Submit Test Button)
    if (showPaletteDrawer) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteDrawer = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = bottomSafeClearance + 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBn) "প্রশ্ন প্যালেট (Question Palette)" else "Question Palette",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBn)
                                "যেকোনো প্রশ্নে সরাসরি যেতে নম্বরে ট্যাপ করুন"
                            else
                                "Tap any question number to jump directly",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { showPaletteDrawer = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close Palette")
                    }
                }

                // 5-Color Status Legend
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LegendDot(
                                color = EmeraldSuccess,
                                text = if (isBn) "Answered (${statusCounts.answered})" else "Answered (${statusCounts.answered})"
                            )
                            LegendDot(
                                color = CrimsonWrong,
                                text = if (isBn) "Not Answered (${statusCounts.notAnswered})" else "Not Answered (${statusCounts.notAnswered})"
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LegendDot(
                                color = PurpleReview,
                                text = if (isBn) "Marked Review (${statusCounts.markedForReview})" else "Marked Review (${statusCounts.markedForReview})"
                            )
                            LegendDot(
                                color = PurpleReview,
                                hasGreenSubDot = true,
                                text = if (isBn) "Ans & Marked (${statusCounts.answeredAndMarkedForReview})" else "Ans & Marked (${statusCounts.answeredAndMarkedForReview})"
                            )
                        }
                        LegendDot(
                            color = MaterialTheme.colorScheme.outline,
                            text = if (isBn) "Not Visited (${statusCounts.notVisited})" else "Not Visited (${statusCounts.notVisited})"
                        )
                    }
                }

                // Full Question Number Grid
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    questions.forEachIndexed { idx, q ->
                        val status = getPaletteStatus(q.id)
                        TestbookQuestionNumberPill(
                            questionNumber = idx + 1,
                            status = status,
                            isCurrent = idx == currentIndex,
                            onClick = {
                                onSelectQuestion(idx)
                                showPaletteDrawer = false
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        showPaletteDrawer = false
                        showSubmitConfirmDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("palette_submit_test_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldSuccess,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBn) "Submit Test (পরীক্ষা জমা দিন)" else "Submit Test",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Submit Summary Confirmation Modal
    if (showSubmitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirmDialog = false },
            title = {
                Text(
                    text = if (isBn) "পরীক্ষা জমা দেওয়ার সারসংক্ষেপ" else "Submit Test Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isBn)
                            "চূড়ান্ত স্কোর হিসাব করার আগে আপনার উত্তরের পরিসংখ্যান দেখে নিন (+1.0 / -0.25 নেগেটিভ মার্কিং):"
                        else
                            "Review your attempt summary before calculating your final score (+1.0 / -0.25 negative marking):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SummaryStatRow(
                                label = if (isBn) "মোট প্রশ্ন (Total Questions)" else "Total Questions",
                                value = "${questions.size}",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            SummaryStatRow(
                                label = if (isBn) "উত্তর দেওয়া হয়েছে (Answered)" else "Answered",
                                value = "${statusCounts.answered + statusCounts.answeredAndMarkedForReview}",
                                color = EmeraldSuccess
                            )
                            SummaryStatRow(
                                label = if (isBn) "উত্তর দেওয়া হয়নি (Not Answered)" else "Not Answered",
                                value = "${statusCounts.notAnswered}",
                                color = CrimsonWrong
                            )
                            SummaryStatRow(
                                label = if (isBn) "রিভিউ মার্ক (Marked for Review)" else "Marked for Review",
                                value = "${statusCounts.markedForReview + statusCounts.answeredAndMarkedForReview}",
                                color = PurpleReview
                            )
                            SummaryStatRow(
                                label = if (isBn) "দেখা হয়নি (Not Visited)" else "Not Visited",
                                value = "${statusCounts.notVisited}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmDialog = false
                        onSubmitTest()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldSuccess,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("confirm_submit_test_button")
                ) {
                    Text(if (isBn) "হ্যাঁ, সাবমিট করুন" else "Confirm & Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmDialog = false }) {
                    Text(if (isBn) "পরীক্ষায় ফিরুন" else "Resume Test")
                }
            }
        )
    }
}

private data class FiveStateCounts(
    val answered: Int,
    val notAnswered: Int,
    val markedForReview: Int,
    val answeredAndMarkedForReview: Int,
    val notVisited: Int
)

/**
 * 5-State Question Status Pill (Exact Testbook Colors):
 * - Answered: Emerald Green
 * - Not Answered (Visited but skipped): Rose Red
 * - Marked for Review (Unanswered): Purple
 * - Answered & Marked for Review: Purple with a small green dot/badge indicator
 * - Not Visited: Subtle Slate/Zinc Grey
 */
@Composable
private fun TestbookQuestionNumberPill(
    questionNumber: Int,
    status: QuestionPaletteStatus,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when (status) {
        QuestionPaletteStatus.ANSWERED -> EmeraldSuccess
        QuestionPaletteStatus.UNANSWERED -> CrimsonWrong
        QuestionPaletteStatus.MARKED_FOR_REVIEW -> PurpleReview
        QuestionPaletteStatus.ANSWERED_AND_MARKED_FOR_REVIEW -> PurpleReview
        QuestionPaletteStatus.NOT_VISITED -> MaterialTheme.colorScheme.surfaceVariant
    }
    val txtColor = if (status == QuestionPaletteStatus.NOT_VISITED)
        MaterialTheme.colorScheme.onSurface else Color.White

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(
                width = if (isCurrent) 2.dp else 1.dp,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$questionNumber",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = txtColor
        )

        // Small green badge dot in the bottom-right corner for ANSWERED_AND_MARKED_FOR_REVIEW
        if (status == QuestionPaletteStatus.ANSWERED_AND_MARKED_FOR_REVIEW) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(3.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(EmeraldSuccess)
                    .border(1.dp, Color.White, CircleShape)
            )
        }
    }
}

@Composable
private fun SummaryStatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private data class SubjectStat(
    val subject: SubjectCategory,
    val total: Int,
    val correct: Int,
    val wrong: Int,
    val skipped: Int,
    val netScore: Double,
    val accuracyPct: Int,
    val avgSeconds: Int
)

private enum class WbpReservationCategory(
    val label: String,
    val safeCutoffOutOf85: Double
) {
    UR("UR / General", 54.5),
    OBC_B("OBC-B", 51.0),
    OBC_A("OBC-A", 46.5),
    SC("SC", 47.0),
    ST("ST", 41.5),
    GORKHA("Gorkha / Hill", 43.0);

    companion object {
        fun fromAspirantCategory(cat: com.example.data.AspirantCasteCategory?): WbpReservationCategory {
            return when (cat) {
                com.example.data.AspirantCasteCategory.GENERAL -> UR
                com.example.data.AspirantCasteCategory.OBC_B -> OBC_B
                com.example.data.AspirantCasteCategory.OBC_A -> OBC_A
                com.example.data.AspirantCasteCategory.SC -> SC
                com.example.data.AspirantCasteCategory.ST -> ST
                com.example.data.AspirantCasteCategory.GORKHA -> GORKHA
                null -> UR
            }
        }
    }
}

@Composable
private fun ScorecardSummaryCard(
    isBn: Boolean,
    questions: List<QuizQuestion>,
    selectedAnswers: Map<String, Int>,
    questionTimeSpent: Map<String, Int>,
    timeTakenSeconds: Int,
    defaultCasteCategory: com.example.data.AspirantCasteCategory? = null,
    onRetake: () -> Unit,
    onBackToHub: () -> Unit,
    onRetryMistakes: () -> Unit,
    onStartWeakChapterDrill: () -> Unit = {},
    hasMistakes: Boolean,
    onAskAiWeakTopic: (String) -> Unit
) {
    var selectedCategory by remember(defaultCasteCategory) {
        mutableStateOf(WbpReservationCategory.fromAspirantCategory(defaultCasteCategory))
    }

    var correct = 0
    var wrong = 0
    var strongAndFastCount = 0
    var correctButSlowCount = 0
    var negativeTrapCount = 0
    var unattemptedGapCount = 0

    questions.forEach { q ->
        val c = selectedAnswers[q.id]
        val sec = questionTimeSpent[q.id] ?: 0
        if (c != null) {
            if (c == q.correctIndex) {
                correct++
                if (sec <= 42) strongAndFastCount++ else correctButSlowCount++
            } else {
                wrong++
                negativeTrapCount++
            }
        } else {
            unattemptedGapCount++
        }
    }
    val skipped = questions.size - (correct + wrong)
    val netScore = (correct * 1.0) - (wrong * 0.25)
    val attempted = correct + wrong
    val accuracy = if (attempted > 0) ((correct.toDouble() / attempted) * 100).roundToInt() else 0

    // Scale cutoff proportionally to the current test's total marks (85 for Full Mock, 25 for Sectional, etc.)
    val maxMarks = questions.size.coerceAtLeast(1)
    val scaledCutoff = (selectedCategory.safeCutoffOutOf85 * maxMarks) / 85.0
    val isQualifiedForPet = netScore >= scaledCutoff

    val subjectStats = SubjectCategory.entries.mapNotNull { sub ->
        val subQs = questions.filter { it.subject.equals(sub.id, ignoreCase = true) }
        if (subQs.isEmpty()) return@mapNotNull null
        var subCorr = 0
        var subWrong = 0
        var totalSec = 0
        subQs.forEach { q ->
            val ans = selectedAnswers[q.id]
            if (ans != null) {
                if (ans == q.correctIndex) subCorr++ else subWrong++
            }
            totalSec += (questionTimeSpent[q.id] ?: 0)
        }
        val subAttempted = subCorr + subWrong
        val subAcc = if (subAttempted > 0) ((subCorr.toDouble() / subAttempted) * 100).roundToInt() else 0
        val avgSec = if (subQs.isNotEmpty()) totalSec / subQs.size else 0
        SubjectStat(
            subject = sub,
            total = subQs.size,
            correct = subCorr,
            wrong = subWrong,
            skipped = subQs.size - subAttempted,
            netScore = (subCorr * 1.0) - (subWrong * 0.25),
            accuracyPct = subAcc,
            avgSeconds = avgSec
        )
    }

    val weakTopics = subjectStats.filter { stat ->
        stat.total > 0 && (stat.wrong > 0 || stat.accuracyPct < 70 || stat.avgSeconds > 42 || stat.skipped == stat.total)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scorecard_summary"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isBn) "WBPRB OMR স্কোরকার্ড ও কাট-অফ প্রেডিক্টর" else "TESTBOOK SELECTION PREDICTOR & SCORECARD",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = String.format(Locale.US, "%.2f / %d", netScore, questions.size),
                style = MaterialTheme.typography.headlineLarge,
                color = if (isQualifiedForPet) EmeraldSuccess else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isBn)
                    "নেগেটিভ মার্কিং (-0.25) বাদ দিয়ে প্রাপ্ত নম্বর"
                else
                    "Net Score after -0.25 Negative Marking",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Category Cut-Off Comparison Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cutoff_predictor_box"),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBn)
                            "ক্যাটাগরি নির্বাচন করে কাট-অফ তুলনা করুন (Category Cut-Off):"
                        else
                            "Select Category for Safe Cut-Off Comparison:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(WbpReservationCategory.entries) { cat ->
                            val isSel = selectedCategory == cat
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable { selectedCategory = cat }
                                    .testTag("cutoff_category_${cat.name}"),
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = cat.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBn)
                                    "${selectedCategory.label} সেফ কাট-অফ: ${String.format(Locale.US, "%.2f", scaledCutoff)} / $maxMarks"
                                else
                                    "${selectedCategory.label} Safe Cut-Off: ${String.format(Locale.US, "%.2f", scaledCutoff)} / $maxMarks",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isBn)
                                    "(৮৫ নম্বরে লক্ষ্যমাত্রা: ${selectedCategory.safeCutoffOutOf85})"
                                else
                                    "(Equivalent to ${selectedCategory.safeCutoffOutOf85} / 85 in Prelims)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            color = if (isQualifiedForPet) EmeraldSuccess.copy(alpha = 0.15f) else CrimsonWrong.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isQualifiedForPet)
                                    "QUALIFIED FOR PMT/PET"
                                else
                                    "BELOW CUT-OFF — NEEDS REVISION",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isQualifiedForPet) EmeraldSuccess else CrimsonWrong,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                    .testTag("cutoff_status_badge")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScoreStatItem(label = if (isBn) "সঠিক (+1)" else "Correct", value = "$correct", color = EmeraldSuccess)
                ScoreStatItem(label = if (isBn) "ভুল (-0.25)" else "Wrong", value = "$wrong", color = CrimsonWrong)
                ScoreStatItem(label = if (isBn) "বাকি (0)" else "Skipped", value = "$skipped", color = MaterialTheme.colorScheme.onSurfaceVariant)
                ScoreStatItem(label = if (isBn) "নির্ভুলতা" else "Accuracy", value = "$accuracy%", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(8.dp))
            val avgPacePerQ = if (questions.isNotEmpty()) timeTakenSeconds / questions.size else 0
            Text(
                text = if (isBn)
                    "সময়: ${timeTakenSeconds / 60}m ${timeTakenSeconds % 60}s • গড় গতি: ${avgPacePerQ}s/প্রশ্ন"
                else
                    "Time: ${timeTakenSeconds / 60}m ${timeTakenSeconds % 60}s • Avg Pace: ${avgPacePerQ}s/Q",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(14.dp))

            // 2. 4-Quadrant Speed & Accuracy Breakdown
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("four_quadrant_breakdown"),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isBn)
                        "৪-কোয়াড্রান্ট স্পিড ও নির্ভুলতা বিশ্লেষণ (4-Quadrant Analysis)"
                    else
                        "4-Quadrant Speed & Accuracy Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuadrantAnalyticTile(
                        title = if (isBn) "Strong & Fast (<42s)" else "Strong & Fast (<42s)",
                        subtitle = if (isBn) "সঠিক ও দ্রুত" else "Correct in ≤ 42s",
                        count = "$strongAndFastCount Q",
                        accentColor = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    QuadrantAnalyticTile(
                        title = if (isBn) "Correct but Slow (>42s)" else "Correct but Slow (>42s)",
                        subtitle = if (isBn) "শর্টকাট ট্রিক প্রয়োজন" else "Needs shortcut practice",
                        count = "$correctButSlowCount Q",
                        accentColor = AmberWarning,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuadrantAnalyticTile(
                        title = if (isBn) "Negative Traps (-0.25)" else "Negative Marking Traps",
                        subtitle = if (isBn) "-${String.format(Locale.US, "%.2f", negativeTrapCount * 0.25)} নম্বর কাটা গেছে" else "Cost -${String.format(Locale.US, "%.2f", negativeTrapCount * 0.25)} marks",
                        count = "$negativeTrapCount Q",
                        accentColor = CrimsonWrong,
                        modifier = Modifier.weight(1f)
                    )
                    QuadrantAnalyticTile(
                        title = if (isBn) "Unattempted / Gaps" else "Unattempted / Gaps",
                        subtitle = if (isBn) "কনসেপ্ট রিভিশন প্রয়োজন" else "Concept gaps to revise",
                        count = "$unattemptedGapCount Q",
                        accentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(14.dp))

            // Subject-Wise Accuracy Breakdown
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subject_accuracy_breakdown"),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = if (isBn)
                        "বিষয়ভিত্তিক নির্ভুলতা (Subject-Wise Accuracy)"
                    else
                        "Subject-Wise Accuracy Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))

                subjectStats.forEach { stat ->
                    val barColor = when {
                        stat.accuracyPct >= 75 -> EmeraldSuccess
                        stat.accuracyPct >= 45 -> MaterialTheme.colorScheme.primary
                        else -> CrimsonWrong
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBn) "${stat.subject.id} • ${stat.subject.titleBn}" else "${stat.subject.id} • ${stat.subject.titleEn}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "✓${stat.correct} ✕${stat.wrong} • ${stat.accuracyPct}% (${stat.avgSeconds}s)",
                                style = MaterialTheme.typography.labelSmall,
                                color = barColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (stat.accuracyPct / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = barColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Weak Topics to Revise Recommendation Box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weak_topics_box"),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = CrimsonWrong,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn)
                                "Weak Topics to Revise (রিভিশন পরামর্শ)"
                            else
                                "Weak Topics to Revise",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (weakTopics.isEmpty()) {
                        Text(
                            text = if (isBn)
                                "সব বিষয়েই আপনার নির্ভুলতা ও গতি ৪২ সেকেন্ডের মধ্যে রয়েছে!"
                            else
                                "Your accuracy and 42-second speed pace are on target across all subjects.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        weakTopics.forEach { weak ->
                            val recText = when (weak.subject) {
                                SubjectCategory.GK -> if (isBn)
                                    "• সাধারণ জ্ঞান (GK): পশ্চিমবঙ্গের ভূগোল, নদনদী ও প্রকল্পগুলো রিভিশন করুন (${weak.accuracyPct}%)।"
                                else
                                    "• General Awareness (GK): Revise West Bengal Geography & Schemes (${weak.accuracyPct}%)."

                                SubjectCategory.MATH -> if (isBn)
                                    "• প্রাথমিক গণিত (Math): ল.সা.গু ও অনুপাত শর্টকাট ট্রিক প্র্যাকটিস করুন (${weak.accuracyPct}%, গড় ${weak.avgSeconds}s)।"
                                else
                                    "• Elementary Math: Practice LCM & Ratio shortcut tricks (${weak.accuracyPct}%, Avg ${weak.avgSeconds}s)."

                                SubjectCategory.REASONING -> if (isBn)
                                    "• রিজনিং (GI): কোডিং, সিরিজ ও রক্তের সম্পর্কের ট্রিকগুলো ঝালিয়ে নিন (${weak.accuracyPct}%)।"
                                else
                                    "• Reasoning & GI: Revise coding-decoding, series & blood relations (${weak.accuracyPct}%)."

                                SubjectCategory.ENGLISH -> if (isBn)
                                    "• ইংরেজি (English): Fixed Prepositions ও Phrasal Verbs পড়ুন (${weak.accuracyPct}%)।"
                                else
                                    "• English Grammar: Revise Fixed Prepositions & Phrasal Verbs (${weak.accuracyPct}%)."
                            }
                            Text(
                                text = recText,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val primaryWeakSubject = weakTopics.first().subject
                        OutlinedButton(
                            onClick = {
                                onAskAiWeakTopic(
                                    if (isBn)
                                        "${primaryWeakSubject.titleBn}-এর দুর্বল অধ্যায়গুলোর ৩০ সেকেন্ডের শর্টকাট ট্রিক দাও"
                                    else
                                        "Give me 30-second exam hall shortcuts for ${primaryWeakSubject.titleEn}"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn)
                                    "AI টিউটরের কাছে শর্টকাট নোট চান"
                                else
                                    "Revise Weak Topics with AI Tutor",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. One-Tap Weak Chapter Recovery Button ("⚡ Fix My Weak Chapters (15-Q Custom Drill)")
            Button(
                onClick = onStartWeakChapterDrill,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("fix_weak_chapters_drill_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "⚡ Fix My Weak Chapters (15-Q Custom Drill)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (hasMistakes) {
                Button(
                    onClick = onRetryMistakes,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonWrong, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isBn) "Retry My Mistakes (ভুলগুলো আবার কুইজ দিন)" else "Retry My Mistakes Mini-Quiz")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onBackToHub,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("back_to_test_hub_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (isBn) "টেস্ট সিরিজ হাব" else "Test Series Hub")
                }

                Button(
                    onClick = onRetake,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBn) "আবার দিন" else "Re-attempt")
                }
            }
        }
    }
}

@Composable
private fun QuadrantAnalyticTile(
    title: String,
    subtitle: String,
    count: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = accentColor.copy(alpha = 0.09f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ScoreStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LegendDot(
    color: Color,
    text: String,
    hasGreenSubDot: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
                    .align(Alignment.Center)
            )
            if (hasGreenSubDot) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(EmeraldSuccess)
                        .align(Alignment.BottomEnd)
                )
            }
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
