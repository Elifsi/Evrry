//
//  PartnerSocialChat.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Realtime Driver-Passenger Chat & Calling Models
//

import SwiftUI

public enum MessageDeliveryStatus: String, Codable {
    case sending
    case sent
    case delivered
    case read
}

public struct PartnerSocialMessage: Identifiable, Equatable {
    public let id: String
    public let rideId: String
    public let senderId: String
    public let senderName: String
    public let text: String
    public let timestamp: Date
    public let isFromDriver: Bool
    public let status: MessageDeliveryStatus
    
    public init(
        id: String = UUID().uuidString,
        rideId: String,
        senderId: String,
        senderName: String,
        text: String,
        timestamp: Date = Date(),
        isFromDriver: Bool = true,
        status: MessageDeliveryStatus = .sent
    ) {
        self.id = id
        self.rideId = rideId
        self.senderId = senderId
        self.senderName = senderName
        self.text = text
        self.timestamp = timestamp
        self.isFromDriver = isFromDriver
        self.status = status
    }
}
