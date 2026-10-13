//
//  EmojiStickerSheetView.swift
//  EvrryConsumer
//
//  WhatsApp-style Emoji + Animated Emoji + Sticker bottom sheet.
//  Uses native SwiftUI – no paid SDK. Animated GIFs rendered via AsyncImage
//  (iOS 15+ WebP/GIF support through URLSession / ImageIO).
//  Open-source assets:
//    • Noto Animated Emoji (Apache 2.0) — animated GIF tab
//    • Twemoji (CC-BY 4.0) — sticker tab
//
//  Elifsi Technologies Private Limited

import SwiftUI

// MARK: - Data

private struct EmojiCategory: Identifiable {
    let id = UUID()
    let icon: String
    let label: String
    let emojis: [String]
}

private let emojiCategories: [EmojiCategory] = [
    EmojiCategory(icon: "😊", label: "Smileys",   emojis: ["😀","😃","😄","😁","😆","😅","😂","🤣","😊","😇","🙂","🙃","😉","😌","😍","🥰","😘","😗","😚","😋","😛","😝","😜","🤪","🤨","🧐","🤓","😎","🥸","🤩","🥳","😏","😒","😞","😔","😟","😕","🙁","☹️","😣","😢","😭","😤","😠","😡","🤬","🤯","😳","🥵","🥶"]),
    EmojiCategory(icon: "👋", label: "Gestures",  emojis: ["👍","👎","👏","🙌","🤝","🤞","✌️","🤘","👌","🫶","💪","🖐️","✋","🤙","☝️","👆","👇","👈","👉","🫡","🤜","🤛","✊","👊","🙏"]),
    EmojiCategory(icon: "❤️", label: "Hearts",    emojis: ["❤️","🧡","💛","💚","💙","💜","🖤","🤍","🤎","💔","💕","💞","💓","💗","💖","💘","💝","💟","❣️","💌"]),
    EmojiCategory(icon: "🎉", label: "Objects",   emojis: ["🎉","🎂","🎁","⭐","✨","🔥","💯","🎶","📷","💻","📱","⏰","💡","🔑","💰","🎯","🏆","🚀","🌈","☀️","🍕","🍔","☕","🍩","🍜","🍣"]),
    EmojiCategory(icon: "🌸", label: "Nature",    emojis: ["🌸","🌺","🌻","🌼","🌷","🍀","🌙","⭐","☁️","🌧️","❄️","🐶","🐱","🐼","🦋","🌊","🐘","🦁","🐯","🦊","🐸"]),
]

// Noto Animated Emoji – Apache 2.0
private let animatedEmojiUrls: [String] = [
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f600/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f602/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f525/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f389/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f499/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f44d/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f64f/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/2764_fe0f/512.gif",
]

// Twemoji SVGs – CC-BY 4.0
private let stickerUrls: [String] = [
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f600.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f602.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f970.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f525.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f389.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f499.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f44d.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f64f.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f4af.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/2764.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f308.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f381.svg",
]

// MARK: - Main View

struct EmojiStickerSheetView: View {
    var onEmojiPick: (String) -> Void
    var onStickerPick: (String) -> Void
    var onClose: () -> Void

    @State private var selectedTab = 0
    @State private var selectedEmojiCat = 0

    private let tabs = ["😊 Emoji", "🎭 Animated", "🗂️ Stickers"]
    private let columns = Array(repeating: GridItem(.flexible(), spacing: 4), count: 8)
    private let mediaColumns = Array(repeating: GridItem(.flexible(), spacing: 8), count: 4)
    private let blue = Color.evrryPrimaryBlue

    var body: some View {
        VStack(spacing: 0) {
            // ── Header ────────────────────────────────────────────────────
            HStack {
                // Tab pills
                HStack(spacing: 0) {
                    ForEach(tabs.indices, id: \.self) { i in
                        Button(action: { selectedTab = i }) {
                            Text(tabs[i])
                                .font(.system(size: 11, weight: selectedTab == i ? .bold : .regular))
                                .foregroundColor(selectedTab == i ? blue : Color.gray)
                                .padding(.horizontal, 10)
                                .padding(.vertical, 6)
                                .background(selectedTab == i ? blue.opacity(0.1) : Color.clear)
                                .cornerRadius(20)
                        }
                    }
                }
                Spacer()
                Button(action: onClose) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.system(size: 20))
                        .foregroundColor(Color.gray.opacity(0.6))
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)

            Divider()

            // ── Content ───────────────────────────────────────────────────
            Group {
                if selectedTab == 0 {
                    // EMOJI TAB
                    VStack(spacing: 0) {
                        // Category row
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 4) {
                                ForEach(emojiCategories.indices, id: \.self) { i in
                                    Button(action: { selectedEmojiCat = i }) {
                                        Text(emojiCategories[i].icon)
                                            .font(.system(size: 22))
                                            .padding(6)
                                            .background(selectedEmojiCat == i ? blue.opacity(0.12) : Color.clear)
                                            .clipShape(Circle())
                                    }
                                }
                            }
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                        }
                        Divider()
                        // Grid
                        ScrollView {
                            LazyVGrid(columns: columns, spacing: 4) {
                                ForEach(emojiCategories[selectedEmojiCat].emojis, id: \.self) { emoji in
                                    Button(action: { onEmojiPick(emoji) }) {
                                        Text(emoji)
                                            .font(.system(size: 26))
                                            .frame(width: 38, height: 38)
                                    }
                                }
                            }
                            .padding(.horizontal, 10)
                            .padding(.vertical, 8)
                        }
                        .frame(height: 180)
                    }

                } else if selectedTab == 1 {
                    // ANIMATED EMOJI TAB
                    ScrollView {
                        LazyVGrid(columns: mediaColumns, spacing: 8) {
                            ForEach(animatedEmojiUrls, id: \.self) { url in
                                Button(action: { onStickerPick(url) }) {
                                    AsyncImage(url: URL(string: url)) { phase in
                                        switch phase {
                                        case .success(let image):
                                            image.resizable().scaledToFit()
                                        case .failure:
                                            Image(systemName: "face.smiling").font(.system(size: 28)).foregroundColor(.gray)
                                        default:
                                            ProgressView().frame(width: 56, height: 56)
                                        }
                                    }
                                    .frame(width: 64, height: 64)
                                    .padding(4)
                                    .background(Color(UIColor.systemGray6))
                                    .cornerRadius(12)
                                }
                            }
                        }
                        .padding(.horizontal, 10)
                        .padding(.vertical, 10)
                    }
                    .frame(height: 220)

                } else {
                    // STICKER TAB
                    ScrollView {
                        LazyVGrid(columns: mediaColumns, spacing: 8) {
                            ForEach(stickerUrls, id: \.self) { url in
                                Button(action: { onStickerPick(url) }) {
                                    AsyncImage(url: URL(string: url)) { phase in
                                        switch phase {
                                        case .success(let image):
                                            image.resizable().scaledToFit()
                                        case .failure:
                                            Image(systemName: "photo").font(.system(size: 28)).foregroundColor(.gray)
                                        default:
                                            ProgressView().frame(width: 56, height: 56)
                                        }
                                    }
                                    .frame(width: 64, height: 64)
                                    .padding(6)
                                    .background(Color(UIColor.systemGray6))
                                    .cornerRadius(12)
                                }
                            }
                        }
                        .padding(.horizontal, 10)
                        .padding(.vertical, 10)
                    }
                    .frame(height: 220)
                }
            }

            Spacer(minLength: 8)
        }
        .background(Color.white)
    }
}
