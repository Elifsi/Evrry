//
//  GroceryPickPackView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Supermarket Pick-and-Pack Order Checklist Screen
//

import SwiftUI

public struct GroceryPickPackView: View {
    @ObservedObject var viewModel: GrocerySupermarketViewModel
    let onOpenScanner: () -> Void
    
    @State private var itemForSubstitution: GroceryOrderItem? = nil
    @State private var itemForWeighing: GroceryOrderItem? = nil
    @State private var weightInputString: String = "1.00"
    @State private var showCourierHandoff = false
    
    public init(
        viewModel: GrocerySupermarketViewModel,
        onOpenScanner: @escaping () -> Void
    ) {
        self.viewModel = viewModel
        self.onOpenScanner = onOpenScanner
    }
    
    public var body: some View {
        let activeOrder = viewModel.getSelectedOrder()
        
        VStack(spacing: 0) {
            // Order Selector Header
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 12) {
                    ForEach(viewModel.orders) { order in
                        let isSelected = (order.id == viewModel.selectedOrderId)
                        Button(action: {
                            viewModel.selectOrder(order.id)
                        }) {
                            VStack(alignment: .leading, spacing: 4) {
                                HStack {
                                    Text("Order #\(order.orderNo)")
                                        .font(.subheadline)
                                        .fontWeight(.bold)
                                        .foregroundColor(isSelected ? .white : .gray)
                                    Spacer()
                                    Circle()
                                        .fill(statusColor(for: order.status))
                                        .frame(width: 8, height: 8)
                                }
                                Text("\(order.consumerName) • \(order.items.count) items")
                                    .font(.caption2)
                                    .foregroundColor(isSelected ? Color(hex: "F97316") : .gray)
                            }
                            .padding(.horizontal, 14)
                            .padding(.vertical, 10)
                            .background(isSelected ? Color(hex: "1E293B") : Color(hex: "0F172A"))
                            .cornerRadius(12)
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(isSelected ? Color(hex: "F97316") : Color(hex: "334155"), lineWidth: 1)
                            )
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
            }
            .background(Color(hex: "0F172A"))
            
            Divider().background(Color(hex: "334155"))
            
            if let order = activeOrder {
                ScrollView {
                    VStack(alignment: .leading, spacing: 18) {
                        // Order Metadata Card
                        HStack(spacing: 12) {
                            VStack(alignment: .leading, spacing: 3) {
                                Text(order.consumerName)
                                    .font(.headline)
                                    .fontWeight(.bold)
                                    .foregroundColor(.white)
                                Text("Delivery: \(order.consumerAddress)")
                                    .font(.caption)
                                    .foregroundColor(.gray)
                                Text("Payment: \(order.paymentMethod) • NPR \(String(format: "%.0f", order.totalNpr))")
                                    .font(.caption2)
                                    .foregroundColor(Color(hex: "22C55E"))
                            }
                            
                            Spacer()
                            
                            VStack(alignment: .trailing, spacing: 4) {
                                Text(statusBadge(for: order.status))
                                    .font(.caption)
                                    .fontWeight(.bold)
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 4)
                                    .background(statusColor(for: order.status).opacity(0.2))
                                    .foregroundColor(statusColor(for: order.status))
                                    .cornerRadius(8)
                                
                                Button(action: onOpenScanner) {
                                    HStack(spacing: 4) {
                                        Image(systemName: "barcode.viewfinder")
                                        Text("Scan SKU")
                                    }
                                    .font(.caption2)
                                    .foregroundColor(Color(hex: "F97316"))
                                }
                            }
                        }
                        .padding()
                        .background(Color(hex: "1E293B"))
                        .cornerRadius(14)
                        
                        // Courier Bay Handoff Notification if Ready
                        if order.status == "ready_for_pickup" {
                            HStack(spacing: 12) {
                                Image(systemName: "box.truck.fill")
                                    .font(.title2)
                                    .foregroundColor(Color(hex: "22C55E"))
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Order Packed • Ready at Bay #3")
                                        .font(.subheadline)
                                        .fontWeight(.bold)
                                        .foregroundColor(.white)
                                    Text("Courier: \(order.riderName ?? "Ramesh Karki") • ETA \(order.riderEtaMinutes ?? 3)m")
                                        .font(.caption)
                                        .foregroundColor(.gray)
                                }
                                Spacer()
                                Button(action: { showCourierHandoff = true }) {
                                    Text("Chat / Bay")
                                        .font(.caption)
                                        .fontWeight(.bold)
                                        .foregroundColor(.white)
                                        .padding(.horizontal, 10)
                                        .padding(.vertical, 6)
                                        .background(Color(hex: "F97316"))
                                        .cornerRadius(8)
                                }
                            }
                            .padding()
                            .background(Color(hex: "166534").opacity(0.3))
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(Color(hex: "22C55E"), lineWidth: 1)
                            )
                            .cornerRadius(12)
                        }
                        
                        // Items Grouped by Department / Aisle
                        let aisleGroups = Dictionary(grouping: order.items, by: { $0.section })
                        
                        ForEach(aisleGroups.keys.sorted(), id: \.self) { section in
                            VStack(alignment: .leading, spacing: 10) {
                                HStack {
                                    Text("Aisle: \(section.uppercased())")
                                        .font(.caption)
                                        .fontWeight(.bold)
                                        .foregroundColor(Color(hex: "F97316"))
                                    Spacer()
                                    let packedCount = aisleGroups[section]?.filter({ $0.isPacked }).count ?? 0
                                    let totalCount = aisleGroups[section]?.count ?? 0
                                    Text("\(packedCount)/\(totalCount) Packed")
                                        .font(.caption2)
                                        .foregroundColor(packedCount == totalCount ? Color(hex: "22C55E") : .gray)
                                }
                                
                                ForEach(aisleGroups[section] ?? []) { item in
                                    OrderItemRow(
                                        item: item,
                                        onTogglePack: {
                                            viewModel.toggleItemPacked(orderId: order.id, orderItemId: item.id)
                                        },
                                        onWeigh: {
                                            itemForWeighing = item
                                            weightInputString = String(format: "%.2f", item.measuredWeight ?? 1.0)
                                        },
                                        onSubstitute: {
                                            itemForSubstitution = item
                                        }
                                    )
                                }
                            }
                            .padding(.vertical, 4)
                        }
                    }
                    .padding(16)
                }
                
