# Consumer Architecture

The Consumer application provides the customer-facing experience for **evrry**, developed by **Elifsi Technologies Private Limited**, across Android, iOS, and Web.

---

## 1. Platform Clients

| Client | Directory | Technology | Build / Status |
|---|---|---|---|
| **Android Native** | `apps/consumer/android/` | Kotlin 2.0, Jetpack Compose, Coroutines, Flow | Gradle (`build.gradle.kts`) — ✅ Implemented |
| **iOS Native** | `apps/consumer/ios/` | Swift 5.10 / 6, SwiftUI, Swift Concurrency | Xcode SPM — ✅ Implemented |
| **Mobile Golden Master** | `prototype/Phone/` | Next.js 16 (App Router), React, TypeScript, Tailwind | npm (`package.json`) — ✅ Golden Master Reference |
| **Web Portal** | `apps/web/consumer/` | Next.js (App Router), React, TypeScript, MapLibre GL | npm (`package.json`) — 🔮 Planned |

---

## 2. Core Functional Subsystems

### A. Nepal Phone Authentication & WhatsApp OTP Verification
- **Phone Validation**: Enforces standard Nepal telephone format (`+977`, 10-digit mobile starting with 98 or 97).
- **Dual Gateway Dispatch**:
  - **Primary**: High-priority WhatsApp OTP template dispatched via Meta Graph API via Supabase Edge Function `whatsapp-otp`.
  - **Fallback**: Sparrow SMS domestic gateway triggered when WhatsApp connectivity is absent or fails.
- **45-Second Cooldown**: Synchronized countdown timer in `AuthViewModel` prevents OTP spam.
- **Auto-Verification**: 6-box input automatically submits verification request upon entry of the 6th digit.
- **Session Establishment**: Secured via device-level encrypted storage (`EncryptedSharedPreferences` on Android, `Keychain` on iOS).

### B. Flagship AI Concierge & Minimal Model Switcher
- **ChatGPT-Style Model Switcher**: Minimal top pill button `[ Ellie Mini ▾ ]` opening a sleek bottom sheet to toggle between:
  1. **Ellie Mini** (`Fast · Everyday`): Lightweight, ultra-responsive model for instant food orders, rides, and daily tasks.
  2. **Ellie Pro** (`Deep Reasoning`): Advanced reasoning model for multi-stop itinerary planning and complex queries.
- **Automatic Multilingual Mirroring**: No manual language toggles or pills. The voice and text agent dynamically detects and responds in **Nepali, Hindi, Maithili, or English** matching the language used by the customer.
- **Voice Waveform Orb**: Animated pulsating circle displaying live voice states (`idle`, `listening`, `thinking`, `speaking`).
- **LiveKit WebRTC Integration**: Sub-second full-duplex audio streaming connected to the server-side voice gateway.

### C. Unified Mobile UI & In-App Navigation (iOS Hardware Parity)
- Because Apple iOS devices lack a physical or OS-level back button, **every single sub-screen, sheet, and detail view renders an accessible in-app back button (`<`)** in `EvrryTopBar` (`44x44 pt` minimum touch target).
- Both Android and iOS clients render identical typography, color tokens, and navigation layouts:
  - `EvrryNavy`: `#0E1B38` (Surfaces, navigation bars, top headers)
  - `EvrryBlue`: `#2563EB` (Primary CTAs, active indicators)
  - `EvrryEmerald`: `#059669` (Success confirmations, WhatsApp badges, payment confirmations)
  - `EvrryPaper`: `#F8FAFC` / `#FFFFFF` (Clean light card surfaces)

### D. Marketplace Discovery, Catalog & Cart
- Multi-category catalog indexing: Food, Grocery, Retail, Hotels, Mobility/Rides, Professional Services.
- Fast-filter pills (All, Momo & Khaja, Grocery, Pharmacy, Sweet Shop).
- Quantity counter steppers (`+` / `-`) that synchronize local `CartState`.
- Floating Cart Summary bar with item count, subtotal in Nepali Rupees (NPR), and checkout CTA.
- Server-side price calculation computing final subtotal, delivery fee, platform fee (Rs 10), and taxes.

