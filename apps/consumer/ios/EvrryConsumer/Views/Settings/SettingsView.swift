//
//  SettingsView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Settings, Profile Management, WeChat-Style QR Code, Loyalty Leagues,
//  Vouchers Hub, Google Drive/iCloud Chat Backup & Privacy View for iOS
//

import SwiftUI

public struct SettingsView: View {
    public let activeModel: VoiceAgentModel
    @ObservedObject public var authViewModel: AuthViewModel
    @ObservedObject public var loyaltyViewModel: LoyaltyViewModel
    public let onSelectModel: (VoiceAgentModel) -> Void
    public let onBack: () -> Void
    public let onLogout: () -> Void

    @State private var selectedTab: Int = 0 // 0=Profile & League, 1=Vouchers & Stamps, 2=Backup & Privacy, 3=Support & More
    @State private var showEditProfileSheet: Bool = false
    @State private var showQrSheet: Bool = false
    @State private var showDeleteAccountAlert: Bool = false
    @State private var showTermsSheet: Bool = false
    @State private var showPrivacySheet: Bool = false
    @State private var showAboutSheet: Bool = false
    @State private var showAddressBookSheet: Bool = false
    @State private var cacheNoticeText: String? = nil

    @State private var promoInputText: String = ""
    @State private var promoFeedbackMessage: String? = nil

    public init(
        activeModel: VoiceAgentModel,
        authViewModel: AuthViewModel,
        loyaltyViewModel: LoyaltyViewModel = LoyaltyViewModel(),
        onSelectModel: @escaping (VoiceAgentModel) -> Void,
        onBack: @escaping () -> Void,
        onLogout: @escaping () -> Void
    ) {
        self.activeModel = activeModel
        self.authViewModel = authViewModel
        self.loyaltyViewModel = loyaltyViewModel
        self.onSelectModel = onSelectModel
        self.onBack = onBack
        self.onLogout = onLogout
    }

    private var userProfile: UserProfile {
        authViewModel.userProfile
    }

    public var body: some View {
        VStack(spacing: 0) {
            // TOP BAR WITH >= 44x44 pt BACK BUTTON (iOS Human Interface Guidelines)
            HStack {
                Button(action: onBack) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                        .frame(width: 44, height: 44)
                }

                Spacer()

                Text("Profile & Settings")
                    .font(.system(size: 17, weight: .bold))
                    .foregroundColor(.evrryDarkCharcoal)

                Spacer()

                Color.clear.frame(width: 44, height: 44)
            }
            .padding(.horizontal, 8)

            // SEGMENTED TAB SELECTOR (Minimal, clean)
            Picker("Tabs", selection: $selectedTab) {
                Text("Profile").tag(0)
                Text("Vouchers").tag(1)
                Text("Backup").tag(2)
                Text("Support").tag(3)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)

            ScrollView {
                VStack(spacing: 16) {
                    // =========================================================
                    // TAB 0: PROFILE & GAMIFIED LEAGUES
                    // =========================================================
                    if selectedTab == 0 {
                        // 1. USER PROFILE CARD (WeChat & WhatsApp Style)
                        VStack(alignment: .leading, spacing: 14) {
                            HStack(spacing: 14) {
                                ZStack(alignment: .bottomTrailing) {
                                    Circle()
                                        .fill(Color.evrryPrimaryBlue.opacity(0.12))
                                        .frame(width: 62, height: 62)
                                        .overlay(
                                            Text(String(userProfile.fullName.prefix(2)).uppercased())
                                                .font(.system(size: 20, weight: .bold))
                                                .foregroundColor(.evrryPrimaryBlue)
                                        )

                                    Circle()
                                        .fill(Color.evrryPrimaryBlue)
                                        .frame(width: 22, height: 22)
                                        .overlay(
                                            Image(systemName: "camera.fill")
                                                .font(.system(size: 10))
                                                .foregroundColor(.white)
                                        )
                                        .overlay(Circle().stroke(Color.white, lineWidth: 1.5))
                                }

                                VStack(alignment: .leading, spacing: 3) {
                                    HStack(spacing: 6) {
                                        Text(userProfile.fullName)
                                            .font(.system(size: 17, weight: .bold))
                                            .foregroundColor(.evrryDarkCharcoal)

                                        Text("KYC")
                                            .font(.system(size: 9, weight: .bold))
                                            .foregroundColor(.evrryGreenSuccess)
                                            .padding(.horizontal, 5)
                                            .padding(.vertical, 2)
                                            .background(Color.green.opacity(0.15))
                                            .cornerRadius(4)
                                    }

                                    Text("@\(userProfile.username)")
                                        .font(.system(size: 13, weight: .semibold))
                                        .foregroundColor(.evrryPrimaryBlue)

                                    Text(userProfile.phone)
                                        .font(.system(size: 12))
                                        .foregroundColor(.evrryMutedText)
                                }

                                Spacer()
                            }

                            // BIO STATUS
                            HStack {
                                Text("About: \(userProfile.bioStatus)")
                                    .font(.system(size: 12))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Spacer()
                            }
                            .padding(.horizontal, 12)
                            .padding(.vertical, 8)
                            .background(Color.evrrySubtleSurface)
                            .cornerRadius(10)

                            // ACTION BUTTONS
                            HStack(spacing: 10) {
                                Button(action: { showEditProfileSheet = true }) {
                                    HStack(spacing: 6) {
                                        Image(systemName: "pencil")
                                        Text("Edit Profile").font(.system(size: 13, weight: .bold))
                                    }
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 10)
                                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrryPrimaryBlue, lineWidth: 1))
                                    .foregroundColor(.evrryPrimaryBlue)
                                }

                                Button(action: { showQrSheet = true }) {
                                    HStack(spacing: 6) {
                                        Image(systemName: "qrcode")
                                        Text("My QR Code").font(.system(size: 13, weight: .bold))
                                    }
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 10)
                                    .background(Color.evrryPrimaryBlue)
                                    .cornerRadius(12)
                                    .foregroundColor(.white)
                                }
                            }
                        }
                        .padding(18)
                        .background(Color.white)
                        .cornerRadius(20)
                        .shadow(color: Color.black.opacity(0.04), radius: 6, y: 2)

