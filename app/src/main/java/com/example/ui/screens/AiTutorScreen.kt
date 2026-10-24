package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.ChatMessageEntity
import com.example.ui.theme.CrimsonWrong
import com.example.ui.theme.EmeraldSuccess

@Composable
fun AiTutorScreen(
    language: AppLanguage,
    messages: List<ChatMessageEntity>,
    isLoading: Boolean,
    onSendPrompt: (String) -> Unit,
    onSendPhotoPrompt: (Bitmap, String) -> Unit = { _, _ -> },
    onClearChat: () -> Unit,
    onOpenMockTestTab: () -> Unit = {},
    selectedDistrict: com.example.data.WbDistrict = com.example.data.WbDistrict.MALDA,
    interviewTurns: List<com.example.data.InterviewChatTurn> = emptyList(),
    latestInterviewScore: Double? = null,
    isInterviewLoading: Boolean = false,
    onSelectDistrict: (com.example.data.WbDistrict) -> Unit = {},
    onSubmitInterviewAnswer: (String) -> Unit = {},
    onResetInterview: () -> Unit = {},
    initialInterviewMode: Boolean = false
) {
    val context = LocalContext.current
    val isBn = language == AppLanguage.BENGALI
    var isInterviewMode by remember { mutableStateOf(initialInterviewMode) }
    var inputText by remember { mutableStateOf("") }
    var attachedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(initialInterviewMode) {
        isInterviewMode = initialInterviewMode
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                attachedBitmap = bmp
            } catch (_: Exception) {
                attachedBitmap = createSampleTextbookQuestionBitmap()
            }
        }
    }

    val quickChips = remember(isBn) {
        if (isBn) {
            listOf(
                "Generate 5 GK questions",
                "নৌকা ও স্রোত (শর্টকাট ট্রিক)",
                "সময় ও কার্য (৩০ সেকেন্ডে অঙ্ক)",
                "পশ্চিমবঙ্গের প্রকল্প ও লোকসংস্কৃতি",
                "Banglish: nouka o srot er onko shortcut bolo"
            )
        } else {
            listOf(
                "Generate 5 GK questions",
                "Boat & Stream (30s Shortcut)",
                "Time & Work Exam Hall Trick",
                "WB Govt Schemes & Folk Culture",
                "Banglish: nouka o srot er onko shortcut bolo"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // iOS-Style Segmented Control: [Doubt Solver (Chat + Photo)] vs [15-Mark Board Interview]
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .clickable { isInterviewMode = false }
                        .testTag("doubt_solver_mode_tab"),
                    color = if (!isInterviewMode) MaterialTheme.colorScheme.surface else Color.Transparent,
                    shape = RoundedCornerShape(9.dp),
                    shadowElevation = if (!isInterviewMode) 1.dp else 0.dp
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBn) "ডাউট সলভার ও ফটো" else "Doubt Solver & Photo",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (!isInterviewMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .clickable { isInterviewMode = true }
                        .testTag("interview_mode_tab"),
                    color = if (isInterviewMode) MaterialTheme.colorScheme.surface else Color.Transparent,
                    shape = RoundedCornerShape(9.dp),
                    shadowElevation = if (isInterviewMode) 1.dp else 0.dp
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBn) "মক ইন্টারভিউ (১৫ নম্বর)" else "15-Mark Interview",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isInterviewMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isInterviewMode) {
            MockInterviewSection(
                language = language,
                selectedDistrict = selectedDistrict,
                interviewTurns = interviewTurns,
                latestScore = latestInterviewScore,
                isLoading = isInterviewLoading,
                onSelectDistrict = onSelectDistrict,
                onSubmitAnswer = onSubmitInterviewAnswer,
                onResetInterview = onResetInterview
            )
            return@Column
        }

        // Minimal Header Action Card with Photo Upload & Demo Photo Buttons
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
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isBn)
                                    "WBPRB AI ডাউট সলভার (বিস্তারিত + শর্টকাট)"
                                else
                                    "WBPRB AI Doubt Solver (Concept + 30s Trick)",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(
                        onClick = onClearChat,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("upload_snap_photo_banner_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Upload / Snap Question Photo",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            attachedBitmap = createSampleTextbookQuestionBitmap()
                        },
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("sample_photo_doubt_button"),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBn) "ডেমো ফটো" else "Demo Photo",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Shortcut Prompt Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(quickChips) { chip ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onSendPrompt(chip) },
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = chip,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Minimal Messaging Chat List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.isUser
                val isJsonBlock = !isUser && msg.text.trimStart().startsWith("[")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(if (isUser) 0.86f else 0.95f),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isUser) 0.dp else 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .border(
                                    width = if (isUser) 0.dp else 1.dp,
                                    color = if (isUser) Color.Transparent else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    )
                                )
                                .padding(14.dp)
                        ) {
                            if (msg.tag.isNotBlank()) {
                                Surface(
                                    color = if (isUser) Color.White.copy(alpha = 0.18f) else MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = msg.tag,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            Text(
                                text = msg.text,
                                style = if (isJsonBlock) {
                                    MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp
                                    )
                                } else {
                                    MaterialTheme.typography.bodyMedium
                                },
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                            )

                            if (isJsonBlock) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = onOpenMockTestTab,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldSuccess,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Assignment,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isBn) "Live OMR মক টেস্টে উত্তর দিন" else "Practice in Live OMR",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn)
                                "বিস্তারিত পদ্ধতি ও শর্টকাট ট্রিক তৈরি হচ্ছে..."
                            else
                                "Solving with Traditional + 30s Shortcut...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Attached Question Photo Preview Card
        AnimatedVisibility(visible = attachedBitmap != null) {
            attachedBitmap?.let { bmp ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("attached_photo_preview"),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Attached Question Photo",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBn) "প্রশ্নের ছবি যুক্ত হয়েছে" else "Question Photo Ready",
                                style = MaterialTheme.typography.labelLarge,
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isBn) "Send বাটনে চাপুন সমাধান পেতে" else "Tap Send for 30s shortcut",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = {
                                onSendPhotoPrompt(bmp, inputText)
                                attachedBitmap = null
                                inputText = ""
                            },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldSuccess,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isBn) "সমাধান" else "Solve",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        IconButton(onClick = { attachedBitmap = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Photo",
                                tint = CrimsonWrong,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Floating Bottom Input Pill Bar with Camera/Attachment Icon inside the Input Pill
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Camera / Photo Attachment Icon inside the Input Pill
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !isLoading) {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("upload_photo_button"),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Upload / Snap Question Photo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (inputText.isEmpty()) {
                        Text(
                            text = if (isBn)
                                "প্রশ্ন লিখুন বা ছবি আপলোড করুন..."
                            else
                                "Ask a doubt or upload a photo...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_tutor_input"),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable(enabled = (inputText.isNotBlank() || attachedBitmap != null) && !isLoading) {
                            val bmp = attachedBitmap
                            if (bmp != null) {
                                onSendPhotoPrompt(bmp, inputText)
                                attachedBitmap = null
                                inputText = ""
                            } else {
                                onSendPrompt(inputText)
                                inputText = ""
                            }
                        }
                        .testTag("ai_tutor_send_button"),
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun createSampleTextbookQuestionBitmap(): Bitmap {
    val width = 640
    val height = 320
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(AndroidColor.parseColor("#F8FAFC"))

    val borderPaint = Paint().apply {
        color = AndroidColor.parseColor("#4F46E5")
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }
    canvas.drawRect(12f, 12f, width - 12f, height - 12f, borderPaint)

    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#4F46E5")
        textSize = 26f
        isFakeBoldText = true
    }
    canvas.drawText("WBPRB Constable Mathematics (Class 10 Standard)", 32f, 58f, titlePaint)

    val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#0F172A")
        textSize = 24f
    }
    canvas.drawText("Q. The ratio of two numbers is 3 : 4 and their", 32f, 115f, bodyPaint)
    canvas.drawText("LCM is 180. Find the larger number.", 32f, 152f, bodyPaint)
    canvas.drawText("(দুটি সংখ্যার অনুপাত ৩:৪ এবং ল.সা.গু ১৮০ হলে বড় সংখ্যাটি কত?)", 32f, 195f, bodyPaint)
    canvas.drawText("(A) 45    (B) 60    (C) 75    (D) 90", 32f, 250f, titlePaint)

    return bitmap
}
