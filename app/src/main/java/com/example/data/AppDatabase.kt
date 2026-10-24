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
            shortcutTrickEn = shortcutTrickEn
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
            val optionsEnJson = Json.encodeToString(ListSerializer(String.serializer()), q.optionsEn)
            val optionsBnJson = Json.encodeToString(ListSerializer(String.serializer()), q.optionsBn)
            return MistakeNotebookEntity(
                questionId = q.id,
                subject = q.subject,
                questionEn = q.questionEn,
                questionBn = q.questionBn,
                optionsEnJson = optionsEnJson,
                optionsBnJson = optionsBnJson,
                correctIndex = q.correctIndex,
                lastWrongIndex = lastWrongIndex,
                explanationEn = q.explanationEn,
                explanationBn = q.explanationBn,
                shortcutTrickBn = q.shortcutTrickBn,
                shortcutTrickEn = q.shortcutTrickEn,
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
            examYearTag = examYearTag,
            chapterId = chapterId,
            examFamily = examFamily
        )
    }

    companion object {
        fun fromQuizQuestion(q: QuizQuestion): QuestionVaultEntity {
            val optionsEnJson = Json.encodeToString(ListSerializer(String.serializer()), q.optionsEn)
            val optionsBnJson = Json.encodeToString(ListSerializer(String.serializer()), q.optionsBn)
            return QuestionVaultEntity(
                id = q.id,
                subject = q.subject,
                questionEn = q.questionEn,
                questionBn = q.questionBn,
                optionsEnJson = optionsEnJson,
                optionsBnJson = optionsBnJson,
                correctIndex = q.correctIndex,
                explanationEn = q.explanationEn,
                explanationBn = q.explanationBn,
                shortcutTrickBn = q.shortcutTrickBn,
                shortcutTrickEn = q.shortcutTrickEn,
                examYearTag = q.examYearTag,
                chapterId = q.chapterId,
                examFamily = q.examFamily,
                timestamp = System.currentTimeMillis()
            )
        }
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

    @Query("SELECT * FROM question_vault ORDER BY timestamp ASC")
    fun getAllVaultQuestions(): Flow<List<QuestionVaultEntity>>

    @Query("SELECT * FROM question_vault")
    suspend fun getAllVaultQuestionsList(): List<QuestionVaultEntity>

    @Query("SELECT COUNT(*) FROM question_vault")
    suspend fun getVaultQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultQuestions(items: List<QuestionVaultEntity>)
}

@Database(
    entities = [
        SyllabusTopicEntity::class,
        MockTestResultEntity::class,
        PetRunLogEntity::class,
        ChatMessageEntity::class,
        MistakeNotebookEntity::class,
        QuestionVaultEntity::class
    ],
    version = 3,
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
    val syllabusTopics: Flow<List<SyllabusTopicEntity>> = dao.getAllSyllabusTopics()
    val mockResults: Flow<List<MockTestResultEntity>> = dao.getAllMockResults()
    val petRunLogs: Flow<List<PetRunLogEntity>> = dao.getAllPetRunLogs()
    val chatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()
    val mistakeNotebook: Flow<List<MistakeNotebookEntity>> = dao.getAllMistakeQuestions()
    val questionVaultEntities: Flow<List<QuestionVaultEntity>> = dao.getAllVaultQuestions()

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
            // Seed 2 starter items (1 wrong answer + 1 bookmarked question) so the Mistake Notebook is immediately actionable
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
            // Seed the Question Vault with all authentic SOPAN Academy PYQs (2005–Present) + preloaded bank
            val initialQuestions = (SopanPyqVaultSeed.buildInitialVaultQuestions() + WbpSampleData.preloadedQuestions)
                .distinctBy { it.displayQuestionText().trim().lowercase() }
            dao.insertVaultQuestions(initialQuestions.map { QuestionVaultEntity.fromQuizQuestion(it) })
        }
    }

    /**
     * Deduplicates against existing questions in the vault (by ID and normalized question text)
     * and permanently appends new MCQs up to 1,000+ per subject.
     * Returns the count of newly inserted unique questions.
     */
    suspend fun bulkAddUniqueQuestionsToVault(newQuestions: List<QuizQuestion>): Int {
        val existingList = dao.getAllVaultQuestionsList()
        val existingIds = existingList.map { it.id }.toMutableSet()
        val existingTexts = existingList.map {
            (if (it.subject.equals("ENGLISH", ignoreCase = true)) it.questionEn else it.questionBn)
                .trim()
                .lowercase()
        }.toMutableSet()

        val uniqueToInsert = mutableListOf<QuestionVaultEntity>()
        for (q in newQuestions) {
            val normText = q.displayQuestionText().trim().lowercase()
            if (normText.isNotBlank() && normText !in existingTexts && q.id !in existingIds) {
                existingIds.add(q.id)
                existingTexts.add(normText)
                uniqueToInsert.add(QuestionVaultEntity.fromQuizQuestion(q))
            }
        }
        if (uniqueToInsert.isNotEmpty()) {
            dao.insertVaultQuestions(uniqueToInsert)
        }
        return uniqueToInsert.size
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