                        // 2. GAMIFIED LEAGUE & REWARDS CARD
                        let currentLeague = loyaltyViewModel.loyaltyState.league
                        let nextLeague = loyaltyViewModel.loyaltyState.nextLeague

                        VStack(alignment: .leading, spacing: 14) {
                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    HStack(spacing: 6) {
                                        Text("LEAGUE")
                                            .font(.system(size: 9, weight: .bold))
                                            .padding(.horizontal, 6)
                                            .padding(.vertical, 2)
                                            .background(Color.evrryPrimaryBlue.opacity(0.12))
                                            .foregroundColor(.evrryPrimaryBlue)
                                            .cornerRadius(6)

                                        Text(currentLeague.label)
                                            .font(.system(size: 20, weight: .heavy))
                                            .foregroundColor(.evrryDarkCharcoal)
                                    }

                                    Text("\(loyaltyViewModel.loyaltyState.tierPoints) Tier Points (TP)")
                                        .font(.system(size: 12))
                                        .foregroundColor(.evrryMutedText)
                                }

                                Spacer()

                                Text("\(loyaltyViewModel.loyaltyState.eCoinsBalance) E-Coins")
                                    .font(.system(size: 13, weight: .bold))
                                    .foregroundColor(.evrryPrimaryBlue)
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 6)
                                    .background(Color.blue.opacity(0.1))
                                    .cornerRadius(10)
                            }

                            // PROGRESS BAR
                            if let next = nextLeague {
                                VStack(spacing: 4) {
                                    HStack {
                                        Text(currentLeague.label).font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                        Spacer()
                                        Text("\(loyaltyViewModel.loyaltyState.pointsToNext) TP to \(next.label)")
                                            .font(.system(size: 11, weight: .bold))
                                            .foregroundColor(.evrryPrimaryBlue)
                                    }
                                    ProgressView(value: loyaltyViewModel.loyaltyState.progressToNext)
                                        .tint(.evrryPrimaryBlue)
                                }
                            } else {
                                Text("🏆 You have reached the top Diamond League!")
                                    .font(.system(size: 12, weight: .bold))
                                    .foregroundColor(.evrryPrimaryBlue)
                            }

                            // PERKS SUMMARY
                            HStack(spacing: 8) {
                                Image(systemName: "star.fill")
                                    .foregroundColor(.orange)
                                    .font(.system(size: 14))
                                Text(currentLeague.perkDescription)
                                    .font(.system(size: 11))
                                    .foregroundColor(.evrryDarkCharcoal)
                            }
                            .padding(10)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(Color.evrrySubtleSurface)
                            .cornerRadius(10)

                            Divider()

                            // DAILY CHECK-IN STREAK
                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    HStack(spacing: 4) {
                                        Text("🔥")
                                        Text("\(loyaltyViewModel.loyaltyState.consecutiveCheckinDays)-Day Streak")
                                            .font(.system(size: 14, weight: .bold))
                                            .foregroundColor(.evrryDarkCharcoal)
                                    }
                                    Text(loyaltyViewModel.loyaltyState.hasCheckedInToday ? "Claimed today!" : "Claim daily E-Coins bonus")
                                        .font(.system(size: 11))
                                        .foregroundColor(.evrryMutedText)
                                }

                                Spacer()

                                Button(action: { _ = loyaltyViewModel.claimDailyCheckin() }) {
                                    Text(loyaltyViewModel.loyaltyState.hasCheckedInToday ? "Claimed ✓" : "Claim Coins")
                                        .font(.system(size: 12, weight: .bold))
                                        .padding(.horizontal, 14)
                                        .padding(.vertical, 8)
                                        .background(loyaltyViewModel.loyaltyState.hasCheckedInToday ? Color.gray.opacity(0.3) : Color.green)
                                        .foregroundColor(.white)
                                        .cornerRadius(10)
                                }
                                .disabled(loyaltyViewModel.loyaltyState.hasCheckedInToday)
                            }
                        }
                        .padding(18)
                        .background(Color.white)
                        .cornerRadius(20)
                        .shadow(color: Color.black.opacity(0.04), radius: 6, y: 2)

                        // 3. SAVED PLACES & ADDRESS BOOK
                        Button(action: { showAddressBookSheet = true }) {
                            HStack(spacing: 12) {
                                ZStack {
                                    Circle()
                                        .fill(Color.evrryPrimaryBlue.opacity(0.1))
                                        .frame(width: 40, height: 40)
                                    Image(systemName: "mappin.and.ellipse")
                                        .foregroundColor(.evrryPrimaryBlue)
                                        .font(.system(size: 18))
                                }
                                
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Saved Places & Address Book")
                                        .font(.system(size: 15, weight: .bold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                    Text("Home, Work & custom locations for fast ride booking")
                                        .font(.system(size: 11))
                                        .foregroundColor(.evrryMutedText)
                                }
                                
                                Spacer()
                                
                                Image(systemName: "chevron.right")
                                    .font(.system(size: 14, weight: .semibold))
                                    .foregroundColor(.evrryMutedText)
                            }
                            .padding(18)
                            .background(Color.white)
                            .cornerRadius(20)
                            .shadow(color: Color.black.opacity(0.04), radius: 6, y: 2)
                        }
                        .buttonStyle(PlainButtonStyle())
                    }

                    // =========================================================
                    // TAB 1: VOUCHERS, STAMPS & REFERRALS
                    // =========================================================
                    if selectedTab == 1 {
                        // 1. REFER & EARN (WhatsApp Viral Loop)
                        VStack(alignment: .leading, spacing: 14) {
                            HStack(spacing: 10) {
                                Circle()
                                    .fill(Color.orange.opacity(0.15))
                                    .frame(width: 36, height: 36)
                                    .overlay(Image(systemName: "gift.fill").foregroundColor(.orange))

                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Refer & Earn NPR 150").font(.system(size: 15, weight: .bold)).foregroundColor(.evrryDarkCharcoal)
                                    Text("Friend gets NPR 100 on signup; you get NPR 150").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                }
                            }

                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("YOUR REFERRAL CODE").font(.system(size: 9, weight: .bold)).foregroundColor(.evrryMutedText)
                                    Text(loyaltyViewModel.referralInfo.referralCode).font(.system(size: 15, weight: .heavy)).foregroundColor(.evrryDarkCharcoal)
                                }

                                Spacer()

                                Button(action: { /* Share WhatsApp */ }) {
                                    HStack(spacing: 4) {
                                        Image(systemName: "square.and.arrow.up")
                                        Text("Share WhatsApp").font(.system(size: 11, weight: .bold))
                                    }
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 8)
                                    .background(Color.green)
                                    .foregroundColor(.white)
                                    .cornerRadius(10)
                                }
                            }
                            .padding(12)
                            .background(Color.evrrySubtleSurface)
                            .cornerRadius(12)

                            HStack {
                                Text("Invited: \(loyaltyViewModel.referralInfo.friendsInvitedCount) friends").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                Spacer()
                                Text("Earned: NPR \(loyaltyViewModel.referralInfo.totalEarnedNpr)").font(.system(size: 11, weight: .bold)).foregroundColor(.green)
                            }
                        }
                        .padding(18)
                        .background(Color.white)
                        .cornerRadius(20)
                        .shadow(color: Color.black.opacity(0.04), radius: 6, y: 2)

                        // 2. DIGITAL STAMP CARD (10 SLOTS)
                        VStack(alignment: .leading, spacing: 12) {
                            HStack {
                                Text("Digital Stamp Card").font(.system(size: 15, weight: .bold)).foregroundColor(.evrryDarkCharcoal)
                                Spacer()
                                Text("\(loyaltyViewModel.stampCard.currentStamps)/10 Stamps").font(.system(size: 12, weight: .bold)).foregroundColor(.evrryPrimaryBlue)
                            }

                            Text("1 stamp per NPR 500 spent. Collect 10 to unlock Flat NPR 500 Rebate Voucher!")
                                .font(.system(size: 11))
                                .foregroundColor(.evrryMutedText)

                            // 10 SLOTS GRID (5x2)
                            HStack(spacing: 6) {
                                ForEach(1...5, id: \.self) { i in
                                    let isStamped = i <= loyaltyViewModel.stampCard.currentStamps
                                    RoundedRectangle(cornerRadius: 8)
                                        .fill(isStamped ? Color.evrryPrimaryBlue.opacity(0.12) : Color.evrrySubtleSurface)
                                        .frame(height: 38)
                                        .overlay(Text(isStamped ? "✓" : "\(i)").font(.system(size: 12, weight: .bold)).foregroundColor(isStamped ? .evrryPrimaryBlue : .evrryMutedText))
                                        .overlay(RoundedRectangle(cornerRadius: 8).stroke(isStamped ? Color.evrryPrimaryBlue : Color.clear, lineWidth: 1))
                                }
                            }

                            HStack(spacing: 6) {
                                ForEach(6...10, id: \.self) { i in
                                    let isStamped = i <= loyaltyViewModel.stampCard.currentStamps
                                    RoundedRectangle(cornerRadius: 8)
                                        .fill(isStamped ? Color.evrryPrimaryBlue.opacity(0.12) : Color.evrrySubtleSurface)
                                        .frame(height: 38)
                                        .overlay(Text(isStamped ? "✓" : "\(i)").font(.system(size: 12, weight: .bold)).foregroundColor(isStamped ? .evrryPrimaryBlue : .evrryMutedText))
                                        .overlay(RoundedRectangle(cornerRadius: 8).stroke(isStamped ? Color.evrryPrimaryBlue : Color.clear, lineWidth: 1))
                                }
                            }
                        }
                        .padding(18)
                        .background(Color.white)
                        .cornerRadius(20)
                        .shadow(color: Color.black.opacity(0.04), radius: 6, y: 2)

                        // 3. REDEEM PROMO CODE
                        VStack(alignment: .leading, spacing: 8) {
                            Text("REDEEM PROMO CODE").font(.system(size: 11, weight: .bold)).foregroundColor(.evrryMutedText)

                            HStack(spacing: 8) {
                                TextField("e.g. DASHAIN or EVRRY50", text: $promoInputText)
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 10)
                                    .background(Color.evrrySubtleSurface)
                                    .cornerRadius(10)

                                Button(action: {
                                    let res = loyaltyViewModel.redeemPromoCode(promoInputText)
                                    promoFeedbackMessage = res.message
                                    if res.success { promoInputText = "" }
                                }) {
                                    Text("Apply")
                                        .font(.system(size: 13, weight: .bold))
                                        .padding(.horizontal, 16)
                                        .padding(.vertical, 10)
                                        .background(Color.evrryPrimaryBlue)
                                        .foregroundColor(.white)
                                        .cornerRadius(10)
                                }
                            }

                            if let msg = promoFeedbackMessage {
                                Text(msg)
                                    .font(.system(size: 11))
                                    .foregroundColor(msg.contains("successfully") ? .green : .red)
                            }
                        }
                        .padding(16)
                        .background(Color.white)
                        .cornerRadius(18)
                        .shadow(color: Color.black.opacity(0.04), radius: 4, y: 2)

                        // 4. ACTIVE VOUCHERS LIST
                        VStack(alignment: .leading, spacing: 10) {
                            Text("ACTIVE VOUCHERS (\(loyaltyViewModel.vouchers.count))")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)

                            ForEach(loyaltyViewModel.vouchers) { voucher in
                                HStack(spacing: 12) {
                                    RoundedRectangle(cornerRadius: 10)
                                        .fill(Color.evrryPrimaryBlue.opacity(0.1))
                                        .frame(width: 44, height: 44)
                                        .overlay(Image(systemName: "ticket.fill").foregroundColor(.evrryPrimaryBlue))

                                    VStack(alignment: .leading, spacing: 2) {
                                        HStack {
                                            Text(voucher.discountText).font(.system(size: 14, weight: .bold)).foregroundColor(.evrryPrimaryBlue)
                                            Text(voucher.code).font(.system(size: 10, weight: .bold)).padding(.horizontal, 4).padding(.vertical, 2).background(Color.gray.opacity(0.15)).cornerRadius(4)
                                        }
                                        Text(voucher.description).font(.system(size: 11)).foregroundColor(.evrryMutedText).lineLimit(1)
                                        Text(voucher.expiryDaysText).font(.system(size: 10, weight: .medium)).foregroundColor(.orange)
                                    }
                                    Spacer()
                                }
                                .padding(14)
                                .background(Color.white)
                                .cornerRadius(16)
                                .shadow(color: Color.black.opacity(0.03), radius: 4, y: 2)
                            }
                        }
                    }

                    // =========================================================
                    // TAB 2: BACKUP, PRIVACY & SECURITY
                    // =========================================================
                    if selectedTab == 2 {
                        // 1. CHAT BACKUP CARD (iCloud & Google Drive)
                        VStack(alignment: .leading, spacing: 14) {
                            HStack(spacing: 12) {
                                Circle()
                                    .fill(Color.blue.opacity(0.1))
                                    .frame(width: 42, height: 42)
                                    .overlay(Image(systemName: "icloud.and.arrow.up.fill").foregroundColor(.evrryPrimaryBlue))

                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Chat Backup").font(.system(size: 16, weight: .bold)).foregroundColor(.evrryDarkCharcoal)
                                    Text("End-to-End Encrypted Cloud Storage").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                }
                            }

                            Text("Back up your messages, photos, voice notes and documents. You can restore them when you reinstall Evrry.")
                                .font(.system(size: 11))
                                .foregroundColor(.evrryMutedText)

                            VStack(spacing: 6) {
                                HStack {
                                    Text("Last Backup:").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                    Spacer()
                                    Text(authViewModel.chatBackupState.lastBackupTimestamp).font(.system(size: 11, weight: .bold))
                                }
                                HStack {
                                    Text("Total Size:").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                    Spacer()
                                    Text("\(String(format: "%.1f", authViewModel.chatBackupState.totalSizeMb)) MB").font(.system(size: 11, weight: .bold))
                                }
                                HStack {
                                    Text("Account:").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                    Spacer()
                                    Text(authViewModel.chatBackupState.cloudAccount).font(.system(size: 11, weight: .medium)).foregroundColor(.evrryPrimaryBlue)
                                }
                            }
                            .padding(12)
                            .background(Color.evrrySubtleSurface)
                            .cornerRadius(12)

                            if authViewModel.chatBackupState.isBackingUp {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("Backing up... \(authViewModel.chatBackupState.backupProgressPercent)%")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(.evrryPrimaryBlue)
                                    ProgressView(value: Double(authViewModel.chatBackupState.backupProgressPercent) / 100.0)
                                        .tint(.evrryPrimaryBlue)
                                }
                            } else {
                                Button(action: { authViewModel.startChatBackup() }) {
                                    HStack {
                                        Image(systemName: "arrow.clockwise")
                                        Text("Back Up Now").font(.system(size: 14, weight: .bold))
                                    }
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 12)
                                    .background(Color.evrryPrimaryBlue)
                                    .foregroundColor(.white)
                                    .cornerRadius(12)
                                }
                            }

                            Divider()

                            Toggle(isOn: Binding(
                                get: { authViewModel.chatBackupState.includeVideos },
                                set: { authViewModel.updateIncludeVideos($0) }
                            )) {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Include Videos").font(.system(size: 13, weight: .semibold))
                                    Text("\(String(format: "%.1f", authViewModel.chatBackupState.videosSizeMb)) MB of videos in backup").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                }
                            }
                            .tint(.evrryPrimaryBlue)
                        }
                        .padding(18)
                        .background(Color.white)
                        .cornerRadius(20)
                        .shadow(color: Color.black.opacity(0.04), radius: 6, y: 2)

                        // 2. PRIVACY & SOCIAL DISCOVERABILITY
                        VStack(alignment: .leading, spacing: 14) {
                            Text("PRIVACY & SOCIAL DISCOVERABILITY").font(.system(size: 11, weight: .bold)).foregroundColor(.evrryMutedText)

                            Toggle(isOn: Binding(
                                get: { userProfile.allowPhoneDiscovery },
                                set: { authViewModel.updatePrivacySettings(allowPhoneDiscovery: $0, readReceiptsEnabled: userProfile.readReceiptsEnabled, lastSeenPrivacy: userProfile.lastSeenPrivacy) }
                            )) {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Find Me by Phone Number").font(.system(size: 13, weight: .semibold))
                                    Text("When off, discoverable only via @handle or QR code.").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                }
                            }
                            .tint(.evrryPrimaryBlue)

                            Divider()

                            Toggle(isOn: Binding(
                                get: { userProfile.readReceiptsEnabled },
                                set: { authViewModel.updatePrivacySettings(allowPhoneDiscovery: userProfile.allowPhoneDiscovery, readReceiptsEnabled: $0, lastSeenPrivacy: userProfile.lastSeenPrivacy) }
                            )) {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Read Receipts (Blue Ticks)").font(.system(size: 13, weight: .semibold))
                                    Text("Show double blue ticks when messages are read.").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                }
                            }
                            .tint(.evrryPrimaryBlue)

                            Divider()

                            Toggle(isOn: Binding(
                                get: { authViewModel.appSecurity.biometricLockEnabled },
                                set: { authViewModel.toggleBiometricLock($0) }
                            )) {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("App Lock (Face ID)").font(.system(size: 13, weight: .semibold))
                                    Text("Require Face ID / Touch ID when opening Evrry.").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                }
                            }
                            .tint(.evrryPrimaryBlue)
                        }
                        .padding(18)
                        .background(Color.white)
                        .cornerRadius(20)
                        .shadow(color: Color.black.opacity(0.04), radius: 6, y: 2)
                    }

                    // =========================================================
                    // TAB 3: HELP, SUPPORT & DANGER ZONE
                    // =========================================================
                    if selectedTab == 3 {
                        // 1. CUSTOMER SUPPORT
                        VStack(alignment: .leading, spacing: 14) {
                            HStack(spacing: 10) {
                                Circle()
                                    .fill(Color.green.opacity(0.15))
                                    .frame(width: 40, height: 40)
                                    .overlay(Image(systemName: "headphones").foregroundColor(.green))

                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Help & Customer Support").font(.system(size: 16, weight: .bold)).foregroundColor(.evrryDarkCharcoal)
                                    Text(authViewModel.supportInfo.supportHours).font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                }
                            }

                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Support Hotline").font(.system(size: 12, weight: .bold))
                                    Text(authViewModel.supportInfo.hotlinePhone).font(.system(size: 13, weight: .bold)).foregroundColor(.evrryPrimaryBlue)
                                }
                                Spacer()
                                Button("Call Now") { /* Call Hotline */ }
                                    .font(.system(size: 11, weight: .bold))
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 6)
                                    .overlay(Capsule().stroke(Color.evrryPrimaryBlue, lineWidth: 1))
                            }
                            .padding(12)
                            .background(Color.evrrySubtleSurface)
                            .cornerRadius(12)

                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Email Support").font(.system(size: 12, weight: .bold))
                                    Text(authViewModel.supportInfo.supportEmail).font(.system(size: 12)).foregroundColor(.evrryMutedText)
                                }
                                Spacer()
                                Button("Write") { /* Email */ }
                                    .font(.system(size: 11, weight: .bold))
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 6)
                                    .overlay(Capsule().stroke(Color.evrryPrimaryBlue, lineWidth: 1))
                            }
                            .padding(12)
                            .background(Color.evrrySubtleSurface)
                            .cornerRadius(12)
                        }
                        .padding(18)
                        .background(Color.white)
                        .cornerRadius(20)
                        .shadow(color: Color.black.opacity(0.04), radius: 6, y: 2)

                        // 2. LEGAL POLICIES & COMPANY
                        VStack(alignment: .leading, spacing: 14) {
                            Text("LEGAL POLICIES & ABOUT")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)

                            Button(action: { showTermsSheet = true }) {
                                HStack(spacing: 12) {
                                    Circle()
                                        .fill(Color.blue.opacity(0.12))
                                        .frame(width: 36, height: 36)
                                        .overlay(Image(systemName: "doc.text.fill").font(.system(size: 15)).foregroundColor(.evrryPrimaryBlue))

                                    VStack(alignment: .leading, spacing: 2) {
                                        Text("Terms of Service").font(.system(size: 14, weight: .semibold)).foregroundColor(.evrryDarkCharcoal)
                                        Text("User agreement, order escrow & rules").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                    }
                                    Spacer()
                                    Image(systemName: "chevron.right").font(.system(size: 12)).foregroundColor(.gray)
                                }
                            }

                            Divider()

                            Button(action: { showPrivacySheet = true }) {
                                HStack(spacing: 12) {
                                    Circle()
                                        .fill(Color.green.opacity(0.12))
                                        .frame(width: 36, height: 36)
                                        .overlay(Image(systemName: "lock.shield.fill").font(.system(size: 15)).foregroundColor(.green))

                                    VStack(alignment: .leading, spacing: 2) {
                                        Text("Privacy Policy").font(.system(size: 14, weight: .semibold)).foregroundColor(.evrryDarkCharcoal)
                                        Text("Zero-knowledge E2EE & Nepal Privacy Act").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                    }
                                    Spacer()
                                    Image(systemName: "chevron.right").font(.system(size: 12)).foregroundColor(.gray)
                                }
                            }

                            Divider()

                            Button(action: { showAboutSheet = true }) {
                                HStack(spacing: 12) {
                                    Circle()
                                        .fill(Color.purple.opacity(0.12))
                                        .frame(width: 36, height: 36)
                                        .overlay(Image(systemName: "info.circle.fill").font(.system(size: 15)).foregroundColor(.purple))

                                    VStack(alignment: .leading, spacing: 2) {
                                        Text("About Evrry").font(.system(size: 14, weight: .semibold)).foregroundColor(.evrryDarkCharcoal)
                                        Text("Elifsi Technologies Private Limited • v2.4.0").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                    }
                                    Spacer()
                                    Image(systemName: "chevron.right").font(.system(size: 12)).foregroundColor(.gray)
                                }
                            }
                        }
                        .padding(18)
                        .background(Color.white)
                        .cornerRadius(20)
                        .shadow(color: Color.black.opacity(0.04), radius: 6, y: 2)

                        // 3. DATA PRIVACY & STORAGE MANAGEMENT (DELETE DATA)
                        VStack(alignment: .leading, spacing: 14) {
                            Text("DATA PRIVACY & STORAGE MANAGEMENT")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)

                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Local App Cache").font(.system(size: 13, weight: .semibold))
                                    Text(authViewModel.dataManagementState.cacheSizeMb > 0 ? "\(String(format: "%.1f", authViewModel.dataManagementState.cacheSizeMb)) MB temporary media" : "Cache is clean (0.0 MB)")
                                        .font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                }
                                Spacer()
                                Button("Clear Cache") {
                                    authViewModel.clearLocalCache()
                                    cacheNoticeText = "Local cache cleared"
                                }
                                .font(.system(size: 11, weight: .bold))
                                .padding(.horizontal, 10)
                                .padding(.vertical, 5)
                                .overlay(Capsule().stroke(Color.evrryPrimaryBlue, lineWidth: 1))
                            }

                            Divider()

                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("AI Concierge Memory").font(.system(size: 13, weight: .semibold))
                                    Text("\(authViewModel.dataManagementState.aiMemoryFactsCount) remembered preferences").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                }
                                Spacer()
                                Button("Clear Memory") {
                                    authViewModel.clearAiMemoryFacts()
                                    cacheNoticeText = "AI memory reset"
                                }
                                .font(.system(size: 11, weight: .bold))
                                .padding(.horizontal, 10)
                                .padding(.vertical, 5)
                                .overlay(Capsule().stroke(Color.evrryPrimaryBlue, lineWidth: 1))
                            }

                            Divider()

                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Download Account Archive").font(.system(size: 13, weight: .semibold))
                                    Text("Export profile & orders in JSON").font(.system(size: 11)).foregroundColor(.evrryMutedText)
                                }
                                Spacer()
                                Button(authViewModel.dataManagementState.isExportingData ? "Exporting..." : "Export JSON") {
                                    authViewModel.requestDataExport()
                                }
                                .font(.system(size: 11, weight: .bold))
                                .padding(.horizontal, 10)
                                .padding(.vertical, 5)
                                .background(Color.evrryPrimaryBlue)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                                .disabled(authViewModel.dataManagementState.isExportingData)
                            }

                            if let url = authViewModel.dataManagementState.exportDownloadUrl {
                                Text("Archive ready! \(url)")
                                    .font(.system(size: 11, weight: .semibold))
                                    .foregroundColor(.green)
                            }

                            if let notice = cacheNoticeText {
                                Text(notice)
                                    .font(.system(size: 11, weight: .semibold))
                                    .foregroundColor(.green)
                            }
                        }
                        .padding(18)
                        .background(Color.white)
                        .cornerRadius(20)
                        .shadow(color: Color.black.opacity(0.04), radius: 6, y: 2)

                        // 4. DANGER ZONE (LOGOUT & DELETE ACCOUNT)
                        VStack(spacing: 12) {
                            Button(action: onLogout) {
                                HStack {
                                    Image(systemName: "rectangle.portrait.and.arrow.right")
                                    Text("Logout / Switch Account").font(.system(size: 14, weight: .bold))
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 12)
                                .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                .foregroundColor(.evrryDarkCharcoal)
                            }

                            Button(action: { showDeleteAccountAlert = true }) {
                                HStack {
                                    Image(systemName: "trash.fill")
                                    Text("Schedule Account Deletion (30 Days)").font(.system(size: 14, weight: .bold))
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 12)
                                .background(Color.red)
                                .foregroundColor(.white)
                                .cornerRadius(12)
                            }
                        }
                        .padding(18)
                        .background(Color.white)
                        .cornerRadius(20)
                        .shadow(color: Color.black.opacity(0.04), radius: 6, y: 2)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
            }
        }
        .background(Color(UIColor.systemGroupedBackground))
        .sheet(isPresented: $showEditProfileSheet) {
            EditProfileSheetView(userProfile: userProfile, onSave: { name, handle, bio in
                authViewModel.updateProfile(fullName: name, username: handle, bioStatus: bio)
            })
        }
        .sheet(isPresented: $showQrSheet) {
            PersonalQrSheetView(userProfile: userProfile)
        }
        .sheet(isPresented: $showTermsSheet) {
            TermsOfServiceSheetView()
        }
        .sheet(isPresented: $showPrivacySheet) {
            PrivacyPolicySheetView()
        }
        .sheet(isPresented: $showAboutSheet) {
            AboutUsSheetView()
        }
        .sheet(isPresented: $showAddressBookSheet) {
            AddressBookView(onBack: { showAddressBookSheet = false })
        }
        .alert("Delete Account (30-Day Cooling Period)?", isPresented: $showDeleteAccountAlert) {
            Button("Schedule 30-Day Deletion", role: .destructive) {
                authViewModel.deleteAccount()
            }
            Button("Keep Account", role: .cancel) {}
        } message: {
            Text("Following global privacy standards (GDPR / ISO 27001 / App Store guidelines):\n\n• Your profile, chat presence, and @\(userProfile.username) will be hidden immediately.\n• 30-Day Grace Period: You can cancel anytime within 30 days simply by logging back in.\n• After 30 days, all account data, chat backups, Tier Points, and E-Coins will be permanently purged.")
        }
    }
}

