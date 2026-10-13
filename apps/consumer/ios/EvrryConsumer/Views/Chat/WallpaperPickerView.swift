//
//  WallpaperPickerView.swift
//  EvrryConsumer
//
//  WhatsApp-style chat wallpaper picker using SwiftUI gradient presets.
//  No external image assets — all presets are in-code gradient/colour combos.
//  Selection is forwarded to ChatViewModel.setWallpaper(chatId:url:) where
//  `url` is actually the preset id string (e.g. "dusk", "ocean").
//
//  Elifsi Technologies Private Limited

import SwiftUI
import PhotosUI

// MARK: - Data

struct WallpaperPreset: Identifiable {
    let id: String
    let label: String
    let colors: [Color]   // 1 = solid, 2+ = gradient
}

let wallpaperPresets: [WallpaperPreset] = [
    WallpaperPreset(id: "default",  label: "Default",  colors: [Color(red: 0.94, green: 0.96, blue: 0.98)]),
    WallpaperPreset(id: "dusk",     label: "Dusk",     colors: [Color(red: 0.40, green: 0.49, blue: 0.92), Color(red: 0.46, green: 0.29, blue: 0.64)]),
    WallpaperPreset(id: "sunrise",  label: "Sunrise",  colors: [Color(red: 1.0, green: 0.42, blue: 0.42), Color(red: 1.0, green: 0.90, blue: 0.43)]),
    WallpaperPreset(id: "ocean",    label: "Ocean",    colors: [Color(red: 0.0, green: 0.71, blue: 0.86), Color(red: 0.0, green: 0.51, blue: 0.69)]),
    WallpaperPreset(id: "forest",   label: "Forest",   colors: [Color(red: 0.07, green: 0.60, blue: 0.56), Color(red: 0.22, green: 0.94, blue: 0.49)]),
    WallpaperPreset(id: "rose",     label: "Rose",     colors: [Color(red: 1.0, green: 0.46, blue: 0.55), Color(red: 1.0, green: 0.49, blue: 0.70)]),
    WallpaperPreset(id: "midnight", label: "Midnight", colors: [Color(red: 0.06, green: 0.13, blue: 0.15), Color(red: 0.17, green: 0.38, blue: 0.44)]),
    WallpaperPreset(id: "peach",    label: "Peach",    colors: [Color(red: 1.0, green: 0.70, blue: 0.28), Color(red: 1.0, green: 0.55, blue: 0.58)]),
    WallpaperPreset(id: "lavender", label: "Lavender", colors: [Color(red: 0.91, green: 0.84, blue: 1.0)]),
    WallpaperPreset(id: "mint",     label: "Mint",     colors: [Color(red: 0.83, green: 0.93, blue: 0.85)]),
    WallpaperPreset(id: "sand",     label: "Sand",     colors: [Color(red: 0.96, green: 0.90, blue: 0.78)]),
    WallpaperPreset(id: "slate",    label: "Slate",    colors: [Color(red: 0.89, green: 0.91, blue: 0.94)]),
]

/// Resolve a preset id to its SwiftUI LinearGradient for use as a chat background.
func wallpaperGradient(for presetId: String?) -> LinearGradient {
    let preset = wallpaperPresets.first(where: { $0.id == presetId }) ?? wallpaperPresets[0]
    let stops = preset.colors.count == 1
        ? [preset.colors[0], preset.colors[0]]
        : preset.colors
    return LinearGradient(colors: stops, startPoint: .topLeading, endPoint: .bottomTrailing)
}

// MARK: - View

struct WallpaperPickerView: View {
    let chatId: String
    var currentPresetId: String?
    var onPick: (String) -> Void
    var onClose: () -> Void

    private let blue = Color.evrryPrimaryBlue
    private let columns = Array(repeating: GridItem(.flexible(), spacing: 10), count: 4)
    @State private var selectedPhotoItem: PhotosPickerItem? = nil

    var body: some View {
        VStack(spacing: 0) {
            // Header
            HStack {
                Text("Chat Wallpaper")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(Color(red: 0.04, green: 0.09, blue: 0.17))
                Spacer()
                Button(action: onClose) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.system(size: 22))
                        .foregroundColor(Color.gray.opacity(0.5))
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 14)
            .padding(.bottom, 4)

            Text("Choose a custom photo or preset for this conversation")
                .font(.system(size: 12))
                .foregroundColor(.gray)
                .padding(.horizontal, 16)
                .padding(.bottom, 8)

            // WhatsApp-style "My Photos" Option
            PhotosPicker(selection: $selectedPhotoItem, matching: .images) {
                HStack(spacing: 12) {
                    ZStack {
                        RoundedRectangle(cornerRadius: 8)
                            .fill(blue.opacity(0.12))
                            .frame(width: 36, height: 36)
                        Image(systemName: "photo.on.rectangle.angled")
                            .font(.system(size: 16))
                            .foregroundColor(blue)
                    }

                    VStack(alignment: .leading, spacing: 2) {
                        Text("My Photos")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(Color(red: 0.04, green: 0.09, blue: 0.17))
                        Text("Choose from your device library")
                            .font(.system(size: 11))
                            .foregroundColor(.gray)
                    }

                    Spacer()

                    Image(systemName: "chevron.right")
                        .font(.system(size: 12, weight: .semibold))
                        .foregroundColor(.gray)
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 10)
                .background(Color(red: 0.94, green: 0.96, blue: 1.0))
                .cornerRadius(12)
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 6)
            .onChange(of: selectedPhotoItem) { item in
                guard let item = item else { return }
                Task {
                    if let data = try? await item.loadTransferable(type: Data.self) {
                        let base64 = "data:image/jpeg;base64," + data.base64EncodedString()
                        await MainActor.run {
                            onPick(base64)
                        }
                    }
                }
            }

            Divider()
            Spacer().frame(height: 6)

            ScrollView {
                LazyVGrid(columns: columns, spacing: 10) {
                    ForEach(wallpaperPresets) { preset in
                        let isSelected = currentPresetId == preset.id
                        let gradient = wallpaperGradient(for: preset.id)

                        ZStack(alignment: .bottom) {
                            RoundedRectangle(cornerRadius: 12)
                                .fill(gradient)
                                .frame(height: 72)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 12)
                                        .stroke(isSelected ? blue : Color.gray.opacity(0.3), lineWidth: isSelected ? 3 : 0.5)
                                )

                            Text(preset.label)
                                .font(.system(size: 8, weight: .semibold))
                                .foregroundColor(.white)
                                .padding(.horizontal, 4)
                                .padding(.vertical, 2)
                                .frame(maxWidth: .infinity)
                                .background(Color.black.opacity(0.30))
                                .cornerRadius(8, corners: [.bottomLeft, .bottomRight])
                        }
                        .onTapGesture { onPick(preset.id) }
                        .shadow(color: Color.black.opacity(0.07), radius: 4, x: 0, y: 2)
                    }
                }
                .padding(.horizontal, 12)
                .padding(.bottom, 20)
            }
            .frame(height: 300)
        }
        .background(Color.white)
    }
}

// MARK: - Corner Radius helper

extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape(RoundedCorner(radius: radius, corners: corners))
    }
}

struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(
            roundedRect: rect,
            byRoundingCorners: corners,
            cornerRadii: CGSize(width: radius, height: radius)
        )
        return Path(path.cgPath)
    }
}
