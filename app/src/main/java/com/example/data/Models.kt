package com.example.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AppLanguage(val code: String, val label: String) {
    BENGALI("BN", "বাংলা"),
    ENGLISH("EN", "English")
}

enum class ExamType(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val totalMarks: Int,
    val durationMinutes: Int,
    val positiveMark: Double,
    val negativeMark: Double
) {
    CONSTABLE(
        id = "CONSTABLE_85",
        titleEn = "WBP/KP Constable (85 Marks)",
        titleBn = "WBP/KP কনস্টেবল (৮৫ নম্বর)",
        totalMarks = 85,
        durationMinutes = 60,
        positiveMark = 1.0,
        negativeMark = 0.25
    ),
    SI_PRELIMS(
        id = "SI_200",
        titleEn = "WBP/KP SI Prelims (200 Marks)",
        titleBn = "WBP/KP সাব-ইন্সপেক্টর প্রিলিমিনারি (২০০ নম্বর)",
        totalMarks = 200,
        durationMinutes = 90,
        positiveMark = 1.0,
        negativeMark = 0.25
    )
}

enum class SubjectCategory(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val subtitleEn: String,
    val subtitleBn: String,
    val constableMarks: Int,
    val siMarks: Int
) {
    GK(
        id = "GK",
        titleEn = "General Awareness & GK",
        titleBn = "সাধারণ জ্ঞান ও কারেন্ট অ্যাফেয়ার্স",
        subtitleEn = "25 Marks - West Bengal GK, History, Geography, Polity, Current Affairs",
        subtitleBn = "২৫ নম্বর - পশ্চিমবঙ্গের ভূগোল, ইতিহাস, সংবিধান, বিজ্ঞান ও সাম্প্রতিক ঘটনা",
        constableMarks = 25,
        siMarks = 100
    ),
    MATH(
        id = "MATH",
        titleEn = "Elementary Mathematics",
        titleBn = "প্রাথমিক গণিত (মাধ্যমিক মান)",
        subtitleEn = "25 Marks - Madhyamik standard Arithmetic & Mensuration",
        subtitleBn = "২৫ নম্বর - মাধ্যমিক স্তরের পাটিগণিত ও পরিমিতি (নিমেষে অঙ্ক)",
        constableMarks = 25,
        siMarks = 50
    ),
    REASONING(
        id = "REASONING",
        titleEn = "Reasoning & Logical Analysis",
        titleBn = "রিজনিং ও জি.আই (লজিক্যাল অ্যানালিসিস)",
        subtitleEn = "25 Marks - Coding-Decoding, Series, Blood Relation, Puzzles",
        subtitleBn = "২৫ নম্বর - সিরিজ, রক্তের সম্পর্ক, দিক নির্ণয় ও লজিক্যাল পাজল",
        constableMarks = 25,
        siMarks = 50
    ),
    ENGLISH(
        id = "ENGLISH",
        titleEn = "English",
        titleBn = "ইংরেজি (English Grammar & Vocab)",
        subtitleEn = "10 Marks - Prepositions, Voice, Narration, Idioms, Error Spotting",
        subtitleBn = "১০ নম্বর - Preposition, Voice Change, Narration, One-word ও Synonym",
        constableMarks = 10,
        siMarks = 0
    )
}

/**
 * Exact Structured Question Database Schema:
 * - `id`: Unique string ID
 * - `subject`: `'GK' | 'Math' | 'Reasoning' | 'English'`
 * - `chapter`: Exact chapter name mapped dynamically to the chosen subject
 * - `is_pyq`: Boolean (`true` or `false`)
 * - `pyq_exam`: String (e.g., `"WBP Constable Main"`, `"KP Constable Prelims"`, `"WBP SI"`, `"KP SI / Sergeant"`, `"WBP Lady Constable"`, `"Excise / Abgari"`, `"Jail Police Warder"`)
 * - `pyq_year`: String (`2005` to `2026`)
 * - `question_bn`: Question text (in Bengali for GK/Math/Reasoning; in English for English subject)
 * - `question_image`: Optional Base64 compressed image string (specifically unlocked when `subject === 'Reasoning'`)
 * - `options_bn`: Array of 4 strings `[Option A, Option B, Option C, Option D]`
 * - `option_images`: Optional array of 4 Base64 compressed image strings `[imgA, imgB, imgC, imgD]` (unlocked when `subject === 'Reasoning'`)
 * - `correct_index`: Number (`0` for A, `1` for B, `2` for C, `3` for D)
 * - `explanation_bn`: Step-by-step solution/shortcut in Bengali
 */
object StructuredQuestionSchema {
    val subjects = listOf("GK", "Math", "Reasoning", "English")

    val gkChapters = listOf(
        "History",
        "Geography",
        "West Bengal Static GK",
        "Polity",
        "Economy",
        "General Science",
        "Computer",
        "Current Affairs"
    )

    val mathChapters = listOf(
        "Percentage",
        "Ratio & Proportion",
        "Mixture & Alligation",
        "Average",
        "Problems on Ages",
        "Simple Interest",
        "Compound Interest",
        "Profit & Loss",
        "Partnership",
        "Time Speed & Distance",
        "Boat & Stream",
        "Time & Work",
        "Pipe & Cistern",
        "Number System",
        "Simplification",
        "LCM & HCF",
        "Mensuration",
        "Geometry"
    )

    val reasoningChapters = listOf(
        "Number Series",
        "Letter Series",
        "Classification",
        "Analogy",
        "Coding & Decoding",
        "Missing Number",
        "Direction Test",
        "Blood Relation",
        "Ranking & Order",
        "Sitting Arrangement",
        "Mathematical Operations",
        "Clock & Calendar",
        "Statement & Conclusion",
        "Counting Figures",
        "Incomplete Figure",
        "Dice & Cubes",
        "Mirror & Water Image",
        "Paper Cutting & Folding",
        "Non-Verbal Series",
        "Venn Diagram"
    )

    val englishChapters = listOf(
        "Prepositions",
        "Phrasal Verbs",
        "Synonyms & Antonyms",
        "Idioms & Phrases",
        "Spotting Errors",
        "Sentence Rearrangement (PQRS)",
        "Correct Spelling",
        "Voice & Narration",
        "One-Word Substitution"
    )

    val pyqExams = listOf(
        "WBP Constable Main",
        "KP Constable Prelims",
        "WBP SI",
        "KP SI / Sergeant",
        "WBP Lady Constable",
        "Excise / Abgari",
        "Jail Police Warder"
    )

    val pyqYears: List<String> = (2026 downTo 2005).map { it.toString() }

    fun chaptersForSubject(subject: String): List<String> {
        return when (subject.uppercase().trim()) {
            "MATH" -> mathChapters
            "REASONING", "GI" -> reasoningChapters
            "ENGLISH" -> englishChapters
            else -> gkChapters
        }
    }

    fun displaySubjectLabel(subject: String): String {
        return when (subject.uppercase().trim()) {
            "MATH" -> "Math"
            "REASONING", "GI" -> "Reasoning"
            "ENGLISH" -> "English"
            else -> "GK"
        }
    }

    fun normalizeSubjectCode(subject: String): String {
        return when (subject.uppercase().trim()) {
            "MATH", "MATHEMATICS" -> "MATH"
            "REASONING", "GI" -> "REASONING"
            "ENGLISH" -> "ENGLISH"
            else -> "GK"
        }
    }

    fun chapterNameToChapterId(subject: String, chapterName: String): String {
        val matched = WbpPyqVaultSeed.findBestMatchingChapter(subject, chapterName)
        return matched.chapterId
    }

    fun pyqExamToExamFamily(pyqExam: String): String {
        val upper = pyqExam.uppercase()
        return when {
            upper.contains("LADY") -> "WBP_LADY_CONSTABLE"
            upper.contains("KP") && upper.contains("CONSTABLE") -> "KP_CONSTABLE"
            upper.contains("SI") || upper.contains("SERGEANT") -> "WBP_KP_SI"
            upper.contains("EXCISE") || upper.contains("ABGARI") -> "EXCISE_ABGARI"
            upper.contains("JAIL") || upper.contains("WARDER") -> "JAIL_WARDER"
            upper.contains("WIRELESS") || upper.contains("AGRAGAMI") || upper.contains("FIRE") || upper.contains("NVF") -> "FIRE_WIRELESS_NVF"
            else -> "WBP_CONSTABLE"
        }
    }
}

@Serializable
data class QuizQuestion(
    val id: String,
    val subject: String, // GK, MATH, REASONING, ENGLISH
    val questionEn: String = "",
    @SerialName("question_bn")
    val questionBn: String,
    val optionsEn: List<String> = emptyList(),
    @SerialName("options_bn")
    val optionsBn: List<String>,
    @SerialName("correct_index")
    val correctIndex: Int,
    val explanationEn: String = "",
    @SerialName("explanation_bn")
    val explanationBn: String,
    val shortcutTrickBn: String = "",
    val shortcutTrickEn: String = "",
    val examYearTag: String = "",
    val chapterId: String = "",
    val examFamily: String = "",
    val chapter: String = "",
    @SerialName("is_pyq")
    val isPyq: Boolean = false,
    @SerialName("pyq_exam")
    val pyqExam: String = "",
    @SerialName("pyq_year")
    val pyqYear: String = "",
    @SerialName("question_image")
    val questionImage: String = "",
    @SerialName("option_images")
    val optionImages: List<String> = listOf("", "", "", "")
) {
    /**
     * Decouples App UI language from Exam Question language:
     * - GK, MATH, and REASONING questions are ALWAYS displayed in Bengali (বাংলা) matching the real WBPRB paper.
     * - Only ENGLISH subject questions are displayed in English.
     */
    fun displayQuestionText(): String =
        if (subject.equals("ENGLISH", ignoreCase = true)) {
            questionEn.ifBlank { questionBn }
        } else {
            questionBn.ifBlank { questionEn }
        }

    /**
     * - GK, MATH, and REASONING options are ALWAYS in Bengali (বাংলা).
     * - Only ENGLISH subject options are in English.
     */
    fun displayOptions(): List<String> =
        if (subject.equals("ENGLISH", ignoreCase = true)) {
            optionsEn.ifEmpty { optionsBn }
        } else {
            optionsBn.ifEmpty { optionsEn }
        }

    /**
     * Step-by-step explanations and shortcuts are ALWAYS shown in Bengali (বাংলা) for all 4 subjects, including English Grammar.
     */
    fun displayExplanationBn(): String = explanationBn.ifBlank { explanationEn }

    fun displayShortcutBn(): String = shortcutTrickBn.ifBlank { shortcutTrickEn }

    fun effectiveChapterName(): String {
        if (chapter.isNotBlank()) return chapter
        if (chapterId.isNotBlank()) {
            val matched = WbpPyqVaultSeed.wbpChapters.firstOrNull { it.chapterId.equals(chapterId, ignoreCase = true) }
            if (matched != null) return matched.titleEn
            if (!chapterId.contains("_")) return chapterId
        }
        val fallbackList = StructuredQuestionSchema.chaptersForSubject(subject)
        return fallbackList.firstOrNull() ?: "General"
    }

    fun effectiveIsPyq(): Boolean {
        if (isPyq) return true
        return pyqExam.isNotBlank() ||
            (examYearTag.isNotBlank() &&
                !examYearTag.contains("Expected", ignoreCase = true) &&
                !examYearTag.contains("Custom", ignoreCase = true) &&
                !examYearTag.contains("Uploaded", ignoreCase = true))
    }

    fun effectivePyqBadge(): String {
        if (isPyq && pyqExam.isNotBlank()) {
            return if (pyqYear.isNotBlank()) "$pyqExam ($pyqYear)" else pyqExam
        }
        return examYearTag
    }

    fun hasQuestionImage(): Boolean = questionImage.isNotBlank()

    fun optionImageAt(index: Int): String = optionImages.getOrNull(index)?.trim().orEmpty()

    fun hasOptionImage(index: Int): Boolean = optionImageAt(index).isNotBlank()

    val isUserUploaded: Boolean
        get() = id.startsWith("MANUAL_", ignoreCase = true) ||
            id.startsWith("SMART_SCAN_", ignoreCase = true) ||
            id.startsWith("UPLOADED_", ignoreCase = true) ||
            examFamily.contains("USER_UPLOADED", ignoreCase = true) ||
            examFamily.contains("MANUAL_STUDIO", ignoreCase = true) ||
            examYearTag.contains("Uploaded", ignoreCase = true) ||
            examYearTag.contains("Scanned", ignoreCase = true)
}

