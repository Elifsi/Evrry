import Foundation

public struct SnapItem: Identifiable, Hashable, Codable {
    public let id: String
    public let imageUrl: String
    public let filterName: String
    public let location: String
    public let caption: String
    public let createdAt: Date
    public var isLocked: Bool

    public init(
        id: String = UUID().uuidString,
        imageUrl: String,
        filterName: String = "Normal",
        location: String = "Kathmandu, Nepal",
        caption: String = "",
        createdAt: Date = Date(),
        isLocked: Bool = false
    ) {
        self.id = id
        self.imageUrl = imageUrl
        self.filterName = filterName
        self.location = location
        self.caption = caption
        self.createdAt = createdAt
        self.isLocked = isLocked
    }
}

public enum MemoryTab: String, CaseIterable, Identifiable {
    case snaps = "snaps"
    case cameraRoll = "cameraRoll"
    case myLocker = "myLocker"

    public var id: String { rawValue }

    public var title: String {
        switch self {
        case .snaps: return "Snaps"
        case .cameraRoll: return "Camera Roll"
        case .myLocker: return "My Locker"
        }
    }
}

public struct QrScanResult: Identifiable, Hashable {
    public let id: String
    public let raw: String
    public let type: String // "fonepay", "contact", "restaurant", "url", "text"
    public let title: String
    public let subtitle: String
    public let actionLabel: String
    public let target: String
    public let amount: Double?

    public init(
        raw: String,
        type: String,
        title: String,
        subtitle: String,
        actionLabel: String,
        target: String,
        amount: Double? = nil
    ) {
        self.id = UUID().uuidString
        self.raw = raw
        self.type = type
        self.title = title
        self.subtitle = subtitle
        self.actionLabel = actionLabel
        self.target = target
        self.amount = amount
    }
}

public struct CameraFilterLens: Identifiable, Hashable {
    public let id: String
    public let name: String
    public let subtitle: String

    public init(id: String, name: String, subtitle: String) {
        self.id = id
        self.name = name
        self.subtitle = subtitle
    }
}

public enum QrParser {
    public static func parse(raw: String) -> QrScanResult {
        let trimmed = raw.trimmingCharacters(in: .whitespacesAndNewlines)

        // 1. Fonepay & Nepal QR (EMVCo standards)
        if trimmed.hasPrefix("000201") ||
            trimmed.lowercased().contains("fonepay") ||
            trimmed.lowercased().contains("nepalqr") {

            var merchant = "Himalayan Java Coffee"
            var amount: Double = 350.0

            if let amtRange = trimmed.range(of: "(?i)amount=([0-9.]+)", options: .regularExpression) {
                let sub = String(trimmed[amtRange])
                let parts = sub.components(separatedBy: "=")
                if parts.count > 1, let val = Double(parts[1]) {
                    amount = val
                }
            }

            if let merRange = trimmed.range(of: "(?i)merchant=([^&]+)", options: .regularExpression) {
                let sub = String(trimmed[merRange])
                let parts = sub.components(separatedBy: "=")
                if parts.count > 1 {
                    merchant = parts[1].removingPercentEncoding?.replacingOccurrences(of: "+", with: " ") ?? parts[1]
                }
            }

            return QrScanResult(
                raw: trimmed,
                type: "fonepay",
                title: "Fonepay Merchant QR",
                subtitle: "\(merchant) · Rs \(Int(amount))",
                actionLabel: "Pay with Fonepay",
                target: "cart",
                amount: amount
            )
        }

        // 2. Evrry Friend Contact QR
        if trimmed.hasPrefix("evrry://user/") ||
            trimmed.hasPrefix("evrry://chat/") ||
            trimmed.contains("evrry.com/u/") ||
            trimmed.hasPrefix("@") {

            let handle = trimmed
                .replacingOccurrences(of: "evrry://user/", with: "")
                .replacingOccurrences(of: "evrry://chat/", with: "")
                .replacingOccurrences(of: "https://evrry.com/u/", with: "")
                .replacingOccurrences(of: "@", with: "")

            let name = handle.prefix(1).capitalized + handle.dropFirst()

            return QrScanResult(
                raw: trimmed,
                type: "contact",
                title: "Evrry User · @\(handle)",
                subtitle: "\(name) Sharma · Available for Chat & Split Pay",
                actionLabel: "Open Chat",
                target: "chat_room"
            )
        }

        // 3. Restaurant Dining Table QR
        if trimmed.hasPrefix("evrry://restaurant/") || trimmed.contains("table=") {
            var tableNum = "4"
            if let tblRange = trimmed.range(of: "(?i)table=([0-9A-Za-z]+)", options: .regularExpression) {
                let sub = String(trimmed[tblRange])
                let parts = sub.components(separatedBy: "=")
                if parts.count > 1 {
                    tableNum = parts[1]
                }
            }

            return QrScanResult(
                raw: trimmed,
                type: "restaurant",
                title: "Table Service Order",
                subtitle: "Roadhouse Cafe (Pulchowk) · Table #\(tableNum)",
                actionLabel: "View Digital Menu",
                target: "restaurant_detail"
            )
        }

        // 4. Standard Web URL
        if trimmed.hasPrefix("http://") || trimmed.hasPrefix("https://") {
            let clean = trimmed
                .replacingOccurrences(of: "https://", with: "")
                .replacingOccurrences(of: "http://", with: "")

            return QrScanResult(
                raw: trimmed,
                type: "url",
                title: "Website Link",
                subtitle: clean,
                actionLabel: "Open Link",
                target: trimmed
            )
        }

        // 5. Plain Text Fallback
        return QrScanResult(
            raw: trimmed,
            type: "text",
            title: "QR Code Content",
            subtitle: trimmed.count > 36 ? String(trimmed.prefix(33)) + "..." : trimmed,
            actionLabel: "Copy Text",
            target: trimmed
        )
    }
}
