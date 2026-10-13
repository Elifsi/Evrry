//
//  FoodSpecializedSheets.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Food Vertical Specialized UI Components & Sheets for iOS
//

import SwiftUI

// MARK: - 1. Delivery vs. Pickup Mode Switcher
public struct FulfillmentModeSwitcher: View {
    @Binding public var selectedType: OrderFulfillmentType
    
    public init(selectedType: Binding<OrderFulfillmentType>) {
        self._selectedType = selectedType
    }
    
    public var body: some View {
        HStack(spacing: 4) {
            // Delivery Pill
            Button(action: {
                withAnimation(.spring(response: 0.3)) {
                    selectedType = .delivery
                }
            }) {
                HStack(spacing: 6) {
                    Text("🛵")
                    Text("Delivery")
                        .font(.system(size: 13, weight: selectedType == .delivery ? .bold : .medium))
                }
                .foregroundColor(selectedType == .delivery ? .white : Color(red: 0.12, green: 0.14, blue: 0.17))
                .frame(maxWidth: .infinity)
                .padding(.vertical, 8)
                .background(selectedType == .delivery ? Color(red: 0.14, green: 0.39, blue: 0.92) : Color.clear)
                .cornerRadius(20)
            }
            
            // Takeaway Pill
            Button(action: {
                withAnimation(.spring(response: 0.3)) {
                    selectedType = .pickup
                }
            }) {
                HStack(spacing: 6) {
                    Text("🛍️")
                    Text("Takeaway")
                        .font(.system(size: 13, weight: selectedType == .pickup ? .bold : .medium))
                }
                .foregroundColor(selectedType == .pickup ? .white : Color(red: 0.12, green: 0.14, blue: 0.17))
                .frame(maxWidth: .infinity)
                .padding(.vertical, 8)
                .background(selectedType == .pickup ? Color(red: 0.14, green: 0.39, blue: 0.92) : Color.clear)
                .cornerRadius(20)
            }
        }
        .padding(4)
        .background(Color(red: 0.95, green: 0.96, blue: 0.98))
        .cornerRadius(24)
    }
}

// MARK: - 2. Store Info & Operating Hours Sheet (Atlas Screen 43)
public struct RestaurantInfoSheet: View {
    public let restaurant: Restaurant
    public let onDismiss: () -> Void
    
