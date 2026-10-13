//
//  AddressBookView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Saved Places & Address Book View (SwiftUI)
//

import SwiftUI

public struct AddressBookView: View {
    @ObservedObject public var service: SavedAddressService = SavedAddressService.shared
    public let onBack: () -> Void
    
    @State private var showAddPicker: Bool = false
    
    public init(onBack: @escaping () -> Void = {}) {
        self.onBack = onBack
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            EvrryTopBar(
                title: "Saved Places",
                canGoBack: true,
                onBack: onBack
            )
            
            VStack(spacing: 16) {
                // ADD NEW PLACE BUTTON
                Button(action: { showAddPicker = true }) {
                    HStack(spacing: 8) {
                        Image(systemName: "plus")
                            .font(.system(size: 14, weight: .bold))
                        Text("Add New Place")
                            .font(.system(size: 14, weight: .bold))
                    }
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
                    .background(Color.evrryPrimaryBlue)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
                }
                
                VStack(alignment: .leading, spacing: 8) {
                    Text("SAVED ADDRESSES (\(service.addresses.count))")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                    
                    ScrollView {
                        LazyVStack(spacing: 12) {
                            ForEach(service.addresses) { address in
                                HStack(spacing: 14) {
                                    ZStack {
                                        Circle()
                                            .fill(Color.blue.opacity(0.08))
                                            .frame(width: 42, height: 42)
                                        Text(address.type.emoji)
                                            .font(.system(size: 20))
                                    }
                                    
                                    VStack(alignment: .leading, spacing: 3) {
                                        HStack(spacing: 6) {
                                            Text(address.label)
                                                .font(.system(size: 15, weight: .bold))
                                                .foregroundColor(.evrryDarkCharcoal)
                                            if address.isDefault {
                                                Text("Default")
                                                    .font(.system(size: 10, weight: .bold))
                                                    .foregroundColor(Color(red: 0.09, green: 0.40, blue: 0.20))
                                                    .padding(.horizontal, 6)
                                                    .padding(.vertical, 2)
                                                    .background(Color(red: 0.86, green: 0.99, blue: 0.91))
                                                    .clipShape(Capsule())
                                            }
                                        }
                                        Text(address.address)
                                            .font(.system(size: 12))
                                            .foregroundColor(.evrryMutedText)
                                    }
                                    
                                    Spacer()
                                    
                                    Button(action: { service.removeAddress(id: address.id) }) {
                                        Image(systemName: "trash")
                                            .foregroundColor(.evrryRedDestructive.opacity(0.7))
                                            .font(.system(size: 16))
                                    }
                                }
                                .padding(16)
                                .background(Color.white)
                                .clipShape(RoundedRectangle(cornerRadius: 16))
                                .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                            }
                        }
                    }
                }
                
                Spacer()
            }
            .padding(16)
        }
        .background(Color.evrryBgTop.ignoresSafeArea())
        .sheet(isPresented: $showAddPicker) {
            LocationPickerSheetView(
                title: "Add Saved Place",
                onLocationSelected: { point in
                    service.addAddress(
                        SavedAddress(
                            id: "addr_\(Date().timeIntervalSince1970)",
                            label: point.label,
                            address: point.address,
                            latitude: point.latitude,
                            longitude: point.longitude,
                            type: .other
                        )
                    )
                    showAddPicker = false
                },
                onDismiss: { showAddPicker = false }
            )
        }
    }
}
