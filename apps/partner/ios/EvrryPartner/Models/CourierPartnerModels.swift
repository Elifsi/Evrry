//
//  CourierPartnerModels.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Food & Grocery Delivery Courier Domain Models
//  Grounded in kaaneneskpc_Deliverr & enatega-multivendor-rider
//

import Foundation
import CoreLocation

// MARK: - Courier Order Status
public enum CourierOrderStatus: String, Codable, CaseIterable {
    case preparing = "preparing"
    case readyForPickup = "ready_for_pickup"
    case dispatched = "dispatched"
    case delivered = "delivered"
    case cancelled = "cancelled"
    
    public var label: String {
        switch self {
        case .preparing: return "Preparing in Kitchen"
        case .readyForPickup: return "Ready for Pickup"
        case .dispatched: return "Out for Delivery"
        case .delivered: return "Delivered"
        case .cancelled: return "Cancelled"
        }
    }
}

// MARK: - Delivery Navigation Stage
public enum CourierDeliveryStage: String, Codable {
    case toStore = "to_store"           // Stage 1: Route to Restaurant / Supermarket
    case atStore = "at_store"           // At store: Verifying package & presenting Order #
    case toCustomer = "to_customer"     // Stage 2: Route to Consumer's delivery address
    case atCustomer = "at_customer"     // At customer door: Handoff & 4-digit OTP input
    case completed = "completed"
    
    public var stepTitle: String {
        switch self {
        case .toStore: return "Head to Store Pickup"
        case .atStore: return "Collect Package at Store"
        case .toCustomer: return "Deliver to Customer"
        case .atCustomer: return "Customer Door Handoff"
        case .completed: return "Delivery Completed"
        }
    }
}

// MARK: - Courier Order Item
public struct CourierOrderItem: Identifiable, Codable, Equatable {
    public var id: String
    public var name: String
    public var quantity: Int
    public var unitPricePaisa: Int64
    public var lineTotalPaisa: Int64
    public var instructions: String?
    
    public var unitPriceRs: Double { Double(unitPricePaisa) / 100.0 }
    public var lineTotalRs: Double { Double(lineTotalPaisa) / 100.0 }
    
    public init(
        id: String = UUID().uuidString,
        name: String,
        quantity: Int,
        unitPricePaisa: Int64,
        lineTotalPaisa: Int64? = nil,
        instructions: String? = nil
    ) {
        self.id = id
        self.name = name
        self.quantity = quantity
        self.unitPricePaisa = unitPricePaisa
        self.lineTotalPaisa = lineTotalPaisa ?? (unitPricePaisa * Int64(quantity))
        self.instructions = instructions
    }
}

// MARK: - Courier Order
public struct CourierOrder: Identifiable, Codable, Equatable {
    public var id: String
    public var orderNo: Int64
    public var consumerId: String
    public var consumerName: String
    public var consumerPhone: String
    public var deliveryAddress: String
    public var deliveryLatitude: Double
    public var deliveryLongitude: Double
    
    public var storeId: String
    public var storeName: String
    public var storeCategory: String // "food" or "grocery"
    public var storeAddress: String
    public var storePhone: String
    public var storeLatitude: Double
    public var storeLongitude: Double
    
    public var riderPartnerId: String?
    public var status: CourierOrderStatus
    public var deliveryStage: CourierDeliveryStage
    
    public var paymentMethod: String // "cod", "esewa", "fonepay_qr", "khalti"
    public var isPaid: Bool
    public var totalPaisa: Int64
    public var deliveryFeePaisa: Int64
    public var riderEarningPaisa: Int64
    public var tipPaisa: Int64
    
    public var pickupDistanceMeters: Int
    public var dropoffDistanceMeters: Int
    public var estimatedTimeMinutes: Int
    
    public var specialInstructions: String?
    public var handoffOtpCode: String // 4-digit code e.g. "4821"
    public var items: [CourierOrderItem]
    public var createdAt: Date
    
    public var isCod: Bool {
        paymentMethod.lowercased() == "cod" || !isPaid
    }
    
    public var totalRs: Double { Double(totalPaisa) / 100.0 }
    public var riderEarningRs: Double { Double(riderEarningPaisa) / 100.0 }
    public var pickupDistanceKm: Double { Double(pickupDistanceMeters) / 1000.0 }
    public var dropoffDistanceKm: Double { Double(dropoffDistanceMeters) / 1000.0 }
    public var totalDistanceKm: Double { Double(pickupDistanceMeters + dropoffDistanceMeters) / 1000.0 }
    
