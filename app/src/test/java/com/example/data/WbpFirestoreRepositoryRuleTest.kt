package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class WbpFirestoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun upsertAndGetAspirantProfile_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = WbpFirestoreRepository(firestore, auth)

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.upsertAspirantProfile(
                fullName = "Rahul Mondal",
                email = ALICE_EMAIL,
                casteCategory = "OBC_B",
                targetExam = "WBP_CONSTABLE_85",
                testsAttempted = 3,
                totalScoreAccumulated = 180.5
            )
        }
        assertTrue(saveResult.isSuccess)

        val profileResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getAspirantProfile(uid)
        }
        assertTrue(profileResult.isSuccess)
        val profile = profileResult.getOrThrow()
        assertNotNull(profile)
        assertEquals("Rahul Mondal", profile?.fullName)
        assertEquals("OBC_B", profile?.casteCategory)
    }

    @Test
    fun createAndQueryQuestion_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = WbpFirestoreRepository(firestore, auth)
        val customId = "q_${UUID.randomUUID().toString().replace("-", "").take(12)}"

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createQuestion(
                subject = "GK",
                chapter = "West Bengal Static GK",
                isPyq = true,
                pyqExam = "WBP Constable Main",
                pyqYear = "2023",
                questionBn = "পশ্চিমবঙ্গের দীর্ঘতম নদী কোনটি?",
                optionsBn = listOf("ভাগীরথী-হুগলি", "দামোদর", "তিস্তা", "সুবর্ণরেখা"),
                correctIndex = 0,
                explanationBn = "ভাগীরথী-হুগলি পশ্চিমবঙ্গের দীর্ঘতম নদী।",
                customId = customId
            )
        }
        assertTrue(createResult.isSuccess)

        val listResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getUserQuestions(uid)
        }
        assertTrue(listResult.isSuccess)
        assertTrue(listResult.getOrThrow().any { it.id == customId })
    }

    @Test
    fun getQuestionById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val aliceRepo = WbpFirestoreRepository(firestore, auth)
        val questionId = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.createQuestion(
                subject = "GK",
                chapter = "History",
                isPyq = false,
                questionBn = "পলাশীর যুদ্ধ কত সালে হয়েছিল?",
                optionsBn = listOf("১৭৫৭", "১৭৬৪", "১৮৫৭", "১৯০৫"),
                correctIndex = 0,
                explanationBn = "১৭৫৭ সালের ২৩ জুন পলাশীর যুদ্ধ হয়েছিল।",
                customId = "q_${UUID.randomUUID().toString().replace("-", "").take(12)}"
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = WbpFirestoreRepository(firestore, auth)
        val crossReadResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getQuestionById(questionId)
        }
        assertTrue(crossReadResult.isFailure)
        val ex = crossReadResult.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull(ex)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, ex?.code)
    }

    @Test
    fun observeUserQuestions_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = WbpFirestoreRepository(firestore, auth)

        var caughtException: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeUserQuestions("unauth_uid").first()
            }
        } catch (e: Throwable) {
            caughtException = e
        }

        assertNotNull(caughtException)
        val firestoreException = checkNotNull(caughtException?.unwrapFirestoreException()) {
            "Expected nested FirebaseFirestoreException, got $caughtException"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }

    private companion object {
        const val ALICE_EMAIL = "alice@wbpprep.com"
        const val BOB_EMAIL = "bob@wbpprep.com"
        const val DEFAULT_TIMEOUT_MS = 10000L
        const val FLOW_TIMEOUT_MS = 10000L
    }
}
