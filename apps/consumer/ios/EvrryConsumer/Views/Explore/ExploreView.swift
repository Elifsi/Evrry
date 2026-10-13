//
//  ExploreView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Explore Catalog & Multi-Vertical View
//

import SwiftUI

public struct ExploreView: View {
    @ObservedObject public var viewModel: ExploreViewModel
    public let onNavigateToCart: () -> Void
    public let onNavigateToFood: () -> Void
    public let onNavigateToGrocery: () -> Void
    public let onProfileClick: () -> Void
    
    public init(
        viewModel: ExploreViewModel,
        onNavigateToCart: @escaping () -> Void = {},
        onNavigateToFood: @escaping () -> Void = {},
        onNavigateToGrocery: @escaping () -> Void = {},
        onProfileClick: @escaping () -> Void = {}
    ) {
        self.viewModel = viewModel
        self.onNavigateToCart = onNavigateToCart
        self.onNavigateToFood = onNavigateToFood
        self.onNavigateToGrocery = onNavigateToGrocery
        self.onProfileClick = onProfileClick
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            // TOP BAR with Cart Icon Button & Count Badge
            EvrryTopBar(
                title: "Explore Evrry",
                canGoBack: false,
                onProfileClick: onProfileClick,
                cartItemCount: viewModel.cart.itemCount,
                onCartClick: onNavigateToCart
            )
            
            // CATEGORY CHIPS SCROLLER
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    let allSelected = viewModel.selectedVertical == "all"
                    Button(action: { viewModel.selectVertical("all") }) {
                        Text("✨ All")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(allSelected ? .white : .evrryDarkCharcoal)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 8)
                            .background(allSelected ? Color.evrryDarkCharcoal : Color.white)
                            .clipShape(Capsule())
                            .overlay(Capsule().stroke(allSelected ? Color.evrryDarkCharcoal : Color.evrrySubtleBorder, lineWidth: 1))
                    }
                    
