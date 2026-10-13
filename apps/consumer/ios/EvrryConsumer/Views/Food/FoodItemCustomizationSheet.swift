//
//  FoodItemCustomizationSheet.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Food Item Customization & Add-to-Cart Sheet (SwiftUI Parity with Commerce & Commerce)
//

import SwiftUI

public struct FoodItemCustomizationSheet: View {
    public let item: FoodMenuItem
    public let restaurant: Restaurant
    public let onDismiss: () -> Void
    public let onAddToCart: (CatalogItem, [String], String, Int) -> Void
    
    @State private var quantity: Int = 1
    @State private var selectedOptionId: String?
    @State private var selectedAddonIds: Set<String> = []
    @State private var specialInstructions: String = ""
    
    public init(
        item: FoodMenuItem,
        restaurant: Restaurant,
        onDismiss: @escaping () -> Void,
        onAddToCart: @escaping (CatalogItem, [String], String, Int) -> Void
    ) {
        self.item = item
        self.restaurant = restaurant
        self.onDismiss = onDismiss
        self.onAddToCart = onAddToCart
        _selectedOptionId = State(initialValue: item.options.first?.id)
    }
    
    private var basePrice: Double {
        item.priceNpr
    }
    
    private var optionExtra: Double {
        item.options.first(where: { $0.id == selectedOptionId })?.priceNpr ?? 0.0
    }
    
    private var addonExtra: Double {
        item.addons.filter { selectedAddonIds.contains($0.id) }.reduce(0) { $0 + $1.priceNpr }
    }
    
    private var unitPrice: Double {
        basePrice + optionExtra + addonExtra
    }
    
    private var totalPrice: Double {
        unitPrice * Double(quantity)
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 18) {
            // Drag Indicator
            HStack {
                Spacer()
                RoundedRectangle(cornerRadius: 3)
                    .fill(Color.evrrySubtleBorder)
                    .frame(width: 40, height: 5)
                Spacer()
            }
            .padding(.top, 8)
            
            // Header: Title, Veg/NonVeg, Close
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    HStack(spacing: 8) {
                        ZStack {
                            RoundedRectangle(cornerRadius: 4)
                                .stroke(item.veg ? Color(red: 0.09, green: 0.64, blue: 0.29) : Color(red: 0.86, green: 0.15, blue: 0.15), lineWidth: 1.5)
                                .frame(width: 16, height: 16)
                            Circle()
                                .fill(item.veg ? Color(red: 0.09, green: 0.64, blue: 0.29) : Color(red: 0.86, green: 0.15, blue: 0.15))
                                .frame(width: 7, height: 7)
                        }
                        
                        Text(item.title)
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                    }
                    
                    if let subtitle = item.subtitle {
                        Text(subtitle)
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)
                    }
                    
