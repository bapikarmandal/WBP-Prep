package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

@Entity(tableName = "syllabus_topics")
data class SyllabusTopicEntity(
    @PrimaryKey val id: String,
    val subject: String, // GK, MATH, REASONING, ENGLISH
    val titleEn: String,
    val titleBn: String,
    val isCompleted: Boolean
)

@Entity(tableName = "mock_test_results")
data class MockTestResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examTitle: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unattemptedCount: Int,
    val netScore: Double,
    val maxMarks: Double,
    val accuracyPercent: Int,
    val timeTakenSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "pet_run_logs")
data class PetRunLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postName: String,
    val gender: String,
    val distanceMeters: Int,
    val timeSeconds: Int,
    val targetSeconds: Int,
    val isQualified: Boolean,
    val pacePerKm: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isUser: Boolean,
    val text: String,
    val tag: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "mistake_notebook")
data class MistakeNotebookEntity(
    @PrimaryKey val questionId: String,
    val subject: String,
    val questionEn: String,
    val questionBn: String,
    val optionsEnJson: String,
    val optionsBnJson: String,
    val correctIndex: Int,
    val lastWrongIndex: Int = -1,
    val explanationEn: String,
    val explanationBn: String,
    val shortcutTrickBn: String = "",
    val shortcutTrickEn: String = "",
    val chapter: String = "",
    val isPyq: Boolean = false,
    val pyqExam: String = "",
    val pyqYear: String = "",
    val questionImage: String = "",
    val optionImagesJson: String = "[\"\",\"\",\"\",\"\"]",
    val isWrongAnswer: Boolean = false,
    val isBookmarked: Boolean = false,
    val wrongAttemptsCount: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toQuizQuestion(): QuizQuestion {
        val optionsEn = try {
            Json.decodeFromString(ListSerializer(String.serializer()), optionsEnJson)
        } catch (_: Exception) {
            listOf("Option A", "Option B", "Option C", "Option D")
        }
        val optionsBn = try {
            Json.decodeFromString(ListSerializer(String.serializer()), optionsBnJson)
        } catch (_: Exception) {
            optionsEn
        }
        val optionImages = try {
            Json.decodeFromString(ListSerializer(String.serializer()), optionImagesJson)
        } catch (_: Exception) {
            listOf("", "", "", "")
        }
        return QuizQuestion(
            id = questionId,
            subject = subject,
            questionEn = questionEn,
            questionBn = questionBn,
            optionsEn = optionsEn,
            optionsBn = optionsBn,
            correctIndex = correctIndex,
            explanationEn = explanationEn,
            explanationBn = explanationBn,
            shortcutTrickBn = shortcutTrickBn,
            shortcutTrickEn = shortcutTrickEn,
            chapter = chapter,
            chapterId = chapter,
            isPyq = isPyq,
            pyqExam = pyqExam,
            pyqYear = pyqYear,
            examYearTag = if (isPyq && pyqExam.isNotBlank()) "$pyqExam $pyqYear".trim() else "",
            questionImage = questionImage,
            optionImages = optionImages
        )
    }

    companion object {
        fun fromQuizQuestion(
            q: QuizQuestion,
            isWrongAnswer: Boolean,
            isBookmarked: Boolean,
            lastWrongIndex: Int = -1,
            wrongAttemptsCount: Int = if (isWrongAnswer) 1 else 0
        ): MistakeNotebookEntity {
            val optionsEnJson = Json.encodeToString(ListSerializer(String.serializer()), q.optionsEn.ifEmpty { q.optionsBn })
            val optionsBnJson = Json.encodeToString(ListSerializer(String.serializer()), q.optionsBn)
            val paddedOptImgs = List(4) { idx -> q.optionImages.getOrNull(idx).orEmpty() }
            val optionImagesJson = Json.encodeToString(ListSerializer(String.serializer()), paddedOptImgs)
            return MistakeNotebookEntity(
                questionId = q.id,
                subject = q.subject,
                questionEn = q.questionEn.ifBlank { q.questionBn },
                questionBn = q.questionBn,
                optionsEnJson = optionsEnJson,
                optionsBnJson = optionsBnJson,
                correctIndex = q.correctIndex,
                lastWrongIndex = lastWrongIndex,
                explanationEn = q.explanationEn.ifBlank { q.explanationBn },
                explanationBn = q.explanationBn,
                shortcutTrickBn = q.shortcutTrickBn,
                shortcutTrickEn = q.shortcutTrickEn,
                chapter = q.chapter.ifBlank { q.chapterId },
                isPyq = q.isPyq,
                pyqExam = q.pyqExam,
                pyqYear = q.pyqYear,
                questionImage = q.questionImage,
                optionImagesJson = optionImagesJson,
                isWrongAnswer = isWrongAnswer,
                isBookmarked = isBookmarked,
                wrongAttemptsCount = wrongAttemptsCount,
                timestamp = System.currentTimeMillis()
            )
        }
    }
}

