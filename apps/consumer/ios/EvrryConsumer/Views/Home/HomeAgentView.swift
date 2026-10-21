//
//  HomeAgentView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  GPT/Gemini-style side drawer, centered voice orb, clean chat composer.
//

import SwiftUI

public struct HomeAgentView: View {
    @ObservedObject public var viewModel: HomeViewModel
    public let displayName: String
    public let onMenuClick: () -> Void
    public let onProfileClick: () -> Void
    public let cartItemCount: Int
    public let onCartClick: (() -> Void)?

    @State private var showSidebar: Bool = false

    public init(
        viewModel: HomeViewModel,
        displayName: String = "Rahul",
        onMenuClick: @escaping () -> Void = {},
        onProfileClick: @escaping () -> Void = {},
        cartItemCount: Int = 0,
        onCartClick: (() -> Void)? = nil
    ) {
        self.viewModel = viewModel
        self.displayName = displayName
        self.onMenuClick = onMenuClick
        self.onProfileClick = onProfileClick
        self.cartItemCount = cartItemCount
        self.onCartClick = onCartClick
    }

    public var body: some View {
        ZStack(alignment: .leading) {
            // MAIN CONTENT
            ZStack {
                LinearGradient(
                    colors: [Color(hex: 0xF9FBFF), Color.white, Color(hex: 0xE8F1FF)],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .ignoresSafeArea()

                VStack(spacing: 0) {
                    // TOP BAR: [hamburger] --- [Ellie] --- [Cart]
                    HStack {
                        Button(action: { withAnimation(.easeInOut(duration: 0.25)) { showSidebar = true } }) {
                            VStack(alignment: .leading, spacing: 5) {
                                RoundedRectangle(cornerRadius: 1)
                                    .fill(Color.evrryDarkCharcoal)
                                    .frame(width: 20, height: 2.5)
                                RoundedRectangle(cornerRadius: 1)
                                    .fill(Color.evrryDarkCharcoal)
                                    .frame(width: 15, height: 2.5)
                            }
                            .frame(width: 40, height: 40)
                        }

                        Spacer()

                        // Static "Ellie" label (no model switcher)
                        Text("Ellie")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.evrryDarkCharcoal)

                        Spacer()

                        // Cart only (profile moved to sidebar)
                        Button(action: { onCartClick?() }) {
                            ZStack(alignment: .topTrailing) {
                                Circle()
                                    .fill(Color.white)
                                    .frame(width: 38, height: 38)
                                    .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))

                                Image(systemName: "cart.fill")
                                    .font(.system(size: 16))
                                    .foregroundColor(cartItemCount > 0 ? .evrryPrimaryBlue : .evrryDarkCharcoal)
                                    .frame(width: 38, height: 38)

                                if cartItemCount > 0 {
                                    Text(cartItemCount > 99 ? "99+" : "\(cartItemCount)")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(.white)
                                        .frame(width: 18, height: 18)
                                        .background(Color.evrryPrimaryBlue)
                                        .clipShape(Circle())
                                        .offset(x: 4, y: -4)
                                }
                            }
                        }
                    }
                    .padding(.horizontal, 20)
                    .padding(.top, 10)
                    .padding(.bottom, 6)

                    // =================================================================
                    // MODE 1: CHAT SCREEN
                    // =================================================================
                    if viewModel.aiMode == "chat" {
                        ZStack(alignment: .bottom) {
                            if viewModel.messages.isEmpty {
                                // EMPTY HERO STATE with Voice Orb
                                VStack(spacing: 0) {
                                    Spacer()

                                    Text("Hi \(displayName), what's on\nyour mind?")
                                        .font(.system(size: 28, weight: .regular))
                                        .foregroundColor(.evrryDarkCharcoal)
                                        .multilineTextAlignment(.center)
                                        .lineSpacing(6)

                                    // ChatGPT-Style Voice Orb
                                    ZStack {
                                        Circle()
                                            .fill(
                                                RadialGradient(
                                                    colors: [Color.evrryPrimaryBlue.opacity(0.15), Color.clear],
                                                    center: .center,
                                                    startRadius: 0,
                                                    endRadius: 44
                                                )
                                            )
                                            .frame(width: 88, height: 88)

                                        Button(action: { viewModel.setAiMode("assistant") }) {
                                            Image(systemName: "mic.fill")
                                                .font(.system(size: 26))
                                                .foregroundColor(.white)
                                                .frame(width: 64, height: 64)
                                                .background(Color.evrryPrimaryBlue)
                                                .clipShape(Circle())
                                                .shadow(color: Color.evrryPrimaryBlue.opacity(0.3), radius: 8, x: 0, y: 4)
                                        }
                                    }
                                    .padding(.top, 40)

                                    Text("Tap to talk with Ellie")
                                        .font(.system(size: 13, weight: .medium))
                                        .foregroundColor(.evrryMutedText)
                                        .padding(.top, 12)

                                    Spacer()
                                }
                                .frame(maxWidth: .infinity, maxHeight: .infinity)
                                .offset(y: -20)
                            } else {
                                // TRANSCRIPT STREAM
                                ScrollViewReader { proxy in
                                    ScrollView {
                                        LazyVStack(spacing: 12) {
                                            ForEach(viewModel.messages) { msg in
                                                HStack {
                                                    if msg.isUser {
                                                        Spacer()
                                                        Text(msg.text)
                                                            .font(.system(size: 14))
                                                            .foregroundColor(.white)
                                                            .padding(.horizontal, 14)
                                                            .padding(.vertical, 10)
                                                            .background(Color.evrryPrimaryBlue)
                                                            .clipShape(RoundedRectangle(cornerRadius: 18))
                                                            .frame(maxWidth: 290, alignment: .trailing)
                                                    } else {
                                                        Text(msg.text)
                                                            .font(.system(size: 14))
                                                            .foregroundColor(.evrryDarkCharcoal)
                                                            .padding(.horizontal, 14)
                                                            .padding(.vertical, 10)
                                                            .background(Color.white)
                                                            .clipShape(RoundedRectangle(cornerRadius: 18))
                                                            .overlay(RoundedRectangle(cornerRadius: 18).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                                            .shadow(color: Color.black.opacity(0.02), radius: 2, x: 0, y: 1)
                                                            .frame(maxWidth: 290, alignment: .leading)
                                                        Spacer()
                                                    }
                                                }
                                                .id(msg.id)
                                            }
                                        }
                                        .padding(.horizontal, 16)
                                        .padding(.top, 8)
                                        .padding(.bottom, 90)
                                    }
                                    .onChange(of: viewModel.messages.count) { _ in
                                        if let last = viewModel.messages.last {
                                            withAnimation { proxy.scrollTo(last.id, anchor: .bottom) }
                                        }
                                    }
                                }
                            }

                            // FLOATING CAPSULE COMPOSER (no mic/waveform icons)
                            HStack(spacing: 8) {
                                Button(action: { /* Media attachment */ }) {
                                    Image(systemName: "plus")
                                        .font(.system(size: 18, weight: .semibold))
                                        .foregroundColor(Color(hex: 0x64748B))
                                        .frame(width: 38, height: 38)
                                }

                                TextField("Ask Evrry...", text: $viewModel.inputText)
                                    .font(.system(size: 15))

                                if !viewModel.inputText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                                    Button(action: { viewModel.sendMessage() }) {
                                        Image(systemName: "arrow.up")
                                            .font(.system(size: 14, weight: .bold))
                                            .foregroundColor(.white)
                                            .frame(width: 36, height: 36)
                                            .background(Color.evrryPrimaryBlue)
                                            .clipShape(Circle())
                                    }
                                }
                            }
                            .padding(.horizontal, 10)
                            .padding(.vertical, 6)
                            .background(Color.white)
                            .clipShape(Capsule())
                            .overlay(Capsule().stroke(Color(hex: 0xE2E8F0), lineWidth: 1))
                            .shadow(color: Color.black.opacity(0.08), radius: 6, x: 0, y: 3)
                            .padding(.horizontal, 16)
                            .padding(.bottom, 12)
                        }
                    }

                    // =================================================================
                    // MODE 2: VOICE ASSISTANT SCREEN
                    // =================================================================
                    if viewModel.aiMode == "assistant" {
                        VStack {
                            Spacer()

                            VStack(spacing: 14) {
                                Text(viewModel.caption.isEmpty ? "Hi \(displayName), what's on\nyour mind?" : viewModel.caption)
                                    .font(.system(size: 26, weight: .regular))
                                    .foregroundColor(.evrryDarkCharcoal)
                                    .multilineTextAlignment(.center)
                                    .lineSpacing(6)
                                    .padding(.horizontal, 32)

                                Text(voiceStateDescription)
                                    .font(.system(size: 13, weight: .medium))
                                    .foregroundColor(Color(hex: 0x94A3B8))
                            }

                            Spacer()

                            HStack(spacing: 24) {
                                Button(action: { /* Media upload */ }) {
                                    Image(systemName: "arrow.up.circle.fill")
                                        .font(.system(size: 22))
                                        .foregroundColor(.evrryDarkCharcoal)
                                        .frame(width: 48, height: 48)
                                        .background(Color.white)
                                        .clipShape(Circle())
                                        .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                        .shadow(color: Color.black.opacity(0.06), radius: 4, x: 0, y: 2)
                                }

                                Button(action: { viewModel.toggleVoiceOrb() }) {
                                    HStack(spacing: 4) {
                                        RoundedRectangle(cornerRadius: 1).fill(Color.evrryPrimaryBlue).frame(width: 3, height: 12)
                                        RoundedRectangle(cornerRadius: 1).fill(Color.evrryPrimaryBlue).frame(width: 3, height: 24)
                                        RoundedRectangle(cornerRadius: 1).fill(Color.evrryPrimaryBlue).frame(width: 3, height: 18)
                                        RoundedRectangle(cornerRadius: 1).fill(Color.evrryPrimaryBlue).frame(width: 3, height: 28)
                                        RoundedRectangle(cornerRadius: 1).fill(Color.evrryPrimaryBlue).frame(width: 3, height: 14)
                                    }
                                    .frame(width: 120, height: 54)
                                    .background(Color.white)
                                    .clipShape(Capsule())
                                    .overlay(Capsule().stroke(viewModel.voiceState != .idle ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: 1.5))
                                    .shadow(color: Color.black.opacity(0.08), radius: 6, x: 0, y: 3)
                                }

                                Button(action: { viewModel.toggleMute() }) {
                                    Image(systemName: viewModel.isMuted ? "mic.slash.fill" : "mic.fill")
                                        .font(.system(size: 20))
                                        .foregroundColor(viewModel.isMuted ? Color(hex: 0xEF4444) : .evrryDarkCharcoal)
                                        .frame(width: 48, height: 48)
                                        .background(viewModel.isMuted ? Color(hex: 0xF1F5F9) : Color.white)
                                        .clipShape(Circle())
                                        .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                        .shadow(color: Color.black.opacity(0.06), radius: 4, x: 0, y: 2)
                                }

                                Button(action: { viewModel.setAiMode("chat") }) {
                                    Image(systemName: "xmark")
                                        .font(.system(size: 18, weight: .bold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                        .frame(width: 48, height: 48)
                                        .background(Color.white)
                                        .clipShape(Circle())
                                        .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                        .shadow(color: Color.black.opacity(0.06), radius: 4, x: 0, y: 2)
                                }
                            }
                            .padding(.bottom, 24)
                        }
                    }
                }
            }
            .offset(x: showSidebar ? 300 : 0)

            // GPT/GEMINI-STYLE SIDEBAR OVERLAY
            if showSidebar {
                Color.black.opacity(0.3)
                    .ignoresSafeArea()
                    .offset(x: 300)
                    .onTapGesture { withAnimation(.easeInOut(duration: 0.25)) { showSidebar = false } }

                sidebarContent
                    .frame(width: 300)
                    .background(Color.white)
                    .transition(.move(edge: .leading))
            }
        }
    }

