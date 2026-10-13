//
//  GroceryPartnerModels.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Domain Models for Grocery Supermarket Partner Operations
//  1:1 Parity with Kotlin Android Models
//

import Foundation

public struct GroceryPartnerItem: Identifiable, Codable {
    public let id: String
    public let storeId: String
    public var name: String
    public var nameNe: String?
    public var brand: String
    public var section: String
    public var pricePaisa: Int64
    public var stockQuantity: Int
    public var isAvailable: Bool
    public var barcode: String?
    public var sku: String?
    public var unit: String
    public var isLooseWeight: Bool
    public var isPerishable: Bool
    public var imageUrl: String?
    
    public var priceNpr: Double {
        return Double(pricePaisa) / 100.0
    }
    
    public init(
        id: String,
        storeId: String = "store-bhatbhateni-pulchowk",
        name: String,
        nameNe: String? = nil,
        brand: String = "",
        section: String,
        pricePaisa: Int64,
        stockQuantity: Int = 20,
        isAvailable: Bool = true,
        barcode: String? = nil,
        sku: String? = nil,
        unit: String = "pack",
        isLooseWeight: Bool = false,
        isPerishable: Bool = false,
        imageUrl: String? = nil
    ) {
        self.id = id
        self.storeId = storeId
        self.name = name
        self.nameNe = nameNe
        self.brand = brand
        self.section = section
        self.pricePaisa = pricePaisa
        self.stockQuantity = stockQuantity
        self.isAvailable = isAvailable
        self.barcode = barcode
        self.sku = sku
        self.unit = unit
        self.isLooseWeight = isLooseWeight
        self.isPerishable = isPerishable
        self.imageUrl = imageUrl
    }
}

public enum SubstitutionStatus: String, Codable {
    case none = "none"
    case proposed = "proposed"
    case approved = "approved"
    case rejected = "rejected"
    
    public var label: String {
        switch self {
        case .none: return "Original Item"
        case .proposed: return "Customer Proposal Sent"
        case .approved: return "Approved by Customer"
        case .rejected: return "Customer Rejected (Refunded)"
        }
    }
}

public struct GroceryOrderItem: Identifiable, Codable {
    public let id: String
    public let orderId: String
    public let itemId: String
    public var name: String
    public let section: String
    public var unitPricePaisa: Int64
    public let quantity: Int
    public var lineTotalPaisa: Int64
    public let unit: String
    public let isLooseWeight: Bool
    public var measuredWeight: Double?
    public var isPacked: Bool
    public var substitutionStatus: SubstitutionStatus
    public var proposedSubstituteItemId: String?
    public var proposedSubstituteName: String?
    public var proposedSubstitutePriceNpr: Double?
    
    public var unitPriceNpr: Double {
        return Double(unitPricePaisa) / 100.0
    }
    
    public var lineTotalNpr: Double {
        return Double(lineTotalPaisa) / 100.0
    }
    
    public init(
        id: String,
        orderId: String,
        itemId: String,
        name: String,
        section: String,
        unitPricePaisa: Int64,
        quantity: Int,
        lineTotalPaisa: Int64,
        unit: String = "pack",
        isLooseWeight: Bool = false,
        measuredWeight: Double? = nil,
        isPacked: Bool = false,
        substitutionStatus: SubstitutionStatus = .none,
        proposedSubstituteItemId: String? = nil,
        proposedSubstituteName: String? = nil,
        proposedSubstitutePriceNpr: Double? = nil
    ) {
        self.id = id
        self.orderId = orderId
        self.itemId = itemId
        self.name = name
        self.section = section
        self.unitPricePaisa = unitPricePaisa
        self.quantity = quantity
        self.lineTotalPaisa = lineTotalPaisa
        self.unit = unit
        self.isLooseWeight = isLooseWeight
        self.measuredWeight = measuredWeight
        self.isPacked = isPacked
        self.substitutionStatus = substitutionStatus
        self.proposedSubstituteItemId = proposedSubstituteItemId
        self.proposedSubstituteName = proposedSubstituteName
        self.proposedSubstitutePriceNpr = proposedSubstitutePriceNpr
    }
}

