package com.elifsi.evrry.consumer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elifsi.evrry.consumer.model.*
import com.elifsi.evrry.consumer.security.EvrryE2EEManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Chat ViewModel managing WhatsApp-grade real-time conversations, read receipts,
 * voice notes, attachments, and order/ride contextual threads.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
class ChatViewModel : ViewModel() {

    private val _activeFilter = MutableStateFlow("all") // "all", "unread", "direct", "orders"
    val activeFilter: StateFlow<String> = _activeFilter.asStateFlow()

    private val _stories = MutableStateFlow(
        listOf(
            StoryStatusItem(
                id = "story-self",
                userId = "user-me",
                userName = "My Status",
                hasUnseenStory = false,
                lastUpdateText = "Tap to add status update",
                mediaUrl = null,
                caption = null
            ),
            StoryStatusItem(
                id = "story-1",
                userId = "user-aayush",
                userName = "Aayush",
                hasUnseenStory = true,
                lastUpdateText = "28 minutes ago",
                mediaUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=800&q=80",
                caption = "Basantapur momo walk with friends! 🥟🔥"
            ),
            StoryStatusItem(
                id = "story-2",
                userId = "user-priyanka",
                userName = "Priyanka S.",
                hasUnseenStory = true,
                lastUpdateText = "Today, 10:14 AM",
                mediaUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=800&q=80",
                caption = "Sunny skies over Kathmandu valley today 🏔️✨"
            ),
            StoryStatusItem(
                id = "story-3",
                userId = "user-bikash",
                userName = "Bikash Rider",
                hasUnseenStory = false,
                lastUpdateText = "Yesterday, 8:45 PM",
                mediaUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&q=80",
                caption = "Completed 18 deliveries across Patan! 🛵💨"
            )
        )
    )
    val stories: StateFlow<List<StoryStatusItem>> = _stories.asStateFlow()

    private val _phoneContacts = MutableStateFlow(
        listOf(
            PhoneContactItem(
                id = "cont-aayush",
                name = "Aayush Shrestha",
                phone = "+977 9841112233",
                avatarUrl = null,
                statusBio = "Momo lover • Urgent calls only",
                isRegisteredOnEvrry = true,
                handle = "aayush_s"
            ),
            PhoneContactItem(
                id = "cont-priyanka",
                name = "Priyanka Sharma",
                phone = "+977 9851234567",
                avatarUrl = null,
                statusBio = "At work • Available on chat",
                isRegisteredOnEvrry = true,
                handle = "priyanka_sharma"
            ),
            PhoneContactItem(
                id = "cont-bikash",
                name = "Bikash Tamang",
                phone = "+977 9860123456",
                avatarUrl = null,
                statusBio = "Moto Captain • On the move",
                isRegisteredOnEvrry = true,
                handle = "bikash_moto"
            ),
            PhoneContactItem(
                id = "cont-suman",
                name = "Suman Gurung",
                phone = "+977 9818765432",
                avatarUrl = null,
                statusBio = "Living life in Pokhara 🏔️",
                isRegisteredOnEvrry = true,
                handle = "suman_g"
            ),
            PhoneContactItem(
                id = "cont-sandesh",
                name = "Sandesh Karki",
                phone = "+977 9849123456",
                avatarUrl = null,
                statusBio = "Available on Evrry",
                isRegisteredOnEvrry = true,
                handle = "sandesh_k"
            ),
            PhoneContactItem(
                id = "cont-ramesh",
                name = "Ramesh Thapa",
                phone = "+977 9841234567",
                avatarUrl = null,
                statusBio = "Invite to Evrry",
                isRegisteredOnEvrry = false,
                handle = null
            ),
            PhoneContactItem(
                id = "cont-sunita",
                name = "Sunita Adhikari",
                phone = "+977 9801234567",
                avatarUrl = null,
                statusBio = "Invite to Evrry",
                isRegisteredOnEvrry = false,
                handle = null
            ),
            PhoneContactItem(
                id = "cont-binod",
                name = "Binod Pokharel",
                phone = "+977 9849876543",
                avatarUrl = null,
                statusBio = "Invite to Evrry",
                isRegisteredOnEvrry = false,
                handle = null
            )
        )
    )
    val phoneContacts: StateFlow<List<PhoneContactItem>> = _phoneContacts.asStateFlow()

