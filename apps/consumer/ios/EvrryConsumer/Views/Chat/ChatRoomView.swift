//
//  ChatRoomView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  WhatsApp-Grade 1-on-1 & Contextual Support Chat Room View for iOS
//

import SwiftUI

public struct ChatRoomView: View {
    public let chat: SocialChat
    @ObservedObject public var chatViewModel: ChatViewModel
    public var onBack: () -> Void
    public var onAudioCallClick: () -> Void = {}
    public var onVideoCallClick: () -> Void = {}
    public var onViewOrderOrRide: (String) -> Void = { _ in }

    @State private var inputText: String = ""
    @State private var showAttachmentSheet: Bool = false
    @State private var showEmojiStickerSheet: Bool = false
    @State private var showWallpaperPicker: Bool = false
    @State private var wallpaperPresetId: String? = nil
    @State private var callNotice: String? = nil
    @State private var showProfileSheet: Bool = false
    @State private var showSecurityQrSheet: Bool = false
    @State private var showReportSheet: Bool = false
    @State private var profileNotice: String? = nil
    @State private var replyingToMessage: SocialMessage? = nil
    @State private var selectedActionMessage: SocialMessage? = nil

    public init(
        chat: SocialChat,
        chatViewModel: ChatViewModel,
        onBack: @escaping () -> Void,
        onAudioCallClick: @escaping () -> Void = {},
        onVideoCallClick: @escaping () -> Void = {},
        onViewOrderOrRide: @escaping (String) -> Void = { _ in }
    ) {
        self.chat = chat
        self.chatViewModel = chatViewModel
        self.onBack = onBack
        self.onAudioCallClick = onAudioCallClick
        self.onVideoCallClick = onVideoCallClick
        self.onViewOrderOrRide = onViewOrderOrRide
    }

    private var messages: [SocialMessage] {
        chatViewModel.getMessagesForChat(chat.id)
    }

    public var body: some View {
        VStack(spacing: 0) {
            // TOP BAR WITH IN-APP BACK BUTTON (iOS Navigation Rule)
            HStack(spacing: 8) {
                Button(action: onBack) {
                    HStack(spacing: 2) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 18, weight: .bold))
                    }
                    .foregroundColor(.evrryDarkCharcoal)
                    .frame(width: 44, height: 44)
                }

