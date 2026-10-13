package com.elifsi.evrry.partner.model

import kotlinx.serialization.Serializable

/**
 * Domain Models for Grocery Supermarket Partner Operations
 * Retail inventory, shelf SKU audit, pick-and-pack checklist, loose weighing,
 * and out-of-stock customer substitution flow.
 * Elifsi Technologies Private Limited
 */

@Serializable
data class GroceryPartnerItem(
    val id: String,
    val storeId: String = "store-bhatbhateni-pulchowk",
    val name: String,
    val nameNe: String? = null,
    val brand: String = "",
    val section: String, // "Fresh Produce", "Dairy & Bakery", "Pantry & Grains", "Snacks & Drinks", "Household"
    val pricePaisa: Long,
    val stockQuantity: Int = 20,
    val isAvailable: Boolean = true,
    val barcode: String? = null,
    val sku: String? = null,
    val unit: String = "pack", // "kg", "g", "pack", "litre", "pcs"
    val isLooseWeight: Boolean = false,
    val isPerishable: Boolean = false,
    val imageUrl: String? = null
) {
    val priceNpr: Double get() = pricePaisa / 100.0
}

@Serializable
enum class SubstitutionStatus(val label: String) {
    NONE("Original Item"),
    PROPOSED("Customer Proposal Sent"),
    APPROVED("Approved by Customer"),
    REJECTED("Customer Rejected (Refunded)")
}

@Serializable
data class GroceryOrderItem(
    val id: String,
    val orderId: String,
    val itemId: String,
    val name: String,
    val section: String,
    val unitPricePaisa: Long,
    val quantity: Int,
    val lineTotalPaisa: Long,
    val unit: String = "pack",
    val isLooseWeight: Boolean = false,
    val measuredWeight: Double? = null,
    val isPacked: Boolean = false,
    val substitutionStatus: SubstitutionStatus = SubstitutionStatus.NONE,
    val proposedSubstituteItemId: String? = null,
    val proposedSubstituteName: String? = null,
    val proposedSubstitutePriceNpr: Double? = null
) {
    val unitPriceNpr: Double get() = unitPricePaisa / 100.0
    val lineTotalNpr: Double get() = lineTotalPaisa / 100.0
}

@Serializable
data class GroceryPartnerOrder(
    val id: String,
    val orderNo: Long,
    val consumerId: String = "usr-cons-01",
    val consumerName: String = "Aayush Sharma",
    val consumerPhone: String = "+977 9841234567",
    val deliveryAddress: String = "Jhamsikhel Road, Lalitpur (Near St. Mary's)",
    val storeId: String = "store-bhatbhateni-pulchowk",
    val status: String = "preparing", // "acknowledged", "preparing", "ready_for_pickup", "dispatched", "delivered"
    val totalPaisa: Long,
    val placedAt: String = "10 mins ago",
    val customerNote: String? = "Please pack leafy greens and dairy in separate bags.",
    val riderPartnerId: String? = "rider-nepal-04",
    val riderName: String? = "Santosh KC",
    val riderPhone: String? = "+977 9813009988",
    val riderVehiclePlate: String? = "Ba 88 Pa 4321",
    val riderEtaMinutes: Int? = 4,
    val pickupBayCode: String = "Bay #3 (Front Express Counter)",
    val items: List<GroceryOrderItem> = emptyList()
) {
    val totalNpr: Double get() = totalPaisa / 100.0
    val packedItemCount: Int get() = items.count { it.isPacked || it.substitutionStatus == SubstitutionStatus.REJECTED }
    val totalItemCount: Int get() = items.size
    val isPackingComplete: Boolean get() = items.isNotEmpty() && items.all { it.isPacked || it.substitutionStatus == SubstitutionStatus.REJECTED }
}

@Serializable
data class PartnerChatMessage(
    val id: String,
    val sender: String, // "You (Supermarket)", "Courier"
    val text: String,
    val time: String
)
