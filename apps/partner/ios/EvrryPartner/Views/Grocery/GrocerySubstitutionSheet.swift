//
//  GrocerySubstitutionSheet.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Out-of-Stock Customer Substitution Flow for Supermarket Partner Mode
//

import SwiftUI

public struct GrocerySubstitutionSheet: View {
    @ObservedObject var viewModel: GrocerySupermarketViewModel
    let order: GroceryPartnerOrder
    let item: GroceryOrderItem
    @Binding var isPresented: Bool
    
    @State private var selectedSubstitute: GroceryPartnerItem? = nil
    @State private var proposalDispatched = false
    
    public init(
        viewModel: GrocerySupermarketViewModel,
        order: GroceryPartnerOrder,
        item: GroceryOrderItem,
        isPresented: Binding<Bool>
    ) {
        self.viewModel = viewModel
        self.order = order
        self.item = item
        self._isPresented = isPresented
    }
    
    public var body: some View {
        NavigationView {
            VStack(alignment: .leading, spacing: 16) {
                // Out of stock warning banner
                HStack(spacing: 12) {
                    Image(systemName: "exclamationmark.triangle.fill")
                        .foregroundColor(.orange)
                        .font(.title2)
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Item Out of Stock on Shelf")
                            .font(.headline)
                            .foregroundColor(.white)
                        Text("\(item.name) (\(item.quantity) \(item.unit)) is missing or depleted.")
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                }
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(hex: "1E293B"))
                .cornerRadius(12)
                
                Text("Select Recommended In-Stock Substitute:")
                    .font(.subheadline)
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                    .padding(.top, 4)
                
                let substitutes = viewModel.getRecommendedSubstitutes(for: item)
                
                if substitutes.isEmpty {
                    VStack(spacing: 8) {
                        Image(systemName: "tray")
                            .font(.largeTitle)
                            .foregroundColor(.gray)
                        Text("No alternative items found in '\(item.section)' aisle.")
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    ScrollView {
                        VStack(spacing: 10) {
                            ForEach(substitutes) { sub in
                                let isSelected = (selectedSubstitute?.id == sub.id)
                                Button(action: {
                                    selectedSubstitute = sub
                                }) {
                                    HStack(spacing: 14) {
                                        VStack(alignment: .leading, spacing: 4) {
                                            Text(sub.name)
                                                .font(.subheadline)
                                                .fontWeight(.bold)
                                                .foregroundColor(.white)
                                            HStack(spacing: 8) {
                                                Text(sub.brand)
                                                    .font(.caption2)
                                                    .foregroundColor(.gray)
                                                Text("•")
                                                    .font(.caption2)
                                                    .foregroundColor(.gray)
                                                Text("Stock: \(sub.stockQuantity) \(sub.unit)")
                                                    .font(.caption2)
                                                    .foregroundColor(sub.stockQuantity < 5 ? .orange : Color(hex: "22C55E"))
                                            }
                                        }
                                        
                                        Spacer()
                                        
                                        VStack(alignment: .trailing, spacing: 4) {
                                            Text("NPR \(String(format: "%.0f", sub.priceNpr))")
                                                .font(.headline)
                                                .fontWeight(.bold)
                                                .foregroundColor(Color(hex: "F97316"))
                                            
                                            let diff = sub.priceNpr - item.unitPriceRs
                                            if diff != 0 {
                                                Text(diff > 0 ? "+NPR \(String(format: "%.0f", diff))" : "-NPR \(String(format: "%.0f", abs(diff)))")
                                                    .font(.caption2)
                                                    .foregroundColor(diff > 0 ? .orange : Color(hex: "22C55E"))
                                            }
                                        }
                                        
                                        Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                                            .foregroundColor(isSelected ? Color(hex: "F97316") : .gray)
                                            .font(.title3)
                                    }
                                    .padding()
                                    .background(isSelected ? Color(hex: "1E293B") : Color(hex: "0F172A"))
                                    .cornerRadius(12)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(isSelected ? Color(hex: "F97316") : Color(hex: "334155"), lineWidth: isSelected ? 2 : 1)
                                    )
                                }
                                .buttonStyle(PlainButtonStyle())
                            }
                        }
                    }
                }
                
                Spacer()
                
                // Action Dispatch Button
                if !proposalDispatched {
                    Button(action: {
                        guard let sub = selectedSubstitute else { return }
                        viewModel.proposeSubstitution(orderId: order.id, orderItemId: item.id, substituteItem: sub)
                        proposalDispatched = true
                    }) {
                        HStack {
                            Image(systemName: "paperplane.fill")
                            Text("Dispatch Substitution Proposal to Customer")
                                .fontWeight(.bold)
                        }
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(selectedSubstitute != nil ? Color(hex: "F97316") : Color.gray)
                        .cornerRadius(14)
                    }
                    .disabled(selectedSubstitute == nil)
                } else {
                    // Simulation Buttons for Testing Consumer Response Flow
                    VStack(spacing: 8) {
                        Text("Proposal Sent to Customer App. Waiting for real-time response...")
                            .font(.caption)
                            .foregroundColor(.orange)
                        
                        HStack(spacing: 12) {
                            Button(action: {
                                viewModel.simulateConsumerSubstitutionResponse(orderId: order.id, orderItemId: item.id, approved: true)
                                isPresented = false
                            }) {
                                Text("Simulate Approve")
                                    .font(.caption)
                                    .fontWeight(.bold)
                                    .foregroundColor(.white)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 10)
                                    .background(Color(hex: "22C55E"))
                                    .cornerRadius(8)
                            }
                            
                            Button(action: {
                                viewModel.simulateConsumerSubstitutionResponse(orderId: order.id, orderItemId: item.id, approved: false)
                                isPresented = false
                            }) {
                                Text("Simulate Reject")
                                    .font(.caption)
                                    .fontWeight(.bold)
                                    .foregroundColor(.white)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 10)
                                    .background(Color(hex: "EF4444"))
                                    .cornerRadius(8)
                            }
                        }
                    }
                    .padding()
                    .background(Color(hex: "1E293B"))
                    .cornerRadius(12)
                }
            }
            .padding()
            .background(Color(hex: "0B132B").edgesIgnoringSafeArea(.all))
            .navigationBarTitle("Out-of-Stock Substitution", displayMode: .inline)
            .navigationBarItems(trailing: Button("Cancel") {
                isPresented = false
            }.foregroundColor(.orange))
        }
    }
}