    public init(restaurant: Restaurant, onDismiss: @escaping () -> Void) {
        self.restaurant = restaurant
        self.onDismiss = onDismiss
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            // Header
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(restaurant.name)
                        .font(.system(size: 20, weight: .heavy))
                        .foregroundColor(Color(red: 0.12, green: 0.14, blue: 0.17))
                    Text(restaurant.cuisines.joined(separator: ", "))
                        .font(.system(size: 13))
                        .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                }
                Spacer()
                HStack(spacing: 3) {
                    Text("★")
                        .font(.system(size: 11))
                        .foregroundColor(.white)
                    Text(String(format: "%.1f", restaurant.rating))
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(.white)
                }
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(Color(red: 0.09, green: 0.64, blue: 0.29))
                .cornerRadius(8)
            }
            
            Divider()
            
            // Operating Hours
            HStack(alignment: .top, spacing: 12) {
                Image(systemName: "clock.fill")
                    .foregroundColor(Color(red: 0.14, green: 0.39, blue: 0.92))
                    .frame(width: 32, height: 32)
                    .background(Color(red: 0.93, green: 0.96, blue: 1.0))
                    .clipShape(Circle())
                
                VStack(alignment: .leading, spacing: 2) {
                    Text("OPERATING HOURS")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                    Text(restaurant.operatingHours)
                        .font(.system(size: 13, weight: .semibold))
                    Text("Open Today · Fresh kitchen orders accepted")
                        .font(.system(size: 12))
                        .foregroundColor(Color(red: 0.09, green: 0.64, blue: 0.29))
                }
            }
            
            // Kitchen Address
            HStack(alignment: .top, spacing: 12) {
                Image(systemName: "mappin.circle.fill")
                    .foregroundColor(Color(red: 0.14, green: 0.39, blue: 0.92))
                    .frame(width: 32, height: 32)
                    .background(Color(red: 0.93, green: 0.96, blue: 1.0))
                    .clipShape(Circle())
                
                VStack(alignment: .leading, spacing: 2) {
                    Text("KITCHEN ADDRESS")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                    Text(restaurant.fullAddress)
                        .font(.system(size: 13, weight: .semibold))
                    Text("Pickup ready in ~\(restaurant.pickupEtaMinutes) mins")
                        .font(.system(size: 12))
                        .foregroundColor(Color(red: 0.14, green: 0.39, blue: 0.92))
                }
            }
            
            // Phone
            HStack(alignment: .top, spacing: 12) {
                Image(systemName: "phone.fill")
                    .foregroundColor(Color(red: 0.14, green: 0.39, blue: 0.92))
                    .frame(width: 32, height: 32)
                    .background(Color(red: 0.93, green: 0.96, blue: 1.0))
                    .clipShape(Circle())
                
                VStack(alignment: .leading, spacing: 2) {
                    Text("PHONE NUMBER")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                    Text(restaurant.phone)
                        .font(.system(size: 13, weight: .semibold))
                }
            }
            
            // Hygiene Card
            HStack(spacing: 10) {
                Text("🛡️")
                    .font(.system(size: 20))
                VStack(alignment: .leading, spacing: 2) {
                    Text("Hygiene & Food Safety")
                        .font(.system(size: 13, weight: .bold))
                        .foregroundColor(Color(red: 0.09, green: 0.40, blue: 0.20))
                    Text(restaurant.hygieneCert)
                        .font(.system(size: 11))
                        .foregroundColor(Color(red: 0.08, green: 0.50, blue: 0.24))
                }
            }
            .padding(12)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color(red: 0.94, green: 0.99, blue: 0.96))
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color(red: 0.73, green: 0.97, blue: 0.82), lineWidth: 1)
            )
            
            Spacer()
            
            Button(action: onDismiss) {
                Text("Close Details")
                    .font(.system(size: 15, weight: .bold))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Color(red: 0.14, green: 0.39, blue: 0.92))
                    .cornerRadius(14)
            }
        }
        .padding(20)
    }
}

// MARK: - 3. Itemized Digital Receipt Sheet (Atlas Screen 26)
public struct ItemizedReceiptSheet: View {
    public let order: OrderActivityItem
    public let onDismiss: () -> Void
    
