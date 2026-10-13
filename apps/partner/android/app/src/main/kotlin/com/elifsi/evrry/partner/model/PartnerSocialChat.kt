package com.elifsi.evrry.partner.model

import kotlinx.serialization.Serializable

/**
 * Realtime Driver-Passenger Chat & Calling Models
 * Platform: Evrry Super App (Partner Android)
 * Elifsi Technologies Private Limited
 */

enum class MessageDeliveryStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

@Serializable
data class PartnerSocialMessage(
    val id: String = "msg_${System.currentTimeMillis()}",
    val rideId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromDriver: Boolean = true,
    val status: MessageDeliveryStatus = MessageDeliveryStatus.SENT
)

enum class CallType {
    VOIP_IN_APP, // WebRTC voice call
    DIRECT_SIM   // Native carrier phone dialer
}
