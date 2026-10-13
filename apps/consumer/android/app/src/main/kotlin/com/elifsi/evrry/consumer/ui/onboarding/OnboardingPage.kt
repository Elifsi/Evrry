package com.elifsi.evrry.consumer.ui.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data Model for Dynamic Onboarding Walkthrough Screens
 * DoorDash / Super-App Grade visual structure
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
data class OnboardingPage(
    val id: String,
    val badge: String,
    val title: String,
    val subtitle: String,
    val accentColor: Color,
    val backgroundColor: Color,
    val icon: ImageVector,
    val highlights: List<String>,
    val previewMetric: String,
    val previewMetricLabel: String
)

object OnboardingRepository {
    val allPages = listOf(
        OnboardingPage(
            id = "food",
            badge = "FOOD & DINING",
            title = "Hot & Fresh Meals, Delivered Fast",
            subtitle = "From piping hot Newari delicacies and local momos to gourmet dining from Kathmandu & Pokhara's finest kitchens.",
            accentColor = Color(0xFFEF4444), // Coral Red
            backgroundColor = Color(0xFFFEF2F2),
            icon = Icons.Rounded.Restaurant,
            highlights = listOf("450+ Verified Restaurants", "Live GPS Rider Tracking", "Zero Hidden Service Fees"),
            previewMetric = "28 min",
            previewMetricLabel = "Avg. Delivery Time"
        ),
        OnboardingPage(
            id = "grocery",
            badge = "QUICK COMMERCE",
            title = "Groceries & Daily Essentials in 15 Mins",
            subtitle = "Fresh organic vegetables, farm dairy, pantry staples, and pharmacy essentials packed with temperature control.",
            accentColor = Color(0xFF10B981), // Emerald Green
            backgroundColor = Color(0xFFECFDF5),
            icon = Icons.Rounded.LocalGroceryStore,
            highlights = listOf("15-Min Express Dispatch", "Farm-Fresh Guarantee", "Free Delivery on First 3 Orders"),
            previewMetric = "15 min",
            previewMetricLabel = "Lightning Dispatch"
        ),
        OnboardingPage(
            id = "rides",
            badge = "MOBILITY & RENTALS",
            title = "Fast & Safe Rides Across Nepal",
            subtitle = "Bikes for beating city traffic, comfortable cabs for family trips, and chauffeur rentals for long-distance escapes.",
            accentColor = Color(0xFF2563EB), // Royal Blue
            backgroundColor = Color(0xFFEFF6FF),
            icon = Icons.Rounded.DirectionsCar,
            highlights = listOf("Transparent Upfront Fares", "24/7 In-Ride SOS Security", "Cash, eSewa & Khalti Accepted"),
            previewMetric = "3 min",
            previewMetricLabel = "Pickup ETA"
        ),
        OnboardingPage(
            id = "chat",
            badge = "END-TO-END ENCRYPTED",
            title = "Private Chat, HD Calls & Money Sharing",
            subtitle = "Signal-grade Double Ratchet encrypted messaging, HD voice & video calls, order coordination, and instant bill split.",
            accentColor = Color(0xFF8B5CF6), // Purple
            backgroundColor = Color(0xFFF5F3FF),
            icon = Icons.Rounded.Chat,
            highlights = listOf("Zero-Knowledge E2EE", "Crystal HD Audio/Video", "Split Bills & Share Live Rides"),
            previewMetric = "100%",
            previewMetricLabel = "Encrypted & Secure"
        ),
        OnboardingPage(
            id = "stays",
            badge = "HOSPITALITY & STAYS",
            title = "Boutique Stays & Vehicle Rentals",
            subtitle = "Curated heritage stays in Patan, lakeside resorts in Pokhara, mountain lodges, and self-drive vehicle hire.",
            accentColor = Color(0xFFF59E0B), // Warm Amber
            backgroundColor = Color(0xFFFFFBEB),
            icon = Icons.Rounded.Hotel,
            highlights = listOf("Verified Host Guarantee", "Instant Digital Check-in", "Exclusive Loyalty Rewards"),
            previewMetric = "4.9★",
            previewMetricLabel = "Guest Satisfaction"
        )
    )

    val defaultPages = allPages.filter { it.id != "stays" }
}
