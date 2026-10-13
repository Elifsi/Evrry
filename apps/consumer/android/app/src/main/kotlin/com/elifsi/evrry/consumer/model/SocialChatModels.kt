package com.elifsi.evrry.consumer.model

import kotlinx.serialization.Serializable

/**
 * Social Messaging & E2EE Chat Domain Models
 * Schema matches Supabase Migration 0007 (public.chats, public.messages, public.connections)
 * UI Design based on WhatsApp Compose Reference Architecture (GetStream & WhatsApp iOS)
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */

enum class ChatType(val id: String, val label: String) {
    DIRECT("DIRECT", "Direct Message"),
    GROUP("GROUP", "Group Chat"),
    ORDER_SUPPORT("ORDER_SUPPORT", "Order Support"),
    RIDE_COORDINATION("RIDE_COORDINATION", "Ride Coordination"),
    ROOM_ENQUIRY("ROOM_ENQUIRY", "Stay / Room Enquiry")
}

enum class MessageContentType(val id: String) {
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    VOICE_NOTE("VOICE_NOTE"),
    LIVE_RIDE_SHARE("LIVE_RIDE_SHARE"),
    CALL_LOG("CALL_LOG"),
    LIVE_LOCATION("LIVE_LOCATION"),
    GIFT_ENVELOPE("GIFT_ENVELOPE"),
    STICKER("STICKER"),
    ANIMATED_EMOJI("ANIMATED_EMOJI")
}

@Serializable
data class CallLogMeta(
    val callType: String = "AUDIO", // "AUDIO" or "VIDEO"
    val durationSeconds: Int = 0,
    val isMissed: Boolean = false,
    val timestampText: String = "Just now"
)

@Serializable
data class LiveLocationMeta(
    val latitude: Double = 27.6766,
    val longitude: Double = 85.3175,
    val address: String = "Pulchowk, Lalitpur",
    val expiresAtTimeText: String = "Live for 58 mins",
    val isSharingActive: Boolean = true
)

@Serializable
data class GiftEnvelopeMeta(
    val amountNpr: Double = 500.0,
    val senderNote: String = "Happy Dashain! 🌸",
    val isClaimed: Boolean = false,
    val claimedByText: String? = null
)

@Serializable
data class ChatBackupConfig(
    val frequency: String = "Daily", // "Never", "Daily", "Weekly", "Monthly"
    val googleAccount: String = "rahul.elifsi@gmail.com",
    val backupOver: String = "Wi-Fi only", // "Wi-Fi only", "Wi-Fi or cellular"
    val includeVideos: Boolean = true,
    val isEndToEndEncrypted: Boolean = true,
    val lastBackupTimestampText: String = "Today, 3:45 PM",
    val lastBackupSizeText: String = "42.6 MB"
)

enum class MessageDeliveryStatus {
    SENDING,
    SENT,        // Single Grey Tick (Saved on server)
    DELIVERED,   // Double Grey Tick (Received on recipient device)
    READ         // Double Blue Tick (Viewed by recipient)
}

@Serializable
data class SocialMessage(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val isMe: Boolean,
    val text: String,
    val messageType: String = "TEXT",
    val mediaUrl: String? = null,
    val audioDurationSeconds: Int? = null,
    val rideMeta: RideShareMeta? = null,
    val callMeta: CallLogMeta? = null,
    val locationMeta: LiveLocationMeta? = null,
    val envelopeMeta: GiftEnvelopeMeta? = null,
    val stickerUrl: String? = null,
    val encryptedPayload: String? = null,
    val isEncrypted: Boolean = true,
    val reactions: Map<String, Int> = emptyMap(),
    val replyToId: String? = null,
    val replyToText: String? = null,
    val replyToSender: String? = null,
    val isStarred: Boolean = false,
    val status: String = "sent", // "sending", "sent", "delivered", "read"
    val timestampMs: Long = System.currentTimeMillis()
) {
    val deliveryStatus: MessageDeliveryStatus
        get() = when (status.lowercase()) {
            "sending" -> MessageDeliveryStatus.SENDING
            "delivered" -> MessageDeliveryStatus.DELIVERED
            "read" -> MessageDeliveryStatus.READ
            else -> MessageDeliveryStatus.SENT
        }
}

@Serializable
data class RideShareMeta(
    val riderName: String,
    val vehicleModel: String,
    val vehicleNumber: String,
    val etaMinutes: Int,
    val pickupAddress: String,
    val destinationAddress: String
)

@Serializable
data class StoryStatusItem(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String? = null,
    val hasUnseenStory: Boolean = true,
    val lastUpdateText: String = "Today, 11:20 AM",
    val mediaUrl: String? = null,
    val caption: String? = null,
    val createdAtMs: Long = System.currentTimeMillis()
)

@Serializable
data class GroupParticipant(
    val userId: String,
    val name: String,
    val phone: String,
    val handle: String? = null,
    val avatarUrl: String? = null,
    val role: String = "MEMBER", // "ADMIN" or "MEMBER"
    val statusBio: String = "Available on Evrry"
)

@Serializable
data class CommonGroupInfo(
    val id: String,
    val title: String,
    val membersCount: Int,
    val avatarUrl: String? = null
)

@Serializable
data class SharedMediaItem(
    val id: String,
    val type: String = "IMAGE", // "IMAGE", "VIDEO", "DOC", "LINK"
    val url: String,
    val title: String? = null,
    val timestampText: String = "Today"
)

@Serializable
data class PublicContactProfile(
    val userId: String,
    val name: String,
    val handle: String? = null,
    val phone: String,
    val isPhoneMasked: Boolean = false,
    val avatarUrl: String? = null,
    val statusBio: String = "Available on Evrry",
    val isRegisteredOnEvrry: Boolean = true,
    val isBlocked: Boolean = false,
    val isMuted: Boolean = false,
    val commonGroups: List<CommonGroupInfo> = emptyList(),
    val sharedMedia: List<SharedMediaItem> = emptyList(),
    val sharedDocsCount: Int = 2,
    val sharedLinksCount: Int = 4,
    val disappearingMessagesTimer: String = "Off" // "Off", "24 hours", "7 days", "90 days"
)

@Serializable
data class SocialChat(
    val id: String,
    val type: String = "DIRECT",
    val title: String,
    val subtitle: String? = null,
    val avatarUrl: String? = null,
    val lastMessage: String,
    val lastMessageTime: String,
    val lastMessageStatus: String = "read", // "sent", "delivered", "read"
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val isTyping: Boolean = false,
    val isMuted: Boolean = false,
    val referenceId: String? = null,       // Order ID or Ride ID for contextual support
    val referenceTag: String? = null,      // e.g. "Food Order #482", "Moto Ride (En Route)"
    val isGroup: Boolean = false,
    val groupDescription: String? = null,
    val participants: List<GroupParticipant> = emptyList(),
    val disappearingMessagesTimer: String = "Off",
    val wallpaperUrl: String? = null
)

/**
 * Address Book Phone Contact matching WhatsApp phone number contact picker
 */
@Serializable
data class PhoneContactItem(
    val id: String,
    val name: String,
    val phone: String,                      // e.g. "+977 9841112233"
    val avatarUrl: String? = null,
    val statusBio: String = "Available on Evrry",
    val isRegisteredOnEvrry: Boolean = true,
    val handle: String? = null
)
