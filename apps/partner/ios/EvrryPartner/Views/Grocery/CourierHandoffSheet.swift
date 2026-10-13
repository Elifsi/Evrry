//
//  CourierHandoffSheet.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Courier Pickup Coordination & Bay Messaging for Supermarket Mode
//

import SwiftUI

public struct CourierHandoffSheet: View {
    @ObservedObject var viewModel: GrocerySupermarketViewModel
    let order: GroceryPartnerOrder
    @Binding var isPresented: Bool
    
    @State private var messageText: String = ""
    
    public init(
        viewModel: GrocerySupermarketViewModel,
        order: GroceryPartnerOrder,
        isPresented: Binding<Bool>
    ) {
        self.viewModel = viewModel
        self.order = order
        self._isPresented = isPresented
    }
    
    public var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Courier Profile Header Card
                VStack(spacing: 12) {
                    HStack(spacing: 14) {
                        ZStack {
                            Circle()
                                .fill(Color(hex: "F97316").opacity(0.2))
                                .frame(width: 52, height: 52)
                            Text("🛵")
                                .font(.title2)
                        }
                        
                        VStack(alignment: .leading, spacing: 3) {
                            Text(order.riderName ?? "Ramesh Karki")
                                .font(.headline)
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                            Text(order.riderVehicle ?? "Bajaj Pulsar 150 • BA 88 PA 4321")
                                .font(.caption)
                                .foregroundColor(.gray)
                            HStack(spacing: 4) {
                                Image(systemName: "star.fill")
                                    .foregroundColor(.yellow)
                                    .font(.caption2)
                                Text("4.9 Rating • 1,420 Deliveries")
                                    .font(.caption2)
                                    .foregroundColor(.gray)
                            }
                        }
                        
                        Spacer()
                        
                        // Direct SIM / Phone Call Button
                        Button(action: {
                            if let phone = order.riderPhone, let url = URL(string: "tel://\(phone)") {
                                #if os(iOS)
                                UIApplication.shared.open(url)
                                #endif
                            }
                        }) {
                            Image(systemName: "phone.fill")
                                .font(.headline)
                                .foregroundColor(.white)
                                .padding(12)
                                .background(Color(hex: "22C55E"))
                                .clipShape(Circle())
                        }
                    }
                    
                    // Pickup Bay & Arrival ETA
                    HStack {
                        HStack(spacing: 6) {
                            Image(systemName: "mappin.and.ellipse")
                                .foregroundColor(Color(hex: "F97316"))
                            Text("Bay #3 Front Express Counter")
                                .font(.caption)
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                        }
                        
                        Spacer()
                        
                        Text("ETA: \(order.riderEtaMinutes ?? 3) mins away")
                            .font(.caption)
                            .fontWeight(.bold)
                            .foregroundColor(Color(hex: "22C55E"))
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 8)
                    .background(Color(hex: "0F172A"))
                    .cornerRadius(8)
                }
                .padding(16)
                .background(Color(hex: "1E293B"))
                
                Divider().background(Color(hex: "334155"))
                
                // Chat Message History
                ScrollView {
                    VStack(spacing: 12) {
                        ForEach(viewModel.courierChatMessages) { msg in
                            let isStore = msg.sender.contains("You") || msg.sender.contains("Supermarket")
                            HStack {
                                if isStore { Spacer() }
                                
                                VStack(alignment: isStore ? .trailing : .leading, spacing: 3) {
                                    Text(msg.sender)
                                        .font(.caption2)
                                        .foregroundColor(.gray)
                                    Text(msg.text)
                                        .font(.subheadline)
                                        .padding(.horizontal, 14)
                                        .padding(.vertical, 10)
                                        .background(isStore ? Color(hex: "F97316") : Color(hex: "1E293B"))
                                        .foregroundColor(.white)
                                        .cornerRadius(14)
                                    Text(msg.time)
                                        .font(.caption2)
                                        .foregroundColor(.gray)
                                }
                                
                                if !isStore { Spacer() }
                            }
                        }
                    }
                    .padding(16)
                }
                
                // Quick Direction Suggestions
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        QuickChip(text: "Ready at Bay #3") {
                            viewModel.sendCourierMessage("Bag packed and waiting at Bay #3!")
                        }
                        QuickChip(text: "Bring insulated bag") {
                            viewModel.sendCourierMessage("Order contains refrigerated dairy, please bring thermal bag.")
                        }
                        QuickChip(text: "Use basement ramp") {
                            viewModel.sendCourierMessage("Please park at the two-wheeler bay near basement ramp.")
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                }
                .background(Color(hex: "0F172A"))
                
                // Message Input Row
                HStack(spacing: 10) {
                    TextField("Message courier...", text: $messageText)
                        .padding(12)
                        .background(Color(hex: "1E293B"))
                        .cornerRadius(20)
                        .foregroundColor(.white)
                    
                    Button(action: {
                        guard !messageText.trimmingCharacters(in: .whitespaces).isEmpty else { return }
                        viewModel.sendCourierMessage(messageText)
                        messageText = ""
                    }) {
                        Image(systemName: "paperplane.fill")
                            .font(.headline)
                            .foregroundColor(.white)
                            .padding(12)
                            .background(messageText.isEmpty ? Color.gray : Color(hex: "F97316"))
                            .clipShape(Circle())
                    }
                    .disabled(messageText.isEmpty)
                }
                .padding(12)
                .background(Color(hex: "0F172A"))
            }
            .background(Color(hex: "0B132B").edgesIgnoringSafeArea(.all))
            .navigationBarTitle("Courier Handoff Coordination", displayMode: .inline)
            .navigationBarItems(trailing: Button("Done") {
                isPresented = false
            }.foregroundColor(.orange))
        }
    }
}

struct QuickChip: View {
    let text: String
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(text)
                .font(.caption2)
                .foregroundColor(.white)
                .padding(.horizontal, 10)
                .padding(.vertical, 6)
                .background(Color(hex: "1E293B"))
                .cornerRadius(12)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(Color(hex: "334155"), lineWidth: 1)
                )
        }
    }
}