                Button(action: { showProfileSheet = true }) {
                    HStack(spacing: 8) {
                        Circle()
                            .fill(Color.evrrySubtleSurface)
                            .frame(width: 38, height: 38)
                            .overlay(
                                Text(String(chat.title.prefix(2)).uppercased())
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.evrryPrimaryBlue)
                            )
                            .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))

                        VStack(alignment: .leading, spacing: 2) {
                            HStack(spacing: 4) {
                                Text(chat.title)
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                    .lineLimit(1)
                                if chat.isGroup {
                                    Image(systemName: "person.2.fill")
                                        .font(.system(size: 10))
                                        .foregroundColor(.evrryMutedText)
                                }
                            }
                            Text(chat.isGroup ? "\(max(chat.participants.count, 3)) participants" : (chat.isOnline ? "Online" : (chat.subtitle ?? "Offline")))
                                .font(.system(size: 11, weight: chat.isOnline ? .semibold : .regular))
                                .foregroundColor(chat.isOnline ? Color.green : .evrryMutedText)
                                .lineLimit(1)
                        }
                    }
                }
                .buttonStyle(PlainButtonStyle())

                Spacer()

                // WALLPAPER BUTTON
                Button(action: {
                    withAnimation(.easeInOut(duration: 0.2)) {
                        showWallpaperPicker.toggle()
                        showEmojiStickerSheet = false
                    }
                }) {
                    Image(systemName: "photo.artframe")
                        .font(.system(size: 16))
                        .foregroundColor(showWallpaperPicker ? .evrryPrimaryBlue : .evrryPrimaryBlue)
                        .frame(width: 36, height: 44)
                }

                // AUDIO CALL & VIDEO CALL (Ready for Phase 3)
                Button(action: {
                    callNotice = "Voice call with \(chat.title)"
                    onAudioCallClick()
                }) {
                    Image(systemName: "phone.fill")
                        .font(.system(size: 16))
                        .foregroundColor(.evrryPrimaryBlue)
                        .frame(width: 40, height: 44)
                }

                Button(action: {
                    callNotice = "Video call with \(chat.title)"
                    onVideoCallClick()
                }) {
                    Image(systemName: "video.fill")
                        .font(.system(size: 18))
                        .foregroundColor(.evrryPrimaryBlue)
                        .frame(width: 40, height: 44)
                }
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(Color.white)
            .shadow(color: Color.black.opacity(0.06), radius: 2, x: 0, y: 1)

            // CONTEXTUAL BANNER
            if let tag = chat.referenceTag, let refId = chat.referenceId {
                HStack {
                    Image(systemName: chat.type == "RIDE_COORDINATION" ? "bicycle" : "fork.knife")
                        .font(.system(size: 16))
                        .foregroundColor(.evrryPrimaryBlue)

                    VStack(alignment: .leading, spacing: 2) {
                        Text(tag)
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("Direct operational communication channel")
                            .font(.system(size: 10))
                            .foregroundColor(.evrryMutedText)
                    }

                    Spacer()

                    Button(action: { onViewOrderOrRide(refId) }) {
                        Text("View")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 10)
                            .padding(.vertical, 4)
                            .background(Color.evrryPrimaryBlue)
                            .cornerRadius(12)
                    }
                }
                .padding(10)
                .background(Color(red: 0.94, green: 0.96, blue: 1.0))
                .cornerRadius(8)
                .padding(.horizontal, 10)
                .padding(.top, 6)
            }

            // CALL NOTICE
            if let notice = callNotice {
                HStack {
                    Text("Initiating \(notice) (Phase 3 WebRTC Engine)...")
                        .font(.system(size: 11))
                        .foregroundColor(Color.orange)
                    Spacer()
                    Button(action: { callNotice = nil }) {
                        Image(systemName: "xmark")
                            .font(.system(size: 12))
                            .foregroundColor(.evrryMutedText)
                    }
                }
                .padding(8)
                .background(Color(red: 1.0, green: 0.97, blue: 0.88))
                .cornerRadius(6)
                .padding(.horizontal, 10)
                .padding(.top, 4)
            }

            // DATE PILL
            HStack {
                Spacer()
                Text("TODAY")
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(.evrryMutedText)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 3)
                    .background(Color.white.opacity(0.9))
                    .cornerRadius(10)
                    .shadow(color: Color.black.opacity(0.04), radius: 2)
                Spacer()
            }
            .padding(.vertical, 8)

            // MESSAGE LIST
            ScrollViewReader { proxy in
                ScrollView {
                    LazyVStack(spacing: 8) {
                        // E2EE SECURITY NOTICE
                        HStack(alignment: .top, spacing: 6) {
                            Image(systemName: "lock.fill")
                                .font(.system(size: 10))
                                .foregroundColor(Color(red: 0.52, green: 0.30, blue: 0.05))
                                .padding(.top, 2)
                            Text("Messages and calls are end-to-end encrypted. No one outside of this chat, not even Evrry, can read or listen to them.")
                                .font(.system(size: 11))
                                .foregroundColor(Color(red: 0.52, green: 0.30, blue: 0.05))
                                .multilineTextAlignment(.center)
                                .lineSpacing(2)
                        }
                        .padding(.horizontal, 14)
                        .padding(.vertical, 7)
                        .background(Color(red: 1.0, green: 0.98, blue: 0.77).opacity(0.95))
                        .cornerRadius(10)
                        .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color(red: 0.99, green: 0.88, blue: 0.28), lineWidth: 0.5))
                        .padding(.horizontal, 16)
                        .padding(.bottom, 4)

                        ForEach(messages) { msg in
                            MessageBubbleView(
                                msg: msg,
                                onCallBack: onAudioCallClick,
                                onMessageClick: { selectedActionMessage = msg },
                                onStopLiveLocation: { msgId in chatViewModel.stopSharingLiveLocation(chatId: chat.id, messageId: msgId) }
                            )
                            .id(msg.id)
                        }
                    }
                    .padding(.horizontal, 12)
                    .padding(.bottom, 8)
                }
                .onAppear {
                    if let last = messages.last {
                        proxy.scrollTo(last.id, anchor: .bottom)
                    }
                }
                .onChange(of: messages.count) { _ in
                    if let last = messages.last {
                        withAnimation { proxy.scrollTo(last.id, anchor: .bottom) }
                    }
                }
            }
            .background(
                Group {
                    if let wp = wallpaperPresetId, (wp.hasPrefix("data:") || wp.hasPrefix("http") || wp.hasPrefix("file:")) {
                        ZStack {
                            AsyncImage(url: URL(string: wp)) { phase in
                                if let img = phase.image {
                                    img.resizable().scaledToFill()
                                } else {
                                    Color.gray.opacity(0.15)
                                }
                            }
                            Color.black.opacity(0.20) // WhatsApp dimming overlay
                        }
                    } else if let wp = wallpaperPresetId {
                        Color.clear.overlay(wallpaperGradient(for: wp))
                    } else {
                        Color(red: 0.94, green: 0.96, blue: 0.98)
                    }
                }
            )


            // INPUT BAR
            VStack(spacing: 0) {
                if let reply = replyingToMessage {
                    HStack(spacing: 8) {
                        RoundedRectangle(cornerRadius: 2)
                            .fill(Color.evrryPrimaryBlue)
                            .frame(width: 3, height: 32)
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Replying to \(reply.senderName)")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryPrimaryBlue)
                            Text(reply.text)
                                .font(.system(size: 11))
                                .foregroundColor(.evrryMutedText)
                                .lineLimit(1)
                        }
                        Spacer()
                        Button(action: { replyingToMessage = nil }) {
                            Image(systemName: "xmark.circle.fill")
                                .font(.system(size: 16))
                                .foregroundColor(.evrryMutedText)
                        }
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(Color(red: 0.95, green: 0.96, blue: 0.98))
                    .cornerRadius(8)
                    .padding(.horizontal, 10)
                    .padding(.top, 4)
                }

                // Emoji / Sticker sheet (shown inline above input)
                if showEmojiStickerSheet {
                    EmojiStickerSheetView(
                        onEmojiPick: { emoji in
                            inputText += emoji
                            showEmojiStickerSheet = false
                        },
                        onStickerPick: { url in
                            chatViewModel.sendSticker(chatId: chat.id, stickerUrl: url)
                            showEmojiStickerSheet = false
                        },
                        onClose: { showEmojiStickerSheet = false }
                    )
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                }

                // Wallpaper picker (shown inline above input)
                if showWallpaperPicker {
                    WallpaperPickerView(
                        chatId: chat.id,
                        currentPresetId: wallpaperPresetId,
                        onPick: { presetId in
                            wallpaperPresetId = presetId
                            chatViewModel.setWallpaper(chatId: chat.id, url: presetId)
                            showWallpaperPicker = false
                        },
                        onClose: { showWallpaperPicker = false }
                    )
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                }

                HStack(spacing: 8) {
                    // Attach button
                    Button(action: { showAttachmentSheet = true }) {
                        Image(systemName: "paperclip")
                            .font(.system(size: 20))
                            .foregroundColor(.evrryMutedText)
                    }

                    // Emoji / Sticker toggle
                    Button(action: {
                        withAnimation(.easeInOut(duration: 0.2)) {
                            showEmojiStickerSheet.toggle()
                            showWallpaperPicker = false
                        }
                    }) {
                        Image(systemName: showEmojiStickerSheet ? "face.smiling.fill" : "face.smiling")
                            .font(.system(size: 20))
                            .foregroundColor(showEmojiStickerSheet ? .evrryPrimaryBlue : .evrryMutedText)
                    }

                    TextField("Message...", text: $inputText)
                        .font(.system(size: 14))
                        .padding(.horizontal, 14)
                        .padding(.vertical, 8)
                        .background(Color.evrrySubtleSurface)
                        .cornerRadius(20)

                    // VOICE NOTE MIC (Always accessible beside Send button)
                    Button(action: {
                        chatViewModel.sendVoiceNote(chatId: chat.id, duration: 14)
                    }) {
                        Circle()
                            .fill(inputText.trimmingCharacters(in: .whitespaces).isEmpty ? Color.evrryPrimaryBlue : Color(UIColor.systemGray6))
                            .frame(width: 40, height: 40)
                            .overlay(
                                Image(systemName: "mic.fill")
                                    .font(.system(size: 18))
                                    .foregroundColor(inputText.trimmingCharacters(in: .whitespaces).isEmpty ? .white : .evrryDarkCharcoal)
                            )
                    }

                    // SEND BUTTON (Right beside Voice Note whenever text is present)
                    if !inputText.trimmingCharacters(in: .whitespaces).isEmpty {
                        Button(action: {
                            chatViewModel.sendMessage(chatId: chat.id, text: inputText, replyToMessage: replyingToMessage)
                            inputText = ""
                            replyingToMessage = nil
                        }) {
                            Circle()
                                .fill(Color.evrryPrimaryBlue)
                                .frame(width: 40, height: 40)
                                .overlay(
                                    Image(systemName: "paperplane.fill")
                                        .font(.system(size: 16))
                                        .foregroundColor(.white)
                                )
                        }
                    }
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 8)
            }
            .background(Color.white)
            .shadow(color: Color.black.opacity(0.08), radius: 4, x: 0, y: -2)
        }
        .actionSheet(isPresented: $showAttachmentSheet) {
            ActionSheet(
                title: Text("Share with \(chat.title)"),
                buttons: [
                    .default(Text("📍 Share Live Location (60m)")) {
                        chatViewModel.sendLiveLocation(chatId: chat.id, durationMinutes: 60)
                    },
                    .default(Text("🏍️ Live Moto Tracking")) {
                        chatViewModel.sendLiveRideShare(chatId: chat.id)
                    },
                    .default(Text("📷 Camera Snap")),
                    .default(Text("🖼️ Photo / Video Gallery")),
                    .cancel()
                ]
            )
        }
        .sheet(isPresented: $showProfileSheet) {
            if chat.isGroup {
                GroupProfileSheetView(
                    chat: chat,
                    onAudioCall: {
                        showProfileSheet = false
                        onAudioCallClick()
                    },
                    onVideoCall: {
                        showProfileSheet = false
                        onVideoCallClick()
                    },
                    onExitGroup: {
                        showProfileSheet = false
                        onBack()
                    },
                    onReportGroup: {
                        showProfileSheet = false
                        showReportSheet = true
                    }
                )
            } else {
                ContactProfileSheetView(
                    profile: chatViewModel.getContactProfile(chat: chat),
                    isBlocked: chatViewModel.blockedUserIds.contains(chatViewModel.getContactProfile(chat: chat).userId),
                    onAudioCall: {
                        showProfileSheet = false
                        onAudioCallClick()
                    },
                    onVideoCall: {
                        showProfileSheet = false
                        onVideoCallClick()
                    },
                    onToggleBlock: { userId in
                        let blocked = chatViewModel.toggleBlockContact(userId: userId)
                        profileNotice = blocked ? "Blocked contact" : "Unblocked contact"
                    },
                    onToggleMute: {
                        chatViewModel.toggleMuteChat(chatId: chat.id)
                    },
                    onVerifySecurity: {
                        showProfileSheet = false
                        showSecurityQrSheet = true
                    },
                    onReport: {
                        showProfileSheet = false
                        showReportSheet = true
                    }
                )
            }
        }
        .sheet(isPresented: $showSecurityQrSheet) {
            SecurityQrCodeSheetView(contactTitle: chat.title)
        }
        .sheet(isPresented: $showReportSheet) {
            ReportContactSheetView(
                targetTitle: chat.title,
                isGroup: chat.isGroup,
                onSubmitted: { reason in
                    _ = chatViewModel.reportContact(userId: chat.id, reason: reason)
                    showReportSheet = false
                }
            )
        }
        .actionSheet(item: $selectedActionMessage) { msg in
            ActionSheet(
                title: Text("Message Actions"),
                buttons: [
                    .default(Text("❤️ Love")) { chatViewModel.toggleReaction(chatId: chat.id, messageId: msg.id, emoji: "❤️") },
                    .default(Text("👍 Thumbs Up")) { chatViewModel.toggleReaction(chatId: chat.id, messageId: msg.id, emoji: "👍") },
                    .default(Text("😂 Laugh")) { chatViewModel.toggleReaction(chatId: chat.id, messageId: msg.id, emoji: "😂") },
                    .default(Text("😮 Wow")) { chatViewModel.toggleReaction(chatId: chat.id, messageId: msg.id, emoji: "😮") },
                    .default(Text("😢 Sad")) { chatViewModel.toggleReaction(chatId: chat.id, messageId: msg.id, emoji: "😢") },
                    .default(Text("🙏 Pray")) { chatViewModel.toggleReaction(chatId: chat.id, messageId: msg.id, emoji: "🙏") },
                    .default(Text("↩️ Reply")) { replyingToMessage = msg },
                    .default(Text(msg.isStarred ? "⭐ Unstar Message" : "⭐ Star Message")) { _ = chatViewModel.toggleStarMessage(chatId: chat.id, messageId: msg.id) },
                    .cancel()
                ]
            )
        }
    }
}

