# Unified Mobile UI Architecture & iOS In-App Navigation Specification
**evrry Super App Platform (Android & iOS Parity)**  
*Elifsi Technologies Private Limited*

---

## 1. Executive Directive: Complete UI Parity (1:1 Mirroring)

To eliminate redundant design work, prevent UI drift, and streamline QA testing:
- **Single Source of Truth**: The Next.js phone prototype in [`prototype/Phone/`](file:///home/rahul/codes/Evrry/prototype/Phone/) serves as the visual and functional golden master.
- **Identical Layouts**: Android (Jetpack Compose) and iOS (SwiftUI) use the exact same layouts, color tokens, card structures, typography, and spacing.
- **Zero Platform Fragmentation**: A customer or driver switching between Android and iPhone experiences an identical interface, with native fluid animations on both platforms.

---

## 2. Critical iOS In-App Navigation & Back Button Rules

Unlike Android, **iOS devices do not have a physical back button or mandatory OS-level navigation bar**. If an iOS screen lacks an in-app back control, the user becomes permanently trapped.

### Rule 1: Mandatory Top-Left In-App Back Button (`44x44 pt` Minimum Tap Target)
Every nested view, catalog category, order tracking screen, checkout step, and profile sub-page **MUST** feature a prominent in-app back button at the top-left:

```
┌─────────────────────────────────────────────────────────┐
│  [ < Back ]               Screen Title             [ 🔍 ]│ <── EvrryTopBar (min 56pt)
├─────────────────────────────────────────────────────────┤
│                                                         │
│                      Screen Content                     │
│                                                         │
```

- **Tap Target**: Minimum `44x44 pt` (Apple Human Interface Guidelines & Material Accessibility standard).
- **Icon**: Chevron / Arrow-Left (`chevron.backward` in SwiftUI / `Icons.AutoMirrored.Filled.ArrowBack` in Jetpack Compose).
- **Component**: Reusable `EvrryTopBar` component:
  - Android: [`EvrryComponents.kt`](file:///home/rahul/codes/Evrry/apps/consumer/android/src/main/kotlin/com/elifsi/evrry/consumer/ui/components/EvrryComponents.kt)
  - iOS: [`EvrryComponents.swift`](file:///home/rahul/codes/Evrry/apps/consumer/ios/EvrryConsumer/Views/Theme/EvrryComponents.swift)
- **Behavior**: Calls the platform navigator (`onBack()` / `navController.popBackStack()` in Compose, `onBack()` / `dismiss()` in SwiftUI).

### Rule 2: Modal & Bottom Sheet Dismissal (Top-Right `✕` & Drag Handle)
All bottom sheets (cart modifiers, model switchers, ride options) and full-screen dialogs must include:
1. **Top Drag Indicator**: Centered pill capsule (`width: 48, height: 4, cornerRadius: 2`) for natural downward swipe-to-dismiss.
2. **Top-Right Close Button**: Dedicated circular `✕` icon button (`44x44 pt` tap area).

### Rule 3: Native Interactive Swipe-to-Go-Back
- SwiftUI `NavigationStack` and Compose gestures keep the interactive edge swipe-to-back gesture active at all times.
- Never disable or intercept edge swipes unless dragging an interactive map or canvas.

### Rule 4: Home Indicator & Safe Area Inset Protection
All sticky bottom action bars (*"Proceed to Checkout"*, *"Book Moto Ride"*, *"Confirm Payment"*):
- Must enforce `WindowInsets.safeDrawing` (Compose) / `.safeAreaInset(edge: .bottom)` (SwiftUI).
- Never place interactive buttons flush against the bottom edge of the glass where they could conflict with the iOS Home indicator bar.

---

## 3. UI Token Parity Matrix (Official EVRRY Direction)

| Design Token | Jetpack Compose (Android) | SwiftUI (iOS) | Web / Next.js (Tailwind) | Hex Code |
|---|---|---|---|---|
| **Navy Surface / Brand** | `EvrryNavy` | `EvrryColors.brandNavy` | `bg-brandNavy` | `#0E1B38` |
| **Primary Accent Blue** | `EvrryBlue` | `EvrryColors.primaryBlue` | `bg-blue-600` | `#2563EB` |
| **Action Emerald** | `EvrryEmerald` | `EvrryColors.actionEmerald` | `bg-emerald-600` | `#059669` |
| **Surface Paper (Light)** | `EvrryPaper` | `EvrryColors.surfacePaper` | `bg-slate-50` | `#F8FAFC` |
| **Ink Primary** | `EvrryInk` | `EvrryColors.inkPrimary` | `text-slate-900` | `#0F172A` |
| **Ink Muted** | `EvrryMuted` | `EvrryColors.inkMuted` | `text-slate-500` | `#64748B` |
| **Corner Radius** | `RoundedCornerShape(16.dp)` | `.cornerRadius(16)` | `rounded-2xl` | `16px` |
| **In-App Back Button** | `IconButton(modifier = Modifier.size(44.dp))` | `Button { onBack() } .frame(width: 44, height: 44)` | `<button className="w-11 h-11">` | $\ge 44\times 44\text{ pt}$ |

---

## 4. AI Concierge Minimal Switcher (ChatGPT Pattern)

The flagship voice interface incorporates a minimalist top pill button:
- **Button**: `[ Ellie ▾ ]` centered in the top header.
- **Model Sheet**: Tapping opens a sleek sheet presenting the 4 models:
  1. **Ellie** (Female): Youthful, energetic everyday guide.
  2. **Rony** (Male): Professional executive concierge.
  3. **Jenny** (Female): Warm, sweet household & pharmacy guide.
  4. **Suka** (Male): Calm, relaxing night companion.
- **Automatic Multilingual Mirroring**: Zero manual language pills cluttering the screen. The engine dynamically mirrors the customer's language (**Nepali, Hindi, Maithili, English**).

---

## 5. Web Suite Alignment (`apps/web/`)

The web ecosystem shares the same responsive foundation:
1. **`apps/web/consumer/`**: Next.js Consumer Portal allowing users to order food, book rides, and reserve hotels directly from desktop or mobile web browsers.
2. **`apps/web/partner/`**: Next.js Partner Portal featuring the Kitchen Display System (KDS), Grocery pick-and-pack checklist, Hotel reservation desk, and thermal bill printing.
3. **`apps/web/admin/`**: Next.js Superadmin Control Center allowing Elifsi staff to configure platform fees (Rs 10), delivery radius fees, driver commission cuts (80%), and dispute resolution without touching SQL.

---

## 6. Unified Startup Navigation Flow (Android & iOS Parity)

Both native client applications execute identical app launch lifecycles:

1. **Brand Splash Screen (`SplashScreen` / `SplashView`)**:
   - Hardware-accelerated 1.5-second animation.
   - Scale & alpha entrance curve, brand hold, and exit fade.
   - Verifies existing auth tokens in secure storage (EncryptedSharedPreferences / Keychain).
   - Routes to Onboarding (`.onboarding`) if first launch, or Phone Input (`.idle`) if previously completed.

2. **DoorDash-Grade Onboarding Walkthrough (`OnboardingScreen` / `OnboardingView`)**:
   - Driven by `OnboardingRepository` covering Nepal's 5 core verticals (Food, Grocery, Mobility, E2EE Chat, Stays).
   - Features smooth background color crossfades, expanding pill pagination indicators, and "Skip" button.
   - On completion or skip, transitions state to `.idle`.

3. **Authentication & Social Sign-In (`PhoneInputScreen` / `PhoneInputView`)**:
   - Primary: Nepal mobile number (`+977 98XXXXXXXX`) with WhatsApp and domestic SMS OTP channels.
   - Secondary: 1-tap Google, Apple, and Email sign-in options.
   - On OTP request, transitions to `.otpRequested` (`OtpVerifyScreen` / `OtpVerifyView`).
   - On verification or social sign-in, transitions to `.authenticated` to mount the 5-tab main coordinator scaffold.

