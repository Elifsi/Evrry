//
//  SocialChatModels.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Social Messaging & E2EE Chat Models matching Supabase 0007 schema
//

import Foundation

public enum ChatType: String, Codable {
    case direct = "DIRECT"
    case group = "GROUP"
    case orderSupport = "ORDER_SUPPORT"
    case rideCoordination = "RIDE_COORDINATION"
    case roomEnquiry = "ROOM_ENQUIRY"
}

public enum MessageContentType: String, Codable {
    case text = "TEXT"
    case image = "IMAGE"
    case voiceNote = "VOICE_NOTE"
    case liveRideShare = "LIVE_RIDE_SHARE"
    case callLog = "CALL_LOG"
    case liveLocation = "LIVE_LOCATION"
    case giftEnvelope = "GIFT_ENVELOPE"
    case sticker = "STICKER"
    case animatedEmoji = "ANIMATED_EMOJI"
}

public struct CallLogMeta: Codable, Equatable {
    public let callType: String
    public let durationSeconds: Int
    public let isMissed: Bool
    public let timestampText: String

    public init(
        callType: String = "AUDIO",
        durationSeconds: Int = 0,
        isMissed: Bool = false,
        timestampText: String = "Just now"
    ) {
        self.callType = callType
        self.durationSeconds = durationSeconds
        self.isMissed = isMissed
        self.timestampText = timestampText
    }
}

public struct LiveLocationMeta: Codable, Equatable {
    public let latitude: Double
    public let longitude: Double
    public let address: String
    public let expiresAtTimeText: String
    public var isSharingActive: Bool

    public init(
        latitude: Double = 27.6766,
        longitude: Double = 85.3175,
        address: String = "Pulchowk, Lalitpur",
        expiresAtTimeText: String = "Live for 58 mins",
        isSharingActive: Bool = true
    ) {
        self.latitude = latitude
        self.longitude = longitude
        self.address = address
        self.expiresAtTimeText = expiresAtTimeText
        self.isSharingActive = isSharingActive
    }
}

public struct GiftEnvelopeMeta: Codable, Equatable {
    public let amountNpr: Double
    public let senderNote: String
    public var isClaimed: Bool
    public var claimedByText: String?

    public init(
        amountNpr: Double = 500.0,
        senderNote: String = "Happy Dashain! 🌸",
        isClaimed: Bool = false,
        claimedByText: String? = nil
    ) {
        self.amountNpr = amountNpr
        self.senderNote = senderNote
        self.isClaimed = isClaimed
        self.claimedByText = claimedByText
    }
}

public struct ChatBackupConfig: Codable, Equatable {
    public var frequency: String // "Never", "Daily", "Weekly", "Monthly"
    public var googleAccount: String
    public var backupOver: String
    public var includeVideos: Bool
    public var isEndToEndEncrypted: Bool
    public var lastBackupTimestampText: String
    public var lastBackupSizeText: String

    public init(
        frequency: String = "Daily",
        googleAccount: String = "rahul.elifsi@gmail.com",
        backupOver: String = "Wi-Fi only",
        includeVideos: Bool = true,
        isEndToEndEncrypted: Bool = true,
        lastBackupTimestampText: String = "Today, 3:45 PM",
        lastBackupSizeText: String = "42.6 MB"
    ) {
        self.frequency = frequency
        self.googleAccount = googleAccount
        self.backupOver = backupOver
        self.includeVideos = includeVideos
        self.isEndToEndEncrypted = isEndToEndEncrypted
        self.lastBackupTimestampText = lastBackupTimestampText
        self.lastBackupSizeText = lastBackupSizeText
    }
}

public enum MessageDeliveryStatus: String, Codable {
    case sending = "sending"
    case sent = "sent"          // Single Grey Tick
    case delivered = "delivered" // Double Grey Tick
    case read = "read"          // Double Blue Tick
}

public struct RideShareMeta: Codable, Equatable {
    public let riderName: String
    public let vehicleModel: String
    public let vehicleNumber: String
    public let etaMinutes: Int
    public let pickupAddress: String
    public let destinationAddress: String

    public init(
        riderName: String,
        vehicleModel: String,
        vehicleNumber: String,
        etaMinutes: Int,
        pickupAddress: String,
        destinationAddress: String
    ) {
        self.riderName = riderName
        self.vehicleModel = vehicleModel
        self.vehicleNumber = vehicleNumber
        self.etaMinutes = etaMinutes
        self.pickupAddress = pickupAddress
        self.destinationAddress = destinationAddress
    }
}

