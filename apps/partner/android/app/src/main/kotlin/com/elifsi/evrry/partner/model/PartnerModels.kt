package com.elifsi.evrry.partner.model

import kotlinx.serialization.Serializable

/**
 * Restaurant Kitchen Domain Models for Evrry Unified Partner Application
 * Platform: Android (Kotlin + Jetpack Compose)
 * Elifsi Technologies Private Limited
 */

@Serializable
data class RestaurantStore(
    val id: String = "store-roadhouse-pulchowk",
    val partnerId: String = "partner-roadhouse-001",
    val category: String = "food",
    val name: String = "Roadhouse Cafe (Pulchowk)",
    val isOpen: Boolean = true,
    val openingHours: String = "10:00 AM – 10:30 PM",
    val avgPrepTimeMinutes: Int = 25,
    val ratingAvg: Double = 4.8,
    val ratingCount: Int = 320,
    val addressText: String = "Ward 3, Pulchowk Marg, Lalitpur, Nepal",
    val minOrderPaisa: Long = 30000L // Rs 300
)

enum class RestaurantOrderStatus(val code: String, val label: String) {
    ACKNOWLEDGED("acknowledged", "New Order"),
    PREPARING("preparing", "In Kitchen"),
    READY_FOR_PICKUP("ready_for_pickup", "Ready for Pickup"),
    DISPATCHED("dispatched", "Out for Delivery"),
    DELIVERED("delivered", "Delivered"),
    CANCELLED("cancelled", "Cancelled"),
    REJECTED("rejected", "Rejected");

    companion object {
        fun fromCode(code: String): RestaurantOrderStatus {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: ACKNOWLEDGED
        }
    }
}

@Serializable
data class RestaurantOrderItem(
    val id: String,
    val itemId: String,
    val name: String,
    val unitPricePaisa: Long,
    val quantity: Int,
    val lineTotalPaisa: Long = unitPricePaisa * quantity,
    val customizations: List<String> = emptyList()
) {
    val unitPriceRs: Double get() = unitPricePaisa / 100.0
    val lineTotalRs: Double get() = lineTotalPaisa / 100.0
}

@Serializable
data class RestaurantOrder(
    val id: String,
    val orderNo: Long,
    val consumerId: String,
    val consumerName: String,
    val consumerPhone: String,
    val storeId: String,
    val riderPartnerId: String? = null,
    val riderName: String? = null,
    val riderPhone: String? = null,
    val riderVehicleType: String? = null, // e.g. "Honda Dio · Ba 88 Pa 4321"
    val riderDistanceMeters: Int? = null,
    val status: RestaurantOrderStatus = RestaurantOrderStatus.ACKNOWLEDGED,
    val paymentMethod: String = "Cash on Delivery", // "cod", "esewa", "fonepay_qr", "khalti"
    val isPaid: Boolean = false,
    val subtotalPaisa: Long,
    val deliveryFeePaisa: Long = 5000L,
    val platformFeePaisa: Long = 1000L,
    val taxPaisa: Long = 0L,
    val totalPaisa: Long,
    val customerNote: String? = null,
    val placedAt: Long = System.currentTimeMillis(),
    val prepTimeMinutes: Int = 25,
    val prepStartedAt: Long? = null,
    val readyAt: Long? = null,
    val items: List<RestaurantOrderItem> = emptyList(),
    val rejectionReason: String? = null
) {
    val subtotalRs: Double get() = subtotalPaisa / 100.0
    val totalRs: Double get() = totalPaisa / 100.0

    /**
     * Remaining seconds on active cooking countdown
     */
    fun remainingPrepSeconds(now: Long = System.currentTimeMillis()): Int {
        if (prepStartedAt == null || status != RestaurantOrderStatus.PREPARING) return prepTimeMinutes * 60
        val targetMillis = prepStartedAt + (prepTimeMinutes * 60 * 1000L)
        val diffSeconds = ((targetMillis - now) / 1000).toInt()
        return if (diffSeconds > 0) diffSeconds else 0
    }

    /**
     * Progress between 0.0f (just started) and 1.0f (completed/overdue)
     */
    fun prepProgress(now: Long = System.currentTimeMillis()): Float {
        if (prepStartedAt == null || status != RestaurantOrderStatus.PREPARING) return 0f
        val totalSeconds = (prepTimeMinutes * 60).toFloat()
        if (totalSeconds <= 0f) return 1f
        val elapsedSeconds = ((now - prepStartedAt) / 1000).toFloat()
        return (elapsedSeconds / totalSeconds).coerceIn(0f, 1f)
    }

    fun isOverdue(now: Long = System.currentTimeMillis()): Boolean {
        if (prepStartedAt == null || status != RestaurantOrderStatus.PREPARING) return false
        val targetMillis = prepStartedAt + (prepTimeMinutes * 60 * 1000L)
        return now > targetMillis
    }
}

@Serializable
data class CatalogItem(
    val id: String,
    val storeId: String,
    val name: String,
    val description: String? = null,
    val pricePaisa: Long,
    val section: String = "Mains",
    val isAvailable: Boolean = true,
    val tags: List<String> = emptyList()
) {
    val priceRs: Double get() = pricePaisa / 100.0
}

enum class KitchenTab(val title: String) {
    NEW_ORDERS("Incoming"),
    PREPARING("Preparing"),
    READY_FOR_PICKUP("Ready for Pickup"),
    COMPLETED("Completed")
}

@Serializable
data class ChatMessage(
    val id: String,
    val orderId: String,
    val senderRole: String, // "store", "consumer", "rider"
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isOutgoing: Boolean = true
)
