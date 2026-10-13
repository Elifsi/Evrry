//
//  GroceryViewModel.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Grocery Quick Mart ViewModel (Express Quick Mart)
//  Matching Android 1:1 Parity
//

import Foundation
import Combine

public class GroceryViewModel: ObservableObject {
    @Published public var activeHub: GroceryHub = GroceryCatalogData.defaultHub
    @Published public var deliveryMode: GroceryDeliveryMode = .expressDarkstore
    @Published public var selectedStore: GroceryStore? = nil
    @Published public var storeFilterType: String = "All"
    @Published public var storeSearchQuery: String = ""
    @Published public var selectedCategoryId: String = "dairy"
    @Published public var selectedSubCategory: String = "All"
    @Published public var searchQuery: String = ""
    @Published public var substitutionPreference: GrocerySubstitutionPreference = .allowStoreSubstitution
    
    public init() {}
    
    public func setDeliveryMode(_ mode: GroceryDeliveryMode) {
        deliveryMode = mode
    }
    
    public func selectStore(_ store: GroceryStore?) {
        selectedStore = store
    }
    
    public func setStoreFilterType(_ filter: String) {
        storeFilterType = filter
    }
    
    public func setStoreSearchQuery(_ query: String) {
        storeSearchQuery = query
    }
    
    public func getStores() -> [GroceryStore] {
        let filter = storeFilterType
        let query = storeSearchQuery.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
        
        return GroceryCatalogData.partnerStores.filter { store in
            let matchesFilter: Bool
            switch filter {
            case "All":
                matchesFilter = true
            case "Supermarkets":
                matchesFilter = (store.storeType == "Supermarket" || store.storeType == "Department Store")
            case "Daily Marts":
                matchesFilter = (store.storeType == "Mart Chain")
            case "Organic & Farm":
                matchesFilter = (store.storeType == "Organic Farm Store")
            case "Local Kirana":
                matchesFilter = (store.storeType == "Local Kirana")
            default:
                matchesFilter = true
            }
            
            let matchesQuery = query.isEmpty ||
                store.name.lowercased().contains(query) ||
                store.tagline.lowercased().contains(query) ||
                store.area.lowercased().contains(query) ||
                store.featuredTags.contains { $0.lowercased().contains(query) }
            
            return matchesFilter && matchesQuery
        }
    }
    
    public func getProductsForStore(_ storeId: String) -> [GroceryProduct] {
        return GroceryCatalogData.storeProducts.filter { $0.storeId == storeId }
    }
    
    public func getProductsForStoreAisle(storeId: String, aisle: String) -> [GroceryProduct] {
        return GroceryCatalogData.storeProducts.filter { product in
            product.storeId == storeId && (aisle == "All" || product.subCategory.caseInsensitiveCompare(aisle) == .orderedSame)
        }
    }
    
    public func selectCategory(_ categoryId: String) {
        selectedCategoryId = categoryId
        selectedSubCategory = "All"
    }
    
    public func selectSubCategory(_ subCategory: String) {
        selectedSubCategory = subCategory
    }
    
    public func setSearchQuery(_ query: String) {
        searchQuery = query
    }
    
    public func setSubstitutionPreference(_ preference: GrocerySubstitutionPreference) {
        substitutionPreference = preference
    }
    
    public func getFilteredProducts() -> [GroceryProduct] {
        let query = searchQuery.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
        let catId = selectedCategoryId
        let subCat = selectedSubCategory
        
        return GroceryCatalogData.products.filter { product in
            let matchesCategory = (product.categoryId == catId)
            let matchesSubCategory = (subCat == "All" || product.subCategory.caseInsensitiveCompare(subCat) == .orderedSame)
            
            let matchesQuery: Bool
            if query.isEmpty {
                matchesQuery = true
            } else {
                let titleMatch = product.title.lowercased().contains(query)
                let titleNeMatch = product.titleNe?.lowercased().contains(query) ?? false
                let brandMatch = product.brand.lowercased().contains(query)
                let subCategoryMatch = product.subCategory.lowercased().contains(query)
                let tagMatch = product.tags.contains { $0.lowercased().contains(query) }
                matchesQuery = titleMatch || titleNeMatch || brandMatch || subCategoryMatch || tagMatch
            }
            
            if !query.isEmpty {
                return matchesQuery
            } else {
                return matchesCategory && matchesSubCategory
            }
        }
    }
    
    public func getProductsByAisle(_ categoryId: String) -> [GroceryProduct] {
        return GroceryCatalogData.products.filter { $0.categoryId == categoryId }
    }
    
    public func getBestSellers() -> [GroceryProduct] {
        return GroceryCatalogData.products.filter { $0.tags.contains("Bestseller") || $0.tags.contains("Daily Essential") }
    }
    
    public func getFlashDeals() -> [GroceryProduct] {
        return GroceryCatalogData.products.filter { $0.discountPercent >= 10 }
    }
    
    public func canIncrement(product: GroceryProduct, currentQty: Int) -> Bool {
        return currentQty < product.stockQuantity && currentQty < product.maxPerOrder
    }
    
    public func isOutOfStock(product: GroceryProduct) -> Bool {
        return product.stockQuantity <= 0
    }
    
    public func isLowStock(product: GroceryProduct) -> Bool {
        return (1...3).contains(product.stockQuantity)
    }
    
    public func canIncrementVariant(variant: GroceryProductVariant, maxPerOrder: Int, currentQty: Int) -> Bool {
        return currentQty < variant.stockQuantity && currentQty < maxPerOrder
    }
    
    public func isVariantOutOfStock(variant: GroceryProductVariant) -> Bool {
        return variant.stockQuantity <= 0
    }
    
    public func isVariantLowStock(variant: GroceryProductVariant) -> Bool {
        return (1...3).contains(variant.stockQuantity)
    }
    
    public func getImpulseAdditions(cartItemIds: Set<String>) -> [GroceryProduct] {
        let priorityIds = [
            "groc-patan-sliced-bread",
            "groc-ddc-milk-500",
            "groc-local-eggs-6",
            "groc-waiwai-chicken-5pack",
            "groc-palungo-saag",
            "groc-current-hot-spicy-noodles",
            "groc-dettol-handwash-200"
        ]
        return priorityIds
            .filter { !cartItemIds.contains($0) }
            .compactMap { id in GroceryCatalogData.products.first { $0.id == id } }
    }
}
