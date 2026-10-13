//
//  CartCheckoutView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Multi-Vertical Common Cart & Checkout View (Food & Grocery Coexistence)
//

import SwiftUI

public struct CartCheckoutView: View {
    @ObservedObject public var exploreViewModel: ExploreViewModel
    @ObservedObject public var activityViewModel: ActivityViewModel
    public let onBack: () -> Void
    public let onOrderSuccess: (String) -> Void
    
    @State private var selectedPayment: PaymentMethod = .esewa
    @State private var showAddressSheet: Bool = false
    @State private var selectedSubstitution: GrocerySubstitutionPreference = .allowStoreSubstitution
    
    private var hasFood: Bool {
        !exploreViewModel.cart.foodItems.isEmpty
    }
    
    private var hasGrocery: Bool {
        !exploreViewModel.cart.groceryItems.isEmpty
    }
    
    private var isMultiStore: Bool {
        hasFood && hasGrocery
    }
    
    private var minOrderNpr: Double {
        if let activeId = exploreViewModel.cart.activeGroceryVendorId,
           let store = GroceryCatalogData.partnerStores.first(where: { $0.id == activeId }) {
            return store.minOrderNpr
        }
        return GroceryCatalogData.defaultHub.minOrderNpr
    }
    
    private var grocerySubtotal: Double {
        exploreViewModel.cart.groceryItems.reduce(0) { $0 + $1.totalNpr }
    }
    
    private var isBelowMinOrder: Bool {
        hasGrocery && grocerySubtotal < minOrderNpr
    }
    