    private val _chats = MutableStateFlow(
        listOf(
            SocialChat(
                id = "chat-rider-01",
                type = "RIDE_COORDINATION",
                title = "Bikash Tamang (Moto Rider)",
                subtitle = "Active Moto Ride • Ba 98 Pa 4321",
                lastMessage = "Dai, ma Pulchowk Bhatbhateni agadi aipuge!",
                lastMessageTime = "12:42 PM",
                lastMessageStatus = "read",
                unreadCount = 1,
                isOnline = true,
                isTyping = false,
                referenceId = "ride-moto-8841",
                referenceTag = "Moto Ride #8841 (In Transit)"
            ),
            SocialChat(
                id = "chat-order-01",
                type = "ORDER_SUPPORT",
                title = "Roadhouse Cafe Pulchowk",
                subtitle = "Order Support • Woodfired Pizza",
                lastMessage = "Your Woodfired Margherita Pizza is in the oven! 🍕",
                lastMessageTime = "12:35 PM",
                lastMessageStatus = "delivered",
                unreadCount = 0,
                isOnline = true,
                isTyping = false,
                referenceId = "ord-001",
                referenceTag = "Food Order #482 (Preparing)"
            ),
            SocialChat(
                id = "chat-direct-01",
                type = "DIRECT",
                title = "Aayush Shrestha",
                subtitle = "Friend • Online",
                lastMessage = "Momo khana jaane haina aaja sanjha Jhamsikhel?",
                lastMessageTime = "11:15 AM",
                lastMessageStatus = "read",
                unreadCount = 2,
                isOnline = true,
                isTyping = false
            ),
            SocialChat(
                id = "chat-direct-02",
                type = "DIRECT",
                title = "Priyanka Sharma",
                subtitle = "Last seen today at 10:30 AM",
                lastMessage = "Voice note (0:18)",
                lastMessageTime = "10:14 AM",
                lastMessageStatus = "delivered",
                unreadCount = 0,
                isOnline = false
            ),
            SocialChat(
                id = "chat-group-foodies",
                type = "GROUP",
                title = "Kathmandu Foodies & Tech",
                subtitle = "Aayush, Priyanka, Bikash, You",
                lastMessage = "Aayush: Roadhouse or Dalle for momos tonight? 🥟",
                lastMessageTime = "1:05 PM",
                lastMessageStatus = "read",
                unreadCount = 3,
                isOnline = true,
                isGroup = true,
                groupDescription = "Exploring local cafes, authentic momo spots & tech meetups across Kathmandu Valley.",
                participants = listOf(
                    GroupParticipant(userId = "cont-aayush", name = "Aayush Shrestha", phone = "+977 9841112233", handle = "aayush_s", role = "ADMIN", statusBio = "Momo lover • Urgent calls only"),
                    GroupParticipant(userId = "cont-priyanka", name = "Priyanka Sharma", phone = "+977 9851234567", handle = "priyanka_sharma", role = "MEMBER", statusBio = "At work • Available on chat"),
                    GroupParticipant(userId = "cont-bikash", name = "Bikash Tamang", phone = "+977 9860123456", handle = "bikash_moto", role = "MEMBER", statusBio = "Moto Captain • On the move"),
                    GroupParticipant(userId = "user-me", name = "You", phone = "+977 9841000000", handle = "rahul", role = "MEMBER", statusBio = "Available on Evrry")
                )
            ),
            SocialChat(
                id = "chat-group-family",
                type = "GROUP",
                title = "Family & Home Hub",
                subtitle = "Priyanka, Suman, You",
                lastMessage = "Priyanka: Fresh apples delivered, thanks!",
                lastMessageTime = "Yesterday",
                lastMessageStatus = "read",
                unreadCount = 0,
                isOnline = false,
                isGroup = true,
                groupDescription = "Family home coordination for groceries, deliveries, and airport pickups.",
                participants = listOf(
                    GroupParticipant(userId = "cont-priyanka", name = "Priyanka Sharma", phone = "+977 9851234567", handle = "priyanka_sharma", role = "ADMIN", statusBio = "At work"),
                    GroupParticipant(userId = "cont-suman", name = "Suman Gurung", phone = "+977 9818765432", handle = "suman_g", role = "MEMBER", statusBio = "In Pokhara"),
                    GroupParticipant(userId = "user-me", name = "You", phone = "+977 9841000000", handle = "rahul", role = "MEMBER", statusBio = "Available on Evrry")
                )
            ),
            SocialChat(
                id = "chat-store-mart",
                type = "ORDER_SUPPORT",
                title = "Himalayan Organic Mart",
                subtitle = "Grocery Quick-Commerce Support",
                lastMessage = "Fresh Jumla apples are packed. Rider dispatched!",
                lastMessageTime = "Yesterday",
                lastMessageStatus = "read",
                unreadCount = 0,
                isOnline = false,
                referenceId = "groc-mart-19",
                referenceTag = "Mart Order #19 (Completed)"
            )
        )
    )
    val chats: StateFlow<List<SocialChat>> = _chats.asStateFlow()

