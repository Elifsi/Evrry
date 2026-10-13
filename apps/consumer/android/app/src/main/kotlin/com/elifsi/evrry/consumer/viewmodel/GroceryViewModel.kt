package com.elifsi.evrry.consumer.viewmodel

import androidx.lifecycle.ViewModel
import com.elifsi.evrry.consumer.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Grocery Quick Mart ViewModel
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
class GroceryViewModel : ViewModel() {
    private val _activeHub = MutableStateFlow<GroceryHub>(GroceryCatalogData.defaultHub)
    val activeHub: StateFlow<GroceryHub> = _activeHub.asStateFlow()

    private val _deliveryMode = MutableStateFlow(GroceryDeliveryMode.EXPRESS_DARKSTORE)
    val deliveryMode: StateFlow<GroceryDeliveryMode> = _deliveryMode.asStateFlow()

    private val _selectedStore = MutableStateFlow<GroceryStore?>(null)
    val selectedStore: StateFlow<GroceryStore?> = _selectedStore.asStateFlow()

    private val _storeFilterType = MutableStateFlow("All")
    val storeFilterType: StateFlow<String> = _storeFilterType.asStateFlow()

    private val _storeSearchQuery = MutableStateFlow("")
    val storeSearchQuery: StateFlow<String> = _storeSearchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String>("dairy")
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private val _selectedSubCategory = MutableStateFlow<String>("All")
    val selectedSubCategory: StateFlow<String> = _selectedSubCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _substitutionPreference = MutableStateFlow(GrocerySubstitutionPreference.ALLOW_STORE_SUBSTITUTION)
    val substitutionPreference: StateFlow<GrocerySubstitutionPreference> = _substitutionPreference.asStateFlow()

    fun setDeliveryMode(mode: GroceryDeliveryMode) {
        _deliveryMode.value = mode
    }

    fun selectStore(store: GroceryStore?) {
        _selectedStore.value = store
    }

    fun setStoreFilterType(filter: String) {
        _storeFilterType.value = filter
    }

    fun setStoreSearchQuery(query: String) {
        _storeSearchQuery.value = query
    }

    fun getStores(): List<GroceryStore> {
        val filter = _storeFilterType.value
        val query = _storeSearchQuery.value.trim().lowercase()

        return GroceryCatalogData.partnerStores.filter { store ->
            val matchesFilter = when (filter) {
                "All" -> true
                "Supermarkets" -> store.storeType == "Supermarket" || store.storeType == "Department Store"
                "Daily Marts" -> store.storeType == "Mart Chain"
                "Organic & Farm" -> store.storeType == "Organic Farm Store"
                "Local Kirana" -> store.storeType == "Local Kirana"
                else -> true
            }

            val matchesQuery = query.isEmpty() ||
                store.name.lowercase().contains(query) ||
                store.tagline.lowercase().contains(query) ||
                store.area.lowercase().contains(query) ||
                store.featuredTags.any { it.lowercase().contains(query) }

            matchesFilter && matchesQuery
        }
    }

    fun getProductsForStore(storeId: String, aisle: String = "All", query: String = ""): List<GroceryProduct> {
        val cleanQuery = query.trim().lowercase()
        return GroceryCatalogData.storeProducts.filter { prod ->
            val matchesStore = prod.storeId == storeId
            val matchesAisle = (aisle == "All" || prod.subCategory.equals(aisle, ignoreCase = true) || prod.categoryId.equals(aisle, ignoreCase = true))
            val matchesQuery = cleanQuery.isEmpty() ||
                prod.title.lowercase().contains(cleanQuery) ||
                (prod.titleNe?.lowercase()?.contains(cleanQuery) == true) ||
                prod.brand.lowercase().contains(cleanQuery) ||
                prod.tags.any { it.lowercase().contains(cleanQuery) }

            matchesStore && (if (cleanQuery.isNotEmpty()) matchesQuery else matchesAisle)
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
        _selectedSubCategory.value = "All"
    }

    fun selectSubCategory(subCategory: String) {
        _selectedSubCategory.value = subCategory
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSubstitutionPreference(preference: GrocerySubstitutionPreference) {
        _substitutionPreference.value = preference
    }

    fun getFilteredProducts(): List<GroceryProduct> {
        val query = _searchQuery.value.trim().lowercase()
        val catId = _selectedCategoryId.value
        val subCat = _selectedSubCategory.value

        return GroceryCatalogData.products.filter { product ->
            // Category & Subcategory filter
            val matchesCategory = (product.categoryId == catId)
            val matchesSubCategory = (subCat == "All" || product.subCategory.equals(subCat, ignoreCase = true))

            // Query filter (matches English or Nepali)
            val matchesQuery = if (query.isEmpty()) {
                true
            } else {
                product.title.lowercase().contains(query) ||
                (product.titleNe?.lowercase()?.contains(query) == true) ||
                product.brand.lowercase().contains(query) ||
                product.subCategory.lowercase().contains(query) ||
                product.tags.any { it.lowercase().contains(query) }
            }

            if (query.isNotEmpty()) {
                matchesQuery // In search mode, search across all categories
            } else {
                matchesCategory && matchesSubCategory
            }
        }
    }

    fun getProductsByAisle(categoryId: String): List<GroceryProduct> {
        return GroceryCatalogData.products.filter { it.categoryId == categoryId }
    }

    fun getBestSellers(): List<GroceryProduct> {
        return GroceryCatalogData.products.filter { it.tags.contains("Bestseller") || it.tags.contains("Daily Essential") }
    }

    fun getFlashDeals(): List<GroceryProduct> {
        return GroceryCatalogData.products.filter { it.discountPercent >= 10 }
    }

    fun canIncrement(product: GroceryProduct, currentQty: Int): Boolean {
        return currentQty < product.stockQuantity && currentQty < product.maxPerOrder
    }

    fun isOutOfStock(product: GroceryProduct): Boolean {
        return product.stockQuantity <= 0
    }

    fun isLowStock(product: GroceryProduct): Boolean {
        return product.stockQuantity in 1..3
    }

    fun canIncrementVariant(variant: GroceryProductVariant, maxPerOrder: Int, currentQty: Int): Boolean {
        return currentQty < variant.stockQuantity && currentQty < maxPerOrder
    }

    fun isVariantOutOfStock(variant: GroceryProductVariant): Boolean {
        return variant.stockQuantity <= 0
    }

    fun isVariantLowStock(variant: GroceryProductVariant): Boolean {
        return variant.stockQuantity in 1..3
    }

    fun getImpulseAdditions(cartItemIds: Set<String>): List<GroceryProduct> {
        val priorityIds = listOf(
            "groc-patan-sliced-bread",
            "groc-ddc-milk-500",
            "groc-local-eggs-6",
            "groc-waiwai-chicken-5pack",
            "groc-palungo-saag",
            "groc-current-hot-spicy-noodles",
            "groc-dettol-handwash-200"
        )
        return priorityIds
            .filter { !cartItemIds.contains(it) }
            .mapNotNull { id -> GroceryCatalogData.products.find { it.id == id } }
    }
}
