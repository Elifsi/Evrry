package com.elifsi.evrry.consumer.location

import kotlinx.serialization.Serializable

/**
 * Reusable Geospatial & Address Models
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 *
 * Coordinates are always the single source of truth for all geographic calculations.
 */

@Serializable
data class LocationPoint(
    val label: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isCurrentLocation: Boolean = false
) {
    val displayTitle: String get() = label.ifBlank { address.substringBefore(",") }
}

enum class AddressType(val title: String, val emoji: String) {
    HOME("Home", "🏠"),
    WORK("Office / Work", "💼"),
    OTHER("Saved Place", "📍")
}

@Serializable
data class SavedAddress(
    val id: String,
    val label: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val type: AddressType = AddressType.OTHER,
    val isDefault: Boolean = false
) {
    fun toLocationPoint(): LocationPoint = LocationPoint(
        label = label,
        address = address,
        latitude = latitude,
        longitude = longitude,
        isCurrentLocation = false
    )
}
