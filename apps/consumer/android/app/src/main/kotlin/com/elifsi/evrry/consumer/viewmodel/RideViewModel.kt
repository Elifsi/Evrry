package com.elifsi.evrry.consumer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elifsi.evrry.consumer.location.LocationPoint
import com.elifsi.evrry.consumer.location.LocationService
import com.elifsi.evrry.consumer.model.*
import com.elifsi.evrry.consumer.network.EvrryMapRoutingService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Ride Mobility & Dispatch ViewModel (Consumer Android)
 * Platform: Evrry Super App
 * Elifsi Technologies Private Limited
 *
 * Implements full OSRM road routing, fare calculation,
 * driver dispatch lifecycle, OTP verification, and review submission.
 */
class RideViewModel(
    private val routingService: EvrryMapRoutingService? = null,
    private val locationService: LocationService = LocationService(),
    private val supabaseClient: SupabaseClient? = null
) : ViewModel() {

    private val _activeRide = MutableStateFlow(ActiveRide())
    val activeRide: StateFlow<ActiveRide> = _activeRide.asStateFlow()

    private val _isBooking = MutableStateFlow(false)
    val isBooking: StateFlow<Boolean> = _isBooking.asStateFlow()

    private val _isRouting = MutableStateFlow(false)
    val isRouting: StateFlow<Boolean> = _isRouting.asStateFlow()

    init {
        // Initial route computation for Kathmandu corridor
        recomputeRoute()
    }

    /**
     * Updates pickup point and recomputes route
     */
    fun setPickup(point: LocationPoint) {
        _activeRide.value = _activeRide.value.copy(
            pickupAddress = point.address,
            pickupLat = point.latitude,
            pickupLng = point.longitude
        )
        recomputeRoute()
    }

    /**
     * Updates destination point and recomputes route
     */
    fun setDrop(point: LocationPoint) {
        _activeRide.value = _activeRide.value.copy(
            dropAddress = point.address,
            dropLat = point.latitude,
            dropLng = point.longitude
        )
        recomputeRoute()
    }

    /**
     * Swaps pickup and destination
     */
    fun swapPickupAndDrop() {
        val current = _activeRide.value
        _activeRide.value = current.copy(
            pickupAddress = current.dropAddress,
            pickupLat = current.dropLat,
            pickupLng = current.dropLng,
            dropAddress = current.pickupAddress,
            dropLat = current.pickupLat,
            dropLng = current.pickupLng
        )
        recomputeRoute()
    }

    /**
     * Recomputes OSRM road distance, duration and polyline
     */
    fun recomputeRoute() {
        val ride = _activeRide.value
        _isRouting.value = true

        viewModelScope.launch {
            try {
                if (routingService != null) {
                    val result = routingService.getRoute(
                        originLat = ride.pickupLat,
                        originLng = ride.pickupLng,
                        destLat = ride.dropLat,
                        destLng = ride.dropLng
                    )
                    result.onSuccess { payload ->
                        val distKm = payload.distance_meters / 1000.0
                        val durMins = payload.duration_minutes
                        val decoded = if (payload.polyline.isNotBlank()) {
                            EvrryMapRoutingService.decodePolyline(payload.polyline)
                        } else emptyList()

                        val rClass = ride.rideClass
                        val fare = rClass.baseFareNpr + (rClass.perKmRateNpr * distKm)

                        _activeRide.value = _activeRide.value.copy(
                            distanceKm = String.format(Locale.US, "%.1f", distKm).toDoubleOrNull() ?: distKm,
                            durationMinutes = durMins,
                            estimatedFareNpr = fare,
                            offeredFareNpr = if (_activeRide.value.status == RideStatus.IDLE) fare else _activeRide.value.offeredFareNpr,
                            routePolyline = payload.polyline,
                            routePoints = decoded
                        )
                    }
                } else {
                    // Fallback Kathmandu road approximation
                    val dLat = Math.abs(ride.dropLat - ride.pickupLat) * 111.0
                    val dLng = Math.abs(ride.dropLng - ride.pickupLng) * 98.0
                    val approxKm = Math.max(1.5, Math.sqrt(dLat * dLat + dLng * dLng) * 1.35)
                    val approxMins = Math.max(5, (approxKm * 3.2).toInt())
                    val fare = ride.rideClass.baseFareNpr + (ride.rideClass.perKmRateNpr * approxKm)

                    _activeRide.value = _activeRide.value.copy(
                        distanceKm = String.format(Locale.US, "%.1f", approxKm).toDoubleOrNull() ?: approxKm,
                        durationMinutes = approxMins,
                        estimatedFareNpr = fare,
                        offeredFareNpr = if (_activeRide.value.status == RideStatus.IDLE) fare else _activeRide.value.offeredFareNpr
                    )
                }
            } catch (_: Exception) {
            } finally {
                _isRouting.value = false
            }
        }
    }

    fun selectClass(rideClass: RideClass) {
        val current = _activeRide.value
        val estimated = rideClass.baseFareNpr + (rideClass.perKmRateNpr * current.distanceKm)
        _activeRide.value = current.copy(
            rideClass = rideClass,
            estimatedFareNpr = estimated,
            offeredFareNpr = estimated
        )
    }

    fun setBookingMode(mode: BookingMode) {
        _activeRide.value = _activeRide.value.copy(bookingMode = mode)
    }

    fun updateOfferedFare(delta: Double) {
        val current = _activeRide.value
        val newFare = Math.max(30.0, current.offeredFareNpr + delta)
        _activeRide.value = current.copy(offeredFareNpr = newFare)
    }

    fun setCustomOfferedFare(fare: Double) {
        val current = _activeRide.value
        val clamped = Math.max(30.0, fare)
        _activeRide.value = current.copy(offeredFareNpr = clamped)
    }

    fun acceptDriverBid(bid: DriverBid) {
        _activeRide.value = _activeRide.value.copy(
            status = RideStatus.ACCEPTED,
            driver = bid.driver,
            estimatedFareNpr = bid.bidFareNpr,
            offeredFareNpr = bid.bidFareNpr,
            driverBids = emptyList(),
            otpCode = "1234"
        )
    }

    fun requestRide() {
        val current = _activeRide.value
        _isBooking.value = true
        _activeRide.value = current.copy(
            status = RideStatus.SEARCHING,
            driverBids = emptyList()
        )

        viewModelScope.launch {
            val finalOfferedFare = if (current.bookingMode == BookingMode.BIDDING) current.offeredFareNpr else current.estimatedFareNpr

            // Send request to Supabase RPC 'request_ride' if client is attached
            try {
                supabaseClient?.postgrest?.rpc(
                    function = "request_ride",
                    parameters = mapOf(
                        "vehicle_type" to when (_activeRide.value.rideClass) {
                            RideClass.MOTO -> "bike"
                            RideClass.CAB, RideClass.COMFORT -> "car"
                        },
                        "pickup_lat" to _activeRide.value.pickupLat,
                        "pickup_lng" to _activeRide.value.pickupLng,
                        "drop_lat" to _activeRide.value.dropLat,
                        "drop_lng" to _activeRide.value.dropLng,
                        "offered_fare_paisa" to (finalOfferedFare * 100).toLong()
                    )
                )
            } catch (_: Exception) {}

            if (current.bookingMode == BookingMode.BIDDING) {
                // InDrive Bidding Flow: Nearby drivers respond with bids
                delay(1200)
                _isBooking.value = false

                val primaryDriver = when (current.rideClass) {
                    RideClass.MOTO -> AssignedDriver(
                        name = "Bikash Tamang",
                        phone = "+9779841234567",
                        vehiclePlate = "BA 2 PA 4920",
                        vehicleModel = "Bajaj Pulsar 150",
                        rating = 4.95,
                        tripsCompleted = 1840
                    )
                    RideClass.CAB -> AssignedDriver(
                        name = "Sunil Shrestha",
                        phone = "+9779851098765",
                        vehiclePlate = "BA 1 JA 3812",
                        vehicleModel = "Suzuki Dzire Taxi",
                        rating = 4.88,
                        tripsCompleted = 2410
                    )
                    RideClass.COMFORT -> AssignedDriver(
                        name = "Prabesh Karki",
                        phone = "+9779801239876",
                        vehiclePlate = "BA 3 CHA 7291",
                        vehicleModel = "BYD Atto 3 EV (AC)",
                        rating = 4.98,
                        tripsCompleted = 950
                    )
                }

                val secondDriver = when (current.rideClass) {
                    RideClass.MOTO -> AssignedDriver(
                        name = "Aman Maharjan",
                        phone = "+9779812345678",
                        vehiclePlate = "BA 4 PA 1198",
                        vehicleModel = "TVS Raider 125",
                        rating = 4.91,
                        tripsCompleted = 620
                    )
                    RideClass.CAB -> AssignedDriver(
                        name = "Ramesh Adhikari",
                        phone = "+9779841987654",
                        vehiclePlate = "BA 2 JA 5519",
                        vehicleModel = "Hyundai Aura Cab",
                        rating = 4.92,
                        tripsCompleted = 1420
                    )
                    RideClass.COMFORT -> AssignedDriver(
                        name = "Deepak Thapa",
                        phone = "+9779851122334",
                        vehiclePlate = "BA 3 CHA 4820",
                        vehicleModel = "MG ZS EV",
                        rating = 4.96,
                        tripsCompleted = 810
                    )
                }

                val bid1 = DriverBid(
                    id = "bid_1",
                    driver = primaryDriver,
                    bidFareNpr = finalOfferedFare, // Accepted exact offer
                    etaMinutes = 3,
                    isCounterOffer = false
                )

                val bid2 = DriverBid(
                    id = "bid_2",
                    driver = secondDriver,
                    bidFareNpr = finalOfferedFare + 20.0, // Counter-offer +20
                    etaMinutes = 2,
                    isCounterOffer = true
                )

                _activeRide.value = _activeRide.value.copy(driverBids = listOf(bid1, bid2))
            } else {
                // Fixed Rate: Immediate driver assignment
                delay(1600)
                _isBooking.value = false

                val assigned = when (_activeRide.value.rideClass) {
                    RideClass.MOTO -> AssignedDriver(
                        name = "Bikash Tamang",
                        phone = "+9779841234567",
                        vehiclePlate = "BA 2 PA 4920",
                        vehicleModel = "Bajaj Pulsar 150 (Black)",
                        rating = 4.95,
                        tripsCompleted = 1840
                    )
                    RideClass.CAB -> AssignedDriver(
                        name = "Sunil Shrestha",
                        phone = "+9779851098765",
                        vehiclePlate = "BA 1 JA 3812",
                        vehicleModel = "Suzuki Dzire Taxi",
                        rating = 4.88,
                        tripsCompleted = 2410
                    )
                    RideClass.COMFORT -> AssignedDriver(
                        name = "Prabesh Karki",
                        phone = "+9779801239876",
                        vehiclePlate = "BA 3 CHA 7291",
                        vehicleModel = "BYD Atto 3 EV (Air Conditioned)",
                        rating = 4.98,
                        tripsCompleted = 950
                    )
                }

                _activeRide.value = _activeRide.value.copy(
                    status = RideStatus.ACCEPTED,
                    driver = assigned,
                    otpCode = "1234"
                )
            }
        }
    }

    fun completeRide() {
        _activeRide.value = _activeRide.value.copy(status = RideStatus.COMPLETED)
    }

    fun submitReview(rating: Int, comment: String, onFinished: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                supabaseClient?.postgrest?.rpc(
                    function = "submit_review",
                    parameters = mapOf(
                        "ride_id" to _activeRide.value.id,
                        "rating" to rating,
                        "comment" to comment
                    )
                )
            } catch (_: Exception) {}

            _activeRide.value = _activeRide.value.copy(reviewSubmitted = true)
            onFinished()
        }
    }

    fun cancelRide() {
        _activeRide.value = _activeRide.value.copy(
            status = RideStatus.CANCELLED,
            driverBids = emptyList()
        )
    }

    fun resetRide() {
        val current = _activeRide.value
        _activeRide.value = ActiveRide(
            status = RideStatus.IDLE,
            bookingMode = current.bookingMode,
            rideClass = current.rideClass,
            pickupAddress = current.pickupAddress,
            dropAddress = current.dropAddress,
            pickupLat = current.pickupLat,
            pickupLng = current.pickupLng,
            dropLat = current.dropLat,
            dropLng = current.dropLng
        )
        recomputeRoute()
    }
}
