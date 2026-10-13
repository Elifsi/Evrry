package com.elifsi.evrry.consumer.ui.home

import androidx.compose.animation.AnimatedVisibility
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
import com.elifsi.evrry.consumer.model.VoiceAgentModel
import com.elifsi.evrry.consumer.model.VoiceAgentRegistry
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.HomeViewModel
import com.elifsi.evrry.consumer.viewmodel.VoiceState

/**
 * Flagship Home AI Concierge Screen
 * Matches 1:1 with prototype chat.png and Assistant.png specifications:
 * - Clean "Hi Rahul, what's on your mind?" empty hero state
 * - Top Bar: [ ☰ ] [ Ellie ▾ ] [ Avatar ]
 * - Bottom Floating Capsule Composer with Attach, Input, Mic, and Waveform launcher
 * - Full-screen Voice Assistant mode with pulsating waveform pill
 * - Slide-up Assistant Drawer for model selection & settings
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
    val activeModel by viewModel.activeModel.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val voiceState by viewModel.voiceState.collectAsState()
    val aiMode by viewModel.aiMode.collectAsState()
    val caption by viewModel.caption.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val inputText by viewModel.inputText.collectAsState()

    var showDrawer by remember { mutableStateOf(false) }
    var showModelMenu by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // SOFT AMBIENT GRADIENT BACKGROUND (from chat.png / Assistant.png)
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
            // TOP BAR MATCHING chat.png AND Assistant.png
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // LEFT: 2-line Hamburger Menu Button
                IconButton(
                    onClick = { showDrawer = true },
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

                // CENTER: ChatGPT-Style Minimal Model Switcher [ Ellie ▾ ]
                Box {
                    Surface(
                        onClick = { showModelMenu = true },
                        color = Color.White.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 1.dp,
                        modifier = Modifier.border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = activeModel.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EvrryDarkCharcoal
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Switch Model",
                                tint = EvrryMutedText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showModelMenu,
                        onDismissRequest = { showModelMenu = false },
                        modifier = Modifier
                            .background(Color.White)
                            .width(240.dp)
                    ) {
                        VoiceAgentRegistry.allModels.forEach { model ->
                            val isSelected = activeModel.id == model.id
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = model.name,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 13.sp,
                                                color = EvrryDarkCharcoal
                                            )
                                            Surface(
                                                color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = model.badge,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) EvrryPrimaryBlue else EvrryMutedText,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = model.description,
                                            fontSize = 10.sp,
                                            color = EvrryMutedText,
                                            maxLines = 1
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.selectModel(model)
                                    showModelMenu = false
                                },
                                trailingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = EvrryPrimaryBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                // RIGHT: Cart Button (with Badge 1, 2, 3...) & Profile Avatar Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // TOP-RIGHT CART ICON WITH BADGE (1, 2, 3...)
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

                    // Profile Avatar Button
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, EvrrySubtleBorder, CircleShape)
                    ) {
                        Text(
                            text = (displayName.takeIf { it.isNotEmpty() } ?: "R").take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EvrryPrimaryBlue
                        )
                    }
                }
            }

            // =========================================================================
            // MODE 1: CHAT SCREEN (chat.png)
            // =========================================================================
            if (aiMode == "chat") {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (messages.isEmpty()) {
                        // EMPTY HERO STATE (Exactly from chat.png)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 28.dp)
                                .offset(y = (-40).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Hi $displayName, what's on\nyour mind?",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Normal,
                                color = EvrryDarkCharcoal,
                                textAlign = TextAlign.Center,
                                lineHeight = 36.sp
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


                    // FLOATING CAPSULE COMPOSER (From chat.png)
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
                            // 1. Plus / Attachment Button
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

                            // 2. Text Input Field
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
                                // Send Button
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
                                // Mic Button
                                IconButton(
                                    onClick = {
                                        viewModel.setAiMode("assistant")
                                    },
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice",
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Waveform Launcher Button (Light-blue circle with audio wave bars)
                                Surface(
                                    onClick = {
                                        viewModel.setAiMode("assistant")
                                    },
                                    color = Color(0xFFE8F1FF),
                                    shape = CircleShape,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(2.5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.width(2.5.dp).height(10.dp).clip(RoundedCornerShape(2.dp)).background(EvrryPrimaryBlue))
                                            Box(modifier = Modifier.width(2.5.dp).height(16.dp).clip(RoundedCornerShape(2.dp)).background(EvrryPrimaryBlue))
                                            Box(modifier = Modifier.width(2.5.dp).height(8.dp).clip(RoundedCornerShape(2.dp)).background(EvrryPrimaryBlue))
                                            Box(modifier = Modifier.width(2.5.dp).height(14.dp).clip(RoundedCornerShape(2.dp)).background(EvrryPrimaryBlue))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // MODE 2: VOICE ASSISTANT SCREEN (Assistant.png)
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

                    // CENTER HERO WITH LIVE CAPTION (From Assistant.png)
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
                                VoiceState.LISTENING -> "Listening…"
                                VoiceState.THINKING -> "Thinking…"
                                VoiceState.SPEAKING -> "Speaking…"
                                else -> "Tap wave to talk"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // BOTTOM ACTION CONTROLS ROW (Assistant.png)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Upload / Attachment Button
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

                        // 2. Center Pulsating Voice Waveform Pill
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

                        // 3. Mic Mute / Unmute Button
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

                        // 4. Close X Button (Returns to Chat Mode)
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

    // =========================================================================
    // SLIDE-UP ASSISTANT DRAWER SHEET (From prototype HomeAgent.tsx)
    // =========================================================================
    if (showDrawer) {
        ModalBottomSheet(
            onDismissRequest = { showDrawer = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "evrry Assistant",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryDarkCharcoal
                            )
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = activeModel.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryPrimaryBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Everything. One Place. Powered by Gemini.",
                            fontSize = 12.sp,
                            color = EvrryMutedText
                        )
                    }
                }

                Divider(color = EvrrySubtleBorder)

                // AI Model Selection
                Text(
                    text = "AI MODEL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryMutedText,
                    letterSpacing = 1.sp
                )

                VoiceAgentRegistry.allModels.forEach { model ->
                    val isSelected = activeModel.id == model.id
                    Surface(
                        onClick = {
                            viewModel.selectModel(model)
                            showDrawer = false
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.5.dp,
                                if (isSelected) EvrryPrimaryBlue else Color.Transparent,
                                RoundedCornerShape(16.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = model.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = EvrryDarkCharcoal
                                    )
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = model.badge,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EvrryPrimaryBlue,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = model.description,
                                    fontSize = 11.sp,
                                    color = EvrryMutedText,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = EvrryPrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Quick Navigation Links
                Surface(
                    onClick = {
                        showDrawer = false
                        onProfileClick()
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = EvrryPrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "AI & App Settings",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EvrryDarkCharcoal
                            )
                        }
                        Text(
                            text = "Configure →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryPrimaryBlue
                        )
                    }
                }

                // Clear Chat Action
                if (messages.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            viewModel.clearChat()
                            showDrawer = false
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Clear Conversation",
                            color = Color(0xFFEF4444),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
