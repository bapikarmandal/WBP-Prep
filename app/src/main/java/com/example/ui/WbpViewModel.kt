package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppLanguage
import com.example.data.ChatMessageEntity
import com.example.data.ExamType
import com.example.data.MistakeNotebookEntity
import com.example.data.MockTestResultEntity
import com.example.data.PetRunLogEntity
import com.example.data.QuestionPaletteStatus
import com.example.data.QuizQuestion
import com.example.data.SyllabusTopicEntity
import com.example.data.WbpRepository
import com.example.data.WbpSampleData
import com.example.network.GeminiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

enum class BottomNavTab(val route: String, val labelEn: String, val labelBn: String) {
    HOME("home", "Home", "হোম"),
    MOCK_TEST("mock_test", "Mock Test", "মক টেস্ট"),
    AI_TUTOR("ai_tutor", "AI Tutor", "AI টিউটর"),
    PET_TRACKER("pet_tracker", "PET Tracker", "মাঠ প্রস্তুতি"),
    SYLLABUS("syllabus", "Syllabus", "সিলেবাস")
}

class WbpViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("wbp_kp_prefs", Context.MODE_PRIVATE)
    private val repository = WbpRepository(AppDatabase.getInstance(application).wbpDao())

    // Persistent Preferences (Language, Dark Mode, Selected Exam, Daily Streak)
    private val _language = MutableStateFlow(
        if (prefs.getString("lang", "BN") == "EN") AppLanguage.ENGLISH else AppLanguage.BENGALI
    )
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _selectedExam = MutableStateFlow(
        if (prefs.getString("exam_type", ExamType.CONSTABLE.id) == ExamType.SI_PRELIMS.id)
            ExamType.SI_PRELIMS else ExamType.CONSTABLE
    )
    val selectedExam: StateFlow<ExamType> = _selectedExam.asStateFlow()

    private val _dailyStreak = MutableStateFlow(prefs.getInt("daily_streak", 5))
    val dailyStreak: StateFlow<Int> = _dailyStreak.asStateFlow()

    private val _currentTab = MutableStateFlow(BottomNavTab.HOME)
    val currentTab: StateFlow<BottomNavTab> = _currentTab.asStateFlow()

    // Room Database Flows
    val syllabusTopics: StateFlow<List<SyllabusTopicEntity>> = repository.syllabusTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mockResults: StateFlow<List<MockTestResultEntity>> = repository.mockResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val petRunLogs: StateFlow<List<PetRunLogEntity>> = repository.petRunLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Automatic Mistake Notebook ("ভুল সংশোধনী খাতা") & Bookmarks Flow
    val mistakeNotebook: StateFlow<List<MistakeNotebookEntity>> = repository.mistakeNotebook
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedQuestionIds: StateFlow<Set<String>> = repository.mistakeNotebook
        .map { list -> list.filter { it.isBookmarked }.map { it.questionId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Master Question Vault (1,000+ capacity per subject, 4,000+ total across GK, Math, Reasoning, English)
    private val initialSeedVault = rememberInitialSeedVault()
    val questionVault: StateFlow<List<QuizQuestion>> = repository.questionVaultEntities
        .map { entities ->
            if (entities.isEmpty()) initialSeedVault else entities.map { it.toQuizQuestion() }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialSeedVault)

    // Non-Repeating Question Bank Engine: seenQuestionIds persisted in SharedPreferences
    private val _seenQuestionIds = MutableStateFlow<Set<String>>(
        prefs.getStringSet("seen_question_ids", emptySet())?.toSet() ?: emptySet()
    )
    val seenQuestionIds: StateFlow<Set<String>> = _seenQuestionIds.asStateFlow()

    private val _bulkGeneratingTarget = MutableStateFlow<String?>(null)
    val bulkGeneratingTarget: StateFlow<String?> = _bulkGeneratingTarget.asStateFlow()

    private val _isImportingPyqBook = MutableStateFlow(false)
    val isImportingPyqBook: StateFlow<Boolean> = _isImportingPyqBook.asStateFlow()

    // Live OMR Mock Test & Testbook Exam Hub State
    private val _questions = MutableStateFlow<List<QuizQuestion>>(initialSeedVault.take(25))
    val questions: StateFlow<List<QuizQuestion>> = _questions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedAnswers = MutableStateFlow<Map<String, Int>>(emptyMap())
    val selectedAnswers: StateFlow<Map<String, Int>> = _selectedAnswers.asStateFlow()

    private val _markedForReview = MutableStateFlow<Set<String>>(emptySet())
    val markedForReview: StateFlow<Set<String>> = _markedForReview.asStateFlow()

    private val _visitedQuestions = MutableStateFlow<Set<String>>(
        setOfNotNull(initialSeedVault.firstOrNull()?.id)
    )
    val visitedQuestions: StateFlow<Set<String>> = _visitedQuestions.asStateFlow()

    private val _isTestSubmitted = MutableStateFlow(false)
    val isTestSubmitted: StateFlow<Boolean> = _isTestSubmitted.asStateFlow()

    private val _isLiveExamActive = MutableStateFlow(false)
    val isLiveExamActive: StateFlow<Boolean> = _isLiveExamActive.asStateFlow()

    private val _isExamPaused = MutableStateFlow(false)
    val isExamPaused: StateFlow<Boolean> = _isExamPaused.asStateFlow()

    private val _activeTestId = MutableStateFlow("FULL_MOCK_WBP_1")
    val activeTestId: StateFlow<String> = _activeTestId.asStateFlow()

    private val _activeTestTitleEn = MutableStateFlow("WBP Constable Full Length Mock Test #1")
    val activeTestTitleEn: StateFlow<String> = _activeTestTitleEn.asStateFlow()

    private val _activeTestTitleBn = MutableStateFlow("WBP কনস্টেবল ফুল লেন্থ মক টেস্ট #১")
    val activeTestTitleBn: StateFlow<String> = _activeTestTitleBn.asStateFlow()

    private val _completedTestScores = MutableStateFlow<Map<String, Pair<Double, Int>>>(loadCompletedScores())
    val completedTestScores: StateFlow<Map<String, Pair<Double, Int>>> = _completedTestScores.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(600) // 10 minutes default
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _totalDurationSeconds = MutableStateFlow(600)
    val totalDurationSeconds: StateFlow<Int> = _totalDurationSeconds.asStateFlow()

    // 42-Second Per-Question Speed Pacer tracking (questionId -> seconds spent)
    private val _questionTimeSpent = MutableStateFlow<Map<String, Int>>(emptyMap())
    val questionTimeSpent: StateFlow<Map<String, Int>> = _questionTimeSpent.asStateFlow()

    private val _isGeneratingAiMock = MutableStateFlow(false)
    val isGeneratingAiMock: StateFlow<Boolean> = _isGeneratingAiMock.asStateFlow()

    private val _generatingSubjectSectional = MutableStateFlow<String?>(null)
    val generatingSubjectSectional: StateFlow<String?> = _generatingSubjectSectional.asStateFlow()

    private val _mockStatusBanner = MutableStateFlow<String?>(null)
    val mockStatusBanner: StateFlow<String?> = _mockStatusBanner.asStateFlow()

    private val _lastGeneratedJson = MutableStateFlow<String?>(null)
    val lastGeneratedJson: StateFlow<String?> = _lastGeneratedJson.asStateFlow()

    private val _isRetryMistakesMode = MutableStateFlow(false)
    val isRetryMistakesMode: StateFlow<Boolean> = _isRetryMistakesMode.asStateFlow()

    // AI Tutor State
    private val _isAiTutorLoading = MutableStateFlow(false)
    val isAiTutorLoading: StateFlow<Boolean> = _isAiTutorLoading.asStateFlow()

    // 15-Mark AI Board Interview Simulator ("মক ইন্টারভিউ") State
    private val _selectedDistrict = MutableStateFlow(com.example.data.WbDistrict.MALDA)
    val selectedDistrict: StateFlow<com.example.data.WbDistrict> = _selectedDistrict.asStateFlow()

    private val _interviewTurns = MutableStateFlow<List<com.example.data.InterviewChatTurn>>(emptyList())
    val interviewTurns: StateFlow<List<com.example.data.InterviewChatTurn>> = _interviewTurns.asStateFlow()

    private val _latestInterviewScore = MutableStateFlow<Double?>(null)
    val latestInterviewScore: StateFlow<Double?> = _latestInterviewScore.asStateFlow()

    private val _interviewTurnCount = MutableStateFlow(1)
    val interviewTurnCount: StateFlow<Int> = _interviewTurnCount.asStateFlow()

    private val _isInterviewLoading = MutableStateFlow(false)
    val isInterviewLoading: StateFlow<Boolean> = _isInterviewLoading.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSeedData()
        }
        startOrResetTimer(600)
        resetInterviewForDistrict(_selectedDistrict.value)
    }

    private fun rememberInitialSeedVault(): List<QuizQuestion> {
        return (com.example.data.SopanPyqVaultSeed.buildInitialVaultQuestions() + WbpSampleData.preloadedQuestions)
            .distinctBy { it.displayQuestionText().trim().lowercase() }
    }

    private fun markQuestionsAsSeen(questionsToMark: List<QuizQuestion>) {
        if (questionsToMark.isEmpty()) return
        val updated = _seenQuestionIds.value + questionsToMark.map { it.id }
        _seenQuestionIds.value = updated
        prefs.edit().putStringSet("seen_question_ids", updated).apply()
    }

    fun resetSeenQuestionsHistory() {
        _seenQuestionIds.value = emptySet()
        prefs.edit().remove("seen_question_ids").apply()
        _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
            "🔄 দেখা প্রশ্নের তালিকা (Seen History) রিসেট করা হয়েছে—সব ভল্ট প্রশ্ন আবার নতুন হিসেবে পাওয়া যাবে!"
        else
            "🔄 Seen question history reset — all Vault questions are now marked Unseen!"
    }

    /**
     * Non-Repeating Question Bank Engine:
     * Strictly filters out any question ID already inside `seenQuestionIds` so questions NEVER repeat across tests.
     * If unseen questions in the vault for the target filter are fewer than `targetCount`, automatically generates
     * and persists fresh non-repeating PYQ-pattern questions into the vault on the fly.
     */
    private fun pickNonRepeatingQuestions(
        subjectFilter: String? = null,
        chapterId: String? = null,
        examFamily: String? = null,
        targetCount: Int = 25
    ): List<QuizQuestion> {
        val currentSeen = _seenQuestionIds.value
        val fullVault = questionVault.value.ifEmpty { initialSeedVault }

        val matchingPool = fullVault.filter { q ->
            val subOk = subjectFilter == null || q.subject.equals(subjectFilter, ignoreCase = true)
            val chapOk = chapterId == null || q.chapterId.equals(chapterId, ignoreCase = true)
            val examOk = examFamily == null || examFamily == "ALL_EXAMS" || q.examFamily.equals(examFamily, ignoreCase = true)
            subOk && chapOk && examOk
        }

        val unseenPool = matchingPool.filter { it.id !in currentSeen }
        val selected = mutableListOf<QuizQuestion>()

        if (subjectFilter == null && chapterId == null && examFamily == null && targetCount >= 30) {
            // Balanced Full Mock across all 4 subjects from unseen pool
            val gkUnseen = unseenPool.filter { it.subject == "GK" }.shuffled()
            val mathUnseen = unseenPool.filter { it.subject == "MATH" }.shuffled()
            val reasUnseen = unseenPool.filter { it.subject == "REASONING" }.shuffled()
            val engUnseen = unseenPool.filter { it.subject == "ENGLISH" }.shuffled()

            val gkTarget = (targetCount * 25) / 85
            val mathTarget = (targetCount * 25) / 85
            val reasTarget = (targetCount * 25) / 85
            val engTarget = max(1, targetCount - (gkTarget + mathTarget + reasTarget))

            selected.addAll(gkUnseen.take(gkTarget))
            selected.addAll(mathUnseen.take(mathTarget))
            selected.addAll(reasUnseen.take(reasTarget))
            selected.addAll(engUnseen.take(engTarget))
        } else {
            selected.addAll(unseenPool.shuffled().take(targetCount))
        }

        // If unseen pool had fewer than targetCount, generate brand-new non-repeating questions on the fly and save to vault
        if (selected.size < targetCount) {
            val needed = targetCount - selected.size
            val targetSub = subjectFilter ?: "GK"
            val offsetSeed = fullVault.size + currentSeen.size + 101
            val freshGenerated = com.example.data.SopanPyqVaultSeed.generateProgrammaticBatch(
                subject = targetSub,
                chapterId = chapterId,
                startOffset = offsetSeed,
                count = needed + 15
            ).filter { it.id !in currentSeen && selected.none { s -> s.id == it.id } }

            val toUse = freshGenerated.take(needed)
            selected.addAll(toUse)
            viewModelScope.launch {
                repository.bulkAddUniqueQuestionsToVault(freshGenerated)
            }
        }

        markQuestionsAsSeen(selected)
        return selected
    }

    /**
     * Permanently appends +50 new unique PYQ-Pattern Bengali MCQs to the Question Vault for a Subject or Chapter
     * until the subject hits 1,000+ questions.
     */
    fun bulkAdd50QuestionsToVault(
        subject: String,
        chapterId: String? = null,
        chapterTitle: String? = null
    ) {
        if (_bulkGeneratingTarget.value != null) return
        val targetKey = chapterId ?: subject.uppercase()
        _bulkGeneratingTarget.value = targetKey

        val currentSubjectCount = questionVault.value.count { it.subject.equals(subject, ignoreCase = true) }
        if (currentSubjectCount >= 1000) {
            _bulkGeneratingTarget.value = null
            _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
                "🏆 $subject ভল্টে ইতিমধ্যে $currentSubjectCount / ১০০০+ প্রশ্ন সংরক্ষিত আছে!"
            else
                "🏆 $subject Vault already has $currentSubjectCount / 1000+ questions!"
            return
        }

        _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
            "⚡ Gemini AI ও PYQ ইঞ্জিন +৫০টি নতুন প্রশ্ন ভল্টে যোগ করছে..."
        else
            "⚡ Adding +50 new unique PYQ-Pattern Bengali questions to Vault..."

        viewModelScope.launch {
            val batch = GeminiClient.generateBulkVaultQuestions(
                subject = subject,
                chapterId = chapterId,
                chapterTitle = chapterTitle,
                startSeedIndex = questionVault.value.size + 1,
                targetCount = 55
            )
            var inserted = repository.bulkAddUniqueQuestionsToVault(batch)
            if (inserted < 50) {
                val extra = com.example.data.SopanPyqVaultSeed.generateProgrammaticBatch(
                    subject = subject.uppercase(),
                    chapterId = chapterId,
                    startOffset = questionVault.value.size + inserted + 500,
                    count = 55 - inserted
                )
                inserted += repository.bulkAddUniqueQuestionsToVault(extra)
            }
            _bulkGeneratingTarget.value = null
            val newSubTotal = questionVault.value.count { it.subject.equals(subject, ignoreCase = true) } + inserted
            _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
                "✅ +$inserted টি নতুন ইউনিক PYQ-প্যাটার্ন প্রশ্ন ভল্টে স্থায়ীভাবে সেভ হয়েছে! ($subject ভল্ট: $newSubTotal / ১০০০)"
            else
                "✅ Permanently added +$inserted new unique PYQ-pattern questions to Vault! ($subject Vault: $newSubTotal / 1000)"
        }
    }

    /**
     * Imports PYQs from the 4 SOPAN Academy WBP Warrior PYQ Books (PDF/TXT file or pasted text) via `gemini-2.5-flash`
     * and permanently stores them in the non-repeating Question Vault.
     */
    fun importPyqBookContent(
        rawText: String,
        base64Document: String? = null,
        documentMimeType: String? = null,
        subjectHint: String = "GK"
    ) {
        if (_isImportingPyqBook.value) return
        _isImportingPyqBook.value = true
        _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
            "📥 Gemini 2.5 Flash SOPAN Academy বুক থেকে PYQ প্রশ্ন, সাল ও ব্যাখ্যা এক্সট্র্যাক্ট করছে..."
        else
            "📥 Gemini 2.5 Flash is extracting PYQ questions, exam year tags & Bengali explanations..."

        viewModelScope.launch {
            val res = GeminiClient.extractPyqsFromBookContent(
                rawText = rawText,
                base64Document = base64Document,
                documentMimeType = documentMimeType,
                subjectHint = subjectHint
            )
            val extractedList = res.getOrDefault(emptyList())
            val addedCount = repository.bulkAddUniqueQuestionsToVault(extractedList)
            _isImportingPyqBook.value = false
            _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
                "✅ SOPAN বুক থেকে ${extractedList.size}টি প্রশ্ন শনাক্ত হয়েছে এবং $addedCount টি নতুন প্রশ্ন স্থায়ীভাবে ভল্টে যুক্ত হয়েছে!"
            else
                "✅ Extracted ${extractedList.size} PYQs ($addedCount brand-new unique questions saved to Vault)!"
        }
    }

    fun startChapterPracticeTest(chapter: com.example.data.SopanChapterSpec) {
        _isRetryMistakesMode.value = false
        _activeTestId.value = "CHAPTER_${chapter.id}"
        _activeTestTitleEn.value = "${chapter.subject} • ${chapter.numberCode}. ${chapter.titleEn}"
        _activeTestTitleBn.value = "${chapter.subject} • ${chapter.numberCode}. ${chapter.titleBn}"
        val selected = pickNonRepeatingQuestions(
            subjectFilter = chapter.subject,
            chapterId = chapter.id,
            targetCount = 15
        )
        _questions.value = selected
        resetOmrSession(durationSeconds = max(300, selected.size * 42))
        _isExamPaused.value = false
        _isLiveExamActive.value = true
        _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
            "📘 অধ্যায়ভিত্তিক প্র্যাকটিস: ${chapter.titleBn} (${selected.size}টি আনসিন প্রশ্ন)"
        else
            "📘 Chapter Practice: ${chapter.titleEn} (${selected.size} Unseen Questions)"
        _currentTab.value = BottomNavTab.MOCK_TEST
    }

    fun startPyqExamGroupTest(examGroupId: String, examGroupLabelEn: String, examGroupLabelBn: String, subjectFilter: String? = null) {
        _isRetryMistakesMode.value = false
        _activeTestId.value = "PYQ_EXAM_${examGroupId}_${subjectFilter ?: "ALL"}"
        _activeTestTitleEn.value = "PYQ Archive: $examGroupLabelEn"
        _activeTestTitleBn.value = "PYQ আর্কাইভ: $examGroupLabelBn"
        val selected = pickNonRepeatingQuestions(
            subjectFilter = subjectFilter,
            examFamily = examGroupId,
            targetCount = 20
        )
        _questions.value = selected
        resetOmrSession(durationSeconds = max(300, selected.size * 42))
        _isExamPaused.value = false
        _isLiveExamActive.value = true
        _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
            "🏛️ বিগত বছরের প্রশ্নপত্র (২০০৫–বর্তমান): $examGroupLabelBn (${selected.size}টি আনসিন প্রশ্ন)"
        else
            "🏛️ Official PYQ Archive (2005–Present): $examGroupLabelEn (${selected.size} Unseen Questions)"
        _currentTab.value = BottomNavTab.MOCK_TEST
    }

    private fun loadCompletedScores(): Map<String, Pair<Double, Int>> {
        val result = mutableMapOf<String, Pair<Double, Int>>()
        WbpSampleData.testSeriesItems.forEach { item ->
            if (prefs.contains("score_${item.id}")) {
                val score = prefs.getFloat("score_${item.id}", 0f).toDouble()
                val maxM = prefs.getInt("max_${item.id}", item.totalMarks)
                result[item.id] = score to maxM
            }
        }
        return result
    }

    private fun saveCompletedScore(testId: String, netScore: Double, maxMarks: Int) {
        prefs.edit()
            .putFloat("score_$testId", netScore.toFloat())
            .putInt("max_$testId", maxMarks)
            .apply()
        _completedTestScores.value = _completedTestScores.value + (testId to (netScore to maxMarks))
    }

    fun selectHomeDistrict(district: com.example.data.WbDistrict) {
        _selectedDistrict.value = district
        resetInterviewForDistrict(district)
    }

    fun resetInterviewForDistrict(district: com.example.data.WbDistrict = _selectedDistrict.value) {
        _interviewTurnCount.value = 1
        _latestInterviewScore.value = null
        _interviewTurns.value = listOf(
            com.example.data.InterviewChatTurn(
                id = System.currentTimeMillis(),
                isCandidate = false,
                speakerTitleBn = "চেয়ারম্যান (IPS DIG — ৩ সদস্যের WBPRB ইন্টারভিউ বোর্ড)",
                messageBn = "নমস্কার! ভেতরে আসুন, বসুন। আপনি ${district.nameBn} জেলা থেকে এসেছেন। আমাদের ১৫ নম্বরের পার্সোনালিটি টেস্টে আপনাকে স্বাগত।\n\n📌 বোর্ডের প্রথম প্রশ্ন (District GK):\n${district.firstQuestionBn}"
            )
        )
    }

    fun submitInterviewAnswer(answerText: String) {
        val clean = answerText.trim()
        if (clean.isBlank() || _isInterviewLoading.value) return
        val lastBoardQuestion = _interviewTurns.value.lastOrNull { !it.isCandidate }?.messageBn
            ?: _selectedDistrict.value.firstQuestionBn
        val currentTurnNum = _interviewTurnCount.value

        val candidateTurn = com.example.data.InterviewChatTurn(
            id = System.currentTimeMillis(),
            isCandidate = true,
            speakerTitleBn = "প্রার্থী (${_selectedDistrict.value.nameBn})",
            messageBn = clean
        )
        _interviewTurns.value = _interviewTurns.value + candidateTurn
        _isInterviewLoading.value = true

        viewModelScope.launch {
            val eval = GeminiClient.evaluateAndAskNextInterviewQuestion(
                district = _selectedDistrict.value,
                turnNumber = currentTurnNum,
                previousQuestionBn = lastBoardQuestion,
                candidateAnswer = clean
            )
            _latestInterviewScore.value = eval.scoreOutOf15
            _interviewTurnCount.value = currentTurnNum + 1

            val boardReply = com.example.data.InterviewChatTurn(
                id = System.currentTimeMillis() + 1,
                isCandidate = false,
                speakerTitleBn = eval.panelMemberBn,
                messageBn = eval.nextQuestionBn,
                scoreOutOf15 = eval.scoreOutOf15,
                strengthsBn = eval.strengthsBn,
                improvementTipsBn = eval.improvementTipsBn,
                modelAnswerBn = eval.modelAnswerBn
            )
            _interviewTurns.value = _interviewTurns.value + boardReply
            _isInterviewLoading.value = false
        }
    }

    fun toggleLanguage() {
        val next = if (_language.value == AppLanguage.BENGALI) AppLanguage.ENGLISH else AppLanguage.BENGALI
        _language.value = next
        prefs.edit().putString("lang", next.code).apply()
    }

    fun toggleDarkMode() {
        val next = !_isDarkMode.value
        _isDarkMode.value = next
        prefs.edit().putBoolean("dark_mode", next).apply()
    }

    fun selectExam(exam: ExamType) {
        _selectedExam.value = exam
        prefs.edit().putString("exam_type", exam.id).apply()
    }

    fun selectTab(tab: BottomNavTab) {
        _currentTab.value = tab
    }

    fun startTestSeriesItem(item: com.example.data.TestSeriesItem) {
        _isRetryMistakesMode.value = false
        _activeTestId.value = item.id
        _activeTestTitleEn.value = item.titleEn
        _activeTestTitleBn.value = item.titleBn
        _questions.value = pickNonRepeatingQuestions(
            subjectFilter = item.subjectFilter,
            targetCount = item.questionCount
        )
        resetOmrSession(durationSeconds = item.durationMinutes * 60)
        _isExamPaused.value = false
        _isLiveExamActive.value = true
        _mockStatusBanner.value = null
        _currentTab.value = BottomNavTab.MOCK_TEST
    }

    fun viewSolutionsForTestSeriesItem(item: com.example.data.TestSeriesItem) {
        _isRetryMistakesMode.value = false
        _activeTestId.value = item.id
        _activeTestTitleEn.value = item.titleEn
        _activeTestTitleBn.value = item.titleBn
        val subset = questionVault.value.let { vault ->
            if (item.subjectFilter == null) vault.take(item.questionCount)
            else vault.filter { it.subject.equals(item.subjectFilter, ignoreCase = true) }.take(item.questionCount)
        }
        _questions.value = subset.ifEmpty { WbpSampleData.buildQuestionsForTest(item.subjectFilter, item.questionCount) }
        resetOmrSession(durationSeconds = item.durationMinutes * 60)
        timerJob?.cancel()
        _isTestSubmitted.value = true
        _isLiveExamActive.value = true
        _currentTab.value = BottomNavTab.MOCK_TEST
    }

    fun startQuickDailyQuiz() {
        _isRetryMistakesMode.value = false
        _activeTestId.value = "DAILY_QUIZ_10"
        _activeTestTitleEn.value = "10-Minute Daily Speed Quiz"
        _activeTestTitleBn.value = "১০ মিনিটের ডেইলি স্পিড কুইজ"
        _questions.value = pickNonRepeatingQuestions(subjectFilter = null, targetCount = 10)
        resetOmrSession(durationSeconds = 600)
        _isExamPaused.value = false
        _isLiveExamActive.value = true
        _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
            "⚡ ১০ মিনিটের ডেইলি কুইজ শুরু হয়েছে! (নন-রিপিটিং ভল্ট প্রশ্ন • +1 / -0.25)"
        else
            "⚡ 10-Minute Quick Daily Quiz Started! (Non-Repeating Vault Questions • +1 / -0.25)"
        _currentTab.value = BottomNavTab.MOCK_TEST
    }

    fun startFullPracticeSet(subjectFilter: String? = null) {
        val matchingSeries = WbpSampleData.testSeriesItems.firstOrNull {
            if (subjectFilter == null) it.isFullMock else it.subjectFilter.equals(subjectFilter, ignoreCase = true)
        }
        if (matchingSeries != null) {
            startTestSeriesItem(matchingSeries)
            return
        }
        _isRetryMistakesMode.value = false
        val list = pickNonRepeatingQuestions(subjectFilter = subjectFilter, targetCount = 25)
        _questions.value = list
        resetOmrSession(durationSeconds = max(300, _questions.value.size * 42))
        _isExamPaused.value = false
        _isLiveExamActive.value = true
        _mockStatusBanner.value = null
        _currentTab.value = BottomNavTab.MOCK_TEST
    }

    /**
     * Launches a custom mini-quiz made only of questions from the user's Mistake Notebook ("ভুল সংশোধনী খাতা").
     */
    fun startRetryMistakesQuiz() {
        val savedList = mistakeNotebook.value
        val mistakeQuestions = savedList
            .filter { it.isWrongAnswer }
            .ifEmpty { savedList }
            .map { it.toQuizQuestion() }

        if (mistakeQuestions.isEmpty()) return

        _isRetryMistakesMode.value = true
        _activeTestId.value = "MISTAKE_RETRY_QUIZ"
        _activeTestTitleEn.value = "Mistake Notebook Retry Quiz (${mistakeQuestions.size}Q)"
        _activeTestTitleBn.value = "ভুল সংশোধনী খাতা কুইজ (${mistakeQuestions.size}টি প্রশ্ন)"
        _questions.value = mistakeQuestions
        resetOmrSession(durationSeconds = max(180, mistakeQuestions.size * 42))
        _isExamPaused.value = false
        _isLiveExamActive.value = true
        _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
            "📓 ভুল সংশোধনী খাতা কুইজ: আপনার পূর্বের ${mistakeQuestions.size}টি ভুল/বুকমার্ক প্রশ্নের স্পেশাল টেস্ট!"
        else
            "📓 Retry My Mistakes Mini-Quiz: Practicing ${mistakeQuestions.size} saved mistake/bookmarked questions!"
        _currentTab.value = BottomNavTab.MOCK_TEST
    }

    fun toggleBookmark(question: QuizQuestion) {
        viewModelScope.launch {
            val isNowBookmarked = repository.toggleQuestionBookmark(question)
            _mockStatusBanner.value = if (isNowBookmarked) {
                if (_language.value == AppLanguage.BENGALI)
                    "⭐ প্রশ্নটি 'ভুল সংশোধনী ও বুকমার্ক খাতায়' সেভ করা হয়েছে!"
                else
                    "⭐ Saved to Mistake Notebook & Bookmarks!"
            } else {
                if (_language.value == AppLanguage.BENGALI)
                    "বুকমার্ক সরানো হয়েছে।"
                else
                    "Bookmark removed."
            }
        }
    }

    fun removeFromMistakeNotebook(questionId: String) {
        viewModelScope.launch {
            repository.removeMistakeOrMastered(questionId)
        }
    }

    fun generateNewAiMockTest(customTopicPrompt: String? = null) {
        if (_isGeneratingAiMock.value) return
        _isGeneratingAiMock.value = true
        _isRetryMistakesMode.value = false
        _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
            "🤖 WBPRB AI পেপার সেটার নতুন মক টেস্ট তৈরি করছে..."
        else
            "🤖 WBPRB AI Paper Setter is generating a fresh Mock Test..."

        viewModelScope.launch {
            val result = GeminiClient.generateAiMockQuestions(_selectedExam.value.titleEn, customTopicPrompt)
            _isGeneratingAiMock.value = false
            result.fold(
                onSuccess = { (aiQuestions, rawJson) ->
                    repository.bulkAddUniqueQuestionsToVault(aiQuestions)
                    val nonRepeating = aiQuestions.filter { it.id !in _seenQuestionIds.value }
                        .ifEmpty { pickNonRepeatingQuestions(null, targetCount = 15) }
                    markQuestionsAsSeen(nonRepeating)
                    _activeTestId.value = "AI_FULL_MOCK"
                    _activeTestTitleEn.value = "AI Generated WBPRB Mock Test (${nonRepeating.size}Q)"
                    _activeTestTitleBn.value = "AI জেনারেটেড WBPRB মক টেস্ট (${nonRepeating.size}টি প্রশ্ন)"
                    _questions.value = nonRepeating
                    _lastGeneratedJson.value = rawJson
                    resetOmrSession(durationSeconds = max(300, nonRepeating.size * 42))
                    _isExamPaused.value = false
                    _isLiveExamActive.value = true
                    _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
                        "✅ নতুন WBPRB মক টেস্ট (${nonRepeating.size}টি আনসিন প্রশ্ন) লোড হয়েছে!"
                    else
                        "✅ Loaded ${nonRepeating.size} fresh unseen WBPRB Mock Test questions!"
                },
                onFailure = { err ->
                    _questions.value = pickNonRepeatingQuestions(null, targetCount = 15)
                    resetOmrSession(durationSeconds = 600)
                    _mockStatusBanner.value = err.message
                }
            )
        }
    }

    fun generateAiSectionalTest(subject: String) {
        if (_isGeneratingAiMock.value || _generatingSubjectSectional.value != null) return
        _generatingSubjectSectional.value = subject
        _isGeneratingAiMock.value = true
        _isRetryMistakesMode.value = false

        val subjectTitleEn = when (subject.uppercase()) {
            "GK" -> "General Awareness & WB GK"
            "MATH" -> "Elementary Mathematics"
            "REASONING" -> "Logical & Analytical Reasoning"
            "ENGLISH" -> "English Grammar & Vocabulary"
            else -> subject
        }
        val subjectTitleBn = when (subject.uppercase()) {
            "GK" -> "সাধারণ জ্ঞান ও পশ্চিমবঙ্গ GK"
            "MATH" -> "প্রাথমিক গণিত (মাধ্যমিক মান)"
            "REASONING" -> "লজিক্যাল ও অ্যানালিটিক্যাল রিজনিং"
            "ENGLISH" -> "ইংরেজি গ্রামার ও ভোকাবুলারি"
            else -> subject
        }

        _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
            "⚡ AI নতুন '$subjectTitleBn' সেকশনাল টেস্ট তৈরি করছে..."
        else
            "⚡ AI is generating a fresh '$subjectTitleEn' Sectional Test..."

        viewModelScope.launch {
            val result = GeminiClient.generateAiMockQuestions(
                examTitle = _selectedExam.value.titleEn,
                customTopicPrompt = null,
                subjectFilter = subject
            )
            _generatingSubjectSectional.value = null
            _isGeneratingAiMock.value = false
            result.fold(
                onSuccess = { (aiQuestions, rawJson) ->
                    repository.bulkAddUniqueQuestionsToVault(aiQuestions)
                    val nonRepeating = aiQuestions.filter { it.id !in _seenQuestionIds.value }
                        .ifEmpty { pickNonRepeatingQuestions(subject, targetCount = 15) }
                    markQuestionsAsSeen(nonRepeating)
                    _activeTestId.value = "AI_SEC_${subject.uppercase()}"
                    _activeTestTitleEn.value = "AI $subjectTitleEn Sectional Test"
                    _activeTestTitleBn.value = "AI $subjectTitleBn সেকশনাল টেস্ট"
                    _questions.value = nonRepeating
                    _lastGeneratedJson.value = rawJson
                    resetOmrSession(durationSeconds = max(300, nonRepeating.size * 42))
                    _isExamPaused.value = false
                    _isLiveExamActive.value = true
                    _mockStatusBanner.value = if (_language.value == AppLanguage.BENGALI)
                        "✅ নতুন '$subjectTitleBn' সেকশনাল টেস্ট (${nonRepeating.size}টি আনসিন প্রশ্ন) প্রস্তুত!"
                    else
                        "✅ Fresh '$subjectTitleEn' Sectional Test (${nonRepeating.size} Unseen Q) ready!"
                },
                onFailure = {
                    val fallback = pickNonRepeatingQuestions(subject, targetCount = 15)
                    _activeTestId.value = "AI_SEC_${subject.uppercase()}"
                    _activeTestTitleEn.value = "AI $subjectTitleEn Sectional Test"
                    _activeTestTitleBn.value = "AI $subjectTitleBn সেকশনাল টেস্ট"
                    _questions.value = fallback
                    resetOmrSession(durationSeconds = max(300, fallback.size * 42))
                    _isExamPaused.value = false
                    _isLiveExamActive.value = true
                }
            )
        }
    }

    fun toggleExamPause() {
        if (_isTestSubmitted.value) return
        _isExamPaused.value = !_isExamPaused.value
    }

    fun exitLiveExamToHub() {
        _isExamPaused.value = true
        _isLiveExamActive.value = false
    }

    fun resumeOrOpenActiveLiveExam() {
        _isExamPaused.value = false
        _isLiveExamActive.value = true
    }

    fun saveAndNextQuestion() {
        val list = _questions.value
        val curIdx = _currentQuestionIndex.value
        val curQ = list.getOrNull(curIdx) ?: return
        // Ensure current question is marked visited so its status becomes Green (Answered) or Rose Red (Not Answered)
        _visitedQuestions.value = _visitedQuestions.value + curQ.id
        if (curIdx < list.lastIndex) {
            val nextIdx = curIdx + 1
            _currentQuestionIndex.value = nextIdx
            _visitedQuestions.value = _visitedQuestions.value + list[nextIdx].id
        }
    }

    private fun resetOmrSession(durationSeconds: Int) {
        _currentQuestionIndex.value = 0
        _selectedAnswers.value = emptyMap()
        _markedForReview.value = emptySet()
        _questionTimeSpent.value = emptyMap()
        _visitedQuestions.value = setOfNotNull(_questions.value.firstOrNull()?.id)
        _isTestSubmitted.value = false
        _isExamPaused.value = false
        startOrResetTimer(durationSeconds)
    }

    private fun startOrResetTimer(seconds: Int) {
        timerJob?.cancel()
        _totalDurationSeconds.value = seconds
        _remainingSeconds.value = seconds
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0 && !_isTestSubmitted.value) {
                delay(1000L)
                if (!_isTestSubmitted.value && !_isExamPaused.value) {
                    _remainingSeconds.value -= 1
                    // Increment per-question time spent for the active question
                    val activeQ = _questions.value.getOrNull(_currentQuestionIndex.value)
                    if (activeQ != null) {
                        val prev = _questionTimeSpent.value[activeQ.id] ?: 0
                        _questionTimeSpent.value = _questionTimeSpent.value + (activeQ.id to (prev + 1))
                    }
                    if (_remainingSeconds.value <= 0) {
                        submitMockTest()
                    }
                }
            }
        }
    }

    fun goToQuestion(index: Int) {
        val list = _questions.value
        if (index in list.indices) {
            _currentQuestionIndex.value = index
            _visitedQuestions.value = _visitedQuestions.value + list[index].id
        }
    }

    fun selectOmrOption(questionId: String, optionIndex: Int) {
        if (_isTestSubmitted.value) return
        _selectedAnswers.value = _selectedAnswers.value + (questionId to optionIndex)
        _visitedQuestions.value = _visitedQuestions.value + questionId
    }

    fun clearOmrOption(questionId: String) {
        if (_isTestSubmitted.value) return
        _selectedAnswers.value = _selectedAnswers.value - questionId
    }

    fun toggleMarkForReview(questionId: String) {
        if (_isTestSubmitted.value) return
        val current = _markedForReview.value
        _markedForReview.value = if (questionId in current) current - questionId else current + questionId
        _visitedQuestions.value = _visitedQuestions.value + questionId
    }

    /**
     * 5-State Question Status Tracking (Exact Testbook Colors):
     * - ANSWERED_AND_MARKED_FOR_REVIEW: Purple with green dot indicator
     * - MARKED_FOR_REVIEW (Unanswered): Purple
     * - ANSWERED: Emerald Green
     * - UNANSWERED (Visited but skipped): Rose Red
     * - NOT_VISITED: Subtle Slate/Zinc Grey
     */
    fun getPaletteStatus(questionId: String): QuestionPaletteStatus {
        val isMarked = questionId in _markedForReview.value
        val isAnswered = questionId in _selectedAnswers.value
        val isVisited = questionId in _visitedQuestions.value
        return when {
            isMarked && isAnswered -> QuestionPaletteStatus.ANSWERED_AND_MARKED_FOR_REVIEW
            isMarked -> QuestionPaletteStatus.MARKED_FOR_REVIEW
            isAnswered -> QuestionPaletteStatus.ANSWERED
            isVisited -> QuestionPaletteStatus.UNANSWERED
            else -> QuestionPaletteStatus.NOT_VISITED
        }
    }

    fun submitMockTest() {
        if (_isTestSubmitted.value) return
        _isTestSubmitted.value = true
        _isExamPaused.value = false
        timerJob?.cancel()

        val qList = _questions.value
        val answers = _selectedAnswers.value
        var correct = 0
        var wrong = 0
        val wrongQuestionsToSave = mutableListOf<Pair<QuizQuestion, Int>>()

        for (q in qList) {
            val chosen = answers[q.id]
            if (chosen != null) {
                if (chosen == q.correctIndex) {
                    correct++
                } else {
                    wrong++
                    wrongQuestionsToSave.add(q to chosen)
                }
            }
        }
        val unattempted = qList.size - (correct + wrong)
        val netScore = (correct * 1.0) - (wrong * 0.25)
        val attempted = correct + wrong
        val accuracy = if (attempted > 0) ((correct.toDouble() / attempted) * 100).roundToInt() else 0
        val timeTaken = max(1, _totalDurationSeconds.value - _remainingSeconds.value)

        // Save score for the active Test Series card so the Hub displays "Re-attempt / View Solutions" with score
        saveCompletedScore(_activeTestId.value, netScore, qList.size)

        // Increment streak on test completion
        val updatedStreak = _dailyStreak.value + 1
        _dailyStreak.value = updatedStreak
        prefs.edit().putInt("daily_streak", updatedStreak).apply()

        viewModelScope.launch {
            // Automatically record every wrong answer into the Mistake Notebook ("ভুল সংশোধনী খাতা")
            wrongQuestionsToSave.forEach { (wrongQ, wrongIdx) ->
                repository.recordWrongAnswer(wrongQ, wrongIdx)
            }

            val titleToSave = if (_isRetryMistakesMode.value) {
                "Retry Mistakes • ${_selectedExam.value.titleEn}"
            } else {
                _activeTestTitleEn.value.ifBlank { _selectedExam.value.titleEn }
            }
            repository.saveMockResult(
                MockTestResultEntity(
                    examTitle = titleToSave,
                    totalQuestions = qList.size,
                    correctCount = correct,
                    wrongCount = wrong,
                    unattemptedCount = unattempted,
                    netScore = netScore,
                    maxMarks = qList.size * 1.0,
                    accuracyPercent = accuracy,
                    timeTakenSeconds = timeTaken
                )
            )
        }
    }

    fun toggleSyllabusItem(item: SyllabusTopicEntity) {
        viewModelScope.launch {
            repository.toggleSyllabusTopic(item)
        }
    }

    fun sendTutorQuestion(prompt: String) {
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isBlank() || _isAiTutorLoading.value) return
        _isAiTutorLoading.value = true
        viewModelScope.launch {
            repository.addChatMessage(isUser = true, text = cleanPrompt)

            if (GeminiClient.isMcqGenerationRequest(cleanPrompt)) {
                val genResult = GeminiClient.generateAiMockQuestions(_selectedExam.value.titleEn, cleanPrompt)
                genResult.onSuccess { (mcqList, jsonArrayStr) ->
                    _questions.value = mcqList
                    _lastGeneratedJson.value = jsonArrayStr
                    resetOmrSession(durationSeconds = max(300, mcqList.size * 60))
                    repository.addChatMessage(
                        isUser = false,
                        text = jsonArrayStr,
                        tag = "Structured JSON • Loaded ${mcqList.size} MCQs into OMR Mock Test"
                    )
                }.onFailure {
                    val reply = GeminiClient.askAiTutor(cleanPrompt, _language.value.label)
                    repository.addChatMessage(isUser = false, text = reply, tag = "WBPRB AI Tutor")
                }
            } else {
                val reply = GeminiClient.askAiTutor(
                    userPrompt = cleanPrompt,
                    preferredLanguage = _language.value.label
                )
                repository.addChatMessage(
                    isUser = false,
                    text = reply,
                    tag = "বিস্তারিত + শর্টকাট ট্রিক • WBPRB Expert"
                )
            }
            _isAiTutorLoading.value = false
        }
    }

    fun sendTutorPhotoQuestion(bitmap: android.graphics.Bitmap, caption: String = "") {
        if (_isAiTutorLoading.value) return
        _isAiTutorLoading.value = true
        viewModelScope.launch {
            val userMsg = if (caption.isNotBlank()) {
                "📷 [Question Photo Uploaded] $caption"
            } else {
                if (_language.value == AppLanguage.BENGALI)
                    "📷 [বই/পিডিএফ থেকে প্রশ্নের ছবি আপলোড করা হয়েছে — বিস্তারিত ও ৩০ সেকেন্ডের শর্টকাট সমাধান দিন]"
                else
                    "📷 [Uploaded Question Photo — Extract & solve with Traditional + 30s Shortcut]"
            }
            repository.addChatMessage(
                isUser = true,
                text = userMsg,
                tag = "📷 Photo Doubt Upload"
            )

            val outputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 82, outputStream)
            val base64Jpeg = android.util.Base64.encodeToString(
                outputStream.toByteArray(),
                android.util.Base64.NO_WRAP
            )

            val reply = GeminiClient.askAiTutorWithImage(
                base64Jpeg = base64Jpeg,
                userCaption = caption.trim(),
                preferredLanguage = _language.value.label
            )
            repository.addChatMessage(
                isUser = false,
                text = reply,
                tag = "📷 Photo Doubt Solved • বিস্তারিত + শর্টকাট ট্রিক"
            )
            _isAiTutorLoading.value = false
        }
    }

    fun clearTutorChat() {
        viewModelScope.launch {
            repository.clearChatHistory()
        }
    }

    fun savePetRun(
        postName: String,
        gender: String,
        distanceMeters: Int,
        timeSeconds: Int,
        targetSeconds: Int,
        isQualified: Boolean,
        pacePerKm: String
    ) {
        viewModelScope.launch {
            repository.savePetRunLog(
                PetRunLogEntity(
                    postName = postName,
                    gender = gender,
                    distanceMeters = distanceMeters,
                    timeSeconds = timeSeconds,
                    targetSeconds = targetSeconds,
                    isQualified = isQualified,
                    pacePerKm = pacePerKm
                )
            )
        }
    }

    fun deletePetRun(id: Int) {
        viewModelScope.launch {
            repository.deletePetRunLog(id)
        }
    }

    // Module 5: Quick Revision Pocket Cards ("এক নজরে রিভিশন") mastery persistence
    private val _revisedPocketCardIds = MutableStateFlow<Set<String>>(
        prefs.getStringSet("revised_pocket_cards", emptySet())?.toSet() ?: emptySet()
    )
    val revisedPocketCardIds: StateFlow<Set<String>> = _revisedPocketCardIds.asStateFlow()

    fun togglePocketCardRevised(cardId: String) {
        val updated = _revisedPocketCardIds.value.toMutableSet()
        if (updated.contains(cardId)) {
            updated.remove(cardId)
        } else {
            updated.add(cardId)
        }
        _revisedPocketCardIds.value = updated
        prefs.edit().putStringSet("revised_pocket_cards", updated).apply()
    }
}