data class ScanVerificationReport(
    val beforeScanTotal: Int,
    val extractedTotalCount: Int,
    val newlyAddedCount: Int,
    val duplicatesIgnoredCount: Int,
    val afterScanTotal: Int,
    val subjectBreakdown: Map<String, Int>,
    val extractedQuestions: List<QuizQuestion>,
    val newlyAddedIds: Set<String> = emptySet(),
    val duplicateIds: Set<String> = emptySet(),
    val timestamp: Long = System.currentTimeMillis()
)

enum class QuestionPaletteStatus {
    NOT_VISITED,
    UNANSWERED,
    ANSWERED,
    MARKED_FOR_REVIEW,
    ANSWERED_AND_MARKED_FOR_REVIEW
}

enum class TestSeriesFilter(val id: String, val labelEn: String, val labelBn: String) {
    ALL("ALL", "All Tests", "All Tests"),
    FULL_MOCK("FULL_MOCK", "Full Length Mocks (85M)", "Full Length Mocks (85M)"),
    SUBJECT_WISE("SUBJECT_WISE", "Subject Sectional Tests", "Subject Sectional Tests"),
    CHAPTER_WISE("CHAPTER_WISE", "Chapter-Wise Tests", "Chapter-Wise Tests"),
    PYQ_VAULT("PYQ_VAULT", "PYQ Papers (2005–2024)", "PYQ Papers (2005–2024)"),
    MISTAKE_RETRY("MISTAKE_RETRY", "Mistake Notebook Retry", "Mistake Notebook Retry"),
    MY_UPLOADED("MY_UPLOADED", "🗂️ Manage Question Database", "🗂️ Manage Question Database")
}

data class TestSeriesItem(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val subtitleEn: String,
    val subtitleBn: String,
    val isFullMock: Boolean,
    val subjectFilter: String?, // null for Full Mock, or "GK", "MATH", "REASONING", "ENGLISH"
    val questionCount: Int,
    val totalMarks: Int,
    val durationMinutes: Int,
    val mediumTagEn: String = "Bengali Medium",
    val mediumTagBn: String = "বাংলা মাধ্যম • Bengali Medium"
)

enum class CandidateGender(val labelEn: String, val labelBn: String) {
    MALE("Male Candidate", "পুরুষ প্রার্থী (Male)"),
    FEMALE("Female Candidate", "মহিলা প্রার্থী (Female)")
}

enum class CandidateCategory(val labelEn: String, val labelBn: String, val isRelaxedHillSt: Boolean) {
    GENERAL("General / Unreserved", "জেনারেল (General)", false),
    OBC("OBC-A / OBC-B", "ওবিসি (OBC-A/B)", false),
    SC("Scheduled Caste (SC)", "তফসিলি জাতি (SC)", false),
    ST("Scheduled Tribe (ST)", "তফসিলি উপজাতি (ST)", true),
    GORKHA("Gorkha / Garhwali / Rajbanshi", "গোর্খা / গাড়োয়ালি / রাজবংশী", true)
}

enum class PolicePost(val labelEn: String, val labelBn: String) {
    CONSTABLE("WBP / KP Constable", "কনস্টেবল (WBP / KP)"),
    SI("WBP / KP Sub-Inspector (SI)", "সাব-ইন্সপেক্টর (SI)")
}

/**
 * Role-Based Access Control (RBAC) Roles:
 * - UNAUTHENTICATED: Must log in via Dual-Mode Authentication Screen
 * - ASPIRANT: Student mode (Question creation, edit/delete, and DB tools strictly hidden; auto-personalized by Profile)
 * - ADMIN: Full Admin Mode (Unlocks 🛠️ Admin Panel, 1–50 Batch Studio, Question Explorer, Aspirants Directory, Cloud DB Sync)
 */
enum class UserRole {
    UNAUTHENTICATED,
    ASPIRANT,
    ADMIN
}

/**
 * Aspirant Caste / Category options from Sign-Up:
 * `General (UR)`, `OBC-A`, `OBC-B`, `SC`, `ST`, `Gorkha / Garhwali / Rajbanshi`
 */
enum class AspirantCasteCategory(
    val displayLabel: String,
    val shortBadge: String,
    val safeCutoffOutOf85: Double,
    val isRelaxedHillSt: Boolean
) {
    GENERAL("General (UR)", "UR", 54.5, false),
    OBC_A("OBC-A", "OBC-A", 46.5, false),
    OBC_B("OBC-B", "OBC-B", 51.0, false),
    SC("SC", "SC", 47.0, false),
    ST("ST", "ST", 41.5, true),
    GORKHA("Gorkha / Garhwali / Rajbanshi", "Gorkha/Hill", 43.0, true);

    fun toCandidateCategory(): CandidateCategory = when (this) {
        GENERAL -> CandidateCategory.GENERAL
        OBC_A, OBC_B -> CandidateCategory.OBC
        SC -> CandidateCategory.SC
        ST -> CandidateCategory.ST
        GORKHA -> CandidateCategory.GORKHA
    }

    companion object {
        fun fromNameOrLabel(value: String): AspirantCasteCategory {
            val trimmed = value.trim()
            return entries.firstOrNull {
                it.name.equals(trimmed, ignoreCase = true) ||
                    it.displayLabel.equals(trimmed, ignoreCase = true) ||
                    it.shortBadge.equals(trimmed, ignoreCase = true)
            } ?: GENERAL
        }
    }
}

/**
 * Aspirant Target Exam options from Sign-Up:
 * `WBP Constable (85 Marks)`, `KP Constable (85 Marks)`, `WBP/KP Sub-Inspector (SI - 200 Marks)`, `WBP Lady Constable (85 Marks)`
 */
enum class AspirantTargetExam(
    val displayLabel: String,
    val shortBadge: String,
    val examType: ExamType,
    val policePost: PolicePost,
    val defaultGender: CandidateGender
) {
    WBP_CONSTABLE(
        displayLabel = "WBP Constable (85 Marks)",
        shortBadge = "WBP Constable (85M)",
        examType = ExamType.CONSTABLE,
        policePost = PolicePost.CONSTABLE,
        defaultGender = CandidateGender.MALE
    ),
    KP_CONSTABLE(
        displayLabel = "KP Constable (85 Marks)",
        shortBadge = "KP Constable (85M)",
        examType = ExamType.CONSTABLE,
        policePost = PolicePost.CONSTABLE,
        defaultGender = CandidateGender.MALE
    ),
    WBP_KP_SI(
        displayLabel = "WBP/KP Sub-Inspector (SI - 200 Marks)",
        shortBadge = "WBP/KP SI (200M)",
        examType = ExamType.SI_PRELIMS,
        policePost = PolicePost.SI,
        defaultGender = CandidateGender.MALE
    ),
    WBP_LADY_CONSTABLE(
        displayLabel = "WBP Lady Constable (85 Marks)",
        shortBadge = "WBP Lady Constable (85M)",
        examType = ExamType.CONSTABLE,
        policePost = PolicePost.CONSTABLE,
        defaultGender = CandidateGender.FEMALE
    );

    companion object {
        fun fromNameOrLabel(value: String): AspirantTargetExam {
            val trimmed = value.trim()
            return entries.firstOrNull {
                it.name.equals(trimmed, ignoreCase = true) ||
                    it.displayLabel.equals(trimmed, ignoreCase = true) ||
                    it.shortBadge.equals(trimmed, ignoreCase = true)
            } ?: WBP_CONSTABLE
        }
    }
}

@Serializable
data class AspirantProfile(
    val id: String,
    val fullName: String,
    val email: String,
    val password: String,
    val casteCategory: String = AspirantCasteCategory.GENERAL.name,
    val targetExam: String = AspirantTargetExam.WBP_CONSTABLE.name,
    val testsAttempted: Int = 0,
    val averageScore: Double = 0.0,
    val joinDate: String = "24 Oct 2026",
    val joinTimestamp: Long = System.currentTimeMillis(),
    val timestamp: Long = joinTimestamp
) {
    val resolvedCasteCategory: AspirantCasteCategory
        get() = AspirantCasteCategory.fromNameOrLabel(casteCategory)

    val resolvedTargetExam: AspirantTargetExam
        get() = AspirantTargetExam.fromNameOrLabel(targetExam)
}

