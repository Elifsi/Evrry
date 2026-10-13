package com.elifsi.evrry.partner.ui.grocery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.GroceryPartnerOrder
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.GrocerySupermarketViewModel

/**
 * Courier Handoff Coordination & In-App Chat Dialog
 * Displays courier vehicle, ETA, pickup counter bay, and multi-party messaging.
 * Elifsi Technologies Private Limited
 */
@Composable
fun CourierHandoffDialog(
    order: GroceryPartnerOrder,
    viewModel: GrocerySupermarketViewModel,
    onDismiss: () -> Unit
) {
    val chatMessages by viewModel.courierChatMessages.collectAsState()
    var messageInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🛵", fontSize = 20.sp)
                    Column {
                        Text(
                            text = "Courier Handoff",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = EvrryNavy
                        )
                        Text(
                            text = "Order #${order.orderNo}",
                            fontSize = 11.sp,
                            color = EvrryMuted
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = EvrryMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Courier Identity Card
                Surface(
                    color = EvrryPaper,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EvrryBorder, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
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
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🛵", fontSize = 18.sp)
                                }
                            }
                            Column {
                                Text(
                                    text = order.riderName ?: "Assigned Courier",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = EvrryNavy
                                )
                                Text(
                                    text = "${order.riderVehiclePlate} • ETA: ~${order.riderEtaMinutes ?: 4} min",
                                    fontSize = 11.sp,
                                    color = EvrryEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Pickup: ${order.pickupBayCode}",
                                    fontSize = 10.sp,
                                    color = EvrryMuted
                                )
                            }
                        }

                        IconButton(
                            onClick = { /* Simulated direct SIM call */ },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EvrryEmeraldBg)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call Courier", tint = EvrryEmerald, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Text(
                    text = "In-App Courier Chat:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryMuted
                )

                // Chat Messages Thread
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatMessages) { msg ->
                        val isMe = msg.sender.startsWith("You")
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                        ) {
                            Surface(
                                color = if (isMe) EvrryPrimaryBlue else EvrryWhite,
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomStart = if (isMe) 10.dp else 2.dp,
                                    bottomEnd = if (isMe) 2.dp else 10.dp
                                ),
                                shadowElevation = 1.dp
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                    Text(
                                        text = msg.text,
                                        fontSize = 12.sp,
                                        color = if (isMe) EvrryWhite else EvrryInk
                                    )
                                    Text(
                                        text = msg.time,
                                        fontSize = 8.sp,
                                        color = if (isMe) EvrryWhite.copy(alpha = 0.7f) else EvrryMuted,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }

                // Chat Input Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("Message courier...", fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                    FilledIconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                viewModel.sendCourierChatMessage(messageInput)
                                messageInput = ""
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = EvrryPrimaryBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = EvrryWhite, modifier = Modifier.size(16.dp))
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
