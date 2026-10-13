import Foundation
import Combine

public final class CameraViewModel: ObservableObject {

    @Published public var snaps: [SnapItem] = [
        SnapItem(
            id: "snap-patan",
            imageUrl: "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800&auto=format&fit=crop&q=80",
            filterName: "Kathmandu Gold",
            location: "Patan Durbar Square, Lalitpur",
            caption: "Golden sunset at Patan Krishna Mandir ✨",
            createdAt: Date().addingTimeInterval(-3600 * 4)
        ),
        SnapItem(
            id: "snap-everest",
            imageUrl: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=80",
            filterName: "Everest Ice",
            location: "Nagarkot Sunrise Point",
            caption: "Morning glow over the snow peaks 🏔️",
            createdAt: Date().addingTimeInterval(-3600 * 24)
        ),
        SnapItem(
            id: "snap-thamel",
            imageUrl: "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&auto=format&fit=crop&q=80",
            filterName: "Cyberpunk Neon",
            location: "Thamel Night Market",
            caption: "Live music and aromatic coffee ☕🎶",
            createdAt: Date().addingTimeInterval(-3600 * 48)
        )
    ]

    @Published public var cameraRoll: [SnapItem] = [
        SnapItem(
            id: "roll-pokhara",
            imageUrl: "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=800&auto=format&fit=crop&q=80",
            filterName: "Camera Roll",
            location: "Phewa Lake, Pokhara",
            caption: "Boating on peaceful Phewa waters ⛵",
            createdAt: Date().addingTimeInterval(-3600 * 72)
        ),
        SnapItem(
            id: "roll-momo",
            imageUrl: "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=800&auto=format&fit=crop&q=80",
            filterName: "Camera Roll",
            location: "Jhamsikhel, Lalitpur",
            caption: "Hot steamed buff momo with spicy timur jhol 🥟🔥",
            createdAt: Date().addingTimeInterval(-3600 * 96)
        )
    ]

    @Published public var lockerSnaps: [SnapItem] = [
        SnapItem(
            id: "locker-itinerary",
            imageUrl: "https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=800&auto=format&fit=crop&q=80",
            filterName: "Private Vault",
            location: "Personal Vault",
            caption: "Private travel receipts & flight passes 🔒",
            createdAt: Date().addingTimeInterval(-3600 * 120),
            isLocked: true
        )
    ]

    @Published public var lockerPin: String = "1234"
    @Published public var isLockerUnlocked: Bool = false
    @Published public var detectedQr: QrScanResult? = nil

    public let lenses: [CameraFilterLens] = [
        CameraFilterLens(id: "normal", name: "Normal", subtitle: "Natural real colors"),
        CameraFilterLens(id: "gold", name: "Kathmandu Gold", subtitle: "Warm heritage sunshine"),
        CameraFilterLens(id: "everest", name: "Everest Ice", subtitle: "Cool mountain contrast"),
        CameraFilterLens(id: "vintage", name: "Vintage 90s", subtitle: "Warm nostalgic retro"),
        CameraFilterLens(id: "cyber", name: "Cyberpunk Neon", subtitle: "High saturation vibrant"),
        CameraFilterLens(id: "noir", name: "Noir B&W", subtitle: "Dramatic black and white")
    ]

    @Published public var selectedLens: CameraFilterLens

    public init() {
        self.selectedLens = CameraFilterLens(id: "normal", name: "Normal", subtitle: "Natural real colors")
    }

    public func selectLens(_ lens: CameraFilterLens) {
        self.selectedLens = lens
    }

    @discardableResult
    public func addSnap(imageUrl: String, filterName: String = "Normal", location: String = "Kathmandu, Nepal", caption: String = "") -> SnapItem {
        let snap = SnapItem(
            imageUrl: imageUrl,
            filterName: filterName,
            location: location,
            caption: caption,
            createdAt: Date(),
            isLocked: false
        )
        snaps.insert(snap, at: 0)
        return snap
    }

    public func deleteSnap(id: String) {
        snaps.removeAll { $0.id == id }
    }

    @discardableResult
    public func addToCameraRoll(imageUrl: String, caption: String = "") -> SnapItem {
        let rollItem = SnapItem(
            imageUrl: imageUrl,
            filterName: "Camera Roll",
            location: "Device Storage",
            caption: caption,
            createdAt: Date(),
            isLocked: false
        )
        cameraRoll.insert(rollItem, at: 0)
        return rollItem
    }

    public func deleteCameraRoll(id: String) {
        cameraRoll.removeAll { $0.id == id }
    }

    public func setLockerPin(_ newPin: String) {
        self.lockerPin = newPin.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    public func unlockLocker(pin: String) -> Bool {
        if pin.trimmingCharacters(in: .whitespacesAndNewlines) == lockerPin {
            isLockerUnlocked = true
            return true
        }
        return false
    }

    public func lockLocker() {
        isLockerUnlocked = false
    }

    public func moveToLocker(id: String, fromTab: MemoryTab) {
        if fromTab == .snaps {
            if let index = snaps.firstIndex(where: { $0.id == id }) {
                var item = snaps.remove(at: index)
                item.isLocked = true
                lockerSnaps.insert(item, at: 0)
            }
        } else {
            if let index = cameraRoll.firstIndex(where: { $0.id == id }) {
                var item = cameraRoll.remove(at: index)
                item.isLocked = true
                lockerSnaps.insert(item, at: 0)
            }
        }
    }

    public func restoreFromLocker(id: String) {
        if let index = lockerSnaps.firstIndex(where: { $0.id == id }) {
            var item = lockerSnaps.remove(at: index)
            item.isLocked = false
            snaps.insert(item, at: 0)
        }
    }

    public func deleteLockerSnap(id: String) {
        lockerSnaps.removeAll { $0.id == id }
    }

    public func detectQr(raw: String) {
        self.detectedQr = QrParser.parse(raw: raw)
    }

    public func clearDetectedQr() {
        self.detectedQr = nil
    }
}