    private val _selectedChat = MutableStateFlow<SocialChat?>(null)
    val selectedChat: StateFlow<SocialChat?> = _selectedChat.asStateFlow()

    private val _backupConfig = MutableStateFlow(ChatBackupConfig())
    val backupConfig: StateFlow<ChatBackupConfig> = _backupConfig.asStateFlow()

    // Per-chat wallpaper URL (null = default tint)
    private val _wallpaperByChat = MutableStateFlow<Map<String, String>>(emptyMap())
    val wallpaperByChat: StateFlow<Map<String, String>> = _wallpaperByChat.asStateFlow()

    fun setWallpaper(chatId: String, url: String) {
        _wallpaperByChat.value = _wallpaperByChat.value + (chatId to url)
    }

    private val _backupProgress = MutableStateFlow<Float?>(null)
    val backupProgress: StateFlow<Float?> = _backupProgress.asStateFlow()

    private val _restoreProgress = MutableStateFlow<Float?>(null)
    val restoreProgress: StateFlow<Float?> = _restoreProgress.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<SocialMessage>>>(
        mapOf(
            "chat-rider-01" to listOf(
                SocialMessage(
                    id = "msg-r1",
                    chatId = "chat-rider-01",
                    senderId = "rider-bikash",
                    senderName = "Bikash Tamang",
                    isMe = false,
                    text = "Namaste sir! Ma 3 minute ma tapai ko location ma aipugchhu.",
                    messageType = "TEXT",
                    status = "read",
                    timestampMs = System.currentTimeMillis() - 360000
                ),
                SocialMessage(
                    id = "msg-r2",
                    chatId = "chat-rider-01",
                    senderId = "me",
                    senderName = "Me",
                    isMe = true,
                    text = "Huss dai, ma main gate agadi blue jacket lagayera basya chhu.",
                    messageType = "TEXT",
                    status = "read",
                    timestampMs = System.currentTimeMillis() - 240000
                ),
                SocialMessage(
                    id = "msg-r3",
                    chatId = "chat-rider-01",
                    senderId = "rider-bikash",
                    senderName = "Bikash Tamang",
                    isMe = false,
                    text = "Dai, ma Pulchowk Bhatbhateni agadi aipuge!",
                    messageType = "TEXT",
                    status = "read",
                    timestampMs = System.currentTimeMillis() - 60000
                )
            ),
            "chat-order-01" to listOf(
                SocialMessage(
                    id = "msg-o1",
                    chatId = "chat-order-01",
                    senderId = "rest-roadhouse",
                    senderName = "Roadhouse Cafe Pulchowk",
                    isMe = false,
                    text = "Namaste! Thank you for your order with Roadhouse Cafe. We have confirmed your order.",
                    messageType = "TEXT",
                    status = "read",
                    timestampMs = System.currentTimeMillis() - 900000
                ),
                SocialMessage(
                    id = "msg-o2",
                    chatId = "chat-order-01",
                    senderId = "me",
                    senderName = "Me",
                    isMe = true,
                    text = "Can you please make sure the crust is extra crispy with chili flakes on the side?",
                    messageType = "TEXT",
                    status = "read",
                    timestampMs = System.currentTimeMillis() - 600000
                ),
                SocialMessage(
                    id = "msg-o3",
                    chatId = "chat-order-01",
                    senderId = "rest-roadhouse",
                    senderName = "Roadhouse Cafe Pulchowk",
                    isMe = false,
                    text = "Your Woodfired Margherita Pizza is in the oven! 🍕 We have included extra dried oregano and chili flakes sachets.",
                    messageType = "TEXT",
                    status = "delivered",
                    timestampMs = System.currentTimeMillis() - 300000
                )
            ),
            "chat-direct-01" to listOf(
                SocialMessage(
                    id = "msg-d1",
                    chatId = "chat-direct-01",
                    senderId = "user-aayush",
                    senderName = "Aayush Shrestha",
                    isMe = false,
                    text = "K chha bro? Office sakeu?",
                    messageType = "TEXT",
                    status = "read",
                    timestampMs = System.currentTimeMillis() - 1800000
                ),
                SocialMessage(
                    id = "msg-d2",
                    chatId = "chat-direct-01",
                    senderId = "me",
                    senderName = "Me",
                    isMe = true,
                    text = "Halka baki chha, 6 baje samma sakincha hola.",
                    messageType = "TEXT",
                    status = "read",
                    timestampMs = System.currentTimeMillis() - 1200000
                ),
                SocialMessage(
                    id = "msg-d3",
                    chatId = "chat-direct-01",
                    senderId = "user-aayush",
                    senderName = "Aayush Shrestha",
                    isMe = false,
                    text = "Momo khana jaane haina aaja sanjha Jhamsikhel?",
                    messageType = "TEXT",
                    status = "read",
                    timestampMs = System.currentTimeMillis() - 600000
                ),
                SocialMessage(
                    id = "msg-d4",
                    chatId = "chat-direct-01",
                    senderId = "user-aayush",
                    senderName = "Aayush Shrestha",
                    isMe = false,
                    text = "Sharing Live Location (1 hour)",
                    messageType = "LIVE_LOCATION",
                    locationMeta = LiveLocationMeta(
                        latitude = 27.6766,
                        longitude = 85.3175,
                        address = "Jhamsikhel Cafe Hub, Lalitpur",
                        expiresAtTimeText = "Live for 52 mins",
                        isSharingActive = true
                    ),
                    status = "read",
                    timestampMs = System.currentTimeMillis() - 300000
                ),
                SocialMessage(
                    id = "msg-d5",
                    chatId = "chat-direct-01",
                    senderId = "user-aayush",
                    senderName = "Aayush Shrestha",
                    isMe = false,
                    text = "Gift Envelope: Rs. 500",
                    messageType = "GIFT_ENVELOPE",
                    envelopeMeta = GiftEnvelopeMeta(
                        amountNpr = 500.0,
                        senderNote = "Happy Dashain / Momo Treat! 🌸",
                        isClaimed = false
                    ),
                    status = "read",
                    timestampMs = System.currentTimeMillis() - 120000
                )
            )
        )
    )

