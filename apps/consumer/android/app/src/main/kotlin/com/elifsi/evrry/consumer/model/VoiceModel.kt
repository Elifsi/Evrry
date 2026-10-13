package com.elifsi.evrry.consumer.model

/**
 * AI Models for Evrry Voice Concierge (Ellie)
 * Minimalist ChatGPT-style model switcher (Ellie Mini & Ellie Pro)
 * Elifsi Technologies Private Limited
 */

data class VoiceAgentModel(
    val id: String,
    val name: String,
    val badge: String,
    val description: String
)

object VoiceAgentRegistry {
    val ellieMini = VoiceAgentModel(
        id = "ellie_mini",
        name = "Ellie Mini",
        badge = "Fast · Everyday",
        description = "Lightweight, ultra-responsive model for instant food orders, rides, and daily tasks"
    )

    val elliePro = VoiceAgentModel(
        id = "ellie_pro",
        name = "Ellie Pro",
        badge = "Deep Reasoning",
        description = "Advanced model for multi-stop trip planning, custom inquiries, and complex itineraries"
    )

    val allModels = listOf(ellieMini, elliePro)

    fun getById(id: String?): VoiceAgentModel {
        return when (id?.lowercase()?.trim()) {
            "ellie_pro", "pro" -> elliePro
            else -> ellieMini
        }
    }
}
