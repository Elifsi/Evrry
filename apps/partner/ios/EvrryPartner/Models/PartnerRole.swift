//
//  PartnerRole.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Unified 4-Role Adaptive App Architecture
//

import SwiftUI

public enum PartnerRole: String, CaseIterable, Identifiable, Codable {
    case driver = "driver"
    case restaurant = "restaurant"
    case grocery = "grocery"
    case rider = "rider"
    
    public var id: String { rawValue }
    
    public var title: String {
        switch self {
        case .driver: return "Ride Driver"
        case .restaurant: return "Restaurant Kitchen"
        case .grocery: return "Grocery Supermarket"
        case .rider: return "Delivery Courier"
        }
    }
    
    public var subtitle: String {
        switch self {
        case .driver: return "Mobility Passenger Transit"
        case .restaurant: return "Kitchen Display System (KDS)"
        case .grocery: return "Supermarket Fulfillment Mode"
        case .rider: return "Multi-Stop Courier Delivery"
        }
    }
    
    public var emoji: String {
        switch self {
        case .driver: return "🚗"
        case .restaurant: return "🍳"
        case .grocery: return "🛒"
        case .rider: return "🛵"
        }
    }
    
    public var description: String {
        switch self {
        case .driver:
            return "Accept bike, cab, or comfort EV passenger rides with InDrive-style live fare negotiation and OSRM turn-by-turn routing."
        case .restaurant:
            return "Kitchen Display System (KDS), preparation timers, instant 86'ing out-of-stock items, and ESC/POS thermal printing."
        case .grocery:
            return "Order pick-and-pack fulfillment checklist, barcode SKU scanning, inventory adjustments, and substitution alerts."
        case .rider:
            return "Claim ready store orders, navigate 2-stage multi-stop routing (Store Pickup → Customer Drop), handoff OTP verification, and COD ledger."
        }
    }
    
    public var accentColor: Color {
        switch self {
        case .driver: return Color(red: 0.14, green: 0.39, blue: 0.92)
        case .restaurant: return Color(red: 0.97, green: 0.45, blue: 0.09)
        case .grocery: return Color(red: 0.06, green: 0.73, blue: 0.51)
        case .rider: return Color(red: 0.55, green: 0.36, blue: 0.96)
        }
    }
}

public struct PartnerProfile: Codable, Equatable {
    public var id: String
    public var ownerId: String
    public var type: PartnerRole
    public var status: String
    public var legalName: String
    public var tradeName: String
    public var contactPhone: String
    public var rating: Double
    public var tripsCompleted: Int
    public var isOnline: Bool
    
    public init(
        id: String = "partner_ktm_001",
        ownerId: String = "usr_nepal_001",
        type: PartnerRole = .driver,
        status: String = "active",
        legalName: String = "Bikash Tamang",
        tradeName: String = "Bikash Mobility Express",
        contactPhone: String = "+9779841234567",
        rating: Double = 4.95,
        tripsCompleted: Int = 1840,
        isOnline: Bool = true
    ) {
        self.id = id
        self.ownerId = ownerId
        self.type = type
        self.status = status
        self.legalName = legalName
        self.tradeName = tradeName
        self.contactPhone = contactPhone
        self.rating = rating
        self.tripsCompleted = tripsCompleted
        self.isOnline = isOnline
    }
}

public struct DriverKycData: Equatable {
    public var licenseNumber: String
    public var licenseExpiry: String
    public var bluebookNumber: String
    public var vehiclePlate: String
    public var vehicleModel: String
    public var vehicleType: String
    public var licensePhotoPath: String?
    public var bluebookPhotoPath: String?
    public var vehiclePhotoPath: String?
    
    public init(
        licenseNumber: String = "01-06-00981245",
        licenseExpiry: String = "2029-05-14",
        bluebookNumber: String = "BB-KTM-88129",
        vehiclePlate: String = "BA 2 PA 4920",
        vehicleModel: String = "Bajaj Pulsar 150",
        vehicleType: String = "bike",
        licensePhotoPath: String? = nil,
        bluebookPhotoPath: String? = nil,
        vehiclePhotoPath: String? = nil
    ) {
        self.licenseNumber = licenseNumber
        self.licenseExpiry = licenseExpiry
        self.bluebookNumber = bluebookNumber
        self.vehiclePlate = vehiclePlate
        self.vehicleModel = vehicleModel
        self.vehicleType = vehicleType
        self.licensePhotoPath = licensePhotoPath
        self.bluebookPhotoPath = bluebookPhotoPath
        self.vehiclePhotoPath = vehiclePhotoPath
    }
}
