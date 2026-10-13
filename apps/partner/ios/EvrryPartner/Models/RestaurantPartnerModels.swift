//
//  RestaurantPartnerModels.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Restaurant Kitchen Partner Domain Models & KDS State
//

import Foundation
import SwiftUI

public enum RestaurantOrderStatus: String, Codable, CaseIterable {
    case acknowledged = "acknowledged"
    case preparing = "preparing"
    case readyForPickup = "ready_for_pickup"
    case dispatched = "dispatched"
    case delivered = "delivered"
    case cancelled = "cancelled"
    case rejected = "rejected"
    
    public var title: String {
        switch self {
        case .acknowledged: return "Incoming"
        case .preparing: return "Preparing"
        case .readyForPickup: return "Ready for Pickup"
        case .dispatched: return "Dispatched"
        case .delivered: return "Delivered"
        case .cancelled: return "Cancelled"
        case .rejected: return "Rejected"
        }
    }
}

public struct RestaurantStore: Identifiable, Codable, Equatable {
    public var id: String
    public var partnerId: String
    public var name: String
    public var category: String
    public var isActive: Bool
    public var openingHours: String
    public var avgPrepTimeMinutes: Int
    public var rating: Double
    public var address: String
    public var panNumber: String
    public var phone: String
    
    public init(
        id: String = "store_ktm_momo_01",
        partnerId: String = "partner_rest_01",
        name: String = "Himalayan Momo & Thakali Kitchen",
        category: String = "food",
        isActive: Bool = true,
        openingHours: String = "10:00 AM - 10:00 PM",
        avgPrepTimeMinutes: Int = 20,
        rating: Double = 4.88,
        address: String = "Jhamsikhel Rd, Lalitpur",
        panNumber: String = "602918234",
        phone: String = "+977 1-5549080"
    ) {
        self.id = id
        self.partnerId = partnerId
        self.name = name
        self.category = category
        self.isActive = isActive
        self.openingHours = openingHours
        self.avgPrepTimeMinutes = avgPrepTimeMinutes
        self.rating = rating
        self.address = address
        self.panNumber = panNumber
        self.phone = phone
    }
}

public struct RestaurantOrderItem: Identifiable, Codable, Equatable {
    public var id: String
    public var name: String
    public var quantity: Int
    public var unitPricePaisa: LongInt
    public var lineTotalPaisa: LongInt
    public var customerNotes: String?
    
    public typealias LongInt = Int64
    
    public init(
        id: String = UUID().uuidString,
        name: String,
        quantity: Int,
        unitPricePaisa: Int64,
        lineTotalPaisa: Int64,
        customerNotes: String? = nil
    ) {
        self.id = id
        self.name = name
        self.quantity = quantity
        self.unitPricePaisa = unitPricePaisa
        self.lineTotalPaisa = lineTotalPaisa
        self.customerNotes = customerNotes
    }
}

public struct RestaurantOrder: Identifiable, Codable, Equatable {
    public var id: String
    public var orderNo: String
    public var consumerId: String
    public var consumerName: String
    public var consumerPhone: String
    public var riderPartnerId: String?
    public var riderName: String?
    public var riderPhone: String?
    public var riderDistanceKm: Double?
    public var riderVehicleType: String?
    public var status: RestaurantOrderStatus
    public var totalPaisa: Int64
    public var customerNote: String?
    public var placedAtEpochSeconds: Int64
    public var prepTimeMinutes: Int
    public var acceptedAtEpochSeconds: Int64?
    public var items: [RestaurantOrderItem]
    public var paymentMethod: String // "cod" or "prepaid"
    
    public init(
        id: String = UUID().uuidString,
        orderNo: String,
        consumerId: String = "c_001",
        consumerName: String = "Aayush Shrestha",
        consumerPhone: String = "+977 9841987654",
        riderPartnerId: String? = nil,
        riderName: String? = nil,
        riderPhone: String? = nil,
        riderDistanceKm: Double? = nil,
        riderVehicleType: String? = nil,
        status: RestaurantOrderStatus = .acknowledged,
        totalPaisa: Int64 = 85000,
        customerNote: String? = nil,
        placedAtEpochSeconds: Int64 = Int64(Date().timeIntervalSince1970),
        prepTimeMinutes: Int = 20,
        acceptedAtEpochSeconds: Int64? = nil,
        items: [RestaurantOrderItem] = [],
        paymentMethod: String = "cod"
    ) {
        self.id = id
        self.orderNo = orderNo
        self.consumerId = consumerId
        self.consumerName = consumerName
        self.consumerPhone = consumerPhone
        self.riderPartnerId = riderPartnerId
        self.riderName = riderName
        self.riderPhone = riderPhone
        self.riderDistanceKm = riderDistanceKm
        self.riderVehicleType = riderVehicleType
        self.status = status
        self.totalPaisa = totalPaisa
        self.customerNote = customerNote
        self.placedAtEpochSeconds = placedAtEpochSeconds
        self.prepTimeMinutes = prepTimeMinutes
        self.acceptedAtEpochSeconds = acceptedAtEpochSeconds
        self.items = items
        self.paymentMethod = paymentMethod
    }
    
