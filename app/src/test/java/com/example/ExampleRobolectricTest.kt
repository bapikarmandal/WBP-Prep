package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.QuestionVaultEntity
import com.example.data.QuizQuestion
import com.example.data.StructuredQuestionSchema
import com.example.data.WbpPyqVaultSeed
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WBP Prep", appName)
    }

    @Test
    fun `structured schema chapters map cleanly to WbpPyqVaultSeed chapters`() {
        assertEquals(8, StructuredQuestionSchema.gkChapters.size)
        assertEquals(18, StructuredQuestionSchema.mathChapters.size)
        assertEquals(20, StructuredQuestionSchema.reasoningChapters.size)
        assertEquals(9, StructuredQuestionSchema.englishChapters.size)
        assertEquals(55, WbpPyqVaultSeed.wbpChapters.size)

        StructuredQuestionSchema.subjects.forEach { subject ->
            val chapters = StructuredQuestionSchema.chaptersForSubject(subject)
            chapters.forEach { chapterName ->
                val chapterId = StructuredQuestionSchema.chapterNameToChapterId(subject, chapterName)
                val matched = WbpPyqVaultSeed.wbpChapters.firstOrNull { it.chapterId == chapterId }
                assertTrue("Expected chapter '$chapterName' ($subject) to match a WbpChapterInfo", matched != null)
                assertEquals(chapterName, matched?.titleEn)
            }
        }
    }

    @Test
    fun `manual studio question with reasoning images survives entity and json backup round-trip`() {
        val sampleQuestion = QuizQuestion(
            id = "MANUAL_REAS_TEST_1",
            subject = "REASONING",
            questionBn = "নিচের চিত্রে কয়টি ত্রিভুজ আছে?",
            optionsBn = listOf("১০টি", "১২টি", "১৪টি", "১৬টি"),
            correctIndex = 2,
            explanationBn = "শর্টকাট সূত্র অনুযায়ী মোট ১৪টি ত্রিভুজ আছে।",
            chapter = "Counting Figures",
            chapterId = StructuredQuestionSchema.chapterNameToChapterId("REASONING", "Counting Figures"),
            isPyq = true,
            pyqExam = "WBP Constable Main",
            pyqYear = "2024",
            examYearTag = "WBP Constable Main 2024",
            examFamily = StructuredQuestionSchema.pyqExamToExamFamily("WBP Constable Main"),
            questionImage = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
            optionImages = listOf(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
                "",
                "",
                ""
            )
        )

        val entity = QuestionVaultEntity.fromQuizQuestion(sampleQuestion)
        val restoredFromEntity = entity.toQuizQuestion()
        assertEquals("Counting Figures", restoredFromEntity.effectiveChapterName())
        assertTrue(restoredFromEntity.effectiveIsPyq())
        assertEquals("WBP Constable Main (2024)", restoredFromEntity.effectivePyqBadge())
        assertTrue(restoredFromEntity.hasQuestionImage())
        assertTrue(restoredFromEntity.hasOptionImage(0))

        val jsonConfig = Json { ignoreUnknownKeys = true; encodeDefaults = true; prettyPrint = true }
        val exportedJson = jsonConfig.encodeToString(ListSerializer(QuizQuestion.serializer()), listOf(restoredFromEntity))
        assertTrue(exportedJson.contains("\"question_bn\""))
        assertTrue(exportedJson.contains("\"question_image\""))
        assertTrue(exportedJson.contains("\"option_images\""))

        val decodedList = jsonConfig.decodeFromString(ListSerializer(QuizQuestion.serializer()), exportedJson)
        assertEquals(1, decodedList.size)
        assertEquals(sampleQuestion.id, decodedList.first().id)
        assertEquals(sampleQuestion.questionImage, decodedList.first().questionImage)
        assertEquals(sampleQuestion.optionImages[0], decodedList.first().optionImages[0])
    }
}

