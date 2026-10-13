//
//  ProfileView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Profile Screen matching prototype/Phone/app/profile/page.tsx
//  - Tier gradient header banner with Avatar, Name, Username, QR button & Tier badge
//  - Grouped NavRow sections: Account, Vouchers & Referrals, Rewards & Loyalty, Activity,
//    Address Book, AI & Voice Settings, Security & Privacy, Support, Sign Out
//  - WeChat-style Personal QR Code Modal
//

import SwiftUI

public struct ProfileView: View {
    @ObservedObject public var authViewModel: AuthViewModel
    @ObservedObject public var loyaltyViewModel: LoyaltyViewModel
    public let onNavigateToActivity: () -> Void
    public let onNavigateToAiSettings: () -> Void
    public let onNavigateToAddressBook: () -> Void
    public let onNavigateToMemories: () -> Void
    public let onNavigateToCamera: () -> Void
    public let onLogout: () -> Void

    @State private var openSection: String? = nil
    @State private var showQrModal: Bool = false
    @State private var showEditAccountSheet: Bool = false
    @State private var showPaymentMethodsSheet: Bool = false
    @State private var showVouchersSheet: Bool = false
    @State private var showRewardsSheet: Bool = false
    @State private var showPrivacySheet: Bool = false
    @State private var showLogoutAlert: Bool = false
    @State private var showDeleteAlert: Bool = false
    @State private var allowPhoneDiscovery: Bool = false

    public init(
        authViewModel: AuthViewModel,
        loyaltyViewModel: LoyaltyViewModel = LoyaltyViewModel(),
        onNavigateToActivity: @escaping () -> Void = {},
        onNavigateToAiSettings: @escaping () -> Void = {},
        onNavigateToAddressBook: @escaping () -> Void = {},
        onNavigateToMemories: @escaping () -> Void = {},
        onNavigateToCamera: @escaping () -> Void = {},
        onLogout: @escaping () -> Void = {}
    ) {
        self.authViewModel = authViewModel
        self.loyaltyViewModel = loyaltyViewModel
        self.onNavigateToActivity = onNavigateToActivity
        self.onNavigateToAiSettings = onNavigateToAiSettings
        self.onNavigateToAddressBook = onNavigateToAddressBook
        self.onNavigateToMemories = onNavigateToMemories
        self.onNavigateToCamera = onNavigateToCamera
        self.onLogout = onLogout
    }

    private var userProfile: UserProfile {
        authViewModel.userProfile
    }

    private var loyaltyState: UserLoyaltyState {
        loyaltyViewModel.loyaltyState
    }

    private var vouchers: [VoucherItem] {
        loyaltyViewModel.vouchers
    }

    private var referralInfo: ReferralInfo {
        loyaltyViewModel.referralInfo
    }

