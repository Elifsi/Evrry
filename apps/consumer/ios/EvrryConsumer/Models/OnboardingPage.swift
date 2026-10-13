//
//  OnboardingPage.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Data Model for Dynamic Onboarding Walkthrough Screens (DoorDash-grade)
//

import SwiftUI

public struct OnboardingPage: Identifiable, Equatable {
    public let id: String
    public let badge: String
    public let title: String
    public let subtitle: String
    public let accentColor: Color
    public let backgroundColor: Color
    public let systemImageName: String
    public let highlights: [String]
    public let previewMetric: String
    public let previewMetricLabel: String

    public init(
        id: String,
        badge: String,
        title: String,
        subtitle: String,
        accentColor: Color,
        backgroundColor: Color,
        systemImageName: String,
        highlights: [String],
        previewMetric: String,
        previewMetricLabel: String
    ) {
        self.id = id
        self.badge = badge
        self.title = title
        self.subtitle = subtitle
        self.accentColor = accentColor
        self.backgroundColor = backgroundColor
        self.systemImageName = systemImageName
        self.highlights = highlights
        self.previewMetric = previewMetric
        self.previewMetricLabel = previewMetricLabel
    }
}

public struct OnboardingRepository {
    public static let allPages: [OnboardingPage] = [
        OnboardingPage(
            id: "food",
            badge: "FOOD & DINING",
            title: "Hot & Fresh Meals, Delivered Fast",
            subtitle: "From sizzling Newari delicacies and local momos to gourmet dining from Kathmandu & Pokhara's finest kitchens.",
            accentColor: Color(red: 239/255, green: 68/255, blue: 68/255), // Coral Red
            backgroundColor: Color(red: 254/255, green: 242/255, blue: 242/255),
            systemImageName: "fork.knife",
            highlights: ["450+ Verified Restaurants", "Live GPS Rider Tracking", "Zero Hidden Service Fees"],
            previewMetric: "28 min",
            previewMetricLabel: "Avg. Delivery Time"
        ),
        OnboardingPage(
            id: "grocery",
            badge: "QUICK COMMERCE",
            title: "Groceries & Daily Essentials in 15 Mins",
            subtitle: "Fresh organic vegetables, farm dairy, pantry staples, and pharmacy essentials packed with temperature control.",
            accentColor: Color(red: 16/255, green: 185/255, blue: 129/255), // Emerald Green
            backgroundColor: Color(red: 236/255, green: 253/255, blue: 245/255),
            systemImageName: "cart.fill",
            highlights: ["15-Min Express Dispatch", "Farm-Fresh Guarantee", "Free Delivery on First 3 Orders"],
            previewMetric: "15 min",
            previewMetricLabel: "Lightning Dispatch"
        ),
        OnboardingPage(
            id: "rides",
            badge: "MOBILITY & RENTALS",
            title: "Fast & Safe Rides Across Nepal",
            subtitle: "Bikes for beating city traffic, comfortable cabs for family trips, and chauffeur rentals for long-distance escapes.",
            accentColor: Color(red: 37/255, green: 99/255, blue: 235/255), // Royal Blue
            backgroundColor: Color(red: 239/255, green: 246/255, blue: 255/255),
            systemImageName: "car.fill",
            highlights: ["Transparent Upfront Fares", "24/7 In-Ride SOS Security", "Cash, eSewa & Khalti Accepted"],
            previewMetric: "3 min",
            previewMetricLabel: "Pickup ETA"
        ),
        OnboardingPage(
            id: "chat",
            badge: "END-TO-END ENCRYPTED",
            title: "Private Chat, HD Calls & Money Sharing",
            subtitle: "Signal-grade Double Ratchet encrypted messaging, HD voice & video calls, order coordination, and instant bill split.",
            accentColor: Color(red: 139/255, green: 92/255, blue: 246/255), // Purple
            backgroundColor: Color(red: 245/255, green: 243/255, blue: 255/255),
            systemImageName: "message.fill",
            highlights: ["Zero-Knowledge E2EE", "Crystal HD Audio/Video", "Split Bills & Share Live Rides"],
            previewMetric: "100%",
            previewMetricLabel: "Encrypted & Secure"
        ),
        OnboardingPage(
            id: "stays",
            badge: "HOSPITALITY & STAYS",
            title: "Boutique Stays & Vehicle Rentals",
            subtitle: "Curated heritage stays in Patan, lakeside resorts in Pokhara, mountain lodges, and self-drive vehicle hire.",
            accentColor: Color(red: 245/255, green: 158/255, blue: 11/255), // Warm Amber
            backgroundColor: Color(red: 255/255, green: 251/255, blue: 235/255),
            systemImageName: "bed.double.fill",
            highlights: ["Verified Host Guarantee", "Instant Digital Check-in", "Exclusive Loyalty Rewards"],
            previewMetric: "4.9★",
            previewMetricLabel: "Guest Satisfaction"
        )
    ]
    
    public static var defaultPages: [OnboardingPage] {
        allPages.filter { $0.id != "stays" }
    }
}
