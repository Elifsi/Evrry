# apps/consumer/ — evrry Consumer Mobile Applications

The **Consumer Mobile Applications** deliver the customer-facing **evrry** Super App experience, developed by **Elifsi Technologies Private Limited**. The implementation provides parallel native applications for **Android** and **iOS** designed from the ground up to match the verified mobile phone golden master prototype at [`prototype/Phone/`](../../prototype/Phone/).

---

## 1. Target Platforms & Structure

| Platform | Technology Stack | Location | Status |
| :--- | :--- | :--- | :--- |
| **Android** | Kotlin 2.0 + Jetpack Compose + Material 3 + Coroutines | [`apps/consumer/android/`](./android/) | ✅ Fully Implemented |
| **iOS** | Swift 5.10 / 6 + SwiftUI + Structured Concurrency | [`apps/consumer/ios/`](./ios/) | ✅ Fully Implemented |
| **Mobile Master Prototype** | Next.js 16 (App Router) + TypeScript + Tailwind CSS | [`prototype/Phone/`](../../prototype/Phone/) | ✅ Golden Master Reference |
| **Consumer Web Portal** | Next.js + TypeScript + MapLibre GL | [`apps/web/consumer/`](../web/consumer/) | 🔮 Planned Web Surface |

---

## 2. Architectural Pillars

### A. Dual Native Parallel Architecture
Android and iOS native apps share a strict 1:1 structural parity across their domain models, network services, ViewModels, and UI layouts:
- **Models**: `AuthState`, `CatalogItem`, `CartState`, `FoodModels`, `GroceryModels`, `RideBooking`, `VoiceModel`, `SocialChatModels`, `SnapModels`, `CallModels`, `LoyaltyModels`, `PaymentMethodModels`, `BackupAndSettingsModels`, `LocationModels`, `OnboardingPage`.
- **Services**: `EvrryAuthService`, `EvrryAiService`, `EvrryMapRoutingService`, `EvrryPaymentService`, `EvrryPushService`, `EvrryEmailService`, `EvrryE2EEManager`, `LocationService`, `SavedAddressRepository`/`SavedAddressService`.
- **ViewModels**: `AuthViewModel`, `HomeViewModel`, `ExploreViewModel`, `FoodViewModel`, `GroceryViewModel`, `RideViewModel`, `ActivityViewModel`, `ChatViewModel`, `CallViewModel`, `CameraViewModel`, `LoyaltyViewModel`.
- **Navigation Hosts**: `EvrryConsumerNavHost` (Android Jetpack Navigation) $\longleftrightarrow$ `MainCoordinatorView` (iOS SwiftUI NavigationStack).

### B. Full Food Delivery Vertical (Deliverr & Enatega Architecture)
Directly incorporates domain models, ViewModels, and UI layouts from reference repositories [`reference/kaaneneskpc_Deliverr`](../../reference/kaaneneskpc_Deliverr) and [`reference/enatega_food-delivery-multivendor`](../../reference/enatega_food-delivery-multivendor):
- **Dedicated Food Home Screen**: Location pill (`Delivering to: Jhamsikhel, Lalitpur`), festival promo banner (`20% OFF`, `NEPAL20`), cravings carousel (`🥟 Momo`, `🍲 Thakali`, `🍕 Pizza`, `🍔 Burgers`, `🥗 Pure Veg`), and quick filters (`4.5+ ★`, `Pure Veg 🟢`, `Under 30 mins ⚡`).
- **Dedicated Restaurant Menu Detail**: Hero gradient header, rating/ETA/price-for-two badge row, categorized menu sections, Veg/Non-Veg indicators (`🟢`/`🔴`), bestseller tags, and item customization triggers.
- **Enatega Multi-Restaurant Conflict Alert**: Interactive modal alert warning if cart contains dishes from another kitchen with options to cancel or start a fresh cart.
- **Deliverr & Enatega Item Customization Sheet**: Bottom sheet modal supporting preparation choices (radio), add-ons & extras (checkboxes), special cooking instructions, quantity steppers, and dynamic price recalculation.
- **Enhanced Cart & Checkout**: Delivery address selector (Home/Work), special instructions field, platform fee (Rs 10), and direct domestic payments.
- **Order Confirmation & Live Tracking Screens**: Dedicated `OrderSuccessScreen` with animated checkmark and `OrderDetailsScreen` with 5-stage status stepper (`placed` → `confirmed` → `kitchen` → `delivering` → `delivered`), live route & dispatch card, driver HUD with direct call action, delivery destination, cooking instructions, and itemized receipt breakdown.

