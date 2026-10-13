//
//  GroceryStoreDetailView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Production Partner Supermarket Store Shopping Screen
//  1:1 Parity with Android GroceryStoreDetailScreen
//

import SwiftUI

public struct GroceryStoreDetailView: View {
    public let store: GroceryStore
    @ObservedObject public var groceryViewModel: GroceryViewModel
    @ObservedObject public var exploreViewModel: ExploreViewModel
    public let onBack: () -> Void
    public let onNavigateToCart: () -> Void
    
    @State private var selectedAisle: String = "All"
    @State private var storeSearch: String = ""
    @State private var showConflictAlert: Bool = false
    @State private var pendingProductToAdd: GroceryProduct? = nil
    
    public init(
        store: GroceryStore,
        groceryViewModel: GroceryViewModel,
        exploreViewModel: ExploreViewModel,
        onBack: @escaping () -> Void,
        onNavigateToCart: @escaping () -> Void
    ) {
        self.store = store
        self.groceryViewModel = groceryViewModel
        self.exploreViewModel = exploreViewModel
        self.onBack = onBack
        self.onNavigateToCart = onNavigateToCart
    }
    
    private let columns = [
        GridItem(.flexible(), spacing: 10),
        GridItem(.flexible(), spacing: 10)
    ]
    
