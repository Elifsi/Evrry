package com.elifsi.evrry.consumer.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.AuthScreenState
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

/**
 * 6-Digit WhatsApp OTP Verification Screen
 * Features:
 * - Auto-opening number keypad without glitches
 * - Back press closes keypad first, then navigates back on subsequent press
 * - Resend button directly beneath OTP boxes
 * - 1-minute countdown timer at bottom for resend on number
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OtpVerifyScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val otpCode by viewModel.otpCode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val targetPhone = (state as? AuthScreenState.OtpRequested)?.phone ?: "+977"
    val channelLabel = (state as? AuthScreenState.OtpRequested)?.channel?.label ?: "WhatsApp"

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isImeVisible = WindowInsets.isImeVisible

    // 1-minute (60 seconds) countdown timer
    var secondsRemaining by remember { mutableIntStateOf(60) }
    var timerTrigger by remember { mutableIntStateOf(0) }
    var resendToastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(timerTrigger) {
        secondsRemaining = 60
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
    }

    // Auto-open numeric keypad cleanly on launch
    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    // BACK PRESS HANDLER: If keyboard is open, close keyboard first. Otherwise pop screen.
    BackHandler {
        if (isImeVisible) {
            keyboardController?.hide()
            focusManager.clearFocus()
        } else {
            viewModel.resetToPhoneInput()
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // IN-APP BACK BUTTON (Top navigation)
            IconButton(
                onClick = {
                    keyboardController?.hide()
                    viewModel.resetToPhoneInput()
                    onBack()
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, EvrrySubtleBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = EvrryDarkCharcoal
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Enter 6-digit Code",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = EvrryDarkCharcoal
            )
            Text(
                text = "We sent a verification code to $targetPhone via $channelLabel.",
                fontSize = 13.sp,
                color = EvrryMutedText,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 6 PIN INPUT DISPLAY BOXES WITH INVISIBLE TEXT FIELD OVERLAY
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
            ) {
                // Visual Boxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 0 until 6) {
                        val char = otpCode.getOrNull(i)?.toString() ?: ""
                        val isCurrent = otpCode.length == i
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) EvrryPrimaryBlue else EvrrySubtleBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = char,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryDarkCharcoal,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Underlying Native BasicTextField for Keyboard Input
                BasicTextField(
                    value = otpCode,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }.take(6)
                        viewModel.updateOtp(digits)
                        if (digits.length == 6) {
                            keyboardController?.hide()
                            viewModel.verifyOtp(digits, onSuccess)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            if (otpCode.length == 6) {
                                viewModel.verifyOtp(otpCode, onSuccess)
                            }
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .focusRequester(focusRequester),
                    decorationBox = { /* Transparent decoration, visual boxes render above */ }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // RESEND BUTTON JUST BELOW THE OTP INPUT PLACE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        viewModel.requestOtp()
                        timerTrigger++
                        resendToastMessage = "Verification code resent to WhatsApp"
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = EvrryPrimaryBlue),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Resend Code",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Resend Code",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (resendToastMessage != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• Code resent!",
                        fontSize = 12.sp,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Auto-Fill for Development Demo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFF6FF))
                    .clickable {
                        viewModel.updateOtp("123456")
                        keyboardController?.hide()
                        viewModel.verifyOtp("123456", onSuccess)
                    }
                    .padding(vertical = 10.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Demo Testing Mode: Tap to auto-fill 123456",
                    fontSize = 11.sp,
                    color = EvrryPrimaryBlue,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Auto-Fill →",
                    fontSize = 11.sp,
                    color = EvrryPrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // BOTTOM SECTION: 1-MIN COUNTDOWN TIMER & VERIFY CTA
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 1 MINUTE COUNTDOWN TIMER (Resend on number in 00:xx)
            if (secondsRemaining > 0) {
                val formattedSeconds = secondsRemaining.toString().padStart(2, '0')
                Text(
                    text = "Resend on number in 00:$formattedSeconds",
                    fontSize = 13.sp,
                    color = EvrryMutedText,
                    textAlign = TextAlign.Center
                )
            } else {
                Row(
                    modifier = Modifier.clickable {
                        viewModel.requestOtp()
                        timerTrigger++
                        resendToastMessage = "Verification code resent to $targetPhone"
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Resend on number ($targetPhone)",
                        fontSize = 13.sp,
                        color = EvrryPrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // VERIFY CTA BUTTON
            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.verifyOtp(otpCode, onSuccess)
                },
                enabled = otpCode.length == 6 && !isLoading,
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
                    Text("Verify & Continue", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