// MARK: - Message Bubble View
struct MessageBubbleView: View {
    let msg: SocialMessage
    var onCallBack: () -> Void = {}
    var onMessageClick: () -> Void = {}
    var onStopLiveLocation: (String) -> Void = { _ in }

    var body: some View {
        if msg.messageType == "CALL_LOG" {
            InChatCallLogCardView(msg: msg, onCallBack: onCallBack)
        } else {
            HStack {
                if msg.isMe { Spacer() }

                VStack(alignment: msg.isMe ? .trailing : .leading, spacing: 4) {
                    // Quoted reply snippet
                    if let replyText = msg.replyToText {
                        HStack(spacing: 6) {
                            RoundedRectangle(cornerRadius: 2)
                                .fill(msg.isMe ? Color.white : Color.evrryPrimaryBlue)
                                .frame(width: 3, height: 26)
                            VStack(alignment: .leading, spacing: 1) {
                                Text(msg.replyToSender ?? "Reply")
                                    .font(.system(size: 10, weight: .bold))
                                    .foregroundColor(msg.isMe ? .white : .evrryPrimaryBlue)
                                Text(replyText)
                                    .font(.system(size: 11))
                                    .foregroundColor(msg.isMe ? Color.white.opacity(0.85) : .evrryMutedText)
                                    .lineLimit(1)
                            }
                        }
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(msg.isMe ? Color.white.opacity(0.18) : Color.evrrySubtleSurface)
                        .cornerRadius(8)
                    }

                    if msg.messageType == "VOICE_NOTE" {
                        VoiceNoteBubbleView(msg: msg)
                    } else if msg.messageType == "LIVE_RIDE_SHARE" {
                        LiveRideShareBubbleView(msg: msg)
                    } else if msg.messageType == "LIVE_LOCATION" {
                        LiveLocationBubbleView(msg: msg, onStopSharing: { onStopLiveLocation(msg.id) })
                    } else if msg.messageType == "STICKER" || msg.messageType == "ANIMATED_EMOJI",
                              let stickerUrl = msg.stickerUrl {
                        // Sticker / animated emoji — no bubble background, just the image
                        AsyncImage(url: URL(string: stickerUrl)) { phase in
                            switch phase {
                            case .success(let image):
                                image.resizable().scaledToFit()
                                    .frame(width: 100, height: 100)
                            case .failure:
                                Text("🎭").font(.system(size: 60))
                            default:
                                ProgressView().frame(width: 80, height: 80)
                            }
                        }
                        .padding(2)
                    } else {
                        Text(msg.text)
                            .font(.system(size: 14))
                            .foregroundColor(msg.isMe ? .white : .evrryDarkCharcoal)
                            .lineSpacing(2)
                    }

                    HStack(spacing: 4) {
                        if msg.isStarred {
                            Image(systemName: "star.fill")
                                .font(.system(size: 10))
                                .foregroundColor(msg.isMe ? Color(red: 1.0, green: 0.88, blue: 0.28) : Color.yellow)
                        }

                        Text(formatTime(msg.timestampMs))
                            .font(.system(size: 10))
                            .foregroundColor(msg.isMe ? Color.white.opacity(0.8) : .evrryMutedText)

                        if msg.isMe {
                            DeliveryTickView(status: msg.status)
                        }
                    }
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
                .background(msg.isMe ? Color.evrryPrimaryBlue : Color.white)
                .cornerRadius(16)
                .shadow(color: Color.black.opacity(0.04), radius: 1, x: 0, y: 1)
                .frame(maxWidth: 280, alignment: msg.isMe ? .trailing : .leading)
                .contentShape(Rectangle())
                .onTapGesture {
                    onMessageClick()
                }
                .overlay(alignment: msg.isMe ? .bottomTrailing : .bottomLeading) {
                    if !msg.reactions.isEmpty {
                        HStack(spacing: 2) {
                            ForEach(Array(msg.reactions.keys), id: \.self) { emoji in
                                Text(emoji)
                                    .font(.system(size: 11))
                            }
                        }
                        .padding(.horizontal, 6)
                        .padding(.vertical, 2)
                        .background(Color.white)
                        .cornerRadius(12)
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrrySubtleBorder, lineWidth: 0.5))
                        .shadow(color: Color.black.opacity(0.06), radius: 2)
                        .offset(y: 12)
                    }
                }

                if !msg.isMe { Spacer() }
            }
            .padding(.bottom, msg.reactions.isEmpty ? 0 : 8)
        }
    }

    private func formatTime(_ ms: Int64) -> String {
        let date = Date(timeIntervalSince1970: Double(ms) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateFormat = "h:mm a"
        return formatter.string(from: date)
    }
}

