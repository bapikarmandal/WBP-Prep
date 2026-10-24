package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.AspirantProfile
import com.example.data.ExamType
import com.example.data.MistakeNotebookEntity
import com.example.data.MockTestResultEntity
import com.example.data.SubjectCategory
import com.example.data.SyllabusTopicEntity
import com.example.data.QuizQuestion
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonWrong
import com.example.ui.theme.EmeraldSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    language: AppLanguage,
    selectedExam: ExamType,
    dailyStreak: Int,
    syllabusTopics: List<SyllabusTopicEntity>,
    mockResults: List<MockTestResultEntity>,
    mistakeNotebook: List<MistakeNotebookEntity>,
    questionVault: List<QuizQuestion> = emptyList(),
    seenQuestionIds: Set<String> = emptySet(),
    bulkGeneratingTarget: String? = null,
    isAdmin: Boolean = false,
    aspirantProfile: AspirantProfile? = null,
    onOpenProfileDialog: () -> Unit = {},
    onLogoutUser: () -> Unit = {},
    onSelectExam: (ExamType) -> Unit,
    onStartQuickQuiz: () -> Unit,
    onStartRetryMistakes: () -> Unit,
    onRemoveMistake: (String) -> Unit,
    onStartSubjectPractice: (String) -> Unit,
    onBulkAdd50Questions: (String) -> Unit = {},
    onOpenSyllabus: () -> Unit,
    onOpenFlashcards: () -> Unit = onOpenSyllabus,
    onOpenMockInterview: () -> Unit = {},
    onOpenPetStopwatch: () -> Unit = {}
) {
    val isBn = language == AppLanguage.BENGALI
    var examDropdownExpanded by remember { mutableStateOf(false) }
    var showMistakeDrawer by remember { mutableStateOf(false) }

    val overallAccuracy = remember(mockResults) {
        if (mockResults.isEmpty()) 78
        else mockResults.map { it.accuracyPercent }.average().roundToInt()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // 0. ASPIRANT PERSONALIZED PROFILE CARD (Greet by name, Target Exam badge, Category badge, Edit/Logout)
        if (aspirantProfile != null) {
            item {
                val cat = aspirantProfile.resolvedCasteCategory
                val target = aspirantProfile.resolvedTargetExam
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("aspirant_dashboard_profile_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ASPIRANT PROFILE • PERSONALIZED PREP",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 0.7.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Welcome, ${aspirantProfile.fullName} 👋",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onOpenProfileDialog() }
                                        .testTag("dashboard_edit_profile_button"),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Edit Profile",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }

                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onLogoutUser() }
                                        .testTag("dashboard_logout_button"),
                                    color = CrimsonWrong.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Log Out",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CrimsonWrong,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Target Exam Badge & Caste/Category Badge Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "🎯 ${target.shortBadge}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            Surface(
                                color = AmberWarning.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "🛡️ Category: ${cat.shortBadge} (Cut-Off: ${cat.safeCutoffOutOf85}/85)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberWarning,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1. TOP: Clean Minimal Hero Card with Exam Selector, Streak Pill & Single "Start Daily Quiz" Button
        item {
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
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Micro-label + Streak & Accuracy Pill Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBn) "WBPRB প্রস্তুতি ড্যাশবোর্ড" else "WBPRB EXAM PREP",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Streak Pill
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Streak",
                                        tint = AmberWarning,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isBn) "$dailyStreak দিন" else "${dailyStreak}d Streak",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Accuracy Pill
                            Surface(
                                color = EmeraldSuccess.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "$overallAccuracy% Acc",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldSuccess,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Clean Exam Selector (Synced/Locked to Aspirant's Registered Target Exam when logged in as Aspirant)
                    Box {
                        val targetLabel = aspirantProfile?.resolvedTargetExam?.displayLabel
                            ?: (if (isBn) selectedExam.titleBn else selectedExam.titleEn)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (aspirantProfile != null) onOpenProfileDialog()
                                    else examDropdownExpanded = true
                                }
                                .testTag("exam_selector_dropdown"),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (aspirantProfile != null)
                                            "LOCKED TARGET EXAM (SYNCED FROM PROFILE)"
                                        else
                                            (if (isBn) "নির্বাচিত পরীক্ষা" else "TARGET EXAM"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = targetLabel,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select Exam",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = examDropdownExpanded,
                            onDismissRequest = { examDropdownExpanded = false }
                        ) {
                            ExamType.entries.forEach { exam ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = if (isBn) exam.titleBn else exam.titleEn,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "${exam.totalMarks} Marks • +1 / -0.25 Marking",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        onSelectExam(exam)
                                        examDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Single Clean Indigo CTA Button: "Start Daily Quiz"
                    Button(
                        onClick = onStartQuickQuiz,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quick_quiz_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "Start Daily Quiz (১০ মিনিটের কুইজ)" else "Start Daily Quiz (10-Min OMR)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    // Live WBP & KP Smart Question Bank Counter on Home Dashboard
                    val totalVaultCount = questionVault.size
                    val totalUnseenCount = remember(questionVault, seenQuestionIds) {
                        questionVault.count { it.id !in seenQuestionIds }
                    }
                    val gkVaultCount = remember(questionVault) { questionVault.count { it.subject.equals("GK", ignoreCase = true) } }
                    val mathVaultCount = remember(questionVault) { questionVault.count { it.subject.equals("MATH", ignoreCase = true) } }
                    val reasVaultCount = remember(questionVault) { questionVault.count { it.subject.equals("REASONING", ignoreCase = true) } }
                    val engVaultCount = remember(questionVault) { questionVault.count { it.subject.equals("ENGLISH", ignoreCase = true) } }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_smart_question_bank_counter"),
                        color = EmeraldSuccess.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, EmeraldSuccess.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = if (isBn)
                                    "WBP & KP Smart Question Bank • মোট প্রশ্ন: $totalVaultCount (Unseen: $totalUnseenCount)"
                                else
                                    "WBP & KP Smart Question Bank • Total: $totalVaultCount Questions (Unseen: $totalUnseenCount)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                            Text(
                                text = "GK: $gkVaultCount • Math: $mathVaultCount • Reasoning: $reasVaultCount • English: $engVaultCount",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 2. MIDDLE: Compact 2x2 Bento Grid for Quick Access
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isBn) "কুইক অ্যাক্সেস টুলস (QUICK TOOLS)" else "QUICK ACCESS TOOLS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BentoGridTile(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("bento_mistake_notebook"),
                        icon = Icons.Default.BookmarkBorder,
                        accentColor = CrimsonWrong,
                        title = if (isBn) "ভুল সংশোধনী" else "Mistake Notebook",
                        subtitle = if (isBn) "${mistakeNotebook.size}টি সেভ করা প্রশ্ন" else "${mistakeNotebook.size} saved items",
                        onClick = { showMistakeDrawer = !showMistakeDrawer }
                    )

                    BentoGridTile(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("bento_pocket_flashcards"),
                        icon = Icons.Default.Style,
                        accentColor = MaterialTheme.colorScheme.primary,
                        title = if (isBn) "পকেট ফ্ল্যাশকার্ড" else "Pocket Flashcards",
                        subtitle = if (isBn) "এক নজরে ৩টি ডেক" else "3 swipeable decks",
                        onClick = onOpenFlashcards
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BentoGridTile(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("bento_mock_interview"),
                        icon = Icons.Default.MicNone,
                        accentColor = EmeraldSuccess,
                        title = if (isBn) "মক ইন্টারভিউ" else "Mock Interview",
                        subtitle = if (isBn) "১৫ নম্বরের AI বোর্ড" else "15-Mark AI Board",
                        onClick = onOpenMockInterview
                    )

                    BentoGridTile(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("bento_pet_stopwatch"),
                        icon = Icons.Default.Timer,
                        accentColor = AmberWarning,
                        title = if (isBn) "মাঠ স্টপওয়াচ" else "PET Stopwatch",
                        subtitle = if (isBn) "1600m / 800m পেস" else "1600m / 800m Pace",
                        onClick = onOpenPetStopwatch
                    )
                }
            }
        }

        // Mistake Notebook Card (compact, sleek minimal card with Retry My Mistakes button)
        item {
            MistakeNotebookSectionCard(
                isBn = isBn,
                mistakeNotebook = mistakeNotebook,
                onRetryMistakes = onStartRetryMistakes,
                onRemoveMistake = onRemoveMistake,
                retryButtonTestTag = "retry_mistakes_home_button",
                initiallyExpanded = showMistakeDrawer
            )
        }

        // 3. BOTTOM: Clean, thin (h-1.5 rounded-full) Subject Progress Bars
        item {
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
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBn) "সিলেবাস অগ্রগতি" else "SYLLABUS PROGRESS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBn) "বিষয়ভিত্তিক প্রস্তুতি (WBPRB)" else "Subject-Wise Mastery",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onOpenSyllabus() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBn) "চেকলিস্ট" else "Checklist",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    SubjectCategory.entries.forEach { subject ->
                        val subTopics = syllabusTopics.filter { it.subject == subject.id }
                        val completedCount = subTopics.count { it.isCompleted }
                        val totalCount = subTopics.size.coerceAtLeast(1)
                        val progress = completedCount.toFloat() / totalCount.toFloat()
                        val progressPct = (progress * 100).roundToInt()

                        val subjectVaultItems = remember(questionVault, subject.id) {
                            questionVault.filter { it.subject.equals(subject.id, ignoreCase = true) }
                        }
                        val subjectSavedCount = subjectVaultItems.size
                        val subjectUnseenCount = remember(subjectVaultItems, seenQuestionIds) {
                            subjectVaultItems.count { it.id !in seenQuestionIds }
                        }

                        val marksText = if (selectedExam == ExamType.CONSTABLE || subject == SubjectCategory.ENGLISH) {
                            "${subject.constableMarks}M"
                        } else {
                            "${subject.siMarks}M"
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .clickable { onStartSubjectPractice(subject.id) }
                                .padding(10.dp)
                                .testTag("subject_card_${subject.id.lowercase()}"),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                    Text(
                                        text = if (isBn) subject.titleBn else subject.titleEn,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = marksText,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "$completedCount/${subTopics.size} ($progressPct%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Thin h-1.5 (6.dp) rounded-full progress bar
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = if (progressPct >= 70) EmeraldSuccess else MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            // Live Question Vault counter badge: [Saved in Vault: X / 1000 Questions • Unseen: Y]
                            VaultCounterBadge(
                                savedCount = subjectSavedCount,
                                unseenCount = subjectUnseenCount,
                                targetCapacity = 1000,
                                modifier = Modifier.testTag("home_vault_badge_${subject.id.lowercase()}")
                            )

                            // ⚡ Bulk Add +50 New PYQ-Pattern Questions to Vault button (Admin Only)
                            if (isAdmin) {
                                BulkAdd50VaultButton(
                                    isBn = isBn,
                                    isLoading = bulkGeneratingTarget == subject.id,
                                    testTag = "home_bulk_add_50_${subject.id.lowercase()}",
                                    onClick = { onBulkAdd50Questions(subject.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Compact Recent Mock Test History (if present)
        if (mockResults.isNotEmpty()) {
            item {
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
                        Text(
                            text = if (isBn) "সাম্প্রতিক মক টেস্ট (RECENT SCORES)" else "RECENT MOCK SCORES",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp
                        )

                        mockResults.take(3).forEach { res ->
                            val dateFmt = remember(res.timestamp) {
                                SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(res.timestamp))
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = res.examTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "✓ ${res.correctCount}  ✕ ${res.wrongCount}  •  ${res.accuracyPercent}%  •  $dateFmt",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    color = EmeraldSuccess.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%.2f / %.0f", res.netScore, res.maxMarks),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = EmeraldSuccess,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun BentoGridTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    accentColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
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
            Surface(
                color = accentColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun MistakeNotebookSectionCard(
    isBn: Boolean,
    mistakeNotebook: List<MistakeNotebookEntity>,
    onRetryMistakes: () -> Unit,
    onRemoveMistake: (String) -> Unit,
    retryButtonTestTag: String,
    initiallyExpanded: Boolean = false
) {
    var expanded by remember(initiallyExpanded) { mutableStateOf(initiallyExpanded) }
    var filterMode by remember { mutableStateOf("ALL") }

    val wrongCount = mistakeNotebook.count { it.isWrongAnswer }
    val bookmarkCount = mistakeNotebook.count { it.isBookmarked }

    val filteredList = remember(mistakeNotebook, filterMode) {
        when (filterMode) {
            "WRONG" -> mistakeNotebook.filter { it.isWrongAnswer }
            "BOOKMARK" -> mistakeNotebook.filter { it.isBookmarked }
            else -> mistakeNotebook
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mistake_notebook_card"),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = CrimsonWrong.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = CrimsonWrong,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = if (isBn) "ভুল সংশোধনী খাতা ও বুকমার্ক" else "Mistake Notebook & Bookmarks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isBn)
                                "ভুল উত্তর: $wrongCount • বুকমার্ক: $bookmarkCount"
                            else
                                "Wrong: $wrongCount • Starred: $bookmarkCount",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Mistake Notebook",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onRetryMistakes,
                enabled = mistakeNotebook.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag(retryButtonTestTag),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonWrong,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBn)
                        "Retry My Mistakes (${mistakeNotebook.size}টি প্রশ্ন)"
                    else
                        "Retry My Mistakes (${mistakeNotebook.size} Questions)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filterMode == "ALL",
                            onClick = { filterMode = "ALL" },
                            label = { Text(if (isBn) "সব (${mistakeNotebook.size})" else "All (${mistakeNotebook.size})") }
                        )
                        FilterChip(
                            selected = filterMode == "WRONG",
                            onClick = { filterMode = "WRONG" },
                            label = { Text(if (isBn) "ভুল ($wrongCount)" else "Wrong ($wrongCount)") }
                        )
                        FilterChip(
                            selected = filterMode == "BOOKMARK",
                            onClick = { filterMode = "BOOKMARK" },
                            label = { Text(if (isBn) "স্টার ($bookmarkCount)" else "Starred ($bookmarkCount)") }
                        )
                    }

                    filteredList.forEach { item ->
                        val q = remember(item) { item.toQuizQuestion() }
                        val opts = q.displayOptions()
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = item.subject,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        if (item.isWrongAnswer) {
                                            Surface(
                                                color = CrimsonWrong.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = if (isBn) "ভুল" else "Wrong",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = CrimsonWrong,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { onRemoveMistake(item.questionId) },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Remove",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = q.displayQuestionText(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = (if (isBn) "✓ সঠিক: " else "✓ Correct: ") +
                                        opts.getOrElse(item.correctIndex) { "" },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = EmeraldSuccess
                                )

                                val trick = q.displayShortcutBn()
                                if (trick.isNotBlank()) {
                                    Text(
                                        text = "💡 $trick",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
