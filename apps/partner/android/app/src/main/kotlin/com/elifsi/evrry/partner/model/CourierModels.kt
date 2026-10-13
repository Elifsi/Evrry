package com.elifsi.evrry.partner.model

import kotlinx.serialization.Serializable

/**
 * Domain Models for Food & Grocery Delivery Courier (Partner Android)
 * Platform: Android (Kotlin + Jetpack Compose)
 * Elifsi Technologies Private Limited
 */

enum class CourierOrderStatus(val code: String, val label: String) {
    READY_FOR_PICKUP("ready_for_pickup", "Ready for Pickup"),
    DISPATCHED("dispatched", "Out for Delivery"),
    DELIVERED("delivered", "Delivered"),
    CANCELLED("cancelled", "Cancelled");

    companion object {
        fun fromCode(code: String): CourierOrderStatus {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: READY_FOR_PICKUP
        }
    }
}

enum class CourierDeliveryStage {
    STAGE_1_TO_STORE,      // Navigating to Restaurant / Supermarket
    STAGE_2_TO_CONSUMER,   // Navigating to Customer address
    COMPLETED              // Handoff verified & delivered
}

enum class CourierChatTab {
    STORE,      // Chat with Kitchen / Supermarket staff
    CUSTOMER    // Chat with Delivery Consumer
}

enum class CallTarget {
    STORE,
    CUSTOMER
}

@Serializable
data class CourierOrderItem(
    val name: String,
    val quantity: Int,
    val unitPriceRs: Double,
    val note: String? = null
)

@Serializable
data class CourierOrder(
    val id: String,
    val orderNo: Long,
    val consumerId: String,
    val consumerName: String,
    val consumerPhone: String,
    val storeId: String,
    val storeName: String,
    val storeCategory: String, // "food" vs "grocery"
    val storeAddress: String,
    val storeLat: Double,
    val storeLng: Double,
    val storePhone: String,
    val deliveryAddress: String,
    val deliveryLat: Double,
    val deliveryLng: Double,
    val status: CourierOrderStatus = CourierOrderStatus.READY_FOR_PICKUP,
    val paymentMethod: String = "cod", // "cod", "esewa", "fonepay_qr", "prepaid"
    val totalPaisa: Long = 48000L,
    val estimatedEarningRs: Double = 185.0,
    val deliveryDistanceM: Int = 2400,
    val pickupDistanceM: Int = 1100,
    val items: List<CourierOrderItem> = emptyList(),
    val customerNote: String? = null,
    val handoffCode: String = "4821", // 4-digit handoff OTP
    val isClaimedByMe: Boolean = false,
    val claimedAt: Long? = null
) {
    val totalRs: Double get() = totalPaisa / 100.0
    val isCod: Boolean get() = paymentMethod.equals("cod", ignoreCase = true)
    val isGrocery: Boolean get() = storeCategory.equals("grocery", ignoreCase = true) || storeCategory.equals("mart", ignoreCase = true)
    val totalDistanceKm: Double get() = (pickupDistanceM + deliveryDistanceM) / 1000.0
    val itemsSummary: String
        get() = if (items.isNotEmpty()) {
            items.joinToString(", ") { "${it.name} x${it.quantity}" }
        } else {
            "Order #${orderNo}"
        }
}

@Serializable
data class CourierLedgerEntry(
    val id: String,
    val orderId: String,
    val orderNo: Long,
    val amountRs: Double,
    val type: String, // "COD_COLLECTED", "DELIVERY_FEE_EARNED", "INSTANT_PAYOUT"
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class CourierChatMessage(
    val id: String,
    val orderId: String,
    val senderRole: String, // "courier", "store", "customer"
    val senderName: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true
)