public struct GroceryPartnerOrder: Identifiable, Codable {
    public let id: String
    public let orderNo: Int64
    public let consumerId: String
    public let consumerName: String
    public let consumerPhone: String
    public let deliveryAddress: String
    public let storeId: String
    public var status: String
    public var totalPaisa: Int64
    public let placedAt: String
    public let customerNote: String?
    public let riderPartnerId: String?
    public let riderName: String?
    public let riderPhone: String?
    public let riderVehiclePlate: String?
    public var riderEtaMinutes: Int?
    public let pickupBayCode: String
    public var items: [GroceryOrderItem]
    
    public var totalNpr: Double {
        return Double(totalPaisa) / 100.0
    }
    
    public var packedItemCount: Int {
        return items.filter { $0.isPacked || $0.substitutionStatus == .rejected }.count
    }
    
    public var totalItemCount: Int {
        return items.count
    }
    
    public var isPackingComplete: Bool {
        return !items.isEmpty && items.allSatisfy { $0.isPacked || $0.substitutionStatus == .rejected }
    }
    
    public init(
        id: String,
        orderNo: Int64,
        consumerId: String = "usr-cons-01",
        consumerName: String = "Aayush Sharma",
        consumerPhone: String = "+977 9841234567",
        deliveryAddress: String = "Jhamsikhel Road, Lalitpur (Near St. Mary's)",
        storeId: String = "store-bhatbhateni-pulchowk",
        status: String = "preparing",
        totalPaisa: Int64,
        placedAt: String = "10 mins ago",
        customerNote: String? = "Please pack leafy greens and dairy in separate bags.",
        riderPartnerId: String? = "rider-nepal-04",
        riderName: String? = "Santosh KC",
        riderPhone: String? = "+977 9813009988",
        riderVehiclePlate: String? = "Ba 88 Pa 4321",
        riderEtaMinutes: Int? = 4,
        pickupBayCode: String = "Bay #3 (Front Express Counter)",
        items: [GroceryOrderItem] = []
    ) {
        self.id = id
        self.orderNo = orderNo
        self.consumerId = consumerId
        self.consumerName = consumerName
        self.consumerPhone = consumerPhone
        self.deliveryAddress = deliveryAddress
        self.storeId = storeId
        self.status = status
        self.totalPaisa = totalPaisa
        self.placedAt = placedAt
        self.customerNote = customerNote
        self.riderPartnerId = riderPartnerId
        self.riderName = riderName
        self.riderPhone = riderPhone
        self.riderVehiclePlate = riderVehiclePlate
        self.riderEtaMinutes = riderEtaMinutes
        self.pickupBayCode = pickupBayCode
        self.items = items
    }
}

public struct PartnerChatMessage: Identifiable, Codable {
    public let id: String
    public let sender: String
    public let text: String
    public let time: String
    
    public init(id: String, sender: String, text: String, time: String) {
        self.id = id
        self.sender = sender
        self.text = text
        self.time = time
    }
}

public struct GroceryKycData: Codable, Equatable {
    public var businessRegistrationNo: String
    public var panVatNumber: String
    public var tradeName: String
    public var legalName: String
    public var storeAddress: String
    public var wardNo: String
    public var businessRegistrationDoc: String?
    public var panVatDoc: String?
    
    public init(
        businessRegistrationNo: String = "BRN-2081-99812",
        panVatNumber: String = "609823145",
        tradeName: String = "Bhatbhateni Supermarket",
        legalName: String = "Bhat-Bhateni Supermarket & Departmental Store Pvt. Ltd.",
        storeAddress: String = "Pulchowk, Lalitpur",
        wardNo: String = "3",
        businessRegistrationDoc: String? = nil,
        panVatDoc: String? = nil
    ) {
        self.businessRegistrationNo = businessRegistrationNo
        self.panVatNumber = panVatNumber
        self.tradeName = tradeName
        self.legalName = legalName
        self.storeAddress = storeAddress
        self.wardNo = wardNo
        self.businessRegistrationDoc = businessRegistrationDoc
        self.panVatDoc = panVatDoc
    }
}

