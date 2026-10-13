# SMS Verification & Authentication Architecture (Nepal Gateways)
**evrry Super App Platform**  
*Elifsi Technologies Private Limited*

---

## 1. Executive Summary

In Nepal's mobile ecosystem, international SMS aggregators (e.g. Twilio, MessageBird) are financially unviable and technically unreliable:
- **Twilio Cost**: ~NPR 6.00 to 10.00+ per SMS (international termination fees on NTC/Ncell).
- **Delivery Delays**: 3 to 5 minutes or frequent spam filtering by domestic telecom firewalls.
- **Local Industry Standard**: **Sparrow SMS (Janaki Technology)** and **Aakash SMS**, with direct SMPP links to Nepal Telecom (NTC) and Ncell, deliver OTPs in < 3 seconds at **NPR 1.00 to 1.40 per SMS**.

To allow seamless development **before** contracts are finalized with Sparrow or Aakash SMS, the evrry backend features an **Adaptive Gateway Architecture**:
1. **Current Development Phase**: Runs in **Dev Mock Mode** (NPR 0 cost, outputs OTPs to server logs, supports whitelisted test numbers).
2. **Production Launch Phase**: Simply inject the API token into Supabase secrets. The backend transitions to live domestic SMS delivery with **zero code modifications**.

---

## 2. System Flow

```
[ User enters Nepal Phone (+977 98XXXXXXXX) on Android / iOS / Web ]
                            │
                            ▼
           [ Supabase Auth: signInWithOtp({ phone }) ]
                            │
                            ▼
           [ Supabase Edge Function: send-sms ]
                            │
               ┌────────────┴────────────┐
               ▼                         ▼
   [ Rate Limiter RPC Check ]    [ Missing Tokens? ]
   Max 3 requests / 10 mins              │
   Min 45-second cooldown                ▼
               │                  [ DEV MOCK MODE ]
               ▼                  • Logs OTP to console
     [ Token Configured? ]        • Stores in sms_dispatch_logs
        ├── SPARROW_SMS_TOKEN ──> [ Sparrow SMS API ] ──> [ NTC / Ncell ] ──> [ User Phone ]
        └── AAKASH_SMS_AUTH_TOKEN ─> [ Aakash SMS API ] ──> [ NTC / Ncell ] ──> [ User Phone ]
```

---

## 3. How to Test Right Now (Zero-Cost Mock Mode)

You do **not** need an SMS provider account right now. Testing works immediately through two parallel mechanisms:

