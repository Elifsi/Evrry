//
//  AuthState.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Authentication and User Session Domain Models
//

import Foundation

public enum OtpChannel: String, Codable, CaseIterable {
    case whatsapp = "whatsapp"
    case sms = "sms"
    
    public var label: String {
        switch self {
        case .whatsapp: return "WhatsApp"
        case .sms: return "Domestic SMS"
        }
    }
    
    public var badge: String {
        switch self {
        case .whatsapp: return "Instant · Free"
        case .sms: return "Backup"
        }
    }
}

public struct UserProfile: Codable, Identifiable {
    public let id: String
    public let phone: String
    public var fullName: String
    public var username: String
    public var avatarUrl: String?
    public var bioStatus: String
    public var loyaltyPoints: Int
    public var kycVerified: Bool
    public var allowPhoneDiscovery: Bool
    public var readReceiptsEnabled: Bool
    public var lastSeenPrivacy: String
    
    public init(
        id: String = "usr_nepal_001",
        phone: String = "+9779812345678",
        fullName: String = "Rahul Sharma",
        username: String = "rahul_evrry",
        avatarUrl: String? = nil,
        bioStatus: String = "Available on Evrry",
        loyaltyPoints: Int = 420,
        kycVerified: Bool = true,
        allowPhoneDiscovery: Bool = false,
        readReceiptsEnabled: Bool = true,
        lastSeenPrivacy: String = "Everyone"
    ) {
        self.id = id
        self.phone = phone
        self.fullName = fullName
        self.username = username
        self.avatarUrl = avatarUrl
        self.bioStatus = bioStatus
        self.loyaltyPoints = loyaltyPoints
        self.kycVerified = kycVerified
        self.allowPhoneDiscovery = allowPhoneDiscovery
        self.readReceiptsEnabled = readReceiptsEnabled
        self.lastSeenPrivacy = lastSeenPrivacy
    }
}

public enum AuthScreenState: Equatable {
    case splash
    case onboarding
    case idle
    case otpRequested(phone: String, channel: OtpChannel, retryAfterSeconds: Int, mockOtp: String?)
    case authenticated(user: UserProfile)
    case error(message: String)
    
    public static func == (lhs: AuthScreenState, rhs: AuthScreenState) -> Bool {
        switch (lhs, rhs) {
        case (.splash, .splash): return true
        case (.onboarding, .onboarding): return true
        case (.idle, .idle): return true
        case (.otpRequested(let p1, let c1, _, _), .otpRequested(let p2, let c2, _, _)):
            return p1 == p2 && c1 == c2
        case (.authenticated(let u1), .authenticated(let u2)):
            return u1.id == u2.id
        case (.error(let m1), .error(let m2)):
            return m1 == m2
        default: return false
        }
    }
}
