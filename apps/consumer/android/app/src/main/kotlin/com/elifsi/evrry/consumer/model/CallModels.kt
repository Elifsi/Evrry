package com.elifsi.evrry.consumer.model

import kotlinx.serialization.Serializable

/**
 * Voice & Video Call Domain Models & Call Logs
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */

enum class CallType {
    AUDIO,
    VIDEO
}

enum class CallState {
    CONNECTING,
    RINGING,
    CONNECTED,
    ENDED
}

enum class CallDirection {
    INCOMING,
    OUTGOING,
    MISSED
}

enum class CallLogStatus {
    COMPLETED,
    MISSED,
    REJECTED,
    FAILED,
    CANCELLED
}

@Serializable
data class ActiveCallSession(
    val callId: String,
    val chatId: String,
    val calleeName: String,
    val calleeAvatar: String? = null,
    val calleePhone: String? = null,
    val callType: CallType = CallType.AUDIO,
    val state: CallState = CallState.CONNECTING,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isVideoEnabled: Boolean = true,
    val isFrontCamera: Boolean = true
)

@Serializable
data class CallLogItem(
    val id: String,
    val chatId: String? = null,
    val contactName: String,
    val contactAvatar: String? = null,
    val contactPhone: String? = null,
    val callType: CallType = CallType.AUDIO,
    val direction: CallDirection = CallDirection.OUTGOING,
    val status: CallLogStatus = CallLogStatus.COMPLETED,
    val durationSeconds: Int = 0,
    val timestampFormatted: String,
    val isDriverOrMerchant: Boolean = false,
    val subtitle: String? = null
)
