# apps/consumer/ios — evrry Native Consumer iOS App

> **Platform**: evrry Super App Ecosystem  
> **Company**: Elifsi Technologies Private Limited  
> **Repository**: [https://github.com/Elifsi/Evrry.git](https://github.com/Elifsi/Evrry.git)  

## Status

✅ **Fully Implemented — Complete Source Code in Repository**

---

## 1. Technology Stack & Key Dependencies

- **Language**: Swift 5.10 / Swift 6
- **UI Toolkit**: 100% Declarative SwiftUI adhering to Apple Human Interface Guidelines (HIG)
- **Architecture**: Clean MVVM Architecture with `@MainActor` ObservableObject ViewModels
- **Concurrency**: Swift Structured Concurrency (`async/await`, `Task`, `@MainActor`)
- **Networking**: `supabase-swift` (`PostgREST`, `Auth`, `Realtime`, `Storage`)
- **Real-Time Voice**: LiveKit iOS SDK for WebRTC sub-second voice agent interaction
- **Security**: Keychain Services for encrypted session tokens; zero secrets embedded in client binary

---

## 2. Implemented Architecture & Code Layout

```
apps/consumer/ios/EvrryConsumer/
├── Models/
│   ├── AuthState.swift       # Auth state machine, OTP status, zero-wallet user profile
│   ├── VoiceModel.swift      # ChatGPT models: Ellie Mini & Ellie Pro
│   ├── FoodModels.swift      # Restaurant, FoodMenuItem, options, addons & Nepal catalog
│   ├── CatalogItem.swift     # Multi-vendor catalog items & services
│   ├── CartState.swift       # DeliveryAddress, DefaultAddresses, special instructions, cart breakdown
│   ├── RideBooking.swift     # Vehicle types (Moto, Cab, Comfort), status & OTP
│   ├── SocialChatModels.swift # SocialMessage (stickerUrl), SocialChat (wallpaperUrl), MessageContentType
│   └── SnapModels.swift      # SnapItem, MemoryTab, QrScanResult, CameraFilterLens, QrParser
├── Services/
│   ├── EvrryAuthService.swift       # Phone OTP & WhatsApp/SMS dispatch
│   ├── EvrryAiService.swift         # WebRTC voice room & Ellie concierge
│   ├── EvrryMapRoutingService.swift # OSRM routing & ETA calculations
│   ├── EvrryPaymentService.swift    # eSewa, Khalti, Fonepay payment intents
│   ├── EvrryPushService.swift       # APNs / FCM token registration
│   └── EvrryEmailService.swift      # Resend notification gateway proxy
├── ViewModels/
│   ├── AuthViewModel.swift      # Phone validation, 45s cooldown, OTP verification (zero wallet)
│   ├── HomeViewModel.swift      # ChatGPT Ellie model switcher, orb state, transcripts
│   ├── ExploreViewModel.swift   # Catalog filters, item search, conflict checks, address picker
│   ├── FoodViewModel.swift      # Cuisines scroller, quick filters, restaurant search
│   ├── RideViewModel.swift      # Vehicle selection, fare estimate, ride OTP
│   ├── ActivityViewModel.swift  # Orders & rides progress stepper, driver HUD, receipts, addOrder
│   ├── ChatViewModel.swift      # E2EE chat, stickers, voice notes, wallpaperByChat, sendImage
│   └── CameraViewModel.swift    # Snapchat camera state, lenses, 3-tab memories, PIN vault
└── Views/
    ├── Theme/
    │   ├── EvrryColors.swift     # Brand navy, blue, emerald, paper, slate
    │   └── EvrryComponents.swift # EvrryTopBar with in-app back '<' (44x44 pt), BottomNavBar, Buttons
    ├── Navigation/
    │   └── MainCoordinatorView.swift # SwiftUI NavigationStack coordinating all routes & order flows
    ├── Auth/
    │   ├── PhoneInputView.swift  # +977 phone input, WhatsApp/SMS toggle
    │   └── OtpVerifyView.swift   # 6-box OTP entry, resend timer, in-app back '<'
    ├── Camera/
    │   ├── CameraView.swift      # Viewfinder, vertical tool dock, QR reticle, lenses, shutter
    │   ├── MemoriesView.swift    # 3 tabs (Snaps, Roll, My Locker with PIN keypad), photo modal
    │   └── SnapReviewView.swift  # Caption overlay, save to memories, story, WhatsApp friend share
    ├── Home/
    │   └── HomeAgentView.swift   # Minimal model switcher [Ellie Mini ▾], voice orb
    ├── Explore/
    │   └── ExploreView.swift     # Multi-vendor grid, food banner, floating cart bar
    ├── Food/
    │   ├── FoodHomeView.swift    # Cuisines scroller, quick filters, restaurant cards
    │   ├── RestaurantDetailView.swift # Menu sections, veg dots, conflict alert, customize trigger
    │   └── FoodItemCustomizationSheet.swift # Modal sheet with options, addons, notes, stepper
    ├── Chat/
    │   ├── ConversationListView.swift   # WhatsApp-grade chat hub, 24-hr status carousel, camera shortcut
    │   ├── StoryViewerView.swift        # Fullscreen 5s story viewer with press-to-pause, reactions, delete, and reply
    │   ├── ChatRoomView.swift           # 1:1 chat, wallpaper container with 20% scrim, borderless stickers
    │   ├── CallView.swift               # WebRTC 1:1 voice & video calling view
    │   ├── EmojiStickerSheetView.swift  # 3-tab sheet (Unicode Emojis, Noto GIFs, Twemoji SVGs)
    │   └── WallpaperPickerView.swift    # 12 presets + "My Photos" gallery picker via PhotosUI
    ├── Orders/
    │   ├── OrderSuccessView.swift# Animated confirmation badge, order ID, ETA, track CTA
    │   └── OrderDetailsView.swift# 5-stage stepper, live route map, driver HUD, itemized receipt
    ├── Rides/
    │   └── RideBookingView.swift # Fare cards, ride confirmation, driver status
    ├── Cart/
    │   └── CartCheckoutView.swift# Address selector, notes, platform fee, direct eSewa/Khalti/COD
    ├── Activity/
    │   └── ActivityView.swift    # Active orders filter, clickable cards opening OrderDetailsView
    └── Settings/
        └── SettingsView.swift    # Profile, Ellie Mini / Pro model selection, dark mode, logout
```

---

## 3. Core Features & In-App Navigation Parity

### A. Mandatory In-App Back Navigation (`<`)
Since iOS devices have no hardware back button, every modal, checkout view, and nested detail screen incorporates a dedicated **in-app back button (`<`)** inside `EvrryTopBar` (minimum `44x44 pt` touch target), completely eliminating navigation dead-ends.

### B. Nepal Phone Auth & WhatsApp OTP
- **Validation**: Enforces 10-digit Nepal mobile numbers (`+977`, 98XXXXXXXX / 97XXXXXXXX).
- **Delivery**: Primary instant WhatsApp OTP with Sparrow SMS fallback.
- **PIN Input**: 6-digit individual box entry with auto-focus and auto-verification.
- **Resend**: 45-second animated countdown timer.

### C. ChatGPT Minimal Model Switcher (Ellie Mini & Ellie Pro)
- **Top Pill Selector**: `[ Ellie Mini ▾ ]` button opens a clean model switch between **Ellie Mini** (`Fast · Everyday`) and **Ellie Pro** (`Deep Reasoning`).
- **Automatic Multilingual Mirroring**: Zero manual language pills. Ellie automatically responds in **Nepali, Hindi, Maithili, or English** matching the language spoken by the user.

### D. Full Food Delivery Vertical (Deliverr & Enatega Architecture)
Ported directly from [`reference/kaaneneskpc_Deliverr`](../../reference/kaaneneskpc_Deliverr) and [`reference/enatega_food-delivery-multivendor`](../../reference/enatega_food-delivery-multivendor):
- **Dedicated Food Home Screen**: Location header (`📍 Delivering to: Jhamsikhel, Lalitpur`), festival banner (`20% OFF`, `NEPAL20`), search bar, cravings horizontal scroll carousel (`🥟 Momo`, `🍲 Thakali`, `🍕 Pizza`, `🍔 Burgers`, `🥗 Pure Veg`), and quick filters (`4.5+ ★`, `Pure Veg 🟢`, `Under 30 mins ⚡`).
- **Dedicated Restaurant Menu Detail**: Hero gradient header, rating/ETA/price-for-two badge row, categorized menu sections, Veg/Non-Veg indicator dots (`🟢`/`🔴`), bestseller tags.
- **Enatega Multi-Restaurant Cart Conflict Alert**: Interactive modal alert warning if cart contains dishes from another kitchen, with confirmation to discard and start fresh.
- **Deliverr & Enatega Customization Sheet**: `FoodItemCustomizationSheet` modal supporting radio variations, checkbox add-ons, special cooking instructions, and quantity steppers.
- **Enhanced Cart & Checkout**: Delivery address picker (`DefaultAddresses.home` / `office`), special instructions field, platform fee (Rs 10), and direct domestic payments.
- **Order Confirmation & Live Tracking Screens**:
  - `OrderSuccessView`: Animated checkmark, Order ID `#ord-XXXX`, ETA badge, live track CTA.
  - `OrderDetailsView`: 5-stage status stepper (`placed` → `confirmed` → `kitchen` → `delivering` → `delivered`), live route & dispatch card, driver HUD with direct call action, delivery destination, cooking instructions, and itemized receipt breakdown.

### E. Direct Payments & Zero In-App Stored Wallet (NRB Compliance)
- Stored value wallet balances are completely purged (`walletBalanceNpr` removed).
- Domestic payment gateways supported exclusively: **eSewa**, **Khalti**, **Fonepay QR**, and **Cash on Delivery (COD)**.

### F. WhatsApp-Grade Social Chat, WebRTC Calling, Stickers & Custom Wallpapers
- **Double Ratchet E2EE Design**: End-to-end encrypted messaging with Signal double-ratchet keys.
- **WebRTC 1:1 Voice & Video Calling (`CallView.swift`)**: Real-time VoIP with duration timer, camera/mic/speaker toggles, and database call logging.
- **Voice Notes**: WhatsApp-style hold-to-record microphone button beside send button with dynamic waveform visualizer and playback timer.
- **Stickers & Animated Emojis (`EmojiStickerSheetView.swift`)**: 3-tab sheet featuring categorized Unicode emojis (5 categories), Google Noto Animated Emojis (Apache 2.0 animated GIFs), and Twitter Twemoji Stickers (CC-BY 4.0 SVGs).
- **Borderless Floating Sticker Bubbles**: Rendered without speech bubble backgrounds, with floating translucent timestamps.
- **12 Presets + "My Photos" Gallery Wallpaper (`WallpaperPickerView.swift`)**: 12 curated color/gradient presets, plus gallery photo selection via native `PhotosPicker` from `PhotosUI`, automatically overlaying an **18–20% dark tint scrim** (`Color.black.opacity(0.20)`) to guarantee 100% text and bubble readability.

---

## 4. Building & Running

1. Open `apps/consumer/ios` in **Xcode 15 or 16**.
2. Allow Swift Package Manager to resolve packages (`supabase-swift`, etc.).
3. Choose an iOS Simulator (e.g., iPhone 15 Pro, iOS 17+) or physical iOS device.
4. Build and run (`Cmd + R`).