// MARK: - In-Chat Call Log Card View
struct InChatCallLogCardView: View {
    let msg: SocialMessage
    let onCallBack: () -> Void

    var body: some View {
        let meta = msg.callMeta
        let isMissed = meta?.isMissed ?? false
        let isVideo = meta?.callType == "VIDEO"

        Button(action: onCallBack) {
            HStack(spacing: 10) {
                Circle()
                    .fill(isMissed ? Color.red.opacity(0.12) : Color.evrryPrimaryBlue.opacity(0.12))
                    .frame(width: 36, height: 36)
                    .overlay(
                        Image(systemName: isVideo ? "video.fill" : "phone.fill")
                            .font(.system(size: 16))
                            .foregroundColor(isMissed ? Color.red : .evrryPrimaryBlue)
                    )

                VStack(alignment: .leading, spacing: 2) {
                    Text(isMissed ? (isVideo ? "Missed Video Call" : "Missed Voice Call") :
                            (isVideo ? "Video Call\(durationText)" : "Voice Call\(durationText)"))
                        .font(.system(size: 13, weight: .semibold))
                        .foregroundColor(isMissed ? Color.red : .evrryDarkCharcoal)

                    Text(meta?.timestampText ?? "Tap to call back")
                        .font(.system(size: 11))
                        .foregroundColor(.evrryMutedText)
                }

                Spacer().frame(width: 6)

                Circle()
                    .fill(Color.evrryPrimaryBlue)
                    .frame(width: 28, height: 28)
                    .overlay(
                        Image(systemName: "phone.fill")
                            .font(.system(size: 12))
                            .foregroundColor(.white)
                    )
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 9)
            .background(Color(red: 0.97, green: 0.98, blue: 0.99))
            .cornerRadius(14)
            .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color(red: 0.89, green: 0.91, blue: 0.94), lineWidth: 1))
        }
        .padding(.vertical, 6)
        .frame(maxWidth: .infinity, alignment: .center)
    }

    private var durationText: String {
        guard let s = msg.callMeta?.durationSeconds, s > 0 else { return "" }
        return " (\(s / 60)m \(s % 60)s)"
    }
}

