package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(
    exception: Exception,
    operationType: OperationType,
    path: String?
): String {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

@IgnoreExtraProperties
data class CloudAspirantUser(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val casteCategory: String = "UR",
    val targetExam: String = "WBP_CONSTABLE_85",
    val testsAttempted: Long = 0L,
    val totalScoreAccumulated: Double = 0.0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CloudVaultQuestion(
    @DocumentId val id: String = "",
    val userId: String = "",
    val subject: String = "GK",
    val chapter: String = "West Bengal Static GK",
    val isPyq: Boolean = false,
    val pyqExam: String? = null,
    val pyqYear: String? = null,
    val questionBn: String = "",
    val optionsBn: List<String> = emptyList(),
    val correctIndex: Long = 0L,
    val explanationBn: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CloudTestAttempt(
    @DocumentId val id: String = "",
    val userId: String = "",
    val testTitle: String = "",
    val scoreObtained: Double = 0.0,
    val maxMarks: Double = 85.0,
    val accuracyPercent: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null
)

class WbpFirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        FirebaseAuth.getInstance()
    )

    private fun requireAuthenticatedUid(): String {
        return checkNotNull(auth.currentUser?.uid) { "User must be signed in to perform Firestore operations." }
    }

    // --- 1. Aspirant Profile Operations (/users/{userId}) ---

    suspend fun upsertAspirantProfile(
        fullName: String,
        email: String,
        casteCategory: String,
        targetExam: String,
        testsAttempted: Int = 0,
        totalScoreAccumulated: Double = 0.0
    ): Result<String> {
        val uid = try {
            requireAuthenticatedUid()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        val docRef = db.collection("users").document(uid)
        return try {
            val existingSnap = docRef.get().await()
            if (existingSnap.exists()) {
                val updatePayload = mapOf<String, Any>(
                    "fullName" to fullName.trim().take(100),
                    "casteCategory" to casteCategory,
                    "targetExam" to targetExam,
                    "testsAttempted" to testsAttempted.toLong(),
                    "totalScoreAccumulated" to totalScoreAccumulated,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updatePayload).await()
            } else {
                val createPayload = mapOf<String, Any>(
                    "userId" to uid,
                    "fullName" to fullName.trim().take(100),
                    "email" to email.trim().take(150),
                    "casteCategory" to casteCategory,
                    "targetExam" to targetExam,
                    "testsAttempted" to testsAttempted.toLong(),
                    "totalScoreAccumulated" to totalScoreAccumulated,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(createPayload).await()
            }
            Result.success(uid)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun getAspirantProfile(userId: String = requireAuthenticatedUid()): Result<CloudAspirantUser?> {
        val docRef = db.collection("users").document(userId)
        return try {
            val snapshot = docRef.get().await()
            val user = snapshot.toObject(
                CloudAspirantUser::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(user)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    // --- 2. VaultQuestion Operations (/questions/{questionId}) ---

    suspend fun createQuestion(
        subject: String,
        chapter: String,
        isPyq: Boolean,
        pyqExam: String? = null,
        pyqYear: String? = null,
        questionBn: String,
        optionsBn: List<String>,
        correctIndex: Int,
        explanationBn: String,
        customId: String? = null
    ): Result<String> {
        val uid = try {
            requireAuthenticatedUid()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        val docRef = if (!customId.isNullOrBlank()) {
            db.collection("questions").document(customId)
        } else {
            db.collection("questions").document()
        }
        val payload = buildMap<String, Any> {
            put("userId", uid)
            put("subject", subject)
            put("chapter", chapter.take(120))
            put("isPyq", isPyq)
            pyqExam?.take(120)?.takeIf { it.isNotBlank() }?.let { put("pyqExam", it) }
            pyqYear?.take(20)?.takeIf { it.isNotBlank() }?.let { put("pyqYear", it) }
            put("questionBn", questionBn.take(2000))
            put("optionsBn", optionsBn.take(4).map { it.take(500) })
            put("correctIndex", correctIndex.coerceIn(0, 3).toLong())
            put("explanationBn", explanationBn.take(3000))
            put("createdAt", FieldValue.serverTimestamp())
            put("updatedAt", FieldValue.serverTimestamp())
        }

        return try {
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun getUserQuestions(userId: String = requireAuthenticatedUid()): Result<List<CloudVaultQuestion>> {
        val path = "questions"
        return try {
            val snap = db.collection(path)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val list = snap.toObjects(
                CloudVaultQuestion::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(list)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            Result.failure(e)
        }
    }

    suspend fun getQuestionById(questionId: String): Result<CloudVaultQuestion?> {
        val docRef = db.collection("questions").document(questionId)
        return try {
            val snap = docRef.get().await()
            val question = snap.toObject(
                CloudVaultQuestion::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(question)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    fun observeUserQuestions(userId: String? = auth.currentUser?.uid): Flow<List<CloudVaultQuestion>> {
        val targetUid = userId ?: ""
        return db.collection("questions")
            .whereEqualTo("userId", targetUid)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    CloudVaultQuestion::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
    }

    // --- 3. TestAttempt Operations (/test_attempts/{attemptId}) ---

    suspend fun recordTestAttempt(
        testTitle: String,
        scoreObtained: Double,
        maxMarks: Double,
        accuracyPercent: Int
    ): Result<String> {
        val uid = try {
            requireAuthenticatedUid()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        val docRef = db.collection("test_attempts").document()
        val payload = mapOf<String, Any>(
            "userId" to uid,
            "testTitle" to testTitle.take(200),
            "scoreObtained" to scoreObtained,
            "maxMarks" to maxMarks.coerceAtLeast(1.0),
            "accuracyPercent" to accuracyPercent.coerceIn(0, 100).toLong(),
            "createdAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }
}