    private func toggleSection(_ id: String) {
        if openSection == id {
            openSection = nil
        } else {
            openSection = id
        }
    }

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 14) {
                // 1. PAGE TITLE (Matching prototype "YOU / Profile")
                VStack(alignment: .leading, spacing: 2) {
                    Text("YOU")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.evrryPrimaryBlue)
                        .tracking(1.0)
                    Text("Profile")
                        .font(.system(size: 26, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                }
                .padding(.top, 16)

                // 2. TIER-COLORED GRADIENT HEADER BANNER
                ZStack {
                    LinearGradient(
                        colors: [Color(red: 0.12, green: 0.23, blue: 0.54), Color(red: 0.23, green: 0.51, blue: 0.96)],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )

                    HStack(spacing: 14) {
                        // Avatar Circle
                        ZStack {
                            Circle()
                                .fill(Color.white.opacity(0.25))
                                .frame(width: 54, height: 54)
                            Text(String(userProfile.fullName.prefix(1)).uppercased())
                                .font(.system(size: 22, weight: .bold))
                                .foregroundColor(.white)
                        }

                        // Name & Username
                        VStack(alignment: .leading, spacing: 2) {
                            HStack(spacing: 6) {
                                Text(userProfile.fullName)
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(.white)
                                if userProfile.kycVerified {
                                    Image(systemName: "checkmark.seal.fill")
                                        .font(.system(size: 14))
                                        .foregroundColor(Color(red: 0.58, green: 0.88, blue: 0.70))
                                }
                            }
                            Text("@\(userProfile.username.isEmpty ? "rahul_evrry" : userProfile.username)")
                                .font(.system(size: 12))
                                .foregroundColor(.white.opacity(0.85))
                        }

                        Spacer()

                        // QR Code Button + Tier Badge
                        HStack(spacing: 8) {
                            Button(action: { showQrModal = true }) {
                                ZStack {
                                    Circle()
                                        .fill(Color.white.opacity(0.22))
                                        .frame(width: 36, height: 36)
                                    Image(systemName: "qrcode")
                                        .font(.system(size: 17, weight: .semibold))
                                        .foregroundColor(.white)
                                }
                            }

                            Text(loyaltyState.league.label)
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.white)
                                .padding(.horizontal, 10)
                                .padding(.vertical, 5)
                                .background(Color.white.opacity(0.25))
                                .clipShape(RoundedRectangle(cornerRadius: 12))
                        }
                    }
                    .padding(18)
                }
                .clipShape(RoundedRectangle(cornerRadius: 22))
                .shadow(color: Color.black.opacity(0.06), radius: 6, x: 0, y: 2)

                // 3. GROUPED NAVIGATION ROWS (Dedicated Screen Navigation)

                // 1. Account Details
                ProfileNavRow(
                    icon: "person.fill",
                    title: "Account Details",
                    subtitle: "@\(userProfile.username.isEmpty ? "rahul_evrry" : userProfile.username) • \(userProfile.phone)",
                    onClick: { showEditAccountSheet = true }
                )

                // 2. Payment Methods (eSewa, Khalti, Fonepay, Cards)
                ProfileNavRow(
                    icon: "creditcard.fill",
                    title: "Payment Methods",
                    subtitle: "Manage saved eSewa, Khalti, Fonepay & cards",
                    onClick: { showPaymentMethodsSheet = true }
                )

                // 3. Vouchers & Referrals
                ProfileNavRow(
                    icon: "ticket.fill",
                    title: "Vouchers & Referrals",
                    subtitle: "\(vouchers.count) active vouchers • Code: \(referralInfo.referralCode)",
                    onClick: { showVouchersSheet = true }
                )

                // 4. Rewards & Loyalty
                ProfileNavRow(
                    icon: "trophy.fill",
                    title: "Rewards & Loyalty",
                    subtitle: "\(userProfile.loyaltyPoints} points • Level: \(loyaltyState.league.label)",
                    onClick: { showRewardsSheet = true }
                )

                // 5. Activity (Orders, Bookings & Rides)
                ProfileNavRow(
                    icon: "clock.arrow.circlepath",
                    title: "Activity",
                    subtitle: "Food orders, grocery dispatches & rides",
                    onClick: onNavigateToActivity
                )

                // 6. Address Book
                ProfileNavRow(
                    icon: "house.fill",
                    title: "Address Book",
                    subtitle: "Saved delivery locations (Home, Office)",
                    onClick: onNavigateToAddressBook
                )

                // 7. Snap Memories & Locker
                ProfileNavRow(
                    icon: "camera.filters",
                    title: "Snap Memories & My Locker 🔒",
                    subtitle: "Saved snaps, stories & PIN-protected vault",
                    onClick: onNavigateToMemories
                )

                // 8. AI & Voice Settings
                ProfileNavRow(
                    icon: "sparkles",
                    title: "AI & Voice Settings",
                    subtitle: "4 Persona Models (Ellie, Rony, Jenny, Suka)",
                    onClick: onNavigateToAiSettings
                )

                // 9. Privacy & Security
                ProfileNavRow(
                    icon: "lock.shield.fill",
                    title: "Privacy & Security",
                    subtitle: "Signal-grade Double Ratchet E2EE",
                    onClick: { showPrivacySheet = true }
                )

                // Sign Out Button
                Button(action: { showLogoutAlert = true }) {
                    HStack(spacing: 8) {
                        Image(systemName: "rectangle.portrait.and.arrow.right")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(Color(red: 0.94, green: 0.27, blue: 0.27))
                        Text("Sign Out of Evrry")
                            .font(.system(size: 13, weight: .bold))
                            .foregroundColor(Color(red: 0.94, green: 0.27, blue: 0.27))
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 16))
                    .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color(red: 0.99, green: 0.79, blue: 0.79), lineWidth: 1))
                    .shadow(color: Color.black.opacity(0.02), radius: 3, x: 0, y: 1)
                }
                .padding(.top, 4)

                // Schedule Account Deletion Button (30 Days Grace Period)
                Button(action: { showDeleteAlert = true }) {
                    HStack(spacing: 6) {
                        Image(systemName: "trash")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)
                        Text("Schedule Account Deletion (30 Days)")
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(.evrryMutedText)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 8)
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 100)
        }
        .background(Color(red: 0.98, green: 0.98, blue: 1.0).ignoresSafeArea())
        // WECHAT-STYLE PERSONAL QR CODE SHEET
        .sheet(isPresented: $showQrModal) {
            VStack(spacing: 18) {
                Text("Personal QR Code")
                    .font(.system(size: 17, weight: .bold))
                    .foregroundColor(.evrryDarkCharcoal)
                    .padding(.top, 24)

                // Simulated QR Box
                ZStack {
                    RoundedRectangle(cornerRadius: 16)
                        .fill(Color(red: 0.97, green: 0.98, blue: 0.99))
                        .frame(width: 200, height: 200)
                        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))

                    Image(systemName: "qrcode")
                        .font(.system(size: 130))
                        .foregroundColor(.evrryDarkCharcoal)
                }

                VStack(spacing: 4) {
                    Text(userProfile.fullName)
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text("@\(userProfile.username.isEmpty ? "rahul_evrry" : userProfile.username)")
                        .font(.system(size: 12))
                        .foregroundColor(.evrryMutedText)
                    Text("Scan this code with Evrry camera to start chat or send money.")
                        .font(.system(size: 11))
                        .foregroundColor(.evrryMutedText)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 24)
                        .padding(.top, 4)
                }

                Spacer()

                HStack(spacing: 12) {
                    Button(action: {
                        showQrModal = false
                        onNavigateToCamera()
                    }) {
                        HStack(spacing: 6) {
                            Image(systemName: "qrcode.viewfinder")
                                .font(.system(size: 15, weight: .bold))
                            Text("Scan QR")
                                .font(.system(size: 14, weight: .bold))
                        }
                        .foregroundColor(.evrryPrimaryBlue)
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(Color.evrryPrimaryBlue.opacity(0.1))
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                    }

                    Button(action: { showQrModal = false }) {
                        Text("Done")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 48)
                            .background(Color.evrryPrimaryBlue)
                            .clipShape(RoundedRectangle(cornerRadius: 14))
                    }
                }
                .padding(.horizontal, 24)
                .padding(.bottom, 24)
            }
            .presentationDetents([.fraction(0.65)])
        }
        // EDIT ACCOUNT SHEET
        .sheet(isPresented: $showEditAccountSheet) {
            VStack(spacing: 16) {
                Text("Account Details")
                    .font(.system(size: 17, weight: .bold))
                    .padding(.top, 20)

                VStack(alignment: .leading, spacing: 12) {
                    HStack {
                        Text("Full Name")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.evrryMutedText)
                        Spacer()
                        Text(userProfile.fullName)
                            .font(.system(size: 14, weight: .bold))
                    }
                    Divider()
                    HStack {
                        Text("Username")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.evrryMutedText)
                        Spacer()
                        Text("@\(userProfile.username)")
                            .font(.system(size: 14, weight: .bold))
                    }
                    Divider()
                    HStack {
                        Text("Phone Number")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.evrryMutedText)
                        Spacer()
                        Text(userProfile.phone)
                            .font(.system(size: 14, weight: .bold))
                    }
                    Divider()
                    HStack {
                        Text("KYC Verification")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.evrryMutedText)
                        Spacer()
                        Text(userProfile.kycVerified ? "Verified (Nagarik App)" : "Pending")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(userProfile.kycVerified ? Color(red: 0.06, green: 0.73, blue: 0.51) : .orange)
                    }
                }
                .padding(16)
                .background(Color.white)
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                .padding(.horizontal, 20)

                Spacer()

                Button(action: { showEditAccountSheet = false }) {
                    Text("Close")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(Color.evrryPrimaryBlue)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 20)
            }
            .presentationDetents([.fraction(0.5)])
        }
        // PAYMENT METHODS DEDICATED SHEET
        .sheet(isPresented: $showPaymentMethodsSheet) {
            PaymentMethodsView()
        }
        // VOUCHERS & REFERRALS DEDICATED SHEET
        .sheet(isPresented: $showVouchersSheet) {
            VouchersReferralsView(loyaltyViewModel: loyaltyViewModel)
        }
        // REWARDS & LOYALTY DEDICATED SHEET
        .sheet(isPresented: $showRewardsSheet) {
            LoyaltyRewardsView(loyaltyViewModel: loyaltyViewModel)
        }
        // PRIVACY & SECURITY DEDICATED SHEET
        .sheet(isPresented: $showPrivacySheet) {
            PrivacySecurityView(authViewModel: authViewModel)
        }
        // LOGOUT ALERT
        .alert("Sign Out", isPresented: $showLogoutAlert) {
            Button("Cancel", role: .cancel) {}
            Button("Sign Out", role: .destructive) {
                onLogout()
            }
        } message: {
            Text("Are you sure you want to sign out of your Evrry account?")
        }
        // ACCOUNT DELETION ALERT (30-DAY GRACE PERIOD COMPLIANCE)
        .alert("Schedule Account Deletion", isPresented: $showDeleteAlert) {
            Button("Cancel", role: .cancel) {}
            Button("Schedule Deletion", role: .destructive) {
                authViewModel.deleteAccount()
            }
        } message: {
            Text("Per the Nepal Privacy Act 2075 and App Store data protection standards, your account will enter a 30-day cooling-off grace period. Logging in anytime within 30 days immediately restores your account. After 30 days, your identity, ride history, and encrypted keys will be permanently expunged.")
        }
    }
}

