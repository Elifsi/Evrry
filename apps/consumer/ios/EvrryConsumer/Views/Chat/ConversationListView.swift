//
//  ConversationListView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  WhatsApp-Grade Conversation, Channels & Call History Hub for iOS
//

import SwiftUI

public struct ConversationListView: View {
    @ObservedObject public var chatViewModel: ChatViewModel
    @ObservedObject public var callViewModel: CallViewModel
    public var onChatClick: (SocialChat) -> Void
    public var onStartAudioCall: (String, String?) -> Void
    public var onStartVideoCall: (String, String?) -> Void
    public var onNewChatClick: () -> Void
    public var onProfileClick: () -> Void
    public var onCameraClick: () -> Void
    public var onAddStoryClick: () -> Void

    @State private var activeHubTab: Int = 0 // 0 = Chats, 1 = Calls
    @State private var searchQuery: String = ""
    @State private var isSearching: Bool = false
    @State private var showSelectContactSheet: Bool = false
    @State private var showCreateGroupSheet: Bool = false
    @State private var viewingStory: StoryStatusItem? = nil

    public init(
        chatViewModel: ChatViewModel,
        callViewModel: CallViewModel,
        onChatClick: @escaping (SocialChat) -> Void,
        onStartAudioCall: @escaping (String, String?) -> Void = { _, _ in },
        onStartVideoCall: @escaping (String, String?) -> Void = { _, _ in },
        onNewChatClick: @escaping () -> Void = {},
        onProfileClick: @escaping () -> Void = {},
        onCameraClick: @escaping () -> Void = {},
        onAddStoryClick: @escaping () -> Void = {}
    ) {
        self.chatViewModel = chatViewModel
        self.callViewModel = callViewModel
        self.onChatClick = onChatClick
        self.onStartAudioCall = onStartAudioCall
        self.onStartVideoCall = onStartVideoCall
        self.onNewChatClick = onNewChatClick
        self.onProfileClick = onProfileClick
        self.onCameraClick = onCameraClick
        self.onAddStoryClick = onAddStoryClick
    }

    private var filteredChats: [SocialChat] {
        chatViewModel.chats.filter { chat in
            let matchesFilter: Bool
            switch chatViewModel.activeFilter {
            case "unread": matchesFilter = chat.unreadCount > 0
            case "direct": matchesFilter = chat.type == "DIRECT" && !chat.isGroup
            case "groups": matchesFilter = chat.isGroup || chat.type == "GROUP"
            case "orders": matchesFilter = chat.type != "DIRECT" && !chat.isGroup
            default: matchesFilter = true
            }

            let matchesSearch = searchQuery.trimmingCharacters(in: .whitespaces).isEmpty ? true :
                (chat.title.localizedCaseInsensitiveContains(searchQuery) ||
                 chat.lastMessage.localizedCaseInsensitiveContains(searchQuery) ||
                 (chat.referenceTag?.localizedCaseInsensitiveContains(searchQuery) ?? false))

            return matchesFilter && matchesSearch
        }
    }

    private var filteredCallLogs: [CallLogItem] {
        if searchQuery.trimmingCharacters(in: .whitespaces).isEmpty {
            return callViewModel.callLogs
        } else {
            return callViewModel.callLogs.filter { log in
                log.contactName.localizedCaseInsensitiveContains(searchQuery) ||
                (log.contactPhone?.contains(searchQuery) ?? false) ||
                (log.subtitle?.localizedCaseInsensitiveContains(searchQuery) ?? false)
            }
        }
    }

