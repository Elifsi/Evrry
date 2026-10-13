//
//  PaymentMethodsView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Dedicated Payment Methods Screen
//  Supports: eSewa, Khalti, Fonepay, Cards with quick 1-tap delete & add
//

import SwiftUI

public struct PaymentMethodsView: View {
    @ObservedObject public var store = SavedPaymentMethodStore.shared
    @Environment(\.presentationMode) var presentationMode
    public var onBack: (() -> Void)? = nil

    @State private var methodToDelete: SavedPaymentMethod? = nil
    @State private var showDeleteConfirm: Bool = false
    @State private var showAddSheet: Bool = false

    // New method state
    @State private var selectedType: SavedPaymentType = .esewa
    @State private var inputAccount: String = ""

    public init(onBack: (() -> Void)? = nil) {
        self.onBack = onBack
    }

    public var body: some View {
        VStack(spacing: 0) {
            // Top Bar
            HStack(spacing: 12) {
                Button(action: {
                    if let onBack = onBack {
                        onBack()
                    } else {
                        presentationMode.wrappedValue.dismiss()
                    }
                }) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                        .frame(width: 38, height: 38)
                        .background(Color.white)
                        .clipShape(Circle())
                        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
                }

                VStack(alignment: .leading, spacing: 2) {
                    Text("Payment Methods")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text("Saved digital wallets, cards & instant pay")
                        .font(.system(size: 11))
                        .foregroundColor(.evrryMutedText)
                }

                Spacer()

                Button(action: { showAddSheet = true }) {
                    Image(systemName: "plus")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.white)
                        .frame(width: 36, height: 36)
                        .background(Color.evrryPrimaryBlue)
                        .clipShape(Circle())
                }
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 14)
            .background(Color.white)

            ScrollView {
                VStack(spacing: 16) {
                    // Security Banner
                    HStack(spacing: 12) {
                        Image(systemName: "checkmark.shield.fill")
                            .font(.system(size: 24))
                            .foregroundColor(Color(red: 0.06, green: 0.73, blue: 0.51))

                        VStack(alignment: .leading, spacing: 2) {
                            Text("Nepal Rastra Bank (NRB) Compliant")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Text("All wallet tokens & cards are 256-bit encrypted with PCI-DSS 4.0 standards.")
                                .font(.system(size: 10))
                                .foregroundColor(.evrryMutedText)
                        }
                    }
                    .padding(14)
                    .background(Color(red: 0.94, green: 0.98, blue: 0.95))
                    .clipShape(RoundedRectangle(cornerRadius: 14))
                    .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color(red: 0.8, green: 0.93, blue: 0.84), lineWidth: 1))
                    .padding(.top, 14)

                    // Saved Instruments List
                    VStack(alignment: .leading, spacing: 10) {
                        Text("SAVED INSTRUMENTS (\(store.paymentMethods.count))")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                            .tracking(0.5)

                        ForEach(store.paymentMethods) { method in
                            VStack(spacing: 12) {
                                HStack(spacing: 12) {
                                    // Brand Icon
                                    ZStack {
                                        RoundedRectangle(cornerRadius: 12)
                                            .fill(method.type.brandColor.opacity(0.12))
                                            .frame(width: 44, height: 44)
                                        Image(systemName: method.type.iconName)
                                            .font(.system(size: 20))
                                            .foregroundColor(method.type.brandColor)
                                    }

                                    VStack(alignment: .leading, spacing: 2) {
                                        HStack(spacing: 6) {
                                            Text(method.title)
                                                .font(.system(size: 14, weight: .bold))
                                                .foregroundColor(.evrryDarkCharcoal)

                                            if method.isDefault {
                                                Text("DEFAULT")
                                                    .font(.system(size: 9, weight: .bold))
                                                    .foregroundColor(.white)
                                                    .padding(.horizontal, 6)
                                                    .padding(.vertical, 2)
                                                    .background(Color.evrryPrimaryBlue)
                                                    .clipShape(Capsule())
                                            }
                                        }

                                        Text(method.subtitle)
                                            .font(.system(size: 12))
                                            .foregroundColor(.evrryMutedText)
                                    }

                                    Spacer()

                                    // Quick Delete Button
                                    Button(action: {
                                        methodToDelete = method
                                        showDeleteConfirm = true
                                    }) {
                                        Image(systemName: "trash")
                                            .font(.system(size: 14))
                                            .foregroundColor(Color(red: 0.94, green: 0.27, blue: 0.27))
                                            .frame(width: 34, height: 34)
                                            .background(Color(red: 1.0, green: 0.94, blue: 0.94))
                                            .clipShape(Circle())
                                    }
                                }

                                Divider()

                                HStack {
                                    Text(method.linkedAt)
                                        .font(.system(size: 10, weight: .medium))
                                        .foregroundColor(Color(red: 0.06, green: 0.73, blue: 0.51))

                                    Spacer()

                                    if !method.isDefault {
                                        Button(action: {
                                            store.setDefault(id: method.id)
                                        }) {
                                            Text("Set as Default")
                                                .font(.system(size: 11, weight: .semibold))
                                                .foregroundColor(.evrryPrimaryBlue)
                                        }
                                    } else {
                                        Text("Primary for 1-Tap Checkout")
                                            .font(.system(size: 10))
                                            .foregroundColor(.evrryMutedText)
                                    }
                                }
                            }
                            .padding(14)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                            .overlay(
                                RoundedRectangle(cornerRadius: 16)
                                    .stroke(method.isDefault ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: method.isDefault ? 1.5 : 1)
                            )
                            .shadow(color: Color.black.opacity(0.02), radius: 4, x: 0, y: 2)
                        }
                    }

