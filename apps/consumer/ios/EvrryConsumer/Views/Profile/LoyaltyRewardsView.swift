//
//  LoyaltyRewardsView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Dedicated Loyalty & Rewards Screen
//

import SwiftUI

public struct LoyaltyRewardsView: View {
    @ObservedObject public var loyaltyViewModel: LoyaltyViewModel
    @Environment(\.presentationMode) var presentationMode
    public var onBack: (() -> Void)? = nil

    public init(loyaltyViewModel: LoyaltyViewModel, onBack: (() -> Void)? = nil) {
        self.loyaltyViewModel = loyaltyViewModel
        self.onBack = onBack
    }

    private var loyaltyState: UserLoyaltyState {
        loyaltyViewModel.loyaltyState
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
                    Text("Rewards & Loyalty")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text("Tier status, Evrry points & exclusive perks")
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
                    // Tier Hero Card
                    ZStack {
                        LinearGradient(
                            colors: [Color(red: 0.12, green: 0.23, blue: 0.54), Color(red: 0.23, green: 0.51, blue: 0.96)],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )

                        VStack(alignment: .leading, spacing: 14) {
                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("CURRENT TIER")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(Color.white.opacity(0.7))
                                        .tracking(1.0)
                                    Text(loyaltyState.league.label)
                                        .font(.system(size: 22, weight: .heavy))
                                        .foregroundColor(.white)
                                }

                                Spacer()

                                ZStack {
                                    Circle()
                                        .fill(Color.white.opacity(0.2))
                                        .frame(width: 48, height: 48)
                                    Image(systemName: "crown.fill")
                                        .font(.system(size: 24))
                                        .foregroundColor(Color(red: 1.0, green: 0.84, blue: 0.0))
                                }
                            }

                            // Progress Bar
                            VStack(alignment: .leading, spacing: 6) {
                                HStack {
                                    Text("\(loyaltyState.tierPoints) Tier Points")
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(.white)
                                    Spacer()
                                    Text("\(loyaltyState.pointsToNext) pts to Next Tier")
                                        .font(.system(size: 11, weight: .medium))
                                        .foregroundColor(Color.white.opacity(0.8))
                                }

                                GeometryReader { geo in
                                    ZStack(alignment: .leading) {
                                        RoundedRectangle(cornerRadius: 6)
                                            .fill(Color.white.opacity(0.2))
                                            .frame(height: 8)
                                        RoundedRectangle(cornerRadius: 6)
                                            .fill(Color(red: 1.0, green: 0.84, blue: 0.0))
                                            .frame(width: max(0, min(geo.size.width * CGFloat(loyaltyState.progressToNext), geo.size.width)), height: 8)
                                    }
                                }
                                .frame(height: 8)
                            }
                        }
                        .padding(18)
                    }
                    .clipShape(RoundedRectangle(cornerRadius: 20))
                    .shadow(color: Color.black.opacity(0.06), radius: 6, x: 0, y: 2)
                    .padding(.top, 14)

                    // Tier Perks Breakdown
                    VStack(alignment: .leading, spacing: 12) {
                        Text("YOUR ACTIVE PERKS")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                            .tracking(0.5)

                        PerkRow(icon: "bolt.fill", title: "Priority Ride Matching", subtitle: "Instant driver dispatch during peak rain & festival surges")
                        PerkRow(icon: "bag.fill", title: "Zero Delivery Fee on Grocery", subtitle: "Valid on all supermarket orders over NPR 999")
                        PerkRow(icon: "headphones", title: "Dedicated 24/7 VIP Support", subtitle: "Direct hotline to senior concierge support")
                        PerkRow(icon: "percent", title: "3% Cashback in Points", subtitle: "Earn points across rides, food & mart orders")
                    }
                    .padding(16)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 18))
                    .shadow(color: Color.black.opacity(0.02), radius: 4, x: 0, y: 2)
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 40)
            }
        }
        .background(Color(red: 0.98, green: 0.98, blue: 1.0).ignoresSafeArea())
    }
}

private struct PerkRow: View {
    let icon: String
    let title: String
    let subtitle: String

    var body: some View {
        HStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(Color.evrryPrimaryBlue.opacity(0.1))
                    .frame(width: 36, height: 36)
                Image(systemName: icon)
                    .font(.system(size: 15))
                    .foregroundColor(.evrryPrimaryBlue)
            }

            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.system(size: 13, weight: .bold))
                    .foregroundColor(.evrryDarkCharcoal)
                Text(subtitle)
                    .font(.system(size: 11))
                    .foregroundColor(.evrryMutedText)
            }
        }
        .padding(.vertical, 4)
    }
}
