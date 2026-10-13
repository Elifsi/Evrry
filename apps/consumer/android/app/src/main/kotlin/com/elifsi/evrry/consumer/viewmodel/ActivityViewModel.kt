package com.elifsi.evrry.consumer.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ActivityFilter {
    ALL, ACTIVE, COMPLETED
}

data class OrderActivityItem(
    val id: String,
    val title: String,
    val vendorName: String,
    val category: String, // "Food", "Mart", "Ride", "Stay"
    val amountRs: Int,
    val status: String,   // "placed", "confirmed", "kitchen", "delivering", "delivered", "cancelled"
    val paymentMethod: String, // "eSewa", "Khalti", "Fonepay QR", "Cash on Delivery"
    val dateFormatted: String,
    val etaMinutes: Int? = null,
    val riderName: String? = null,
    val riderPhone: String? = null,
    val rideOtp: String? = null,
    val vehicleNumber: String? = null,
    val deliveryAddress: String? = "Pulchowk, Ward 3, Lalitpur",
    val specialInstructions: String? = null,
    val lineItems: List<String> = emptyList(),
    val subtotalRs: Int = 0,
    val deliveryFeeRs: Int = 50,
    val platformFeeRs: Int = 10
)

data class ActivityUiState(
    val selectedFilter: ActivityFilter = ActivityFilter.ALL,
    val items: List<OrderActivityItem> = listOf(
        OrderActivityItem(
            id = "ord-001",
            title = "Steamed Chicken Momo (2x) + Buff Chilly",
            vendorName = "Jimbu Thakali Kitchen",
            category = "Food",
            amountRs = 480,
            status = "kitchen",
            paymentMethod = "eSewa Direct Payment",
            dateFormatted = "Today, 1:15 PM",
            etaMinutes = 18,
            riderName = "Bikash Thapa",
            riderPhone = "9841000001",
            vehicleNumber = "Ba 88 Pa 4321",
            deliveryAddress = "Pulchowk, Ward 3, Lalitpur",
            specialInstructions = "Extra spicy timur achar",
            lineItems = listOf("Steamed Chicken Momo x2 (Rs 360)", "Buff Chilly Momo x1 (Rs 120)"),
            subtotalRs = 420,
            deliveryFeeRs = 50
        ),
        OrderActivityItem(
            id = "ride-002",
            title = "Moto Ride to Thamel Chowk",
            vendorName = "evrry Mobility",
            category = "Ride",
            amountRs = 165,
            status = "delivering",
            paymentMethod = "Cash on Delivery",
            dateFormatted = "Today, 12:40 PM",
            etaMinutes = 6,
            riderName = "Kiran Shrestha",
            riderPhone = "9841000002",
            rideOtp = "1234",
            vehicleNumber = "Ba 92 Pa 8765",
            deliveryAddress = "Thamel Marg, Kathmandu",
            lineItems = listOf("Evrry Moto · 6.4 km · Patan to Thamel")
        ),
        OrderActivityItem(
            id = "ord-003",
            title = "Fresh Cow Milk (2L) + Organic Eggs (12 pcs)",
            vendorName = "Kalimati Fresh Mart",
            category = "Mart",
            amountRs = 390,
            status = "delivered",
            paymentMethod = "Fonepay Dynamic QR",
            dateFormatted = "Yesterday, 6:30 PM",
            deliveryAddress = "Pulchowk, Ward 3, Lalitpur",
            lineItems = listOf("Fresh Cow Milk 2L (Rs 220)", "Organic Farm Eggs 12pcs (Rs 170)")
        )
    )
)

class ActivityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

    fun setFilter(filter: ActivityFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    /**
     * Dynamically append newly placed orders to the activity stream
     */
    fun addOrder(order: OrderActivityItem) {
        _uiState.value = _uiState.value.copy(
            items = listOf(order) + _uiState.value.items
        )
    }

    fun getOrder(orderId: String): OrderActivityItem? {
        return _uiState.value.items.find { it.id == orderId }
    }

    fun getFilteredItems(): List<OrderActivityItem> {
        val current = _uiState.value
        return when (current.selectedFilter) {
            ActivityFilter.ALL -> current.items
            ActivityFilter.ACTIVE -> current.items.filter { it.status in listOf("placed", "confirmed", "kitchen", "delivering", "draft") }
            ActivityFilter.COMPLETED -> current.items.filter { it.status in listOf("delivered", "completed", "cancelled") }
        }
    }

    fun cancelOrder(orderId: String) {
        _uiState.value = _uiState.value.copy(
            items = _uiState.value.items.map {
                if (it.id == orderId) it.copy(status = "cancelled") else it
            }
        )
    }
}
