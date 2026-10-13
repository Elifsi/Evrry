package com.elifsi.evrry.partner.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sin

/**
 * High-Priority Audio Chime System for Restaurant Kitchen Partner
 * Platform: Android (Kotlin)
 * Elifsi Technologies Private Limited
 *
 * Implements persistent, looping high-decibel audio alarms for incoming kitchen orders.
 * Configured with AudioAttributes.USAGE_ALARM to pierce silent / do-not-disturb modes,
 * grounded in enatega-multivendor-store sound.context.tsx.
 */
@Singleton
class KitchenAudioChimeService @Inject constructor() {

    private val _isRinging = MutableStateFlow(false)
    val isRinging: StateFlow<Boolean> = _isRinging.asStateFlow()

    private val _activeRingOrderId = MutableStateFlow<String?>(null)
    val activeRingOrderId: StateFlow<String?> = _activeRingOrderId.asStateFlow()

    private var chimeJob: Job? = null
    private var isSilencedForOrder: String? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Start high-priority chime loop for an incoming kitchen ticket
     */
    fun startIncomingOrderChime(orderId: String) {
        if (isSilencedForOrder == orderId) return
        if (_isRinging.value && _activeRingOrderId.value == orderId) return

        stopChime()
        _isRinging.value = true
        _activeRingOrderId.value = orderId

        chimeJob = scope.launch {
            try {
                playLoopingChime()
            } catch (e: CancellationException) {
                // Cooperative cancellation
            } catch (e: Exception) {
                // Fallback safe
            } finally {
                _isRinging.value = false
            }
        }
    }

    /**
     * Silence the current ringing alarm without accepting the order yet
     */
    fun silenceRing() {
        val current = _activeRingOrderId.value
        if (current != null) {
            isSilencedForOrder = current
        }
        stopChime()
    }

    /**
     * Stop chime completely (called when order is accepted, rejected, or cleared)
     */
    fun stopChime() {
        chimeJob?.cancel()
        chimeJob = null
        _isRinging.value = false
        _activeRingOrderId.value = null
    }

    /**
     * Generates and plays alternating dual-frequency alert chime (880Hz / 1320Hz)
     * using AudioTrack with AudioAttributes.USAGE_ALARM
     */
    private suspend fun playLoopingChime() = withContext(Dispatchers.IO) {
        val sampleRate = 44100
        val durationMs = 350
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()

        // Tone 1 (High: 1320 Hz)
        val bufferHigh = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * i / (sampleRate / 1320.0)
            bufferHigh[i] = (sin(angle) * Short.MAX_VALUE * 0.85).toInt().toShort()
        }

        // Tone 2 (Mid: 880 Hz)
        val bufferMid = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * i / (sampleRate / 880.0)
            bufferMid[i] = (sin(angle) * Short.MAX_VALUE * 0.90).toInt().toShort()
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setSampleRate(sampleRate)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()

        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(numSamples * 2)

        var audioTrack: AudioTrack? = null
        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack.play()

            while (coroutineContext.isActive) {
                // Beep High
                audioTrack.write(bufferHigh, 0, bufferHigh.size)
                delay(80)

                // Beep Mid
                audioTrack.write(bufferMid, 0, bufferMid.size)
                delay(80)

                // Beep High again
                audioTrack.write(bufferHigh, 0, bufferHigh.size)

                // Rest pause between alert bursts (repeats every 2 seconds)
                delay(1500)
            }
        } finally {
            try {
                audioTrack?.stop()
                audioTrack?.release()
            } catch (e: Exception) {
                // no-op
            }
        }
    }
}