    public var body: some View {
        ZStack(alignment: .bottomTrailing) {
            VStack(spacing: 0) {
                // TOP HEADER
                VStack(spacing: 0) {
                    HStack {
                        if !isSearching {
                            HStack(spacing: 6) {
                                Text("Evrry")
                                    .font(.system(size: 22, weight: .black))
                                    .foregroundColor(.evrryPrimaryBlue)
                                Text(activeHubTab == 0 ? "Chats" : "Calls")
                                    .font(.system(size: 22, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                            }

                            Spacer()

                            HStack(spacing: 12) {
                                Button(action: onCameraClick) {
                                    Image(systemName: "camera")
                                        .font(.system(size: 18, weight: .semibold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                }

                                Button(action: { isSearching = true }) {
                                    Image(systemName: "magnifyingglass")
                                        .font(.system(size: 18, weight: .semibold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                }

                                Button(action: onProfileClick) {
                                    Circle()
                                        .fill(Color.evrrySubtleSurface)
                                        .frame(width: 34, height: 34)
                                        .overlay(
                                            Image(systemName: "person.fill")
                                                .font(.system(size: 16))
                                                .foregroundColor(.evrryPrimaryBlue)
                                        )
                                        .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                }
                            }
                        } else {
                            HStack {
                                Button(action: {
                                    isSearching = false
                                    searchQuery = ""
                                }) {
                                    Image(systemName: "chevron.left")
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                }

                                TextField(activeHubTab == 0 ? "Search chats, riders, stores..." : "Search call logs...", text: $searchQuery)
                                    .font(.system(size: 14))
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 8)
                                    .background(Color.evrrySubtleSurface)
                                    .cornerRadius(20)

                                if !searchQuery.isEmpty {
                                    Button(action: { searchQuery = "" }) {
                                        Image(systemName: "xmark.circle.fill")
                                            .foregroundColor(.evrryMutedText)
                                    }
                                }
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)

                    // MINIMAL SEGMENTED PILL: CHATS vs CALLS
                    HStack(spacing: 4) {
                        Button(action: { activeHubTab = 0 }) {
                            HStack(spacing: 6) {
                                Image(systemName: "bubble.left.and.bubble.right.fill")
                                    .font(.system(size: 12))
                                Text("Chats")
                                    .font(.system(size: 13, weight: activeHubTab == 0 ? .bold : .medium))
                                let unreadSum = chatViewModel.chats.reduce(0) { $0 + $1.unreadCount }
                                if unreadSum > 0 {
                                    Text("\(unreadSum)")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(.white)
                                        .padding(.horizontal, 6)
                                        .padding(.vertical, 2)
                                        .background(Color(red: 0.06, green: 0.72, blue: 0.51))
                                        .clipShape(Capsule())
                                }
                            }
                            .foregroundColor(activeHubTab == 0 ? .evrryDarkCharcoal : Color(red: 0.39, green: 0.45, blue: 0.55))
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 7)
                            .background(activeHubTab == 0 ? Color.white : Color.clear)
                            .cornerRadius(10)
                            .shadow(color: activeHubTab == 0 ? Color.black.opacity(0.06) : Color.clear, radius: 2, y: 1)
                        }

                        Button(action: { activeHubTab = 1 }) {
                            HStack(spacing: 6) {
                                Image(systemName: "phone.fill")
                                    .font(.system(size: 12))
                                Text("Calls")
                                    .font(.system(size: 13, weight: activeHubTab == 1 ? .bold : .medium))
                                let missedCount = callViewModel.callLogs.filter { $0.direction == .missed }.count
                                if missedCount > 0 {
                                    Text("\(missedCount)")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(.white)
                                        .padding(.horizontal, 6)
                                        .padding(.vertical, 2)
                                        .background(Color(red: 0.94, green: 0.27, blue: 0.27))
                                        .clipShape(Capsule())
                                }
                            }
                            .foregroundColor(activeHubTab == 1 ? .evrryDarkCharcoal : Color(red: 0.39, green: 0.45, blue: 0.55))
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 7)
                            .background(activeHubTab == 1 ? Color.white : Color.clear)
                            .cornerRadius(10)
                            .shadow(color: activeHubTab == 1 ? Color.black.opacity(0.06) : Color.clear, radius: 2, y: 1)
                        }
                    }
                    .padding(3)
                    .background(Color(red: 0.95, green: 0.96, blue: 0.98))
                    .cornerRadius(12)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 4)

                    if activeHubTab == 0 {
                        // 24-HR STATUS STORIES ROW
                        StatusStoriesView(
                            stories: chatViewModel.stories,
                            onStoryClick: { story in
                                if story.id == "story-self" {
                                    if story.hasUnseenStory && story.mediaUrl != nil {
                                        viewingStory = story
                                    } else {
                                        onAddStoryClick()
                                    }
                                } else {
                                    chatViewModel.markStorySeen(storyId: story.id)
                                    viewingStory = story
                                }
                            },
                            onAddStoryClick: onAddStoryClick
                        )
                            .padding(.vertical, 4)

                        // FILTER CHIPS
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                FilterChipView(
                                    label: "All",
                                    count: chatViewModel.chats.count,
                                    isSelected: chatViewModel.activeFilter == "all",
                                    action: { chatViewModel.setFilter("all") }
                                )
                                FilterChipView(
                                    label: "Unread",
                                    count: chatViewModel.chats.filter { $0.unreadCount > 0 }.count,
                                    isSelected: chatViewModel.activeFilter == "unread",
                                    action: { chatViewModel.setFilter("unread") }
                                )
                                FilterChipView(
                                    label: "Direct (Friends)",
                                    count: chatViewModel.chats.filter { $0.type == "DIRECT" && !$0.isGroup }.count,
                                    isSelected: chatViewModel.activeFilter == "direct",
                                    action: { chatViewModel.setFilter("direct") }
                                )
                                FilterChipView(
                                    label: "Groups",
                                    count: chatViewModel.chats.filter { $0.isGroup || $0.type == "GROUP" }.count,
                                    isSelected: chatViewModel.activeFilter == "groups",
                                    action: { chatViewModel.setFilter("groups") }
                                )
                                FilterChipView(
                                    label: "Orders & Rides",
                                    count: chatViewModel.chats.filter { $0.type != "DIRECT" && !$0.isGroup }.count,
                                    isSelected: chatViewModel.activeFilter == "orders",
                                    action: { chatViewModel.setFilter("orders") }
                                )
                            }
                            .padding(.horizontal, 16)
                            .padding(.vertical, 6)
                        }
                    }

                    Divider()
                        .background(Color.evrrySubtleBorder)
                }
                .background(Color.white)
                .shadow(color: Color.black.opacity(0.03), radius: 3, y: 2)

                // CONTENT LIST: CHATS OR CALLS
                if activeHubTab == 0 {
                    // CHATS
                    if filteredChats.isEmpty {
                        VStack(spacing: 12) {
                            Spacer()
                            Image(systemName: "bubble.left.and.bubble.right")
                                .font(.system(size: 46))
                                .foregroundColor(.evrryMutedText)
                            Text("No conversations found")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Text("Start chatting with friends, delivery riders, or stores")
                                .font(.system(size: 13))
                                .foregroundColor(.evrryMutedText)
                            Spacer()
                        }
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                    } else {
                        List {
                            ForEach(filteredChats) { chat in
                                Button(action: {
                                    chatViewModel.selectChat(chat)
                                    onChatClick(chat)
                                }) {
                                    ConversationRowView(chat: chat)
                                }
                                .listRowInsets(EdgeInsets())
                                .listRowSeparator(.visible)
                            }
                        }
                        .listStyle(.plain)
                    }
                } else {
                    // CALL LOGS
                    if filteredCallLogs.isEmpty {
                        VStack(spacing: 12) {
                            Spacer()
                            Image(systemName: "phone.badge.plus")
                                .font(.system(size: 46))
                                .foregroundColor(.evrryMutedText)
                            Text("No recent calls")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Text("WebRTC voice and video calls with partners and friends appear here")
                                .font(.system(size: 13))
                                .foregroundColor(.evrryMutedText)
                            Spacer()
                        }
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                    } else {
                        List {
                            Section(header: Text("Recent").font(.system(size: 13, weight: .bold)).foregroundColor(.evrryMutedText)) {
                                ForEach(filteredCallLogs) { log in
                                    CallLogRowView(
                                        item: log,
                                        onCallClick: {
                                            if log.callType == .video {
                                                onStartVideoCall(log.contactName, log.contactPhone)
                                            } else {
                                                onStartAudioCall(log.contactName, log.contactPhone)
                                            }
                                        }
                                    )
                                    .listRowInsets(EdgeInsets())
                                    .listRowSeparator(.visible)
                                }
                            }
                        }
                        .listStyle(.plain)
                    }
                }
            }

            // FLOATING ACTION BUTTON (FAB)
            Button(action: {
                if activeHubTab == 0 {
                    showSelectContactSheet = true
                    onNewChatClick()
                } else {
                    onStartAudioCall("Ramesh Thapa", "9841234567")
                }
            }) {
                Circle()
                    .fill(Color.evrryPrimaryBlue)
                    .frame(width: 56, height: 56)
                    .shadow(color: Color.evrryPrimaryBlue.opacity(0.35), radius: 8, x: 0, y: 4)
                    .overlay(
                        Image(systemName: activeHubTab == 0 ? "square.and.pencil" : "phone.arrow.up.right.fill")
                            .font(.system(size: 22, weight: .semibold))
                            .foregroundColor(.white)
                    )
            }
            .padding(.trailing, 20)
            .padding(.bottom, 24)
        }
        .sheet(isPresented: $showSelectContactSheet) {
            SelectContactSheetView(
                contacts: chatViewModel.phoneContacts,
                onDismiss: { showSelectContactSheet = false },
                onSelectContact: { contact in
                    showSelectContactSheet = false
                    let chat = chatViewModel.openOrCreateDirectChatWithContact(contact)
                    onChatClick(chat)
                },
                onDirectPhoneChat: { phone in
                    showSelectContactSheet = false
                    let chat = chatViewModel.openOrCreateChatByPhoneNumber(phone)
                    onChatClick(chat)
                },
                onCreateGroup: {
                    showSelectContactSheet = false
                    showCreateGroupSheet = true
                }
            )
        }
        .sheet(isPresented: $showCreateGroupSheet) {
            CreateGroupSheetView(
                contacts: chatViewModel.phoneContacts,
                onCreateGroup: { title, ids in
                    let newGroup = chatViewModel.createGroup(title: title, description: nil, participantIds: ids)
                    onChatClick(newGroup)
                }
            )
        }
        .fullScreenCover(item: $viewingStory) { story in
            StoryViewerView(
                story: story,
                onDismiss: { viewingStory = nil },
                onDeleteStory: {
                    chatViewModel.deleteMyStory()
                    viewingStory = nil
                },
                onAddAnother: {
                    viewingStory = nil
                    onAddStoryClick()
                },
                onReply: { replyMsg in
                    chatViewModel.sendStoryReply(storyUserId: story.userId, messageText: replyMsg)
                    viewingStory = nil
                }
            )
        }
    }
}

// MARK: - Call Log Row View
struct CallLogRowView: View {
    let item: CallLogItem
    let onCallClick: () -> Void

    var body: some View {
        HStack(spacing: 14) {
            // AVATAR
            Circle()
                .fill(item.isDriverOrMerchant ? Color.evrryPrimaryBlue.opacity(0.12) : Color(red: 0.95, green: 0.96, blue: 0.98))
                .frame(width: 48, height: 48)
                .overlay(
                    Text(String(item.contactName.prefix(2)).uppercased())
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(item.isDriverOrMerchant ? .evrryPrimaryBlue : .evrryDarkCharcoal)
                )
                .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))

            // INFO
            VStack(alignment: .leading, spacing: 3) {
                HStack(spacing: 6) {
                    Text(item.contactName)
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundColor(item.direction == .missed ? Color.red : .evrryDarkCharcoal)
                        .lineLimit(1)

                    if item.isDriverOrMerchant {
                        Text("Partner")
                            .font(.system(size: 9, weight: .bold))
                            .foregroundColor(.evrryPrimaryBlue)
                            .padding(.horizontal, 5)
                            .padding(.vertical, 1)
                            .background(Color.evrryPrimaryBlue.opacity(0.1))
                            .cornerRadius(4)
                    }
                }

                HStack(spacing: 4) {
                    Group {
                        switch item.direction {
                        case .missed:
                            Image(systemName: "phone.arrow.down.left.fill")
                                .foregroundColor(Color.red)
                        case .incoming:
                            Image(systemName: "phone.arrow.down.left.fill")
                                .foregroundColor(Color(red: 0.05, green: 0.58, blue: 0.53))
                        case .outgoing:
                            Image(systemName: "phone.arrow.up.right.fill")
                                .foregroundColor(Color(red: 0.06, green: 0.72, blue: 0.51))
                        }
                    }
                    .font(.system(size: 11))

                    Text(item.subtitle ?? item.timestampFormatted)
                        .font(.system(size: 12))
                        .foregroundColor(.evrryMutedText)
                }
            }

            Spacer()

            // TRAILING 1-TAP CALL BUTTON
            Button(action: onCallClick) {
                Image(systemName: item.callType == .video ? "video.fill" : "phone.fill")
                    .font(.system(size: 18))
                    .foregroundColor(.evrryPrimaryBlue)
                    .frame(width: 44, height: 44)
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
        .contentShape(Rectangle())
    }
}

// MARK: - Status Stories Row
struct StatusStoriesView: View {
    let stories: [StoryStatusItem]
    var onStoryClick: (StoryStatusItem) -> Void = { _ in }
    var onAddStoryClick: () -> Void = {}

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 14) {
                ForEach(stories) { story in
                    let isMe = story.id == "story-self"
                    VStack(spacing: 4) {
                        ZStack(alignment: .bottomTrailing) {
                            Circle()
                                .stroke(story.hasUnseenStory ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: 2)
                                .frame(width: 54, height: 54)
                                .overlay(
                                    Circle()
                                        .fill(Color.evrrySubtleSurface)
                                        .frame(width: 48, height: 48)
                                        .overlay(
                                            Text(String(story.userName.prefix(2)).uppercased())
                                                .font(.system(size: 15, weight: .bold))
                                                .foregroundColor(.evrryPrimaryBlue)
                                        )
                                )

                            if isMe {
                                Button(action: onAddStoryClick) {
                                    Circle()
                                        .fill(Color.evrryPrimaryBlue)
                                        .frame(width: 18, height: 18)
                                        .overlay(
                                            Image(systemName: "plus")
                                                .font(.system(size: 11, weight: .bold))
                                                .foregroundColor(.white)
                                        )
                                        .overlay(Circle().stroke(Color.white, lineWidth: 1.5))
                                }
                            }
                        }

                        Text(isMe ? "My Status" : story.userName)
                            .font(.system(size: 11, weight: .medium))
                            .foregroundColor(.evrryDarkCharcoal)
                            .lineLimit(1)
                            .frame(width: 62)
                    }
                    .contentShape(Rectangle())
                    .onTapGesture {
                        onStoryClick(story)
                    }
                }
            }
            .padding(.horizontal, 16)
        }
    }
}