    public var storeLocationCoordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: storeLatitude, longitude: storeLongitude)
    }
    
    public var dropoffLocationCoordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: deliveryLatitude, longitude: deliveryLongitude)
    }
    
    public init(
        id: String = "ord_\(UUID().uuidString.prefix(8))",
        orderNo: Int64 = 10428,
        consumerId: String = "c_9981",
        consumerName: String = "Aayush Shrestha",
        consumerPhone: String = "+9779841892110",
        deliveryAddress: String = "House #14, Jhamsikhel Marg, Sanepa, Lalitpur",
        deliveryLatitude: Double = 27.6830,
        deliveryLongitude: Double = 85.3120,
        storeId: String = "store_roadhouse_01",
        storeName: String = "Roadhouse Cafe",
        storeCategory: String = "food",
        storeAddress: String = "Ward 3, Pulchowk Marg, Lalitpur",
        storePhone: String = "+977015522334",
        storeLatitude: Double = 27.6780,
        storeLongitude: Double = 85.3170,
        riderPartnerId: String? = nil,
        status: CourierOrderStatus = .readyForPickup,
        deliveryStage: CourierDeliveryStage = .toStore,
        paymentMethod: String = "cod",
        isPaid: Bool = false,
        totalPaisa: Int64 = 145000, // NPR 1,450
        deliveryFeePaisa: Int64 = 12000, // NPR 120
        riderEarningPaisa: Int64 = 16000, // NPR 160
        tipPaisa: Int64 = 2000, // NPR 20
        pickupDistanceMeters: Int = 1200,
        dropoffDistanceMeters: Int = 2800,
        estimatedTimeMinutes: Int = 22,
        specialInstructions: String? = "Ring bell twice, gate code is 4920",
        handoffOtpCode: String = "4821",
        items: [CourierOrderItem] = [],
        createdAt: Date = Date()
    ) {
        self.id = id
        self.orderNo = orderNo
        self.consumerId = consumerId
        self.consumerName = consumerName
        self.consumerPhone = consumerPhone
        self.deliveryAddress = deliveryAddress
        self.deliveryLatitude = deliveryLatitude
        self.deliveryLongitude = deliveryLongitude
        self.storeId = storeId
        self.storeName = storeName
        self.storeCategory = storeCategory
        self.storeAddress = storeAddress
        self.storePhone = storePhone
        self.storeLatitude = storeLatitude
        self.storeLongitude = storeLongitude
        self.riderPartnerId = riderPartnerId
        self.status = status
        self.deliveryStage = deliveryStage
        self.paymentMethod = paymentMethod
        self.isPaid = isPaid
        self.totalPaisa = totalPaisa
        self.deliveryFeePaisa = deliveryFeePaisa
        self.riderEarningPaisa = riderEarningPaisa
        self.tipPaisa = tipPaisa
        self.pickupDistanceMeters = pickupDistanceMeters
        self.dropoffDistanceMeters = dropoffDistanceMeters
        self.estimatedTimeMinutes = estimatedTimeMinutes
        self.specialInstructions = specialInstructions
        self.handoffOtpCode = handoffOtpCode
        self.items = items
        self.createdAt = createdAt
    }
}

// MARK: - Courier Chat Models
public enum CourierChatTab: String, CaseIterable, Identifiable {
    case store = "Store / Kitchen"
    case customer = "Customer"
    
    public var id: String { rawValue }
    
    public var icon: String {
        switch self {
        case .store: return "building.2.crop.circle"
        case .customer: return "person.crop.circle"
        }
    }
}

public struct CourierChatMessage: Identifiable, Codable, Equatable {
    public var id: String
    public var orderId: String
    public var senderRole: String // "rider", "store", "consumer"
    public var senderName: String
    public var text: String
    public var timestamp: Date
    public var isOutgoing: Bool
    
    public init(
        id: String = UUID().uuidString,
        orderId: String,
        senderRole: String,
        senderName: String,
        text: String,
        timestamp: Date = Date(),
        isOutgoing: Bool
    ) {
        self.id = id
        self.orderId = orderId
        self.senderRole = senderRole
        self.senderName = senderName
        self.text = text
        self.timestamp = timestamp
        self.isOutgoing = isOutgoing
    }
}

// MARK: - Courier Cash-on-Delivery Ledger
public struct CourierLedgerEntry: Identifiable, Codable, Equatable {
    public var id: String
    public var orderId: String
    public var orderNo: Int64
    public var type: String // "cod_collected", "delivery_earning", "tip", "settlement"
    public var title: String
    public var amountPaisa: Int64
    public var isCredit: Bool // true: added to courier earning / false: COD owed to platform
    public var timestamp: Date
    
    public var amountRs: Double { Double(amountPaisa) / 100.0 }
    
    public init(
        id: String = UUID().uuidString,
        orderId: String,
        orderNo: Int64,
        type: String,
        title: String,
        amountPaisa: Int64,
        isCredit: Bool,
        timestamp: Date = Date()
    ) {
        self.id = id
        self.orderId = orderId
        self.orderNo = orderNo
        self.type = type
        self.title = title
        self.amountPaisa = amountPaisa
        self.isCredit = isCredit
        self.timestamp = timestamp
    }
}

// MARK: - Courier KYC & Vehicle Data
public struct CourierKycData: Equatable {
    public var licenseNumber: String
    public var vehiclePlate: String
    public var vehicleType: String
    public var bluebookNumber: String
    public var licensePhotoPath: String?
    public var bluebookPhotoPath: String?
    public var vehiclePhotoPath: String?
    
    public init(
        licenseNumber: String = "01-06-00981245",
        vehiclePlate: String = "BA 88 PA 4321",
        vehicleType: String = "scooter",
        bluebookNumber: String = "BB-KTM-99214",
        licensePhotoPath: String? = nil,
        bluebookPhotoPath: String? = nil,
        vehiclePhotoPath: String? = nil
    ) {
        self.licenseNumber = licenseNumber
        self.vehiclePlate = vehiclePlate
        self.vehicleType = vehicleType
        self.bluebookNumber = bluebookNumber
        self.licensePhotoPath = licensePhotoPath
        self.bluebookPhotoPath = bluebookPhotoPath
        self.vehiclePhotoPath = vehiclePhotoPath
    }
}
