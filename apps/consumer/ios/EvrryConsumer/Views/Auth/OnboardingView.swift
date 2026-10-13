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
    @State private var showLocationSetup: Bool = false
    @State private var showLocationPickerSheet: Bool = false
    @State private var selectedHomeLocation: SavedAddress = SavedAddress(
        id: "addr_home",
        label: "Home",
        address: "Sanepa Heights, Lalitpur",
        latitude: 27.6841,
        longitude: 85.3088,
        type: .home,
        isDefault: true
    )
    
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
            if showLocationSetup {
                locationSetupView
            } else {
                walkthroughView
            }
        }
        .sheet(isPresented: $showLocationPickerSheet) {
            LocationPickerSheetView(
                title: "Pick Home Location",
                onLocationSelected: { loc in
                    selectedHomeLocation = SavedAddress(
                        id: "addr_home",
                        label: "Home",
                        address: loc.address,
                        latitude: loc.latitude,
                        longitude: loc.longitude,
                        type: .home,
                        isDefault: true
                    )
                    showLocationPickerSheet = false
                },
                onDismiss: { showLocationPickerSheet = false }
            )
        }
    }

    private var walkthroughView: some View {
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
                        showLocationSetup = true
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
                            showLocationSetup = true
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

    private var locationSetupView: some View {
        VStack(alignment: .leading, spacing: 18) {
            // Header
            VStack(alignment: .leading, spacing: 4) {
                Text("DOORSTEP SETUP")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(.evrryPrimaryBlue)
                    .tracking(1.0)
                Text("Set Your Home Location 📍")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(.evrryDarkCharcoal)
                Text("Pick where your food, groceries, and rides should arrive by default.")
                    .font(.system(size: 13))
                    .foregroundColor(.evrryMutedText)
            }
            .padding(.top, 24)

            // Selected Card
            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Image(systemName: "house.fill")
                        .foregroundColor(.evrryPrimaryBlue)
                    Text(selectedHomeLocation.label)
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Spacer()
                    Image(systemName: "checkmark.circle.fill")
                        .foregroundColor(Color(red: 0.09, green: 0.64, blue: 0.29))
                }
                Text(selectedHomeLocation.address)
                    .font(.system(size: 13))
                    .foregroundColor(.evrryMutedText)
            }
            .padding(16)
            .background(Color.white)
            .clipShape(RoundedRectangle(cornerRadius: 16))
            .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrryPrimaryBlue.opacity(0.3), lineWidth: 1.5))

            // GPS Button
            Button(action: {
                LocationService.shared.requestPermission()
                if let loc = LocationService.shared.currentLocation {
                    selectedHomeLocation = SavedAddress(
                        id: "addr_home",
                        label: "Home",
                        address: "GPS Fix: \(String(format: "%.4f", loc.coordinate.latitude)), \(String(format: "%.4f", loc.coordinate.longitude))",
                        latitude: loc.coordinate.latitude,
                        longitude: loc.coordinate.longitude,
                        type: .home,
                        isDefault: true
                    )
                }
            }) {
                HStack(spacing: 8) {
                    Image(systemName: "location.fill")
                    Text("Use My Current Location (GPS)")
                        .font(.system(size: 14, weight: .bold))
                }
                .foregroundColor(.evrryPrimaryBlue)
                .frame(maxWidth: .infinity)
                .frame(height: 50)
                .background(Color.evrryPrimaryBlue.opacity(0.1))
                .clipShape(RoundedRectangle(cornerRadius: 14))
            }

            // Search on Map Button
            Button(action: { showLocationPickerSheet = true }) {
                HStack(spacing: 8) {
                    Image(systemName: "magnifyingglass")
                    Text("Search Area / Street on Map")
                        .font(.system(size: 14, weight: .semibold))
                }
                .foregroundColor(.evrryDarkCharcoal)
                .frame(maxWidth: .infinity)
                .frame(height: 50)
                .background(Color.white)
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1))
            }

            // Popular Neighborhoods
            Text("POPULAR NEIGHBORHOODS")
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(.evrryMutedText)
                .padding(.top, 6)

            let popular = [
                ("Sanepa Heights", "Sanepa Heights, Lalitpur", 27.6841, 85.3088),
                ("Jhamsikhel", "Jhamsikhel, Lalitpur", 27.6830, 85.3120),
                ("Durbar Marg", "Durbar Marg, Kathmandu", 27.7118, 85.3168),
                ("Thamel", "Thamel, Kathmandu", 27.7154, 85.3123),
                ("New Baneshwor", "New Baneshwor, Kathmandu", 27.6915, 85.3420),
                ("Pokhara Lakeside", "Lakeside, Pokhara", 28.2096, 83.9595)
            ]

            LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 8) {
                ForEach(popular, id: \.0) { item in
                    Button(action: {
                        selectedHomeLocation = SavedAddress(
                            id: "addr_home",
                            label: "Home",
                            address: item.1,
                            latitude: item.2,
                            longitude: item.3,
                            type: .home,
                            isDefault: true
                        )
                    }) {
                        Text(item.0)
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(selectedHomeLocation.address == item.1 ? .evrryPrimaryBlue : .evrryDarkCharcoal)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 10)
                            .background(selectedHomeLocation.address == item.1 ? Color.evrryPrimaryBlue.opacity(0.1) : Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                            .overlay(RoundedRectangle(cornerRadius: 10).stroke(selectedHomeLocation.address == item.1 ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: 1))
                    }
                }
            }

            Spacer()

            // Save Button
            Button(action: {
                SavedAddressService.shared.setHomeAddress(selectedHomeLocation)
                onComplete()
                viewModel.finishOnboarding()
            }) {
                Text("Save Home & Start Exploring")
                    .font(.system(size: 15, weight: .bold))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
                    .background(Color.evrryPrimaryBlue)
                    .clipShape(RoundedRectangle(cornerRadius: 16))
            }
            .padding(.bottom, 24)
        }
        .padding(.horizontal, 24)
        .background(Color(red: 0.98, green: 0.98, blue: 1.0).ignoresSafeArea())
    }
}

private extension Collection {
    subscript(safe index: Index) -> Element? {
        indices.contains(index) ? self[index] : nil
    }
}
