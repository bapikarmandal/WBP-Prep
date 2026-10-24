package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.example.data.AspirantCasteCategory
import com.example.data.AspirantProfile
import com.example.data.AspirantTargetExam
import com.example.data.CloudDbConfig
import com.example.data.QuizQuestion
import com.example.data.ScanVerificationReport
import com.example.network.GeminiClient
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonWrong
import com.example.ui.theme.EmeraldSuccess
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 1. DUAL-MODE AUTHENTICATION SCREEN (Aspirant vs. Admin)
 * - Tab A: Aspirant (Student) Login & Sign-Up (Full Name, Gmail, Password, Caste/Category Dropdown, Target Exam Dropdown, Quick Demo Aspirant Login)
 * - Tab B: Admin Portal Login (Protected with Admin Email & Secret Passkey; default admin@wbpprep.com / admin123)
 */
@Composable
fun DualModeAuthScreen(
    authErrorMessage: String?,
    onClearError: () -> Unit,
    onLoginAspirant: (email: String, password: String) -> Unit,
    onSignUpAspirant: (
        fullName: String,
        email: String,
        password: String,
        casteCategory: AspirantCasteCategory,
        targetExam: AspirantTargetExam
    ) -> Unit,
    onQuickDemoAspirantLogin: () -> Unit,
    onLoginAdmin: (email: String, passkey: String) -> Unit,
    onSignInWithGoogleIdToken: ((
        idToken: String,
        displayName: String?,
        email: String?,
        casteCategory: AspirantCasteCategory,
        targetExam: AspirantTargetExam
    ) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    // 0 = Tab A: Aspirant Login & Sign-Up, 1 = Tab B: Admin Portal Login
    var selectedTopTab by remember { mutableIntStateOf(0) }
    var isAspirantSignUpMode by remember { mutableStateOf(false) }

    // Aspirant fields
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AspirantCasteCategory.GENERAL) }
    var selectedTargetExam by remember { mutableStateOf(AspirantTargetExam.WBP_CONSTABLE) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var targetExamDropdownExpanded by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    // Admin fields
    var adminEmail by remember { mutableStateOf("admin@wbpprep.com") }
    var adminPasskey by remember { mutableStateOf("") }
    var showAdminPasskey by remember { mutableStateOf(false) }

    val topSafeClearance = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()
        .coerceAtLeast(36.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = topSafeClearance, bottom = 24.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Brand Logo & App Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "WBP & KP Prep",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "WBP & KP Smart Exam Prep",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "WBPRB Constable & SI • Bengali Medium Exam Portal",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Dual-Mode Top Switcher Tabs: [🎓 Aspirant Portal] vs [🛠️ Admin Portal]
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                selectedTopTab = 0
                                onClearError()
                            }
                            .testTag("auth_tab_aspirant"),
                        color = if (selectedTopTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 11.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (selectedTopTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Aspirant (Student)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTopTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                selectedTopTab = 1
                                onClearError()
                            }
                            .testTag("auth_tab_admin"),
                        color = if (selectedTopTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 11.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = if (selectedTopTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Admin Portal",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTopTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Error Alert Banner
            if (!authErrorMessage.isNullOrBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_error_banner"),
                    color = CrimsonWrong.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CrimsonWrong.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = authErrorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = CrimsonWrong,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onClearError,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss error",
                                tint = CrimsonWrong,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (selectedTopTab == 0) {
                // TAB A: ASPIRANT (STUDENT) LOGIN & SIGN-UP CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Sub-mode switcher: [Aspirant Login] vs [New Aspirant Sign-Up]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isAspirantSignUpMode) "CREATE ASPIRANT ACCOUNT" else "ASPIRANT STUDENT LOGIN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 0.7.sp
                                )
                                Text(
                                    text = if (isAspirantSignUpMode)
                                        "Personalize Cut-Offs, Target Exam & PET Standards"
                                    else
                                        "Sign in with your registered Gmail & Password",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        isAspirantSignUpMode = !isAspirantSignUpMode
                                        onClearError()
                                    }
                                    .testTag("toggle_aspirant_signup_mode"),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isAspirantSignUpMode) "Have Account? Login" else "New? Sign Up",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                        if (isAspirantSignUpMode) {
                            // 1. Full Name
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("1. Full Name (e.g., Rahul Mondal)") },
                                placeholder = { Text("Enter your full name") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signup_full_name_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // 2. Gmail / Email Address
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = {
                                Text(
                                    if (isAspirantSignUpMode) "2. Gmail / Email Address"
                                    else "Registered Gmail / Email Address"
                                )
                            },
                            placeholder = { Text("rahul.mondal@gmail.com") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("aspirant_email_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // 3. Password
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = {
                                Text(
                                    if (isAspirantSignUpMode) "3. Password (min 4 characters)"
                                    else "Password"
                                )
                            },
                            placeholder = { Text("Enter password") },
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password visibility"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("aspirant_password_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (isAspirantSignUpMode) {
                            // 4. Caste / Category Dropdown
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { categoryDropdownExpanded = true }
                                        .testTag("signup_category_dropdown"),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "4. Caste / Category (Syncs Cut-Off & PMT/PET)",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = selectedCategory.displayLabel,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Select Category",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = categoryDropdownExpanded,
                                    onDismissRequest = { categoryDropdownExpanded = false }
                                ) {
                                    AspirantCasteCategory.entries.forEach { cat ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(
                                                        text = cat.displayLabel,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        text = "Safe Prelims Cut-Off: ${cat.safeCutoffOutOf85}/85" +
                                                            if (cat.isRelaxedHillSt) " • PMT Height/Chest Relaxed" else "",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedCategory = cat
                                                categoryDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // 5. Target Exam Dropdown
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { targetExamDropdownExpanded = true }
                                        .testTag("signup_target_exam_dropdown"),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "5. Target Exam (Syncs Dashboard & Mock Pattern)",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = selectedTargetExam.displayLabel,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Select Target Exam",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = targetExamDropdownExpanded,
                                    onDismissRequest = { targetExamDropdownExpanded = false }
                                ) {
                                    AspirantTargetExam.entries.forEach { target ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = target.displayLabel,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            },
                                            onClick = {
                                                selectedTargetExam = target
                                                targetExamDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Primary Submit CTA Button (Login or Sign-Up)
                        Button(
                            onClick = {
                                if (isAspirantSignUpMode) {
                                    onSignUpAspirant(
                                        fullName,
                                        email,
                                        password,
                                        selectedCategory,
                                        selectedTargetExam
                                    )
                                } else {
                                    onLoginAspirant(email, password)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("aspirant_submit_auth_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAspirantSignUpMode)
                                    "Create Aspirant Account & Start Prep"
                                else
                                    "Login as Aspirant",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Google Sign-In via Jetpack Credential Manager (Firebase Auth + Cloud Firestore)
                        if (onSignInWithGoogleIdToken != null) {
                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val webClientId = context.getString(R.string.default_web_client_id)
                                        try {
                                            val signInOption = GetSignInWithGoogleOption.Builder(webClientId).build()
                                            val request = GetCredentialRequest.Builder()
                                                .addCredentialOption(signInOption)
                                                .build()
                                            val result = credentialManager.getCredential(context, request)
                                            val cred = result.credential
                                            if (cred is CustomCredential && cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                                val tokenCred = GoogleIdTokenCredential.createFrom(cred.data)
                                                onSignInWithGoogleIdToken(
                                                    tokenCred.idToken,
                                                    tokenCred.displayName,
                                                    tokenCred.id,
                                                    selectedCategory,
                                                    selectedTargetExam
                                                )
                                            }
                                        } catch (e: GetCredentialCancellationException) {
                                            android.util.Log.w("Auth", "User cancelled Google Sign-In", e)
                                        } catch (e: Exception) {
                                            android.util.Log.e("Auth", "Sign-in failed", e)
                                            Toast.makeText(
                                                context,
                                                "Google Sign-In unavailable on this emulator; using Quick Demo Login.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onQuickDemoAspirantLogin()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("google_sign_in_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sign in with Google (Firebase Cloud Sync)",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Quick Demo Aspirant Login Button (1-Tap fast testing)
                        OutlinedButton(
                            onClick = onQuickDemoAspirantLogin,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("quick_demo_aspirant_login_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "⚡ Quick Demo Aspirant Login (Rahul Mondal • OBC-B)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldSuccess
                            )
                        }
                    }
                }
            } else {
                // TAB B: ADMIN PORTAL LOGIN (PROTECTED)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = AmberWarning.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = AmberWarning,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "PROTECTED ADMIN PORTAL LOGIN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberWarning,
                                    letterSpacing = 0.7.sp
                                )
                                Text(
                                    text = "Unlocks 1–50 Batch Studio, Question Explorer & Aspirant Directory",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                        OutlinedTextField(
                            value = adminEmail,
                            onValueChange = { adminEmail = it },
                            label = { Text("Admin Email Address") },
                            placeholder = { Text("admin@wbpprep.com") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_email_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = adminPasskey,
                            onValueChange = { adminPasskey = it },
                            label = { Text("Admin Secret Passkey") },
                            placeholder = { Text("Enter secret passkey (default: admin123)") },
                            singleLine = true,
                            visualTransformation = if (showAdminPasskey) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showAdminPasskey = !showAdminPasskey }) {
                                    Icon(
                                        imageVector = if (showAdminPasskey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle passkey visibility"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_passkey_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Helper note showing default credentials for testing
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Default Admin: admin@wbpprep.com",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Default Secret Passkey: admin123",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            adminEmail = "admin@wbpprep.com"
                                            adminPasskey = "admin123"
                                        }
                                        .testTag("fill_default_admin_button"),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Auto-Fill",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { onLoginAdmin(adminEmail, adminPasskey) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("admin_login_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Unlock Admin Control Panel",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 3. ASPIRANT PROFILE & AUTO-PERSONALIZATION MODAL
 * Displays the logged-in Aspirant's Target Exam badge, Category badge, test stats,
 * and allows updating their Full Name, Caste/Category, or Target Exam, or logging out.
 */
@Composable
fun AspirantProfileDialog(
    aspirant: AspirantProfile,
    onDismiss: () -> Unit,
    onUpdateProfile: (fullName: String, category: AspirantCasteCategory, targetExam: AspirantTargetExam) -> Unit,
    onSwitchToAdminOneTap: (() -> Unit)? = null,
    onLogout: () -> Unit
) {
    var fullName by remember(aspirant) { mutableStateOf(aspirant.fullName) }
    var selectedCategory by remember(aspirant) { mutableStateOf(aspirant.resolvedCasteCategory) }
    var selectedTargetExam by remember(aspirant) { mutableStateOf(aspirant.resolvedTargetExam) }
    var catExpanded by remember { mutableStateOf(false) }
    var examExpanded by remember { mutableStateOf(false) }

    val joinDateStr = remember(aspirant.joinTimestamp) {
        SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(aspirant.joinTimestamp))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 460.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = aspirant.fullName.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Column {
                            Text(
                                text = aspirant.fullName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${aspirant.email} • Joined $joinDateStr",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Live Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${aspirant.testsAttempted}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Tests Attempted",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.1f", aspirant.averageScore),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                            Text(
                                text = "Average Score",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Surface(
                        color = AmberWarning.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${selectedCategory.safeCutoffOutOf85}/85",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmberWarning
                            )
                            Text(
                                text = "${selectedCategory.shortBadge} Safe Cut-Off",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Category Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { catExpanded = true },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Registered Caste / Category",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = selectedCategory.displayLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                        }
                    }
                    DropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        AspirantCasteCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayLabel) },
                                onClick = {
                                    selectedCategory = cat
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                // Target Exam Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { examExpanded = true },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Registered Target Exam",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = selectedTargetExam.displayLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                        }
                    }
                    DropdownMenu(
                        expanded = examExpanded,
                        onDismissRequest = { examExpanded = false }
                    ) {
                        AspirantTargetExam.entries.forEach { target ->
                            DropdownMenuItem(
                                text = { Text(target.displayLabel) },
                                onClick = {
                                    selectedTargetExam = target
                                    examExpanded = false
                                }
                            )
                        }
                    }
                }

                if (onSwitchToAdminOneTap != null) {
                    OutlinedButton(
                        onClick = onSwitchToAdminOneTap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("profile_switch_to_admin_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🛠️ 1-Tap Switch to Admin Studio (Testing)",
                            color = AmberWarning,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onLogout()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("aspirant_logout_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = CrimsonWrong,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Log Out",
                            color = CrimsonWrong,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            onUpdateProfile(fullName, selectedCategory, selectedTargetExam)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("aspirant_save_profile_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Save Profile",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 4. IN-APP "ADMIN DATABASE CONTROL CENTER" (Where Admin Accesses All Data):
 * Provides 3 clean management sub-tabs:
 * 1. `➕ Add Questions Studio (1–50 Batch)`
 * 2. `🗂️ Question Bank Explorer`
 * 3. `👥 Registered Aspirants Directory`
 * Plus `☁️ Cloud DB Settings` & Admin Passkey Manager.
 */
@Composable
fun AdminControlPanelScreen(
    questionVault: List<QuizQuestion>,
    registeredAspirants: List<AspirantProfile>,
    cloudDbConfig: CloudDbConfig,
    isSyncingCloud: Boolean,
    adminPasskey: String,
    isImportingPyqBook: Boolean,
    scannerCurrentPage: Int,
    scannerTotalPages: Int,
    scannerAddedCount: Int,
    scannerExtractedCount: Int,
    scannerStep: Int,
    scannerErrorMessage: String?,
    scannerVerificationReport: ScanVerificationReport?,
    scannerToastMessage: String?,
    isGeneratingExplanation: Boolean,
    onClearScannerToast: () -> Unit,
    onClearScannerError: () -> Unit,
    onNotifyScannerInputLoaded: () -> Unit,
    onUpdateUploadedQuestion: (QuizQuestion) -> Unit,
    onDeleteUploadedQuestion: (String) -> Unit,
    onStartUploadedQuestionsTest: (List<QuizQuestion>) -> Unit,
    onImportPyqBookContent: (String, String?, String?, String) -> Unit,
    onScanSmartBatch: (String, List<GeminiClient.ScannerInlineAttachment>, Int, String?) -> Unit,
    onSaveManualBatch: (List<QuizQuestion>) -> Unit,
    onAutoGenerateExplanation: (String, String, String, List<String>, Int, (String) -> Unit) -> Unit,
    onExportDatabaseJson: ((String) -> Unit) -> Unit,
    onImportDatabaseJson: (String) -> Unit,
    onSaveCloudConfig: (CloudDbConfig) -> Unit,
    onTriggerCloudSync: () -> Unit,
    onUpdateAdminPasskey: (String) -> Unit,
    onDeleteAspirant: (String) -> Unit,
    onLogoutAdmin: () -> Unit
) {
    val context = LocalContext.current
    // 0 = ➕ Add Questions Studio (1–50 Batch), 1 = 🗂️ Question Bank Explorer, 2 = 👥 Registered Aspirants Directory
    var activeSubTab by remember { mutableIntStateOf(0) }
    var showStudioModal by remember { mutableStateOf(false) }
    var showCloudSettingsModal by remember { mutableStateOf(false) }
    var aspirantSearchQuery by remember { mutableStateOf("") }

    var pendingExportJson by remember { mutableStateOf<String?>(null) }
    val exportDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val json = pendingExportJson
        if (uri != null && !json.isNullOrBlank()) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(json.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "✅ Full Database (.json) exported successfully!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "⚠️ Export error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val importDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val rawJson = context.contentResolver.openInputStream(uri)?.use {
                    it.bufferedReader().readText()
                }.orEmpty()
                if (rawJson.isNotBlank()) {
                    onImportDatabaseJson(rawJson)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "⚠️ Failed to read JSON file: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    if (showStudioModal) {
        PyqBookImporterDialog(
            isBn = false,
            isImporting = isImportingPyqBook,
            scannerCurrentPage = scannerCurrentPage,
            scannerTotalPages = scannerTotalPages,
            scannerAddedCount = scannerAddedCount,
            scannerExtractedCount = scannerExtractedCount,
            scannerStep = scannerStep,
            scannerErrorMessage = scannerErrorMessage,
            scannerVerificationReport = scannerVerificationReport,
            scannerToastMessage = scannerToastMessage,
            questionVault = questionVault,
            isGeneratingExplanation = isGeneratingExplanation,
            onClearToastMessage = onClearScannerToast,
            onClearErrorMessage = onClearScannerError,
            onNotifyInputLoaded = onNotifyScannerInputLoaded,
            onUpdateUploadedQuestion = onUpdateUploadedQuestion,
            onDeleteUploadedQuestion = onDeleteUploadedQuestion,
            onStartUploadedQuestionsTest = onStartUploadedQuestionsTest,
            onDismiss = { showStudioModal = false },
            onImportBookContent = onImportPyqBookContent,
            onScanSmartBatch = onScanSmartBatch,
            onSaveManualBatch = onSaveManualBatch,
            onAutoGenerateExplanation = onAutoGenerateExplanation,
            onExportDatabaseJson = onExportDatabaseJson,
            onImportDatabaseJson = onImportDatabaseJson
        )
    }

    if (showCloudSettingsModal) {
        CloudDatabaseSettingsDialog(
            currentConfig = cloudDbConfig,
            currentAdminPasskey = adminPasskey,
            isSyncingCloud = isSyncingCloud,
            onDismiss = { showCloudSettingsModal = false },
            onSaveCloudConfig = onSaveCloudConfig,
            onTriggerCloudSync = onTriggerCloudSync,
            onUpdateAdminPasskey = onUpdateAdminPasskey
        )
    }

    val gkCount = remember(questionVault) { questionVault.count { it.subject.equals("GK", ignoreCase = true) } }
    val mathCount = remember(questionVault) { questionVault.count { it.subject.equals("MATH", ignoreCase = true) } }
    val reasCount = remember(questionVault) { questionVault.count { it.subject.equals("REASONING", ignoreCase = true) } }
    val engCount = remember(questionVault) { questionVault.count { it.subject.equals("ENGLISH", ignoreCase = true) } }
    val pyqCount = remember(questionVault) { questionVault.count { it.effectiveIsPyq() } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Admin Control Center Summary Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_control_center_header"),
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = AmberWarning.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "🛠️ ADMIN MODE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberWarning,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Surface(
                                    color = if (cloudDbConfig.isCloudConfigured)
                                        EmeraldSuccess.copy(alpha = 0.14f)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (cloudDbConfig.isCloudConfigured)
                                            "☁️ Cloud Sync Active"
                                        else
                                            "💾 Local SQLite + JSON",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (cloudDbConfig.isCloudConfigured)
                                            EmeraldSuccess
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Admin Database Control Center",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { showCloudSettingsModal = true }
                                    .testTag("open_cloud_db_settings_button"),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudSync,
                                        contentDescription = "Cloud DB Settings",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "☁️ Cloud DB",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onLogoutAdmin() }
                                    .testTag("admin_logout_button"),
                                color = CrimsonWrong.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = "Logout Admin",
                                        tint = CrimsonWrong,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Live Database KPI Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminMetricBox(
                            modifier = Modifier.weight(1f),
                            value = "${questionVault.size}",
                            label = "Total Questions",
                            accent = MaterialTheme.colorScheme.primary
                        )
                        AdminMetricBox(
                            modifier = Modifier.weight(1f),
                            value = "$pyqCount",
                            label = "Tagged PYQs",
                            accent = AmberWarning
                        )
                        AdminMetricBox(
                            modifier = Modifier.weight(1f),
                            value = "${registeredAspirants.size}",
                            label = "Aspirants",
                            accent = EmeraldSuccess
                        )
                    }

                    // 1-Click Full Database Export / Import (.json) Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onExportDatabaseJson { jsonStr ->
                                    pendingExportJson = jsonStr
                                    exportDocumentLauncher.launch("wbp_full_database_backup.json")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("admin_export_full_db_button"),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "📤 Export Full DB (.json)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = { importDocumentLauncher.launch("application/json") },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("admin_import_full_db_button"),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "📥 Import DB (.json)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 3 Management Sub-Tabs Selector Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val subTabs = listOf(
                    Triple(0, "➕ Add Questions (1–50 Batch)", "admin_subtab_add_studio"),
                    Triple(1, "🗂️ Question Database Explorer (${questionVault.size})", "admin_subtab_explorer"),
                    Triple(2, "👥 Registered Aspirants (${registeredAspirants.size})", "admin_subtab_aspirants")
                )
                subTabs.forEach { (idx, label, tag) ->
                    val isSelected = activeSubTab == idx
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { activeSubTab = idx }
                            .testTag(tag),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(50)
                                )
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        )
                    }
                }
            }
        }

        when (activeSubTab) {
            0 -> {
                // SUB-TAB 1: ➕ ADD QUESTIONS STUDIO (1–50 BATCH)
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
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "MANUAL BATCH QUESTION CREATOR (1 TO 50 QUESTIONS AT ONCE)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.7.sp
                            )
                            Text(
                                text = "Add 1 to 50 structured Bengali/English questions with sticky Subject, Chapter (55 official chapters), PYQ Exam & Year (2005–2026) tags, Reasoning Question/Option diagram upload, and 1-Click AI Bengali Shortcut generator.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Subject-Wise Question Bank Breakdown:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "• GK (8 Chapters): $gkCount Qs   • Math (18 Chapters): $mathCount Qs\n• Reasoning (20 Chapters): $reasCount Qs   • English (9 Chapters): $engCount Qs",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { showStudioModal = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("admin_launch_batch_studio_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircleOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "➕ Launch 1–50 Batch Question Studio",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // SUB-TAB 2: 🗂️ QUESTION BANK EXPLORER
                item {
                    MyUploadedQuestionsVaultSection(
                        isBn = false,
                        uploadedQuestions = questionVault,
                        onStartUploadedTest = onStartUploadedQuestionsTest,
                        onOpenScannerModal = { showStudioModal = true },
                        onUpdateUploadedQuestion = onUpdateUploadedQuestion,
                        onDeleteUploadedQuestion = onDeleteUploadedQuestion,
                        onExportDatabaseJson = onExportDatabaseJson,
                        onImportDatabaseJson = onImportDatabaseJson,
                        initiallyExpandedList = true
                    )
                }
            }

            2 -> {
                // SUB-TAB 3: 👥 REGISTERED ASPIRANTS DIRECTORY
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_aspirants_directory_card"),
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
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "REGISTERED ASPIRANTS DIRECTORY (${registeredAspirants.size} USERS)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 0.7.sp
                                    )
                                    Text(
                                        text = "Real-time directory of signed-up students, target exams & test scores",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = aspirantSearchQuery,
                                onValueChange = { aspirantSearchQuery = it },
                                label = { Text("Search Aspirant by Name, Gmail, Category or Exam") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_aspirant_search_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            val filteredAspirants = remember(registeredAspirants, aspirantSearchQuery) {
                                val q = aspirantSearchQuery.trim().lowercase()
                                if (q.isEmpty()) registeredAspirants
                                else registeredAspirants.filter {
                                    it.fullName.lowercase().contains(q) ||
                                        it.email.lowercase().contains(q) ||
                                        it.casteCategory.lowercase().contains(q) ||
                                        it.targetExam.lowercase().contains(q)
                                }
                            }

                            filteredAspirants.forEachIndexed { idx, user ->
                                AspirantDirectoryRowCard(
                                    index = idx + 1,
                                    aspirant = user,
                                    onDelete = { onDeleteAspirant(user.email) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMetricBox(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    accent: Color
) {
    Surface(
        modifier = modifier,
        color = accent.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accent
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AspirantDirectoryRowCard(
    index: Int,
    aspirant: AspirantProfile,
    onDelete: () -> Unit
) {
    val joinDateStr = remember(aspirant.joinTimestamp) {
        SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(aspirant.joinTimestamp))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("aspirant_row_${aspirant.email}"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
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
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "#$index",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = aspirant.fullName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = aspirant.email,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remove user",
                        tint = CrimsonWrong,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Badges Row: Caste/Category + Target Exam + Join Date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = AmberWarning.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Category: ${aspirant.resolvedCasteCategory.shortBadge}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AmberWarning,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = aspirant.resolvedTargetExam.shortBadge,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = EmeraldSuccess.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Tests: ${aspirant.testsAttempted} • Avg: ${String.format(Locale.US, "%.1f", aspirant.averageScore)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "Joined $joinDateStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 5. CLOUD DATABASE CONNECT & ADMIN PASSKEY SETTINGS MODAL (`☁️ Cloud DB Settings` inside Admin Panel)
 */
@Composable
fun CloudDatabaseSettingsDialog(
    currentConfig: CloudDbConfig,
    currentAdminPasskey: String,
    isSyncingCloud: Boolean,
    onDismiss: () -> Unit,
    onSaveCloudConfig: (CloudDbConfig) -> Unit,
    onTriggerCloudSync: () -> Unit,
    onUpdateAdminPasskey: (String) -> Unit
) {
    var provider by remember(currentConfig) { mutableStateOf(currentConfig.provider) }
    var firebaseJson by remember(currentConfig) { mutableStateOf(currentConfig.firebaseConfigJson) }
    var supabaseUrl by remember(currentConfig) { mutableStateOf(currentConfig.supabaseUrl) }
    var supabaseKey by remember(currentConfig) { mutableStateOf(currentConfig.supabaseAnonKey) }
    var autoSync by remember(currentConfig) { mutableStateOf(currentConfig.autoSyncEnabled) }
    var newPasskey by remember(currentAdminPasskey) { mutableStateOf(currentAdminPasskey) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 500.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                    .verticalScroll(rememberScrollState())
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
                            text = "☁️ CLOUD DB SYNC & ADMIN SECURITY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.7.sp
                        )
                        Text(
                            text = "Unified Local SQLite + Optional Firebase / Supabase Sync",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Provider Selector Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val providers = listOf(
                        "LOCAL_ONLY" to "💾 Local SQLite",
                        "FIREBASE_FIRESTORE" to "🔥 Firebase JSON",
                        "SUPABASE" to "⚡ Supabase"
                    )
                    providers.forEach { (code, label) ->
                        val isSel = provider == code
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { provider = code }
                                .testTag("cloud_provider_$code"),
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                if (provider == "FIREBASE_FIRESTORE") {
                    OutlinedTextField(
                        value = firebaseJson,
                        onValueChange = { firebaseJson = it },
                        label = { Text("Paste Firebase Firestore Config JSON") },
                        placeholder = {
                            Text("{\"projectId\": \"wbp-prep-cloud\", \"apiKey\": \"...\", \"appId\": \"...\"}")
                        },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("firebase_config_json_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else if (provider == "SUPABASE") {
                    OutlinedTextField(
                        value = supabaseUrl,
                        onValueChange = { supabaseUrl = it },
                        label = { Text("Supabase Project URL") },
                        placeholder = { Text("https://xyzcompany.supabase.co") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("supabase_url_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = supabaseKey,
                        onValueChange = { supabaseKey = it },
                        label = { Text("Supabase Anon Key") },
                        placeholder = { Text("eyJhbGciOiJIUzI1NiIsInR5cCI6...") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("supabase_anon_key_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✅ Default Local Mode Active: All questions, registered aspirants, and test attempts are persisted automatically in Room SQLite + local JSON backup.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldSuccess,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto-Sync New Questions & Aspirants",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Automatically syncs batches & sign-ups when Cloud Config is saved",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = autoSync,
                        onCheckedChange = { autoSync = it }
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                // Admin Secret Passkey Change Section
                Text(
                    text = "🔑 CHANGE ADMIN SECRET PASSKEY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AmberWarning
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newPasskey,
                        onValueChange = { newPasskey = it },
                        label = { Text("Admin Passkey") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("change_admin_passkey_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = { onUpdateAdminPasskey(newPasskey) },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("save_admin_passkey_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Update Key", fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onTriggerCloudSync,
                        enabled = !isSyncingCloud,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSyncingCloud) {
                            CircularProgressIndicator(modifier = Modifier.size(15.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text("🔄 Sync Now", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            onSaveCloudConfig(
                                currentConfig.copy(
                                    provider = provider,
                                    firebaseConfigJson = firebaseJson.trim(),
                                    supabaseUrl = supabaseUrl.trim(),
                                    supabaseAnonKey = supabaseKey.trim(),
                                    autoSyncEnabled = autoSync,
                                    lastSyncedTimestamp = System.currentTimeMillis()
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("save_cloud_config_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Settings", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