                // Bottom Fixed "All Items Packed" Bar
                VStack(spacing: 8) {
                    let totalItems = order.items.count
                    let packedItems = order.items.filter { $0.isPacked }.count
                    let allDone = (packedItems == totalItems && totalItems > 0)
                    
                    HStack {
                        Text("\(packedItems) of \(totalItems) Items Packed")
                            .font(.subheadline)
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                        Spacer()
                        Text(allDone ? "All Checked Off ✓" : "\(totalItems - packedItems) Remaining")
                            .font(.caption)
                            .foregroundColor(allDone ? Color(hex: "22C55E") : .orange)
                    }
                    
                    if order.status != "ready_for_pickup" {
                        Button(action: {
                            viewModel.completeOrderPacking(orderId: order.id)
                        }) {
                            HStack {
                                Image(systemName: "checkmark.seal.fill")
                                Text("All Items Packed → Ready for Courier")
                                    .fontWeight(.bold)
                            }
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                            .background(allDone ? Color(hex: "22C55E") : Color(hex: "334155"))
                            .cornerRadius(12)
                        }
                        .disabled(!allDone)
                    }
                }
                .padding(16)
                .background(Color(hex: "0F172A"))
            } else {
                VStack(spacing: 12) {
                    Image(systemName: "cart.badge.questionmark")
                        .font(.system(size: 48))
                        .foregroundColor(.gray)
                    Text("No Orders to Pick")
                        .font(.headline)
                        .foregroundColor(.white)
                    Text("Incoming grocery carts from customers will appear here.")
                        .font(.caption)
                        .foregroundColor(.gray)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
        .background(Color(hex: "0B132B").edgesIgnoringSafeArea(.all))
        // Weight Entry Modal Dialog
        .sheet(item: $itemForWeighing) { item in
            VStack(spacing: 20) {
                Text("⚖️ Enter Measured Weight")
                    .font(.headline)
                    .foregroundColor(.white)
                
                Text("\(item.name)")
                    .font(.subheadline)
                    .foregroundColor(.gray)
                
                HStack(spacing: 8) {
                    TextField("Weight in kg", text: $weightInputString)
                        .keyboardType(.decimalPad)
                        .padding()
                        .background(Color(hex: "1E293B"))
                        .cornerRadius(10)
                        .foregroundColor(.white)
                        .multilineTextAlignment(.center)
                        .font(.title2)
                    Text("kg")
                        .font(.headline)
                        .foregroundColor(.white)
                }
                .padding(.horizontal)
                
                if let w = Double(weightInputString), w > 0 {
                    let calculatedPrice = Double(item.unitPricePaisa) / 100.0 * w
                    Text("Recalculated Line Total: NPR \(String(format: "%.0f", calculatedPrice))")
                        .font(.caption)
                        .foregroundColor(Color(hex: "22C55E"))
                }
                
                HStack(spacing: 14) {
                    Button("Cancel") {
                        itemForWeighing = nil
                    }
                    .foregroundColor(.gray)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color(hex: "1E293B"))
                    .cornerRadius(10)
                    
                    Button("Confirm & Pack") {
                        if let w = Double(weightInputString), let order = activeOrder {
                            viewModel.setMeasuredWeight(orderId: order.id, orderItemId: item.id, weightKg: w)
                        }
                        itemForWeighing = nil
                    }
                    .foregroundColor(.white)
                    .fontWeight(.bold)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color(hex: "22C55E"))
                    .cornerRadius(10)
                }
                .padding(.horizontal)
            }
            .padding()
            .background(Color(hex: "0F172A").edgesIgnoringSafeArea(.all))
        }
        // Substitution Sheet
        .sheet(item: $itemForSubstitution) { item in
            if let order = activeOrder {
                GrocerySubstitutionSheet(
                    viewModel: viewModel,
                    order: order,
                    item: item,
                    isPresented: Binding(
                        get: { itemForSubstitution != nil },
                        set: { if !$0 { itemForSubstitution = nil } }
                    )
                )
            }
        }
        // Courier Handoff Sheet
        .sheet(isPresented: $showCourierHandoff) {
            if let order = activeOrder {
                CourierHandoffSheet(
                    viewModel: viewModel,
                    order: order,
                    isPresented: $showCourierHandoff
                )
            }
        }
    }
    
    private func statusBadge(for status: String) -> String {
        switch status {
        case "acknowledged": return "New Cart"
        case "preparing": return "Packing"
        case "ready_for_pickup": return "Packed & Ready"
        case "dispatched": return "Out with Courier"
        case "delivered": return "Delivered"
        default: return status.capitalized
        }
    }
    
    private func statusColor(for status: String) -> Color {
        switch status {
        case "acknowledged": return .orange
        case "preparing": return Color(hex: "3B82F6")
        case "ready_for_pickup": return Color(hex: "22C55E")
        case "dispatched": return Color(hex: "A855F7")
        default: return .gray
        }
    }
}

