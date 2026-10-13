//
//  GroceryHomeView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Production Express Quick Mart Grocery Quick Mart Home View
//  1:1 Parity with Android GroceryHomeScreen
//

import SwiftUI

public struct GroceryHomeView: View {
    @ObservedObject public var groceryViewModel: GroceryViewModel
    @ObservedObject public var exploreViewModel: ExploreViewModel
    public let onBack: () -> Void
    public let onNavigateToCart: () -> Void
    public let onStoreClick: (GroceryStore) -> Void
    
    public init(
        groceryViewModel: GroceryViewModel,
        exploreViewModel: ExploreViewModel,
        onBack: @escaping () -> Void,
        onNavigateToCart: @escaping () -> Void,
        onStoreClick: @escaping (GroceryStore) -> Void = { _ in }
    ) {
        self.groceryViewModel = groceryViewModel
        self.exploreViewModel = exploreViewModel
        self.onBack = onBack
        self.onNavigateToCart = onNavigateToCart
        self.onStoreClick = onStoreClick
    }
    
    private let columns = [
        GridItem(.flexible(), spacing: 10),
        GridItem(.flexible(), spacing: 10)
    ]
    
    public var body: some View {
        let activeHub = groceryViewModel.activeHub
        let currentCategoryObj = GroceryCatalogData.categories.first(where: { $0.id == groceryViewModel.selectedCategoryId }) ?? GroceryCatalogData.categories[0]
        let filteredProducts = groceryViewModel.getFilteredProducts()
        
        ZStack(alignment: .bottom) {
            Color(red: 0.98, green: 0.98, blue: 1.0)
                .ignoresSafeArea()
            
            VStack(spacing: 0) {
                // 1. PINNED TOP NAVIGATION BAR (Mandatory 44pt in-app back for iOS parity)
                HStack {
                    Button(action: onBack) {
                        HStack(spacing: 4) {
                            Image(systemName: "chevron.left")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Text("Explore Verticals")
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
                            Text("\(activeHub.etaMinutes) min delivery")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                        }
                        .padding(.horizontal, 10)
                        .padding(.vertical, 5)
                        .background(Color(red: 0.93, green: 0.99, blue: 0.96))
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color(red: 0.65, green: 0.95, blue: 0.82), lineWidth: 1)
                        )
                        
                        // Top-Right Cart Button with badge
                        Button(action: onNavigateToCart) {
                            ZStack(alignment: .topTrailing) {
                                Circle()
                                    .fill(Color.white)
                                    .frame(width: 40, height: 40)
                                    .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                    .shadow(color: Color.black.opacity(0.04), radius: 2, y: 1)
                                
                                Image(systemName: "cart.fill")
                                    .font(.system(size: 16))
                                    .foregroundColor(exploreViewModel.cart.itemCount > 0 ? Color(red: 0.02, green: 0.47, blue: 0.34) : .evrryDarkCharcoal)
                                    .frame(width: 40, height: 40)
                                
                                if exploreViewModel.cart.itemCount > 0 {
                                    Text("\(min(exploreViewModel.cart.itemCount, 99))")
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
                        .frame(minWidth: 44, minHeight: 44)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 10)
                
                // DUAL-MODE FULFILLMENT SELECTOR: Express Dark Store vs. Supermarket Stores
                HStack(spacing: 4) {
                    // Express Tab
                    Button(action: { groceryViewModel.setDeliveryMode(.expressDarkstore) }) {
                        HStack(spacing: 4) {
                            Text("⚡").font(.system(size: 12))
                            Text("10m Express Mart")
                                .font(.system(size: 12, weight: groceryViewModel.deliveryMode == .expressDarkstore ? .bold : .medium))
                                .foregroundColor(groceryViewModel.deliveryMode == .expressDarkstore ? .white : .evrryMutedText)
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(groceryViewModel.deliveryMode == .expressDarkstore ? Color(red: 0.02, green: 0.47, blue: 0.34) : Color.clear)
                        .clipShape(RoundedRectangle(cornerRadius: 9))
                    }
                    
                    // Supermarket Stores Tab
                    Button(action: { groceryViewModel.setDeliveryMode(.groceryStores) }) {
                        HStack(spacing: 4) {
                            Text("🏪").font(.system(size: 12))
                            Text("Supermarket Stores")
                                .font(.system(size: 12, weight: groceryViewModel.deliveryMode == .groceryStores ? .bold : .medium))
                                .foregroundColor(groceryViewModel.deliveryMode == .groceryStores ? .white : .evrryMutedText)
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(groceryViewModel.deliveryMode == .groceryStores ? Color(red: 0.02, green: 0.47, blue: 0.34) : Color.clear)
                        .clipShape(RoundedRectangle(cornerRadius: 9))
                    }
                }
                .padding(3)
                .background(Color(red: 0.95, green: 0.96, blue: 0.98))
                .clipShape(RoundedRectangle(cornerRadius: 12))
                .padding(.horizontal, 16)
                .padding(.vertical, 4)
                
                if groceryViewModel.deliveryMode == .groceryStores {
                    // SUPERMARKET STORES DIRECTORY
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Partner Supermarkets")
                            .font(.system(size: 22, weight: .black))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("Shop from Bhatbhateni, Big Mart & local Kathmandu grocery stores")
                            .font(.system(size: 12))
                            .foregroundColor(.evrryMutedText)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 4)
                    
                    // Stores Search Bar
                    HStack(spacing: 8) {
                        Image(systemName: "magnifyingglass")
                            .font(.system(size: 14))
                            .foregroundColor(.evrryMutedText)
                        
                        TextField("Search stores (Bhatbhateni, Big Mart, Kirana)...", text: $groceryViewModel.storeSearchQuery)
                            .font(.system(size: 12, weight: .medium))
                        
                        if !groceryViewModel.storeSearchQuery.isEmpty {
                            Button(action: { groceryViewModel.storeSearchQuery = "" }) {
                                Image(systemName: "xmark.circle.fill")
                                    .font(.system(size: 14))
                                    .foregroundColor(.evrryMutedText)
                            }
                        }
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 10)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
                    .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    .padding(.horizontal, 16)
                    .padding(.vertical, 4)
                    
                    // Store Filter Chips
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(["All", "Supermarkets", "Daily Marts", "Organic & Farm", "Local Kirana"], id: \.self) { filterName in
                                let isSelected = groceryViewModel.storeFilterType == filterName
                                Button(action: { groceryViewModel.setStoreFilterType(filterName) }) {
                                    Text(filterName)
                                        .font(.system(size: 11, weight: isSelected ? .bold : .medium))
                                        .foregroundColor(isSelected ? .white : .evrryDarkCharcoal)
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 6)
                                        .background(isSelected ? Color(red: 0.02, green: 0.47, blue: 0.34) : Color.white)
                                        .clipShape(Capsule())
                                        .overlay(Capsule().stroke(isSelected ? Color.clear : Color.evrrySubtleBorder, lineWidth: 1))
                                }
                            }
                        }
                        .padding(.horizontal, 16)
                    }
                    .padding(.vertical, 4)
                    
                    // Store List ScrollView
                    ScrollView {
                        VStack(spacing: 12) {
                            // Marketplace banner
                            ZStack {
                                LinearGradient(
                                    colors: [Color(red: 0.12, green: 0.23, blue: 0.54), Color(red: 0.15, green: 0.39, blue: 0.92)],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                                HStack {
                                    VStack(alignment: .leading, spacing: 4) {
                                        Text("🏪 PHYSICAL SUPERMARKET NETWORK")
                                            .font(.system(size: 9, weight: .black))
                                            .foregroundColor(.white)
                                            .padding(.horizontal, 8)
                                            .padding(.vertical, 3)
                                            .background(Color.white.opacity(0.2))
                                            .clipShape(Capsule())
                                        
                                        Text("Bhatbhateni & Big Mart")
                                            .font(.system(size: 15, weight: .bold))
                                            .foregroundColor(.white)
                                        
                                        Text("Doorstep fulfillment from real retail store shelves in 25-45 mins")
                                            .font(.system(size: 11))
                                            .foregroundColor(.white.opacity(0.85))
                                    }
                                    Spacer()
                                    Text("🛒").font(.system(size: 36))
                                }
                                .padding(14)
                            }
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                            .padding(.horizontal, 16)
                            
                            HStack {
                                Text("Available Stores near you (\(groceryViewModel.getStores().count))")
                                    .font(.system(size: 13, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Spacer()
                                Text("Delivery: 20-45 mins")
                                    .font(.system(size: 11, weight: .semibold))
                                    .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                            }
                            .padding(.horizontal, 16)
                            
                            let stores = groceryViewModel.getStores()
                            if stores.isEmpty {
                                VStack(spacing: 8) {
                                    Text("🏪").font(.system(size: 40))
                                    Text("No partner stores match your search")
                                        .font(.system(size: 14, weight: .bold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                    Text("Try clearing filters or search term")
                                        .font(.system(size: 12))
                                        .foregroundColor(.evrryMutedText)
                                }
                                .padding(.vertical, 40)
                            } else {
                                ForEach(stores) { store in
                                    StoreDirectoryCardView(store: store, onShop: { onStoreClick(store) })
                                }
                                .padding(.horizontal, 16)
                            }
                            
                            Spacer()
                                .frame(height: exploreViewModel.cart.itemCount > 0 ? 120 : 30)
                        }
                    }
                } else {
                    // 2. LOCATION & TITLE
                VStack(alignment: .leading, spacing: 2) {
                    HStack(spacing: 6) {
                        Text("📍").font(.system(size: 13))
                        Text(activeHub.name)
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                    }
                    Text("Groceries & Essentials")
                        .font(.system(size: 24, weight: .black))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text("Fresh dairy, produce & daily staples delivered in minutes")
                        .font(.system(size: 12))
                        .foregroundColor(.evrryMutedText)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 16)
                .padding(.vertical, 4)
                
                // 3. SEARCH BAR
                HStack(spacing: 8) {
                    Image(systemName: "magnifyingglass")
                        .font(.system(size: 14))
                        .foregroundColor(.evrryMutedText)
                    
                    TextField("Search milk, eggs, wai wai, atta...", text: $groceryViewModel.searchQuery)
                        .font(.system(size: 12, weight: .medium))
                    
                    if !groceryViewModel.searchQuery.isEmpty {
                        Button(action: { groceryViewModel.searchQuery = "" }) {
                            Image(systemName: "xmark.circle.fill")
                                .font(.system(size: 14))
                                .foregroundColor(.evrryMutedText)
                        }
                    }
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 10)
                .background(Color.white)
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .stroke(Color.evrrySubtleBorder, lineWidth: 1)
                )
                .padding(.horizontal, 16)
                .padding(.top, 6)
                
                // 4. MAIN SCROLLABLE CONTENT (Quick Mart 2-column grid + Aisles)
                ScrollView {
                    VStack(spacing: 12) {
                        // PROMO BANNER
                        ZStack {
                            LinearGradient(
                                colors: [Color(red: 0.02, green: 0.59, blue: 0.41), Color(red: 0.05, green: 0.58, blue: 0.53)],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                            HStack {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("🥛 MORNING ESSENTIALS")
                                        .font(.system(size: 9, weight: .black))
                                        .foregroundColor(.white)
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 3)
                                        .background(Color.white.opacity(0.2))
                                        .clipShape(Capsule())
                                    
                                    Text("Fresh DDC Milk & Bakery")
                                        .font(.system(size: 15, weight: .bold))
                                        .foregroundColor(.white)
                                    
                                    Text("Free delivery on orders above Rs. 1,000")
                                        .font(.system(size: 11))
                                        .foregroundColor(.white.opacity(0.85))
                                }
                                Spacer()
                                Text("🥬").font(.system(size: 36))
                            }
                            .padding(14)
                        }
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .padding(.horizontal, 16)
                        .padding(.top, 8)
                        
                        // AISLES HORIZONTAL SCROLLER
                        VStack(alignment: .leading, spacing: 6) {
                            Text("EXPLORE AISLES")
                                .font(.system(size: 11, weight: .black))
                                .foregroundColor(.evrryMutedText)
                                .padding(.horizontal, 16)
                            
                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 8) {
                                    ForEach(GroceryCatalogData.categories) { cat in
                                        let isSelected = groceryViewModel.selectedCategoryId == cat.id
                                        Button(action: { groceryViewModel.selectCategory(cat.id) }) {
                                            HStack(spacing: 6) {
                                                Text(cat.emoji).font(.system(size: 14))
                                                Text(cat.name)
                                                    .font(.system(size: 12, weight: isSelected ? .bold : .semibold))
                                                    .foregroundColor(isSelected ? .white : .evrryDarkCharcoal)
                                            }
                                            .padding(.horizontal, 12)
                                            .padding(.vertical, 8)
                                            .background(isSelected ? Color(red: 0.02, green: 0.47, blue: 0.34) : Color.white)
                                            .clipShape(RoundedRectangle(cornerRadius: 12))
                                            .overlay(
                                                RoundedRectangle(cornerRadius: 12)
                                                    .stroke(isSelected ? Color(red: 0.02, green: 0.47, blue: 0.34) : Color.evrrySubtleBorder, lineWidth: 1)
                                            )
                                        }
                                    }
                                }
                                .padding(.horizontal, 16)
                            }
                        }
                        
                        // SUBCATEGORY PILLS
                        if !currentCategoryObj.subcategories.isEmpty {
                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 6) {
                                    ForEach(currentCategoryObj.subcategories, id: \.self) { subCat in
                                        let isSubSelected = groceryViewModel.selectedSubCategory == subCat
                                        Button(action: { groceryViewModel.selectSubCategory(subCat) }) {
                                            Text(subCat)
                                                .font(.system(size: 11, weight: isSubSelected ? .bold : .medium))
                                                .foregroundColor(isSubSelected ? Color(red: 0.02, green: 0.47, blue: 0.34) : .evrryDarkCharcoal)
                                                .padding(.horizontal, 10)
                                                .padding(.vertical, 5)
                                                .background(isSubSelected ? Color(red: 0.93, green: 0.99, blue: 0.96) : Color.white)
                                                .clipShape(Capsule())
                                                .overlay(
                                                    Capsule()
                                                        .stroke(isSubSelected ? Color(red: 0.06, green: 0.73, blue: 0.51) : Color.evrrySubtleBorder, lineWidth: 1)
                                                )
                                        }
                                    }
                                }
                                .padding(.horizontal, 16)
                            }
                        }
                        
                        // COUNT HEADER
                        HStack {
                            Text("\(currentCategoryObj.name) (\(filteredProducts.count))")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Spacer()
                            Text("⚡ 12m dispatch")
                                .font(.system(size: 10, weight: .bold))
                                .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                        }
                        .padding(.horizontal, 16)
                        .padding(.top, 4)
                        
                        // 2-COLUMN PRODUCT GRID
                        LazyVGrid(columns: columns, spacing: 10) {
                            ForEach(filteredProducts) { product in
                                GroceryProductCardView(
                                    product: product,
                                    activeHub: activeHub,
                                    groceryViewModel: groceryViewModel,
                                    exploreViewModel: exploreViewModel
                                )
                            }
                        }
                        .padding(.horizontal, 16)
                        
                        // 4B. DID YOU FORGET ANYTHING? (Quick Mart Pre-Checkout Impulse Additions)
                        let cartItemIds = Set(exploreViewModel.cart.items.map { $0.item.id })
                        let impulseItems = groceryViewModel.getImpulseAdditions(cartItemIds: cartItemIds)
                        if !impulseItems.isEmpty {
                            VStack(alignment: .leading, spacing: 8) {
                                HStack {
                                    Text("DID YOU FORGET ANYTHING? ⚡")
                                        .font(.system(size: 11, weight: .black))
                                        .foregroundColor(.evrryMutedText)
                                    Spacer()
                                    Text("Quick Add")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                                }
                                .padding(.horizontal, 16)
                                
                                ScrollView(.horizontal, showsIndicators: false) {
                                    HStack(spacing: 10) {
                                        ForEach(impulseItems) { item in
                                            let catalogItem = CatalogItem(
                                                id: item.id,
                                                vertical: "grocery",
                                                title: item.title,
                                                subtitle: "\(item.packSize) • \(item.brand)",
                                                priceNpr: item.priceNpr,
                                                originalPriceNpr: item.mrpNpr,
                                                vendorName: activeHub.name,
                                                vendorId: activeHub.id,
                                                rating: 4.9,
                                                etaMinutes: activeHub.etaMinutes,
                                                tags: item.tags
                                            )
                                            
                                            VStack(alignment: .leading, spacing: 4) {
                                                ZStack {
                                                    Color(red: 0.95, green: 0.96, blue: 0.98)
                                                    Text(getEmoji(for: item.categoryId))
                                                        .font(.system(size: 26))
                                                }
                                                .frame(width: 130, height: 65)
                                                .clipShape(RoundedRectangle(cornerRadius: 8))
                                                
                                                Text(item.packSize)
                                                    .font(.system(size: 9, weight: .bold))
                                                    .foregroundColor(.evrryMutedText)
                                                
                                                Text(item.title)
                                                    .font(.system(size: 11, weight: .bold))
                                                    .foregroundColor(.evrryDarkCharcoal)
                                                    .lineLimit(1)
                                                
                                                HStack {
                                                    Text("Rs \(Int(item.priceNpr))")
                                                        .font(.system(size: 12, weight: .black))
                                                        .foregroundColor(.evrryDarkCharcoal)
                                                    Spacer()
                                                    Button(action: {
                                                        if exploreViewModel.hasConflict(vendorId: activeHub.id, vertical: "grocery") {
                                                            exploreViewModel.clearAndAdd(catalogItem)
                                                        } else {
                                                            _ = exploreViewModel.addToCart(catalogItem)
                                                        }
                                                    }) {
                                                        Text("+ ADD")
                                                            .font(.system(size: 10, weight: .black))
                                                            .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                                                            .padding(.horizontal, 6)
                                                            .padding(.vertical, 3)
                                                            .background(Color.white)
                                                            .clipShape(RoundedRectangle(cornerRadius: 6))
                                                            .overlay(
                                                                RoundedRectangle(cornerRadius: 6)
                                                                    .stroke(Color(red: 0.02, green: 0.47, blue: 0.34), lineWidth: 1)
                                                            )
                                                    }
                                                }
                                            }
                                            .padding(10)
                                            .frame(width: 145)
                                            .background(Color.white)
                                            .clipShape(RoundedRectangle(cornerRadius: 14))
                                            .overlay(
                                                RoundedRectangle(cornerRadius: 14)
                                                    .stroke(Color.evrrySubtleBorder, lineWidth: 1)
                                            )
                                        }
                                    }
                                    .padding(.horizontal, 16)
                                }
                            }
                            .padding(.top, 8)
                        }
                        
                        Spacer()
                            .frame(height: exploreViewModel.cart.itemCount > 0 ? 120 : 30)
                    }
                }
            }
        }
            
            // 5. PERSISTENT FLOATING BASKET & FREE DELIVERY NUDGE (Express Commerce Standard)
            if exploreViewModel.cart.itemCount > 0 {
                VStack(spacing: 6) {
                    // FREE DELIVERY PROGRESS BAR
                    VStack(alignment: .leading, spacing: 4) {
                        HStack {
                            if exploreViewModel.cart.isFreeDeliveryEligible {
                                HStack(spacing: 4) {
                                    Text("🎉").font(.system(size: 12))
                                    Text("FREE Delivery Unlocked! (Saved Rs 50)")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                                }
                            } else {
                                HStack(spacing: 4) {
                                    Text("🚚").font(.system(size: 12))
                                    Text("Add Rs \(Int(exploreViewModel.cart.amountNeededForFreeDelivery)) more for FREE Delivery")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                }
                            }
                            Spacer()
                            Text("\(Int(exploreViewModel.cart.freeDeliveryProgress * 100))%")
                                .font(.system(size: 10, weight: .bold))
                                .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                        }
                        
                        GeometryReader { geo in
                            ZStack(alignment: .leading) {
                                RoundedRectangle(cornerRadius: 3)
                                    .fill(Color(red: 0.89, green: 0.91, blue: 0.94))
                                    .frame(height: 5)
                                RoundedRectangle(cornerRadius: 3)
                                    .fill(Color(red: 0.02, green: 0.47, blue: 0.34))
                                    .frame(width: geo.size.width * CGFloat(exploreViewModel.cart.freeDeliveryProgress), height: 5)
                            }
                        }
                        .frame(height: 5)
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 8)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color(red: 0.82, green: 0.98, blue: 0.90), lineWidth: 1)
                    )
                    .shadow(color: Color.black.opacity(0.06), radius: 4, x: 0, y: 2)
                    
                    // FLOATING BASKET ACTION BAR
                    Button(action: onNavigateToCart) {
                        HStack {
                            HStack(spacing: 10) {
                                ZStack {
                                    Circle()
                                        .fill(Color.white.opacity(0.2))
                                        .frame(width: 32, height: 32)
                                    Image(systemName: "cart.fill")
                                        .font(.system(size: 14))
                                        .foregroundColor(.white)
                                }
                                VStack(alignment: .leading, spacing: 1) {
                                    Text("\(exploreViewModel.cart.itemCount) \(exploreViewModel.cart.itemCount == 1 ? "ITEM" : "ITEMS")")
                                        .font(.system(size: 10, weight: .black))
                                        .foregroundColor(.white.opacity(0.85))
                                    Text("Rs \(Int(exploreViewModel.cart.subtotal))")
                                        .font(.system(size: 15, weight: .bold))
                                        .foregroundColor(.white)
                                }
                            }
                            Spacer()
                            HStack(spacing: 4) {
                                Text("View Basket")
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.white)
                                Text("→")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(.white)
                            }
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 14)
                        .background(Color(red: 0.02, green: 0.47, blue: 0.34))
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .shadow(color: Color.black.opacity(0.15), radius: 8, x: 0, y: 4)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 16)
            }
        }
        .navigationBarHidden(true)
    }
    
    private func getEmoji(for categoryId: String) -> String {
        switch categoryId {
        case "dairy": return "🥛"
        case "produce": return "🥦"
        case "instant": return "🍜"
        case "staples": return "🍚"
        case "beverages": return "🧃"
        case "bakery": return "🍞"
        case "cleaning": return "🧼"
        default: return "🛍️"
        }
    }
}