### C. Unified UI & In-App Navigation (iOS Hardware Parity)
Because Apple iOS devices have no physical or OS-level hardware back button, every sub-screen, sheet, modal, and checkout view renders a consistent, accessible **in-app back button (`<`)** with a minimum $44 \times 44\text{ pt/dp}$ touch target within `EvrryTopBar`. Both Android and iOS clients render identical typography, color tokens, and navigation layouts:
- **Brand Navy**: `#0E1B38` (Surfaces, navigation bars, top headers)
- **Primary Blue**: `#2563EB` (Primary CTAs, active indicators, brand accent)
- **Action Emerald**: `#059669` (Success confirmations, WhatsApp badges, payment confirmations)
- **Surface Paper**: `#F8FAFC` / `#FFFFFF` (Light backgrounds, clean card surfaces)
- **Ink Primary**: `#0F172A` (Crisp readable typography)

### D. Domestic Payments Only (Zero In-App Stored Wallet)
In compliance with Nepal financial regulations, all in-app stored wallet balances have been completely eliminated (`walletBalanceNpr` purged). Orders and rides use domestic direct payment methods exclusively:
- **eSewa Direct Payment**: Seamless integration with Nepal's #1 digital wallet
- **Khalti Digital Wallet**: Instant wallet transfer
- **Fonepay Direct QR**: Instant interoperable banking QR
- **Cash on Delivery (COD)**: Pay rider upon arrival

### E. AI Voice Assistant: Ellie (Mini & Pro)
The AI Concierge features an authentic, clean ChatGPT-style model switch without gimmicky personas or clutter:
- **ChatGPT-Style Model Switcher**: Minimal top pill `[ Ellie Mini ▾ ]` switching between:
  1. **Ellie Mini** (`Fast · Everyday`): Lightweight, ultra-responsive model for instant food orders, rides, and daily tasks.
  2. **Ellie Pro** (`Deep Reasoning`): Advanced reasoning model for multi-stop itinerary planning and complex queries.
- **Dynamic Language Mirroring**: The conversational engine automatically detects and mirrors the user's spoken language (**Nepali**, **Hindi**, **Maithili**, or **English**) without manual toggles.
- **Voice Waveform Orb**: Organic pulsating animated orb reflecting voice states (`idle`, `listening`, `thinking`, `speaking`).
- **LiveKit WebRTC Low-Latency Voice**: Real-time room token negotiation with server-side AI voice gateway.

### F. WhatsApp-Grade Social Chat, WebRTC Calling, Stickers & Custom Wallpapers
- **Signal-Grade E2EE Double Ratchet Architecture**: End-to-end encrypted messaging with client keystore storage and ECDH P-256 / Curve25519 key agreements.
- **WebRTC 1:1 Voice & Video Calling**: Low-latency VoIP audio and video streaming with duration timer, PIP/fullscreen toggling, camera/mic controls, and database call logs (`call_logs`).
- **Voice Notes**: WhatsApp-style hold-to-record microphone button adjacent to send button with dynamic waveform visualizer and playback timer.
- **Zero Paid SDKs & Open-Source Royalty-Free Expressions**:
  - **Categorized Unicode 15.0 Emojis**: 5 category tabs (Smileys & Emotion, People & Body, Food & Drink, Activities & Travel, Objects & Symbols).
  - **Google Noto Animated Emojis**: Official open-source animated GIF assets under **Apache 2.0 license** (`fonts.gstatic.com`).
  - **Twitter Twemoji Stickers**: Official open-source vector sticker assets under **CC-BY 4.0 license** (`raw.githubusercontent.com/twitter/twemoji`).
- **Borderless Floating Sticker Bubbles**: Stickers and animated emojis render as borderless transparent elements without conventional speech bubble backgrounds, with floating translucent timestamps.
- **Chat Wallpaper Personalization**:
  - 12 high-contrast solid color and gradient presets (WhatsApp Classic Doodle, Warm Emerald, Midnight Navy, Sunset Amber, Rose Quartz, Slate Glass, etc.).
  - "My Photos" Custom Device Gallery Wallpaper: Select any photo from camera roll (Android `ActivityResultContracts.GetContent()`, iOS `PhotosPicker` via `PhotosUI`).
  - **18–20% Dark Scrim Invariant**: An automatic dark tint scrim (`alpha = 0.20f` / `rgba(0,0,0,0.18)`) is overlaid across custom photo wallpapers to guarantee that white message text and colored chat bubbles remain 100% readable.

---

## 3. Screen Inventory

