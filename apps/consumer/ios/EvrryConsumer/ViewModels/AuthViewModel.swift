//
//  AuthViewModel.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Authentication ViewModel (Nepal Phone + WhatsApp OTP / SMS)
//

import SwiftUI
import Combine

@MainActor
public final class AuthViewModel: ObservableObject {
    @Published public var state: AuthScreenState = .splash
    @Published public var phoneNumber: String = ""
    @Published public var selectedChannel: OtpChannel = .whatsapp
    @Published public var otpCode: String = ""
    @Published public var isLoading: Bool = false
    @Published public var errorMessage: String? = nil
    
    public init() {}
    
    public func finishSplash(hasSeenOnboarding: Bool = false) {
        self.state = hasSeenOnboarding ? .idle : .onboarding
    }
    
    public func finishOnboarding() {
        self.state = .idle
    }
    
    public func signInWithGoogle(onSuccess: @escaping () -> Void = {}) {
        self.isLoading = true
        self.errorMessage = nil
        Task {
            try? await Task.sleep(nanoseconds: 600_000_000)
            self.isLoading = false
            let user = UserProfile(
                id: "usr_google_nepal",
                phone: "+9779812345678",
                fullName: "Rahul Sharma",
                username: "rahul_google"
            )
            self.state = .authenticated(user: user)
            onSuccess()
        }
    }
    
    public func signInWithApple(onSuccess: @escaping () -> Void = {}) {
        self.isLoading = true
        self.errorMessage = nil
        Task {
            try? await Task.sleep(nanoseconds: 600_000_000)
            self.isLoading = false
            let user = UserProfile(
                id: "usr_apple_nepal",
                phone: "+9779812345678",
                fullName: "Rahul Sharma",
                username: "rahul_apple"
            )
            self.state = .authenticated(user: user)
            onSuccess()
        }
    }
    
    public func signInWithEmail(_ email: String, onSuccess: @escaping () -> Void = {}) {
        self.isLoading = true
        self.errorMessage = nil
        Task {
            try? await Task.sleep(nanoseconds: 600_000_000)
            self.isLoading = false
            let prefix = email.components(separatedBy: "@").first ?? "user"
            let user = UserProfile(
                id: "usr_email_nepal",
                phone: "+9779812345678",
                fullName: prefix.capitalized,
                username: prefix
            )
            self.state = .authenticated(user: user)
            onSuccess()
        }
    }
    
    public func signInWithPassword(identifier: String, password: String, onSuccess: @escaping () -> Void = {}) {
        self.isLoading = true
        self.errorMessage = nil
        Task {
            try? await Task.sleep(nanoseconds: 500_000_000)
            self.isLoading = false
            let isEmail = identifier.contains("@")
            let prefix = isEmail ? (identifier.components(separatedBy: "@").first ?? "user") : "user"
            let phone = isEmail ? "+9779812345678" : (identifier.hasPrefix("+") ? identifier : "+977\(identifier)")
            let user = UserProfile(
                id: "usr_pass_\(Int(Date().timeIntervalSince1970))",
                phone: phone,
                fullName: prefix.capitalized,
                username: prefix
            )
            self.state = .authenticated(user: user)
            onSuccess()
        }
    }
    
    public func signUpWithPassword(fullName: String, identifier: String, password: String, onSuccess: @escaping () -> Void = {}) {
        self.isLoading = true
        self.errorMessage = nil
        Task {
            try? await Task.sleep(nanoseconds: 500_000_000)
            self.isLoading = false
            let isEmail = identifier.contains("@")
            let prefix = isEmail ? (identifier.components(separatedBy: "@").first ?? "user") : "user"
            let phone = isEmail ? "+9779812345678" : (identifier.hasPrefix("+") ? identifier : "+977\(identifier)")
            let user = UserProfile(
                id: "usr_pass_\(Int(Date().timeIntervalSince1970))",
                phone: phone,
                fullName: fullName.isEmpty ? prefix.capitalized : fullName,
                username: prefix
            )
            self.state = .authenticated(user: user)
            onSuccess()
        }
    }
    
    public func updatePhoneNumber(_ text: String) {
        let filtered = text.filter { $0.isNumber }
        self.phoneNumber = String(filtered.prefix(10))
    }
    
    public func selectChannel(_ channel: OtpChannel) {
        self.selectedChannel = channel
    }
    
    public func updateOtp(_ text: String) {
        let filtered = text.filter { $0.isNumber }
        self.otpCode = String(filtered.prefix(6))
    }
    
    public func requestOtp() {
        guard phoneNumber.count == 10 else {
            self.errorMessage = "Please enter a valid 10-digit Nepal mobile number (e.g. 9812345678)."
            return
        }
        
        self.isLoading = true
        self.errorMessage = nil
        
        Task {
            try? await Task.sleep(nanoseconds: 500_000_000)
            self.isLoading = false
            self.state = .otpRequested(
                phone: "+977\(phoneNumber)",
                channel: selectedChannel,
                retryAfterSeconds: 45,
                mockOtp: "123456"
            )
        }
    }
    