/**
 * Express Quick Mart Single-Card Grocery Product Card
 * Features inline variant pills (e.g., 500 ml / 1 Litre or 6 Pcs / Crate of 30)
 * avoiding catalog card duplication and updating price/cart mapping dynamically.
 */
struct GroceryProductCardView: View {
    let product: GroceryProduct
    let activeHub: GroceryHub
    @ObservedObject var groceryViewModel: GroceryViewModel
    @ObservedObject var exploreViewModel: ExploreViewModel
    
    @State private var selectedVariantIndex: Int = 0
    
    private var activeVariant: GroceryProductVariant? {
        if product.variants.indices.contains(selectedVariantIndex) {
            return product.variants[selectedVariantIndex]
        }
        return product.variants.first
    }
    
    private var activeItemId: String {
        activeVariant?.id ?? product.id
    }
    
    private var currentPrice: Double {
        activeVariant?.priceNpr ?? product.priceNpr
    }
    
    private var currentMrp: Double {
        activeVariant?.mrpNpr ?? product.mrpNpr
    }
    
    private var currentPackSize: String {
        activeVariant?.packSize ?? product.packSize
    }
    
    private var currentStock: Int {
        activeVariant?.stockQuantity ?? product.stockQuantity
    }
    
    private var currentDiscount: Int {
        activeVariant?.discountPercent ?? product.discountPercent
    }
    