| Screen | Route / View | Functionality |
| :--- | :--- | :--- |
| **Phone Input** | `PhoneInputScreen` / `PhoneInputView` | Country prefix `+977`, 10-digit validation, WhatsApp/SMS toggle, Terms agreement |
| **OTP Verify** | `OtpVerifyScreen` / `OtpVerifyView` | 6-digit box layout, auto-verify, 45s resend timer, in-app `<` back navigation |
| **Home Agent** | `HomeAgentScreen` / `HomeAgentView` | ChatGPT `[ Ellie Mini ▾ ]` switcher, pulsating voice orb, quick prompt chips, transcript stream |
| **Explore** | `ExploreScreen` / `ExploreView` | Multi-vendor catalog (Food, Mart, Pharmacy), food hero card, floating cart |
| **Food Delivery Home** | `FoodHomeScreen` / `FoodHomeView` | Cuisines carousel, quick filters, restaurant cards, delivery ETA, search bar |
| **Restaurant Menu Detail** | `RestaurantDetailScreen` / `RestaurantDetailView` | Hero gradient, menu sections, veg indicators, item customization, Enatega conflict alert |
| **Item Customization** | `FoodItemCustomizationSheet` | Radio variations, checkbox add-ons, cooking instructions, quantity steppers |
| **Cart Checkout** | `CartCheckoutScreen` / `CartCheckoutView` | Delivery address picker, special instructions, platform fee (Rs 10), eSewa/Khalti/COD |
| **Order Success** | `OrderSuccessScreen` / `OrderSuccessView` | Animated confirmation badge, Order ID `#ord-XXXX`, ETA badge, live track CTA |
| **Order Details & Tracking** | `OrderDetailsScreen` / `OrderDetailsView` | 5-stage status stepper, route & dispatch map, Rider HUD with call button, itemized receipt |
| **Ride Booking** | `RideBookingScreen` / `RideBookingView` | Pickup/dropoff pins, vehicle selection (Moto, Cab, Comfort), live Rs fare, OTP display |
| **Chat Room** | `ChatRoomScreen` / `ChatRoomView` | 1:1 E2EE chat, wallpaper picker trigger, emoji/sticker toggle, borderless stickers, voice notes |
| **Voice & Video Call** | `CallScreen` / `CallView` | WebRTC 1:1 voice & video calling, duration timer, mic/speaker/camera toggles, end call CTA |
| **Emoji & Sticker Sheet** | `EmojiStickerSheet` / `EmojiStickerSheetView` | 3-tab sheet (Unicode Emojis, Google Noto Animated GIFs, Twemoji SVGs) |
| **Chat Wallpaper Picker** | `WallpaperPickerSheet` / `WallpaperPickerView` | 12 presets + "My Photos" device gallery picker with 20% dark scrim |
| **Activity** | `ActivityScreen` / `ActivityView` | Active orders filter, clickable cards opening `OrderDetailsScreen`, live status badges |
| **Splash** | `SplashScreen` / `SplashView` | Animated brand splash with auto-transition to onboarding or auth |
| **Onboarding** | `OnboardingScreen` / `OnboardingView` | 3-page swipeable intro carousel with skip and continue |
| **Grocery Home** | `GroceryHomeScreen` / `GroceryHomeView` | Blinkit-style dual-pane PLP, category browser, search, delivery ETA |
| **Grocery Store Detail** | `GroceryStoreDetailScreen` / `GroceryStoreDetailView` | Store inventory grid, item cards, add-to-cart, category filters |
| **Location Picker** | `LocationPickerSheet` / `LocationPickerSheetView` | Map-based address selection with search and saved address integration |
| **Profile** | `ProfileScreen` / `ProfileView` | User avatar with tier ring, account details, address book, payment methods, loyalty, vouchers, privacy |
| **Account Details** | `AccountDetailsScreen` | Phone verification, name editing, email, profile photo |
| **Address Book** | `AddressBookScreen` / `AddressBookView` | Saved delivery addresses (Home, Work, custom) with add/edit/delete |
| **Payment Methods** | `PaymentMethodsScreen` / `PaymentMethodsView` | Linked eSewa, Khalti accounts and payment preferences |
| **Vouchers & Referrals** | `VouchersReferralsScreen` / `VouchersReferralsView` | Active promo codes, referral sharing, earned credits |
| **Loyalty Rewards** | `LoyaltyRewardsScreen` / `LoyaltyRewardsView` | Points balance, league tier, streak tracking, redeemable rewards |
| **Privacy & Security** | `PrivacySecurityScreen` / `PrivacySecurityView` | Change password, two-factor, data export, account deletion |
| **Live Tracking Map** | `EvrryLiveTrackingMap` / `EvrryLiveTrackingMapView` | Animated route polyline, vehicle marker, ETA updates for orders and rides |
| **Settings** | `SettingsScreen` / `SettingsView` | Profile info, Ellie Mini / Ellie Pro model selector, dark mode toggle, terms & logout |

---

## 4. Development & Running

### Android (`apps/consumer/android`)
- Open `apps/consumer/android` in Android Studio Ladybug or later.
- Ensure Android SDK 34+ and Kotlin 2.0+ are installed.
- Run `./gradlew assembleDebug` or install directly to an Android emulator/device.

### iOS (`apps/consumer/ios`)
- Open `apps/consumer/ios` in Xcode 15 or 16.
- Compatible with iOS 16.0+ targets.
- Ensure Supabase Swift SDK package dependencies are resolved.
- Build and run on iPhone Simulator or physical test device.
