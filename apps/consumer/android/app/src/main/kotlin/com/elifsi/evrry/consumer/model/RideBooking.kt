package com.elifsi.evrry.consumer.model

import kotlinx.serialization.Serializable

/**
 * Ride Mobility and Driver Dispatch Models
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */

enum class RideClass(
    val id: String,
    val title: String,
    val emoji: String,
    val baseFareNpr: Double,
    val perKmRateNpr: Double,
    val capacity: String
) {
    MOTO("moto", "Evrry Moto", "🏍️", 50.0, 18.0, "1 Person"),
    CAB("cab", "City Cab", "🚖", 120.0, 45.0, "4 People"),
    COMFORT("comfort", "Comfort EV", "⚡", 180.0, 60.0, "4 People · AC")
}

enum class RideStatus(val label: String, val badgeColor: Long) {
    IDLE("Ready to Book", 0xFF64748B),
    SEARCHING("Matching Nearby Driver…", 0xFF005EFF),
    ACCEPTED("Driver Assigned & En Route", 0xFF0D9488),
    ARRIVED("Driver Arrived at Pickup", 0xFF16A34A),
    TRIP_ACTIVE("Trip in Progress to Drop", 0xFF4F46E5),
    COMPLETED("Trip Completed", 0xFF16A34A),
    CANCELLED("Trip Cancelled", 0xFFEF4444)
}

@Serializable
data class AssignedDriver(
    val name: String = "Bikash Tamang",
    val phone: String = "+9779841234567",
    val vehiclePlate: String = "BA 2 PA 4920",
    val vehicleModel: String = "Bajaj Pulsar 150",
    val rating: Double = 4.95,
    val tripsCompleted: Int = 1840,
    val photoUrl: String? = null,
    val isVerified: Boolean = true
)

enum class BookingMode(val title: String, val subtitle: String) {
    FIXED_FARE("Fixed Rate", "Standard upfront fare · Instant match"),
    BIDDING("Offer Your Fare", "InDrive-style bidding · Drivers counter-offer")
}

@Serializable
data class DriverBid(
    val id: String,
    val driver: AssignedDriver,
    val bidFareNpr: Double,
    val etaMinutes: Int = 3,
    val isCounterOffer: Boolean = false
)

data class ActiveRide(
    val id: String = "ride_ktm_8812",
    val rideClass: RideClass = RideClass.MOTO,
    val bookingMode: BookingMode = BookingMode.FIXED_FARE,
    val pickupAddress: String = "Thamel Marg, Kathmandu",
    val dropAddress: String = "Patan Durbar Square, Lalitpur",
    val pickupLat: Double = 27.7154,
    val pickupLng: Double = 85.3123,
    val dropLat: Double = 27.6727,
    val dropLng: Double = 85.3253,
    val distanceKm: Double = 6.4,
    val durationMinutes: Int = 18,
    val estimatedFareNpr: Double = 165.0,
    val offeredFareNpr: Double = 165.0,
    val otpCode: String = "1234",
    val status: RideStatus = RideStatus.IDLE,
    val driver: AssignedDriver? = null,
    val driverBids: List<DriverBid> = emptyList(),
    val routePolyline: String = "",
    val routePoints: List<Pair<Double, Double>> = emptyList(),
    val reviewSubmitted: Boolean = false
)