// MARK: - Edit Profile Sheet
struct EditProfileSheetView: View {
    let userProfile: UserProfile
    let onSave: (String, String, String) -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var fullName: String = ""
    @State private var username: String = ""
    @State private var bioStatus: String = ""

    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Public Identity")) {
                    TextField("Full Name", text: $fullName)
                    TextField("Username (@handle)", text: $username)
                }

                Section(header: Text("Bio / Status")) {
                    TextField("Status (e.g. Available on Evrry)", text: $bioStatus)
                }
            }
            .navigationTitle("Edit Profile")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        onSave(fullName, username, bioStatus)
                        dismiss()
                    }
                    .font(.headline)
                }
            }
            .onAppear {
                fullName = userProfile.fullName
                username = userProfile.username
                bioStatus = userProfile.bioStatus
            }
        }
    }
}

// MARK: - Personal QR Code Sheet
struct PersonalQrSheetView: View {
    let userProfile: UserProfile
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        VStack(spacing: 20) {
            HStack {
                Spacer()
                Button(action: { dismiss() }) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.system(size: 24))
                        .foregroundColor(.gray)
                }
            }
            .padding(.top, 16)
            .padding(.horizontal, 20)

            Text("My QR Code")
                .font(.system(size: 20, weight: .bold))
                .foregroundColor(.evrryDarkCharcoal)

            Text("Scan to add on Evrry without sharing phone number")
                .font(.system(size: 12))
                .foregroundColor(.evrryMutedText)

            RoundedRectangle(cornerRadius: 20)
                .fill(Color.white)
                .frame(width: 200, height: 200)
                .overlay(
                    Image(systemName: "qrcode")
                        .resizable()
                        .padding(24)
                        .foregroundColor(.evrryDarkCharcoal)
                )
                .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                .shadow(color: Color.black.opacity(0.05), radius: 8, y: 4)

            VStack(spacing: 4) {
                Text(userProfile.fullName)
                    .font(.system(size: 17, weight: .bold))
                Text("@\(userProfile.username)")
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundColor(.evrryPrimaryBlue)
            }

            Spacer()
        }
        .padding()
    }
}