                    Text("Rs \(Int(item.priceNpr))")
                        .font(.system(size: 16, weight: .black))
                        .foregroundColor(.evrryPrimaryBlue)
                }
                
                Spacer()
                
                Button(action: onDismiss) {
                    Image(systemName: "xmark")
                        .font(.system(size: 13, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                        .frame(width: 32, height: 32)
                        .background(Color(red: 0.94, green: 0.96, blue: 0.98))
                        .clipShape(Circle())
                }
            }
            
            Divider()
            
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // OPTIONS
                    if !item.options.isEmpty {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("CHOOSE PREPARATION")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                            
                            ForEach(item.options) { opt in
                                let isSelected = opt.id == selectedOptionId
                                Button(action: { selectedOptionId = opt.id }) {
                                    HStack {
                                        Text(opt.name)
                                            .font(.system(size: 14, weight: .medium))
                                            .foregroundColor(.evrryDarkCharcoal)
                                        Spacer()
                                        Text(opt.priceNpr > 0 ? "+Rs \(Int(opt.priceNpr))" : "Free")
                                            .font(.system(size: 12, weight: .semibold))
                                            .foregroundColor(isSelected ? .evrryPrimaryBlue : .evrryMutedText)
                                    }
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 12)
                                    .background(isSelected ? Color(red: 0.94, green: 0.96, blue: 1.0) : Color.white)
                                    .clipShape(RoundedRectangle(cornerRadius: 12))
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(isSelected ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: 1)
                                    )
                                }
                            }
                        }
                    }
                    
                    // ADDONS
                    if !item.addons.isEmpty {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("ADD-ONS & EXTRAS")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                            
                            ForEach(item.addons) { addon in
                                let isChecked = selectedAddonIds.contains(addon.id)
                                Button(action: {
                                    if isChecked {
                                        selectedAddonIds.remove(addon.id)
                                    } else {
                                        selectedAddonIds.insert(addon.id)
                                    }
                                }) {
                                    HStack {
                                        Text(addon.name)
                                            .font(.system(size: 14, weight: .medium))
                                            .foregroundColor(.evrryDarkCharcoal)
                                        Spacer()
                                        Text("+Rs \(Int(addon.priceNpr))")
                                            .font(.system(size: 12, weight: .semibold))
                                            .foregroundColor(.evrryPrimaryBlue)
                                    }
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 12)
                                    .background(isChecked ? Color(red: 0.94, green: 0.96, blue: 1.0) : Color.white)
                                    .clipShape(RoundedRectangle(cornerRadius: 12))
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(isChecked ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: 1)
                                    )
                                }
                            }
                        }
                    }
                    
                    // SPECIAL INSTRUCTIONS
                    VStack(alignment: .leading, spacing: 6) {
                        Text("SPECIAL INSTRUCTIONS")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                        
                        TextField("E.g. Less spicy, extra momo achar...", text: $specialInstructions)
                            .font(.system(size: 13))
                            .padding(12)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    }
                }
            }
            
            // ACTION BAR: Stepper + Add Button
            HStack(spacing: 14) {
                // Stepper
                HStack(spacing: 12) {
                    Button(action: { if quantity > 1 { quantity -= 1 } }) {
                        Text("-")
                            .font(.system(size: 20, weight: .bold))
                            .foregroundColor(quantity > 1 ? .evrryDarkCharcoal : .evrryMutedText)
                            .frame(width: 24, height: 24)
                    }
                    .disabled(quantity <= 1)
                    
                    Text("\(quantity)")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    
                    Button(action: { quantity += 1 }) {
                        Text("+")
                            .font(.system(size: 20, weight: .bold))
                            .foregroundColor(.evrryPrimaryBlue)
                            .frame(width: 24, height: 24)
                    }
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 10)
                .background(Color.white)
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                
                // Add CTA
                Button(action: {
                    var chosenOptions: [String] = []
                    if let opt = item.options.first(where: { $0.id == selectedOptionId }) {
                        chosenOptions.append(opt.name)
                    }
                    item.addons.filter { selectedAddonIds.contains($0.id) }.forEach {
                        chosenOptions.append($0.name)
                    }
                    
                    let catalogItem = CatalogItem(
                        id: item.id,
                        vertical: "food",
                        title: item.title,
                        subtitle: item.subtitle ?? restaurant.name,
                        priceNpr: unitPrice,
                        originalPriceNpr: nil,
                        vendorName: restaurant.name,
                        vendorId: restaurant.id,
                        rating: restaurant.rating,
                        etaMinutes: restaurant.deliveryEtaMinutes,
                        imageUrl: item.imageUrl
                    )
                    
                    onAddToCart(catalogItem, chosenOptions, specialInstructions, quantity)
                    onDismiss()
                }) {
                    Text("Add to Cart · Rs \(Int(totalPrice))")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(Color.evrryPrimaryBlue)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                }
            }
            .padding(.top, 4)
            .padding(.bottom, 16)
        }
        .padding(.horizontal, 20)
        .background(Color.white)
    }
}
