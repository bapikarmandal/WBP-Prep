package com.example.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.AppLanguage
import com.example.data.InterviewChatTurn
import com.example.data.WbDistrict
import com.example.ui.theme.EmeraldSuccess
import java.util.Locale

@Composable
fun MockInterviewSection(
    language: AppLanguage,
    selectedDistrict: WbDistrict,
    interviewTurns: List<InterviewChatTurn>,
    latestScore: Double?,
    isLoading: Boolean,
    onSelectDistrict: (WbDistrict) -> Unit,
    onSubmitAnswer: (String) -> Unit,
    onResetInterview: () -> Unit
) {
    val context = LocalContext.current
    val isBn = language == AppLanguage.BENGALI
    var districtMenuExpanded by remember { mutableStateOf(false) }
    var candidateInput by remember { mutableStateOf("") }
    var voiceStatusHint by remember { mutableStateOf<String?>(null) }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                candidateInput = spokenText
                voiceStatusHint = if (isBn) "ভয়েস থেকে টেক্সট গ্রহণ করা হয়েছে!" else "Voice transcribed!"
            }
        } else {
            candidateInput = selectedDistrict.sampleVoiceAnswerBn
            voiceStatusHint = if (isBn)
                "ভয়েস সিমুলেশন ইনপুট লোড হয়েছে (Send বাটনে চাপুন)"
            else
                "Voice-to-text sample loaded (Tap Send)"
        }
    }

    val launchSpeechToText = {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-IN")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "WBPRB ইন্টারভিউ বোর্ডের উত্তর বলুন...")
            }
            speechRecognizerLauncher.launch(intent)
        } catch (_: Exception) {
            candidateInput = selectedDistrict.sampleVoiceAnswerBn
            voiceStatusHint = if (isBn)
                "ভয়েস-টু-টেক্সট উত্তর প্রস্তুত (Send বাটনে চাপুন)"
            else
                "Voice-to-text response ready (Tap Send)"
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        launchSpeechToText()
    }

    val sampleQuickAnswers = remember(selectedDistrict) {
        listOf(
            selectedDistrict.sampleVoiceAnswerBn,
            "স্যার, মানুষের জীবন রক্ষা করা পুলিশের প্রথম কর্তব্য। আমি তৎক্ষণাৎ কন্ট্রোল রুমে জানিয়ে আহতকে হাসপাতালে পাঠাব এবং শান্তিশৃঙ্খলা বজায় রাখব।",
            "স্যার, কন্যাশ্রী ও লক্ষ্মীর ভাণ্ডার প্রকল্প নারী ক্ষমতায়নে বড় ভূমিকা রাখছে। আর যেকোনো থানায় ঘটনাস্থল নির্বিশেষে দায়ের করা FIR-কে Zero FIR বলে।"
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Minimal 15-Mark Interview Board Header & Home District Selector
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
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isBn) "১৫-নম্বর WBPRB ইন্টারভিউ বোর্ড" else "15-Mark WBPRB Interview Board",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isBn) "৩-সদস্যের প্যানেল: চেয়ারম্যান (IPS) • SP • শিক্ষাবিদ" else "3-Member Panel: IPS Chairman • SP • Academician",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (latestScore != null) {
                        Surface(
                            color = EmeraldSuccess.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("interview_score_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format(Locale.US, "%.1f / 15", latestScore),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = EmeraldSuccess,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    IconButton(onClick = onResetInterview, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart Interview",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Home District Selector Dropdown
                Box {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { districtMenuExpanded = true }
                            .testTag("district_selector_dropdown"),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = if (isBn)
                                            "আপনার জেলা: ${selectedDistrict.nameBn}"
                                        else
                                            "Home District: ${selectedDistrict.nameEn}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = selectedDistrict.keyHighlightBn,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Change District",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = districtMenuExpanded,
                        onDismissRequest = { districtMenuExpanded = false }
                    ) {
                        WbDistrict.entries.forEach { dist ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = dist.nameBn, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            text = dist.keyHighlightBn,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                },
                                onClick = {
                                    onSelectDistrict(dist)
                                    districtMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Sample Spoken Answers Strip
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(sampleQuickAnswers) { sampleAns ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onSubmitAnswer(sampleAns) },
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = sampleAns.take(44) + "...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Interview Transcript List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(interviewTurns, key = { it.id }) { turn ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (turn.isCandidate) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(if (turn.isCandidate) 0.86f else 0.95f),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (turn.isCandidate) 16.dp else 4.dp,
                            bottomEnd = if (turn.isCandidate) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (turn.isCandidate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (turn.isCandidate) 0.dp else 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .border(
                                    width = if (turn.isCandidate) 0.dp else 1.dp,
                                    color = if (turn.isCandidate) Color.Transparent else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (turn.isCandidate) 16.dp else 4.dp,
                                        bottomEnd = if (turn.isCandidate) 4.dp else 16.dp
                                    )
                                )
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = turn.speakerTitleBn,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (turn.isCandidate) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                if (turn.scoreOutOf15 != null) {
                                    Surface(
                                        color = EmeraldSuccess.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = String.format(Locale.US, "Score: %.1f / 15", turn.scoreOutOf15),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = EmeraldSuccess,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            if (turn.scoreOutOf15 != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (turn.strengthsBn.isNotBlank()) {
                                            Row(verticalAlignment = Alignment.Top) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = EmeraldSuccess,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "ভালো দিক: ${turn.strengthsBn}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                        if (turn.improvementTipsBn.isNotBlank()) {
                                            Row(verticalAlignment = Alignment.Top) {
                                                Icon(
                                                    imageVector = Icons.Default.Lightbulb,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "উন্নতির পরামর্শ (Improvement Tip): ${turn.improvementTipsBn}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                        if (turn.modelAnswerBn.isNotBlank()) {
                                            Text(
                                                text = "আদর্শ উত্তর: \"${turn.modelAnswerBn}\"",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "পরবর্তী প্রশ্ন (Next Panel Question):",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            } else {
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            Text(
                                text = turn.messageBn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (turn.isCandidate) Color.White else MaterialTheme.colorScheme.onSurface
                            )
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
                                "ইন্টারভিউ বোর্ড আপনার উত্তর মূল্যায়ন করছে..."
                            else
                                "Panel is grading your response out of 15...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (!voiceStatusHint.isNullOrBlank()) {
            Text(
                text = voiceStatusHint!!,
                style = MaterialTheme.typography.labelSmall,
                color = EmeraldSuccess,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Floating Bottom Voice + Text Input Pill Bar
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
                // Microphone Voice-to-Text Button inside Input Pill
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !isLoading) {
                            val hasAudioPerm = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasAudioPerm) {
                                launchSpeechToText()
                            } else {
                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                        .testTag("interview_mic_button"),
                    color = EmeraldSuccess.copy(alpha = 0.14f),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Microphone Voice Input",
                            tint = EmeraldSuccess,
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
                    if (candidateInput.isEmpty()) {
                        Text(
                            text = if (isBn)
                                "মাইকে বলুন বা বাংলায় উত্তর লিখুন..."
                            else
                                "Tap Mic to speak or type your answer...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    BasicTextField(
                        value = candidateInput,
                        onValueChange = { candidateInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("interview_answer_input"),
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
                        .clickable(enabled = candidateInput.isNotBlank() && !isLoading) {
                            onSubmitAnswer(candidateInput)
                            candidateInput = ""
                            voiceStatusHint = null
                        }
                        .testTag("interview_send_button"),
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Submit Interview Answer",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
