package com.elifsi.evrry.consumer.viewmodel

import androidx.lifecycle.ViewModel
import com.elifsi.evrry.consumer.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Explore & Shared Cart ViewModel
 * Implements unified multi-vertical cart rules & store management
 * Elifsi Technologies Private Limited
 */
class ExploreViewModel : ViewModel() {
    private val _selectedVertical = MutableStateFlow<String>("all")
    val selectedVertical: StateFlow<String> = _selectedVertical.asStateFlow()

    private val _items = MutableStateFlow<List<CatalogItem>>(
        MockCatalogData.items.filter { it.vertical != "hotels" && it.vertical != "rentals" }
    )
    val items: StateFlow<List<CatalogItem>> = _items.asStateFlow()

    private val _cart = MutableStateFlow(CartState())
    val cart: StateFlow<CartState> = _cart.asStateFlow()

    fun selectVertical(vertical: String) {
        _selectedVertical.value = vertical
        if (vertical == "all") {
            _items.value = MockCatalogData.items.filter { it.vertical != "hotels" && it.vertical != "rentals" }
        } else {
            _items.value = MockCatalogData.items.filter { it.vertical == vertical }
        }
    }

    /**
     * Check if adding an item from [vendorId] conflicts with current cart in the specified [vertical].
     * Food items only conflict with other food kitchens; grocery items only conflict with other grocery hubs.
     * Food and Grocery items coexist in the common multi-vertical cart.
     */
    fun hasConflict(vendorId: String, vertical: String? = null): Boolean {
        val targetVertical = vertical ?: if (_cart.value.groceryItems.any { it.item.vendorId == vendorId }) {
            "grocery"
        } else if (_cart.value.foodItems.any { it.item.vendorId == vendorId }) {
            "food"
        } else {
            if (_cart.value.foodItems.isNotEmpty() && _cart.value.groceryItems.isEmpty()) "food"
            else if (_cart.value.groceryItems.isNotEmpty() && _cart.value.foodItems.isEmpty()) "grocery"
            else "food"
        }

        return when (targetVertical) {
            "grocery" -> {
                val activeHub = _cart.value.activeGroceryVendorId
                activeHub != null && activeHub != vendorId && _cart.value.groceryItems.isNotEmpty()
            }
            else -> {
                val activeKitchen = _cart.value.activeFoodVendorId
                activeKitchen != null && activeKitchen != vendorId && _cart.value.foodItems.isNotEmpty()
            }
        }
    }

    /**
     * Clear conflicting vertical items and add fresh item (preserving items from other verticals)
     */
    fun clearAndAdd(item: CatalogItem, options: List<String> = emptyList(), notes: String = "", quantity: Int = 1) {
        val remainingItems = _cart.value.items.filter { it.item.vertical != item.vertical }
        _cart.value = _cart.value.copy(
            items = remainingItems + CartItem(item = item, quantity = quantity, selectedOptions = options, specialNotes = notes)
        )
    }

