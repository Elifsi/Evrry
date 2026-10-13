//
//  ExploreViewModel.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Explore Catalog & Multi-Vertical ViewModel
//

import SwiftUI
import Combine

@MainActor
public final class ExploreViewModel: ObservableObject {
    @Published public var selectedVertical: String = "all"
    @Published public var items: [CatalogItem] = MockCatalogData.items.filter { $0.vertical != "hotels" && $0.vertical != "rentals" }
    @Published public var cart: CartState = CartState()
    
    public init() {}
    
    public func selectVertical(_ vertical: String) {
        self.selectedVertical = vertical
        if vertical == "all" {
            self.items = MockCatalogData.items.filter { $0.vertical != "hotels" && $0.vertical != "rentals" }
        } else {
            self.items = MockCatalogData.items.filter { $0.vertical == vertical }
        }
    }
    
    public func hasConflict(with item: CatalogItem) -> Bool {
        if item.vertical == "food" {
            guard let activeFood = cart.activeFoodVendorId, !cart.foodItems.isEmpty else { return false }
            return activeFood != item.vendorId
        } else if item.vertical == "grocery" {
            guard let activeGrocery = cart.activeGroceryVendorId, !cart.groceryItems.isEmpty else { return false }
            return activeGrocery != item.vendorId
        }
        return false
    }

    public func hasConflict(vendorId: String, vertical: String = "food") -> Bool {
        if vertical == "food" {
            guard let activeFood = cart.activeFoodVendorId, !cart.foodItems.isEmpty else { return false }
            return activeFood != vendorId
        } else if vertical == "grocery" {
            guard let activeGrocery = cart.activeGroceryVendorId, !cart.groceryItems.isEmpty else { return false }
            return activeGrocery != vendorId
        }
        return false
    }
    
    public func clearAndAdd(_ item: CatalogItem, options: [String] = [], quantity: Int = 1) {
        let nonConflicting = cart.items.filter { $0.item.vertical != item.vertical }
        let newCartItem = CartItem(item: item, quantity: quantity, selectedOptions: options)
        self.cart.items = nonConflicting + [newCartItem]
    }
    
    @discardableResult
    public func addToCart(_ item: CatalogItem) -> Bool {
        addToCartWithOptions(item, options: [], quantity: 1)
    }
    
    @discardableResult
    public func addToCartWithOptions(_ item: CatalogItem, options: [String] = [], quantity: Int = 1) -> Bool {
        var baseItems = cart.items
        var restaurantSwitched = false
        
        if item.vertical == "food" {
            let hasOtherRestaurant = baseItems.contains {
                $0.item.vertical == "food" && $0.item.vendorId != item.vendorId
            }
            if hasOtherRestaurant {
                baseItems.removeAll { $0.item.vertical == "food" }
                restaurantSwitched = true
            }
        } else if item.vertical == "grocery" {
            let hasOtherHub = baseItems.contains {
                $0.item.vertical == "grocery" && $0.item.vendorId != item.vendorId
            }
            if hasOtherHub {
                baseItems.removeAll { $0.item.vertical == "grocery" }
            }
        }
        
        if let idx = baseItems.firstIndex(where: { $0.item.id == item.id && $0.selectedOptions == options }) {
            baseItems[idx].quantity += quantity
        } else {
            baseItems.append(CartItem(item: item, quantity: quantity, selectedOptions: options))
        }
        self.cart.items = baseItems
        return restaurantSwitched
    }
    
    public func removeFromCart(_ itemId: String) {
        var currentItems = cart.items
        if let idx = currentItems.firstIndex(where: { $0.item.id == itemId }) {
            if currentItems[idx].quantity > 1 {
                currentItems[idx].quantity -= 1
            } else {
                currentItems.remove(at: idx)
            }
        }
        self.cart.items = currentItems
    }
    
    public func setCartQty(_ itemId: String, _ qty: Int) {
        var currentItems = cart.items
        if let idx = currentItems.firstIndex(where: { $0.item.id == itemId }) {
            if qty <= 0 {
                currentItems.remove(at: idx)
            } else {
                currentItems[idx].quantity = qty
            }
            self.cart.items = currentItems
        }
    }
    