### Method A: Fixed Whitelisted Test Numbers
Configured in [`supabase/config.toml`](file:///home/rahul/codes/Evrry/supabase/config.toml):
* **Phone**: `+9779800000001` → **Fixed OTP**: `123456`
* **Phone**: `+9779800000002` → **Fixed OTP**: `123456`
* **Phone**: `+9779800000003` → **Fixed OTP**: `123456`

### Method B: Edge Function Mock Console Output
When any real Nepal mobile number (e.g. `9812345678`) requests an OTP, the Edge Function prints the code directly in your server terminal:
```
===================================================================
📱 [NEPAL SMS GATEWAY — DEV MOCK MODE]
Recipient:   +9779812345678 (Local: 9812345678)
OTP Code:    [ 492018 ]
Message:     Your evrry security code is 492018. Valid for 10 minutes.
Cost:        NPR 0.00 (Mocked development mode)
Notice:      Set SPARROW_SMS_TOKEN in Supabase secrets to go live.
===================================================================
```
You can simply read the 6-digit code from the terminal and authenticate.

---

## 4. How to Go Live (When You Receive Your Contract)

Once Elifsi Technologies signs the bulk SMS contract with Sparrow SMS or Aakash SMS:

### 1. If Using Sparrow SMS (Recommended)
You will receive:
- **API Token**: e.g., `sparrow_live_abcdef123456`
- **Sender ID (Identity)**: e.g., `EVRRY` or `ELIFSI`

Run these two commands:
```bash
supabase secrets set SPARROW_SMS_TOKEN="your_sparrow_token_here"
supabase secrets set SPARROW_SMS_FROM="EVRRY"
```

### 2. If Using Aakash SMS
Run:
```bash
supabase secrets set AAKASH_SMS_AUTH_TOKEN="your_aakash_auth_token_here"
```

**That is all.** The `send-sms` function automatically detects the new secret and immediately starts dispatching real telecom SMS to phones across Nepal.

---

## 5. Security & Anti-SMS Bombing Invariants (Migration 0014)

SMS attacks (where malicious bots request thousands of OTPs to drain corporate funds) are prevented by database-level rate limiting in [`supabase/migrations/20261005000014_sms_verification_and_rate_limiting.sql`](file:///home/rahul/codes/Evrry/supabase/migrations/20261005000014_sms_verification_and_rate_limiting.sql):

1. **Cooldown**: Minimum 45 seconds between sequential requests for the same number.
2. **Quota Window**: Maximum 3 requests within any rolling 10-minute (600s) window.
3. **Automated Lockdown**: Exceeding 3 attempts automatically locks the phone number for **15 minutes (900s)**, returning HTTP 429 (`rate_limit_exceeded`).
4. **Audit Trail**: Every attempt (mock or live) is logged to `public.sms_dispatch_logs` with timestamps, carrier cost (`cost_paisa`), and provider message IDs.

---

## 6. Client Implementations

All client platforms connect to the same centralized authentication engine:

| Platform | Location | Method |
|---|---|---|
| **Android Consumer (Kotlin)** | [`apps/consumer/android/.../EvrryAuthService.kt`](file:///home/rahul/codes/Evrry/apps/consumer/android/src/main/kotlin/com/elifsi/evrry/consumer/network/EvrryAuthService.kt) | `signInWithPhone()`, `verifyPhoneOtp()` |
| **Android Partner (Kotlin)** | [`apps/partner/android/.../EvrryPartnerAuthService.kt`](file:///home/rahul/codes/Evrry/apps/partner/android/src/main/kotlin/com/elifsi/evrry/partner/network/EvrryPartnerAuthService.kt) | `signInWithPhone()`, `verifyPhoneOtp()` |
| **iOS Consumer (Swift)** | [`apps/consumer/ios/.../EvrryAuthService.swift`](file:///home/rahul/codes/Evrry/apps/consumer/ios/EvrryConsumer/Services/EvrryAuthService.swift) | `signInWithPhone()`, `verifyPhoneOtp()` |
| **iOS Partner (Swift)** | [`apps/partner/ios/.../EvrryAuthService.swift`](file:///home/rahul/codes/Evrry/apps/partner/ios/EvrryPartner/Services/EvrryAuthService.swift) | `signInWithPhone()`, `verifyPhoneOtp()` |
| **Web (Next.js)** | [`apps/web/common/auth.ts`](file:///home/rahul/codes/Evrry/apps/web/common/auth.ts) | `signInWithPhone()`, `verifyPhoneOtp()` |

---

## 7. 1-Tap Social Sign-In (Google, Apple, Email) & Phone Binding

To lower initial registration friction while maintaining operational dispatch compliance in Nepal:
1. **Google Sign-In**:
   - 1-tap OAuth sign-in (`signInWithGoogle()`).
   - Retrieves user full name, verified email, and profile avatar.
2. **Apple Sign-In**:
   - Sign in with Apple (`signInWithApple()`) supporting relay email masking.
3. **Email Sign-In**:
   - Magic link or OTP email authentication (`signInWithEmail(email)`).
4. **Mandatory Nepal Mobile Binding Invariant**:
   - Social accounts are permitted for initial onboarding and browsing.
   - However, placing a food/grocery order or hailing a taxi requires a registered 10-digit Nepal mobile number (`+977 98XXXXXXXX`).
   - The user profile triggers a non-blocking prompt to bind and verify their domestic phone number for rider/driver phone contact.

---

## 8. Client Authentication Lifecycle State Machine

The client apps (Android Jetpack Compose & iOS SwiftUI) enforce a strict, unified state machine:

```
                  ┌──────────────────────┐
                  │ AuthScreenState.     │
                  │        Splash        │ (1.5-second brand animation)
                  └──────────┬───────────┘
                             │
            ┌────────────────┴────────────────┐
            ▼                                 ▼
   [ First-time install ]            [ Returning session ]
            │                                 │
            ▼                                 ▼
┌───────────────────────┐            ┌─────────────────┐
│   AuthScreenState.    │            │ AuthScreenState.│
│      Onboarding       │            │  Authenticated  │
└───────────┬───────────┘            └─────────────────┘
            │
            │ (User taps "Skip" or completes 5-vertical tour)
            ▼
┌───────────────────────────────────────────────┐
│             AuthScreenState.Idle              │
│  - Phone input field (🇳🇵 +977)                │
│  - Channel selector (WhatsApp vs Domestic SMS)│
│  - Continue with Google / Apple / Email       │
└───────────────────────┬───────────────────────┘
                        │
                        ▼ (User requests code)
┌───────────────────────────────────────────────┐
│          AuthScreenState.OtpRequested         │
│  - 6-box discrete PIN input                   │
│  - 45-second animated cooldown resend         │
└───────────────────────┬───────────────────────┘
                        │
                        ▼ (OTP verified)
┌───────────────────────────────────────────────┐
│        AuthScreenState.Authenticated          │
│  - Loads 5-tab main app (Home, Explore,       │
│    Chats, Rides, Activity/Orders)             │
└───────────────────────────────────────────────┘
```