// MARK: - Voice Note Bubble View
struct VoiceNoteBubbleView: View {
    let msg: SocialMessage
    @State private var isPlaying: Bool = false

    var body: some View {
        HStack(spacing: 8) {
            Button(action: { isPlaying.toggle() }) {
                Circle()
                    .fill(msg.isMe ? Color.white.opacity(0.25) : Color.evrrySubtleSurface)
                    .frame(width: 34, height: 34)
                    .overlay(
                        Image(systemName: isPlaying ? "pause.fill" : "play.fill")
                            .font(.system(size: 14))
                            .foregroundColor(msg.isMe ? .white : .evrryPrimaryBlue)
                    )
            }

            VStack(alignment: .leading, spacing: 2) {
                // Waveform bars
                HStack(spacing: 2) {
                    ForEach([6, 12, 16, 8, 14, 18, 10, 15, 7, 13, 9, 17, 11, 8, 14], id: \.self) { height in
                        RoundedRectangle(cornerRadius: 1.5)
                            .fill(msg.isMe ? Color.white.opacity(0.85) : Color.evrryPrimaryBlue.opacity(0.7))
                            .frame(width: 3, height: CGFloat(height))
                    }
                }
                .frame(height: 18)

                Text("0:\(msg.audioDurationSeconds ?? 12)")
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(msg.isMe ? Color.white.opacity(0.85) : .evrryMutedText)
            }
        }
        .padding(.vertical, 4)
    }
}

// MARK: - Live Ride Share Bubble View
struct LiveRideShareBubbleView: View {
    let msg: SocialMessage

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack(spacing: 6) {
                Image(systemName: "bicycle")
                    .font(.system(size: 14, weight: .bold))
                Text("LIVE RIDE & TRIP TRACKING")
                    .font(.system(size: 11, weight: .black))
            }
            .foregroundColor(msg.isMe ? .white : .evrryPrimaryBlue)

            // Live 3D Isometric Map Preview inside Chat Bubble
            EvrryLiveTrackingMapView(
                vendorName: "Pickup Location",
                destinationAddress: msg.rideMeta?.destinationAddress ?? "Sanepa Heights",
                vehicleType: .motorcycle,
                riderName: msg.rideMeta?.riderName,
                etaMinutes: msg.rideMeta?.etaMinutes ?? 12,
                speedKmh: 28
            )
            .frame(height: 130)

            if let meta = msg.rideMeta {
                VStack(alignment: .leading, spacing: 3) {
                    Text("\(meta.riderName) • \(meta.vehicleNumber)")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(msg.isMe ? .white : .evrryDarkCharcoal)
                    Text("ETA: \(meta.etaMinutes) mins to destination")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(msg.isMe ? Color.white.opacity(0.9) : .evrryPrimaryBlue)
                    Text("To: \(meta.destinationAddress)")
                        .font(.system(size: 10))
                        .foregroundColor(msg.isMe ? Color.white.opacity(0.8) : .evrryMutedText)
                        .lineLimit(1)
                }
                .padding(8)
                .background(msg.isMe ? Color.white.opacity(0.15) : Color.evrrySubtleSurface)
                .cornerRadius(8)
            }
        }
        .padding(.vertical, 4)
    }
}

// MARK: - Live Location Bubble View
struct LiveLocationBubbleView: View {
    let msg: SocialMessage
    var onStopSharing: () -> Void = {}

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack(spacing: 6) {
                Image(systemName: "location.fill")
                    .font(.system(size: 13, weight: .bold))
                Text("LIVE LOCATION")
                    .font(.system(size: 11, weight: .black))
            }
            .foregroundColor(msg.isMe ? .white : Color(red: 0.02, green: 0.59, blue: 0.41))

