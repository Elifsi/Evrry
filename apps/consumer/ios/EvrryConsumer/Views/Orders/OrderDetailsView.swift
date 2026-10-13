//
//  OrderDetailsView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Live Order Tracking & Details Screen (SwiftUI Parity with Commerce & Commerce)
//

import SwiftUI

public struct OrderDetailsView: View {
    public let orderId: String
    @ObservedObject public var activityViewModel: ActivityViewModel
    public let onBack: () -> Void
    public var onReorder: ((OrderActivityItem) -> Void)?
    public var onChatWithRider: ((String) -> Void)?
    
    @State private var showReceipt: Bool = false
    @State private var showRating: Bool = false
    
    public init(
        orderId: String,
        activityViewModel: ActivityViewModel,
        onBack: @escaping () -> Void,
        onReorder: ((OrderActivityItem) -> Void)? = nil,
        onChatWithRider: ((String) -> Void)? = nil
    ) {
        self.orderId = orderId
        self.activityViewModel = activityViewModel
        self.onBack = onBack
        self.onReorder = onReorder
        self.onChatWithRider = onChatWithRider
    }
    
    private var order: OrderActivityItem {
        activityViewModel.getOrder(id: orderId) ?? activityViewModel.items.first ?? OrderActivityItem(
            id: orderId,
            title: "Food Order",
            vendorName: "Jimbu Thakali Kitchen",
            category: "Food",
            amountRs: 480,
            status: "kitchen",
            paymentMethod: "eSewa Direct Payment",
            dateFormatted: "Just now",
            etaMinutes: 20,
            riderName: "Bikash Thapa",
            riderPhone: "9841000001",
            vehicleNumber: "Ba 88 Pa 4321"
        )
    }
    
    private var isGrocery: Bool {
        order.category.caseInsensitiveCompare("Mart") == .orderedSame ||
        order.category.caseInsensitiveCompare("Grocery") == .orderedSame ||
        order.vendorName.localizedCaseInsensitiveContains("Mart") ||
        order.vendorName.localizedCaseInsensitiveContains("Quick")
    }
    
    private var stages: [(String, String)] {
        if isGrocery {
            return [
                ("placed", "Placed"),
                ("confirmed", "Accepted"),
                ("packing", "Picking"),
                ("delivering", "Dispatched"),
                ("delivered", "Delivered")
            ]
        } else {
            return [
                ("placed", "Placed"),
                ("confirmed", "Confirmed"),
                ("kitchen", "In Kitchen"),
                ("delivering", "On the Way"),
                ("delivered", "Delivered")
            ]
        }
    }
    