@Entity(tableName = "question_vault")
data class QuestionVaultEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val questionEn: String,
    val questionBn: String,
    val optionsEnJson: String,
    val optionsBnJson: String,
    val correctIndex: Int,
    val explanationEn: String,
    val explanationBn: String,
    val shortcutTrickBn: String = "",
    val shortcutTrickEn: String = "",
    val examYearTag: String = "",
    val chapterId: String = "",
    val examFamily: String = "",
    val chapter: String = "",
    val isPyq: Boolean = false,
    val pyqExam: String = "",
    val pyqYear: String = "",
    val questionImage: String = "",
    val optionImagesJson: String = "[\"\",\"\",\"\",\"\"]",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toQuizQuestion(): QuizQuestion {
        val optionsEn = try {
            Json.decodeFromString(ListSerializer(String.serializer()), optionsEnJson)
        } catch (_: Exception) {
            listOf("Option A", "Option B", "Option C", "Option D")
        }
        val optionsBn = try {
            Json.decodeFromString(ListSerializer(String.serializer()), optionsBnJson)
        } catch (_: Exception) {
            optionsEn
        }
        val optionImages = try {
            Json.decodeFromString(ListSerializer(String.serializer()), optionImagesJson)
        } catch (_: Exception) {
            listOf("", "", "", "")
        }
        val resolvedChapter = chapter.ifBlank { chapterId }
        val resolvedExamYearTag = examYearTag.ifBlank {
            if (isPyq && pyqExam.isNotBlank()) "$pyqExam $pyqYear".trim() else ""
        }
        return QuizQuestion(
            id = id,
            subject = subject,
            questionEn = questionEn,
            questionBn = questionBn,
            optionsEn = optionsEn,
            optionsBn = optionsBn,
            correctIndex = correctIndex,
            explanationEn = explanationEn,
            explanationBn = explanationBn,
            shortcutTrickBn = shortcutTrickBn,
            shortcutTrickEn = shortcutTrickEn,
            examYearTag = resolvedExamYearTag,
            chapterId = chapterId.ifBlank { resolvedChapter },
            examFamily = examFamily,
            chapter = resolvedChapter,
            isPyq = isPyq,
            pyqExam = pyqExam,
            pyqYear = pyqYear,
            questionImage = questionImage,
            optionImages = optionImages
        )
    }

    companion object {
        fun fromQuizQuestion(q: QuizQuestion): QuestionVaultEntity {
            val cleanOptionsBn = if (q.optionsBn.isNotEmpty()) q.optionsBn else q.optionsEn
            val cleanOptionsEn = if (q.optionsEn.isNotEmpty()) q.optionsEn else cleanOptionsBn
            val optionsEnJson = Json.encodeToString(ListSerializer(String.serializer()), cleanOptionsEn)
            val optionsBnJson = Json.encodeToString(ListSerializer(String.serializer()), cleanOptionsBn)
            val paddedOptImgs = List(4) { idx -> q.optionImages.getOrNull(idx).orEmpty() }
            val optionImagesJson = Json.encodeToString(ListSerializer(String.serializer()), paddedOptImgs)
            val resolvedChapter = q.chapter.ifBlank { q.chapterId }
            val matchedSpec = WbpPyqVaultSeed.findBestMatchingChapter(q.subject, "$resolvedChapter ${q.questionBn}")
            val resolvedChapterId = if (q.chapterId.isNotBlank()) q.chapterId else matchedSpec.chapterId
            val resolvedExamYearTag = if (q.isPyq && q.pyqExam.isNotBlank()) {
                if (q.pyqYear.isNotBlank()) "${q.pyqExam} ${q.pyqYear}" else q.pyqExam
            } else {
                q.examYearTag
            }
            return QuestionVaultEntity(
                id = q.id,
                subject = StructuredQuestionSchema.normalizeSubjectCode(q.subject),
                questionEn = q.questionEn.ifBlank { q.questionBn },
                questionBn = q.questionBn.ifBlank { q.questionEn },
                optionsEnJson = optionsEnJson,
                optionsBnJson = optionsBnJson,
                correctIndex = q.correctIndex.coerceIn(0, 3),
                explanationEn = q.explanationEn.ifBlank { q.explanationBn },
                explanationBn = q.explanationBn.ifBlank { q.explanationEn },
                shortcutTrickBn = q.shortcutTrickBn,
                shortcutTrickEn = q.shortcutTrickEn,
                examYearTag = resolvedExamYearTag,
                chapterId = resolvedChapterId,
                examFamily = q.examFamily,
                chapter = resolvedChapter.ifBlank { matchedSpec.titleEn },
                isPyq = q.isPyq || (q.pyqExam.isNotBlank()),
                pyqExam = q.pyqExam,
                pyqYear = q.pyqYear,
                questionImage = q.questionImage,
                optionImagesJson = optionImagesJson,
                timestamp = System.currentTimeMillis()
            )
        }
    }
}

