package com.elifsi.evrry.consumer.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.CallDirection
import com.elifsi.evrry.consumer.model.CallLogItem
import com.elifsi.evrry.consumer.model.CallType
import com.elifsi.evrry.consumer.model.PhoneContactItem
import com.elifsi.evrry.consumer.model.SocialChat
import com.elifsi.evrry.consumer.model.StoryStatusItem
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.CallViewModel
import com.elifsi.evrry.consumer.viewmodel.ChatViewModel

/**
 * WhatsApp-Grade Conversation & Channels List Screen
 * Modeled after GetStream WhatsApp Clone Compose architecture & WhatsApp iOS
 * Supports Direct Chats, Active Order/Ride Coordination, Status Stories,
 * WebRTC Call History Logs, and Search.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@Composable
fun ConversationListScreen(
    chatViewModel: ChatViewModel,
    callViewModel: CallViewModel? = null,
    onChatClick: (SocialChat) -> Unit,
    onStartAudioCall: (String, String?) -> Unit = { _, _ -> },
    onStartVideoCall: (String, String?) -> Unit = { _, _ -> },
    onNewChatClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onAddStoryClick: () -> Unit = {}
) {
    val chats by chatViewModel.chats.collectAsState()
    val stories by chatViewModel.stories.collectAsState()
    val activeFilter by chatViewModel.activeFilter.collectAsState()
    val phoneContacts by chatViewModel.phoneContacts.collectAsState()
    val callLogs by (callViewModel?.callLogs ?: remember { mutableStateOf(emptyList<CallLogItem>()) }).let {
        if (callViewModel != null) callViewModel.callLogs.collectAsState()
        else remember { mutableStateOf(emptyList()) }
    }

    var activeHubTab by remember { mutableIntStateOf(0) } // 0 = Chats, 1 = Calls
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var showSelectContactSheet by remember { mutableStateOf(false) }
    var viewingStory by remember { mutableStateOf<StoryStatusItem?>(null) }

    val filteredChats = remember(chats, activeFilter, searchQuery) {
        chats.filter { chat ->
            val matchesFilter = when (activeFilter) {
                "unread" -> chat.unreadCount > 0
                "direct" -> chat.type == "DIRECT" && !chat.isGroup
                "groups" -> chat.isGroup || chat.type == "GROUP"
                "orders" -> chat.type in listOf("ORDER_SUPPORT", "RIDE_COORDINATION", "ROOM_ENQUIRY")
                else -> true
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                chat.title.contains(searchQuery, ignoreCase = true) ||
                        chat.lastMessage.contains(searchQuery, ignoreCase = true) ||
                        (chat.referenceTag?.contains(searchQuery, ignoreCase = true) ?: false)
            }
            matchesFilter && matchesSearch
        }
    }

    val filteredCallLogs = remember(callLogs, searchQuery) {
        if (searchQuery.isBlank()) callLogs
        else {
            callLogs.filter { log ->
                log.contactName.contains(searchQuery, ignoreCase = true) ||
                        (log.contactPhone?.contains(searchQuery) ?: false) ||
                        (log.subtitle?.contains(searchQuery, ignoreCase = true) ?: false)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (activeHubTab == 0) {
                        showSelectContactSheet = true
                        onNewChatClick()
                    } else {
                        // Start a new call with top partner or dialer
                        onStartAudioCall("Ramesh Thapa", "9841234567")
                    }
                },
                containerColor = EvrryPrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = if (activeHubTab == 0) Icons.Default.Chat else Icons.Default.Call,
                    contentDescription = if (activeHubTab == 0) "New Conversation" else "Start Call",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // TOP BAR
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (!isSearching) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Evrry",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EvrryPrimaryBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (activeHubTab == 0) "Chats" else "Calls",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryDarkCharcoal
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(onClick = onCameraClick) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = "Camera",
                                        tint = EvrryDarkCharcoal
                                    )
                                }
                                IconButton(onClick = { isSearching = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = EvrryDarkCharcoal
                                    )
                                }
                                IconButton(onClick = onProfileClick) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(EvrrySubtleSurface)
                                            .border(1.dp, EvrrySubtleBorder, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Profile",
                                            tint = EvrryPrimaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(onClick = {
                                    isSearching = false
                                    searchQuery = ""
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Close Search",
                                        tint = EvrryDarkCharcoal
                                    )
                                }
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = {
                                        Text(
                                            if (activeHubTab == 0) "Search chats, riders, stores..."
                                            else "Search call history & contacts...",
                                            fontSize = 14.sp
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(25.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedBorderColor = EvrryPrimaryBlue
                                    )
                                )
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            }
                        }
                    }

                    // MINIMAL CLUTTER-FREE SEGMENTED PILL: CHATS vs CALLS
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            onClick = { activeHubTab = 0 },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = if (activeHubTab == 0) Color.White else Color.Transparent,
                            shadowElevation = if (activeHubTab == 0) 1.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = if (activeHubTab == 0) EvrryPrimaryBlue else Color(0xFF64748B),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Chats",
                                    fontSize = 13.sp,
                                    fontWeight = if (activeHubTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    color = if (activeHubTab == 0) EvrryDarkCharcoal else Color(0xFF64748B)
                                )
                                val unreadSum = chats.sumOf { it.unreadCount }
                                if (unreadSum > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .background(Color(0xFF10B981), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$unreadSum",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Surface(
                            onClick = { activeHubTab = 1 },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = if (activeHubTab == 1) Color.White else Color.Transparent,
                            shadowElevation = if (activeHubTab == 1) 1.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = if (activeHubTab == 1) EvrryPrimaryBlue else Color(0xFF64748B),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Calls",
                                    fontSize = 13.sp,
                                    fontWeight = if (activeHubTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    color = if (activeHubTab == 1) EvrryDarkCharcoal else Color(0xFF64748B)
                                )
                                val missedCount = callLogs.count { it.direction == CallDirection.MISSED }
                                if (missedCount > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .background(Color(0xFFEF4444), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$missedCount",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (activeHubTab == 0) {
                        // STORIES / STATUS ROW (WhatsApp Status 24-hr circles)
                        StoriesStatusRow(
                            stories = stories,
                            onStoryClick = { story ->
                                if (story.id == "story-self") {
                                    if (story.hasUnseenStory && !story.mediaUrl.isNullOrBlank()) {
                                        viewingStory = story
                                    } else {
                                        onAddStoryClick()
                                    }
                                } else {
                                    chatViewModel.markStorySeen(story.id)
                                    viewingStory = story
                                }
                            },
                            onAddStoryClick = onAddStoryClick
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // FILTER CHIPS ROW (All, Unread, Direct, Orders & Rides)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChipItem(
                                label = "All",
                                count = chats.size,
                                isSelected = activeFilter == "all",
                                onClick = { chatViewModel.setFilter("all") }
                            )
                            FilterChipItem(
                                label = "Unread",
                                count = chats.count { it.unreadCount > 0 },
                                isSelected = activeFilter == "unread",
                                onClick = { chatViewModel.setFilter("unread") }
                            )
                            FilterChipItem(
                                label = "Direct (Friends)",
                                count = chats.count { it.type == "DIRECT" && !it.isGroup },
                                isSelected = activeFilter == "direct",
                                onClick = { chatViewModel.setFilter("direct") }
                            )
                            FilterChipItem(
                                label = "Groups",
                                count = chats.count { it.isGroup || it.type == "GROUP" },
                                isSelected = activeFilter == "groups",
                                onClick = { chatViewModel.setFilter("groups") }
                            )
                            FilterChipItem(
                                label = "Orders & Rides",
                                count = chats.count { it.type != "DIRECT" && !it.isGroup },
                                isSelected = activeFilter == "orders",
                                onClick = { chatViewModel.setFilter("orders") }
                            )
                        }
                    }

                    HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)
                }
            }

            // BODY VIEW: CHATS OR CALLS
            if (activeHubTab == 0) {
                // CHAT LIST
                if (filteredChats.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = EvrryMutedText,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No conversations found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryDarkCharcoal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Start chatting with friends, delivery riders, or stores",
                                fontSize = 13.sp,
                                color = EvrryMutedText
                            )
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredChats, key = { it.id }) { chat ->
                            ConversationItemRow(
                                chat = chat,
                                onClick = {
                                    chatViewModel.selectChat(chat)
                                    onChatClick(chat)
                                }
                            )
                            HorizontalDivider(
                                color = EvrrySubtleBorder.copy(alpha = 0.5f),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(start = 76.dp)
                            )
                        }
                    }
                }
            } else {
                // CALL HISTORY LOGS
                if (filteredCallLogs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = EvrryMutedText,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No recent calls",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryDarkCharcoal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "WebRTC voice and video calls with partners and friends appear here",
                                fontSize = 13.sp,
                                color = EvrryMutedText
                            )
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = "Recent",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryMutedText,
                                modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 6.dp)
                            )
                        }
                        items(filteredCallLogs, key = { it.id }) { callItem ->
                            CallLogItemRow(
                                item = callItem,
                                onCallClick = {
                                    if (callItem.callType == CallType.VIDEO) {
                                        onStartVideoCall(callItem.contactName, callItem.contactPhone)
                                    } else {
                                        onStartAudioCall(callItem.contactName, callItem.contactPhone)
                                    }
                                }
                            )
                            HorizontalDivider(
                                color = EvrrySubtleBorder.copy(alpha = 0.5f),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(start = 76.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    var showCreateGroupDialog by remember { mutableStateOf(false) }

    if (showSelectContactSheet) {
        SelectContactBottomSheet(
            contacts = phoneContacts,
            onDismiss = { showSelectContactSheet = false },
            onSelectContact = { contact ->
                showSelectContactSheet = false
                val chat = chatViewModel.openOrCreateDirectChatWithContact(contact)
                onChatClick(chat)
            },
            onDirectPhoneChat = { phone ->
                showSelectContactSheet = false
                val chat = chatViewModel.openOrCreateChatByPhoneNumber(phone)
                onChatClick(chat)
            },
            onCreateGroup = {
                showSelectContactSheet = false
                showCreateGroupDialog = true
            }
        )
    }

    if (showCreateGroupDialog) {
        CreateGroupDialog(
            contacts = phoneContacts,
            onDismiss = { showCreateGroupDialog = false },
            onCreateGroup = { title, participantIds ->
                showCreateGroupDialog = false
                val group = chatViewModel.createGroup(title, null, participantIds)
                onChatClick(group)
            }
        )
    }

    viewingStory?.let { story ->
        StoryViewerDialog(
            story = story,
            onDismiss = { viewingStory = null },
            onDeleteStory = {
                chatViewModel.deleteMyStory()
                viewingStory = null
            },
            onAddAnother = {
                viewingStory = null
                onAddStoryClick()
            },
            onReply = { replyMsg ->
                chatViewModel.sendStoryReply(story.userId, replyMsg)
                viewingStory = null
            }
        )
    }
}

/**
 * WhatsApp-Style "Select Contact" Address Book Bottom Sheet
 * Supports:
 * - Search by contact name, @handle or Nepal phone number (+977...)
 * - Instant "Message +977..." dynamic action when typing any phone number
 * - Quick Action buttons: New Group, New Contact, Scan QR
 * - "CONTACTS ON EVRRY" section with 1-tap direct chat initiation
 * - "INVITE TO EVRRY" section with SMS invite trigger
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectContactBottomSheet(
    contacts: List<PhoneContactItem>,
    onDismiss: () -> Unit,
    onSelectContact: (PhoneContactItem) -> Unit,
    onDirectPhoneChat: (String) -> Unit,
    onCreateGroup: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val registeredContacts = remember(contacts, searchQuery) {
        contacts.filter { it.isRegisteredOnEvrry }.filter {
            if (searchQuery.isBlank()) true else {
                it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phone.contains(searchQuery) ||
                    (it.handle?.contains(searchQuery, ignoreCase = true) ?: false)
            }
        }
    }

    val inviteContacts = remember(contacts, searchQuery) {
        contacts.filter { !it.isRegisteredOnEvrry }.filter {
            if (searchQuery.isBlank()) true else {
                it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phone.contains(searchQuery)
            }
        }
    }

    val isPhoneNumberQuery = remember(searchQuery) {
        val digits = searchQuery.filter { it.isDigit() }
        digits.length >= 7
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            // TOP BAR (WhatsApp style)
            Surface(
                color = EvrryPrimaryBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(44.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                        Text(
                            text = "Select contact",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${contacts.count { it.isRegisteredOnEvrry }} contacts on Evrry",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search name or phone number (+977...)", fontSize = 13.sp) },
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = EvrryMutedText)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = EvrryMutedText)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF1F5F9),
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = EvrryPrimaryBlue
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // DYNAMIC DIRECT PHONE MESSAGE ACTION (When typing raw phone number)
                if (isPhoneNumberQuery) {
                    item {
                        Surface(
                            onClick = { onDirectPhoneChat(searchQuery.trim()) },
                            color = Color(0xFFEFF6FF),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    color = EvrryPrimaryBlue,
                                    shape = CircleShape,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Message $searchQuery",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = EvrryPrimaryBlue
                                    )
                                    Text(
                                        text = "Start end-to-end encrypted direct chat",
                                        fontSize = 12.sp,
                                        color = EvrryMutedText
                                    )
                                }
                            }
                        }
                    }
                }

                // QUICK ACTIONS (WhatsApp Style: New Group, New Contact, Scan QR)
                item {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        QuickActionRow(
                            icon = Icons.Default.Group,
                            title = "New group",
                            subtitle = "Create family, ride or order group",
                            onClick = onCreateGroup
                        )
                        QuickActionRow(
                            icon = Icons.Default.PersonAdd,
                            title = "New contact",
                            subtitle = "Add to phonebook",
                            onClick = { /* New contact */ }
                        )
                        QuickActionRow(
                            icon = Icons.Default.QrCode,
                            title = "Scan QR Code",
                            subtitle = "Scan friend's personal QR to add instantly",
                            onClick = { /* Scan QR */ }
                        )
                    }
                    HorizontalDivider(
                        color = EvrrySubtleBorder,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                // SECTION 1: CONTACTS ON EVRRY
                if (registeredContacts.isNotEmpty()) {
                    item {
                        Text(
                            text = "CONTACTS ON EVRRY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryMutedText,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    items(registeredContacts, key = { it.id }) { contact ->
                        ContactItemRow(
                            contact = contact,
                            onClick = { onSelectContact(contact) }
                        )
                    }
                }

                // SECTION 2: INVITE TO EVRRY
                if (inviteContacts.isNotEmpty()) {
                    item {
                        Text(
                            text = "INVITE TO EVRRY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryMutedText,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }

                    items(inviteContacts, key = { it.id }) { contact ->
                        InviteContactItemRow(contact = contact)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            color = EvrryPrimaryBlue,
            shape = CircleShape,
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = EvrryDarkCharcoal
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = EvrryMutedText
            )
        }
    }
}

@Composable
private fun ContactItemRow(
    contact: PhoneContactItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // AVATAR
        Surface(
            color = EvrryPrimaryBlue.copy(alpha = 0.12f),
            shape = CircleShape,
            modifier = Modifier.size(46.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = contact.name.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = EvrryPrimaryBlue
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = contact.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = EvrryDarkCharcoal
                )
                if (contact.handle != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "@${contact.handle}",
                        fontSize = 12.sp,
                        color = EvrryPrimaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = contact.statusBio,
                fontSize = 12.sp,
                color = EvrryMutedText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = contact.phone,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun InviteContactItemRow(
    contact: PhoneContactItem
) {
    var invited by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            color = Color(0xFFF1F5F9),
            shape = CircleShape,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = contact.name.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = EvrryMutedText
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = EvrryDarkCharcoal
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = contact.phone,
                fontSize = 12.sp,
                color = EvrryMutedText
            )
        }

        OutlinedButton(
            onClick = { invited = true },
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (invited) EvrryGreenSuccess else EvrryPrimaryBlue
            )
        ) {
            Text(
                text = if (invited) "INVITED" else "INVITE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CallLogItemRow(
    item: CallLogItem,
    onCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCallClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // AVATAR
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    if (item.isDriverOrMerchant) EvrryPrimaryBlue.copy(alpha = 0.12f)
                    else Color(0xFFF1F5F9)
                )
                .border(1.dp, EvrrySubtleBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.contactName.take(2).uppercase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.isDriverOrMerchant) EvrryPrimaryBlue else EvrryDarkCharcoal
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // CONTACT NAME & CALL DIRECTION / TIME
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.contactName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.direction == CallDirection.MISSED) Color(0xFFEF4444) else EvrryDarkCharcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.isDriverOrMerchant) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Partner",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryPrimaryBlue,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = when (item.direction) {
                    CallDirection.MISSED -> Icons.AutoMirrored.Filled.CallMissed
                    CallDirection.INCOMING -> Icons.AutoMirrored.Filled.CallReceived
                    CallDirection.OUTGOING -> Icons.AutoMirrored.Filled.CallMade
                }
                val iconTint = when (item.direction) {
                    CallDirection.MISSED -> Color(0xFFEF4444)
                    CallDirection.INCOMING -> Color(0xFF0D9488)
                    CallDirection.OUTGOING -> Color(0xFF10B981)
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(13.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = item.subtitle ?: item.timestampFormatted,
                    fontSize = 13.sp,
                    color = EvrryMutedText
                )
            }
        }

        // TRAILING CALL TRIGGER BUTTON (1-TAP INSTANT REDIAL)
        IconButton(
            onClick = onCallClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (item.callType == CallType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
                contentDescription = "Call Back",
                tint = EvrryPrimaryBlue,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun StoriesStatusRow(
    stories: List<StoryStatusItem>,
    onStoryClick: (StoryStatusItem) -> Unit,
    onAddStoryClick: () -> Unit = {}
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(stories, key = { it.id }) { story ->
            val isMyStatus = story.id == "story-self"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(62.dp)
                    .clickable {
                        onStoryClick(story)
                    }
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                if (story.hasUnseenStory) EvrryPrimaryBlue else EvrrySubtleBorder
                            )
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(EvrrySubtleSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = story.userName.take(2).uppercase(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryPrimaryBlue
                        )
                    }

                    if (isMyStatus) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(EvrryPrimaryBlue)
                                .border(1.5.dp, Color.White, CircleShape)
                                .clickable { onAddStoryClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Status",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isMyStatus) "My Status" else story.userName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = EvrryDarkCharcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) EvrryPrimaryBlue else EvrrySubtleSurface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .border(
                1.dp,
                if (isSelected) EvrryPrimaryBlue else EvrrySubtleBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else EvrryDarkCharcoal
            )
            if (count > 0 && !isSelected) {
                Surface(
                    color = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(16.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = count.toString(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryMutedText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationItemRow(
    chat: SocialChat,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // AVATAR WITH ONLINE BADGE
        Box(contentAlignment = Alignment.BottomEnd) {
            val avatarBg = when {
                chat.isGroup -> Color(0xFFEFF6FF)
                chat.type == "RIDE_COORDINATION" -> Color(0xFFEFF6FF)
                chat.type == "ORDER_SUPPORT" -> Color(0xFFFEF2F2)
                else -> Color(0xFFF0FDF4)
            }
            val avatarTextColor = when {
                chat.isGroup -> EvrryPrimaryBlue
                chat.type == "RIDE_COORDINATION" -> EvrryPrimaryBlue
                chat.type == "ORDER_SUPPORT" -> Color(0xFFDC2626)
                else -> Color(0xFF16A34A)
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(avatarBg)
                    .border(1.dp, EvrrySubtleBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (chat.isGroup) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = avatarTextColor,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (chat.type == "RIDE_COORDINATION") {
                    Icon(
                        imageVector = Icons.Default.DirectionsBike,
                        contentDescription = null,
                        tint = avatarTextColor,
                        modifier = Modifier.size(26.dp)
                    )
                } else if (chat.type == "ORDER_SUPPORT") {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = avatarTextColor,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = chat.title.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = avatarTextColor
                    )
                }
            }

            if (chat.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // CHAT INFO
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = chat.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = EvrryDarkCharcoal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (chat.isGroup) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Group",
                            tint = EvrryMutedText,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Text(
                    text = chat.lastMessageTime,
                    fontSize = 11.sp,
                    color = if (chat.unreadCount > 0) EvrryPrimaryBlue else EvrryMutedText,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            // OPTIONAL CONTEXT TAG (e.g. "Food Order #482", "Moto Ride")
            if (chat.referenceTag != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    color = EvrrySubtleSurface,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = chat.referenceTag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EvrryPrimaryBlue,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // LAST MESSAGE SNIPPET & READ RECEIPTS
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Delivery Receipt Status Tick (for messages sent by me)
                    DeliveryTickIcon(status = chat.lastMessageStatus)
                    Spacer(modifier = Modifier.width(3.dp))

                    Text(
                        text = chat.lastMessage,
                        fontSize = 13.sp,
                        color = if (chat.unreadCount > 0) EvrryDarkCharcoal else EvrryMutedText,
                        fontWeight = if (chat.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // UNREAD COUNT BADGE
                if (chat.unreadCount > 0) {
                    Surface(
                        color = EvrryPrimaryBlue,
                        shape = CircleShape,
                        modifier = Modifier
                            .height(20.dp)
                            .defaultMinSize(minWidth = 20.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        ) {
                            Text(
                                text = chat.unreadCount.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryTickIcon(status: String) {
    when (status.lowercase()) {
        "sending" -> {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Sending",
                tint = EvrryMutedText,
                modifier = Modifier.size(13.dp)
            )
        }
        "sent" -> {
            // Single Grey Tick
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sent",
                tint = EvrryMutedText,
                modifier = Modifier.size(13.dp)
            )
        }
        "delivered" -> {
            // Double Grey Tick
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Delivered",
                tint = EvrryMutedText,
                modifier = Modifier.size(14.dp)
            )
        }
        "read" -> {
            // Double Blue Tick
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Read",
                tint = EvrryPrimaryBlue,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun CreateGroupDialog(
    contacts: List<PhoneContactItem>,
    onDismiss: () -> Unit,
    onCreateGroup: (String, List<String>) -> Unit
) {
    var groupTitle by remember { mutableStateOf("") }
    var selectedIds by remember { mutableStateOf(setOf<String>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Group", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EvrryDarkCharcoal) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = groupTitle,
                    onValueChange = { groupTitle = it },
                    label = { Text("Group Subject / Name") },
                    placeholder = { Text("e.g. Kathmandu Foodies") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Add participants (${selectedIds.size} selected):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)

                LazyColumn(modifier = Modifier.height(160.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val registered = contacts.filter { it.isRegisteredOnEvrry }
                    items(registered) { contact ->
                        val isSelected = selectedIds.contains(contact.id)
                        Surface(
                            onClick = {
                                selectedIds = if (isSelected) selectedIds - contact.id else selectedIds + contact.id
                            },
                            color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(color = EvrryPrimaryBlue.copy(alpha = 0.12f), shape = CircleShape, modifier = Modifier.size(28.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(contact.name.take(2).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(contact.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = EvrryDarkCharcoal)
                                }
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        selectedIds = if (checked) selectedIds + contact.id else selectedIds - contact.id
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (groupTitle.isNotBlank()) {
                        onCreateGroup(groupTitle.trim(), selectedIds.toList())
                    }
                },
                enabled = groupTitle.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = EvrryMutedText)
            }
        }
    )
}

