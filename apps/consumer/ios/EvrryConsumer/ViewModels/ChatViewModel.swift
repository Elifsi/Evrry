//
//  ChatViewModel.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Realtime Social Chat & Messaging ViewModel for iOS
//

import Foundation
import Combine

public class ChatViewModel: ObservableObject {
    @Published public var activeFilter: String = "all" // "all", "unread", "direct", "orders"
    @Published public var stories: [StoryStatusItem] = [
        StoryStatusItem(
            id: "story-self",
            userId: "user-me",
            userName: "My Status",
            hasUnseenStory: false,
            lastUpdateText: "Tap to add status update",
            mediaUrl: nil,
            caption: nil
        ),
        StoryStatusItem(
            id: "story-1",
            userId: "user-aayush",
            userName: "Aayush",
            hasUnseenStory: true,
            lastUpdateText: "28 mins ago",
            mediaUrl: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=800&q=80",
            caption: "Basantapur momo walk with friends! 🥟🔥"
        ),
        StoryStatusItem(
            id: "story-2",
            userId: "user-priyanka",
            userName: "Priyanka S.",
            hasUnseenStory: true,
            lastUpdateText: "Today, 10:14 AM",
            mediaUrl: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=800&q=80",
            caption: "Sunny skies over Kathmandu valley today 🏔️✨"
        ),
        StoryStatusItem(
            id: "story-3",
            userId: "user-bikash",
            userName: "Bikash Rider",
            hasUnseenStory: false,
            lastUpdateText: "Yesterday, 8:45 PM",
            mediaUrl: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&q=80",
            caption: "Completed 18 deliveries across Patan! 🛵💨"
        )
    ]
    @Published public var phoneContacts: [PhoneContactItem] = [
        PhoneContactItem(id: "cont-aayush", name: "Aayush Shrestha", phone: "+977 9841112233", statusBio: "Momo lover • Urgent calls only", isRegisteredOnEvrry: true, handle: "aayush_s"),
        PhoneContactItem(id: "cont-priyanka", name: "Priyanka Sharma", phone: "+977 9851234567", statusBio: "At work • Available on chat", isRegisteredOnEvrry: true, handle: "priyanka_sharma"),
        PhoneContactItem(id: "cont-bikash", name: "Bikash Tamang", phone: "+977 9860123456", statusBio: "Moto Captain • On the move", isRegisteredOnEvrry: true, handle: "bikash_moto"),
        PhoneContactItem(id: "cont-suman", name: "Suman Gurung", phone: "+977 9818765432", statusBio: "Living life in Pokhara 🏔️", isRegisteredOnEvrry: true, handle: "suman_g"),
        PhoneContactItem(id: "cont-sandesh", name: "Sandesh Karki", phone: "+977 9849123456", statusBio: "Available on Evrry", isRegisteredOnEvrry: true, handle: "sandesh_k"),
        PhoneContactItem(id: "cont-ramesh", name: "Ramesh Thapa", phone: "+977 9841234567", statusBio: "Invite to Evrry", isRegisteredOnEvrry: false),
        PhoneContactItem(id: "cont-sunita", name: "Sunita Adhikari", phone: "+977 9801234567", statusBio: "Invite to Evrry", isRegisteredOnEvrry: false),
        PhoneContactItem(id: "cont-binod", name: "Binod Pokharel", phone: "+977 9849876543", statusBio: "Invite to Evrry", isRegisteredOnEvrry: false)
    ]
    @Published public var chats: [SocialChat] = [
        SocialChat(
            id: "chat-rider-01",
            type: "RIDE_COORDINATION",
            title: "Bikash Tamang (Moto Rider)",
            subtitle: "Active Moto Ride • Ba 98 Pa 4321",
            lastMessage: "Dai, ma Pulchowk Bhatbhateni agadi aipuge!",
            lastMessageTime: "12:42 PM",
            lastMessageStatus: "read",
            unreadCount: 1,
            isOnline: true,
            isTyping: false,
            referenceId: "ride-moto-8841",
            referenceTag: "Moto Ride #8841 (In Transit)"
        ),
        SocialChat(
            id: "chat-order-01",
            type: "ORDER_SUPPORT",
            title: "Roadhouse Cafe Pulchowk",
            subtitle: "Order Support • Woodfired Pizza",
            lastMessage: "Your Woodfired Margherita Pizza is in the oven! 🍕",
            lastMessageTime: "12:35 PM",
            lastMessageStatus: "delivered",
            unreadCount: 0,
            isOnline: true,
            isTyping: false,
            referenceId: "ord-001",
            referenceTag: "Food Order #482 (Preparing)"
        ),
        SocialChat(
            id: "chat-direct-01",
            type: "DIRECT",
            title: "Aayush Shrestha",
            subtitle: "Friend • Online",
            lastMessage: "Momo khana jaane haina aaja sanjha Jhamsikhel?",
            lastMessageTime: "11:15 AM",
            lastMessageStatus: "read",
            unreadCount: 2,
            isOnline: true,
            isTyping: false
        ),
        SocialChat(
            id: "chat-direct-02",
            type: "DIRECT",
            title: "Priyanka Sharma",
            subtitle: "Last seen today at 10:30 AM",
            lastMessage: "Voice note (0:18)",
            lastMessageTime: "10:14 AM",
            lastMessageStatus: "delivered",
            unreadCount: 0,
            isOnline: false
        ),
        SocialChat(
            id: "chat-group-foodies",
            type: "GROUP",
            title: "Kathmandu Foodies & Tech",
            subtitle: "Aayush, Priyanka, Bikash, You",
            lastMessage: "Aayush: Roadhouse or Dalle for momos tonight? 🥟",
            lastMessageTime: "1:05 PM",
            lastMessageStatus: "read",
            unreadCount: 3,
            isOnline: true,
            isGroup: true,
            groupDescription: "Exploring local cafes, authentic momo spots & tech meetups across Kathmandu Valley.",
            participants: [
                GroupParticipant(id: "cont-aayush", name: "Aayush Shrestha", phone: "+977 9841112233", handle: "aayush_s", role: "ADMIN", statusBio: "Momo lover • Urgent calls only"),
                GroupParticipant(id: "cont-priyanka", name: "Priyanka Sharma", phone: "+977 9851234567", handle: "priyanka_sharma", role: "MEMBER", statusBio: "At work • Available on chat"),
                GroupParticipant(id: "cont-bikash", name: "Bikash Tamang", phone: "+977 9860123456", handle: "bikash_moto", role: "MEMBER", statusBio: "Moto Captain • On the move"),
                GroupParticipant(id: "user-me", name: "You", phone: "+977 9841000000", handle: "rahul", role: "MEMBER", statusBio: "Available on Evrry")
            ]
        ),
        SocialChat(
            id: "chat-group-family",
            type: "GROUP",
            title: "Family & Home Hub",
            subtitle: "Priyanka, Suman, You",
            lastMessage: "Priyanka: Fresh apples delivered, thanks!",
            lastMessageTime: "Yesterday",
            lastMessageStatus: "read",
            unreadCount: 0,
            isOnline: false,
            isGroup: true,
            groupDescription: "Family home coordination for groceries, deliveries, and airport pickups.",
            participants: [
                GroupParticipant(id: "cont-priyanka", name: "Priyanka Sharma", phone: "+977 9851234567", handle: "priyanka_sharma", role: "ADMIN", statusBio: "At work"),
                GroupParticipant(id: "cont-suman", name: "Suman Gurung", phone: "+977 9818765432", handle: "suman_g", role: "MEMBER", statusBio: "In Pokhara"),
                GroupParticipant(id: "user-me", name: "You", phone: "+977 9841000000", handle: "rahul", role: "MEMBER", statusBio: "Available on Evrry")
            ]
        ),
        SocialChat(
            id: "chat-store-mart",
            type: "ORDER_SUPPORT",
            title: "Himalayan Organic Mart",
            subtitle: "Grocery Quick-Commerce Support",
            lastMessage: "Fresh Jumla apples are packed. Rider dispatched!",
            lastMessageTime: "Yesterday",
            lastMessageStatus: "read",
            unreadCount: 0,
            isOnline: false,
            referenceId: "groc-mart-19",
            referenceTag: "Mart Order #19 (Completed)"
        )
    ]
    @Published public var selectedChat: SocialChat? = nil
    @Published public var blockedUserIds: Set<String> = []
    @Published public var backupConfig: ChatBackupConfig = ChatBackupConfig()
    @Published public var backupProgress: Double? = nil
    @Published public var restoreProgress: Double? = nil

