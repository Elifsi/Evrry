//
//  DriverMobilityModels.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Mobility Driver Ride Lifecycle & Negotiation Models
//

import SwiftUI
import CoreLocation

public enum DriverRideStatus: String, Codable {
    case requested = "requested"
    case bidding = "bidding"
    case accepted = "accepted"
    case arrived = "arrived"
    case inProgress = "in_progress"
    case completed = "completed"
    case cancelled = "cancelled"
    
    public var label: String {
        switch self {
        case .requested: return "Ride Requested"
        case .bidding: return "Open for Bids"
        case .accepted: return "En Route to Pickup"
        case .arrived: return "Arrived at Pickup"
        case .inProgress: return "Trip in Progress to Drop"
        case .completed: return "Trip Completed"
        case .cancelled: return "Trip Cancelled"
        }
    }
    
    public var color: Color {
        switch self {
        case .requested: return Color.blue
        case .bidding: return Color.orange
        case .accepted: return Color(red: 0.05, green: 0.58, blue: 0.53)
        case .arrived: return Color.green
        case .inProgress: return Color(red: 0.31, green: 0.27, blue: 0.90)
        case .completed: return Color.green
        case .cancelled: return Color.red
        }
    }
}

public struct DriverRideRequest: Identifiable, Equatable {
    public let id: String
    public let passengerId: String
    public let passengerName: String
    public let passengerPhone: String
    public let passengerRating: Double
    public let pickupAddress: String
    public let dropAddress: String
    public let pickupLat: Double
    public let pickupLng: Double
    public let dropLat: Double
    public let dropLng: Double
    public var distanceKm: Double
    public var durationMinutes: Int
    public var offeredFareNpr: Double
    public var finalFareNpr: Double
    public var vehicleType: String
    public var status: DriverRideStatus
    public var startOtp: String
    public var routePolyline: String
    public var myBidFareNpr: Double?
    public var myBidStatus: String? // "pending", "accepted", "rejected"
    public var paymentMethod: String
    
    public init(
        id: String = "ride_ktm_8812",
        passengerId: String = "usr_nepal_4492",
        passengerName: String = "Aayush Shrestha",
        passengerPhone: String = "+9779812987654",
        passengerRating: Double = 4.92,
        pickupAddress: String = "Thamel Marg, Kathmandu",
        dropAddress: String = "Patan Durbar Square, Lalitpur",
        pickupLat: Double = 27.7154,
        pickupLng: Double = 85.3123,
        dropLat: Double = 27.6727,
        dropLng: Double = 85.3253,
        distanceKm: Double = 6.4,
        durationMinutes: Int = 18,
        offeredFareNpr: Double = 165.0,
        finalFareNpr: Double = 165.0,
        vehicleType: String = "bike",
        status: DriverRideStatus = .bidding,
        startOtp: String = "1234",
        routePolyline: String = "",
        myBidFareNpr: Double? = nil,
        myBidStatus: String? = nil,
        paymentMethod: String = "cod"
    ) {
        self.id = id
        self.passengerId = passengerId
        self.passengerName = passengerName
        self.passengerPhone = passengerPhone
        self.passengerRating = passengerRating
        self.pickupAddress = pickupAddress
        self.dropAddress = dropAddress
        self.pickupLat = pickupLat
        self.pickupLng = pickupLng
        self.dropLat = dropLat
        self.dropLng = dropLng
        self.distanceKm = distanceKm
        self.durationMinutes = durationMinutes
        self.offeredFareNpr = offeredFareNpr
        self.finalFareNpr = finalFareNpr
        self.vehicleType = vehicleType
        self.status = status
        self.startOtp = startOtp
        self.routePolyline = routePolyline
        self.myBidFareNpr = myBidFareNpr
        self.myBidStatus = myBidStatus
        self.paymentMethod = paymentMethod
    }
    
    public var pickupCoordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: pickupLat, longitude: pickupLng)
    }
    
    public var dropCoordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: dropLat, longitude: dropLng)
    }
}

public struct DriverLocationState: Equatable {
    public var partnerId: String
    public var vehicleType: String
    public var latitude: Double
    public var longitude: Double
    public var bearing: Double
    public var speedKmh: Int
    public var isOnline: Bool
    
    public init(
        partnerId: String = "partner_ktm_001",
        vehicleType: String = "bike",
        latitude: Double = 27.7145,
        longitude: Double = 85.3123,
        bearing: Double = 45.0,
        speedKmh: Int = 28,
        isOnline: Bool = true
    ) {
        self.partnerId = partnerId
        self.vehicleType = vehicleType
        self.latitude = latitude
        self.longitude = longitude
        self.bearing = bearing
        self.speedKmh = speedKmh
        self.isOnline = isOnline
    }
    
    public var coordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
    }
}

public struct FareBreakdown: Equatable {
    public let baseFareNpr: Double
    public let distanceFareNpr: Double
    public let totalFareNpr: Double
    public let platformCommissionNpr: Double
    public let driverNetNpr: Double
    public let paymentMethod: String
    
    public init(
        baseFareNpr: Double,
        distanceFareNpr: Double,
        totalFareNpr: Double,
        platformCommissionNpr: Double,
        driverNetNpr: Double,
        paymentMethod: String = "Cash on Delivery (COD)"
    ) {
        self.baseFareNpr = baseFareNpr
        self.distanceFareNpr = distanceFareNpr
        self.totalFareNpr = totalFareNpr
        self.platformCommissionNpr = platformCommissionNpr
        self.driverNetNpr = driverNetNpr
        self.paymentMethod = paymentMethod
    }
}
