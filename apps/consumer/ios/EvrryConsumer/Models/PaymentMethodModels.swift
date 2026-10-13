//
//  PaymentMethodModels.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Domain Models for Saved Payment Instruments in Evrry Super App
//  Supports: eSewa, Khalti, Fonepay Direct, Visa/Mastercard, COD
//

import Foundation
import SwiftUI

public enum SavedPaymentType: String, CaseIterable, Identifiable, Codable {
    case esewa = "eSewa Mobile Wallet"
    case khalti = "Khalti Digital Wallet"
    case fonepay = "Fonepay Direct Interbank"
    case card = "Debit / Credit Card"
    case cod = "Cash on Delivery"

    public var id: String { rawValue }

    public var brandColor: Color {
        switch self {
        case .esewa: return Color(red: 0.38, green: 0.73, blue: 0.27)
        case .khalti: return Color(red: 0.36, green: 0.18, blue: 0.57)
        case .fonepay: return Color(red: 0.83, green: 0.18, blue: 0.18)
        case .card: return Color(red: 0.12, green: 0.23, blue: 0.54)
        case .cod: return Color(red: 0.28, green: 0.33, blue: 0.41)
        }
    }

    public var iconName: String {
        switch self {
        case .esewa: return "wallet.pass.fill"
        case .khalti: return "sparkles.rectangle.stack.fill"
        case .fonepay: return "building.columns.fill"
        case .card: return "creditcard.fill"
        case .cod: return "banknote.fill"
        }
    }
}

public struct SavedPaymentMethod: Identifiable, Codable {
    public let id: String
    public let type: SavedPaymentType
    public var title: String
    public var subtitle: String
    public var isDefault: Bool
    public var linkedAt: String

    public init(
        id: String = UUID().uuidString,
        type: SavedPaymentType,
        title: String,
        subtitle: String,
        isDefault: Bool = false,
        linkedAt: String = "Active"
    ) {
        self.id = id
        self.type = type
        self.title = title
        self.subtitle = subtitle
        self.isDefault = isDefault
        self.linkedAt = linkedAt
    }
}

public class SavedPaymentMethodStore: ObservableObject {
    public static let shared = SavedPaymentMethodStore()

    @Published public var paymentMethods: [SavedPaymentMethod] = [
        SavedPaymentMethod(
            id: "pm_esewa_default",
            type: .esewa,
            title: "eSewa Mobile Wallet",
            subtitle: "+977 9812345678",
            isDefault: true,
            linkedAt: "Verified"
        ),
        SavedPaymentMethod(
            id: "pm_khalti_1",
            type: .khalti,
            title: "Khalti Digital Wallet",
            subtitle: "+977 9812345678",
            isDefault: false,
            linkedAt: "Verified"
        ),
        SavedPaymentMethod(
            id: "pm_fonepay_1",
            type: .fonepay,
            title: "Fonepay Direct (Nabil Bank)",
            subtitle: "Account •••• 5821",
            isDefault: false,
            linkedAt: "Verified"
        ),
        SavedPaymentMethod(
            id: "pm_card_1",
            type: .card,
            title: "Visa Global Debit Card",
            subtitle: "•••• 4242 · Exp 08/28",
            isDefault: false,
            linkedAt: "Active"
        )
    ]

    public func delete(id: String) {
        paymentMethods.removeAll { $0.id == id }
        if !paymentMethods.isEmpty && !paymentMethods.contains(where: { $0.isDefault }) {
            paymentMethods[0].isDefault = true
        }
    }

    public func setDefault(id: String) {
        for index in paymentMethods.indices {
            paymentMethods[index].isDefault = (paymentMethods[index].id == id)
        }
    }

    public func add(method: SavedPaymentMethod) {
        if method.isDefault {
            for index in paymentMethods.indices {
                paymentMethods[index].isDefault = false
            }
        }
        paymentMethods.append(method)
    }
}
