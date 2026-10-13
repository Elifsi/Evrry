package com.elifsi.evrry.consumer.model

import kotlinx.serialization.Serializable

/**
 * Authentication and User Session Domain Models
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */

enum class OtpChannel(val label: String, val badge: String) {
    WHATSAPP("WhatsApp", "Instant · Free"),
    SMS("Domestic SMS", "Backup")
}

@Serializable
data class UserProfile(
    val id: String,
    val phone: String,
    val fullName: String = "Rahul Sharma",
    val username: String = "rahul_evrry",
    val avatarUrl: String? = null,
    val bioStatus: String = "Available on Evrry",
    val loyaltyPoints: Int = 420,
    val kycVerified: Boolean = true,
    val allowPhoneDiscovery: Boolean = false,
    val readReceiptsEnabled: Boolean = true,
    val lastSeenPrivacy: String = "Everyone" // "Everyone", "Connections", "Nobody"
)

sealed class AuthScreenState {
    object Splash : AuthScreenState()
    object Onboarding : AuthScreenState()
    object Idle : AuthScreenState()
    data class OtpRequested(
        val phone: String,
        val channel: OtpChannel,
        val retryAfterSeconds: Int = 45,
        val mockOtp: String? = null
    ) : AuthScreenState()
    data class Authenticated(val user: UserProfile) : AuthScreenState()
    data class Error(val message: String) : AuthScreenState()
}
