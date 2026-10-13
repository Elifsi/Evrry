//
//  ExploreView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Explore Services Hub matching prototype/Phone/app/explore/page.tsx
//  Strictly limited to the 3 live verticals:
//  1. City Rides (Cab & Bike)
//  2. Food Delivery
//  3. Grocery Quick Mart
//  (Hotels & Room Rentals strictly excluded from user navigation per release scope)
//

import SwiftUI

public struct ExploreView: View {
    @ObservedObject public var viewModel: ExploreViewModel
    @ObservedObject private var addressService = SavedAddressService.shared
    public let onNavigateToCart: () -> Void
    public let onNavigateToFood: () -> Void
    public let onNavigateToGrocery: () -> Void
    public let onNavigateToRides: () -> Void
    public let onNavigateToHome: () -> Void
    public let onNavigateToCamera: () -> Void
    public let onProfileClick: () -> Void

    @State private var searchQuery: String = ""
    @State private var showLocationPicker: Bool = false

    public init(
        viewModel: ExploreViewModel,
        onNavigateToCart: @escaping () -> Void = {},
        onNavigateToFood: @escaping () -> Void = {},
        onNavigateToGrocery: @escaping () -> Void = {},
        onNavigateToRides: @escaping () -> Void = {},
        onNavigateToHome: @escaping () -> Void = {},
        onNavigateToCamera: @escaping () -> Void = {},
        onProfileClick: @escaping () -> Void = {}
    ) {
        self.viewModel = viewModel
        self.onNavigateToCart = onNavigateToCart
        self.onNavigateToFood = onNavigateToFood
        self.onNavigateToGrocery = onNavigateToGrocery
        self.onNavigateToRides = onNavigateToRides
        self.onNavigateToHome = onNavigateToHome
        self.onNavigateToCamera = onNavigateToCamera
        self.onProfileClick = onProfileClick
    }

    private var activeAddress: SavedAddress? {
        addressService.addresses.first(where: { $0.isDefault }) ?? addressService.addresses.first
    }