@Serializable
data class TestAttemptRecord(
    val id: Int = 0,
    val examName: String,
    val totalQuestions: Int,
    val attempted: Int,
    val correct: Int,
    val wrong: Int,
    val netScore: Double,
    val maxMarks: Double,
    val accuracyPercent: Int,
    val timeTakenSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class CloudDbConfig(
    val isEnabled: Boolean = true,
    val provider: String = "FIREBASE_FIRESTORE", // "LOCAL_ONLY", "FIREBASE_FIRESTORE", "SUPABASE"
    val projectUrlOrId: String = "wbp-prep",
    val apiKeyOrToken: String = "",
    val firebaseConfigJson: String = "",
    val supabaseUrl: String = "",
    val supabaseAnonKey: String = "",
    val autoSyncEnabled: Boolean = true,
    val lastSyncStatus: String = "Firebase Firestore Provisioned & Active (Ready)",
    val lastSyncedAt: String = "",
    val lastSyncedTimestamp: Long = System.currentTimeMillis()
) {
    val isCloudConfigured: Boolean
        get() = isEnabled ||
            (provider.contains("FIREBASE", ignoreCase = true) && (firebaseConfigJson.isNotBlank() || projectUrlOrId.isNotBlank())) ||
            (provider == "SUPABASE" && supabaseUrl.isNotBlank() && supabaseAnonKey.isNotBlank()) ||
            firebaseConfigJson.isNotBlank() ||
            (supabaseUrl.isNotBlank() && supabaseAnonKey.isNotBlank())
}

@Serializable
data class FullDatabaseBackupBundle(
    val schemaVersion: Int = 2,
    val exportedAt: Long = System.currentTimeMillis(),
    val questions: List<QuizQuestion> = emptyList(),
    val users: List<AspirantProfile> = emptyList(),
    val testAttempts: List<TestAttemptRecord> = emptyList()
)

object WbpSampleData {
    val defaultSyllabusTopics = listOf(
        // GK (7 topics)
        SyllabusSeed("GK_1", "GK", "West Bengal Geography, Rivers & Districts", "পশ্চিমবঙ্গের ভূগোল, নদনদী ও জেলা পরিচয়", true),
        SyllabusSeed("GK_2", "GK", "Indian National Movement & Bengal Renaissance", "ভারতের স্বাধীনতা আন্দোলন ও বাংলার নবজাগরণ", true),
        SyllabusSeed("GK_3", "GK", "Indian Constitution, Fundamental Rights & Panchayati Raj", "ভারতীয় সংবিধান, মৌলিক অধিকার ও পঞ্চায়েতি রাজ", true),
        SyllabusSeed("GK_4", "GK", "Everyday General Science (Physics, Chemistry, Biology)", "দৈনন্দিন সাধারণ বিজ্ঞান (ভৌত ও জীবন বিজ্ঞান)", false),
        SyllabusSeed("GK_5", "GK", "National Parks, Wildlife Sanctuaries & Ramsar Sites of WB", "পশ্চিমবঙ্গের জাতীয় উদ্যান, অভয়ারণ্য ও রামসার সাইট", true),
        SyllabusSeed("GK_6", "GK", "Folk Dances, Festivals & UNESCO Heritage of Bengal", "বাংলার লোকনৃত্য, উৎসব ও ইউনেস্কো হেরিটেজ", false),
        SyllabusSeed("GK_7", "GK", "Current Affairs, Sports, Awards & WB State Schemes", "সাম্প্রতিক ঘটনা, খেলাধুলা, পুরস্কার ও রাজ্য সরকারি প্রকল্প", false),

        // MATH (7 topics)
        SyllabusSeed("MATH_1", "MATH", "Ratio, Proportion & Partnership", "অনুপাত, সমানুপাত ও অংশীদারি কারবার", true),
        SyllabusSeed("MATH_2", "MATH", "Percentage, Profit & Loss, Discount", "শতকরা, লাভ ও ক্ষতি এবং ছাড়", true),
        SyllabusSeed("MATH_3", "MATH", "Time & Work, Pipes & Cisterns", "সময় ও কার্য এবং নল ও চৌবাচ্চা", true),
        SyllabusSeed("MATH_4", "MATH", "Time, Speed, Distance, Train & Boat-Stream", "সময় ও দূরত্ব, ট্রেন এবং নৌকা ও স্রোত", false),
        SyllabusSeed("MATH_5", "MATH", "Simple & Compound Interest", "সরল সুদ ও জটিল সুদকষা", false),
        SyllabusSeed("MATH_6", "MATH", "LCM, HCF, Number System & Simplification", "ল.সা.গু, গ.সা.গু, সংখ্যা তত্ত্ব ও সরলীকরণ", true),
        SyllabusSeed("MATH_7", "MATH", "2D & 3D Mensuration (Area, Perimeter, Volume)", "পরিমিতি (আয়তক্ষেত্র, বর্গক্ষেত্র, বৃত্ত, চোঙ, শঙ্কু)", false),

        // REASONING (6 topics)
        SyllabusSeed("REAS_1", "REASONING", "Number & Alphabet Series Completion", "সংখ্যা ও বর্ণমালা সিরিজ (Number & Letter Series)", true),
        SyllabusSeed("REAS_2", "REASONING", "Coding-Decoding & Analogy", "কোডিং-ডিকোডিং ও সাদৃশ্য (Analogy)", true),
        SyllabusSeed("REAS_3", "REASONING", "Blood Relations & Direction Sense", "রক্তের সম্পর্ক (Blood Relation) ও দিক নির্ণয়", true),
        SyllabusSeed("REAS_4", "REASONING", "Syllogism & Logical Venn Diagrams", "ন্যায্য অনুমান (Syllogism) ও ভেন ডায়াগ্রাম", false),
        SyllabusSeed("REAS_5", "REASONING", "Seating Arrangement & Ranking Order", "সিটিং অ্যারেঞ্জমেন্ট ও ক্রম নির্ণয় (Ranking)", false),
        SyllabusSeed("REAS_6", "REASONING", "Mirror/Water Image, Clock & Calendar", "দর্পণ ও জল প্রতিবিম্ব, ঘড়ি এবং ক্যালেন্ডার", false),

        // ENGLISH (5 topics)
        SyllabusSeed("ENG_1", "ENGLISH", "Appropriate Prepositions & Articles", "Appropriate Prepositions ও Articles এর ব্যবহার", true),
        SyllabusSeed("ENG_2", "ENGLISH", "Voice Change & Narration Change", "Voice Change এবং Direct/Indirect Narration", true),
        SyllabusSeed("ENG_3", "ENGLISH", "Synonyms, Antonyms & Spelling Correction", "সমার্থক শব্দ (Synonyms), বিপরীত শব্দ ও সঠিক বানান", false),
        SyllabusSeed("ENG_4", "ENGLISH", "One Word Substitution & Idioms/Phrases", "এক কথায় প্রকাশ (One Word) ও Idioms/Phrases", false),
        SyllabusSeed("ENG_5", "ENGLISH", "Subject-Verb Agreement & Spotting Errors", "Subject-Verb Agreement ও Error Correction", false)
    )

    val preloadedQuestions = listOf(
        // --- GK QUESTIONS ---
        QuizQuestion(
            id = "Q_GK_1",
            subject = "GK",
            questionEn = "Which national park in West Bengal is famous for the Indian One-Horned Rhinoceros?",
            questionBn = "পশ্চিমবঙ্গের কোন জাতীয় উদ্যানটি একশৃঙ্গ গন্ডারের জন্য বিখ্যাত?",
            optionsEn = listOf("Sundarbans National Park", "Jaldapara National Park", "Neora Valley National Park", "Singalila National Park"),
            optionsBn = listOf("সুন্দরবন জাতীয় উদ্যান", "জলদাপাড়া জাতীয় উদ্যান", "নেওড়া ভ্যালি জাতীয় উদ্যান", "সিঙ্গালিলা জাতীয় উদ্যান"),
            correctIndex = 1,
            explanationEn = "Jaldapara National Park (located in Alipurduar district on the banks of the Torsa River) has the largest population of Indian One-Horned Rhinoceros in West Bengal, followed by Gorumara.",
            explanationBn = "আলিপুরদুয়ার জেলায় তোর্সা নদীর তীরে অবস্থিত জলদাপাড়া জাতীয় উদ্যান পশ্চিমবঙ্গের একশৃঙ্গ গন্ডারের প্রধান আবাসস্থল।",
            shortcutTrickBn = "মনে রাখার ট্রিক: তোর্সা নদীর তীরে জলদাপাড়া = একশৃঙ্গ গন্ডার; সিঙ্গালিলা = রেড পান্ডা।",
            shortcutTrickEn = "Memory Tip: Jaldapara (Torsa river) = One-Horned Rhino; Singalila = Red Panda."
        ),
        QuizQuestion(
            id = "Q_GK_2",
            subject = "GK",
            questionEn = "The Tropic of Cancer (23.5° N) passes through which of the following places in Nadia district of West Bengal?",
            questionBn = "পশ্চিমবঙ্গের নদিয়া জেলার কোন শহরের ওপর দিয়ে কর্কটক্রান্তি রেখা (২৩.৫° উত্তর) বিস্তৃত হয়েছে?",
            optionsEn = listOf("Kalyani", "Krishnanagar", "Chakdaha", "Ranaghat"),
            optionsBn = listOf("কল্যাণী", "কৃষ্ণনগর", "চাকদহ", "রানাঘাট"),
            correctIndex = 1,
            explanationEn = "The Tropic of Cancer passes through 5 districts in West Bengal: Purulia, Bankura, Purba Bardhaman, Paschim Bardhaman, and Nadia (near Krishnanagar/Dhubulia).",
            explanationBn = "পশ্চিমবঙ্গের ৫টি জেলার ওপর দিয়ে কর্কটক্রান্তি রেখা গেছে: পুরুলিয়া, বাঁকুড়া, পশ্চিম বর্ধমান, পূর্ব বর্ধমান এবং নদিয়া (কৃষ্ণনগর/ধুবুলিয়া)।",
            shortcutTrickBn = "ট্রিক: 'পূর্বা ও বাপ্পা নদীতে' -> পুরুলিয়া, বর্ধমান (পূর্ব ও পশ্চিম), বাঁকুড়া, নদিয়া (কৃষ্ণনগর)।",
            shortcutTrickEn = "5 WB Districts on Tropic of Cancer: Purulia, Bankura, Paschim Bardhaman, Purba Bardhaman, Nadia (Krishnanagar)."
        ),
        QuizQuestion(
            id = "Q_GK_3",
            subject = "GK",
            questionEn = "Who founded the 'Atmiya Sabha' in Kolkata in 1815?",
            questionBn = "১৮১৫ সালে কলকাতায় 'আত্মীয় সভা' কে প্রতিষ্ঠা করেছিলেন?",
            optionsEn = listOf("Ishwar Chandra Vidyasagar", "Debendranath Tagore", "Raja Ram Mohan Roy", "Keshab Chandra Sen"),
            optionsBn = listOf("ঈশ্বরচন্দ্র বিদ্যাসাগর", "দেবেন্দ্রনাথ ঠাকুর", "রাজা রামমোহন রায়", "কেশবচন্দ্র সেন"),
            correctIndex = 2,
            explanationEn = "Raja Ram Mohan Roy founded the Atmiya Sabha in 1815 in Kolkata to promote philosophical discussions on monotheism and social reform, later founding the Brahmo Sabha in 1828.",
            explanationBn = "১৮১৫ সালে রাজা রামমোহন রায় কলকাতায় একেশ্বরবাদ ও সমাজ সংস্কারের আলোচনার জন্য 'আত্মীয় সভা' এবং ১৮২৮ সালে 'ব্রাহ্ম সভা' প্রতিষ্ঠা করেন।",
            shortcutTrickBn = "১৮১৫ = আত্মীয় সভা (রামমোহন), ১৮৩৯ = তত্ত্ববোধিনী সভা (দেবেন্দ্রনাথ ঠাকুর)।",
            shortcutTrickEn = "1815 = Atmiya Sabha (Ram Mohan Roy), 1839 = Tattvabodhini Sabha (Debendranath Tagore)."
        ),
        QuizQuestion(
            id = "Q_GK_4",
            subject = "GK",
            questionEn = "Under which Article of the Indian Constitution can the President proclaim a Financial Emergency?",
            questionBn = "ভারতীয় সংবিধানের কোন ধারা অনুযায়ী রাষ্ট্রপতি 'আর্থিক জরুরি অবস্থা' (Financial Emergency) জারি করতে পারেন?",
            optionsEn = listOf("Article 352", "Article 356", "Article 360", "Article 368"),
            optionsBn = listOf("৩৫২ নং ধারা", "৩৫৬ নং ধারা", "৩৬০ নং ধারা", "৩৬৮ নং ধারা"),
            correctIndex = 2,
            explanationEn = "Article 352 deals with National Emergency, Article 356 with State Emergency (President's Rule), and Article 360 with Financial Emergency. Article 368 relates to Constitutional Amendment.",
            explanationBn = "৩৫২ ধারা: জাতীয় জরুরি অবস্থা, ৩৫৬ ধারা: রাজ্যে রাষ্ট্রপতি শাসন এবং ৩৬০ ধারা: আর্থিক জরুরি অবস্থা (+৪ করে বৃদ্ধি পায়)।",
            shortcutTrickBn = "নিমেষে ট্রিক: ৩৫২ (+৪) = ৩৫৬ (+৪) = ৩৬০ (জাতীয় -> রাজ্য -> আর্থিক জরুরি অবস্থা)।",
            shortcutTrickEn = "Shortcut: 352 (+4) = 356 (+4) = 360 (National -> State -> Financial Emergency)."
        ),
        QuizQuestion(
            id = "Q_GK_5",
            subject = "GK",
            questionEn = "What is the highest peak of West Bengal?",
            questionBn = "পশ্চিমবঙ্গের সর্বোচ্চ শৃঙ্গ কোনটি?",
            optionsEn = listOf("Phalut", "Sandakphu", "Tiger Hill", "Rishihat"),
            optionsBn = listOf("ফালুট", "সান্দাকফু", "টাইগার হিল", "ঋষিহাট"),
            correctIndex = 1,
            explanationEn = "Sandakphu (3,636 meters / 11,929 ft) located in the Singalila Ridge of Darjeeling district is the highest peak in West Bengal. Phalut (3,600 m) is the second highest.",
            explanationBn = "দার্জিলিং জেলার সিঙ্গালিলা শৈলশিরায় অবস্থিত সান্দাকফু (৩,৬৩৬ মিটার) পশ্চিমবঙ্গের সর্বোচ্চ শৃঙ্গ। দ্বিতীয় সর্বোচ্চ শৃঙ্গ হলো ফালুট (৩,৬০০ মিটার)।",
            shortcutTrickBn = "সান্দাকফু = ৩৬৩৬ মিটার (সিঙ্গালিলা রেঞ্জ)।",
            shortcutTrickEn = "Sandakphu = 3636 meters (Singalila Ridge, Darjeeling)."
        ),
        QuizQuestion(
            id = "Q_GK_6",
            subject = "GK",
            questionEn = "Which gas is primarily responsible for the 'Bhopal Gas Tragedy' of 1984?",
            questionBn = "১৯৮৪ সালের 'ভোপাল গ্যাস দুর্ঘটনা'-র জন্য প্রধানত কোন গ্যাসটি দায়ী ছিল?",
            optionsEn = listOf("Carbon Monoxide", "Methyl Isocyanate (MIC)", "Sulphur Dioxide", "Ammonia"),
            optionsBn = listOf("কার্বন মনোক্সাইড", "মিথাইল আইসোসায়ানেট (MIC)", "সালফার ডাই-অক্সাইড", "অ্যামোনিয়া"),
            correctIndex = 1,
            explanationEn = "Methyl Isocyanate (MIC - CH3NCO) leaked from the Union Carbide India Limited pesticide plant in Bhopal on December 2-3, 1984.",
            explanationBn = "১৯৮৪ সালের ২-৩ ডিসেম্বর মধ্যপ্রদেশের ভোপালে ইউনিয়ন কার্বাইড কারখানা থেকে বিষাক্ত মিথাইল আইসোসায়ানেট (MIC) গ্যাস নির্গত হয়েছিল।",
            shortcutTrickBn = "ভোপাল গ্যাস = MIC (Methyl Isocyanate - সাইয়ানাইড নয়, আইসোসায়ানেট!)।",
            shortcutTrickEn = "Bhopal Gas = MIC (Methyl Isocyanate, NOT Cyanide)."
        ),

        // --- ELEMENTARY MATHEMATICS QUESTIONS ---
        QuizQuestion(
            id = "Q_MATH_1",
            subject = "MATH",
            questionEn = "A train 150 meters long is running at a speed of 54 km/hr. In how many seconds will it cross a telegraph post?",
            questionBn = "১৫০ মিটার দীর্ঘ একটি ট্রেন ঘণ্টায় ৫৪ কিমি বেগে চললে, লাইনের পাশের একটি টেলিগ্রাফ পোস্টকে কত সেকেন্ডে অতিক্রম করবে?",
            optionsEn = listOf("8 seconds", "10 seconds", "12 seconds", "15 seconds"),
            optionsBn = listOf("৮ সেকেন্ড", "১০ সেকেন্ড", "১২ সেকেন্ড", "১৫ সেকেন্ড"),
            correctIndex = 1,
            explanationEn = "Convert speed from km/hr to m/s: 54 × (5/18) = 15 m/s. Time taken to cross a post = Length of train / Speed = 150 / 15 = 10 seconds.",
            explanationBn = "গতিবেগ = ৫৪ কিমি/ঘণ্টা = ৫৪ × (৫/১৮) মিটার/সেকেন্ড = ১৫ মিটার/সেকেন্ড। টেলিগ্রাফ পোস্ট অতিক্রম করা মানে ট্রেনের নিজের দৈর্ঘ্য অতিক্রম করা। সুতরাং সময় = ১৫০ / ১৫ = ১০ সেকেন্ড।",
            shortcutTrickBn = "নিমেষে অঙ্ক: কিমি/ঘণ্টা থেকে মি/সে করতে ৫/১৮ দিয়ে গুণ করুন -> ৫৪ × ৫/১৮ = ১৫ মি/সে; সময় = ১৫০/১৫ = ১০ সেকেন্ড!",
            shortcutTrickEn = "Shortcut: 18 km/hr = 5 m/s. Since 54 is 18×3, speed is 5×3 = 15 m/s. Time = 150/15 = 10s."
        ),
        QuizQuestion(
            id = "Q_MATH_2",
            subject = "MATH",
            questionEn = "If the cost price of 15 articles is equal to the selling price of 12 articles, find the profit percentage.",
            questionBn = "১৫টি দ্রব্যের ক্রয়মূল্য ১২টি দ্রব্যের বিক্রয়মূল্যের সমান হলে, শতকরা লাভের হার কত?",
            optionsEn = listOf("20%", "25%", "16.67%", "30%"),
            optionsBn = listOf("২০%", "২৫%", "১৬.৬৭%", "৩০%"),
            correctIndex = 1,
            explanationEn = "15 CP = 12 SP => SP/CP = 15/12 = 5/4. Profit = 5 - 4 = 1 unit on CP of 4 units. Profit % = (1 / 4) × 100 = 25%.",
            explanationBn = "১৫টি দ্রব্যের ক্রয়মূল্য = ১২টি দ্রব্যের বিক্রয়মূল্য। লাভ = (১৫ - ১২) = ৩টি দ্রব্য। শতকরা লাভ = (৩ / ১২) × ১০০% = ২৫%।",
            shortcutTrickBn = "নিমেষে অঙ্ক: [(ক্রয় সংখ্যা - বিক্রয় সংখ্যা) / বিক্রয় সংখ্যা] × ১০০ = [(১৫-১২)/১২] × ১০০ = ২৫%।",
            shortcutTrickEn = "Shortcut Formula: [(CP count - SP count) / SP count] × 100 = [(15 - 12)/12] × 100 = 25%."
        ),
        QuizQuestion(
            id = "Q_MATH_3",
            subject = "MATH",
            questionEn = "A can complete a piece of work in 12 days and B can complete the same work in 24 days. If they work together, in how many days will the work be finished?",
            questionBn = "A একটি কাজ ১২ দিনে এবং B ওই কাজটি ২৪ দিনে করতে পারে। তারা একত্রে কাজটি কত দিনে শেষ করবে?",
            optionsEn = listOf("6 days", "8 days", "9 days", "10 days"),
            optionsBn = listOf("৬ দিনে", "৮ দিনে", "৯ দিনে", "১০ দিনে"),
            correctIndex = 1,
            explanationEn = "Using XY / (X + Y): (12 × 24) / (12 + 24) = 288 / 36 = 8 days.",
            explanationBn = "একত্রে সময় = (X × Y) / (X + Y) = (১২ × ২৪) / (১২ + ২৪) = ২৮৮ / ৩৬ = ৮ দিন।",
            shortcutTrickBn = "নিমেষে অঙ্ক: ল.সা.গু পদ্ধতি -> মোট কাজ ২৪ একক। A করে ২ একক/দিন, B করে ১ একক/দিন। মোট = ২৪/(২+১) = ৮ দিন!",
            shortcutTrickEn = "LCM Trick: Total work = LCM(12,24) = 24 units. Efficiency A=2, B=1. Together = 24/(2+1) = 8 days."
        ),
        QuizQuestion(
            id = "Q_MATH_4",
            subject = "MATH",
            questionEn = "At what rate of simple interest per annum will a sum of money double itself in 8 years?",
            questionBn = "বার্ষিক শতকরা কত হার সরল সুদে কোনো আসল ৮ বছরে সুদে-আসলে দ্বিগুণ হবে?",
            optionsEn = listOf("10%", "12%", "12.5%", "15%"),
            optionsBn = listOf("১০%", "১২%", "১২.৫%", "১৫%"),
            correctIndex = 2,
            explanationEn = "For a sum to double, Simple Interest = Principal (100%). Rate R = 100 / Time = 100 / 8 = 12.5% per annum.",
            explanationBn = "আসল দ্বিগুণ হওয়া মানে ১০০ টাকা আসলের সুদ ১০০ টাকা। সুতরাং সুদের হার R = ১০০ / সময় = ১০০ / ৮ = ১২.৫%।",
            shortcutTrickBn = "নিমেষে অঙ্ক: সুদে-আসলে N গুণ হলে, R = [(N - 1) × 100] / T = [(2 - 1) × 100] / 8 = 12.5%।",
            shortcutTrickEn = "Shortcut: When sum becomes N times, Rate R = [(N - 1) × 100] / T = 100 / 8 = 12.5%."
        ),
        QuizQuestion(
            id = "Q_MATH_5",
            subject = "MATH",
            questionEn = "If the radius of a circle is increased by 20%, by what percentage will its area increase?",
            questionBn = "একটি বৃত্তের ব্যাসার্ধ ২০% বৃদ্ধি পেলে তার ক্ষেত্রফল শতকরা কত বৃদ্ধি পাবে?",
            optionsEn = listOf("40%", "42%", "44%", "48%"),
            optionsBn = listOf("৪০%", "৪২%", "৪৪%", "৪৮%"),
            correctIndex = 2,
            explanationEn = "Using successive percentage formula: X + Y + (XY/100) = 20 + 20 + (20×20)/100 = 40 + 4 = 44%.",
            explanationBn = "বৃত্তের ক্ষেত্রফল ব্যাসার্ধের বর্গের সমানুপাতিক (πr²)। তাই বৃদ্ধি = 20 + 20 + (20×20)/100 = 40 + 4 = 44%।",
            shortcutTrickBn = "নিমেষে অঙ্ক: 2x + (x²/100) = 2(20) + (400/100) = 40 + 4 = 44%!",
            shortcutTrickEn = "Successive Formula: 2x + (x²/100) = 40 + 4 = 44%."
        ),

        // --- REASONING & LOGICAL ANALYSIS QUESTIONS ---
        QuizQuestion(
            id = "Q_REAS_1",
            subject = "REASONING",
            questionEn = "Find the missing number in the series: 3, 7, 15, 31, 63, ?",
            questionBn = "নিচের সংখ্যা সিরিজের শূন্যস্থানে কোন সংখ্যাটি বসবে? ৩, ৭, ১৫, ৩১, ৬৩, ?",
            optionsEn = listOf("125", "126", "127", "129"),
            optionsBn = listOf("১২৫", "১২৬", "১২৭", "১২৯"),
            correctIndex = 2,
            explanationEn = "Each term follows the pattern (×2 + 1): 3×2+1=7, 7×2+1=15, 15×2+1=31, 31×2+1=63, so 63×2+1 = 127.",
            explanationBn = "প্রতিটি পদ আগের পদের দ্বিগুণের সাথে ১ যোগ করে গঠিত: ৩×২+১=৭, ৭×২+১=১৫, ১৫×২+১=৩১, ৩১×২+১=৬৩, তাই ৬৩×২+১ = ১২৭।",
            shortcutTrickBn = "প্যাটার্ন: (×২ + ১) অথবা (২ⁿ - ১) -> ২⁷ - ১ = ১২৮ - ১ = ১২৭।",
            shortcutTrickEn = "Pattern: (×2 + 1) -> 63 × 2 + 1 = 127."
        ),
        QuizQuestion(
            id = "Q_REAS_2",
            subject = "REASONING",
            questionEn = "In a certain code language, if 'POLICE' is coded as '16-15-12-9-3-5', how will 'KHAKEE' be coded?",
            questionBn = "কোনো সাংকেতিক ভাষায় 'POLICE' কে '16-15-12-9-3-5' লেখা হলে, 'KHAKEE' কে কী লেখা হবে?",
            optionsEn = listOf("11-8-1-11-5-5", "11-9-1-11-5-5", "10-8-1-10-5-5", "12-8-1-12-5-5"),
            optionsBn = listOf("11-8-1-11-5-5", "11-9-1-11-5-5", "10-8-1-10-5-5", "12-8-1-12-5-5"),
            correctIndex = 0,
            explanationEn = "Each letter is replaced by its direct alphabetical position: K=11, H=8, A=1, K=11, E=5, E=5 -> 11-8-1-11-5-5.",
            explanationBn = "ইংরেজি বর্ণমালায় প্রতিটি অক্ষরের অবস্থান মান বসানো হয়েছে: K=11, H=8, A=1, K=11, E=5, E=5 -> 11-8-1-11-5-5।",
            shortcutTrickBn = "EJOTY ট্রিক: E=5, J=10, O=15, T=20, Y=25। J=10 হলে K=11!",
            shortcutTrickEn = "EJOTY Rule: E=5, J=10, so K=11, H=8, A=1 -> 11-8-1-11-5-5."
        ),
        QuizQuestion(
            id = "Q_REAS_3",
            subject = "REASONING",
            questionEn = "Pointing to a photograph, Suman said, 'She is the daughter of the only son of my grandfather.' How is the girl related to Suman?",
            questionBn = "একটি ছবির দিকে দেখিয়ে সুমন বলল, 'সে হলো আমার ঠাকুরদার একমাত্র পুত্রের কন্যা।' ছবিটির মেয়েটি সুমনের কে হয়?",
            optionsEn = listOf("Mother", "Sister", "Cousin", "Aunt"),
            optionsBn = listOf("মা (Mother)", "বোন (Sister)", "খুড়তুতো বোন (Cousin)", "পিসি (Aunt)"),
            correctIndex = 1,
            explanationEn = "'Only son of my grandfather' is Suman's father. The daughter of Suman's father is Suman's sister.",
            explanationBn = "'আমার ঠাকুরদার একমাত্র পুত্র' হলেন সুমনের বাবা। সুতরাং সুমনের বাবার কন্যা হলেন সুমনের নিজের বোন (Sister)।",
            shortcutTrickBn = "নিজের থেকে সম্পর্ক শুরু করুন: ঠাকুরদার একমাত্র পুত্র = বাবা -> বাবার কন্যা = বোন।",
            shortcutTrickEn = "Trace from 'my': My grandfather's only son = My Father -> Father's daughter = Sister."
        ),
        QuizQuestion(
            id = "Q_REAS_4",
            subject = "REASONING",
            questionEn = "A cadet walks 4 km North, turns right and walks 3 km. How far is he now from his starting point (shortest distance)?",
            questionBn = "একজন ক্যাডেট উত্তর দিকে ৪ কিমি হাঁটার পর ডানদিকে ঘুরে আরও ৩ কিমি হাঁটলেন। শুরুর স্থান থেকে এখন তাঁর সরাসরি ন্যূনতম দূরত্ব কত?",
            optionsEn = listOf("5 km", "6 km", "7 km", "4.5 km"),
            optionsBn = listOf("৫ কিমি", "৬ কিমি", "৭ কিমি", "৪.৫ কিমি"),
            correctIndex = 0,
            explanationEn = "By Pythagoras Theorem, shortest distance = √(4² + 3²) = √(16 + 9) = √25 = 5 km.",
            explanationBn = "পিথাগোরাসের উপপাদ্য অনুযায়ী সমকোণী ত্রিভুজের অতিভুজ = √(৪² + ৩²) = √(১৬ + ৯) = √২৫ = ৫ কিমি।",
            shortcutTrickBn = "পিথাগোরিয়ান ট্রিপলেট মুখস্থ রাখুন: (৩, ৪, ৫), (৫, ১২, ১৩), (৮, ১৫, ১৭)!",
            shortcutTrickEn = "Memorize Pythagorean Triplets: (3, 4, 5), (5, 12, 13), (8, 15, 17)."
        ),

        // --- ENGLISH QUESTIONS ---
        QuizQuestion(
            id = "Q_ENG_1",
            subject = "ENGLISH",
            questionEn = "Fill in the blank with the appropriate preposition: 'The sub-inspector abided _____ the rules of the department.'",
            questionBn = "সঠিক Preposition দিয়ে শূন্যস্থান পূরণ করুন: 'The sub-inspector abided _____ the rules of the department.'",
            optionsEn = listOf("with", "by", "to", "in"),
            optionsBn = listOf("with", "by", "to", "in"),
            correctIndex = 1,
            explanationEn = "'Abide by' is a fixed phrasal verb meaning to accept or act in accordance with a rule, decision, or law.",
            explanationBn = "'Abide by' মানে কোনো নিয়ম বা আইন মেনে চলা। তাই এখানে 'by' বসবে।",
            shortcutTrickBn = "Fixed Preposition: Abide by (মেনে চলা), Accused of (অভিযুক্ত), Senior to (বড়)।",
            shortcutTrickEn = "Fixed Preposition: Abide BY rules, Accused OF crime, Senior TO someone."
        ),
        QuizQuestion(
            id = "Q_ENG_2",
            subject = "ENGLISH",
            questionEn = "Choose the correct Passive Voice of: 'The police caught the thief red-handed.'",
            questionBn = "সঠিক Passive Voice নির্বাচন করুন: 'The police caught the thief red-handed.'",
            optionsEn = listOf(
                "The thief is caught red-handed by the police.",
                "The thief was caught red-handed by the police.",
                "The thief had been caught red-handed by the police.",
                "The thief was being caught red-handed by the police."
            ),
            optionsBn = listOf(
                "The thief is caught red-handed by the police.",
                "The thief was caught red-handed by the police.",
                "The thief had been caught red-handed by the police.",
                "The thief was being caught red-handed by the police."
            ),
            correctIndex = 1,
            explanationEn = "The active sentence is in Simple Past tense ('caught' = V2). Its passive form uses 'was/were + V3' -> 'The thief was caught red-handed by the police.'",
            explanationBn = "মূল বাক্যটি Past Indefinite Tense ('caught')-এ আছে। তাই Passive করতে 'was/were + V3 (caught)' ব্যবহৃত হয়েছে।",
            shortcutTrickBn = "V2 (Past Simple) থাকলে সর্বদা 'was/were + V3' খুঁজুন!",
            shortcutTrickEn = "Active V2 (caught) -> Passive 'was/were + V3' (was caught)."
        ),
        QuizQuestion(
            id = "Q_ENG_3",
            subject = "ENGLISH",
            questionEn = "What is the meaning of the idiom 'To burn the midnight oil'?",
            questionBn = "'To burn the midnight oil' — এই Idiom-টির সঠিক অর্থ কী?",
            optionsEn = listOf(
                "To waste expensive fuel",
                "To work or study late into the night",
                "To create a fire hazard",
                "To wake up early in the morning"
            ),
            optionsBn = listOf(
                "To waste expensive fuel (জ্বালানি নষ্ট করা)",
                "To work or study late into the night (রাত জেগে কঠোর পরিশ্রম বা পড়াশোনা করা)",
                "To create a fire hazard (আগুন লাগানো)",
                "To wake up early in the morning (ভোরে ঘুম থেকে ওঠা)"
            ),
            correctIndex = 1,
            explanationEn = "'To burn the midnight oil' means to read, study, or work diligently late into the night.",
            explanationBn = "'To burn the midnight oil' কথার অর্থ হলো রাত জেগে কঠোর অধ্যবসায় বা পড়াশোনা করা (যেমন WBP পরীক্ষার প্রস্তুতি)।",
            shortcutTrickBn = "Midnight oil = রাতের প্রদীপ জ্বালিয়ে কঠোর পড়াশোনা করা।",
            shortcutTrickEn = "Midnight oil = Studying/working late into the night."
        ),
        QuizQuestion(
            id = "Q_GK_7",
            subject = "GK",
            questionEn = "In which district of West Bengal is the Mukutmanipur Dam situated?",
            questionBn = "পশ্চিমবঙ্গের কোন জেলায় মুকুটমণিপুর জলাধার (বাঁধ) অবস্থিত এবং এটি কোন দুটি নদীর সংযোগস্থলে গড়ে উঠেছে?",
            optionsEn = listOf("Purulia — Subarnarekha", "Bankura — Kangsabati & Kumari", "Birbhum — Mayurakshi", "Paschim Medinipur — Shilabati"),
            optionsBn = listOf("পুরুলিয়া — সুবর্ণরেখা নদী", "বাঁকুড়া — কংসাবতী ও কুমারী নদী", "বীরভূম — ময়ূরাক্ষী নদী", "পশ্চিম মেদিনীপুর — শিলাবতী নদী"),
            correctIndex = 1,
            explanationEn = "Mukutmanipur Dam in Bankura district is situated at the confluence of the Kangsabati and Kumari rivers.",
            explanationBn = "বাঁকুড়া জেলার খাতড়া মহকুমায় কংসাবতী ও কুমারী নদীর সংযোগস্থলে ভারতের দ্বিতীয় বৃহত্তম মাটির বাঁধ মুকুটমণিপুর অবস্থিত।",
            shortcutTrickBn = "মনে রাখার ট্রিক: মুকুটমণিপুর (বাঁকুড়া) = কংসাবতী + কুমারী নদী; ম্যাসাঞ্জোর বাঁধ (বীরভূম) = ময়ূরাক্ষী নদী।",
            shortcutTrickEn = "Mukutmanipur (Bankura) = Kangsabati + Kumari; Massanjore = Mayurakshi."
        ),
        QuizQuestion(
            id = "Q_GK_8",
            subject = "GK",
            questionEn = "Which scheme of the West Bengal Government received the prestigious UN Public Service Award in 2017?",
            questionBn = "পশ্চিমবঙ্গ সরকারের কোন জনকল্যাণমূলক প্রকল্পটি ২০১৭ সালে রাষ্ট্রসংঘের (UN) 'Public Service Award' লাভ করে?",
            optionsEn = listOf("Sabooj Sathi", "Kanyashree Prakalpa", "Swasthya Sathi", "Rupashree Prakalpa"),
            optionsBn = listOf("সবুজ সাথী প্রকল্প", "কন্যাশ্রী প্রকল্প", "স্বাস্থ্য সাথী প্রকল্প", "রূপশ্রী প্রকল্প"),
            correctIndex = 1,
            explanationEn = "Kanyashree Prakalpa (launched in 2013) won first place in the United Nations Public Service Award at The Hague in 2017.",
            explanationBn = "২০১৩ সালে চালু হওয়া 'কন্যাশ্রী প্রকল্প' বাল্যবিবাহ রোধ ও নারী শিক্ষার প্রসারের জন্য ২০১৭ সালে নেদারল্যান্ডসের দ্য হেগ শহরে রাষ্ট্রসংঘের সর্বোচ্চ জনসেবা পুরস্কার লাভ করে।",
            shortcutTrickBn = "কন্যাশ্রী = ২০১৩ (চালু) এবং ২০১৭ (UN Award, ১ম স্থান)।",
            shortcutTrickEn = "Kanyashree = Launched 2013, UN Award 2017."
        ),
        QuizQuestion(
            id = "Q_MATH_6",
            subject = "MATH",
            questionEn = "The ratio of two numbers is 3 : 4 and their HCF is 5. What is their LCM?",
            questionBn = "দুটি সংখ্যার অনুপাত ৩ : ৪ এবং তাদের গ.সা.গু (HCF) ৫ হলে, সংখ্যা দুটির ল.সা.গু (LCM) কত?",
            optionsEn = listOf("45", "60", "75", "80"),
            optionsBn = listOf("৪৫", "৬০", "৭৫", "৮০"),
            correctIndex = 1,
            explanationEn = "LCM = HCF × Product of coprime ratio terms = 5 × 3 × 4 = 60.",
            explanationBn = "বিস্তারিত পদ্ধতি: সংখ্যা দুটি যথাক্রমে ৩×৫ = ১৫ এবং ৪×৫ = ২০। ১৫ ও ২০-এর ল.সা.গু = ৬০।\nশর্টকাট ট্রিক: ল.সা.গু = গ.সা.গু × অনুপাতদ্বয়ের গুণফল = ৫ × ৩ × ৪ = ৬০।",
            shortcutTrickBn = "নিমেষে অঙ্ক (৫ সেকেন্ডে): ল.সা.গু = গ.সা.গু × অনুপাতের গুণফল = ৫ × ৩ × ৪ = ৬০!",
            shortcutTrickEn = "Shortcut: LCM = HCF × a × b = 5 × 3 × 4 = 60."
        ),
        QuizQuestion(
            id = "Q_MATH_7",
            subject = "MATH",
            questionEn = "A boat goes 12 km/hr downstream and 8 km/hr upstream. Find the speed of the boat in still water.",
            questionBn = "একটি নৌকা স্রোতের অনুকূলে ঘণ্টায় ১২ কিমি এবং স্রোতের প্রতিকূলে ঘণ্টায় ৮ কিমি যায়। স্থির জলে নৌকার গতিবেগ কত?",
            optionsEn = listOf("9 km/hr", "10 km/hr", "2 km/hr", "11 km/hr"),
            optionsBn = listOf("৯ কিমি/ঘণ্টা", "১০ কিমি/ঘণ্টা", "২ কিমি/ঘণ্টা", "১১ কিমি/ঘণ্টা"),
            correctIndex = 1,
            explanationEn = "Speed in still water = (Downstream + Upstream) / 2 = (12 + 8) / 2 = 10 km/hr.",
            explanationBn = "স্থির জলে নৌকার বেগ = (অনুকূলে বেগ + প্রতিকূলে বেগ) ÷ ২ = (১২ + ৮) ÷ ২ = ১০ কিমি/ঘণ্টা। আর স্রোতের বেগ = (১২ - ৮) ÷ ২ = ২ কিমি/ঘণ্টা।",
            shortcutTrickBn = "নিমেষে অঙ্ক: স্থির জলে নৌকার বেগ = (D + U) ÷ 2 = (12 + 8)/2 = 10 কিমি/ঘণ্টা!",
            shortcutTrickEn = "Boat speed = (D + U) / 2 = 10 km/hr; Stream speed = (D - U) / 2 = 2 km/hr."
        ),
        QuizQuestion(
            id = "Q_REAS_5",
            subject = "REASONING",
            questionEn = "In a row of 40 cadets, Rohit is 14th from the left end. What is his position from the right end?",
            questionBn = "৪০ জন ক্যাডেটের একটি সারিতে রোহিত বাঁদিক থেকে ১৪তম স্থানে আছে। ডানদিক থেকে তার অবস্থান কততম?",
            optionsEn = listOf("26th", "27th", "28th", "25th"),
            optionsBn = listOf("২৬তম", "২৭তম", "২৮তম", "২৫তম"),
            correctIndex = 1,
            explanationEn = "Position from right = (Total - Left position) + 1 = (40 - 14) + 1 = 26 + 1 = 27th.",
            explanationBn = "ডানদিক থেকে অবস্থান = (মোট সংখ্যা - বাঁদিক থেকে অবস্থান) + ১ = (৪০ - ১৪) + ১ = ২৬ + ১ = ২৭তম।",
            shortcutTrickBn = "র‍্যাঙ্কিং শর্টকাট সূত্র: Total = (Left + Right) - 1 => Right = (40 - 14) + 1 = 27তম!",
            shortcutTrickEn = "Formula: Right = (Total - Left) + 1 = 40 - 14 + 1 = 27th."
        ),
        QuizQuestion(
            id = "Q_REAS_6",
            subject = "REASONING",
            questionEn = "Find the odd one out from the following West Bengal district headquarters:",
            questionBn = "নিচের চারটি শহরের মধ্যে কোনটি বাকি তিনটি থেকে আলাদা (Odd One Out) — কৃষ্ণনগর, বহরমপুর, চুঁচুড়া, আসানসোল?",
            optionsEn = listOf("Krishnanagar", "Berhampore", "Chinsurah", "Asansol"),
            optionsBn = listOf("কৃষ্ণনগর", "বহরমপুর", "চুঁচুড়া", "আসানসোল"),
            correctIndex = 3,
            explanationEn = "Asansol is the headquarters of Paschim Bardhaman (an industrial coal city on Damodar), while Krishnanagar, Berhampore, and Chinsurah are historic riverine headquarters along the Bhagirathi-Hooghly.",
            explanationBn = "কৃষ্ণনগর (নদিয়া), বহরমপুর (মুর্শিদাবাদ) ও চুঁচুড়া (হুগলি)—এই তিনটিই ভাগীরথী-হুগলি নদী তীরবর্তী প্রাচীন জেলা সদর, কিন্তু আসানসোল হলো দামোদর উপত্যকার কয়লা ও শিল্পাঞ্চল (পশ্চিম বর্ধমানের সদর)।",
            shortcutTrickBn = "জেলা সদর ও নদী ভিত্তিক Odd-One-Out WBP পরীক্ষায় প্রায়ই আসে!",
            shortcutTrickEn = "District Geography + Reasoning combined question."
        ),
        QuizQuestion(
            id = "Q_ENG_4",
            subject = "ENGLISH",
            questionEn = "Select the correct One-Word Substitution for: 'A person who loves and collects books'",
            questionBn = "এক কথায় প্রকাশ করুন (One-Word Substitution): 'A person who loves and collects books'",
            optionsEn = listOf("Philanthropist", "Bibliophile", "Optimist", "Lexicographer"),
            optionsBn = listOf("Philanthropist", "Bibliophile", "Optimist", "Lexicographer"),
            correctIndex = 1,
            explanationEn = "'Bibliophile' means a person who collects or has a great love of books.",
            explanationBn = "'Biblio' শব্দের অর্থ বই এবং 'Phile' মানে প্রেমী। তাই যে ব্যক্তি বই ভালোবাসেন ও সংগ্রহ করেন তাঁকে 'Bibliophile' (পুস্তকপ্রেমী) বলা হয়। Lexicographer মানে অভিধান রচয়িতা।",
            shortcutTrickBn = "Root Word ট্রিক: Biblio (বই) + Phile (ভালোবাসা) = Bibliophile!",
            shortcutTrickEn = "Biblio (Book) + Phile (Lover) = Bibliophile."
        ),
        QuizQuestion(
            id = "Q_ENG_5",
            subject = "ENGLISH",
            questionEn = "Choose the correctly spelt word from the options below:",
            questionBn = "নিচের অপশনগুলোর মধ্যে সঠিক বানান (Correctly Spelt Word) কোনটি?",
            optionsEn = listOf("Lietenant", "Lieutenant", "Leutenant", "Lieutanant"),
            optionsBn = listOf("Lietenant", "Lieutenant", "Leutenant", "Lieutanant"),
            correctIndex = 1,
            explanationEn = "The correct spelling is 'Lieutenant' (L-I-E-U-T-E-N-A-N-T).",
            explanationBn = "পুলিশ ও সামরিক পরীক্ষায় 'Lieutenant' বানানটি বহুবার এসেছে। মনে রাখার ট্রিক: মিথ্যা (LIE) + তুমি (U) + দশ (TEN) + পিঁপড়ে (ANT) = LIE + U + TEN + ANT!",
            shortcutTrickBn = "ম্যাজিক বানান ট্রিক: মিথ্যা (LIE) + তুমি (U) + দশ (TEN) + পিঁপড়ে (ANT) = LIEUTENANT!",
            shortcutTrickEn = "Memory Trick: LIE + U + TEN + ANT = Lieutenant."
        )
    )

    /**
     * Testbook-style Test Series Hub definitions:
     * - Full Mock (85 Marks • 60 Mins)
     * - 4 Subject-Wise Sectional Tests matching the exact WBPRB specifications:
     *   1. General Awareness & WB GK Sectional Test (25 Questions • 25 Marks • 15 Mins)
     *   2. Elementary Mathematics Sectional Test (25 Questions • 25 Marks • 20 Mins)
     *   3. Logical & Analytical Reasoning Sectional Test (25 Questions • 25 Marks • 18 Mins)
     *   4. English Grammar & Vocabulary Sectional Test (10 Questions • 10 Marks • 8 Mins)
     */
    val testSeriesItems = listOf(
        TestSeriesItem(
            id = "FULL_MOCK_WBP_1",
            titleEn = "WBP Constable Full Length Mock Test #1",
            titleBn = "WBP কনস্টেবল ফুল লেন্থ মক টেস্ট #১ (৮৫ নম্বর)",
            subtitleEn = "Complete WBPRB Prelims Pattern • GK, Math, Reasoning & English",
            subtitleBn = "সম্পূর্ণ WBPRB প্যাটার্ন • জিকে, গণিত, রিজনিং ও ইংরেজি",
            isFullMock = true,
            subjectFilter = null,
            questionCount = 85,
            totalMarks = 85,
            durationMinutes = 60
        ),
        TestSeriesItem(
            id = "FULL_MOCK_KP_2",
            titleEn = "Kolkata Police (KP) Constable Full Mock Test #2",
            titleBn = "কলকাতা পুলিশ (KP) কনস্টেবল ফুল মক টেস্ট #২",
            subtitleEn = "High-Yield WB Static GK, Madhyamik Math & GI Speed Test",
            subtitleBn = "পশ্চিমবঙ্গ জিকে, মাধ্যমিক পাটিগণিত ও জি.আই স্পিড টেস্ট",
            isFullMock = true,
            subjectFilter = null,
            questionCount = 85,
            totalMarks = 85,
            durationMinutes = 60
        ),
        TestSeriesItem(
            id = "SEC_GK_25",
            titleEn = "General Awareness & WB GK Sectional Test",
            titleBn = "সাধারণ জ্ঞান ও পশ্চিমবঙ্গ GK সেকশনাল টেস্ট",
            subtitleEn = "WB Geography, History, Folk Culture, Schemes & Current Affairs",
            subtitleBn = "পশ্চিমবঙ্গের ভূগোল, ইতিহাস, লোকসংস্কৃতি, সরকারি প্রকল্প ও সংবিধান",
            isFullMock = false,
            subjectFilter = "GK",
            questionCount = 25,
            totalMarks = 25,
            durationMinutes = 15
        ),
        TestSeriesItem(
            id = "SEC_MATH_25",
            titleEn = "Elementary Mathematics Sectional Test",
            titleBn = "প্রাথমিক গণিত (মাধ্যমিক মান) সেকশনাল টেস্ট",
            subtitleEn = "Madhyamik Arithmetic, Train, Boat-Stream, Profit-Loss & Mensuration",
            subtitleBn = "মাধ্যমিক পাটিগণিত, ট্রেন, নৌকা-স্রোত, লাভ-ক্ষতি, সুদকষা ও পরিমিতি",
            isFullMock = false,
            subjectFilter = "MATH",
            questionCount = 25,
            totalMarks = 25,
            durationMinutes = 20
        ),
        TestSeriesItem(
            id = "SEC_REASONING_25",
            titleEn = "Logical & Analytical Reasoning Sectional Test",
            titleBn = "লজিক্যাল ও অ্যানালিটিক্যাল রিজনিং সেকশনাল টেস্ট",
            subtitleEn = "Number Series, Coding-Decoding, Blood Relations & Direction Sense",
            subtitleBn = "সংখ্যা সিরিজ, কোডিং-ডিকোডিং, রক্তের সম্পর্ক, দিক নির্ণয় ও র‍্যাঙ্কিং",
            isFullMock = false,
            subjectFilter = "REASONING",
            questionCount = 25,
            totalMarks = 25,
            durationMinutes = 18
        ),
        TestSeriesItem(
            id = "SEC_ENGLISH_10",
            titleEn = "English Grammar & Vocabulary Sectional Test",
            titleBn = "ইংরেজি গ্রামার ও ভোকাবুলারি সেকশনাল টেস্ট",
            subtitleEn = "Fixed Prepositions, Voice/Narration, Idioms & One-Word Substitution",
            subtitleBn = "ইংরেজি প্রশ্ন ও অপশন + বাংলায় সহজ ব্যাখ্যা ও শর্টকাট ট্রিক",
            isFullMock = false,
            subjectFilter = "ENGLISH",
            questionCount = 10,
            totalMarks = 10,
            durationMinutes = 8
        )
    )

    fun buildQuestionsForTest(subjectFilter: String?, targetCount: Int): List<QuizQuestion> {
        return TestbookQuestionEngine.buildUniqueTestPaper(
            subject = subjectFilter,
            count = targetCount,
            examType = "WBP_CONSTABLE",
            existingVault = preloadedQuestions
        ).first
    }
}

data class SyllabusSeed(
    val id: String,
    val subject: String,
    val titleEn: String,
    val titleBn: String,
    val defaultCompleted: Boolean
)

enum class WbDistrict(
    val nameEn: String,
    val nameBn: String,
    val keyHighlightBn: String,
    val firstQuestionBn: String,
    val sampleVoiceAnswerBn: String
) {
    MALDA(
        nameEn = "Malda",
        nameBn = "মালদা (Malda)",
        keyHighlightBn = "গৌড় ও পাণ্ডুয়া স্থাপত্য, আদিনা মসজিদ, ফজলি আম, গম্ভীরা লোকনৃত্য, মহানন্দা ও গঙ্গা নদী",
        firstQuestionBn = "আপনি মালদা জেলা থেকে এসেছেন। বলুন তো, মালদা জেলার ঐতিহাসিক 'গৌড়' ও 'আদিনা মসজিদ'-এর গুরুত্ব কী এবং মালদার বিখ্যাত লোকনৃত্য 'গম্ভীরা' সমাজ সচেতনতায় কীভাবে সাহায্য করে?",
        sampleVoiceAnswerBn = "স্যার, মালদা জেলার প্রাচীন বাংলার রাজধানী গৌড় এবং সিকান্দার শাহ নির্মিত আদিনা মসজিদ আমাদের ঐতিহাসিক ঐতিহ্য। আর মালদার গম্ভীরা লোকনৃত্যের মাধ্যমে গানের ছলে সমাজের নানা সমস্যা ও সরকারি সচেতনতার বার্তা সাধারণ মানুষের কাছে পৌঁছে দেওয়া হয়।"
    ),
    MURSHIDABAD(
        nameEn = "Murshidabad",
        nameBn = "মুর্শিদাবাদ (Murshidabad)",
        keyHighlightBn = "হাজারদুয়ারি প্রাসাদ, নবাব সিরাজউদ্দৌলা, পলাশীর যুদ্ধ (১৭৫৭), ভাগীরথী নদী, মুর্শিদাবাদ সিল্ক ও ফরাক্কা ব্যারাজ",
        firstQuestionBn = "আপনার জেলা মুর্শিদাবাদ। ভাগীরথী নদী মুর্শিদাবাদ জেলাকে কোন দুটি ভৌগোলিক অংশে ভাগ করেছে এবং ফরাক্কা ব্যারাজ প্রকল্পের মূল উদ্দেশ্য কী ছিল?",
        sampleVoiceAnswerBn = "স্যার, ভাগীরথী নদী মুর্শিদাবাদ জেলাকে পশ্চিমে 'রাঢ় অঞ্চল' এবং পূর্বে 'বাগড়ি অঞ্চল'—এই দুটি ভাগে ভাগ করেছে। আর কলকাতা বন্দরের নাব্যতা বজায় রাখার জন্য ১৯৭৫ সালে ফরাক্কা ব্যারাজ চালু করা হয়।"
    ),
    NADIA(
        nameEn = "Nadia",
        nameBn = "নদিয়া (Nadia)",
        keyHighlightBn = "শ্রীচৈতন্যদেবের জন্মস্থান নবদ্বীপ, কৃষ্ণনগরের মাটির পুতুল, বেথুয়াডহরি অভয়ারণ্য, পলাশী ও কর্কটক্রান্তি রেখা",
        firstQuestionBn = "নদিয়া জেলা থেকে আপনাকে স্বাগত! বলুন তো, নদিয়া জেলার কোন শহরের ওপর দিয়ে কর্কটক্রান্তি রেখা গেছে এবং কৃষ্ণনগরের কোন শিল্প বিশ্বজুড়ে সমাদৃত?",
        sampleVoiceAnswerBn = "স্যার, নদিয়া জেলার সদর শহর কৃষ্ণনগর ও ধুবুলিয়ার কাছ দিয়ে কর্কটক্রান্তি রেখা বিস্তৃত হয়েছে। আর কৃষ্ণনগরের ঘূর্ণি অঞ্চলের নিখুঁত মাটির পুতুল শিল্প দেশ-বিদেশে সমাদৃত।"
    ),
    BANKURA(
        nameEn = "Bankura",
        nameBn = "বাঁকুড়া (Bankura)",
        keyHighlightBn = "বিষ্ণুপুরের টেরাকোটা মন্দির, বালুচরী শাড়ি, শুশুনিয়া ও বিহারীনাথ পাহাড়, মুকুটমণিপুর জলাধার (কংসাবতী ও কুমারী নদী)",
        firstQuestionBn = "আপনি বাঁকুড়া জেলার প্রার্থী। বিষ্ণুপুরের টেরাকোটা শিল্প ও বালুচরী শাড়ির বৈশিষ্ট্য কী এবং মুকুটমণিপুর বাঁধ কোন দুটি নদীর সংযোগস্থলে অবস্থিত?",
        sampleVoiceAnswerBn = "স্যার, মল্ল রাজাদের আমলে তৈরি বিষ্ণুপুরের পোড়ামাটির টেরাকোটা মন্দির এবং রামায়ণ-মহাভারতের কাহিনী খচিত বালুচরী শাড়ি বাঁকুড়ার গর্ব। আর মুকুটমণিপুর বাঁধ কংসাবতী ও কুমারী নদীর সংযোগস্থলে অবস্থিত।"
    ),
    PURULIA(
        nameEn = "Purulia",
        nameBn = "পুরুলিয়া (Purulia)",
        keyHighlightBn = "অযোধ্যা পাহাড়, ছৌ নাচ (UNESCO হেরিটেজ), ঝুমুর গান, কংসাবতী ও সুবর্ণরেখা নদী, পাম্পড স্টোরেজ প্রজেক্ট",
        firstQuestionBn = "পুরুলিয়া জেলা থেকে এসেছেন আপনি। পুরুলিয়ার 'ছৌ নাচ' আন্তর্জাতিক স্তরে কী স্বীকৃতি পেয়েছে এবং অযোধ্যা পাহাড়ের পাম্পড স্টোরেজ বিদ্যুৎ প্রকল্পটি কেন গুরুত্বপূর্ণ?",
        sampleVoiceAnswerBn = "স্যার, পুরুলিয়ার মুখোশ পরিহিত ছৌ নাচ ২০১০ সালে ইউনেস্কোর ইনট্যানজিবল কালচারাল হেরিটেজের স্বীকৃতি পেয়েছে। আর অযোধ্যা পাহাড়ের পুরুলিয়া পাম্পড স্টোরেজ প্রজেক্টটি রাজ্যের পিক-আওয়ারে বিদ্যুৎ চাহিদা মেটাতে অত্যন্ত গুরুত্বপূর্ণ।"
    ),
    KOLKATA(
        nameEn = "Kolkata",
        nameBn = "কলকাতা (Kolkata)",
        keyHighlightBn = "কলকাতা পুলিশ কমিশনারেট, লালবাজার সদর দপ্তর, ভিক্টোরিয়া মেমোরিয়াল, হাওড়া ব্রিজ, ভারতের প্রথম মেট্রো ও আন্ডারওয়াটার মেট্রো",
        firstQuestionBn = "আপনি কলকাতা থেকে এসেছেন। কলকাতা পুলিশের সদর দপ্তর লালবাজারের ঐতিহাসিক গুরুত্ব কী এবং একজন পুলিশ কনস্টেবল/এসআই হিসেবে ভিড় নিয়ন্ত্রণ বা ট্রাফিক ব্যবস্থাপনায় আপনার প্রথম কর্তব্য কী হবে?",
        sampleVoiceAnswerBn = "স্যার, ১৮৫৬ সাল থেকে লালবাজার কলকাতা পুলিশের সদর দপ্তর হিসেবে নাগরিক সুরক্ষার প্রতীক। ভিড় বা ট্রাফিক নিয়ন্ত্রণের সময় আমি শান্ত ও সংযত থেকে সাধারণ মানুষের সাথে ভদ্র ব্যবহার করব এবং অ্যাম্বুলেন্স ও জরুরি পরিষেবাকে অগ্রাধিকার দেব।"
    ),
    NORTH_24_PARGANAS(
        nameEn = "North 24 Parganas",
        nameBn = "উত্তর ২৪ পরগনা (North 24 Parganas)",
        keyHighlightBn = "বারাসাত সদর, ইছামতী ও বিদ্যাধরী নদী, পেট্রাপোল সীমান্ত, বিভূতিভূষণ বন্যপ্রাণী অভয়ারণ্য (পারমাদান)",
        firstQuestionBn = "উত্তর ২৪ পরগনা একটি সীমান্তবর্তী জেলা। এই জেলার আন্তর্জাতিক সীমান্ত সুরক্ষায় রাজ্য পুলিশের ভূমিকা কী এবং পারমাদান অভয়ারণ্যটি কোন নদীর তীরে অবস্থিত?",
        sampleVoiceAnswerBn = "স্যার, উত্তর ২৪ পরগনার পেট্রাপোল হলো ভারতের বৃহত্তম স্থলবন্দর। সীমান্ত অঞ্চলে চোরাচালান ও অপরাধ রুখতে বিএসএফ-এর সাথে রাজ্য পুলিশের সমন্বয় অত্যন্ত জরুরি। আর পারমাদান অভয়ারণ্যটি ইছামতী নদীর তীরে অবস্থিত।"
    ),
    HOOGHLY(
        nameEn = "Hooghly",
        nameBn = "হুগলি (Hooghly)",
        keyHighlightBn = "চুঁচুড়া সদর, কামারপুকুর (শ্রীরামকৃষ্ণ), দেবানন্দপুর (শরৎচন্দ্র চট্টোপাধ্যায়), ব্যান্ডেল চার্চ ও হুগলি ইমামবাড়া",
        firstQuestionBn = "হুগলি জেলার ঐতিহাসিক গুরুত্ব অপরিসীম। হুগলি জেলায় কোন কোন ইউরোপীয় শক্তি বাণিজ্য কুঠি স্থাপন করেছিল এবং কামারপুকুর ও খানাকুল কোন দুই মনীষীর জন্মস্থান?",
        sampleVoiceAnswerBn = "স্যার, হুগলিতে পর্তুগিজ (ব্যান্ডেল), ওলন্দাজ (চুঁচুড়া), ফরাসি (চন্দননগর) ও ডেনমার্কবাসীরা (শ্রীরামপুর) কুঠি স্থাপন করেছিল। আর কামারপুকুর শ্রীরামকৃষ্ণ পরমহংসদেব এবং খানাকুলের রাধানগর রাজা রামমোহন রায়ের জন্মস্থান।"
    ),
    DARJEELING(
        nameEn = "Darjeeling",
        nameBn = "দার্জিলিং (Darjeeling)",
        keyHighlightBn = "সান্দাকফু (সর্বোচ্চ শৃঙ্গ), সিঙ্গালিলা জাতীয় উদ্যান (রেড পান্ডা), দার্জিলিং হিমালয়ান রেলওয়ে (টয় ট্রেন), তিস্তা নদী",
        firstQuestionBn = "আপনি দার্জিলিং জেলা থেকে এসেছেন। পর্যটন মরশুমে দার্জিলিংয়ে আগত পর্যটকদের নিরাপত্তা ও সহায়তা নিশ্চিত করতে একজন পুলিশ কর্মী হিসেবে আপনি কী পদক্ষেপ নেবেন?",
        sampleVoiceAnswerBn = "স্যার, দার্জিলিংয়ে দেশ-বিদেশের পর্যটকরা আসেন। আমি পুলিশ সহায়তা কেন্দ্রের মাধ্যমে তাঁদের সঠিক পথনির্দেশ দেব, পাহাড়ি রাস্তায় যানজট ও দুর্ঘটনা রোধে সতর্ক থাকব এবং যেকোনো বিপদে দ্রুত রেসপন্স করব।"
    )
}

data class InterviewChatTurn(
    val id: Long = System.currentTimeMillis(),
    val isCandidate: Boolean,
    val speakerTitleBn: String,
    val messageBn: String,
    val scoreOutOf15: Double? = null,
    val strengthsBn: String = "",
    val improvementTipsBn: String = "",
    val modelAnswerBn: String = ""
)