    public init(order: OrderActivityItem, onDismiss: @escaping () -> Void) {
        self.order = order
        self.onDismiss = onDismiss
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("DIGITAL TAX INVOICE")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                    Text("Order #\(order.id)")
                        .font(.system(size: 18, weight: .black))
                        .foregroundColor(Color(red: 0.12, green: 0.14, blue: 0.17))
                }
                Spacer()
                Text("PAID")
                    .font(.system(size: 11, weight: .black))
                    .foregroundColor(Color(red: 0.02, green: 0.59, blue: 0.41))
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(Color(red: 0.82, green: 0.98, blue: 0.90))
                    .cornerRadius(6)
            }
            
            VStack(alignment: .leading, spacing: 4) {
                Text(order.vendorName)
                    .font(.system(size: 13, weight: .bold))
                Text("PAN/VAT: 601239841 · Inland Revenue Dept (IRD)")
                    .font(.system(size: 11))
                    .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                Text("Date: \(order.dateFormatted)")
                    .font(.system(size: 11))
                    .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
            }
            .padding(12)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color(red: 0.97, green: 0.98, blue: 0.99))
            .cornerRadius(12)
            
            Divider()
            
            Text("LINE ITEMS")
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
            
            if !order.lineItems.isEmpty {
                ForEach(order.lineItems, id: \.self) { item in
                    Text(item)
                        .font(.system(size: 13))
                }
            } else {
                Text(order.title)
                    .font(.system(size: 13))
            }
            
            Divider()
            
            HStack {
                Text("Delivery Charge")
                    .font(.system(size: 12))
                    .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                Spacer()
                Text((order.deliveryFeeRs ?? 0) == 0 ? "FREE (Takeaway)" : "Rs \(order.deliveryFeeRs ?? 0)")
                    .font(.system(size: 12))
            }
            
            HStack {
                Text("Platform Service Fee")
                    .font(.system(size: 12))
                    .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                Spacer()
                Text("Rs \(order.platformFeeRs ?? 10)")
                    .font(.system(size: 12))
            }
            
            Divider()
            
            HStack {
                Text("Total Paid")
                    .font(.system(size: 16, weight: .bold))
                Spacer()
                Text("Rs \(order.amountRs)")
                    .font(.system(size: 17, weight: .black))
                    .foregroundColor(Color(red: 0.14, green: 0.39, blue: 0.92))
            }
            
            HStack {
                Text("Payment Method")
                    .font(.system(size: 12))
                    .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                Spacer()
                Text(order.paymentMethod)
                    .font(.system(size: 12, weight: .bold))
            }
            
            Spacer()
            
            Button(action: onDismiss) {
                Text("Done")
                    .font(.system(size: 15, weight: .bold))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Color(red: 0.14, green: 0.39, blue: 0.92))
                    .cornerRadius(14)
            }
        }
        .padding(20)
    }
}

// MARK: - 4. Post-Delivery Rating Sheet (Atlas Screens 59, 60, 77, 78)
public struct OrderRatingSheet: View {
    public let order: OrderActivityItem
    public let onDismiss: () -> Void
    public let onSubmit: (Int, Int, [String], String) -> Void
    
    @State private var foodRating: Int = 5
    @State private var riderRating: Int = 5
    @State private var selectedTags: Set<String> = []
    @State private var reviewNote: String = ""
    @State private var isSubmitted: Bool = false
    
    private var tags: [String] {
        let foodTags = ["Hot & Fresh 🔥", "Super Tasty 😋", "Generous Portion 🍲"]
        let riderTags = ["Polite Rider 🤝", "Super Fast ⚡"]
        return order.riderName != nil ? (foodTags + riderTags) : foodTags
    }
    
