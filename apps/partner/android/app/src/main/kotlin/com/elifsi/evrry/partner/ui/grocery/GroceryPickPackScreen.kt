package com.elifsi.evrry.partner.ui.grocery

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.GroceryOrderItem
import com.elifsi.evrry.partner.model.GroceryPartnerOrder
import com.elifsi.evrry.partner.model.SubstitutionStatus
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.GrocerySupermarketViewModel

/**
 * Supermarket Pick-and-Pack Order Checklist Screen
 * Incoming grocery basket organized by department/aisle with loose item weight confirmation,
 * item-by-item pack checkoff, substitution proposals, and courier handoff coordination.
 * Grounded in Aakash901_BlinkitClone orders & checklist pattern.
 * Elifsi Technologies Private Limited
 */
@Composable
fun GroceryPickPackScreen(
    viewModel: GrocerySupermarketViewModel,
    onOpenScanner: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val selectedOrderId by viewModel.selectedOrderId.collectAsState()
    val activeOrder = remember(orders, selectedOrderId) { viewModel.getSelectedOrder() }

    var itemToWeigh by remember { mutableStateOf<GroceryOrderItem?>(null) }
    var itemToSubstitute by remember { mutableStateOf<GroceryOrderItem?>(null) }
    var showCourierChat by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EvrryPaper)
    ) {
        if (activeOrder == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active supermarket orders at this time", color = EvrryMuted)
            }
            return
        }

        // ORDER SELECTOR & STATUS HEADER
        Surface(
            color = EvrryWhite,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Order #${activeOrder.orderNo}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = EvrryNavy
                            )
                            Surface(
                                color = when (activeOrder.status) {
                                    "ready_for_pickup" -> EvrryEmeraldBg
                                    "preparing" -> Color(0xFFFEF3C7)
                                    else -> Color(0xFFEFF6FF)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = when (activeOrder.status) {
                                        "ready_for_pickup" -> "READY FOR COURIER"
                                        "preparing" -> "PICK & PACK IN PROGRESS"
                                        else -> activeOrder.status.uppercase()
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (activeOrder.status) {
                                        "ready_for_pickup" -> EvrryEmerald
                                        "preparing" -> EvrryAmberWarning
                                        else -> EvrryPrimaryBlue
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Text(
                            text = "${activeOrder.consumerName} • ${activeOrder.deliveryAddress}",
                            fontSize = 12.sp,
                            color = EvrryMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Barcode audit button
                    IconButton(
                        onClick = onOpenScanner,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(EvrryPaper)
                    ) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = "Scan Barcode",
                            tint = EvrryPrimaryBlue
                        )
                    }
                }

                if (activeOrder.customerNote != null) {
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = EvrryAmberWarning, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Note: ${activeOrder.customerNote}",
                                fontSize = 11.sp,
                                color = Color(0xFF92400E),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Progress Indicator
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pack Progress: ${activeOrder.packedItemCount} / ${activeOrder.totalItemCount} Items",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryNavy
                    )
                    Text(
                        text = "NPR ${String.format("%.2f", activeOrder.totalNpr)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = EvrryPrimaryBlue
                    )
                }
                LinearProgressIndicator(
                    progress = { if (activeOrder.totalItemCount > 0) activeOrder.packedItemCount.toFloat() / activeOrder.totalItemCount else 0f },
                    color = EvrryEmerald,
                    trackColor = EvrrySubtleBorder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }

        // COURIER HANDOFF BANNER (When Order is Ready For Pickup)
        if (activeOrder.status == "ready_for_pickup") {
            Surface(
                color = EvrryEmeraldBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFA7F3D0)),
                shape = RoundedCornerShape(0.dp)
            ) {
                Row(
                    modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = EvrryEmerald,
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🛵", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "Courier: ${activeOrder.riderName ?: "Assigned Courier"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = EvrryNavy
                            )
                            Text(
                                text = "Vehicle: ${activeOrder.riderVehiclePlate} • ETA: ~${activeOrder.riderEtaMinutes} mins",
                                fontSize = 11.sp,
                                color = EvrryEmerald,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Pickup: ${activeOrder.pickupBayCode}",
                                fontSize = 10.sp,
                                color = EvrryMuted
                            )
                        }
                    }

                    Button(
                        onClick = { showCourierChat = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryNavy),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(14.dp), tint = EvrryWhite)
                            Text("Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // CHECKLIST ITEMS ORGANIZED BY AISLE / DEPARTMENT
        val itemsBySection = remember(activeOrder.items) {
            activeOrder.items.groupBy { it.section }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp)
        ) {
            itemsBySection.forEach { (section, itemsInSection) ->
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Section / Aisle Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                        ) {
                            Text(
                                text = when (section) {
                                    "Fresh Produce" -> "🥦"
                                    "Dairy & Bakery" -> "🥛"
                                    "Pantry & Grains" -> "🍚"
                                    "Snacks & Drinks" -> "🥨"
                                    "Household" -> "🧼"
                                    else -> "🛒"
                                },
                                fontSize = 14.sp
                            )
                            Text(
                                text = section.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = EvrryMuted,
                                letterSpacing = 1.sp
                            )
                        }

                        // Items in this section
                        itemsInSection.forEach { item ->
                            PickPackItemCard(
                                item = item,
                                onTogglePacked = {
                                    viewModel.toggleItemPacked(activeOrder.id, item.id)
                                },
                                onWeighItem = {
                                    itemToWeigh = item
                                },
                                onSubstitute = {
                                    itemToSubstitute = item
                                }
                            )
                        }
                    }
                }
            }
        }

        // "ALL ITEMS PACKED" BOTTOM ACTION BAR
        Surface(
            color = EvrryWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${activeOrder.packedItemCount}/${activeOrder.totalItemCount} Packed",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EvrryNavy
                    )
                    Text(
                        text = if (activeOrder.isPackingComplete) "Ready for courier handoff" else "Check off all shelf items",
                        fontSize = 11.sp,
                        color = if (activeOrder.isPackingComplete) EvrryEmerald else EvrryMuted
                    )
                }

                Button(
                    onClick = {
                        viewModel.completeOrderPacking(activeOrder.id)
                    },
                    enabled = activeOrder.isPackingComplete && activeOrder.status != "ready_for_pickup",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EvrryEmerald,
                        disabledContainerColor = Color(0xFFCBD5E1)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            if (activeOrder.status == "ready_for_pickup") Icons.Default.Check else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EvrryWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (activeOrder.status == "ready_for_pickup") "Order Dispatched" else "All Items Packed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EvrryWhite
                        )
                    }
                }
            }
        }
    }

    // Loose Weight Input Dialog
    if (itemToWeigh != null) {
        val currentItem = itemToWeigh!!
        var weightInput by remember { mutableStateOf(currentItem.measuredWeight?.toString() ?: "1.00") }

        AlertDialog(
            onDismissRequest = { itemToWeigh = null },
            title = {
                Text(
                    text = "Confirm Measured Weight",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = EvrryNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${currentItem.name} (${currentItem.unit})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EvrryInk
                    )
                    Text(
                        text = "Enter actual measured weight on scale. Price will adjust proportionally.",
                        fontSize = 12.sp,
                        color = EvrryMuted
                    )
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight (${currentItem.unit})") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val weight = weightInput.toDoubleOrNull() ?: 1.0
                        viewModel.setMeasuredWeight(activeOrder!!.id, currentItem.id, weight)
                        itemToWeigh = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                ) {
                    Text("Save & Pack", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToWeigh = null }) {
                    Text("Cancel", color = EvrryMuted)
                }
            }
        )
    }

    // Out-of-Stock Substitution Dialog
    if (itemToSubstitute != null && activeOrder != null) {
        GrocerySubstitutionDialog(
            orderItem = itemToSubstitute!!,
            viewModel = viewModel,
            orderId = activeOrder.id,
            onDismiss = { itemToSubstitute = null }
        )
    }

    // Courier In-App Chat Modal
    if (showCourierChat && activeOrder != null) {
        CourierHandoffDialog(
            order = activeOrder,
            viewModel = viewModel,
            onDismiss = { showCourierChat = false }
        )
    }
}

