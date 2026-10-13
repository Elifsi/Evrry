package com.elifsi.evrry.partner.ui.courier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.CallTarget
import com.elifsi.evrry.partner.model.CourierOrder
import com.elifsi.evrry.partner.ui.theme.*

/**
 * Dual Calling Bottom Sheet for Delivery Courier
 * Provides 2 calling options:
 * 1. In-App VoIP HD Audio Call (Logged to public.call_logs)
 * 2. Direct SIM Carrier Phone Call (Cellular network dialer)
 *
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierDualCallSheet(
    order: CourierOrder,
    callTarget: CallTarget,
    onVoipCall: () -> Unit,
    onSimCall: () -> Unit,
    onDismiss: () -> Unit
) {
    val targetName = if (callTarget == CallTarget.STORE) order.storeName else order.consumerName
    val targetPhone = if (callTarget == CallTarget.STORE) order.storePhone else order.consumerPhone
    val targetRole = if (callTarget == CallTarget.STORE) {
        if (order.isGrocery) "Supermarket Desk" else "Restaurant Kitchen"
    } else {
        "Consumer"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EvrryNavyMedium,
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Target Avatar & Title
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(EvrryOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    tint = EvrryOrange,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Contact $targetName",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryTextPrimary
            )
            Text(
                text = "$targetRole · $targetPhone · Order #${order.orderNo}",
                fontSize = 12.sp,
                color = EvrryTextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Option 1: In-App VoIP Call
            Card(
                colors = CardDefaults.cardColors(containerColor = EvrryNavyDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVoipCall() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EvrryGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HeadsetMic,
                            contentDescription = "VoIP Call",
                            tint = EvrryGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "In-App VoIP HD Call",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = EvrryGreen,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "FREE DATA",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "High-definition encrypted voice calling without cellular balance",
                            fontSize = 11.sp,
                            color = EvrryTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Option 2: Direct SIM Carrier Call
            Card(
                colors = CardDefaults.cardColors(containerColor = EvrryNavyDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSimCall() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EvrryBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = "SIM Call",
                            tint = EvrryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Direct SIM Carrier Call",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = EvrryNavyLight,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "NTC / Ncell",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryTextSecondary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Standard cellular call to $targetPhone using device phone app",
                            fontSize = 11.sp,
                            color = EvrryTextSecondary
                        )
                    }
                }
            }
        }
    }
}