    private var filteredItems: [CatalogItem] {
        if searchQuery.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            return viewModel.items
        }
        return viewModel.items.filter {
            $0.title.localizedCaseInsensitiveContains(searchQuery) ||
            $0.subtitle.localizedCaseInsensitiveContains(searchQuery) ||
            $0.vendorName.localizedCaseInsensitiveContains(searchQuery)
        }
    }

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 18) {
                // 1. LOCATION BAR & AI CONCIERGE CHIP
                HStack {
                    Button(action: { showLocationPicker = true }) {
                        HStack(spacing: 6) {
                            Image(systemName: "mappin.and.ellipse")
                                .font(.system(size: 15))
                                .foregroundColor(.evrryPrimaryBlue)
                            Text(activeAddress?.label ?? "Kathmandu Valley")
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Text(activeAddress != nil ? "(\(activeAddress!.address.prefix(16))...)" : "(Doorstep)")
                                .font(.system(size: 11))
                                .foregroundColor(.evrryMutedText)
                            Image(systemName: "chevron.down")
                                .font(.system(size: 10, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                        }
                    }

                    Spacer()

                        // Top-Right Cart Icon Button with Badge (1, 2, 3...)
                        Button(action: onNavigateToCart) {
                            ZStack(alignment: .topTrailing) {
                                Circle()
                                    .fill(Color.white)
                                    .frame(width: 36, height: 36)
                                    .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                Image(systemName: "cart.fill")
                                    .font(.system(size: 15))
                                    .foregroundColor(viewModel.cart.itemCount > 0 ? .evrryPrimaryBlue : .evrryDarkCharcoal)
                                    .frame(width: 36, height: 36)
                                
                                if viewModel.cart.itemCount > 0 {
                                    Text(viewModel.cart.itemCount > 99 ? "99+" : "\(viewModel.cart.itemCount)")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(.white)
                                        .frame(width: 17, height: 17)
                                        .background(Color.evrryPrimaryBlue)
                                        .clipShape(Circle())
                                        .offset(x: 4, y: -4)
                                }
                            }
                        }

                        Button(action: onNavigateToHome) {
                            HStack(spacing: 4) {
                                Image(systemName: "sparkles")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(.evrryPrimaryBlue)
                                Text("AI Concierge")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(.evrryPrimaryBlue)
                            }
                            .padding(.horizontal, 10)
                            .padding(.vertical, 6)
                            .background(Color(red: 0.94, green: 0.96, blue: 1.0))
                            .clipShape(Capsule())
                            .overlay(Capsule().stroke(Color(red: 0.75, green: 0.86, blue: 0.99), lineWidth: 1))
                        }
                    }
                }
                .padding(.top, 16)

                // 2. HEADER TITLE & SUBTITLE
                VStack(alignment: .leading, spacing: 2) {
                    Text("Explore Services")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text("Rides · Food · Groceries — and more coming soon across Nepal.")
                        .font(.system(size: 12))
                        .foregroundColor(.evrryMutedText)
                }

                // 3. GLOBAL SEARCH BAR
                HStack(spacing: 10) {
                    Image(systemName: "magnifyingglass")
                        .font(.system(size: 16))
                        .foregroundColor(.evrryMutedText)
                    TextField("Search food, groceries, rides...", text: $searchQuery)
                        .font(.system(size: 13))
                    Button(action: onNavigateToCamera) {
                        Image(systemName: "qrcode.viewfinder")
                            .font(.system(size: 18))
                            .foregroundColor(.evrryPrimaryBlue)
                    }
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 12)
                .background(Color.white)
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                .shadow(color: Color.black.opacity(0.02), radius: 3, x: 0, y: 1)

                // 4. "AVAILABLE NOW" SECTION HEADER
                HStack {
                    Text("AVAILABLE NOW")
                        .font(.system(size: 11, weight: .black))
                        .foregroundColor(.evrryMutedText)
                        .tracking(1.0)
                    Spacer()
                    Text("Live")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundColor(.evrryPrimaryBlue)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 3)
                        .background(Color(red: 0.94, green: 0.96, blue: 1.0))
                        .clipShape(Capsule())
                }

                // 5. VERTICAL 1: CITY RIDES
                Button(action: onNavigateToRides) {
                    HStack(spacing: 14) {
                        ZStack {
                            RoundedRectangle(cornerRadius: 14)
                                .fill(LinearGradient(
                                    colors: [Color(red: 0.15, green: 0.39, blue: 0.92), Color(red: 0.26, green: 0.22, blue: 0.79)],
                                    startPoint: .topLeading,
                                    endPoint: .bottomTrailing
                                ))
                                .frame(width: 50, height: 50)
                            Image(systemName: "car.fill")
                                .font(.system(size: 22))
                                .foregroundColor(.white)
                        }

                        VStack(alignment: .leading, spacing: 2) {
                            HStack(spacing: 6) {
                                Text("City Rides")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Text("EV & Moto")
                                    .font(.system(size: 9, weight: .bold))
                                    .foregroundColor(Color(red: 0.11, green: 0.31, blue: 0.85))
                                    .padding(.horizontal, 5)
                                    .padding(.vertical, 2)
                                    .background(Color(red: 0.94, green: 0.96, blue: 1.0))
                                    .clipShape(RoundedRectangle(cornerRadius: 6))
                            }
                            Text("Pathao & Uber style · Bike, Auto & Cab")
                                .font(.system(size: 11))
                                .foregroundColor(.evrryMutedText)
                            Text("Rs 65 base · Tap to book")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryPrimaryBlue)
                                .padding(.top, 2)
                        }

                        Spacer()

                        Image(systemName: "chevron.right")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(.evrryMutedText)
                    }
                    .padding(16)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 20))
                    .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color(red: 0.75, green: 0.86, blue: 0.99), lineWidth: 1))
                    .shadow(color: Color.black.opacity(0.03), radius: 4, x: 0, y: 1)
                }
                .buttonStyle(PlainButtonStyle())

                // 6. VERTICAL 2: FOOD DELIVERY
                Button(action: onNavigateToFood) {
                    HStack(spacing: 14) {
                        ZStack {
                            RoundedRectangle(cornerRadius: 14)
                                .fill(LinearGradient(
                                    colors: [Color(red: 0.98, green: 0.45, blue: 0.09), Color(red: 0.85, green: 0.47, blue: 0.02)],
                                    startPoint: .topLeading,
                                    endPoint: .bottomTrailing
                                ))
                                .frame(width: 50, height: 50)
                            Text("🍲")
                                .font(.system(size: 24))
                        }

                        VStack(alignment: .leading, spacing: 2) {
                            HStack(spacing: 6) {
                                Text("Food Delivery")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Text("25-35 min")
                                    .font(.system(size: 9, weight: .bold))
                                    .foregroundColor(Color(red: 0.76, green: 0.25, blue: 0.05))
                                    .padding(.horizontal, 5)
                                    .padding(.vertical, 2)
                                    .background(Color(red: 1.0, green: 0.95, blue: 0.93))
                                    .clipShape(RoundedRectangle(cornerRadius: 6))
                            }
                            Text("DoorDash & Bhojdeals style · 40+ restaurants")
                                .font(.system(size: 11))
                                .foregroundColor(.evrryMutedText)
                            Text("Momos, Burgers & Newari sets")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(Color(red: 0.98, green: 0.45, blue: 0.09))
                                .padding(.top, 2)
                        }

                        Spacer()

                        Image(systemName: "chevron.right")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(.evrryMutedText)
                    }
                    .padding(16)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 20))
                    .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color(red: 0.99, green: 0.87, blue: 0.80), lineWidth: 1))
                    .shadow(color: Color.black.opacity(0.03), radius: 4, x: 0, y: 1)
                }
                .buttonStyle(PlainButtonStyle())

                // 7. VERTICAL 3: GROCERY QUICK MART
                Button(action: onNavigateToGrocery) {
                    HStack(spacing: 14) {
                        ZStack {
                            RoundedRectangle(cornerRadius: 14)
                                .fill(LinearGradient(
                                    colors: [Color(red: 0.02, green: 0.59, blue: 0.41), Color(red: 0.05, green: 0.58, blue: 0.53)],
                                    startPoint: .topLeading,
                                    endPoint: .bottomTrailing
                                ))
                                .frame(width: 50, height: 50)
                            Text("🥦")
                                .font(.system(size: 24))
                        }

                        VStack(alignment: .leading, spacing: 2) {
                            HStack(spacing: 6) {
                                Text("Grocery Marts")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Text("10-15 min")
                                    .font(.system(size: 9, weight: .bold))
                                    .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                                    .padding(.horizontal, 5)
                                    .padding(.vertical, 2)
                                    .background(Color(red: 0.92, green: 0.98, blue: 0.95))
                                    .clipShape(RoundedRectangle(cornerRadius: 6))
                            }
                            Text("Blinkit style · 500+ daily essentials")
                                .font(.system(size: 11))
                                .foregroundColor(.evrryMutedText)
                            Text("Milk, eggs, vegetables & staples")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(Color(red: 0.02, green: 0.59, blue: 0.41))
                                .padding(.top, 2)
                        }

                        Spacer()

                        Image(systemName: "chevron.right")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(.evrryMutedText)
                    }
                    .padding(16)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 20))
                    .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color(red: 0.77, green: 0.93, blue: 0.86), lineWidth: 1))
                    .shadow(color: Color.black.opacity(0.03), radius: 4, x: 0, y: 1)
                }
                .buttonStyle(PlainButtonStyle())

                // 8. SECONDARY SERVICES (Teasers for upcoming expansion)
                VStack(alignment: .leading, spacing: 10) {
                    Text("MORE SERVICES COMING SOON")
                        .font(.system(size: 11, weight: .black))
                        .foregroundColor(.evrryMutedText)
                        .tracking(1.0)

                    HStack(spacing: 10) {
                        ServiceTeaserPill(emoji: "📱", title: "Electronics")
                        ServiceTeaserPill(emoji: "👕", title: "Fashion")
                        ServiceTeaserPill(emoji: "🧹", title: "Home Care")
                    }
                }
                .padding(.top, 4)

                // 9. POPULAR ITEMS
                VStack(alignment: .leading, spacing: 12) {
                    Text("POPULAR PICKS")
                        .font(.system(size: 11, weight: .black))
                        .foregroundColor(.evrryMutedText)
                        .tracking(1.0)

                    ForEach(filteredItems) { item in
                        HStack(alignment: .top, spacing: 12) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(item.title)
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Text(item.subtitle)
                                    .font(.system(size: 11))
                                    .foregroundColor(.evrryMutedText)
                                Text("by \(item.vendorName)")
                                    .font(.system(size: 10))
                                    .foregroundColor(.evrryMutedText)
                                HStack(spacing: 4) {
                                    Text("Rs \(Int(item.priceNpr))")
                                        .font(.system(size: 14, weight: .black))
                                        .foregroundColor(.evrryDarkCharcoal)
                                    if let orig = item.originalPriceNpr {
                                        Text("Rs \(Int(orig))")
                                            .font(.system(size: 11))
                                            .strikethrough()
                                            .foregroundColor(.evrryMutedText)
                                    }
                                }
                                .padding(.top, 2)
                            }

                            Spacer()

                            Button(action: { viewModel.addToCart(item) }) {
                                HStack(spacing: 4) {
                                    Image(systemName: "plus")
                                        .font(.system(size: 11, weight: .bold))
                                    Text("Add")
                                        .font(.system(size: 12, weight: .bold))
                                }
                                .foregroundColor(.white)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 7)
                                .background(Color.evrryPrimaryBlue)
                                .clipShape(RoundedRectangle(cornerRadius: 10))
                            }
                        }
                        .padding(14)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        .shadow(color: Color.black.opacity(0.02), radius: 3, x: 0, y: 1)
                    }
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, viewModel.cart.itemCount > 0 ? 100 : 30)
        }
        .background(Color(red: 0.98, green: 0.98, blue: 1.0).ignoresSafeArea())
        .sheet(isPresented: $showLocationPicker) {
            LocationPickerSheetView(
                title: "Choose Delivery Location",
                onLocationSelected: { loc in
                    let newAddr = SavedAddress(
                        id: "addr_\(Int(Date().timeIntervalSince1970))",
                        label: loc.label.isEmpty ? loc.displayTitle : loc.label,
                        address: loc.address,
                        latitude: loc.latitude,
                        longitude: loc.longitude,
                        type: .other,
                        isDefault: true
                    )
                    SavedAddressService.shared.addAddress(newAddr)
                    SavedAddressService.shared.setDefault(id: newAddr.id)
                    showLocationPicker = false
                },
                onDismiss: { showLocationPicker = false }
            )
        }
    }
}

private struct ServiceTeaserPill: View {
    let emoji: String
    let title: String

    var body: some View {
        HStack(spacing: 6) {
            Text(emoji)
                .font(.system(size: 14))
            Text(title)
                .font(.system(size: 12, weight: .medium))
                .foregroundColor(.evrryMutedText)
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrrySubtleBorder, lineWidth: 1))
    }
}
