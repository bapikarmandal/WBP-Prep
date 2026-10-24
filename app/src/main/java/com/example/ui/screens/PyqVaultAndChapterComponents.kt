package com.example.ui.screens

import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.data.QuizQuestion
import com.example.data.ScanVerificationReport
import com.example.data.WbpChapterSpec
import com.example.data.WbpPyqVaultSeed
import com.example.data.WbpExamHistoryGroup
import com.example.network.GeminiClient
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonWrong
import com.example.ui.theme.EmeraldSuccess
import java.io.ByteArrayOutputStream

/**
 * Live counter badge displayed on every Subject Card and Chapter Card:
 * `[Saved in Vault: X / 1000 Questions • Unseen: Y]`
 */
@Composable
fun VaultCounterBadge(
    savedCount: Int,
    unseenCount: Int,
    targetCapacity: Int = 1000,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = EmeraldSuccess.copy(alpha = 0.11f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .border(1.dp, EmeraldSuccess.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldSuccess,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = "[Saved in Vault: $savedCount / $targetCapacity Questions • Unseen: $unseenCount]",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = EmeraldSuccess
            )
        }
    }
}

/**
 * "⚡ Bulk Add +50 New PYQ-Pattern Questions to Vault" button on every Subject and Chapter card.
 */
@Composable
fun BulkAdd50VaultButton(
    isBn: Boolean,
    isLoading: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isBn)
                    "⚡ ভল্টে +৫০টি নতুন PYQ প্রশ্ন যোগ হচ্ছে..."
                else
                    "⚡ Adding +50 New PYQ Questions to Vault...",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = AmberWarning,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "⚡ Bulk Add +50 New PYQ-Pattern Questions to Vault",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Top Summary Card inside the Mock Test Hub showing Master Question Vault status,
 * Non-Repeating Engine status, subject-wise counts, and the "Manual Question Studio & Structured Database Manager" button.
 */