            if let meta = msg.locationMeta {
                VStack(alignment: .leading, spacing: 3) {
                    Text(meta.address)
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(msg.isMe ? .white : .evrryDarkCharcoal)
                    Text(meta.isSharingActive ? "● \(meta.expiresAtTimeText)" : "Live sharing ended")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(meta.isSharingActive ? (msg.isMe ? Color(red: 0.52, green: 0.94, blue: 0.67) : Color(red: 0.02, green: 0.59, blue: 0.41)) : (msg.isMe ? Color.white.opacity(0.7) : .evrryMutedText))

                    if msg.isMe && meta.isSharingActive {
                        Button(action: onStopSharing) {
                            Text("Stop Sharing")
                                .font(.system(size: 10, weight: .bold))
                                .foregroundColor(msg.isMe ? .white : Color.red)
                                .padding(.horizontal, 8)
                                .padding(.vertical, 3)
                                .background(msg.isMe ? Color.white.opacity(0.25) : Color.red.opacity(0.12))
                                .cornerRadius(6)
                        }
                        .padding(.top, 4)
                    }
                }
                .padding(8)
                .background(msg.isMe ? Color.white.opacity(0.15) : Color.evrrySubtleSurface)
                .cornerRadius(8)
            }
        }
        .padding(.vertical, 4)
    }
}

// MARK: - Group Profile Sheet View
struct GroupProfileSheetView: View {
    let chat: SocialChat
    var onAudioCall: () -> Void = {}
    var onVideoCall: () -> Void = {}
    var onExitGroup: () -> Void = {}
    var onReportGroup: () -> Void = {}
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 16) {
                    // Large Group Avatar
                    Circle()
                        .fill(Color.evrryPrimaryBlue.opacity(0.12))
                        .frame(width: 80, height: 80)
                        .overlay(
                            Image(systemName: "person.2.fill")
                                .font(.system(size: 32, weight: .bold))
                                .foregroundColor(.evrryPrimaryBlue)
                        )
                        .padding(.top, 12)

                    VStack(spacing: 4) {
                        Text(chat.title)
                            .font(.system(size: 20, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                            .multilineTextAlignment(.center)
                        Text("Group • \(max(chat.participants.count, 3)) participants")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)
                    }

                    // Action buttons
                    HStack(spacing: 16) {
                        Button(action: onAudioCall) {
                            HStack(spacing: 6) {
                                Image(systemName: "phone.fill")
                                Text("Voice Call")
                            }
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(.evrryPrimaryBlue)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 10)
                            .background(Color.evrryPrimaryBlue.opacity(0.08))
                            .cornerRadius(12)
                        }

                        Button(action: onVideoCall) {
                            HStack(spacing: 6) {
                                Image(systemName: "video.fill")
                                Text("Video Call")
                            }
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(.evrryPrimaryBlue)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 10)
                            .background(Color.evrryPrimaryBlue.opacity(0.08))
                            .cornerRadius(12)
                        }
                    }

                    // Description card
                    VStack(alignment: .leading, spacing: 6) {
                        Text("GROUP DESCRIPTION")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                        Text(chat.groupDescription ?? "Official Evrry group conversation")
                            .font(.system(size: 14))
                            .foregroundColor(.evrryDarkCharcoal)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(14)
                    .background(Color.evrrySubtleSurface)
                    .cornerRadius(12)

                    // Participants List
                    VStack(alignment: .leading, spacing: 10) {
                        Text("\(max(chat.participants.count, 3)) PARTICIPANTS")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)

                        ForEach(chat.participants) { participant in
                            HStack(spacing: 12) {
                                Circle()
                                    .fill(Color.evrryPrimaryBlue.opacity(0.12))
                                    .frame(width: 36, height: 36)
                                    .overlay(
                                        Text(String(participant.name.prefix(2)).uppercased())
                                            .font(.system(size: 12, weight: .bold))
                                            .foregroundColor(.evrryPrimaryBlue)
                                    )

                                VStack(alignment: .leading, spacing: 2) {
                                    Text(participant.name)
                                        .font(.system(size: 14, weight: .semibold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                    Text(participant.statusBio)
                                        .font(.system(size: 11))
                                        .foregroundColor(.evrryMutedText)
                                }

                                Spacer()

                                if participant.role == "ADMIN" {
                                    Text("Group Admin")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(Color(red: 0.09, green: 0.64, blue: 0.29))
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 3)
                                        .background(Color(red: 0.86, green: 0.99, blue: 0.90))
                                        .cornerRadius(6)
                                }
                            }
                            .padding(.vertical, 4)
                            Divider()
                        }
                    }
                    .padding(14)
                    .background(Color.evrrySubtleSurface)
                    .cornerRadius(12)

                    // Exit & Report Group
                    VStack(spacing: 10) {
                        Button(action: onReportGroup) {
                            HStack {
                                Image(systemName: "exclamationmark.triangle")
                                Text("Report Group")
                            }
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.evrryRedDestructive)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(Color.white)
                            .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrryRedDestructive.opacity(0.4), lineWidth: 1))
                            .cornerRadius(12)
                        }

                        Button(action: onExitGroup) {
                            HStack {
                                Image(systemName: "rectangle.portrait.and.arrow.right")
                                Text("Exit Group")
                            }
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(Color.evrryRedDestructive)
                            .cornerRadius(12)
                        }
                    }
                    .padding(.top, 4)
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 24)
            }
            .navigationTitle("Group Info")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") { presentationMode.wrappedValue.dismiss() }
                }
            }
        }
    }
}