### E. Mobility & Ride Booking
- Vehicle tiers: Moto (Bike, fastest), Cab (Standard Car), or Comfort (Premium AC).
- Live distance and fare calculation in Nepali Rupees (Rs).
- Booking confirmation state machine generating a 4-digit ride start OTP (e.g. `1234`) displayed on the HUD.

### F. Direct Domestic Checkout (Zero Stored-Value Wallet Compliance)
- Per Nepal Rastra Bank (NRB) regulations, Evrry holds zero customer wallet balance (no PSP license burden).
- All checkouts process directly via trusted aggregators: eSewa, Khalti, Fonepay Dynamic QR, or Cash on Delivery (COD).
- App deep-linking and web-hosted redirection handle authentication securely with no card/credential storage on device.

### G. Order Tracking & Activity Subsystem (Daraz Model)
- High-level consolidated activity feed eliminating notification spam.
- 5-stage visual progress stepper (`Placed` → `Confirmed` → `Prepared` → `Out for Delivery` → `Delivered`).
- Rider HUD card with real-time GPS coordinate updates over Supabase Realtime / Redis.
- Itemized receipt breakdown and one-tap reorder.

### H. WhatsApp-Grade Social Chat, WebRTC Calling & Media Subsystem
- **Signal Double Ratchet E2EE Architecture**: End-to-end encrypted direct messaging between consumers, friends, and delivery coordinators with X3DH pre-keys and cryptographic ratchets.
- **WebRTC 1:1 Voice & Video Calling**: Low-latency peer-to-peer VoIP with active call timers, front/back camera toggling, mic muting, speakerphone switching, and persistent PostgreSQL call logs (`public.call_logs`).
- **Voice Notes**: WhatsApp-style hold-to-record microphone button positioned beside the send button, with dynamic audio waveform visualizer and playback timer.
- **Zero Paid SDKs & Royalty-Free Asset Ecosystem**:
  - **Categorized Unicode 15.0 Emojis**: 5 curated categories (Smileys & Emotion, People & Body, Food & Drink, Activities & Travel, Objects & Symbols) with fast-filter chips and grid navigation.
  - **Google Noto Animated Emojis**: 100% royalty-free open-source animated GIF assets under **Apache 2.0 license** (`fonts.gstatic.com`).
  - **Twitter Twemoji Stickers**: 100% royalty-free open-source vector sticker assets under **CC-BY 4.0 license** (`raw.githubusercontent.com/twitter/twemoji`).
- **Borderless Floating Sticker Bubbles**: Stickers and animated emojis render as borderless transparent elements without conventional speech bubble backgrounds, with floating translucent timestamps.
- **Chat Wallpaper Personalization**:
  - 12 high-contrast color and gradient presets (WhatsApp Classic Doodle, Warm Emerald, Midnight Navy, Sunset Amber, Rose Quartz, Slate Glass, etc.).
  - "My Photos" Custom Device Gallery Wallpaper: Select any photo from camera roll (Android `ActivityResultContracts.GetContent()`, iOS `PhotosPicker` via `PhotosUI`).
  - **18–20% Dark Scrim Invariant**: An automatic dark tint scrim (`alpha = 0.20f` / `rgba(0,0,0,0.18)`) is overlaid across custom photo wallpapers to guarantee that white message text and colored chat bubbles remain 100% readable.
  - Per-chat wallpaper persistence via `public.set_chat_wallpaper()` RPC.

---

## 3. Client Security Boundaries

- Consumer clients only possess the Supabase `anon` key.
- Row Level Security (RLS) ensures consumers cannot query other consumers' profiles, carts, orders, or private conversations.
- No direct database write access to order status or payment records; state transitions are handled through validated database functions or Edge Functions.
- Zero private gateway keys, Resend keys, or Twilio/Meta secrets are embedded in the mobile applications.
