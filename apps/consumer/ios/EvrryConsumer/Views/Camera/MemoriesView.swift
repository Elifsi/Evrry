import SwiftUI

public struct MemoriesView: View {
    @ObservedObject public var cameraViewModel: CameraViewModel
    @ObservedObject public var chatViewModel: ChatViewModel
    public var onBack: () -> Void

    @State private var activeTab: MemoryTab = .snaps
    @State private var selectedSnap: SnapItem? = nil
    @State private var showSendToFriendSheet: Bool = false
    @State private var toastMessage: String? = nil

    // PIN Keypad State for My Locker
    @State private var pinInput: String = ""
    @State private var pinError: Bool = false

    private let columns = [
        GridItem(.flexible(), spacing: 12),
        GridItem(.flexible(), spacing: 12)
    ]

    public init(
        cameraViewModel: CameraViewModel,
        chatViewModel: ChatViewModel,
        onBack: @escaping () -> Void
    ) {
        self.cameraViewModel = cameraViewModel
        self.chatViewModel = chatViewModel
        self.onBack = onBack
    }

    public var body: some View {
        ZStack {
            Color(red: 0.05, green: 0.06, blue: 0.09).ignoresSafeArea()

            VStack(spacing: 0) {
                // Top Header Bar
                headerBar

                // 3-Tab Segment Selector (Snaps, Camera Roll, My Locker)
                tabSelectorBar

                // Tab Content
                Group {
                    switch activeTab {
                    case .snaps:
                        snapsGridView
                    case .cameraRoll:
                        cameraRollGridView
                    case .myLocker:
                        myLockerView
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }

            // Snap Detail Fullscreen Modal
            if let snap = selectedSnap {
                snapDetailModal(snap: snap)
                    .transition(.opacity)
            }

            // Floating Toast Notification
            if let toast = toastMessage {
                VStack {
                    Spacer()
                    Text(toast)
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.black)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 12)
                        .background(Color.white)
                        .clipShape(Capsule())
                        .shadow(color: Color.black.opacity(0.4), radius: 10, y: 4)
                        .padding(.bottom, 40)
                }
                .transition(.scale.combined(with: .opacity))
            }
        }
        .sheet(isPresented: $showSendToFriendSheet) {
            sendToFriendSheet
        }
    }

    // MARK: - Header Bar
    private var headerBar: some View {
        HStack {
            Button(action: onBack) {
                Image(systemName: "chevron.left")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(.white)
                    .frame(width: 40, height: 40)
                    .background(Color.white.opacity(0.1))
                    .clipShape(Circle())
            }

            Spacer()

            Text("Memories")
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(.white)

            Spacer()

            // Quick Add / Import Button
            Button(action: {
                let randomSnaps = [
                    "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=800&auto=format&fit=crop&q=80"
                ]
                let randomUrl = randomSnaps.randomElement()!
                if activeTab == .cameraRoll {
                    cameraViewModel.addToCameraRoll(imageUrl: randomUrl, caption: "Imported from Library")
                    triggerToast("Imported photo to Camera Roll 📸")
                } else {
                    cameraViewModel.addSnap(imageUrl: randomUrl, filterName: "Imported", location: "Kathmandu Valley", caption: "Added to Memories")
                    triggerToast("Added to Snaps ✨")
                }
            }) {
                Image(systemName: "plus")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(.white)
                    .frame(width: 40, height: 40)
                    .background(Color.white.opacity(0.1))
                    .clipShape(Circle())
            }
        }
        .padding(.horizontal, 16)
        .padding(.top, 50)
        .padding(.bottom, 12)
    }

