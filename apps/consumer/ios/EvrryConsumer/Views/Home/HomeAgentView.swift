//
//  HomeAgentView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Flagship Home AI Concierge View (Matching chat.png & Assistant.png)
//

import SwiftUI

public struct HomeAgentView: View {
    @ObservedObject public var viewModel: HomeViewModel
    public let displayName: String
    public let onMenuClick: () -> Void
    public let onProfileClick: () -> Void
    public let cartItemCount: Int
    public let onCartClick: (() -> Void)?
    
    @State private var showDrawer: Bool = false
    @State private var showModelMenu: Bool = false
    
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
        ZStack {
            // SOFT AMBIENT GRADIENT BACKGROUND
            LinearGradient(
                colors: [Color(hex: 0xF9FBFF), Color.white, Color(hex: 0xE8F1FF)],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()
            
            VStack(spacing: 0) {
                // TOP BAR MATCHING chat.png AND Assistant.png
                HStack {
                    // Left: 2-line Hamburger Menu Button
                    Button(action: { showDrawer = true }) {
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
                    
                    // Center: ChatGPT-Style Minimal Model Switcher [ Ellie ▾ ]
                    Menu {
                        ForEach(VoiceAgentRegistry.allModels, id: \.id) { model in
                            Button(action: { viewModel.selectModel(model) }) {
                                HStack {
                                    Text("\(model.name) (\(model.badge))")
                                    if viewModel.activeModel.id == model.id {
                                        Image(systemName: "checkmark")
                                    }
                                }
                            }
                        }
                    } label: {
                        HStack(spacing: 6) {
                            Text(viewModel.activeModel.name)
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Image(systemName: "chevron.down")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                        }
                        .padding(.horizontal, 14)
                        .padding(.vertical, 6)
                        .background(Color.white.opacity(0.85))
                        .clipShape(Capsule())
                        .overlay(Capsule().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        .shadow(color: Color.black.opacity(0.03), radius: 2, x: 0, y: 1)
                    }
                    
                    Spacer()
                    
                    // Right: Cart Button (with Badge 1, 2, 3...) & Profile Avatar Button
                    HStack(spacing: 8) {
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
                        
                        Button(action: onProfileClick) {
                            ZStack {
                                Circle()
                                    .fill(Color.white)
                                    .frame(width: 38, height: 38)
                                    .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                
                                Text(String(displayName.prefix(1)).uppercased())
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.evrryPrimaryBlue)
                            }
                        }
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 10)
                .padding(.bottom, 6)
                
                // =====================================================================
                // MODE 1: CHAT SCREEN (chat.png)
                // =====================================================================
                if viewModel.aiMode == "chat" {
                    ZStack(alignment: .bottom) {
                        if viewModel.messages.isEmpty {
                            // EMPTY HERO STATE (Exactly from chat.png)
                            VStack(spacing: 0) {
                                Spacer()
                                Text("Hi \(displayName), what's on\nyour mind?")
                                    .font(.system(size: 28, weight: .regular))
                                    .foregroundColor(.evrryDarkCharcoal)
                                    .multilineTextAlignment(.center)
                                    .lineSpacing(6)
                                    .offset(y: -40)
                                Spacer()
                            }
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
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
                        
                        
                        // FLOATING CAPSULE COMPOSER (From chat.png)
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
                            } else {
                                Button(action: { viewModel.setAiMode("assistant") }) {
                                    Image(systemName: "mic.fill")
                                        .font(.system(size: 16))
                                        .foregroundColor(Color(hex: 0x64748B))
                                        .frame(width: 36, height: 36)
                                }
                                
                                // Waveform launcher button
                                Button(action: { viewModel.setAiMode("assistant") }) {
                                    HStack(spacing: 2.5) {
                                        RoundedRectangle(cornerRadius: 1).fill(Color.evrryPrimaryBlue).frame(width: 2.5, height: 10)
                                        RoundedRectangle(cornerRadius: 1).fill(Color.evrryPrimaryBlue).frame(width: 2.5, height: 16)
                                        RoundedRectangle(cornerRadius: 1).fill(Color.evrryPrimaryBlue).frame(width: 2.5, height: 8)
                                        RoundedRectangle(cornerRadius: 1).fill(Color.evrryPrimaryBlue).frame(width: 2.5, height: 14)
                                    }
                                    .frame(width: 36, height: 36)
                                    .background(Color(hex: 0xE8F1FF))
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
                
                // =====================================================================
                // MODE 2: VOICE ASSISTANT SCREEN (Assistant.png)
                // =====================================================================
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
                        
                        // Bottom action controls row
                        HStack(spacing: 24) {
                            // 1. Upload
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
                            
                            // 2. Waveform Pill
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
                            
                            // 3. Mute Toggle
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
                            
                            // 4. Close X
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
        .sheet(isPresented: $showDrawer) {
            assistantDrawer
        }
    }
    
    private var voiceStateDescription: String {
        switch viewModel.voiceState {
        case .listening: return "Listening…"
        case .thinking: return "Thinking…"
        case .speaking: return "Speaking…"
        case .idle: return "Tap wave to talk"
        }
    }
    
    private var assistantDrawer: some View {
        VStack(alignment: .leading, spacing: 18) {
            HStack {
                VStack(alignment: .leading, spacing: 3) {
                    HStack(spacing: 8) {
                        Text("evrry Assistant")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text(viewModel.activeModel.name)
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryPrimaryBlue)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 3)
                            .background(Color(hex: 0xEFF6FF))
                            .clipShape(Capsule())
                    }
                    Text("Everything. One Place. Powered by Gemini.")
                        .font(.system(size: 12))
                        .foregroundColor(.evrryMutedText)
                }
                Spacer()
                Button(action: { showDrawer = false }) {
                    Image(systemName: "xmark")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                }
            }
            
            Divider()
            
            Text("AI MODEL")
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(.evrryMutedText)
            
            ForEach(VoiceAgentRegistry.allModels, id: \.id) { model in
                let isSelected = viewModel.activeModel.id == model.id
                Button(action: {
                    viewModel.selectModel(model)
                    showDrawer = false
                }) {
                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            HStack(spacing: 6) {
                                Text(model.name)
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Text(model.badge)
                                    .font(.system(size: 9, weight: .bold))
                                    .foregroundColor(.evrryPrimaryBlue)
                                    .padding(.horizontal, 4)
                                    .padding(.vertical, 1)
                                    .background(Color.white)
                                    .clipShape(RoundedRectangle(cornerRadius: 4))
                            }
                            Text(model.description)
                                .font(.system(size: 11))
                                .foregroundColor(.evrryMutedText)
                        }
                        Spacer()
                        if isSelected {
                            Image(systemName: "checkmark")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundColor(.evrryPrimaryBlue)
                        }
                    }
                    .padding(14)
                    .background(isSelected ? Color(hex: 0xEFF6FF) : Color(hex: 0xF8FAFC))
                    .clipShape(RoundedRectangle(cornerRadius: 16))
                    .overlay(RoundedRectangle(cornerRadius: 16).stroke(isSelected ? Color.evrryPrimaryBlue : Color.clear, lineWidth: 1.5))
                }
            }
            
            Button(action: {
                showDrawer = false
                onProfileClick()
            }) {
                HStack {
                    HStack(spacing: 10) {
                        Image(systemName: "gearshape.fill")
                            .foregroundColor(.evrryPrimaryBlue)
                        Text("AI & App Settings")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(.evrryDarkCharcoal)
                    }
                    Spacer()
                    Text("Configure →")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(.evrryPrimaryBlue)
                }
                .padding(14)
                .background(Color(hex: 0xF8FAFC))
                .clipShape(RoundedRectangle(cornerRadius: 16))
            }
            
            if !viewModel.messages.isEmpty {
                Button(action: {
                    viewModel.clearChat()
                    showDrawer = false
                }) {
                    Text("Clear Conversation")
                        .font(.system(size: 13, weight: .semibold))
                        .foregroundColor(Color(hex: 0xEF4444))
                        .frame(maxWidth: .infinity)
                }
                .padding(.top, 4)
            }
            
            Spacer()
        }
        .padding(24)
        .presentationDetents([.medium, .large])
    }
}
