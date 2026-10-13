package com.elifsi.evrry.partner.ui.courier

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
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
import com.elifsi.evrry.partner.model.CourierDeliveryStage
import com.elifsi.evrry.partner.model.CourierOrder
import com.elifsi.evrry.partner.ui.theme.*

/**
 * Two-Stage Multi-Stop Active Navigation HUD for Delivery Courier
 * Modeled after kaaneneskpc/Deliverr and enatega-multivendor-rider
 *
 * Stage 1: Courier -> Restaurant / Supermarket Store (Pickup)
 * Stage 2: Store -> Consumer Doorstep (Dropoff & 4-Digit OTP Handshake)
 *
 * Elifsi Technologies Private Limited
 */
@Composable
fun CourierActiveDeliveryHud(
    order: CourierOrder,
    stage: CourierDeliveryStage,
    hasArrivedAtStore: Boolean,
    hasArrivedAtCustomer: Boolean,
    onArrivedAtStoreClick: () -> Unit,
    onConfirmPickupClick: () -> Unit,
    onArrivedAtCustomerClick: () -> Unit,
    onOpenOtpDialogClick: () -> Unit,
    onOpenChatClick: () -> Unit,
    onOpenCallSheet: (CallTarget) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EvrryNavyDark)
            .padding(16.dp)
    ) {
        // Active Multi-Stop Stage Indicator Banner
        Surface(
            color = EvrryNavyMedium,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (stage == CourierDeliveryStage.STAGE_1_TO_STORE) EvrryOrange.copy(alpha = 0.2f)
                            else EvrryGreen.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (stage == CourierDeliveryStage.STAGE_1_TO_STORE) (if (order.isGrocery) "🛒" else "🍳") else "🛵",
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (stage == CourierDeliveryStage.STAGE_1_TO_STORE) "STAGE 1: PICK UP FROM STORE" else "STAGE 2: DELIVER TO CUSTOMER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (stage == CourierDeliveryStage.STAGE_1_TO_STORE) EvrryOrange else EvrryGreen
                    )
                    Text(
                        text = if (stage == CourierDeliveryStage.STAGE_1_TO_STORE) order.storeName else order.consumerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryTextPrimary
                    )
                }

                // Quick Communication Actions (Chat & Call)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = onOpenChatClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EvrryNavyLight)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "Chat",
                            tint = EvrryOrange,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val target = if (stage == CourierDeliveryStage.STAGE_1_TO_STORE) CallTarget.STORE else CallTarget.CUSTOMER
                            onOpenCallSheet(target)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EvrryNavyLight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            tint = EvrryGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Simulated Turn-by-Turn Map & Route Overview Card
        Surface(
            color = EvrryNavyMedium,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background dark simulated map grid
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = EvrryOrange,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (stage == CourierDeliveryStage.STAGE_1_TO_STORE) "TO STORE" else "TO CONSUMER",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "OSRM Routing Engine · Turn-by-Turn",
                                fontSize = 11.sp,
                                color = EvrryTextSecondary
                            )
                        }

                        Surface(
                            color = EvrryNavyDark,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder)
                        ) {
                            Text(
                                text = "ETA: ${if (stage == CourierDeliveryStage.STAGE_1_TO_STORE) 5 else 12} mins",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Turn Maneuver Banner
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(EvrryNavyDark.copy(alpha = 0.85f))
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TurnRight,
                            contentDescription = "Turn Right",
                            tint = EvrryOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (stage == CourierDeliveryStage.STAGE_1_TO_STORE) "Turn right onto Pulchowk Marg in 200m" else "Head north on Sanepa Heights Rd in 150m",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryTextPrimary
                            )
                            Text(
                                text = if (stage == CourierDeliveryStage.STAGE_1_TO_STORE) "Destination: ${order.storeAddress}" else "Destination: ${order.deliveryAddress}",
                                fontSize = 10.sp,
                                color = EvrryTextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Scrollable Order Details & Stage Action Panel
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stage 1 Specific: Present Order # to Store Clerk
            if (stage == CourierDeliveryStage.STAGE_1_TO_STORE) {
                item {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🛍️", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "PRESENT TO STORE CLERK",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                    Text(
                                        text = "Order #${order.orderNo}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (order.isGrocery) "SUPERMARKET" else "KITCHEN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Stage 2 Specific: COD Cash Alert
            if (stage == CourierDeliveryStage.STAGE_2_TO_CONSUMER && order.isCod) {
                item {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, EvrryRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "COD Alert",
                                tint = EvrryRed,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "COLLECT CASH: NPR ${order.totalRs.toInt()}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EvrryRed
                                )
                                Text(
                                    text = "Collect physical banknotes before asking consumer for 4-digit secret OTP",
                                    fontSize = 11.sp,
                                    color = Color(0xFF991B1B)
                                )
                            }
                        }
                    }
                }
            }

            // Package Contents Verification Checklist
            item {
                Surface(
                    color = EvrryNavyMedium,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "PACKAGE CONTENTS CHECKLIST",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryTextSecondary
                            )
                            Text(
                                text = "${order.items.size} item(s)",
                                fontSize = 11.sp,
                                color = EvrryOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EvrryGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${item.name} x${item.quantity}",
                                        fontSize = 13.sp,
                                        color = EvrryTextPrimary
                                    )
                                }
                                Text(
                                    text = "Rs ${item.unitPriceRs.toInt() * item.quantity}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EvrryTextSecondary
                                )
                            }
                        }

                        if (!order.customerNote.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = EvrryNavyDark,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Note: ${order.customerNote}",
                                    fontSize = 11.sp,
                                    color = EvrryTextSecondary,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons according to Stage
        when (stage) {
            CourierDeliveryStage.STAGE_1_TO_STORE -> {
                if (!hasArrivedAtStore) {
                    Button(
                        onClick = onArrivedAtStoreClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryOrange)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ARRIVED AT STORE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                } else {
                    Button(
                        onClick = onConfirmPickupClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PICK UP ORDER & NAVIGATE TO CUSTOMER",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }

            CourierDeliveryStage.STAGE_2_TO_CONSUMER -> {
                if (!hasArrivedAtCustomer) {
                    Button(
                        onClick = onArrivedAtCustomerClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryOrange)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ARRIVED AT CUSTOMER (SEND NOTIFICATION)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                } else {
                    Button(
                        onClick = onOpenOtpDialogClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ENTER 4-DIGIT CUSTOMER OTP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }

            CourierDeliveryStage.COMPLETED -> {
                Surface(
                    color = EvrryGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EvrryGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DELIVERY COMPLETED", fontWeight = FontWeight.Bold, color = EvrryGreen)
                    }
                }
            }
        }
    }
}