// MARK: - Order Item Row Component
struct OrderItemRow: View {
    let item: GroceryOrderItem
    let onTogglePack: () -> Void
    let onWeigh: () -> Void
    let onSubstitute: () -> Void
    
    var body: some View {
        HStack(spacing: 12) {
            Button(action: onTogglePack) {
                Image(systemName: item.isPacked ? "checkmark.square.fill" : "square")
                    .font(.title2)
                    .foregroundColor(item.isPacked ? Color(hex: "22C55E") : .gray)
            }
            .buttonStyle(PlainButtonStyle())
            
            VStack(alignment: .leading, spacing: 3) {
                Text(item.name)
                    .font(.subheadline)
                    .fontWeight(.bold)
                    .foregroundColor(item.isPacked ? .gray : .white)
                    .strikethrough(item.isPacked)
                
                HStack(spacing: 6) {
                    Text("\(item.quantity) \(item.unit)")
                        .font(.caption2)
                        .foregroundColor(Color(hex: "F97316"))
                    Text("•")
                        .font(.caption2)
                        .foregroundColor(.gray)
                    Text("NPR \(String(format: "%.0f", item.unitPriceRs))")
                        .font(.caption2)
                        .foregroundColor(.gray)
                    
                    if item.isLooseWeight {
                        Text("Weigh Required")
                            .font(.caption2)
                            .fontWeight(.bold)
                            .padding(.horizontal, 4)
                            .padding(.vertical, 1)
                            .background(Color.blue.opacity(0.2))
                            .foregroundColor(Color(hex: "38BDF8"))
                            .cornerRadius(4)
                    }
                }
                
                if let weight = item.measuredWeight {
                    Text("Scale: \(String(format: "%.2f", weight)) kg → NPR \(String(format: "%.0f", item.lineTotalRs))")
                        .font(.caption2)
                        .foregroundColor(Color(hex: "22C55E"))
                }
                
                if item.substitutionStatus != .none {
                    HStack(spacing: 4) {
                        Image(systemName: "arrow.triangle.swap")
                            .font(.caption2)
                        Text(item.substitutionStatus.label)
                            .font(.caption2)
                    }
                    .foregroundColor(substitutionColor(item.substitutionStatus))
                }
            }
            
            Spacer()
            
            VStack(alignment: .trailing, spacing: 4) {
                Text("NPR \(String(format: "%.0f", item.lineTotalRs))")
                    .font(.subheadline)
                    .fontWeight(.bold)
                    .foregroundColor(item.isPacked ? .gray : .white)
                
                HStack(spacing: 6) {
                    if item.isLooseWeight && !item.isPacked {
                        Button(action: onWeigh) {
                            Text("⚖️ Weigh")
                                .font(.caption2)
                                .fontWeight(.bold)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 3)
                                .background(Color(hex: "0284C7"))
                                .foregroundColor(.white)
                                .cornerRadius(6)
                        }
                    }
                    
                    if !item.isPacked {
                        Button(action: onSubstitute) {
                            Text("Out of Stock")
                                .font(.caption2)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 3)
                                .background(Color(hex: "EF4444").opacity(0.2))
                                .foregroundColor(Color(hex: "EF4444"))
                                .cornerRadius(6)
                        }
                    }
                }
            }
        }
        .padding()
        .background(item.isPacked ? Color(hex: "0F172A").opacity(0.6) : Color(hex: "1E293B"))
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(item.isPacked ? Color(hex: "22C55E").opacity(0.3) : Color(hex: "334155"), lineWidth: 1)
        )
    }
    
    private func substitutionColor(_ status: SubstitutionStatus) -> Color {
        switch status {
        case .none: return .gray
        case .proposed: return .orange
        case .approved: return Color(hex: "22C55E")
        case .rejected: return Color(hex: "EF4444")
        }
    }
}