    private var voiceStateDescription: String {
        switch viewModel.voiceState {
        case .listening: return "Listening..."
        case .thinking: return "Thinking..."
        case .speaking: return "Speaking..."
        case .idle: return "Tap wave to talk"
        }
    }

    private var sidebarContent: some View {
        VStack(alignment: .leading, spacing: 0) {
            // Header
            HStack {
                Text("Evrry AI")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(.evrryDarkCharcoal)
                Spacer()
                Button(action: {
                    withAnimation(.easeInOut(duration: 0.25)) { showSidebar = false }
                    viewModel.clearChat()
                }) {
                    Image(systemName: "square.and.pencil")
                        .font(.system(size: 16))
                        .foregroundColor(.evrryMutedText)
                }
            }
            .padding(.horizontal, 20)
            .padding(.top, 60)
            .padding(.bottom, 12)

            // New Chat Button
            Button(action: {
                withAnimation(.easeInOut(duration: 0.25)) { showSidebar = false }
                viewModel.clearChat()
            }) {
                HStack(spacing: 10) {
                    Image(systemName: "plus")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text("New conversation")
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(.evrryDarkCharcoal)
                }
                .padding(12)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(hex: 0xF1F5F9))
                .clipShape(RoundedRectangle(cornerRadius: 12))
            }
            .padding(.horizontal, 16)

            // Recent Label
            Text("Recent")
                .font(.system(size: 12, weight: .semibold))
                .foregroundColor(.evrryMutedText)
                .padding(.horizontal, 20)
                .padding(.top, 16)
                .padding(.bottom, 4)

            // Chat History (scrollable middle)
            ScrollView {
                VStack(spacing: 2) {
                    if !viewModel.messages.isEmpty {
                        let firstUserMsg = viewModel.messages.first(where: { $0.isUser })?.text ?? "Conversation"
                        Button(action: { withAnimation(.easeInOut(duration: 0.25)) { showSidebar = false } }) {
                            HStack(spacing: 10) {
                                Image(systemName: "bubble.left")
                                    .font(.system(size: 14))
                                    .foregroundColor(.evrryPrimaryBlue)
                                Text(String(firstUserMsg.prefix(32)) + (firstUserMsg.count > 32 ? "..." : ""))
                                    .font(.system(size: 13, weight: .medium))
                                    .foregroundColor(.evrryDarkCharcoal)
                                    .lineLimit(1)
                            }
                            .padding(12)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(Color(hex: 0xEFF6FF))
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                        }
                    }
                }
                .padding(.horizontal, 12)
            }

            Divider()
                .padding(.horizontal, 16)

            // AI & App Settings
            Button(action: {
                withAnimation(.easeInOut(duration: 0.25)) { showSidebar = false }
                onProfileClick()
            }) {
                HStack(spacing: 12) {
                    Image(systemName: "gearshape.fill")
                        .font(.system(size: 16))
                        .foregroundColor(.evrryMutedText)
                    Text("AI & App Settings")
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(.evrryDarkCharcoal)
                }
                .padding(12)
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .padding(.horizontal, 12)
            .padding(.top, 8)

            // Profile at very bottom
            Button(action: {
                withAnimation(.easeInOut(duration: 0.25)) { showSidebar = false }
                onProfileClick()
            }) {
                HStack(spacing: 12) {
                    ZStack {
                        Circle()
                            .fill(
                                LinearGradient(
                                    colors: [Color.evrryPrimaryBlue, Color(hex: 0x7C3AED)],
                                    startPoint: .topLeading,
                                    endPoint: .bottomTrailing
                                )
                            )
                            .frame(width: 32, height: 32)

                        Text(String(displayName.prefix(1)).uppercased())
                            .font(.system(size: 13, weight: .bold))
                            .foregroundColor(.white)
                    }

                    VStack(alignment: .leading, spacing: 1) {
                        Text(displayName.isEmpty ? "User" : displayName)
                            .font(.system(size: 14, weight: .semibold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("View profile")
                            .font(.system(size: 11))
                            .foregroundColor(.evrryMutedText)
                    }
                }
                .padding(12)
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .padding(.horizontal, 12)
            .padding(.bottom, 16)
        }
    }
}