public struct SocialMessage: Identifiable, Codable, Equatable {
    public let id: String
    public let chatId: String
    public let senderId: String
    public let senderName: String
    public let isMe: BooleanLiteralType
    public let text: String
    public let messageType: String
    public let mediaUrl: String?
    public let audioDurationSeconds: Int?
    public let rideMeta: RideShareMeta?
    public let callMeta: CallLogMeta?
    public let locationMeta: LiveLocationMeta?
    public let envelopeMeta: GiftEnvelopeMeta?
    public let stickerUrl: String?
    public var encryptedPayload: String?
    public var isEncrypted: Bool
    public var reactions: [String: Int]
    public let replyToId: String?
    public let replyToText: String?
    public let replyToSender: String?
    public var isStarred: Bool
    public var status: String
    public let timestampMs: Int64

    public init(
        id: String = UUID().uuidString,
        chatId: String,
        senderId: String,
        senderName: String,
        isMe: Bool,
        text: String,
        messageType: String = "TEXT",
        mediaUrl: String? = nil,
        audioDurationSeconds: Int? = nil,
        rideMeta: RideShareMeta? = nil,
        callMeta: CallLogMeta? = nil,
        locationMeta: LiveLocationMeta? = nil,
        envelopeMeta: GiftEnvelopeMeta? = nil,
        stickerUrl: String? = nil,
        encryptedPayload: String? = nil,
        isEncrypted: Bool = true,
        reactions: [String: Int] = [:],
        replyToId: String? = nil,
        replyToText: String? = nil,
        replyToSender: String? = nil,
        isStarred: Bool = false,
        status: String = "sent",
        timestampMs: Int64 = Int64(Date().timeIntervalSince1970 * 1000)
    ) {
        self.id = id
        self.chatId = chatId
        self.senderId = senderId
        self.senderName = senderName
        self.isMe = isMe
        self.text = text
        self.messageType = messageType
        self.mediaUrl = mediaUrl
        self.audioDurationSeconds = audioDurationSeconds
        self.rideMeta = rideMeta
        self.callMeta = callMeta
        self.locationMeta = locationMeta
        self.envelopeMeta = envelopeMeta
        self.stickerUrl = stickerUrl
        self.encryptedPayload = encryptedPayload
        self.isEncrypted = isEncrypted
        self.reactions = reactions
        self.replyToId = replyToId
        self.replyToText = replyToText
        self.replyToSender = replyToSender
        self.isStarred = isStarred
        self.status = status
        self.timestampMs = timestampMs
    }
}

public struct StoryStatusItem: Identifiable, Codable, Equatable {
    public let id: String
    public let userId: String
    public let userName: String
    public let userAvatarUrl: String?
    public var hasUnseenStory: Bool
    public var lastUpdateText: String
    public var mediaUrl: String?
    public var caption: String?
    public var createdAtMs: Int64

    public init(
        id: String,
        userId: String,
        userName: String,
        userAvatarUrl: String? = nil,
        hasUnseenStory: Bool = true,
        lastUpdateText: String = "Today, 11:20 AM",
        mediaUrl: String? = nil,
        caption: String? = nil,
        createdAtMs: Int64 = Int64(Date().timeIntervalSince1970 * 1000)
    ) {
        self.id = id
        self.userId = userId
        self.userName = userName
        self.userAvatarUrl = userAvatarUrl
        self.hasUnseenStory = hasUnseenStory
        self.lastUpdateText = lastUpdateText
        self.mediaUrl = mediaUrl
        self.caption = caption
        self.createdAtMs = createdAtMs
    }
}

public struct GroupParticipant: Identifiable, Codable, Equatable {
    public let id: String
    public let name: String
    public let phone: String
    public let handle: String?
    public let avatarUrl: String?
    public let role: String
    public let statusBio: String

    public init(
        id: String,
        name: String,
        phone: String,
        handle: String? = nil,
        avatarUrl: String? = nil,
        role: String = "MEMBER",
        statusBio: String = "Available on Evrry"
    ) {
        self.id = id
        self.name = name
        self.phone = phone
        self.handle = handle
        self.avatarUrl = avatarUrl
        self.role = role
        self.statusBio = statusBio
    }
}

public struct CommonGroupInfo: Identifiable, Codable, Equatable {
    public let id: String
    public let title: String
    public let membersCount: Int
    public let avatarUrl: String?

    public init(id: String, title: String, membersCount: Int, avatarUrl: String? = nil) {
        self.id = id
        self.title = title
        self.membersCount = membersCount
        self.avatarUrl = avatarUrl
    }
}

public struct SharedMediaItem: Identifiable, Codable, Equatable {
    public let id: String
    public let type: String
    public let url: String
    public let title: String?
    public let timestampText: String