@Entity(tableName = "registered_aspirants")
data class AspirantUserEntity(
    @PrimaryKey val email: String,
    val id: String,
    val fullName: String,
    val password: String,
    val casteCategory: String = AspirantCasteCategory.GENERAL.name,
    val targetExam: String = AspirantTargetExam.WBP_CONSTABLE.name,
    val testsAttempted: Int = 0,
    val averageScore: Double = 0.0,
    val joinDate: String = "24 Oct 2026",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toProfile(): AspirantProfile = AspirantProfile(
        id = id,
        fullName = fullName,
        email = email,
        password = password,
        casteCategory = casteCategory,
        targetExam = targetExam,
        testsAttempted = testsAttempted,
        averageScore = averageScore,
        joinDate = joinDate,
        timestamp = timestamp
    )

    companion object {
        fun fromProfile(profile: AspirantProfile): AspirantUserEntity = AspirantUserEntity(
            email = profile.email.trim().lowercase(),
            id = profile.id,
            fullName = profile.fullName.trim(),
            password = profile.password,
            casteCategory = profile.casteCategory,
            targetExam = profile.targetExam,
            testsAttempted = profile.testsAttempted,
            averageScore = profile.averageScore,
            joinDate = profile.joinDate,
            timestamp = profile.timestamp
        )
    }
}

@Dao
interface WbpDao {
    @Query("SELECT * FROM syllabus_topics")
    fun getAllSyllabusTopics(): Flow<List<SyllabusTopicEntity>>

