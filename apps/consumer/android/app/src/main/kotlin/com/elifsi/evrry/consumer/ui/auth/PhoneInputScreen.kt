package com.elifsi.evrry.consumer.ui.auth

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
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.OtpChannel
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.AuthViewModel

/**
 * Mobile Phone Login Screen
 * Nepal Mobile Number (+977) with WhatsApp & SMS Channels
 * + 1-Tap Social Login (Google, Apple, Email)
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@Composable
fun PhoneInputScreen(
    viewModel: AuthViewModel,
    onContinue: () -> Unit
) {
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val selectedChannel by viewModel.selectedChannel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showEmailDialog by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))

            // Brand Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = EvrryPrimaryBlue,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("e", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                }
                Text("evrry", fontWeight = FontWeight.Black, fontSize = 24.sp, color = EvrryDarkCharcoal)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to evrry",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = EvrryDarkCharcoal
            )
            Text(
                text = "Enter your Nepal mobile number to sign in or register your account.",
                fontSize = 13.sp,
                color = EvrryMutedText,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Phone Input Field with Nepal Flag 🇳🇵 +977
            Text(
                text = "MOBILE NUMBER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryMutedText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.5.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🇳🇵 +977",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = EvrryDarkCharcoal,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(EvrrySubtleBorder)
                )
                Spacer(modifier = Modifier.width(10.dp))
                TextField(
                    value = phoneNumber,
                    onValueChange = { viewModel.updatePhoneNumber(it) },
                    placeholder = { Text("98XXXXXXXX", color = Color(0xFF94A3B8)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // OTP Channel Selection (WhatsApp vs SMS)
            Text(
                text = "VERIFICATION CHANNEL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryMutedText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OtpChannel.values().forEach { channel ->
                    val isSelected = selectedChannel == channel
                    Surface(
                        color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) EvrryPrimaryBlue else EvrrySubtleBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.selectChannel(channel) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = channel.label,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = if (isSelected) EvrryPrimaryBlue else EvrryDarkCharcoal
                            )
                            Text(
                                text = channel.badge,
                                fontSize = 10.sp,
                                color = if (isSelected) EvrryPrimaryBlue.copy(alpha = 0.8f) else EvrryMutedText,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Phone Verification CTA
            Button(
                onClick = {
                    viewModel.requestOtp()
                    onContinue()
                },
                enabled = phoneNumber.length == 10 && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EvrryPrimaryBlue,
                    disabledContainerColor = EvrryPrimaryBlue.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Get Verification Code", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Divider: OR CONTINUE WITH
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = EvrrySubtleBorder)
                Text(
                    text = "OR CONTINUE WITH",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EvrryMutedText,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = EvrrySubtleBorder)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Social Sign-In Buttons (Google, Apple, Email)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Continue with Google
                OutlinedButton(
                    onClick = { viewModel.signInWithGoogle() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(22.dp),
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("G", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFF4285F4))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google",
                            color = EvrryDarkCharcoal,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Continue with Apple
                OutlinedButton(
                    onClick = { viewModel.signInWithApple() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0F172A))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Continue with Apple",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Continue with Email
                OutlinedButton(
                    onClick = { showEmailDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Email,
                            contentDescription = "Email",
                            tint = EvrryMutedText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Email",
                            color = EvrryDarkCharcoal,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Terms Footer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 8.dp)
        ) {
            Text(
                text = "By continuing, you agree to Evrry Terms & Privacy Policy.",
                fontSize = 11.sp,
                color = EvrryMutedText
            )
        }
    }

    // Email Sign-In Dialog
    if (showEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEmailDialog = false },
            title = {
                Text("Sign in with Email", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column {
                    Text(
                        "Enter your email address to sign in. A Nepal phone number will be linked for ride and delivery dispatches.",
                        fontSize = 13.sp,
                        color = EvrryMutedText
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        placeholder = { Text("name@example.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (emailInput.contains("@") && emailInput.contains(".")) {
                            viewModel.signInWithEmail(emailInput)
                            showEmailDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmailDialog = false }) {
                    Text("Cancel", color = EvrryMutedText)
                }
            }
        )
    }
}