    // Per-chat wallpaper URL
    @Published var wallpaperByChat: [String: String] = [:]

    func setWallpaper(chatId: String, url: String) {
        wallpaperByChat[chatId] = url
    }

    @Published public var messages: [String: [SocialMessage]] = [
        "chat-group-foodies": [
            SocialMessage(
                id: "msg-gf1",
                chatId: "chat-group-foodies",
                senderId: "cont-bikash",
                senderName: "Bikash Tamang",
                isMe: false,
                text: "Guys, rain has stopped in Patan area! ☀️",
                messageType: "TEXT",
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 7200) * 1000)
            ),
            SocialMessage(
                id: "msg-gf2",
                chatId: "chat-group-foodies",
                senderId: "cont-priyanka",
                senderName: "Priyanka Sharma",
                isMe: false,
                text: "Great! Let's do an evening meetup then.",
                messageType: "TEXT",
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 3600) * 1000)
            ),
            SocialMessage(
                id: "msg-gf3",
                chatId: "chat-group-foodies",
                senderId: "cont-aayush",
                senderName: "Aayush Shrestha",
                isMe: false,
                text: "Roadhouse or Dalle for momos tonight? 🥟",
                messageType: "TEXT",
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 1800) * 1000)
            )
        ],
        "chat-rider-01": [
            SocialMessage(
                id: "msg-r1",
                chatId: "chat-rider-01",
                senderId: "rider-bikash",
                senderName: "Bikash Tamang",
                isMe: false,
                text: "Namaste sir! Ma 3 minute ma tapai ko location ma aipugchhu.",
                messageType: "TEXT",
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 360) * 1000)
            ),
            SocialMessage(
                id: "msg-r2",
                chatId: "chat-rider-01",
                senderId: "me",
                senderName: "Me",
                isMe: true,
                text: "Huss dai, ma main gate agadi blue jacket lagayera basya chhu.",
                messageType: "TEXT",
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 240) * 1000)
            ),
            SocialMessage(
                id: "msg-r3",
                chatId: "chat-rider-01",
                senderId: "rider-bikash",
                senderName: "Bikash Tamang",
                isMe: false,
                text: "Dai, ma Pulchowk Bhatbhateni agadi aipuge!",
                messageType: "TEXT",
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 60) * 1000)
            )
        ],
        "chat-order-01": [
            SocialMessage(
                id: "msg-o1",
                chatId: "chat-order-01",
                senderId: "rest-roadhouse",
                senderName: "Roadhouse Cafe Pulchowk",
                isMe: false,
                text: "Namaste! Thank you for your order with Roadhouse Cafe. We have confirmed your order.",
                messageType: "TEXT",
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 900) * 1000)
            ),
            SocialMessage(
                id: "msg-o2",
                chatId: "chat-order-01",
                senderId: "me",
                senderName: "Me",
                isMe: true,
                text: "Can you please make sure the crust is extra crispy with chili flakes on the side?",
                messageType: "TEXT",
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 600) * 1000)
            ),
            SocialMessage(
                id: "msg-o3",
                chatId: "chat-order-01",
                senderId: "rest-roadhouse",
                senderName: "Roadhouse Cafe Pulchowk",
                isMe: false,
                text: "Your Woodfired Margherita Pizza is in the oven! 🍕 We have included extra dried oregano and chili flakes sachets.",
                messageType: "TEXT",
                status: "delivered",
                timestampMs: Int64((Date().timeIntervalSince1970 - 300) * 1000)
            )
        ],
        "chat-direct-01": [
            SocialMessage(
                id: "msg-d1",
                chatId: "chat-direct-01",
                senderId: "user-aayush",
                senderName: "Aayush Shrestha",
                isMe: false,
                text: "K chha bro? Office sakeu?",
                messageType: "TEXT",
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 1800) * 1000)
            ),
            SocialMessage(
                id: "msg-d2",
                chatId: "chat-direct-01",
                senderId: "me",
                senderName: "Me",
                isMe: true,
                text: "Halka baki chha, 6 baje samma sakincha hola.",
                messageType: "TEXT",
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 1200) * 1000)
            ),
            SocialMessage(
                id: "msg-d3",
                chatId: "chat-direct-01",
                senderId: "user-aayush",
                senderName: "Aayush Shrestha",
                isMe: false,
                text: "Momo khana jaane haina aaja sanjha Jhamsikhel?",
                messageType: "TEXT",
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 600) * 1000)
            ),
            SocialMessage(
                id: "msg-d4",
                chatId: "chat-direct-01",
                senderId: "user-aayush",
                senderName: "Aayush Shrestha",
                isMe: false,
                text: "Sharing Live Location (1 hour)",
                messageType: "LIVE_LOCATION",
                locationMeta: LiveLocationMeta(
                    latitude: 27.6766,
                    longitude: 85.3175,
                    address: "Jhamsikhel Cafe Hub, Lalitpur",
                    expiresAtTimeText: "Live for 52 mins",
                    isSharingActive: true
                ),
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 300) * 1000)
            ),
            SocialMessage(
                id: "msg-d5",
                chatId: "chat-direct-01",
                senderId: "user-aayush",
                senderName: "Aayush Shrestha",
                isMe: false,
                text: "Gift Envelope: Rs. 500",
                messageType: "GIFT_ENVELOPE",
                envelopeMeta: GiftEnvelopeMeta(
                    amountNpr: 500.0,
                    senderNote: "Happy Dashain / Momo Treat! 🌸",
                    isClaimed: false
                ),
                status: "read",
                timestampMs: Int64((Date().timeIntervalSince1970 - 120) * 1000)
            )
        ]
    ]

    public var totalUnreadCount: Int {
        chats.reduce(0) { $0 + $1.unreadCount }
    }

    public init() {}

    public func setFilter(_ filter: String) {
        self.activeFilter = filter
    }

    public func selectChat(_ chat: SocialChat) {
        self.selectedChat = chat
        markChatAsRead(chat.id)
    }

    public func clearSelectedChat() {
        self.selectedChat = nil
    }

    public func markChatAsRead(_ chatId: String) {
        if let idx = chats.firstIndex(where: { $0.id == chatId }) {
            chats[idx].unreadCount = 0
        }
    }

    public func getMessagesForChat(_ chatId: String) -> [SocialMessage] {
        return messages[chatId] ?? []
    }

    public func sendMessage(chatId: String, text: String, replyToMessage: SocialMessage? = nil) {
        let trimmed = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }

        let newMsgId = "msg-" + UUID().uuidString.prefix(8)
        let encryptedPayload = try? EvrryE2EEManager.shared.encrypt(
            plaintext: trimmed,
            recipientPublicKeyBase64: EvrryE2EEManager.shared.getMyPublicKeyBase64()
        )
        let outgoing = SocialMessage(
            id: String(newMsgId),
            chatId: chatId,
            senderId: "me",
            senderName: "Me",
            isMe: true,
            text: trimmed,
            messageType: "TEXT",
            encryptedPayload: encryptedPayload,
            isEncrypted: true,
            replyToId: replyToMessage?.id,
            replyToText: replyToMessage?.text.prefix(80).description,
            replyToSender: replyToMessage?.senderName,
            status: "sending",
            timestampMs: Int64(Date().timeIntervalSince1970 * 1000)
        )

        var list = messages[chatId] ?? []
        list.append(outgoing)
        messages[chatId] = list

        if let idx = chats.firstIndex(where: { $0.id == chatId }) {
            chats[idx].lastMessage = trimmed
            chats[idx].lastMessageTime = "Just now"
            chats[idx].lastMessageStatus = "sending"
        }

        // Simulate ACK & Deliveries
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) { [weak self] in
            self?.updateMessageStatus(chatId: chatId, msgId: String(newMsgId), status: "sent")
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.8) { [weak self] in
            self?.updateMessageStatus(chatId: chatId, msgId: String(newMsgId), status: "delivered")
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.4) { [weak self] in
            self?.updateMessageStatus(chatId: chatId, msgId: String(newMsgId), status: "read")
        }

        // Simulate reply
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.2) { [weak self] in
            self?.simulateReply(chatId: chatId, userText: trimmed)
        }
    }

    public func toggleReaction(chatId: String, messageId: String, emoji: String) {
        guard var list = messages[chatId], let idx = list.firstIndex(where: { $0.id == messageId }) else { return }
        var reacts = list[idx].reactions
        if let count = reacts[emoji], count > 0 {
            reacts.removeValue(forKey: emoji)
        } else {
            reacts[emoji] = 1
        }
        list[idx].reactions = reacts
        messages[chatId] = list
    }

    public func toggleStarMessage(chatId: String, messageId: String) -> Bool {
        guard var list = messages[chatId], let idx = list.firstIndex(where: { $0.id == messageId }) else { return false }
        list[idx].isStarred.toggle()
        let isNowStarred = list[idx].isStarred
        messages[chatId] = list
        return isNowStarred
    }

    public func setDisappearingTimer(chatId: String, timer: String) {
        if let idx = chats.firstIndex(where: { $0.id == chatId }) {
            chats[idx].disappearingMessagesTimer = timer
        }
    }

    public func sendVoiceNote(chatId: String, duration: Int = 14) {
        let newMsgId = "voice-" + UUID().uuidString.prefix(8)
        let plainText = "Voice message (\(duration)s)"
        let encryptedPayload = try? EvrryE2EEManager.shared.encrypt(
            plaintext: plainText,
            recipientPublicKeyBase64: EvrryE2EEManager.shared.getMyPublicKeyBase64()
        )
        let outgoing = SocialMessage(
            id: String(newMsgId),
            chatId: chatId,
            senderId: "me",
            senderName: "Me",
            isMe: true,
            text: plainText,
            messageType: "VOICE_NOTE",
            audioDurationSeconds: duration,
            encryptedPayload: encryptedPayload,
            isEncrypted: true,
            status: "sent",
            timestampMs: Int64(Date().timeIntervalSince1970 * 1000)
        )

        var list = messages[chatId] ?? []
        list.append(outgoing)
        messages[chatId] = list

        if let idx = chats.firstIndex(where: { $0.id == chatId }) {
            chats[idx].lastMessage = "🎤 Voice message (\(duration)s)"
            chats[idx].lastMessageTime = "Just now"
            chats[idx].lastMessageStatus = "sent"
        }
    }

    func sendSticker(chatId: String, stickerUrl: String) {
        let newId = "sticker-" + UUID().uuidString.prefix(8)
        let msg = SocialMessage(
            id: String(newId),
            chatId: chatId,
            senderId: "me",
            senderName: "Me",
            isMe: true,
            text: "Sticker",
            messageType: "STICKER",
            stickerUrl: stickerUrl,
            status: "sent"
        )
        messages[chatId, default: []].append(msg)
        if let idx = chats.firstIndex(where: { $0.id == chatId }) {
            chats[idx].lastMessage = "😄 Sticker"
            chats[idx].lastMessageTime = "Just now"
            chats[idx].lastMessageStatus = "sent"
        }
    }

    public func sendImage(chatId: String, imageUrl: String, caption: String = "") {
        let newId = "media-" + UUID().uuidString.prefix(8)
        let plainText = caption.isEmpty ? "Photo" : caption
        let msg = SocialMessage(
            id: String(newId),
            chatId: chatId,
            senderId: "me",
            senderName: "Me",
            isMe: true,
            text: plainText,
            messageType: "IMAGE",
            mediaUrl: imageUrl,
            status: "sent"
        )
        messages[chatId, default: []].append(msg)
        if let idx = chats.firstIndex(where: { $0.id == chatId }) {
            chats[idx].lastMessage = "📷 \(plainText)"
            chats[idx].lastMessageTime = "Just now"
            chats[idx].lastMessageStatus = "sent"
        }
    }

    public func postStory(mediaUrl: String, caption: String = "") {
        if let idx = stories.firstIndex(where: { $0.id == "story-self" }) {
            stories[idx].hasUnseenStory = true
            stories[idx].lastUpdateText = "Just now"
            stories[idx].mediaUrl = mediaUrl
            stories[idx].caption = caption.isEmpty ? nil : caption
            stories[idx].createdAtMs = Int64(Date().timeIntervalSince1970 * 1000)
        }
    }

    public func deleteMyStory() {
        if let idx = stories.firstIndex(where: { $0.id == "story-self" }) {
            stories[idx].hasUnseenStory = false
            stories[idx].lastUpdateText = "Tap to add status update"
            stories[idx].mediaUrl = nil
            stories[idx].caption = nil
        }
    }

    public func markStorySeen(storyId: String) {
        if let idx = stories.firstIndex(where: { $0.id == storyId }) {
            stories[idx].hasUnseenStory = false
        }
    }

    public func sendStoryReply(storyUserId: String, messageText: String) {
        let targetChat = chats.first { chat in
            switch storyUserId {
            case "user-aayush":
                return chat.id == "chat-direct-01" || chat.title.localizedCaseInsensitiveContains("Aayush")
            case "user-priyanka":
                return chat.id == "chat-direct-02" || chat.title.localizedCaseInsensitiveContains("Priyanka")
            case "user-bikash":
                return chat.id == "chat-rider-01" || chat.title.localizedCaseInsensitiveContains("Bikash")
            default:
                return chat.title.localizedCaseInsensitiveContains(storyUserId)
            }
        } ?? chats.first

        if let chat = targetChat {
            sendMessage(chatId: chat.id, text: messageText)
        }
    }

    public func sendLiveRideShare(chatId: String) {
        let newMsgId = "ride-share-" + UUID().uuidString.prefix(8)
        let meta = RideShareMeta(
            riderName: "Bikash Tamang",
            vehicleModel: "Honda CB Shine 125",
            vehicleNumber: "Ba 98 Pa 4321",
            etaMinutes = 4,
            pickupAddress: "Pulchowk Main Gate, Lalitpur",
            destinationAddress: "Thamel Marg, Kathmandu"
        )
        let plainText = "Sharing my live Moto trip with Bikash Tamang (ETA: 4 mins)"
        let encryptedPayload = try? EvrryE2EEManager.shared.encrypt(
            plaintext: plainText,
            recipientPublicKeyBase64: EvrryE2EEManager.shared.getMyPublicKeyBase64()
        )
        let outgoing = SocialMessage(
            id: String(newMsgId),
            chatId: chatId,
            senderId: "me",
            senderName: "Me",
            isMe: true,
            text: plainText,
            messageType: "LIVE_RIDE_SHARE",
            rideMeta: meta,
            encryptedPayload: encryptedPayload,
            isEncrypted: true,
            status: "sent",
            timestampMs: Int64(Date().timeIntervalSince1970 * 1000)
        )

        var list = messages[chatId] ?? []
        list.append(outgoing)
        messages[chatId] = list

        if let idx = chats.firstIndex(where: { $0.id == chatId }) {
            chats[idx].lastMessage = "📍 Shared Live Ride (ETA 4 min)"
            chats[idx].lastMessageTime = "Just now"
            chats[idx].lastMessageStatus = "sent"
        }
    }

    private func updateMessageStatus(chatId: String, msgId: String, status: String) {
        if var list = messages[chatId], let idx = list.firstIndex(where: { $0.id == msgId }) {
            list[idx].status = status
            messages[chatId] = list
        }
        if let idx = chats.firstIndex(where: { $0.id == chatId }) {
            chats[idx].lastMessageStatus = status
        }
    }

    private func simulateReply(chatId: String, userText: String) {
        guard let chat = chats.first(where: { $0.id == chatId }) else { return }
        let replyText: String
        if chat.type == "RIDE_COORDINATION" {
            replyText = "Huss sir, ma white helmet lagayeko chhu, right side ma horn bajauchhu."
        } else if chat.type == "ORDER_SUPPORT" {
            replyText = "Sure! Chef has noted your preference. It will arrive piping hot."
        } else {
            replyText = userText.lowercased().contains("momo") ? "Dammi plan! 6:30 ma Jhamsikhel meet garum la!" : "Thik chha, ma 5 min ma update garchhu!"
        }

        let replyEncrypted = try? EvrryE2EEManager.shared.encrypt(
            plaintext: replyText,
            recipientPublicKeyBase64: EvrryE2EEManager.shared.getMyPublicKeyBase64()
        )
        let replyMsg = SocialMessage(
            id: "msg-" + UUID().uuidString.prefix(8),
            chatId: chatId,
            senderId: "counterpart",
            senderName: chat.title,
            isMe: false,
            text: replyText,
            messageType: "TEXT",
            encryptedPayload: replyEncrypted,
            isEncrypted: true,
            status: "read",
            timestampMs: Int64(Date().timeIntervalSince1970 * 1000)
        )

        var list = messages[chatId] ?? []
        list.append(replyMsg)
        messages[chatId] = list

        if let idx = chats.firstIndex(where: { $0.id == chatId }) {
            chats[idx].lastMessage = replyText
            chats[idx].lastMessageTime = "Just now"
            if selectedChat?.id != chatId {
                chats[idx].unreadCount += 1
            }
        }
    }

    public func openOrCreateOrderChat(orderId: String, vendorName: String, riderName: String?) -> SocialChat {
        if let existing = chats.first(where: { $0.referenceId == orderId }) {
            selectChat(existing)
            return existing
        }
        let newChat = SocialChat(
            id: "chat-order-\(orderId)",
            title: riderName != nil ? "\(vendorName) & \(riderName!)" : vendorName,
            type: "ORDER_SUPPORT",
            referenceId: orderId,
            referenceTag: "ORD #\(orderId.suffix(6).uppercased())",
            lastMessage: "Order support channel opened with \(vendorName).",
            lastMessageTime: "Just now",
            lastMessageStatus: "sent",
            unreadCount: 0,
            isOnline: true
        )
        chats.insert(newChat, at: 0)
        messages[newChat.id] = [
            SocialMessage(
                id: "msg-welcome-\(orderId)",
                chatId: newChat.id,
                senderId: "system",
                senderName: "System",
                isMe: false,
                text: "Welcome to live order support for \(vendorName). Rider: \(riderName ?? "Assigned soon").",
                messageType: "TEXT",
                status: "read",
                timestampMs: Int64(Date().timeIntervalSince1970 * 1000)
            )
        ]
        selectChat(newChat)
        return newChat
    }

    public func openOrCreateDirectChatWithContact(_ contact: PhoneContactItem) -> SocialChat {
        if let existing = chats.first(where: { $0.title == contact.name || ($0.subtitle?.contains(contact.phone) ?? false) }) {
            selectChat(existing)
            return existing
        }

        let newChat = SocialChat(
            id: "chat-direct-\(contact.id)",
            type: "DIRECT",
            title: contact.name,
            subtitle: contact.phone,
            avatarUrl: contact.avatarUrl,
            lastMessage: "Started a new conversation with \(contact.name)",
            lastMessageTime: "Just now",
            lastMessageStatus: "read",
            unreadCount: 0,
            isOnline: true
        )
        chats.insert(newChat, at: 0)
        selectChat(newChat)
        return newChat
    }

    public func openOrCreateChatByPhoneNumber(_ phoneInput: String) -> SocialChat {
        let digitsOnly = phoneInput.filter { $0.isNumber }
        let formattedPhone = phoneInput.hasPrefix("+") ? phoneInput : "+977 \(digitsOnly)"

        if let existing = chats.first(where: { $0.title == formattedPhone || ($0.subtitle?.contains(digitsOnly) ?? false) }) {
            selectChat(existing)
            return existing
        }

        let newChat = SocialChat(
            id: "chat-direct-phone-\(digitsOnly)",
            type: "DIRECT",
            title: formattedPhone,
            subtitle: "Direct Mobile Chat",
            avatarUrl: nil,
            lastMessage: "Tap here to send an end-to-end encrypted message",
            lastMessageTime: "Just now",
            lastMessageStatus: "sent",
            unreadCount: 0,
            isOnline: false
        )
        chats.insert(newChat, at: 0)
        selectChat(newChat)
        return newChat
    }

    public func getContactProfile(chat: SocialChat) -> PublicContactProfile {
        let phoneContact = phoneContacts.first(where: { $0.name.caseInsensitiveCompare(chat.title) == .orderedSame })
        let isBlocked = blockedUserIds.contains(phoneContact?.id ?? chat.id)

        let commonGroups = chats.filter { $0.isGroup }.map {
            CommonGroupInfo(
                id: $0.id,
                title: $0.title,
                membersCount: max($0.participants.count, 3),
                avatarUrl: $0.avatarUrl
            )
        }

        let sharedMedia = [
            SharedMediaItem(id: "sm-1", type: "IMAGE", url: "https://images.unsplash.com/photo-1513104890138-7c749659a591", title: "Pizza Order Delivery", timestampText: "Yesterday"),
            SharedMediaItem(id: "sm-2", type: "IMAGE", url: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c", title: "Kathmandu Momo Gathering", timestampText: "Oct 08"),
            SharedMediaItem(id: "sm-3", type: "IMAGE", url: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd", title: "Burger Combo Receipt", timestampText: "Oct 06")
        ]

        return PublicContactProfile(
            userId: phoneContact?.id ?? chat.id,
            name: chat.title,
            handle: phoneContact?.handle ?? chat.title.lowercased().replacingOccurrences(of: " ", with: "_"),
            phone: phoneContact?.phone ?? "+977 9841******",
            isPhoneMasked: false,
            avatarUrl: chat.avatarUrl,
            statusBio: phoneContact?.statusBio ?? "Available on Evrry",
            isRegisteredOnEvrry: true,
            isBlocked: isBlocked,
            isMuted: chat.isMuted,
            commonGroups: commonGroups,
            sharedMedia: sharedMedia,
            sharedDocsCount: 2,
            sharedLinksCount: 4
        )
    }

    public func toggleBlockContact(userId: String) -> Bool {
        if blockedUserIds.contains(userId) {
            blockedUserIds.remove(userId)
            return false
        } else {
            blockedUserIds.insert(userId)
            return true
        }
    }

    public func reportContact(userId: String, reason: String = "Spam / Harassment") -> Bool {
        // Log trust & safety audit record and auto-block
        blockedUserIds.insert(userId)
        return true
    }

    public func toggleMuteChat(chatId: String) {
        if let idx = chats.firstIndex(where: { $0.id == chatId }) {
            chats[idx].isMuted.toggle()
            if selectedChat?.id == chatId {
                selectedChat?.isMuted = chats[idx].isMuted
            }
        }
    }

    public func createGroup(title: String, description: String?, participantIds: [String]) -> SocialChat {
        var selectedParticipants = phoneContacts.filter { participantIds.contains($0.id) }.map {
            GroupParticipant(
                id: $0.id,
                name: $0.name,
                phone: $0.phone,
                handle: $0.handle,
                role: "MEMBER",
                statusBio: $0.statusBio
            )
        }

        selectedParticipants.append(
            GroupParticipant(id: "user-me", name: "You", phone: "+977 9841000000", handle: "rahul", role: "ADMIN", statusBio: "Available on Evrry")
        )

        let newGroup = SocialChat(
            id: "chat-group-\(UUID().uuidString.prefix(8))",
            type: "GROUP",
            title: title,
            subtitle: "\(selectedParticipants.count) participants",
            lastMessage: "You created group \"\(title)\"",
            lastMessageTime: "Just now",
            lastMessageStatus: "read",
            unreadCount: 0,
            isOnline: true,
            isGroup: true,
            groupDescription: description ?? "Evrry WhatsApp-grade Group Chat",
            participants: selectedParticipants
        )

        chats.insert(newGroup, at: 0)
        selectChat(newGroup)
        return newGroup
    }

    // MARK: - Live Location Sharing
    public func sendLiveLocation(chatId: String, durationMinutes: Int = 60) {
        let meta = LiveLocationMeta(
            latitude: 27.6766,
            longitude: 85.3175,
            address: "Pulchowk Engineering Campus, Lalitpur",
            expiresAtTimeText: "Live for \(durationMinutes) mins",
            isSharingActive: true
        )
        let outgoingMsg = SocialMessage(
            id: "msg-\(UUID().uuidString.prefix(8))",
            chatId: chatId,
            senderId: "me",
            senderName: "Me",
            isMe: true,
            text: "Sharing Live Location (\(durationMinutes) mins)",
            messageType: "LIVE_LOCATION",
            locationMeta: meta,
            status: "sending",
            timestampMs: Int64(Date().timeIntervalSince1970 * 1000)
        )
        var currentList = messages[chatId] ?? []
        currentList.append(outgoingMsg)
        messages[chatId] = currentList

        if let idx = chats.firstIndex(where: { $0.id == chatId }) {
            chats[idx].lastMessage = "📍 Live Location (\(durationMinutes) mins)"
            chats[idx].lastMessageTime = "Just now"
            chats[idx].lastMessageStatus = "sent"
        }
    }

    public func stopSharingLiveLocation(chatId: String, messageId: String) {
        guard var currentList = messages[chatId] else { return }
        if let idx = currentList.firstIndex(where: { $0.id == messageId }) {
            if var loc = currentList[idx].locationMeta {
                loc.isSharingActive = false
                currentList[idx].locationMeta = loc
                messages[chatId] = currentList
            }
        }
    }

    // MARK: - Google Drive E2EE Backup & Restore
    public func updateBackupFrequency(_ frequency: String) {
        backupConfig.frequency = frequency
    }

    public func backupChatsToGoogleDrive(completion: @escaping () -> Void = {}) {
        backupProgress = 0.10
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.4) {
            self.backupProgress = 0.45
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                self.backupProgress = 0.85
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.4) {
                    self.backupProgress = 1.0
                    self.backupConfig.lastBackupTimestampText = "Just now"
                    self.backupConfig.lastBackupSizeText = "43.1 MB"
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                        self.backupProgress = nil
                        completion()
                    }
                }
            }
        }
    }

    public func restoreChatsFromGoogleDrive(completion: @escaping () -> Void = {}) {
        restoreProgress = 0.15
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            self.restoreProgress = 0.60
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                self.restoreProgress = 1.0
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                    self.restoreProgress = nil
                    completion()
                }
            }
        }
    }

    public func setDisappearingMessages(chatId: String, timerSeconds: Int64) {
        if let idx = chats.firstIndex(where: { $0.id == chatId }) {
            chats[idx].disappearingTimerSeconds = timerSeconds
            if selectedChat?.id == chatId {
                selectedChat?.disappearingTimerSeconds = timerSeconds
            }
        }
    }
}

