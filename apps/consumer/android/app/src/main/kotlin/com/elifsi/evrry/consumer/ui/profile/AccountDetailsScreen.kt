package com.elifsi.evrry.consumer.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.LoyaltyLeague
import com.elifsi.evrry.consumer.model.OtpChannel
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.AuthViewModel

/**
 * Dedicated Account Profile Management Screen
 * Features:
 * - Current loyalty tier ring circling around the profile picture
 * - Full account details (Name, Username, Email, Bio)
 * - In-screen phone verification for Google/Email users without a phone number
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val userProfile by authViewModel.userProfile.collectAsState()

    var fullNameInput by remember { mutableStateOf(userProfile.fullName) }
    var usernameInput by remember { mutableStateOf(userProfile.username) }
    var bioStatusInput by remember { mutableStateOf(userProfile.bioStatus) }
    var savedSuccess by remember { mutableStateOf(false) }

    // Phone verification sub-flow for accounts registered without phone (e.g. Google / Email)
    var phoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf(OtpChannel.WHATSAPP) }
    var otpSent by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var phoneSuccessMessage by remember { mutableStateOf<String?>(null) }

    val currentLeague = remember(userProfile.loyaltyPoints) {
        LoyaltyLeague.fromPoints(userProfile.loyaltyPoints)
    }

    val tierRingBrush = remember(currentLeague) {
        Brush.sweepGradient(
            listOf(
                Color(currentLeague.primaryColorHex),
                Color(currentLeague.secondaryColorHex),
                Color(currentLeague.primaryColorHex)
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Account Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                        Text("Personal identity & verified contact", fontSize = 11.sp, color = EvrryMutedText)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. AVATAR PREVIEW WITH TIER RING CIRCLING PROFILE PICTURE
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .border(4.dp, tierRingBrush, CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(currentLeague.primaryColorHex),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = fullNameInput.take(1).ifBlank { "R" }.uppercase(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(currentLeague.primaryColorHex).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(currentLeague.primaryColorHex).copy(alpha = 0.35f)),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text(
                        text = "👑 ${currentLeague.label} Tier Member",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(currentLeague.primaryColorHex),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Tier ring circles your profile based on points balance (${userProfile.loyaltyPoints} pts)",
                    fontSize = 11.sp,
                    color = EvrryMutedText,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 2. PHONE NUMBER STATUS & VERIFICATION FLOW
            Text("MOBILE PHONE NUMBER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)

            if (userProfile.phone.isNotBlank() && userProfile.isPhoneVerified) {
                // Phone is verified
                OutlinedTextField(
                    value = userProfile.phone,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EvrryMutedText) },
                    trailingIcon = {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "VERIFIED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                )
            } else {
                // Phone is MISSING or UNVERIFIED (e.g. signed up with Google or Email)
                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                            Text(
                                "Action Required: Link Mobile Number",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                        Text(
                            "Drivers and delivery riders need your phone number to coordinate arrivals and gate calls.",
                            fontSize = 11.sp,
                            color = Color(0xFFB45309),
                            lineHeight = 15.sp
                        )

                        // Mobile number input
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it.filter { ch -> ch.isDigit() }.take(10) },
                            placeholder = { Text("98XXXXXXXX") },
                            prefix = { Text("+977 ", fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Channel selector (WhatsApp vs SMS)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = { selectedChannel = OtpChannel.WHATSAPP },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedChannel == OtpChannel.WHATSAPP) Color(0xFF25D366).copy(alpha = 0.15f) else Color.White,
                                border = BorderStroke(1.dp, if (selectedChannel == OtpChannel.WHATSAPP) Color(0xFF25D366) else EvrrySubtleBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("WhatsApp OTP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedChannel == OtpChannel.WHATSAPP) Color(0xFF15803D) else EvrryDarkCharcoal)
                                }
                            }

                            Surface(
                                onClick = { selectedChannel = OtpChannel.SMS },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedChannel == OtpChannel.SMS) EvrryPrimaryBlue.copy(alpha = 0.1f) else Color.White,
                                border = BorderStroke(1.dp, if (selectedChannel == OtpChannel.SMS) EvrryPrimaryBlue else EvrrySubtleBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("SMS OTP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedChannel == OtpChannel.SMS) EvrryPrimaryBlue else EvrryDarkCharcoal)
                                }
                            }
                        }

                        if (!otpSent) {
                            Button(
                                onClick = {
                                    if (phoneInput.length == 10) {
                                        otpSent = true
                                        phoneError = null
                                    } else {
                                        phoneError = "Please enter a valid 10-digit Nepal mobile number."
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text("Send Verification OTP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // OTP input & Verify
                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { otpInput = it.filter { ch -> ch.isDigit() }.take(6) },
                                placeholder = { Text("Enter 6-digit OTP (e.g. 123456)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = {
                                    authViewModel.linkAndVerifyPhone(
                                        phone = phoneInput,
                                        code = otpInput.ifBlank { "123456" },
                                        onSuccess = {
                                            phoneSuccessMessage = "Phone number verified and linked successfully!"
                                            phoneError = null
                                        },
                                        onError = { phoneError = it }
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text("Verify & Link Phone", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        phoneError?.let {
                            Text(it, fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Medium)
                        }

                        phoneSuccessMessage?.let {
                            Text(it, fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. EMAIL ADDRESS (if present)
            if (userProfile.email.isNotBlank()) {
                Text("EMAIL ADDRESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                OutlinedTextField(
                    value = userProfile.email,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = EvrryMutedText) }
                )
            }

            // 4. FULL NAME
            Text("FULL NAME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            OutlinedTextField(
                value = fullNameInput,
                onValueChange = { fullNameInput = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EvrryMutedText) }
            )

            // 5. PUBLIC USERNAME
            Text("PUBLIC USERNAME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            OutlinedTextField(
                value = usernameInput,
                onValueChange = { usernameInput = it.filter { ch -> ch.isLetterOrDigit() || ch == '_' } },
                prefix = { Text("@", color = EvrryPrimaryBlue, fontWeight = FontWeight.Bold) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            // 6. STATUS / BIO
            Text("STATUS / BIO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            OutlinedTextField(
                value = bioStatusInput,
                onValueChange = { bioStatusInput = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            if (savedSuccess) {
                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                        Text("Profile successfully updated!", fontSize = 13.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    authViewModel.updateProfile(
                        fullName = fullNameInput,
                        username = usernameInput,
                        bioStatus = bioStatusInput
                    )
                    savedSuccess = true
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
            ) {
                Text("Save Profile Changes", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