    public func verifyOtp(code: String? = nil, onSuccess: @escaping () -> Void = {}) {
        let targetCode = code ?? otpCode
        guard targetCode.count == 6 else {
            self.errorMessage = "Please enter the complete 6-digit verification code."
            return
        }
        
        self.isLoading = true
        self.errorMessage = nil
        
        Task {
            try? await Task.sleep(nanoseconds: 600_000_000)
            self.isLoading = false
            let user = UserProfile(
                id: "usr_nepal_\(phoneNumber)",
                phone: "+977\(phoneNumber.isEmpty ? "9812345678" : phoneNumber)",
                fullName = "Rahul Sharma",
                loyaltyPoints: 420
            )
            self.userProfile = user
            self.state = .authenticated(user: user)
            onSuccess()
        }
    }
    
    @Published public var userProfile: UserProfile = UserProfile()

    public func updateProfile(fullName: String, username: String, bioStatus: String, avatarUrl: String? = nil) {
        let sanitized = username.replacingOccurrences(of: "@", with: "")
        self.userProfile.fullName = fullName
        self.userProfile.username = sanitized
        self.userProfile.bioStatus = bioStatus
        if let avatarUrl = avatarUrl {
            self.userProfile.avatarUrl = avatarUrl
        }
    }

    public func updatePrivacySettings(allowPhoneDiscovery: Bool, readReceiptsEnabled: Bool, lastSeenPrivacy: String) {
        self.userProfile.allowPhoneDiscovery = allowPhoneDiscovery
        self.userProfile.readReceiptsEnabled = readReceiptsEnabled
        self.userProfile.lastSeenPrivacy = lastSeenPrivacy
    }

    // --- WHATSAPP-GRADE CHAT BACKUP (ICLOUD & GOOGLE DRIVE) ---
    @Published public var chatBackupState: ChatBackupState = ChatBackupState()

    public func startChatBackup() {
        guard !chatBackupState.isBackingUp else { return }
        chatBackupState.isBackingUp = true
        chatBackupState.backupProgressPercent = 10

        Task {
            try? await Task.sleep(nanoseconds: 400_000_000)
            self.chatBackupState.backupProgressPercent = 45
            try? await Task.sleep(nanoseconds: 500_000_000)
            self.chatBackupState.backupProgressPercent = 85
            try? await Task.sleep(nanoseconds: 400_000_000)
            self.chatBackupState.isBackingUp = false
            self.chatBackupState.backupProgressPercent = 100
            self.chatBackupState.lastBackupTimestamp = "Just now"
        }
    }

    public func updateBackupFrequency(_ frequency: String) {
        self.chatBackupState.autoBackupFrequency = frequency
    }

    public func updateIncludeVideos(_ include: Bool) {
        self.chatBackupState.includeVideos = include
    }

    // --- APP SECURITY & BIOMETRIC LOCK (FACE ID) ---
    @Published public var appSecurity: AppSecuritySettings = AppSecuritySettings()

    public func toggleBiometricLock(_ enabled: Bool) {
        self.appSecurity.biometricLockEnabled = enabled
    }

    // --- CUSTOMER SUPPORT ---
    @Published public var supportInfo: SupportContactInfo = SupportContactInfo()

    // --- DATA MANAGEMENT & ERASURE (GDPR / APPLE 5.1.1(v) DATA PRIVACY) ---
    @Published public var dataManagementState: DataManagementState = DataManagementState()

    public func clearLocalCache() {
        self.dataManagementState.cacheSizeMb = 0.0
        self.dataManagementState.isCacheCleared = true
    }

    public func clearAiMemoryFacts() {
        self.dataManagementState.aiMemoryFactsCount = 0
    }

    public func requestDataExport() {
        guard !dataManagementState.isExportingData else { return }
        dataManagementState.isExportingData = true

        Task {
            try? await Task.sleep(nanoseconds: 1_200_000_000)
            self.dataManagementState.isExportingData = false
            self.dataManagementState.exportDownloadUrl = "https://evrry.com/api/user/export-archive.json"
        }
    }

    // --- ACCOUNT DELETION (APPLE APP STORE GUIDELINE 5.1.1(v) COMPLIANCE) ---
    public func deleteAccount() {
        self.userProfile = UserProfile(
            id: "",
            phone: "",
            fullName: "Deleted User",
            username: "deleted",
            avatarUrl: nil
        )
        self.dataManagementState = DataManagementState(cacheSizeMb: 0.0, aiMemoryFactsCount = 0, isCacheCleared: true)
        resetToPhoneInput()
    }

    public func resetToPhoneInput() {
        self.otpCode = ""
        self.errorMessage = nil
        self.state = .idle
    }
}
