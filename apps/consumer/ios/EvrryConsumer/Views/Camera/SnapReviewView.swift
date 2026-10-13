import SwiftUI

public struct SnapReviewView: View {
    public let dataUrl: String
    public let filterName: String
    public let forStory: Bool
    @ObservedObject public var cameraViewModel: CameraViewModel
    @ObservedObject public var chatViewModel: ChatViewModel
    public var onClose: () -> Void
    public var onNavigateToChat: ((String) -> Void)? = nil

    @State private var caption: String = ""
    @State private var isEditingCaption: Bool = false
    @State private var showSendSheet: Bool = false
    @State private var confirmationMessage: String? = nil

    public init(
        dataUrl: String,
        filterName: String,
        forStory: Bool = false,
        cameraViewModel: CameraViewModel,
        chatViewModel: ChatViewModel,
        onClose: @escaping () -> Void,
        onNavigateToChat: ((String) -> Void)? = nil
    ) {
        self.dataUrl = dataUrl
        self.filterName = filterName
        self.forStory = forStory
        self.cameraViewModel = cameraViewModel
        self.chatViewModel = chatViewModel
        self.onClose = onClose
        self.onNavigateToChat = onNavigateToChat
    }

    public var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()

            // Photo Preview
            AsyncImage(url: URL(string: dataUrl)) { phase in
                if let img = phase.image {
                    img.resizable().aspectRatio(contentMode: .fill)
                } else {
                    ProgressView().tint(.white)
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .clipped()
            .ignoresSafeArea()

            // Top Bar Overlay
            topBar

            // Caption Overlay
            captionOverlay

            // Bottom Actions Bar
            bottomActionsBar

            // Animated Confirmation Toast
            if let confirmation = confirmationMessage {
                VStack {
                    Spacer()
                    Text(confirmation)
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.black)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 12)
                        .background(Color.white)
                        .clipShape(Capsule())
                        .shadow(color: Color.black.opacity(0.4), radius: 10, y: 4)
                        .padding(.bottom, 120)
                }
                .transition(.scale.combined(with: .opacity))
            }
        }
        .sheet(isPresented: $showSendSheet) {
            sendToFriendSheet
        }
    }

    // MARK: - Top Bar
    private var topBar: some View {
        VStack {
            HStack {
                Button(action: onClose) {
                    Image(systemName: "xmark")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.white)
                        .frame(width: 40, height: 40)
                        .background(Color.black.opacity(0.55))
                        .clipShape(Circle())
                }

                Spacer()

                HStack(spacing: 10) {
                    // Caption button
                    Button(action: {
                        withAnimation { isEditingCaption.toggle() }
                    }) {
                        Image(systemName: "textformat")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.white)
                            .frame(width: 40, height: 40)
                            .background(Color.black.opacity(0.55))
                            .clipShape(Circle())
                    }

                    // Direct Save to My Locker (Private Vault)
                    Button(action: {
                        let snap = cameraViewModel.addSnap(
                            imageUrl: dataUrl,
                            filterName: filterName,
                            location: "Personal Vault",
                            caption: caption
                        )
                        cameraViewModel.moveToLocker(id: snap.id, fromTab: .snaps)
                        flashAndClose(msg: "Saved to My Locker 🔒")
                    }) {
                        Image(systemName: "lock.fill")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.yellow)
                            .frame(width: 40, height: 40)
                            .background(Color.yellow.opacity(0.25))
                            .clipShape(Circle())
                            .overlay(
                                Circle().stroke(Color.yellow.opacity(0.6), lineWidth: 1)
                            )
                    }
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 50)

            Spacer()
        }
    }

    // MARK: - Caption Overlay
    private var captionOverlay: some View {
        VStack {
            Spacer()

            if isEditingCaption {
                VStack(spacing: 10) {
                    TextField("Add a caption...", text: $caption)
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundColor(.white)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 12)
                        .background(Color.black.opacity(0.75))
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .overlay(
                            RoundedRectangle(cornerRadius: 16)
                                .stroke(Color.white.opacity(0.2), lineWidth: 1)
                        )

                    Button("Done") {
                        withAnimation { isEditingCaption = false }
                    }
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(.white)
                    .padding(.horizontal, 20)
                    .padding(.vertical, 6)
                    .background(Color.white.opacity(0.2))
                    .clipShape(Capsule())
                }
                .padding(.horizontal, 30)
                .padding(.bottom, 140)
            } else if !caption.isEmpty {
                Text(caption)
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(.white)
                    .padding(.horizontal, 20)
                    .padding(.vertical, 10)
                    .background(Color.black.opacity(0.65))
                    .clipShape(RoundedRectangle(cornerRadius: 18))
                    .overlay(
                        RoundedRectangle(cornerRadius: 18)
                            .stroke(Color.white.opacity(0.15), lineWidth: 1)
                    )
                    .onTapGesture {
                        withAnimation { isEditingCaption = true }
                    }
                    .padding(.bottom, 120)
            } else {
                Text("Tap to add caption...")
                    .font(.system(size: 12))
                    .foregroundColor(.white.opacity(0.7))
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(Color.black.opacity(0.4))
                    .clipShape(Capsule())
                    .onTapGesture {
                        withAnimation { isEditingCaption = true }
                    }
                    .padding(.bottom, 120)
            }
        }
    }

    // MARK: - Bottom Actions Bar
    private var bottomActionsBar: some View {
        VStack {
            Spacer()

            HStack(spacing: 24) {
                if forStory {
                    Button(action: {
                        chatViewModel.postStory(mediaUrl: dataUrl, caption: caption)
                        flashAndClose(msg: "Posted to your Story 📖")
                    }) {
                        HStack(spacing: 8) {
                            Image(systemName: "book.fill")
                            Text("Post to Story")
                        }
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.white)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 14)
                        .background(Color.evrryPrimaryBlue)
                        .clipShape(Capsule())
                    }
                } else {
                    // Save to Memories Action
                    Button(action: {
                        cameraViewModel.addSnap(
                            imageUrl: dataUrl,
                            filterName: filterName,
                            location: "Kathmandu, Nepal",
                            caption: caption
                        )
                        flashAndClose(msg: "Saved to Memories ✨")
                    }) {
                        VStack(spacing: 4) {
                            ZStack {
                                Circle()
                                    .fill(Color.white.opacity(0.15))
                                    .frame(width: 46, height: 46)
                                Image(systemName: "square.and.arrow.down")
                                    .font(.system(size: 18))
                                    .foregroundColor(.white)
                            }
                            Text("Save")
                                .font(.system(size: 11, weight: .medium))
                                .foregroundColor(.white.opacity(0.85))
                        }
                    }

                    // Post to Story Action
                    Button(action: {
                        chatViewModel.postStory(mediaUrl: dataUrl, caption: caption)
                        flashAndClose(msg: "Posted to your Story 📖")
                    }) {
                        VStack(spacing: 4) {
                            ZStack {
                                Circle()
                                    .fill(Color.white.opacity(0.15))
                                    .frame(width: 46, height: 46)
                                Image(systemName: "book.fill")
                                    .font(.system(size: 18))
                                    .foregroundColor(.white)
                            }
                            Text("Story")
                                .font(.system(size: 11, weight: .medium))
                                .foregroundColor(.white.opacity(0.85))
                        }
                    }

                    // Send To Friend Action
                    Button(action: { showSendSheet = true }) {
                        HStack(spacing: 6) {
                            Text("Send To")
                            Image(systemName: "paperplane.fill")
                        }
                        .font(.system(size: 13, weight: .bold))
                        .foregroundColor(.white)
                        .padding(.horizontal, 20)
                        .padding(.vertical, 12)
                        .background(Color.evrryPrimaryBlue)
                        .clipShape(Capsule())
                    }
                }
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 16)
            .background(Color.black.opacity(0.92))
            .overlay(
                Rectangle().frame(height: 1).foregroundColor(Color.white.opacity(0.1)),
                alignment: .top
            )
        }
        .ignoresSafeArea(.container, edges: .bottom)
    }

    // MARK: - Send to Friend Sheet
    private var sendToFriendSheet: some View {
        NavigationView {
            List {
                Section(header: Text("Share via WhatsApp-grade E2EE Chat")) {
                    ForEach(chatViewModel.chats) { chat in
                        Button(action: {
                            chatViewModel.sendImage(chatId: chat.id, imageUrl: dataUrl, caption: caption)
                            showSendSheet = false
                            flashAndClose(msg: "Sent to \(chat.title)! 🚀")
                            onNavigateToChat?(chat.id)
                        }) {
                            HStack(spacing: 12) {
                                ZStack {
                                    Circle()
                                        .fill(Color.evrryPrimaryBlue.opacity(0.2))
                                        .frame(width: 42, height: 42)
                                    Text(String(chat.title.prefix(1)))
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(.evrryPrimaryBlue)
                                }

                                VStack(alignment: .leading, spacing: 3) {
                                    Text(chat.title)
                                        .font(.system(size: 14, weight: .bold))
                                        .foregroundColor(.primary)
                                    Text(chat.participantPhone)
                                        .font(.system(size: 12))
                                        .foregroundColor(.secondary)
                                }

                                Spacer()

                                Image(systemName: "paperplane.fill")
                                    .foregroundColor(.evrryPrimaryBlue)
                                    .font(.system(size: 14))
                            }
                            .padding(.vertical, 4)
                        }
                    }
                }
            }
            .navigationTitle("Send To")
            .navigationBarItems(trailing: Button("Cancel") { showSendSheet = false })
        }
    }

    private func flashAndClose(msg: String) {
        confirmationMessage = msg
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.85) {
            confirmationMessage = nil
            onClose()
        }
    }
}
