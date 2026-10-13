import SwiftUI

/**
 * Fullscreen Interactive Story & Status Viewer for iOS
 * WhatsApp 24-hr Status & Snapchat My Story Synchronized Experience
 * Features segmented progress bar, press-to-pause, caption pill, quick emoji reactions,
 * direct chat reply integration, and author status management.
 */
public struct StoryViewerView: View {
    let story: StoryStatusItem
    let onDismiss: () -> Void
    var onDeleteStory: () -> Void = {}
    var onAddAnother: () -> Void = {}
    var onReply: (String) -> Void = { _ in }

    @State private var progress: CGFloat = 0.0
    @State private var isPaused: Bool = false
    @State private var replyText: String = ""
    @State private var showDeleteConfirm: Bool = false

    private let timer = Timer.publish(every: 0.05, on: .main, in: .common).autoconnect()
    private let durationSeconds: Double = 5.0

    private var isMyStatus: Bool {
        story.id == "story-self"
    }

    public init(
        story: StoryStatusItem,
        onDismiss: @escaping () -> Void,
        onDeleteStory: @escaping () -> Void = {},
        onAddAnother: @escaping () -> Void = {},
        onReply: @escaping (String) -> Void = { _ in }
    ) {
        self.story = story
        self.onDismiss = onDismiss
        self.onDeleteStory = onDeleteStory
        self.onAddAnother = onAddAnother
        self.onReply = onReply
    }

    public var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()

            // 1. Background Media View
            mediaLayer
                .ignoresSafeArea()

            // 2. Press to Pause & Tap left/right gestures
            HStack(spacing: 0) {
                // Left 35%: rewind progress
                Color.clear
                    .contentShape(Rectangle())
                    .onTapGesture {
                        progress = 0.0
                    }
                // Right 65%: advance to next / dismiss
                Color.clear
                    .contentShape(Rectangle())
                    .onTapGesture {
                        onDismiss()
                    }
            }
            .simultaneousGesture(
                DragGesture(minimumDistance: 0)
                    .onChanged { _ in isPaused = true }
                    .onEnded { _ in isPaused = false }
            )

            // 3. Top Vignette Gradient
            VStack {
                LinearGradient(
                    colors: [Color.black.opacity(0.8), Color.clear],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: 140)
                Spacer()
            }
            .ignoresSafeArea()

            // 4. Top Header & Progress Bar
            VStack(spacing: 12) {
                // Top Segmented Progress Bar
                GeometryReader { geo in
                    ZStack(alignment: .leading) {
                        Capsule()
                            .fill(Color.white.opacity(0.35))
                            .frame(height: 3.5)

                        Capsule()
                            .fill(Color.white)
                            .frame(width: max(0, min(geo.size.width * progress, geo.size.width)), height: 3.5)
                    }
                }
                .frame(height: 3.5)
                .padding(.horizontal, 16)
                .padding(.top, 8)

                // Author Header Row
                HStack(spacing: 10) {
                    // Author Avatar Initials
                    ZStack {
                        Circle()
                            .fill(Color.evrryPrimaryBlue)
                            .frame(width: 38, height: 38)
                            .overlay(Circle().stroke(Color.white, lineWidth: 1.5))

                        Text(String(story.userName.prefix(2)).uppercased())
                            .font(.system(size: 13, weight: .bold))
                            .foregroundColor(.white)
                    }

                    VStack(alignment: .leading, spacing: 2) {
                        Text(isMyStatus ? "My Status" : story.userName)
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.white)

                        Text(story.lastUpdateText)
                            .font(.system(size: 11, weight: .regular))
                            .foregroundColor(.white.opacity(0.8))
                    }

                    Spacer()

                    if isMyStatus {
                        // Delete own story button
                        Button(action: { showDeleteConfirm = true }) {
                            Image(systemName: "trash.fill")
                                .font(.system(size: 15))
                                .foregroundColor(Color(hex: "#FDA4AF"))
                                .frame(width: 36, height: 36)
                                .background(Color.black.opacity(0.45))
                                .clipShape(Circle())
                        }

                        // Add new story button
                        Button(action: onAddAnother) {
                            Image(systemName: "plus")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.white)
                                .frame(width: 36, height: 36)
                                .background(Color.black.opacity(0.45))
                                .clipShape(Circle())
                        }
                    }

