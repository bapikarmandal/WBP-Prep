package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.CandidateCategory
import com.example.data.CandidateGender
import com.example.data.PetRunLogEntity
import com.example.data.PolicePost
import com.example.ui.theme.CrimsonWrong
import com.example.ui.theme.EmeraldSuccess
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun PetTrackerScreen(
    language: AppLanguage,
    petRunLogs: List<PetRunLogEntity>,
    onSaveRun: (String, String, Int, Int, Int, Boolean, String) -> Unit,
    onDeleteRun: (Int) -> Unit
) {
    val isBn = language == AppLanguage.BENGALI

    var selectedPost by remember { mutableStateOf(PolicePost.CONSTABLE) }
    var selectedGender by remember { mutableStateOf(CandidateGender.MALE) }
    var selectedCategory by remember { mutableStateOf(CandidateCategory.GENERAL) }

    // PMT Input States
    var heightInput by remember { mutableStateOf("168") }
    var weightInput by remember { mutableStateOf("58") }
    var chestNormalInput by remember { mutableStateOf("79") }
    var chestExpandedInput by remember { mutableStateOf("84") }

    // Official WBPRB PMT Requirements
    val reqHeight = remember(selectedGender, selectedCategory) {
        if (selectedGender == CandidateGender.MALE) {
            if (selectedCategory.isRelaxedHillSt) 160.0 else 167.0
        } else {
            if (selectedCategory.isRelaxedHillSt) 152.0 else 160.0
        }
    }
    val reqWeight = remember(selectedPost, selectedGender, selectedCategory) {
        if (selectedGender == CandidateGender.MALE) {
            if (selectedCategory.isRelaxedHillSt) 53.0 else 57.0
        } else {
            if (selectedCategory.isRelaxedHillSt) 45.0 else 49.0
        }
    }
    val reqChestNormal = remember(selectedCategory) {
        if (selectedCategory.isRelaxedHillSt) 76.0 else 78.0
    }
    val reqChestExpansion = 5.0

    val hVal = heightInput.toDoubleOrNull() ?: 0.0
    val wVal = weightInput.toDoubleOrNull() ?: 0.0
    val cNormVal = chestNormalInput.toDoubleOrNull() ?: 0.0
    val cExpVal = chestExpandedInput.toDoubleOrNull() ?: 0.0
    val expansionVal = cExpVal - cNormVal

    val heightOk = hVal >= reqHeight
    val weightOk = wVal >= reqWeight
    val chestOk = if (selectedGender == CandidateGender.FEMALE) true
    else (cNormVal >= reqChestNormal && expansionVal >= reqChestExpansion)

    val pmtAllEligible = heightOk && weightOk && chestOk

    // Official WBPRB PET Target Distance & Qualifying Seconds
    val targetDistanceMeters = remember(selectedPost, selectedGender) {
        when {
            selectedPost == PolicePost.CONSTABLE && selectedGender == CandidateGender.MALE -> 1600
            selectedPost == PolicePost.CONSTABLE && selectedGender == CandidateGender.FEMALE -> 800
            selectedPost == PolicePost.SI && selectedGender == CandidateGender.MALE -> 800
            else -> 400
        }
    }
    val qualifyingSeconds = remember(selectedPost, selectedGender) {
        when {
            selectedPost == PolicePost.CONSTABLE && selectedGender == CandidateGender.MALE -> 390 // 6m 30s
            selectedPost == PolicePost.CONSTABLE && selectedGender == CandidateGender.FEMALE -> 240 // 4m 00s
            selectedPost == PolicePost.SI && selectedGender == CandidateGender.MALE -> 180 // 3m 00s
            else -> 120 // 2m 00s
        }
    }

    // Running Stopwatch State
    var isRunning by remember { mutableStateOf(false) }
    var elapsedDeciseconds by remember { mutableIntStateOf(0) }
    val lapTimes = remember { mutableStateListOf<Int>() }

    // Manual Pace Calculator Inputs
    var manualMinutesInput by remember { mutableStateOf("6") }
    var manualSecondsInput by remember { mutableStateOf("15") }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(100L)
            elapsedDeciseconds += 1
        }
    }

    val activeTotalSeconds = if (elapsedDeciseconds > 0) {
        elapsedDeciseconds / 10
    } else {
        (manualMinutesInput.toIntOrNull() ?: 0) * 60 + (manualSecondsInput.toIntOrNull() ?: 0)
    }

    val isWithinQualifyingTime = activeTotalSeconds in 1..qualifyingSeconds
    val paceSecondsPerKm = if (activeTotalSeconds > 0 && targetDistanceMeters > 0) {
        (activeTotalSeconds.toDouble() / (targetDistanceMeters / 1000.0)).toInt()
    } else 0
    val paceFormatted = String.format(Locale.US, "%d:%02d /km", paceSecondsPerKm / 60, paceSecondsPerKm % 60)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // Post, Gender & Category Selector Card
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
                    Text(
                        text = if (isBn) "প্রার্থী ও ক্যাটাগরি (CANDIDATE PROFILE)" else "CANDIDATE PROFILE (PMT & PET)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PolicePost.entries.forEach { post ->
                            FilterChip(
                                selected = selectedPost == post,
                                onClick = { selectedPost = post },
                                label = { Text(if (isBn) post.labelBn else post.labelEn) }
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CandidateGender.entries.forEach { gender ->
                            FilterChip(
                                selected = selectedGender == gender,
                                onClick = { selectedGender = gender },
                                label = { Text(if (isBn) gender.labelBn else gender.labelEn) },
                                modifier = Modifier.testTag("gender_chip_${gender.name.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isBn) "ক্যাটাগরি (Category):" else "Category:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CandidateCategory.entries.chunked(2).forEach { rowCats ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowCats.forEach { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(if (isBn) cat.labelBn else cat.labelEn, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // PMT (Physical Measurement Test) Eligibility Checker Card
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
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Straighten,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "শারীরিক মাপজোক (PMT Eligibility)" else "PMT Eligibility Checker",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Surface(
                            color = if (pmtAllEligible) EmeraldSuccess.copy(alpha = 0.12f) else CrimsonWrong.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = if (pmtAllEligible)
                                    (if (isBn) "✓ যোগ্য (ELIGIBLE)" else "✓ ELIGIBLE")
                                else
                                    (if (isBn) "✕ অযোগ্য" else "✕ NOT ELIGIBLE"),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (pmtAllEligible) EmeraldSuccess else CrimsonWrong,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = heightInput,
                            onValueChange = { heightInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pmt_height_input"),
                            label = { Text(if (isBn) "উচ্চতা (cm)" else "Height (cm)") },
                            supportingText = {
                                Text(
                                    text = if (isBn) "ন্যূনতম: ${reqHeight.toInt()} cm" else "Min: ${reqHeight.toInt()} cm",
                                    color = if (heightOk) EmeraldSuccess else CrimsonWrong
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pmt_weight_input"),
                            label = { Text(if (isBn) "ওজন (kg)" else "Weight (kg)") },
                            supportingText = {
                                Text(
                                    text = if (isBn) "ন্যূনতম: ${reqWeight.toInt()} kg" else "Min: ${reqWeight.toInt()} kg",
                                    color = if (weightOk) EmeraldSuccess else CrimsonWrong
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    if (selectedGender == CandidateGender.MALE) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = chestNormalInput,
                                onValueChange = { chestNormalInput = it },
                                modifier = Modifier.weight(1f),
                                label = { Text(if (isBn) "ছাতি (cm)" else "Chest Normal (cm)") },
                                supportingText = {
                                    Text(
                                        text = "Min: ${reqChestNormal.toInt()} cm",
                                        color = if (cNormVal >= reqChestNormal) EmeraldSuccess else CrimsonWrong
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = chestExpandedInput,
                                onValueChange = { chestExpandedInput = it },
                                modifier = Modifier.weight(1f),
                                label = { Text(if (isBn) "ছাতি ফুলিয়ে (cm)" else "Expanded (cm)") },
                                supportingText = {
                                    Text(
                                        text = "+${expansionVal.toInt()} cm (Min +5)",
                                        color = if (expansionVal >= reqChestExpansion) EmeraldSuccess else CrimsonWrong
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // PET Running Stopwatch & Pace Calculator Card
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.AutoMirrored.Filled.DirectionsRun,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isBn) "দৌড় স্টপওয়াচ ও পেস ক্যালকুলেটর (PET)" else "PET Stopwatch & Pace Calculator",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isBn)
                                        "টার্গেট: ${targetDistanceMeters}m দৌড় ${qualifyingSeconds / 60}m ${qualifyingSeconds % 60}s-এ"
                                    else
                                        "Target: ${targetDistanceMeters}m Run in ${qualifyingSeconds / 60}m ${qualifyingSeconds % 60}s",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val swTotalSec = elapsedDeciseconds / 10
                    val swMin = swTotalSec / 60
                    val swSec = swTotalSec % 60
                    val swDeci = elapsedDeciseconds % 10

                    Text(
                        text = String.format(Locale.US, "%02d:%02d.%d", swMin, swSec, swDeci),
                        style = MaterialTheme.typography.headlineLarge,
                        color = if (swTotalSec <= qualifyingSeconds) EmeraldSuccess else CrimsonWrong,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { isRunning = !isRunning },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pet_stopwatch_start_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRunning) CrimsonWrong else MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isRunning) (if (isBn) "থামান" else "Pause") else (if (isBn) "শুরু করুন" else "Start Run"))
                        }

                        OutlinedButton(
                            onClick = {
                                if (elapsedDeciseconds > 0) {
                                    lapTimes.add(elapsedDeciseconds / 10)
                                }
                            },
                            enabled = isRunning,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("400m Lap")
                        }

                        OutlinedButton(
                            onClick = {
                                isRunning = false
                                elapsedDeciseconds = 0
                                lapTimes.clear()
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                        }
                    }

                    if (lapTimes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Laps: " + lapTimes.mapIndexed { i, s -> "L${i + 1}: ${s / 60}m ${s % 60}s" }.joinToString(" • "),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isBn) "অথবা আপনার দৌড়ের সময় বসিয়ে যোগ্যতা যাচাই করুন:" else "Or enter run time to check qualifying status:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = manualMinutesInput,
                            onValueChange = { manualMinutesInput = it },
                            modifier = Modifier.weight(1f),
                            label = { Text(if (isBn) "মিনিট (Minutes)" else "Minutes") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = manualSecondsInput,
                            onValueChange = { manualSecondsInput = it },
                            modifier = Modifier.weight(1f),
                            label = { Text(if (isBn) "সেকেন্ড (Seconds)" else "Seconds") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val diffSeconds = qualifyingSeconds - activeTotalSeconds
                    Surface(
                        color = if (isWithinQualifyingTime) EmeraldSuccess.copy(alpha = 0.12f) else CrimsonWrong.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isWithinQualifyingTime) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (isWithinQualifyingTime) EmeraldSuccess else CrimsonWrong,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isWithinQualifyingTime) {
                                        if (isBn) "কোয়ালিফাইং সময়ের মধ্যে আছেন (${diffSeconds}s হাতে আছে)"
                                        else "QUALIFIED! ${diffSeconds}s ahead of cutoff!"
                                    } else {
                                        if (isBn) "আরও ${-diffSeconds} সেকেন্ড কমাতে হবে।"
                                        else "Trim ${-diffSeconds} seconds to qualify."
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isWithinQualifyingTime) EmeraldSuccess else CrimsonWrong
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBn)
                                    "পেস: $paceFormatted • প্রতি ৪০০ মিটার ল্যাপ: ${activeTotalSeconds / (targetDistanceMeters / 400).coerceAtLeast(1)}s"
                                else
                                    "Pace: $paceFormatted • Avg 400m Lap: ${activeTotalSeconds / (targetDistanceMeters / 400).coerceAtLeast(1)}s",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onSaveRun(
                                selectedPost.labelEn,
                                selectedGender.labelEn,
                                targetDistanceMeters,
                                activeTotalSeconds,
                                qualifyingSeconds,
                                isWithinQualifyingTime,
                                paceFormatted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("save_pet_run_button"),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBn) "দৌড়ের রেকর্ড সেভ করুন (Save PET Log)" else "Save PET Run Record")
                    }
                }
            }
        }

        // Saved PET Run History
        if (petRunLogs.isNotEmpty()) {
            item {
                Text(
                    text = if (isBn) "সংরক্ষিত মাঠ লগ (SAVED PET LOGS)" else "SAVED PET LOGS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
            }
            items(petRunLogs, key = { it.id }) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${log.distanceMeters}m • ${log.timeSeconds / 60}m ${log.timeSeconds % 60}s (${log.pacePerKm})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (log.isQualified) EmeraldSuccess else CrimsonWrong
                            )
                            Text(
                                text = "${log.postName} • ${log.gender} • ${if (log.isQualified) "QUALIFIED" else "Needs Practice"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDeleteRun(log.id) }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Log",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
