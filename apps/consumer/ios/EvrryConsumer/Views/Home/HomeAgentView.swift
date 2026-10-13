//
//  HomeAgentView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Home AI Concierge View (ChatGPT Model Switcher & Real-time voice)
//

import SwiftUI

public struct HomeAgentView: View {
    @ObservedObject public var viewModel: HomeViewModel
    public let onMenuClick: () -> Void
    public let onProfileClick: () -> Void
    public let cartItemCount: Int
    public let onCartClick: (() -> Void)?
    
    public init(
        viewModel: HomeViewModel,
        onMenuClick: @escaping () -> Void = {},
        onProfileClick: @escaping () -> Void = {},
        cartItemCount: Int = 0,
        onCartClick: (() -> Void)? = nil
    ) {
        self.viewModel = viewModel
        self.onMenuClick = onMenuClick
        self.onProfileClick = onProfileClick
        self.cartItemCount = cartItemCount
        self.onCartClick = onCartClick
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            // TOP BAR with ChatGPT-Style Model Switcher [ Ellie ▾ ] & Cart Button
            EvrryTopBar(
                isHome: true,
                activeModel: viewModel.activeModel,
                onSelectModel: { viewModel.selectModel($0) },
                onMenuClick: onMenuClick,
                onProfileClick: onProfileClick,
                cartItemCount: cartItemCount,
                onCartClick: onCartClick
            )
            
            // CHAT TRANSCRIPT STREAM
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
                                        .frame(maxWidth: 280, alignment: .trailing)
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
                                        .frame(maxWidth: 280, alignment: .leading)
                                    Spacer()
                                }
                            }
                            .id(msg.id)
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.top, 8)
                    .padding(.bottom, 16)
                }
                .onChange(of: viewModel.messages.count) { _ in
                    if let last = viewModel.messages.last {
                        withAnimation {
                            proxy.scrollTo(last.id, anchor: .bottom)
                        }
                    }
                }
            }
            
            // CENTER VOICE VISUALIZER ORB (Animated)
            if viewModel.voiceState != .idle {
                Button(action: { viewModel.toggleVoiceOrb() }) {
                    HStack(spacing: 8) {
                        Text("🎙️")
                        Text(voiceStateLabel)
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(.evrryPrimaryBlue)
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(Color.blue.opacity(0.1))
                    .clipShape(Capsule())
                    .overlay(Capsule().stroke(Color.evrryPrimaryBlue, lineWidth: 1.5))
                }
                .padding(.vertical, 6)
            }
            
            // CAPSULE COMPOSER (Bottom)
            HStack(spacing: 8) {
                Button(action: { /* Media attach */ }) {
                    Image(systemName: "plus")
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(.evrryMutedText)
                        .frame(width: 36, height: 36)
                }
                
                TextField("Ask Evrry…", text: $viewModel.inputText)
                    .font(.system(size: 14))
                
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
                    Button(action: { viewModel.toggleVoiceOrb() }) {
                        Text("🎙️")
                            .font(.system(size: 16))
                            .frame(width: 36, height: 36)
                            .background(Color.blue.opacity(0.08))
                            .clipShape(Circle())
                    }
                }
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 6)
            .background(Color.white)
            .clipShape(Capsule())
            .overlay(Capsule().stroke(Color.evrrySubtleBorder, lineWidth: 1))
            .shadow(color: Color.black.opacity(0.04), radius: 3, x: 0, y: 1)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
        }
        .background(Color.evrryBgTop.ignoresSafeArea())
    }
    
    private var voiceStateLabel: String {
        switch viewModel.voiceState {
        case .listening: return "Listening…"
        case .thinking: return "Thinking…"
        case .speaking: return "Speaking…"
        case .idle: return "Tap to speak"
        }
    }
}
