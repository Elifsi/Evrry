package com.elifsi.evrry.partner.viewmodel

import androidx.lifecycle.ViewModel
import com.elifsi.evrry.partner.data.GroceryCatalogSampleData
import com.elifsi.evrry.partner.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Supermarket Partner ViewModel
 * Implements retail inventory management, barcode SKU audit, pick-and-pack checklist,
 * loose item weight adjustments, out-of-stock customer substitution flow,
 * and courier handoff coordination.
 * Elifsi Technologies Private Limited
 */
class GrocerySupermarketViewModel : ViewModel() {

    private val _catalog = MutableStateFlow<List<GroceryPartnerItem>>(GroceryCatalogSampleData.initialCatalog)
    val catalog: StateFlow<List<GroceryPartnerItem>> = _catalog.asStateFlow()

    private val _selectedAisle = MutableStateFlow("All")
    val selectedAisle: StateFlow<String> = _selectedAisle.asStateFlow()

    private val _inventorySearchQuery = MutableStateFlow("")
    val inventorySearchQuery: StateFlow<String> = _inventorySearchQuery.asStateFlow()

    private val _orders = MutableStateFlow<List<GroceryPartnerOrder>>(GroceryCatalogSampleData.createInitialOrders())
    val orders: StateFlow<List<GroceryPartnerOrder>> = _orders.asStateFlow()

    private val _selectedOrderId = MutableStateFlow<String?>(GroceryCatalogSampleData.createInitialOrders().firstOrNull()?.id)
    val selectedOrderId: StateFlow<String?> = _selectedOrderId.asStateFlow()

    // Barcode scanner state
    private val _scannedBarcode = MutableStateFlow<String?>(null)
    val scannedBarcode: StateFlow<String?> = _scannedBarcode.asStateFlow()

    private val _scannedItem = MutableStateFlow<GroceryPartnerItem?>(null)
    val scannedItem: StateFlow<GroceryPartnerItem?> = _scannedItem.asStateFlow()

    // Courier in-app chat
    private val _courierChatMessages = MutableStateFlow<List<PartnerChatMessage>>(
        listOf(
            PartnerChatMessage(
                id = "msg-1",
                sender = "Courier",
                text = "Namaste dai! I am at the traffic chowk. Which gate for supermarket pickup?",
                time = "10:32 AM"
            ),
            PartnerChatMessage(
                id = "msg-2",
                sender = "You (Supermarket)",
                text = "Namaste! Come to Bay #3 at the Front Express Counter beside the escalator.",
                time = "10:33 AM"
            )
        )
    )
    val courierChatMessages: StateFlow<List<PartnerChatMessage>> = _courierChatMessages.asStateFlow()

    // -------------------------------------------------------------------------
    // Order Fulfillment & Pick-and-Pack Checklist
    // -------------------------------------------------------------------------

    fun getSelectedOrder(): GroceryPartnerOrder? {
        val currentId = _selectedOrderId.value ?: return null
        return _orders.value.firstOrNull { it.id == currentId }
    }

    fun selectOrder(orderId: String) {
        _selectedOrderId.value = orderId
    }

