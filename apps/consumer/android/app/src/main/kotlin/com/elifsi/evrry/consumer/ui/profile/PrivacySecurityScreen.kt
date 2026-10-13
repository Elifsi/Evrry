package com.elifsi.evrry.consumer.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.AuthViewModel

/**
 * Dedicated Privacy & Security Management Screen
 * Features:
 * - Signal-Grade Double Ratchet E2EE verification
 * - Change Password (Current Password, New Password, Confirm Password, Forgot Password button)
 * - WhatsApp-Grade Google Drive Chat & Media Backup (Back Up Now, Frequency, Include Videos)
 * - Discovery, Read Receipts & Last-Seen visibility
 * - 30-Day Account Deletion Schedule
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val userProfile by authViewModel.userProfile.collectAsState()
    val chatBackupState by authViewModel.chatBackupState.collectAsState()

    var allowPhoneDiscovery by remember { mutableStateOf(userProfile.allowPhoneDiscovery) }
    var readReceiptsEnabled by remember { mutableStateOf(userProfile.readReceiptsEnabled) }
    var lastSeenOption by remember { mutableStateOf(userProfile.lastSeenPrivacy) }

    // Change Password Form State
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var passwordMessage by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    // Frequency Dropdown
    var showFrequencyMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Privacy & Security", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                        Text("Password, backup & encryption settings", fontSize = 11.sp, color = EvrryMutedText)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = EvrryDarkCharcoal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF9FBFF))
            )
        },
        containerColor = Color(0xFFF9FBFF)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            // 1. E2EE STATUS BANNER
            item {
                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(18.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFF10B981), modifier = Modifier.size(40.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                        }
                        Column {
                            Text("Signal-Grade Double Ratchet E2EE", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF065F46))
                            Text("Messages, snaps, voice notes & VoIP calls are encrypted peer-to-peer directly on device.", fontSize = 11.sp, color = Color(0xFF047857), lineHeight = 15.sp)
                        }
                    }
                }
            }

            // 2. CHANGE PASSWORD SECTION
            item {
                Text("ACCOUNT SECURITY & PASSWORD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            }

            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, EvrrySubtleBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(20.dp))
                            Text("Change Password", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EvrryDarkCharcoal)
                        }

                        // Current Password
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text("Current Password") },
                            singleLine = true,
                            visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                                    Icon(
                                        imageVector = if (showCurrentPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = EvrryMutedText
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Forgot Password Button
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Text(
                                text = "Forgot Password?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryPrimaryBlue,
                                modifier = Modifier.clickable { showForgotPasswordDialog = true }
                            )
                        }

                        // New Password
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New Password (min 6 characters)") },
                            singleLine = true,
                            visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { showNewPassword = !showNewPassword }) {
                                    Icon(
                                        imageVector = if (showNewPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = EvrryMutedText
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Confirm Password
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm New Password") },
                            singleLine = true,
                            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                    Icon(
                                        imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = EvrryMutedText
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        passwordMessage?.let { (success, msg) ->
                            Text(
                                text = msg,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (success) Color(0xFF16A34A) else Color(0xFFDC2626)
                            )
                        }

                        Button(
                            onClick = {
                                authViewModel.changePassword(
                                    current = currentPassword,
                                    new = newPassword,
                                    confirm = confirmPassword
                                ) { success, msg ->
                                    passwordMessage = success to msg
                                    if (success) {
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text("Update Password", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // 3. WHATSAPP-GRADE CHAT & MEDIA BACKUP (GOOGLE DRIVE)
            item {
                Text("CHAT & MEDIA BACKUP (GOOGLE DRIVE)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            }

            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, EvrrySubtleBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Backup Header Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFEFF6FF),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(22.dp))
                                    }
                                }
                                Column {
                                    Text("Google Drive Backup", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EvrryDarkCharcoal)
                                    Text("Last backup: ${chatBackupState.lastBackupTimestamp}", fontSize = 11.sp, color = EvrryMutedText)
                                    Text("Size: ${chatBackupState.totalSizeMb} MB", fontSize = 11.sp, color = EvrryMutedText)
                                }
                            }

                            // E2EE shield badge
                            Surface(
                                color = Color(0xFFECFDF5),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(11.dp))
                                    Text("E2EE Encrypted", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                }
                            }
                        }

                        // Progress Indicator if backing up
                        AnimatedVisibility(visible = chatBackupState.isBackingUp) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                LinearProgressIndicator(
                                    progress = { chatBackupState.backupProgressPercent / 100f },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = EvrryPrimaryBlue
                                )
                                Text(
                                    text = "Backing up chats to Google Drive... ${chatBackupState.backupProgressPercent}%",
                                    fontSize = 11.sp,
                                    color = EvrryPrimaryBlue
                                )
                            }
                        }

                        // Back Up Now Button
                        Button(
                            onClick = { authViewModel.startChatBackup() },
                            enabled = !chatBackupState.isBackingUp,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (chatBackupState.isBackingUp) "Backing Up..." else "Back Up Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        HorizontalDivider(color = EvrrySubtleBorder)

                        // Google Account
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Google Account", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                                Text(chatBackupState.googleDriveAccount, fontSize = 11.sp, color = EvrryMutedText)
                            }
                            Text("Linked", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF10B981))
                        }

                        HorizontalDivider(color = EvrrySubtleBorder)

                        // Backup Frequency
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Back up to Google Drive", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                                Text(chatBackupState.autoBackupFrequency, fontSize = 11.sp, color = EvrryPrimaryBlue, fontWeight = FontWeight.Bold)
                            }

                            Box {
                                TextButton(onClick = { showFrequencyMenu = true }) {
                                    Text("Change", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EvrryPrimaryBlue)
                                }
                                DropdownMenu(
                                    expanded = showFrequencyMenu,
                                    onDismissRequest = { showFrequencyMenu = false }
                                ) {
                                    listOf("Daily", "Weekly", "Monthly", "Off").forEach { freq ->
                                        DropdownMenuItem(
                                            text = { Text(freq) },
                                            onClick = {
                                                authViewModel.updateBackupFrequency(freq)
                                                showFrequencyMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = EvrrySubtleBorder)

                        // Include Videos Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Include Videos", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                                Text("${chatBackupState.videosSizeMb} MB of videos included in backup", fontSize = 11.sp, color = EvrryMutedText)
                            }
                            Switch(
                                checked = chatBackupState.includeVideos,
                                onCheckedChange = { authViewModel.updateIncludeVideos(it) }
                            )
                        }
                    }
                }
            }

            // 4. DISCOVERY & VISIBILITY
            item {
                Text("DISCOVERY & CONTACTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            }

            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, EvrrySubtleBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Find by Phone Number", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                                Text("Allow people who have your mobile number saved to find you on Evrry", fontSize = 11.sp, color = EvrryMutedText)
                            }
                            Switch(
                                checked = allowPhoneDiscovery,
                                onCheckedChange = {
                                    allowPhoneDiscovery = it
                                    authViewModel.updatePrivacySettings(it, readReceiptsEnabled, lastSeenOption)
                                }
                            )
                        }

                        HorizontalDivider(color = EvrrySubtleBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Read Receipts", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                                Text("Show blue ticks when you have read messages", fontSize = 11.sp, color = EvrryMutedText)
                            }
                            Switch(
                                checked = readReceiptsEnabled,
                                onCheckedChange = {
                                    readReceiptsEnabled = it
                                    authViewModel.updatePrivacySettings(allowPhoneDiscovery, it, lastSeenOption)
                                }
                            )
                        }
                    }
                }
            }

            // 5. LAST SEEN VISIBILITY
            item {
                Text("WHO CAN SEE MY LAST SEEN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            }

            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, EvrrySubtleBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Everyone", "My Connections", "Nobody").forEach { option ->
                            val isSelected = lastSeenOption == option
                            Surface(
                                onClick = {
                                    lastSeenOption = option
                                    authViewModel.updatePrivacySettings(allowPhoneDiscovery, readReceiptsEnabled, option)
                                },
                                color = if (isSelected) Color(0xFFEFF6FF) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) EvrryPrimaryBlue else EvrryDarkCharcoal
                                    )
                                    if (isSelected) {
                                        RadioButton(selected = true, onClick = null)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. DANGER ZONE - 30 DAYS ACCOUNT DELETION
            item {
                Text("DANGER ZONE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
            }

            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                            Text("Schedule Account Deletion (30 Days)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFDC2626))
                        }
                        Text(
                            text = "Industry-standard 30-day cooling-off period. Sign back in anytime within 30 days to immediately cancel deletion and restore your account.",
                            fontSize = 11.sp,
                            color = EvrryMutedText,
                            lineHeight = 15.sp
                        )
                        Button(
                            onClick = { authViewModel.deleteAccount() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text("Schedule Account Deletion", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // FORGOT PASSWORD DIALOG
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
            text = {
                Text("We will send a password reset verification link and OTP to your registered phone or email address.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.requestPasswordReset { msg ->
                            passwordMessage = true to msg
                        }
                        showForgotPasswordDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                ) {
                    Text("Send Reset Link & OTP")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
