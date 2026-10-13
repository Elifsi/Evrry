//
//  KitchenChatSheetView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Contextual Kitchen Chat with Consumer & Courier
//

import SwiftUI

public struct KitchenChatSheetView: View {
    @ObservedObject public var viewModel: RestaurantKitchenViewModel
    public let order: RestaurantOrder
    public let onDismiss: () -> Void
    
    @State private var inputText: String = ""
    
    public init(viewModel: RestaurantKitchenViewModel, order: RestaurantOrder, onDismiss: @escaping () -> Void) {
        self.viewModel = viewModel
        self.order = order
        self.onDismiss = onDismiss
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            // Drag handle
            Capsule()
                .fill(PartnerKitchenTheme.cardBorder)
                .frame(width: 44, height: 5)
                .padding(.top, 10)
                .padding(.bottom, 12)
            
            // Header
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    HStack(spacing: 6) {
                        Text("Order #\(order.orderNo) Chat")
                            .font(.system(size: 18, weight: .black))
                            .foregroundColor(PartnerKitchenTheme.textPrimary)
                        Text(viewModel.chatRecipient == "consumer" ? "CUSTOMER" : "COURIER")
                            .font(.system(size: 9, weight: .heavy))
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(viewModel.chatRecipient == "consumer" ? PartnerKitchenTheme.primaryOrange.opacity(0.2) : PartnerKitchenTheme.courierBlue.opacity(0.2))
                            .foregroundColor(viewModel.chatRecipient == "consumer" ? PartnerKitchenTheme.primaryOrange : PartnerKitchenTheme.courierBlue)
                            .cornerRadius(4)
                    }
                    Text(viewModel.chatRecipient == "consumer" ? "\(order.consumerName) (\(order.consumerPhone))" : "\(order.riderName ?? "Assigned Courier") (\(order.riderPhone ?? "N/A"))")
                        .font(.system(size: 12))
                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                }
                Spacer()
                Button(action: onDismiss) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.system(size: 22))
                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                }
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 10)
            
            // Recipient Switcher (if courier assigned)
            if order.riderName != nil {
                Picker("Recipient", selection: $viewModel.chatRecipient) {
                    Text("Customer 👤").tag("consumer")
                    Text("Courier 🛵").tag("courier")
                }
                .pickerStyle(SegmentedPickerStyle())
                .padding(.horizontal, 16)
                .padding(.bottom, 10)
            }
            
            Divider()
                .background(PartnerKitchenTheme.cardBorder)
            
            // Message Thread
            ScrollViewReader { proxy in
                ScrollView {
                    LazyVStack(spacing: 12) {
                        ForEach(viewModel.chatMessages) { msg in
                            let isMe = (msg.senderRole == "restaurant")
                            HStack {
                                if isMe { Spacer() }
                                
                                VStack(alignment: isMe ? .trailing : .leading, spacing: 3) {
                                    Text(msg.senderName)
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(PartnerKitchenTheme.textMuted)
                                    
                                    Text(msg.message)
                                        .font(.system(size: 13))
                                        .padding(.horizontal, 14)
                                        .padding(.vertical, 10)
                                        .background(isMe ? PartnerKitchenTheme.primaryOrange : PartnerKitchenTheme.cardDark)
                                        .foregroundColor(PartnerKitchenTheme.textPrimary)
                                        .cornerRadius(14)
                                        .overlay(
                                            RoundedRectangle(cornerRadius: 14)
                                                .stroke(isMe ? Color.clear : PartnerKitchenTheme.cardBorder, lineWidth: 1)
                                        )
                                }
                                
                                if !isMe { Spacer() }
                            }
                            .id(msg.id)
                        }
                    }
                    .padding(16)
                }
                .onChange(of: viewModel.chatMessages.count) { _ in
                    if let lastId = viewModel.chatMessages.last?.id {
                        withAnimation {
                            proxy.scrollTo(lastId, anchor: .bottom)
                        }
                    }
                }
            }
            
            // Input Bar
            HStack(spacing: 8) {
                TextField("Type kitchen reply...", text: $inputText)
                    .padding(12)
                    .background(PartnerKitchenTheme.cardDark)
                    .foregroundColor(PartnerKitchenTheme.textPrimary)
                    .cornerRadius(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
                    )
                
                Button {
                    let trimmed = inputText.trimmingCharacters(in: .whitespacesAndNewlines)
                    guard !trimmed.isEmpty else { return }
                    viewModel.sendChatMessage(text: trimmed)
                    inputText = ""
                } label: {
                    Image(systemName: "paperplane.fill")
                        .font(.system(size: 16))
                        .foregroundColor(.white)
                        .padding(12)
                        .background(PartnerKitchenTheme.primaryOrange)
                        .cornerRadius(12)
                }
            }
            .padding(12)
            .background(PartnerKitchenTheme.cardDark.opacity(0.8))
        }
        .background(PartnerKitchenTheme.bgDark)
    }
}
