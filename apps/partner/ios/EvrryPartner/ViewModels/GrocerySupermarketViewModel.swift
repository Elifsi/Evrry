//
//  GrocerySupermarketViewModel.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Supermarket Partner Operational ViewModel
//  1:1 Parity with Android GrocerySupermarketViewModel
//

import Foundation
import Combine

public class GrocerySupermarketViewModel: ObservableObject {
    @Published public var catalog: [GroceryPartnerItem] = GroceryCatalogData.initialCatalog
    @Published public var selectedAisle: String = "All"
    @Published public var inventorySearchQuery: String = ""
    @Published public var orders: [GroceryPartnerOrder] = GroceryCatalogData.createInitialOrders()
    @Published public var selectedOrderId: String? = GroceryCatalogData.createInitialOrders().first?.id
    
    // Barcode scanner
    @Published public var scannedBarcode: String? = nil
    @Published public var scannedItem: GroceryPartnerItem? = nil
    
    // Courier chat
    @Published public var courierChatMessages: [PartnerChatMessage] = [
        PartnerChatMessage(
            id: "msg-1",
            sender: "Courier",
            text: "Namaste dai! I am at the traffic chowk. Which gate for supermarket pickup?",
            time: "10:32 AM"
        ),
        PartnerChatMessage(
            id: "msg-2",
            sender: "You (Supermarket)",
            text: "Namaste! Come to Bay #3 at the Front Express Counter beside the escalator.",
            time: "10:33 AM"
        )
    ]
    
    public init() {}
    
    // MARK: - Order Pick & Pack Checklist
    
    public func getSelectedOrder() -> GroceryPartnerOrder? {
        guard let id = selectedOrderId else { return nil }
        return orders.first { $0.id == id }
    }
    
    public func selectOrder(_ orderId: String) {
        selectedOrderId = orderId
    }
    
    public func toggleItemPacked(orderId: String, orderItemId: String) {
        guard let orderIndex = orders.firstIndex(where: { $0.id == orderId }) else { return }
        guard let itemIndex = orders[orderIndex].items.firstIndex(where: { $0.id == orderItemId }) else { return }
        
        orders[orderIndex].items[itemIndex].isPacked.toggle()
    }
    
    public func setMeasuredWeight(orderId: String, orderItemId: String, weightKg: Double) {
        guard let orderIndex = orders.firstIndex(where: { $0.id == orderId }) else { return }
        guard let itemIndex = orders[orderIndex].items.firstIndex(where: { $0.id == orderItemId }) else { return }
        
        let unitPrice = orders[orderIndex].items[itemIndex].unitPricePaisa
        let newLineTotal = Int64(Double(unitPrice) * weightKg)
        
        orders[orderIndex].items[itemIndex].measuredWeight = weightKg
        orders[orderIndex].items[itemIndex].lineTotalPaisa = newLineTotal
        orders[orderIndex].items[itemIndex].isPacked = true
        
        let newTotal = orders[orderIndex].items.reduce(0) { $0 + $1.lineTotalPaisa }
        orders[orderIndex].totalPaisa = newTotal
    }
    
    public func completeOrderPacking(orderId: String) {
        guard let orderIndex = orders.firstIndex(where: { $0.id == orderId }) else { return }
        orders[orderIndex].status = "ready_for_pickup"
        orders[orderIndex].riderEtaMinutes = 3
    }
    
    // MARK: - Out-of-Stock Customer Substitution Flow
    
    public func getRecommendedSubstitutes(for orderItem: GroceryOrderItem) -> [GroceryPartnerItem] {
        return catalog.filter { item in
            item.section.caseInsensitiveCompare(orderItem.section) == .orderedSame &&
            item.id != orderItem.itemId &&
            item.isAvailable &&
            item.stockQuantity > 0
        }
    }
    
    public func proposeSubstitution(orderId: String, orderItemId: String, substituteItem: GroceryPartnerItem) {
        guard let orderIndex = orders.firstIndex(where: { $0.id == orderId }) else { return }
        guard let itemIndex = orders[orderIndex].items.firstIndex(where: { $0.id == orderItemId }) else { return }
        
        orders[orderIndex].items[itemIndex].substitutionStatus = .proposed
        orders[orderIndex].items[itemIndex].proposedSubstituteItemId = substituteItem.id
        orders[orderIndex].items[itemIndex].proposedSubstituteName = substituteItem.name
        orders[orderIndex].items[itemIndex].proposedSubstitutePriceNpr = substituteItem.priceNpr
        orders[orderIndex].items[itemIndex].isPacked = false
    }
    
