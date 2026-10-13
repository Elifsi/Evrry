//
//  GroceryInventoryView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Supermarket Inventory Management & Rapid Stock Toggling Screen
//

import SwiftUI

public struct GroceryInventoryView: View {
    @ObservedObject var viewModel: GrocerySupermarketViewModel
    let onOpenScanner: () -> Void
    
    let aisles = [
        "All",
        "Fresh Produce",
        "Dairy & Bakery",
        "Beverages",
        "Packaged Foods",
        "Snacks & Noodles",
        "Personal Care",
        "Household"
    ]
    
    public init(
        viewModel: GrocerySupermarketViewModel,
        onOpenScanner: @escaping () -> Void
    ) {
        self.viewModel = viewModel
        self.onOpenScanner = onOpenScanner
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            // Search & Barcode Scan Bar
            HStack(spacing: 12) {
                HStack(spacing: 8) {
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(.gray)
                    TextField("Search catalog name, SKU or barcode...", text: $viewModel.inventorySearchQuery)
                        .foregroundColor(.white)
                        .font(.subheadline)
                    if !viewModel.inventorySearchQuery.isEmpty {
                        Button(action: { viewModel.inventorySearchQuery = "" }) {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundColor(.gray)
                        }
                    }
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 10)
                .background(Color(hex: "1E293B"))
                .cornerRadius(12)
                
                Button(action: onOpenScanner) {
                    Image(systemName: "barcode.viewfinder")
                        .font(.title3)
                        .foregroundColor(.white)
                        .padding(10)
                        .background(Color(hex: "F97316"))
                        .cornerRadius(12)
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 12)
            .padding(.bottom, 8)
            .background(Color(hex: "0F172A"))
            
            // Department / Aisle Chips
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(aisles, id: \.self) { aisle in
                        let isSelected = (aisle == viewModel.selectedAisle)
                        Button(action: {
                            viewModel.selectedAisle = aisle
                        }) {
                            Text(aisle)
                                .font(.caption)
                                .fontWeight(isSelected ? .bold : .medium)
                                .padding(.horizontal, 14)
                                .padding(.vertical, 8)
                                .background(isSelected ? Color(hex: "F97316") : Color(hex: "1E293B"))
                                .foregroundColor(isSelected ? .white : .gray)
                                .cornerRadius(20)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 20)
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
            
            // Filtered Items List
            let filteredItems = viewModel.getFilteredInventory()
            
            if filteredItems.isEmpty {
                VStack(spacing: 12) {
                    Image(systemName: "shippingbox")
                        .font(.system(size: 44))
                        .foregroundColor(.gray)
                    Text("No Matching Supermarket Items")
                        .font(.headline)
                        .foregroundColor(.white)
                    Text("Try adjusting your aisle filter or search query.")
                        .font(.caption)
                        .foregroundColor(.gray)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                ScrollView {
                    LazyVStack(spacing: 10) {
                        ForEach(filteredItems) { item in
                            InventoryItemCard(
                                item: item,
                                onUpdateStock: { delta in
                                    viewModel.updateStockQuantity(itemId: item.id, delta: delta)
                                },
                                onToggleAvailability: {
                                    viewModel.toggleStockAvailability(itemId: item.id)
                                }
                            )
                        }
                    }
                    .padding(16)
                }
            }
        }
        .background(Color(hex: "0B132B").edgesIgnoringSafeArea(.all))
    }
}

// MARK: - Inventory Item Card
struct InventoryItemCard: View {
    let item: GroceryPartnerItem
    let onUpdateStock: (Int) -> Void
    let onToggleAvailability: () -> Void
    
    var body: some View {
        HStack(spacing: 14) {
            // Thumbnail / Icon
            ZStack {
                RoundedRectangle(cornerRadius: 10)
                    .fill(Color(hex: "0F172A"))
                    .frame(width: 50, height: 50)
                Text(itemIcon(for: item.section))
                    .font(.title2)
            }
            
            // Item Info
            VStack(alignment: .leading, spacing: 3) {
                Text(item.name)
                    .font(.subheadline)
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                    .lineLimit(1)
                
                if let nameNe = item.nameNe {
                    Text(nameNe)
                        .font(.caption2)
                        .foregroundColor(.gray)
                }
                
                HStack(spacing: 6) {
                    Text(item.brand)
                        .font(.caption2)
                        .foregroundColor(.gray)
                    Text("•")
                        .font(.caption2)
                        .foregroundColor(.gray)
                    Text("SKU: \(item.sku ?? "N/A")")
                        .font(.caption2)
                        .foregroundColor(Color(hex: "F97316"))
                }
                
                Text("NPR \(String(format: "%.0f", item.priceNpr)) / \(item.unit)")
                    .font(.caption)
                    .fontWeight(.bold)
                    .foregroundColor(Color(hex: "22C55E"))
            }
            
            Spacer()
            
            // Stock Controls
            VStack(alignment: .trailing, spacing: 6) {
                // In Stock / 86'd Toggle Button
                Button(action: onToggleAvailability) {
                    Text(item.isAvailable ? "In Stock" : "86'd Out")
                        .font(.caption2)
                        .fontWeight(.bold)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(item.isAvailable ? Color(hex: "22C55E").opacity(0.2) : Color(hex: "EF4444").opacity(0.2))
                        .foregroundColor(item.isAvailable ? Color(hex: "22C55E") : Color(hex: "EF4444"))
                        .cornerRadius(6)
                }
                
                // Rapid Counter (+ / -)
                HStack(spacing: 8) {
                    Button(action: { onUpdateStock(-1) }) {
                        Text("-")
                            .font(.subheadline)
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                            .frame(width: 28, height: 28)
                            .background(Color(hex: "334155"))
                            .clipShape(Circle())
                    }
                    
                    Text("\(item.stockQuantity)")
                        .font(.subheadline)
                        .fontWeight(.bold)
                        .foregroundColor(item.stockQuantity < 5 ? .orange : .white)
                        .frame(minWidth: 24)
                    
                    Button(action: { onUpdateStock(1) }) {
                        Text("+")
                            .font(.subheadline)
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                            .frame(width: 28, height: 28)
                            .background(Color(hex: "F97316"))
                            .clipShape(Circle())
                    }
                }
            }
        }
        .padding(14)
        .background(Color(hex: "1E293B"))
        .cornerRadius(14)
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .stroke(item.isAvailable ? Color(hex: "334155") : Color(hex: "EF4444").opacity(0.5), lineWidth: 1)
        )
    }
    
    private func itemIcon(for section: String) -> String {
        switch section.lowercased() {
        case "fresh produce": return "🥬"
        case "dairy & bakery": return "🥛"
        case "beverages": return "🧃"
        case "packaged foods": return "🍚"
        case "snacks & noodles": return "🍜"
        case "personal care": return "🧼"
        case "household": return "🧹"
        default: return "📦"
        }
    }
}
