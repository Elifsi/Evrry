package com.elifsi.evrry.consumer.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elifsi.evrry.consumer.model.ActiveCallSession
import com.elifsi.evrry.consumer.model.CallDirection
import com.elifsi.evrry.consumer.model.CallLogItem
import com.elifsi.evrry.consumer.model.CallLogStatus
import com.elifsi.evrry.consumer.model.CallState
import com.elifsi.evrry.consumer.model.CallType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Call ViewModel managing WhatsApp-grade WebRTC VoIP voice and video calls,
 * call history logs, active timers, hardware audio routing, and low-connectivity cellular fallback.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
class CallViewModel : ViewModel() {

    private val _callSession = MutableStateFlow<ActiveCallSession?>(null)
    val callSession: StateFlow<ActiveCallSession?> = _callSession.asStateFlow()

    private val _callLogs = MutableStateFlow<List<CallLogItem>>(getInitialCallLogs())
    val callLogs: StateFlow<List<CallLogItem>> = _callLogs.asStateFlow()

    private var timerJob: Job? = null

    fun startCall(
        chatId: String,
        calleeName: String,
        calleeAvatar: String? = null,
        calleePhone: String? = null,
        callType: CallType = CallType.AUDIO
    ) {
        timerJob?.cancel()

        val newSession = ActiveCallSession(
            callId = "call-" + UUID.randomUUID().toString().take(12),
            chatId = chatId,
            calleeName = calleeName,
            calleeAvatar = calleeAvatar,
            calleePhone = calleePhone,
            callType = callType,
            state = CallState.CONNECTING,
            durationSeconds = 0,
            isMuted = false,
            isSpeakerOn = callType == CallType.VIDEO, // Default speaker for video calls
            isVideoEnabled = callType == CallType.VIDEO,
            isFrontCamera = true
        )
        _callSession.value = newSession

        // Simulate WebRTC signaling & ringing sequence
        viewModelScope.launch {
            delay(800)
            _callSession.value = _callSession.value?.copy(state = CallState.RINGING)

            // Simulate counterpart answering
            delay(2200)
            if (_callSession.value != null && _callSession.value?.state != CallState.ENDED) {
                _callSession.value = _callSession.value?.copy(state = CallState.CONNECTED)
                startTimer()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _callSession.value = _callSession.value?.let { session ->
                    if (session.state == CallState.CONNECTED) {
                        session.copy(durationSeconds = session.durationSeconds + 1)
                    } else session
                }
            }
        }
    }

    fun toggleMute() {
        _callSession.value = _callSession.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleSpeaker() {
        _callSession.value = _callSession.value?.let { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun toggleVideo() {
        _callSession.value = _callSession.value?.let { it.copy(isVideoEnabled = !it.isVideoEnabled) }
    }

    fun flipCamera() {
        _callSession.value = _callSession.value?.let { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun endCall() {
        timerJob?.cancel()
        val current = _callSession.value
        if (current != null) {
            val isAnswered = current.durationSeconds > 0
            val logItem = CallLogItem(
                id = UUID.randomUUID().toString(),
                chatId = current.chatId,
                contactName = current.calleeName,
                contactAvatar = current.calleeAvatar,
                contactPhone = current.calleePhone,
                callType = current.callType,
                direction = if (isAnswered) CallDirection.OUTGOING else CallDirection.MISSED,
                status = if (isAnswered) CallLogStatus.COMPLETED else CallLogStatus.MISSED,
                durationSeconds = current.durationSeconds,
                timestampFormatted = "Just now",
                subtitle = if (isAnswered) "${formatDuration(current.durationSeconds)} • Outgoing" else "Cancelled / Missed"
            )
            _callLogs.value = listOf(logItem) + _callLogs.value
        }

        _callSession.value = _callSession.value?.copy(state = CallState.ENDED)
        viewModelScope.launch {
            delay(500)
            _callSession.value = null
        }
    }

    fun deleteCallLog(logId: String) {
        _callLogs.value = _callLogs.value.filterNot { it.id == logId }
    }

    fun clearCallLogs() {
        _callLogs.value = emptyList()
    }

    /**
     * Fallback to direct Nepal cellular GSM SIM calling if WebRTC connection is poor.
     */
    fun dialCellularFallback(context: Context) {
        val phone = _callSession.value?.calleePhone ?: "9841000001"
        endCall()
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(dialIntent)
        } catch (_: Exception) {
            // Log fallback dial error
        }
    }

    fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }

    private fun getInitialCallLogs(): List<CallLogItem> {
        return listOf(
            CallLogItem(
                id = "call-log-1",
                chatId = "chat-order-482",
                contactName = "Ramesh Thapa",
                contactPhone = "9841234567",
                callType = CallType.AUDIO,
                direction = CallDirection.OUTGOING,
                status = CallLogStatus.COMPLETED,
                durationSeconds = 195,
                timestampFormatted = "Today, 12:45 PM",
                isDriverOrMerchant = true,
                subtitle = "Delivery Captain • 3m 15s"
            ),
            CallLogItem(
                id = "call-log-2",
                chatId = "chat-direct-1",
                contactName = "Suman Sharma",
                contactPhone = "9801239876",
                callType = CallType.VIDEO,
                direction = CallDirection.INCOMING,
                status = CallLogStatus.COMPLETED,
                durationSeconds = 745,
                timestampFormatted = "Today, 10:14 AM",
                subtitle = "Video Call • 12m 25s"
            ),
            CallLogItem(
                id = "call-log-3",
                chatId = "chat-ride-901",
                contactName = "Bikash Gurung",
                contactPhone = "9851122334",
                callType = CallType.AUDIO,
                direction = CallDirection.MISSED,
                status = CallLogStatus.MISSED,
                durationSeconds = 0,
                timestampFormatted = "Yesterday, 6:30 PM",
                isDriverOrMerchant = true,
                subtitle = "Ride Captain • Missed Call"
            ),
            CallLogItem(
                id = "call-log-4",
                chatId = "chat-direct-2",
                contactName = "Pooja Adhikari",
                contactPhone = "9818445566",
                callType = CallType.AUDIO,
                direction = CallDirection.OUTGOING,
                status = CallLogStatus.COMPLETED,
                durationSeconds = 312,
                timestampFormatted = "Yesterday, 2:15 PM",
                subtitle = "Voice Call • 5m 12s"
            ),
            CallLogItem(
                id = "call-log-5",
                chatId = null,
                contactName = "Kathmandu Fresh Mart",
                contactPhone = "9802001122",
                callType = CallType.AUDIO,
                direction = CallDirection.INCOMING,
                status = CallLogStatus.COMPLETED,
                durationSeconds = 48,
                timestampFormatted = "Oct 8, 4:20 PM",
                isDriverOrMerchant = true,
                subtitle = "Store Partner • 48s"
            )
        )
    }
}