// MARK: - Filter Chip View
struct FilterChipView: View {
    let label: String
    let count: Int
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 6) {
                Text(label)
                    .font(.system(size: 12, weight: isSelected ? .bold : .medium))
                    .foregroundColor(isSelected ? .white : .evrryDarkCharcoal)

                if count > 0 && !isSelected {
                    Text("\(count)")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                        .padding(.horizontal, 5)
                        .padding(.vertical, 1)
                        .background(Color.white)
                        .clipShape(Capsule())
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(isSelected ? Color.evrryPrimaryBlue : Color.evrrySubtleSurface)
            .cornerRadius(16)
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(isSelected ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: 1)
            )
        }
    }
}

// MARK: - Conversation Row View
struct ConversationRowView: View {
    let chat: SocialChat

    var body: some View {
        HStack(spacing: 14) {
            // AVATAR
            ZStack(alignment: .bottomTrailing) {
                Circle()
                    .fill(avatarBg)
                    .frame(width: 52, height: 52)
                    .overlay(
                        Group {
                            if chat.isGroup {
                                Image(systemName: "person.2.fill")
                                    .font(.system(size: 20))
                                    .foregroundColor(avatarTextColor)
                            } else if chat.type == "RIDE_COORDINATION" {
                                Image(systemName: "bicycle")
                                    .font(.system(size: 22))
                                    .foregroundColor(avatarTextColor)
                            } else if chat.type == "ORDER_SUPPORT" {
                                Image(systemName: "fork.knife")
                                    .font(.system(size: 20))
                                    .foregroundColor(avatarTextColor)
                            } else {
                                Text(String(chat.title.prefix(2)).uppercased())
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(avatarTextColor)
                            }
                        }
                    )
                    .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))

                if chat.isOnline {
                    Circle()
                        .fill(Color(red: 0.13, green: 0.77, blue: 0.37))
                        .frame(width: 14, height: 14)
                        .overlay(Circle().stroke(Color.white, lineWidth: 2))
                }
            }

