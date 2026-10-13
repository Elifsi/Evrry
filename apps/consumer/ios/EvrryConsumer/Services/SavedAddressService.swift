//
//  SavedAddressService.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Saved Places & Address Book Service
//

import Foundation
import Combine

public final class SavedAddressService: ObservableObject {
    public static let shared = SavedAddressService()
    
    @Published public var addresses: [SavedAddress] = [
        SavedAddress(
            id: "addr_home",
            label: "Home",
            address: "Sanepa Heights, Lalitpur",
            latitude: 27.6841,
            longitude: 85.3088,
            type: .home,
            isDefault: true
        ),
        SavedAddress(
            id: "addr_work",
            label: "Office",
            address: "Durbar Marg, Kathmandu",
            latitude: 27.7118,
            longitude: 85.3168,
            type: .work,
            isDefault: false
        ),
        SavedAddress(
            id: "addr_gym",
            label: "Fitness Club",
            address: "Jhamsikhel, Lalitpur",
            latitude: 27.6830,
            longitude: 85.3120,
            type: .other,
            isDefault: false
        )
    ]
    
    public init() {}
    
    public func addAddress(_ address: SavedAddress) {
        addresses.append(address)
    }
    
    public func removeAddress(id: String) {
        addresses.removeAll { $0.id == id }
    }
    
    public func setDefault(id: String) {
        addresses = addresses.map {
            SavedAddress(
                id: $0.id,
                label: $0.label,
                address: $0.address,
                latitude: $0.latitude,
                longitude: $0.longitude,
                type: $0.type,
                isDefault: ($0.id == id)
            )
        }
    }
    
    public func setHomeAddress(_ address: SavedAddress) {
        let filtered = addresses.filter { $0.type != .home }
        let updatedHome = SavedAddress(
            id: address.id,
            label: address.label,
            address: address.address,
            latitude: address.latitude,
            longitude: address.longitude,
            type: .home,
            isDefault: true
        )
        addresses = [updatedHome] + filtered.map {
            SavedAddress(
                id: $0.id,
                label: $0.label,
                address: $0.address,
                latitude: $0.latitude,
                longitude: $0.longitude,
                type: $0.type,
                isDefault: false
            )
        }
    }
}
