//
//  RestaurantDetailView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Restaurant Menu & Ordering Detail View (iOS SwiftUI)
//

import SwiftUI

public struct RestaurantDetailView: View {
    public let restaurantId: String
    @ObservedObject public var foodViewModel: FoodViewModel
    @ObservedObject public var exploreViewModel: ExploreViewModel
    public let onBack: () -> Void
    public let onNavigateToCart: () -> Void
    @State private var itemToCustomize: FoodMenuItem? = nil
    @State private var conflictItem: FoodMenuItem? = nil
    @State private var showConflictAlert: Bool = false
    @State private var showInfoSheet: Bool = false
    @State private var showSwitchBanner: Bool = false
    public init(
        restaurantId: String,
        foodViewModel: FoodViewModel,
        exploreViewModel: ExploreViewModel,
        onBack: @escaping () -> Void,
        onNavigateToCart: @escaping () -> Void
    ) {
        self.restaurantId = restaurantId
        self.foodViewModel = foodViewModel
        self.exploreViewModel = exploreViewModel
        self.onBack = onBack
        self.onNavigateToCart = onNavigateToCart
    }
    
    private var restaurant: Restaurant {
        FoodCatalogData.findRestaurant(id: restaurantId) ?? FoodCatalogData.restaurants[0]
    }
    
    private var menuItems: [FoodMenuItem] {
        FoodCatalogData.menuByRestaurant(restaurantId: restaurant.id)
    }
    
    private var sections: [String] {
        var set: [String] = []
        for item in menuItems {
            if !set.contains(item.menuSection) {
                set.append(item.menuSection)
            }
        }
        return set
    }
    