// MARK: - Terms of Service Sheet
struct TermsOfServiceSheetView: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 18) {
                    Group {
                        Text("1. Agreement to Terms")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("By accessing or using Evrry Super App, operated by Elifsi Technologies Private Limited in Kathmandu, Nepal, you agree to be bound by these Terms of Service. If you do not agree, please discontinue using the application.")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)

                        Text("2. Super App Ecosystem & Services")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("Evrry provides an on-demand multi-service platform connecting consumers with food & grocery merchants, mobility ride drivers, digital vouchers, and end-to-end encrypted messaging. All service fulfillments adhere to regulations in Nepal.")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)

                        Text("3. Orders, Payments & Escrow")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("Payments are securely processed via licensed payment service providers in Nepal (Fonepay, eSewa, Khalti, ConnectIPS) or Cash on Delivery (COD). Delivery fees and tips are held in escrow until successful completion of the delivery or trip.")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)
                    }

                    Group {
                        Text("4. Social Chat & VoIP Standards")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("You agree to use social messaging and in-app VoIP calling solely for lawful communications. Harassment, spamming, unauthorized automated scraping, and illicit content distribution are strictly prohibited.")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)

                        Text("5. Governing Law & Jurisdiction")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("These terms are governed by and construed in accordance with the laws of Nepal. Any legal disputes shall be subject to the exclusive jurisdiction of the competent courts of Kathmandu, Nepal.")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)
                    }
                }
                .padding(20)
            }
            .navigationTitle("Terms of Service")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Done") { dismiss() }.font(.headline)
                }
            }
        }
    }
}