            // CHAT INFO
            VStack(alignment: .leading, spacing: 3) {
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

                    Spacer()

                    Text(chat.lastMessageTime)
                        .font(.system(size: 11, weight: chat.unreadCount > 0 ? .bold : .regular))
                        .foregroundColor(chat.unreadCount > 0 ? .evrryPrimaryBlue : .evrryMutedText)
                }

                if let tag = chat.referenceTag {
                    Text(tag)
                        .font(.system(size: 10, weight: .semibold))
                        .foregroundColor(.evrryPrimaryBlue)
                        .padding(.horizontal, 5)
                        .padding(.vertical, 2)
                        .background(Color.evrrySubtleSurface)
                        .cornerRadius(4)
                }

                HStack {
                    DeliveryTickView(status: chat.lastMessageStatus)

                    Text(chat.lastMessage)
                        .font(.system(size: 13, weight: chat.unreadCount > 0 ? .semibold : .regular))
                        .foregroundColor(chat.unreadCount > 0 ? .evrryDarkCharcoal : .evrryMutedText)
                        .lineLimit(1)

                    Spacer()

                    if chat.unreadCount > 0 {
                        Text("\(chat.unreadCount)")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(Color.evrryPrimaryBlue)
                            .clipShape(Capsule())
                    }
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .contentShape(Rectangle())
    }

