//
//  ActivityView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Orders, Bookings, Deliveries & Rides Activity View
//

import SwiftUI

public struct ActivityView: View {
    @ObservedObject var viewModel: ActivityViewModel
    public var onBack: (() -> Void)?
    public var onSelectOrder: ((String) -> Void)?
    public var onReorder: ((OrderActivityItem) -> Void)?
    
    @State private var receiptOrder: OrderActivityItem? = nil
    @State private var ratingOrder: OrderActivityItem? = nil
    
    public init(
        viewModel: ActivityViewModel,
        onBack: (() -> Void)? = nil,
        onSelectOrder: ((String) -> Void)? = nil,
        onReorder: ((OrderActivityItem) -> Void)? = nil
    ) {
        self.viewModel = viewModel
        self.onBack = onBack
        self.onSelectOrder = onSelectOrder
        self.onReorder = onReorder
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            // Top Bar with In-App Back
            EvrryTopBar(
                title: "Activity",
                canGoBack: onBack != nil,
                onBackClick: { onBack?() }
            )
            
            // Filter Pills
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(ActivityFilter.allCases) { filter in
                        Button(action: {
                            viewModel.selectedFilter = filter
                        }) {
                            Text(filter.rawValue)
                                .font(.system(size: 13, weight: .semibold))
                                .padding(.horizontal, 14)
                                .padding(.vertical, 8)
                                .background(viewModel.selectedFilter == filter ? EvrryColors.primaryBlue : Color.white)
                                .foregroundColor(viewModel.selectedFilter == filter ? .white : EvrryColors.inkMuted)
                                .cornerRadius(20)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 20)
                                        .stroke(viewModel.selectedFilter == filter ? Color.clear : Color.black.opacity(0.1), lineWidth: 1)
                                )
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
            }
            
            // Order & Ride List
            if viewModel.filteredItems.isEmpty {
                VStack(spacing: 12) {
                    Spacer()
                    Image(systemName: "list.clipboard")
                        .font(.system(size: 48))
                        .foregroundColor(EvrryColors.inkMuted)
                    Text("No activity found")
                        .font(.headline)
                        .foregroundColor(EvrryColors.inkPrimary)
                    Text("Your active and past orders will appear here.")
                        .font(.subheadline)
                        .foregroundColor(EvrryColors.inkMuted)
                    Spacer()
                }
            } else {
                ScrollView {
                    LazyVStack(spacing: 14) {
                        ForEach(viewModel.filteredItems) { item in
                            OrderActivityCardView(
                                item: item,
                                onClick: { onSelectOrder?(item.id) },
                                onReorder: { onReorder?(item) },
                                onReceipt: { receiptOrder = item },
                                onRate: { ratingOrder = item }
                            )
                        }
                    }
                    .padding(16)
                }
            }
        }
        .background(EvrryColors.surfacePaper)
        .sheet(item: $receiptOrder) { order in
            ItemizedReceiptSheet(order: order, onDismiss: { receiptOrder = nil })
        }
        .sheet(item: $ratingOrder) { order in
            OrderRatingSheet(order: order, onDismiss: { ratingOrder = nil }, onSubmit: { _, _, _, _ in })
        }
    }
}

