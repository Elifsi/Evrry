package com.elifsi.evrry.consumer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elifsi.evrry.consumer.model.VoiceAgentModel
import com.elifsi.evrry.consumer.model.VoiceAgentRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class VoiceState { IDLE, LISTENING, THINKING, SPEAKING }

class HomeViewModel : ViewModel() {
    private val _activeModel = MutableStateFlow<VoiceAgentModel>(VoiceAgentRegistry.ellie)
    val activeModel: StateFlow<VoiceAgentModel> = _activeModel.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _aiMode = MutableStateFlow<String>("chat") // "chat" | "assistant"
    val aiMode: StateFlow<String> = _aiMode.asStateFlow()

    private val _caption = MutableStateFlow<String>("")
    val caption: StateFlow<String> = _caption.asStateFlow()

    private val _isMuted = MutableStateFlow<Boolean>(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    fun selectModel(model: VoiceAgentModel) {
        _activeModel.value = model
        if (_messages.value.isNotEmpty()) {
            _messages.value = _messages.value + ChatMessage(
                isUser = false,
                text = "Switched to ${model.name} (${model.badge})."
            )
        }
    }

    fun setAiMode(mode: String) {
        _aiMode.value = mode
        if (mode == "assistant") {
            _voiceState.value = VoiceState.LISTENING
            _caption.value = ""
        } else {
            _voiceState.value = VoiceState.IDLE
        }
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun clearChat() {
        _messages.value = emptyList()
        _inputText.value = ""
        _caption.value = ""
        _voiceState.value = VoiceState.IDLE
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun sendMessage(text: String = _inputText.value) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val userMsg = ChatMessage(isUser = true, text = trimmed)
        _messages.value = _messages.value + userMsg
        _inputText.value = ""

        _voiceState.value = VoiceState.THINKING
        _caption.value = trimmed

        viewModelScope.launch {
            delay(800)
            _voiceState.value = VoiceState.SPEAKING
            val replyText = generateAutonomousReply(trimmed, _activeModel.value)
            _messages.value = _messages.value + ChatMessage(isUser = false, text = replyText)
            _caption.value = replyText
            delay(1500)
            _voiceState.value = VoiceState.IDLE
        }
    }

    fun toggleVoiceOrb() {
        when (_voiceState.value) {
            VoiceState.IDLE -> {
                _voiceState.value = VoiceState.LISTENING
                viewModelScope.launch {
                    delay(3000)
                    sendMessage("Momo ani bike ride book gardeu na Patan samma")
                }
            }
            VoiceState.LISTENING -> _voiceState.value = VoiceState.IDLE
            VoiceState.SPEAKING -> _voiceState.value = VoiceState.IDLE
            VoiceState.THINKING -> {}
        }
    }

    private fun generateAutonomousReply(query: String, model: VoiceAgentModel): String {
        val lower = query.lowercase()
        return when {
            "momo" in lower || "khaja" in lower ->
                "Kathmandu Momo Corner bata C-Momo (Rs 280) ready chha! Cart ma add gardiye — order confirm garum?"
            "ride" in lower || "patan" in lower || "bike" in lower ->
                "Thamel bata Patan samma Moto rider Bikash Tamang 2 min ma aaudai chha (Rs 165). OTP: 1234."
            "grocery" in lower || "apple" in lower ->
                "Jumla organic apples (Rs 240/kg) Himalayan Mart bata 15 min ma deliver hunchha!"
            else ->
                "Namaste, I can help you find restaurants, book rides across Kathmandu, or order groceries. What would you like?"
        }
    }
}
