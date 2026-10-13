//
//  SplashView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Brand Animated Splash Screen (1.5 seconds)
//  Lightweight, battery-efficient, native SwiftUI animations.
//

import SwiftUI

public struct SplashView: View {
    @ObservedObject public var viewModel: AuthViewModel
    public var hasSeenOnboarding: Bool = false
    
    @State private var opacity: Double = 0.0
    @State private var scale: CGFloat = 0.88
    
    public init(viewModel: AuthViewModel, hasSeenOnboarding: Bool = false) {
        self.viewModel = viewModel
        self.hasSeenOnboarding = hasSeenOnboarding
    }
    
    public var body: some View {
        ZStack {
            LinearGradient(
                colors: [Color.white, Color(red: 240/255, green: 246/255, blue: 255/255)],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()
            
            VStack(spacing: 0) {
                // Brandmark Icon
                ZStack {
                    RoundedRectangle(cornerRadius: 26, style: .continuous)
                        .fill(Color.evrryPrimaryBlue)
                        .frame(width: 92, height: 92)
                        .shadow(color: Color.evrryPrimaryBlue.opacity(0.35), radius: 16, x: 0, y: 8)
                    
                    Text("e")
                        .font(.system(size: 52, weight: .black))
                        .foregroundColor(.white)
                }
                
                Spacer().frame(height: 20)
                
                // App Name + Nepal Flag
                HStack(spacing: 6) {
                    Text("evrry")
                        .font(.system(size: 34, weight: .black))
                        .foregroundColor(.evrryDarkCharcoal)
                        .tracking(-1)
                    
                    Text("🇳🇵")
                        .font(.system(size: 22))
                }
                
                Spacer().frame(height: 8)
                
                // Brand Tagline
                Text("Nepal's Everything Super App")
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(.evrryMutedText)
                    .tracking(0.5)
                
                Spacer().frame(height: 48)
                
                // Lightweight Micro Status Indicator
                HStack(spacing: 6) {
                    Circle()
                        .fill(Color.evrryPrimaryBlue)
                        .frame(width: 6, height: 6)
                    Circle()
                        .fill(Color.evrryPrimaryBlue.opacity(0.5))
                        .frame(width: 6, height: 6)
                    Circle()
                        .fill(Color.evrryPrimaryBlue.opacity(0.25))
                        .frame(width: 6, height: 6)
                }
            }
            .opacity(opacity)
            .scaleEffect(scale)
        }
        .onAppear {
            withAnimation(.easeOut(duration: 0.45)) {
                opacity = 1.0
                scale = 1.0
            }
            
            // Hold and auto-route at 1.5s (1500ms)
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.15) {
                withAnimation(.easeIn(duration: 0.35)) {
                    opacity = 0.0
                }
            }
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                viewModel.finishSplash(hasSeenOnboarding: hasSeenOnboarding)
            }
        }
    }
}