// MARK: - Privacy Policy Sheet
struct PrivacyPolicySheetView: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 18) {
                    Group {
                        Text("1. Zero-Eavesdropping Commitment")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("All 1-to-1 chats, group conversations, audio calls, and video streams utilize Signal Protocol End-to-End Encryption (E2EE). Neither Evrry nor Elifsi Technologies can access your conversation contents, photos, or voice streams.")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)

                        Text("2. Information We Collect")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("We collect your verified mobile phone number (for OTP authentication), device push token, and live GPS coordinates strictly during active ride bookings or food delivery dispatch.")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)

                        Text("3. WeChat-Style Phone Stealth")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("Your phone number is masked from riders, restaurants, and group chats. You can disable 'Find Me by Phone' so contacts can only discover you via your chosen @username or personal QR code.")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)
                    }

                    Group {
                        Text("4. Google Drive & iCloud Chat Backups")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("Chat archives and media are backed up directly to your personal Google Drive or Apple iCloud account with end-to-end encryption keys. We never store raw chat backups on company servers.")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)

                        Text("5. Right to Erasure & Data Portability")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("Under Section 27 of Nepal's Individual Privacy Act, 2075, you hold the unconditional right to export your account data archive, clear cached files, or permanently delete your profile and all associated data.")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)
                    }
                }
                .padding(20)
            }
            .navigationTitle("Privacy Policy")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Done") { dismiss() }.font(.headline)
                }
            }
        }
    }
}

