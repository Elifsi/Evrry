package com.elifsi.evrry.partner.ui.kds

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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.*
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.RestaurantKitchenViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Main Kitchen Display System (KDS) Screen
 * Platform: Android (Jetpack Compose)
 * Elifsi Technologies Private Limited
 */
@Composable
fun KitchenDisplayScreen(
    viewModel: RestaurantKitchenViewModel
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val currentTime by viewModel.ticker.collectAsState()

    val newOrders = remember(orders) { orders.filter { it.status == RestaurantOrderStatus.ACKNOWLEDGED } }
    val preparingOrders = remember(orders) { orders.filter { it.status == RestaurantOrderStatus.PREPARING } }
    val readyOrders = remember(orders) { orders.filter { it.status == RestaurantOrderStatus.READY_FOR_PICKUP || it.status == RestaurantOrderStatus.DISPATCHED } }
    val completedOrders = remember(orders) { orders.filter { it.status == RestaurantOrderStatus.DELIVERED || it.status == RestaurantOrderStatus.CANCELLED || it.status == RestaurantOrderStatus.REJECTED } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EvrryNavyDark)
    ) {
        // KDS 4-Column Tab Bar
        TabRow(
            selectedTabIndex = currentTab.ordinal,
            containerColor = EvrryNavyMedium,
            contentColor = EvrryOrange,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab.ordinal]),
                    color = EvrryOrange
                )
            }
        ) {
            KitchenTab.entries.forEach { tab ->
                val count = when (tab) {
                    KitchenTab.NEW_ORDERS -> newOrders.size
                    KitchenTab.PREPARING -> preparingOrders.size
                    KitchenTab.READY_FOR_PICKUP -> readyOrders.size
                    KitchenTab.COMPLETED -> completedOrders.size
                }
                Tab(
                    selected = currentTab == tab,
                    onClick = { viewModel.selectTab(tab) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = tab.title,
                                fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (currentTab == tab) EvrryTextPrimary else EvrryTextSecondary
                            )
                            if (count > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (tab == KitchenTab.NEW_ORDERS) EvrryRed else EvrryNavyLight)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$count",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }

        // Active Tab Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when (currentTab) {
                KitchenTab.NEW_ORDERS -> {
                    IncomingOrdersView(
                        orders = newOrders,
                        onAccept = { viewModel.openPrepTimeModal(it) },
                        onReject = { viewModel.openRejectModal(it) },
                        onPrintKot = { viewModel.printTicket(it, isKot = true) },
                        onChat = { order -> viewModel.openChat(order, "consumer") }
                    )
                }
                KitchenTab.PREPARING -> {
                    PreparingOrdersView(
                        orders = preparingOrders,
                        now = currentTime,
                        onMarkReady = { viewModel.markReadyForPickup(it.id) },
                        onPrintKot = { viewModel.previewKot(it) },
                        onChat = { order -> viewModel.openChat(order, "consumer") }
                    )
                }
                KitchenTab.READY_FOR_PICKUP -> {
                    ReadyForPickupOrdersView(
                        orders = readyOrders,
                        onPrintBill = { viewModel.previewBill(it) },
                        onChatCourier = { order -> viewModel.openChat(order, "rider") }
                    )
                }
                KitchenTab.COMPLETED -> {
                    CompletedOrdersView(
                        orders = completedOrders,
                        dailyCount = viewModel.dailyCompletedCount,
                        grossSalesRs = viewModel.dailyGrossSalesRs,
                        avgPrepFormatted = viewModel.avgPrepMinutesFormatted,
                        onPrintBill = { viewModel.previewBill(it) }
                    )
                }
            }
        }
    }
}

/**
 * 1. Incoming / New Orders Tab
 */
