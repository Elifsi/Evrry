package com.elifsi.evrry.consumer.viewmodel

import androidx.lifecycle.ViewModel
import com.elifsi.evrry.consumer.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CuisineFilter(
    val id: String,
    val label: String,
    val emoji: String,
    val filterMatch: String? = null,
    val isVegOnly: Boolean = false
)

data class QuickFilter(
    val id: String,
    val label: String
)

class FoodViewModel : ViewModel() {
    val cuisines = listOf(
        CuisineFilter(id = "all", label = "All", emoji = "🍽️"),
        CuisineFilter(id = "momo", label = "Momo & Newari", emoji = "🥟", filterMatch = "Momo"),
        CuisineFilter(id = "thakali", label = "Thakali Thali", emoji = "🍲", filterMatch = "Thakali"),
        CuisineFilter(id = "pizza", label = "Woodfired Pizza", emoji = "🍕", filterMatch = "Pizza"),
        CuisineFilter(id = "burgers", label = "Burgers & CFC", emoji = "🍔", filterMatch = "Burgers"),
        CuisineFilter(id = "japanese", label = "Japanese & Sushi", emoji = "🍱", filterMatch = "Japanese"),
        CuisineFilter(id = "bakery", label = "Bakery & Desserts", emoji = "🥐", filterMatch = "Bakery"),
        CuisineFilter(id = "pure_veg", label = "Pure Veg", emoji = "🥗", isVegOnly = true)
    )

    val quickFilters = listOf(
        QuickFilter(id = "rating", label = "Rating 4.5+ ★"),
        QuickFilter(id = "veg", label = "Pure Veg 🟢"),
        QuickFilter(id = "fast", label = "Under 30 mins ⚡"),
        QuickFilter(id = "offers", label = "Special Offers 🏷️")
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCuisine = MutableStateFlow("all")
    val selectedCuisine: StateFlow<String> = _selectedCuisine.asStateFlow()

    private val _activeFilters = MutableStateFlow<Set<String>>(emptySet())
    val activeFilters: StateFlow<Set<String>> = _activeFilters.asStateFlow()

    private val _restaurantSwitchedBanner = MutableStateFlow<String?>(null)
    val restaurantSwitchedBanner: StateFlow<String?> = _restaurantSwitchedBanner.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCuisine(cuisineId: String) {
        _selectedCuisine.value = cuisineId
    }

    fun toggleFilter(filterId: String) {
        val current = _activeFilters.value.toMutableSet()
        if (current.contains(filterId)) {
            current.remove(filterId)
        } else {
            current.add(filterId)
        }
        _activeFilters.value = current
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _selectedCuisine.value = "all"
        _activeFilters.value = emptySet()
    }

    fun clearSwitchBanner() {
        _restaurantSwitchedBanner.value = null
    }

    fun notifyRestaurantSwitched(restaurantName: String) {
        _restaurantSwitchedBanner.value = "Started a fresh order from $restaurantName — items from your previous restaurant were cleared."
    }

    fun getFilteredRestaurants(): List<Restaurant> {
        val query = _searchQuery.value.trim().lowercase()
        val cuisineId = _selectedCuisine.value
        val filters = _activeFilters.value

        return FoodCatalogData.restaurants.filter { r ->
            // Search match (checks restaurant name, cuisines, area, and menu dishes)
            if (query.isNotEmpty()) {
                val menuDishes = FoodCatalogData.menuByRestaurant(r.id).joinToString(" ") { "${it.title} ${it.subtitle ?: ""}" }
                val fullText = "${r.name} ${r.cuisines.joinToString(" ")} ${r.area} $menuDishes".lowercase()
                if (!fullText.contains(query)) return@filter false
            }

            // Cuisine filter
            if (cuisineId != "all") {
                val c = cuisines.find { it.id == cuisineId }
                if (c?.isVegOnly == true && !r.isPureVeg) return@filter false
                if (c?.filterMatch != null && !r.cuisines.any { it.contains(c.filterMatch, ignoreCase = true) }) {
                    return@filter false
                }
            }

            // Quick filters
            if (filters.contains("rating") && r.rating < 4.5) return@filter false
            if (filters.contains("veg") && !r.isPureVeg) return@filter false
            if (filters.contains("fast") && r.deliveryEtaMinutes > 30) return@filter false
            if (filters.contains("offers") && r.offer == null) return@filter false

            true
        }
    }
}
