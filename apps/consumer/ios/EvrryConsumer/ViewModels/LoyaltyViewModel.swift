//
//  LoyaltyViewModel.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Loyalty, Gamified Leagues, Stamp Cards & Vouchers ViewModel for iOS
//

import Foundation
import Combine

public class LoyaltyViewModel: ObservableObject {
    @Published public var loyaltyState: UserLoyaltyState = UserLoyaltyState()
    @Published public var stampCard: DigitalStampCard = DigitalStampCard()
    @Published public var vouchers: [VoucherItem] = [
        VoucherItem(
            id: "v-wel-100",
            code: "WELCOME100",
            title: "Welcome Bonus Voucher",
            description: "Valid across all Food Delivery and Ride orders above NPR 300",
            discountText: "NPR 100 OFF",
            discountType: "flat_amount",
            minOrderNpr: 300.0,
            applicableVertical: "all",
            expiryDaysText: "Expires in 5 days"
        ),
        VoucherItem(
            id: "v-momo-150",
            code: "MOMOLOVE",
            title: "Food & Grocery Quick Perk",
            description: "Flat NPR 150 off on Roadhouse, Himalayan Mart or local eateries",
            discountText: "NPR 150 OFF",
            discountType: "flat_amount",
            minOrderNpr: 600.0,
            applicableVertical: "food",
            expiryDaysText: "Expires tomorrow"
        ),
        VoucherItem(
            id: "v-free-ride",
            code: "FREERIDE",
            title: "Zero Platform Fee Ride Pass",
            description: "Waives platform booking fees for your next 3 Moto or Cab rides",
            discountText: "FREE PLATFORM FEE",
            discountType: "free_delivery",
            minOrderNpr: 150.0,
            applicableVertical: "rides",
            expiryDaysText: "Expires in 12 days"
        )
    ]
    @Published public var referralInfo: ReferralInfo = ReferralInfo()

    public init() {}

    public func claimDailyCheckin() -> Bool {
        guard !loyaltyState.hasCheckedInToday else { return false }
        let currentStreak = loyaltyState.consecutiveCheckinDays + 1
        let earnedCoins = 10 + min(50, currentStreak * 5)
        let newBalance = loyaltyState.eCoinsBalance + earnedCoins
        let newTierPoints = loyaltyState.tierPoints + 15

        loyaltyState.hasCheckedInToday = true
        loyaltyState.consecutiveCheckinDays = currentStreak
        loyaltyState.eCoinsBalance = newBalance
        loyaltyState.tierPoints = newTierPoints
        return true
    }

    public func redeemPromoCode(_ inputCode: String) -> (success: Bool, message: String) {
        let clean = inputCode.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        guard !clean.isEmpty else { return (false, "Please enter a valid code") }

        if vouchers.contains(where: { $0.code.uppercased() == clean }) {
            return (false, "Voucher already in your wallet")
        }

        switch clean {
        case "EVRRY50", "DASHAIN":
            let newVoucher = VoucherItem(
                id: "v-promo-\(Int(Date().timeIntervalSince1970))",
                code: clean,
                title: "Festive Promotional Voucher",
                description: "Special festive rebate valid across all services",
                discountText: "NPR 150 OFF",
                discountType: "flat_amount",
                minOrderNpr: 500.0,
                applicableVertical: "all",
                expiryDaysText: "Expires in 7 days"
            )
            vouchers.insert(newVoucher, at: 0)
            return (true, "Voucher successfully added to your wallet!")
        case "FREEDEL":
            let newVoucher = VoucherItem(
                id: "v-promo-\(Int(Date().timeIntervalSince1970))",
                code: clean,
                title: "Free Express Delivery",
                description: "Zero delivery charges on your next order",
                discountText: "FREE DELIVERY",
                discountType: "free_delivery",
                minOrderNpr: 400.0,
                applicableVertical: "food",
                expiryDaysText: "Expires in 3 days"
            )
            vouchers.insert(newVoucher, at: 0)
            return (true, "Free delivery voucher unlocked!")
        default:
            return (false, "Invalid or expired promo code")
        }
    }
}
