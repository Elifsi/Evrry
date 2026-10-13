package com.elifsi.evrry.consumer.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable

/**
 * Domain Models for Saved Payment Instruments in Evrry Super App
 * Supports: eSewa, Khalti, Fonepay Direct, Visa/Mastercard, COD
 * Platform: Consumer Android
 * Elifsi Technologies Private Limited
 */

enum class SavedPaymentType(val label: String, val badgeColorHex: Long) {
    ESEWA("eSewa Mobile Wallet", 0xFF60BB46),
    KHALTI("Khalti Digital Wallet", 0xFF5C2D91),
    FONEPAY("Fonepay Direct Interbank", 0xFFD32F2F),
    CARD("Debit / Credit Card", 0xFF1E3A8A),
    COD("Cash on Delivery", 0xFF475569)
}

@Serializable
data class SavedPaymentMethod(
    val id: String,
    val type: SavedPaymentType,
    val title: String,
    val subtitle: String,
    val isDefault: Boolean = false,
    val linkedAt: String = "Active"
)

/**
 * In-Memory & Realtime Repository for Consumer Payment Methods
 * Allows seamless adding, deleting, and setting default payment method.
 */
object SavedPaymentMethodRepository {
    private val _paymentMethods = MutableStateFlow(
        listOf(
            SavedPaymentMethod(
                id = "pm_esewa_default",
                type = SavedPaymentType.ESEWA,
                title = "eSewa Mobile Wallet",
                subtitle = "+977 9812345678",
                isDefault = true,
                linkedAt = "Verified"
            ),
            SavedPaymentMethod(
                id = "pm_khalti_1",
                type = SavedPaymentType.KHALTI,
                title = "Khalti Digital Wallet",
                subtitle = "+977 9812345678",
                isDefault = false,
                linkedAt = "Verified"
            ),
            SavedPaymentMethod(
                id = "pm_fonepay_1",
                type = SavedPaymentType.FONEPAY,
                title = "Fonepay Direct (Nabil Bank)",
                subtitle = "Account •••• 5821",
                isDefault = false,
                linkedAt = "Verified"
            ),
            SavedPaymentMethod(
                id = "pm_card_1",
                type = SavedPaymentType.CARD,
                title = "Visa Global Debit Card",
                subtitle = "•••• 4242 · Exp 08/28",
                isDefault = false,
                linkedAt = "Active"
            )
        )
    )

    val paymentMethods: StateFlow<List<SavedPaymentMethod>> = _paymentMethods.asStateFlow()

    fun deletePaymentMethod(id: String) {
        _paymentMethods.update { list ->
            val updated = list.filter { it.id != id }
            if (updated.isNotEmpty() && updated.none { it.isDefault }) {
                updated.mapIndexed { idx, item -> if (idx == 0) item.copy(isDefault = true) else item }
            } else {
                updated
            }
        }
    }

    fun setDefault(id: String) {
        _paymentMethods.update { list ->
            list.map { it.copy(isDefault = it.id == id) }
        }
    }

    fun addPaymentMethod(method: SavedPaymentMethod) {
        _paymentMethods.update { list ->
            val baseList = if (method.isDefault) list.map { it.copy(isDefault = false) } else list
            baseList + method
        }
    }
}