@Composable
fun QuestionVaultMasterBar(
    isBn: Boolean,
    questionVault: List<QuizQuestion>,
    seenQuestionIds: Set<String>,
    isImportingBook: Boolean,
    scannerCurrentPage: Int = 0,
    scannerTotalPages: Int = 0,
    scannerAddedCount: Int = 0,
    onOpenImporterModal: () -> Unit,
    onResetSeenHistory: () -> Unit
) {
    val totalVault = questionVault.size
    val totalUnseen = questionVault.count { it.id !in seenQuestionIds }
    val gkCount = remember(questionVault) { questionVault.count { it.subject.equals("GK", ignoreCase = true) } }
    val mathCount = remember(questionVault) { questionVault.count { it.subject.equals("MATH", ignoreCase = true) } }
    val reasCount = remember(questionVault) { questionVault.count { it.subject.equals("REASONING", ignoreCase = true) } }
    val engCount = remember(questionVault) { questionVault.count { it.subject.equals("ENGLISH", ignoreCase = true) } }
    val pyqCount = remember(questionVault) { questionVault.count { it.effectiveIsPyq() } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("question_vault_master_bar"),
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
                        text = "WBPRB OFFICIAL PYQ VAULT (2005–PRESENT) • WBP & KP SMART QUESTION BANK",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "[Saved in Vault: $totalVault / 4000 Questions • Unseen: $totalUnseen • PYQ: $pyqCount]",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "GK: $gkCount • Math: $mathCount • Reasoning: $reasCount • English: $engCount",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (seenQuestionIds.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onResetSeenHistory() }
                            .testTag("reset_seen_history_button"),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Seen Questions",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Reset Seen (${seenQuestionIds.size})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // "✍️ Manual Question Studio (Batch 1–50) & 🗂️ Database Manager" Button
            Button(
                onClick = onOpenImporterModal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("open_pyq_book_importer_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "✍️ Manual Question Studio (Batch 1–50) & 🗂️ Database Manager",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Compresses an uploaded Reasoning diagram/option image using a client-side canvas
 * (max width/height `500px`, JPEG quality `75`) onto a crisp white background before Base64 encoding,
 * so storing hundreds of Reasoning figures never slows down the app.
 */
fun compressReasoningImageToBase64(
    context: android.content.Context,
    uri: Uri,
    maxDimension: Int = 500
): String? {
    return try {
        val rawBytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        val src = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size) ?: return null
        val w = src.width.coerceAtLeast(1)
        val h = src.height.coerceAtLeast(1)
        val ratio = w.toFloat() / h.toFloat()
        val (targetW, targetH) = if (w <= maxDimension && h <= maxDimension) {
            w to h
        } else if (w >= h) {
            maxDimension to (maxDimension / ratio).toInt().coerceAtLeast(1)
        } else {
            (maxDimension * ratio).toInt().coerceAtLeast(1) to maxDimension
        }
        val softSrc = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O &&
            src.config == Bitmap.Config.HARDWARE
        ) {
            src.copy(Bitmap.Config.ARGB_8888, false) ?: src
        } else {
            src
        }
        val canvasBmp = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(canvasBmp)
        canvas.drawColor(android.graphics.Color.WHITE)
        val scaled = if (softSrc.width == targetW && softSrc.height == targetH) {
            softSrc
        } else {
            Bitmap.createScaledBitmap(softSrc, targetW, targetH, true)
        }
        canvas.drawBitmap(scaled, 0f, 0f, null)
        val bos = ByteArrayOutputStream()
        canvasBmp.compress(Bitmap.CompressFormat.JPEG, 75, bos)
        Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP)
            .substringAfter("base64,")
            .replace(Regex("\\s+"), "")
            .trim()
    } catch (_: Exception) {
        null
    }
}

/**
 * Decodes a Base64-encoded image string into an Android [Bitmap] safely.
 */
fun decodeBase64ToBitmap(base64Str: String?): Bitmap? {
    if (base64Str.isNullOrBlank()) return null
    return try {
        val clean = base64Str.substringAfter("base64,").replace(Regex("\\s+"), "").trim()
        val bytes = Base64.decode(clean, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) {
        null
    }
}

/**
 * Feature 4 Component: Renders `question_image` as a clean, centered diagram card below the question text
 * with tap-to-zoom full-screen preview in Live Mock Tests, Chapter Tests, and Post-Test Solutions.
 */
@Composable
fun QuestionDiagramCard(
    base64Image: String?,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(base64Image) { decodeBase64ToBitmap(base64Image) } ?: return
    var showZoomDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { showZoomDialog = true }
            .testTag("reasoning_question_diagram_card"),
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Question Figure / Diagram",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "🔍 Tap Figure to Zoom Full-Screen",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF475569),
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (showZoomDialog) {
        Dialog(
            onDismissRequest = { showZoomDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(18.dp)),
                color = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🖼️ Reasoning Question Figure (Full View)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        IconButton(onClick = { showZoomDialog = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Zoom",
                                tint = Color(0xFF0F172A)
                            )
                        }
                    }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Zoomed Question Figure",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

/**
 * Feature 4 Component: Renders `option_images[i]` neatly inside Option A, B, C, or D card
 * alongside any option text in Live Mock Tests and Solutions.
 */
@Composable
fun OptionDiagramThumbnail(
    base64Image: String?,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(base64Image) { decodeBase64ToBitmap(base64Image) } ?: return
    var showZoomDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { showZoomDialog = true },
        color = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Option Figure",
            modifier = Modifier
                .size(width = 88.dp, height = 68.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(3.dp),
            contentScale = ContentScale.Fit
        )
    }

    if (showZoomDialog) {
        Dialog(onDismissRequest = { showZoomDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🖼️ Option Figure Preview",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        IconButton(onClick = { showZoomDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF0F172A))
                        }
                    }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Zoomed Option Figure",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

/**
 * Manual Question Studio & Structured Database Manager Modal:
 * Replaces the old AI OCR scanner modal with a 100% deterministic, structured studio featuring:
 * 1. Tab 1: "✍️ Manual Question Creator (Batch 1–50)"
 *    - Sticky Metadata Header (`Subject`: GK/Math/Reasoning/English, `Chapter`: dynamically mapped,
 *      `Is this a PYQ?` toggle + `Exam Name` & `Exam Year 2005–2026` selectors).
 *    - 1-to-50 Batch Staging Queue with clickable numbered pills (`#1, #2, #3...`) to review/edit any staged item.
 *    - Question Text Box + Dedicated Reasoning Question Figure Uploader (`question_image`).
 *    - 4 Option Inputs (A, B, C, D) with Correct Answer (`0–3`) selector + Dedicated Reasoning Option Image Uploaders (`option_images[0..3]`).
 *    - Explanation Box (`explanation_bn`) + `"✨ Auto-Generate Bengali Shortcut"` button powered by `gemini-2.5-flash`.
 *    - `"+ Add to Batch & Write Next Question (Batch: X / 50)"` and `"✅ Save All [X] Questions to Database Now"`.
 * 2. Tab 2: "🗂️ Manage Question Database"
 *    - Full Database Explorer with Subject, Chapter, PYQ Only vs All, and Keyword Search filters.
 *    - 1-Click Database Backup (`"📤 Export Database Backup (.json)"` & `"📥 Import Database Backup (.json)"`).
 *    - 1-Tap Edit or Delete on any question.
 */
@Composable
fun PyqBookImporterDialog(
    isBn: Boolean,
    isImporting: Boolean,
    scannerCurrentPage: Int = 0,
    scannerTotalPages: Int = 0,
    scannerAddedCount: Int = 0,
    scannerExtractedCount: Int = 0,
    scannerStep: Int = 0,
    scannerErrorMessage: String? = null,
    scannerVerificationReport: ScanVerificationReport? = null,
    scannerToastMessage: String? = null,
    questionVault: List<QuizQuestion> = emptyList(),
    isGeneratingExplanation: Boolean = false,
    onClearToastMessage: () -> Unit = {},
    onClearErrorMessage: () -> Unit = {},
    onNotifyInputLoaded: () -> Unit = {},
    onUpdateUploadedQuestion: (QuizQuestion) -> Unit = {},
    onDeleteUploadedQuestion: (String) -> Unit = {},
    onStartUploadedQuestionsTest: (List<QuizQuestion>) -> Unit = {},
    onSaveManualBatch: (List<QuizQuestion>) -> Unit = {},
    onExportDatabaseJson: ((String) -> Unit) -> Unit = {},
    onImportDatabaseJson: (String) -> Unit = {},
    onAutoGenerateExplanation: (
        subject: String,
        chapter: String,
        questionText: String,
        options: List<String>,
        correctIndex: Int,
        onResult: (String) -> Unit
    ) -> Unit = { _, _, _, _, _, _ -> },
    onDismiss: () -> Unit,
    onImportBookContent: (rawText: String, base64Doc: String?, mimeType: String?, subjectHint: String) -> Unit = { _, _, _, _ -> },
    onScanSmartBatch: (
        rawTextOrUrl: String,
        attachments: List<GeminiClient.ScannerInlineAttachment>,
        declaredPageCount: Int,
        subjectHint: String?
    ) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current
    var activeStudioTab by remember { mutableStateOf(0) } // 0 = Manual Batch Creator (1-50), 1 = Manage Question Database

    // Sticky Metadata Header State (stays locked across batch questions!)
    var selectedSubject by remember { mutableStateOf("GK") } // "GK", "Math", "Reasoning", "English"
    val chaptersForCurrentSubject = remember(selectedSubject) {
        com.example.data.StructuredQuestionSchema.chaptersForSubject(selectedSubject)
    }
    var selectedChapter by remember(selectedSubject) {
        mutableStateOf(chaptersForCurrentSubject.firstOrNull() ?: "History")
    }
    var isPyq by remember { mutableStateOf(false) }
    var selectedPyqExam by remember { mutableStateOf(com.example.data.StructuredQuestionSchema.pyqExams.first()) }
    var selectedPyqYear by remember { mutableStateOf("2024") }

    // 1-to-50 Batch Staging Queue State
    var stagedBatch by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var editingBatchIndex by remember { mutableStateOf<Int?>(null) } // null = writing new question, 0..N = editing queued item

    // Current Question Entry Form State
    var questionText by remember { mutableStateOf("") }
    var questionImageBase64 by remember { mutableStateOf<String?>(null) }
    var optionAText by remember { mutableStateOf("") }
    var optionBText by remember { mutableStateOf("") }
    var optionCText by remember { mutableStateOf("") }
    var optionDText by remember { mutableStateOf("") }
    var optionImagesBase64 by remember { mutableStateOf(listOf("", "", "", "")) }
    var correctIndex by remember { mutableStateOf(0) }
    var explanationBn by remember { mutableStateOf("") }
    var formValidationMessage by remember { mutableStateOf<String?>(null) }
    var pendingOptionImageSlot by remember { mutableStateOf(0) }

    LaunchedEffect(scannerToastMessage) {
        if (!scannerToastMessage.isNullOrBlank()) {
            Toast.makeText(context, scannerToastMessage, Toast.LENGTH_LONG).show()
            onClearToastMessage()
        }
    }

    // Reasoning Question Figure Picker
    val questionFigurePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val b64 = compressReasoningImageToBase64(context, uri, maxDimension = 500)
            if (!b64.isNullOrBlank()) {
                questionImageBase64 = b64
                formValidationMessage = null
            } else {
                formValidationMessage = "⚠️ Could not read image file. Please select a valid PNG/JPG/WebP image."
            }
        }
    }

    // Reasoning 4 Option Image Picker
    val optionFigurePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val slot = pendingOptionImageSlot.coerceIn(0, 3)
            val b64 = compressReasoningImageToBase64(context, uri, maxDimension = 500)
            if (!b64.isNullOrBlank()) {
                val mutable = optionImagesBase64.toMutableList()
                mutable[slot] = b64
                optionImagesBase64 = mutable
                formValidationMessage = null
            }
        }
    }

    fun loadStagedQuestionIntoForm(index: Int) {
        val q = stagedBatch.getOrNull(index) ?: return
        editingBatchIndex = index
        selectedSubject = com.example.data.StructuredQuestionSchema.displaySubjectLabel(q.subject)
        selectedChapter = q.effectiveChapterName()
        isPyq = q.effectiveIsPyq()
        if (q.pyqExam.isNotBlank()) selectedPyqExam = q.pyqExam
        if (q.pyqYear.isNotBlank()) selectedPyqYear = q.pyqYear
        questionText = q.displayQuestionText()
        questionImageBase64 = q.questionImage
        val opts = q.displayOptions()
        optionAText = opts.getOrElse(0) { "" }
        optionBText = opts.getOrElse(1) { "" }
        optionCText = opts.getOrElse(2) { "" }
        optionDText = opts.getOrElse(3) { "" }
        optionImagesBase64 = List(4) { i -> q.optionImages.getOrElse(i) { "" } }
        correctIndex = q.correctIndex.coerceIn(0, 3)
        explanationBn = q.displayExplanationBn()
        formValidationMessage = null
    }

    fun clearEntryFormKeepMetadata() {
        editingBatchIndex = null
        questionText = ""
        questionImageBase64 = null
        optionAText = ""
        optionBText = ""
        optionCText = ""
        optionDText = ""
        optionImagesBase64 = listOf("", "", "", "")
        correctIndex = 0
        explanationBn = ""
        formValidationMessage = null
    }

    fun buildValidatedQuizQuestionFromForm(): QuizQuestion? {
        val isReasoning = selectedSubject.equals("Reasoning", ignoreCase = true)
        val cleanQText = questionText.trim()
        if (cleanQText.isEmpty() && (!isReasoning || questionImageBase64.isNullOrBlank())) {
            formValidationMessage = "⚠️ Please enter the Question Text (or attach a Question Figure for Reasoning)."
            return null
        }

        val rawOpts = listOf(optionAText.trim(), optionBText.trim(), optionCText.trim(), optionDText.trim())
        val finalOpts = rawOpts.mapIndexed { idx, txt ->
            val hasImg = isReasoning && optionImagesBase64.getOrNull(idx)?.isNotBlank() == true
            if (txt.isNotBlank()) txt
            else if (hasImg) "চিত্র ${'A' + idx}"
            else ""
        }

        if (finalOpts.any { it.isBlank() }) {
            formValidationMessage = "⚠️ Please fill in all 4 Options (A, B, C, D) or attach Option Images for Reasoning."
            return null
        }

        val normSubject = com.example.data.StructuredQuestionSchema.normalizeSubjectCode(selectedSubject)
        val finalQuestionText = cleanQText.ifBlank { "প্রদত্ত চিত্রটি পর্যবেক্ষণ করে সঠিক উত্তরটি নির্বাচন করুন:" }
        val correctLetter = listOf("A", "B", "C", "D")[correctIndex.coerceIn(0, 3)]
        val finalExp = explanationBn.trim().ifBlank {
            "✅ সঠিক উত্তর: ($correctLetter) ${finalOpts[correctIndex.coerceIn(0, 3)]}। অধ্যায়: $selectedChapter।"
        }
        val pyqBadgeTag = if (isPyq) "$selectedPyqExam $selectedPyqYear" else ""
        val existingId = editingBatchIndex?.let { stagedBatch.getOrNull(it)?.id }
            ?: "MANUAL_${normSubject}_${System.currentTimeMillis()}_${stagedBatch.size + 1}"

        return QuizQuestion(
            id = existingId,
            subject = normSubject,
            chapter = selectedChapter,
            chapterId = com.example.data.StructuredQuestionSchema.chapterNameToChapterId(normSubject, selectedChapter),
            isPyq = isPyq,
            pyqExam = if (isPyq) selectedPyqExam else "",
            pyqYear = if (isPyq) selectedPyqYear else "",
            examYearTag = pyqBadgeTag,
            examFamily = if (isPyq) com.example.data.StructuredQuestionSchema.pyqExamToExamFamily(selectedPyqExam) else "WBP_CONSTABLE",
            questionBn = finalQuestionText,
            questionEn = finalQuestionText,
            questionImage = if (isReasoning) questionImageBase64?.takeIf { it.isNotBlank() }.orEmpty() else "",
            optionsBn = finalOpts,
            optionsEn = finalOpts,
            optionImages = if (isReasoning && optionImagesBase64.any { it.isNotBlank() }) optionImagesBase64 else emptyList(),
            correctIndex = correctIndex.coerceIn(0, 3),
            explanationBn = finalExp,
            explanationEn = finalExp,
            shortcutTrickBn = "⚡ অধ্যায়: $selectedChapter",
            shortcutTrickEn = "⚡ Chapter: $selectedChapter"
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(20.dp))
                .testTag("pyq_book_importer_modal"),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Manual Question Studio & Database Manager",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Add 1–50 Structured Questions • Reasoning Figures • PYQ (2005–2026) • JSON Backup",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Studio",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Studio Top Mode Switcher: [✍️ Manual Creator (Batch 1–50)] vs [🗂️ Manage Question Database]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        0 to "✍️ Manual Creator (Batch ${stagedBatch.size}/50)",
                        1 to "🗂️ Manage Database (${questionVault.size})"
                    ).forEach { (tabIdx, tabTitle) ->
                        val isSel = activeStudioTab == tabIdx
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { activeStudioTab = tabIdx }
                                .testTag("studio_mode_tab_$tabIdx"),
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = tabTitle,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (activeStudioTab == 0) {
                    // =========================================================================
                    // TAB 1: MANUAL QUESTION CREATOR (1 TO 50 QUESTIONS AT ONCE)
                    // =========================================================================

                    // 1-to-50 Batch Staging Queue Numbered Pills (#1, #2, #3...)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📋 STAGING BATCH QUEUE (${stagedBatch.size} / 50 Questions Ready)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (editingBatchIndex != null) {
                                    Text(
                                        text = "+ Write New Question",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess,
                                        modifier = Modifier.clickable { clearEntryFormKeepMetadata() }
                                    )
                                }
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(stagedBatch.size) { idx ->
                                    val isEditingThis = editingBatchIndex == idx
                                    val itemQ = stagedBatch[idx]
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .clickable { loadStagedQuestionIntoForm(idx) }
                                            .testTag("batch_pill_${idx + 1}"),
                                        color = if (isEditingThis) MaterialTheme.colorScheme.primary else EmeraldSuccess.copy(alpha = 0.16f),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = "#${idx + 1} (${com.example.data.StructuredQuestionSchema.displaySubjectLabel(itemQ.subject)})",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isEditingThis) Color.White else EmeraldSuccess,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                                item {
                                    val isNewActive = editingBatchIndex == null
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .clickable { clearEntryFormKeepMetadata() },
                                        color = if (isNewActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = "✍️ #${stagedBatch.size + 1} (Current Draft)",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // A. STICKY METADATA HEADER (Subject, Chapter, PYQ Toggle + Exam Name & Year 2005–2026)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "📌 STICKY METADATA HEADER (Stays Locked Across Your Batch)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // 1. Subject Selector: [GK] [Math] [Reasoning] [English]
                            Text(
                                text = "1. Select Subject:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                com.example.data.StructuredQuestionSchema.subjects.forEach { subLabel ->
                                    val isSel = selectedSubject.equals(subLabel, ignoreCase = true)
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                selectedSubject = subLabel
                                                val newChaps = com.example.data.StructuredQuestionSchema.chaptersForSubject(subLabel)
                                                if (selectedChapter !in newChaps) {
                                                    selectedChapter = newChaps.firstOrNull() ?: ""
                                                }
                                            }
                                            .testTag("manual_subject_chip_$subLabel"),
                                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .border(
                                                    1.dp,
                                                    if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .padding(vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = subLabel,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            // 2. Dynamic Chapter Selector (Mapped strictly to chosen Subject)
                            Text(
                                text = "2. Select $selectedSubject Chapter (${chaptersForCurrentSubject.size} Official Chapters):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(chaptersForCurrentSubject) { chapName ->
                                    val isChapSel = selectedChapter == chapName
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .clickable { selectedChapter = chapName },
                                        color = if (isChapSel) EmeraldSuccess else MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = chapName,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isChapSel) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isChapSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier
                                                .border(
                                                    1.dp,
                                                    if (isChapSel) EmeraldSuccess else MaterialTheme.colorScheme.outline,
                                                    RoundedCornerShape(50)
                                                )
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            // 3. PYQ Toggle ("Is this a Previous Year Question (PYQ)?")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { isPyq = !isPyq }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "3. Is this a Previous Year Question (PYQ)?",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isPyq) "Tagged as Official WBPRB PYQ ($selectedPyqExam - $selectedPyqYear)"
                                        else "Toggle ON to tag with Exam Name & Year (2005–2026)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isPyq) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    color = if (isPyq) AmberWarning else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = if (isPyq) "✓ PYQ ON" else "PYQ OFF",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPyq) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .border(1.dp, if (isPyq) AmberWarning else MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
                                            .padding(horizontal = 12.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            AnimatedVisibility(visible = isPyq) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Select PYQ Exam Name:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(com.example.data.StructuredQuestionSchema.pyqExams) { examName ->
                                            val isExamSel = selectedPyqExam == examName
                                            Surface(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(50))
                                                    .clickable { selectedPyqExam = examName },
                                                color = if (isExamSel) AmberWarning else MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(50)
                                            ) {
                                                Text(
                                                    text = examName,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (isExamSel) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isExamSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier
                                                        .border(1.dp, if (isExamSel) AmberWarning else MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
                                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = "Select PYQ Exam Year (2005–2026):",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(com.example.data.StructuredQuestionSchema.pyqYears) { yr ->
                                            val isYrSel = selectedPyqYear == yr
                                            Surface(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(50))
                                                    .clickable { selectedPyqYear = yr },
                                                color = if (isYrSel) AmberWarning else MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(50)
                                            ) {
                                                Text(
                                                    text = yr,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (isYrSel) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isYrSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier
                                                        .border(1.dp, if (isYrSel) AmberWarning else MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
                                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // B. QUESTION & OPTIONS ENTRY FORM
                    val isReasoningSubject = selectedSubject.equals("Reasoning", ignoreCase = true)
                    val isEnglishSubject = selectedSubject.equals("English", ignoreCase = true)

                    OutlinedTextField(
                        value = questionText,
                        onValueChange = {
                            questionText = it
                            formValidationMessage = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_question_text_input"),
                        minLines = 3,
                        maxLines = 6,
                        label = {
                            Text(
                                if (isEnglishSubject)
                                    "Question Text (Type or Paste in English)"
                                else
                                    "Question Text (প্রশ্নটি বাংলায় টাইপ বা পেস্ট করুন)"
                            )
                        },
                        placeholder = {
                            Text(
                                if (isEnglishSubject)
                                    "e.g., Choose the correct preposition: He died ___ cholera."
                                else
                                    "যেমন: সৌরজগতে মোট গ্রহের সংখ্যা কয়টি?"
                            )
                        }
                    )

                    // Feature 3A: Dedicated Question Image Uploader for Reasoning
                    if (isReasoningSubject) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🧩 Reasoning Figure / Diagram Support",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (!questionImageBase64.isNullOrBlank()) {
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .clickable { questionImageBase64 = null },
                                            color = CrimsonWrong.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "✕ Remove Figure",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = CrimsonWrong,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                OutlinedButton(
                                    onClick = { questionFigurePicker.launch("image/*") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("attach_reasoning_question_figure_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (questionImageBase64.isNullOrBlank())
                                            "🖼️ Attach Question Figure / Diagram (Optional)"
                                        else
                                            "🖼️ Replace Question Figure / Diagram",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (!questionImageBase64.isNullOrBlank()) {
                                    QuestionDiagramCard(base64Image = questionImageBase64)
                                }
                            }
                        }
                    }

                    // 4 Option Inputs (A, B, C, D) + Correct Answer Selector + Reasoning Option Images
                    Text(
                        text = "Select the Correct Answer (Tap A / B / C / D) & Enter 4 Options:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val optionConfigs = listOf(
                        Triple(0, "Option A", optionAText to { v: String -> optionAText = v }),
                        Triple(1, "Option B", optionBText to { v: String -> optionBText = v }),
                        Triple(2, "Option C", optionCText to { v: String -> optionCText = v }),
                        Triple(3, "Option D", optionDText to { v: String -> optionDText = v })
                    )

                    optionConfigs.forEach { (idx, label, statePair) ->
                        val isCorrect = correctIndex == idx
                        val optLetter = ('A' + idx).toString()
                        val optImgB64 = optionImagesBase64.getOrNull(idx).orEmpty()

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { correctIndex = idx }
                                        .testTag("select_correct_option_$optLetter"),
                                    color = if (isCorrect) EmeraldSuccess else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = if (isCorrect) "✓ $optLetter" else optLetter,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCorrect) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                                    )
                                }

                                OutlinedTextField(
                                    value = statePair.first,
                                    onValueChange = {
                                        statePair.second(it)
                                        formValidationMessage = null
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("manual_option_input_$optLetter"),
                                    singleLine = true,
                                    label = {
                                        Text(
                                            if (isCorrect) "$label (✓ Correct Answer)"
                                            else label
                                        )
                                    }
                                )

                                // Feature 3B: 4 Option Image Uploaders when Subject == Reasoning
                                if (isReasoningSubject) {
                                    OutlinedButton(
                                        onClick = {
                                            pendingOptionImageSlot = idx
                                            optionFigurePicker.launch("image/*")
                                        },
                                        modifier = Modifier.height(48.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (optImgB64.isBlank()) "🖼️ Add Option Image" else "🖼️✓",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            if (isReasoningSubject && optImgB64.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(start = 48.dp)
                                ) {
                                    OptionDiagramThumbnail(base64Image = optImgB64)
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                val mutable = optionImagesBase64.toMutableList()
                                                mutable[idx] = ""
                                                optionImagesBase64 = mutable
                                            },
                                        color = CrimsonWrong.copy(alpha = 0.14f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "✕ Remove Option $optLetter Image",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = CrimsonWrong,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Feature 7: Explanation (বাংলায়) + 1-Click "✨ Auto-Generate Bengali Shortcut" Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Explanation / Shortcut (বাংলায়):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedButton(
                            onClick = {
                                val optsList = listOf(optionAText, optionBText, optionCText, optionDText)
                                onAutoGenerateExplanation(
                                    selectedSubject,
                                    selectedChapter,
                                    questionText,
                                    optsList,
                                    correctIndex
                                ) { generatedExp ->
                                    explanationBn = generatedExp
                                }
                            },
                            enabled = !isGeneratingExplanation,
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("auto_generate_bengali_shortcut_button"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            if (isGeneratingExplanation) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(13.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Generating...",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AmberWarning,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "✨ Auto-Generate Bengali Shortcut",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = explanationBn,
                        onValueChange = { explanationBn = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_explanation_input"),
                        minLines = 2,
                        maxLines = 5,
                        label = { Text("Explanation (বাংলায়) — Optional or Click Auto-Generate Above") },
                        placeholder = { Text("ধাপে ধাপে সমাধান বা ৩০ সেকেন্ডের শর্টকাট ট্রিক...") }
                    )

                    if (!formValidationMessage.isNullOrBlank()) {
                        Surface(
                            color = CrimsonWrong.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = formValidationMessage!!,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CrimsonWrong,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Feature 2C: Two Action Buttons for 1-to-50 Batch Staging & Saving
                    OutlinedButton(
                        onClick = {
                            if (stagedBatch.size >= 50 && editingBatchIndex == null) {
                                formValidationMessage = "⚠️ Batch reached maximum 50 questions! Click 'Save All Questions to Database Now' first."
                                return@OutlinedButton
                            }
                            val built = buildValidatedQuizQuestionFromForm() ?: return@OutlinedButton
                            stagedBatch = if (editingBatchIndex != null) {
                                stagedBatch.toMutableList().also { it[editingBatchIndex!!] = built }
                            } else {
                                stagedBatch + built
                            }
                            clearEntryFormKeepMetadata()
                            Toast.makeText(
                                context,
                                "Added to staging batch (${stagedBatch.size} / 50). Metadata locked — write next question!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("add_to_batch_next_question_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (editingBatchIndex != null)
                                "💾 Update Question #${editingBatchIndex!! + 1} in Batch & Write Next (Batch: ${stagedBatch.size} / 50)"
                            else
                                "+ Add to Batch & Write Next Question (Batch: ${stagedBatch.size} / 50)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val totalReadyToSave = stagedBatch.size + if (editingBatchIndex == null && (questionText.isNotBlank() || !questionImageBase64.isNullOrBlank())) 1 else 0
                    Button(
                        onClick = {
                            val finalBatch = stagedBatch.toMutableList()
                            if (editingBatchIndex != null) {
                                val edited = buildValidatedQuizQuestionFromForm() ?: return@Button
                                finalBatch[editingBatchIndex!!] = edited
                            } else if (questionText.isNotBlank() || !questionImageBase64.isNullOrBlank()) {
                                val currentDraft = buildValidatedQuizQuestionFromForm() ?: return@Button
                                finalBatch.add(currentDraft)
                            }
                            if (finalBatch.isEmpty()) {
                                formValidationMessage = "⚠️ Please write at least 1 question before saving to Database."
                                return@Button
                            }
                            onSaveManualBatch(finalBatch)
                            stagedBatch = emptyList()
                            clearEntryFormKeepMetadata()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_all_batch_questions_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldSuccess,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✅ Save All [${totalReadyToSave.coerceAtLeast(1)}] Questions to Database Now",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Show Verification Card & Practice Button right after saving a batch
                    if (scannerVerificationReport != null && scannerVerificationReport.extractedQuestions.isNotEmpty()) {
                        BeforeAfterCounterProofCard(report = scannerVerificationReport)
                        Button(
                            onClick = {
                                onDismiss()
                                onStartUploadedQuestionsTest(scannerVerificationReport.extractedQuestions)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("test_uploaded_questions_now_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Practice These [${scannerVerificationReport.extractedQuestions.size}] Saved Questions Now",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // =========================================================================
                    // TAB 2: MANAGE QUESTION DATABASE EXPLORER & JSON BACKUP
                    // =========================================================================
                    MyUploadedQuestionsVaultSection(
                        isBn = false,
                        uploadedQuestions = questionVault,
                        onStartUploadedTest = { qs ->
                            onDismiss()
                            onStartUploadedQuestionsTest(qs)
                        },
                        onOpenScannerModal = { activeStudioTab = 0 },
                        onUpdateUploadedQuestion = onUpdateUploadedQuestion,
                        onDeleteUploadedQuestion = onDeleteUploadedQuestion,
                        onExportDatabaseJson = onExportDatabaseJson,
                        onImportDatabaseJson = onImportDatabaseJson
                    )
                }
            }
        }
    }
}

/**
 * English Verification Counter Card:
 * `"Database Before Save: X | Newly Added: +Y | New Database Total: X+Y"`
 */
@Composable
private fun BeforeAfterCounterProofCard(
    report: ScanVerificationReport
) {
    val breakdownText = remember(report.subjectBreakdown) {
        val parts = listOf("GK", "MATH", "REASONING", "ENGLISH").mapNotNull { sub ->
            val c = report.subjectBreakdown[sub] ?: 0
            if (c > 0) {
                val label = com.example.data.StructuredQuestionSchema.displaySubjectLabel(sub)
                "$label: +$c"
            } else null
        }
        if (parts.isEmpty()) "GK: +0 | Math: +0 | Reasoning: +0 | English: +0"
        else parts.joinToString(" | ")
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scanner_before_after_proof_card"),
        color = EmeraldSuccess.copy(alpha = 0.1f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, EmeraldSuccess.copy(alpha = 0.65f), RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "QUESTION DATABASE VERIFICATION",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldSuccess
                )
            }

            Text(
                text = "Database Before Save: ${report.beforeScanTotal} | Newly Added: +${report.newlyAddedCount} | New Database Total: ${report.afterScanTotal}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = EmeraldSuccess
            )

            if (report.duplicatesIgnoredCount > 0) {
                Text(
                    text = "(${report.duplicatesIgnoredCount} Duplicate Question(s) Updated/Ignored)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AmberWarning
                )
            }

            Text(
                text = "📊 Subject Breakdown: $breakdownText",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Individual Structured Question Card with Reasoning Diagram Rendering, PYQ & Chapter Badges,
 * Inline Editor (text, chapter, PYQ tag, options, Reasoning images), and 1-Tap Delete (🗑️).
 */
@Composable
fun UploadedQuestionPreviewCard(
    index: Int,
    question: QuizQuestion,
    isDuplicateIgnored: Boolean = false,
    onSaveEditedQuestion: (QuizQuestion) -> Unit,
    onDeleteQuestion: () -> Unit
) {
    val context = LocalContext.current
    var isEditing by remember(question.id) { mutableStateOf(false) }
    var editedSubject by remember(question.id, question.subject) {
        mutableStateOf(com.example.data.StructuredQuestionSchema.displaySubjectLabel(question.subject))
    }
    var editedChapter by remember(question.id, question.chapter, question.chapterId) {
        mutableStateOf(question.effectiveChapterName())
    }
    var editedIsPyq by remember(question.id, question.isPyq, question.examYearTag) {
        mutableStateOf(question.effectiveIsPyq())
    }
    var editedPyqExam by remember(question.id, question.pyqExam) {
        mutableStateOf(question.pyqExam.ifBlank { com.example.data.StructuredQuestionSchema.pyqExams.first() })
    }
    var editedPyqYear by remember(question.id, question.pyqYear) {
        mutableStateOf(question.pyqYear.ifBlank { "2024" })
    }
    var editedQuestionText by remember(question.id, question.questionBn, question.questionEn) {
        mutableStateOf(question.displayQuestionText())
    }
    var editedQuestionImage by remember(question.id, question.questionImage) {
        mutableStateOf(question.questionImage)
    }
    val initialOpts = remember(question.id, question.optionsBn, question.optionsEn) {
        val list = question.displayOptions()
        List(4) { i -> list.getOrElse(i) { "Option ${'A' + i}" } }
    }
    var editedOptA by remember(question.id) { mutableStateOf(initialOpts[0]) }
    var editedOptB by remember(question.id) { mutableStateOf(initialOpts[1]) }
    var editedOptC by remember(question.id) { mutableStateOf(initialOpts[2]) }
    var editedOptD by remember(question.id) { mutableStateOf(initialOpts[3]) }
    var editedCorrectIdx by remember(question.id, question.correctIndex) {
        mutableStateOf(question.correctIndex.coerceIn(0, 3))
    }
    var editedExplanation by remember(question.id, question.explanationBn) {
        mutableStateOf(question.displayExplanationBn())
    }

    val editFigurePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val b64 = compressReasoningImageToBase64(context, uri, 500)
            if (!b64.isNullOrBlank()) editedQuestionImage = b64
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("uploaded_preview_card_${question.id}"),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Row: Question #, Subject + Chapter Badge, and PYQ Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Q#$index • ${com.example.data.StructuredQuestionSchema.displaySubjectLabel(question.subject)} • ${question.effectiveChapterName()}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                val pyqBadge = question.effectivePyqBadge()
                if (pyqBadge.isNotBlank()) {
                    Surface(
                        color = AmberWarning.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "🏛️ $pyqBadge",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AmberWarning,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                } else {
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "✅ Saved in Bank",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (isEditing) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        com.example.data.StructuredQuestionSchema.subjects.forEach { sub ->
                            val sel = editedSubject.equals(sub, ignoreCase = true)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable {
                                        editedSubject = sub
                                        editedChapter = com.example.data.StructuredQuestionSchema.chaptersForSubject(sub).firstOrNull() ?: ""
                                    },
                                color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = sub,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Chapter selector in inline edit mode
                    val editChaps = com.example.data.StructuredQuestionSchema.chaptersForSubject(editedSubject)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(editChaps) { ch ->
                            val sel = editedChapter == ch
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable { editedChapter = ch },
                                color = if (sel) EmeraldSuccess else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = ch,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editedQuestionText,
                        onValueChange = { editedQuestionText = it },
                        label = { Text("Question Text (বাংলা / English)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (editedSubject.equals("Reasoning", ignoreCase = true)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(onClick = { editFigurePicker.launch("image/*") }) {
                                Text(if (editedQuestionImage.isNullOrBlank()) "🖼️ Attach Figure" else "🖼️ Replace Figure")
                            }
                            if (editedQuestionImage.isNotBlank()) {
                                OutlinedButton(onClick = { editedQuestionImage = "" }) {
                                    Text("✕ Remove Figure", color = CrimsonWrong)
                                }
                            }
                        }
                    }

                    listOf(
                        Triple(0, "Option A", editedOptA to { v: String -> editedOptA = v }),
                        Triple(1, "Option B", editedOptB to { v: String -> editedOptB = v }),
                        Triple(2, "Option C", editedOptC to { v: String -> editedOptC = v }),
                        Triple(3, "Option D", editedOptD to { v: String -> editedOptD = v })
                    ).forEach { (optIdx, label, pair) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { editedCorrectIdx = optIdx },
                                color = if (editedCorrectIdx == optIdx) EmeraldSuccess else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (editedCorrectIdx == optIdx) "✓ ${'A' + optIdx}" else "${'A' + optIdx}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (editedCorrectIdx == optIdx) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                                )
                            }
                            OutlinedTextField(
                                value = pair.first,
                                onValueChange = pair.second,
                                label = { Text(label) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    OutlinedTextField(
                        value = editedExplanation,
                        onValueChange = { editedExplanation = it },
                        label = { Text("Explanation (বাংলায়)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { isEditing = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val newOpts = listOf(editedOptA.trim(), editedOptB.trim(), editedOptC.trim(), editedOptD.trim())
                                val normSub = com.example.data.StructuredQuestionSchema.normalizeSubjectCode(editedSubject)
                                val updated = question.copy(
                                    subject = normSub,
                                    chapter = editedChapter,
                                    chapterId = com.example.data.StructuredQuestionSchema.chapterNameToChapterId(normSub, editedChapter),
                                    isPyq = editedIsPyq,
                                    pyqExam = if (editedIsPyq) editedPyqExam else "",
                                    pyqYear = if (editedIsPyq) editedPyqYear else "",
                                    examYearTag = if (editedIsPyq) "$editedPyqExam $editedPyqYear" else question.examYearTag,
                                    questionBn = editedQuestionText.trim(),
                                    questionEn = editedQuestionText.trim(),
                                    questionImage = editedQuestionImage,
                                    optionsBn = newOpts,
                                    optionsEn = newOpts,
                                    correctIndex = editedCorrectIdx,
                                    explanationBn = editedExplanation.trim(),
                                    explanationEn = editedExplanation.trim()
                                )
                                onSaveEditedQuestion(updated)
                                isEditing = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess, contentColor = Color.White)
                        ) {
                            Text("Save Changes")
                        }
                    }
                }
            } else {
                // Full Question Text
                Text(
                    text = question.displayQuestionText(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Render Reasoning Question Figure if present
                if (question.hasQuestionImage()) {
                    QuestionDiagramCard(base64Image = question.questionImage)
                }

                // 4 Options (A, B, C, D) with Green Highlight on Correct Answer + Reasoning Option Images
                val opts = question.displayOptions()
                val optLabels = listOf("A", "B", "C", "D")
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    opts.take(4).forEachIndexed { optIdx, optText ->
                        val isCorrect = optIdx == question.correctIndex
                        val optImg = question.optionImageAt(optIdx)
                        Surface(
                            color = if (isCorrect) EmeraldSuccess.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        color = if (isCorrect) EmeraldSuccess else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "(${optLabels.getOrElse(optIdx) { "A" }}) $optText",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCorrect) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (!optImg.isNullOrBlank()) {
                                        OptionDiagramThumbnail(base64Image = optImg)
                                    }
                                }
                                if (isCorrect) {
                                    Text(
                                        text = "✓ Correct",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess
                                    )
                                }
                            }
                        }
                    }
                }

                // Bengali Shortcut Explanation Box
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "💡 ${question.displayExplanationBn()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Action Row: "✏️ Edit" and "🗑️ Delete"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { isEditing = true },
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("edit_scanned_question_${question.id}"),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Question",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "✏️ Edit",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onDeleteQuestion,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("delete_scanned_question_${question.id}"),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Question",
                            tint = CrimsonWrong,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "🗑️ Delete",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = CrimsonWrong
                        )
                    }
                }
            }
        }
    }
}

/**
 * Feature 5 & 6 Component: "🗂️ Manage Question Database" Explorer, Filter, Edit/Delete & 1-Click JSON Backup Manager
 * - View total questions count and breakdown by Subject, Chapter, and PYQ status.
 * - Filter questions by Subject, Chapter, PYQ Only vs All, or search by keyword.
 * - 1-Click Database Backup: "📤 Export Database Backup (.json)" & "📥 Import Database Backup (.json)".
 * - Edit any existing question or Delete with 1 tap.
 */
@Composable
fun MyUploadedQuestionsVaultSection(
    isBn: Boolean,
    uploadedQuestions: List<QuizQuestion>,
    onStartUploadedTest: (List<QuizQuestion>) -> Unit,
    onOpenScannerModal: () -> Unit,
    onUpdateUploadedQuestion: (QuizQuestion) -> Unit,
    onDeleteUploadedQuestion: (String) -> Unit,
    onExportDatabaseJson: ((String) -> Unit) -> Unit = {},
    onImportDatabaseJson: (String) -> Unit = {},
    initiallyExpandedList: Boolean = true
) {
    val context = LocalContext.current
    var selectedSubjectFilter by remember { mutableStateOf("ALL") } // "ALL", "GK", "MATH", "REASONING", "ENGLISH"
    var selectedChapterFilter by remember { mutableStateOf("ALL") }
    var pyqFilterMode by remember { mutableStateOf("ALL") } // "ALL", "PYQ_ONLY", "CUSTOM_ONLY"
    var searchQuery by remember { mutableStateOf("") }
    var pendingExportJsonContent by remember { mutableStateOf<String?>(null) }
    var showQuestionCards by remember(initiallyExpandedList) { mutableStateOf(initiallyExpandedList) }
    var visibleLimit by remember(selectedSubjectFilter, selectedChapterFilter, pyqFilterMode, searchQuery) {
        mutableStateOf(10)
    }

    // CreateDocument launcher for saving the .json backup file to device Downloads/storage
    val exportJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        val contentToWrite = pendingExportJsonContent
        if (uri != null && !contentToWrite.isNullOrBlank()) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(contentToWrite.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "✅ Database Backup (.json) saved to device!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "⚠️ Could not save file: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // GetContent launcher for importing a .json backup file
    val importJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val rawJson = context.contentResolver.openInputStream(uri)?.use {
                    String(it.readBytes(), Charsets.UTF_8)
                }
                if (!rawJson.isNullOrBlank()) {
                    onImportDatabaseJson(rawJson)
                } else {
                    Toast.makeText(context, "⚠️ Selected JSON file is empty.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "⚠️ Failed to read JSON file: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val availableChaptersForFilter = remember(selectedSubjectFilter) {
        if (selectedSubjectFilter == "ALL") emptyList()
        else com.example.data.StructuredQuestionSchema.chaptersForSubject(selectedSubjectFilter)
    }

    val filteredQuestions = remember(
        uploadedQuestions,
        selectedSubjectFilter,
        selectedChapterFilter,
        pyqFilterMode,
        searchQuery
    ) {
        uploadedQuestions.filter { q ->
            val subOk = selectedSubjectFilter == "ALL" || q.subject.equals(selectedSubjectFilter, ignoreCase = true)
            val chapOk = selectedChapterFilter == "ALL" ||
                q.effectiveChapterName().equals(selectedChapterFilter, ignoreCase = true)
            val pyqOk = when (pyqFilterMode) {
                "PYQ_ONLY" -> q.effectiveIsPyq()
                "CUSTOM_ONLY" -> q.id.startsWith("MANUAL_") || q.id.startsWith("SCAN_") || q.id.startsWith("IMPORT_")
                else -> true
            }
            val queryTrim = searchQuery.trim()
            val searchOk = queryTrim.isEmpty() ||
                q.displayQuestionText().contains(queryTrim, ignoreCase = true) ||
                q.effectiveChapterName().contains(queryTrim, ignoreCase = true) ||
                q.effectivePyqBadge().contains(queryTrim, ignoreCase = true) ||
                q.displayOptions().any { it.contains(queryTrim, ignoreCase = true) }
            subOk && chapOk && pyqOk && searchOk
        }
    }

    val pyqTotalCount = remember(uploadedQuestions) { uploadedQuestions.count { it.effectiveIsPyq() } }
    val reasoningImgCount = remember(uploadedQuestions) {
        uploadedQuestions.count { it.hasQuestionImage() || it.optionImages.any { img -> img.isNotBlank() } }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("my_uploaded_questions_vault_section"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header + "+ Add 1–50 Questions" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🗂️ MANAGE QUESTION DATABASE (${uploadedQuestions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Total: ${uploadedQuestions.size} • PYQ Tagged: $pyqTotalCount • With Reasoning Figures: $reasoningImgCount",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldSuccess
                    )
                }

                Button(
                    onClick = onOpenScannerModal,
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✍️ + Add 1–50 Qs",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Feature 6: 1-Click Database Backup (Export & Import .json) Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onExportDatabaseJson { jsonStr ->
                            pendingExportJsonContent = jsonStr
                            exportJsonLauncher.launch("wbp_kp_question_database_backup.json")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("export_database_backup_button"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "📤 Export Database Backup (.json)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = { importJsonLauncher.launch("*/*") },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("import_database_backup_button"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "📥 Import Database Backup (.json)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Keyword Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("database_search_input"),
                singleLine = true,
                label = { Text("🔍 Search by Question Text, Chapter, Option, or PYQ Year...") }
            )

            // Subject Filter Chips
            val subFilters = listOf(
                "ALL" to "All (${uploadedQuestions.size})",
                "GK" to "GK (${uploadedQuestions.count { it.subject.equals("GK", true) }})",
                "MATH" to "Math (${uploadedQuestions.count { it.subject.equals("MATH", true) }})",
                "REASONING" to "Reasoning (${uploadedQuestions.count { it.subject.equals("REASONING", true) }})",
                "ENGLISH" to "English (${uploadedQuestions.count { it.subject.equals("ENGLISH", true) }})"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(subFilters) { (code, label) ->
                    val isSel = selectedSubjectFilter == code
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                selectedSubjectFilter = code
                                selectedChapterFilter = "ALL"
                            },
                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Dynamic Chapter Filter Chips when a Subject is selected
            if (availableChaptersForFilter.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        val isAllChap = selectedChapterFilter == "ALL"
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { selectedChapterFilter = "ALL" },
                            color = if (isAllChap) EmeraldSuccess else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "All Chapters",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isAllChap) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                    items(availableChaptersForFilter) { chap ->
                        val isSel = selectedChapterFilter == chap
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { selectedChapterFilter = chap },
                            color = if (isSel) EmeraldSuccess else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = chap,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // PYQ Only vs All Filter Pills
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "ALL" to "All Questions",
                    "PYQ_ONLY" to "🏛️ PYQ Only ($pyqTotalCount)",
                    "CUSTOM_ONLY" to "✍️ Manually Added Only"
                ).forEach { (mode, label) ->
                    val isSel = pyqFilterMode == mode
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { pyqFilterMode = mode },
                        color = if (isSel) AmberWarning else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            if (filteredQuestions.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No questions match the current filter. Tap '✍️ + Add 1–50 Qs' to add new questions in the Manual Question Studio!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onOpenScannerModal,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("✍️ Open Manual Question Studio")
                        }
                    }
                }
            } else {
                // Practice Filtered Questions CTA
                Button(
                    onClick = { onStartUploadedTest(filteredQuestions.take(50)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("start_my_uploaded_test_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldSuccess,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Practice Filtered Questions Now (${filteredQuestions.size} Matching)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Toggleable & Paginated Question Explorer List
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showQuestionCards = !showQuestionCards }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showQuestionCards)
                            "Hide Question Cards ▲ (${filteredQuestions.size} Matching)"
                        else
                            "Show Question Cards ▼ (${filteredQuestions.size} Matching — Edit / Delete)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = if (showQuestionCards) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                AnimatedVisibility(visible = showQuestionCards) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        filteredQuestions.take(visibleLimit).forEachIndexed { idx, q ->
                            UploadedQuestionPreviewCard(
                                index = idx + 1,
                                question = q,
                                isDuplicateIgnored = false,
                                onSaveEditedQuestion = onUpdateUploadedQuestion,
                                onDeleteQuestion = { onDeleteUploadedQuestion(q.id) }
                            )
                        }

                        if (filteredQuestions.size > visibleLimit) {
                            OutlinedButton(
                                onClick = { visibleLimit += 20 },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .testTag("show_more_database_questions_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Show More Questions (${filteredQuestions.size - visibleLimit} remaining)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Part 3A: [PYQ Vault (2005–Present)] View
 * Supports filtering across all official WBP & KP recruitment exams (2005–Present)
 * and displays the authentic extracted PYQs with official Exam Year badges and Bengali bullet-point explanations.
 */
@Composable
fun PyqVaultArchiveSection(
    isBn: Boolean,
    questionVault: List<QuizQuestion>,
    seenQuestionIds: Set<String>,
    bulkGeneratingTarget: String?,
    onStartExamGroupTest: (WbpExamHistoryGroup, String?) -> Unit,
    onBulkAdd50Questions: (String, String?, String?) -> Unit
) {
    val examGroups = WbpPyqVaultSeed.examHistoryGroups
    var selectedExamGroup by remember { mutableStateOf(examGroups.first()) }
    var selectedSubjectFilter by remember { mutableStateOf<String?>(null) }
    var showPreviewQuestions by remember { mutableStateOf(true) }

    val matchingQuestions = remember(questionVault, selectedExamGroup, selectedSubjectFilter) {
        questionVault.filter { q ->
            val examMatch = selectedExamGroup.id == "ALL_EXAMS" ||
                q.examFamily.equals(selectedExamGroup.id, ignoreCase = true) ||
                q.examYearTag.isNotBlank()
            val strictExamMatch = if (selectedExamGroup.id == "ALL_EXAMS") {
                q.examYearTag.isNotBlank()
            } else {
                q.examFamily.equals(selectedExamGroup.id, ignoreCase = true)
            }
            val subMatch = selectedSubjectFilter == null || q.subject.equals(selectedSubjectFilter, ignoreCase = true)
            strictExamMatch && subMatch && examMatch
        }
    }

    val unseenCount = remember(matchingQuestions, seenQuestionIds) {
        matchingQuestions.count { it.id !in seenQuestionIds }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isBn)
                "বিগত বছরের প্রশ্নপত্র ভল্ট (WBPRB OFFICIAL PYQ VAULT 2005–PRESENT)"
            else
                "WBPRB OFFICIAL PYQ VAULT (2005–PRESENT) • WBP & KP SMART QUESTION BANK",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp
        )

        // Exam Family Filter Pills (2005–Present)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(examGroups, key = { it.id }) { group ->
                val isSelected = selectedExamGroup.id == group.id
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { selectedExamGroup = group }
                        .testTag("pyq_exam_filter_${group.id}"),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = if (isBn) group.titleBn else group.titleEn,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // Subject Sub-Filter Pills: [All Subjects], [GK], [MATH], [REASONING], [ENGLISH]
        val subjectOptions = listOf(
            null to "All Subjects",
            "GK" to "GK (2005–2024)",
            "MATH" to "Math (Arithmetic)",
            "REASONING" to "Reasoning (GI)",
            "ENGLISH" to "English"
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(subjectOptions) { (subCode, label) ->
                val isSel = selectedSubjectFilter == subCode
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { selectedSubjectFilter = subCode },
                    color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .border(
                                1.dp,
                                if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(50)
                            )
                            .padding(horizontal = 11.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Selected Exam Archive Action Card
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = selectedExamGroup.yearsCovered,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = if (isBn) "বাংলা মাধ্যম • অফিশিয়াল PYQ" else "Official PYQ Archive",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = if (isBn) selectedExamGroup.titleBn else selectedExamGroup.titleEn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                VaultCounterBadge(
                    savedCount = matchingQuestions.size,
                    unseenCount = unseenCount,
                    targetCapacity = if (selectedSubjectFilter == null) 4000 else 1000
                )

                Button(
                    onClick = { onStartExamGroupTest(selectedExamGroup, selectedSubjectFilter) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("start_pyq_archive_test_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn)
                            "Start Non-Repeating PYQ Test (পরীক্ষা শুরু করুন)"
                        else
                            "Start Non-Repeating PYQ Test",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                val targetSub = selectedSubjectFilter ?: "GK"
                BulkAdd50VaultButton(
                    isBn = isBn,
                    isLoading = bulkGeneratingTarget == targetSub,
                    testTag = "bulk_add_50_pyq_vault_$targetSub",
                    onClick = {
                        onBulkAdd50Questions(targetSub, null, selectedExamGroup.titleEn)
                    }
                )
            }
        }

        // Authentic Extracted PYQs Preview List with Exam Year Badges & Bengali Explanations
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPreviewQuestions = !showPreviewQuestions }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isBn)
                    "অফিশিয়াল PYQ প্রশ্ন ও বাংলা ব্যাখ্যা (${matchingQuestions.size}টি প্রশ্ন)"
                else
                    "AUTHENTIC EXTRACTED PYQS WITH YEAR BADGES (${matchingQuestions.size})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                imageVector = if (showPreviewQuestions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(visible = showPreviewQuestions) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                matchingQuestions.take(25).forEachIndexed { idx, q ->
                    PyqVaultQuestionPreviewCard(
                        index = idx + 1,
                        question = q,
                        isSeen = q.id in seenQuestionIds,
                        isBn = isBn
                    )
                }
            }
        }
    }
}

@Composable
private fun PyqVaultQuestionPreviewCard(
    index: Int,
    question: QuizQuestion,
    isSeen: Boolean,
    isBn: Boolean
) {
    var showExplanation by remember { mutableStateOf(false) }
    val options = question.displayOptions()
    val optionLetters = listOf("a", "b", "c", "d")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                            text = "#$index • ${question.subject}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }

                    if (question.examYearTag.isNotBlank()) {
                        Surface(
                            color = AmberWarning.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HistoryEdu,
                                    contentDescription = null,
                                    tint = AmberWarning,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "[${question.examYearTag}]",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberWarning
                                )
                            }
                        }
                    }
                }

                Text(
                    text = if (isSeen) (if (isBn) "দেখা হয়েছে" else "Seen") else (if (isBn) "নতুন (Unseen)" else "Unseen"),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSeen) MaterialTheme.colorScheme.onSurfaceVariant else EmeraldSuccess
                )
            }

            Text(
                text = question.displayQuestionText(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Compact 2x2 or vertical options preview
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                options.forEachIndexed { optIdx, optText ->
                    val isCorrect = showExplanation && optIdx == question.correctIndex
                    Text(
                        text = "(${optionLetters.getOrElse(optIdx) { "" }}) $optText",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCorrect) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showExplanation) {
                        if (isBn) "সমাধান লুকান ▲" else "Hide Solution ▲"
                    } else {
                        if (isBn) "সঠিক উত্তর ও ব্যাখ্যা দেখুন ▼" else "Show Answer & Bengali Solution ▼"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { showExplanation = !showExplanation }
                )
            }

            AnimatedVisibility(visible = showExplanation) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "✓ সঠিক উত্তর: (${optionLetters.getOrElse(question.correctIndex) { "a" }}) ${options.getOrElse(question.correctIndex) { "" }}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        )
                        Text(
                            text = question.displayExplanationBn(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val trick = question.displayShortcutBn()
                        if (trick.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = AmberWarning,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = trick,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AmberWarning
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Part 3B: [Chapter-Wise Practice] View
 * Organized by the Official 53 WBPRB Chapter Structure across the 4 Subjects:
 * - GK (7 Chapters)
 * - Elementary Mathematics (18 Chapters)
 * - Logical & Analytical Reasoning (20 Chapters)
 * - English (8 Topics)
 */
@Composable
fun WbpChapterWiseSection(
    isBn: Boolean,
    questionVault: List<QuizQuestion>,
    seenQuestionIds: Set<String>,
    bulkGeneratingTarget: String?,
    onStartChapterTest: (WbpChapterSpec) -> Unit,
    onBulkAdd50ForChapter: (WbpChapterSpec) -> Unit
) {
    var selectedSubject by remember { mutableStateOf("GK") }

    val subjectTabs = listOf(
        Triple("GK", "GK (8 Chapters)", "GK (৮টি অধ্যায়)"),
        Triple("MATH", "Math (18 Chapters)", "গণিত (১৮টি অধ্যায়)"),
        Triple("REASONING", "Reasoning (20 Chapters)", "রিজনিং (২০টি অধ্যায়)"),
        Triple("ENGLISH", "English (9 Topics)", "ইংরেজি (৯টি অধ্যায়)")
    )

    val chaptersForSubject = remember(selectedSubject) {
        WbpPyqVaultSeed.wbpChapters.filter { it.subject == selectedSubject }
    }

    val subjectVaultQuestions = remember(questionVault, selectedSubject) {
        questionVault.filter { it.subject.equals(selectedSubject, ignoreCase = true) }
    }
    val subjectUnseenCount = remember(subjectVaultQuestions, seenQuestionIds) {
        subjectVaultQuestions.count { it.id !in seenQuestionIds }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isBn)
                "অধ্যায়ভিত্তিক প্র্যাকটিস • WBP & KP SMART QUESTION BANK (৫৫টি অধ্যায়)"
            else
                "WBP & KP SMART QUESTION BANK • CHAPTER-WISE PRACTICE (55 CHAPTERS)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp
        )

        // Subject Pill Bar
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(subjectTabs, key = { it.first }) { (subCode, labelEn, labelBn) ->
                val isSelected = selectedSubject == subCode
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { selectedSubject = subCode }
                        .testTag("chapter_subject_tab_$subCode"),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = if (isBn) labelBn else labelEn,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Subject Vault Counter Summary
        VaultCounterBadge(
            savedCount = subjectVaultQuestions.size,
            unseenCount = subjectUnseenCount,
            targetCapacity = 1000,
            modifier = Modifier.fillMaxWidth()
        )

        // Chapter Cards List
        chaptersForSubject.forEach { chapter ->
            val chapterQuestions = remember(subjectVaultQuestions, chapter.id, chapter.titleEn) {
                subjectVaultQuestions.filter {
                    it.chapterId.equals(chapter.id, ignoreCase = true) ||
                        it.effectiveChapterName().equals(chapter.titleEn, ignoreCase = true)
                }
            }
            val chapterSaved = maxOf(chapterQuestions.size, 1)
            val chapterUnseen = chapterQuestions.count { it.id !in seenQuestionIds }.coerceAtLeast(1)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chapter_card_${chapter.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${chapter.subject} • ${chapter.numberCode}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = chapter.titleEn,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = chapter.subtitleEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Live Vault Counter Badge on every Chapter Card
                    VaultCounterBadge(
                        savedCount = chapterSaved,
                        unseenCount = chapterUnseen,
                        targetCapacity = 100
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onStartChapterTest(chapter) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("start_chapter_test_${chapter.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn)
                                    "অধ্যায়ভিত্তিক টেস্ট শুরু করুন (${chapterSaved}+ Q)"
                                else
                                    "Start Chapter Practice (${chapterSaved}+ Q)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    BulkAdd50VaultButton(
                        isBn = isBn,
                        isLoading = bulkGeneratingTarget == chapter.id,
                        testTag = "bulk_add_50_chapter_${chapter.id}",
                        onClick = { onBulkAdd50ForChapter(chapter) }
                    )
                }
            }
        }
    }
}