    fun toggleItemPacked(orderId: String, orderItemId: String) {
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) {
                val updatedItems = order.items.map { item ->
                    if (item.id == orderItemId) {
                        item.copy(isPacked = !item.isPacked)
                    } else item
                }
                order.copy(items = updatedItems)
            } else order
        }
    }

    fun setMeasuredWeight(orderId: String, orderItemId: String, weightKg: Double) {
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) {
                val updatedItems = order.items.map { item ->
                    if (item.id == orderItemId) {
                        val newLineTotalPaisa = (item.unitPricePaisa * weightKg).toLong()
                        item.copy(
                            measuredWeight = weightKg,
                            lineTotalPaisa = newLineTotalPaisa,
                            isPacked = true
                        )
                    } else item
                }
                val newSubtotal = updatedItems.sumOf { it.lineTotalPaisa }
                order.copy(items = updatedItems, totalPaisa = newSubtotal)
            } else order
        }
    }

    fun completeOrderPacking(orderId: String) {
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) {
                order.copy(
                    status = "ready_for_pickup",
                    riderEtaMinutes = 3
                )
            } else order
        }
    }

    // -------------------------------------------------------------------------
    // Out-of-Stock Customer Substitution Flow
    // -------------------------------------------------------------------------

    fun getRecommendedSubstitutes(orderItem: GroceryOrderItem): List<GroceryPartnerItem> {
        return _catalog.value.filter { item ->
            item.section.equals(orderItem.section, ignoreCase = true) &&
                    item.id != orderItem.itemId &&
                    item.isAvailable &&
                    item.stockQuantity > 0
        }
    }

    fun proposeSubstitution(
        orderId: String,
        orderItemId: String,
        substituteItem: GroceryPartnerItem
    ) {
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) {
                val updatedItems = order.items.map { item ->
                    if (item.id == orderItemId) {
                        item.copy(
                            substitutionStatus = SubstitutionStatus.PROPOSED,
                            proposedSubstituteItemId = substituteItem.id,
                            proposedSubstituteName = substituteItem.name,
                            proposedSubstitutePriceNpr = substituteItem.priceNpr,
                            isPacked = false
                        )
                    } else item
                }
                order.copy(items = updatedItems)
            } else order
        }
    }

    fun resolveSubstitution(
        orderId: String,
        orderItemId: String,
        approved: Boolean
    ) {
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) {
                val updatedItems = order.items.map { item ->
                    if (item.id == orderItemId) {
                        if (approved && item.proposedSubstituteItemId != null) {
                            val subItem = _catalog.value.firstOrNull { it.id == item.proposedSubstituteItemId }
                            val newUnitPricePaisa = subItem?.pricePaisa ?: item.unitPricePaisa
                            item.copy(
                                name = item.proposedSubstituteName ?: item.name,
                                unitPricePaisa = newUnitPricePaisa,
                                lineTotalPaisa = newUnitPricePaisa * item.quantity,
                                substitutionStatus = SubstitutionStatus.APPROVED,
                                isPacked = true
                            )
                        } else {
                            item.copy(
                                substitutionStatus = SubstitutionStatus.REJECTED,
                                isPacked = false,
                                lineTotalPaisa = 0L
                            )
                        }
                    } else item
                }
                val newSubtotal = updatedItems.sumOf { it.lineTotalPaisa }
                order.copy(items = updatedItems, totalPaisa = newSubtotal)
            } else order
        }
    }

    // -------------------------------------------------------------------------
    // Inventory & Shelf Stock Management
    // -------------------------------------------------------------------------

    fun setAisleFilter(aisle: String) {
        _selectedAisle.value = aisle
    }

    fun setInventorySearchQuery(query: String) {
        _inventorySearchQuery.value = query
    }

    fun getFilteredCatalog(): List<GroceryPartnerItem> {
        val aisle = _selectedAisle.value
        val query = _inventorySearchQuery.value.trim().lowercase(Locale.getDefault())

        return _catalog.value.filter { item ->
            val matchesAisle = (aisle == "All") || item.section.equals(aisle, ignoreCase = true)
            val matchesQuery = query.isEmpty() ||
                    item.name.lowercase(Locale.getDefault()).contains(query) ||
                    (item.nameNe != null && item.nameNe.contains(query)) ||
                    item.brand.lowercase(Locale.getDefault()).contains(query) ||
                    (item.barcode != null && item.barcode.contains(query)) ||
                    (item.sku != null && item.sku.lowercase(Locale.getDefault()).contains(query))
            matchesAisle && matchesQuery
        }
    }

    fun adjustStock(itemId: String, delta: Int) {
        _catalog.value = _catalog.value.map { item ->
            if (item.id == itemId) {
                val newQuantity = (item.stockQuantity + delta).coerceAtLeast(0)
                item.copy(
                    stockQuantity = newQuantity,
                    isAvailable = newQuantity > 0
                )
            } else item
        }
        // Update scanned item if matched
        if (_scannedItem.value?.id == itemId) {
            _scannedItem.value = _catalog.value.firstOrNull { it.id == itemId }
        }
    }

    fun toggleItemAvailability(itemId: String) {
        _catalog.value = _catalog.value.map { item ->
            if (item.id == itemId) {
                item.copy(isAvailable = !item.isAvailable)
            } else item
        }
        if (_scannedItem.value?.id == itemId) {
            _scannedItem.value = _catalog.value.firstOrNull { it.id == itemId }
        }
    }

    fun updateItemPrice(itemId: String, newPriceNpr: Double) {
        val newPricePaisa = (newPriceNpr * 100).toLong()
        _catalog.value = _catalog.value.map { item ->
            if (item.id == itemId) {
                item.copy(pricePaisa = newPricePaisa)
            } else item
        }
        if (_scannedItem.value?.id == itemId) {
            _scannedItem.value = _catalog.value.firstOrNull { it.id == itemId }
        }
    }

    // -------------------------------------------------------------------------
    // Barcode Scanner & SKU Lookup
    // -------------------------------------------------------------------------

    fun onBarcodeScanned(code: String) {
        _scannedBarcode.value = code
        val matched = _catalog.value.firstOrNull {
            it.barcode == code || it.sku?.equals(code, ignoreCase = true) == true
        }
        _scannedItem.value = matched
    }

    fun clearScannedBarcode() {
        _scannedBarcode.value = null
        _scannedItem.value = null
    }

    // -------------------------------------------------------------------------
    // Courier Coordination
    // -------------------------------------------------------------------------

    fun sendCourierChatMessage(text: String) {
        if (text.isBlank()) return
        val newMsg = PartnerChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            sender = "You (Supermarket)",
            text = text.trim(),
            time = "Just now"
        )
        _courierChatMessages.value = _courierChatMessages.value + newMsg
    }
}
