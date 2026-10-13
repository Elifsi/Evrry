# apps/consumer/android — evrry Native Consumer Android App

> **Platform**: evrry Super App Ecosystem  
> **Company**: Elifsi Technologies Private Limited  
> **Repository**: [https://github.com/Elifsi/Evrry.git](https://github.com/Elifsi/Evrry.git)  

## Status

✅ **Fully Implemented — Complete Source Code in Repository**

---

## 1. Technology Stack & Key Dependencies

- **Language**: Kotlin 2.0+
- **UI Toolkit**: 100% Jetpack Compose with Material 3 theming
- **Architecture**: MVI / MVVM with Clean Architecture (Domain, Data, Presentation layers)
- **State Management**: Reactive Kotlin Coroutines and `StateFlow` / `SharedFlow`
- **Networking**: Supabase Kotlin SDK (`postgrest-kt`, `auth-kt`, `realtime-kt`, `storage-kt`) + Ktor HTTP client
- **Audio & Realtime**: LiveKit Android WebRTC SDK for sub-second voice AI streaming
- **Serialization**: `kotlinx.serialization` (JSON)
- **Dependency Injection**: Hilt / Dagger

---

## 2. Implemented Architecture & Code Layout

```
apps/consumer/android/
├── build.gradle.kts          # Root Gradle build script
├── settings.gradle.kts       # Root settings including :app
├── gradle.properties         # JVM args & AndroidX flags
├── local.properties          # SDK location (/home/rahul/Android/Sdk)
├── gradlew / gradlew.bat     # Gradle wrapper scripts
└── app/
    ├── build.gradle.kts      # Application module build script (BOM 2024.06.00, compileSdk 34)
    └── src/main/
        ├── AndroidManifest.xml # Permissions, Application class, MainActivity launcher
        ├── res/               # Colors, Strings, Themes (Theme.EvrryConsumer)
        └── kotlin/com/elifsi/evrry/consumer/
            ├── MainActivity.kt       # Application entry point hosting NavHost
            ├── model/
            │   ├── AuthState.kt      # Phone validation, OTP state, user session models (zero wallet)
            │   ├── VoiceModel.kt     # ChatGPT models: Ellie Mini & Ellie Pro
            │   ├── FoodModels.kt     # Restaurant, FoodMenuItem, options, addons, Nepal catalog
            │   ├── CatalogItem.kt    # Multi-vendor products & grocery items
            │   ├── CartState.kt      # DeliveryAddress, DefaultAddresses, special instructions, breakdown
            │   ├── RideBooking.kt    # Ride options (Moto, Cab, Comfort), status & OTP
            │   ├── SocialChatModels.kt # SocialMessage (stickerUrl), SocialChat (wallpaperUrl), MessageContentType
            │   └── SnapModels.kt     # SnapItem, MemoryTab, QrScanResult, QrParser, CameraFilterLens
            ├── network/
            │   ├── EvrryAuthService.kt       # Nepal phone + WhatsApp OTP & SMS fallback
            │   ├── EvrryAiService.kt         # WebRTC voice room & AI concierge proxy
            │   ├── EvrryMapRoutingService.kt # OSRM routing & ETA calculations
            │   ├── EvrryPaymentService.kt    # eSewa, Khalti, Fonepay direct payment intents
            │   ├── EvrryPushService.kt       # FCM device token registration
            │   └── EvrryEmailService.kt      # Resend notification gateway proxy
            ├── viewmodel/
            │   ├── AuthViewModel.kt      # Phone validation, 45s countdown timer, OTP verify
            │   ├── HomeViewModel.kt      # ChatGPT Ellie model switcher, orb state, transcripts
            │   ├── ExploreViewModel.kt   # Catalog filters, item search, conflict checks, address picker
            │   ├── FoodViewModel.kt      # Cuisines scroller, quick filters, restaurant search
            │   ├── RideViewModel.kt      # Vehicle selection, fare estimate, ride OTP
            │   ├── ActivityViewModel.kt  # Orders & rides progress stepper, driver HUD, receipts, addOrder
            │   ├── ChatViewModel.kt      # E2EE chat, stickers, voice notes, wallpaper StateFlow, sendImage
            │   └── CameraViewModel.kt    # Snapchat camera state, lenses, 3-tab memories, PIN vault
            └── ui/
                ├── theme/
                │   └── Color.kt          # EvrryNavy, EvrryBlue, EvrryEmerald, EvrryPaper tokens
                ├── components/
                │   └── EvrryComponents.kt# EvrryTopBar with in-app back '<', BottomNavBar, Buttons
                ├── navigation/
                │   └── EvrryConsumerNavHost.kt # NavHost with 5 tabs + food + camera + memories routes
                ├── auth/
                │   ├── PhoneInputScreen.kt # +977 phone input, WhatsApp/SMS toggle
                │   └── OtpVerifyScreen.kt  # 6-box OTP entry, resend timer, in-app back '<'
                ├── camera/
                │   ├── CameraScreen.kt     # Viewfinder, vertical tool dock, QR reticle, lenses, shutter
                │   ├── MemoriesScreen.kt   # 3 tabs (Snaps, Roll, My Locker with PIN keypad), photo modal
                │   └── SnapReviewScreen.kt # Caption overlay, save to memories, story, WhatsApp friend share
                ├── home/
                │   └── HomeAgentScreen.kt  # Minimal model switcher [Ellie Mini ▾], voice orb
                ├── explore/
                │   └── ExploreScreen.kt    # Multi-vendor grid, food banner, floating cart bar
                ├── food/
                │   ├── FoodHomeScreen.kt   # Cuisines scroller, quick filters, restaurant cards
                │   ├── RestaurantDetailScreen.kt # Menu sections, veg dots, conflict alert, customize trigger
                │   └── FoodItemCustomizationSheet.kt # Modal sheet with options, addons, notes, stepper
                ├── chat/
                │   ├── ConversationListScreen.kt # WhatsApp-grade chat hub, 24-hr status carousel, camera shortcut
                │   ├── StoryViewerDialog.kt    # Fullscreen 5s story viewer with press-to-pause, reactions, delete, and reply
                │   ├── ChatRoomScreen.kt       # 1:1 chat, wallpaper container with 20% scrim, borderless stickers
                │   ├── CallScreen.kt           # WebRTC 1:1 voice & video calling HUD
                │   ├── EmojiStickerSheet.kt    # 3-tab sheet (Unicode Emojis, Noto GIFs, Twemoji SVGs via Coil)
                │   └── WallpaperPickerSheet.kt # 12 presets + "My Photos" gallery picker via GetContent()
                ├── orders/
                │   ├── OrderSuccessScreen.kt # Animated confirmation badge, order ID, ETA, track CTA
                │   └── OrderDetailsScreen.kt # 5-stage stepper, live route map, driver HUD, itemized receipt
                ├── rides/
                │   └── RideBookingScreen.kt# Fare cards, ride confirmation, driver status, OTP 1234
                ├── cart/
                │   └── CartCheckoutScreen.kt # Address selector, notes, platform fee, direct eSewa/Khalti/COD
                ├── activity/
                │   └── ActivityScreen.kt   # Active orders filter, clickable cards opening OrderDetailsScreen
                └── settings/
                    └── SettingsScreen.kt   # Profile, Ellie Mini / Pro model selection, dark mode, logout
```

---

## 3. Core Features & Capabilities

### A. Nepal Authentication & WhatsApp OTP
- **Phone Input**: Enforces valid Nepal mobile numbers (`+977`, 98XXXXXXXX / 97XXXXXXXX).
- **Dual Delivery**: Primary WhatsApp template messaging via Edge Functions, with Sparrow SMS fallback.
- **Auto-Verification**: Auto-submits upon entering the 6th digit; 45s cooldown resend timer.

### B. Flagship AI Voice Agent (Ellie Mini & Ellie Pro)
- **ChatGPT-Style Model Switcher**: Minimal top pill button `[ Ellie Mini ▾ ]` switching between:
  1. **Ellie Mini** (`Fast · Everyday`): Lightweight, ultra-responsive model for instant food orders, rides, and daily tasks.
  2. **Ellie Pro** (`Deep Reasoning`): Advanced reasoning model for multi-stop itinerary planning and complex queries.
- **Zero Language Selector Pills**: Ellie automatically detects and replies in **Nepali, Hindi, Maithili, or English**.
- **Voice Waveform Orb**: Animated pulsating circle matching voice states (`idle`, `listening`, `thinking`, `speaking`).

### C. Full Food Delivery Vertical (Deliverr & Enatega Architecture)
Ported directly from [`reference/kaaneneskpc_Deliverr`](../../reference/kaaneneskpc_Deliverr) and [`reference/enatega_food-delivery-multivendor`](../../reference/enatega_food-delivery-multivendor):
- **Dedicated Food Home Screen**: Location header (`📍 Delivering to: Jhamsikhel, Lalitpur`), festival banner (`20% OFF`, `NEPAL20`), search bar, cravings carousel (`🥟 Momo`, `🍲 Thakali`, `🍕 Pizza`, `🍔 Burgers`, `🥗 Pure Veg`), and quick filters (`4.5+ ★`, `Pure Veg 🟢`, `Under 30 mins ⚡`).
- **Dedicated Restaurant Menu Detail**: Hero gradient header, rating/ETA/price-for-two badge row, categorized menu sections, Veg/Non-Veg indicator dots (`🟢`/`🔴`), bestseller tags.
- **Enatega Multi-Restaurant Cart Conflict Alert**: Interactive modal warning if cart contains dishes from another kitchen, with confirmation to discard and start fresh.
- **Deliverr & Enatega Customization Sheet**: `FoodItemCustomizationSheet` modal supporting radio variations, checkbox add-ons, special cooking instructions, and quantity steppers.
- **Enhanced Cart & Checkout**: Delivery address picker (`DefaultAddresses.HOME` / `OFFICE`), special instructions field, platform fee (Rs 10), and direct domestic payments.
- **Order Confirmation & Live Tracking Screens**:
  - `OrderSuccessScreen`: Animated checkmark, Order ID `#ord-XXXX`, ETA badge, live track CTA.
  - `OrderDetailsScreen`: 5-stage status stepper (`placed` → `confirmed` → `kitchen` → `delivering` → `delivered`), live route & dispatch card, driver HUD with direct call action, delivery destination, cooking instructions, and itemized receipt breakdown.

### D. Direct Payments & Zero In-App Wallet (NRB Compliance)
- In compliance with Nepal Rastra Bank (NRB) regulations, in-app stored value wallets are completely excluded (`walletBalanceNpr` purged).
- Cart checkout supports direct gateways: **eSewa**, **Khalti**, **Fonepay QR**, and **Cash on Delivery (COD)** with a standard Rs 10 platform fee.

### E. Activity & Order Tracking (4th Tab)
- Replaces legacy wallet tab with full live tracking.
- Clickable order cards opening dedicated `OrderDetailsScreen`.
- 5-stage order progress stepper (`Placed` → `Confirmed` → `In Kitchen` → `On the Way` → `Delivered`).
- Rider HUD with live vehicle plate (`Ba 88 Pa 4321`), driver name (`Bikash Thapa`), 4.9 rating, and ride start OTP (`OTP: 1234`).
- Filter by `All`, `Active`, and `Completed` with itemized receipt breakdowns.

### F. In-App Navigation Parity (iOS Hardware Parity)
- Every screen and sub-view renders `EvrryTopBar` featuring an explicit **in-app back button (`<`)** with a minimum $44 \times 44\text{ dp}$ touch target, ensuring 1:1 behavioral and visual parity with iOS devices where no physical back button exists.

### G. WhatsApp-Grade Social Chat, WebRTC Calling, Stickers & Custom Wallpapers
- **Double Ratchet E2EE Design**: End-to-end encrypted messaging with Signal double-ratchet keys.
- **WebRTC 1:1 Voice & Video Calling (`CallScreen.kt`)**: Real-time VoIP with duration timer, camera/mic/speaker toggles, and database call logging.
- **Voice Notes**: WhatsApp-style hold-to-record microphone button beside send button with dynamic waveform visualizer and playback timer.
- **Stickers & Animated Emojis (`EmojiStickerSheet.kt`)**: 3-tab bottom sheet featuring categorized Unicode emojis (5 categories), Google Noto Animated Emojis (Apache 2.0 animated GIFs), and Twitter Twemoji Stickers (CC-BY 4.0 SVGs) loaded via Coil.
- **Borderless Floating Sticker Bubbles**: Rendered without speech bubble backgrounds, with floating translucent timestamps.
- **12 Presets + "My Photos" Gallery Wallpaper (`WallpaperPickerSheet.kt`)**: 12 curated color/gradient presets, plus gallery photo selection via `ActivityResultContracts.GetContent()`, automatically overlaying an **18–20% dark tint scrim** (`Color.Black.copy(alpha = 0.20f)`) to guarantee 100% text and bubble readability.

---

## 4. Building & Running in Android Studio

### Prerequisites
- Android Studio Ladybug (2024.2+) or newer installed (`/opt/android-studio/bin/studio.sh`).
- Android SDK installed at `/home/rahul/Android/Sdk` (API 34+).
- Java 21 JDK (`/usr/lib/jvm/java-21-openjdk-amd64`).

### Steps
1. **Open the Project**:
   - Launch Android Studio:
     ```bash
     /opt/android-studio/bin/studio.sh &
     ```
   - Click **Open** and select `/home/rahul/codes/Evrry/apps/consumer/android`.
2. **Gradle Sync**:
   - Android Studio will automatically sync the project using Gradle 8.9 and AGP 8.5.2.
3. **Run on Physical Phone or Emulator**:
   - **Physical Phone**: Enable **Developer Options** and **USB Debugging** on your Android phone, connect it via USB, and accept the debugging prompt.
   - **Emulator**: Start an Android Virtual Device (AVD) from Device Manager.
   - Click the green **Run** button (`Shift + F10`) in Android Studio.
4. **Command Line Alternative**:
   - Build debug APK directly:
     ```bash
     cd /home/rahul/codes/Evrry/apps/consumer/android
     JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew assembleDebug
     ```
   - Generated APK path: `app/build/outputs/apk/debug/app-debug.apk` (16.4 MB).
