//
//  RideBooking.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Ride Mobility and Driver Dispatch Models
//

import SwiftUI
import CoreLocation

public enum RideClass: String, CaseIterable, Identifiable {
    case moto = "moto"
    case cab = "cab"
    case comfort = "comfort"
    
    public var id: String { rawValue }
    
    public var title: String {
        switch self {
        case .moto: return "Evrry Moto"
        case .cab: return "City Cab"
        case .comfort: return "Comfort EV"
        }
    }
    
    public var emoji: String {
        switch self {
        case .moto: return "🏍️"
        case .cab: return "🚖"
        case .comfort: return "⚡"
        }
    }
    
    public var baseFareNpr: Double {
        switch self {
        case .moto: return 50.0
        case .cab: return 120.0
        case .comfort: return 180.0
        }
    }
    
    public var perKmRateNpr: Double {
        switch self {
        case .moto: return 18.0
        case .cab: return 45.0
        case .comfort: return 60.0
        }
    }
    
    public var capacity: String {
        switch self {
        case .moto: return "1 Person"
        case .cab: return "4 People"
        case .comfort: return "4 People · AC"
        }
    }
}

public enum RideStatus: String, Codable {
    case idle
    case searching
    case accepted
    case arrived
    case tripActive
    case completed
    case cancelled
    
    public var label: String {
        switch self {
        case .idle: return "Ready to Book"
        case .searching: return "Matching Nearby Driver…"
        case .accepted: return "Driver Assigned & En Route"
        case .arrived: return "Driver Arrived at Pickup"
        case .tripActive: return "Trip in Progress to Drop"
        case .completed: return "Trip Completed"
        case .cancelled: return "Trip Cancelled"
        }
    }
    
    public var color: Color {
        switch self {
        case .idle: return Color(red: 0.39, green: 0.45, blue: 0.55)
        case .searching: return Color(red: 0.0, green: 0.37, blue: 1.0)
        case .accepted: return Color(red: 0.05, green: 0.58, blue: 0.53)
        case .arrived: return Color(red: 0.09, green: 0.64, blue: 0.29)
        case .tripActive: return Color(red: 0.31, green: 0.27, blue: 0.90)
        case .completed: return Color(red: 0.09, green: 0.64, blue: 0.29)
        case .cancelled: return Color.red
        }
    }
}

public struct AssignedDriver: Codable, Equatable {
    public let name: String
    public let phone: String
    public let vehiclePlate: String
    public let vehicleModel: String
    public let rating: Double
    public let tripsCompleted: Int
    public let photoUrl: String?
    public let isVerified: Bool
    
    public init(
        name: String = "Bikash Tamang",
        phone: String = "+9779841234567",
        vehiclePlate: String = "BA 2 PA 4920",
        vehicleModel: "Bajaj Pulsar 150",
        rating: Double = 4.95,
        tripsCompleted: Int = 1840,
        photoUrl: String? = nil,
        isVerified: Bool = true
    ) {
        self.name = name
        self.phone = phone
        self.vehiclePlate = vehiclePlate
        self.vehicleModel = vehicleModel
        self.rating = rating
        self.tripsCompleted = tripsCompleted
        self.photoUrl = photoUrl
        self.isVerified = isVerified
    }
}

public enum BookingMode: String, CaseIterable, Identifiable {
    case fixedFare = "fixed_fare"
    case bidding = "bidding"
    
    public var id: String { rawValue }
    public var title: String {
        switch self {
        case .fixedFare: return "⚡ Fixed Rate"
        case .bidding: return "🤝 InDrive Bidding"
        }
    }
}

public struct DriverBid: Identifiable, Equatable {
    public let id: String
    public let driver: AssignedDriver
    public let bidFareNpr: Double
    public let etaMinutes: Int
    public let isCounterOffer: Bool
    
    public init(
        id: String,
        driver: AssignedDriver,
        bidFareNpr: Double,
        etaMinutes: Int = 3,
        isCounterOffer: Bool = false
    ) {
        self.id = id
        self.driver = driver
        self.bidFareNpr = bidFareNpr
        self.etaMinutes = etaMinutes
        self.isCounterOffer = isCounterOffer
    }
}

public struct ActiveRide: Equatable {
    public let id: String
    public var rideClass: RideClass
    public var bookingMode: BookingMode
    public var pickupAddress: String
    public var dropAddress: String
    public var pickupLat: Double
    public var pickupLng: Double
    public var dropLat: Double
    public var dropLng: Double
    public var distanceKm: Double
    public var durationMinutes: Int
    public var estimatedFareNpr: Double
    public var offeredFareNpr: Double
    public var otpCode: String
    public var status: RideStatus
    public var driver: AssignedDriver?
    public var driverBids: [DriverBid]
    public var routePolyline: String
    public var reviewSubmitted: Bool
    
    public init(
        id: String = "ride_ktm_8812",
        rideClass: RideClass = .moto,
        bookingMode: BookingMode = .fixedFare,
        pickupAddress: String = "Thamel Marg, Kathmandu",
        dropAddress: String = "Patan Durbar Square, Lalitpur",
        pickupLat: Double = 27.7154,
        pickupLng: Double = 85.3123,
        dropLat: Double = 27.6727,
        dropLng: Double = 85.3253,
        distanceKm: Double = 6.4,
        durationMinutes: Int = 18,
        estimatedFareNpr: Double = 165.0,
        offeredFareNpr: Double = 165.0,
        otpCode: String = "1234",
        status: RideStatus = .idle,
        driver: AssignedDriver? = nil,
        driverBids: [DriverBid] = [],
        routePolyline: String = "",
        reviewSubmitted: Bool = false
    ) {
        self.id = id
        self.rideClass = rideClass
        self.bookingMode = bookingMode
        self.pickupAddress = pickupAddress
        self.dropAddress = dropAddress
        self.pickupLat = pickupLat
        self.pickupLng = pickupLng
        self.dropLat = dropLat
        self.dropLng = dropLng
        self.distanceKm = distanceKm
        self.durationMinutes = durationMinutes
        self.estimatedFareNpr = estimatedFareNpr
        self.offeredFareNpr = offeredFareNpr
        self.otpCode = otpCode
        self.status = status
        self.driver = driver
        self.driverBids = driverBids
        self.routePolyline = routePolyline
        self.reviewSubmitted = reviewSubmitted
    }
    
    public var pickupCoordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: pickupLat, longitude: pickupLng)
    }
    
    public var dropCoordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: dropLat, longitude: dropLng)
    }
}
