//
//  OnboardingView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Dynamic DoorDash-Grade Onboarding Walkthrough (iOS SwiftUI)
//

import SwiftUI

public struct OnboardingView: View {
    @ObservedObject public var viewModel: AuthViewModel
    public var pages: [OnboardingPage] = OnboardingRepository.defaultPages
    public var onComplete: () -> Void = {}
    
    @State private var currentIndex: Int = 0
    
    public init(
        viewModel: AuthViewModel,
        pages: [OnboardingPage] = OnboardingRepository.defaultPages,
        onComplete: @escaping () -> Void = {}
    ) {
        self.viewModel = viewModel
        self.pages = pages
        self.onComplete = onComplete
    }
    
    private var currentPage: OnboardingPage {
        pages[safe: currentIndex] ?? pages[0]
    }
    
    public var body: some View {
        ZStack {
            currentPage.backgroundColor
                .ignoresSafeArea()
                .animation(.easeInOut(duration: 0.35), value: currentIndex)
            
            VStack(spacing: 0) {
                // TOP HEADER: Brand + Skip
                HStack {
                    HStack(spacing: 8) {
                        Text("e")
                            .font(.system(size: 20, weight: .bold))
                            .foregroundColor(.white)
                            .frame(width: 32, height: 32)
                            .background(Color.evrryPrimaryBlue)
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                        
                        Text("evrry")
                            .font(.system(size: 22, weight: .black))
                            .foregroundColor(.evrryDarkCharcoal)
                    }
                    
                    Spacer()
                    
                    Button(action: {
                        onComplete()
                        viewModel.finishOnboarding()
                    }) {
                        Text("Skip")
                            .font(.system(size: 14, weight: .semibold))
                            .foregroundColor(.evrryMutedText)
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                    }
                }
                .padding(.horizontal, 24)
                .padding(.top, 12)
                
                // CENTER CAROUSEL (TabView with PageTabViewStyle)
                TabView(selection: $currentIndex) {
                    ForEach(Array(pages.enumerated()), id: \.element.id) { index, page in
                        VStack(spacing: 0) {
                            Spacer()
                            
                            // Visual Card Artwork
                            ZStack {
                                RoundedRectangle(cornerRadius: 32, style: .continuous)
                                    .fill(
                                        LinearGradient(
                                            colors: [Color.white, page.backgroundColor],
                                            startPoint: .topLeading,
                                            endPoint: .bottomTrailing
                                        )
                                    )
                                    .frame(width: 240, height: 240)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 32, style: .continuous)
                                            .stroke(page.accentColor.opacity(0.2), lineWidth: 1.5)
                                    )
                                    .shadow(color: page.accentColor.opacity(0.22), radius: 14, x: 0, y: 8)
                                
                                // Central Icon Hero
                                RoundedRectangle(cornerRadius: 26, style: .continuous)
                                    .fill(page.accentColor.opacity(0.12))
                                    .frame(width: 96, height: 96)
                                    .overlay(
                                        Image(systemName: page.systemImageName)
                                            .font(.system(size: 44, weight: .semibold))
                                            .foregroundColor(page.accentColor)
                                    )
                                
                                // Floating Live Metric Badge
                                VStack {
                                    Spacer()
                                    HStack(spacing: 6) {
                                        Circle()
                                            .fill(page.accentColor)
                                            .frame(width: 8, height: 8)
                                        Text("\(page.previewMetric) · \(page.previewMetricLabel)")
                                            .font(.system(size: 11, weight: .bold))
                                            .foregroundColor(.evrryDarkCharcoal)
                                    }
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 6)
                                    .background(Color.white)
                                    .clipShape(Capsule())
                                    .shadow(color: Color.black.opacity(0.08), radius: 6, x: 0, y: 3)
                                    .offset(y: -14)
                                }
                                .frame(width: 240, height: 240)
                            }
                            
                            Spacer().frame(height: 32)
                            
                            // Badge Pill
                            Text(page.badge)
                                .font(.system(size: 11, weight: .bold))
                                .tracking(1)
                                .foregroundColor(page.accentColor)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 5)
                                .background(page.accentColor.opacity(0.12))
                                .clipShape(Capsule())
                            
                            Spacer().frame(height: 14)
                            
                            // Title
                            Text(page.title)
                                .font(.system(size: 22, weight: .heavy))
                                .foregroundColor(.evrryDarkCharcoal)
                                .multilineTextAlignment(.center)
                                .lineSpacing(2)
                                .padding(.horizontal, 20)
                            
                            Spacer().frame(height: 10)
                            
                            // Subtitle
                            Text(page.subtitle)
                                .font(.system(size: 13))
                                .foregroundColor(.evrryMutedText)
                                .multilineTextAlignment(.center)
                                .lineSpacing(3)
                                .padding(.horizontal, 24)
                            
                            Spacer().frame(height: 18)
                            
                            // Highlight Badges
                            HStack(spacing: 8) {
                                ForEach(Array(page.highlights.prefix(2)), id: \.self) { highlight in
                                    HStack(spacing: 4) {
                                        Image(systemName: "checkmark.circle.fill")
                                            .font(.system(size: 12))
                                            .foregroundColor(page.accentColor)
                                        Text(highlight)
                                            .font(.system(size: 11, weight: .medium))
                                            .foregroundColor(.evrryDarkCharcoal)
                                    }
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 5)
                                    .background(Color.white.opacity(0.85))
                                    .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 10, style: .continuous)
                                            .stroke(page.accentColor.opacity(0.2), lineWidth: 1)
                                    )
                                }
                            }
                            
                            Spacer()
                        }
                        .tag(index)
                    }
                }
                .tabViewStyle(.page(indexDisplayMode: .never))
                
                // BOTTOM CONTROLS: Dot Indicator + CTA
                VStack(spacing: 20) {
                    // Expanding Pill Indicator
                    HStack(spacing: 6) {
                        ForEach(0..<pages.count, id: \.self) { index in
                            let isSelected = currentIndex == index
                            RoundedRectangle(cornerRadius: 4)
                                .fill(isSelected ? currentPage.accentColor : Color(red: 203/255, green: 213/255, blue: 225/255))
                                .frame(width: isSelected ? 24 : 8, height: 8)
                                .animation(.spring(response: 0.3), value: currentIndex)
                                .onTapGesture {
                                    withAnimation { currentIndex = index }
                                }
                        }
                    }
                    
                    // Action CTA
                    let isLast = currentIndex == pages.count - 1
                    Button(action: {
                        if isLast {
                            onComplete()
                            viewModel.finishOnboarding()
                        } else {
                            withAnimation { currentIndex += 1 }
                        }
                    }) {
                        HStack(spacing: 8) {
                            Text(isLast ? "Get Started with Evrry" : "Continue")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.white)
                            
                            Image(systemName: "arrow.right")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.white)
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 54)
                        .background(isLast ? Color.evrryPrimaryBlue : currentPage.accentColor)
                        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                    }
                }
                .padding(.horizontal, 24)
                .padding(.bottom, 24)
            }
        }
    }
}

private extension Collection {
    subscript(safe index: Index) -> Element? {
        indices.contains(index) ? self[index] : nil
    }
}
