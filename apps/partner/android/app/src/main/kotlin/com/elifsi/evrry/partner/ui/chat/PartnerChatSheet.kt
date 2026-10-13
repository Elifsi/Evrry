package com.elifsi.evrry.partner.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.elifsi.evrry.partner.model.ChatMessage
import com.elifsi.evrry.partner.model.RestaurantOrder
import com.elifsi.evrry.partner.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Contextual In-Order Chat Sheet (with Consumer or Courier)
 * Platform: Android (Jetpack Compose)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerChatSheet(
    order: RestaurantOrder,
    recipientRole: String, // "consumer" or "rider"
    messages: List<ChatMessage>,
    onSendMessage: (text: String) -> Unit,
    onDismiss: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    val recipientTitle = if (recipientRole == "consumer") {
        "Customer: ${order.consumerName}"
    } else {
        "Courier: ${order.riderName ?: "Assigned Rider"}"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EvrryNavyMedium,
        scrimColor = Color.Black.copy(alpha = 0.65f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = recipientTitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryTextPrimary
                    )
                    Text(
                        text = "Order #${order.orderNo} · Direct In-Trip Dispatch Chat",
                        fontSize = 12.sp,
                        color = EvrryOrange
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

            HorizontalDivider(
                color = EvrryNavyBorder,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            // Messages List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isStore = msg.isOutgoing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isStore) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            horizontalAlignment = if (isStore) Alignment.End else Alignment.Start,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 14.dp,
                                            topEnd = 14.dp,
                                            bottomStart = if (isStore) 14.dp else 2.dp,
                                            bottomEnd = if (isStore) 2.dp else 14.dp
                                        )
                                    )
                                    .background(if (isStore) EvrryOrange else EvrryNavyLight)
                                    .border(
                                        1.dp,
                                        if (isStore) EvrryOrangeDark else EvrryNavyBorder,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = "${msg.senderName} · ${timeFormatter.format(Date(msg.timestamp))}",
                                color = EvrryTextMuted,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Send message bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Type reply...", color = EvrryTextSecondary, fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EvrryOrange,
                        unfocusedBorderColor = EvrryNavyBorder,
                        focusedTextColor = EvrryTextPrimary,
                        unfocusedTextColor = EvrryTextPrimary,
                        focusedContainerColor = EvrryNavyLight,
                        unfocusedContainerColor = EvrryNavyLight
                    ),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            onSendMessage(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .background(EvrryOrange)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