    public init(id: String, type: String = "IMAGE", url: String, title: String? = nil, timestampText: String = "Today") {
        self.id = id
        self.type = type
        self.url = url
        self.title = title
        self.timestampText = timestampText
    }
}

public struct PublicContactProfile: Codable, Equatable {
    public let userId: String
    public let name: String
    public let handle: String?
    public let phone: String
    public let isPhoneMasked: Bool
    public let avatarUrl: String?
    public let statusBio: String
    public let isRegisteredOnEvrry: Bool
    public var isBlocked: Bool
    public var isMuted: Bool
    public let commonGroups: [CommonGroupInfo]
    public let sharedMedia: [SharedMediaItem]
    public let sharedDocsCount: Int
    public let sharedLinksCount: Int
    public var disappearingMessagesTimer: String

    public init(
        userId: String,
        name: String,
        handle: String? = nil,
        phone: String,
        isPhoneMasked: Bool = false,
        avatarUrl: String? = nil,
        statusBio: String = "Available on Evrry",
        isRegisteredOnEvrry: Bool = true,
        isBlocked: Bool = false,
        isMuted: Bool = false,
        commonGroups: [CommonGroupInfo] = [],
        sharedMedia: [SharedMediaItem] = [],
        sharedDocsCount: Int = 2,
        sharedLinksCount: Int = 4,
        disappearingMessagesTimer: String = "Off"
    ) {
        self.userId = userId
        self.name = name
        self.handle = handle
        self.phone = phone
        self.isPhoneMasked = isPhoneMasked
        self.avatarUrl = avatarUrl
        self.statusBio = statusBio
        self.isRegisteredOnEvrry = isRegisteredOnEvrry
        self.isBlocked = isBlocked
        self.isMuted = isMuted
        self.commonGroups = commonGroups
        self.sharedMedia = sharedMedia
        self.sharedDocsCount = sharedDocsCount
        self.sharedLinksCount = sharedLinksCount
        self.disappearingMessagesTimer = disappearingMessagesTimer
    }
}

public struct SocialChat: Identifiable, Codable, Equatable {
    public let id: String
    public let type: String
    public let title: String
    public let subtitle: String?
    public let avatarUrl: String?
    public var lastMessage: String
    public var lastMessageTime: String
    public var lastMessageStatus: String
    public var unreadCount: Int
    public let isOnline: Bool
    public let isTyping: Bool
    public var isMuted: Bool
    public let referenceId: String?
    public let referenceTag: String?
    public let isGroup: Bool
    public let groupDescription: String?
    public var participants: [GroupParticipant]
    public var disappearingMessagesTimer: String
    public var wallpaperUrl: String?

    public init(
        id: String,
        type: String = "DIRECT",
        title: String,
        subtitle: String? = nil,
        avatarUrl: String? = nil,
        lastMessage: String,
        lastMessageTime: String,
        lastMessageStatus: String = "read",
        unreadCount: Int = 0,
        isOnline: Bool = false,
        isTyping: Bool = false,
        isMuted: Bool = false,
        referenceId: String? = nil,
        referenceTag: String? = nil,
        isGroup: Bool = false,
        groupDescription: String? = nil,
        participants: [GroupParticipant] = [],
        disappearingMessagesTimer: String = "Off",
        wallpaperUrl: String? = nil
    ) {
        self.id = id
        self.type = type
        self.title = title
        self.subtitle = subtitle
        self.avatarUrl = avatarUrl
        self.lastMessage = lastMessage
        self.lastMessageTime = lastMessageTime
        self.lastMessageStatus = lastMessageStatus
        self.unreadCount = unreadCount
        self.isOnline = isOnline
        self.isTyping = isTyping
        self.isMuted = isMuted
        self.referenceId = referenceId
        self.referenceTag = referenceTag
        self.isGroup = isGroup
        self.groupDescription = groupDescription
        self.participants = participants
        self.disappearingMessagesTimer = disappearingMessagesTimer
        self.wallpaperUrl = wallpaperUrl
    }
}

public struct PhoneContactItem: Identifiable, Codable, Equatable {
    public let id: String
    public let name: String
    public let phone: String
    public let avatarUrl: String?
    public let statusBio: String
    public let isRegisteredOnEvrry: Bool
    public let handle: String?

    public init(
        id: String,
        name: String,
        phone: String,
        avatarUrl: String? = nil,
        statusBio: String = "Available on Evrry",
        isRegisteredOnEvrry: Bool = true,
        handle: String? = nil
    ) {
        self.id = id
        self.name = name
        self.phone = phone
        self.avatarUrl = avatarUrl
        self.statusBio = statusBio
        self.isRegisteredOnEvrry = isRegisteredOnEvrry
        self.handle = handle
    }
}
