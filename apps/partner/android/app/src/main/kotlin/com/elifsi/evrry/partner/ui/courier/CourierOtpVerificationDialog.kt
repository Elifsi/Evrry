package com.elifsi.evrry.partner.ui.courier

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.elifsi.evrry.partner.model.CourierOrder
import com.elifsi.evrry.partner.ui.theme.*

/**
 * 4-Digit Customer Delivery OTP Handoff Modal
 * Modeled after Nepal FinTech Handshake & COD Anti-Fraud protocol
 * Verifies secret 4-digit code against public.order_handoff_codes
 *
 * Elifsi Technologies Private Limited
 */
@Composable
fun CourierOtpVerificationDialog(
    order: CourierOrder,
    otpInput: String,
    otpError: String?,
    isVerifying: Boolean,
    codConfirmed: Boolean,
    onDigitPress: (String) -> Unit,
    onDeletePress: () -> Unit,
    onToggleCodConfirmed: (Boolean) -> Unit,
    onVerifyClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = EvrryNavyMedium,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, EvrryNavyBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EvrryOrange.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "OTP Handshake",
                                tint = EvrryOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Delivery Handoff OTP",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryTextPrimary
                            )
                            Text(
                                text = "Order #${order.orderNo}",
                                fontSize = 12.sp,
                                color = EvrryTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = EvrryTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Nepal FinTech Anti-Fraud COD Cash Collection Warning
                if (order.isCod) {
                    Surface(
                        color = if (codConfirmed) EvrryGreen.copy(alpha = 0.15f) else EvrryRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (codConfirmed) EvrryGreen else EvrryRed
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleCodConfirmed(!codConfirmed) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (codConfirmed) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = "COD Alert",
                                tint = if (codConfirmed) EvrryGreen else EvrryRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (codConfirmed) "CASH COLLECTED: Rs ${order.totalRs.toInt()}" else "COLLECT PHYSICAL CASH: Rs ${order.totalRs.toInt()}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (codConfirmed) EvrryGreen else EvrryRed
                                )
                                Text(
                                    text = if (codConfirmed) "Tap to uncheck" else "Tap here to confirm cash in hand before entering OTP",
                                    fontSize = 10.sp,
                                    color = EvrryTextSecondary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                Text(
                    text = "Ask ${order.consumerName} for their secret 4-digit OTP shown in their Evrry App:",
                    fontSize = 12.sp,
                    color = EvrryTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 4-Digit Display Boxes
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val char = otpInput.getOrNull(i)?.toString() ?: ""
                        val isFilled = char.isNotEmpty()
                        val isCurrent = otpInput.length == i

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isFilled) EvrryNavyLight else EvrryNavyDark)
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) EvrryOrange else if (isFilled) EvrryGreen else EvrryNavyBorder,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isFilled) EvrryTextPrimary else EvrryTextSecondary
                            )
                        }
                    }
                }

                // Error Message
                AnimatedVisibility(
                    visible = otpError != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = otpError ?: "",
                        color = EvrryRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Numeric Keypad
                NumericKeypad(
                    onDigitPress = onDigitPress,
                    onDeletePress = onDeletePress
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Verification Button
                Button(
                    onClick = onVerifyClick,
                    enabled = otpInput.length == 4 && !isVerifying,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EvrryGreen,
                        disabledContainerColor = EvrryNavyLight
                    )
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "VERIFY & COMPLETE DELIVERY",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NumericKeypad(
    onDigitPress: (String) -> Unit,
    onDeletePress: () -> Unit
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "DEL")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        keys.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (key.isNotEmpty()) EvrryNavyLight else Color.Transparent)
                            .clickable(enabled = key.isNotEmpty()) {
                                if (key == "DEL") onDeletePress() else onDigitPress(key)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (key == "DEL") {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Delete",
                                tint = EvrryTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = key,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