                    // Close Button
                    Button(action: onDismiss) {
                        Image(systemName: "xmark")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.white)
                            .frame(width: 36, height: 36)
                            .background(Color.black.opacity(0.45))
                            .clipShape(Circle())
                    }
                }
                .padding(.horizontal, 16)

                Spacer()
            }

            // 5. Bottom Vignette & Interaction Bar
            VStack {
                Spacer()

                LinearGradient(
                    colors: [Color.clear, Color.black.opacity(0.85)],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: 180)
                .overlay(
                    VStack(spacing: 12) {
                        Spacer()

                        // Caption Pill
                        if let caption = story.caption, !caption.isEmpty {
                            Text(caption)
                                .font(.system(size: 14, weight: .medium))
                                .foregroundColor(.white)
                                .multilineTextAlignment(.center)
                                .padding(.horizontal, 18)
                                .padding(.vertical, 8)
                                .background(Color.black.opacity(0.65))
                                .clipShape(Capsule())
                                .overlay(Capsule().stroke(Color.white.opacity(0.2), lineWidth: 1))
                        }

                        if isMyStatus {
                            // My Story Status Footer (14 views counter + Add button)
                            HStack {
                                HStack(spacing: 6) {
                                    Image(systemName: "eye.fill")
                                        .font(.system(size: 13))
                                    Text("14 views")
                                        .font(.system(size: 12, weight: .medium))
                                }
                                .foregroundColor(.white)
                                .padding(.horizontal, 14)
                                .padding(.vertical, 8)
                                .background(Color.white.opacity(0.18))
                                .clipShape(Capsule())

                                Spacer()

                                Button(action: onAddAnother) {
                                    HStack(spacing: 6) {
                                        Image(systemName: "plus")
                                            .font(.system(size: 13, weight: .bold))
                                        Text("Add Status")
                                            .font(.system(size: 13, weight: .bold))
                                    }
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 16)
                                    .padding(.vertical, 8)
                                    .background(Color.evrryPrimaryBlue)
                                    .clipShape(Capsule())
                                }
                            }
                            .padding(.horizontal, 16)
                        } else {
                            // Contact Story Quick Reactions Bar
                            HStack(spacing: 14) {
                                ForEach(["❤️", "😂", "😮", "😢", "🙏", "👏"], id: \.self) { emoji in
                                    Button(action: {
                                        onReply(emoji)
                                        onDismiss()
                                    }) {
                                        Text(emoji)
                                            .font(.system(size: 22))
                                            .frame(width: 42, height: 42)
                                            .background(Color.white.opacity(0.18))
                                            .clipShape(Circle())
                                    }
                                }
                            }

                            // Direct Chat Reply Input
                            HStack(spacing: 10) {
                                TextField("Reply to \(story.userName.components(separatedBy: " ").first ?? story.userName)...", text: $replyText)
                                    .font(.system(size: 14))
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 16)
                                    .padding(.vertical, 10)
                                    .background(Color.white.opacity(0.15))
                                    .clipShape(Capsule())
                                    .overlay(Capsule().stroke(Color.white.opacity(0.2), lineWidth: 1))

                                if !replyText.trimmingCharacters(in: .whitespaces).isEmpty {
                                    Button(action: {
                                        onReply(replyText)
                                        replyText = ""
                                        onDismiss()
                                    }) {
                                        Image(systemName: "paperplane.fill")
                                            .font(.system(size: 14, weight: .bold))
                                            .foregroundColor(.white)
                                            .frame(width: 38, height: 38)
                                            .background(Color.evrryPrimaryBlue)
                                            .clipShape(Circle())
                                    }
                                }
                            }
                            .padding(.horizontal, 16)
                        }
                    }
                    .padding(.bottom, 24)
                )
            }
            .ignoresSafeArea()
        }
        .onReceive(timer) { _ in
            guard !isPaused && !showDeleteConfirm else { return }
            let step = 0.05 / durationSeconds
            if progress < 1.0 {
                progress += CGFloat(step)
            } else {
                onDismiss()
            }
        }
        .alert(isPresented: $showDeleteConfirm) {
            Alert(
                title: Text("Delete this status update?"),
                message: Text("It will be deleted for everyone who hasn't viewed it yet."),
                primaryButton: .destructive(Text("Delete")) {
                    onDeleteStory()
                    onDismiss()
                },
                secondaryButton: .cancel()
            )
        }
    }

    // MARK: - Media Renderer
    @ViewBuilder
    private var mediaLayer: some View {
        if let mediaUrl = story.mediaUrl, !mediaUrl.isEmpty {
            if mediaUrl.hasPrefix("http") {
                AsyncImage(url: URL(string: mediaUrl)) { phase in
                    switch phase {
                    case .success(let image):
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                    case .failure:
                        fallbackGradientCanvas
                    case .empty:
                        ZStack {
                            Color.black
                            ProgressView()
                                .tint(.white)
                        }
                    @unknown default:
                        fallbackGradientCanvas
                    }
                }
            } else if mediaUrl.hasPrefix("data:image"),
                      let commaIdx = mediaUrl.firstIndex(of: ","),
                      let data = Data(base64Encoded: String(mediaUrl[mediaUrl.index(after: commaIdx)...])) {
                #if canImport(UIKit)
                if let uiImage = UIImage(data: data) {
                    Image(uiImage: uiImage)
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                } else {
                    fallbackGradientCanvas
                }
                #else
                fallbackGradientCanvas
                #endif
            } else {
                fallbackGradientCanvas
            }
        } else {
            fallbackGradientCanvas
        }
    }

    private var fallbackGradientCanvas: some View {
        LinearGradient(
            colors: [Color(hex: "#0F172A"), Color(hex: "#1E293B"), Color(hex: "#090D16")],
            startPoint: .top,
            endPoint: .bottom
        )
        .overlay(
            VStack(spacing: 16) {
                ZStack {
                    Circle()
                        .fill(Color.evrryPrimaryBlue.opacity(0.2))
                        .frame(width: 90, height: 90)
                        .overlay(Circle().stroke(Color.evrryPrimaryBlue, lineWidth: 2))

                    Text(String(story.userName.prefix(2)).uppercased())
                        .font(.system(size: 32, weight: .bold))
                        .foregroundColor(.white)
                }

                Text(story.caption ?? "Shared a moment on Evrry ✨")
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundColor(.white)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 32)
            }
        )
    }
}
