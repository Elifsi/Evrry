package com.elifsi.evrry.consumer.model

import kotlinx.serialization.Serializable

/**
 * WhatsApp-Grade Chat Backup, Security, and Customer Support Domain Models
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */

@Serializable
data class ChatBackupState(
    val lastBackupTimestamp: String = "Today, 04:15 AM",
    val totalSizeMb: Double = 42.8,
    val googleDriveAccount: String = "rahul.sharma@gmail.com",
    val autoBackupFrequency: String = "Daily", // "Daily", "Weekly", "Monthly", "Off"
    val backupNetwork: String = "Wi-Fi only",   // "Wi-Fi only", "Wi-Fi or cellular"
    val includeVideos: Boolean = true,
    val videosSizeMb: Double = 18.2,
    val isE2EEEncrypted: Boolean = true,
    val isBackingUp: Boolean = false,
    val backupProgressPercent: Int = 0
)

@Serializable
data class SupportContactInfo(
    val hotlinePhone: String = "+977-1-4567890",
    val supportEmail: String = "support@evrry.com.np",
    val supportHours: String = "24/7 Hotline & Emergency Dispatch",
    val officeAddress: String = "Evrry HQ, Pulchowk, Lalitpur, Nepal"
)

@Serializable
data class AppSecuritySettings(
    val biometricLockEnabled: Boolean = false,
    val blockedContactsCount: Int = 0,
    val activeSessionsCount: Int = 1
)

@Serializable
data class DataManagementState(
    val cacheSizeMb: Double = 14.8,
    val aiMemoryFactsCount: Int = 3,
    val isExportingData: Boolean = false,
    val exportDownloadUrl: String? = null,
    val isCacheCleared: Boolean = false
)

@Serializable
data class LegalSection(
    val title: String,
    val content: String
)

@Serializable
data class AboutCompanyInfo(
    val appName: String = "Evrry Super App",
    val companyName: String = "Elifsi Technologies Private Limited",
    val registrationNumber: String = "REG-314982/080/081",
    val headquarters: String = "Pulchowk, Lalitpur, Bagmati Province, Nepal",
    val appVersion: String = "2.4.0 (Build 2026.10)",
    val website: String = "https://evrry.com",
    val techStackSummary: String = "Native Kotlin Jetpack Compose, Native Swift/SwiftUI, PostGIS, Supabase, WebRTC Signal Protocol E2EE",
    val copyright: String = "© 2026 Elifsi Technologies Private Limited. All rights reserved."
)

