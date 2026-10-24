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
import androidx.compose.material.icons.filled.MenuBook
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
import com.example.data.QuizQuestion
import com.example.data.SopanChapterSpec
import com.example.data.SopanPyqVaultSeed
import com.example.data.WbpExamHistoryGroup
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess

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
 * Non-Repeating Engine status, and the "📥 Import PYQ PDF / Book Text" button.
 */
@Composable
fun QuestionVaultMasterBar(
    isBn: Boolean,
    questionVault: List<QuizQuestion>,
    seenQuestionIds: Set<String>,
    isImportingBook: Boolean,
    onOpenImporterModal: () -> Unit,
    onResetSeenHistory: () -> Unit
) {
    val totalVault = questionVault.size
    val totalUnseen = questionVault.count { it.id !in seenQuestionIds }

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
                        text = if (isBn)
                            "SOPAN ACADEMY WBP WARRIOR • নন-রিপিটিং ভল্ট (২০০৫–বর্তমান)"
                        else
                            "SOPAN ACADEMY PYQ VAULT (2005–PRESENT • ZERO REPETITION)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "[Saved in Vault: $totalVault / 4000 Questions • Unseen: $totalUnseen]",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
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
                                text = if (isBn) "রিসেট (${seenQuestionIds.size})" else "Reset Seen (${seenQuestionIds.size})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // "📥 Import PYQ PDF / Book Text" Button
            OutlinedButton(
                onClick = onOpenImporterModal,
                enabled = !isImportingBook,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("open_pyq_book_importer_button"),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                if (isImportingBook) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBn)
                            "📥 Gemini 2.5 Flash বুক থেকে PYQ এক্সট্র্যাক্ট করছে..."
                        else
                            "📥 Extracting PYQs with Gemini 2.5 Flash...",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📥 Import PYQ PDF / Book Text (SOPAN 4 Books)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Part 2: "PYQ Book PDF / Text Importer" Modal (For the 4 SOPAN Academy WBP Warrior PYQ Books)
 */
@Composable
fun PyqBookImporterDialog(
    isBn: Boolean,
    isImporting: Boolean,
    onDismiss: () -> Unit,
    onImportBookContent: (rawText: String, base64Doc: String?, mimeType: String?, subjectHint: String) -> Unit
) {
    val context = LocalContext.current
    var selectedBookSubject by remember { mutableStateOf("GK") }
    var rawTextInput by remember { mutableStateOf("") }
    var uploadedFileName by remember { mutableStateOf<String?>(null) }
    var uploadedBase64 by remember { mutableStateOf<String?>(null) }
    var uploadedMimeType by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val resolver = context.contentResolver
                val mime = resolver.getType(uri) ?: "application/pdf"
                val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) {
                    uploadedFileName = uri.lastPathSegment ?: "sopan_pyq_book"
                    if (mime.startsWith("text/") || uploadedFileName?.endsWith(".txt", ignoreCase = true) == true) {
                        val decodedText = String(bytes, Charsets.UTF_8)
                        rawTextInput = decodedText.take(12000)
                        uploadedBase64 = null
                        uploadedMimeType = null
                    } else {
                        uploadedBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                        uploadedMimeType = mime
                        if (rawTextInput.isBlank()) {
                            rawTextInput = "Uploaded SOPAN Academy PYQ PDF ($uploadedFileName) for $selectedBookSubject."
                        }
                    }
                }
            } catch (_: Exception) {
                uploadedFileName = "Selected File"
            }
        }
    }

    val books = listOf(
        "GK" to "GK Warrior (2005–Present)",
        "MATH" to "Math Warrior (18 Chapters)",
        "REASONING" to "Reasoning Warrior (20 Chapters)",
        "ENGLISH" to "English Warrior (8 Topics)"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .testTag("pyq_book_importer_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📥 Import PYQ PDF / Book Text",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isBn)
                                "SOPAN Academy-র ৪টি WBP Warrior PYQ বুক থেকে প্রশ্ন, সাল ও ব্যাখ্যা ভল্টে সেভ করুন"
                            else
                                "Extract questions, Exam/Year tags & Bengali explanations via Gemini 2.5 Flash",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Modal"
                        )
                    }
                }

                // Book / Subject Selector
                Text(
                    text = if (isBn) "১. বই নির্বাচন করুন (SOPAN 4 Books):" else "1. Select SOPAN PYQ Book:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(books) { (subCode, label) ->
                        val isSel = selectedBookSubject == subCode
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { selectedBookSubject = subCode },
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                // File Picker Row (PDF / TXT) + Sample Paste Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("upload_pyq_pdf_txt_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "PDF / TXT ফাইল" else "Upload PDF / TXT",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            rawTextInput = """
                                [KP Constable Prelims 2023] ‘কাদম্বরী’ গ্রন্থের রচয়িতা কে?
                                (a) কালিদাস (b) ভবভূতি (c) বাণভট্ট (d) বিশাখদত্ত
                                সঠিক উত্তর: (c) বাণভট্ট
                                ব্যাখ্যা: বাণভট্ট ছিলেন হর্ষবর্ধনের সভাকবি; তাঁর রচিত অপর বিখ্যাত গ্রন্থ ‘হর্ষচরিত’।

                                [WBP Constable Prelims 2021] একটি আয়তক্ষেত্রের দৈর্ঘ্য ১০% বৃদ্ধি ও প্রস্থ ১০% হ্রাস পেলে ক্ষেত্রফলের শতকরা পরিবর্তন কত?
                                (a) ১% বৃদ্ধি (b) ১% হ্রাস (c) কোনো পরিবর্তন হবে না (d) ২% হ্রাস
                                সঠিক উত্তর: (b) ১% হ্রাস
                                ব্যাখ্যা: শর্টকাট সূত্র x² / 100 % হ্রাস = (10 × 10) / 100 = 1% হ্রাস।
                            """.trimIndent()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("paste_sample_sopan_text_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBn) "নমুনা PYQ টেক্সট" else "Sample PYQ Text",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (uploadedFileName != null) {
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📄 Attached: $uploadedFileName",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldSuccess,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Raw Text Input Box
                OutlinedTextField(
                    value = rawTextInput,
                    onValueChange = { rawTextInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .testTag("pyq_book_raw_text_input"),
                    label = {
                        Text(
                            if (isBn)
                                "SOPAN PYQ বইয়ের টেক্সট এখানে পেস্ট করুন..."
                            else
                                "Paste raw text from SOPAN Academy WBP Warrior PYQ book..."
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                // Submit / Extract Button
                Button(
                    onClick = {
                        onImportBookContent(
                            rawTextInput,
                            uploadedBase64,
                            uploadedMimeType,
                            selectedBookSubject
                        )
                        onDismiss()
                    },
                    enabled = (rawTextInput.isNotBlank() || uploadedBase64 != null) && !isImporting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("confirm_import_pyq_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBn)
                            "Gemini 2.5 Flash দিয়ে ভল্টে সেভ করুন"
                        else
                            "Extract & Save to Question Vault (Gemini 2.5 Flash)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
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
    val examGroups = SopanPyqVaultSeed.examHistoryGroups
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
                "বিগত বছরের প্রশ্নপত্র ভল্ট (২০০৫–বর্তমান • SOPAN PYQ ARCHIVE)"
            else
                "OFFICIAL WBP & KP PYQ VAULT (2005–PRESENT • SOPAN WARRIOR BOOKS)",
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
            null to (if (isBn) "সব বিষয় (All)" else "All Subjects"),
            "GK" to "GK (২০০৫–২৪)",
            "MATH" to "Math (পাটিগণিত)",
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
 * Organized by the exact SOPAN Academy Chapter Structure across the 4 WBP Warrior PYQ Books:
 * - GK (7 Chapters)
 * - Elementary Mathematics (18 Chapters)
 * - Logical & Analytical Reasoning (20 Chapters)
 * - English (8 Topics)
 */
@Composable
fun SopanChapterWiseSection(
    isBn: Boolean,
    questionVault: List<QuizQuestion>,
    seenQuestionIds: Set<String>,
    bulkGeneratingTarget: String?,
    onStartChapterTest: (SopanChapterSpec) -> Unit,
    onBulkAdd50ForChapter: (SopanChapterSpec) -> Unit
) {
    var selectedSubject by remember { mutableStateOf("GK") }

    val subjectTabs = listOf(
        Triple("GK", "GK (7 Chapters)", "GK (৭টি অধ্যায়)"),
        Triple("MATH", "Math (18 Chapters)", "গণিত (১৮টি অধ্যায়)"),
        Triple("REASONING", "Reasoning (20 Chapters)", "রিজনিং (২০টি অধ্যায়)"),
        Triple("ENGLISH", "English (8 Topics)", "ইংরেজি (৮টি অধ্যায়)")
    )

    val chaptersForSubject = remember(selectedSubject) {
        SopanPyqVaultSeed.sopanChapters.filter { it.subject == selectedSubject }
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
                "SOPAN ACADEMY অধ্যায়ভিত্তিক প্র্যাকটিস (CHAPTER-WISE PRACTICE)"
            else
                "SOPAN ACADEMY CHAPTER-WISE PRACTICE (53 CHAPTERS ACROSS 4 BOOKS)",
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
            val chapterQuestions = remember(subjectVaultQuestions, chapter.id) {
                subjectVaultQuestions.filter { it.chapterId.equals(chapter.id, ignoreCase = true) }
            }
            val chapterSaved = maxOf(chapterQuestions.size, 1)
            val chapterUnseen = chapterQuestions.count { it.id !in seenQuestionIds }

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
                                text = if (isBn) "${chapter.titleBn} (${chapter.titleEn})" else "${chapter.titleEn} (${chapter.titleBn})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = chapter.subtitleBn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Live Vault Counter Badge on every Chapter Card
                    VaultCounterBadge(
                        savedCount = subjectVaultQuestions.size,
                        unseenCount = subjectUnseenCount.coerceAtLeast(chapterUnseen),
                        targetCapacity = 1000
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
                                imageVector = Icons.Default.MenuBook,
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