    public func resolveSubstitution(orderId: String, orderItemId: String, approved: Bool) {
        guard let orderIndex = orders.firstIndex(where: { $0.id == orderId }) else { return }
        guard let itemIndex = orders[orderIndex].items.firstIndex(where: { $0.id == orderItemId }) else { return }
        
        if approved, let subId = orders[orderIndex].items[itemIndex].proposedSubstituteItemId {
            let subItem = catalog.first { $0.id == subId }
            let newPrice = subItem?.pricePaisa ?? orders[orderIndex].items[itemIndex].unitPricePaisa
            
            orders[orderIndex].items[itemIndex].name = orders[orderIndex].items[itemIndex].proposedSubstituteName ?? orders[orderIndex].items[itemIndex].name
            orders[orderIndex].items[itemIndex].unitPricePaisa = newPrice
            orders[orderIndex].items[itemIndex].lineTotalPaisa = newPrice * Int64(orders[orderIndex].items[itemIndex].quantity)
            orders[orderIndex].items[itemIndex].substitutionStatus = .approved
            orders[orderIndex].items[itemIndex].isPacked = true
        } else {
            orders[orderIndex].items[itemIndex].substitutionStatus = .rejected
            orders[orderIndex].items[itemIndex].isPacked = false
            orders[orderIndex].items[itemIndex].lineTotalPaisa = 0
        }
        
        let newTotal = orders[orderIndex].items.reduce(0) { $0 + $1.lineTotalPaisa }
        orders[orderIndex].totalPaisa = newTotal
    }
    
    // MARK: - Inventory & Stock Management
    
    public func setAisleFilter(_ aisle: String) {
        selectedAisle = aisle
    }
    
    public func setInventorySearchQuery(_ query: String) {
        inventorySearchQuery = query
    }
    
    public func getFilteredCatalog() -> [GroceryPartnerItem] {
        let aisle = selectedAisle
        let query = inventorySearchQuery.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
        
        return catalog.filter { item in
            let matchesAisle = (aisle == "All") || (item.section.caseInsensitiveCompare(aisle) == .orderedSame)
            let matchesQuery = query.isEmpty ||
                item.name.lowercased().contains(query) ||
                (item.nameNe?.lowercased().contains(query) ?? false) ||
                item.brand.lowercased().contains(query) ||
                (item.barcode?.lowercased().contains(query) ?? false) ||
                (item.sku?.lowercased().contains(query) ?? false)
            return matchesAisle && matchesQuery
        }
    }
    
    public func adjustStock(itemId: String, delta: Int) {
        guard let index = catalog.firstIndex(where: { $0.id == itemId }) else { return }
        let newQty = max(0, catalog[index].stockQuantity + delta)
        catalog[index].stockQuantity = newQty
        catalog[index].isAvailable = (newQty > 0)
        
        if scannedItem?.id == itemId {
            scannedItem = catalog[index]
        }
    }
    
    public func toggleItemAvailability(itemId: String) {
        guard let index = catalog.firstIndex(where: { $0.id == itemId }) else { return }
        catalog[index].isAvailable.toggle()
        
        if scannedItem?.id == itemId {
            scannedItem = catalog[index]
        }
    }
    
    public func updateItemPrice(itemId: String, newPriceNpr: Double) {
        guard let index = catalog.firstIndex(where: { $0.id == itemId }) else { return }
        catalog[index].pricePaisa = Int64(newPriceNpr * 100.0)
        
        if scannedItem?.id == itemId {
            scannedItem = catalog[index]
        }
    }
    
    // MARK: - Barcode Scanner & SKU Audit
    
    public func onBarcodeScanned(_ code: String) {
        scannedBarcode = code
        scannedItem = catalog.first {
            $0.barcode == code || ($0.sku?.caseInsensitiveCompare(code) == .orderedSame)
        }
    }
    
    public func clearScannedBarcode() {
        scannedBarcode = nil
        scannedItem = nil
    }
    
    // MARK: - Courier Chat
    
    public func sendCourierChatMessage(_ text: String) {
        guard !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return }
        let msg = PartnerChatMessage(
            id: "msg-\(Date().timeIntervalSince1970)",
            sender: "You (Supermarket)",
            text: text,
            time: "Just now"
        )
        courierChatMessages.append(msg)
    }
}