@Composable
private fun IncomingOrdersView(
    orders: List<RestaurantOrder>,
    onAccept: (RestaurantOrder) -> Unit,
    onReject: (RestaurantOrder) -> Unit,
    onPrintKot: (RestaurantOrder) -> Unit,
    onChat: (RestaurantOrder) -> Unit
) {
    if (orders.isEmpty()) {
        EmptyKdsState(
            icon = Icons.Default.Restaurant,
            title = "No Incoming Orders",
            subtitle = "New tickets will chime loudly as soon as consumers order."
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(orders, key = { it.id }) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, EvrryOrange, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = EvrryNavyMedium)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EvrryOrange)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "#${order.orderNo}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = order.consumerName,
                                        color = EvrryTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${order.paymentMethod} · NPR ${String.format(Locale.US, "%.0f", order.totalRs)}",
                                        color = EvrryTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // Flash pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(EvrryRed.copy(alpha = 0.2f))
                                    .border(1.dp, EvrryRed, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ACTION REQUIRED",
                                    color = EvrryRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(
                            color = EvrryNavyBorder,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // Items list
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.quantity}x",
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryOrange,
                                        fontSize = 14.sp,
                                        modifier = Modifier.width(32.dp)
                                    )
                                    Column {
                                        Text(
                                            text = item.name,
                                            color = EvrryTextPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                        item.customizations.forEach { custom ->
                                            Text(
                                                text = "• $custom",
                                                color = EvrryYellow,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Special instructions / note
                        if (!order.customerNote.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EvrryNavyLight)
                                    .border(1.dp, EvrryNavyBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = EvrryOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Note: ${order.customerNote}",
                                        color = EvrryTextPrimary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: Accept / Reject / Print / Chat
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onReject(order) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EvrryRed),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(EvrryRed.copy(alpha = 0.5f))
                                )
                            ) {
                                Text("Reject", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { onPrintKot(order) },
                                shape = RoundedCornerShape(10.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(EvrryNavyBorder)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = "Print KOT",
                                    tint = EvrryTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("KOT", fontSize = 12.sp, color = EvrryTextPrimary)
                            }

                            FilledTonalButton(
                                onClick = { onChat(order) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = EvrryNavyLight)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Chat",
                                    tint = EvrryTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Button(
                                onClick = { onAccept(order) },
                                modifier = Modifier.weight(1.5f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EvrryGreen)
                            ) {
                                Text("Accept & Prep", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2. Preparing Tab (With Circular Countdown Timers)
 */
@Composable
private fun PreparingOrdersView(
    orders: List<RestaurantOrder>,
    now: Long,
    onMarkReady: (RestaurantOrder) -> Unit,
    onPrintKot: (RestaurantOrder) -> Unit,
    onChat: (RestaurantOrder) -> Unit
) {
    if (orders.isEmpty()) {
        EmptyKdsState(
            icon = Icons.Default.Kitchen,
            title = "No Active Cooking Tickets",
            subtitle = "Accepted orders will appear here with active countdown timers."
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(orders, key = { it.id }) { order ->
                val remainingSec = order.remainingPrepSeconds(now)
                val progress = order.prepProgress(now)
                val isOverdue = order.isOverdue(now)
                val minutesLeft = remainingSec / 60
                val secondsLeft = remainingSec % 60

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            if (isOverdue) EvrryRed else EvrryNavyBorder,
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = EvrryNavyMedium)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${order.orderNo}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = EvrryTextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = order.consumerName,
                                    color = EvrryTextSecondary,
                                    fontSize = 13.sp
                                )
                            }

                            // Circular Timer Display
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier.size(46.dp),
                                        color = if (isOverdue) EvrryRed else if (progress > 0.8f) EvrryYellow else EvrryGreen,
                                        trackColor = EvrryNavyLight,
                                        strokeWidth = 4.dp
                                    )
                                    Text(
                                        text = if (isOverdue) "LATE" else "${minutesLeft}m",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOverdue) EvrryRed else EvrryTextPrimary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (isOverdue) "OVERDUE" else String.format(Locale.US, "%02d:%02d", minutesLeft, secondsLeft),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOverdue) EvrryRed else EvrryOrange
                                    )
                                    Text(
                                        text = "Target: ${order.prepTimeMinutes}m",
                                        fontSize = 10.sp,
                                        color = EvrryTextSecondary
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            color = EvrryNavyBorder,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // Items
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${item.quantity}x",
                                    color = EvrryOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.width(30.dp)
                                )
                                Column {
                                    Text(
                                        text = item.name,
                                        color = EvrryTextPrimary,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    item.customizations.forEach { custom ->
                                        Text(
                                            text = "• $custom",
                                            color = EvrryYellow,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onPrintKot(order) },
                                shape = RoundedCornerShape(10.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(EvrryNavyBorder)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = "Print KOT",
                                    tint = EvrryTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("KOT", fontSize = 12.sp, color = EvrryTextPrimary)
                            }

                            FilledTonalButton(
                                onClick = { onChat(order) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = EvrryNavyLight)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Chat",
                                    tint = EvrryTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Button(
                                onClick = { onMarkReady(order) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EvrryOrange)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mark Ready for Pickup", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 3. Ready for Pickup Tab
 */
@Composable
private fun ReadyForPickupOrdersView(
    orders: List<RestaurantOrder>,
    onPrintBill: (RestaurantOrder) -> Unit,
    onChatCourier: (RestaurantOrder) -> Unit
) {
    if (orders.isEmpty()) {
        EmptyKdsState(
            icon = Icons.Default.TwoWheeler,
            title = "No Orders Awaiting Courier",
            subtitle = "Orders marked Ready will show here with courier live ETA."
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(orders, key = { it.id }) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, EvrryGreen, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = EvrryNavyMedium)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ORDER #${order.orderNo}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = EvrryTextPrimary
                                )
                                Text(
                                    text = "Ready on Dispatch Counter",
                                    color = EvrryGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(EvrryGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "PACKED ✓",
                                    color = EvrryGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Courier Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(EvrryNavyLight)
                                .border(1.dp, EvrryNavyBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TwoWheeler,
                                        contentDescription = "Courier",
                                        tint = EvrryOrange,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = order.riderName ?: "Assigning Nearby Courier...",
                                            fontWeight = FontWeight.Bold,
                                            color = EvrryTextPrimary,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = order.riderVehicleType ?: "Searching delivery network",
                                            color = EvrryTextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                if (order.riderDistanceMeters != null) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${order.riderDistanceMeters}m away",
                                            fontWeight = FontWeight.Bold,
                                            color = EvrryOrange,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Arriving in ~2 min",
                                            fontSize = 11.sp,
                                            color = EvrryTextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions: Print Customer Receipt & Chat with Courier
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onPrintBill(order) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(EvrryNavyBorder)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = "Receipt",
                                    tint = EvrryTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Customer Bill", color = EvrryTextPrimary, fontSize = 13.sp)
                            }

                            Button(
                                onClick = { onChatCourier(order) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EvrryBlue)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Chat",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Chat Courier", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4. Completed Orders / Daily History Tab
 */
@Composable
private fun CompletedOrdersView(
    orders: List<RestaurantOrder>,
    dailyCount: Int,
    grossSalesRs: Double,
    avgPrepFormatted: String,
    onPrintBill: (RestaurantOrder) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Daily Summary Stats Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            colors = CardDefaults.cardColors(containerColor = EvrryNavyMedium),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(EvrryNavyBorder)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "TODAY'S ORDERS", fontSize = 10.sp, color = EvrryTextSecondary, fontWeight = FontWeight.Bold)
                    Text(text = "$dailyCount", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = EvrryTextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "GROSS REVENUE", fontSize = 10.sp, color = EvrryTextSecondary, fontWeight = FontWeight.Bold)
                    Text(text = "NPR ${String.format(Locale.US, "%.0f", grossSalesRs)}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = EvrryGreen)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "AVG PREP", fontSize = 10.sp, color = EvrryTextSecondary, fontWeight = FontWeight.Bold)
                    Text(text = avgPrepFormatted, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = EvrryOrange)
                }
            }
        }

        if (orders.isEmpty()) {
            EmptyKdsState(
                icon = Icons.Default.History,
                title = "No Completed Orders Today",
                subtitle = "Delivered and settled tickets will be listed here."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EvrryNavyMedium),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${order.orderNo}",
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryTextPrimary,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = order.status.label,
                                        color = if (order.status == RestaurantOrderStatus.DELIVERED) EvrryGreen else EvrryRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "${order.consumerName} · ${dateFormatter.format(Date(order.placedAt))}",
                                    color = EvrryTextSecondary,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${order.items.size} item(s) · ${order.paymentMethod}",
                                    color = EvrryTextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "NPR ${String.format(Locale.US, "%.0f", order.totalRs)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = EvrryTextPrimary
                                )
                                IconButton(
                                    onClick = { onPrintBill(order) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Print,
                                        contentDescription = "Print",
                                        tint = EvrryOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyKdsState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(EvrryNavyLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EvrryTextSecondary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = EvrryTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = EvrryTextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