    private var avatarBg: Color {
        if chat.isGroup { return Color(red: 0.94, green: 0.96, blue: 1.0) }
        switch chat.type {
        case "RIDE_COORDINATION": return Color(red: 0.94, green: 0.96, blue: 1.0)
        case "ORDER_SUPPORT": return Color(red: 1.0, green: 0.95, blue: 0.95)
        default: return Color(red: 0.94, green: 0.99, blue: 0.96)
        }
    }

    private var avatarTextColor: Color {
        if chat.isGroup { return .evrryPrimaryBlue }
        switch chat.type {
        case "RIDE_COORDINATION": return .evrryPrimaryBlue
        case "ORDER_SUPPORT": return Color.red
        default: return Color.green
        }
    }
}

// MARK: - Delivery Status Tick View
public struct DeliveryTickView: View {
    public let status: String

    public init(status: String) {
        self.status = status
    }

    public var body: some View {
        switch status.lowercased() {
        case "sending":
            Image(systemName: "clock")
                .font(.system(size: 10))
                .foregroundColor(.evrryMutedText)
        case "sent":
            Image(systemName: "checkmark")
                .font(.system(size: 10, weight: .semibold))
                .foregroundColor(.evrryMutedText)
        case "delivered":
            HStack(spacing: -3) {
                Image(systemName: "checkmark")
                Image(systemName: "checkmark")
            }
            .font(.system(size: 9, weight: .bold))
            .foregroundColor(.evrryMutedText)
        case "read":
            HStack(spacing: -3) {
                Image(systemName: "checkmark")
                Image(systemName: "checkmark")
            }
            .font(.system(size: 9, weight: .bold))
            .foregroundColor(.evrryPrimaryBlue)
        default:
            EmptyView()
        }
    }
}