    // MARK: - Tab Selector
    private var tabSelectorBar: some View {
        HStack(spacing: 8) {
            ForEach(MemoryTab.allCases) { tab in
                let isSelected = activeTab == tab
                Button(action: {
                    withAnimation(.easeInOut(duration: 0.2)) {
                        activeTab = tab
                        if tab != .myLocker {
                            pinInput = ""
                            pinError = false
                        }
                    }
                }) {
                    HStack(spacing: 6) {
                        if tab == .myLocker {
                            Image(systemName: cameraViewModel.isLockerUnlocked ? "lock.open.fill" : "lock.fill")
                                .font(.system(size: 12))
                                .foregroundColor(isSelected ? .yellow : .white.opacity(0.6))
                        }
                        Text(tab.title)
                            .font(.system(size: 13, weight: isSelected ? .bold : .medium))
                            .foregroundColor(isSelected ? .white : .white.opacity(0.6))
                    }
                    .padding(.vertical, 8)
                    .padding(.horizontal, 14)
                    .background(isSelected ? Color.white.opacity(0.18) : Color.white.opacity(0.04))
                    .clipShape(Capsule())
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.bottom, 14)
    }

    // MARK: - Snaps Grid
    private var snapsGridView: some View {
        ScrollView {
            if cameraViewModel.snaps.isEmpty {
                emptyState(title: "No Snaps Yet", subtitle: "Take a photo with Evrry Camera to save memories.")
            } else {
                LazyVGrid(columns: columns, spacing: 12) {
                    ForEach(cameraViewModel.snaps) { snap in
                        snapGridCard(snap: snap)
                            .onTapGesture {
                                selectedSnap = snap
                            }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)
                .padding(.bottom, 30)
            }
        }
    }

    // MARK: - Camera Roll Grid
    private var cameraRollGridView: some View {
        ScrollView {
            if cameraViewModel.cameraRoll.isEmpty {
                emptyState(title: "Camera Roll Empty", subtitle: "Import device photos or capture new shots.")
            } else {
                LazyVGrid(columns: columns, spacing: 12) {
                    ForEach(cameraViewModel.cameraRoll) { snap in
                        snapGridCard(snap: snap)
                            .onTapGesture {
                                selectedSnap = snap
                            }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)
                .padding(.bottom, 30)
            }
        }
    }

    // MARK: - My Locker (Private Vault)
    private var myLockerView: some View {
        Group {
            if !cameraViewModel.isLockerUnlocked {
                // PIN Lock Screen
                lockerPinKeypadView
            } else {
                // Unlocked Vault Grid
                VStack(spacing: 0) {
                    HStack {
                        HStack(spacing: 6) {
                            Image(systemName: "lock.open.fill")
                                .foregroundColor(.green)
                            Text("Private Vault Unlocked")
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.white)
                        }
                        Spacer()
                        Button(action: {
                            cameraViewModel.lockLocker()
                            pinInput = ""
                            triggerToast("Vault locked 🔒")
                        }) {
                            Text("Lock Now")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(.white)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 6)
                                .background(Color.white.opacity(0.15))
                                .clipShape(Capsule())
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
                    .background(Color.yellow.opacity(0.1))

                    ScrollView {
                        if cameraViewModel.lockerSnaps.isEmpty {
                            emptyState(title: "Your Locker is Empty", subtitle: "Move personal photos from Snaps to keep them isolated.")
                        } else {
                            LazyVGrid(columns: columns, spacing: 12) {
                                ForEach(cameraViewModel.lockerSnaps) { snap in
                                    snapGridCard(snap: snap)
                                        .onTapGesture {
                                            selectedSnap = snap
                                        }
                                }
                            }
                            .padding(.horizontal, 16)
                            .padding(.top, 12)
                            .padding(.bottom, 30)
                        }
                    }
                }
            }
        }
    }

    // MARK: - PIN Keypad View
    private var lockerPinKeypadView: some View {
        VStack(spacing: 24) {
            Spacer()

            VStack(spacing: 8) {
                ZStack {
                    Circle()
                        .fill(Color(red: 0.85, green: 0.6, blue: 0.05).opacity(0.2))
                        .frame(width: 64, height: 64)
                    Image(systemName: "lock.shield.fill")
                        .font(.system(size: 30))
                        .foregroundColor(Color(red: 0.95, green: 0.75, blue: 0.2))
                }

                Text("Enter Passcode")
                    .font(.system(size: 20, weight: .bold))
                    .foregroundColor(.white)

                Text("Your private vault is protected with device encryption.\nDefault PIN: 1234")
                    .font(.system(size: 12))
                    .foregroundColor(.white.opacity(0.6))
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 30)
            }

            // 4 Dots Indicator
            HStack(spacing: 16) {
                ForEach(0..<4, id: \.self) { index in
                    Circle()
                        .fill(index < pinInput.count ? Color.white : Color.white.opacity(0.2))
                        .frame(width: 16, height: 16)
                        .overlay(
                            Circle().stroke(pinError ? Color.red : Color.clear, lineWidth: 2)
                        )
                }
            }
            .padding(.vertical, 8)

            if pinError {
                Text("Incorrect Passcode. Try 1234.")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(.red)
            }

            // Numeric Keypad Grid
            VStack(spacing: 14) {
                let rows = [
                    ["1", "2", "3"],
                    ["4", "5", "6"],
                    ["7", "8", "9"],
                    ["", "0", "delete"]
                ]

                ForEach(rows, id: \.self) { row in
                    HStack(spacing: 24) {
                        ForEach(row, id: \.self) { key in
                            if key.isEmpty {
                                Color.clear.frame(width: 68, height: 68)
                            } else if key == "delete" {
                                Button(action: {
                                    if !pinInput.isEmpty {
                                        pinInput.removeLast()
                                        pinError = false
                                    }
                                }) {
                                    ZStack {
                                        Circle()
                                            .fill(Color.white.opacity(0.08))
                                            .frame(width: 68, height: 68)
                                        Image(systemName: "delete.left.fill")
                                            .font(.system(size: 20))
                                            .foregroundColor(.white.opacity(0.8))
                                    }
                                }
                            } else {
                                Button(action: {
                                    handleDigitPress(digit: key)
                                }) {
                                    ZStack {
                                        Circle()
                                            .fill(Color.white.opacity(0.12))
                                            .frame(width: 68, height: 68)
                                        Text(key)
                                            .font(.system(size: 24, weight: .bold))
                                            .foregroundColor(.white)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer()
        }
        .padding(.horizontal, 24)
    }

    private func handleDigitPress(digit: String) {
        if pinInput.count < 4 {
            pinInput.append(digit)
            pinError = false

            if pinInput.count == 4 {
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.15) {
                    let success = cameraViewModel.unlockLocker(pin: pinInput)
                    if !success {
                        pinError = true
                        pinInput = ""
                    }
                }
            }
        }
    }

    // MARK: - Snap Grid Card
    private func snapGridCard(snap: SnapItem) -> some View {
        ZStack(alignment: .bottomLeading) {
            AsyncImage(url: URL(string: snap.imageUrl)) { phase in
                if let img = phase.image {
                    img.resizable().aspectRatio(contentMode: .fill)
                } else {
                    Color.gray.opacity(0.3)
                }
            }
            .frame(height: 220)
            .clipped()

            // Vignette gradient
            LinearGradient(
                colors: [Color.clear, Color.black.opacity(0.8)],
                startPoint: .center,
                endPoint: .bottom
            )

            // Overlay info
            VStack(alignment: .leading, spacing: 3) {
                if !snap.caption.isEmpty {
                    Text(snap.caption)
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(.white)
                        .lineLimit(1)
                }

                HStack(spacing: 4) {
                    Image(systemName: "mappin.and.ellipse")
                        .font(.system(size: 9))
                    Text(snap.location)
                        .font(.system(size: 10))
                        .lineLimit(1)
                }
                .foregroundColor(.white.opacity(0.8))
            }
            .padding(10)

            // Top lock badge if in locker
            if snap.isLocked {
                VStack {
                    HStack {
                        Spacer()
                        Image(systemName: "lock.fill")
                            .font(.system(size: 11))
                            .foregroundColor(.yellow)
                            .padding(6)
                            .background(Color.black.opacity(0.5))
                            .clipShape(Circle())
                    }
                    Spacer()
                }
                .padding(8)
            }
        }
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.white.opacity(0.12), lineWidth: 1)
        )
    }

    // MARK: - Snap Detail Fullscreen Modal
    private func snapDetailModal(snap: SnapItem) -> some View {
        ZStack {
            Color.black.ignoresSafeArea()

            // Photo Preview
            AsyncImage(url: URL(string: snap.imageUrl)) { phase in
                if let img = phase.image {
                    img.resizable().aspectRatio(contentMode: .fit)
                } else {
                    ProgressView().tint(.white)
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            // Top Header
            VStack {
                HStack {
                    Button(action: { selectedSnap = nil }) {
                        Image(systemName: "xmark")
                            .font(.system(size: 17, weight: .bold))
                            .foregroundColor(.white)
                            .frame(width: 40, height: 40)
                            .background(Color.black.opacity(0.5))
                            .clipShape(Circle())
                    }

                    Spacer()

                    Text(snap.filterName)
                        .font(.system(size: 13, weight: .semibold))
                        .foregroundColor(.white)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 6)
                        .background(Color.black.opacity(0.5))
                        .clipShape(Capsule())

                    Spacer()

                    // Delete button
                    Button(action: {
                        if snap.isLocked {
                            cameraViewModel.deleteLockerSnap(id: snap.id)
                        } else if activeTab == .cameraRoll {
                            cameraViewModel.deleteCameraRoll(id: snap.id)
                        } else {
                            cameraViewModel.deleteSnap(id: snap.id)
                        }
                        selectedSnap = nil
                        triggerToast("Deleted snap 🗑️")
                    }) {
                        Image(systemName: "trash")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.white)
                            .frame(width: 40, height: 40)
                            .background(Color.black.opacity(0.5))
                            .clipShape(Circle())
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 50)

                Spacer()

                // Bottom Controls Card
                VStack(spacing: 14) {
                    if !snap.caption.isEmpty {
                        Text(snap.caption)
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.white)
                            .multilineTextAlignment(.center)
                    }

                    HStack(spacing: 6) {
                        Image(systemName: "mappin.circle.fill")
                            .foregroundColor(.evrryPrimaryBlue)
                        Text(snap.location)
                            .font(.system(size: 12))
                            .foregroundColor(.white.opacity(0.8))
                    }

                    HStack(spacing: 16) {
                        // Send To Friend Action
                        Button(action: {
                            showSendToFriendSheet = true
                        }) {
                            HStack(spacing: 6) {
                                Image(systemName: "paperplane.fill")
                                Text("Send To")
                            }
                            .font(.system(size: 13, weight: .bold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 18)
                            .padding(.vertical, 10)
                            .background(Color.evrryPrimaryBlue)
                            .clipShape(Capsule())
                        }

                        // Post To Story Action
                        Button(action: {
                            chatViewModel.postStory(mediaUrl: snap.imageUrl, caption: snap.caption ?? "")
                            triggerToast("Posted to your Story 📖")
                        }) {
                            HStack(spacing: 6) {
                                Image(systemName: "book.fill")
                                Text("Story")
                            }
                            .font(.system(size: 13, weight: .bold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 10)
                            .background(Color.white.opacity(0.2))
                            .clipShape(Capsule())
                        }

                        // Move to Locker or Restore
                        if snap.isLocked {
                            Button(action: {
                                cameraViewModel.restoreFromLocker(id: snap.id)
                                selectedSnap = nil
                                triggerToast("Restored to Snaps ✨")
                            }) {
                                HStack(spacing: 4) {
                                    Image(systemName: "lock.open.fill")
                                    Text("Unhide")
                                }
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.white)
                                .padding(.horizontal, 14)
                                .padding(.vertical, 10)
                                .background(Color.white.opacity(0.2))
                                .clipShape(Capsule())
                            }
                        } else {
                            Button(action: {
                                cameraViewModel.moveToLocker(id: snap.id, fromTab: activeTab)
                                selectedSnap = nil
                                triggerToast("Moved to My Locker 🔒")
                            }) {
                                HStack(spacing: 4) {
                                    Image(systemName: "lock.fill")
                                    Text("Lock")
                                }
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.yellow)
                                .padding(.horizontal, 14)
                                .padding(.vertical, 10)
                                .background(Color.yellow.opacity(0.2))
                                .clipShape(Capsule())
                            }
                        }
                    }
                }
                .padding(20)
                .frame(maxWidth: .infinity)
                .background(Color.black.opacity(0.75))
                .clipShape(RoundedRectangle(cornerRadius: 24))
                .padding(.horizontal, 16)
                .padding(.bottom, 40)
            }
        }
    }

    // MARK: - Send to Friend Sheet
    private var sendToFriendSheet: some View {
        NavigationView {
            List {
                Section(header: Text("Share via WhatsApp-grade E2EE Chat")) {
                    ForEach(chatViewModel.chats) { chat in
                        Button(action: {
                            if let snap = selectedSnap {
                                chatViewModel.sendImage(chatId: chat.id, imageUrl: snap.imageUrl, caption: snap.caption)
                                showSendToFriendSheet = false
                                triggerToast("Sent to \(chat.title)! 🚀")
                            }
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
            .navigationTitle("Send Snap")
            .navigationBarItems(trailing: Button("Cancel") { showSendToFriendSheet = false })
        }
    }

    private func emptyState(title: String, subtitle: String) -> some View {
        VStack(spacing: 10) {
            Spacer().frame(height: 80)
            Image(systemName: "photo.stack")
                .font(.system(size: 44))
                .foregroundColor(.white.opacity(0.3))
            Text(title)
                .font(.system(size: 16, weight: .bold))
                .foregroundColor(.white)
            Text(subtitle)
                .font(.system(size: 13))
                .foregroundColor(.white.opacity(0.6))
                .multilineTextAlignment(.center)
                .padding(.horizontal, 40)
        }
    }

    private func triggerToast(_ msg: String) {
        toastMessage = msg
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.8) {
            if toastMessage == msg {
                toastMessage = nil
            }
        }
    }
}
