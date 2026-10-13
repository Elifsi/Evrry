//
//  VouchersReferralsView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Dedicated Vouchers & Referrals Screen
//

import SwiftUI

public struct VouchersReferralsView: View {
    @ObservedObject public var loyaltyViewModel: LoyaltyViewModel
    @Environment(\.presentationMode) var presentationMode
    public var onBack: (() -> Void)? = nil

    @State private var promoCodeInput: String = ""
    @State private var showCopiedToast: Bool = false

    public init(loyaltyViewModel: LoyaltyViewModel, onBack: (() -> Void)? = nil) {
        self.loyaltyViewModel = loyaltyViewModel
        self.onBack = onBack
    }

    private var referralInfo: ReferralInfo {
        loyaltyViewModel.referralInfo
    }

    private var vouchers: [VoucherItem] {
        loyaltyViewModel.vouchers
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
                    Text("Vouchers & Referrals")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text("Promo discounts, coupons & earn NPR with friends")
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
                    // Referral Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            ZStack {
                                RoundedRectangle(cornerRadius: 10)
                                    .fill(Color.evrryPrimaryBlue.opacity(0.12))
                                    .frame(width: 36, height: 36)
                                Image(systemName: "gift.fill")
                                    .foregroundColor(.evrryPrimaryBlue)
                            }

                            VStack(alignment: .leading, spacing: 2) {
                                Text("Refer & Earn NPR \(referralInfo.referrerRewardNpr)")
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Text("Friend gets NPR \(referralInfo.inviteeRewardNpr) off first ride/order")
                                    .font(.system(size: 11))
                                    .foregroundColor(.evrryMutedText)
                            }
                        }

                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("YOUR UNIQUE REFERRAL CODE")
                                    .font(.system(size: 10, weight: .bold))
                                    .foregroundColor(.evrryMutedText)
                                Text(referralInfo.referralCode)
                                    .font(.system(size: 18, weight: .heavy))
                                    .foregroundColor(.evrryPrimaryBlue)
                            }

                            Spacer()

                            Button(action: {
                                UIPasteboard.general.string = referralInfo.referralCode
                                showCopiedToast = true
                                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                    showCopiedToast = false
                                }
                            }) {
                                HStack(spacing: 4) {
                                    Image(systemName: "doc.on.doc")
                                        .font(.system(size: 12, weight: .bold))
                                    Text(showCopiedToast ? "Copied!" : "Copy")
                                        .font(.system(size: 12, weight: .bold))
                                }
                                .foregroundColor(.white)
                                .padding(.horizontal, 14)
                                .padding(.vertical, 8)
                                .background(Color.evrryPrimaryBlue)
                                .clipShape(RoundedRectangle(cornerRadius: 10))
                            }
                        }
                        .padding(12)
                        .background(Color(red: 0.95, green: 0.97, blue: 1.0))
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                    .padding(16)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 18))
                    .shadow(color: Color.black.opacity(0.03), radius: 6, x: 0, y: 2)
                    .padding(.top, 14)

                    // Promo Code Input Box
                    VStack(alignment: .leading, spacing: 10) {
                        Text("HAVE A PROMO CODE?")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                            .tracking(0.5)

                        HStack(spacing: 10) {
                            TextField("Enter promo code (e.g. EVRRYFEST)", text: $promoCodeInput)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                                .textInputAutocapitalization(.characters)

                            Button(action: {
                                // Apply promo
                                promoCodeInput = ""
                            }) {
                                Text("Apply")
                                    .font(.system(size: 13, weight: .bold))
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 16)
                                    .frame(height: 36)
                                    .background(Color.evrryPrimaryBlue)
                                    .clipShape(RoundedRectangle(cornerRadius: 8))
                            }
                        }
                    }
                    .padding(16)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 18))

                    // Available Vouchers
                    VStack(alignment: .leading, spacing: 10) {
                        Text("ACTIVE VOUCHERS (\(vouchers.count))")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                            .tracking(0.5)

                        ForEach(vouchers) { voucher in
                            HStack(spacing: 12) {
                                ZStack {
                                    RoundedRectangle(cornerRadius: 12)
                                        .fill(Color.evrryPrimaryBlue.opacity(0.12))
                                        .frame(width: 44, height: 44)
                                    Image(systemName: "ticket.fill")
                                        .font(.system(size: 20))
                                        .foregroundColor(.evrryPrimaryBlue)
                                }

                                VStack(alignment: .leading, spacing: 2) {
                                    Text(voucher.title)
                                        .font(.system(size: 13, weight: .bold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                    Text("Code: \(voucher.code) • \(voucher.description)")
                                        .font(.system(size: 11))
                                        .foregroundColor(.evrryMutedText)
                                    Text(voucher.expiryDaysText)
                                        .font(.system(size: 10))
                                        .foregroundColor(.evrryMutedText)
                                }

                                Spacer()

                                Text(voucher.discountText)
                                    .font(.system(size: 13, weight: .bold))
                                    .foregroundColor(Color(red: 0.06, green: 0.73, blue: 0.51))
                            }
                            .padding(14)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                            .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        }
                    }
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 40)
            }
        }
        .background(Color(red: 0.98, green: 0.98, blue: 1.0).ignoresSafeArea())
    }
}
