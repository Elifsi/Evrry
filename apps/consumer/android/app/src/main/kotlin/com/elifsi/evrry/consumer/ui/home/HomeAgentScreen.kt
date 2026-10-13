package com.elifsi.evrry.consumer.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.ui.components.EvrryTopBar
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.HomeViewModel
import com.elifsi.evrry.consumer.viewmodel.VoiceState

@Composable
fun HomeAgentScreen(
    viewModel: HomeViewModel,
    cartItemCount: Int = 0,
    onCartClick: (() -> Unit)? = null,
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val activeModel by viewModel.activeModel.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val voiceState by viewModel.voiceState.collectAsState()
    val inputText by viewModel.inputText.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
    ) {
        // TOP APP BAR with ChatGPT Model Switcher & Top-Right Cart Icon
        EvrryTopBar(
            isHome = true,
            activeModel = activeModel,
            onSelectModel = { viewModel.selectModel(it) },
            onMenuClick = onMenuClick,
            onProfileClick = onProfileClick,
            cartItemCount = cartItemCount,
            onCartClick = onCartClick
        )

        // CHAT TRANSCRIPT STREAM
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.isUser
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = if (isUser) EvrryPrimaryBlue else Color.White,
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        ),
                        shadowElevation = if (isUser) 0.dp else 1.dp,
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .border(
                                width = if (isUser) 0.dp else 1.dp,
                                color = if (isUser) Color.Transparent else EvrrySubtleBorder,
                                shape = RoundedCornerShape(18.dp)
                            )
                    ) {
                        Text(
                            text = msg.text,
                            fontSize = 14.sp,
                            color = if (isUser) Color.White else EvrryDarkCharcoal,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        // CENTER VOICE VISUALIZER ORB (Animated)
        if (voiceState != VoiceState.IDLE) {
            val infiniteTransition = rememberInfiniteTransition()
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = EvrryPrimaryBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .scale(pulseScale)
                        .border(1.5.dp, EvrryPrimaryBlue, RoundedCornerShape(24.dp))
                        .clickable { viewModel.toggleVoiceOrb() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🎙️", fontSize = 16.sp)
                        Text(
                            text = when (voiceState) {
                                VoiceState.LISTENING -> "Listening…"
                                VoiceState.THINKING -> "Thinking…"
                                VoiceState.SPEAKING -> "Speaking…"
                                else -> "Tap to speak"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = EvrryPrimaryBlue
                        )
                    }
                }
            }
        }

        // CAPSULE COMPOSER (Bottom)
        Surface(
            color = Color.White,
            shadowElevation = 4.dp,
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(30.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* Media attachment */ },
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Attach",
                        tint = EvrryMutedText,
                        modifier = Modifier.size(20.dp)
                    )
                }

                TextField(
                    value = inputText,
                    onValueChange = { viewModel.updateInputText(it) },
                    placeholder = { Text("Ask Evrry…", fontSize = 14.sp, color = Color(0xFF94A3B8)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                if (inputText.trim().isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.sendMessage() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EvrryPrimaryBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { viewModel.toggleVoiceOrb() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF))
                    ) {
                        Text("🎙️", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
