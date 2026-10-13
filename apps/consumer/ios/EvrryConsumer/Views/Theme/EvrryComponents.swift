//
//  EvrryComponents.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Shared UI Components: In-App Back Navigation & ChatGPT Model Switcher
//

import SwiftUI

public struct EvrryTopBar: View {
    public let title: String?
    public let canGoBack: Bool
    public let onBackClick: () -> Void
    public let isHome: Bool
    public let activeModel: VoiceAgentModel
    public let onSelectModel: (VoiceAgentModel) -> Void
    public let onMenuClick: () -> Void
    public let onProfileClick: () -> Void
    public let cartItemCount: Int
    public let onCartClick: (() -> Void)?
    
    @State private var showModelMenu: Bool = false
    
    public init(
        title: String? = nil,
        canGoBack: Bool = false,
        onBackClick: @escaping () -> Void = {},
        isHome: Bool = false,
        activeModel: VoiceAgentModel = VoiceAgentRegistry.ellieMini,
        onSelectModel: @escaping (VoiceAgentModel) -> Void = { _ in },
        onMenuClick: @escaping () -> Void = {},
        onProfileClick: @escaping () -> Void = {},
        cartItemCount: Int = 0,
        onCartClick: (() -> Void)? = nil
    ) {
        self.title = title
        self.canGoBack = canGoBack
        self.onBackClick = onBackClick
        self.isHome = isHome
        self.activeModel = activeModel
        self.onSelectModel = onSelectModel
        self.onMenuClick = onMenuClick
        self.onProfileClick = onProfileClick
        self.cartItemCount = cartItemCount
        self.onCartClick = onCartClick
    }
    
    public var body: some View {
        HStack {
            // LEFT: In-App Back Button or Menu (Essential for iOS)
            if canGoBack {
                Button(action: onBackClick) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                        .frame(width: 44, height: 44)
                        .background(Color.white)
                        .clipShape(Circle())
                        .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        .shadow(color: Color.black.opacity(0.04), radius: 3, x: 0, y: 1)
                }
            } else if isHome {
                Button(action: onMenuClick) {
                    Image(systemName: "line.3.horizontal")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                        .frame(width: 44, height: 44)
                        .background(Color.white)
                        .clipShape(Circle())
                        .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                }
            } else {
                Spacer().frame(width: 44)
            }
            
            Spacer()
            
            // CENTER: ChatGPT-Style Minimal Model Switcher [ Ellie Mini ▾ ] or Title
            if isHome {
                Menu {
                    Section("Select AI Model") {
                        ForEach(VoiceAgentRegistry.allModels) { model in
                            Button(action: { onSelectModel(model) }) {
                                HStack {
                                    Text("\(model.name) (\(model.badge))")
                                    if model.id == activeModel.id {
                                        Image(systemName: "checkmark")
                                    }
                                }
                            }
                        }
                    }
                } label: {
                    HStack(spacing: 6) {
                        Text(activeModel.name)
                            .font(.system(size: 14, weight: .semibold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Image(systemName: "chevron.down")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                    }
                    .padding(.horizontal, 14)
                    .padding(.vertical, 7)
                    .background(Color.white.opacity(0.95))
                    .clipShape(Capsule())
                    .overlay(Capsule().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    .shadow(color: Color.black.opacity(0.03), radius: 2, x: 0, y: 1)
                }
            } else if let title = title {
                Text(title)
                    .font(.system(size: 17, weight: .bold))
                    .foregroundColor(.evrryDarkCharcoal)
            }
            
            Spacer()
            
            // RIGHT: Cart Icon Button with Badge + Profile Avatar
            HStack(spacing: 8) {
                if let onCartClick = onCartClick {
                    Button(action: onCartClick) {
                        ZStack(alignment: .topTrailing) {
                            Circle()
                                .fill(Color.white)
                                .frame(width: 40, height: 40)
                                .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                .shadow(color: Color.black.opacity(0.04), radius: 2, y: 1)
                            
                            Image(systemName: "cart.fill")
                                .font(.system(size: 16))
                                .foregroundColor(cartItemCount > 0 ? .evrryPrimaryBlue : .evrryDarkCharcoal)
                                .frame(width: 40, height: 40)
                            
                            if cartItemCount > 0 {
                                Text("\(min(cartItemCount, 99))")
                                    .font(.system(size: 10, weight: .bold))
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 4)
                                    .padding(.vertical, 2)
                                    .background(Color.evrryPrimaryBlue)
                                    .clipShape(Capsule())
                                    .offset(x: 4, y: -4)
                            }
                        }
                    }
                    .frame(width: 40, height: 40)
                }
                
                Button(action: onProfileClick) {
                    Image(systemName: "person.fill")
                        .font(.system(size: 16))
                        .foregroundColor(.evrryPrimaryBlue)
                        .frame(width: 40, height: 40)
                        .background(Color.white)
                        .clipShape(Circle())
                        .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        .shadow(color: Color.black.opacity(0.04), radius: 3, x: 0, y: 1)
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
    }
}

public struct EvrryBottomBar: View {
    public let currentRoute: String
    public let chatsUnreadCount: Int
    public let onNavigate: (String) -> Void
    
    public init(currentRoute: String, chatsUnreadCount: Int = 3, onNavigate: @escaping (String) -> Void) {
        self.currentRoute = currentRoute
        self.chatsUnreadCount = chatsUnreadCount
        self.onNavigate = onNavigate
    }
    
    public var body: some View {
        HStack {
            tabItem(route: "home", label: "AI", systemImage: "sparkles")
            Spacer()
            tabItem(route: "explore", label: "Explore", systemImage: "safari")
            Spacer()
            tabItem(route: "chats", label: "Chats", systemImage: "bubble.left.and.bubble.right.fill", badge: chatsUnreadCount)
            Spacer()
            tabItem(route: "rides", label: "Rides", systemImage: "car.fill")
            Spacer()
            tabItem(route: "activity", label: "Activity", systemImage: "list.bullet.rectangle.portrait")
            Spacer()
            tabItem(route: "settings", label: "Profile", systemImage: "person.crop.circle")
        }
        .padding(.horizontal, 16)
        .padding(.top, 10)
        .padding(.bottom, 22)
        .background(Color.white)
        .overlay(Rectangle().frame(height: 1).foregroundColor(.evrrySubtleBorder), alignment: .top)
        .shadow(color: Color.black.opacity(0.05), radius: 6, x: 0, y: -2)
    }
    
    private func tabItem(route: String, label: String, systemImage: String, badge: Int = 0) -> some View {
        let isSelected = currentRoute == route
        return Button(action: { onNavigate(route) }) {
            VStack(spacing: 3) {
                ZStack(alignment: .topTrailing) {
                    Image(systemName: systemImage)
                        .font(.system(size: 20))
                        .foregroundColor(isSelected ? .evrryPrimaryBlue : .evrryMutedText)
                    if badge > 0 {
                        Text(badge > 9 ? "9+" : "\(badge)")
                            .font(.system(size: 8, weight: .bold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 4)
                            .padding(.vertical, 1)
                            .background(Color.evrryPrimaryBlue)
                            .clipShape(Capsule())
                            .offset(x: 10, y: -6)
                    }
                }
                Text(label)
                    .font(.system(size: 10, weight: isSelected ? .bold : .medium))
                    .foregroundColor(isSelected ? .evrryPrimaryBlue : .evrryMutedText)
            }
        }
    }
}
