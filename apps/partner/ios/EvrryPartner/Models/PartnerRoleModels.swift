//
//  PartnerRoleModels.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Unified Partner Roles & Retail KYC Domain Models
//

import Foundation

public enum PartnerRole: String, CaseIterable, Identifiable, Codable {
    case driver = "driver"
    case restaurant = "restaurant"
    case grocery = "grocery"
    case rider = "rider"
    
    public var id: String { rawValue }
    
    public var title: String {
        switch self {
        case .driver: return "Mobility Ride Driver"
        case .restaurant: return "Restaurant Kitchen"
        case .grocery: return "Grocery Supermarket"
        case .rider: return "Delivery Courier"
        }
    }
    
    public var subtitle: String {
        switch self {
        case .driver: return "Bike, Taxi, Cab, Comfort EV transit"
        case .restaurant: return "Kitchen Display System & prep timers"
        case .grocery: return "Shelf SKU lookup, pick-pack & inventory"
        case .rider: return "Order claiming & 2-stage handoff OTP"
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
    
    public var badge: String {
        switch self {
        case .driver: return "Phase 1"
        case .restaurant: return "Phase 2"
        case .grocery: return "Supermarket Mode"
        case .rider: return "Phase 4"
        }
    }
}

public struct PartnerKycState: Codable {
    public var businessRegistrationNo: String
    public var panVatNumber: String
    public var tradeName: String
    public var legalName: String
    public var storeAddress: String
    public var wardNo: Int
    public var businessRegDocUploaded: Bool
    public var panVatDocUploaded: Bool
    public var kycStatus: String
    public var isVerified: Bool
    
    public init(
        businessRegistrationNo: String = "BRN-2081-99812",
        panVatNumber: String = "609823145",
        tradeName: String = "Bhatbhateni Supermarket",
        legalName: String = "Bhat-Bhateni Supermarket & Departmental Store Pvt. Ltd.",
        storeAddress: String = "Pulchowk, Lalitpur Ward 3",
        wardNo: Int = 3,
        businessRegDocUploaded: Bool = true,
        panVatDocUploaded: Bool = true,
        kycStatus: String = "verified",
        isVerified: Bool = true
    ) {
        self.businessRegistrationNo = businessRegistrationNo
        self.panVatNumber = panVatNumber
        self.tradeName = tradeName
        self.legalName = legalName
        self.storeAddress = storeAddress
        self.wardNo = wardNo
        self.businessRegDocUploaded = businessRegDocUploaded
        self.panVatDocUploaded = panVatDocUploaded
        self.kycStatus = kycStatus
        self.isVerified = isVerified
    }
}

public struct PartnerProfile: Codable {
    public var id: String
    public var ownerId: String
    public var type: PartnerRole
    public var legalName: String
    public var tradeName: String
    public var status: String
    public var activeStoreId: String
    
    public init(
        id: String = "partner-grocery-01",
        ownerId: String = "usr-partner-owner",
        type: PartnerRole = .grocery,
        legalName: String = "Bhat-Bhateni Supermarket & Departmental Store Pvt. Ltd.",
        tradeName: String = "Bhatbhateni Supermarket",
        status: String = "active",
        activeStoreId: String = "store-bhatbhateni-pulchowk"
    ) {
        self.id = id
        self.ownerId = ownerId
        self.type = type
        self.legalName = legalName
        self.tradeName = tradeName
        self.status = status
        self.activeStoreId = activeStoreId
    }
}
