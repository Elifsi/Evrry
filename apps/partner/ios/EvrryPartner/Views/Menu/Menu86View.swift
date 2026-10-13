//
//  Menu86View.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Instant Menu Item 86'ing (Out-of-Stock Toggle) Drawer
//

import SwiftUI

public struct Menu86View: View {
    @ObservedObject public var viewModel: RestaurantKitchenViewModel
    public let onDismiss: () -> Void
    
    @State private var searchQuery: String = ""
    @State private var selectedCategory: String = "All"
    
    public init(viewModel: RestaurantKitchenViewModel, onDismiss: @escaping () -> Void) {
        self.viewModel = viewModel
        self.onDismiss = onDismiss
    }
    
    private var categories: [String] {
        var cats = ["All"]
        let uniqueCats = Array(Set(viewModel.catalogItems.map { $0.category })).sorted()
        cats.append(contentsOf: uniqueCats)
        return cats
    }
    
    private var filteredItems: [RestaurantCatalogItem] {
        viewModel.catalogItems.filter { item in
            let matchesCat = (selectedCategory == "All" || item.category == selectedCategory)
            let matchesSearch = searchQuery.isEmpty || item.name.localizedCaseInsensitiveContains(searchQuery)
            return matchesCat && matchesSearch
        }
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
                        Text("86'd Items & Stock Control")
                            .font(.system(size: 18, weight: .black))
                            .foregroundColor(PartnerKitchenTheme.textPrimary)
                        Text("LIVE")
                            .font(.system(size: 9, weight: .heavy))
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(PartnerKitchenTheme.accentGreen.opacity(0.2))
                            .foregroundColor(PartnerKitchenTheme.accentGreen)
                            .cornerRadius(4)
                    }
                    Text("Toggling off immediately removes the dish from Consumer ordering")
                        .font(.system(size: 11))
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
            .padding(.bottom, 12)
            
            // Search field
            HStack(spacing: 8) {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(PartnerKitchenTheme.textSecondary)
                TextField("Search dishes by name...", text: $searchQuery)
                    .foregroundColor(PartnerKitchenTheme.textPrimary)
            }
            .padding(10)
            .background(PartnerKitchenTheme.cardDark)
            .cornerRadius(10)
            .overlay(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
            )
            .padding(.horizontal, 16)
            .padding(.bottom, 10)
            
            // Category Filter Pills
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(categories, id: \.self) { cat in
                        let isSelected = (selectedCategory == cat)
                        Button {
                            selectedCategory = cat
                        } label: {
                            Text(cat)
                                .font(.system(size: 12, weight: isSelected ? .bold : .medium))
                                .padding(.horizontal, 12)
                                .padding(.vertical, 6)
                                .background(isSelected ? PartnerKitchenTheme.primaryOrange : PartnerKitchenTheme.cardDark)
                                .foregroundColor(isSelected ? .white : PartnerKitchenTheme.textSecondary)
                                .cornerRadius(8)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 8)
                                        .stroke(isSelected ? PartnerKitchenTheme.primaryOrange : PartnerKitchenTheme.cardBorder, lineWidth: 1)
                                    )
                        }
                    }
                }
                .padding(.horizontal, 16)
            }
            .padding(.bottom, 12)
            
            Divider()
                .background(PartnerKitchenTheme.cardBorder)
            
            // Items List
            ScrollView {
                LazyVStack(spacing: 10) {
                    ForEach(filteredItems) { item in
                        HStack(spacing: 12) {
                            VStack(alignment: .leading, spacing: 3) {
                                Text(item.name)
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(item.isAvailable ? PartnerKitchenTheme.textPrimary : PartnerKitchenTheme.textMuted)
                                    .strikethrough(!item.isAvailable, color: PartnerKitchenTheme.alertRed)
                                
                                HStack(spacing: 6) {
                                    Text("NPR \(String(format: "%.0f", item.priceNpr))")
                                        .font(.system(size: 12, weight: .semibold))
                                        .foregroundColor(PartnerKitchenTheme.primaryOrange)
                                    Text("•")
                                        .foregroundColor(PartnerKitchenTheme.textMuted)
                                    Text(item.category)
                                        .font(.system(size: 11))
                                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                                }
                            }
                            
                            Spacer()
                            
                            // Stock Toggle Switch
                            Button {
                                withAnimation(.spring(response: 0.3)) {
                                    viewModel.toggle86Item(id: item.id)
                                }
                            } label: {
                                HStack(spacing: 6) {
                                    Text(item.isAvailable ? "IN STOCK" : "86'D / SOLD OUT")
                                        .font(.system(size: 10, weight: .heavy))
                                    Image(systemName: item.isAvailable ? "checkmark.circle.fill" : "slash.circle.fill")
                                        .font(.system(size: 14))
                                }
                                .padding(.horizontal, 10)
                                .padding(.vertical, 7)
                                .background(item.isAvailable ? PartnerKitchenTheme.accentGreen.opacity(0.15) : PartnerKitchenTheme.alertRed.opacity(0.2))
                                .foregroundColor(item.isAvailable ? PartnerKitchenTheme.accentGreen : PartnerKitchenTheme.alertRed)
                                .cornerRadius(8)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 8)
                                        .stroke(item.isAvailable ? PartnerKitchenTheme.accentGreen.opacity(0.5) : PartnerKitchenTheme.alertRed.opacity(0.5), lineWidth: 1)
                                )
                            }
                        }
                        .padding(12)
                        .background(PartnerKitchenTheme.cardDark)
                        .cornerRadius(12)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
                        )
                    }
                }
                .padding(16)
            }
        }
        .background(PartnerKitchenTheme.bgDark)
    }
}