// MARK: - About Us Sheet
struct AboutUsSheetView: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Circle()
                    .fill(Color.blue.opacity(0.12))
                    .frame(width: 70, height: 70)
                    .overlay(Image(systemName: "app.badge.fill").font(.system(size: 32)).foregroundColor(.evrryPrimaryBlue))
                    .padding(.top, 24)

                VStack(spacing: 4) {
                    Text("Evrry Super App")
                        .font(.system(size: 22, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text("Version 2.4.0 (Build 2026.10)")
                        .font(.system(size: 13, weight: .semibold))
                        .foregroundColor(.evrryPrimaryBlue)
                }

                Text("Developed with ❤️ by\nElifsi Technologies Private Limited\nKathmandu, Nepal")
                    .font(.system(size: 14))
                    .multilineTextAlignment(.center)
                    .foregroundColor(.evrryDarkCharcoal)
                    .lineSpacing(4)

                VStack(alignment: .leading, spacing: 8) {
                    Text("Company Reg: REG-314982/080/081").font(.system(size: 12)).foregroundColor(.evrryMutedText)
                    Text("Headquarters: Pulchowk, Lalitpur, Nepal").font(.system(size: 12)).foregroundColor(.evrryMutedText)
                    Text("Website: https://evrry.com").font(.system(size: 12, weight: .semibold)).foregroundColor(.evrryPrimaryBlue)
                    Text("Tech Stack: Native SwiftUI & Jetpack Compose, PostGIS, Signal E2EE").font(.system(size: 12)).foregroundColor(.evrryMutedText)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(16)
                .background(Color(UIColor.secondarySystemGroupedBackground))
                .cornerRadius(16)
                .padding(.horizontal, 20)

                Spacer()
            }
            .background(Color(UIColor.systemGroupedBackground))
            .navigationTitle("About Evrry")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Done") { dismiss() }.font(.headline)
                }
            }
        }
    }
}

