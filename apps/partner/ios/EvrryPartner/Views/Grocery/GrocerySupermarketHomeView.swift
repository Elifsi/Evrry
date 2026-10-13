//
//  GrocerySupermarketHomeView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Supermarket Partner Main Container View
//  Pick-and-Pack Orders, Inventory Stock, Barcode / SKU Scanner
//

import SwiftUI

public struct GrocerySupermarketHomeView: View {
    @ObservedObject var roleViewModel: PartnerRoleViewModel
    @StateObject var groceryViewModel = GrocerySupermarketViewModel()
    
    @State private var activeTab: Int = 0 // 0: Pick & Pack, 1: Inventory, 2: Barcode Scanner
    
    public init(roleViewModel: PartnerRoleViewModel) {
        self.roleViewModel = roleViewModel
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            // Adaptive Top Bar with 1-Tap Partner Role Switcher
            if activeTab != 2 {
                PartnerTopBar(
                    roleViewModel: roleViewModel,
                    storeName: "Bhatbhateni Supermarket • Pulchowk"
                )
            }
            
            // Tab Content
            ZStack {
                switch activeTab {
                case 0:
                    GroceryPickPackView(
                        viewModel: groceryViewModel,
                        onOpenScanner: { activeTab = 2 }
                    )
                case 1:
                    GroceryInventoryView(
                        viewModel: groceryViewModel,
                        onOpenScanner: { activeTab = 2 }
                    )
                case 2:
                    GroceryBarcodeScannerView(
                        viewModel: groceryViewModel,
                        onBack: { activeTab = 0 }
                    )
                default:
                    EmptyView()
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            
            // Bottom Tab Bar (hidden during full screen barcode scanning)
            if activeTab != 2 {
                Divider().background(Color(hex: "334155"))
                
                HStack(spacing: 0) {
                    TabBarButton(
                        icon: "checklist",
                        title: "Pick & Pack",
                        isSelected: activeTab == 0
                    ) {
                        activeTab = 0
                    }
                    
                    TabBarButton(
                        icon: "shippingbox",
                        title: "Inventory",
                        isSelected: activeTab == 1
                    ) {
                        activeTab = 1
                    }
                    
                    TabBarButton(
                        icon: "barcode.viewfinder",
                        title: "Barcode Audit",
                        isSelected: activeTab == 2
                    ) {
                        activeTab = 2
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(Color(hex: "0F172A"))
            }
        }
        .background(Color(hex: "0B132B").edgesIgnoringSafeArea(.all))
    }
}

struct TabBarButton: View {
    let icon: String
    let title: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 4) {
                Image(systemName: icon)
                    .font(.system(size: 20))
                    .foregroundColor(isSelected ? Color(hex: "F97316") : .gray)
                Text(title)
                    .font(.system(size: 11, weight: isSelected ? .bold : .medium))
                    .foregroundColor(isSelected ? Color(hex: "F97316") : .gray)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 4)
        }
    }
}
