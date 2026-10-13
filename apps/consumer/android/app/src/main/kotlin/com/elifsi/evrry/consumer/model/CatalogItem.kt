package com.elifsi.evrry.consumer.model

import kotlinx.serialization.Serializable

/**
 * Multi-Vertical Catalog & Commerce Domain Models
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */

enum class ServiceVertical(
    val id: String,
    val title: String,
    val emoji: String,
    val isEnabledInRelease: Boolean = true
) {
    FOOD("food", "Food & Momo", "🥟", isEnabledInRelease = true),
    GROCERY("grocery", "Grocery Mart", "🥦", isEnabledInRelease = true),
    RIDES("rides", "Cab & Bike", "🚖", isEnabledInRelease = true),
    HOTELS("hotels", "Stays & Resorts", "🏨", isEnabledInRelease = false), // Out of scope for this release
    RENTALS("rentals", "Flats & Rooms", "🏠", isEnabledInRelease = false), // Out of scope for this release
    SERVICES("services", "Home Services", "🛠️", isEnabledInRelease = true);

    companion object {
        val activeVerticals: List<ServiceVertical>
            get() = values().filter { it.isEnabledInRelease }
    }
}

@Serializable
data class CatalogItem(
    val id: String,
    val vertical: String,
    val title: String,
    val subtitle: String,
    val priceNpr: Double,
    val originalPriceNpr: Double? = null,
    val vendorName: String,
    val vendorId: String,
    val rating: Double = 4.8,
    val etaMinutes: Int = 25,
    val imageUrl: String? = null,
    val tags: List<String> = emptyList()
)

object MockCatalogData {
    val items = listOf(
        CatalogItem(
            id = "food_momo_01",
            vertical = "food",
            title = "Steam C-Momo (Buff / Chicken)",
            subtitle = "Spicy sesame Timur achar with 10 juicy dumplings",
            priceNpr = 280.0,
            originalPriceNpr = 320.0,
            vendorName = "Kathmandu Momo Corner",
            vendorId = "vnd_momo_ktm",
            rating = 4.9,
            etaMinutes = 20,
            tags = listOf("Bestseller", "Local", "Spicy")
        ),
        CatalogItem(
            id = "food_thakali_02",
            vertical = "food",
            title = "Authentic Jimbu Thakali Thali",
            subtitle = "Mustang black lentils, local ghee, gundruk & mutton curry",
            priceNpr = 550.0,
            vendorName = "Mustang Thakali Kitchen",
            vendorId = "vnd_thakali_ktm",
            rating = 4.9,
            etaMinutes = 35,
            tags = listOf("Thakali", "Authentic")
        ),
        CatalogItem(
            id = "groc_apples_01",
            vertical = "grocery",
            title = "Jumla Organic Apples (1 Kg)",
            subtitle = "Crisp, sweet, directly harvested high-altitude organic apples",
            priceNpr = 240.0,
            vendorName = "Himalayan Fresh Mart",
            vendorId = "vnd_mart_fresh",
            rating = 4.8,
            etaMinutes = 15,
            tags = listOf("Organic", "Fresh")
        ),
        CatalogItem(
            id = "hotel_lakeview_01",
            vertical = "hotels",
            title = "Pokhara Lakeside Boutique Suite",
            subtitle = "Annapurna mountain view balcony, King bed, Breakfast included",
            priceNpr = 4500.0,
            originalPriceNpr = 5200.0,
            vendorName = "Lakeview Peace Resort",
            vendorId = "vnd_hotel_lakeside",
            rating = 4.9,
            etaMinutes = 0,
            tags = listOf("Mountain View", "Breakfast Included")
        ),
        CatalogItem(
            id = "rental_bakhundole_01",
            vertical = "rentals",
            title = "Furnished 2BHK Flat — Jhamsikhel",
            subtitle = "Lalpurja verified, 24/7 water, solar backup, balcony",
            priceNpr = 38000.0,
            vendorName = "Jhamsikhel Residency",
            vendorId = "vnd_res_jham",
            rating = 4.7,
            etaMinutes = 0,
            tags = listOf("Verified Lalpurja", "No Broker Fee")
        )
    )
}
