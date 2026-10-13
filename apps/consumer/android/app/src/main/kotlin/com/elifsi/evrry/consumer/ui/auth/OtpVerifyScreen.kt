package com.elifsi.evrry.consumer.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.AuthScreenState
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.AuthViewModel

/**
 * 6-Digit OTP Verification Screen
 * In-App Back Navigation, PIN inputs, and Resend Timer
 */
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // IN-APP BACK BUTTON (Top navigation)
            IconButton(
                onClick = {
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
                    contentDescription = "Change Number",
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

            // 6 PIN Input Display Boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0 until 6) {
                    val char = otpCode.getOrNull(i)?.toString() ?: ""
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .border(
                                width = if (otpCode.length == i) 2.dp else 1.dp,
                                color = if (otpCode.length == i) EvrryPrimaryBlue else EvrrySubtleBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = char,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryDarkCharcoal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Hidden Keyboard Receiver
            TextField(
                value = otpCode,
                onValueChange = {
                    viewModel.updateOtp(it)
                    if (it.length == 6) {
                        viewModel.verifyOtp(it, onSuccess)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.dp) // Hidden input triggering numerical keyboard
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Auto-Fill for Development Demo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFF6FF))
                    .clickable {
                        viewModel.updateOtp("123456")
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

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Didn't receive code? Resend in 0:42",
                    fontSize = 12.sp,
                    color = EvrryMutedText
                )
            }
        }

        // Verify CTA Button
        Button(
            onClick = { viewModel.verifyOtp(otpCode, onSuccess) },
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
