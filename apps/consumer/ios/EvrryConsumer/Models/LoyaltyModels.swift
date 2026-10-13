//
//  LoyaltyModels.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Loyalty Leagues, Stamp Cards, Vouchers & Referrals matching Supabase Migration 0008
//

import Foundation
import SwiftUI

public enum LoyaltyLeague: String, Codable, CaseIterable {
    case copper = "copper"
    case bronze = "bronze"
    case silver = "silver"
    case platinum = "platinum"
    case diamond = "diamond"

    public var label: String {
        switch self {
        case .copper: return "Copper"
        case .bronze: return "Bronze"
        case .silver: return "Silver"
        case .platinum: return "Platinum"
        case .diamond: return "Diamond"
        }
    }

    public var minPoints: Int {
        switch self {
        case .copper: return 0
        case .bronze: return 200
        case .silver: return 800
        case .platinum: return 2000
        case .diamond: return 5000
        }
    }

    public var coinMultiplier: Double {
        switch self {
        case .copper: return 1.0
        case .bronze: return 1.1
        case .silver: return 1.25
        case .platinum: return 1.5
        case .diamond: return 2.0
        }
    }

    public var perkDescription: String {
        switch self {
        case .copper: return "Standard loyalty points & basic concierge support"
        case .bronze: return "1.1x E-Coins multiplier on food & rides"
        case .silver: return "1.25x E-Coins · 1 monthly free delivery voucher"
        case .platinum: return "1.5x E-Coins · Zero ride platform fees · Priority support"
        case .diamond: return "2.0x E-Coins · VIP Concierge · Exclusive merchant rebates"
        }
    }

    public static func fromPoints(_ points: Int) -> LoyaltyLeague {
        if points >= LoyaltyLeague.diamond.minPoints { return .diamond }
        if points >= LoyaltyLeague.platinum.minPoints { return .platinum }
        if points >= LoyaltyLeague.silver.minPoints { return .silver }
        if points >= LoyaltyLeague.bronze.minPoints { return .bronze }
        return .copper
    }

    public var nextLeague: LoyaltyLeague? {
        switch self {
        case .copper: return .bronze
        case .bronze: return .silver
        case .silver: return .platinum
        case .platinum: return .diamond
        case .diamond: return nil
        }
    }
}

public struct UserLoyaltyState: Codable, Equatable {
    public var currentLeague: String
    public var tierPoints: Int
    public var eCoinsBalance: Int
    public var consecutiveCheckinDays: Int
    public var hasCheckedInToday: Bool
    public var lastCheckinDate: String?

    public init(
        currentLeague: String = "silver",
        tierPoints: Int = 840,
        eCoinsBalance: Int = 320,
        consecutiveCheckinDays: Int = 4,
        hasCheckedInToday: Bool = false,
        lastCheckinDate: String? = "2026-10-09"
    ) {
        self.currentLeague = currentLeague
        self.tierPoints = tierPoints
        self.eCoinsBalance = eCoinsBalance
        self.consecutiveCheckinDays = consecutiveCheckinDays
        self.hasCheckedInToday = hasCheckedInToday
        self.lastCheckinDate = lastCheckinDate
    }

    public var league: LoyaltyLeague {
        LoyaltyLeague.fromPoints(tierPoints)
    }

    public var nextLeague: LoyaltyLeague? {
        league.nextLeague
    }

    public var progressToNext: Double {
        guard let next = nextLeague else { return 1.0 }
        let span = next.minPoints - league.minPoints
        if span <= 0 { return 1.0 }
        return max(0.0, min(1.0, Double(tierPoints - league.minPoints) / Double(span)))
    }

    public var pointsToNext: Int {
        guard let next = nextLeague else { return 0 }
        return max(0, next.minPoints - tierPoints)
    }
}

public struct DigitalStampCard: Identifiable, Codable, Equatable {
    public let id: String
    public let vertical: String
    public var currentStamps: Int
    public let totalSlots: Int
    public let rewardTitle: String
    public var completedSheetsCount: Int

    public init(
        id: String = "stamp-card-main",
        vertical: String = "all",
        currentStamps: Int = 7,
        totalSlots: Int = 10,
        rewardTitle: String = "Flat NPR 500 Rebate Voucher",
        completedSheetsCount: Int = 1
    ) {
        self.id = id
        self.vertical = vertical
        self.currentStamps = currentStamps
        self.totalSlots = totalSlots
        self.rewardTitle = rewardTitle
        self.completedSheetsCount = completedSheetsCount
    }

    public var isCompleted: Bool { currentStamps >= totalSlots }
    public var stampsRemaining: Int { max(0, totalSlots - currentStamps) }
}

public struct VoucherItem: Identifiable, Codable, Equatable {
    public let id: String
    public let code: String
    public let title: String
    public let description: String
    public let discountText: String
    public let discountType: String
    public let minOrderNpr: Double
    public let applicableVertical: String
    public let expiryDaysText: String
    public var isUsed: Bool

