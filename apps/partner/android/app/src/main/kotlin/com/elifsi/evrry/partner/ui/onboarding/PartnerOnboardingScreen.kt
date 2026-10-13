package com.elifsi.evrry.partner.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Phone
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
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.PartnerAuthState
import com.elifsi.evrry.partner.viewmodel.PartnerAuthViewModel

/**
 * Shared Partner Onboarding & Phone OTP Screen
 * Facilitates phone WhatsApp OTP (primary) with Domestic SMS fallback.
 * Platform: Evrry Super App (Partner Android)
 * Elifsi Technologies Private Limited
 */
@Composable
fun PartnerOnboardingScreen(
    authViewModel: PartnerAuthViewModel,
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.authState.collectAsState()
    val phoneNumber by authViewModel.phoneNumber.collectAsState()
    val otpCode by authViewModel.otpCode.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo & Badge
        Surface(
            color = EvrryPrimaryBlue.copy(alpha = 0.1f),
            shape = CircleShape,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🇳🇵", fontSize = 34.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "evrry Partner",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = EvrryDarkCharcoal
        )

        Text(
            text = "Unified Operator Portal for Nepal",
            fontSize = 13.sp,
            color = EvrryMutedText,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // Error message banner
        if (errorMessage != null) {
            Surface(
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EvrryRedDestructive.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = EvrryRedDestructive,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        AnimatedContent(
            targetState = authState,
            label = "auth_step"
        ) { state ->
            if (state == PartnerAuthState.UNAUTHENTICATED) {
                // PHONE NUMBER INPUT STEP
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Enter Your Mobile Number",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryDarkCharcoal
                    )

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { authViewModel.updatePhoneNumber(it) },
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                            ) {
                                Text("🇳🇵 +977", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(modifier = Modifier.size(1.dp, 20.dp).background(EvrrySubtleBorder))
                            }
                        },
                        placeholder = { Text("98XXXXXXXX", fontSize = 14.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EvrryPrimaryBlue,
                            unfocusedBorderColor = EvrrySubtleBorder
                        )
                    )

                    // Request OTP Button (WhatsApp primary)
                    Button(
                        onClick = { authViewModel.requestOtp(channel = "whatsapp") },
                        enabled = !isLoading && phoneNumber.isNotBlank(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Get WhatsApp OTP 💬", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // SMS Fallback Button
                    OutlinedButton(
                        onClick = { authViewModel.requestOtp(channel = "sms") },
                        enabled = !isLoading && phoneNumber.isNotBlank(),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send via SMS instead", fontSize = 13.sp, color = EvrryMutedText)
                    }
                }
            } else {
                // OTP CODE VERIFICATION STEP
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Enter 6-Digit Verification Code",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryDarkCharcoal
                    )

                    Text(
                        text = "Code sent to +977 $phoneNumber",
                        fontSize = 12.sp,
                        color = EvrryMutedText
                    )

                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { authViewModel.updateOtpCode(it) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Rounded.Lock, contentDescription = "OTP", tint = EvrryPrimaryBlue)
                        },
                        placeholder = { Text("• • • • • •", fontSize = 16.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EvrryPrimaryBlue,
                            unfocusedBorderColor = EvrrySubtleBorder
                        )
                    )

                    Button(
                        onClick = { authViewModel.verifyOtp() },
                        enabled = !isLoading && otpCode.isNotBlank(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryGreenSuccess),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Verify & Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    TextButton(onClick = { authViewModel.requestOtp(channel = "whatsapp") }) {
                        Text("Resend Code", fontSize = 12.sp, color = EvrryPrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