                    // Add Payment Method Button
                    Button(action: { showAddSheet = true }) {
                        HStack(spacing: 8) {
                            Image(systemName: "plus.circle.fill")
                                .font(.system(size: 16))
                                .foregroundColor(.evrryPrimaryBlue)
                            Text("Add Another Payment Method")
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.evrryPrimaryBlue)
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrryPrimaryBlue.opacity(0.3), lineWidth: 1))
                    }
                    .padding(.top, 4)
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 40)
            }
        }
        .background(Color(red: 0.98, green: 0.98, blue: 1.0).ignoresSafeArea())
        // Delete Confirmation Alert
        .alert(isPresented: $showDeleteConfirm) {
            Alert(
                title: Text("Remove Payment Method?"),
                message: Text("Are you sure you want to delete \(methodToDelete?.title ?? "this method") (\(methodToDelete?.subtitle ?? "")) from your saved payment methods?"),
                primaryButton: .destructive(Text("Delete")) {
                    if let method = methodToDelete {
                        store.delete(id: method.id)
                    }
                },
                secondaryButton: .cancel()
            )
        }
        // Add Sheet
        .sheet(isPresented: $showAddSheet) {
            VStack(spacing: 20) {
                Text("Add Payment Method")
                    .font(.system(size: 17, weight: .bold))
                    .padding(.top, 20)

                Picker("Type", selection: $selectedType) {
                    Text("eSewa").tag(SavedPaymentType.esewa)
                    Text("Khalti").tag(SavedPaymentType.khalti)
                    Text("Fonepay").tag(SavedPaymentType.fonepay)
                    Text("Card").tag(SavedPaymentType.card)
                }
                .pickerStyle(SegmentedPickerStyle())
                .padding(.horizontal, 20)

                VStack(alignment: .leading, spacing: 6) {
                    Text(selectedType == .card ? "Card Number (16 Digits)" : "Phone Number (+977)")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(.evrryMutedText)

                    TextField(selectedType == .card ? "4242 •••• •••• 4242" : "98XXXXXXXX", text: $inputAccount)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                }
                .padding(.horizontal, 20)

                Spacer()

                Button(action: {
                    let title = selectedType.rawValue
                    let subtitle = inputAccount.isEmpty ? "+977 9800000000" : inputAccount
                    let newMethod = SavedPaymentMethod(
                        type: selectedType,
                        title: title,
                        subtitle: subtitle,
                        isDefault: false,
                        linkedAt: "Verified"
                    )
                    store.add(method: newMethod)
                    inputAccount = ""
                    showAddSheet = false
                }) {
                    Text("Link & Save Method")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(Color.evrryPrimaryBlue)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 24)
            }
            .presentationDetents([.fraction(0.5)])
        }
    }
}
