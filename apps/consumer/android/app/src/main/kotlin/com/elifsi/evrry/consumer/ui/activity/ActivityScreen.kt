package com.elifsi.evrry.consumer.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.ActivityFilter
import com.elifsi.evrry.consumer.viewmodel.ActivityViewModel
import com.elifsi.evrry.consumer.viewmodel.OrderActivityItem

import com.elifsi.evrry.consumer.ui.food.ItemizedReceiptSheet
import com.elifsi.evrry.consumer.ui.food.OrderRatingSheet

@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel,
    onBack: (() -> Unit)? = null,
    onOrderClick: (String) -> Unit = {},
    onReorderClick: (OrderActivityItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredItems = viewModel.getFilteredItems()
    var receiptOrder by remember { mutableStateOf<OrderActivityItem?>(null) }
    var ratingOrder by remember { mutableStateOf<OrderActivityItem?>(null) }

    val activeReceipt = receiptOrder
    if (activeReceipt != null) {
        ItemizedReceiptSheet(order = activeReceipt, onDismiss = { receiptOrder = null })
    }

    val activeRating = ratingOrder
    if (activeRating != null) {
        OrderRatingSheet(order = activeRating, onDismiss = { ratingOrder = null }, onSubmit = { _, _, _, _ -> })
    }

    Scaffold(
        topBar = {
            EvrryTopBar(
                title = "Activity",
                subtitle = "Orders · Bookings · Rides",
                onBack = onBack
            )
        },
        containerColor = EvrryPaper,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedFilter == ActivityFilter.ALL,
                        onClick = { viewModel.setFilter(ActivityFilter.ALL) },
                        label = { Text("All (${uiState.items.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EvrryBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.selectedFilter == ActivityFilter.ACTIVE,
                        onClick = { viewModel.setFilter(ActivityFilter.ACTIVE) },
                        label = { Text("Active") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EvrryBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.selectedFilter == ActivityFilter.COMPLETED,
                        onClick = { viewModel.setFilter(ActivityFilter.COMPLETED) },
                        label = { Text("Completed") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EvrryBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = EvrryMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No activity in this view",
                            style = MaterialTheme.typography.titleMedium,
                            color = EvrryInk
                        )
                        Text(
                            text = "Your food orders, rides, and deliveries will appear here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EvrryMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        OrderActivityCard(
                            item = item,
                            onClick = { onOrderClick(item.id) },
                            onReorder = { onReorderClick(item) },
                            onReceipt = { receiptOrder = item },
                            onRate = { ratingOrder = item }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderActivityCard(
    item: OrderActivityItem,
    onClick: () -> Unit = {},
    onReorder: () -> Unit = {},
    onReceipt: () -> Unit = {},
    onRate: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Vendor and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.vendorName,
                        style = MaterialTheme.typography.bodySmall,
                        color = EvrryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = EvrryInk,
                        fontWeight = FontWeight.Bold
                    )
                }

                StatusBadge(status = item.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details: Amount & Payment Method
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rs ${item.amountRs}",
                    style = MaterialTheme.typography.titleMedium,
                    color = EvrryInk,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Paid via ${item.paymentMethod}",
                    style = MaterialTheme.typography.bodySmall,
                    color = EvrryMuted
                )
            }

            // Rider / Live Tracking HUD (if active)
            if (item.riderName != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EvrryPaper)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (item.category == "Ride") Icons.Default.DirectionsBike else Icons.Default.DeliveryDining,
                            contentDescription = null,
                            tint = EvrryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = item.riderName,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = EvrryInk
                            )
                            if (item.vehicleNumber != null) {
                                Text(
                                    text = item.vehicleNumber,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EvrryMuted
                                )
                            }
                        }
                    }

                    if (item.rideOtp != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EvrryNavy,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "OTP: ${item.rideOtp}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else if (item.etaMinutes != null) {
                        Text(
                            text = "~${item.etaMinutes} mins",
                            style = MaterialTheme.typography.labelSmall,
                            color = EvrryEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Specialized Completed Order Action Buttons (Reorder, Receipt, Rate)
            if (item.status.lowercase() in listOf("delivered", "completed")) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onReorder,
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(38.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text("🔁 Reorder", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReceipt,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder),
                        modifier = Modifier.height(38.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text("🧾 Receipt", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                    }

                    OutlinedButton(
                        onClick = onRate,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder),
                        modifier = Modifier.height(38.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text("⭐ Rate", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFD97706))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer date
            Text(
                text = item.dateFormatted,
                style = MaterialTheme.typography.labelSmall,
                color = EvrryMuted
            )
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "kitchen" -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), "In Kitchen")
        "delivering" -> Triple(Color(0xFFDBEAFE), Color(0xFF2563EB), "On The Way")
        "delivered" -> Triple(Color(0xFFD1FAE5), Color(0xFF059669), "Delivered")
        "confirmed" -> Triple(Color(0xFFE0E7FF), Color(0xFF4338CA), "Confirmed")
        "placed" -> Triple(Color(0xFFF3F4F6), Color(0xFF4B5563), "Placed")
        else -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), status.replaceFirstChar { it.uppercase() })
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