    public init(
        exploreViewModel: ExploreViewModel,
        activityViewModel: ActivityViewModel,
        onBack: @escaping () -> Void,
        onOrderSuccess: @escaping (String) -> Void
    ) {
        self.exploreViewModel = exploreViewModel
        self.activityViewModel = activityViewModel
        self.onBack = onBack
        self.onOrderSuccess = onOrderSuccess
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            // TOP BAR with In-App Back Navigation
            EvrryTopBar(
                title: "My Cart & Checkout",
                canGoBack: true,
                onBackClick: onBack
            )
            
            if exploreViewModel.cart.items.isEmpty {
                // EMPTY STATE
                VStack(spacing: 16) {
                    Spacer()
                    Text("🛒").font(.system(size: 48))
                    Text("Your Cart is Empty")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text("Browse food, groceries, and services in Explore.")
                        .font(.system(size: 13))
                        .foregroundColor(.evrryMutedText)
                    Button(action: onBack) {
                        Text("Explore Catalog")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 20)
                            .padding(.vertical, 10)
                            .background(Color.evrryPrimaryBlue)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                    Spacer()
                }
            } else {
                // CHECKOUT CONTENT
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        // FULFILLMENT MODE SWITCHER (Delivery vs Takeaway for Food)
                        if hasFood {
                            FulfillmentModeSwitcher(selectedType: $exploreViewModel.cart.fulfillmentType)
                        }
                        
                        // DELIVERY / PICKUP ADDRESS CARD
                        VStack(alignment: .leading, spacing: 6) {
                            Text(hasFood && exploreViewModel.cart.fulfillmentType == .pickup ? "SELF-PICKUP LOCATION" : "DELIVER TO")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                            
                            HStack(alignment: .center, spacing: 12) {
                                Image(systemName: "mappin.circle.fill")
                                    .font(.system(size: 24))
                                    .foregroundColor(.evrryPrimaryBlue)
                                
                                VStack(alignment: .leading, spacing: 2) {
                                    if hasFood && exploreViewModel.cart.fulfillmentType == .pickup {
                                        Text("Self-Pickup from \(exploreViewModel.cart.activeFoodVendorName ?? "Kitchen")")
                                            .font(.system(size: 14, weight: .bold))
                                            .foregroundColor(.evrryDarkCharcoal)
                                        Text("Ready in ~15 mins · No delivery fee")
                                            .font(.system(size: 12))
                                            .foregroundColor(Color(red: 0.09, green: 0.64, blue: 0.29))
                                    } else {
                                        HStack(spacing: 6) {
                                            Text(exploreViewModel.cart.selectedAddress.title)
                                                .font(.system(size: 14, weight: .bold))
                                                .foregroundColor(.evrryDarkCharcoal)
                                            if exploreViewModel.cart.selectedAddress.isDefault {
                                                Text("Default")
                                                    .font(.system(size: 10, weight: .bold))
                                                    .foregroundColor(.evrryPrimaryBlue)
                                                    .padding(.horizontal, 6)
                                                    .padding(.vertical, 2)
                                                    .background(Color(red: 0.94, green: 0.96, blue: 1.0))
                                                    .clipShape(Capsule())
                                            }
                                        }
                                        Text(exploreViewModel.cart.selectedAddress.detail)
                                            .font(.system(size: 12))
                                            .foregroundColor(.evrryMutedText)
                                        if isMultiStore {
                                            Text("Coordinated delivery for food + grocery")
                                                .font(.system(size: 11, weight: .medium))
                                                .foregroundColor(.evrryPrimaryBlue)
                                        }
                                    }
                                }
                                
                                Spacer()
                                
                                if !(hasFood && exploreViewModel.cart.fulfillmentType == .pickup) {
                                    Button(action: { showAddressSheet = true }) {
                                        Text("Change")
                                            .font(.system(size: 12, weight: .bold))
                                            .foregroundColor(.evrryPrimaryBlue)
                                            .padding(.horizontal, 10)
                                            .padding(.vertical, 6)
                                            .background(Color(red: 0.94, green: 0.96, blue: 1.0))
                                            .clipShape(RoundedRectangle(cornerRadius: 8))
                                    }
                                }
                            }
                            .padding(14)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                            .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        }
                        
                        // 1. FOOD DELIVERY ITEMS SECTION
                        if hasFood {
                            VStack(alignment: .leading, spacing: 8) {
                                HStack {
                                    HStack(spacing: 6) {
                                        Text("🍽️").font(.system(size: 14))
                                        VStack(alignment: .leading, spacing: 1) {
                                            Text("FOOD DELIVERY")
                                                .font(.system(size: 10, weight: .black))
                                                .foregroundColor(Color(red: 0.98, green: 0.45, blue: 0.09))
                                            Text(exploreViewModel.cart.activeFoodVendorName ?? "Restaurant Kitchen")
                                                .font(.system(size: 13, weight: .bold))
                                                .foregroundColor(.evrryDarkCharcoal)
                                        }
                                    }
                                    Spacer()
                                    Text("\(exploreViewModel.cart.foodItems.reduce(0) { $0 + $1.quantity }) items")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(Color(red: 0.98, green: 0.45, blue: 0.09))
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 3)
                                        .background(Color(red: 1.0, green: 0.95, blue: 0.93))
                                        .clipShape(Capsule())
                                }
                                .padding(.horizontal, 4)
                                
                                ForEach(exploreViewModel.cart.foodItems) { cartItem in
                                    CartItemCardRow(
                                        cartItem: cartItem,
                                        onIncrement: { exploreViewModel.addToCart(cartItem.item) },
                                        onDecrement: { exploreViewModel.removeFromCart(cartItem.item.id) }
                                    )
                                }
                            }
                        }
                        
                        // 2. QUICK MART & GROCERY ITEMS SECTION
                        if hasGrocery {
                            VStack(alignment: .leading, spacing: 8) {
                                HStack {
                                    HStack(spacing: 6) {
                                        Text("⚡").font(.system(size: 14))
                                        VStack(alignment: .leading, spacing: 1) {
                                            Text("QUICK MART & GROCERY")
                                                .font(.system(size: 10, weight: .black))
                                                .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                                            Text(exploreViewModel.cart.activeGroceryVendorName ?? "Evrry Quick Mart Hub")
                                                .font(.system(size: 13, weight: .bold))
                                                .foregroundColor(.evrryDarkCharcoal)
                                        }
                                    }
                                    Spacer()
                                    Text("\(exploreViewModel.cart.groceryItems.reduce(0) { $0 + $1.quantity }) items")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 3)
                                        .background(Color(red: 0.93, green: 0.99, blue: 0.96))
                                        .clipShape(Capsule())
                                }
                                .padding(.horizontal, 4)
                                
                                ForEach(exploreViewModel.cart.groceryItems) { cartItem in
                                    CartItemCardRow(
                                        cartItem: cartItem,
                                        onIncrement: { exploreViewModel.addToCart(cartItem.item) },
                                        onDecrement: { exploreViewModel.removeFromCart(cartItem.item.id) }
                                    )
                                }
                            }
                        }
                        
