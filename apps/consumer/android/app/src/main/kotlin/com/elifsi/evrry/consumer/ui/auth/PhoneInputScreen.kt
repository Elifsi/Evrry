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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.OtpChannel
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.AuthViewModel

/**
 * Universal Registration & Login Screen
 * Defaults to Register screen with Phone Number + Password.
 * Supports 1-tap switch to Email + Password.
 * Verifies Phone Number via WhatsApp OTP.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@Composable
fun PhoneInputScreen(
    viewModel: AuthViewModel,
    onContinue: () -> Unit
) {
    val currentPhoneNumber by viewModel.phoneNumber.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 1. User arrives at the Register screen by default
    var isSignUp by remember { mutableStateOf(true) }

    // 2. Default is Phone Number input; tap "Continue with Email" switches to Email mode
    var isEmailAuth by remember { mutableStateOf(false) }

    var phoneInput by remember { mutableStateOf(currentPhoneNumber) }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

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

            // BRAND HEADER
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

            // TITLE & SUBTITLE
            if (isSignUp) {
                Text(
                    text = "Create your Account",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = EvrryDarkCharcoal
                )
                Text(
                    text = if (isEmailAuth) "Sign up with your email and password to get started." else "Enter your Nepal mobile number and password to register.",
                    fontSize = 13.sp,
                    color = EvrryMutedText,
                    modifier = Modifier.padding(top = 6.dp)
                )
            } else {
                Text(
                    text = "Welcome to evrry",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = EvrryDarkCharcoal
                )
                Text(
                    text = if (isEmailAuth) "Sign in with your email and password to access your account." else "Sign in with your Nepal mobile number to access your account.",
                    fontSize = 13.sp,
                    color = EvrryMutedText,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // INPUT SECTION
            if (!isEmailAuth) {
                // PHONE NUMBER INPUT
                Text(
                    text = "NEPAL MOBILE NUMBER",
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
                        value = phoneInput,
                        onValueChange = {
                            val digits = it.filter { ch -> ch.isDigit() }.take(10)
                            phoneInput = digits
                            viewModel.updatePhoneNumber(digits)
                        },
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

                Spacer(modifier = Modifier.height(16.dp))

                // PASSWORD INPUT
                Text(
                    text = "PASSWORD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryMutedText
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    placeholder = { Text("••••••••", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = EvrryMutedText, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showPassword) "Hide password" else "Show password",
                                tint = EvrryMutedText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = EvrrySubtleBorder,
                        focusedBorderColor = EvrryPrimaryBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(22.dp))

                // PRIMARY CTA: Triggers WhatsApp OTP verification on phone number
                val isPhoneValid = phoneInput.length == 10 && passwordInput.length >= 6
                Button(
                    onClick = {
                        viewModel.updatePhoneNumber(phoneInput)
                        viewModel.selectChannel(OtpChannel.WHATSAPP)
                        viewModel.requestOtp()
                        onContinue()
                    },
                    enabled = isPhoneValid && !isLoading,
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
                        Text(
                            text = if (isSignUp) "Create Account" else "Sign In",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

            } else {
                // EMAIL INPUT MODE
                Text(
                    text = "EMAIL ADDRESS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryMutedText
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    placeholder = { Text("name@example.com", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(Icons.Rounded.Email, contentDescription = null, tint = EvrryMutedText, modifier = Modifier.size(18.dp))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = EvrrySubtleBorder,
                        focusedBorderColor = EvrryPrimaryBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PASSWORD INPUT
                Text(
                    text = "PASSWORD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryMutedText
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    placeholder = { Text("••••••••", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = EvrryMutedText, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showPassword) "Hide password" else "Show password",
                                tint = EvrryMutedText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = EvrrySubtleBorder,
                        focusedBorderColor = EvrryPrimaryBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(22.dp))

                // PRIMARY CTA FOR EMAIL
                val isEmailValid = emailInput.contains("@") && emailInput.contains(".") && passwordInput.length >= 6
                Button(
                    onClick = {
                        if (isSignUp) {
                            viewModel.signUpWithPassword("", emailInput, passwordInput)
                        } else {
                            viewModel.signInWithPassword(emailInput, passwordInput)
                        }
                    },
                    enabled = isEmailValid && !isLoading,
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
                        Text(
                            text = if (isSignUp) "Create Account" else "Sign In",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DIVIDER: OR CONTINUE WITH
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

            Spacer(modifier = Modifier.height(18.dp))

            // DYNAMIC ALTERNATIVE BUTTONS
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!isEmailAuth) {
                    // CONTINUE WITH EMAIL BUTTON
                    OutlinedButton(
                        onClick = { isEmailAuth = true },
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
                                contentDescription = null,
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
                } else {
                    // CONTINUE WITH PHONE NUMBER BUTTON (switches back to phone)
                    OutlinedButton(
                        onClick = { isEmailAuth = false },
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
                                imageVector = Icons.Rounded.Phone,
                                contentDescription = null,
                                tint = EvrryPrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Continue with Phone Number",
                                color = EvrryDarkCharcoal,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // CONTINUE WITH GOOGLE
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
            }
        }

        // BOTTOM SWITCH FOOTER ("Already on Evrry? Login here" <-> "New on Evrry? Register now")
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isSignUp) "Already on Evrry? " else "New on Evrry? ",
                    fontSize = 13.sp,
                    color = EvrryDarkCharcoal
                )
                Text(
                    text = if (isSignUp) "Login here" else "Register now",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryPrimaryBlue,
                    modifier = Modifier.clickable {
                        isSignUp = !isSignUp
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "By continuing, you agree to Evrry Terms & Privacy Policy.",
                fontSize = 11.sp,
                color = EvrryMutedText
            )
        }
    }
}