    fun setFilter(filter: String) {
        _activeFilter.value = filter
    }

    fun selectChat(chat: SocialChat) {
        _selectedChat.value = chat
        markChatAsRead(chat.id)
    }

    fun selectChat(chatId: String) {
        val found = _chats.value.find { it.id == chatId }
        if (found != null) {
            selectChat(found)
        }
    }

    fun startOrOpenDriverChat(driverName: String, driverPhone: String, rideId: String? = null): SocialChat {
        val existing = _chats.value.find { it.title.contains(driverName, ignoreCase = true) }
        if (existing != null) {
            selectChat(existing)
            return existing
        }
        val newChat = SocialChat(
            id = "chat-driver-${UUID.randomUUID().toString().take(8)}",
            type = "DIRECT",
            title = driverName,
            subtitle = "Assigned Driver · $driverPhone",
            lastMessage = "Driver assigned. Coordinate pickup location here.",
            lastMessageTime = "Just now",
            lastMessageStatus = "delivered",
            unreadCount = 0,
            isOnline = true,
            referenceId = rideId,
            referenceTag = "Ride in Progress"
        )
        _chats.value = listOf(newChat) + _chats.value
        selectChat(newChat)
        return newChat
    }

    fun clearSelectedChat() {
        _selectedChat.value = null
    }

    fun markChatAsRead(chatId: String) {
        _chats.value = _chats.value.map { c ->
            if (c.id == chatId) c.copy(unreadCount = 0) else c
        }
    }

    fun getMessagesForChat(chatId: String): List<SocialMessage> {
        return _messages.value[chatId] ?: emptyList()
    }

    fun sendMessage(chatId: String, text: String, replyToMessage: SocialMessage? = null) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val newMsgId = "msg-" + UUID.randomUUID().toString().take(8)
        val encryptedPayload = EvrryE2EEManager.encrypt(trimmed, EvrryE2EEManager.getMyPublicKeyBase64())
        val outgoingMsg = SocialMessage(
            id = newMsgId,
            chatId = chatId,
            senderId = "me",
            senderName = "Me",
            isMe = true,
            text = trimmed,
            messageType = "TEXT",
            encryptedPayload = encryptedPayload,
            replyToId = replyToMessage?.id,
            replyToText = replyToMessage?.text?.take(80),
            replyToSender = replyToMessage?.senderName,
            status = "sending",
            timestampMs = System.currentTimeMillis()
        )

        // Optimistic UI update
        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.value = _messages.value + (chatId to (currentList + outgoingMsg))

        // Update chat list snippet
        _chats.value = _chats.value.map { c ->
            if (c.id == chatId) c.copy(
                lastMessage = trimmed,
                lastMessageTime = "Just now",
                lastMessageStatus = "sending"
            ) else c
        }

