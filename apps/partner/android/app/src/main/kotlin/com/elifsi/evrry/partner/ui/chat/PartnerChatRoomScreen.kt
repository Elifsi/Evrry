package com.elifsi.evrry.partner.ui.chat

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
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
import com.elifsi.evrry.partner.model.PartnerSocialMessage
import com.elifsi.evrry.partner.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Driver-Passenger In-Trip Realtime Chat Room
 * Grounded in GetStream WhatsApp Clone & WhatsApp Android patterns
 * Platform: Evrry Super App (Partner Android)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerChatRoomScreen(
    passengerName: String,
    passengerPhone: String,
    rideId: String,
    messages: List<PartnerSocialMessage>,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    onCallClick: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Canned Quick Reply Chips for Drivers
    val cannedReplies = remember {
        listOf(
            "I'm waiting outside 📍",
            "I have arrived at pickup point ✅",
            "Heavy traffic, 2-3 mins delay 🚦",
            "Please confirm your exact gate number 🏠"
        )
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EvrryPrimaryBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 18.sp)
                        }
                        Column {
                            Text(passengerName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EvrryDarkCharcoal)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(EvrryGreenSuccess)
                                )
                                Text("Trip #${rideId.takeLast(6).uppercase()} · $passengerPhone", fontSize = 10.sp, color = EvrryGreenSuccess, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    IconButton(onClick = onCallClick) {
                        Icon(imageVector = Icons.Rounded.Call, contentDescription = "Call", tint = EvrryPrimaryBlue)
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
            ) {
                // Quick Canned Replies
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cannedReplies) { reply ->
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { onSendMessage(reply) }
                        ) {
                            Text(
                                text = reply,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = EvrryDarkCharcoal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Message passenger…", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = EvrrySubtleBorder,
                            focusedBorderColor = EvrryPrimaryBlue
                        ),
                        maxLines = 3
                    )

                    if (inputText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                onSendMessage(inputText.trim())
                                inputText = ""
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EvrryPrimaryBlue)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                        }
                    } else {
                        IconButton(
                            onClick = { onSendMessage("🎤 [Voice Note: 0:04]") },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF))
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice Note", tint = EvrryPrimaryBlue)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                val timeStr = timeFormat.format(Date(msg.timestamp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (msg.isFromDriver) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Surface(
                        color = if (msg.isFromDriver) Color(0xFFDCFCE7) else Color.White,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (msg.isFromDriver) 16.dp else 4.dp,
                            bottomEnd = if (msg.isFromDriver) 4.dp else 16.dp
                        ),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .border(
                                1.dp,
                                if (msg.isFromDriver) Color(0xFFBBF7D0) else EvrrySubtleBorder,
                                RoundedCornerShape(16.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = msg.text,
                                fontSize = 13.sp,
                                color = EvrryDarkCharcoal
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = timeStr,
                                    fontSize = 10.sp,
                                    color = EvrryMutedText
                                )
                                if (msg.isFromDriver) {
                                    Text("✓✓", fontSize = 10.sp, color = EvrryPrimaryBlue, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