@Composable
fun PickPackItemCard(
    item: GroceryOrderItem,
    onTogglePacked: () -> Unit,
    onWeighItem: () -> Unit,
    onSubstitute: () -> Unit
) {
    Surface(
        color = if (item.isPacked) Color(0xFFF8FAFC) else EvrryWhite,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (item.isPacked) Color(0xFFE2E8F0) else EvrrySubtleBorder,
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Interactive Checkbox
                Checkbox(
                    checked = item.isPacked,
                    onCheckedChange = { onTogglePacked() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = EvrryEmerald,
                        uncheckedColor = EvrryMuted
                    )
                )

                // Item Name and Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isPacked) EvrryMuted else EvrryInk,
                        textDecoration = if (item.isPacked) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = if (item.isLooseWeight && item.measuredWeight != null)
                                "Wt: ${item.measuredWeight} kg"
                            else
                                "Qty: ${item.quantity} ${item.unit}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = EvrryMuted
                        )
                        Text(
                            text = "•",
                            fontSize = 10.sp,
                            color = EvrryMuted
                        )
                        Text(
                            text = "NPR ${String.format("%.2f", item.lineTotalNpr)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryPrimaryBlue
                        )
                    }
                }

                // Action buttons (Weigh / Out-of-Stock)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (item.isLooseWeight) {
                        FilledTonalIconButton(
                            onClick = onWeighItem,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Text("⚖️", fontSize = 14.sp)
                        }
                    }

                    if (!item.isPacked) {
                        IconButton(
                            onClick = onSubstitute,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.Sync,
                                contentDescription = "Substitute",
                                modifier = Modifier.size(18.dp),
                                tint = EvrryPrimaryCoral
                            )
                        }
                    }
                }
            }

            // Substitution status badge
            if (item.substitutionStatus != SubstitutionStatus.NONE) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = when (item.substitutionStatus) {
                        SubstitutionStatus.PROPOSED -> Color(0xFFFEF3C7)
                        SubstitutionStatus.APPROVED -> EvrryEmeraldBg
                        SubstitutionStatus.REJECTED -> Color(0xFFFEE2E2)
                        else -> EvrryPaper
                    },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when (item.substitutionStatus) {
                                SubstitutionStatus.PROPOSED -> "Proposal sent: ${item.proposedSubstituteName} (NPR ${item.proposedSubstitutePriceNpr})"
                                SubstitutionStatus.APPROVED -> "Customer Approved Replacement ✅"
                                SubstitutionStatus.REJECTED -> "Customer Rejected — Item Refunded ❌"
                                else -> ""
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (item.substitutionStatus) {
                                SubstitutionStatus.PROPOSED -> Color(0xFF92400E)
                                SubstitutionStatus.APPROVED -> EvrryEmerald
                                SubstitutionStatus.REJECTED -> EvrryRedDestructive
                                else -> EvrryInk
                            }
                        )
                    }
                }
            }
        }
    }
}
