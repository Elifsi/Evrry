package com.elifsi.evrry.consumer.model

/**
 * 4 AI Voice Persona Models for Evrry Super App Concierge
 * (Ellie, Rony, Jenny, Suka)
 * Elifsi Technologies Private Limited
 */

data class VoiceAgentModel(
    val id: String,
    val name: String,
    val badge: String,
    val description: String,
    val emoji: String = "⚡",
    val gender: String = "Female",
    val colorHex: Long = 0xFF005EFF
)

object VoiceAgentRegistry {
    val ellie = VoiceAgentModel(
        id = "ellie",
        name = "Ellie",
        badge = "Everyday Guide",
        description = "Youthful, energetic & quick — food orders, rides & daily errands in Kathmandu",
        emoji = "⚡",
        gender = "Female",
        colorHex = 0xFF005EFF
    )

    val rony = VoiceAgentModel(
        id = "rony",
        name = "Rony",
        badge = "Executive Concierge",
        description = "Deep baritone, authoritative & polite — fare negotiation & corporate coordination",
        emoji = "👔",
        gender = "Male",
        colorHex = 0xFF4F46E5
    )

    val jenny = VoiceAgentModel(
        id = "jenny",
        name = "Jenny",
        badge = "Grocery Companion",
        description = "Warm, sweet & hospitable — fresh groceries, kitchen essentials & family care",
        emoji = "🌸",
        gender = "Female",
        colorHex = 0xFFEC4899
    )

    val suka = VoiceAgentModel(
        id = "suka",
        name = "Suka",
        badge = "Evening Calmer",
        description = "Calm, soothing & deeply patient — late night rides, comfort food & mindful unwinding",
        emoji = "🌙",
        gender = "Male",
        colorHex = 0xFF0D9488
    )

    val allModels = listOf(ellie, rony, jenny, suka)

    // Backward compatibility aliases
    val ellieMini = ellie
    val elliePro = rony

    fun getById(id: String?): VoiceAgentModel {
        return when (id?.lowercase()?.trim()) {
            "rony" -> rony
            "jenny" -> jenny
            "suka" -> suka
            "ellie_pro", "pro" -> rony
            else -> ellie
        }
    }
}

