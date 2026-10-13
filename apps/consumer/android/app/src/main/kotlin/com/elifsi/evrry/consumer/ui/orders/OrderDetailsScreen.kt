package com.elifsi.evrry.consumer.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.ui.components.EvrryTopBar
import com.elifsi.evrry.consumer.ui.components.EvrryLiveTrackingMap
import com.elifsi.evrry.consumer.ui.components.VehicleType
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.ActivityViewModel
import com.elifsi.evrry.consumer.viewmodel.OrderActivityItem

import com.elifsi.evrry.consumer.ui.food.ItemizedReceiptSheet
import com.elifsi.evrry.consumer.ui.food.OrderRatingSheet

/**
 * Live Order Details & Tracking Screen
 * Real-time order lifecycle tracking, driver HUD, route tracking card, and itemized receipt
 * Elifsi Technologies Private Limited
 */
@Composable
fun OrderDetailsScreen(
    orderId: String,
    activityViewModel: ActivityViewModel,
    onBack: () -> Unit,
    onReorder: ((OrderActivityItem) -> Unit)? = null,
    onChatWithRider: ((String) -> Unit)? = null
) {
    val uiState by activityViewModel.uiState.collectAsState()
    var showReceipt by remember { mutableStateOf(false) }
    var showRating by remember { mutableStateOf(false) }
    val order = remember(orderId, uiState.items) {
        activityViewModel.getOrder(orderId) ?: uiState.items.firstOrNull() ?: OrderActivityItem(
            id = orderId,
            title = "Food Order",
            vendorName = "Jimbu Thakali Kitchen",
            category = "Food",
            amountRs = 480,
            status = "kitchen",
            paymentMethod = "eSewa Direct Payment",
            dateFormatted = "Just now",
            etaMinutes = 20,
            riderName = "Bikash Thapa",
            riderPhone = "9841000001",
            vehicleNumber = "Ba 88 Pa 4321"
        )
    }

    val isGrocery = order.category.equals("Mart", ignoreCase = true) || 
                    order.category.equals("Grocery", ignoreCase = true) || 
                    order.vendorName.contains("Mart", ignoreCase = true) || 
                    order.vendorName.contains("Quick", ignoreCase = true)

    val stages = if (isGrocery) {
        listOf(
            "placed" to "Order Placed",
            "confirmed" to "Hub Accepted",
            "packing" to "Store Picking",
            "delivering" to "Out for Delivery",
            "delivered" to "Delivered"
        )
    } else {
        listOf(
            "placed" to "Order Placed",
            "confirmed" to "Confirmed",
            "kitchen" to "In Kitchen",
            "delivering" to "Out for Delivery",
            "delivered" to "Delivered"
        )
    }

    val currentStageIndex = when (order.status.lowercase()) {
        "placed", "acknowledged" -> 0
        "confirmed" -> 1
        "kitchen", "preparing", "packing" -> 2
        "ready_for_pickup", "delivering", "dispatched" -> 3
        "delivered" -> 4
        else -> 2
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
    ) {
        // TOP APP BAR with In-App Back Navigation
        EvrryTopBar(
            title = "Order #${order.id}",
            canGoBack = true,
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
        ) {
            // LIVE STATUS PIPELINE (5-Stage Stepper)
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ESTIMATED DELIVERY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                Text("~${order.etaMinutes ?: (if (isGrocery) 12 else 25)} Minutes", fontSize = 18.sp, fontWeight = FontWeight.Black, color = if (isGrocery) Color(0xFF047857) else EvrryPrimaryBlue)
                            }
                            Surface(
                                color = if (order.status == "delivered") Color(0xFFDCFCE7) else if (isGrocery) Color(0xFFECFDF5) else Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isGrocery && order.status == "kitchen") "PACKING" else order.status.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (order.status == "delivered" || isGrocery) Color(0xFF047857) else EvrryPrimaryBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // 5-STAGE PROGRESS BAR
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            stages.forEachIndexed { idx, _ ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            if (idx <= currentStageIndex) (if (isGrocery) Color(0xFF047857) else EvrryPrimaryBlue) else Color(0xFFE2E8F0)
                                        )
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            stages.forEachIndexed { idx, stage ->
                                Text(
                                    text = stage.second,
                                    fontSize = 9.sp,
                                    fontWeight = if (idx == currentStageIndex) FontWeight.Bold else FontWeight.Normal,
                                    color = if (idx <= currentStageIndex) EvrryDarkCharcoal else EvrryMutedText
                                )
                            }
                        }

                        if (isGrocery) {
                            Surface(
                                color = Color(0xFFF0FDF4),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🛒", fontSize = 20.sp)
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "Dark-Store Micro Fulfillment",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF166534)
                                        )
                                        Text(
                                            text = "Jhamsikhel Hub • Picker packing in sealed bag #JH-8821",
                                            fontSize = 11.sp,
                                            color = Color(0xFF15803D)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ROUTE & TRACKING MAP CARD (Live Driver Navigation)
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                ) {
                    val isTakeaway = order.riderName == null
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (isTakeaway) "PICKUP LOCATION & INSTRUCTIONS" else "LIVE ROUTE & DISPATCH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryMutedText
                        )

                        if (isTakeaway) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🛍️", fontSize = 32.sp)
                                    Text(
                                        text = "Ready for Self-Pickup at ${order.vendorName}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                    Text(
                                        text = "Collect directly at the counter with your Order ID #${order.id}",
                                        fontSize = 10.sp,
                                        color = Color(0xFFB45309)
                                    )
                                }
                            }
                        } else {
                            EvrryLiveTrackingMap(
                                vendorName = order.vendorName,
                                destinationAddress = "Sanepa Heights (House 42)",
                                vehicleType = VehicleType.SCOOTER,
                                riderName = order.riderName,
                                etaMinutes = order.etaMinutes ?: (if (isGrocery) 12 else 20),
                                speedKmh = 26
                            )
                        }
                    }
                }
            }

            // RIDER HUD CARD
            if (order.riderName != null) {
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Surface(
                                    color = EvrryPrimaryBlue.copy(alpha = 0.1f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("👨‍✈️", fontSize = 22.sp)
                                    }
                                }
                                Column {
                                    Text(order.riderName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EvrryDarkCharcoal)
                                    Text(order.vehicleNumber ?: "Motorbike", fontSize = 12.sp, color = EvrryMutedText)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                if (onChatWithRider != null) {
                                    OutlinedButton(
                                        onClick = { onChatWithRider(order.id) },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, EvrryPrimaryBlue.copy(alpha = 0.3f))
                                    ) {
                                        Text("Chat", color = EvrryPrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Button(
                                    onClick = { /* Call driver */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Icon(imageVector = Icons.Rounded.Call, contentDescription = "Call", tint = EvrryPrimaryBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Call", color = EvrryPrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // DELIVERY HANDSHAKE OTP CARD (Nepal FinTech / COD Anti-Fraud Handshake)
            val otpCode = order.rideOtp ?: if (order.paymentMethod.contains("Cash", ignoreCase = true) || order.paymentMethod.contains("COD", ignoreCase = true)) "4821" else null
            if (otpCode != null && order.status != "delivered" && order.status != "cancelled") {
                item {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, Color(0xFFF59E0B), RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    color = Color(0xFFF59E0B),
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🔐", fontSize = 18.sp)
                                    }
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "DELIVERY HANDSHAKE OTP",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                    Text(
                                        text = "Share with rider only after receiving sealed package",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB45309)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                            ) {
                                Text(
                                    text = otpCode,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp,
                                    color = Color(0xFF92400E),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // DELIVERY ADDRESS CARD
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("DELIVERY DESTINATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(18.dp))
                            Text(order.deliveryAddress ?: "Pulchowk, Ward 3, Lalitpur", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                        }
                        order.specialInstructions?.let {
                            Text("Note: $it", fontSize = 12.sp, color = EvrryMutedText, modifier = Modifier.padding(start = 26.dp))
                        }
                    }
                }
            }

            // ITEMIZED RECEIPT
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("ORDER SUMMARY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)

                        if (order.lineItems.isNotEmpty()) {
                            order.lineItems.forEach { itemDesc ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(itemDesc, fontSize = 13.sp, color = EvrryDarkCharcoal)
                                }
                            }
                        } else {
                            Text(order.title, fontSize = 13.sp, color = EvrryDarkCharcoal)
                        }

                        HorizontalDivider(color = EvrrySubtleBorder, thickness = 1.dp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Delivery Fee", fontSize = 12.sp, color = EvrryMutedText)
                            Text("Rs ${order.deliveryFeeRs}", fontSize = 12.sp, color = EvrryDarkCharcoal)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Payment via", fontSize = 12.sp, color = EvrryMutedText)
                            Text(order.paymentMethod, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EvrryPrimaryBlue)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grand Total", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                            Text("Rs ${order.amountRs}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = EvrryDarkCharcoal)
                        }
                    }
                }
            }

            // CANCEL ORDER BUTTON (Enabled if order is placed or kitchen)
            if (order.status in listOf("placed", "kitchen", "confirmed")) {
                item {
                    Button(
                        onClick = {
                            activityViewModel.cancelOrder(order.id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Cancel Order", color = EvrryRedDestructive, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // COMPLETED ORDER ACTIONS (Reorder, Receipt, Rate)
            if (order.status.lowercase() in listOf("delivered", "completed")) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onReorder?.invoke(order) },
                            colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text(if (isGrocery) "🔁 Reorder These Items" else "🔁 Reorder This Meal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showReceipt = true },
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder),
                                modifier = Modifier.weight(1f).height(46.dp)
                            ) {
                                Text("🧾 Tax Invoice", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                            }

                            OutlinedButton(
                                onClick = { showRating = true },
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder),
                                modifier = Modifier.weight(1f).height(46.dp)
                            ) {
                                Text("⭐ Rate Order", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFFD97706))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showReceipt) {
        ItemizedReceiptSheet(order = order, onDismiss = { showReceipt = false })
    }

    if (showRating) {
        OrderRatingSheet(order = order, onDismiss = { showRating = false }, onSubmit = { _, _, _, _ -> })
    }
}