                        // 3. ANY OTHER CATALOG ITEMS
                        let otherItems = exploreViewModel.cart.items.filter { $0.item.vertical != "food" && $0.item.vertical != "grocery" }
                        if !otherItems.isEmpty {
                            VStack(alignment: .leading, spacing: 8) {
                                Text("OTHER SERVICES")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(.evrryMutedText)
                                    .padding(.horizontal, 4)
                                
                                ForEach(otherItems) { cartItem in
                                    CartItemCardRow(
                                        cartItem: cartItem,
                                        onIncrement: { exploreViewModel.addToCart(cartItem.item) },
                                        onDecrement: { exploreViewModel.removeFromCart(cartItem.item.id) }
                                    )
                                }
                            }
                        }
                        
                        // COOKING / DELIVERY INSTRUCTIONS
                        VStack(alignment: .leading, spacing: 6) {
                            Text("COOKING & DELIVERY INSTRUCTIONS")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                            
                            TextField("E.g. Extra spicy, don't ring the bell, call when arriving...", text: Binding(
                                get: { exploreViewModel.cart.specialInstructions },
                                set: { exploreViewModel.setSpecialInstructions($0) }
                            ))
                            .font(.system(size: 13))
                            .padding(12)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 14))
                            .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        }
                        
                        // OUT-OF-STOCK SUBSTITUTION (Only shown if grocery items present)
                        if hasGrocery {
                            VStack(alignment: .leading, spacing: 6) {
                                Text("OUT OF STOCK ITEM PREFERENCE")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(.evrryMutedText)
                                
                                HStack(spacing: 8) {
                                    ForEach(GrocerySubstitutionPreference.allCases, id: \.self) { pref in
                                        let isSel = selectedSubstitution == pref
                                        Button(action: { selectedSubstitution = pref }) {
                                            Text(pref == .allowStoreSubstitution ? "🔄 Replace closest" : (pref == .callCustomer ? "📞 Call me" : "💵 Refund item"))
                                                .font(.system(size: 11, weight: isSel ? .bold : .medium))
                                                .foregroundColor(isSel ? Color(red: 0.02, green: 0.47, blue: 0.34) : .evrryDarkCharcoal)
                                                .frame(maxWidth: .infinity)
                                                .padding(.vertical, 8)
                                                .background(isSel ? Color(red: 0.94, green: 0.99, blue: 0.96) : Color.white)
                                                .clipShape(RoundedRectangle(cornerRadius: 10))
                                                .overlay(RoundedRectangle(cornerRadius: 10).stroke(isSel ? Color(red: 0.02, green: 0.47, blue: 0.34) : Color.evrrySubtleBorder, lineWidth: 1))
                                        }
                                    }
                                }
                            }
                        }
                        
                        // FREQUENTLY ORDERED UPSELLS
                        PreCheckoutUpsellSection(
                            popularAddons: exploreViewModel.popularAddons,
                            onAddAddon: { exploreViewModel.addAddonToCart($0) }
                        )
                        
                        // BILL DETAILS
                        VStack(alignment: .leading, spacing: 6) {
                            Text("BILL BREAKDOWN")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                            
                            VStack(spacing: 8) {
                                if isMultiStore {
                                    HStack {
                                        Text("Food Delivery Subtotal").foregroundColor(.evrryMutedText)
                                        Spacer()
                                        Text("Rs \(Int(exploreViewModel.cart.foodItems.reduce(0) { $0 + $1.totalNpr }))")
                                            .fontWeight(.semibold)
                                            .foregroundColor(.evrryDarkCharcoal)
                                    }
                                    HStack {
                                        Text("Quick Mart Subtotal").foregroundColor(.evrryMutedText)
                                        Spacer()
                                        Text("Rs \(Int(exploreViewModel.cart.groceryItems.reduce(0) { $0 + $1.totalNpr }))")
                                            .fontWeight(.semibold)
                                            .foregroundColor(.evrryDarkCharcoal)
                                    }
                                } else {
                                    HStack {
                                        Text("Item Subtotal").foregroundColor(.evrryMutedText)
                                        Spacer()
                                        Text("Rs \(Int(exploreViewModel.cart.subtotalNpr))")
                                            .fontWeight(.semibold)
                                            .foregroundColor(.evrryDarkCharcoal)
                                    }
                                }
                                HStack {
                                    Text("Delivery Partner Fee").foregroundColor(.evrryMutedText)
                                    Spacer()
                                    let feeText: String = {
                                        if hasFood && exploreViewModel.cart.fulfillmentType == .pickup && !hasGrocery {
                                            return "FREE (Takeaway)"
                                        } else if exploreViewModel.cart.effectiveDeliveryFeeNpr == 0.0 {
                                            return "FREE (Order over Rs 1,000)"
                                        } else {
                                            return "Rs \(Int(exploreViewModel.cart.deliveryFeeNpr))"
                                        }
                                    }()
                                    let feeColor: Color = exploreViewModel.cart.effectiveDeliveryFeeNpr == 0.0 ? Color(red: 0.09, green: 0.64, blue: 0.29) : .evrryDarkCharcoal
                                    Text(feeText)
                                        .fontWeight(.semibold)
                                        .foregroundColor(feeColor)
                                }
                                HStack {
                                    Text("Platform Service Fee").foregroundColor(.evrryMutedText)
                                    Spacer()
                                    Text("Rs \(Int(exploreViewModel.cart.platformFeeNpr))")
                                        .fontWeight(.semibold)
                                        .foregroundColor(.evrryDarkCharcoal)
                                }
                                Divider().background(Color.evrrySubtleBorder).padding(.vertical, 4)
                                HStack {
                                    Text("Total Amount").fontWeight(.bold).foregroundColor(.evrryDarkCharcoal)
                                    Spacer()
                                    Text("Rs \(Int(exploreViewModel.cart.totalNpr))")
                                        .font(.system(size: 17, weight: .black))
                                        .foregroundColor(hasGrocery && !hasFood ? Color(red: 0.02, green: 0.47, blue: 0.34) : .evrryPrimaryBlue)
                                }
                            }
                            .font(.system(size: 13))
                            .padding(16)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                            .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        }
                        
                        // DOMESTIC PAYMENT METHOD SELECTOR (Strict NRB Direct Payment)
                        VStack(alignment: .leading, spacing: 6) {
                            Text("SELECT PAYMENT METHOD")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                            
                            VStack(spacing: 8) {
                                ForEach(PaymentMethod.allCases) { method in
                                    let isSelected = selectedPayment == method
                                    Button(action: { selectedPayment = method }) {
                                        HStack {
                                            VStack(alignment: .leading, spacing: 2) {
                                                Text(method.label)
                                                    .font(.system(size: 13, weight: .semibold))
                                                    .foregroundColor(.evrryDarkCharcoal)
                                                Text(method.badge)
                                                    .font(.system(size: 11))
                                                    .foregroundColor(isSelected ? .evrryPrimaryBlue : .evrryMutedText)
                                            }
                                            Spacer()
                                            if isSelected {
                                                Image(systemName: "checkmark")
                                                    .foregroundColor(.evrryPrimaryBlue)
                                            }
                                        }
                                        .padding(14)
                                        .background(isSelected ? Color(red: 0.94, green: 0.96, blue: 1.0) : Color.white)
                                        .clipShape(RoundedRectangle(cornerRadius: 14))
                                        .overlay(RoundedRectangle(cornerRadius: 14).stroke(isSelected ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: isSelected ? 1.5 : 1))
                                    }
                                }
                            }
                        }
                    }
                    .padding(16)
                }
                
                // PLACE ORDER CTA
                let buttonText = isBelowMinOrder ? "Min Grocery Rs \(Int(minOrderNpr)) (Add Rs \(Int(minOrderNpr - grocerySubtotal)) more)" : "Pay Rs \(Int(exploreViewModel.cart.totalNpr)) with \(selectedPayment.label)"
                let isButtonEnabled = !isBelowMinOrder && !exploreViewModel.cart.items.isEmpty
                
                Button(action: {
                    let itemsSummary = exploreViewModel.cart.items.map {
                        let optStr = !$0.selectedOptions.isEmpty ? " (\($0.selectedOptions.joined(separator: ", ")))" : ""
                        let prefix = $0.item.vertical == "grocery" ? "[Grocery] " : "[Food] "
                        return "\(prefix)\($0.quantity)x \($0.item.title)\(optStr) (Rs \(Int($0.totalNpr)))"
                    }
                    let firstFood = exploreViewModel.cart.foodItems.first?.item.title
                    let firstGrocery = exploreViewModel.cart.groceryItems.first?.item.title
                    let orderTitle: String = {
                        if isMultiStore {
                            return "Food & Grocery Order (\(exploreViewModel.cart.itemCount) items)"
                        } else if hasGrocery {
                            return exploreViewModel.cart.groceryItems.count > 1 ? "\(firstGrocery ?? "Grocery") + \(exploreViewModel.cart.groceryItems.count - 1) more" : (firstGrocery ?? "Grocery Order")
                        } else {
                            return exploreViewModel.cart.foodItems.count > 1 ? "\(firstFood ?? "Food") + \(exploreViewModel.cart.foodItems.count - 1) more" : (firstFood ?? "Food Order")
                        }
                    }()
                    
                    let isPickup = hasFood && !hasGrocery && exploreViewModel.cart.fulfillmentType == .pickup
                    var instructions = exploreViewModel.cart.specialInstructions
                    if hasGrocery {
                        if !instructions.isEmpty { instructions += " • " }
                        instructions += "Sub: \(selectedSubstitution.label)"
                    }
                    
                    let vendorDisplay: String = {
                        if isMultiStore {
                            return "\(exploreViewModel.cart.activeFoodVendorName ?? "Kitchen") & \(exploreViewModel.cart.activeGroceryVendorName ?? "Quick Mart")"
                        } else if hasGrocery {
                            return exploreViewModel.cart.activeGroceryVendorName ?? "Evrry Quick Mart Hub"
                        } else {
                            return exploreViewModel.cart.activeFoodVendorName ?? "Restaurant Kitchen"
                        }
                    }()
                    
                    let categoryDisplay = isMultiStore ? "Food & Mart" : (hasGrocery ? "Mart" : "Food")
                    
                    let orderId = activityViewModel.addOrder(
                        vendorName: vendorDisplay,
                        category: categoryDisplay,
                        title: orderTitle,
                        amountRs: Int(exploreViewModel.cart.totalNpr),
                        paymentMethod: selectedPayment.label,
                        lineItems: itemsSummary,
                        deliveryAddress: isPickup ? "Self-Pickup from \(exploreViewModel.cart.activeFoodVendorName ?? "Kitchen")" : "\(exploreViewModel.cart.selectedAddress.title) - \(exploreViewModel.cart.selectedAddress.detail)",
                        specialInstructions: instructions,
                        subtotalRs: Int(exploreViewModel.cart.subtotalNpr),
                        deliveryFeeRs: Int(exploreViewModel.cart.effectiveDeliveryFeeNpr),
                        platformFeeRs: Int(exploreViewModel.cart.platformFeeNpr),
                        etaMinutes: hasGrocery && !hasFood ? 12 : (isPickup ? 15 : 25),
                        rideOtp: selectedPayment == .cod ? "4821" : nil
                    )
                    
                    exploreViewModel.clearCart()
                    onOrderSuccess(orderId)
                }) {
                    Text(buttonText)
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(isBelowMinOrder ? Color.gray : (hasGrocery && !hasFood ? Color(red: 0.02, green: 0.47, blue: 0.34) : Color.evrryPrimaryBlue))
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                }
                .disabled(!isButtonEnabled)
                .padding(16)
            }
        }
        .background(Color(red: 0.98, green: 0.98, blue: 1.0).ignoresSafeArea())
        .sheet(isPresented: $showAddressSheet) {
            VStack(alignment: .leading, spacing: 16) {
                HStack {
                    Text("Select Delivery Address")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Spacer()
                    Button(action: { showAddressSheet = false }) {
                        Image(systemName: "xmark")
                            .foregroundColor(.evrryDarkCharcoal)
                            .padding(6)
                            .background(Color(red: 0.94, green: 0.96, blue: 0.98))
                            .clipShape(Circle())
                    }
                }
                .padding(.top, 16)
                
                ForEach(DefaultAddresses.all) { addr in
                    let isSelected = addr.id == exploreViewModel.cart.selectedAddress.id
                    Button(action: {
                        exploreViewModel.selectDeliveryAddress(addr)
                        showAddressSheet = false
                    }) {
                        HStack(alignment: .top, spacing: 12) {
                            Image(systemName: addr.title == "Home" ? "house.fill" : "briefcase.fill")
                                .font(.system(size: 18))
                                .foregroundColor(isSelected ? .evrryPrimaryBlue : .evrryMutedText)
                                .padding(.top, 2)
                            
                            VStack(alignment: .leading, spacing: 2) {
                                Text(addr.title)
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Text(addr.detail)
                                    .font(.system(size: 12))
                                    .foregroundColor(.evrryMutedText)
                                if let landmark = addr.landmark {
                                    Text("Landmark: \(landmark)")
                                        .font(.system(size: 11))
                                        .foregroundColor(.evrryMutedText)
                                }
                            }
                            
                            Spacer()
                            
                            if isSelected {
                                Image(systemName: "checkmark.circle.fill")
                                    .foregroundColor(.evrryPrimaryBlue)
                            }
                        }
                        .padding(14)
                        .background(isSelected ? Color(red: 0.94, green: 0.96, blue: 1.0) : Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                        .overlay(RoundedRectangle(cornerRadius: 14).stroke(isSelected ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: 1))
                    }
                }
                
                Spacer()
            }
            .padding(20)
            .presentationDetents([.medium])
        }
    }
}

