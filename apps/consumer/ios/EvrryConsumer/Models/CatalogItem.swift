//
//  CatalogItem.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Multi-Vertical Catalog & Commerce Domain Models
//

import Foundation

public enum ServiceVertical: String, CaseIterable, Identifiable {
    case food = "food"
    case grocery = "grocery"
    case rides = "rides"
    case hotels = "hotels"
    case rentals = "rentals"
    case services = "services"
    
    public var id: String { rawValue }
    
    public var title: String {
        switch self {
        case .food: return "Food & Momo"
        case .grocery: return "Grocery Mart"
        case .rides: return "Cab & Bike"
        case .hotels: return "Stays & Resorts"
        case .rentals: return "Flats & Rooms"
        case .services: return "Home Services"
        }
    }
    
    public var emoji: String {
        switch self {
        case .food: return "🥟"
        case .grocery: return "🥦"
        case .rides: return "🚖"
        case .hotels: return "🏨"
        case .rentals: return "🏠"
        case .services: return "🛠️"
        }
    }
    
    public var isEnabledInRelease: Bool {
        switch self {
        case .hotels, .rentals: return false
        default: return true
        }
    }
    
    public static var activeCases: [ServiceVertical] {
        allCases.filter { $0.isEnabledInRelease }
    }
}

public struct CatalogItem: Identifiable, Codable, Equatable {
    public let id: String
    public let vertical: String
    public let title: String
    public let subtitle: String
    public let priceNpr: Double
    public let originalPriceNpr: Double?
    public let vendorName: String
    public let vendorId: String
    public let rating: Double
    public let etaMinutes: Int
    public let imageUrl: String?
    public let tags: [String]
    
    public init(
        id: String,
        vertical: String,
        title: String,
        subtitle: String,
        priceNpr: Double,
        originalPriceNpr: Double? = nil,
        vendorName: String,
        vendorId: String,
        rating: Double = 4.8,
        etaMinutes: Int = 25,
        imageUrl: String? = nil,
        tags: [String] = []
    ) {
        self.id = id
        self.vertical = vertical
        self.title = title
        self.subtitle = subtitle
        self.priceNpr = priceNpr
        self.originalPriceNpr = originalPriceNpr
        self.vendorName = vendorName
        self.vendorId = vendorId
        self.rating = rating
        self.etaMinutes = etaMinutes
        self.imageUrl = imageUrl
        self.tags = tags
    }
}

public struct MockCatalogData {
    public static let items: [CatalogItem] = [
        CatalogItem(
            id: "food_momo_01",
            vertical: "food",
            title: "Steam C-Momo (Buff / Chicken)",
            subtitle: "Spicy sesame Timur achar with 10 juicy dumplings",
            priceNpr: 280.0,
            originalPriceNpr: 320.0,
            vendorName: "Kathmandu Momo Corner",
            vendorId: "vnd_momo_ktm",
            rating: 4.9,
            etaMinutes: 20,
            tags: ["Bestseller", "Local", "Spicy"]
        ),
        CatalogItem(
            id: "food_thakali_02",
            vertical: "food",
            title: "Authentic Jimbu Thakali Thali",
            subtitle: "Mustang black lentils, local ghee, gundruk & mutton curry",
            priceNpr: 550.0,
            vendorName: "Mustang Thakali Kitchen",
            vendorId: "vnd_thakali_ktm",
            rating: 4.9,
            etaMinutes: 35,
            tags: ["Thakali", "Authentic"]
        ),
        CatalogItem(
            id: "groc_apples_01",
            vertical: "grocery",
            title: "Jumla Organic Apples (1 Kg)",
            subtitle: "Crisp, sweet, directly harvested high-altitude organic apples",
            priceNpr: 240.0,
            vendorName: "Himalayan Fresh Mart",
            vendorId: "vnd_mart_fresh",
            rating: 4.8,
            etaMinutes: 15,
            tags: ["Organic", "Fresh"]
        ),
        CatalogItem(
            id: "hotel_lakeview_01",
            vertical: "hotels",
            title: "Pokhara Lakeside Boutique Suite",
            subtitle: "Annapurna mountain view balcony, King bed, Breakfast included",
            priceNpr: 4500.0,
            originalPriceNpr: 5200.0,
            vendorName: "Lakeview Peace Resort",
            vendorId: "vnd_hotel_lakeside",
            rating: 4.9,
            etaMinutes: 0,
            tags: ["Mountain View", "Breakfast Included"]
        ),
        CatalogItem(
            id: "rental_bakhundole_01",
            vertical: "rentals",
            title: "Furnished 2BHK Flat — Jhamsikhel",
            subtitle: "Lalpurja verified, 24/7 water, solar backup, balcony",
            priceNpr: 38000.0,
            vendorName: "Jhamsikhel Residency",
            vendorId: "vnd_res_jham",
            rating: 4.7,
            etaMinutes: 0,
            tags: ["Verified Lalpurja", "No Broker Fee"]
        )
    ]
}
