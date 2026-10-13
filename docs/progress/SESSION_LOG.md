# EVRRY Super App — Master Session & Progress Log
**Elifsi Technologies Private Limited**  
*Repository: [https://github.com/Elifsi/Evrry.git](https://github.com/Elifsi/Evrry.git)*  
*Last Updated: 2026-10-10*

> **ORIENTATION FOR ANY NEW AI OR DEVELOPER**:  
> Read this single document before touching any code. It documents the exact current state of the platform, the architecture invariants that must NEVER be violated, and what has been built across all sessions.

---

## 1. Golden Architecture Invariants (MUST ALWAYS FOLLOW)

1. **Zero Client Secrets**:
   - Native Android (Kotlin), iOS (Swift), and Web (Next.js) client apps must **NEVER** hold third-party API keys (`RESEND_API_KEY`, `SPARROW_SMS_TOKEN`, `ESEWA_SECRET_KEY`, `KHALTI_SECRET_KEY`, `FIREBASE_SERVICE_ACCOUNT`, `OPENROUTER_API_KEY`).
   - All third-party integrations run server-side in **Supabase Edge Functions** (`supabase/functions/`).
2. **Double-Entry General Ledger (`public.platform_ledger`)**:
   - Every financial transaction must be posted in balanced pairs (debits = credits).
   - Postgres trigger `assert_ledger_group_balanced()` enforces that an unbalanced posting rolls back the transaction.
3. **Nepal Pre-Registration 0% VAT Policy**:
   - The platform starts pre-VAT registration with `vat_bps = 0` (0%).
   - Stored in `public.platform_settings`. When IRD registration completes, changing this to `1300` (13%) activates VAT platform-wide with zero code changes.
4. **Zero-Hub COD Digital Settlement**:
   - No cash counters or physical branch visits for delivery riders.
   - Cash-in-hand is reconciled automatically via: (a) earnings offset during midnight settlement, (b) digital reverse QR payment (`settle_rider_cod_digital`), or (c) admin reconciliation.
   - Soft limit: `rider_cod_limit_paisa = 500000` (NPR 5,000).
5. **Mobile vs. Web Scope Separation**:
   - **Mobile Partner App (`apps/partner`)**: Contains **all 5 verticals** (Food, Grocery, Cabs, Hotels, Rentals) because smartphone cameras are essential for menu OCR/room photos, and mobile GPS is essential for driving/riding.
   - **Web Partner Dashboard (`apps/web/partner`)**: Strictly for **stationary venue merchants & hosts** (restaurants, groceries, hotels, landlords). Zero driving/riding on web.
   - **Multi-Business Switcher**: Single user (`auth.users`) owns multiple stores/hotels via `partner_profiles.owner_id` (1:N) with an Instagram-style profile switcher.

---

## 2. Database Migrations Inventory (`supabase/migrations/`)

| Migration File | Key Tables, Enums & Functions |
|---|---|
| `20261005000001_extensions_and_enums.sql` | PostGIS, btree_gist, pg_trgm, pgcrypto, all domain enums |
| `20261005000002_geography_spine.sql` | Nepal administrative spine (provinces, districts, municipalities, wards) |
| `20261005000003_identity_partners_admin.sql` | Profiles, partner_profiles, bank_accounts, KYC docs, admin audit logs |
| `20261005000004_catalog_orders.sql` | Stores, catalog_items, inventory, `place_order()`, orders state machine |
| `20261005000005_stays_rooms_rides.sql` | Stays, rooms (anti-double-booking gist), InDrive rides (`place_ride_bid`, `accept_ride_bid`) |
| `20261005000006_payments_ledger_payouts.sql` | Payments, platform_ledger (`assert_ledger_group_balanced`), settlement_batches, `confirm_payment` |
| `20261005000007_social_chat.sql` | E2EE keys, chats, messages, ephemeral burning snaps (`open_and_burn_snap`), 24h stories |
| `20261005000008_loyalty_vouchers_referrals.sql` | Loyalty leagues, daily check-in (Asia/Kathmandu), vouchers, referral chains |
| `20261005000009_ai_memory_and_retention.sql` | Privacy-gated AI memory (`get_ai_context`, `save_user_memory`), 50-item cap, 30-day purge |
| `20261005000010_privileges_and_storage.sql` | Function privilege lockdown (service_role only), Storage bucket RLS policies |
| `20261005000011_seed_nepal_geography.sql` | Deterministic seed data: 7 provinces, 77 districts, 753 local levels |
| `20261005000012_enhancements_reviews_reconciliation.sql` | Reviews table, 0% VAT default, rider COD safety limit, digital COD settlement (`settle_rider_cod_digital`) |
| `20261005000013_automated_invoice_dispatch_and_email_queue.sql` | `email_dispatch_queue`, trigger `trigger_queue_delivered_order_invoice` on order delivery |
| `20261005000014_sms_verification_and_rate_limiting.sql` | `sms_dispatch_logs`, `sms_rate_limits`, anti-bombing rate limiter RPC `check_sms_rate_limit` |
| `20261005000015_device_push_tokens.sql` | `user_device_tokens` (FCM/APNs), RPC `register_device_token`, dead-token pruning |
| `20261005000016_on_demand_instant_payouts.sql` | On-demand instant cash-out, RPC `request_on_demand_payout`, COD lock, instant fee |
| `20261005000017_ai_voice_personas.sql` | 4 Voice Personas (Ellie female, Rony male, Jenny female, Suka male), `ai_voice_personas` table, voice preferences, RPCs |
| `20261005000018_updated_pricing_and_delivery_rules.sql` | Rs 1000 free delivery threshold, 3km Rs 50 base, Rs 15/km extra, Rs 40 driver payout + 80% extra fee share, Rs 10 platform fee, ledger balancing |
| `20261005000019_whatsapp_otp_and_channel_dispatch.sql` | Meta WhatsApp Cloud API primary OTP, multi-channel failover to SMS, rate limiting & audit logs |
| `20261005000020_cron_schedules_and_automated_maintenance.sql` | pg_cron automated maintenance, midnight ConnectIPS settlement, stale ride bid expiration, ephemera purge |
| `20261010000001_call_logs.sql` | 1:1 Voice & Video Call logs (`call_logs`), status (`initiated`, `ringing`, `connected`, `ended`, `missed`, `declined`), duration in seconds, media type (`VOICE`, `VIDEO`), signaling metadata, and secure RLS |
| `20261010000002_chat_wallpapers_and_stickers.sql` | Per-chat customized wallpapers (`wallpaper_url`), sticker message support (`sticker_url`), expanded `message_type` check constraint (`STICKER`, `ANIMATED_EMOJI`), and secure `set_chat_wallpaper()` RPC |

---

## 3. Supabase Edge Functions Inventory (`supabase/functions/`)

All 11 functions are **100% implemented, production-ready, and support Dev Mock Mode** (running at zero cost when keys are omitted):

| Function | Primary Purpose | Supported Adapters & Features |
|---|---|---|
| **`send-otp/`** | Primary WhatsApp Cloud API OTP dispatcher with SMS failover | Meta Graph API v21.0, Copy Code button, Sparrow/Aakash domestic SMS fallback |
| **`send-sms/`** | Phone OTP verification for Nepal mobile carriers | Sparrow SMS, Aakash SMS, Dev Mock Mode, rate limiter (max 3 / 10m) |
| **`whatsapp-webhook/`** | Meta delivery status receipts & inbound events | Webhook verification challenge (`hub.challenge`), delivery receipts (`sent`/`delivered`/`read`) |
| **`routing/`** | Spatial turn-by-turn routing & navigation engine | OSRM routing engine, polyline decoding, duration & road distance across Nepal |
| **`send-email/`** | Customer tax invoices, OTPs, KYC notices, daily payout statements | Resend REST API, responsive HTML templates, Dev Mock Mode |
| **`payment-initiate/`** | Creates gateway payment sessions with server-side pricing | eSewa ePay v2 (HMAC-SHA256), Khalti v2 (`pidx`), ConnectIPS |
| **`payment-verify/`** | Verifies payment completion & updates ledger | eSewa server status API, Khalti `/epayment/lookup/`, calls `confirm_payment` |
| **`payout-execute/`** | Midnight partner settlement & banking disbursement | Audits balances, offsets rider cash, ConnectIPS NCHL CSV export, eSewa & Khalti direct payout adapters |
| **`push-notify/`** | High-priority push alerts to devices | Firebase Cloud Messaging (FCM HTTP v1) via OAuth2, APNs, dead token pruning |
| **`process-outbox/`** | Resilient transactional outbox worker | Polls `email_dispatch_queue`, retries failures with exponential backoff |
| **`ai-gateway/`** | Server-side AI Concierge, 4 Voice Personas & WebRTC Room Broker | OpenRouter/Gemini, user memory (`get_ai_context`), 4 Personas (Ellie, Rony, Jenny, Suka), `create_voice_room` (LiveKit WebRTC), Photo-to-Menu OCR, Kitchen Voice |

---

## 4. Chronological Session History

### Session 1: Platform Foundation & Core Verticals
- Analyzed and merged food, grocery, ride-hailing (InDrive bidding), stays, and long-term rental business models.
- Established the double-entry general ledger architecture with deferred balancing triggers.
- Formulated the Zero-Hub COD digital settlement math and Pre-VAT 0% policy.
- Implemented Migrations `0001` through `0012`.

### Session 2: Resend Email Subsystem
- Designed and tested responsive HTML tax invoice and OTP verification templates.
- Pinned `resend@^6.1.1` and wrote 4 Vitest unit tests verifying currency formatting, HTML output, and mock modes.
- Committed under git commit `6a7588b`.

### Session 3: Universal Backend & Real-Time AI Voice Streaming Stack
- **Universal Email Edge Function & Invoicing Trigger**:
  - Implemented `supabase/functions/send-email/` and Migration `0013` (`email_dispatch_queue`).
  - Created client bridges in Kotlin, Swift, and Next.js Web. Committed `906fd59`.
- **Nepal SMS & Dual Auth Infrastructure**:
  - Implemented Migration `0014` (`sms_dispatch_logs`, `sms_rate_limits`, anti-bombing rate limiter RPC).
  - Built `supabase/functions/send-sms/` supporting Sparrow SMS, Aakash SMS, and Dev Mock Mode.
  - Added whitelisted test numbers in `supabase/config.toml`. Committed `6836f21`.
- **Nepal Direct Payment Rails**:
  - Implemented `supabase/functions/payment-initiate/` and `payment-verify/` for eSewa v2 HMAC, Khalti v2 pidx, and Fonepay dynamic QR.
  - Created client payment bridges for Android, iOS, and Web. Committed `8b5aa37`.
- **Midnight Settlement & ConnectIPS Disbursement**:
  - Implemented `supabase/functions/payout-execute/` supporting automated audit, COD offset, ConnectIPS CSV export, and email receipts. Committed `c93142e`.
- **Push Notification Service**:
  - Implemented Migration `0015` (`user_device_tokens`) and `supabase/functions/push-notify/` supporting FCM v1, APNs, and dead token auto-cleaning. Committed `e575442`.
- **24/7 On-Demand Instant Payouts**:
  - Implemented Migration `0016` and instant payout execution with Zero-Hub COD lock and NPR 15 flat fee. Committed `5052fe8`.
- **Automated Direct Payouts (eSewa & Khalti)**:
  - Added direct wallet payout adapters `disburseViaEsewa()` and `disburseViaKhalti()` to `supabase/functions/payout-execute/index.ts`.
  - Supports automated disbursements for both on-demand instant payouts and midnight batch settlement with idempotency keys and error-resilient fallbacks. Committed `f77c9e3`.
- **4 Persona AI Voice Agents & Low-Latency WebRTC Pipeline**:
  - Implemented Migration `0017` (`20261005000017_ai_voice_personas.sql`) defining the 4 Voice Personas (**Ellie** [Female], **Rony** [Male], **Jenny** [Female], **Suka** [Male]), public personas catalog, and user preference RPCs.
  - Refactored persona naming to avoid OpenAI trademark conflicts (renamed Sol to Suka [Male], finalized Ellie as Female flagship guide). Committed `5c1ef17`.
  - Built the Python AI Voice Gateway microservice in `services/ai-voice-gateway/` with:
    - Faster-Whisper in-memory PCM ASR + AI4Bharat IndicConformer.
    - AI4Bharat IndicXlit Romanized-to-Devanagari real-time phonetic transliteration.
    - Piper-TTS & AI4Bharat Indic-Parler-TTS with dynamic persona pitch and speed modulation.
    - Dialog orchestrator with tool schemas (`search_catalog`, `add_to_cart`, `calculate_ride_fare`).
    - Real-time `VoicePipeline` featuring VAD, turn completion detection, and barge-in / interruption handling.
    - LiveKit WebRTC Worker Agent (`LiveKitVoiceAgent`) & direct binary WebSocket (`/ws/voice-agent`).
  - Added LiveKit WebRTC server to `docker-compose.yml`.
  - Upgraded `supabase/functions/ai-gateway/index.ts` to broker WebRTC room tokens (`create_voice_room`) and inject persona prompts.
  - Updated multiplatform client services (`EvrryAiService.kt`, `EvrryAiService.swift`, `apps/web/common/ai.ts`).
  - Created unit tests in `prototype/Phone/lib/ai/personas.test.ts`.
- **Dynamic Platform Fee Architecture (Rs 5 Default)**:
  - System default set to **Rs 5.00** (`500` paisa) in `public.platform_settings.platform_fee_paisa`.
  - Client checkout calculation in `prototype/Phone/lib/pricing.ts` enforces `platformFee = 5`.
  - Database trigger `post_order_settlement` posts customer platform fees directly to `platform_revenue` in the double-entry general ledger.
  - Invoice generation in `email_dispatch_queue` itemizes `platformFeePaisa` transparently on receipts.
- **Zero-Fee Map Routing & Navigation Engine (OSRM & Google Maps)**:
  - Setup pipeline script `infra/osrm/setup-osrm-nepal.sh` to download and preprocess Geofabrik `nepal-latest.osm.pbf` for the self-hosted OSRM container (`docker-compose.yml`).
  - Implemented `supabase/functions/routing/index.ts` supporting `action: 'route'`, `action: 'nearest'` (GPS road snapping), and `action: 'matrix'` (multi-driver dispatch matching) with automatic fallback to high-precision Haversine + 1.28 circuity model.
  - Built multiplatform client routing bridges:
    - Consumer Android: `EvrryMapRoutingService.kt`
    - Partner Driver/Rider Android: `EvrryPartnerMapRoutingService.kt`
    - Consumer iOS: `EvrryMapRoutingService.swift`
    - Partner Driver/Rider iOS: `EvrryPartnerMapRoutingService.swift`
    - Web Common: `apps/web/common/routing.ts`
    - Prototype: `prototype/Phone/lib/maps/polyline.ts` & `routing.ts`
  - Created unit tests in `prototype/Phone/lib/maps/routing.test.ts`. Committed `00a5cf3`.
- **Superadmin Operations & Zero-SQL Control Center Specification**:
  - Authored comprehensive architecture document `docs/architecture/superadmin-panel-control-center.md` providing complete UI wireframes, form schemas, and RPC workflows.
  - Covers dynamic delivery fees by vertical (Food, Mart, Parcel), global & partner-level commissions, platform fees (Rs 5 default + promotional waiver toggle), voucher creation wizard with budget & per-user limits, split-screen KYC document verification, midnight settlement payouts, and emergency ward kill-switches—all completely dynamic without manual SQL commands.
- **Updated Delivery Fees, Free Threshold, Driver Earning Share & Platform Fee**:
  - Implemented Migration `0018` (`20261005000018_updated_pricing_and_delivery_rules.sql`):
    - **Free Delivery Threshold**: Set to **Rs 1,000** (`100,000` paisa). Orders >= Rs 1,000 get free delivery.
    - **Customer Delivery Fee**: **Rs 50** (`5,000` paisa) within 3 km; **Rs 15 per km** (`1,500` paisa) extra beyond 3 km.
    - **Driver Delivery Payout**: **Rs 40** (`4,000` paisa) base up to 3 km; plus **80%** (`8,000` bps) of extra delivery fee beyond 3 km.
    - **Platform Delivery Share**: Platform retains Rs 10 base delivery margin + 20% of extra delivery fee.
    - **Free Delivery Subsidy**: When order >= Rs 1,000, platform subsidizes driver payout as marketing expense (`marketing_expense`), maintaining exact mathematical double-entry balance.
    - **Platform Fee**: Updated to **Rs 10.00** (`1,000` paisa) flat per order.
  - Updated `prototype/Phone/lib/pricing.ts` and unit tests in `prototype/Phone/lib/pricing.test.ts`.
- **Full Verification**:
  - Ran complete test suite: 15/15 test files passed, 97/97 unit and integration tests passed (100% success).
  - Next.js 16 build passed: 41/41 routes compiled successfully with 0 type errors.

### Session 4: WhatsApp OTP Verification, pg_cron Maintenance & 100% Backend Completion
- **WhatsApp Cloud API OTP Engine with Domestic SMS Failover**:
  - Implemented Edge Function `send-otp/` integrating Meta Graph API v21.0 authentication message templates (`evrry_auth_code`) with interactive 1-tap "Copy Code" button.
  - Formulated 60-second cooldown timer UX: primary dispatch to WhatsApp, automatic failover to Sparrow/Aakash domestic SMS.
  - Implemented Migration `0019` (`20261005000019_whatsapp_otp_and_channel_dispatch.sql`) extending `sms_dispatch_logs` with multi-channel audit trail and `check_otp_rate_limit` RPC.
  - Implemented `whatsapp-webhook/` Edge Function for Meta delivery status callbacks (`delivered`, `read`) and incoming messages.
  - Updated client network bridges across Android (`EvrryAuthService.kt`, `EvrryPartnerAuthService.kt`), iOS (`EvrryAuthService.swift`), and Web (`auth.ts`).
- **Automated Maintenance & pg_cron Schedules**:
  - Implemented Migration `0020` (`20261005000020_cron_schedules_and_automated_maintenance.sql`) registering automated cron jobs for:
    - Midnight ConnectIPS bank settlement (`settle_daily_merchant_balances`).
    - Stale InDrive ride bid expiration (`expire_stale_ride_bids`, runs every 2 mins).
    - Unpaid hold release (`expire_unpaid_holds`, runs every 5 mins).
    - Ephemeral media purge (`purge_expired_ephemera`, runs hourly).
    - Rate limit pruning (`cleanup_expired_rate_limits`, runs daily).
- **Master API Key Centralization & 1-Click Sync Script**:
  - Consolidated all 24+ environment secrets into a single master template: `.env.example`.
  - Built `scripts/sync-secrets.sh` to upload all backend keys directly to Supabase Edge Functions with a single command.
- **Architectural Documentation Additions**:
  - Authored `docs/architecture/self-hosting-and-infrastructure-guide.md` (Docker Compose topology, RAM/CPU sizing, storage growth, backup recovery).
  - Authored `docs/architecture/unified-mobile-ui-and-ios-navigation.md` (Android & iOS 1:1 UI parity, mandatory iOS in-app top-left back buttons, modal dismissal rules).
  - Authored `docs/architecture/whatsapp-otp-verification.md` (Meta Graph API v21.0 setup, template specification, dual-rail failover).
- **100% Backend Verification**:
  - Created and ran `scripts/verify-backend-readiness.sh`:
    - All 20 SQL migrations verified.
    - All 11 Supabase Edge Functions verified.
    - All Client Network Bridges verified.
    - 15/15 test files passing (97/97 tests), 0 build errors.

---

## 6. Session Update: Web Map Strategy, 3D Tracking & Prototype Clarifications

- **Documentation Accuracy & Prototype Delineation**:
  - Corrected misleading descriptions of `prototype/Phone/` across all repository documentation (`README.md`, `AGENTS.md`, `ARCHITECTURE.md`, `apps/web/README.md`, `apps/web/consumer/README.md`, `apps/consumer/ios/README.md`, `apps/consumer/android/README.md`, `docs/roadmap/README.md`, `prototype/Phone/README.md`).
  - Explicitly established that `prototype/Phone/` is strictly an **Interactive Mobile Phone Prototype** (simulates smartphone viewport, bottom navigation bar, smartphone touch interactions, and camera/mic flows) serving as the **Smartphone UI & UX Golden Master** for native iOS and Android apps, NOT a desktop web app. Production web portals live in `apps/web/`.
- **Map & Geolocation Architecture Division**:
  - Authored comprehensive specification: [`docs/architecture/mapping-routing-and-vehicle-tracking.md`](file:///home/rahul/codes/Evrry/docs/architecture/mapping-routing-and-vehicle-tracking.md).
  - **Zero-Cost Web Mapping**: `apps/web/consumer` uses **MapLibre GL and OpenStreetMap tiles** (100% open-source, WebGL accelerated, $0 cost with unlimited map loads) to avoid Google Maps JavaScript API fees ($7.00/1,000 loads).
  - **Live Tracking Helper Banner**: Web order and ride tracking renders a lightweight route polyline alongside a persistent helper prompt:
    > 📱 *"For real-time 3D vehicle tracking and turn-by-turn navigation, open in the Evrry Phone App"*
  - **Mobile 3D Isometric Vehicle Tracking**: Native Android and iOS apps use Google Maps SDK (100% free unlimited loads on Android, high free tier on iOS) rendering smooth **3D isometric vehicle markers (Scooter, Motorcycle, and Cab/Car)** with dynamic 360° bearing rotation along OSRM road routes and realistic ground drop-shadows.
  - **Shared Routing Layer**: Both web and mobile use the self-hosted OSRM engine via Supabase Edge Function `routing` (`EvrryWebRoutingClient` in `apps/web/common/routing.ts`).
  - Added `VehicleType`, `VehicleTrackingState`, `calculateBearing` mathematical helper, and `WEB_LIVE_TRACKING_PROMPT` to `apps/web/common/routing.ts`.
- **Platform Roadmap Phase Synchronization**:
  - Marked Phase 1 (Shared Backend & Database Foundations) as **100% Complete** across all 20 SQL migrations, 11 Edge Functions, PostGIS, Storage, and pg_cron maintenance schedules in `docs/roadmap/README.md`.
  - Updated Phase 2 to focus on Production Web Applications (`apps/web/admin`, `apps/web/common`, `apps/web/consumer`, `apps/web/partner`).

---

---

## 7. Frontend Milestone: Superadmin Operations Console (`apps/web/admin`) Complete

- **Full Application Scaffolding**:
  - Scaffolded Next.js 16 (Turbopack App Router), React 18, TypeScript, Tailwind CSS, Lucide Icons, and `@supabase/ssr` / `@supabase/supabase-js`.
  - Built `AdminSidebar` with MFA security badge, live environment status (`KTM-DC-01`), and multi-section navigation.
  - Built `AdminHeader` with OCI cluster health, Postgres 16 RLS status, and breadcrumbs.
- **Implemented Modules & Pages**:
  - **Overview Dashboard (`/`)**: Live GMV (NPR 2.26M), Platform gross revenue, pending KYC alert, and immutable audit trail (`public.admin_audit_logs`).
  - **Dynamic Pricing & Commission Hub (`/pricing`)**: Sliders and inputs for the **Rs 10 platform fee**, fee waiver toggle, **Rs 1,000 free delivery threshold**, **Rs 50 base (3 km)**, **Rs 15/km extra**, **Rs 40 driver base pay + 80% extra cut**, vertical commission take-rates, and instant live 5 km delivery financial calculator.
  - **Partner KYC Verification Queue (`/compliance/kyc`)**: Split-screen interface with high-resolution pan/zoom/contrast viewer for smartphone uploads of Nepali Smart Driving Licenses, Vehicle Blue Book tax clearance stamps, and IRD PAN certificates; 1-click Approve & Activate, Reject with pre-configured reasons, and Request Re-upload with automated SMS trigger.
  - **Midnight Settlement & Payouts Console (`/finance/payouts`)**: Daily settlement aggregation across 48 venues and 112 riders, ledger deductions breakdown, downloadable **ConnectIPS NCHL-128 batch clearing file**, and **Zero-Hub Rider COD Safety Monitor** (Rs 3,500 soft warning and Rs 5,000 automatic hard safety lock).
  - **Vouchers & Marketing Engine (`/marketing/vouchers`)**: Campaign creation wizard (`EVRRYNEW100`), per-user limits, budget burn tracker, and pause/resume controls.
  - **Compliance & Emergency Governance (`/settings/governance`)**: Inland Revenue Department **13% VAT one-click activation**, monsoon weather surge slider (1.0x–2.0x), and ward-level emergency kill-switches.
- **Build & Quality Validation**:
  - `npm run build` in `apps/web/admin`: **8/8 routes compiled successfully, 0 errors**.
  - `npm test` in `prototype/Phone`: **15/15 test suites passed, 97/97 tests passed (100%)**.
  - `bash scripts/verify-backend-readiness.sh`: **100% verified (20 migrations, 11 edge functions, client bridges)**.

---

---

## 8. Consumer Web & Partner Milestones

1. **Partner Web Operations Prototype (`prototype/Partner`)**:
   - Fresh standalone Next.js 16 setup with Lucide, Framer Motion, and Tailwind CSS.
   - 4-mode partner workflow: Restaurant KDS with order acceptance & preparation stages, Retail/Grocery inventory & scanner, Cab/Bike Driver dispatch & ride offers, Hotel PMS reservation calendar.
2. **Consumer Phone Prototype Reference (`prototype/Phone`)**:
   - Full mobile phone UX golden master in Next.js 16 (App Router).
   - Minimalist ChatGPT model switcher (`[ Ellie ▾ ]`), dynamic multi-vertical catalog, and wallet simulation.

---

## 9. Parallel Native Consumer Mobile Apps (Android & iOS) Fully Implemented

- **Parallel Dual-Native Architecture**:
  - Implemented 1:1 structural parity across **Android** (`apps/consumer/android`) in Kotlin + Jetpack Compose and **iOS** (`apps/consumer/ios`) in Swift + SwiftUI.
  - **Shared Domain Models**:
    - `AuthState`: Phone validation, OTP status, active user profile.
    - `VoiceModel`: 4 official personas (**Ellie**, **Rony**, **Jenny**, **Suka**). Zero Sol references.
    - `CatalogItem`: Multi-vendor products with pricing and stock.
    - `CartState`: Quantities, item add/remove, subtotal, and fee computation.
    - `RideBooking`: Moto, Cab, and Comfort tiers with live fare calculations and ride OTP.
  - **Shared Network & Client Services**:
    - `EvrryAuthService`: Nepal phone auth + WhatsApp primary OTP + SMS fallback.
    - `EvrryAiService`: WebRTC voice room token minting & Ellie AI concierge.
    - `EvrryMapRoutingService`: OSRM routing and turn-by-turn geometry.
    - `EvrryPaymentService`: eSewa, Khalti, and Fonepay integration bridges.
    - `EvrryPushService`: FCM and APNs push notification token registration.
    - `EvrryEmailService`: Resend transactional notifications.
  - **Reactive ViewModels**:
    - `AuthViewModel`: +977 phone validation, 45s resend timer, 6-box OTP auto-verification.
    - `HomeViewModel`: Minimalist ChatGPT model switcher, pulsating voice waveform orb states (`idle`, `listening`, `thinking`, `speaking`).
    - `ExploreViewModel`: Multi-vendor filters, search, quick-commerce counter steppers.
    - `RideViewModel`: Ride booking state machine, driver assignment, ride start OTP.
    - `ActivityViewModel`: Real-time order progress steppers, Rider live tracking HUD, itemized receipts.
- **Unified UI & In-App Navigation (iOS Hardware Parity)**:
  - Addressed the complete lack of a physical hardware back button on iOS devices.
  - Standardized `EvrryTopBar` with an explicit in-app back button (`<`, tap target $\ge 44 \times 44\text{ pt}$) on every sub-screen, sheet, and detail view across both Android and iOS.
  - Aligned official design tokens: `EvrryNavy` (`#0E1B38`), `EvrryBlue` (`#2563EB`), `EvrryEmerald` (`#059669`), `EvrryPaper` (`#F8FAFC`).
- **AI Voice Persona Polish**:
  - Persona officially confirmed as **Ellie** (replacing Eli). Purged all references to "Sol" in favor of **Suka**.
  - ChatGPT-style minimal switcher `[ Ellie ▾ ]` positioned on the Home top bar.
  - Zero manual language toggle pills; AI conversational pipeline automatically detects and mirrors **Nepali**, **Hindi**, **Maithili**, or **English**.

---

## 10. Purge of In-App Wallet & Production Android Studio Project Stabilization

- **Total In-App Wallet Purge**:
  - In strict compliance with Nepal Rastra Bank (NRB) guidelines prohibiting non-licensed marketplace operators from holding customer funds in open-loop stored-value balances, all in-app wallet screens (`WalletScreen.kt`, `WalletViewModel.kt`, `WalletView.swift`, `WalletViewModel.swift`) and `.wallet` checkout options were completely purged.
  - Replaced by the 4th canonical navigation tab: **Activity & Orders** (`ActivityScreen.kt`, `ActivityViewModel.kt` on Android; `ActivityView.swift`, `ActivityViewModel.swift` on iOS).
- **Activity & Orders Screen Implementation**:
  - Multi-tab filter state: `All`, `Active`, `Completed`.
  - 5-stage visual order stepper (`Placed` → `Confirmed` → `In Kitchen` → `On the Way` → `Delivered`).
  - Active Rider HUD with vehicle license plate (`Ba 2 Pa 4521`), driver name (`Ram Bahadur`), 4.9 rating, and live Ride start OTP (`OTP: 1234`).
  - Itemized receipt breakdown formatted in Nepali Rupees (`Rs.`) with direct domestic payment method tags (`Paid via eSewa`, `Khalti`, `Fonepay QR`, `Cash on Delivery`).
  - Fixed Rs 10 platform fee added to Cart checkout with direct payment gateway selector.
- **Full Android Studio Project Configuration**:
  - Android Gradle multi-project layout configured in `apps/consumer/android/`:
    - Gradle 8.9 wrapper (`gradlew`, `gradlew.bat`, `gradle-wrapper.jar`).
    - Root `settings.gradle.kts` and `build.gradle.kts` with AGP 8.5.2 & Kotlin 1.9.24.
    - Application module `app/build.gradle.kts` targeting Android 14 (compileSdk 34, minSdk 26).
    - Jetpack Compose BOM `2024.06.00`, Material 3, Navigation Compose `2.7.7`, ViewModel Compose `2.8.2`.
    - Android Manifest with network, audio, and location permissions.
    - Resource xmls: `strings.xml`, `colors.xml`, `themes.xml`.
    - `MainActivity.kt` hosting `EvrryConsumerNavHost`.

---

## 11. Full Manual Food Delivery Vertical Implementation (Parallel Android & iOS)

- **Manual Food Delivery Sub-App Built Inside Super App**:
  - Replicated golden master prototype (`prototype/Phone/app/explore/food/` & `prototype/Phone/components/RestaurantMenuView.tsx`) into complete production native implementations on both Android and iOS.
  - **Authentic Nepal Food Catalog & Models**:
    - Created `FoodModels.kt` (Android) and `FoodModels.swift` (iOS).
    - Top Kathmandu & Lalitpur restaurants: Roadhouse Cafe (Pulchowk), Jimbu Thakali Kitchen (Jhamsikhel), Dalle Restaurant (Kamaladi), Fire & Ice Pizzeria (Thamel), The Burger House & CFC (New Baneshwor), Mitho Mitho Organic Pure Veg (Patan Durbar Square).
    - 20+ itemized dishes across categories (Momo, Thakali Thali, Woodfired Pizza, Burgers, Desserts) with genuine descriptions, prices in NPR, veg indicators, and bestseller flags.
  - **Dedicated Food Home Screen (`FoodHomeScreen.kt` / `FoodHomeView.swift`)**:
    - Pinned Top Navigation with in-app back (`< Explore Verticals`) and delivery ETA badge (`⚡ 25-35 min delivery`).
    - Delivering to location pill: `📍 Delivering to: Jhamsikhel, Lalitpur`.
    - Promotional Perks Banner: `EVRRY Food Fest` (20% OFF on Roadhouse & Jimbu, `NEPAL20`, free delivery on Rs 1,000+, 🍕).
    - Real-time search bar for dishes, cuisines, and restaurants with clear button.
    - Horizontal Cravings Carousel: `🍽️ All`, `🥟 Momo & Newari`, `🍲 Thakali Thali`, `🍕 Woodfired Pizza`, `🍔 Burgers & CFC`, `🍱 Japanese & Sushi`, `🥐 Bakery & Desserts`, `🥗 Pure Veg`.
    - Quick Filter Chips: `Rating 4.5+ ★`, `Pure Veg 🟢`, `Under 30 mins ⚡`, `Special Offers 🏷️`.
    - Open Restaurants catalog cards with gradient banners, rating badges (★ 4.8), ETA (28 min), area (Pulchowk, Lalitpur), price for two, and offer badges.
  - **Dedicated Restaurant Menu Detail Screen (`RestaurantDetailScreen.kt` / `RestaurantDetailView.swift`)**:
    - Hero header with restaurant gradient accent and in-app back navigation (`< Food`).
    - Restaurant metadata row: Rating badge, delivery ETA, location, price for two, promo offer badge, pure veg badge.
    - Categorized menu sections (e.g. `Woodfired Pizza`, `Starters`, `Thakali Thali`, `Momo Specials`, `Desserts`).
    - Dish row items:
      - Veg / Non-Veg indicator dot (`🟢` green box / `🔴` red box).
      - Bestseller badge.
      - Title, description / ingredients, price in NPR (`Rs 650`).
      - Dish preview thumbnail.
      - Blinkit / DoorDash style interactive quantity stepper (`ADD` button transforms to `− [qty] +` stepper).
  - **Single-Restaurant Cart Invariant**:
    - Adding an item from a different restaurant automatically clears items from the previous restaurant and triggers an alert banner: `Started a fresh order from {restaurant} — items from your previous restaurant were cleared.`
  - **Seamless Navigation & Checkout Transition**:
    - Bottom Floating Cart Bar displays item count, subtotal in NPR, and `View Cart →`.
    - Transitions directly to `CartCheckoutScreen` / `CartCheckoutView` with breakdown: Subtotal + Rs 50 Delivery Fee + Rs 10 Platform Fee.
    - Direct payment methods: eSewa, Khalti, Fonepay Dynamic QR, and Cash on Delivery.
    - Order placement transitions directly to Activity tab (`ActivityScreen` / `ActivityView`) with 5-stage live tracking stepper and Rider HUD with OTP `1234`.
  - **Compilation & Verification**:
    - Compiled `./gradlew assembleDebug` in `apps/consumer/android`: **BUILD SUCCESSFUL** with **0 errors**, generating `app-debug.apk` (16.4 MB).
    - Compiled `./gradlew test` in `apps/consumer/android`: **BUILD SUCCESSFUL** across 40 tasks.

---

## 12. Deliverr & Enatega Architectural Integration, Ellie Voice Models & Complete Wallet Purge

- **Direct Incorporation from Reference Repositories**:
  - Incorporated core architecture and business logic from [`reference/kaaneneskpc_Deliverr`](../../reference/kaaneneskpc_Deliverr) and [`reference/enatega_food-delivery-multivendor`](../../reference/enatega_food-delivery-multivendor).
  - **Item Customization Sheet (`FoodItemCustomizationSheet`)**:
    - Ported from Deliverr's `FoodDetailsScreen` & Enatega's `ItemDetail`.
    - Modal bottom sheet displaying dish image, title, veg/non-veg status, and dynamic base price.
    - Radio button option selectors (e.g. preparation, size) with price deltas.
    - Checkbox add-ons & extras with itemized prices.
    - Special cooking instructions text field for restaurant kitchen notes.
    - Interactive quantity stepper and dynamic "Add to Cart · Rs {total}" CTA.
  - **Enatega Multi-Restaurant Cart Replacement Alert**:
    - When adding an item from a different restaurant while items already exist in the cart, an interactive `AlertDialog` warns: `"Replace cart items? Discard them and start a new order with {restaurant}?"` with confirmation to discard or cancel.
  - **Enhanced Cart & Checkout Flow**:
    - Added `DeliveryAddress` and default saved addresses (`DefaultAddresses.HOME` / `OFFICE`) with modal selector sheet.
    - Rendered customized option badges on cart line items.
    - Integrated special cooking instructions field passed from cart to order.
    - Subtotal + Delivery Partner Fee (Rs 50) + Platform Fee (Rs 10).
    - Domestic Direct Payment methods only (eSewa, Khalti, Fonepay QR, COD).
  - **Order Confirmation & Live Tracking Lifecycle**:
    - `OrderSuccessScreen` (Android) / `OrderSuccessView` (iOS): Deliverr-style animated success badge, Order ID `#ord-XXXX`, arrival ETA, and "Track Live Status" CTA.
    - `OrderDetailsScreen` (Android) / `OrderDetailsView` (iOS): 5-stage status stepper (`placed` → `confirmed` → `kitchen` → `delivering` → `delivered`), live route & dispatch card with GPS socket simulation, driver HUD with driver name, motorbike number, and direct "Call" action, delivery destination, cooking instructions, and itemized receipt breakdown with payment method.
    - Dynamic dispatch: Placing an order in Cart creates an `OrderActivityItem` in `ActivityViewModel`, clears the cart, and routes directly to `OrderSuccessScreen`.
    - Activity Tab order cards are clickable, opening `OrderDetailsScreen` with in-app back navigation.

- **AI Voice Model Overhaul (Ellie Mini & Ellie Pro)**:
  - Replaced theatrical 4-persona setup (which had `Suka`, emojis, and gimmicky bios) with a clean ChatGPT-style model switch:
    - **Ellie Mini** (`Fast · Everyday`): Lightweight, ultra-responsive model for instant food orders, rides, and daily tasks.
    - **Ellie Pro** (`Deep Reasoning`): Advanced reasoning model for multi-stop itinerary planning and complex queries.
  - Retained automatic multilingual mirroring (Nepali, Hindi, Maithili, English).

- **Complete Elimination of In-App Wallet (NRB Compliance)**:
  - Completely purged `walletBalanceNpr` from `UserProfile` models and ViewModels on both Android and iOS.
  - The app strictly utilizes direct domestic payment gateways (eSewa, Khalti, Fonepay Dynamic QR, and Cash on Delivery).

- **Build Verification**:
  - Android (`apps/consumer/android`): `./gradlew assembleDebug` compiled with 0 errors (`BUILD SUCCESSFUL in 2m 8s`), producing `app/build/outputs/apk/debug/app-debug.apk` (16.4 MB).
  - In-app back navigation (`<`) verified on every screen with minimum $44 \times 44\text{ pt/dp}$ touch target on both Android and iOS.

---

## 13. Dynamic 1.5s Animated Splash Screen, DoorDash-Grade 5-Vertical Onboarding Walkthrough & 1-Tap Social Login

- **Brand Animated Splash Screen (1.5s Duration)**:
  - **Android (`SplashScreen.kt`) & iOS (`SplashView.swift`)**:
    - Animated entrance (scale 0.88 → 1.0, alpha 0 → 1.0) over 450 ms, brand hold for 700 ms, exit crossfade for 350 ms (total duration exactly 1500 ms).
    - Evrry rounded squircle brandmark badge (`92×92 dp/pt`, 26 dp radius, `#2563EB`) with bold "e", `evrry` wordmark with `🇳🇵` Nepal flag, and the official tagline *"Nepal's Everything Super App"*.
    - **Thermal & Battery Optimization**: Native hardware-accelerated Compose `Animatable` and SwiftUI `.animation` primitives with zero unthrottled timer loops or heavy video files to prevent device overheating and battery drain.
    - **Auto-Routing**: Checks saved session / local storage. If authenticated → routes to Home; if first-time install → routes to Onboarding (`finishSplash(hasSeenOnboarding = false)`); if returning unauthenticated → routes to Phone Login.

- **DoorDash-Grade Dynamic Onboarding Walkthrough (5 Core Verticals)**:
  - **Dynamic Data Model & Repository (`OnboardingPage` & `OnboardingRepository`)**:
    - Extensible data model with `id`, `badge`, `title`, `subtitle`, `accentColor`, `backgroundColor`, `icon/systemImage`, `highlights`, `previewMetric`, and `previewMetricLabel`.
    - **5 Core Slides Implemented**:
      1. **Food Delivery & Dining** (`#EF4444` Coral Red): *"Hot & Fresh Meals, Delivered Fast"* · 28 min avg delivery · 450+ verified kitchens · Live GPS rider tracking.
      2. **Grocery Quick Mart** (`#10B981` Emerald Green): *"Groceries & Daily Essentials in 15 Mins"* · 15 min express dispatch · Farm-fresh produce · First 3 orders free delivery.
      3. **Mobility & Rides** (`#2563EB` Royal Blue): *"Fast & Safe Rides Across Nepal"* · 3 min pickup ETA · Upfront transparent fares · 24/7 In-ride SOS security.
      4. **End-to-End Encrypted Chat** (`#8B5CF6` Purple): *"Private Chat, HD Calls & Money Sharing"* · 100% Signal-grade Double Ratchet E2EE · Crystal HD voice/video calls · In-chat bill split.
      5. **Hospitality & Rentals** (`#F59E0B` Warm Amber): *"Boutique Stays & Vehicle Rentals"* · 4.9★ rating · Verified host guarantee · Instant digital check-in.
  - **Interactive UI & Motion Controls (`OnboardingScreen.kt` & `OnboardingView.swift`)**:
    - Smooth background color crossfading synced with vertical theme colors as users swipe through slides.
    - Center layered artwork card (`240×240 dp/pt`) with drop shadow, category badge, central hero vector icon, floating live metric badge, bold headline, descriptive subtitle, and checked feature highlight chips.
    - **Expanding Pill Page Indicator**: Active page smoothly expands from 8 dp to 24 dp with direct tap-to-navigate support.
    - Top-bar "Skip" action and bottom "Continue / Get Started with Evrry" CTA.

- **1-Tap Social Sign-In (Google, Apple, Email)**:
  - **Android (`PhoneInputScreen.kt`) & iOS (`PhoneInputView.swift`)**:
    - Added `─── OR CONTINUE WITH ───` subtle divider below the primary Nepal mobile number (`🇳🇵 +977`) input and verification button.
    - **Continue with Google**: Pure white surface, subtle 1 px border, official Google `G` emblem, invoking `signInWithGoogle()`.
    - **Continue with Apple**: Premium `#0F172A` deep charcoal surface with official `` Apple icon, invoking `signInWithApple()`.
    - **Continue with Email**: White surface with outline envelope icon, opening an inline email dialog/sheet, invoking `signInWithEmail(email)`.
    - **Mandatory Nepal Mobile Binding Invariant**: Social sign-in accounts receive a non-blocking prompt to bind and verify their 10-digit Nepal mobile number (+977) for rider/driver phone contact during dispatches.

- **Root Navigation Integration & Compilation Verification**:
  - Android [`EvrryConsumerNavHost.kt`](apps/consumer/android/app/src/main/kotlin/com/elifsi/evrry/consumer/ui/navigation/EvrryConsumerNavHost.kt): Added `AuthScreenState.Splash` and `AuthScreenState.Onboarding` branches to `when (authState)`.
  - iOS [`MainCoordinatorView.swift`](apps/consumer/ios/EvrryConsumer/Views/Navigation/MainCoordinatorView.swift): Added `.splash` and `.onboarding` cases to `switch authViewModel.state`.
  - Resolved Coil dependency by adding `implementation("io.coil-kt:coil-compose:2.6.0")` to `build.gradle.kts`.
  - Added `@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)` to `OnboardingScreen.kt` for `HorizontalPager`.
  - Verified compilation via `./gradlew compileDebugKotlin` in `apps/consumer/android`: **`BUILD SUCCESSFUL in 5m 36s`** with 0 errors.

---

## 14. WhatsApp-Grade Social Chat, WebRTC Calling, Stickers, Animated Emojis & Custom Gallery Wallpapers (Android, iOS & Phone Prototype)

- **Complete WhatsApp-Grade Social Messaging & Calling Subsystem**:
  - Implemented end-to-end messaging parity across Android (`apps/consumer/android`), iOS (`apps/consumer/ios`), and the mobile prototype (`prototype/Phone/`).
  - **Zero Paid SDKs & Open-Source Royalty-Free Assets**:
    - Avoided proprietary, paid SDKs (e.g. Giphy, Tenor commercial licensing) and external API key requirements.
    - **Google Noto Animated Emojis**: Official open-source animated GIF assets under **Apache 2.0 license** (`fonts.gstatic.com`).
    - **Twitter Twemoji Stickers**: Official open-source vector sticker assets under **CC-BY 4.0 license** (`raw.githubusercontent.com/twitter/twemoji`).
    - **Categorized Unicode 15.0 Emojis**: Curated cross-platform Unicode emojis organized across 5 distinct categories (Smileys & Emotion, People & Body, Food & Drink, Activities & Travel, Objects & Symbols).

- **Native Android Implementation (`apps/consumer/android`)**:
  - **Domain Models (`model/SocialChatModels.kt`)**:
    - Added `STICKER("STICKER")` and `ANIMATED_EMOJI("ANIMATED_EMOJI")` to `MessageContentType`.
    - Extended `SocialMessage` with `val stickerUrl: String? = null`.
    - Extended `SocialChat` with `val wallpaperUrl: String? = null`.
  - **State Management (`viewmodel/ChatViewModel.kt`)**:
    - Added `_wallpaperByChat` StateFlow and `setWallpaper(chatId, url)`.
    - Added `sendSticker(chatId, stickerUrl)` dispatching rich sticker messages with preview updates.
  - **Interactive 3-Tab Emoji & Sticker Picker (`ui/chat/EmojiStickerSheet.kt`)**:
    - Tab 1 (`Emojis`): 5 category chips with fast horizontal scroll and responsive Unicode grid.
    - Tab 2 (`Animated`): Google Noto Animated GIFs rendered via Coil `AsyncImage`.
    - Tab 3 (`Stickers`): Curated Twemoji vector stickers rendered via Coil `AsyncImage`.
  - **Chat Wallpaper Picker Sheet (`ui/chat/WallpaperPickerSheet.kt`)**:
    - 12 high-contrast color and gradient presets (WhatsApp Classic Doodle/Warm Emerald, Deep Navy, Midnight Violet, Sunset Amber, Rose Quartz, Slate Glass, etc.).
    - "My Photos" device gallery picker via native `ActivityResultContracts.GetContent()`.
    - Instant preview and per-chat wallpaper persistence.
  - **Chat Room Screen (`ui/chat/ChatRoomScreen.kt`)**:
    - Top bar wallpaper button (`Icons.Default.Palette`) with accessible touch target.
    - Input bar emoji/sticker sheet toggle (`Icons.Default.Mood`).
    - Dynamic wallpaper container supporting custom photo rendering via Coil with an **18–20% dark tint scrim** (`Color.Black.copy(alpha = 0.20f)`) to guarantee 100% text and bubble legibility.
    - **Borderless Floating Sticker Bubbles**: Stickers and animated emojis render as borderless transparent elements without rectangular speech bubble borders, featuring floating translucent timestamps.
  - **WebRTC Calling Screen (`ui/chat/CallScreen.kt`)**:
    - 1:1 Voice and Video Call screen with duration timer, camera toggle, mic mute, speakerphone switch, and red end-call CTA.

- **Native iOS Implementation (`apps/consumer/ios`)**:
  - **Domain Models (`Models/SocialChatModels.swift`)**:
    - Added `case sticker = "STICKER"` and `case animatedEmoji = "ANIMATED_EMOJI"` to `MessageContentType`.
    - Extended `SocialMessage` with `public let stickerUrl: String?`.
    - Extended `SocialChat` with `public var wallpaperUrl: String?`.
  - **State Management (`ViewModels/ChatViewModel.swift`)**:
    - Added `@Published var wallpaperByChat: [String: String]` and `setWallpaper(chatId:url:)`.
    - Added `sendSticker(chatId:stickerUrl:)` dispatching rich sticker messages.
  - **Interactive 3-Tab Emoji & Sticker Picker (`Views/Chat/EmojiStickerSheetView.swift`)**:
    - 3 tabs (Emojis, Animated, Stickers) with category chips, SwiftUI `AsyncImage`, and tap-to-send handlers.
  - **Chat Wallpaper Picker View (`Views/Chat/WallpaperPickerView.swift`)**:
    - 12 color/gradient preset cards with checkmark indicators.
    - "My Photos" gallery launcher powered by native `PhotosPicker` from `PhotosUI` with `Transferable` image data loading.
  - **Chat Room View (`Views/Chat/ChatRoomView.swift`)**:
    - Header wallpaper button (`photo.artframe`) and input bar emoji toggle (`face.smiling`).
    - Dynamic background wallpaper container with custom photo rendering and 20% legibility scrim (`Color.black.opacity(0.20)`).
    - Borderless transparent floating sticker message rendering with floating timestamps.
  - **WebRTC Calling View (`Views/Chat/CallView.swift`)**:
    - 1:1 Voice and Video Call interface with camera flipping, mute, speaker, and call duration tracking.

- **Mobile Golden Master Prototype (`prototype/Phone`)**:
  - **Zustand Store (`lib/store/useChatStore/types.ts` & `slices/messagingSlice.ts`)**:
    - Added `ChatMessageType = 'text' | 'image' | 'voice' | 'location' | 'contact' | 'document' | 'sticker'`.
    - Added `stickerUrl?: string`, `wallpaperByContact: Record<string, string>`, and `setWallpaper(contactId, wallpaper)`.
    - Updated `sendMessage` to seamlessly support sticker payload routing.
  - **Sticker & Animated Emoji Sheet (`components/chat/StickerSheet.tsx`)**:
    - 2-tab bottom sheet (Stickers & Animated Emojis) with responsive grid and image loading.
  - **Chat Wallpaper Picker (`components/chat/WallpaperPicker.tsx`)**:
    - 12 presets + "My Photos" file reader converting gallery images to persistent data URLs.
  - **Chat Room Page (`app/chat/[contactId]/page.tsx`)**:
    - Header wallpaper button (`Palette`), sticker picker trigger (`🎭`), dynamic wallpaper container with 18% dark scrim (`linear-gradient(rgba(0,0,0,0.18), rgba(0,0,0,0.18))`).
    - Borderless sticker rendering with floating timestamp pills.
  - **Automated Vitest Verification**:
    - Added integration tests in `lib/store/useChatStore/messaging.integration.test.ts` verifying sticker message routing and per-contact wallpaper persistence.
    - Verified full test suite: **15/15 test files passing**, **97/97 tests passing**.

- **Supabase Database Migrations**:
  - **Migration `20261010000001_call_logs.sql`**:
    - Created `public.call_logs` table tracking 1:1 voice & video call history (`call_id`, `caller_id`, `callee_id`, `call_type` VOICE/VIDEO, `status` initiated/ringing/connected/ended/missed/declined, `duration_seconds`, `started_at`, `ended_at`, `signaling_meta`).
    - PostGIS/UUID indexing and strict RLS policies allowing callers/callees to view and log calls.
  - **Migration `20261010000002_chat_wallpapers_and_stickers.sql`**:
    - Added `wallpaper_url TEXT` to `public.chats`.
    - Added `sticker_url TEXT` to `public.messages`.
    - Expanded `public.messages.message_type` check constraint to include `STICKER` and `ANIMATED_EMOJI`.
    - Added security definer RPC `public.set_chat_wallpaper(p_chat_id UUID, p_wallpaper_url TEXT)` enforcing chat participant membership.



---

## 15. Snapchat-Grade Camera, Real-Time QR Scanner, Lenses & Memories Subsystem (Android, iOS & Phone Prototype)

- **Comprehensive Mobile Camera & Memories Subsystem**:
  - Implemented 1:1 cross-platform camera and memory capture capabilities across Android (`apps/consumer/android`), iOS (`apps/consumer/ios`), and the mobile prototype (`prototype/Phone/`).
  - **Trademark & Legal Safety Compliance**:
    - Completely avoided all proprietary Snap Inc. trademarks ("Snapchat", "Snapcode", "Bitmoji", and "My Eyes Only").
    - Cleanly branded under Evrry design system tokens as **Evrry Camera / Lens**, **Memories**, **My Locker / Private Vault**, and **Stories**.
  - **Integrated Real-Time QR Scanner Reticle**:
    - Built directly into the camera viewfinder (mirroring modern iOS Camera and Snapchat Scan behavior).
    - Features animated center reticle framing brackets and intelligent live QR payload detection.
    - Decodes Nepal & global QR formats into actionable interactive glassmorphism cards:
      - **Fonepay & EMVCo Nepal QR** (`nepalqr://...`, `000201...`) $\rightarrow$ Extracts merchant name and NPR amount with **"Pay with Fonepay"** CTA navigating directly to checkout.
      - **Evrry Friend Contact QR** (`evrry://user/@handle`, `@handle`) $\rightarrow$ Decodes contact identity with **"Open Chat"** CTA navigating directly into WhatsApp-grade E2EE chat.
      - **Restaurant Dining Table QR** (`evrry://restaurant/roadhouse?table=4`) $\rightarrow$ Decodes table number with **"View Digital Menu"** CTA navigating to restaurant detail.
      - **Web URLs** (`https://...`) $\rightarrow$ Decodes website destination with **"Open Link"** CTA.
      - Built-in Demo QR simulation menu enabling instant test coverage across all QR types without requiring physical QR codes.

- **Memories Subsystem (3-Tab Segments)**:
  - **Snaps Tab**:
    - Saved camera captures with date stamps, Nepal location tags (*Patan Durbar Square*, *Nagarkot Sunrise Point*, *Thamel Night Market*), and active lens names.
    - Fullscreen photo detail modal with options to Share to Friend, Post to Story, Move to Locker, or Delete.
  - **Camera Roll Tab**:
    - Device photo library grid with instant import action and preview.
  - **My Locker Tab (Private Vault)**:
    - Dedicated secure vault isolated from regular photo roll.
    - **4-Digit Passcode Protection**: Interactive numeric keypad (0–9, backspace, clear) with 4-dot indicator circles.
    - Smooth shake animation and error toast on incorrect passcode entry (default out-of-the-box PIN: `1234`).
    - Instant Lock/Unlock session state toggle with "Lock Now" banner.
    - Unhide / Restore action allowing photos to be safely returned to Snaps.

- **WhatsApp-Grade Chat & Story Integration**:
  - Direct 1-tap sharing sheet in both `SnapReviewScreen` and `MemoriesScreen` allowing immediate transmission into any active conversation via `sendImage`.
  - Borderless photo snap rendering in chat messages.
  - 1-tap "Post to Story" button immediately updating the 24-hr status carousel.

- **Native Android Implementation (`apps/consumer/android`)**:
  - `model/SnapModels.kt`: `SnapItem`, `MemoryTab`, `QrScanResult`, `QrParser`, and `CameraFilterLens`.
  - `viewmodel/CameraViewModel.kt`: StateFlows for `snaps`, `cameraRoll`, `lockerSnaps`, `isLockerUnlocked`, `lockerPin`, `detectedQr`, `selectedLens`, and vault mutation methods.
  - `viewmodel/ChatViewModel.kt`: Added `sendImage(chatId, imageUrl, caption)` and `postStory(mediaUrl)`.
  - `ui/camera/CameraScreen.kt`: Fullscreen viewfinder, vertical right-side tool dock (Flip, Flash, Night, Grid, Lenses, QR), center QR reticle, floating QR card, hold-to-record circular shutter, lens carousel, and memories thumbnail shortcut.
  - `ui/camera/MemoriesScreen.kt`: 3-tab layout, 4-digit PIN keypad, detail viewer, friend sharing sheet, and lock/restore flows.
  - `ui/camera/SnapReviewScreen.kt`: Post-capture preview, text caption editing pill, direct locker button, story button, and WhatsApp friend picker.
  - `ui/navigation/EvrryConsumerNavHost.kt`: Wired sub-routes `"camera"`, `"memories"`, and `"snap_review"`.
  - `ui/chat/ConversationListScreen.kt`: Added camera icon button in top bar and linked status stories row to camera capture.

- **Native iOS Implementation (`apps/consumer/ios`)**:
  - `Models/SnapModels.swift`: Swift `SnapItem`, `MemoryTab`, `QrScanResult`, `CameraFilterLens`, and `QrParser`.
  - `ViewModels/CameraViewModel.swift`: `@Published` properties for snaps, roll, locker, PIN, QR state, and vault actions.
  - `ViewModels/ChatViewModel.swift`: Added `sendImage(chatId:imageUrl:caption:)` and `postStory(mediaUrl:)`.
  - `Views/Camera/CameraView.swift`: SwiftUI fullscreen viewfinder, right tool dock, QR reticle, `QrScanResult` glassmorphism card, circular shutter, lens badges, and memories shortcut.
  - `Views/Camera/MemoriesView.swift`: 3 tabs with SwiftUI `LazyVGrid`, 4-dot PIN keypad, detail modal, send to friend sheet, and locker actions.
  - `Views/Camera/SnapReviewView.swift`: Captured preview, caption overlay, direct locker button, and friend sharing sheet.
  - `Views/Navigation/MainCoordinatorView.swift`: Wired subroutes `"camera"`, `"memories"`, and `"snap_review"`.
  - `Views/Chat/ConversationListView.swift`: Added camera button in header and wired status stories row.

- **Golden Master Prototype (`prototype/Phone`)**:
  - `lib/types/snap.ts`: Extended with `location`, `caption`, `isLocked`, `QrActionType`, `QrScanResult`, `MemoryTab`.
  - `lib/store/useSnapStore.ts`: Extended with `cameraRoll`, `lockerSnaps`, `lockerPin`, `isLockerUnlocked`, and actions.
  - `lib/camera/qrScanner.ts`: `parseQrPayload`, `detectQrFromCanvas`, and `SAMPLE_QR_PRESETS`.
  - `components/snap/QrScanCard.tsx`: Floating QR banner with dynamic action button.
  - `components/snap/MemoriesScreen.tsx`: 3-tab Memories screen with 4-digit PIN keypad and photo detail modal.
  - `components/snap/SnapReviewScreen.tsx`: Review screen with caption overlay, story, locker, and WhatsApp send sheet.
  - `app/snap/page.tsx`: Fullscreen camera with tool dock, live QR scanning loop, shutter button, lens carousel, and memories thumbnail shortcut.
  - Verified Vitest suite: **17/17 test files passing, 109/109 tests passing**.

---

## 16. Unified Story & 24-hr Status Architecture, Fullscreen Story Viewer & Bi-Directional Social Synchronization (Android, iOS & Phone Prototype)

- **Problem Addressed & Architectural Unification**:
  - Addressed architectural gap where Camera/Memories "Story" and WhatsApp-grade Chat "Status" operated in potential isolation.
  - In Snapchat, ephemeral updates are called "My Story"; in WhatsApp, they are called "Status Updates". In the Evrry Super App platform, these **are now 100% unified as the exact same social feature**.
  - Single Source of Truth: Centralized `ChatViewModel` / `useChatStore` as the authoritative social status registry across all surfaces.

- **End-to-End Synchronization Workflows**:
  - **Capture to Story**: When a user captures a photo in the Camera (or reviews an existing photo in Memories) and taps "Post to Story", the photo immediately becomes their active 24-hr status in the Chat hub carousel with badge rings, timestamp ("Just now"), and media URL.
  - **Add Story from Chat**: Tapping the `+` badge or "My Status" (when no story exists) immediately opens the Evrry Camera in Story mode (`isStorySnap = true`), ensuring zero friction.
  - **Returning to Chat**: Completing a Story snap review seamlessly transitions the user directly back to the Chat list tab where their new status is live.
  - **Memories 1-Tap Story Sharing**: Any photo in Memories or Camera Roll can be posted directly to their 24-hr Story with one tap via the new "Story" button.

- **Interactive Fullscreen Story Viewer (Android Compose & iOS SwiftUI)**:
  - **Immersive Viewport**: Edge-to-edge dark container (`Color.Black`) with upper and lower gradient vignettes for contrast.
  - **5-Second Segmented Progress Bar**: Animated progress indicator (0% to 100% over 5.0 seconds). Auto-advances or dismisses on completion.
  - **Press-to-Pause**: Touching and holding down anywhere on the screen instantly pauses the story countdown; releasing resumes playback.
  - **Tap-to-Navigate**: Tapping the left 35% of the screen rewinds progress to the beginning; tapping the right 65% advances to next or dismisses.
  - **Header Bar**: Displays author initials avatar, author name, timestamp ("28 mins ago", "Just now"), Delete button (trash can for own story), and Close (`X`) button.
  - **Media & Fallback Canvas**: High-resolution image preview supporting URLs, data URIs, and artistic gradient canvas fallbacks.
  - **Caption Pill**: Semi-transparent frosted bubble rendering text captions.
  - **Author Mode**: Displays "👁️ 14 views" metric badge and "+ Add Status" shortcut button.
  - **Friend Mode**: Quick emoji reaction bar (❤️, 😂, 😮, 😢, 🙏, 👏) and direct text reply field. Tapping any reaction or sending text immediately transmits a direct message into the contact's chat thread.

- **Native Android Components (`apps/consumer/android`)**:
  - `model/SocialChatModels.kt`: Added `mediaUrl`, `caption`, and `createdAtMs` to `StoryStatusItem`.
  - `viewmodel/ChatViewModel.kt`: Added `postStory(mediaUrl, caption)`, `deleteMyStory()`, `markStorySeen(storyId)`, and `sendStoryReply(storyUserId, text)`. Added realistic sample media and captions for Aayush, Priyanka, and Bikash.
  - `ui/chat/StoryViewerDialog.kt`: Created Compose fullscreen dialog with `Animatable` progress, tap gestures, quick emoji reactions, and delete confirmation.
  - `ui/chat/ConversationListScreen.kt`: Wired `StoriesStatusRow` and `StoryViewerDialog`.
  - `ui/camera/SnapReviewScreen.kt`: Passed caption to `postStory(dataUrl, caption)`.
  - `ui/camera/MemoriesScreen.kt`: Added "Story" button to memory detail action bar.
  - `ui/navigation/EvrryConsumerNavHost.kt`: Preserved `isStorySnap` and routed back to chats tab upon story post completion.

- **Native iOS Components (`apps/consumer/ios`)**:
  - `Models/SocialChatModels.swift`: Added `mediaUrl`, `caption`, and `createdAtMs` to `StoryStatusItem`.
  - `ViewModels/ChatViewModel.swift`: Added `postStory(mediaUrl:caption:)`, `deleteMyStory()`, `markStorySeen(storyId:)`, and `sendStoryReply(storyUserId:messageText:)`.
  - `Views/Chat/StoryViewerView.swift`: Created SwiftUI fullscreen view with progress timer, drag gestures, author header, quick emoji reactions, and reply field.
  - `Views/Chat/ConversationListView.swift`: Wired `.fullScreenCover(item: $viewingStory)` and `StatusStoriesView`.
  - `Views/Camera/SnapReviewView.swift`: Passed caption to `chatViewModel.postStory(mediaUrl:caption:)`.
  - `Views/Camera/MemoriesView.swift`: Passed caption to `chatViewModel.postStory`.
  - `Views/Navigation/MainCoordinatorView.swift`: Preserved `isStorySnap` and routed back to chats tab upon story post completion.

- **Mobile Golden Master Prototype (`prototype/Phone`)**:
  - Verified `setMyStory(dataUrl)` and `clearMyStory()` in `useChatStore`.
  - Verified `SnapReviewScreen.tsx` calling `setMyStory`.
  - Verified `MemoriesScreen.tsx` calling `handlePostStory`.
  - Verified full test suite: **17/17 test files passed, 109/109 tests passed**.

---

### Session 14: Unified Native Partner App — Mobility Ride Driver Implementation
- **Architecture & Invariant Compliance**:
  - `prototype/Partner/` remained strictly deleted and untouched.
  - Built unified native 4-role Partner applications: `apps/partner/android/` (Kotlin + Jetpack Compose) and `apps/partner/ios/` (Swift + SwiftUI).
  - Grounded strictly in `reference/` (Luxeride, Uber-Clone, ridesharing-uber-lyft-app, GetStream WhatsApp Clone) and Supabase migrations (`rides`, `ride_bids`, `driver_locations`).

- **Shared Partner Onboarding & 4-Role Architecture**:
  - **Shared Phone + OTP Auth**: `PartnerAuthViewModel` (Android & iOS) verifying Nepal mobile phone numbers and SMS OTP via Supabase Edge Function `send-sms`.
  - **Visual Role Selection Screen**: 4 distinct venture cards (`Ride Driver`, `Restaurant Kitchen`, `Grocery Supermarket`, `Delivery Courier`) with gradient accents, badge indicators, and role descriptions.
  - **DoTM KYC Submission**: When `Ride Driver` is selected, collects `DRIVING_LICENSE`, `VEHICLE_BLUEBOOK`, and `VEHICLE_PHOTO` documents and sets `partner_profiles.type = 'driver'`.
  - **Top-Bar 1-Tap Role Switcher**: `PartnerTopBar` (Android) and `PartnerTopBarView` (iOS) allowing multi-venture operators to switch active roles with zero logout/re-login overhead.

- **Mobility Ride Driver HUD & Lifecycle**:
  - **Online/Offline Switch & Telemetry**: Toggle starting foreground GPS service broadcasting lat, lng, and heading to `public.driver_locations` every 3.5s (`PartnerLocationBroadcaster`).
  - **InDrive Live Negotiation & Bidding HUD**: Incoming passenger ride card displaying pickup address, dropoff address, OSRM route distance (km), offered fare (NPR), and passenger rating.
  - **Quick Counter-Offer Controls**: Stepper buttons (`[-10]`, `[+10]`) and instant chips (`+20`, `+50`, `+100`) posting into `public.ride_bids`.
  - **Turn-by-Turn Navigation Canvas**: Live route polyline rendering with animated bearing smoothing, radar pulse beacon, dynamic speed and ETA calculations.
  - **Arrival & Start OTP Verification**: "Arrived at Pickup" status transition and 4-digit PIN verification against `rides.start_otp` (transitions to `in_progress`).
  - **Trip Completion & Cash Collection**: "Complete Trip" status transition generating full fare breakdown (base fare, distance rate, waiting charges) and COD cash ledger settlement.
  - **In-Trip Chat & Dual Calling**:
    - Realtime messaging screen (`PartnerChatRoomScreen` / `PartnerChatRoomView`) with canned quick reply chips.
    - Dual call chooser sheet offering 🌐 "Call in App" (WebRTC / LiveKit In-App VoIP) and 📱 "Direct SIM Call" (`Intent.ACTION_DIAL` / `tel://`).

- **Verification & Build Status**:
  - Android Kotlin Compilation: `./gradlew compileDebugKotlin` in `apps/partner/android/` passed with **BUILD SUCCESSFUL, 0 errors**.

---

### Session 15 & 16: Unified Native Partner App — Grocery Supermarket Partner Mode (Phase 3)
- **Architecture & Rules Compliance**:
  - `prototype/Partner/` strictly remained deleted and was never recreated or referenced.
  - **Supermarket Mode ONLY**: Dark Store micro-fulfillment explicitly deferred; focused 100% on retail supermarket and mart operations (Bhatbhateni Pulchowk sample mart).
  - Built across Android (`apps/partner/android/` in Kotlin + Jetpack Compose) and iOS (`apps/partner/ios/` in Swift + SwiftUI) with unified shared auth and 1-tap top-bar role switching.
  - Aligned with `reference/Aakash901_BlinkitClone/` and Consumer tracking screens (`OrderDetailsScreen.kt` / `OrderDetailsView.swift`).

- **Database & Backend Migrations**:
  - Created `supabase/migrations/20261012000001_grocery_supermarket_extensions.sql`:
    - Extended `public.catalog_items` with `barcode` (VARCHAR 64), `sku` (VARCHAR 64), `stock_quantity` (INT), and `unit` (VARCHAR 24) plus btree indexes.
    - Extended `public.orders` with `substitution_proposals` (JSONB) storing customer replacement requests.
    - Extended `public.order_items` with `measured_weight` (NUMERIC 8,3), `is_packed` (BOOLEAN), `substitution_status` (TEXT), and `substituted_item_id` (UUID).
    - Implemented stored procedures:
      - `public.propose_grocery_substitution`: Dispatches replacement item proposal to consumer.
      - `public.resolve_grocery_substitution`: Records consumer approval/rejection and updates cart pricing.
      - `public.pack_order_item`: Toggles packed state and records measured weight for loose produce.
      - `public.complete_order_packing`: Verifies all items are packed or resolved and transitions order to `'ready_for_pickup'`.

- **Android Partner App (`apps/partner/android`)**:
  - `model/GroceryPartnerModels.kt`: Data models for `GroceryPartnerItem`, `GroceryOrderItem`, `GroceryPartnerOrder`, `SubstitutionStatus`, and `PartnerChatMessage`.
  - `data/GroceryCatalogSampleData.kt`: Bhatbhateni Pulchowk catalog with real EAN-13 barcodes, shelf SKUs, and incoming basket.
  - `viewmodel/GrocerySupermarketViewModel.kt`: Pick-and-pack fulfillment state, loose weight recording, substitution suggestions, camera barcode lookup, and courier chat.
  - `ui/grocery/GrocerySupermarketScreen.kt`: 3-tab container coordinating Pick & Pack, Inventory, and Barcode Audit.
  - `ui/grocery/GroceryPickPackScreen.kt`: Department/aisle grouped checklist, loose produce weighing dialog, and "All Items Packed" trigger.
  - `ui/grocery/GroceryBarcodeScannerScreen.kt`: CameraX + ML Kit Barcode Scanning with explicit `DisposableEffect` camera lifecycle release.
  - `ui/grocery/GrocerySubstitutionDialog.kt`: Out-of-stock substitute suggestion picker and real-time consumer notification dispatch.
  - `ui/grocery/GroceryInventoryScreen.kt`: Aisle filter chips, search, rapid +/- stock counters, and 86'ing toggle.
  - `ui/grocery/CourierHandoffDialog.kt`: Courier profile, ETA, vehicle plate, bay directions, and live in-app messaging.
  - `ui/onboarding/PartnerRoleSelectionScreen.kt` & `PartnerOnboardingViewModel.kt`: Retail KYC onboarding sheet for Business Registration Number (BRN) and PAN/VAT certificate upload.
  - `ui/navigation/PartnerNavHost.kt` & `PartnerTopBar.kt`: Routed `PartnerRole.GROCERY` to supermarket view with adaptive title and sub-bar.

- **iOS Partner App (`apps/partner/ios`)**:
  - `Models/GroceryPartnerModels.swift`, `Models/PartnerRoleModels.swift`: Swift models conforming to `Identifiable` and `Codable`.
  - `Data/GroceryCatalogData.swift`: Kathmandu supermarket catalog with EAN-13 codes, departments, and active order queue.
  - `ViewModels/GrocerySupermarketViewModel.swift`: ObservableObject managing pick-pack verification, loose produce scales, substitution dispatch, and stock counters.
  - `Views/Grocery/GrocerySupermarketHomeView.swift`: 3-tab coordinator with `PartnerTopBar`.
  - `Views/Grocery/GroceryPickPackView.swift`: Aisle checklist, loose weight scale modal, and "All Items Packed" trigger.
  - `Views/Grocery/GroceryBarcodeScannerView.swift`: `AVCaptureSession` + `AVCaptureMetadataOutput` barcode scanner with hardware release on `dismantleUIView` / `onDisappear`.
  - `Views/Grocery/GrocerySubstitutionSheet.swift`: Replacement item selector and customer notification flow.
  - `Views/Grocery/GroceryInventoryView.swift`: Aisle chips, search, +/- stock counters, and 86'ing toggle.
  - `Views/Grocery/CourierHandoffSheet.swift`: Courier ETA, bay pickup instructions, and live in-app chat.
  - `Views/Onboarding/PartnerRoleSelectionView.swift` & `ViewModels/PartnerRoleViewModel.swift`: Retail KYC document upload flow.
  - `EvrryPartnerApp.swift`: Integrated `GrocerySupermarketHomeView` into role coordinator with 1-tap switching.

- **Verification & Consumer Parity**:
  - Android Kotlin compilation: `./gradlew compileDebugKotlin` in `apps/partner/android/` passed with **BUILD SUCCESSFUL, 0 errors**.
  - State machine verified against Consumer App (`OrderDetailsScreen.kt` & `OrderDetailsView.swift`): Completing pack phase transitions status to `'ready_for_pickup'` (Stage 3).
  - Clean camera hardware release verified on both Android (CameraX `unbindAll`) and iOS (AVFoundation `stopRunning`).