    public var totalNpr: Double {
        return Double(totalPaisa) / 100.0
    }
    
    public func remainingPrepSeconds(now: Date = Date()) -> Int {
        guard let acceptedAt = acceptedAtEpochSeconds, status == .preparing else {
            return prepTimeMinutes * 60
        }
        let totalAllowedSec = prepTimeMinutes * 60
        let elapsedSec = Int(Int64(now.timeIntervalSince1970) - acceptedAt)
        return max(0, totalAllowedSec - elapsedSec)
    }
    
    public func prepProgress(now: Date = Date()) -> Double {
        guard let acceptedAt = acceptedAtEpochSeconds, status == .preparing else {
            return 0.0
        }
        let totalAllowedSec = Double(prepTimeMinutes * 60)
        guard totalAllowedSec > 0 else { return 1.0 }
        let elapsedSec = Double(Int64(now.timeIntervalSince1970) - acceptedAt)
        return min(1.0, max(0.0, elapsedSec / totalAllowedSec))
    }
    
    public func isOverdue(now: Date = Date()) -> Bool {
        guard let acceptedAt = acceptedAtEpochSeconds, status == .preparing else {
            return false
        }
        let totalAllowedSec = prepTimeMinutes * 60
        let elapsedSec = Int(Int64(now.timeIntervalSince1970) - acceptedAt)
        return elapsedSec > totalAllowedSec
    }
    
    public func formattedRemainingTime(now: Date = Date()) -> String {
        let sec = remainingPrepSeconds(now: now)
        let mins = sec / 60
        let secs = sec % 60
        return String(format: "%02d:%02d", mins, secs)
    }
}

public struct RestaurantCatalogItem: Identifiable, Codable, Equatable {
    public var id: String
    public var storeId: String
    public var name: String
    public var category: String
    public var pricePaisa: Int64
    public var isAvailable: Bool
    public var description: String?
    
    public init(
        id: String,
        storeId: String = "store_ktm_momo_01",
        name: String,
        category: String,
        pricePaisa: Int64,
        isAvailable: Bool = true,
        description: String? = nil
    ) {
        self.id = id
        self.storeId = storeId
        self.name = name
        self.category = category
        self.pricePaisa = pricePaisa
        self.isAvailable = isAvailable
        self.description = description
    }
    
    public var priceNpr: Double {
        return Double(pricePaisa) / 100.0
    }
}

public enum KitchenTab: String, CaseIterable, Identifiable {
    case incoming = "Incoming"
    case preparing = "Preparing"
    case readyForPickup = "Ready"
    case completed = "Completed"
    
    public var id: String { rawValue }
    
    public var iconName: String {
        switch self {
        case .incoming: return "bell.badge.fill"
        case .preparing: return "flame.fill"
        case .readyForPickup: return "checkmark.seal.fill"
        case .completed: return "clock.fill"
        }
    }
}

public struct RestaurantChatMessage: Identifiable, Codable, Equatable {
    public var id: String
    public var orderId: String
    public var senderRole: String // "restaurant", "consumer", "courier"
    public var senderName: String
    public var message: String
    public var timestamp: Date
    
    public init(
        id: String = UUID().uuidString,
        orderId: String,
        senderRole: String,
        senderName: String,
        message: String,
        timestamp: Date = Date()
    ) {
        self.id = id
        self.orderId = orderId
        self.senderRole = senderRole
        self.senderName = senderName
        self.message = message
        self.timestamp = timestamp
    }
}

public struct RestaurantKycData: Codable, Equatable {
    public var businessRegistrationNo: String
    public var panVatNumber: String
    public var foodHygienePermitNo: String
    public var restaurantName: String
    public var ownerName: String
    public var storeAddress: String
    public var wardNumber: Int
    public var businessRegDocUploaded: Bool
    public var panVatDocUploaded: Bool
    public var foodHygieneDocUploaded: Bool
    public var isVerified: Bool
    
    public init(
        businessRegistrationNo: String = "REG-2081-FD-9982",
        panVatNumber: String = "602918234",
        foodHygienePermitNo: String = "DFTQC-KTM-2081-332",
        restaurantName: String = "Himalayan Momo & Thakali Kitchen",
        ownerName: String = "Sujan Shakya",
        storeAddress: String = "Jhamsikhel Rd, Ward 3, Lalitpur",
        wardNumber: Int = 3,
        businessRegDocUploaded: Bool = true,
        panVatDocUploaded: Bool = true,
        foodHygieneDocUploaded: Bool = true,
        isVerified: Bool = true
    ) {
        self.businessRegistrationNo = businessRegistrationNo
        self.panVatNumber = panVatNumber
        self.foodHygienePermitNo = foodHygienePermitNo
        self.restaurantName = restaurantName
        self.ownerName = ownerName
        self.storeAddress = storeAddress
        self.wardNumber = wardNumber
        self.businessRegDocUploaded = businessRegDocUploaded
        self.panVatDocUploaded = panVatDocUploaded
        self.foodHygieneDocUploaded = foodHygieneDocUploaded
        self.isVerified = isVerified
    }
}
