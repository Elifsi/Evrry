//
//  PrepTimeModalView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Preparation Time Stepper & Auto-Print KOT Modal
//

import SwiftUI

public struct PrepTimeModalView: View {
    public let order: RestaurantOrder
    public let onConfirm: (Int, Bool) -> Void
    public let onCancel: () -> Void
    
    @State private var selectedMinutes: Int = 20
    @State private var autoPrintKot: Bool = true
    
    private let presetOptions: [Int] = [15, 20, 25, 30, 40]
    
    public init(
        order: RestaurantOrder,
        onConfirm: @escaping (Int, Bool) -> Void,
        onCancel: @escaping () -> Void
    ) {
        self.order = order
        self.onConfirm = onConfirm
        self.onCancel = onCancel
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            // Drag handle
            Capsule()
                .fill(PartnerKitchenTheme.cardBorder)
                .frame(width: 44, height: 5)
                .padding(.top, 10)
                .padding(.bottom, 16)
            
            VStack(alignment: .leading, spacing: 18) {
                // Header
                HStack {
                    VStack(alignment: .leading, spacing: 3) {
                        Text("Accept Order #\(order.orderNo)")
                            .font(.system(size: 20, weight: .black))
                            .foregroundColor(PartnerKitchenTheme.textPrimary)
                        Text("Select kitchen cooking time estimate")
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
                
                // Item preview summary
                HStack(spacing: 8) {
                    Image(systemName: "fork.knife")
                        .foregroundColor(PartnerKitchenTheme.primaryOrange)
                    Text("\(order.items.count) item(s) • Total NPR \(String(format: "%.0f", order.totalNpr))")
                        .font(.system(size: 13, weight: .bold))
                        .foregroundColor(PartnerKitchenTheme.textPrimary)
                    Spacer()
                    Text(order.paymentMethod.uppercased())
                        .font(.system(size: 11, weight: .heavy))
                        .padding(.horizontal, 8)
                        .padding(.vertical, 3)
                        .background(order.paymentMethod == "prepaid" ? PartnerKitchenTheme.accentGreen.opacity(0.2) : PartnerKitchenTheme.timerAmber.opacity(0.2))
                        .foregroundColor(order.paymentMethod == "prepaid" ? PartnerKitchenTheme.accentGreen : PartnerKitchenTheme.timerAmber)
                        .cornerRadius(6)
                }
                .padding(12)
                .background(PartnerKitchenTheme.cardBorder.opacity(0.4))
                .cornerRadius(10)
                
                // Preset Chips
                VStack(alignment: .leading, spacing: 8) {
                    Text("QUICK ESTIMATE")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                    
                    HStack(spacing: 8) {
                        ForEach(presetOptions, id: \.self) { mins in
                            let isSelected = (selectedMinutes == mins)
                            Button {
                                selectedMinutes = mins
                            } label: {
                                Text("\(mins)m")
                                    .font(.system(size: 14, weight: .bold))
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 12)
                                    .background(isSelected ? PartnerKitchenTheme.primaryOrange : PartnerKitchenTheme.cardDark)
                                    .foregroundColor(isSelected ? .white : PartnerKitchenTheme.textPrimary)
                                    .cornerRadius(10)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 10)
                                            .stroke(isSelected ? PartnerKitchenTheme.primaryOrange : PartnerKitchenTheme.cardBorder, lineWidth: 1)
                                    )
                            }
                        }
                    }
                }
                
                // Fine-tune Stepper
                HStack {
                    Text("Fine-tune Time:")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(PartnerKitchenTheme.textPrimary)
                    Spacer()
                    
                    HStack(spacing: 14) {
                        Button {
                            if selectedMinutes > 5 { selectedMinutes -= 5 }
                        } label: {
                            Image(systemName: "minus.circle.fill")
                                .font(.system(size: 28))
                                .foregroundColor(selectedMinutes > 5 ? PartnerKitchenTheme.primaryOrange : PartnerKitchenTheme.textMuted)
                        }
                        .disabled(selectedMinutes <= 5)
                        
                        Text("\(selectedMinutes) min")
                            .font(.system(size: 18, weight: .black))
                            .foregroundColor(PartnerKitchenTheme.textPrimary)
                            .frame(minWidth: 70)
                        
                        Button {
                            if selectedMinutes < 90 { selectedMinutes += 5 }
                        } label: {
                            Image(systemName: "plus.circle.fill")
                                .font(.system(size: 28))
                                .foregroundColor(selectedMinutes < 90 ? PartnerKitchenTheme.primaryOrange : PartnerKitchenTheme.textMuted)
                        }
                        .disabled(selectedMinutes >= 90)
                    }
                }
                .padding(14)
                .background(PartnerKitchenTheme.cardDark)
                .cornerRadius(12)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
                )
                
                // Auto Print KOT Toggle
                Toggle(isOn: $autoPrintKot) {
                    HStack(spacing: 8) {
                        Image(systemName: "printer.fill")
                            .foregroundColor(PartnerKitchenTheme.courierBlue)
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Auto-Print Kitchen Ticket (KOT)")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundColor(PartnerKitchenTheme.textPrimary)
                            Text("Prints immediately to ESC/POS thermal printer")
                                .font(.system(size: 11))
                                .foregroundColor(PartnerKitchenTheme.textSecondary)
                        }
                    }
                }
                .toggleStyle(SwitchToggleStyle(tint: PartnerKitchenTheme.primaryOrange))
                .padding(.horizontal, 4)
                
                // Confirm Button
                Button {
                    onConfirm(selectedMinutes, autoPrintKot)
                } label: {
                    HStack(spacing: 8) {
                        Image(systemName: "flame.fill")
                            .font(.system(size: 16))
                        Text("Start Cooking (\(selectedMinutes) mins) 🔥")
                            .font(.system(size: 16, weight: .black))
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 16)
                    .background(PartnerKitchenTheme.primaryOrange)
                    .foregroundColor(.white)
                    .cornerRadius(14)
                    .shadow(color: PartnerKitchenTheme.primaryOrange.opacity(0.4), radius: 8, y: 4)
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
