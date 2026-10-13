package com.elifsi.evrry.consumer.ui.chat

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.elifsi.evrry.consumer.model.*
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.ui.components.EvrryLiveTrackingMap
import com.elifsi.evrry.consumer.ui.components.VehicleType
import com.elifsi.evrry.consumer.viewmodel.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * WhatsApp-Grade 1-on-1, Group & Contextual Support Chat Room Screen
 * Modeled after GetStream WhatsApp Clone & WhatsApp iOS UI
 * Features: Tail-shaped message bubbles, single/double/blue ticks, voice notes,
 * Contact/Group profile sheets, media sharing, common groups, block & report.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    chat: SocialChat,
    chatViewModel: ChatViewModel,
    onBack: () -> Unit,
    onAudioCallClick: () -> Unit = {},
    onVideoCallClick: () -> Unit = {},
    onViewOrderOrRide: (String) -> Unit = {},
    onCameraClick: () -> Unit = {}
) {
    val messages = remember(chat.id, chatViewModel.chats) {
        chatViewModel.getMessagesForChat(chat.id)
    }
    val wallpaperByChat by chatViewModel.wallpaperByChat.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showEmojiStickerSheet by remember { mutableStateOf(false) }
    var showWallpaperSheet by remember { mutableStateOf(false) }
    var wallpaperPresetId by remember { mutableStateOf<String?>(null) }
    var showCallNotice by remember { mutableStateOf<String?>(null) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showSecurityQrDialog by remember { mutableStateOf(false) }
    var profileNotice by remember { mutableStateOf<String?>(null) }
    var replyingToMessage by remember { mutableStateOf<SocialMessage?>(null) }
    var selectedMessageForAction by remember { mutableStateOf<SocialMessage?>(null) }
    var viewingImageUrl by remember { mutableStateOf<String?>(null) }
    var viewingSnap by remember { mutableStateOf<SocialMessage?>(null) }
    val listState = rememberLazyListState()

    // Sync wallpaper from ViewModel (restored across sessions)
    LaunchedEffect(chat.id, wallpaperByChat) {
        wallpaperPresetId = wallpaperByChat[chat.id]
    }

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // BACK BUTTON
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = EvrryDarkCharcoal
                        )
                    }

                    // CLICKABLE AVATAR & TITLE (OPENS WHATSAPP-GRADE CONTACT/GROUP INFO)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showProfileSheet = true }
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // AVATAR
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (chat.isGroup) Color(0xFFEFF6FF) else EvrrySubtleSurface)
                                .border(1.dp, EvrrySubtleBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (chat.isGroup) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = EvrryPrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = chat.title.take(2).uppercase(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryPrimaryBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // TITLE & SUBTITLE
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = chat.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryDarkCharcoal,
                                maxLines = 1
                            )
                            Text(
                                text = if (chat.isGroup) "${chat.participants.size.coerceAtLeast(3)} participants" else if (chat.isOnline) "Online" else chat.subtitle ?: "Offline",
                                fontSize = 11.sp,
                                color = if (chat.isOnline && !chat.isGroup) Color(0xFF16A34A) else EvrryMutedText,
                                fontWeight = if (chat.isOnline && !chat.isGroup) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }

                    // WALLPAPER PICKER BUTTON
                    IconButton(onClick = {
                        showWallpaperSheet = !showWallpaperSheet
                        showEmojiStickerSheet = false
                    }) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Chat Wallpaper",
                            tint = EvrryPrimaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // AUDIO CALL & VIDEO CALL BUTTONS (Ready for Phase 3)
                    IconButton(onClick = {
                        showCallNotice = "Voice call with ${chat.title}"
                        onAudioCallClick()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Voice Call",
                            tint = EvrryPrimaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(onClick = {
                        showCallNotice = "Video call with ${chat.title}"
                        onVideoCallClick()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = EvrryPrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        val isCustomPhoto = remember(wallpaperPresetId) {
            wallpaperPresetId?.let {
                it.startsWith("content://") || it.startsWith("file://") || it.startsWith("http") || it.startsWith("data:")
            } ?: false
        }
        val wallpaperBrush = remember(wallpaperPresetId, isCustomPhoto) {
            if (!isCustomPhoto && wallpaperPresetId != null) {
                val preset = WALLPAPER_PRESETS.firstOrNull { it.id == wallpaperPresetId } ?: WALLPAPER_PRESETS[0]
                if (preset.colors.size == 1) Brush.radialGradient(listOf(preset.colors[0], preset.colors[0]))
                else Brush.linearGradient(preset.colors)
            } else {
                Brush.linearGradient(listOf(Color(0xFFF0F4F8), Color(0xFFF0F4F8)))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (!isCustomPhoto) Modifier.background(wallpaperBrush) else Modifier)
                .padding(paddingValues)
        ) {
            if (isCustomPhoto) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(wallpaperPresetId)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // WhatsApp-style darkening tint over custom gallery photos
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.20f))
                )
            }
            Column(modifier = Modifier.fillMaxSize()) {

                // CONTEXTUAL ORDER / RIDE BANNER (If linked to order/ride)
                if (chat.referenceTag != null && chat.referenceId != null) {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (chat.type == "RIDE_COORDINATION") Icons.Default.DirectionsBike else Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = EvrryPrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = chat.referenceTag,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryDarkCharcoal
                                    )
                                    Text(
                                        text = "Direct driver/vendor operational channel",
                                        fontSize = 10.sp,
                                        color = EvrryMutedText
                                    )
                                }
                            }

                            Button(
                                onClick = { onViewOrderOrRide(chat.referenceId) },
                                colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // CALL NOTICE BANNER
                if (showCallNotice != null) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Initiating $showCallNotice (Phase 3 WebRTC Engine)...",
                                fontSize = 11.sp,
                                color = Color(0xFF92400E)
                            )
                            IconButton(
                                onClick = { showCallNotice = null },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                // DATE DIVIDER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            text = "TODAY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryMutedText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                // MESSAGE BUBBLE LIST
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(key = "e2ee-banner") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                color = Color(0xFFFEF9C3).copy(alpha = 0.95f),
                                shape = RoundedCornerShape(10.dp),
                                shadowElevation = 0.5.dp,
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFFDE047))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF854D0E),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Messages and calls are end-to-end encrypted. No one outside of this chat, not even Evrry, can read or listen to them.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF854D0E),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    items(messages, key = { it.id }) { msg ->
                        MessageBubbleRow(
                            msg = msg,
                            onCallBack = onAudioCallClick,
                            onMessageClick = { selectedMessageForAction = msg },
                            onImageClick = { url -> viewingImageUrl = url },
                            onSnapClick = { snap -> viewingSnap = snap },
                            onStopLiveLocation = { msgId -> chatViewModel.stopSharingLiveLocation(chat.id, msgId) }
                        )
                    }
                }

                // INPUT AREA (WhatsApp-Grade Bottom Bar)
                ChatInputBar(
                    text = inputText,
                    onTextChanged = { inputText = it },
                    onSendText = {
                        chatViewModel.sendMessage(chat.id, inputText, replyingToMessage)
                        inputText = ""
                        replyingToMessage = null
                    },
                    onAttachClick = { showAttachmentSheet = true },
                    onEmojiStickerClick = {
                        showEmojiStickerSheet = !showEmojiStickerSheet
                        showWallpaperSheet = false
                    },
                    onWallpaperClick = {
                        showWallpaperSheet = !showWallpaperSheet
                        showEmojiStickerSheet = false
                    },
                    emojiStickerActive = showEmojiStickerSheet,
                    wallpaperActive = showWallpaperSheet,
                    onRecordVoice = {
                        chatViewModel.sendVoiceNote(chat.id, 14)
                    },
                    replyingToMessage = replyingToMessage,
                    onCancelReply = { replyingToMessage = null }
                )

                // EMOJI / STICKER SHEET (inline, above keyboard)
                if (showEmojiStickerSheet) {
                    EmojiStickerSheet(
                        onEmojiPick = { emoji ->
                            inputText += emoji
                            showEmojiStickerSheet = false
                        },
                        onStickerPick = { url ->
                            chatViewModel.sendSticker(chat.id, url)
                            showEmojiStickerSheet = false
                        },
                        onClose = { showEmojiStickerSheet = false }
                    )
                }

                // WALLPAPER PICKER SHEET
                if (showWallpaperSheet) {
                    WallpaperPickerSheet(
                        currentWallpaperId = wallpaperPresetId,
                        onPick = { presetId ->
                            wallpaperPresetId = presetId
                            chatViewModel.setWallpaper(chat.id, presetId)
                            showWallpaperSheet = false
                        },
                        onClose = { showWallpaperSheet = false }
                    )
                }
            }

            // ATTACHMENT MODAL SHEET
            if (showAttachmentSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showAttachmentSheet = false },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = "Share with ${chat.title}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryDarkCharcoal
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            AttachmentOptionItem(
                                icon = Icons.Default.PhotoCamera,
                                label = "Camera Snap",
                                color = Color(0xFFEC4899),
                                onClick = {
                                    showAttachmentSheet = false
                                }
                            )
                            AttachmentOptionItem(
                                icon = Icons.Default.Image,
                                label = "Gallery",
                                color = Color(0xFF10B981),
                                onClick = {
                                    showAttachmentSheet = false
                                }
                            )
                            AttachmentOptionItem(
                                icon = Icons.Default.Description,
                                label = "Document",
                                color = Color(0xFF2563EB),
                                onClick = {
                                    showAttachmentSheet = false
                                }
                            )
                            AttachmentOptionItem(
                                icon = Icons.Default.Person,
                                label = "Contact",
                                color = Color(0xFF4F46E5),
                                onClick = {
                                    showAttachmentSheet = false
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            AttachmentOptionItem(
                                icon = Icons.Default.LocationOn,
                                label = "Live Location",
                                color = Color(0xFF059669),
                                onClick = {
                                    chatViewModel.sendLiveLocation(chat.id, 60)
                                    showAttachmentSheet = false
                                }
                            )
                            AttachmentOptionItem(
                                icon = Icons.Default.DirectionsBike,
                                label = "Live Ride",
                                color = EvrryPrimaryBlue,
                                onClick = {
                                    chatViewModel.sendLiveRideShare(chat.id)
                                    showAttachmentSheet = false
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    // 0. MESSAGE ACTION SHEET (Emoji Reactions & Reply / Star)
    if (selectedMessageForAction != null) {
        val targetMsg = selectedMessageForAction!!
        ModalBottomSheet(
            onDismissRequest = { selectedMessageForAction = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // EMOJI REACTION QUICK PICKER BAR
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("❤️", "👍", "😂", "😮", "😢", "🙏").forEach { emoji ->
                            Text(
                                text = emoji,
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        chatViewModel.toggleReaction(chat.id, targetMsg.id, emoji)
                                        selectedMessageForAction = null
                                    }
                                    .padding(4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ACTION: REPLY
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            replyingToMessage = targetMsg
                            selectedMessageForAction = null
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.Reply, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Text("Reply", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                }

                HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)

                // ACTION: STAR / UNSTAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            chatViewModel.toggleStarMessage(chat.id, targetMsg.id)
                            selectedMessageForAction = null
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (targetMsg.isStarred) Icons.Default.StarBorder else Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFEAB308),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        if (targetMsg.isStarred) "Unstar message" else "Star message",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EvrryDarkCharcoal
                    )
                }

                HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)

                // ACTION: COPY
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedMessageForAction = null
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = EvrryMutedText, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Text("Copy text", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // 1. CONTACT / GROUP PROFILE MODAL BOTTOM SHEET
    if (showProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProfileSheet = false },
            containerColor = Color.White
        ) {
            if (chat.isGroup) {
                // GROUP INFO VIEW
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = CircleShape,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(36.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(chat.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                        Text(
                            "Group • ${chat.participants.size.coerceAtLeast(3)} participants",
                            fontSize = 12.sp,
                            color = EvrryMutedText
                        )
                    }

                    if (chat.groupDescription != null) {
                        item {
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Group Description", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(chat.groupDescription, fontSize = 12.sp, color = EvrryDarkCharcoal)
                                }
                            }
                        }
                    }

                    // ACTION BUTTONS (Audio Call, Video Call)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(
                                onClick = {
                                    showProfileSheet = false
                                    onAudioCallClick()
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Voice Call", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    showProfileSheet = false
                                    onVideoCallClick()
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Video Call", fontSize = 12.sp)
                            }
                        }
                    }

                    // PARTICIPANTS LIST
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "${chat.participants.size.coerceAtLeast(3)} PARTICIPANTS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryMutedText
                            )
                        }
                    }

                    items(chat.participants) { participant ->
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = EvrryPrimaryBlue.copy(alpha = 0.1f),
                                        shape = CircleShape,
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(participant.name.take(2).uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(participant.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                                        Text(participant.statusBio, fontSize = 11.sp, color = EvrryMutedText)
                                    }
                                }

                                if (participant.role == "ADMIN") {
                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            "Group Admin",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EvrryGreenSuccess,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // EXIT GROUP & REPORT GROUP
                    item {
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    showProfileSheet = false
                                    showReportDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = EvrryRedDestructive, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Report Group", color = EvrryRedDestructive, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    showProfileSheet = false
                                    onBack()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EvrryRedDestructive)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Exit Group", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            } else {
                // CONTACT INFO VIEW
                val profile = remember(chat) { chatViewModel.getContactProfile(chat) }
                val blockedUsers by chatViewModel.blockedUserIds.collectAsState()
                val isBlocked = blockedUsers.contains(profile.userId)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Surface(
                            color = EvrryPrimaryBlue.copy(alpha = 0.12f),
                            shape = CircleShape,
                            modifier = Modifier.size(76.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(profile.name.take(2).uppercase(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(profile.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                        Text("@${profile.handle} • ${profile.phone}", fontSize = 12.sp, color = EvrryMutedText)
                    }

                    // QUICK ACTION BUTTONS: Audio, Video, Share
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(
                                onClick = {
                                    showProfileSheet = false
                                    onAudioCallClick()
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = EvrryPrimaryBlue)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Voice Call", fontSize = 12.sp, color = EvrryDarkCharcoal)
                            }

                            OutlinedButton(
                                onClick = {
                                    showProfileSheet = false
                                    onVideoCallClick()
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp), tint = EvrryPrimaryBlue)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Video Call", fontSize = 12.sp, color = EvrryDarkCharcoal)
                            }

                            OutlinedButton(
                                onClick = { /* Share contact */ },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = EvrryPrimaryBlue)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share", fontSize = 12.sp, color = EvrryDarkCharcoal)
                            }
                        }
                    }

                    // ABOUT & PHONE CARD
                    item {
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("ABOUT / BIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(profile.statusBio, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = EvrryDarkCharcoal)
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("PHONE NUMBER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(profile.phone, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryPrimaryBlue)
                            }
                        }
                    }

                    // MEDIA, LINKS & DOCS CARD
                    item {
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("MEDIA, LINKS & DOCS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                    Text("${profile.sharedMedia.size} items >", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    profile.sharedMedia.forEach { _ ->
                                        Surface(
                                            color = Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).height(60.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Image, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(24.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // GROUPS IN COMMON
                    item {
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "${profile.commonGroups.size} GROUPS IN COMMON",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryMutedText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                profile.commonGroups.forEach { grp ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(color = Color(0xFFEFF6FF), shape = CircleShape, modifier = Modifier.size(28.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Group, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(grp.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                                            Text("${grp.membersCount} participants", fontSize = 10.sp, color = EvrryMutedText)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECURITY & ENCRYPTION CARD
                    item {
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showSecurityQrDialog = true },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = EvrryGreenSuccess, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Encryption", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                            Text("End-to-end encrypted. Tap to verify security QR code.", fontSize = 11.sp, color = EvrryMutedText)
                                        }
                                    }
                                    Icon(Icons.Default.QrCode2, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(18.dp))
                                }

                                HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Notifications, contentDescription = null, tint = EvrryMutedText, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Mute Notifications", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                    }
                                    Switch(
                                        checked = profile.isMuted,
                                        onCheckedChange = { chatViewModel.toggleMuteChat(chat.id) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EvrryPrimaryBlue)
                                    )
                                }

                                HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)

                                // DISAPPEARING MESSAGES
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val timers = listOf("Off", "24 hours", "7 days", "90 days")
                                            val currentIndex = timers.indexOf(chat.disappearingMessagesTimer).coerceAtLeast(0)
                                            val nextTimer = timers[(currentIndex + 1) % timers.size]
                                            chatViewModel.setDisappearingTimer(chat.id, nextTimer)
                                            profileNotice = "Disappearing messages timer set to $nextTimer"
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = EvrryMutedText, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Disappearing Messages", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                            Text("Timer: ${chat.disappearingMessagesTimer}", fontSize = 11.sp, color = EvrryMutedText)
                                        }
                                    }
                                    Text(
                                        text = chat.disappearingMessagesTimer,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryPrimaryBlue
                                    )
                                }

                                HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)

                                // STARRED MESSAGES
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Starred Messages", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                    }
                                    Text(
                                        text = "${messages.count { it.isStarred }} starred",
                                        fontSize = 11.sp,
                                        color = EvrryMutedText
                                    )
                                }
                            }
                        }
                    }

                    // DANGER ZONE (BLOCK & REPORT)
                    item {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                // BLOCK USER
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val blocked = chatViewModel.toggleBlockContact(profile.userId)
                                            profileNotice = if (blocked) "Blocked ${profile.name}" else "Unblocked ${profile.name}"
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = EvrryRedDestructive, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        if (isBlocked) "Unblock ${profile.name}" else "Block ${profile.name}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryRedDestructive
                                    )
                                }

                                HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)

                                // REPORT USER
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showReportDialog = true },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = EvrryRedDestructive, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Report ${profile.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EvrryRedDestructive)
                                }
                            }
                        }

                        if (profileNotice != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(profileNotice ?: "", fontSize = 12.sp, color = EvrryGreenSuccess, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    // 2. REPORT DIALOG
    if (showReportDialog) {
        var selectedReason by remember { mutableStateOf("Spam or unsolicited messages") }
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report Contact", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("The last 5 messages will be forwarded to Evrry Trust & Safety.", fontSize = 11.sp, color = EvrryMutedText)
                    listOf("Spam or unsolicited messages", "Harassment or offensive behavior", "Scam, fraud or fake account", "Other").forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = reason }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = selectedReason == reason, onClick = { selectedReason = reason })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(reason, fontSize = 12.sp, color = EvrryDarkCharcoal)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        chatViewModel.reportContact(chat.id, selectedReason)
                        showReportDialog = false
                        profileNotice = "Report submitted. Thank you for keeping Evrry safe."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryRedDestructive)
                ) {
                    Text("Report & Block", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. SECURITY QR CODE VERIFICATION DIALOG
    if (showSecurityQrDialog) {
        Dialog(onDismissRequest = { showSecurityQrDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Verify Security Code", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Scan this QR code on ${chat.title}'s phone to verify Signal Protocol End-to-End Encryption.",
                        fontSize = 11.sp,
                        color = EvrryMutedText,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(170.dp).border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = EvrryDarkCharcoal, modifier = Modifier.size(130.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("49201 88301 77291 00293\n55410 99812 33410 77192", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showSecurityQrDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 4. FULL-SCREEN IMAGE VIEWER
    if (viewingImageUrl != null) {
        Dialog(
            onDismissRequest = { viewingImageUrl = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = viewingImageUrl,
                    contentDescription = "Full Image View",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { viewingImageUrl = null },
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }

    // 5. SNAPCHAT FULL-SCREEN EPHEMERAL SNAP VIEWER WITH COUNTDOWN TIMER
    if (viewingSnap != null) {
        val snap = viewingSnap!!
        var countdown by remember { mutableStateOf(snap.audioDurationSeconds ?: 5) }
        LaunchedEffect(snap.id) {
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            viewingSnap = null
        }

        Dialog(
            onDismissRequest = { viewingSnap = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { viewingSnap = null }
            ) {
                AsyncImage(
                    model = snap.mediaUrl ?: "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800&auto=format&fit=crop&q=80",
                    contentDescription = "Snap image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top Header: Sender name + Countdown Timer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = snap.senderName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "• Snap",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }

                    // Countdown Ring
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .border(2.dp, Color(0xFFEF4444), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$countdown",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Bottom Caption & Reply
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 36.dp, start = 20.dp, end = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (snap.text.isNotBlank() && !snap.text.startsWith("Snap")) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = snap.text,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Surface(
                        onClick = {
                            viewingSnap = null
                            onCameraClick()
                        },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("Double-tap or tap to reply with Snap 📸", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun MessageBubbleRow(
    msg: SocialMessage,
    onCallBack: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onImageClick: (String) -> Unit = {},
    onSnapClick: (SocialMessage) -> Unit = {},
    onStopLiveLocation: (String) -> Unit = {}
) {
    if (msg.messageType == "CALL_LOG") {
        InChatCallLogCard(msg = msg, onCallBack = onCallBack)
        return
    }

    val isMe = msg.isMe
    val alignment = if (isMe) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        val isSticker = msg.messageType == "STICKER" || msg.messageType == "ANIMATED_EMOJI"
        val bubbleShape = if (isMe) {
            RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp)
        } else {
            RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
        }

        val bubbleBg = if (isSticker) Color.Transparent else if (isMe) EvrryPrimaryBlue else Color.White
        val bubbleElevation = if (isSticker) 0.dp else 1.dp

        Surface(
            color = bubbleBg,
            shape = bubbleShape,
            shadowElevation = bubbleElevation,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clickable { onMessageClick() }
        ) {
            Column(modifier = Modifier.padding(horizontal = if (isSticker) 4.dp else 12.dp, vertical = if (isSticker) 4.dp else 8.dp)) {

                // QUOTED REPLY PREVIEW
                if (msg.replyToText != null) {
                    Surface(
                        color = if (isMe) Color.White.copy(alpha = 0.18f) else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(28.dp)
                                    .background(if (isMe) Color.White else EvrryPrimaryBlue, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = msg.replyToSender ?: "Reply",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMe) Color.White else EvrryPrimaryBlue
                                )
                                Text(
                                    text = msg.replyToText,
                                    fontSize = 11.sp,
                                    color = if (isMe) Color.White.copy(alpha = 0.85f) else EvrryMutedText,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                when (msg.messageType) {
                    "IMAGE" -> {
                        Column {
                            if (msg.mediaUrl != null) {
                                val context = LocalContext.current
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(msg.mediaUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Image preview",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 240.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onImageClick(msg.mediaUrl) },
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            if (msg.text.isNotBlank() && msg.text != "Photo") {
                                Text(
                                    text = msg.text,
                                    fontSize = 14.sp,
                                    color = if (isMe) Color.White else EvrryDarkCharcoal,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                    "SNAP" -> {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isMe) Color(0xFFEF4444) else Color(0xFF8B5CF6),
                            modifier = Modifier.clickable { onSnapClick(msg) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (msg.status == "read") Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isMe) "Delivered Snap" else if (msg.status == "read") "Opened Snap" else "New Snap • Tap to view (${msg.audioDurationSeconds ?: 5}s)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (msg.status == "read") "Double-tap to reply 📸" else "Disappears after viewing",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }
                    }
                    "VOICE_NOTE" -> {
                        VoiceNoteBubbleContent(msg = msg, isMe = isMe)
                    }
                    "LIVE_RIDE_SHARE" -> {
                        LiveRideShareBubbleContent(msg = msg, isMe = isMe)
                    }
                    "LIVE_LOCATION" -> {
                        LiveLocationBubbleContent(msg = msg, isMe = isMe, onStopSharing = { onStopLiveLocation(msg.id) })
                    }
                    "STICKER", "ANIMATED_EMOJI" -> {
                        if (msg.stickerUrl != null) {
                            val context = LocalContext.current
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(msg.stickerUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Sticker",
                                modifier = Modifier
                                    .size(110.dp)
                                    .padding(4.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Text(
                                text = "🎭 [Sticker]",
                                fontSize = 14.sp,
                                color = if (isMe) Color.White else EvrryDarkCharcoal
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = msg.text,
                            fontSize = 14.sp,
                            color = if (isMe) Color.White else EvrryDarkCharcoal,
                            lineHeight = 19.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // TIMESTAMP & STATUS TICKS
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .then(
                            if (isSticker) {
                                Modifier
                                    .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            } else Modifier
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val timeString = remember(msg.timestampMs) {
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(msg.timestampMs))
                    }

                    if (msg.isStarred) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Starred",
                            tint = if (isMe) Color(0xFFFDE047) else Color(0xFFEAB308),
                            modifier = Modifier.size(11.dp)
                        )
                    }

                    Text(
                        text = timeString,
                        fontSize = 10.sp,
                        color = if (isMe) Color.White.copy(alpha = 0.8f) else EvrryMutedText
                    )

                    if (isMe) {
                        DeliveryTickIcon(status = msg.status)
                    }
                }
            }
        }

        // EMOJI REACTION PILL
        if (msg.reactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .padding(horizontal = 8.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(0.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .clickable { onMessageClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                msg.reactions.forEach { (emoji, count) ->
                    Text(text = emoji, fontSize = 11.sp)
                    if (count > 1) {
                        Text(text = "$count", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceNoteBubbleContent(msg: SocialMessage, isMe: Boolean) {
    var isPlaying by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        IconButton(
            onClick = { isPlaying = !isPlaying },
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (isMe) Color.White.copy(alpha = 0.25f) else EvrrySubtleSurface)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play Voice",
                tint = if (isMe) Color.White else EvrryPrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        Column {
            // Simulated audio waveform lines
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.height(18.dp)
            ) {
                val barHeights = listOf(6, 12, 16, 8, 14, 18, 10, 15, 7, 13, 9, 17, 11, 8, 14)
                barHeights.forEach { h ->
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(h.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isMe) Color.White.copy(alpha = 0.85f) else EvrryPrimaryBlue.copy(alpha = 0.7f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "0:${msg.audioDurationSeconds ?: 12}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMe) Color.White.copy(alpha = 0.85f) else EvrryMutedText
            )
        }
    }
}

@Composable
private fun LiveRideShareBubbleContent(msg: SocialMessage, isMe: Boolean) {
    val meta = msg.rideMeta
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBike,
                contentDescription = null,
                tint = if (isMe) Color.White else EvrryPrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "LIVE MOTO TRACKING",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (isMe) Color.White else EvrryPrimaryBlue,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // LIVE 3D ISOMETRIC ROUTE MAP PREVIEW
        EvrryLiveTrackingMap(
            vendorName = "Pickup Location",
            destinationAddress = meta?.destinationAddress ?: "Sanepa Heights",
            vehicleType = VehicleType.MOTORCYCLE,
            riderName = meta?.riderName,
            etaMinutes = meta?.etaMinutes ?: 12,
            speedKmh = 28,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (meta != null) {
            Surface(
                color = if (isMe) Color.White.copy(alpha = 0.15f) else EvrrySubtleSurface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "${meta.riderName} • ${meta.vehicleNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isMe) Color.White else EvrryDarkCharcoal
                    )
                    Text(
                        text = "ETA: ${meta.etaMinutes} mins to destination",
                        fontSize = 11.sp,
                        color = if (isMe) Color.White.copy(alpha = 0.9f) else EvrryPrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "To: ${meta.destinationAddress}",
                        fontSize = 10.sp,
                        color = if (isMe) Color.White.copy(alpha = 0.8f) else EvrryMutedText,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveLocationBubbleContent(
    msg: SocialMessage,
    isMe: Boolean,
    onStopSharing: () -> Unit = {}
) {
    val meta = msg.locationMeta
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (isMe) Color.White else Color(0xFF059669),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "LIVE LOCATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (isMe) Color.White else Color(0xFF059669),
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            color = if (isMe) Color.White.copy(alpha = 0.15f) else EvrrySubtleSurface,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = meta?.address ?: "Kathmandu Valley, Nepal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isMe) Color.White else EvrryDarkCharcoal
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (meta?.isSharingActive == true) {
                        "● ${meta.expiresAtTimeText}"
                    } else {
                        "Live sharing ended"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (meta?.isSharingActive == true) {
                        if (isMe) Color(0xFF86EFAC) else Color(0xFF059669)
                    } else {
                        if (isMe) Color.White.copy(alpha = 0.7f) else EvrryMutedText
                    }
                )

                if (isMe && meta?.isSharingActive == true) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = onStopSharing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMe) Color.White.copy(alpha = 0.25f) else Color(0xFFFEE2E2),
                            contentColor = if (isMe) Color.White else Color(0xFFDC2626)
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text(text = "Stop Sharing", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendText: () -> Unit,
    onAttachClick: () -> Unit,
    onEmojiStickerClick: () -> Unit = {},
    onWallpaperClick: () -> Unit = {},
    emojiStickerActive: Boolean = false,
    wallpaperActive: Boolean = false,
    onRecordVoice: () -> Unit,
    replyingToMessage: SocialMessage? = null,
    onCancelReply: () -> Unit = {}
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // QUOTED REPLY BANNER
            if (replyingToMessage != null) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(30.dp)
                                .background(EvrryPrimaryBlue, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Replying to ${replyingToMessage.senderName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryPrimaryBlue
                            )
                            Text(
                                text = replyingToMessage.text,
                                fontSize = 11.sp,
                                color = EvrryMutedText,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = onCancelReply, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel Reply", tint = EvrryMutedText, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
            // ATTACHMENT BUTTON
            IconButton(onClick = onAttachClick) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach",
                    tint = EvrryMutedText
                )
            }

            // EMOJI / STICKER BUTTON
            IconButton(onClick = onEmojiStickerClick) {
                Icon(
                    imageVector = Icons.Default.Mood,
                    contentDescription = "Emoji & Stickers",
                    tint = if (emojiStickerActive) EvrryPrimaryBlue else EvrryMutedText
                )
            }

            // WALLPAPER THEME BUTTON
            IconButton(onClick = onWallpaperClick) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Chat Wallpaper",
                    tint = if (wallpaperActive) EvrryPrimaryBlue else EvrryMutedText
                )
            }

            // TEXT FIELD
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                placeholder = { Text("Message...", fontSize = 14.sp, color = EvrryMutedText) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp, max = 100.dp),
                shape = RoundedCornerShape(22.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = EvrrySubtleSurface,
                    focusedContainerColor = EvrrySubtleSurface,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = EvrryPrimaryBlue
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            // VOICE NOTE MIC BUTTON (Always accessible beside Send button)
            IconButton(
                onClick = onRecordVoice,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (text.isNotBlank()) Color(0xFFF1F5F9) else EvrryPrimaryBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Record Voice Note",
                    tint = if (text.isNotBlank()) EvrryDarkCharcoal else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // SEND BUTTON (Appears directly beside Voice Note when text is present)
            if (text.isNotBlank()) {
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onSendText,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(EvrryPrimaryBlue)
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
}

@Composable
private fun AttachmentOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f))
                .border(1.dp, color.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = EvrryDarkCharcoal
        )
    }
}

@Composable
private fun InChatCallLogCard(
    msg: SocialMessage,
    onCallBack: () -> Unit
) {
    val meta = msg.callMeta
    val isMissed = meta?.isMissed ?: false
    val isVideo = meta?.callType == "VIDEO"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(14.dp),
            shadowElevation = 0.5.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.clickable(onClick = onCallBack)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isMissed) Color(0xFFFEE2E2) else EvrryPrimaryBlue.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Call,
                        contentDescription = null,
                        tint = if (isMissed) Color(0xFFEF4444) else EvrryPrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = if (isMissed) {
                            if (isVideo) "Missed Video Call" else "Missed Voice Call"
                        } else {
                            val durationText = meta?.durationSeconds?.let { s ->
                                if (s > 0) " (${s / 60}m ${s % 60}s)" else ""
                            } ?: ""
                            if (isVideo) "Video Call$durationText" else "Voice Call$durationText"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isMissed) Color(0xFFEF4444) else EvrryDarkCharcoal
                    )
                    Text(
                        text = meta?.timestampText ?: "Tap to call back",
                        fontSize = 11.sp,
                        color = EvrryMutedText
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                    color = EvrryPrimaryBlue,
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call Back",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

