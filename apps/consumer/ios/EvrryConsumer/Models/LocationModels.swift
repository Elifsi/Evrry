//
//  LocationModels.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Reusable Geospatial & Address Domain Models
//

import Foundation
import CoreLocation

public struct LocationPoint: Identifiable, Codable, Equatable {
    public var id: String { "\(latitude),\(longitude)" }
    public let label: String
    public let address: String
    public let latitude: Double
    public let longitude: Double
    public let isCurrentLocation: Bool
    
    public init(
        label: String,
        address: String,
        latitude: Double,
        longitude: Double,
        isCurrentLocation: Bool = false
    ) {
        self.label = label
        self.address = address
        self.latitude = latitude
        self.longitude = longitude
        self.isCurrentLocation = isCurrentLocation
    }
    
    public var coordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
    }
    
    public var displayTitle: String {
        label.isEmpty ? (address.components(separatedBy: ",").first ?? address) : label
    }
}

public enum AddressType: String, Codable, CaseIterable {
    case home = "Home"
    case work = "Office / Work"
    case other = "Saved Place"
    
    public var emoji: String {
        switch self {
        case .home: return "🏠"
        case .work: return "💼"
        case .other: return "📍"
        }
    }
}

public struct SavedAddress: Identifiable, Codable, Equatable {
    public let id: String
    public let label: String
    public let address: String
    public let latitude: Double
    public let longitude: Double
    public let type: AddressType
    public let isDefault: Bool
    
    public init(
        id: String,
        label: String,
        address: String,
        latitude: Double,
        longitude: Double,
        type: AddressType = .other,
        isDefault: Bool = false
    ) {
        self.id = id
        self.label = label
        self.address = address
        self.latitude = latitude
        self.longitude = longitude
        self.type = type
        self.isDefault = isDefault
    }
    
    public func toLocationPoint() -> LocationPoint {
        LocationPoint(
            label: label,
            address: address,
            latitude: latitude,
            longitude: longitude,
            isCurrentLocation: false
        )
    }
}