    private var currentStageIndex: Int {
        switch order.status.lowercased() {
        case "placed", "acknowledged": return 0
        case "confirmed": return 1
        case "kitchen", "preparing", "packing": return 2
        case "ready_for_pickup", "delivering", "dispatched": return 3
        case "delivered": return 4
        default: return 2
        }
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            // TOP BAR with in-app back navigation
            EvrryTopBar(
                title: "Order #\(order.id)",
                canGoBack: true,
                onBackClick: onBack
            )
            
            ScrollView {
                VStack(spacing: 16) {
                    // LIVE STATUS PIPELINE (Commerce & Commerce 5-Stage Stepper)
                    VStack(alignment: .leading, spacing: 14) {
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("ESTIMATED DELIVERY")
                                    .font(.system(size: 10, weight: .bold))
                                    .foregroundColor(.evrryMutedText)
                                Text("~\(order.etaMinutes ?? (isGrocery ? 12 : 25)) Minutes")
                                    .font(.system(size: 18, weight: .black))
                                    .foregroundColor(isGrocery ? Color(red: 0.02, green: 0.47, blue: 0.34) : .evrryPrimaryBlue)
                            }
                            Spacer()
                            Text(isGrocery && order.status == "kitchen" ? "PACKING" : order.status.uppercased())
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(order.status == "delivered" || isGrocery ? Color(red: 0.02, green: 0.47, blue: 0.34) : .evrryPrimaryBlue)
                                .padding(.horizontal, 8)
                                .padding(.vertical, 4)
                                .background(order.status == "delivered" || isGrocery ? Color(red: 0.86, green: 0.99, blue: 0.91) : Color(red: 0.94, green: 0.96, blue: 1.0))
                                .clipShape(RoundedRectangle(cornerRadius: 8))
                        }
                        
                        // 5-STAGE PROGRESS BAR
                        HStack(spacing: 4) {
                            ForEach(0..<stages.count, id: \.self) { idx in
                                RoundedRectangle(cornerRadius: 3)
                                    .fill(idx <= currentStageIndex ? (isGrocery ? Color(red: 0.02, green: 0.47, blue: 0.34) : Color.evrryPrimaryBlue) : Color(red: 0.89, green: 0.91, blue: 0.94))
                                    .frame(height: 6)
                            }
                        }
                        
                        HStack {
                            ForEach(0..<stages.count, id: \.self) { idx in
                                Text(stages[idx].1)
                                    .font(.system(size: 9, weight: idx == currentStageIndex ? .bold : .regular))
                                    .foregroundColor(idx <= currentStageIndex ? .evrryDarkCharcoal : .evrryMutedText)
                                if idx < stages.count - 1 {
                                    Spacer()
                                }
                            }
                        }
                        
                        if isGrocery {
                            HStack(spacing: 10) {
                                Text("🛒").font(.system(size: 20))
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Dark-Store Micro Fulfillment")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(Color(red: 0.09, green: 0.40, blue: 0.20))
                                    Text("Jhamsikhel Hub • Picker packing in sealed bag #JH-8821")
                                        .font(.system(size: 11))
                                        .foregroundColor(Color(red: 0.08, green: 0.50, blue: 0.24))
                                }
                            }
                            .padding(12)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(Color(red: 0.94, green: 0.99, blue: 0.96))
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color(red: 0.73, green: 0.97, blue: 0.82), lineWidth: 1))
                        }
                    }
                    .padding(18)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 20))
                    .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    
                    // ROUTE TRACKING CARD
                    let isTakeaway = order.riderName == nil
                    VStack(alignment: .leading, spacing: 10) {
                        Text(isTakeaway ? "PICKUP LOCATION & INSTRUCTIONS" : "LIVE ROUTE & DISPATCH")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                        
                        if isTakeaway {
                            ZStack {
                                RoundedRectangle(cornerRadius: 14)
                                    .fill(Color(red: 0.99, green: 0.95, blue: 0.78))
                                    .frame(height: 120)
                                
                                VStack(spacing: 4) {
                                    Text("🛍️")
                                        .font(.system(size: 32))
                                    Text("Ready for Self-Pickup at \(order.vendorName)")
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(Color(red: 0.57, green: 0.25, blue: 0.05))
                                    Text("Collect directly at the counter with your Order ID #\(order.id)")
                                        .font(.system(size: 10))
                                        .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                                }
                            }
                        } else {
                            EvrryLiveTrackingMapView(
                                vendorName: order.vendorName,
                                destinationAddress: "Sanepa Heights (House 42)",
                                vehicleType: .scooter,
                                riderName: order.riderName,
                                etaMinutes: order.etaMinutes ?? (isGrocery ? 12 : 20),
                                speedKmh: 26
                            )
                        }
                    }
                    .padding(16)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 20))
                    .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    
                    // RIDER HUD CARD
                    if let riderName = order.riderName {
                        HStack {
                            HStack(spacing: 12) {
                                ZStack {
                                    Circle()
                                        .fill(Color.evrryPrimaryBlue.opacity(0.1))
                                        .frame(width: 46, height: 46)
                                    Text("👨‍✈️")
                                        .font(.system(size: 22))
                                }
                                
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(riderName)
                                        .font(.system(size: 15, weight: .bold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                    Text(order.vehicleNumber ?? "Motorbike")
                                        .font(.system(size: 12))
                                        .foregroundColor(.evrryMutedText)
                                }
                            }
                            
                            Spacer()
                            
                            HStack(spacing: 8) {
                                if let onChatWithRider = onChatWithRider {
                                    Button(action: {
                                        onChatWithRider(order.id)
                                    }) {
                                        HStack(spacing: 4) {
                                            Image(systemName: "bubble.left.and.bubble.right.fill")
                                                .font(.system(size: 11))
                                            Text("Chat")
                                                .font(.system(size: 13, weight: .bold))
                                        }
                                        .foregroundColor(.evrryPrimaryBlue)
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 8)
                                        .background(Color.white)
                                        .clipShape(RoundedRectangle(cornerRadius: 12))
                                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrryPrimaryBlue.opacity(0.3), lineWidth: 1))
                                    }
                                }

                                Button(action: {
                                    // Call rider action
                                }) {
                                    HStack(spacing: 4) {
                                        Image(systemName: "phone.fill")
                                            .font(.system(size: 12))
                                        Text("Call")
                                            .font(.system(size: 13, weight: .bold))
                                    }
                                    .foregroundColor(.evrryPrimaryBlue)
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 8)
                                    .background(Color(red: 0.94, green: 0.96, blue: 1.0))
                                    .clipShape(RoundedRectangle(cornerRadius: 12))
                                }
                            }
                        }
                        .padding(16)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 20))
                        .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    }
                    
                    // DELIVERY HANDSHAKE OTP CARD (Nepal FinTech / COD Anti-Fraud Handshake)
                    let otpCode = order.rideOtp ?? (order.paymentMethod.localizedCaseInsensitiveContains("Cash") || order.paymentMethod.localizedCaseInsensitiveContains("COD") ? "4821" : nil)
                    if let otp = otpCode, order.status != "delivered", order.status != "cancelled" {
                        HStack(spacing: 12) {
                            HStack(spacing: 12) {
                                ZStack {
                                    Circle()
                                        .fill(Color(red: 0.96, green: 0.62, blue: 0.04))
                                        .frame(width: 40, height: 40)
                                    Text("🔐")
                                        .font(.system(size: 18))
                                }
                                
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("DELIVERY HANDSHAKE OTP")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(Color(red: 0.57, green: 0.25, blue: 0.05))
                                    Text("Share with rider only after receiving sealed package")
                                        .font(.system(size: 11))
                                        .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                                }
                            }
                            
                            Spacer()
                            
                            Text(otp)
                                .font(.system(size: 18, weight: .black, design: .monospaced))
                                .foregroundColor(Color(red: 0.57, green: 0.25, blue: 0.05))
                                .padding(.horizontal, 14)
                                .padding(.vertical, 8)
                                .background(Color.white)
                                .clipShape(RoundedRectangle(cornerRadius: 12))
                                .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color(red: 0.99, green: 0.90, blue: 0.54), lineWidth: 1))
                        }
                        .padding(16)
                        .background(Color(red: 0.99, green: 0.95, blue: 0.78))
                        .clipShape(RoundedRectangle(cornerRadius: 20))
                        .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color(red: 0.96, green: 0.62, blue: 0.04), lineWidth: 1.5))
                    }
                    
                    // DELIVERY ADDRESS
                    if let address = order.deliveryAddress {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("DELIVERY DESTINATION")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                            
                            HStack(spacing: 10) {
                                Image(systemName: "mappin.circle.fill")
                                    .font(.system(size: 20))
                                    .foregroundColor(.evrryPrimaryBlue)
                                Text(address)
                                    .font(.system(size: 13, weight: .medium))
                                    .foregroundColor(.evrryDarkCharcoal)
                            }
                        }
                        .padding(16)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 20))
                        .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    }
                    
                    // SPECIAL INSTRUCTIONS
                    if let instructions = order.specialInstructions, !instructions.isEmpty {
                        VStack(alignment: .leading, spacing: 6) {
                            Text("COOKING INSTRUCTIONS")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                            Text("\"\(instructions)\"")
                                .font(.system(size: 13, weight: .medium))
                                .italic()
                                .foregroundColor(.evrryDarkCharcoal)
                        }
                        .padding(16)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 20))
                        .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    }
                    
                    // ITEMIZED RECEIPT
                    VStack(alignment: .leading, spacing: 10) {
                        Text("ORDER RECEIPT")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                        
                        if !order.lineItems.isEmpty {
                            VStack(spacing: 6) {
                                ForEach(order.lineItems, id: \.self) { line in
                                    HStack {
                                        Text(line)
                                            .font(.system(size: 13))
                                            .foregroundColor(.evrryDarkCharcoal)
                                        Spacer()
                                    }
                                }
                            }
                            Divider().padding(.vertical, 4)
                        }
                        
                        VStack(spacing: 6) {
                            if let subtotal = order.subtotalRs {
                                HStack {
                                    Text("Items Subtotal").foregroundColor(.evrryMutedText)
                                    Spacer()
                                    Text("Rs \(subtotal)").foregroundColor(.evrryDarkCharcoal)
                                }
                            }
                            if let fee = order.deliveryFeeRs {
                                HStack {
                                    Text("Delivery Partner Fee").foregroundColor(.evrryMutedText)
                                    Spacer()
                                    Text("Rs \(fee)").foregroundColor(.evrryDarkCharcoal)
                                }
                            }
                            if let platform = order.platformFeeRs {
                                HStack {
                                    Text("Platform Service Fee").foregroundColor(.evrryMutedText)
                                    Spacer()
                                    Text("Rs \(platform)").foregroundColor(.evrryDarkCharcoal)
                                }
                            }
                            
                            Divider().padding(.vertical, 4)
                            
                            HStack {
                                Text("Total Paid").font(.system(size: 14, weight: .bold)).foregroundColor(.evrryDarkCharcoal)
                                Spacer()
                                Text("Rs \(order.amountRs)")
                                    .font(.system(size: 16, weight: .black))
                                    .foregroundColor(.evrryPrimaryBlue)
                            }
                            
                            HStack {
                                Text("Paid Via").font(.system(size: 12)).foregroundColor(.evrryMutedText)
                                Spacer()
                                Text(order.paymentMethod)
                                    .font(.system(size: 12, weight: .semibold))
                                    .foregroundColor(.evrryDarkCharcoal)
                            }
                        }
                        .font(.system(size: 13))
                    }
                    .padding(16)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 20))
                    .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    
                    // CANCEL ORDER BUTTON
                    if ["placed", "kitchen", "confirmed"].contains(order.status.lowercased()) {
                        Button(action: {
                            activityViewModel.cancelOrder(id: order.id)
                        }) {
                            Text("Cancel Order")
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.evrryRedDestructive)
                                .frame(maxWidth: .infinity)
                                .frame(height: 48)
                                .background(Color(red: 0.99, green: 0.95, blue: 0.95))
                                .clipShape(RoundedRectangle(cornerRadius: 14))
                        }
                    }
                    
                    // COMPLETED ORDER ACTIONS (Specialized Reorder, Receipt, Rate)
                    if ["delivered", "completed"].contains(order.status.lowercased()) {
                        VStack(spacing: 10) {
                            Button(action: {
                                onReorder?(order)
                            }) {
                                Text(isGrocery ? "🔁 Reorder These Items" : "🔁 Reorder This Meal")
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.white)
                                    .frame(maxWidth: .infinity)
                                    .frame(height: 48)
                                    .background(Color.evrryPrimaryBlue)
                                    .clipShape(RoundedRectangle(cornerRadius: 14))
                            }
                            
                            HStack(spacing: 10) {
                                Button(action: {
                                    showReceipt = true
                                }) {
                                    Text("🧾 Tax Invoice")
                                        .font(.system(size: 13, weight: .semibold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                        .frame(maxWidth: .infinity)
                                        .frame(height: 46)
                                        .background(Color.white)
                                        .clipShape(RoundedRectangle(cornerRadius: 14))
                                        .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                }
                                
                                Button(action: {
                                    showRating = true
                                }) {
                                    Text("⭐ Rate Order")
                                        .font(.system(size: 13, weight: .semibold))
                                        .foregroundColor(Color(red: 0.85, green: 0.47, blue: 0.02))
                                        .frame(maxWidth: .infinity)
                                        .frame(height: 46)
                                        .background(Color.white)
                                        .clipShape(RoundedRectangle(cornerRadius: 14))
                                        .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                }
                            }
                        }
                    }
                }
                .padding(16)
            }
        }
        .background(Color(red: 0.98, green: 0.98, blue: 1.0).ignoresSafeArea())
        .sheet(isPresented: $showReceipt) {
            ItemizedReceiptSheet(order: order, onDismiss: { showReceipt = false })
        }
        .sheet(isPresented: $showRating) {
            OrderRatingSheet(order: order, onDismiss: { showRating = false }, onSubmit: { _, _, _, _ in })
        }
    }
}
