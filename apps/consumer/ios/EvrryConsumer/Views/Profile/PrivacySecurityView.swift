//
//  PrivacySecurityView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Dedicated Privacy & Security Screen
//

import SwiftUI

public struct PrivacySecurityView: View {
    @ObservedObject public var authViewModel: AuthViewModel
    @Environment(\.presentationMode) var presentationMode
    public var onBack: (() -> Void)? = nil

    @State private var allowPhoneDiscovery: Bool = true
    @State private var readReceiptsEnabled: Bool = true
    @State private var showDeleteConfirm: Bool = false

    public init(authViewModel: AuthViewModel, onBack: (() -> Void)? = nil) {
        self.authViewModel = authViewModel
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
                    Text("Privacy & Security")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text("Signal-grade E2EE, phone privacy & account security")
                        .font(.system(size: 11))
                        .foregroundColor(.evrryMutedText)
                }

                Spacer()
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 14)
            .background(Color.white)

            ScrollView {
                VStack(spacing: 16) {
                    // E2EE Card
                    HStack(spacing: 12) {
                        Image(systemName: "checkmark.shield.fill")
                            .font(.system(size: 26))
                            .foregroundColor(Color(red: 0.06, green: 0.73, blue: 0.51))

                        VStack(alignment: .leading, spacing: 2) {
                            Text("Double Ratchet E2EE Active")
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Text("All peer chats, voice calls & media transfers are encrypted directly on your device. Evrry servers cannot read your messages.")
                                .font(.system(size: 11))
                                .foregroundColor(.evrryMutedText)
                        }
                    }
                    .padding(14)
                    .background(Color(red: 0.94, green: 0.98, blue: 0.95))
                    .clipShape(RoundedRectangle(cornerRadius: 16))
                    .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color(red: 0.8, green: 0.93, blue: 0.84), lineWidth: 1))
                    .padding(.top, 14)

                    // Preferences
                    VStack(spacing: 12) {
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Find by Phone Number")
                                    .font(.system(size: 13, weight: .semibold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Text("Let synced contacts locate you on Evrry Chat")
                                    .font(.system(size: 11))
                                    .foregroundColor(.evrryMutedText)
                            }
                            Spacer()
                            Toggle("", isOn: $allowPhoneDiscovery)
                                .labelsHidden()
                        }

                        Divider()

                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Read Receipts & Online Presence")
                                    .font(.system(size: 13, weight: .semibold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Text("Show blue ticks when you read incoming messages")
                                    .font(.system(size: 11))
                                    .foregroundColor(.evrryMutedText)
                            }
                            Spacer()
                            Toggle("", isOn: $readReceiptsEnabled)
                                .labelsHidden()
                        }
                    }
                    .padding(16)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 18))
                    .shadow(color: Color.black.opacity(0.02), radius: 4, x: 0, y: 2)

                    // Delete Account Section
                    VStack(alignment: .leading, spacing: 8) {
                        Text("DANGER ZONE")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(Color(red: 0.94, green: 0.27, blue: 0.27))
                            .tracking(0.5)

                        Button(action: { showDeleteConfirm = true }) {
                            HStack(spacing: 8) {
                                Image(systemName: "trash.fill")
                                    .font(.system(size: 14))
                                    .foregroundColor(Color(red: 0.94, green: 0.27, blue: 0.27))
                                Text("Schedule Account Deletion (30 Days)")
                                    .font(.system(size: 13, weight: .bold))
                                    .foregroundColor(Color(red: 0.94, green: 0.27, blue: 0.27))
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 48)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 14))
                            .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color(red: 0.99, green: 0.79, blue: 0.79), lineWidth: 1))
                        }

                        Text("Includes a 30-day industry-standard grace period during which you can log back in at any time to cancel deletion.")
                            .font(.system(size: 10))
                            .foregroundColor(.evrryMutedText)
                            .padding(.horizontal, 4)
                    }
                    .padding(.top, 8)
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 40)
            }
        }
        .background(Color(red: 0.98, green: 0.98, blue: 1.0).ignoresSafeArea())
        .alert(isPresented: $showDeleteConfirm) {
            Alert(
                title: Text("Schedule Account Deletion?"),
                message: Text("Your account will be deactivated and permanently deleted after 30 days. You can sign in anytime before then to cancel."),
                primaryButton: .destructive(Text("Schedule Deletion")) {
                    authViewModel.scheduleAccountDeletion()
                },
                secondaryButton: .cancel()
            )
        }
    }
}
