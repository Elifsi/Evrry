package com.elifsi.evrry.consumer.model

import kotlinx.serialization.Serializable

/**
 * Gamified Loyalty, Tiered Leagues, Stamp Cards & Vouchers Domain Models
 * Aligned with Supabase Migration 0008 (public.user_loyalty, public.vouchers, public.user_stamp_cards)
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */

enum class LoyaltyLeague(
    val id: String,
    val label: String,
    val minPoints: Int,
    val coinMultiplier: Double,
    val perkDescription: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long
) {
    COPPER("copper", "Copper", 0, 1.0, "Standard loyalty points & basic concierge support", 0xFF94A3B8, 0xFF64748B),
    BRONZE("bronze", "Bronze", 200, 1.1, "1.1x E-Coins multiplier on food & rides", 0xFFCD7F32, 0xFF8C531B),
    SILVER("silver", "Silver", 800, 1.25, "1.25x E-Coins · 1 monthly free delivery voucher", 0xFF94A3B8, 0xFF475569),
    PLATINUM("platinum", "Platinum", 2000, 1.5, "1.5x E-Coins · Zero ride platform fees · Priority support", 0xFF0284C7, 0xFF0369A1),
    DIAMOND("diamond", "Diamond", 5000, 2.0, "2.0x E-Coins · VIP Concierge · Exclusive merchant rebates", 0xFF7C3AED, 0xFF5B21B6);

    companion object {
        fun fromPoints(points: Int): LoyaltyLeague {
            return when {
                points >= DIAMOND.minPoints -> DIAMOND
                points >= PLATINUM.minPoints -> PLATINUM
                points >= SILVER.minPoints -> SILVER
                points >= BRONZE.minPoints -> BRONZE
                else -> COPPER
            }
        }

        fun nextLeague(current: LoyaltyLeague): LoyaltyLeague? {
            return when (current) {
                COPPER -> BRONZE
                BRONZE -> SILVER
                SILVER -> PLATINUM
                PLATINUM -> DIAMOND
                DIAMOND -> null
            }
        }
    }
}

@Serializable
data class UserLoyaltyState(
    val currentLeague: String = "silver",
    val tierPoints: Int = 840,
    val eCoinsBalance: Int = 320,
    val consecutiveCheckinDays: Int = 4,
    val hasCheckedInToday: Boolean = false,
    val lastCheckinDate: String? = "2026-10-09"
) {
    val league: LoyaltyLeague get() = LoyaltyLeague.fromPoints(tierPoints)
    val nextLeague: LoyaltyLeague? get() = LoyaltyLeague.nextLeague(league)

    val progressToNext: Float
        get() {
            val next = nextLeague ?: return 1f
            val span = next.minPoints - league.minPoints
            if (span <= 0) return 1f
            return ((tierPoints - league.minPoints).toFloat() / span.toFloat()).coerceIn(0f, 1f)
        }

    val pointsToNext: Int
        get() {
            val next = nextLeague ?: return 0
            return (next.minPoints - tierPoints).coerceAtLeast(0)
        }
}

@Serializable
data class DigitalStampCard(
    val id: String = "stamp-card-main",
    val vertical: String = "all",
    val currentStamps: Int = 7,
    val totalSlots: Int = 10,
    val rewardTitle: String = "Flat NPR 500 Rebate Voucher",
    val completedSheetsCount: Int = 1
) {
    val isCompleted: Boolean get() = currentStamps >= totalSlots
    val stampsRemaining: Int get() = (totalSlots - currentStamps).coerceAtLeast(0)
}

@Serializable
data class VoucherItem(
    val id: String,
    val code: String,
    val title: String,
    val description: String,
    val discountText: String,               // e.g. "NPR 150 OFF", "FREE DELIVERY"
    val discountType: String,               // "flat_amount", "percentage", "free_delivery"
    val minOrderNpr: Double,
    val applicableVertical: String,         // "all", "food", "grocery", "rides", "hotels"
    val expiryDaysText: String,             // e.g. "Expires in 4 days"
    val isUsed: Boolean = false
)

@Serializable
data class ReferralInfo(
    val referralCode: String = "EVRRY-RAHUL",
    val inviteeRewardNpr: Int = 100,
    val referrerRewardNpr: Int = 150,
    val friendsInvitedCount: Int = 4,
    val totalEarnedNpr: Int = 450,
    val shareUrl: String = "https://evrry.com.np/r/rahul_evrry"
)
