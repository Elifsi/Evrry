package com.elifsi.evrry.partner.model

import kotlinx.serialization.Serializable

/**
 * Driver Mobility & Bidding Models (Partner Android)
 * Platform: Evrry Super App
 * Elifsi Technologies Private Limited
 *
 * Aligned with public.rides, public.ride_bids, and public.driver_locations schemas
 */

enum class DriverRideStatus(val dbValue: String, val label: String, val badgeColor: Long) {
    REQUESTED("requested", "Ride Requested", 0xFF3B82F6),
    BIDDING("bidding", "Open for Bids", 0xFFF59E0B),
    ACCEPTED("accepted", "En Route to Pickup", 0xFF0D9488),
    ARRIVED("arrived", "Arrived at Pickup", 0xFF16A34A),
    IN_PROGRESS("in_progress", "Trip in Progress to Drop", 0xFF4F46E5),
    COMPLETED("completed", "Trip Completed", 0xFF16A34A),
    CANCELLED("cancelled", "Trip Cancelled", 0xFFEF4444);

    companion object {
        fun fromDb(value: String): DriverRideStatus =
            entries.find { it.dbValue.equals(value, ignoreCase = true) } ?: BIDDING
    }
}

enum class DriverVehicleType(val code: String, val title: String, val emoji: String) {
    BIKE("bike", "Moto Ride", "🏍️"),
    CAB("cab", "City Cab", "🚖"),
    COMFORT_EV("comfort_ev", "Comfort EV", "⚡");

    companion object {
        fun fromCode(code: String): DriverVehicleType =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: BIKE
    }
}

@Serializable
data class RideRequest(
    val id: String = "ride_ktm_8812",
    val passengerId: String = "usr_nepal_4492",
    val passengerName: String = "Aayush Shrestha",
    val passengerPhone: String = "+9779812987654",
    val passengerRating: Double = 4.92,
    val pickupAddress: String = "Thamel Marg, Kathmandu",
    val dropAddress: String = "Patan Durbar Square, Lalitpur",
    val pickupLat: Double = 27.7154,
    val pickupLng: Double = 85.3123,
    val dropLat: Double = 27.6727,
    val dropLng: Double = 85.3253,
    val distanceKm: Double = 6.4,
    val durationMinutes: Int = 18,
    val offeredFareNpr: Double = 165.0,
    val finalFareNpr: Double = 165.0,
    val vehicleType: String = "bike",
    val status: DriverRideStatus = DriverRideStatus.BIDDING,
    val startOtp: String = "1234",
    val routePolyline: String = "",
    val myBidFareNpr: Double? = null,
    val myBidStatus: String? = null, // "pending", "accepted", "rejected", "withdrawn"
    val paymentMethod: String = "cod" // "cod" | "fonepay_qr"
)

@Serializable
data class DriverLocationState(
    val partnerId: String = "partner_ktm_001",
    val vehicleType: String = "bike",
    val latitude: Double = 27.7130,
    val longitude: Double = 85.3110,
    val bearing: Float = 45f,
    val speedKmh: Int = 28,
    val isOnline: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Serializable
data class FareBreakdown(
    val baseFareNpr: Double,
    val distanceFareNpr: Double,
    val totalFareNpr: Double,
    val platformCommissionNpr: Double,
    val driverNetNpr: Double,
    val paymentMethod: String = "Cash on Delivery (COD)"
)
