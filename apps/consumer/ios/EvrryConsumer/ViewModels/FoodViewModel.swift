//
//  FoodViewModel.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Food Delivery Vertical ViewModel
//

import SwiftUI
import Combine

public struct CuisineFilter: Identifiable, Hashable {
    public let id: String
    public let label: String
    public let emoji: String
    public let filterMatch: String?
    public let isVegOnly: Bool
    
    public init(id: String, label: String, emoji: String, filterMatch: String? = nil, isVegOnly: Bool = false) {
        self.id = id
        self.label = label
        self.emoji = emoji
        self.filterMatch = filterMatch
        self.isVegOnly = isVegOnly
    }
}

public struct QuickFilter: Identifiable, Hashable {
    public let id: String
    public let label: String
    
    public init(id: String, label: String) {
        self.id = id
        self.label = label
    }
}

@MainActor
public final class FoodViewModel: ObservableObject {
    public let cuisines: [CuisineFilter] = [
        CuisineFilter(id: "all", label: "All", emoji: "🍽️"),
        CuisineFilter(id: "momo", label: "Momo & Newari", emoji: "🥟", filterMatch: "Momo"),
        CuisineFilter(id: "thakali", label: "Thakali Thali", emoji: "🍲", filterMatch: "Thakali"),
        CuisineFilter(id: "pizza", label: "Woodfired Pizza", emoji: "🍕", filterMatch: "Pizza"),
        CuisineFilter(id: "burgers", label: "Burgers & CFC", emoji: "🍔", filterMatch: "Burgers"),
        CuisineFilter(id: "japanese", label: "Japanese & Sushi", emoji: "🍱", filterMatch: "Japanese"),
        CuisineFilter(id: "bakery", label: "Bakery & Desserts", emoji: "🥐", filterMatch: "Bakery"),
        CuisineFilter(id: "pure_veg", label: "Pure Veg", emoji: "🥗", isVegOnly: true)
    ]
    
    public let quickFilters: [QuickFilter] = [
        QuickFilter(id: "rating", label: "Rating 4.5+ ★"),
        QuickFilter(id: "veg", label: "Pure Veg 🟢"),
        QuickFilter(id: "fast", label: "Under 30 mins ⚡"),
        QuickFilter(id: "offers", label: "Special Offers 🏷️")
    ]
    
    @Published public var searchQuery: String = ""
    @Published public var selectedCuisine: String = "all"
    @Published public var activeFilters: Set<String> = []
    @Published public var restaurantSwitchedBanner: String? = nil
    
    public init() {}
    
    public func selectCuisine(_ cuisineId: String) {
        self.selectedCuisine = cuisineId
    }
    
    public func toggleFilter(_ filterId: String) {
        if activeFilters.contains(filterId) {
            activeFilters.remove(filterId)
        } else {
            activeFilters.insert(filterId)
        }
    }
    
    public func resetFilters() {
        self.searchQuery = ""
        self.selectedCuisine = "all"
        self.activeFilters = []
    }
    
    public func clearSwitchBanner() {
        self.restaurantSwitchedBanner = nil
    }
    
    public func notifyRestaurantSwitched(restaurantName: String) {
        self.restaurantSwitchedBanner = "Started a fresh order from \(restaurantName) — items from your previous restaurant were cleared."
    }
    
    public func filteredRestaurants() -> [Restaurant] {
        let query = searchQuery.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
        let cuisineId = selectedCuisine
        let filters = activeFilters
        
        return FoodCatalogData.restaurants.filter { r in
            // Search match
            if !query.isEmpty {
                let fullText = "\(r.name) \(r.cuisines.joined(separator: " ")) \(r.area)".lowercased()
                if !fullText.contains(query) {
                    return false
                }
            }
            
            // Cuisine filter
            if cuisineId != "all" {
                if let c = cuisines.first(where: { $0.id == cuisineId }) {
                    if c.isVegOnly && !r.isPureVeg {
                        return false
                    }
                    if let match = c.filterMatch {
                        if !r.cuisines.contains(where: { $0.localizedCaseInsensitiveContains(match) }) {
                            return false
                        }
                    }
                }
            }
            
            // Quick filters
            if filters.contains("rating") && r.rating < 4.5 { return false }
            if filters.contains("veg") && !r.isPureVeg { return false }
            if filters.contains("fast") && r.deliveryEtaMinutes > 30 { return false }
            if filters.contains("offers") && r.offer == nil { return false }
            
            return true
        }
    }
}