    fun addToCart(item: CatalogItem): Boolean {
        val currentItems = _cart.value.items.toMutableList()
        val index = currentItems.indexOfFirst { it.item.id == item.id }
        if (index >= 0) {
            val existing = currentItems[index]
            currentItems[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentItems.add(CartItem(item = item, quantity = 1))
        }
        _cart.value = _cart.value.copy(items = currentItems)
        return false
    }

    fun addAddonToCart(addon: CatalogItem) {
        val currentVendorId = _cart.value.activeVendorId ?: addon.vendorId
        val currentVendorName = _cart.value.activeVendorName ?: addon.vendorName
        val contextualAddon = addon.copy(
            vendorId = currentVendorId,
            vendorName = currentVendorName
        )
        addToCart(contextualAddon)
    }

    fun addToCartWithOptions(item: CatalogItem, options: List<String>, notes: String = "", quantity: Int = 1) {
        val currentItems = _cart.value.items.toMutableList()
        val index = currentItems.indexOfFirst { it.item.id == item.id && it.selectedOptions == options }
        if (index >= 0) {
            val existing = currentItems[index]
            currentItems[index] = existing.copy(quantity = existing.quantity + quantity, specialNotes = notes)
        } else {
            currentItems.add(CartItem(item = item, quantity = quantity, selectedOptions = options, specialNotes = notes))
        }
        _cart.value = _cart.value.copy(items = currentItems)
    }

    fun removeFromCart(itemId: String) {
        val currentItems = _cart.value.items.toMutableList()
        val index = currentItems.indexOfFirst { it.item.id == itemId }
        if (index >= 0) {
            val existing = currentItems[index]
            if (existing.quantity > 1) {
                currentItems[index] = existing.copy(quantity = existing.quantity - 1)
            } else {
                currentItems.removeAt(index)
            }
        }
        _cart.value = _cart.value.copy(items = currentItems)
    }

    fun setCartQty(itemId: String, qty: Int) {
        val currentItems = _cart.value.items.toMutableList()
        val index = currentItems.indexOfFirst { it.item.id == itemId }
        if (index >= 0) {
            if (qty <= 0) {
                currentItems.removeAt(index)
            } else {
                currentItems[index] = currentItems[index].copy(quantity = qty)
            }
            _cart.value = _cart.value.copy(items = currentItems)
        }
    }

    fun getItemQty(itemId: String): Int {
        return _cart.value.items.find { it.item.id == itemId }?.quantity ?: 0
    }

    fun setSpecialInstructions(instructions: String) {
        _cart.value = _cart.value.copy(specialInstructions = instructions)
    }

    fun setFulfillmentType(type: OrderFulfillmentType) {
        _cart.value = _cart.value.copy(fulfillmentType = type)
    }

    val popularAddons = listOf(
        CatalogItem(
            id = "addon-coke",
            vertical = "food",
            vendorId = "vendor-addons",
            vendorName = "Beverages",
            title = "Chilled Coca-Cola (500ml)",
            subtitle = "Served ice-cold with lemon wedge",
            priceNpr = 70.0,
            rating = 4.9
        ),
        CatalogItem(
            id = "addon-dalle",
            vertical = "food",
            vendorId = "vendor-addons",
            vendorName = "Condiments",
            title = "Spicy Dalle Timur Pickle Dip",
            subtitle = "Authentic stone-ground fiery relish",
            priceNpr = 40.0,
            rating = 4.8
        ),
        CatalogItem(
            id = "addon-gulab",
            vertical = "food",
            vendorId = "vendor-addons",
            vendorName = "Desserts",
            title = "Warm Gulab Jamun (2 Pcs)",
            subtitle = "Cardamom sugar syrup infused",
            priceNpr = 110.0,
            rating = 4.7
        )
    )

    fun reorderOrder(vendorName: String, title: String, amountRs: Int, lineItems: List<String>) {
        val reorderItems = if (lineItems.isNotEmpty()) {
            lineItems.mapIndexed { idx, itemStr ->
                val name = itemStr.substringBefore(" x").substringBefore(" (")
                val parsedPrice = itemStr.substringAfter("(Rs ", "").substringBefore(")").toDoubleOrNull()
                val price = parsedPrice ?: (amountRs.toDouble() / maxOf(1, lineItems.size))
                CartItem(
                    item = CatalogItem(
                        id = "reorder-item-$idx",
                        vertical = "food",
                        vendorId = "reorder-vendor",
                        vendorName = vendorName,
                        title = name,
                        subtitle = "Reordered from $vendorName",
                        priceNpr = price,
                        rating = 4.8
                    ),
                    quantity = 1
                )
            }
        } else {
            listOf(
                CartItem(
                    item = CatalogItem(
                        id = "reorder-single",
                        vertical = "food",
                        vendorId = "reorder-vendor",
                        vendorName = vendorName,
                        title = title,
                        subtitle = "Reordered from $vendorName",
                        priceNpr = amountRs.toDouble(),
                        rating = 4.8
                    ),
                    quantity = 1
                )
            )
        }
        _cart.value = _cart.value.copy(items = reorderItems)
    }

    fun selectDeliveryAddress(address: DeliveryAddress) {
        _cart.value = _cart.value.copy(deliveryAddress = address)
    }

    fun clearCart() {
        _cart.value = _cart.value.copy(
            items = emptyList(),
            specialInstructions = ""
        )
    }
}
