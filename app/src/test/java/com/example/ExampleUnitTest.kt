package com.example

import com.example.network.GeminiClient
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testGeminiJsonParsing() {
        val sampleJson = """
            ```json
            [
              {
                "id": 1,
                "subject": "GK",
                "chapter": "Geography",
                "question_bn": "সৌরজগতে মোট গ্রহের সংখ্যা কয়টি?",
                "options_bn": ["৭টি", "৮টি", "৯টি", "১০টি"],
                "correct_index": 1,
                "explanation_bn": "সৌরজগতে মোট ৮টি গ্রহ রয়েছে: বুধ, শুক্র, পৃথিবী, মঙ্গল, বৃহস্পতি, শনি, ইউরেনাস এবং নেপচুন। শর্টকাট ট্রিক: ২০০৬ সালে প্লুটোকে বামন গ্রহ ঘোষণা করার পর গ্রহের সংখ্যা ৮টি।"
              }
            ]
            ```
        """.trimIndent()
        val parsed = GeminiClient.parseQuestionsDtoFromRawJson(sampleJson)
        assertEquals(1, parsed.size)
        assertEquals("সৌরজগতে মোট গ্রহের সংখ্যা কয়টি?", parsed[0].questionBn)
        assertEquals(1, parsed[0].correctIndex)
    }
}