// MARK: - WhatsApp-Grade Select Contact Sheet View
public struct SelectContactSheetView: View {
    public let contacts: [PhoneContactItem]
    public let onDismiss: () -> Void
    public let onSelectContact: (PhoneContactItem) -> Void
    public let onDirectPhoneChat: (String) -> Void
    public var onCreateGroup: () -> Void = {}

    public init(
        contacts: [PhoneContactItem],
        onDismiss: @escaping () -> Void,
        onSelectContact: @escaping (PhoneContactItem) -> Void,
        onDirectPhoneChat: @escaping (String) -> Void,
        onCreateGroup: @escaping () -> Void = {}
    ) {
        self.contacts = contacts
        self.onDismiss = onDismiss
        self.onSelectContact = onSelectContact
        self.onDirectPhoneChat = onDirectPhoneChat
        self.onCreateGroup = onCreateGroup
    }

    @State private var searchQuery: String = ""

    private var registeredContacts: [PhoneContactItem] {
        contacts.filter { $0.isRegisteredOnEvrry }.filter {
            if searchQuery.trimmingCharacters(in: .whitespaces).isEmpty { return true }
            return $0.name.localizedCaseInsensitiveContains(searchQuery) ||
                $0.phone.contains(searchQuery) ||
                ($0.handle?.localizedCaseInsensitiveContains(searchQuery) ?? false)
        }
    }

    private var inviteContacts: [PhoneContactItem] {
        contacts.filter { !$0.isRegisteredOnEvrry }.filter {
            if searchQuery.trimmingCharacters(in: .whitespaces).isEmpty { return true }
            return $0.name.localizedCaseInsensitiveContains(searchQuery) ||
                $0.phone.contains(searchQuery)
        }
    }

    private var isPhoneNumberQuery: Bool {
        let digits = searchQuery.filter { $0.isNumber }
        return digits.count >= 7
    }