    private var catalogItem: CatalogItem {
        let title = (activeVariant != nil && product.variants.count > 1) ? "\(product.title) (\(activeVariant!.packSize))" : product.title
        return CatalogItem(
            id: activeItemId,
            vertical: "grocery",
            title: title,
            subtitle: "\(currentPackSize) • \(product.brand)",
            priceNpr: currentPrice,
            originalPriceNpr: currentMrp,
            vendorName: activeHub.name,
            vendorId: activeHub.id,
            rating: 4.9,
            etaMinutes: activeHub.etaMinutes,
            tags: product.tags
        )
    }
    
    private var isOutOfStock: Bool {
        currentStock <= 0
    }
    
    private var isLowStock: Bool {
        (1...3).contains(currentStock)
    }
    
    var body: some View {
        let currentQty = exploreViewModel.getItemQty(activeItemId)
        
        VStack(alignment: .leading, spacing: 6) {
            // Thumbnail Container
            ZStack {
                Color(red: 0.95, green: 0.96, blue: 0.98)
                Text(getEmoji(for: product.categoryId))
                    .font(.system(size: 38))
                
                if currentDiscount > 0 {
                    VStack {
                        HStack {
                            Text("\(currentDiscount)% OFF")
                                .font(.system(size: 9, weight: .black))
                                .foregroundColor(.white)
                                .padding(.horizontal, 4)
                                .padding(.vertical, 2)
                                .background(Color(red: 0.02, green: 0.47, blue: 0.34))
                                .clipShape(RoundedRectangle(cornerRadius: 4))
                            Spacer()
                        }
                        Spacer()
                    }
                    .padding(6)
                }
                
                if isLowStock {
                    VStack {
                        Spacer()
                        HStack {
                            Text("Only \(currentStock) left")
                                .font(.system(size: 9, weight: .bold))
                                .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                                .padding(.horizontal, 4)
                                .padding(.vertical, 2)
                                .background(Color(red: 1.0, green: 0.98, blue: 0.92))
                                .clipShape(RoundedRectangle(cornerRadius: 4))
                            Spacer()
                        }
                    }
                    .padding(6)
                }
            }
            .frame(height: 100)
            .clipShape(RoundedRectangle(cornerRadius: 10))
            
            // Pack Size Badge or Variant Selector Pills (Express Quick Mart Single-Card Variant Switcher)
            if product.variants.count > 1 {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 4) {
                        ForEach(Array(product.variants.enumerated()), id: \.offset) { index, variant in
                            let isSelected = index == selectedVariantIndex
                            Button(action: { selectedVariantIndex = index }) {
                                Text(variant.packSize)
                                    .font(.system(size: 9, weight: isSelected ? .black : .medium))
                                    .foregroundColor(isSelected ? Color(red: 0.02, green: 0.47, blue: 0.34) : .evrryMutedText)
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 3)
                                    .background(isSelected ? Color(red: 0.93, green: 0.99, blue: 0.96) : Color(red: 0.97, green: 0.98, blue: 0.99))
                                    .clipShape(RoundedRectangle(cornerRadius: 6))
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 6)
                                            .stroke(isSelected ? Color(red: 0.06, green: 0.73, blue: 0.51) : Color(red: 0.89, green: 0.91, blue: 0.94), lineWidth: 1)
                                    )
                            }
                            .buttonStyle(.plain)
                        }
                    }
                }
            } else {
                Text(currentPackSize)
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(.evrryMutedText)
                    .padding(.horizontal, 4)
                    .padding(.vertical, 2)
                    .background(Color(red: 0.97, green: 0.98, blue: 0.99))
                    .clipShape(RoundedRectangle(cornerRadius: 4))
            }
            
            // Title
            Text(product.title)
                .font(.system(size: 13, weight: .bold))
                .foregroundColor(.evrryDarkCharcoal)
                .lineLimit(2)
                .frame(height: 34, alignment: .topLeading)
            
            if let ne = product.titleNe {
                Text(ne)
                    .font(.system(size: 10))
                    .foregroundColor(.evrryMutedText)
                    .lineLimit(1)
            }
            
            // Price & Stepper Row
            HStack(alignment: .bottom) {
                VStack(alignment: .leading, spacing: 1) {
                    Text("Rs \(Int(currentPrice))")
                        .font(.system(size: 14, weight: .black))
                        .foregroundColor(.evrryDarkCharcoal)
                    if currentMrp > currentPrice {
                        Text("Rs \(Int(currentMrp))")
                            .font(.system(size: 10))
                            .foregroundColor(.evrryMutedText)
                            .strikethrough()
                    }
                }
                
                Spacer()
                
                // Stepper
                if isOutOfStock {
                    Text("Sold Out")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 6)
                        .background(Color(red: 0.95, green: 0.96, blue: 0.98))
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                } else if currentQty == 0 {
                    Button(action: {
                        if exploreViewModel.hasConflict(vendorId: activeHub.id, vertical: "grocery") {
                            exploreViewModel.clearAndAdd(catalogItem)
                        } else {
                            _ = exploreViewModel.addToCart(catalogItem)
                        }
                    }) {
                        Text("ADD")
                            .font(.system(size: 12, weight: .black))
                            .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                            .padding(.horizontal, 14)
                            .padding(.vertical, 6)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 8))
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color(red: 0.02, green: 0.47, blue: 0.34), lineWidth: 1.5)
                            )
                    }
                } else {
                    HStack(spacing: 6) {
                        Button(action: { exploreViewModel.removeFromCart(itemId: activeItemId) }) {
                            Text("−")
                                .font(.system(size: 14, weight: .black))
                                .foregroundColor(.white)
                        }
                        Text("\(currentQty)")
                            .font(.system(size: 12, weight: .black))
                            .foregroundColor(.white)
                        Button(action: {
                            let canInc: Bool
                            if let variant = activeVariant {
                                canInc = groceryViewModel.canIncrementVariant(variant: variant, maxPerOrder: product.maxPerOrder, currentQty: currentQty)
                            } else {
                                canInc = groceryViewModel.canIncrement(product: product, currentQty: currentQty)
                            }
                            if canInc {
                                _ = exploreViewModel.addToCart(item: catalogItem)
                            }
                        }) {
                            Text("+")
                                .font(.system(size: 14, weight: .black))
                                .foregroundColor(.white)
                        }
                    }
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(Color(red: 0.02, green: 0.47, blue: 0.34))
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                }
            }
        }
        .padding(10)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.evrrySubtleBorder, lineWidth: 1)
        )
    }
    
    private func getEmoji(for categoryId: String) -> String {
        switch categoryId {
        case "dairy": return "🥛"
        case "produce": return "🥦"
        case "instant": return "🍜"
        case "staples": return "🍚"
        case "beverages": return "🧃"
        case "bakery": return "🍞"
        case "cleaning": return "🧼"
        default: return "🛍️"
        }
    }
}