public struct CartItemCardRow: View {
    public let cartItem: CartItem
    public let onIncrement: () -> Void
    public let onDecrement: () -> Void
    
    public init(cartItem: CartItem, onIncrement: @escaping () -> Void, onDecrement: @escaping () -> Void) {
        self.cartItem = cartItem
        self.onIncrement = onIncrement
        self.onDecrement = onDecrement
    }
    
    public var body: some View {
        HStack(alignment: .center) {
            VStack(alignment: .leading, spacing: 3) {
                Text(cartItem.item.title)
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(.evrryDarkCharcoal)
                
                if !cartItem.item.subtitle.isEmpty {
                    Text(cartItem.item.subtitle)
                        .font(.system(size: 11))
                        .foregroundColor(.evrryMutedText)
                }
                
                if !cartItem.selectedOptions.isEmpty {
                    Text(cartItem.selectedOptions.joined(separator: ", "))
                        .font(.system(size: 11))
                        .foregroundColor(.evrryPrimaryBlue)
                }
                
                Text("Rs \(Int(cartItem.item.priceNpr)) each")
                    .font(.system(size: 12))
                    .foregroundColor(.evrryMutedText)
            }
            
            Spacer()
            
            HStack(spacing: 8) {
                Button(action: onDecrement) {
                    Text("-")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                        .frame(width: 28, height: 28)
                        .background(Color(red: 0.94, green: 0.96, blue: 0.98))
                        .clipShape(Circle())
                }
                
                Text("\(cartItem.quantity)")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(.evrryDarkCharcoal)
                    .frame(minWidth: 16)
                
                Button(action: onIncrement) {
                    Text("+")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.evrryPrimaryBlue)
                        .frame(width: 28, height: 28)
                        .background(Color(red: 0.94, green: 0.96, blue: 1.0))
                        .clipShape(Circle())
                }
            }
        }
        .padding(14)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
    }
}
