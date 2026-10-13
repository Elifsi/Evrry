package com.elifsi.evrry.partner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elifsi.evrry.partner.model.*
import com.elifsi.evrry.partner.printer.EscPosPrinterService
import com.elifsi.evrry.partner.sound.KitchenAudioChimeService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main ViewModel for Restaurant Kitchen Partner & KDS Operations
 * Platform: Android (Kotlin + Jetpack Compose)
 * Elifsi Technologies Private Limited
 */
class RestaurantKitchenViewModel @Inject constructor(
    val audioChimeService: KitchenAudioChimeService = KitchenAudioChimeService(),
    val printerService: EscPosPrinterService = EscPosPrinterService()
) : ViewModel() {

    // Store status
    private val _store = MutableStateFlow(
        RestaurantStore(
            id = "store-roadhouse-pulchowk",
            partnerId = "partner-roadhouse-001",
            name = "Roadhouse Cafe & Kitchen (Pulchowk)",
            isOpen = true,
            avgPrepTimeMinutes = 25,
            addressText = "Ward 3, Pulchowk Marg, Lalitpur, Nepal"
        )
    )
    val store: StateFlow<RestaurantStore> = _store.asStateFlow()

    // Current KDS Tab
    private val _currentTab = MutableStateFlow(KitchenTab.NEW_ORDERS)
    val currentTab: StateFlow<KitchenTab> = _currentTab.asStateFlow()

    // Active Orders List
    private val _orders = MutableStateFlow<List<RestaurantOrder>>(initialSampleOrders())
    val orders: StateFlow<List<RestaurantOrder>> = _orders.asStateFlow()

    // Catalog items for 86'ing
    private val _catalogItems = MutableStateFlow<List<CatalogItem>>(initialCatalogItems())
    val catalogItems: StateFlow<List<CatalogItem>> = _catalogItems.asStateFlow()

    // Modal & Sheet States
    private val _selectedOrderForPrep = MutableStateFlow<RestaurantOrder?>(null)
    val selectedOrderForPrep: StateFlow<RestaurantOrder?> = _selectedOrderForPrep.asStateFlow()

    private val _selectedOrderForReject = MutableStateFlow<RestaurantOrder?>(null)
    val selectedOrderForReject: StateFlow<RestaurantOrder?> = _selectedOrderForReject.asStateFlow()

    private val _receiptPreviewText = MutableStateFlow<String?>(null)
    val receiptPreviewText: StateFlow<String?> = _receiptPreviewText.asStateFlow()

    private val _receiptPreviewTitle = MutableStateFlow<String>("Ticket Preview")
    val receiptPreviewTitle: StateFlow<String> = _receiptPreviewTitle.asStateFlow()

    // Chat state
    private val _activeChatOrder = MutableStateFlow<RestaurantOrder?>(null)
    val activeChatOrder: StateFlow<RestaurantOrder?> = _activeChatOrder.asStateFlow()

    private val _chatRecipientRole = MutableStateFlow("consumer") // "consumer" or "rider"
    val chatRecipientRole: StateFlow<String> = _chatRecipientRole.asStateFlow()

    private val _chatMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(initialChatMessages())
    val chatMessages: StateFlow<Map<String, List<ChatMessage>>> = _chatMessages.asStateFlow()

    // Notification toast banner
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Periodic ticker for circular countdowns
    val ticker = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), System.currentTimeMillis())

    init {
        // Monitor incoming orders to ring high-decibel chime loop
        viewModelScope.launch {
            _orders.collect { orderList ->
                val unacknowledged = orderList.firstOrNull { it.status == RestaurantOrderStatus.ACKNOWLEDGED }
                if (unacknowledged != null && _store.value.isOpen) {
                    audioChimeService.startIncomingOrderChime(unacknowledged.id)
                } else {
                    audioChimeService.stopChime()
                }
            }
        }
    }

    fun selectTab(tab: KitchenTab) {
        _currentTab.value = tab
    }

    fun toggleStoreOpen() {
        val updated = _store.value.copy(isOpen = !_store.value.isOpen)
        _store.value = updated
        if (!updated.isOpen) {
            audioChimeService.stopChime()
            showToast("Kitchen is now CLOSED. Incoming orders paused.")
        } else {
            showToast("Kitchen is OPEN & accepting orders.")
        }
    }

    fun openPrepTimeModal(order: RestaurantOrder) {
        _selectedOrderForPrep.value = order
    }

    fun closePrepTimeModal() {
        _selectedOrderForPrep.value = null
    }

    fun openRejectModal(order: RestaurantOrder) {
        _selectedOrderForReject.value = order
    }

    fun closeRejectModal() {
        _selectedOrderForReject.value = null
    }

    fun silenceChime() {
        audioChimeService.silenceRing()
        showToast("Audio alert silenced for current tickets.")
    }

    /**
     * Accept order with selected prep time (transitions ACKNOWLEDGED -> PREPARING)
     */
    fun acceptOrder(orderId: String, prepMinutes: Int) {
        val now = System.currentTimeMillis()
        audioChimeService.stopChime()
        _orders.update { list ->
            list.map { o ->
                if (o.id == orderId) {
                    o.copy(
                        status = RestaurantOrderStatus.PREPARING,
                        prepTimeMinutes = prepMinutes,
                        prepStartedAt = now
                    )
                } else o
            }
        }
        closePrepTimeModal()
        showToast("Order accepted! Cooking timer set to $prepMinutes mins.")
    }

    /**
     * Reject order with structured reason (transitions ACKNOWLEDGED -> REJECTED)
     */
    fun rejectOrder(orderId: String, reason: String) {
        audioChimeService.stopChime()
        _orders.update { list ->
            list.map { o ->
                if (o.id == orderId) {
                    o.copy(
                        status = RestaurantOrderStatus.REJECTED,
                        rejectionReason = reason
                    )
                } else o
            }
        }
        closeRejectModal()
        showToast("Order rejected: $reason")
    }

    /**
     * Mark order ready for courier pickup (transitions PREPARING -> READY_FOR_PICKUP)
     */
    fun markReadyForPickup(orderId: String) {
        val now = System.currentTimeMillis()
        _orders.update { list ->
            list.map { o ->
                if (o.id == orderId) {
                    o.copy(
                        status = RestaurantOrderStatus.READY_FOR_PICKUP,
                        readyAt = now,
                        riderName = o.riderName ?: "Bikash Thapa",
                        riderPhone = o.riderPhone ?: "9841000001",
                        riderVehicleType = o.riderVehicleType ?: "Honda Dio · Ba 88 Pa 4321",
                        riderDistanceMeters = o.riderDistanceMeters ?: 420
                    )
                } else o
            }
        }
        showToast("Order marked Ready! Assigned delivery courier notified.")
    }

    /**
     * Instant Menu Item 86'ing (Out-of-Stock Toggle)
     */
    fun toggle86Item(itemId: String) {
        _catalogItems.update { items ->
            items.map { item ->
                if (item.id == itemId) {
                    val updated = !item.isAvailable
                    showToast(if (updated) "${item.name} is now IN STOCK 🟢" else "${item.name} 86'd / SOLD OUT 🔴")
                    item.copy(isAvailable = updated)
                } else item
            }
        }
    }

    /**
     * Preview Kitchen Order Ticket (KOT)
     */
    fun previewKot(order: RestaurantOrder) {
        _receiptPreviewTitle.value = "Kitchen Order Ticket #${order.orderNo}"
        _receiptPreviewText.value = printerService.formatKotTextPreview(order, _store.value)
    }

    /**
     * Preview Customer Receipt
     */
    fun previewBill(order: RestaurantOrder) {
        _receiptPreviewTitle.value = "Customer Receipt #${order.orderNo}"
        _receiptPreviewText.value = printerService.formatCustomerBillPreview(order, _store.value)
    }

    fun closeReceiptPreview() {
        _receiptPreviewText.value = null
    }

    /**
     * Send receipt bytes to simulated ESC/POS thermal printer
     */
    fun printTicket(order: RestaurantOrder, isKot: Boolean) {
        if (isKot) {
            val bytes = printerService.buildKotBytes(order, _store.value)
            showToast("Printed KOT #${order.orderNo} (${bytes.size} bytes dispatched to thermal printer)")
        } else {
            val bytes = printerService.buildCustomerBillBytes(order, _store.value)
            showToast("Printed Bill #${order.orderNo} (${bytes.size} bytes dispatched to thermal printer)")
        }
        closeReceiptPreview()
    }

    /**
     * Open contextual chat with Consumer or Courier
     */
    fun openChat(order: RestaurantOrder, recipientRole: String) {
        _activeChatOrder.value = order
        _chatRecipientRole.value = recipientRole
    }

    fun closeChat() {
        _activeChatOrder.value = null
    }

    fun sendChatMessage(text: String) {
        val order = _activeChatOrder.value ?: return
        if (text.isBlank()) return

        val message = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            orderId = order.id,
            senderRole = "store",
            senderName = _store.value.name,
            text = text,
            timestamp = System.currentTimeMillis(),
            isOutgoing = true
        )

        _chatMessages.update { map ->
            val existing = map[order.id] ?: emptyList()
            map + (order.id to (existing + message))
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    // Daily Sales Stats
    val dailyCompletedCount: Int
        get() = _orders.value.count { it.status == RestaurantOrderStatus.DELIVERED }

    val dailyGrossSalesRs: Double
        get() = _orders.value.filter { it.status == RestaurantOrderStatus.DELIVERED }
            .sumOf { it.totalRs }

    val avgPrepMinutesFormatted: String
        get() = "${_store.value.avgPrepTimeMinutes}m"

    // Seed Data
    private fun initialSampleOrders(): List<RestaurantOrder> {
        val now = System.currentTimeMillis()
        return listOf(
            RestaurantOrder(
                id = "ord-10243",
                orderNo = 10243L,
                consumerId = "usr-aarav",
                consumerName = "Aarav Shrestha",
                consumerPhone = "+977-9801234567",
                storeId = "store-roadhouse-pulchowk",
                status = RestaurantOrderStatus.ACKNOWLEDGED,
                paymentMethod = "Cash on Delivery",
                isPaid = false,
                subtotalPaisa = 125000L, // Rs 1250
                deliveryFeePaisa = 5000L,
                platformFeePaisa = 1000L,
                taxPaisa = 16250L,
                totalPaisa = 147250L, // Rs 1472.50
                customerNote = "Extra chili flakes and timur dip please. Ring bell once at gate.",
                placedAt = now - 90_000L, // 1.5 min ago
                prepTimeMinutes = 25,
                items = listOf(
                    RestaurantOrderItem(
                        id = "item-1",
                        itemId = "cat-pizza-01",
                        name = "Woodfired Smoked Chicken Pizza",
                        unitPricePaisa = 85000L,
                        quantity = 1,
                        customizations = listOf("Thin crust", "Extra mozzarella", "Spicy timur dip")
                    ),
                    RestaurantOrderItem(
                        id = "item-2",
                        itemId = "cat-starters-02",
                        name = "Crispy Fried Chicken Wings (6 pcs)",
                        unitPricePaisa = 40000L,
                        quantity = 1,
                        customizations = listOf("Hot Garlic Glaze")
                    )
                )
            ),
            RestaurantOrder(
                id = "ord-10242",
                orderNo = 10242L,
                consumerId = "usr-priya",
                consumerName = "Priya Adhikari",
                consumerPhone = "+977-9841987654",
                storeId = "store-roadhouse-pulchowk",
                status = RestaurantOrderStatus.PREPARING,
                paymentMethod = "eSewa Direct Payment",
                isPaid = true,
                subtotalPaisa = 98000L,
                totalPaisa = 104000L,
                customerNote = "No onions on pasta please.",
                placedAt = now - 600_000L, // 10 mins ago
                prepStartedAt = now - 480_000L, // 8 mins ago
                prepTimeMinutes = 20,
                items = listOf(
                    RestaurantOrderItem(
                        id = "item-3",
                        itemId = "cat-pasta-01",
                        name = "Fettuccine Alfredo with Mushroom",
                        unitPricePaisa = 65000L,
                        quantity = 1,
                        customizations = listOf("Strictly no onion")
                    ),
                    RestaurantOrderItem(
                        id = "item-4",
                        itemId = "cat-drink-01",
                        name = "Fresh Peach Mint Iced Tea",
                        unitPricePaisa = 33000L,
                        quantity = 1
                    )
                )
            ),
            RestaurantOrder(
                id = "ord-10241",
                orderNo = 10241L,
                consumerId = "usr-rohan",
                consumerName = "Rohan Maharjan",
                consumerPhone = "+977-9818112233",
                storeId = "store-roadhouse-pulchowk",
                riderPartnerId = "rider-bikash",
                riderName = "Bikash Thapa",
                riderPhone = "+977-9841000001",
                riderVehicleType = "Honda Dio · Ba 88 Pa 4321",
                riderDistanceMeters = 350,
                status = RestaurantOrderStatus.READY_FOR_PICKUP,
                paymentMethod = "Fonepay Dynamic QR",
                isPaid = true,
                subtotalPaisa = 72000L,
                totalPaisa = 78000L,
                placedAt = now - 1500_000L,
                prepStartedAt = now - 1400_000L,
                readyAt = now - 120_000L,
                items = listOf(
                    RestaurantOrderItem(
                        id = "item-5",
                        itemId = "cat-burger-01",
                        name = "Classic Buff Cheeseburger + Fries",
                        unitPricePaisa = 52000L,
                        quantity = 1
                    ),
                    RestaurantOrderItem(
                        id = "item-6",
                        itemId = "cat-drink-02",
                        name = "Belgian Chocolate Shake",
                        unitPricePaisa = 20000L,
                        quantity = 1
                    )
                )
            ),
            RestaurantOrder(
                id = "ord-10240",
                orderNo = 10240L,
                consumerId = "usr-sita",
                consumerName = "Sita Sharma",
                consumerPhone = "+977-9849556677",
                storeId = "store-roadhouse-pulchowk",
                riderName = "Ramesh Karki",
                riderVehicleType = "TVS Raider · Ba 92 Pa 9988",
                status = RestaurantOrderStatus.DELIVERED,
                paymentMethod = "Cash on Delivery",
                isPaid = true,
                subtotalPaisa = 145000L,
                totalPaisa = 151000L,
                placedAt = now - 3600_000L,
                items = listOf(
                    RestaurantOrderItem(
                        id = "item-7",
                        itemId = "cat-pizza-02",
                        name = "Quattro Formaggi Pizza (4 Cheese)",
                        unitPricePaisa = 95000L,
                        quantity = 1
                    ),
                    RestaurantOrderItem(
                        id = "item-8",
                        itemId = "cat-starters-01",
                        name = "Garlic Herb Bread with Dip",
                        unitPricePaisa = 50000L,
                        quantity = 1
                    )
                )
            )
        )
    }

    private fun initialCatalogItems(): List<CatalogItem> {
        return listOf(
            CatalogItem(
                id = "cat-pizza-01",
                storeId = "store-roadhouse-pulchowk",
                name = "Woodfired Smoked Chicken Pizza",
                description = "Hand-stretched dough, San Marzano tomato, smoked chicken, mozzarella",
                pricePaisa = 85000L,
                section = "Woodfired Pizza",
                isAvailable = true,
                tags = listOf("Bestseller", "Non-Veg")
            ),
            CatalogItem(
                id = "cat-pizza-02",
                storeId = "store-roadhouse-pulchowk",
                name = "Quattro Formaggi Pizza (4 Cheese)",
                description = "Mozzarella, Gorgonzola, Parmesan, and Yak cheese from Langtang",
                pricePaisa = 95000L,
                section = "Woodfired Pizza",
                isAvailable = true,
                tags = listOf("Veg", "Chef Special")
            ),
            CatalogItem(
                id = "cat-starters-01",
                storeId = "store-roadhouse-pulchowk",
                name = "Garlic Herb Bread with Dip",
                description = "Freshly toasted baguette with garlic butter and herb marinara",
                pricePaisa = 50000L,
                section = "Starters",
                isAvailable = true,
                tags = listOf("Veg")
            ),
            CatalogItem(
                id = "cat-starters-02",
                storeId = "store-roadhouse-pulchowk",
                name = "Crispy Fried Chicken Wings (6 pcs)",
                description = "Double fried with hot garlic dip and timur glaze",
                pricePaisa = 40000L,
                section = "Starters",
                isAvailable = true,
                tags = listOf("Non-Veg", "Spicy")
            ),
            CatalogItem(
                id = "cat-pasta-01",
                storeId = "store-roadhouse-pulchowk",
                name = "Fettuccine Alfredo with Mushroom",
                description = "Creamy parmesan white sauce with button and oyster mushrooms",
                pricePaisa = 65000L,
                section = "Pasta & Mains",
                isAvailable = true,
                tags = listOf("Veg")
            ),
            CatalogItem(
                id = "cat-burger-01",
                storeId = "store-roadhouse-pulchowk",
                name = "Classic Buff Cheeseburger + Fries",
                description = "Local buff patty, aged cheddar, caramelized onion, peri-peri fries",
                pricePaisa = 52000L,
                section = "Burgers",
                isAvailable = true,
                tags = listOf("Non-Veg")
            ),
            CatalogItem(
                id = "cat-drink-01",
                storeId = "store-roadhouse-pulchowk",
                name = "Fresh Peach Mint Iced Tea",
                description = "Cold brewed Ilam black tea with peach puree and fresh garden mint",
                pricePaisa = 33000L,
                section = "Beverages",
                isAvailable = true,
                tags = listOf("Chilled")
            ),
            CatalogItem(
                id = "cat-drink-02",
                storeId = "store-roadhouse-pulchowk",
                name = "Belgian Chocolate Shake",
                description = "Rich chocolate gelato blended with fresh organic milk",
                pricePaisa = 20000L,
                section = "Beverages",
                isAvailable = true,
                tags = listOf("Dessert Drink")
            )
        )
    }

    private fun initialChatMessages(): Map<String, List<ChatMessage>> {
        val now = System.currentTimeMillis()
        return mapOf(
            "ord-10243" to listOf(
                ChatMessage(
                    id = "msg-1",
                    orderId = "ord-10243",
                    senderRole = "consumer",
                    senderName = "Aarav Shrestha",
                    text = "Namaste! Could you please make the chicken wings extra crispy?",
                    timestamp = now - 60_000L,
                    isOutgoing = false
                ),
                ChatMessage(
                    id = "msg-2",
                    orderId = "ord-10243",
                    senderRole = "store",
                    senderName = "Roadhouse Cafe Kitchen",
                    text = "Namaste Aarav! Sure thing, chef is double-frying them with timur glaze.",
                    timestamp = now - 30_000L,
                    isOutgoing = true
                )
            ),
            "ord-10241" to listOf(
                ChatMessage(
                    id = "msg-3",
                    orderId = "ord-10241",
                    senderRole = "rider",
                    senderName = "Bikash Thapa (Courier)",
                    text = "Dai ma pulchowk chowk pugisake. 2 min ma order pickup garna aipugchhu.",
                    timestamp = now - 45_000L,
                    isOutgoing = false
                ),
                ChatMessage(
                    id = "msg-4",
                    orderId = "ord-10241",
                    senderRole = "store",
                    senderName = "Roadhouse Cafe Kitchen",
                    text = "Huss Bikash ji, bag pack garera counter mai ready chha.",
                    timestamp = now - 20_000L,
                    isOutgoing = true
                )
            )
        )
    }
}
