package com.elifsi.evrry.partner.ui.courier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.CourierChatMessage
import com.elifsi.evrry.partner.model.CourierChatTab
import com.elifsi.evrry.partner.model.CourierOrder
import com.elifsi.evrry.partner.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Multi-Party Chat Sheet for Active Delivery
 * Features 2 separate channels:
 * 1. Chat with Merchant (Store / Supermarket clerk)
 * 2. Chat with Consumer (Customer)
 *
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierMultiPartyChatSheet(
    order: CourierOrder,
    activeTab: CourierChatTab,
    storeMessages: List<CourierChatMessage>,
    customerMessages: List<CourierChatMessage>,
    onSelectTab: (CourierChatTab) -> Unit,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val currentMessages = if (activeTab == CourierChatTab.STORE) storeMessages else customerMessages
    val listState = rememberLazyListState()

    val quickReplies = if (activeTab == CourierChatTab.STORE) {
        listOf(
            "Order #${order.orderNo} ready?",
            "I have arrived at store counter",
            "Please hand over sealed package",
            "5 mins away from store"
        )
    } else {
        listOf(
            "I'm on the way with your order",
            "Arrived at your gate / entrance",
            "Please share your 4-digit OTP",
            "Please come downstairs"
        )
    }

    LaunchedEffect(currentMessages.size) {
        if (currentMessages.isNotEmpty()) {
            listState.animateScrollToItem(currentMessages.size - 1)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EvrryNavyMedium,
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "In-Delivery Dispatch Chat",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryTextPrimary
                    )
                    Text(
                        text = "Order #${order.orderNo} · Real-time coordination",
                        fontSize = 11.sp,
                        color = EvrryTextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = EvrryTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Two Tabs: Store Chat vs Customer Chat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(EvrryNavyDark)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Store Tab
                TabButton(
                    selected = activeTab == CourierChatTab.STORE,
                    icon = Icons.Default.Store,
                    title = if (order.isGrocery) "Supermarket Staff" else "Kitchen Staff",
                    subtitle = order.storeName,
                    onClick = { onSelectTab(CourierChatTab.STORE) },
                    modifier = Modifier.weight(1f)
                )

                // Customer Tab
                TabButton(
                    selected = activeTab == CourierChatTab.CUSTOMER,
                    icon = Icons.Default.Person,
                    title = "Customer",
                    subtitle = order.consumerName,
                    onClick = { onSelectTab(CourierChatTab.CUSTOMER) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Reply Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickReplies) { reply ->
                    Surface(
                        color = EvrryNavyLight,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder),
                        modifier = Modifier.clickable { onSendMessage(reply) }
                    ) {
                        Text(
                            text = reply,
                            fontSize = 11.sp,
                            color = EvrryOrange,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Messages List
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(EvrryNavyDark)
                    .padding(12.dp)
            ) {
                items(currentMessages) { msg ->
                    val isMe = msg.senderRole.equals("courier", ignoreCase = true)
                    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                            modifier = Modifier.fillMaxWidth(0.82f)
                        ) {
                            Text(
                                text = msg.senderName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMe) EvrryOrange else EvrryTextSecondary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                            Surface(
                                color = if (isMe) EvrryOrange.copy(alpha = 0.25f) else EvrryNavyLight,
                                shape = RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (isMe) 14.dp else 2.dp,
                                    bottomEnd = if (isMe) 2.dp else 14.dp
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isMe) EvrryOrange.copy(alpha = 0.5f) else EvrryNavyBorder
                                )
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                    Text(
                                        text = msg.messageText,
                                        fontSize = 13.sp,
                                        color = EvrryTextPrimary
                                    )
                                    Text(
                                        text = timeStr,
                                        fontSize = 9.sp,
                                        color = EvrryTextSecondary,
                                        modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Text Input & Send Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (activeTab == CourierChatTab.STORE) "Message store..." else "Message customer...",
                            fontSize = 13.sp,
                            color = EvrryTextSecondary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = EvrryNavyLight,
                        unfocusedContainerColor = EvrryNavyLight,
                        focusedTextColor = EvrryTextPrimary,
                        unfocusedTextColor = EvrryTextPrimary,
                        focusedBorderColor = EvrryOrange,
                        unfocusedBorderColor = EvrryNavyBorder
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(EvrryOrange)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) EvrryNavyMedium else Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, EvrryOrange) else null,
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) EvrryOrange else EvrryTextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) EvrryTextPrimary else EvrryTextSecondary
                )
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    maxLines = 1,
                    color = EvrryTextSecondary
                )
            }
        }
    }
}