    public init(
        order: OrderActivityItem,
        onDismiss: @escaping () -> Void,
        onSubmit: @escaping (Int, Int, [String], String) -> Void
    ) {
        self.order = order
        self.onDismiss = onDismiss
        self.onSubmit = onSubmit
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            if isSubmitted {
                VStack(spacing: 12) {
                    Text("🎉")
                        .font(.system(size: 48))
                    Text("Thank You for Your Feedback!")
                        .font(.system(size: 18, weight: .heavy))
                    Text("Your review helps \(order.vendorName) and the delivery team improve.")
                        .font(.system(size: 13))
                        .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                        .multilineTextAlignment(.center)
                    
                    Button(action: onDismiss) {
                        Text("Close")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 24)
                            .padding(.vertical, 10)
                            .background(Color(red: 0.14, green: 0.39, blue: 0.92))
                            .cornerRadius(12)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 40)
            } else {
                Text("RATE YOUR ORDER")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                
                Text("How was your experience with \(order.vendorName)?")
                    .font(.system(size: 16, weight: .bold))
                
                // Food Rating
                VStack(alignment: .leading, spacing: 6) {
                    Text("Food Quality & Taste")
                        .font(.system(size: 13, weight: .semibold))
                    HStack(spacing: 10) {
                        ForEach(1...5, id: \.self) { star in
                            Text(star <= foodRating ? "★" : "☆")
                                .font(.system(size: 30))
                                .foregroundColor(star <= foodRating ? Color(red: 0.96, green: 0.62, blue: 0.04) : Color.gray.opacity(0.4))
                                .onTapGesture { foodRating = star }
                        }
                    }
                }
                
                // Rider Rating
                if order.riderName != nil {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Delivery Partner (\(order.riderName ?? "Rider"))")
                            .font(.system(size: 13, weight: .semibold))
                        HStack(spacing: 10) {
                            ForEach(1...5, id: \.self) { star in
                                Text(star <= riderRating ? "★" : "☆")
                                    .font(.system(size: 30))
                                    .foregroundColor(star <= riderRating ? Color(red: 0.96, green: 0.62, blue: 0.04) : Color.gray.opacity(0.4))
                                    .onTapGesture { riderRating = star }
                            }
                        }
                    }
                }
                
                // Tags
                Text("WHAT WENT WELL?")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(tags, id: \.self) { tag in
                            let isSelected = selectedTags.contains(tag)
                            Text(tag)
                                .font(.system(size: 12, weight: isSelected ? .bold : .medium))
                                .padding(.horizontal, 12)
                                .padding(.vertical, 6)
                                .background(isSelected ? Color(red: 0.14, green: 0.39, blue: 0.92).opacity(0.12) : Color(red: 0.95, green: 0.96, blue: 0.98))
                                .foregroundColor(isSelected ? Color(red: 0.14, green: 0.39, blue: 0.92) : Color(red: 0.12, green: 0.14, blue: 0.17))
                                .cornerRadius(20)
                                .onTapGesture {
                                    if isSelected { selectedTags.remove(tag) } else { selectedTags.insert(tag) }
                                }
                        }
                    }
                }
                
                TextField("Add comments for the kitchen...", text: $reviewNote)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                
                Spacer()
                
                Button(action: {
                    onSubmit(foodRating, riderRating, Array(selectedTags), reviewNote)
                    isSubmitted = true
                }) {
                    Text("Submit Rating")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 14)
                        .background(Color(red: 0.14, green: 0.39, blue: 0.92))
                        .cornerRadius(14)
                }
            }
        }
        .padding(20)
    }
}

// MARK: - 5. Pre-Checkout Upsell Drawer (Atlas Screen 89)
public struct PreCheckoutUpsellSection: View {
    public let popularAddons: [CatalogItem]
    public let onAddAddon: (CatalogItem) -> Void
    
    public init(popularAddons: [CatalogItem], onAddAddon: @escaping (CatalogItem) -> Void) {
        self.popularAddons = popularAddons
        self.onAddAddon = onAddAddon
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text("FREQUENTLY ORDERED TOGETHER")
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
            
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 10) {
                    ForEach(popularAddons) { addon in
                        VStack(alignment: .leading, spacing: 4) {
                            Text(addon.title)
                                .font(.system(size: 13, weight: .bold))
                                .lineLimit(1)
                            Text(addon.subtitle)
                                .font(.system(size: 11))
                                .foregroundColor(Color(red: 0.45, green: 0.50, blue: 0.58))
                                .lineLimit(1)
                            
                            HStack {
                                Text("Rs \(Int(addon.priceNpr))")
                                    .font(.system(size: 13, weight: .bold))
                                    .foregroundColor(Color(red: 0.14, green: 0.39, blue: 0.92))
                                Spacer()
                                Button(action: { onAddAddon(addon) }) {
                                    Image(systemName: "plus")
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(Color(red: 0.14, green: 0.39, blue: 0.92))
                                        .frame(width: 26, height: 26)
                                        .background(Color(red: 0.14, green: 0.39, blue: 0.92).opacity(0.1))
                                        .clipShape(Circle())
                                }
                            }
                        }
                        .padding(12)
                        .frame(width: 190)
                        .background(Color.white)
                        .cornerRadius(14)
                        .overlay(
                            RoundedRectangle(cornerRadius: 14)
                                .stroke(Color(red: 0.90, green: 0.92, blue: 0.95), lineWidth: 1)
                        )
                    }
                }
            }
        }
    }
}