    public var body: some View {
        ZStack(alignment: .bottom) {
            Color(red: 0.98, green: 0.98, blue: 1.0)
                .ignoresSafeArea()
            
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    // HERO BANNER WITH IN-APP BACK BUTTON (iOS Parity - 44x44 pt min target)
                    ZStack(alignment: .topLeading) {
                        LinearGradient(
                            colors: [restaurant.gradientFrom, restaurant.gradientTo],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                        .frame(height: 140)
                        
                        HStack(spacing: 8) {
                            Button(action: onBack) {
                                Image(systemName: "chevron.left")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(.white)
                                    .frame(width: 44, height: 44)
                                    .background(Color.black.opacity(0.35))
                                    .clipShape(Circle())
                            }
                            
                            Spacer()
                            
                            if restaurant.isPureVeg {
                                Text("100% PURE VEG")
                                    .font(.system(size: 10, weight: .black))
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 4)
                                    .background(Color(red: 0.09, green: 0.64, blue: 0.29))
                                    .clipShape(RoundedRectangle(cornerRadius: 8))
                            }
                            
                            // Top-Right Cart Button with badge
                            Button(action: onNavigateToCart) {
                                ZStack(alignment: .topTrailing) {
                                    Circle()
                                        .fill(Color.white)
                                        .frame(width: 40, height: 40)
                                        .shadow(color: Color.black.opacity(0.12), radius: 3, y: 1)
                                    
                                    Image(systemName: "cart.fill")
                                        .font(.system(size: 16))
                                        .foregroundColor(exploreViewModel.cart.itemCount > 0 ? Color(red: 0.98, green: 0.45, blue: 0.09) : .evrryDarkCharcoal)
                                        .frame(width: 40, height: 40)
                                    
                                    if exploreViewModel.cart.itemCount > 0 {
                                        Text("\(min(exploreViewModel.cart.itemCount, 99))")
                                            .font(.system(size: 10, weight: .bold))
                                            .foregroundColor(.white)
                                            .padding(.horizontal, 4)
                                            .padding(.vertical, 2)
                                            .background(Color(red: 0.98, green: 0.45, blue: 0.09))
                                            .clipShape(Capsule())
                                            .offset(x: 4, y: -4)
                                    }
                                }
                            }
                            .frame(minWidth: 44, minHeight: 44)
                        }
                        .padding(16)
                    }
                    
                    // SWITCH ALERT BANNER
                    if showSwitchBanner {
                        HStack(spacing: 8) {
                            Text("ℹ️").font(.system(size: 13))
                            Text("Started a fresh order from \(restaurant.name) — items from your previous restaurant were cleared.")
                                .font(.system(size: 12, weight: .medium))
                                .foregroundColor(Color(red: 0.57, green: 0.25, blue: 0.05))
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color(red: 0.99, green: 0.95, blue: 0.78))
                        .transition(.move(edge: .top).combined(with: .opacity))
                    }
                    
                    // RESTAURANT METADATA
                    VStack(alignment: .leading, spacing: 6) {
                        HStack {
                            Text(restaurant.name)
                                .font(.system(size: 22, weight: .black))
                                .foregroundColor(.evrryDarkCharcoal)
                            
                            Spacer()
                            
                            // Store Information Modal
                            Button(action: { showInfoSheet = true }) {
                                Image(systemName: "info.circle.fill")
                                    .font(.system(size: 20))
                                    .foregroundColor(Color(red: 0.14, green: 0.39, blue: 0.92))
                                    .frame(width: 44, height: 44)
                            }
                        }
                        
                        Text(restaurant.cuisines.joined(separator: ", "))
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)
                        
                        HStack(spacing: 10) {
                            HStack(spacing: 3) {
                                Text("★").font(.system(size: 10)).foregroundColor(.white)
                                Text(String(format: "%.1f", restaurant.rating))
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(.white)
                            }
                            .padding(.horizontal, 6)
                            .padding(.vertical, 3)
                            .background(Color(red: 0.09, green: 0.64, blue: 0.29))
                            .clipShape(RoundedRectangle(cornerRadius: 6))
                            
                            Text(exploreViewModel.cart.fulfillmentType == .pickup ? "🛍️ ~\(restaurant.pickupEtaMinutes) min pickup" : "🕒 \(restaurant.deliveryEtaMinutes) min delivery")
                                .font(.system(size: 12, weight: .medium))
                                .foregroundColor(.evrryDarkCharcoal)
                            
                            Text("•").foregroundColor(.evrryMutedText)
                            
                            Text("📍 \(restaurant.area)")
                                .font(.system(size: 12))
                                .foregroundColor(.evrryMutedText)
                            
                            Text("•").foregroundColor(.evrryMutedText)
                            
                            Text("Rs \(restaurant.priceForTwo) for two")
                                .font(.system(size: 12))
                                .foregroundColor(.evrryDarkCharcoal)
                        }
                        .padding(.top, 4)
                        
                        if let offer = restaurant.offer {
                            HStack(spacing: 4) {
                                Text("🏷️").font(.system(size: 11))
                                Text(offer)
                                    .font(.system(size: 12, weight: .bold))
                                    .foregroundColor(Color(red: 0.76, green: 0.25, blue: 0.05))
                            }
                            .padding(.horizontal, 10)
                            .padding(.vertical, 5)
                            .background(Color(red: 1.0, green: 0.97, blue: 0.93))
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(Color(red: 1.0, green: 0.93, blue: 0.84), lineWidth: 1)
                            )
                            .padding(.top, 4)
                        }
                        
                        // Delivery vs Takeaway Switcher
                        FulfillmentModeSwitcher(selectedType: $exploreViewModel.cart.fulfillmentType)
                            .padding(.top, 6)
                        
                        if exploreViewModel.cart.fulfillmentType == .pickup {
                            HStack(spacing: 8) {
                                Text("📍").font(.system(size: 14))
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Self-Pickup from Kitchen")
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(Color(red: 0.14, green: 0.39, blue: 0.92))
                                    Text(restaurant.fullAddress)
                                        .font(.system(size: 11))
                                        .foregroundColor(.evrryDarkCharcoal)
                                }
                            }
                            .padding(10)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(Color(red: 0.93, green: 0.96, blue: 1.0))
                            .cornerRadius(10)
                            .padding(.top, 4)
                        }
                    }
                    .padding(16)
                    .background(Color.white)
                    
                    // MENU SECTIONS
                    VStack(alignment: .leading, spacing: 16) {
                        ForEach(sections, id: \.self) { section in
                            VStack(alignment: .leading, spacing: 8) {
                                Text(section.uppercased())
                                    .font(.system(size: 12, weight: .black))
                                    .foregroundColor(.evrryMutedText)
                                    .padding(.horizontal, 16)
                                    .padding(.top, 14)
                                    
                                let itemsInSection = menuItems.filter { $0.menuSection == section }
                                ForEach(itemsInSection) { menuItem in
                                    let qty = exploreViewModel.getItemQty(menuItem.id)
                                    MenuItemRowView(
                                        item: menuItem,
                                        quantity: qty,
                                        onAdd: {
                                            let catItem = menuItem.toCatalogItem(restaurant: restaurant)
                                            if exploreViewModel.hasConflict(with: catItem) {
                                                conflictItem = menuItem
                                                showConflictAlert = true
                                            } else {
                                                itemToCustomize = menuItem
                                            }
                                        },
                                        onRemove: {
                                            exploreViewModel.removeFromCart(menuItem.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer().frame(height: exploreViewModel.cart.itemCount > 0 ? 110 : 40)
                }
            }
            
            // FLOATING CART BAR
            if exploreViewModel.cart.itemCount > 0 {
                Button(action: onNavigateToCart) {
                    HStack {
                        HStack(spacing: 8) {
                            Image(systemName: "cart.fill")
                                .foregroundColor(.white)
                            Text("\(exploreViewModel.cart.itemCount) items in Cart")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(.white)
                        }
                        Spacer()
                        Text("View Cart · Rs \(Int(exploreViewModel.cart.totalNpr)) →")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.white)
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 14)
                    .background(Color.evrryDarkCharcoal)
                    .clipShape(RoundedRectangle(cornerRadius: 18))
                    .shadow(color: Color.black.opacity(0.15), radius: 8, x: 0, y: 4)
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 16)
            }
        }
        .sheet(item: $itemToCustomize) { item in
            FoodItemCustomizationSheet(
                item: item,
                restaurant: restaurant,
                onDismiss: { itemToCustomize = nil },
                onAddToCart: { catalogItem, options, instructions, qty in
                    if exploreViewModel.hasConflict(with: catalogItem) {
                        exploreViewModel.clearAndAdd(catalogItem, options: options, quantity: qty)
                    } else {
                        exploreViewModel.addToCartWithOptions(catalogItem, options: options, quantity: qty)
                    }
                    if !instructions.isEmpty {
                        exploreViewModel.setSpecialInstructions(instructions)
                    }
                }
            )
        }
        .sheet(isPresented: $showInfoSheet) {
            RestaurantInfoSheet(
                restaurant: restaurant,
                onDismiss: { showInfoSheet = false }
            )
        }
        .alert(isPresented: $showConflictAlert) {
            Alert(
                title: Text("Replace cart items?"),
                message: Text("Your cart contains dishes from \(exploreViewModel.cart.activeVendorName ?? "another restaurant"). Discard them and start a new order with \(restaurant.name)?"),
                primaryButton: .destructive(Text("Yes, Discard & Start")) {
                    if let item = conflictItem {
                        itemToCustomize = item
                        withAnimation {
                            showSwitchBanner = true
                        }
                    }
                    conflictItem = nil
                },
                secondaryButton: .cancel(Text("Cancel")) {
                    conflictItem = nil
                }
            )
        }
    }
}

public struct MenuItemRowView: View {
    public let item: FoodMenuItem
    public let quantity: Int
    public let onAdd: () -> Void
    public let onRemove: () -> Void
    
    public var body: some View {
        HStack(alignment: .center, spacing: 12) {
            VStack(alignment: .leading, spacing: 4) {
                // Veg / Non-Veg Indicator & Bestseller Badge
                HStack(spacing: 6) {
                    ZStack {
                        RoundedRectangle(cornerRadius: 3)
                            .stroke(item.veg ? Color(red: 0.09, green: 0.64, blue: 0.29) : Color(red: 0.86, green: 0.15, blue: 0.15), lineWidth: 1)
                            .frame(width: 14, height: 14)
                        Circle()
                            .fill(item.veg ? Color(red: 0.09, green: 0.64, blue: 0.29) : Color(red: 0.86, green: 0.15, blue: 0.15))
                            .frame(width: 6, height: 6)
                    }
                    
                    if item.isBestseller {
                        Text("BESTSELLER")
                            .font(.system(size: 9, weight: .black))
                            .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                            .padding(.horizontal, 5)
                            .padding(.vertical, 2)
                            .background(Color(red: 0.99, green: 0.95, blue: 0.78))
                            .clipShape(RoundedRectangle(cornerRadius: 4))
                    }
                }
                
                Text(item.title)
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(.evrryDarkCharcoal)
                
                Text("Rs \(Int(item.priceNpr))")
                    .font(.system(size: 13, weight: .black))
                    .foregroundColor(.evrryDarkCharcoal)
                
                if let subtitle = item.subtitle {
                    Text(subtitle)
                        .font(.system(size: 11))
                        .foregroundColor(.evrryMutedText)
                        .lineLimit(2)
                }
            }
            
            Spacer()
            
            // DISH PREVIEW & EXPRESS QUANTITY STEPPER
            VStack(spacing: 6) {
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color(red: 1.0, green: 0.97, blue: 0.93))
                        .frame(width: 68, height: 68)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color(red: 1.0, green: 0.93, blue: 0.84), lineWidth: 1)
                        )
                    Text(item.veg ? "🥗" : "🍗")
                        .font(.system(size: 28))
                }
                
                if quantity == 0 {
                    Button(action: onAdd) {
                        Text("ADD")
                            .font(.system(size: 12, weight: .black))
                            .foregroundColor(Color(red: 0.98, green: 0.45, blue: 0.09))
                            .frame(width: 68, height: 30)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 8))
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color(red: 0.98, green: 0.45, blue: 0.09), lineWidth: 1)
                            )
                            .shadow(color: Color.black.opacity(0.05), radius: 2, x: 0, y: 1)
                    }
                    .buttonStyle(PlainButtonStyle())
                } else {
                    HStack {
                        Button(action: onRemove) {
                            Text("−")
                                .font(.system(size: 14, weight: .black))
                                .foregroundColor(.white)
                                .frame(width: 22, height: 30)
                        }
                        
                        Text("\(quantity)")
                            .font(.system(size: 12, weight: .black))
                            .foregroundColor(.white)
                            .frame(minWidth: 16)
                        
                        Button(action: onAdd) {
                            Text("+")
                                .font(.system(size: 14, weight: .black))
                                .foregroundColor(.white)
                                .frame(width: 22, height: 30)
                        }
                    }
                    .frame(width: 68, height: 30)
                    .background(Color(red: 0.98, green: 0.45, blue: 0.09))
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                    .shadow(color: Color.black.opacity(0.05), radius: 2, x: 0, y: 1)
                }
            }
        }
        .padding(14)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.evrrySubtleBorder, lineWidth: 1)
        )
        .padding(.horizontal, 16)
    }
}
