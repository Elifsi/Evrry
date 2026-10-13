package com.elifsi.evrry.consumer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elifsi.evrry.consumer.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Loyalty, Gamified Leagues, Digital Stamp Cards & Vouchers ViewModel
 * Manages daily Asia/Kathmandu check-in streaks, E-Coin micro-currencies,
 * tier promotions, promo code redemption, and two-sided referrals.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
class LoyaltyViewModel : ViewModel() {

    private val _loyaltyState = MutableStateFlow(
        UserLoyaltyState(
            currentLeague = "silver",
            tierPoints = 840,
            eCoinsBalance = 320,
            consecutiveCheckinDays = 4,
            hasCheckedInToday = false,
            lastCheckinDate = "2026-10-09"
        )
    )
    val loyaltyState: StateFlow<UserLoyaltyState> = _loyaltyState.asStateFlow()

    private val _stampCard = MutableStateFlow(
        DigitalStampCard(
            id = "stamp-card-main",
            vertical = "all",
            currentStamps = 7,
            totalSlots = 10,
            rewardTitle = "Flat NPR 500 Rebate Voucher",
            completedSheetsCount = 1
        )
    )
    val stampCard: StateFlow<DigitalStampCard> = _stampCard.asStateFlow()

    private val _vouchers = MutableStateFlow(
        listOf(
            VoucherItem(
                id = "v-wel-100",
                code = "WELCOME100",
                title = "Welcome Bonus Voucher",
                description = "Valid across all Food Delivery and Ride orders above NPR 300",
                discountText = "NPR 100 OFF",
                discountType = "flat_amount",
                minOrderNpr = 300.0,
                applicableVertical = "all",
                expiryDaysText = "Expires in 5 days"
            ),
            VoucherItem(
                id = "v-momo-150",
                code = "MOMOLOVE",
                title = "Food & Grocery Quick Perk",
                description = "Flat NPR 150 off on Roadhouse, Himalayan Mart or local eateries",
                discountText = "NPR 150 OFF",
                discountType = "flat_amount",
                minOrderNpr = 600.0,
                applicableVertical = "food",
                expiryDaysText = "Expires tomorrow"
            ),
            VoucherItem(
                id = "v-free-ride",
                code = "FREERIDE",
                title = "Zero Platform Fee Ride Pass",
                description = "Waives platform booking fees for your next 3 Moto or Cab rides",
                discountText = "FREE PLATFORM FEE",
                discountType = "free_delivery",
                minOrderNpr = 150.0,
                applicableVertical = "rides",
                expiryDaysText = "Expires in 12 days"
            )
        )
    )
    val vouchers: StateFlow<List<VoucherItem>> = _vouchers.asStateFlow()

    private val _referralInfo = MutableStateFlow(
        ReferralInfo(
            referralCode = "EVRRY-RAHUL",
            inviteeRewardNpr = 100,
            referrerRewardNpr = 150,
            friendsInvitedCount = 4,
            totalEarnedNpr = 450,
            shareUrl = "https://evrry.com.np/r/rahul_evrry"
        )
    )
    val referralInfo: StateFlow<ReferralInfo> = _referralInfo.asStateFlow()

    /**
     * Daily Check-In streak: awards E-Coins once per Nepal calendar day
     */
    fun claimDailyCheckin(): Boolean {
        if (_loyaltyState.value.hasCheckedInToday) return false

        val currentStreak = _loyaltyState.value.consecutiveCheckinDays + 1
        val earnedCoins = 10 + (currentStreak * 5).coerceAtMost(50) // streak bonus
        val newBalance = _loyaltyState.value.eCoinsBalance + earnedCoins
        val newTierPoints = _loyaltyState.value.tierPoints + 15

        _loyaltyState.value = _loyaltyState.value.copy(
            hasCheckedInToday = true,
            consecutiveCheckinDays = currentStreak,
            eCoinsBalance = newBalance,
            tierPoints = newTierPoints
        )
        return true
    }

    /**
     * Redeem promotional code (e.g. EVRRY50, DASHAIN, NEWYEAR)
     */
    fun redeemPromoCode(inputCode: String): Pair<Boolean, String> {
        val clean = inputCode.trim().uppercase()
        if (clean.isEmpty()) return Pair(false, "Please enter a valid code")

        if (_vouchers.value.any { it.code.uppercase() == clean }) {
            return Pair(false, "Voucher already claimed in your wallet")
        }

        return when (clean) {
            "EVRRY50", "DASHAIN" -> {
                val newVoucher = VoucherItem(
                    id = "v-promo-${System.currentTimeMillis()}",
                    code = clean,
                    title = "Festive Promotional Voucher",
                    description = "Special festive rebate valid across all services",
                    discountText = "NPR 150 OFF",
                    discountType = "flat_amount",
                    minOrderNpr = 500.0,
                    applicableVertical = "all",
                    expiryDaysText = "Expires in 7 days"
                )
                _vouchers.value = listOf(newVoucher) + _vouchers.value
                Pair(true, "Voucher successfully added to your wallet!")
            }
            "FREEDEL" -> {
                val newVoucher = VoucherItem(
                    id = "v-promo-${System.currentTimeMillis()}",
                    code = clean,
                    title = "Free Express Delivery",
                    description = "Zero delivery charges on your next order",
                    discountText = "FREE DELIVERY",
                    discountType = "free_delivery",
                    minOrderNpr = 400.0,
                    applicableVertical = "food",
                    expiryDaysText = "Expires in 3 days"
                )
                _vouchers.value = listOf(newVoucher) + _vouchers.value
                Pair(true, "Free delivery voucher unlocked!")
            }
            else -> Pair(false, "Invalid or expired promo code")
        }
    }
}
