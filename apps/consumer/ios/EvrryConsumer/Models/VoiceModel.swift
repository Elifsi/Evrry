//
//  VoiceModel.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  4 AI Voice Persona Models (Ellie, Rony, Jenny, Suka)
//

import SwiftUI

public struct VoiceAgentModel: Identifiable, Equatable {
    public let id: String
    public let name: String
    public let badge: String
    public let description: String
    public let emoji: String
    public let gender: String
    public let colorHex: UInt
    
    public init(
        id: String,
        name: String,
        badge: String,
        description: String,
        emoji: String = "⚡",
        gender: String = "Female",
        colorHex: UInt = 0x005EFF
    ) {
        self.id = id
        self.name = name
        self.badge = badge
        self.description = description
        self.emoji = emoji
        self.gender = gender
        self.colorHex = colorHex
    }
}

public struct VoiceAgentRegistry {
    public static let ellie = VoiceAgentModel(
        id = "ellie",
        name = "Ellie",
        badge = "Everyday Guide",
        description = "Youthful, energetic & quick — food orders, rides & daily errands in Kathmandu",
        emoji = "⚡",
        gender = "Female",
        colorHex = 0x005EFF
    )
    
    public static let rony = VoiceAgentModel(
        id = "rony",
        name = "Rony",
        badge = "Executive Concierge",
        description = "Deep baritone, authoritative & polite — fare negotiation & corporate coordination",
        emoji = "👔",
        gender = "Male",
        colorHex = 0x4F46E5
    )
    
    public static let jenny = VoiceAgentModel(
        id = "jenny",
        name = "Jenny",
        badge = "Grocery Companion",
        description = "Warm, sweet & hospitable — fresh groceries, kitchen essentials & family care",
        emoji = "🌸",
        gender = "Female",
        colorHex = 0xEC4899
    )
    
    public static let suka = VoiceAgentModel(
        id = "suka",
        name = "Suka",
        badge = "Evening Calmer",
        description = "Calm, soothing & deeply patient — late night rides, comfort food & mindful unwinding",
        emoji = "🌙",
        gender = "Male",
        colorHex = 0x0D9488
    )
    
    public static let allModels: [VoiceAgentModel] = [ellie, rony, jenny, suka]
    
    // Backward compatibility aliases
    public static let ellieMini = ellie
    public static let elliePro = rony
    
    public static func getById(_ id: String?) -> VoiceAgentModel {
        switch id?.lowercased().trimmingCharacters(in: .whitespacesAndNewlines) {
        case "rony": return rony
        case "jenny": return jenny
        case "suka": return suka
        case "ellie_pro", "pro": return rony
        default: return ellie
        }
    }
}

