//
//  FoodHomeView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Food Delivery Vertical Home Screen (iOS SwiftUI)
//

import SwiftUI

public struct FoodHomeView: View {
    @ObservedObject public var foodViewModel: FoodViewModel
    @ObservedObject public var exploreViewModel: ExploreViewModel
    public let onBack: () -> Void
    public let onRestaurantClick: (String) -> Void
    public let onNavigateToCart: () -> Void
    
    public init(
        foodViewModel: FoodViewModel,
        exploreViewModel: ExploreViewModel,
        onBack: @escaping () -> Void,
        onRestaurantClick: @escaping (String) -> Void,
        onNavigateToCart: @escaping () -> Void
    ) {
        self.foodViewModel = foodViewModel
        self.exploreViewModel = exploreViewModel
        self.onBack = onBack
        self.onRestaurantClick = onRestaurantClick
        self.onNavigateToCart = onNavigateToCart
    }
    
    public var body: some View {
        ZStack(alignment: .bottom) {
            Color(red: 0.98, green: 0.98, blue: 1.0)
                .ignoresSafeArea()
            
            ScrollView {
                VStack(alignment: .leading, spacing: 14) {
                    // TOP BAR WITH IN-APP BACK NAVIGATION (iOS Parity - 44x44 pt min target)
                    HStack {
                        Button(action: onBack) {
                            HStack(spacing: 4) {
                                Image(systemName: "chevron.left")
                                    .font(.system(size: 15, weight: .semibold))
                                Text("Back")
                                    .font(.system(size: 13, weight: .semibold))
                            }
                            .foregroundColor(.evrryDarkCharcoal)
                            .frame(minHeight: 44)
                        }
                        
                        Spacer()
                        
                        HStack(spacing: 8) {
                            HStack(spacing: 4) {
                                Text("⚡").font(.system(size: 11))
                                Text("25-35 min delivery")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(Color(red: 0.76, green: 0.25, blue: 0.05))
                            }
                            .padding(.horizontal, 10)
                            .padding(.vertical, 5)
                            .background(Color(red: 1.0, green: 0.97, blue: 0.93))
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(Color(red: 1.0, green: 0.93, blue: 0.84), lineWidth: 1)
                            )
                            
                            // Top-Right Cart Icon Button with badge
                            Button(action: onNavigateToCart) {
                                ZStack(alignment: .topTrailing) {
                                    Circle()
                                        .fill(Color.white)
                                        .frame(width: 40, height: 40)
                                        .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                        .shadow(color: Color.black.opacity(0.04), radius: 2, y: 1)
                                    
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
                    }
                    .padding(.horizontal, 16)
                    .padding(.top, 8)
                    
                    // HEADER & LOCATION
                    VStack(alignment: .leading, spacing: 3) {
                        HStack(spacing: 6) {
                            Text("📍").font(.system(size: 12))
                            Text("Delivering to: Jhamsikhel, Lalitpur")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                        }
                        
                        Text("Food Delivery")
                            .font(.system(size: 26, weight: .black))
                            .foregroundColor(.evrryDarkCharcoal)
                            .padding(.top, 2)
                        
                        Text("Order from Kathmandu & Lalitpur's top kitchens • Authentic local taste")
                            .font(.system(size: 12))
                            .foregroundColor(.evrryMutedText)
                        
                        // Delivery vs Takeaway Switcher
                        FulfillmentModeSwitcher(selectedType: $exploreViewModel.cart.fulfillmentType)
                            .padding(.top, 4)
                    }
                    .padding(.horizontal, 16)
                    
                    // PROMOTIONAL PERKS BANNER
                    ZStack {
                        LinearGradient(
                            colors: [Color(red: 0.98, green: 0.45, blue: 0.09), Color(red: 0.85, green: 0.47, blue: 0.02)],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                        
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("🏷️ EVRRY Food Fest")
                                    .font(.system(size: 10, weight: .black))
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 3)
                                    .background(Color.white.opacity(0.25))
                                    .clipShape(RoundedRectangle(cornerRadius: 8))
                                
                                Text("20% OFF on Roadhouse & Jimbu")
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.white)
                                
                                Text("Use code: NEPAL20 • Free delivery on Rs 1,000+")
                                    .font(.system(size: 11))
                                    .foregroundColor(.white.opacity(0.85))
                            }
                            Spacer()
                            Text("🍕")
                                .font(.system(size: 38))
                        }
                        .padding(16)
                    }
                    .clipShape(RoundedRectangle(cornerRadius: 20))
                    .padding(.horizontal, 16)
                    
                    // SEARCH BAR
                    HStack(spacing: 10) {
                        Image(systemName: "magnifyingglass")
                            .font(.system(size: 15))
                            .foregroundColor(.evrryMutedText)
                        
                        TextField("Search restaurants, cuisines, momo, pizza...", text: $foodViewModel.searchQuery)
                            .font(.system(size: 12))
                        
                        if !foodViewModel.searchQuery.isEmpty {
                            Button(action: { foodViewModel.searchQuery = "" }) {
                                Image(systemName: "xmark.circle.fill")
                                    .foregroundColor(.evrryMutedText)
                            }
                        }
                    }
                    .padding(.horizontal, 14)
                    .padding(.vertical, 10)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 16))
                    .overlay(
                        RoundedRectangle(cornerRadius: 16)
                            .stroke(Color.evrrySubtleBorder, lineWidth: 1)
                    )
                    .padding(.horizontal, 16)
                    
                    // WHAT ARE YOU CRAVING? CUISINES SCROLLER
                    VStack(alignment: .leading, spacing: 6) {
                        Text("WHAT ARE YOU CRAVING?")
                            .font(.system(size: 11, weight: .black))
                            .foregroundColor(.evrryMutedText)
                            .padding(.horizontal, 16)
                        
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                ForEach(foodViewModel.cuisines) { c in
                                    let isSelected = foodViewModel.selectedCuisine == c.id
                                    Button(action: { foodViewModel.selectCuisine(c.id) }) {
                                        VStack(spacing: 4) {
                                            Text(c.emoji)
                                                .font(.system(size: 22))
                                            Text(c.label)
                                                .font(.system(size: 11, weight: .semibold))
                                                .foregroundColor(isSelected ? .white : .evrryDarkCharcoal)
                                        }
                                        .padding(.horizontal, 14)
                                        .padding(.vertical, 10)
                                        .background(isSelected ? Color(red: 0.98, green: 0.45, blue: 0.09) : Color.white)
                                        .clipShape(RoundedRectangle(cornerRadius: 16))
                                        .overlay(
                                            RoundedRectangle(cornerRadius: 16)
                                                .stroke(isSelected ? Color(red: 0.98, green: 0.45, blue: 0.09) : Color.evrrySubtleBorder, lineWidth: 1)
                                        )
                                    }
                                }
                            }
                            .padding(.horizontal, 16)
                        }
                    }
                    
                    // QUICK FILTER CHIPS
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(foodViewModel.quickFilters) { f in
                                let isSelected = foodViewModel.activeFilters.contains(f.id)
                                Button(action: { foodViewModel.toggleFilter(f.id) }) {
                                    Text(f.label)
                                        .font(.system(size: 12, weight: .semibold))
                                        .foregroundColor(isSelected ? Color(red: 0.92, green: 0.35, blue: 0.05) : .evrryDarkCharcoal)
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 6)
                                        .background(isSelected ? Color(red: 1.0, green: 0.97, blue: 0.93) : Color.white)
                                        .clipShape(Capsule())
                                        .overlay(
                                            Capsule()
                                                .stroke(isSelected ? Color(red: 0.98, green: 0.45, blue: 0.09) : Color.evrrySubtleBorder, lineWidth: 1)
                                        )
                                }
                            }
                        }
                        .padding(.horizontal, 16)
                    }
                    
                    // OPEN RESTAURANTS HEADER
                    let restaurants = foodViewModel.filteredRestaurants()
                    HStack {
                        Text("OPEN RESTAURANTS (\(restaurants.count))")
                            .font(.system(size: 11, weight: .black))
                            .foregroundColor(.evrryMutedText)
                        Spacer()
                        Text("Sorted by Rating & Distance")
                            .font(.system(size: 11))
                            .foregroundColor(.evrryMutedText)
                    }
                    .padding(.horizontal, 16)
                    .padding(.top, 4)
                    
                    // RESTAURANT CARDS
                    if restaurants.isEmpty {
                        VStack(spacing: 10) {
                            Text("🍽️").font(.system(size: 42))
                            Text("No kitchens match your filter")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Text("Try searching for Momo, Thakali, or reset filters.")
                                .font(.system(size: 12))
                                .foregroundColor(.evrryMutedText)
                            Button(action: { foodViewModel.resetFilters() }) {
                                Text("Reset Filters")
                                    .font(.system(size: 13, weight: .bold))
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 16)
                                    .padding(.vertical, 8)
                                    .background(Color(red: 0.98, green: 0.45, blue: 0.09))
                                    .clipShape(RoundedRectangle(cornerRadius: 12))
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 32)
                    } else {
                        LazyVStack(spacing: 12) {
                            ForEach(restaurants) { r in
                                RestaurantCardView(restaurant: r) {
                                    onRestaurantClick(r.id)
                                }
                            }
                        }
                        .padding(.horizontal, 16)
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
    }
}