    @Query("SELECT COUNT(*) FROM syllabus_topics")
    suspend fun getSyllabusCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabusTopics(topics: List<SyllabusTopicEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSyllabusTopic(topic: SyllabusTopicEntity)

    @Query("SELECT * FROM mock_test_results ORDER BY timestamp DESC")
    fun getAllMockResults(): Flow<List<MockTestResultEntity>>

    @Query("SELECT * FROM mock_test_results ORDER BY timestamp DESC")
    suspend fun getAllMockResultsList(): List<MockTestResultEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockResult(result: MockTestResultEntity)

    @Query("SELECT * FROM pet_run_logs ORDER BY timestamp DESC")
    fun getAllPetRunLogs(): Flow<List<PetRunLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPetRunLog(log: PetRunLogEntity)

    @Query("DELETE FROM pet_run_logs WHERE id = :id")
    suspend fun deletePetRunLog(id: Int)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT COUNT(*) FROM chat_messages")
    suspend fun getChatCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()

    @Query("SELECT * FROM mistake_notebook ORDER BY timestamp DESC")
    fun getAllMistakeQuestions(): Flow<List<MistakeNotebookEntity>>

    @Query("SELECT * FROM mistake_notebook WHERE questionId = :id LIMIT 1")
    suspend fun getMistakeById(id: String): MistakeNotebookEntity?

    @Query("SELECT COUNT(*) FROM mistake_notebook")
    suspend fun getMistakeCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMistakeQuestion(item: MistakeNotebookEntity)

    @Query("DELETE FROM mistake_notebook WHERE questionId = :id")
    suspend fun deleteMistakeQuestion(id: String)

    @Query("SELECT * FROM question_vault ORDER BY CASE WHEN id LIKE 'MANUAL_%' OR id LIKE 'SCAN_%' OR id LIKE 'IMPORT_%' OR examFamily = 'USER_UPLOADED' OR examFamily = 'MANUAL_STUDIO' THEN 0 ELSE 1 END ASC, timestamp DESC")
    fun getAllVaultQuestions(): Flow<List<QuestionVaultEntity>>

    @Query("SELECT * FROM question_vault")
    suspend fun getAllVaultQuestionsList(): List<QuestionVaultEntity>

    @Query("SELECT COUNT(*) FROM question_vault")
    suspend fun getVaultQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultQuestions(items: List<QuestionVaultEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVaultQuestion(item: QuestionVaultEntity)

    @Query("DELETE FROM question_vault WHERE id = :id")
    suspend fun deleteVaultQuestionById(id: String)

    @Query("SELECT * FROM registered_aspirants ORDER BY timestamp DESC")
    fun getAllAspirants(): Flow<List<AspirantUserEntity>>

    @Query("SELECT * FROM registered_aspirants ORDER BY timestamp DESC")
    suspend fun getAllAspirantsList(): List<AspirantUserEntity>

    @Query("SELECT * FROM registered_aspirants WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getAspirantByEmail(email: String): AspirantUserEntity?

    @Query("SELECT COUNT(*) FROM registered_aspirants")
    suspend fun getAspirantCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAspirant(user: AspirantUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAspirants(users: List<AspirantUserEntity>)

    @Query("DELETE FROM registered_aspirants WHERE LOWER(email) = LOWER(:email)")
    suspend fun deleteAspirantByEmail(email: String)
}

@Database(
    entities = [
        SyllabusTopicEntity::class,
        MockTestResultEntity::class,
        PetRunLogEntity::class,
        ChatMessageEntity::class,
        MistakeNotebookEntity::class,
        QuestionVaultEntity::class,
        AspirantUserEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wbpDao(): WbpDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wbp_kp_prep_db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class WbpRepository(private val dao: WbpDao) {
    private val backupJsonFormat = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

    val syllabusTopics: Flow<List<SyllabusTopicEntity>> = dao.getAllSyllabusTopics()
    val mockResults: Flow<List<MockTestResultEntity>> = dao.getAllMockResults()
    val petRunLogs: Flow<List<PetRunLogEntity>> = dao.getAllPetRunLogs()
    val chatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()
    val mistakeNotebook: Flow<List<MistakeNotebookEntity>> = dao.getAllMistakeQuestions()
    val questionVaultEntities: Flow<List<QuestionVaultEntity>> = dao.getAllVaultQuestions()
    val registeredAspirants: Flow<List<AspirantUserEntity>> = dao.getAllAspirants()

    suspend fun ensureSeedData() {
        if (dao.getSyllabusCount() == 0) {
            val entities = WbpSampleData.defaultSyllabusTopics.map {
                SyllabusTopicEntity(
                    id = it.id,
                    subject = it.subject,
                    titleEn = it.titleEn,
                    titleBn = it.titleBn,
                    isCompleted = it.defaultCompleted
                )
            }
            dao.insertSyllabusTopics(entities)
        }
        if (dao.getChatCount() == 0) {
            dao.insertChatMessage(
                ChatMessageEntity(
                    isUser = false,
                    text = "নমস্কার যোদ্ধা! 👮‍♂️ আমি আপনার **WBP/KP AI Tutor**।\n\nযেমন খুশি **বাংলা**, **Banglish** বা **English**-এ আপনার প্রশ্ন লিখুন:\n• **নিমেষে অঙ্ক (Math Shortcuts)**: বিস্তারিত পদ্ধতি + ৩০ সেকেন্ডের শর্টকাট ট্রিক।\n• **রিজনিং ট্রিকস**: কোডিং-ডিকোডিং, সিরিজ, রক্তের সম্পর্ক।\n• **পশ্চিমবঙ্গ ও ভারতের GK**: ইতিহাস, ভূগোল, সংবিধান, লোকসংস্কৃতি ও সরকারি প্রকল্প।\n\nনিচের যেকোনো একটি টপিক বেছে নিন বা নিজের প্রশ্ন টাইপ করুন!",
                    tag = "নিমেষে অঙ্ক • WBPRB Special"
                )
            )
        }
        if (dao.getMistakeCount() == 0) {
            val sampleMath = WbpSampleData.preloadedQuestions.firstOrNull { it.id == "Q_MATH_2" }
            val sampleGk = WbpSampleData.preloadedQuestions.firstOrNull { it.id == "Q_GK_4" }
            if (sampleMath != null) {
                dao.upsertMistakeQuestion(
                    MistakeNotebookEntity.fromQuizQuestion(
                        q = sampleMath,
                        isWrongAnswer = true,
                        isBookmarked = false,
                        lastWrongIndex = 0,
                        wrongAttemptsCount = 1
                    )
                )
            }
            if (sampleGk != null) {
                dao.upsertMistakeQuestion(
                    MistakeNotebookEntity.fromQuizQuestion(
                        q = sampleGk,
                        isWrongAnswer = false,
                        isBookmarked = true,
                        lastWrongIndex = -1,
                        wrongAttemptsCount = 0
                    )
                )
            }
        }
        if (dao.getVaultQuestionCount() == 0) {
            val initialQuestions = (WbpPyqVaultSeed.buildInitialVaultQuestions() + WbpSampleData.preloadedQuestions)
                .distinctBy { TestbookQuestionEngine.normalizeQuestionText(it) }
                .map { q ->
                    val matchedChap = WbpPyqVaultSeed.findBestMatchingChapter(q.subject, "${q.chapterId} ${q.questionBn}")
                    val isOfficialPyq = q.examYearTag.isNotBlank() && !q.examYearTag.contains("Expected", ignoreCase = true)
                    q.copy(
                        chapter = q.chapter.ifBlank { matchedChap.titleEn },
                        isPyq = q.isPyq || isOfficialPyq,
                        pyqExam = q.pyqExam.ifBlank { if (isOfficialPyq) q.examYearTag.replace(Regex("\\b20\\d{2}\\b"), "").trim() else "" },
                        pyqYear = q.pyqYear.ifBlank { Regex("\\b(20\\d{2})\\b").find(q.examYearTag)?.value ?: "" }
                    )
                }
            dao.insertVaultQuestions(initialQuestions.map { QuestionVaultEntity.fromQuizQuestion(it) })
        }
        if (dao.getAspirantCount() == 0) {
            val defaultAspirants = listOf(
                AspirantUserEntity(
                    email = "rahul.mondal@gmail.com",
                    id = "ASP_DEMO_1",
                    fullName = "Rahul Mondal",
                    password = "rahul123",
                    casteCategory = AspirantCasteCategory.OBC_B.name,
                    targetExam = AspirantTargetExam.WBP_CONSTABLE.name,
                    testsAttempted = 8,
                    averageScore = 58.25,
                    joinDate = "12 Oct 2026",
                    timestamp = System.currentTimeMillis() - 86_400_000L * 12
                ),
                AspirantUserEntity(
                    email = "ananya.roy@gmail.com",
                    id = "ASP_DEMO_2",
                    fullName = "Ananya Roy",
                    password = "ananya123",
                    casteCategory = AspirantCasteCategory.GENERAL.name,
                    targetExam = AspirantTargetExam.WBP_LADY_CONSTABLE.name,
                    testsAttempted = 11,
                    averageScore = 62.50,
                    joinDate = "15 Oct 2026",
                    timestamp = System.currentTimeMillis() - 86_400_000L * 9
                ),
                AspirantUserEntity(
                    email = "sourav.das@gmail.com",
                    id = "ASP_DEMO_3",
                    fullName = "Sourav Das",
                    password = "sourav123",
                    casteCategory = AspirantCasteCategory.SC.name,
                    targetExam = AspirantTargetExam.WBP_KP_SI.name,
                    testsAttempted = 6,
                    averageScore = 134.75,
                    joinDate = "19 Oct 2026",
                    timestamp = System.currentTimeMillis() - 86_400_000L * 5
                ),
                AspirantUserEntity(
                    email = "pemba.tamang@gmail.com",
                    id = "ASP_DEMO_4",
                    fullName = "Pemba Tamang",
                    password = "pemba123",
                    casteCategory = AspirantCasteCategory.GORKHA.name,
                    targetExam = AspirantTargetExam.KP_CONSTABLE.name,
                    testsAttempted = 5,
                    averageScore = 53.00,
                    joinDate = "21 Oct 2026",
                    timestamp = System.currentTimeMillis() - 86_400_000L * 3
                )
            )
            dao.insertAspirants(defaultAspirants)
        }
    }

    suspend fun getAspirantByEmail(email: String): AspirantProfile? =
        dao.getAspirantByEmail(email.trim().lowercase())?.toProfile()

    suspend fun upsertAspirantProfile(profile: AspirantProfile) {
        dao.upsertAspirant(AspirantUserEntity.fromProfile(profile))
    }

    suspend fun deleteAspirantProfile(email: String) {
        dao.deleteAspirantByEmail(email.trim().lowercase())
    }

    suspend fun recordAspirantTestScore(email: String, netScore: Double): AspirantProfile? {
        val current = dao.getAspirantByEmail(email.trim().lowercase()) ?: return null
        val prevCount = current.testsAttempted.coerceAtLeast(0)
        val nextCount = prevCount + 1
        val nextAvg = if (prevCount == 0) {
            netScore
        } else {
            ((current.averageScore * prevCount) + netScore) / nextCount.toDouble()
        }
        val updated = current.copy(
            testsAttempted = nextCount,
            averageScore = kotlin.math.round(nextAvg * 100.0) / 100.0
        )
        dao.upsertAspirant(updated)
        return updated.toProfile()
    }

    /**
     * Deduplicates against existing questions in the vault (by ID and normalized question text:
     * `question_bn.trim().replace(/\s+/g, ' ')`) and permanently appends new MCQs up to 1,000+ per subject.
     * Returns the count of newly inserted unique questions.
     */
    suspend fun bulkAddUniqueQuestionsToVault(newQuestions: List<QuizQuestion>): Int {
        val existingList = dao.getAllVaultQuestionsList()
        val existingIds = existingList.map { it.id.trim() }.toMutableSet()
        val existingTexts = existingList.map {
            TestbookQuestionEngine.normalizeQuestionText(it.toQuizQuestion())
        }.toMutableSet()

        val uniqueToInsert = mutableListOf<QuestionVaultEntity>()
        for (q in newQuestions) {
            val cleanId = q.id.trim()
            val normText = TestbookQuestionEngine.normalizeQuestionText(q)
            val normKey = if (normText.isNotBlank()) normText else "IMG_${q.questionImage.hashCode()}_${cleanId}"
            if (normKey !in existingTexts && cleanId !in existingIds) {
                existingIds.add(cleanId)
                existingTexts.add(normKey)
                uniqueToInsert.add(QuestionVaultEntity.fromQuizQuestion(q))
            }
        }
        if (uniqueToInsert.isNotEmpty()) {
            dao.insertVaultQuestions(uniqueToInsert)
        }
        return uniqueToInsert.size
    }

    /**
     * Ingests a batch of manual or imported questions into the Question Bank and returns a detailed
     * Before-vs-After verification report including newly added IDs, duplicate IDs, and subject breakdown.
     */
    suspend fun ingestScannedQuestionsWithReport(scannedQuestions: List<QuizQuestion>): ScanVerificationReport {
        val existingList = dao.getAllVaultQuestionsList()
        val beforeCount = existingList.size
        val existingIds = existingList.map { it.id.trim() }.toMutableSet()
        val existingByNormText = existingList.associateBy {
            val q = it.toQuizQuestion()
            val norm = TestbookQuestionEngine.normalizeQuestionText(q)
            if (norm.isNotBlank()) norm else "IMG_${q.questionImage.hashCode()}_${q.id}"
        }.toMutableMap()

        val uniqueEntitiesToInsert = mutableListOf<QuestionVaultEntity>()
        val finalDisplayQuestions = mutableListOf<QuizQuestion>()
        val newlyAddedIds = mutableSetOf<String>()
        val duplicateIds = mutableSetOf<String>()
        val subjectBreakdown = mutableMapOf(
            "GK" to 0,
            "MATH" to 0,
            "REASONING" to 0,
            "ENGLISH" to 0
        )

        for (rawQ in scannedQuestions) {
            val normSub = StructuredQuestionSchema.normalizeSubjectCode(rawQ.subject)
            val resolvedPyqTag = if (rawQ.isPyq && rawQ.pyqExam.isNotBlank()) {
                if (rawQ.pyqYear.isNotBlank()) "${rawQ.pyqExam} (${rawQ.pyqYear})" else rawQ.pyqExam
            } else {
                rawQ.examYearTag.ifBlank { "Custom Studio Question" }
            }
            val taggedQ = rawQ.copy(
                id = if (rawQ.id.startsWith("MANUAL_", ignoreCase = true) ||
                    rawQ.id.startsWith("SMART_SCAN_", ignoreCase = true) ||
                    rawQ.id.startsWith("UPLOADED_", ignoreCase = true)
                ) {
                    rawQ.id
                } else {
                    "MANUAL_${normSub}_${System.currentTimeMillis()}_${rawQ.id}"
                },
                subject = normSub,
                examFamily = if (rawQ.examFamily.isNotBlank()) rawQ.examFamily else "USER_UPLOADED",
                examYearTag = resolvedPyqTag
            )
            val cleanId = taggedQ.id.trim()
            val rawNorm = TestbookQuestionEngine.normalizeQuestionText(taggedQ)
            val normKey = if (rawNorm.isNotBlank()) rawNorm else "IMG_${taggedQ.questionImage.hashCode()}_${cleanId}"
            if (normKey !in existingByNormText && cleanId !in existingIds) {
                existingIds.add(cleanId)
                val entity = QuestionVaultEntity.fromQuizQuestion(taggedQ)
                existingByNormText[normKey] = entity
                uniqueEntitiesToInsert.add(entity)
                newlyAddedIds.add(cleanId)
                finalDisplayQuestions.add(entity.toQuizQuestion())
                val subKey = taggedQ.subject.uppercase()
                subjectBreakdown[subKey] = (subjectBreakdown[subKey] ?: 0) + 1
            } else {
                val existingEntity = existingByNormText[normKey]
                val resolvedQ = existingEntity?.toQuizQuestion()?.copy(examFamily = "USER_UPLOADED") ?: taggedQ
                if (existingEntity != null) {
                    val updatedEntity = QuestionVaultEntity.fromQuizQuestion(
                        taggedQ.copy(id = existingEntity.id, examFamily = "USER_UPLOADED")
                    )
                    dao.upsertVaultQuestion(updatedEntity)
                }
                duplicateIds.add(resolvedQ.id)
                finalDisplayQuestions.add(resolvedQ)
            }
        }

        if (uniqueEntitiesToInsert.isNotEmpty()) {
            dao.insertVaultQuestions(uniqueEntitiesToInsert)
        }

        val afterCount = beforeCount + uniqueEntitiesToInsert.size
        return ScanVerificationReport(
            beforeScanTotal = beforeCount,
            extractedTotalCount = finalDisplayQuestions.size,
            newlyAddedCount = uniqueEntitiesToInsert.size,
            duplicatesIgnoredCount = duplicateIds.size,
            afterScanTotal = afterCount,
            subjectBreakdown = subjectBreakdown.filterValues { it > 0 },
            extractedQuestions = finalDisplayQuestions,
            newlyAddedIds = newlyAddedIds,
            duplicateIds = duplicateIds
        )
    }

    /**
     * Exports the entire Question Bank as a clean, pretty-printed JSON array matching the
     * Structured Question Database Schema (`id`, `subject`, `chapter`, `is_pyq`, `pyq_exam`, `pyq_year`,
     * `question_bn`, `question_image`, `options_bn`, `option_images`, `correct_index`, `explanation_bn`).
     */
    suspend fun exportVaultAsJson(): String {
        val allQuestions = dao.getAllVaultQuestionsList().map { it.toQuizQuestion() }
        return backupJsonFormat.encodeToString(ListSerializer(QuizQuestion.serializer()), allQuestions)
    }

    /**
     * Exports the Full Unified Database Bundle (`questions`, `users`, and `testAttempts`) as JSON.
     */
    suspend fun exportFullDatabaseBundleJson(): String {
        val allQuestions = dao.getAllVaultQuestionsList().map { it.toQuizQuestion() }
        val allUsers = dao.getAllAspirantsList().map { it.toProfile() }
        val allAttempts = dao.getAllMockResultsList().map {
            TestAttemptRecord(
                id = it.id,
                examName = it.examTitle,
                totalQuestions = it.totalQuestions,
                attempted = (it.totalQuestions - it.unattemptedCount).coerceAtLeast(0),
                correct = it.correctCount,
                wrong = it.wrongCount,
                netScore = it.netScore,
                maxMarks = it.maxMarks,
                accuracyPercent = it.accuracyPercent,
                timeTakenSeconds = it.timeTakenSeconds,
                timestamp = it.timestamp
            )
        }
        val bundle = FullDatabaseBackupBundle(
            schemaVersion = 2,
            exportedAt = System.currentTimeMillis(),
            questions = allQuestions,
            users = allUsers,
            testAttempts = allAttempts
        )
        return backupJsonFormat.encodeToString(FullDatabaseBackupBundle.serializer(), bundle)
    }

    /**
     * Imports a JSON Backup file (`.json`) into the Unified Database, supporting both:
     * 1) `FullDatabaseBackupBundle` (`questions` + `users` + `testAttempts`)
     * 2) `List<QuizQuestion>` arrays
     */
    suspend fun importDatabaseBackupJson(rawJson: String): ScanVerificationReport {
        val cleaned = rawJson.trim().removePrefix("\uFEFF")
        if (cleaned.startsWith("{")) {
            try {
                val bundle = backupJsonFormat.decodeFromString(FullDatabaseBackupBundle.serializer(), cleaned)
                if (bundle.users.isNotEmpty()) {
                    dao.insertAspirants(bundle.users.map { AspirantUserEntity.fromProfile(it) })
                }
                return ingestScannedQuestionsWithReport(bundle.questions)
            } catch (_: Exception) {
            }
        }
        val parsedQuestions: List<QuizQuestion> = try {
            backupJsonFormat.decodeFromString(ListSerializer(QuizQuestion.serializer()), cleaned)
        } catch (_: Exception) {
            emptyList()
        }
        return ingestScannedQuestionsWithReport(parsedQuestions)
    }

    suspend fun updateVaultQuestion(updatedQuestion: QuizQuestion) {
        val tagged = updatedQuestion.copy(
            examFamily = if (updatedQuestion.examFamily.isBlank()) "USER_UPLOADED" else updatedQuestion.examFamily
        )
        dao.upsertVaultQuestion(QuestionVaultEntity.fromQuizQuestion(tagged))
    }

    suspend fun deleteVaultQuestion(questionId: String) {
        dao.deleteVaultQuestionById(questionId)
    }

    suspend fun toggleSyllabusTopic(topic: SyllabusTopicEntity) {
        dao.updateSyllabusTopic(topic.copy(isCompleted = !topic.isCompleted))
    }

    suspend fun saveMockResult(result: MockTestResultEntity) {
        dao.insertMockResult(result)
    }

    suspend fun savePetRunLog(log: PetRunLogEntity) {
        dao.insertPetRunLog(log)
    }

    suspend fun deletePetRunLog(id: Int) {
        dao.deletePetRunLog(id)
    }

    suspend fun addChatMessage(isUser: Boolean, text: String, tag: String = "") {
        dao.insertChatMessage(ChatMessageEntity(isUser = isUser, text = text, tag = tag))
    }

    suspend fun clearChatHistory() {
        dao.clearChatMessages()
        ensureSeedData()
    }

    suspend fun recordWrongAnswer(question: QuizQuestion, wrongIndex: Int) {
        val existing = dao.getMistakeById(question.id)
        val updated = MistakeNotebookEntity.fromQuizQuestion(
            q = question,
            isWrongAnswer = true,
            isBookmarked = existing?.isBookmarked ?: false,
            lastWrongIndex = wrongIndex,
            wrongAttemptsCount = (existing?.wrongAttemptsCount ?: 0) + 1
        )
        dao.upsertMistakeQuestion(updated)
    }

    suspend fun toggleQuestionBookmark(question: QuizQuestion): Boolean {
        val existing = dao.getMistakeById(question.id)
        val nextBookmarkState = !(existing?.isBookmarked ?: false)
        if (!nextBookmarkState && existing?.isWrongAnswer != true) {
            dao.deleteMistakeQuestion(question.id)
            return false
        }
        val updated = MistakeNotebookEntity.fromQuizQuestion(
            q = question,
            isWrongAnswer = existing?.isWrongAnswer ?: false,
            isBookmarked = nextBookmarkState,
            lastWrongIndex = existing?.lastWrongIndex ?: -1,
            wrongAttemptsCount = existing?.wrongAttemptsCount ?: 0
        )
        dao.upsertMistakeQuestion(updated)
        return nextBookmarkState
    }

    suspend fun removeMistakeOrMastered(questionId: String) {
        dao.deleteMistakeQuestion(questionId)
    }
}