        // Simulate Realtime Server ACK (sent: single grey tick)
        viewModelScope.launch {
            delay(300)
            updateMessageStatus(chatId, newMsgId, "sent")

            // Simulate Recipient Device Delivery (delivered: double grey tick)
            delay(700)
            updateMessageStatus(chatId, newMsgId, "delivered")

            // Simulate Recipient Read (read: double blue tick)
            delay(1200)
            updateMessageStatus(chatId, newMsgId, "read")

            // Simulate realistic reply from counterpart
            delay(2000)
            simulateCounterpartReply(chatId, trimmed)
        }
    }

    fun toggleReaction(chatId: String, messageId: String, emoji: String) {
        val currentList = _messages.value[chatId] ?: return
        _messages.value = _messages.value + (chatId to currentList.map { msg ->
            if (msg.id == messageId) {
                val currentCount = msg.reactions[emoji] ?: 0
                val newReactions = msg.reactions.toMutableMap()
                if (currentCount > 0) {
                    newReactions.remove(emoji)
                } else {
                    newReactions[emoji] = 1
                }
                msg.copy(reactions = newReactions)
            } else msg
        })
    }

    fun toggleStarMessage(chatId: String, messageId: String): Boolean {
        var isNowStarred = false
        val currentList = _messages.value[chatId] ?: return false
        _messages.value = _messages.value + (chatId to currentList.map { msg ->
            if (msg.id == messageId) {
                isNowStarred = !msg.isStarred
                msg.copy(isStarred = isNowStarred)
            } else msg
        })
        return isNowStarred
    }

    fun setDisappearingTimer(chatId: String, timer: String) {
        _chats.value = _chats.value.map { c ->
            if (c.id == chatId) c.copy(disappearingMessagesTimer = timer) else c
        }
    }

    fun sendVoiceNote(chatId: String, durationSeconds: Int = 12) {
        val newMsgId = "voice-" + UUID.randomUUID().toString().take(8)
        val plainText = "Voice message (${durationSeconds}s)"
        val encryptedPayload = EvrryE2EEManager.encrypt(plainText, EvrryE2EEManager.getMyPublicKeyBase64())
        val outgoingMsg = SocialMessage(
            id = newMsgId,
            chatId = chatId,
            senderId = "me",
            senderName = "Me",
            isMe = true,
            text = plainText,
            messageType = "VOICE_NOTE",
            audioDurationSeconds = durationSeconds,
            encryptedPayload = encryptedPayload,
            isEncrypted = true,
            status = "sent",
            timestampMs = System.currentTimeMillis()
        )

        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.value = _messages.value + (chatId to (currentList + outgoingMsg))

        _chats.value = _chats.value.map { c ->
            if (c.id == chatId) c.copy(
                lastMessage = "🎤 Voice message (${durationSeconds}s)",
                lastMessageTime = "Just now",
                lastMessageStatus = "sent"
            ) else c
        }
    }

    fun sendSticker(chatId: String, stickerUrl: String) {
        val newMsgId = "sticker-" + UUID.randomUUID().toString().take(8)
        val outgoingMsg = SocialMessage(
            id = newMsgId,
            chatId = chatId,
            senderId = "me",
            senderName = "Me",
            isMe = true,
            text = "Sticker",
            messageType = "STICKER",
            stickerUrl = stickerUrl,
            status = "sent",
            timestampMs = System.currentTimeMillis()
        )
        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.value = _messages.value + (chatId to (currentList + outgoingMsg))
        _chats.value = _chats.value.map { c ->
            if (c.id == chatId) c.copy(
                lastMessage = "😄 Sticker",
                lastMessageTime = "Just now",
                lastMessageStatus = "sent"
            ) else c
        }
    }

    fun sendImage(chatId: String, imageUrl: String, caption: String = "") {
        val newMsgId = "media-" + UUID.randomUUID().toString().take(8)
        val plainText = caption.ifBlank { "Photo" }
        val outgoingMsg = SocialMessage(
            id = newMsgId,
            chatId = chatId,
            senderId = "me",
            senderName = "Me",
            isMe = true,
            text = plainText,
            messageType = "IMAGE",
            mediaUrl = imageUrl,
            status = "sent",
            timestampMs = System.currentTimeMillis()
        )
        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.value = _messages.value + (chatId to (currentList + outgoingMsg))
        _chats.value = _chats.value.map { c ->
            if (c.id == chatId) c.copy(
                lastMessage = "📷 $plainText",
                lastMessageTime = "Just now",
                lastMessageStatus = "sent"
            ) else c
        }
    }

    fun postStory(mediaUrl: String, caption: String = "") {
        _stories.value = _stories.value.map { s ->
            if (s.id == "story-self") {
                s.copy(
                    mediaUrl = mediaUrl,
                    caption = caption.ifBlank { null },
                    hasUnseenStory = true,
                    lastUpdateText = "Just now",
                    createdAtMs = System.currentTimeMillis()
                )
            } else s
        }
    }

    fun deleteMyStory() {
        _stories.value = _stories.value.map { s ->
            if (s.id == "story-self") {
                s.copy(
                    mediaUrl = null,
                    caption = null,
                    hasUnseenStory = false,
                    lastUpdateText = "Tap to add status update",
                    createdAtMs = System.currentTimeMillis()
                )
            } else s
        }
    }

    fun markStorySeen(storyId: String) {
        _stories.value = _stories.value.map { s ->
            if (s.id == storyId) s.copy(hasUnseenStory = false) else s
        }
    }

    fun sendStoryReply(storyUserId: String, messageText: String) {
        val targetChat = _chats.value.find { chat ->
            when (storyUserId) {
                "user-aayush" -> chat.id == "chat-direct-01" || chat.title.contains("Aayush", ignoreCase = true)
                "user-priyanka" -> chat.id == "chat-direct-02" || chat.title.contains("Priyanka", ignoreCase = true)
                "user-bikash" -> chat.id == "chat-rider-01" || chat.title.contains("Bikash", ignoreCase = true)
                else -> chat.title.contains(storyUserId, ignoreCase = true)
            }
        } ?: _chats.value.firstOrNull()

        if (targetChat != null) {
            sendMessage(targetChat.id, messageText)
        }
    }

    fun sendLiveRideShare(chatId: String) {
        val newMsgId = "ride-share-" + UUID.randomUUID().toString().take(8)
        val meta = RideShareMeta(
            riderName = "Bikash Tamang",
            vehicleModel = "Honda CB Shine 125",
            vehicleNumber = "Ba 98 Pa 4321",
            etaMinutes = 4,
            pickupAddress = "Pulchowk Main Gate, Lalitpur",
            destinationAddress = "Thamel Marg, Kathmandu"
        )
        val plainText = "Sharing my live Moto trip with Bikash Tamang (ETA: 4 mins)"
        val encryptedPayload = EvrryE2EEManager.encrypt(plainText, EvrryE2EEManager.getMyPublicKeyBase64())
        val outgoingMsg = SocialMessage(
            id = newMsgId,
            chatId = chatId,
            senderId = "me",
            senderName = "Me",
            isMe = true,
            text = plainText,
            messageType = "LIVE_RIDE_SHARE",
            rideMeta = meta,
            encryptedPayload = encryptedPayload,
            isEncrypted = true,
            status = "sent",
            timestampMs = System.currentTimeMillis()
        )

        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.value = _messages.value + (chatId to (currentList + outgoingMsg))

        _chats.value = _chats.value.map { c ->
            if (c.id == chatId) c.copy(
                lastMessage = "📍 Shared Live Ride (ETA 4 min)",
                lastMessageTime = "Just now",
                lastMessageStatus = "sent"
            ) else c
        }
    }

    fun sendLiveLocation(chatId: String, durationMinutes: Int = 60) {
        val newMsgId = "loc-" + UUID.randomUUID().toString().take(8)
        val meta = LiveLocationMeta(
            latitude = 27.6766,
            longitude = 85.3175,
            address = "Pulchowk, Lalitpur (Live)",
            expiresAtTimeText = "Live for ${durationMinutes} mins",
            isSharingActive = true
        )
        val outgoingMsg = SocialMessage(
            id = newMsgId,
            chatId = chatId,
            senderId = "me",
            senderName = "Me",
            isMe = true,
            text = "📍 Live Location (${durationMinutes}m)",
            messageType = "LIVE_LOCATION",
            locationMeta = meta,
            status = "sent",
            timestampMs = System.currentTimeMillis()
        )
        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.value = _messages.value + (chatId to (currentList + outgoingMsg))
        _chats.value = _chats.value.map { c ->
            if (c.id == chatId) c.copy(
                lastMessage = "📍 Live Location (${durationMinutes}m)",
                lastMessageTime = "Just now",
                lastMessageStatus = "sent"
            ) else c
        }
    }

    fun stopSharingLiveLocation(chatId: String, messageId: String) {
        val currentList = _messages.value[chatId] ?: return
        _messages.value = _messages.value + (chatId to currentList.map { m ->
            if (m.id == messageId && m.locationMeta != null) {
                m.copy(locationMeta = m.locationMeta.copy(isSharingActive = false, expiresAtTimeText = "Live location ended"))
            } else m
        })
    }

    fun updateBackupFrequency(frequency: String) {
        _backupConfig.value = _backupConfig.value.copy(frequency = frequency)
    }

    fun backupChatsToGoogleDrive(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _backupProgress.value = 0.05f
            delay(400)
            _backupProgress.value = 0.35f
            delay(500)
            _backupProgress.value = 0.70f
            delay(600)
            _backupProgress.value = 1.0f
            delay(300)
            _backupConfig.value = _backupConfig.value.copy(
                lastBackupTimestampText = "Just now",
                lastBackupSizeText = "43.1 MB"
            )
            _backupProgress.value = null
            onComplete()
        }
    }

    fun restoreChatsFromGoogleDrive(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _restoreProgress.value = 0.10f
            delay(500)
            _restoreProgress.value = 0.50f
            delay(600)
            _restoreProgress.value = 0.90f
            delay(400)
            _restoreProgress.value = 1.0f
            delay(300)
            _restoreProgress.value = null
            onComplete()
        }
    }

    private fun updateMessageStatus(chatId: String, messageId: String, newStatus: String) {
        val currentList = _messages.value[chatId] ?: return
        val updated = currentList.map { m ->
            if (m.id == messageId) m.copy(status = newStatus) else m
        }
        _messages.value = _messages.value + (chatId to updated)

        _chats.value = _chats.value.map { c ->
            if (c.id == chatId) c.copy(lastMessageStatus = newStatus) else c
        }
    }

    private fun simulateCounterpartReply(chatId: String, userText: String) {
        val chat = _chats.value.firstOrNull { it.id == chatId } ?: return
        val replyText = when (chat.type) {
            "RIDE_COORDINATION" -> "Huss sir, ma white helmet lagayeko chhu, right side ma horn bajauchhu."
            "ORDER_SUPPORT" -> "Sure! Chef has noted your preference. It will arrive piping hot."
            else -> when {
                "momo" in userText.lowercase() -> "Dammi plan! 6:30 ma Jhamsikhel meet garum la!"
                "bhetum" in userText.lowercase() || "meet" in userText.lowercase() -> "Done bro, location pathau na!"
                else -> "Thik chha, ma 5 min ma update garchhu!"
            }
        }

        val replyEncrypted = EvrryE2EEManager.encrypt(replyText, EvrryE2EEManager.getMyPublicKeyBase64())
        val replyMsg = SocialMessage(
            id = "msg-" + UUID.randomUUID().toString().take(8),
            chatId = chatId,
            senderId = "counterpart",
            senderName = chat.title,
            isMe = false,
            text = replyText,
            messageType = "TEXT",
            encryptedPayload = replyEncrypted,
            isEncrypted = true,
            status = "read",
            timestampMs = System.currentTimeMillis()
        )

        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.value = _messages.value + (chatId to (currentList + replyMsg))

        _chats.value = _chats.value.map { c ->
            if (c.id == chatId) c.copy(
                lastMessage = replyText,
                lastMessageTime = "Just now",
            ) else c
        }
    }

    fun openOrCreateOrderChat(orderId: String, vendorName: String, riderName: String?): SocialChat {
        val existing = _chats.value.firstOrNull { it.referenceId == orderId }
        if (existing != null) {
            selectChat(existing)
            return existing
        }
        val newChat = SocialChat(
            id = "chat-order-$orderId",
            title = riderName?.let { "$vendorName & $it" } ?: vendorName,
            type = "ORDER_SUPPORT",
            referenceId = orderId,
            referenceTag = "ORD #${orderId.takeLast(6).uppercase()}",
            lastMessage = "Order support channel opened with $vendorName and delivery partner.",
            lastMessageTime = "Just now",
            lastMessageStatus = "sent",
            unreadCount = 0,
            isOnline = true
        )
        _chats.value = listOf(newChat) + _chats.value
        _messages.value = _messages.value + (newChat.id to listOf(
            SocialMessage(
                id = "msg-welcome-$orderId",
                chatId = newChat.id,
                senderId = "system",
                senderName = "System",
                isMe = false,
                text = "Welcome to live order support for $vendorName. Rider: ${riderName ?: "Assigned soon"}.",
                messageType = "TEXT",
                status = "read",
                timestampMs = System.currentTimeMillis()
            )
        ))
        selectChat(newChat)
        return newChat
    }

    /**
     * WhatsApp-style 1-tap open or create chat with phonebook contact
     */
    fun openOrCreateDirectChatWithContact(contact: PhoneContactItem): SocialChat {
        val existing = _chats.value.firstOrNull {
            it.title == contact.name || (it.subtitle?.contains(contact.phone) == true)
        }
        if (existing != null) {
            selectChat(existing)
            return existing
        }

        val newChat = SocialChat(
            id = "chat-direct-${contact.id}",
            type = "DIRECT",
            title = contact.name,
            subtitle = contact.phone,
            avatarUrl = contact.avatarUrl,
            lastMessage = "Started a new conversation with ${contact.name}",
            lastMessageTime = "Just now",
            lastMessageStatus = "read",
            unreadCount = 0,
            isOnline = true
        )
        _chats.value = listOf(newChat) + _chats.value
        selectChat(newChat)
        return newChat
    }

    /**
     * Start direct chat with raw phone number (WhatsApp "Message +977..." feature)
     */
    fun openOrCreateChatByPhoneNumber(phoneInput: String): SocialChat {
        val digitsOnly = phoneInput.filter { it.isDigit() }
        val formattedPhone = if (phoneInput.startsWith("+")) phoneInput else "+977 $digitsOnly"
        
        val existing = _chats.value.firstOrNull {
            it.title == formattedPhone || (it.subtitle?.contains(digitsOnly) == true)
        }
        if (existing != null) {
            selectChat(existing)
            return existing
        }

        val newChat = SocialChat(
            id = "chat-direct-phone-$digitsOnly",
            type = "DIRECT",
            title = formattedPhone,
            subtitle = "Direct Mobile Chat",
            avatarUrl = null,
            lastMessage = "Tap here to send an end-to-end encrypted message",
            lastMessageTime = "Just now",
            lastMessageStatus = "sent",
            unreadCount = 0,
            isOnline = false
        )
        _chats.value = listOf(newChat) + _chats.value
        selectChat(newChat)
        return newChat
    }

    // --- CONTACT PROFILE & SAFETY (BLOCK & REPORT) ---
    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val blockedUserIds: StateFlow<Set<String>> = _blockedUserIds.asStateFlow()

    fun getContactProfile(chat: SocialChat): PublicContactProfile {
        val phoneContact = _phoneContacts.value.find { it.name.equals(chat.title, ignoreCase = true) }
        val isBlocked = _blockedUserIds.value.contains(phoneContact?.id ?: chat.id)

        val commonGroups = _chats.value.filter { it.isGroup }.map {
            CommonGroupInfo(
                id = it.id,
                title = it.title,
                membersCount = it.participants.size.coerceAtLeast(3),
                avatarUrl = it.avatarUrl
            )
        }

        val sharedMedia = listOf(
            SharedMediaItem("sm-1", "IMAGE", "https://images.unsplash.com/photo-1513104890138-7c749659a591", "Pizza Order Delivery", "Yesterday"),
            SharedMediaItem("sm-2", "IMAGE", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c", "Kathmandu Momo Gathering", "Oct 08"),
            SharedMediaItem("sm-3", "IMAGE", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd", "Burger Combo Receipt", "Oct 06")
        )

        return PublicContactProfile(
            userId = phoneContact?.id ?: chat.id,
            name = chat.title,
            handle = phoneContact?.handle ?: chat.title.lowercase().replace(" ", "_"),
            phone = phoneContact?.phone ?: "+977 9841******",
            isPhoneMasked = false,
            avatarUrl = chat.avatarUrl,
            statusBio = phoneContact?.statusBio ?: "Available on Evrry",
            isRegisteredOnEvrry = true,
            isBlocked = isBlocked,
            isMuted = chat.isMuted,
            commonGroups = commonGroups,
            sharedMedia = sharedMedia,
            sharedDocsCount = 2,
            sharedLinksCount = 4
        )
    }

    fun toggleBlockContact(userId: String): Boolean {
        val current = _blockedUserIds.value.toMutableSet()
        val willBlock = !current.contains(userId)
        if (willBlock) {
            current.add(userId)
        } else {
            current.remove(userId)
        }
        _blockedUserIds.value = current
        return willBlock
    }

    fun reportContact(userId: String, reason: String = "Spam / Harassment"): Boolean {
        val current = _blockedUserIds.value.toMutableSet()
        current.add(userId)
        _blockedUserIds.value = current
        return true
    }

    fun toggleMuteChat(chatId: String) {
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(isMuted = !it.isMuted) else it
        }
    }

    fun createGroup(title: String, description: String?, participantIds: List<String>): SocialChat {
        val selectedParticipants = _phoneContacts.value.filter { participantIds.contains(it.id) }.map {
            GroupParticipant(
                userId = it.id,
                name = it.name,
                phone = it.phone,
                handle = it.handle,
                role = "MEMBER"
            )
        }.toMutableList()

        selectedParticipants.add(
            GroupParticipant(userId = "user-me", name = "You", phone = "+977 9841000000", handle = "rahul", role = "ADMIN")
        )

        val newGroup = SocialChat(
            id = "chat-group-${UUID.randomUUID().toString().take(8)}",
            type = "GROUP",
            title = title,
            subtitle = "${selectedParticipants.size} participants",
            lastMessage = "You created group \"$title\"",
            lastMessageTime = "Just now",
            lastMessageStatus = "read",
            unreadCount = 0,
            isGroup = true,
            groupDescription = description ?: "Evrry WhatsApp-grade Group Chat",
            participants = selectedParticipants
        )

        _chats.value = listOf(newGroup) + _chats.value
        selectChat(newGroup)
        return newGroup
    }
}

