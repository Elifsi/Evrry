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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.HomeViewModel
import com.elifsi.evrry.consumer.viewmodel.VoiceState
import kotlinx.coroutines.launch

/**
 * Flagship Home AI Concierge Screen
 * GPT/Gemini-style side drawer, centered voice orb, clean chat composer.
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAgentScreen(
    viewModel: HomeViewModel,
    displayName: String = "Rahul",
    cartItemCount: Int = 0,
    onCartClick: (() -> Unit)? = null,
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val messages by viewModel.messages.collectAsState()
    val voiceState by viewModel.voiceState.collectAsState()
    val aiMode by viewModel.aiMode.collectAsState()
    val caption by viewModel.caption.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val inputText by viewModel.inputText.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 16.dp)
                ) {
                    // Drawer Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Evrry AI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryDarkCharcoal
                        )
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.clearChat()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "New Chat",
                                tint = EvrryMutedText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // New Chat Button
                    Surface(
                        onClick = {
                            scope.launch { drawerState.close() }
                            viewModel.clearChat()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New",
                                tint = EvrryDarkCharcoal,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "New conversation",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = EvrryDarkCharcoal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recent Section Label
                    Text(
                        text = "Recent",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EvrryMutedText,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )

                    // Chat History (scrollable middle)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (messages.isNotEmpty()) {
                            val firstUserMsg = messages.firstOrNull { it.isUser }?.text ?: "Conversation"
                            Surface(
                                onClick = { scope.launch { drawerState.close() } },
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFEFF6FF),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = EvrryPrimaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = firstUserMsg.take(32) + if (firstUserMsg.length > 32) "..." else "",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = EvrryDarkCharcoal,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // Bottom: Settings + Profile
                    HorizontalDivider(
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // AI & App Settings
                    Surface(
                        onClick = {
                            scope.launch { drawerState.close() }
                            onProfileClick()
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = EvrryMutedText,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "AI & App Settings",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = EvrryDarkCharcoal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Profile at very bottom
                    Surface(
                        onClick = {
                            scope.launch { drawerState.close() }
                            onProfileClick()
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(EvrryPrimaryBlue, Color(0xFF7C3AED))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (displayName.takeIf { it.isNotEmpty() } ?: "R").take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                            Column {
                                Text(
                                    text = displayName.ifEmpty { "User" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EvrryDarkCharcoal
                                )
                                Text(
                                    text = "View profile",
                                    fontSize = 11.sp,
                                    color = EvrryMutedText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    ) {
        // MAIN CONTENT
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF9FBFF),
                            Color(0xFFFFFFFF),
                            Color(0xFFE8F1FF)
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // TOP BAR: [hamburger] --- [Ellie] --- [Cart]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.width(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.5.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(EvrryDarkCharcoal)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .height(2.5.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(EvrryDarkCharcoal)
                            )
                        }
                    }

                    // Static "Ellie" label (no model switcher)
                    Text(
                        text = "Ellie",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EvrryDarkCharcoal
                    )

                    // Cart only (profile moved to sidebar)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, EvrrySubtleBorder, CircleShape)
                            .clickable { onCartClick?.invoke() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = if (cartItemCount > 0) EvrryPrimaryBlue else EvrryDarkCharcoal,
                            modifier = Modifier.size(18.dp)
                        )
                        if (cartItemCount > 0) {
                            Surface(
                                color = EvrryPrimaryBlue,
                                shape = CircleShape,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (cartItemCount > 99) "99+" else "$cartItemCount",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // MODE 1: CHAT SCREEN
                // =========================================================================
                if (aiMode == "chat") {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (messages.isEmpty()) {
                            // EMPTY HERO STATE with Voice Orb
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 28.dp)
                                    .offset(y = (-20).dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Hi $displayName, what's on\nyour mind?",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = EvrryDarkCharcoal,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 36.sp
                                )

                                Spacer(modifier = Modifier.height(40.dp))

                                // ChatGPT-Style Voice Orb
                                val infiniteTransition = rememberInfiniteTransition()
                                val glowScale by infiniteTransition.animateFloat(
                                    initialValue = 1f,
                                    targetValue = 1.15f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(2000, easing = FastOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                    )
                                )

                                Box(contentAlignment = Alignment.Center) {
                                    Box(
                                        modifier = Modifier
                                            .size(88.dp)
                                            .scale(glowScale)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        EvrryPrimaryBlue.copy(alpha = 0.15f),
                                                        Color.Transparent
                                                    )
                                                )
                                            )
                                    )
                                    Surface(
                                        onClick = { viewModel.setAiMode("assistant") },
                                        shape = CircleShape,
                                        color = EvrryPrimaryBlue,
                                        shadowElevation = 8.dp,
                                        modifier = Modifier.size(64.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Mic,
                                                contentDescription = "Voice Chat",
                                                tint = Color.White,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Tap to talk with Ellie",
                                    fontSize = 13.sp,
                                    color = EvrryMutedText,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            // TRANSCRIPT STREAM
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
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
                                                .widthIn(max = 290.dp)
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
                        }

                        // FLOATING CAPSULE COMPOSER (no mic/waveform icons)
                        Surface(
                            color = Color.White,
                            shadowElevation = 6.dp,
                            shape = RoundedCornerShape(32.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(32.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { /* Media attachment */ },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Attach",
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                TextField(
                                    value = inputText,
                                    onValueChange = { viewModel.updateInputText(it) },
                                    placeholder = {
                                        Text(
                                            "Ask Evrry...",
                                            fontSize = 15.sp,
                                            color = Color(0xFF94A3B8),
                                            fontWeight = FontWeight.Normal
                                        )
                                    },
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
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // MODE 2: VOICE ASSISTANT SCREEN
                // =========================================================================
                if (aiMode == "assistant") {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = caption.ifEmpty { "Hi $displayName, what's on\nyour mind?" },
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Normal,
                                color = EvrryDarkCharcoal,
                                textAlign = TextAlign.Center,
                                lineHeight = 35.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = when (voiceState) {
                                    VoiceState.LISTENING -> "Listening..."
                                    VoiceState.THINKING -> "Thinking..."
                                    VoiceState.SPEAKING -> "Speaking..."
                                    else -> "Tap wave to talk"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 24.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = { /* Media upload */ },
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 3.dp,
                                modifier = Modifier
                                    .size(48.dp)
                                    .border(1.dp, EvrrySubtleBorder, CircleShape)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.FileUpload,
                                        contentDescription = "Upload",
                                        tint = EvrryDarkCharcoal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            val infiniteTransition = rememberInfiniteTransition()
                            val pulseScale by infiniteTransition.animateFloat(
                                initialValue = 0.95f,
                                targetValue = 1.05f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(600, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )

                            Surface(
                                onClick = { viewModel.toggleVoiceOrb() },
                                color = Color.White,
                                shape = RoundedCornerShape(30.dp),
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(54.dp)
                                    .scale(if (voiceState != VoiceState.IDLE) pulseScale else 1.0f)
                                    .border(1.5.dp, if (voiceState != VoiceState.IDLE) EvrryPrimaryBlue else EvrrySubtleBorder, RoundedCornerShape(30.dp))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val barColor = if (voiceState != VoiceState.IDLE) EvrryPrimaryBlue else Color(0xFF94A3B8)
                                        Box(modifier = Modifier.width(3.dp).height(12.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
                                        Box(modifier = Modifier.width(3.dp).height(24.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
                                        Box(modifier = Modifier.width(3.dp).height(18.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
                                        Box(modifier = Modifier.width(3.dp).height(28.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
                                        Box(modifier = Modifier.width(3.dp).height(14.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
                                    }
                                }
                            }

                            Surface(
                                onClick = { viewModel.toggleMute() },
                                shape = CircleShape,
                                color = if (isMuted) Color(0xFFF1F5F9) else Color.White,
                                shadowElevation = 3.dp,
                                modifier = Modifier
                                    .size(48.dp)
                                    .border(1.dp, EvrrySubtleBorder, CircleShape)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = "Mute",
                                        tint = if (isMuted) Color(0xFFEF4444) else EvrryDarkCharcoal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Surface(
                                onClick = { viewModel.setAiMode("chat") },
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 3.dp,
                                modifier = Modifier
                                    .size(48.dp)
                                    .border(1.dp, EvrrySubtleBorder, CircleShape)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = EvrryDarkCharcoal,
                                        modifier = Modifier.size(20.dp)
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