public struct RestaurantCardView: View {
    public let restaurant: Restaurant
    public let onClick: () -> Void
    
    public var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 0) {
                // Header Gradient
                ZStack(alignment: .topLeading) {
                    LinearGradient(
                        colors: [restaurant.gradientFrom, restaurant.gradientTo],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                    .frame(height: 90)
                    
                    if let img = restaurant.imageUrl, !img.isEmpty {
                        let fullUrl = img.hasPrefix("http") ? img : "https://vsdkaijsiuzommgqibse.supabase.co/storage/v1/object/public/catalog/\(img)"
                        if let u = URL(string: fullUrl) {
                            AsyncImage(url: u) { phase in
                                if let image = phase.image {
                                    image.resizable().aspectRatio(contentMode: .fill)
                                }
                            }
                            .frame(height: 90)
                            .clipped()
                            
                            LinearGradient(
                                colors: [Color.black.opacity(0.45), Color.black.opacity(0.2)],
                                startPoint: .top,
                                endPoint: .bottom
                            )
                            .frame(height: 90)
                        }
                    }
                    
                    HStack {
                        if restaurant.isPureVeg {
                            Text("PURE VEG")
                                .font(.system(size: 9, weight: .black))
                                .foregroundColor(.white)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(Color(red: 0.09, green: 0.64, blue: 0.29))
                                .clipShape(RoundedRectangle(cornerRadius: 6))
                        }
                        
                        Spacer()
                        
                        HStack(spacing: 3) {
                            Text("★").font(.system(size: 10)).foregroundColor(.white)
                            Text(String(format: "%.1f", restaurant.rating))
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.white)
                        }
                        .padding(.horizontal, 6)
                        .padding(.vertical, 3)
                        .background(Color(red: 0.09, green: 0.64, blue: 0.29))
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                    }
                    .padding(12)
                }
                
                // Content
                VStack(alignment: .leading, spacing: 6) {
                    Text(restaurant.name)
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    
                    Text(restaurant.cuisines.joined(separator: " • "))
                        .font(.system(size: 12))
                        .foregroundColor(.evrryMutedText)
                    
                    HStack {
                        HStack(spacing: 8) {
                            Text("🕒 \(restaurant.deliveryEtaMinutes) min")
                                .font(.system(size: 11, weight: .medium))
                                .foregroundColor(.evrryDarkCharcoal)
                            Text("📍 \(restaurant.area)")
                                .font(.system(size: 11))
                                .foregroundColor(.evrryMutedText)
                        }
                        Spacer()
                        Text("Rs \(restaurant.priceForTwo) for two")
                            .font(.system(size: 11, weight: .semibold))
                            .foregroundColor(.evrryDarkCharcoal)
                    }
                    .padding(.top, 4)
                    
                    if let offer = restaurant.offer {
                        HStack(spacing: 4) {
                            Text("🏷️").font(.system(size: 10))
                            Text(offer)
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(Color(red: 0.76, green: 0.25, blue: 0.05))
                        }
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(Color(red: 1.0, green: 0.97, blue: 0.93))
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                        .padding(.top, 2)
                    }
                }
                .padding(14)
            }
            .background(Color.white)
            .clipShape(RoundedRectangle(cornerRadius: 20))
            .overlay(
                RoundedRectangle(cornerRadius: 20)
                    .stroke(Color.evrrySubtleBorder, lineWidth: 1)
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
}