    public var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // SEARCH BAR
                HStack(spacing: 8) {
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(.evrryMutedText)
                    TextField("Search name or phone number (+977...)", text: $searchQuery)
                        .font(.system(size: 14))
                    if !searchQuery.isEmpty {
                        Button(action: { searchQuery = "" }) {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundColor(.evrryMutedText)
                        }
                    }
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
                .background(Color(UIColor.secondarySystemBackground))
                .cornerRadius(10)
                .padding(.horizontal, 16)
                .padding(.vertical, 10)

                List {
                    // DYNAMIC DIRECT PHONE ACTION
                    if isPhoneNumberQuery {
                        Section {
                            Button(action: { onDirectPhoneChat(searchQuery.trimmingCharacters(in: .whitespaces)) }) {
                                HStack(spacing: 14) {
                                    Circle()
                                        .fill(Color.evrryPrimaryBlue)
                                        .frame(width: 42, height: 42)
                                        .overlay(
                                            Image(systemName: "phone.fill")
                                                .font(.system(size: 16))
                                                .foregroundColor(.white)
                                        )

                                    VStack(alignment: .leading, spacing: 2) {
                                        Text("Message \(searchQuery)")
                                            .font(.system(size: 15, weight: .bold))
                                            .foregroundColor(.evrryPrimaryBlue)
                                        Text("Start end-to-end encrypted direct chat")
                                            .font(.system(size: 12))
                                            .foregroundColor(.evrryMutedText)
                                    }
                                }
                                .padding(.vertical, 4)
                            }
                        }
                    }

                    // QUICK ACTIONS (WhatsApp Style)
                    Section {
                        Button(action: onCreateGroup) {
                            HStack(spacing: 14) {
                                Circle()
                                    .fill(Color.evrryPrimaryBlue)
                                    .frame(width: 42, height: 42)
                                    .overlay(Image(systemName: "person.3.fill").font(.system(size: 15)).foregroundColor(.white))
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("New group").font(.system(size: 15, weight: .bold)).foregroundColor(.evrryDarkCharcoal)
                                    Text("Create family, ride or order group").font(.system(size: 12)).foregroundColor(.evrryMutedText)
                                }
                            }
                            .padding(.vertical, 4)
                        }

                        HStack(spacing: 14) {
                            Circle()
                                .fill(Color.evrryPrimaryBlue)
                                .frame(width: 42, height: 42)
                                .overlay(Image(systemName: "person.badge.plus").font(.system(size: 16)).foregroundColor(.white))
                            VStack(alignment: .leading, spacing: 2) {
                                Text("New contact").font(.system(size: 15, weight: .bold)).foregroundColor(.evrryDarkCharcoal)
                                Text("Add to phonebook").font(.system(size: 12)).foregroundColor(.evrryMutedText)
                            }
                        }
                        .padding(.vertical, 4)

                        HStack(spacing: 14) {
                            Circle()
                                .fill(Color.evrryPrimaryBlue)
                                .frame(width: 42, height: 42)
                                .overlay(Image(systemName: "qrcode").font(.system(size: 16)).foregroundColor(.white))
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Scan QR Code").font(.system(size: 15, weight: .bold)).foregroundColor(.evrryDarkCharcoal)
                                Text("Scan friend's personal QR to add instantly").font(.system(size: 12)).foregroundColor(.evrryMutedText)
                            }
                        }
                        .padding(.vertical, 4)
                    }

                    // CONTACTS ON EVRRY
                    if !registeredContacts.isEmpty {
                        Section(header: Text("CONTACTS ON EVRRY").font(.system(size: 12, weight: .bold)).foregroundColor(.evrryMutedText)) {
                            ForEach(registeredContacts) { contact in
                                Button(action: { onSelectContact(contact) }) {
                                    HStack(spacing: 14) {
                                        Circle()
                                            .fill(Color.evrryPrimaryBlue.opacity(0.12))
                                            .frame(width: 46, height: 46)
                                            .overlay(
                                                Text(String(contact.name.prefix(2)).uppercased())
                                                    .font(.system(size: 16, weight: .bold))
                                                    .foregroundColor(.evrryPrimaryBlue)
                                            )

                                        VStack(alignment: .leading, spacing: 3) {
                                            HStack(spacing: 6) {
                                                Text(contact.name)
                                                    .font(.system(size: 15, weight: .bold))
                                                    .foregroundColor(.evrryDarkCharcoal)
                                                if let handle = contact.handle {
                                                    Text("@\(handle)")
                                                        .font(.system(size: 12, weight: .medium))
                                                        .foregroundColor(.evrryPrimaryBlue)
                                                }
                                            }

                                            Text(contact.statusBio)
                                                .font(.system(size: 12))
                                                .foregroundColor(.evrryMutedText)
                                                .lineLimit(1)
                                        }

                                        Spacer()

                                        Text(contact.phone)
                                            .font(.system(size: 12, weight: .medium))
                                            .foregroundColor(Color(UIColor.secondaryLabel))
                                    }
                                    .padding(.vertical, 4)
                                }
                            }
                        }
                    }

                    // INVITE TO EVRRY
                    if !inviteContacts.isEmpty {
                        Section(header: Text("INVITE TO EVRRY").font(.system(size: 12, weight: .bold)).foregroundColor(.evrryMutedText)) {
                            ForEach(inviteContacts) { contact in
                                InviteContactRowView(contact: contact)
                            }
                        }
                    }
                }
                .listStyle(.plain)
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    VStack(spacing: 2) {
                        Text("Select contact")
                            .font(.system(size: 17, weight: .bold))
                        Text("\(contacts.filter { $0.isRegisteredOnEvrry }.count) contacts on Evrry")
                            .font(.system(size: 11))
                            .foregroundColor(.evrryMutedText)
                    }
                }
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: onDismiss) {
                        Image(systemName: "xmark")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.evrryDarkCharcoal)
                            .frame(width: 44, height: 44)
                    }
                }
            }
        }
    }
}

