//
//  RejectReasonSheetView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Structured Order Rejection Reason Sheet
//

import SwiftUI

public struct RejectReasonSheetView: View {
    public let order: RestaurantOrder
    public let onConfirm: (String) -> Void
    public let onCancel: () -> Void
    
    @State private var selectedReason: String = "Out of ingredients"
    @State private var customReason: String = ""
    
    private let standardReasons: [String] = [
        "Out of ingredients",
        "Kitchen overloaded / peak rush",
        "Restaurant closing soon",
        "Equipment / electricity breakdown",
        "Item price discrepancy",
        "Custom reason"
    ]
    
    public init(
        order: RestaurantOrder,
        onConfirm: @escaping (String) -> Void,
        onCancel: @escaping () -> Void
    ) {
        self.order = order
        self.onConfirm = onConfirm
        self.onCancel = onCancel
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            Capsule()
                .fill(PartnerKitchenTheme.cardBorder)
                .frame(width: 44, height: 5)
                .padding(.top, 10)
                .padding(.bottom, 16)
            
            VStack(alignment: .leading, spacing: 18) {
                HStack {
                    VStack(alignment: .leading, spacing: 3) {
                        Text("Decline Order #\(order.orderNo)")
                            .font(.system(size: 20, weight: .black))
                            .foregroundColor(PartnerKitchenTheme.alertRed)
                        Text("Select reason to notify customer and update analytics")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(PartnerKitchenTheme.textSecondary)
                    }
                    Spacer()
                    Button(action: onCancel) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.system(size: 24))
                            .foregroundColor(PartnerKitchenTheme.textSecondary)
                    }
                }
                
                VStack(spacing: 8) {
                    ForEach(standardReasons, id: \.self) { reason in
                        let isSelected = (selectedReason == reason)
                        Button {
                            selectedReason = reason
                        } label: {
                            HStack {
                                Image(systemName: isSelected ? "largecircle.fill.circle" : "circle")
                                    .foregroundColor(isSelected ? PartnerKitchenTheme.alertRed : PartnerKitchenTheme.textSecondary)
                                    .font(.system(size: 18))
                                Text(reason)
                                    .font(.system(size: 14, weight: isSelected ? .bold : .medium))
                                    .foregroundColor(PartnerKitchenTheme.textPrimary)
                                Spacer()
                            }
                            .padding(12)
                            .background(isSelected ? PartnerKitchenTheme.alertRed.opacity(0.12) : PartnerKitchenTheme.cardDark)
                            .cornerRadius(10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(isSelected ? PartnerKitchenTheme.alertRed : PartnerKitchenTheme.cardBorder, lineWidth: 1)
                            )
                        }
                    }
                }
                
                if selectedReason == "Custom reason" {
                    TextField("Enter reason...", text: $customReason)
                        .padding(12)
                        .background(PartnerKitchenTheme.cardDark)
                        .foregroundColor(PartnerKitchenTheme.textPrimary)
                        .cornerRadius(10)
                        .overlay(
                            RoundedRectangle(cornerRadius: 10)
                                .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
                        )
                }
                
                Button {
                    let finalReason = (selectedReason == "Custom reason" && !customReason.isEmpty) ? customReason : selectedReason
                    onConfirm(finalReason)
                } label: {
                    Text("Confirm Rejection ❌")
                        .font(.system(size: 16, weight: .black))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(PartnerKitchenTheme.alertRed)
                        .foregroundColor(.white)
                        .cornerRadius(14)
                }
                .padding(.top, 6)
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 24)
        }
        .background(PartnerKitchenTheme.bgDark)
        .cornerRadius(24)
        .overlay(
            RoundedRectangle(cornerRadius: 24)
                .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
        )
    }
}