// MARK: - Contact Profile Sheet View
struct ContactProfileSheetView: View {
    let profile: PublicContactProfile
    let isBlocked: Bool
    var onAudioCall: () -> Void = {}
    var onVideoCall: () -> Void = {}
    var onToggleBlock: (String) -> Void = { _ in }
    var onToggleMute: () -> Void = {}
    var onVerifySecurity: () -> Void = {}
    var onReport: () -> Void = {}
    @Environment(\.presentationMode) var presentationMode
    @State private var isMutedLocal: Bool = false

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 16) {
                    // Avatar & Name Header
                    Circle()
                        .fill(Color.evrryPrimaryBlue.opacity(0.12))
                        .frame(width: 84, height: 84)
                        .overlay(
                            Text(String(profile.name.prefix(2)).uppercased())
                                .font(.system(size: 28, weight: .bold))
                                .foregroundColor(.evrryPrimaryBlue)
                        )
                        .padding(.top, 10)

                    VStack(spacing: 4) {
                        Text(profile.name)
                            .font(.system(size: 20, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("@\(profile.handle ?? "user") • \(profile.phone)")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)
                    }

                    // Quick Action Buttons
                    HStack(spacing: 12) {
                        Button(action: onAudioCall) {
                            VStack(spacing: 4) {
                                Image(systemName: "phone.fill")
                                    .font(.system(size: 16))
                                Text("Audio")
                                    .font(.system(size: 11, weight: .semibold))
                            }
                            .foregroundColor(.evrryPrimaryBlue)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 10)
                            .background(Color.evrrySubtleSurface)
                            .cornerRadius(12)
                        }

                        Button(action: onVideoCall) {
                            VStack(spacing: 4) {
                                Image(systemName: "video.fill")
                                    .font(.system(size: 16))
                                Text("Video")
                                    .font(.system(size: 11, weight: .semibold))
                            }
                            .foregroundColor(.evrryPrimaryBlue)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 10)
                            .background(Color.evrrySubtleSurface)
                            .cornerRadius(12)
                        }

                        Button(action: { /* Share Contact */ }) {
                            VStack(spacing: 4) {
                                Image(systemName: "square.and.arrow.up")
                                    .font(.system(size: 16))
                                Text("Share")
                                    .font(.system(size: 11, weight: .semibold))
                            }
                            .foregroundColor(.evrryPrimaryBlue)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 10)
                            .background(Color.evrrySubtleSurface)
                            .cornerRadius(12)
                        }
                    }

                    // About / Bio Card
                    VStack(alignment: .leading, spacing: 10) {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("ABOUT / BIO")
                                .font(.system(size: 10, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                            Text(profile.statusBio)
                                .font(.system(size: 14, weight: .medium))
                                .foregroundColor(.evrryDarkCharcoal)
                        }
                        Divider()
                        VStack(alignment: .leading, spacing: 4) {
                            Text("PHONE NUMBER")
                                .font(.system(size: 10, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                            Text(profile.phone)
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(.evrryPrimaryBlue)
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(14)
                    .background(Color.evrrySubtleSurface)
                    .cornerRadius(14)

                    // Media, Links & Docs Card
                    VStack(alignment: .leading, spacing: 10) {
                        HStack {
                            Text("MEDIA, LINKS & DOCS")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                            Spacer()
                            Text("\(profile.sharedMedia.count) items >")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryPrimaryBlue)
                        }

                        HStack(spacing: 8) {
                            ForEach(profile.sharedMedia) { item in
                                RoundedRectangle(cornerRadius: 10)
                                    .fill(Color.evrrySubtleBorder.opacity(0.6))
                                    .frame(height: 64)
                                    .overlay(
                                        Image(systemName: "photo")
                                            .font(.system(size: 22))
                                            .foregroundColor(.evrryPrimaryBlue)
                                    )
                            }
                        }
                    }
                    .padding(14)
                    .background(Color.evrrySubtleSurface)
                    .cornerRadius(14)

                    // Common Groups Card
                    if !profile.commonGroups.isEmpty {
                        VStack(alignment: .leading, spacing: 10) {
                            Text("\(profile.commonGroups.count) GROUPS IN COMMON")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)

                            ForEach(profile.commonGroups) { grp in
                                HStack(spacing: 10) {
                                    Circle()
                                        .fill(Color.evrryPrimaryBlue.opacity(0.12))
                                        .frame(width: 32, height: 32)
                                        .overlay(
                                            Image(systemName: "person.2.fill")
                                                .font(.system(size: 13))
                                                .foregroundColor(.evrryPrimaryBlue)
                                        )

                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(grp.title)
                                            .font(.system(size: 13, weight: .semibold))
                                            .foregroundColor(.evrryDarkCharcoal)
                                        Text("\(grp.membersCount) participants")
                                            .font(.system(size: 11))
                                            .foregroundColor(.evrryMutedText)
                                    }
                                    Spacer()
                                }
                                .padding(.vertical, 2)
                            }
                        }
                        .padding(14)
                        .background(Color.evrrySubtleSurface)
                        .cornerRadius(14)
                    }

                    // Security & Encryption Card
                    VStack(spacing: 12) {
                        Button(action: onVerifySecurity) {
                            HStack(spacing: 10) {
                                Image(systemName: "lock.fill")
                                    .font(.system(size: 16))
                                    .foregroundColor(Color(red: 0.09, green: 0.64, blue: 0.29))

                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Encryption")
                                        .font(.system(size: 13, weight: .bold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                    Text("End-to-end encrypted. Tap to verify security QR code.")
                                        .font(.system(size: 11))
                                        .foregroundColor(.evrryMutedText)
                                }

                                Spacer()

                                Image(systemName: "qrcode")
                                    .font(.system(size: 18))
                                    .foregroundColor(.evrryPrimaryBlue)
                            }
                        }

                        Divider()

                        Toggle(isOn: $isMutedLocal) {
                            HStack(spacing: 10) {
                                Image(systemName: "bell.slash.fill")
                                    .font(.system(size: 15))
                                    .foregroundColor(.evrryMutedText)
                                Text("Mute Notifications")
                                    .font(.system(size: 13, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                            }
                        }
                        .onChange(of: isMutedLocal) { _ in
                            onToggleMute()
                        }
                    }
                    .padding(14)
                    .background(Color.evrrySubtleSurface)
                    .cornerRadius(14)

                    // Danger Zone (Block & Report)
                    VStack(spacing: 12) {
                        Button(action: { onToggleBlock(profile.userId) }) {
                            HStack(spacing: 10) {
                                Image(systemName: "slash.circle")
                                    .font(.system(size: 16, weight: .bold))
                                Text(isBlocked ? "Unblock \(profile.name)" : "Block \(profile.name)")
                                    .font(.system(size: 14, weight: .bold))
                            }
                            .foregroundColor(.evrryRedDestructive)
                            .frame(maxWidth: .infinity, alignment: .leading)
                        }

                        Divider()

                        Button(action: onReport) {
                            HStack(spacing: 10) {
                                Image(systemName: "exclamationmark.triangle")
                                    .font(.system(size: 16, weight: .bold))
                                Text("Report \(profile.name)")
                                    .font(.system(size: 14, weight: .bold))
                            }
                            .foregroundColor(.evrryRedDestructive)
                            .frame(maxWidth: .infinity, alignment: .leading)
                        }
                    }
                    .padding(14)
                    .background(Color.white)
                    .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    .cornerRadius(14)
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 24)
            }
            .navigationTitle("Contact Info")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") { presentationMode.wrappedValue.dismiss() }
                }
            }
            .onAppear {
                self.isMutedLocal = profile.isMuted
            }
        }
    }
}