// MARK: - Invite Contact Row View
private struct InviteContactRowView: View {
    let contact: PhoneContactItem
    @State private var isInvited: Bool = false

    var body: some View {
        HStack(spacing: 14) {
            Circle()
                .fill(Color(UIColor.tertiarySystemFill))
                .frame(width: 42, height: 42)
                .overlay(
                    Text(String(contact.name.prefix(2)).uppercased())
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                )

            VStack(alignment: .leading, spacing: 2) {
                Text(contact.name)
                    .font(.system(size: 15, weight: .bold))
                    .foregroundColor(.evrryDarkCharcoal)
                Text(contact.phone)
                    .font(.system(size: 12))
                    .foregroundColor(.evrryMutedText)
            }

            Spacer()

            Button(action: { isInvited = true }) {
                Text(isInvited ? "INVITED" : "INVITE")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(isInvited ? .evrryGreenSuccess : .evrryPrimaryBlue)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 6)
                    .overlay(
                        Capsule().stroke(isInvited ? Color.evrryGreenSuccess : Color.evrryPrimaryBlue, lineWidth: 1)
                    )
            }
        }
        .padding(.vertical, 4)
    }
}

// MARK: - Create Group Sheet View
struct CreateGroupSheetView: View {
    let contacts: [PhoneContactItem]
    var onCreateGroup: (String, [String]) -> Void
    @Environment(\.presentationMode) var presentationMode
    @State private var groupTitle: String = ""
    @State private var selectedIds: Set<String> = []

    var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                TextField("Group Subject / Name", text: $groupTitle)
                    .font(.system(size: 15))
                    .padding(12)
                    .background(Color.evrrySubtleSurface)
                    .cornerRadius(10)
                    .padding(.horizontal, 16)
                    .padding(.top, 10)

                VStack(alignment: .leading, spacing: 6) {
                    Text("Select participants (\(selectedIds.count) selected):")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                        .padding(.horizontal, 16)

                    List {
                        ForEach(contacts.filter { $0.isRegisteredOnEvrry }) { contact in
                            Button(action: {
                                if selectedIds.contains(contact.id) {
                                    selectedIds.remove(contact.id)
                                } else {
                                    selectedIds.insert(contact.id)
                                }
                            }) {
                                HStack(spacing: 12) {
                                    Circle()
                                        .fill(Color.evrryPrimaryBlue.opacity(0.12))
                                        .frame(width: 36, height: 36)
                                        .overlay(
                                            Text(String(contact.name.prefix(2)).uppercased())
                                                .font(.system(size: 13, weight: .bold))
                                                .foregroundColor(.evrryPrimaryBlue)
                                        )

                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(contact.name)
                                            .font(.system(size: 14, weight: .semibold))
                                            .foregroundColor(.evrryDarkCharcoal)
                                        Text(contact.statusBio)
                                            .font(.system(size: 11))
                                            .foregroundColor(.evrryMutedText)
                                    }

                                    Spacer()

                                    Image(systemName: selectedIds.contains(contact.id) ? "checkmark.circle.fill" : "circle")
                                        .foregroundColor(selectedIds.contains(contact.id) ? .evrryPrimaryBlue : .evrrySubtleBorder)
                                }
                                .padding(.vertical, 4)
                            }
                        }
                    }
                    .listStyle(PlainListStyle())
                }

                Spacer()

                Button(action: {
                    if !groupTitle.trimmingCharacters(in: .whitespaces).isEmpty {
                        onCreateGroup(groupTitle.trimmingCharacters(in: .whitespaces), Array(selectedIds))
                        presentationMode.wrappedValue.dismiss()
                    }
                }) {
                    Text("Create Group")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 14)
                        .background(groupTitle.trimmingCharacters(in: .whitespaces).isEmpty ? Color.gray.opacity(0.5) : Color.evrryPrimaryBlue)
                        .cornerRadius(12)
                }
                .disabled(groupTitle.trimmingCharacters(in: .whitespaces).isEmpty)
                .padding(.horizontal, 16)
                .padding(.bottom, 16)
            }
            .navigationTitle("New Group")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") { presentationMode.wrappedValue.dismiss() }
                }
            }
        }
    }
}

