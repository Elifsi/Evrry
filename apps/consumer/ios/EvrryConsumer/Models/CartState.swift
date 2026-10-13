//
//  CartState.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Cart and Multi-Vendor Checkout Models
//

import Foundation

public enum PaymentMethod: String, CaseIterable, Identifiable {
    case esewa = "esewa"
    case khalti = "khalti"
    case fonepay = "fonepay"
    case cod = "cod"
    
    public var id: String { rawValue }
    
    public var label: String {
        switch self {
        case .esewa: return "eSewa Mobile Wallet"
        case .khalti: return "Khalti Digital Wallet"
        case .fonepay: return "Fonepay Direct QR / Bank"
        case .cod: return "Cash on Delivery"
        }
    }
    
    public var badge: String {
        switch self {
        case .esewa: return "Nepal #1 Wallet"
        case .khalti: return "Instant Transfer"
        case .fonepay: return "All Nepal Banks"
        case .cod: return "Pay Rider on Arrival"
        }
    }
}

public struct CartItem: Identifiable, Codable, Equatable {
    public var id: String { item.id }
    public let item: CatalogItem
    public var quantity: Int
    public var selectedOptions: [String]
    
    public init(item: CatalogItem, quantity: Int = 1, selectedOptions: [String] = []) {
        self.item = item
        self.quantity = quantity
        self.selectedOptions = selectedOptions
    }
    
    public var totalNpr: Double {
        item.priceNpr * Double(quantity)
    }
}

public struct DeliveryAddress: Identifiable, Codable, Equatable, Hashable {
    public let id: String
    public let title: String
    public let detail: String
    public let landmark: String?
    public let isDefault: Bool
    
    public init(id: String, title: String, detail: String, landmark: String? = nil, isDefault: Bool = false) {
        self.id = id
        self.title = title
        self.detail = detail
        self.landmark = landmark
        self.isDefault = isDefault
    }
}

public struct DefaultAddresses {
    public static let home = DeliveryAddress(
        id: "addr-home",
        title: "Home",
        detail: "Ward 3, Jhamsikhel, Lalitpur",
        landmark: "Near British School",
        isDefault: true
    )
    public static let office = DeliveryAddress(
        id: "addr-office",
        title: "Work",
        detail: "Level 4, Trade Tower, Thapathali, Kathmandu",
        landmark: "Opposite Nepal Rastra Bank",
        isDefault: false
    )
    public static let all: [DeliveryAddress] = [home, office]
}

public enum OrderFulfillmentType: String, CaseIterable, Identifiable {
    case delivery = "delivery"
    case pickup = "pickup"
    
    public var id: String { rawValue }
}

public struct CartState: Equatable {
    public var items: [CartItem] = []
    public var fulfillmentType: OrderFulfillmentType = .delivery
    public var deliveryFeeNpr: Double = 50.0
    public var platformFeeNpr: Double = 10.0
    public var discountNpr: Double = 0.0
    public var specialInstructions: String = ""
    public var selectedAddress: DeliveryAddress = DefaultAddresses.home
    
    public init(
        items: [CartItem] = [],
        fulfillmentType: OrderFulfillmentType = .delivery,
        deliveryFeeNpr: Double = 50.0,
        platformFeeNpr: Double = 10.0,
        discountNpr: Double = 0.0,
        specialInstructions: String = "",
        selectedAddress: DeliveryAddress = DefaultAddresses.home
    ) {
        self.items = items
        self.fulfillmentType = fulfillmentType
        self.deliveryFeeNpr = deliveryFeeNpr
        self.platformFeeNpr = platformFeeNpr
        self.discountNpr = discountNpr
        self.specialInstructions = specialInstructions
        self.selectedAddress = selectedAddress
    }
    
    public var subtotalNpr: Double {
        items.reduce(0) { $0 + $1.totalNpr }
    }
    
    public var isFreeDeliveryEligible: Bool {
        subtotalNpr >= 1000.0
    }
    
    public var amountNeededForFreeDelivery: Double {
        max(0.0, 1000.0 - subtotalNpr)
    }
    
    public var freeDeliveryProgress: Double {
        max(0.0, min(1.0, subtotalNpr / 1000.0))
    }
    
    public var effectiveDeliveryFeeNpr: Double {
        items.isEmpty ? 0.0 : ((fulfillmentType == .pickup || isFreeDeliveryEligible) ? 0.0 : deliveryFeeNpr)
    }
    
    public var effectivePlatformFeeNpr: Double {
        items.isEmpty ? 0.0 : platformFeeNpr
    }
    
    public var totalNpr: Double {
        if items.isEmpty { return 0.0 }
        return max(0.0, subtotalNpr + effectiveDeliveryFeeNpr + effectivePlatformFeeNpr - discountNpr)
    }
    
    public var subtotal: Double {
        subtotalNpr
    }
    
    public var finalPayableNpr: Double {
        totalNpr
    }
    
    public var vendorName: String {
        activeGroceryVendorName ?? activeVendorName ?? "Store"
    }
    
    public var itemCount: Int {
        items.reduce(0) { $0 + $1.quantity }
    }
    
    public var foodItems: [CartItem] {
        items.filter { $0.item.vertical == "food" }
    }
    
    public var groceryItems: [CartItem] {
        items.filter { $0.item.vertical == "grocery" }
    }
    
    public var activeVendorId: String? {
        items.first?.item.vendorId
    }
    
    public var activeVendorName: String? {
        items.first?.item.vendorName
    }
    
    public var activeFoodVendorId: String? {
        foodItems.first?.item.vendorId
    }
    
    public var activeFoodVendorName: String? {
        foodItems.first?.item.vendorName
    }
    
    public var activeGroceryVendorId: String? {
        groceryItems.first?.item.vendorId
    }
    
    public var activeGroceryVendorName: String? {
        groceryItems.first?.item.vendorName
    }
}
