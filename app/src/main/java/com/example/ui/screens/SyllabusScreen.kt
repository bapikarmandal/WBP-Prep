package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.ChapterStudyNotesRepository
import com.example.data.PocketCardsRepository
import com.example.data.PocketRevisionCard
import com.example.data.QuizQuestion
import com.example.data.RevisionDeckType
import com.example.data.WbpChapterInfo
import com.example.data.WbpPyqVaultSeed
import com.example.data.StaticRevisionTable
import com.example.data.SubjectCategory
import com.example.data.SyllabusTopicEntity
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SyllabusScreen(
    language: AppLanguage,
    syllabusTopics: List<SyllabusTopicEntity>,
    onToggleTopic: (SyllabusTopicEntity) -> Unit,
    persistedRevisedIds: Set<String> = emptySet(),
    onToggleCardRevised: ((String) -> Unit)? = null,
    initialFlashcardsMode: Boolean = false,
    questionVault: List<QuizQuestion> = emptyList(),
    seenQuestionIds: Set<String> = emptySet(),
    bulkGeneratingTarget: String? = null,
    onStartChapterTest: (WbpChapterInfo) -> Unit = {},
    onBulkAdd50ForChapter: (WbpChapterInfo) -> Unit = {}
) {
    val isBn = language == AppLanguage.BENGALI
    var isFlashcardsTab by remember { mutableStateOf(initialFlashcardsMode) }
    var selectedSubjectFilter by remember { mutableStateOf<String?>(null) }
    var selectedDeck by remember { mutableStateOf(RevisionDeckType.WB_STATIC_GK) }
    var selectedStudySubject by remember { mutableStateOf("MATH") }
    var expandedChapterId by remember { mutableStateOf<String?>("MATH_CH_01") }
    var expandedTableId by remember { mutableStateOf<String?>("WB_RIVERS_DAMS") }

    LaunchedEffect(initialFlashcardsMode) {
        isFlashcardsTab = initialFlashcardsMode
    }

    val flippedCards = remember { mutableStateMapOf<String, Boolean>() }
    val localRevisedCardIds = remember { mutableStateMapOf<String, Boolean>() }
    val effectiveRevisedMap = remember(persistedRevisedIds, localRevisedCardIds.toMap()) {
        buildMap {
            putAll(localRevisedCardIds)
            persistedRevisedIds.forEach { put(it, true) }
        }
    }

    val deckCards = remember(selectedDeck) {
        PocketCardsRepository.getCardsForDeck(selectedDeck)
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { deckCards.size }
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedDeck) {
        pagerState.scrollToPage(0)
    }

    val completedTotal = syllabusTopics.count { it.isCompleted }
    val totalItems = syllabusTopics.size.coerceAtLeast(1)
    val overallPct = ((completedTotal.toFloat() / totalItems) * 100).roundToInt()

    val filteredTopics = remember(syllabusTopics, selectedSubjectFilter) {
        if (selectedSubjectFilter == null) syllabusTopics
        else syllabusTopics.filter { it.subject == selectedSubjectFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // iOS-Style Segmented Control: [Syllabus Checklist] vs [Swipeable Flashcards]
        item {
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
                            .clickable { isFlashcardsTab = false }
                            .testTag("syllabus_checklist_subtab"),
                        color = if (!isFlashcardsTab) MaterialTheme.colorScheme.surface else Color.Transparent,
                        shape = RoundedCornerShape(9.dp),
                        shadowElevation = if (!isFlashcardsTab) 1.dp else 0.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBn) "সিলেবাস চেকলিস্ট" else "Syllabus Checklist",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (!isFlashcardsTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .clickable { isFlashcardsTab = true }
                            .testTag("syllabus_flashcards_subtab"),
                        color = if (isFlashcardsTab) MaterialTheme.colorScheme.surface else Color.Transparent,
                        shape = RoundedCornerShape(9.dp),
                        shadowElevation = if (isFlashcardsTab) 1.dp else 0.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBn) "পকেট ফ্ল্যাশকার্ড (৩ ডেক)" else "Swipeable Flashcards",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isFlashcardsTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        if (isFlashcardsTab) {
            item {
                QuickRevisionPocketCardsSection(
                    isBn = isBn,
                    selectedDeck = selectedDeck,
                    deckCards = deckCards,
                    pagerState = pagerState,
                    flippedCards = flippedCards,
                    revisedCardIds = effectiveRevisedMap,
                    onSelectDeck = { selectedDeck = it },
                    onToggleFlip = { cardId ->
                        flippedCards[cardId] = !(flippedCards[cardId] ?: false)
                    },
                    onToggleRevised = { cardId ->
                        if (onToggleCardRevised != null) {
                            onToggleCardRevised(cardId)
                        } else {
                            localRevisedCardIds[cardId] = !(localRevisedCardIds[cardId] ?: false)
                        }
                    },
                    onPrevCard = {
                        if (pagerState.currentPage > 0) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    },
                    onNextCard = {
                        if (pagerState.currentPage < deckCards.lastIndex) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            return@LazyColumn
        }

        // Compact Flashcard Quick-Launch Banner + Full Pocket Cards Section
        item {
            QuickRevisionPocketCardsSection(
                isBn = isBn,
                selectedDeck = selectedDeck,
                deckCards = deckCards,
                pagerState = pagerState,
                flippedCards = flippedCards,
                revisedCardIds = effectiveRevisedMap,
                onSelectDeck = { selectedDeck = it },
                onToggleFlip = { cardId ->
                    flippedCards[cardId] = !(flippedCards[cardId] ?: false)
                },
                onToggleRevised = { cardId ->
                    if (onToggleCardRevised != null) {
                        onToggleCardRevised(cardId)
                    } else {
                        localRevisedCardIds[cardId] = !(localRevisedCardIds[cardId] ?: false)
                    }
                },
                onPrevCard = {
                    if (pagerState.currentPage > 0) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                },
                onNextCard = {
                    if (pagerState.currentPage < deckCards.lastIndex) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            )
        }

        // REQUIREMENT 3: CHAPTER-WISE STUDY NOTES, SHORTCUT FORMULAS & CHEAT SHEETS (All 53 WBP & KP Chapters)
        item {
            ChapterWiseStudyHubSection(
                isBn = isBn,
                selectedSubject = selectedStudySubject,
                expandedChapterId = expandedChapterId,
                questionVault = questionVault,
                seenQuestionIds = seenQuestionIds,
                bulkGeneratingTarget = bulkGeneratingTarget,
                onSelectSubject = { sub ->
                    selectedStudySubject = sub
                    expandedChapterId = WbpPyqVaultSeed.getChaptersForSubject(sub).firstOrNull()?.chapterId
                },
                onToggleChapterExpand = { chId ->
                    expandedChapterId = if (expandedChapterId == chId) null else chId
                },
                onStartChapterTest = onStartChapterTest,
                onBulkAdd50ForChapter = onBulkAdd50ForChapter
            )
        }

        // West Bengal Special Static GK Revision Tables & High-Frequency WBP English Cheat Sheets
        item {
            StaticGkAndEnglishCheatTablesSection(
                isBn = isBn,
                expandedTableId = expandedTableId,
                onToggleTable = { id ->
                    expandedTableId = if (expandedTableId == id) null else id
                }
            )
        }

        // Official WBPRB Syllabus Checklist Header Card
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
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBn) "WBPRB সিলেবাস চেকলিস্ট" else "OFFICIAL WBPRB SYLLABUS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBn)
                                    "$completedTotal / ${syllabusTopics.size}টি টপিক সম্পন্ন হয়েছে"
                                else
                                    "$completedTotal of ${syllabusTopics.size} syllabus modules completed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Surface(
                            color = EmeraldSuccess.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$overallPct%",
                                style = MaterialTheme.typography.labelLarge,
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { completedTotal.toFloat() / totalItems },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = EmeraldSuccess,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // Subject Filter Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedSubjectFilter == null,
                    onClick = { selectedSubjectFilter = null },
                    label = { Text(if (isBn) "সব (All)" else "All") }
                )
                SubjectCategory.entries.forEach { sub ->
                    FilterChip(
                        selected = selectedSubjectFilter == sub.id,
                        onClick = { selectedSubjectFilter = sub.id },
                        label = { Text("${sub.id} (${sub.constableMarks}M)") }
                    )
                }
            }
        }

        // Checklist Items
        items(filteredTopics, key = { it.id }) { topic ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleTopic(topic) }
                    .testTag("syllabus_topic_${topic.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (topic.isCompleted) EmeraldSuccess.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = topic.isCompleted,
                        onCheckedChange = { onToggleTopic(topic) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = EmeraldSuccess,
                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBn) topic.titleBn else topic.titleEn,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (topic.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                        Text(
                            text = "${topic.subject} • ${if (isBn) topic.titleEn else topic.titleBn}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun QuickRevisionPocketCardsSection(
    isBn: Boolean,
    selectedDeck: RevisionDeckType,
    deckCards: List<PocketRevisionCard>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    flippedCards: Map<String, Boolean>,
    revisedCardIds: Map<String, Boolean>,
    onSelectDeck: (RevisionDeckType) -> Unit,
    onToggleFlip: (String) -> Unit,
    onToggleRevised: (String) -> Unit,
    onPrevCard: () -> Unit,
    onNextCard: () -> Unit
) {
    val totalRevisedInDeck = deckCards.count { revisedCardIds[it.id] == true }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pocket_cards_section"),
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
            // Section Header
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
                        Icon(
                            imageVector = Icons.Default.Style,
                            contentDescription = "Pocket Flashcards",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(7.dp)
                                .size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isBn) "এক নজরে রিভিশন (Pocket Flashcards)" else "Quick Revision Pocket Cards",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isBn)
                                "সোয়াইপ ফ্ল্যাশকার্ড • ৩টি ডেক"
                            else
                                "Swipeable flashcards across 3 decks",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = EmeraldSuccess.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isBn) "$totalRevisedInDeck/${deckCards.size} সম্পন্ন" else "$totalRevisedInDeck/${deckCards.size} Done",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // 3 Deck Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RevisionDeckType.entries.forEach { deck ->
                    val isSelected = selectedDeck == deck
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectDeck(deck) },
                        modifier = Modifier.testTag("deck_chip_${deck.id}"),
                        label = {
                            Text(
                                text = if (isBn) deck.titleBn else deck.titleEn,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Active Deck Subtitle
            Text(
                text = if (isBn) selectedDeck.subtitleBn else selectedDeck.subtitleEn,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Swipeable HorizontalPager for Flashcards
            if (deckCards.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pocket_card_pager"),
                    pageSpacing = 12.dp
                ) { pageIndex ->
                    val card = deckCards[pageIndex]
                    val isFlipped = flippedCards[card.id] == true
                    val isRevised = revisedCardIds[card.id] == true

                    PocketFlashcardItem(
                        card = card,
                        pageIndex = pageIndex,
                        totalCards = deckCards.size,
                        isBn = isBn,
                        isFlipped = isFlipped,
                        isRevised = isRevised,
                        onFlip = { onToggleFlip(card.id) },
                        onToggleRevised = { onToggleRevised(card.id) }
                    )
                }

                // Bottom Pager Dots + Prev/Next Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPrevCard,
                        enabled = pagerState.currentPage > 0,
                        modifier = Modifier.testTag("pocket_card_prev_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Card",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isBn) "আগের" else "Prev",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        deckCards.forEachIndexed { idx, c ->
                            val isCurrent = idx == pagerState.currentPage
                            val isCardDone = revisedCardIds[c.id] == true
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(if (isCurrent) 18.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> MaterialTheme.colorScheme.primary
                                            isCardDone -> EmeraldSuccess
                                            else -> MaterialTheme.colorScheme.outline
                                        }
                                    )
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = onNextCard,
                        enabled = pagerState.currentPage < deckCards.lastIndex,
                        modifier = Modifier.testTag("pocket_card_next_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            text = if (isBn) "পরের" else "Next",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Card",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PocketFlashcardItem(
    card: PocketRevisionCard,
    pageIndex: Int,
    totalCards: Int,
    isBn: Boolean,
    isFlipped: Boolean,
    isRevised: Boolean,
    onFlip: () -> Unit,
    onToggleRevised: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onFlip() }
            .testTag("pocket_flashcard_${card.id}"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isRevised) EmeraldSuccess.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card Top Bar: Category Badge + Card Counter
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
                        text = if (isBn) card.categoryTagBn else card.categoryTagEn,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = if (isBn)
                        "কার্ড ${pageIndex + 1}/$totalCards • সোয়াইপ ⇄"
                    else
                        "Card ${pageIndex + 1}/$totalCards • Swipe ⇄",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Flashcard Headline
            Text(
                text = if (isBn) card.titleBn else card.titleEn,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            // Front Key Bullet Points
            val bullets = if (isBn) card.frontSummaryBn else card.frontSummaryEn
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                bullets.forEach { point ->
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Flipped / Expanded Shortcut & PYQ Box
            AnimatedVisibility(visible = isFlipped) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (isBn) "শর্টকাট ট্রিক ও মেমরি টিপস:" else "Shortcut Trick & Memory Tip:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = if (isBn) card.backShortcutOrTipBn else card.backShortcutOrTipEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🎯 ${if (isBn) card.pyqHighlightBn else card.pyqHighlightEn}",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldSuccess,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // Card Footer Actions: Flip Button & Mark Revised Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onFlip() }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                        .testTag("flip_card_btn_${card.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flip,
                        contentDescription = "Flip Flashcard",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (isFlipped) {
                            if (isBn) "শর্টকাট লুকান" else "Hide Shortcut & PYQ"
                        } else {
                            if (isBn) "ট্যাপ করে শর্টকাট ও PYQ দেখুন" else "Tap for Shortcut & PYQ"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    color = if (isRevised) EmeraldSuccess else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .border(1.dp, if (isRevised) EmeraldSuccess else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .clickable { onToggleRevised() }
                        .testTag("revise_done_btn_${card.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Mark Revised",
                            tint = if (isRevised) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isRevised) {
                                if (isBn) "রিভিশন সম্পন্ন ✓" else "Revised ✓"
                            } else {
                                if (isBn) "মুখস্থ হয়েছে" else "Mark Revised"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isRevised) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Complete Chapter-Wise Study Hub across all 53 WBP & KP Smart Question Bank chapters with 3 sub-tabs inside every chapter:
 * [1. Concept & Shortcut Notes (বাংলায়)], [2. Chapter PYQs (2005–2024)], and [3. Start Chapter Test].
 */
@Composable
private fun ChapterWiseStudyHubSection(
    isBn: Boolean,
    selectedSubject: String,
    expandedChapterId: String?,
    questionVault: List<QuizQuestion>,
    seenQuestionIds: Set<String>,
    bulkGeneratingTarget: String?,
    onSelectSubject: (String) -> Unit,
    onToggleChapterExpand: (String) -> Unit,
    onStartChapterTest: (WbpChapterInfo) -> Unit,
    onBulkAdd50ForChapter: (WbpChapterInfo) -> Unit
) {
    val subjectTabs = listOf(
        Triple("MATH", "Math Shortcuts (18 Ch)", "গণিত শর্টকাট (১৮ অধ্যায়)"),
        Triple("GK", "WB & Static GK (7 Ch)", "জিকে নোটস (৭ অধ্যায়)"),
        Triple("REASONING", "GI & Reasoning (20 Ch)", "রিজনিং ট্রিকস (২০ অধ্যায়)"),
        Triple("ENGLISH", "WBP English (8 Ch)", "ইংরেজি নোটস (৮ অধ্যায়)")
    )

    val chapters = remember(selectedSubject) {
        WbpPyqVaultSeed.getChaptersForSubject(selectedSubject)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chapter_study_hub_section"),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = if (isBn)
                            "অধ্যায়ভিত্তিক স্টাডি নোটস, শর্টকাট সূত্র ও PYQ হাব"
                        else
                            "CHAPTER-WISE STUDY NOTES, SHORTCUT FORMULAS & PYQ HUB",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isBn)
                            "প্রতিটি অধ্যায়ে রয়েছে: [১. শর্টকাট নোটস] • [২. বিগত বছরের PYQ] • [৩. অধ্যায় টেস্ট]"
                        else
                            "Inside every chapter: [1. Concept & Shortcut Notes] • [2. Chapter PYQs] • [3. Start Chapter Test]",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Subject Selector Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjectTabs.forEach { (subCode, labelEn, labelBn) ->
                    val isSelected = selectedSubject == subCode
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { onSelectSubject(subCode) }
                            .testTag("study_hub_subject_$subCode"),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = if (isBn) labelBn else labelEn,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            // Chapters Accordion List
            chapters.forEach { chapter ->
                val isExpanded = expandedChapterId == chapter.chapterId
                var activeSubTab by remember(chapter.chapterId) { mutableIntStateOf(0) }
                val studyNote = remember(chapter.chapterId) {
                    ChapterStudyNotesRepository.getStudyNoteForChapter(chapter)
                }
                val chapterPyqs = remember(questionVault, chapter.chapterId) {
                    val exact = questionVault.filter { it.chapterId.equals(chapter.chapterId, ignoreCase = true) }
                    if (exact.isNotEmpty()) exact else questionVault.filter { it.subject.equals(chapter.subject, ignoreCase = true) }.take(5)
                }
                val unseenInChapter = remember(chapterPyqs, seenQuestionIds) {
                    chapterPyqs.count { it.id !in seenQuestionIds }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("study_chapter_card_${chapter.chapterId}"),
                    color = if (isExpanded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleChapterExpand(chapter.chapterId) },
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
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${chapter.subject} ${chapter.numberCode}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isBn) "${chapter.titleBn} (${chapter.titleEn})" else "${chapter.titleEn} (${chapter.titleBn})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (isBn)
                                            "শর্টকাট সূত্র • ${chapterPyqs.size}টি PYQ প্রশ্ন • লাইভ চ্যাপ্টার টেস্ট"
                                        else
                                            "Shortcut Notes • ${chapterPyqs.size} PYQs • Chapter Test",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // 3 Sub-Tabs inside every chapter:
                                // [1. Concept & Shortcut Notes (বাংলায়)], [2. Chapter PYQs (2005–2024)], [3. Start Chapter Test]
                                val subTabLabels = listOf(
                                    "1. Concept & Shortcut Notes (বাংলায়)",
                                    "2. Chapter PYQs (2005–2024)",
                                    "3. Start Chapter Test"
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    subTabLabels.forEachIndexed { idx, tabTitle ->
                                        val isTabSelected = activeSubTab == idx
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { activeSubTab = idx }
                                                .testTag("chapter_subtab_${chapter.chapterId}_${idx + 1}"),
                                            color = if (isTabSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = tabTitle,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isTabSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier
                                                    .border(
                                                        1.dp,
                                                        if (isTabSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                when (activeSubTab) {
                                    0 -> {
                                        // Sub-Tab 1: Concept & Shortcut Notes (বাংলায়)
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            studyNote.summaryBulletsBn.forEach { bullet ->
                                                Text(
                                                    text = "• $bullet",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            studyNote.shortcutFormulas.forEach { formula ->
                                                Surface(
                                                    color = MaterialTheme.colorScheme.surface,
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(
                                                        modifier = Modifier
                                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                                            .padding(10.dp),
                                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Text(
                                                            text = "⚡ ${formula.titleBn}",
                                                            style = MaterialTheme.typography.labelMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                        Text(
                                                            text = formula.formulaOrRuleBn,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = EmeraldSuccess
                                                        )
                                                        Text(
                                                            text = formula.solvedExampleBn,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }

                                            Surface(
                                                color = AmberWarning.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "🎯 Exam Hall Tip: ${studyNote.examTipBn}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = AmberWarning,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }
                                    }

                                    1 -> {
                                        // Sub-Tab 2: Chapter PYQs (2005–2024)
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            chapterPyqs.take(5).forEachIndexed { qIdx, q ->
                                                Surface(
                                                    color = MaterialTheme.colorScheme.surface,
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(
                                                        modifier = Modifier
                                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                                            .padding(10.dp),
                                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = "PYQ #${qIdx + 1}",
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                            Text(
                                                                text = "[${q.examYearTag.ifBlank { "WBPRB PYQ" }}]",
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color = AmberWarning
                                                            )
                                                        }
                                                        Text(
                                                            text = q.displayQuestionText(),
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        val opts = q.displayOptions()
                                                        Text(
                                                            text = "✓ উত্তর: ${opts.getOrElse(q.correctIndex) { "" }}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = EmeraldSuccess
                                                        )
                                                        Text(
                                                            text = q.displayExplanationBn(),
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    else -> {
                                        // Sub-Tab 3: Start Chapter Test
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            VaultCounterBadge(
                                                savedCount = chapterPyqs.size,
                                                unseenCount = unseenInChapter,
                                                targetCapacity = 1000
                                            )

                                            Button(
                                                onClick = { onStartChapterTest(chapter) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(42.dp)
                                                    .testTag("syllabus_start_chapter_test_${chapter.chapterId}"),
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
                                                        "Start Chapter Test (${chapter.titleBn} • ১৫টি প্রশ্ন)"
                                                    else
                                                        "Start Chapter Test (${chapter.titleEn} • 15Q)",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }

                                            BulkAdd50VaultButton(
                                                isBn = isBn,
                                                isLoading = bulkGeneratingTarget == chapter.chapterId,
                                                testTag = "syllabus_bulk_add_50_${chapter.chapterId}",
                                                onClick = { onBulkAdd50ForChapter(chapter) }
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
    }
}

/**
 * West Bengal Special Static GK Revision Tables & High-Frequency WBP English Cheat Sheets in Bengali.
 */
@Composable
private fun StaticGkAndEnglishCheatTablesSection(
    isBn: Boolean,
    expandedTableId: String?,
    onToggleTable: (String) -> Unit
) {
    val tables: List<StaticRevisionTable> = ChapterStudyNotesRepository.wbStaticGkTables

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("static_gk_tables_section"),
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TableChart,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = if (isBn)
                            "পশ্চিমবঙ্গ স্পেশাল স্ট্যাটিক জিকে ও ইংরেজি চিট-শিট টেবিল"
                        else
                            "WB STATIC GK REVISION TABLES & ENGLISH CHEAT SHEETS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isBn)
                            "নদনদী ও বাঁধ, অভয়ারণ্য, লোকনৃত্য, ঐতিহাসিক যুদ্ধ, সংবিধানের ধারা ও Fixed Prepositions"
                        else
                            "Rivers & Dams, National Parks, Folk Dances, Battles, Articles & Fixed Prepositions",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            tables.forEach { table ->
                val isExpanded = expandedTableId == table.id
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("revision_table_${table.id}"),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleTable(table.id) },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBn) table.titleBn else table.titleEn,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                // Table Header Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = table.headersBn.first,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.weight(0.32f)
                                    )
                                    Text(
                                        text = table.headersBn.second,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.weight(0.32f)
                                    )
                                    Text(
                                        text = table.headersBn.third,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.weight(0.36f)
                                    )
                                }

                                table.rows.forEach { row ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = row.col1,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.weight(0.32f)
                                        )
                                        Text(
                                            text = row.col2,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(0.32f)
                                        )
                                        Text(
                                            text = row.col3,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.weight(0.36f)
                                        )
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

