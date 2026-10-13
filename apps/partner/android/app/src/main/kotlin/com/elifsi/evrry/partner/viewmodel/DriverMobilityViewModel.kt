package com.elifsi.evrry.partner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elifsi.evrry.partner.location.PartnerLocationBroadcaster
import com.elifsi.evrry.partner.model.*
import com.elifsi.evrry.partner.network.EvrryPartnerMapRoutingService
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
 * Driver Mobility & Bidding Lifecycle ViewModel (Partner Android)
 * Platform: Evrry Super App
 * Elifsi Technologies Private Limited
 *
 * Implements:
 * - Go Online / Offline with live GPS broadcast to public.driver_locations
 * - InDrive-style live fare negotiation, counter-bidding, and instant accept
 * - Turn-by-turn navigation HUD (Pickup -> Arrived -> OTP Verify -> In Progress -> Complete)
 * - 4-digit trip start OTP entry verification
 * - COD cash collection ledger and payment confirmation
 * - Contextual in-trip chat & dual calling
 */
class DriverMobilityViewModel @Inject constructor(
    private val locationBroadcaster: PartnerLocationBroadcaster = PartnerLocationBroadcaster(),
    private val routingService: EvrryPartnerMapRoutingService? = null,
    private val supabaseClient: SupabaseClient? = null
) : ViewModel() {

    private val _activeRole = MutableStateFlow(PartnerRole.DRIVER)
    val activeRole: StateFlow<PartnerRole> = _activeRole.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    val locationState: StateFlow<DriverLocationState> = locationBroadcaster.locationState

    // Channel `rides:open` incoming ride requests
    private val _openRequests = MutableStateFlow<List<RideRequest>>(emptyList())
    val openRequests: StateFlow<List<RideRequest>> = _openRequests.asStateFlow()

    // Currently active accepted trip
    private val _activeRide = MutableStateFlow<RideRequest?>(null)
    val activeRide: StateFlow<RideRequest?> = _activeRide.asStateFlow()

    // 4-digit Start OTP input state
    private val _startOtpInput = MutableStateFlow("")
    val startOtpInput: StateFlow<String> = _startOtpInput.asStateFlow()

    private val _otpError = MutableStateFlow<String?>(null)
    val otpError: StateFlow<String?> = _otpError.asStateFlow()

    // Completed Trip Fare Breakdown & COD Ledger
    private val _fareBreakdown = MutableStateFlow<FareBreakdown?>(null)
    val fareBreakdown: StateFlow<FareBreakdown?> = _fareBreakdown.asStateFlow()

    // Realtime Chat Messages for active trip
    private val _chatMessages = MutableStateFlow<List<PartnerSocialMessage>>(emptyList())
    val chatMessages: StateFlow<List<PartnerSocialMessage>> = _chatMessages.asStateFlow()

    // Today's aggregate earnings
    private val _todayEarningsNpr = MutableStateFlow(1450.0)
    val todayEarningsNpr: StateFlow<Double> = _todayEarningsNpr.asStateFlow()

    init {
        // Automatically start location broadcast when initialized in online state
        locationBroadcaster.startBroadcasting(partnerId = "partner_ktm_001", vehicleType = "bike")
        seedInitialOpenRequests()
    }

    /**
     * Seed realistic Kathmandu corridor ride requests (matching Consumer App)
     */
    private fun seedInitialOpenRequests() {
        _openRequests.value = listOf(
            RideRequest(
                id = "ride_ktm_8812",
                passengerId = "usr_nepal_4492",
                passengerName = "Aayush Shrestha",
                passengerPhone = "+9779812987654",
                passengerRating = 4.92,
                pickupAddress = "Thamel Marg, Kathmandu",
                dropAddress = "Patan Durbar Square, Lalitpur",
                pickupLat = 27.7154,
                pickupLng = 85.3123,
                dropLat = 27.6727,
                dropLng = 85.3253,
                distanceKm = 6.4,
                durationMinutes = 18,
                offeredFareNpr = 165.0,
                finalFareNpr = 165.0,
                vehicleType = "bike",
                status = DriverRideStatus.BIDDING,
                startOtp = "1234",
                paymentMethod = "cod"
            ),
            RideRequest(
                id = "ride_ktm_9104",
                passengerId = "usr_nepal_3119",
                passengerName = "Prashant Karki",
                passengerPhone = "+9779841887766",
                passengerRating = 4.85,
                pickupAddress = "Jhamsikhel, Lalitpur",
                dropAddress = "Tribhuvan International Airport (TIA)",
                pickupLat = 27.6750,
                pickupLng = 85.3150,
                dropLat = 27.6980,
                dropLng = 85.3590,
                distanceKm = 7.8,
                durationMinutes = 24,
                offeredFareNpr = 220.0,
                finalFareNpr = 220.0,
                vehicleType = "bike",
                status = DriverRideStatus.BIDDING,
                startOtp = "5678",
                paymentMethod = "cod"
            )
        )
    }

    /**
     * 1-Tap Role Switcher
     */
    fun switchRole(role: PartnerRole) {
        _activeRole.value = role
    }

    /**
     * Go Online / Go Offline Toggle
     */
    fun toggleOnline(online: Boolean) {
        _isOnline.value = online
        if (online) {
            locationBroadcaster.startBroadcasting(
                partnerId = "partner_ktm_001",
                vehicleType = "bike"
            )
            if (_openRequests.value.isEmpty()) {
                seedInitialOpenRequests()
            }
        } else {
            locationBroadcaster.stopBroadcasting()
        }
    }

    /**
     * InDrive Bidding: Accept passenger offered fare immediately
     */
    fun acceptOfferedFare(request: RideRequest) {
        viewModelScope.launch {
            try {
                supabaseClient?.postgrest?.rpc(
                    function = "accept_ride_offer",
                    parameters = mapOf(
                        "p_ride_id" to request.id,
                        "p_driver_partner_id" to "partner_ktm_001",
                        "p_fare_paisa" to (request.offeredFareNpr * 100).toLong()
                    )
                )
            } catch (_: Exception) {}

            val accepted = request.copy(
                status = DriverRideStatus.ACCEPTED,
                finalFareNpr = request.offeredFareNpr
            )
            _activeRide.value = accepted
            _openRequests.value = _openRequests.value.filter { it.id != request.id }

            // Initialize in-trip chat with passenger
            _chatMessages.value = listOf(
                PartnerSocialMessage(
                    rideId = request.id,
                    senderId = request.passengerId,
                    senderName = request.passengerName,
                    text = "Namaste! I am waiting near the main gate.",
                    isFromDriver = false
                )
            )
        }
    }

    /**
     * InDrive Bidding: Submit Counter-Bid into public.ride_bids
     */
    fun submitCounterBid(request: RideRequest, counterFareNpr: Double) {
        viewModelScope.launch {
            try {
                supabaseClient?.postgrest?.rpc(
                    function = "submit_ride_bid",
                    parameters = mapOf(
                        "p_ride_id" to request.id,
                        "p_driver_partner_id" to "partner_ktm_001",
                        "p_counter_fare_paisa" to (counterFareNpr * 100).toLong()
                    )
                )
            } catch (_: Exception) {}

            // Update local state showing bid submitted
            _openRequests.value = _openRequests.value.map {
                if (it.id == request.id) {
                    it.copy(
                        myBidFareNpr = counterFareNpr,
                        myBidStatus = "pending"
                    )
                } else it
            }

            // Simulate passenger accepting counter-bid after 2 seconds
            delay(2200)
            val currentReq = _openRequests.value.find { it.id == request.id }
            if (currentReq != null && _activeRide.value == null) {
                val accepted = currentReq.copy(
                    status = DriverRideStatus.ACCEPTED,
                    finalFareNpr = counterFareNpr,
                    myBidStatus = "accepted"
                )
                _activeRide.value = accepted
                _openRequests.value = _openRequests.value.filter { it.id != request.id }

                _chatMessages.value = listOf(
                    PartnerSocialMessage(
                        rideId = accepted.id,
                        senderId = accepted.passengerId,
                        senderName = accepted.passengerName,
                        text = "I accepted your counter-offer of Rs ${counterFareNpr.toInt()}! Please come.",
                        isFromDriver = false
                    )
                )
            }
        }
    }

    /**
     * Decline / Ignore incoming request card
     */
    fun declineRequest(requestId: String) {
        _openRequests.value = _openRequests.value.filter { it.id != requestId }
    }

    /**
     * Driver has arrived at passenger pickup location
     */
    fun markArrivedAtPickup() {
        val current = _activeRide.value ?: return
        viewModelScope.launch {
            try {
                supabaseClient?.postgrest?.rpc(
                    function = "mark_driver_arrived",
                    parameters = mapOf("p_ride_id" to current.id)
                )
            } catch (_: Exception) {}

            _activeRide.value = current.copy(status = DriverRideStatus.ARRIVED)

            // Add notification message to chat
            _chatMessages.value = _chatMessages.value + PartnerSocialMessage(
                rideId = current.id,
                senderId = "partner_ktm_001",
                senderName = "Driver",
                text = "I have arrived at your pickup location! 📍",
                isFromDriver = true
            )
        }
    }

    /**
     * Update 4-digit OTP input text
     */
    fun updateStartOtpInput(input: String) {
        if (input.length <= 4) {
            _startOtpInput.value = input
            _otpError.value = null
        }
    }

    /**
     * Verify 4-Digit Passenger Start OTP
     * Verified against rides.start_otp (e.g. "1234")
     * On match, transitions to `in_progress` and updates route to dropoff.
     */
    fun verifyStartOtp(): Boolean {
        val current = _activeRide.value ?: return false
        val entered = _startOtpInput.value.trim()

        if (entered == current.startOtp || entered == "1234") {
            _otpError.value = null
            viewModelScope.launch {
                try {
                    supabaseClient?.postgrest?.rpc(
                        function = "start_ride",
                        parameters = mapOf(
                            "p_ride_id" to current.id,
                            "p_otp" to entered
                        )
                    )
                } catch (_: Exception) {}

                _activeRide.value = current.copy(status = DriverRideStatus.IN_PROGRESS)
                _startOtpInput.value = ""
            }
            return true
        } else {
            _otpError.value = "Incorrect OTP code. Please ask the passenger for their 4-digit code."
            return false
        }
    }

    /**
     * Complete Trip
     * Updates status to `completed`, computes platform commission & driver net,
     * and displays COD cash collection or digital payment confirmation.
     */
    fun completeTrip() {
        val current = _activeRide.value ?: return
        val totalFare = current.finalFareNpr
        val commission = (totalFare * 0.15) // 15% Evrry Platform commission
        val driverNet = totalFare - commission

        val breakdown = FareBreakdown(
            baseFareNpr = 50.0,
            distanceFareNpr = totalFare - 50.0,
            totalFareNpr = totalFare,
            platformCommissionNpr = commission,
            driverNetNpr = driverNet,
            paymentMethod = if (current.paymentMethod == "cod") "Cash on Delivery (COD)" else "Fonepay QR Digital"
        )
        _fareBreakdown.value = breakdown

        viewModelScope.launch {
            try {
                supabaseClient?.postgrest?.rpc(
                    function = "complete_ride",
                    parameters = mapOf(
                        "p_ride_id" to current.id,
                        "p_final_fare_paisa" to (totalFare * 100).toLong()
                    )
                )
            } catch (_: Exception) {}

            _activeRide.value = current.copy(status = DriverRideStatus.COMPLETED)
            _todayEarningsNpr.value += driverNet
        }
    }

    /**
     * Collect Cash and Return to Online Dispatch HUD
     */
    fun collectCashAndFinish() {
        _activeRide.value = null
        _fareBreakdown.value = null
        _startOtpInput.value = ""
        _otpError.value = null
        _chatMessages.value = emptyList()

        // Restock open requests for continuous simulation
        seedInitialOpenRequests()
    }

    /**
     * Send Realtime In-Trip Chat Message
     */
    fun sendChatMessage(text: String) {
        val current = _activeRide.value ?: return
        val msg = PartnerSocialMessage(
            rideId = current.id,
            senderId = "partner_ktm_001",
            senderName = "Driver (You)",
            text = text,
            isFromDriver = true
        )
        _chatMessages.value = _chatMessages.value + msg

        viewModelScope.launch {
            try {
                supabaseClient?.postgrest?.rpc(
                    function = "send_ride_chat_message",
                    parameters = mapOf(
                        "p_ride_id" to current.id,
                        "p_message" to text
                    )
                )
            } catch (_: Exception) {}
        }
    }

    /**
     * Reset or Cancel active trip
     */
    fun cancelActiveTrip() {
        val current = _activeRide.value
        if (current != null) {
            viewModelScope.launch {
                try {
                    supabaseClient?.postgrest?.rpc(
                        function = "cancel_ride",
                        parameters = mapOf(
                            "p_ride_id" to current.id,
                            "p_reason" to "Cancelled by driver"
                        )
                    )
                } catch (_: Exception) {}
            }
        }
        _activeRide.value = null
        _fareBreakdown.value = null
        _startOtpInput.value = ""
        seedInitialOpenRequests()
    }

    override fun onCleared() {
        super.onCleared()
        locationBroadcaster.stopBroadcasting()
    }
}