// MARK: - Security QR Code Sheet View
struct SecurityQrCodeSheetView: View {
    let contactTitle: String
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Text("Scan this QR code on another device or compare numbers to verify end-to-end encryption with \(contactTitle).")
                    .font(.system(size: 13))
                    .foregroundColor(.evrryMutedText)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 24)
                    .padding(.top, 16)

                // Mock QR Code Box
                ZStack {
                    RoundedRectangle(cornerRadius: 16)
                        .fill(Color.white)
                        .frame(width: 200, height: 200)
                        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 2))
                        .shadow(color: Color.black.opacity(0.06), radius: 8)

                    Image(systemName: "qrcode")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 160, height: 160)
                        .foregroundColor(.evrryDarkCharcoal)

                    Circle()
                        .fill(Color.white)
                        .frame(width: 44, height: 44)
                        .overlay(
                            Image(systemName: "lock.shield.fill")
                                .font(.system(size: 22))
                                .foregroundColor(Color(red: 0.09, green: 0.64, blue: 0.29))
                        )
                }

                VStack(spacing: 4) {
                    Text("SIGNAL DOUBLE RATCHET FINGERPRINT")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                    Text("4819 2019 4820 9182 3019 4819\n0281 9482 1029 4829 1049 2038")
                        .font(.system(size: 13, weight: .medium, design: .monospaced))
                        .foregroundColor(.evrryDarkCharcoal)
                        .multilineTextAlignment(.center)
                        .padding(12)
                        .background(Color.evrrySubtleSurface)
                        .cornerRadius(10)
                }
                .padding(.horizontal, 20)

                Spacer()
            }
            .navigationTitle("Verify Security Code")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") { presentationMode.wrappedValue.dismiss() }
                }
            }
        }
    }
}

// MARK: - Report Contact Sheet View
struct ReportContactSheetView: View {
    let targetTitle: String
    let isGroup: Bool
    var onSubmitted: (String) -> Void
    @Environment(\.presentationMode) var presentationMode
    @State private var selectedReason: String = "Spam / Unsolicited messages"

    private let reasons = [
        "Spam / Unsolicited messages",
        "Harassment or Hate speech",
        "Fraud, Scam, or Impersonation",
        "Other Trust & Safety concerns"
    ]

    var body: some View {
        NavigationView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Report \"\(targetTitle)\"")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(.evrryDarkCharcoal)
                    .padding(.top, 16)

                Text("The last 5 messages will be forwarded to the Evrry Trust & Safety team. No one in the chat will be notified.")
                    .font(.system(size: 13))
                    .foregroundColor(.evrryMutedText)

                VStack(spacing: 8) {
                    ForEach(reasons, id: \.self) { reason in
                        Button(action: { selectedReason = reason }) {
                            HStack {
                                Text(reason)
                                    .font(.system(size: 14))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Spacer()
                                if selectedReason == reason {
                                    Image(systemName: "checkmark.circle.fill")
                                        .foregroundColor(.evrryPrimaryBlue)
                                } else {
                                    Image(systemName: "circle")
                                        .foregroundColor(.evrrySubtleBorder)
                                }
                            }
                            .padding(12)
                            .background(Color.evrrySubtleSurface)
                            .cornerRadius(10)
                        }
                    }
                }

                Spacer()

                Button(action: {
                    onSubmitted(selectedReason)
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Text("Submit Report")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 14)
                        .background(Color.evrryRedDestructive)
                        .cornerRadius(12)
                }
                .padding(.bottom, 16)
            }
            .padding(.horizontal, 20)
            .navigationTitle("Trust & Safety")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") { presentationMode.wrappedValue.dismiss() }
                }
            }
        }
    }
}
