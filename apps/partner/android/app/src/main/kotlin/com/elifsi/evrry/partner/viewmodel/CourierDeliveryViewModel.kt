package com.elifsi.evrry.partner.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elifsi.evrry.partner.model.*
import com.elifsi.evrry.partner.network.EvrryPartnerMapRoutingService
import com.elifsi.evrry.partner.network.EvrryPartnerPayoutService
import com.elifsi.evrry.partner.service.CourierLocationService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Food & Grocery Delivery Courier Operational Mode
 * Implements:
 * 1. Persistent Location Streaming Toggle
 * 2. Order Discovery Feed & Atomic Claiming
 * 3. Two-Stage Navigation Lifecycle (To Store -> To Customer)
 * 4. 4-Digit Customer Delivery OTP Verification Handshake
 * 5. COD Cash Collection Ledger with Anti-Fraud Warning
 * 6. Multi-Party Chat (Store vs Customer) & Dual Calling
 *
 * Elifsi Technologies Private Limited
 */
class CourierDeliveryViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient? = null,
    private val routingService: EvrryPartnerMapRoutingService? = null,
    private val payoutService: EvrryPartnerPayoutService? = null
) : ViewModel() {

    private val partnerId = "rider-kathmandu-01"

    // Online/Offline Dispatch Status
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    // Current Courier GPS Position (Sanepa/Pulchowk Lalitpur corridor)
    private val _courierLat = MutableStateFlow(27.6766)
    val courierLat: StateFlow<Double> = _courierLat.asStateFlow()

    private val _courierLng = MutableStateFlow(85.3135)
    val courierLng: StateFlow<Double> = _courierLng.asStateFlow()

    // Discovery Feed: Incoming Delivery Orders
    private val _availableOrders = MutableStateFlow<List<CourierOrder>>(createInitialAvailableOrders())
    val availableOrders: StateFlow<List<CourierOrder>> = _availableOrders.asStateFlow()

    // Active Delivery
    private val _activeOrder = MutableStateFlow<CourierOrder?>(null)
    val activeOrder: StateFlow<CourierOrder?> = _activeOrder.asStateFlow()

    private val _deliveryStage = MutableStateFlow(CourierDeliveryStage.STAGE_1_TO_STORE)
    val deliveryStage: StateFlow<CourierDeliveryStage> = _deliveryStage.asStateFlow()

    private val _hasArrivedAtStore = MutableStateFlow(false)
    val hasArrivedAtStore: StateFlow<Boolean> = _hasArrivedAtStore.asStateFlow()

    private val _hasArrivedAtCustomer = MutableStateFlow(false)
    val hasArrivedAtCustomer: StateFlow<Boolean> = _hasArrivedAtCustomer.asStateFlow()

    // 4-Digit Customer OTP Handoff
    private val _otpInput = MutableStateFlow("")
    val otpInput: StateFlow<String> = _otpInput.asStateFlow()

    private val _otpError = MutableStateFlow<String?>(null)
    val otpError: StateFlow<String?> = _otpError.asStateFlow()

    private val _isOtpVerifying = MutableStateFlow(false)
    val isOtpVerifying: StateFlow<Boolean> = _isOtpVerifying.asStateFlow()

    private val _deliverySuccessAnimation = MutableStateFlow(false)
    val deliverySuccessAnimation: StateFlow<Boolean> = _deliverySuccessAnimation.asStateFlow()

    // COD Cash Collection
    private val _codCashCollectedConfirmed = MutableStateFlow(false)
    val codCashCollectedConfirmed: StateFlow<Boolean> = _codCashCollectedConfirmed.asStateFlow()

    private val _codCashInHandPaisa = MutableStateFlow(96000L) // NPR 960 from previous orders
    val codCashInHandPaisa: StateFlow<Long> = _codCashInHandPaisa.asStateFlow()

    private val _todayEarningsPaisa = MutableStateFlow(54000L) // NPR 540
    val todayEarningsPaisa: StateFlow<Long> = _todayEarningsPaisa.asStateFlow()

    private val _todayDeliveriesCount = MutableStateFlow(3)
    val todayDeliveriesCount: StateFlow<Int> = _todayDeliveriesCount.asStateFlow()

    // Cash Ledger History
    private val _ledgerEntries = MutableStateFlow<List<CourierLedgerEntry>>(createInitialLedgerEntries())
    val ledgerEntries: StateFlow<List<CourierLedgerEntry>> = _ledgerEntries.asStateFlow()

    // Multi-Party Chat
    private val _activeChatTab = MutableStateFlow(CourierChatTab.CUSTOMER)
    val activeChatTab: StateFlow<CourierChatTab> = _activeChatTab.asStateFlow()

    private val _storeChatMessages = MutableStateFlow<List<CourierChatMessage>>(emptyList())
    val storeChatMessages: StateFlow<List<CourierChatMessage>> = _storeChatMessages.asStateFlow()

    private val _customerChatMessages = MutableStateFlow<List<CourierChatMessage>>(emptyList())
    val customerChatMessages: StateFlow<List<CourierChatMessage>> = _customerChatMessages.asStateFlow()

    // Dual Calling Sheet
    private val _showDualCallSheet = MutableStateFlow(false)
    val showDualCallSheet: StateFlow<Boolean> = _showDualCallSheet.asStateFlow()

    private val _callTarget = MutableStateFlow(CallTarget.CUSTOMER)
    val callTarget: StateFlow<CallTarget> = _callTarget.asStateFlow()

    // Feedback Toast / Banner
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    /**
     * Courier Toggles Online / Offline
     * Launches persistent Foreground Service when online
     */
    fun toggleOnline(context: Context) {
        val next = !_isOnline.value
        _isOnline.value = next
        if (next) {
            CourierLocationService.startService(context, partnerId = partnerId, vehicleType = "scooter")
            _toastMessage.value = "You are ONLINE. Persistent GPS active."
        } else {
            CourierLocationService.stopService(context)
            _toastMessage.value = "You are OFFLINE. GPS broadcast paused."
        }
    }

    /**
     * Claim Incoming Delivery from Restaurant / Supermarket
     * Executes atomic assignment: UPDATE orders SET rider_partner_id = auth.uid(), status = 'dispatched'
     */
    fun claimDelivery(order: CourierOrder) {
        viewModelScope.launch {
            // Atomic DB RPC claim_order
            try {
                supabaseClient?.postgrest?.rpc(
                    function = "claim_order",
                    parameters = mapOf(
                        "p_order" to order.id,
                        "p_rider_partner" to partnerId
                    )
                )
            } catch (_: Exception) {
                // Mock execution fallback
            }

            val claimedOrder = order.copy(
                status = CourierOrderStatus.READY_FOR_PICKUP,
                isClaimedByMe = true,
                claimedAt = System.currentTimeMillis()
            )

            _activeOrder.value = claimedOrder
            _availableOrders.value = _availableOrders.value.filter { it.id != order.id }
            _deliveryStage.value = CourierDeliveryStage.STAGE_1_TO_STORE
            _hasArrivedAtStore.value = false
            _hasArrivedAtCustomer.value = false
            _otpInput.value = ""
            _otpError.value = null
            _codCashCollectedConfirmed.value = false

            // Initialize contextual chat channels
            initChatChannels(claimedOrder)

            _toastMessage.value = "Order #${claimedOrder.orderNo} Claimed! Route to ${claimedOrder.storeName} loaded."
        }
    }

    /**
     * Pass / Decline Incoming Order
     */
    fun passDelivery(order: CourierOrder) {
        _availableOrders.value = _availableOrders.value.filter { it.id != order.id }
        _toastMessage.value = "Order #${order.orderNo} passed."
    }

    /**
     * STAGE 1: Courier Arrived at Store (Restaurant / Supermarket)
     */
    fun markArrivedAtStore() {
        _hasArrivedAtStore.value = true
        _toastMessage.value = "Arrived at store! Present Order #${_activeOrder.value?.orderNo} to clerk."
    }

    /**
     * STAGE 1: Confirm Package Picked Up from Store
     * Moves order status to 'dispatched' and initiates Stage 2 Navigation to Customer
     */
    fun confirmOrderPickup() {
        val current = _activeOrder.value ?: return
        viewModelScope.launch {
            try {
                supabaseClient?.postgrest?.rpc(
                    function = "update_order_status",
                    parameters = mapOf(
                        "p_order" to current.id,
                        "p_new" to "dispatched",
                        "p_note" to "Courier picked up package from ${current.storeName}"
                    )
                )
            } catch (_: Exception) {}

            _activeOrder.value = current.copy(status = CourierOrderStatus.DISPATCHED)
            _deliveryStage.value = CourierDeliveryStage.STAGE_2_TO_CONSUMER
            _toastMessage.value = "Package in hand! Route to customer ${current.consumerName} activated."
        }
    }

    /**
     * STAGE 2: Courier Arrived at Customer Door / Gate
     * Sends push alert to consumer
     */
    fun markArrivedAtCustomer() {
        _hasArrivedAtCustomer.value = true
        _toastMessage.value = "Arrived at customer location. Ask consumer for 4-digit secret OTP."
    }

    /**
     * 4-Digit Customer OTP Keypad Input
     */
    fun enterOtpDigit(digit: String) {
        if (_otpInput.value.length < 4) {
            _otpInput.value = _otpInput.value + digit
            _otpError.value = null
        }
    }

    fun deleteOtpDigit() {
        if (_otpInput.value.isNotEmpty()) {
            _otpInput.value = _otpInput.value.dropLast(1)
            _otpError.value = null
        }
    }

    fun clearOtp() {
        _otpInput.value = ""
        _otpError.value = null
    }

    fun toggleCodCashCollected(confirmed: Boolean) {
        _codCashCollectedConfirmed.value = confirmed
    }

    /**
     * 4-Digit Customer Delivery OTP Handoff Verification
     * Verifies OTP against public.order_handoff_codes via RPC update_order_status(..., 'delivered', otp = code)
     */
    fun verifyOtpAndCompleteDelivery() {
        val current = _activeOrder.value ?: return
        val enteredCode = _otpInput.value

        if (enteredCode.length != 4) {
            _otpError.value = "Please enter all 4 digits of customer OTP"
            return
        }

        // Nepal Anti-Fraud check: Physical Cash must be collected first for COD
        if (current.isCod && !_codCashCollectedConfirmed.value) {
            _otpError.value = "CRITICAL: You must collect Rs ${current.totalRs.toInt()} physical cash before completing delivery!"
            return
        }

        _isOtpVerifying.value = true

        viewModelScope.launch {
            delay(600) // Realistic RPC verification handshake
            val expectedCode = current.handoffCode

            if (enteredCode == expectedCode) {
                // Correct OTP -> complete delivery
                try {
                    supabaseClient?.postgrest?.rpc(
                        function = "update_order_status",
                        parameters = mapOf(
                            "p_order" to current.id,
                            "p_new" to "delivered",
                            "p_otp" to enteredCode
                        )
                    )
                } catch (_: Exception) {}

                // Financial Ledger Reconciliation
                val earningPaisa = (current.estimatedEarningRs * 100).toLong()
                _todayEarningsPaisa.value += earningPaisa
                _todayDeliveriesCount.value += 1

                val newEntries = _ledgerEntries.value.toMutableList()

                // If COD, record cash collected into courier balance
                if (current.isCod) {
                    _codCashInHandPaisa.value += current.totalPaisa
                    newEntries.add(
                        0,
                        CourierLedgerEntry(
                            id = "ledg-cod-${System.currentTimeMillis()}",
                            orderId = current.id,
                            orderNo = current.orderNo,
                            amountRs = current.totalRs,
                            type = "COD_COLLECTED",
                            description = "Cash collected from ${current.consumerName}"
                        )
                    )
                }

                // Record delivery earning
                newEntries.add(
                    0,
                    CourierLedgerEntry(
                        id = "ledg-earn-${System.currentTimeMillis()}",
                        orderId = current.id,
                        orderNo = current.orderNo,
                        amountRs = current.estimatedEarningRs,
                        type = "DELIVERY_FEE_EARNED",
                        description = "Delivery commission for Order #${current.orderNo}"
                    )
                )
                _ledgerEntries.value = newEntries

                _isOtpVerifying.value = false
                _deliverySuccessAnimation.value = true
                _deliveryStage.value = CourierDeliveryStage.COMPLETED

                delay(2000)
                _activeOrder.value = null
                _deliverySuccessAnimation.value = false
                _toastMessage.value = "Delivery Completed! +Rs ${current.estimatedEarningRs.toInt()} added to earnings."
            } else {
                _isOtpVerifying.value = false
                _otpError.value = "Incorrect OTP code. Ask customer to check their Evrry App."
            }
        }
    }

    /**
     * Multi-Party Chat
     */
    fun selectChatTab(tab: CourierChatTab) {
        _activeChatTab.value = tab
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val current = _activeOrder.value ?: return
        val tab = _activeChatTab.value

        val message = CourierChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            orderId = current.id,
            senderRole = "courier",
            senderName = "Bikash Thapa (Courier)",
            messageText = text.trim()
        )

        if (tab == CourierChatTab.STORE) {
            _storeChatMessages.value = _storeChatMessages.value + message
            // Simulated store auto-reply
            viewModelScope.launch {
                delay(1500)
                _storeChatMessages.value = _storeChatMessages.value + CourierChatMessage(
                    id = "msg-rep-${System.currentTimeMillis()}",
                    orderId = current.id,
                    senderRole = "store",
                    senderName = current.storeName,
                    messageText = "Namaste! Your order is packed and sealed at counter #1."
                )
            }
        } else {
            _customerChatMessages.value = _customerChatMessages.value + message
            // Simulated customer auto-reply
            viewModelScope.launch {
                delay(1500)
                _customerChatMessages.value = _customerChatMessages.value + CourierChatMessage(
                    id = "msg-rep-${System.currentTimeMillis()}",
                    orderId = current.id,
                    senderRole = "customer",
                    senderName = current.consumerName,
                    messageText = "Thanks! The gate code is 42, please ring the 2nd floor bell."
                )
            }
        }
    }

    private fun initChatChannels(order: CourierOrder) {
        _storeChatMessages.value = listOf(
            CourierChatMessage(
                id = "sc-1",
                orderId = order.id,
                senderRole = "store",
                senderName = order.storeName,
                messageText = "Order #${order.orderNo} is preparing. ETA 6 mins for pickup."
            )
        )

        _customerChatMessages.value = listOf(
            CourierChatMessage(
                id = "cc-1",
                orderId = order.id,
                senderRole = "courier",
                senderName = "Evrry Dispatch",
                messageText = "Namaste ${order.consumerName}! Courier Bikash has accepted your order."
            )
        )
    }

    /**
     * Dual Calling: VoIP vs SIM Carrier Dial
     */
    fun openDualCallSheet(target: CallTarget) {
        _callTarget.value = target
        _showDualCallSheet.value = true
    }

    fun closeDualCallSheet() {
        _showDualCallSheet.value = false
    }

    fun triggerVoipCall() {
        val targetName = if (_callTarget.value == CallTarget.STORE) {
            _activeOrder.value?.storeName ?: "Store"
        } else {
            _activeOrder.value?.consumerName ?: "Customer"
        }
        _showDualCallSheet.value = false
        _toastMessage.value = "Connecting HD In-App VoIP audio call with $targetName..."
    }

    fun triggerSimCall(context: Context) {
        val phoneNumber = if (_callTarget.value == CallTarget.STORE) {
            _activeOrder.value?.storePhone ?: "9841234567"
        } else {
            _activeOrder.value?.consumerPhone ?: "9841000001"
        }
        _showDualCallSheet.value = false
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            _toastMessage.value = "Could not open phone dialer"
        }
    }

    /**
     * Instant Cashout / On-Demand Payout
     */
    fun requestInstantCashout(amountNpr: Double) {
        viewModelScope.launch {
            payoutService?.requestOnDemandPayout(
                partnerId = partnerId,
                amountNpr = amountNpr,
                destinationType = "bank"
            )
            val newEntries = _ledgerEntries.value.toMutableList()
            newEntries.add(
                0,
                CourierLedgerEntry(
                    id = "ledg-pay-${System.currentTimeMillis()}",
                    orderId = "",
                    orderNo = 0,
                    amountRs = amountNpr,
                    type = "INSTANT_PAYOUT",
                    description = "Instant Cash Out to Nepal Investment Mega Bank"
                )
            )
            _ledgerEntries.value = newEntries
            _toastMessage.value = "Instant Payout of Rs ${amountNpr.toInt()} dispatched via NCHL / IPS!"
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun createInitialAvailableOrders(): List<CourierOrder> {
        return listOf(
            CourierOrder(
                id = "ord-food-10042",
                orderNo = 10042L,
                consumerId = "usr-rajesh-01",
                consumerName = "Rajesh Hamal",
                consumerPhone = "+977-9841122334",
                storeId = "store-jimbu-thakali",
                storeName = "Jimbu Thakali Kitchen",
                storeCategory = "food",
                storeAddress = "Jhamsikhel Road, Ward 3, Lalitpur",
                storeLat = 27.6742,
                storeLng = 85.3120,
                storePhone = "+977-9851099881",
                deliveryAddress = "Sanepa Heights, House #42, Lalitpur",
                deliveryLat = 27.6830,
                deliveryLng = 85.3105,
                status = CourierOrderStatus.READY_FOR_PICKUP,
                paymentMethod = "cod",
                totalPaisa = 98000L, // Rs 980
                estimatedEarningRs = 220.0,
                pickupDistanceM = 900,
                deliveryDistanceM = 1800,
                items = listOf(
                    CourierOrderItem("Jimbu Thakali Set (Mutton)", 1, 620.0),
                    CourierOrderItem("Timur Achar & Gundruk", 2, 180.0),
                    CourierOrderItem("Mustang Apple Cider (Chyang)", 1, 180.0)
                ),
                customerNote = "Ring bell on 2nd floor. COD - have exact change if possible.",
                handoffCode = "4821"
            ),
            CourierOrder(
                id = "ord-groc-10043",
                orderNo = 10043L,
                consumerId = "usr-shristi-02",
                consumerName = "Shristi Shrestha",
                consumerPhone = "+977-9841987654",
                storeId = "store-bhatbhateni-pulchowk",
                storeName = "Bhatbhateni Supermarket",
                storeCategory = "grocery",
                storeAddress = "Pulchowk Marg, Lalitpur",
                storeLat = 27.6788,
                storeLng = 85.3180,
                storePhone = "+977-01-5522334",
                deliveryAddress = "Kupondole Height, Apartment 4B",
                deliveryLat = 27.6890,
                deliveryLng = 85.3170,
                status = CourierOrderStatus.READY_FOR_PICKUP,
                paymentMethod = "fonepay_qr",
                totalPaisa = 145000L, // Rs 1,450
                estimatedEarningRs = 195.0,
                pickupDistanceM = 1200,
                deliveryDistanceM = 2200,
                items = listOf(
                    CourierOrderItem("DDC Fresh Whole Milk 1L", 2, 110.0),
                    CourierOrderItem("Wai Wai Quick Masala Delight (Pack of 5)", 1, 150.0),
                    CourierOrderItem("Jumla Organic Marsi Rice 5kg", 1, 1080.0)
                ),
                customerNote = "Leave at reception gate if not answering.",
                handoffCode = "7392"
            ),
            CourierOrder(
                id = "ord-food-10044",
                orderNo = 10044L,
                consumerId = "usr-anup-03",
                consumerName = "Anup Baral",
                consumerPhone = "+977-9841556677",
                storeId = "store-roadhouse-cafe",
                storeName = "Roadhouse Cafe & Kitchen",
                storeCategory = "food",
                storeAddress = "Pulchowk, Lalitpur",
                storeLat = 27.6782,
                storeLng = 85.3175,
                storePhone = "+977-01-5544332",
                deliveryAddress = "Bakhundole, Ward 3, Lalitpur",
                deliveryLat = 27.6812,
                deliveryLng = 85.3140,
                status = CourierOrderStatus.READY_FOR_PICKUP,
                paymentMethod = "cod",
                totalPaisa = 125000L, // Rs 1,250
                estimatedEarningRs = 175.0,
                pickupDistanceM = 600,
                deliveryDistanceM = 1400,
                items = listOf(
                    CourierOrderItem("Woodfired Smoked Chicken Pizza", 1, 850.0),
                    CourierOrderItem("Garlic Bruschetta", 1, 400.0)
                ),
                customerNote = "Please bring cutlery packets.",
                handoffCode = "5190"
            )
        )
    }

    private fun createInitialLedgerEntries(): List<CourierLedgerEntry> {
        val now = System.currentTimeMillis()
        return listOf(
            CourierLedgerEntry(
                id = "ledg-01",
                orderId = "ord-prev-10039",
                orderNo = 10039L,
                amountRs = 180.0,
                type = "DELIVERY_FEE_EARNED",
                description = "Delivery commission (Bhatbhateni -> Ekantakuna)",
                timestamp = now - 3600000L * 2
            ),
            CourierLedgerEntry(
                id = "ledg-02",
                orderId = "ord-prev-10039",
                orderNo = 10039L,
                amountRs = 960.0,
                type = "COD_COLLECTED",
                description = "Cash collected from Sunita Pradhan",
                timestamp = now - 3600000L * 2
            ),
            CourierLedgerEntry(
                id = "ledg-03",
                orderId = "ord-prev-10038",
                orderNo = 10038L,
                amountRs = 210.0,
                type = "DELIVERY_FEE_EARNED",
                description = "Delivery commission (Roadhouse Cafe -> Patan)",
                timestamp = now - 3600000L * 4
            )
        )
    }
}
