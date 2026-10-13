//
//  CallModels.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  WhatsApp-Grade WebRTC VoIP Voice & Video Call Domain Models for iOS
//

import Foundation

public enum CallType: String, Codable {
    case audio = "AUDIO"
    case video = "VIDEO"
}

public enum CallState: String, Codable {
    case connecting = "CONNECTING"
    case ringing = "RINGING"
    case connected = "CONNECTED"
    case ended = "ENDED"
}

public enum CallDirection: String, Codable {
    case incoming = "INCOMING"
    case outgoing = "OUTGOING"
    case missed = "MISSED"
}

public enum CallLogStatus: String, Codable {
    case completed = "COMPLETED"
    case missed = "MISSED"
    case rejected = "REJECTED"
    case failed = "FAILED"
    case cancelled = "CANCELLED"
}

public struct ActiveCallSession: Identifiable, Codable, Equatable {
    public let id: String
    public let chatId: String
    public let calleeName: String
    public let calleeAvatar: String?
    public let calleePhone: String?
    public let callType: CallType
    public var state: CallState
    public var durationSeconds: Int
    public var isMuted: Bool
    public var isSpeakerOn: Bool
    public var powerIsSpeakerOn: Bool { isSpeakerOn }
    public var isVideoEnabled: Bool
    public var isFrontCamera: Bool

    public init(
        id: String = UUID().uuidString,
        chatId: String,
        calleeName: String,
        calleeAvatar: String? = nil,
        calleePhone: String? = nil,
        callType: CallType = .audio,
        state: CallState = .connecting,
        durationSeconds: Int = 0,
        isMuted: Bool = false,
        isSpeakerOn: Bool = false,
        isVideoEnabled: Bool = true,
        isFrontCamera: Bool = true
    ) {
        self.id = id
        self.chatId = chatId
        self.calleeName = calleeName
        self.calleeAvatar = calleeAvatar
        self.calleePhone = calleePhone
        self.callType = callType
        self.state = state
        self.durationSeconds = durationSeconds
        self.isMuted = isMuted
        self.isSpeakerOn = isSpeakerOn
        self.isVideoEnabled = isVideoEnabled
        self.isFrontCamera = isFrontCamera
    }
}

public struct CallLogItem: Identifiable, Codable, Equatable {
    public let id: String
    public let chatId: String?
    public let contactName: String
    public let contactAvatar: String?
    public let contactPhone: String?
    public let callType: CallType
    public let direction: CallDirection
    public let status: CallLogStatus
    public let durationSeconds: Int
    public let timestampFormatted: String
    public let isDriverOrMerchant: Bool
    public let subtitle: String?

    public init(
        id: String = UUID().uuidString,
        chatId: String? = nil,
        contactName: String,
        contactAvatar: String? = nil,
        contactPhone: String? = nil,
        callType: CallType = .audio,
        direction: CallDirection = .outgoing,
        status: CallLogStatus = .completed,
        durationSeconds: Int = 0,
        timestampFormatted: String,
        isDriverOrMerchant: Bool = false,
        subtitle: String? = nil
    ) {
        self.id = id
        self.chatId = chatId
        self.contactName = contactName
        self.contactAvatar = contactAvatar
        self.contactPhone = contactPhone
        self.callType = callType
        self.direction = direction
        self.status = status
        self.durationSeconds = durationSeconds
        self.timestampFormatted = timestampFormatted
        self.isDriverOrMerchant = isDriverOrMerchant
        self.subtitle = subtitle
    }
}