    public init(
        id: String,
        code: String,
        title: String,
        description: String,
        discountText: String,
        discountType: String,
        minOrderNpr: Double,
        applicableVertical: String,
        expiryDaysText: String,
        isUsed: Bool = false
    ) {
        self.id = id
        self.code = code
        self.title = title
        self.description = description
        self.discountText = discountText
        self.discountType = discountType
        self.minOrderNpr = minOrderNpr
        self.applicableVertical = applicableVertical
        self.expiryDaysText = expiryDaysText
        self.isUsed = isUsed
    }
}

public struct ReferralInfo: Codable, Equatable {
    public var referralCode: String
    public var inviteeRewardNpr: Int
    public var referrerRewardNpr: Int
    public var friendsInvitedCount: Int
    public var totalEarnedNpr: Int
    public var shareUrl: String

    public init(
        referralCode: String = "EVRRY-RAHUL",
        inviteeRewardNpr: Int = 100,
        referrerRewardNpr: Int = 150,
        friendsInvitedCount: Int = 4,
        totalEarnedNpr: Int = 450,
        shareUrl: String = "https://evrry.com.np/r/rahul_evrry"
    ) {
        self.referralCode = referralCode
        self.inviteeRewardNpr = inviteeRewardNpr
        self.referrerRewardNpr = referrerRewardNpr
        self.friendsInvitedCount = friendsInvitedCount
        self.totalEarnedNpr = totalEarnedNpr
        self.shareUrl = shareUrl
    }
}

public struct ChatBackupState: Codable, Equatable {
    public var lastBackupTimestamp: String
    public var totalSizeMb: Double
    public var cloudAccount: String
    public var autoBackupFrequency: String
    public var backupNetwork: String
    public var includeVideos: Bool
    public var videosSizeMb: Double
    public var isE2EEEncrypted: Bool
    public var isBackingUp: Bool
    public var backupProgressPercent: Int

    public init(
        lastBackupTimestamp: String = "Today, 04:15 AM",
        totalSizeMb: Double = 42.8,
        cloudAccount: String = "iCloud / rahul.sharma@icloud.com",
        autoBackupFrequency: String = "Daily",
        backupNetwork: String = "Wi-Fi only",
        includeVideos: Bool = true,
        videosSizeMb: Double = 18.2,
        isE2EEEncrypted: Bool = true,
        isBackingUp: Bool = false,
        backupProgressPercent: Int = 0
    ) {
        self.lastBackupTimestamp = lastBackupTimestamp
        self.totalSizeMb = totalSizeMb
        self.cloudAccount = cloudAccount
        self.autoBackupFrequency = autoBackupFrequency
        self.backupNetwork = backupNetwork
        self.includeVideos = includeVideos
        self.videosSizeMb = videosSizeMb
        self.isE2EEEncrypted = isE2EEEncrypted
        self.isBackingUp = isBackingUp
        self.backupProgressPercent = backupProgressPercent
    }
}

public struct SupportContactInfo: Codable, Equatable {
    public var hotlinePhone: String
    public var supportEmail: String
    public var supportHours: String
    public var officeAddress: String

    public init(
        hotlinePhone: String = "+977-1-4567890",
        supportEmail: String = "support@evrry.com.np",
        supportHours: String = "24/7 Hotline & Emergency Dispatch",
        officeAddress: String = "Evrry HQ, Pulchowk, Lalitpur, Nepal"
    ) {
        self.hotlinePhone = hotlinePhone
        self.supportEmail = supportEmail
        self.supportHours = supportHours
        self.officeAddress = officeAddress
    }
}

public struct AppSecuritySettings: Codable, Equatable {
    public var biometricLockEnabled: Bool
    public var blockedContactsCount: Int
    public var activeSessionsCount: Int

    public init(
        biometricLockEnabled: Bool = false,
        blockedContactsCount: Int = 0,
        activeSessionsCount: Int = 1
    ) {
        self.biometricLockEnabled = biometricLockEnabled
        self.blockedContactsCount = blockedContactsCount
        self.activeSessionsCount = activeSessionsCount
    }
}

public struct DataManagementState: Codable, Equatable {
    public var cacheSizeMb: Double
    public var aiMemoryFactsCount: Int
    public var isExportingData: Bool
    public var exportDownloadUrl: String?
    public var isCacheCleared: Bool

    public init(
        cacheSizeMb: Double = 14.8,
        aiMemoryFactsCount: Int = 3,
        isExportingData: Bool = false,
        exportDownloadUrl: String? = nil,
        isCacheCleared: Bool = false
    ) {
        self.cacheSizeMb = cacheSizeMb
        self.aiMemoryFactsCount = aiMemoryFactsCount
        self.isExportingData = isExportingData
        self.exportDownloadUrl = exportDownloadUrl
        self.isCacheCleared = isCacheCleared
    }
}

