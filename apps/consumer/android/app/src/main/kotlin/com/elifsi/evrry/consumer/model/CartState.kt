package com.elifsi.evrry.consumer.model

import kotlinx.serialization.Serializable

/**
 * Cart, Delivery Address, and Multi-Vendor Checkout Models
 * Platform: Evrry Super App (Consumer Android)
 * Multi-Vertical Unified Commerce Architecture
 * Elifsi Technologies Private Limited
 */

enum class PaymentMethod(val id: String, val label: String, val badge: String, val feeNpr: Double) {
    ESEWA("esewa", "eSewa Direct Payment", "Nepal #1 Wallet", 0.0),
    KHALTI("khalti", "Khalti Direct Transfer", "Instant Pay", 0.0),
    FONEPAY("fonepay", "Fonepay Dynamic QR", "All Nepal Banks", 0.0),
    COD("cod", "Cash on Delivery", "Pay Rider on Arrival", 0.0)
}

@Serializable
data class DeliveryAddress(
    val id: String,
    val label: String, // "Home", "Office"
    val fullAddress: String,
    val landmark: String? = null,
    val phone: String = "+977 9812345678",
    val isDefault: Boolean = false
)

object DefaultAddresses {
    val home = DeliveryAddress(
        id = "addr-01",
        label = "Home",
        fullAddress = "Pulchowk, Ward 3, Lalitpur",
        landmark = "Near Labim Mall",
        phone = "+977 9812345678",
        isDefault = true
    )
    val office = DeliveryAddress(
        id = "addr-02",
        label = "Office",
        fullAddress = "Thamel Marg, Kathmandu",
        landmark = "Near Garden of Dreams",
        phone = "+977 9812345678",
        isDefault = false
    )
    val list = listOf(home, office)
}

@Serializable
data class CartItem(
    val item: CatalogItem,
    val quantity: Int = 1,
    val selectedOptions: List<String> = emptyList(),
    val specialNotes: String = ""
) {
    val totalNpr: Double get() = item.priceNpr * quantity
}

enum class OrderFulfillmentType {
    DELIVERY,
    PICKUP
}

data class CartState(
    val items: List<CartItem> = emptyList(),
    val fulfillmentType: OrderFulfillmentType = OrderFulfillmentType.DELIVERY,
    val deliveryFeeNpr: Double = 50.0,
    val platformFeeNpr: Double = 10.0,
    val discountNpr: Double = 0.0,
    val specialInstructions: String = "",
    val deliveryAddress: DeliveryAddress = DefaultAddresses.home
) {
    val subtotalNpr: Double get() = items.sumOf { it.totalNpr }
    val isFreeDeliveryEligible: Boolean get() = subtotalNpr >= 1000.0
    val amountNeededForFreeDelivery: Double get() = maxOf(0.0, 1000.0 - subtotalNpr)
    val freeDeliveryProgress: Float get() = (subtotalNpr / 1000.0).toFloat().coerceIn(0f, 1f)
    val effectiveDeliveryFeeNpr: Double get() = if (items.isEmpty() || fulfillmentType == OrderFulfillmentType.PICKUP || isFreeDeliveryEligible) 0.0 else deliveryFeeNpr
    val effectivePlatformFeeNpr: Double get() = if (items.isEmpty()) 0.0 else platformFeeNpr
    val totalNpr: Double get() = if (items.isEmpty()) 0.0 else maxOf(0.0, subtotalNpr + effectiveDeliveryFeeNpr + effectivePlatformFeeNpr - discountNpr)
    val subtotal: Double get() = subtotalNpr
    val finalPayableNpr: Double get() = totalNpr
    val vendorName: String get() = activeGroceryVendorName ?: activeVendorName ?: "Store"
    val itemCount: Int get() = items.sumOf { it.quantity }
    val foodItems: List<CartItem> get() = items.filter { it.item.vertical == "food" }
    val groceryItems: List<CartItem> get() = items.filter { it.item.vertical == "grocery" }
    val activeVendorId: String? get() = items.firstOrNull()?.item?.vendorId
    val activeVendorName: String? get() = items.firstOrNull()?.item?.vendorName
    val activeFoodVendorId: String? get() = foodItems.firstOrNull()?.item?.vendorId
    val activeFoodVendorName: String? get() = foodItems.firstOrNull()?.item?.vendorName
    val activeGroceryVendorId: String? get() = groceryItems.firstOrNull()?.item?.vendorId
    val activeGroceryVendorName: String? get() = groceryItems.firstOrNull()?.item?.vendorName
}