                    ForEach(ServiceVertical.activeCases) { vertical in
                        let isSelected = viewModel.selectedVertical == vertical.id
                        Button(action: {
                            switch vertical {
                            case .food:
                                onNavigateToFood()
                            case .grocery:
                                onNavigateToGrocery()
                            default:
                                viewModel.selectVertical(vertical.id)
                            }
                        }) {
                            Text("\(vertical.emoji) \(vertical.title)")
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundColor(isSelected ? .white : .evrryDarkCharcoal)
                                .padding(.horizontal, 14)
                                .padding(.vertical, 8)
                                .background(isSelected ? Color.evrryDarkCharcoal : Color.white)
                                .clipShape(Capsule())
                                .overlay(Capsule().stroke(isSelected ? Color.evrryDarkCharcoal : Color.evrrySubtleBorder, lineWidth: 1))
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
            }
            
            // CATALOG CARDS LIST
            ScrollView {
                LazyVStack(spacing: 12) {
                    // DEDICATED FOOD DELIVERY HERO BANNER
                    Button(action: onNavigateToFood) {
                        ZStack {
                            LinearGradient(
                                colors: [Color(red: 0.98, green: 0.45, blue: 0.09), Color(red: 0.85, green: 0.47, blue: 0.02)],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                            
                            HStack {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("🥟 FRESH & HOT")
                                        .font(.system(size: 10, weight: .black))
                                        .foregroundColor(.white.opacity(0.85))
                                    Text("Food & Restaurant Delivery")
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(.white)
                                    Text("Kathmandu & Lalitpur top kitchens • 25 min delivery")
                                        .font(.system(size: 11))
                                        .foregroundColor(.white.opacity(0.9))
                                    
                                    Text("Explore Kitchens →")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(Color(red: 0.76, green: 0.25, blue: 0.05))
                                        .padding(.horizontal, 10)
                                        .padding(.vertical, 4)
                                        .background(Color.white)
                                        .clipShape(RoundedRectangle(cornerRadius: 8))
                                        .padding(.top, 4)
                                }
                                Spacer()
                                Text("🍲")
                                    .font(.system(size: 38))
                            }
                            .padding(16)
                        }
                        .clipShape(RoundedRectangle(cornerRadius: 20))
                    }
                    .buttonStyle(PlainButtonStyle())

                    // DEDICATED GROCERY QUICK MART HERO BANNER
                    Button(action: onNavigateToGrocery) {
                        ZStack {
                            LinearGradient(
                                colors: [Color(red: 0.02, green: 0.59, blue: 0.41), Color(red: 0.05, green: 0.58, blue: 0.53)],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                            
                            HStack {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("⚡ 10-15 MIN DISPATCH")
                                        .font(.system(size: 10, weight: .black))
                                        .foregroundColor(.white.opacity(0.85))
                                    Text("Grocery Quick Mart")
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(.white)
                                    Text("DDC Milk, Farm Eggs, Wai Wai & Fresh Produce")
                                        .font(.system(size: 11))
                                        .foregroundColor(.white.opacity(0.9))
                                    
                                    Text("Shop Quick Mart →")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                                        .padding(.horizontal, 10)
                                        .padding(.vertical, 4)
                                        .background(Color.white)
                                        .clipShape(RoundedRectangle(cornerRadius: 8))
                                        .padding(.top, 4)
                                }
                                Spacer()
                                Text("🥦")
                                    .font(.system(size: 38))
                            }
                            .padding(16)
                        }
                        .clipShape(RoundedRectangle(cornerRadius: 20))
                    }
                    .buttonStyle(PlainButtonStyle())
                    ForEach(viewModel.items) { item in
                        VStack(alignment: .leading, spacing: 14) {
                            HStack(alignment: .top) {
                                VStack(alignment: .leading, spacing: 3) {
                                    Text(item.title)
                                        .font(.system(size: 15, weight: .bold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                    Text(item.subtitle)
                                        .font(.system(size: 12))
                                        .foregroundColor(.evrryMutedText)
                                }
                                Spacer()
                                HStack(spacing: 3) {
                                    Text("⭐").font(.system(size: 10))
                                    Text(String(format: "%.1f", item.rating))
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                                }
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(Color(red: 0.99, green: 0.95, blue: 0.78))
                                .clipShape(RoundedRectangle(cornerRadius: 8))
                            }
                            
                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    HStack(spacing: 4) {
                                        Text("Rs \(Int(item.priceNpr))")
                                            .font(.system(size: 16, weight: .black))
                                            .foregroundColor(.evrryDarkCharcoal)
                                        if let orig = item.originalPriceNpr {
                                            Text("Rs \(Int(orig))")
                                                .font(.system(size: 12))
                                                .strikethrough()
                                                .foregroundColor(.evrryMutedText)
                                        }
                                    }
                                    Text("by \(item.vendorName)")
                                        .font(.system(size: 11))
                                        .foregroundColor(.evrryMutedText)
                                }
                                Spacer()
                                Button(action: { viewModel.addToCart(item) }) {
                                    HStack(spacing: 4) {
                                        Image(systemName: "plus")
                                            .font(.system(size: 12, weight: .bold))
                                        Text("Add")
                                            .font(.system(size: 13, weight: .bold))
                                    }
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 8)
                                    .background(Color.evrryPrimaryBlue)
                                    .clipShape(RoundedRectangle(cornerRadius: 12))
                                }
                            }
                        }
                        .padding(16)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 20))
                        .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        .shadow(color: Color.black.opacity(0.02), radius: 3, x: 0, y: 1)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 6)
                .padding(.bottom, viewModel.cart.itemCount > 0 ? 100 : 20)
            }
            
            // FLOATING CART BAR (If cart has items)
            if viewModel.cart.itemCount > 0 {
                Button(action: onNavigateToCart) {
                    HStack {
                        Image(systemName: "cart.fill")
                            .foregroundColor(.white)
                        Text("\(viewModel.cart.itemCount) items in Cart")
                            .font(.system(size: 14, weight: .semibold))
                            .foregroundColor(.white)
                        Spacer()
                        Text("View Cart · Rs \(Int(viewModel.cart.totalNpr)) →")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.white)
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 14)
                    .background(Color.evrryDarkCharcoal)
                    .clipShape(RoundedRectangle(cornerRadius: 18))
                    .shadow(color: Color.black.opacity(0.12), radius: 8, x: 0, y: 3)
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 10)
            }
        }
        .background(Color.evrryBgTop.ignoresSafeArea())
    }
}