    public var body: some View {
        let cart = exploreViewModel.cart
        let allStoreProducts = groceryViewModel.getProductsForStoreAisle(storeId: store.id, aisle: selectedAisle)
        let filteredProducts = allStoreProducts.filter { product in
            let q = storeSearch.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
            if q.isEmpty { return true }
            return product.title.lowercased().contains(q) ||
                (product.titleNe?.lowercased().contains(q) ?? false) ||
                product.brand.lowercased().contains(q)
        }
        
        let allAisles = ["All"] + store.aisles
        
        ZStack(alignment: .bottom) {
            Color(red: 0.98, green: 0.98, blue: 1.0)
                .ignoresSafeArea()
            
            VStack(spacing: 0) {
                // 1. PINNED TOP BAR
                HStack {
                    Button(action: onBack) {
                        HStack(spacing: 4) {
                            Image(systemName: "chevron.left")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Text("Stores")
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundColor(.evrryDarkCharcoal)
                        }
                        .frame(minHeight: 44)
                        .padding(.horizontal, 8)
                        .background(Color.white.opacity(0.8))
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                    
                    Spacer()
                    
                    HStack(spacing: 8) {
                        HStack(spacing: 4) {
                            Text("⚡").font(.system(size: 11))
                            Text(store.etaRange)
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                        }
                        .padding(.horizontal, 10)
                        .padding(.vertical, 5)
                        .background(Color(red: 0.93, green: 0.99, blue: 0.96))
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                        
                        Button(action: onNavigateToCart) {
                            ZStack(alignment: .topTrailing) {
                                Circle()
                                    .fill(Color.white)
                                    .frame(width: 40, height: 40)
                                    .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                
                                Image(systemName: "cart.fill")
                                    .font(.system(size: 16))
                                    .foregroundColor(cart.itemCount > 0 ? Color(red: 0.02, green: 0.47, blue: 0.34) : .evrryDarkCharcoal)
                                    .frame(width: 40, height: 40)
                                
                                if cart.itemCount > 0 {
                                    Text("\(min(cart.itemCount, 99))")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(.white)
                                        .padding(.horizontal, 4)
                                        .padding(.vertical, 2)
                                        .background(Color(red: 0.02, green: 0.47, blue: 0.34))
                                        .clipShape(Capsule())
                                        .offset(x: 4, y: -4)
                                }
                            }
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 6)
                
                // 2. STORE HEADER HERO CARD
                VStack(alignment: .leading, spacing: 8) {
                    HStack(alignment: .top, spacing: 12) {
                        Text(store.emoji)
                            .font(.system(size: 32))
                            .frame(width: 52, height: 52)
                            .background(Color(red: 0.93, green: 0.99, blue: 0.96))
                            .clipShape(RoundedRectangle(cornerRadius: 14))
                        
                        VStack(alignment: .leading, spacing: 3) {
                            HStack(spacing: 6) {
                                Text(store.name)
                                    .font(.system(size: 17, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                
                                Text(store.badge)
                                    .font(.system(size: 9, weight: .black))
                                    .foregroundColor(Color(red: 0.57, green: 0.25, blue: 0.05))
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 2)
                                    .background(Color(red: 0.99, green: 0.95, blue: 0.78))
                                    .clipShape(RoundedRectangle(cornerRadius: 6))
                            }
                            
                            Text(store.tagline)
                                .font(.system(size: 12))
                                .foregroundColor(.evrryMutedText)
                                .lineLimit(1)
                            
                            HStack(spacing: 8) {
                                Text("📍 \(store.area) (\(String(format: "%.1f", store.distanceKm)) km)")
                                    .font(.system(size: 11, weight: .medium))
                                    .foregroundColor(.evrryMutedText)
                                
                                Text("⭐ \(String(format: "%.1f", store.rating)) (\(store.reviewCount))")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                            }
                        }
                    }
                    
                    Divider()
                    
                    HStack {
                        Text("Min Order: Rs \(Int(store.minOrderNpr))")
                            .font(.system(size: 11, weight: .semibold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("•")
                            .foregroundColor(.evrryMutedText)
                        Text("Free delivery above Rs \(Int(store.freeDeliveryAboveNpr))")
                            .font(.system(size: 11, weight: .semibold))
                            .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                    }
                }
                .padding(14)
                .background(Color.white)
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                .padding(.horizontal, 16)
                .padding(.vertical, 4)
                
                // 3. IN-STORE SEARCH BAR
                HStack(spacing: 8) {
                    Image(systemName: "magnifyingglass")
                        .font(.system(size: 14))
                        .foregroundColor(.evrryMutedText)
                    
                    TextField("Search items in \(store.name)...", text: $storeSearch)
                        .font(.system(size: 13))
                    
                    if !storeSearch.isEmpty {
                        Button(action: { storeSearch = "" }) {
                            Image(systemName: "xmark.circle.fill")
                                .font(.system(size: 14))
                                .foregroundColor(.evrryMutedText)
                        }
                    }
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 9)
                .background(Color.white)
                .clipShape(RoundedRectangle(cornerRadius: 12))
                .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                .padding(.horizontal, 16)
                .padding(.top, 4)
                
                // 4. AISLE HORIZONTAL TABS
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(allAisles, id: \.self) { aisle in
                            let isSelected = selectedAisle == aisle
                            Button(action: { selectedAisle = aisle }) {
                                Text(aisle)
                                    .font(.system(size: 12, weight: isSelected ? .bold : .medium))
                                    .foregroundColor(isSelected ? .white : .evrryDarkCharcoal)
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 6)
                                    .background(isSelected ? Color(red: 0.02, green: 0.47, blue: 0.34) : Color.white)
                                    .clipShape(Capsule())
                                    .overlay(Capsule().stroke(isSelected ? Color.clear : Color.evrrySubtleBorder, lineWidth: 1))
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                }
                
                // 5. PRODUCT GRID
                ScrollView {
                    LazyVGrid(columns: columns, spacing: 10) {
                        ForEach(filteredProducts) { product in
                            StoreProductCard(
                                product: product,
                                store: store,
                                exploreViewModel: exploreViewModel,
                                onAddRequested: {
                                    if exploreViewModel.hasConflict(vendorId: store.id, vertical: "grocery") {
                                        pendingProductToAdd = product
                                        showConflictAlert = true
                                    } else {
                                        let catalogItem = CatalogItem(
                                            id: product.id,
                                            vertical: "grocery",
                                            title: product.title,
                                            subtitle: "\(product.packSize) • \(product.brand)",
                                            priceNpr: product.priceNpr,
                                            originalPriceNpr: product.mrpNpr,
                                            vendorName: store.name,
                                            vendorId: store.id,
                                            rating: store.rating,
                                            etaMinutes: 30,
                                            tags: product.tags
                                        )
                                        exploreViewModel.addToCart(catalogItem)
                                    }
                                }
                            )
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.bottom, cart.itemCount > 0 ? 90 : 24)
                }
            }
            
            // 6. FLOATING BASKET ACTION BAR
            if cart.itemCount > 0 {
                VStack(spacing: 6) {
                    Button(action: onNavigateToCart) {
                        HStack {
                            HStack(spacing: 10) {
                                Image(systemName: "cart.fill")
                                    .font(.system(size: 15))
                                    .foregroundColor(.white)
                                    .padding(8)
                                    .background(Color.white.opacity(0.2))
                                    .clipShape(Circle())
                                
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("\(cart.itemCount) \(cart.itemCount == 1 ? "ITEM" : "ITEMS") • Rs \(Int(cart.finalPayableNpr))")
                                        .font(.system(size: 12, weight: .black))
                                        .foregroundColor(.white)
                                    Text(cart.vendorName)
                                        .font(.system(size: 10))
                                        .foregroundColor(.white.opacity(0.85))
                                        .lineLimit(1)
                                }
                            }
                            
                            Spacer()
                            
                            HStack(spacing: 4) {
                                Text("View Basket")
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.white)
                                Image(systemName: "arrow.right")
                                    .font(.system(size: 13, weight: .bold))
                                    .foregroundColor(.white)
                            }
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 12)
                        .background(Color(red: 0.02, green: 0.47, blue: 0.34))
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .shadow(color: Color.black.opacity(0.12), radius: 8, y: 3)
                    }
                }
                .padding(16)
            }
        }
        .alert(isPresented: $showConflictAlert) {
            Alert(
                title: Text("Replace Cart Items?"),
                message: Text("Your grocery basket contains items from \(exploreViewModel.cart.vendorName). Would you like to clear conflicting grocery items and start adding items from \(store.name)?"),
                primaryButton: .destructive(Text("Clear & Add")) {
                    if let product = pendingProductToAdd {
                        let catalogItem = CatalogItem(
                            id: product.id,
                            vertical: "grocery",
                            title: product.title,
                            subtitle: "\(product.packSize) • \(product.brand)",
                            priceNpr: product.priceNpr,
                            originalPriceNpr: product.mrpNpr,
                            vendorName: store.name,
                            vendorId: store.id,
                            rating: store.rating,
                            etaMinutes: 30,
                            tags: product.tags
                        )
                        exploreViewModel.clearAndAdd(catalogItem)
                    }
                    pendingProductToAdd = nil
                },
                secondaryButton: .cancel(Text("Keep Current Cart")) {
                    pendingProductToAdd = nil
                }
            )
        }
    }
}

public struct StoreProductCard: View {
    public let product: GroceryProduct
    public let store: GroceryStore
    @ObservedObject public var exploreViewModel: ExploreViewModel
    public let onAddRequested: () -> Void
    
    public init(
        product: GroceryProduct,
        store: GroceryStore,
        exploreViewModel: ExploreViewModel,
        onAddRequested: @escaping () -> Void
    ) {
        self.product = product
        self.store = store
        self.exploreViewModel = exploreViewModel
        self.onAddRequested = onAddRequested
    }
    
    public var body: some View {
        let currentQty = exploreViewModel.cart.items.first(where: { $0.item.id == product.id })?.quantity ?? 0
        let isOutOfStock = product.stockQuantity <= 0
        
        VStack(alignment: .leading, spacing: 6) {
            // Product Icon Tile
            ZStack {
                Color(red: 0.95, green: 0.96, blue: 0.98)
                    .frame(height: 100)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                
                Text(emojiForCategory(product.categoryId))
                    .font(.system(size: 42))
            }
            
            Text(product.packSize)
                .font(.system(size: 10, weight: .bold))
                .foregroundColor(.evrryMutedText)
            
            Text(product.title)
                .font(.system(size: 13, weight: .bold))
                .foregroundColor(.evrryDarkCharcoal)
                .lineLimit(2)
                .frame(height: 34, alignment: .topLeading)
            
            HStack {
                VStack(alignment: .leading, spacing: 1) {
                    Text("Rs \(Int(product.priceNpr))")
                        .font(.system(size: 13, weight: .black))
                        .foregroundColor(.evrryDarkCharcoal)
                    if product.discountPercent > 0 {
                        Text("Rs \(Int(product.mrpNpr))")
                            .font(.system(size: 10))
                            .foregroundColor(.evrryMutedText)
                            .strikethrough()
                    }
                }
                
                Spacer()
                
                if isOutOfStock {
                    Text("Sold Out")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 5)
                        .background(Color(red: 0.95, green: 0.96, blue: 0.98))
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                } else if currentQty == 0 {
                    Button(action: onAddRequested) {
                        Text("ADD")
                            .font(.system(size: 11, weight: .black))
                            .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                            .padding(.horizontal, 14)
                            .padding(.vertical, 6)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 8))
                            .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color(red: 0.02, green: 0.47, blue: 0.34), lineWidth: 1.5))
                    }
                } else {
                    HStack(spacing: 8) {
                        Button(action: {
                            exploreViewModel.removeFromCart(product.id)
                        }) {
                            Text("−")
                                .font(.system(size: 14, weight: .black))
                                .foregroundColor(.white)
                        }
                        
                        Text("\(currentQty)")
                            .font(.system(size: 12, weight: .black))
                            .foregroundColor(.white)
                        
                        Button(action: {
                            if currentQty < product.stockQuantity && currentQty < product.maxPerOrder {
                                onAddRequested()
                            }
                        }) {
                            Text("+")
                                .font(.system(size: 14, weight: .black))
                                .foregroundColor(.white)
                        }
                    }
                    .padding(.horizontal, 8)
                    .padding(.vertical, 5)
                    .background(Color(red: 0.02, green: 0.47, blue: 0.34))
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                }
            }
        }
        .padding(10)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 14))
        .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1))
    }
    
    private func emojiForCategory(_ categoryId: String) -> String {
        switch categoryId {
        case "dairy": return "🥛"
        case "produce": return "🥦"
        case "instant": return "🍜"
        case "personal": return "🧴"
        case "cleaning": return "🧼"
        case "pantry": return "🌾"
        default: return "🛍️"
        }
    }
}