// Subcomponents for Profile View
private struct ProfileNavRow: View {
    let icon: String
    let title: String
    let subtitle: String
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            HStack(spacing: 14) {
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color(red: 0.94, green: 0.96, blue: 1.0))
                        .frame(width: 40, height: 40)
                    Image(systemName: icon)
                        .font(.system(size: 16))
                        .foregroundColor(.evrryPrimaryBlue)
                }

                VStack(alignment: .leading, spacing: 2) {
                    Text(title)
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text(subtitle)
                        .font(.system(size: 11))
                        .foregroundColor(.evrryMutedText)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.system(size: 12, weight: .semibold))
                    .foregroundColor(.evrryMutedText)
            }
            .padding(14)
            .background(Color.white)
            .clipShape(RoundedRectangle(cornerRadius: 16))
            .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
            .shadow(color: Color.black.opacity(0.02), radius: 3, x: 0, y: 1)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

private struct ProfileExpandableRow<Content: View>: View {
    let icon: String
    let title: String
    let subtitle: String
    let isOpen: Bool
    let onToggle: () -> Void
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(spacing: 0) {
            Button(action: onToggle) {
                HStack(spacing: 14) {
                    ZStack {
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color(red: 0.94, green: 0.96, blue: 1.0))
                            .frame(width: 40, height: 40)
                        Image(systemName: icon)
                            .font(.system(size: 16))
                            .foregroundColor(.evrryPrimaryBlue)
                    }

                    VStack(alignment: .leading, spacing: 2) {
                        Text(title)
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text(subtitle)
                            .font(.system(size: 11))
                            .foregroundColor(.evrryMutedText)
                    }

                    Spacer()

                    Image(systemName: isOpen ? "chevron.up" : "chevron.down")
                        .font(.system(size: 12, weight: .semibold))
                        .foregroundColor(.evrryMutedText)
                }
                .padding(14)
            }
            .buttonStyle(PlainButtonStyle())

            if isOpen {
                Divider()
                    .foregroundColor(.evrrySubtleBorder)
                content()
            }
        }
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
        .shadow(color: Color.black.opacity(0.02), radius: 3, x: 0, y: 1)
    }
}