/**
 * Partner Supermarket Store Card Component (iOS)
 * Displays physical retail supermarkets (Bhatbhateni, Big Mart, Saleways, etc.)
 * with ratings, ETA (25-45m), minimum order limit, free delivery threshold, and aisles.
 */
public struct StoreDirectoryCardView: View {
    public let store: GroceryStore
    public let onShop: () -> Void
    
    public init(store: GroceryStore, onShop: @escaping () -> Void) {
        self.store = store
        self.onShop = onShop
    }
    
    public var body: some View {
        Button(action: onShop) {
            VStack(alignment: .leading, spacing: 10) {
                // Header Row
                HStack(alignment: .top, spacing: 12) {
                    Text(store.emoji)
                        .font(.system(size: 26))
                        .frame(width: 48, height: 48)
                        .background(Color(red: 0.93, green: 0.99, blue: 0.96))
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                    
                    VStack(alignment: .leading, spacing: 2) {
                        HStack(spacing: 6) {
                            Text(store.name)
                                .font(.system(size: 16, weight: .bold))
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
                    }
                }
                
                // Location & Rating row
                HStack {
                    Text("📍 \(store.area) (\(String(format: "%.1f", store.distanceKm)) km)")
                        .font(.system(size: 11, weight: .medium))
                        .foregroundColor(.evrryMutedText)
                    
                    Spacer()
                    
                    HStack(spacing: 6) {
                        Text("⭐ \(String(format: "%.1f", store.rating)) (\(store.reviewCount))")
                            .font(.system(size: 10, weight: .bold))
                            .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(Color(red: 1.0, green: 0.98, blue: 0.92))
                            .clipShape(RoundedRectangle(cornerRadius: 6))
                        
                        Text("⚡ \(store.etaRange)")
                            .font(.system(size: 10, weight: .bold))
                            .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(Color(red: 0.93, green: 0.99, blue: 0.96))
                            .clipShape(RoundedRectangle(cornerRadius: 6))
                    }
                }
                
                Divider()
                
                // Policy Strip & Shop Button
                HStack {
                    HStack(spacing: 6) {
                        Text("Min Rs \(Int(store.minOrderNpr))")
                            .font(.system(size: 11, weight: .semibold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("•")
                            .foregroundColor(.evrryMutedText)
                        Text("Free above Rs \(Int(store.freeDeliveryAboveNpr))")
                            .font(.system(size: 11, weight: .semibold))
                            .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                    }
                    
                    Spacer()
                    
                    Text("Shop Store →")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.white)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 5)
                        .background(Color(red: 0.02, green: 0.47, blue: 0.34))
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                }
                
                // Aisles Preview
                if !store.aisles.isEmpty {
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 6) {
                            ForEach(store.aisles.prefix(4), id: \.self) { aisle in
                                Text(aisle)
                                    .font(.system(size: 10))
                                    .foregroundColor(.evrryMutedText)
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 3)
                                    .background(Color(red: 0.97, green: 0.98, blue: 0.99))
                                    .clipShape(RoundedRectangle(cornerRadius: 6))
                                    .overlay(RoundedRectangle(cornerRadius: 6).stroke(Color.evrrySubtleBorder, lineWidth: 0.5))
                            }
                        }
                    }
                }
            }
            .padding(14)
            .background(Color.white)
            .clipShape(RoundedRectangle(cornerRadius: 16))
            .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
        }
        .buttonStyle(PlainButtonStyle())
    }
}