struct OrderActivityCardView: View {
    let item: OrderActivityItem
    var onClick: () -> Void = {}
    var onReorder: () -> Void = {}
    var onReceipt: () -> Void = {}
    var onRate: () -> Void = {}
    
    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            // Header
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(item.vendorName)
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(EvrryColors.primaryBlue)
                    Text(item.title)
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(EvrryColors.inkPrimary)
                }
                Spacer()
                statusBadge(item.status)
            }
            .contentShape(Rectangle())
            .onTapGesture { onClick() }
            
            // Amount & Payment
            HStack {
                Text("Rs \(item.amountRs)")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(EvrryColors.inkPrimary)
                Spacer()
                Text("Paid via \(item.paymentMethod)")
                    .font(.system(size: 12))
                    .foregroundColor(EvrryColors.inkMuted)
            }
            .contentShape(Rectangle())
            .onTapGesture { onClick() }
            
            // Live Rider HUD
            if let riderName = item.riderName {
                HStack {
                    Image(systemName: item.category == "Ride" ? "bicycle" : "figure.walk")
                        .foregroundColor(EvrryColors.primaryBlue)
                    VStack(alignment: .leading, spacing: 1) {
                        Text(riderName)
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(EvrryColors.inkPrimary)
                        if let plate = item.vehicleNumber {
                            Text(plate)
                                .font(.system(size: 10))
                                .foregroundColor(EvrryColors.inkMuted)
                        }
                    }
                    Spacer()
                    if let otp = item.rideOtp {
                        Text("OTP: \(otp)")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(EvrryColors.brandNavy)
                            .cornerRadius(6)
                    } else if let eta = item.etaMinutes {
                        Text("~\(eta) mins")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(EvrryColors.actionEmerald)
                    }
                }
                .padding(8)
                .background(EvrryColors.surfacePaper)
                .cornerRadius(10)
            }
            
            // Specialized Completed Order Action Buttons (Reorder, Receipt, Rate)
            if item.status.lowercased() == "delivered" || item.status.lowercased() == "completed" {
                HStack(spacing: 8) {
                    Button(action: onReorder) {
                        HStack(spacing: 4) {
                            Text("🔁")
                            Text("Reorder")
                                .font(.system(size: 12, weight: .bold))
                        }
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(Color.evrryPrimaryBlue)
                        .cornerRadius(10)
                    }
                    
                    Button(action: onReceipt) {
                        HStack(spacing: 4) {
                            Text("🧾")
                            Text("Receipt")
                                .font(.system(size: 12, weight: .semibold))
                        }
                        .foregroundColor(EvrryColors.inkPrimary)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 8)
                        .background(Color.white)
                        .cornerRadius(10)
                        .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    }
                    
                    Button(action: onRate) {
                        HStack(spacing: 4) {
                            Text("⭐")
                            Text("Rate")
                                .font(.system(size: 12, weight: .semibold))
                        }
                        .foregroundColor(Color(red: 0.85, green: 0.45, blue: 0.0))
                        .padding(.horizontal, 10)
                        .padding(.vertical, 8)
                        .background(Color.white)
                        .cornerRadius(10)
                        .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    }
                }
                .padding(.top, 4)
            }
            
            // Date
            Text(item.dateFormatted)
                .font(.system(size: 11))
                .foregroundColor(EvrryColors.inkMuted)
        }
        .padding(14)
        .background(Color.white)
        .cornerRadius(14)
        .shadow(color: Color.black.opacity(0.04), radius: 4, x: 0, y: 2)
    }
    
    @ViewBuilder
    func statusBadge(_ status: String) -> some View {
        let (label, bg, fg) = badgeStyle(status)
        Text(label)
            .font(.system(size: 11, weight: .bold))
            .foregroundColor(fg)
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(bg)
            .cornerRadius(6)
    }
    
    func badgeStyle(_ status: String) -> (String, Color, Color) {
        switch status.lowercased() {
        case "kitchen": return ("In Kitchen", Color(red: 1.0, green: 0.95, blue: 0.8), Color(red: 0.85, green: 0.45, blue: 0.0))
        case "delivering": return ("On The Way", Color(red: 0.85, green: 0.92, blue: 1.0), EvrryColors.primaryBlue)
        case "delivered": return ("Delivered", Color(red: 0.82, green: 0.98, blue: 0.9), EvrryColors.actionEmerald)
        case "confirmed": return ("Confirmed", Color(red: 0.9, green: 0.9, blue: 1.0), Color.indigo)
        default: return (status.capitalized, Color.gray.opacity(0.15), EvrryColors.inkMuted)
        }
    }
}
