//
//  VoiceModel.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Minimalist ChatGPT-style model switcher (Ellie Mini & Ellie Pro)
//

import SwiftUI

public struct VoiceAgentModel: Identifiable, Equatable {
    public let id: String
    public let name: String
    public let badge: String
    public let description: String
    
    public init(
        id: String,
        name: String,
        badge: String,
        description: String
    ) {
        self.id = id
        self.name = name
        self.badge = badge
        self.description = description
    }
}

public struct VoiceAgentRegistry {
    public static let ellieMini = VoiceAgentModel(
        id = "ellie_mini",
        name = "Ellie Mini",
        badge = "Fast · Everyday",
        description = "Lightweight, ultra-responsive model for instant food orders, rides, and daily tasks"
    )
    
    public static let elliePro = VoiceAgentModel(
        id = "ellie_pro",
        name = "Ellie Pro",
        badge = "Deep Reasoning",
        description = "Advanced model for multi-stop trip planning, custom inquiries, and complex itineraries"
    )
    
    public static let allModels: [VoiceAgentModel] = [ellieMini, elliePro]
    
    public static func getById(_ id: String?) -> VoiceAgentModel {
        switch id?.lowercased().trimmingCharacters(in: .whitespacesAndNewlines) {
        case "ellie_pro", "pro": return elliePro
        default: return ellieMini
        }
    }
}