    public func getItemQty(_ itemId: String) -> Int {
        cart.items.first(where: { $0.item.id == itemId })?.quantity ?? 0
    }
    
    public func setSpecialInstructions(_ instructions: String) {
        self.cart.specialInstructions = instructions
    }
    
    public func setFulfillmentType(_ type: OrderFulfillmentType) {
        self.cart.fulfillmentType = type
    }
    
    public let popularAddons: [CatalogItem] = [
        CatalogItem(
            id: "addon-coke",
            vertical: "food",
            vendorId: "vendor-addons",
            vendorName: "Beverages",
            title: "Chilled Coca-Cola (500ml)",
            subtitle: "Served ice-cold with lemon wedge",
            priceNpr: 70.0,
            rating: 4.9
        ),
        CatalogItem(
            id: "addon-dalle",
            vertical: "food",
            vendorId: "vendor-addons",
            vendorName: "Condiments",
            title: "Spicy Dalle Timur Pickle Dip",
            subtitle: "Authentic stone-ground fiery relish",
            priceNpr: 40.0,
            rating: 4.8
        ),
        CatalogItem(
            id: "addon-gulab",
            vertical: "food",
            vendorId: "vendor-addons",
            vendorName: "Desserts",
            title: "Warm Gulab Jamun (2 Pcs)",
            subtitle: "Cardamom sugar syrup infused",
            priceNpr: 110.0,
            rating: 4.7
        )
    ]
    
    public func addAddonToCart(_ addon: CatalogItem) {
        let boundVendorId = cart.activeVendorId ?? "addon-vendor"
        let boundVendorName = cart.activeVendorName ?? "Addon Kitchen"
        let boundItem = CatalogItem(
            id: addon.id,
            vertical: "food",
            title: addon.title,
            subtitle: addon.subtitle,
            priceNpr: addon.priceNpr,
            originalPriceNpr: addon.originalPriceNpr,
            vendorName: boundVendorName,
            vendorId: boundVendorId,
            rating: addon.rating,
            etaMinutes: addon.etaMinutes,
            imageUrl: addon.imageUrl,
            tags: addon.tags
        )
        _ = addToCart(boundItem)
    }
    
    public func reorderOrder(vendorName: String, title: String, amountRs: Int, lineItems: [String]) {
        let reorderItems: [CartItem]
        if !lineItems.isEmpty {
            reorderItems = lineItems.enumerated().map { idx, itemStr in
                let name = itemStr.components(separatedBy: " x").first ?? itemStr
                var parsedPrice: Double? = nil
                if let rangeStart = itemStr.range(of: "(Rs "),
                   let rangeEnd = itemStr.range(of: ")", range: rangeStart.upperBound..<itemStr.endIndex) {
                    let priceString = String(itemStr[rangeStart.upperBound..<rangeEnd.lowerBound]).trimmingCharacters(in: .whitespaces)
                    if let val = Double(priceString) {
                        parsedPrice = val
                    }
                }
                let finalPrice = parsedPrice ?? (Double(amountRs) / Double(max(1, lineItems.count)))
                return CartItem(
                    item: CatalogItem(
                        id: "reorder-item-\(idx)",
                        vertical: "food",
                        title: name,
                        subtitle: "Reordered from \(vendorName)",
                        priceNpr: finalPrice,
                        originalPriceNpr: nil,
                        vendorName: vendorName,
                        vendorId: "reorder-vendor",
                        rating: 4.8
                    ),
                    quantity: 1
                )
            }
        } else {
            reorderItems = [
                CartItem(
                    item: CatalogItem(
                        id: "reorder-single",
                        vertical: "food",
                        title: title,
                        subtitle: "Reordered from \(vendorName)",
                        priceNpr: Double(amountRs),
                        originalPriceNpr: nil,
                        vendorName: vendorName,
                        vendorId: "reorder-vendor",
                        rating: 4.8
                    ),
                    quantity: 1
                )
            ]
        }
        self.cart.items = reorderItems
    }
    
    public func selectDeliveryAddress(_ address: DeliveryAddress) {
        self.cart.selectedAddress = address
    }
    
    public func clearCart() {
        self.cart.items = []
        self.cart.specialInstructions = ""
    }
}
