<!-- BEGIN:nextjs-agent-rules -->

# This is NOT the Next.js you know

This version has breaking changes — APIs, conventions, and file structure may all differ from your training data. Read the relevant guide in `node_modules/next/dist/docs/` (resolved from this file's directory; in monorepos the `next` package may not be visible from the repo root) before writing any code. Heed deprecation notices.

This block is written and re-added by `next dev` — verify at `node_modules/next/dist/server/lib/generate-agent-files.js`. Removing it from a diff only re-creates the uncommitted change; committing it with your work keeps the tree clean.

<!-- END:nextjs-agent-rules -->

> **Monorepo note**: This repository contains the **Evrry** Super App platform, developed by **Elifsi Technologies Private Limited**:
>
> | Path | Purpose | Status |
> |------|---------|--------|
> | `prototype/Phone/` | Mobile Phone Experience Prototype (Next.js — reference only, NOT the active dev target) | ✅ Reference |
> | `apps/consumer/android/` | **🎯 Consumer Android App** (Kotlin · Jetpack Compose · Hilt · Google Maps SDK) | ✅ Fully Wired & Compiling |
> | `apps/consumer/ios/` | **🎯 Consumer iOS App** (Swift · SwiftUI · CoreLocation · Google Maps SDK) | ✅ Fully Wired |
> | `apps/partner/android/` | **🎯 Partner Android App** (Kotlin · Jetpack Compose · Unified Adaptive 4-Role App) | 🚀 4-Phase Dev Blueprint |
> | `apps/partner/ios/` | **🎯 Partner iOS App** (Swift · SwiftUI · Unified Adaptive 4-Role App) | 🚀 4-Phase Dev Blueprint |
> | `apps/web/` | Production web applications (`consumer/`, `partner/`, `admin/`, shared `common/`; MapLibre GL maps) | 🔮 Planned |
> | `supabase/` | Centralized shared backend (22 Migrations, 11 Edge Functions, PostGIS, Storage, pg_cron) | ✅ 100% Implemented |
>
> **⚠️ 4-PARTNER UNIFIED ADAPTIVE APP ARCHITECTURE:**
> The Partner app (`apps/partner/android/` & `apps/partner/ios/`) is **ONE unified adaptive application** featuring shared Phone+OTP auth, a visual Role Selection screen on onboarding, and a top-bar 1-tap Role Switcher for multi-venture Nepali operators:
> 1. **Phase 1: Mobility Ride Driver** (`partner_type: driver`) — Passenger transit (Bike, Taxi, Cab, Comfort EV), InDrive bidding negotiation, OSRM turn-by-turn routing, passenger start OTP (4 digits), In-Trip Chat & Dual Calling (In-App VoIP + Direct SIM), live GPS broadcasting to `driver_locations`.
> 2. **Phase 2: Restaurant Kitchen Partner** (`partner_type: restaurant`) — Kitchen Display System (KDS), high-decibel audio incoming order chimes, prep timers (15m, 25m, 40m), instant 86'ing out-of-stock items, ESC/POS thermal printing, courier/consumer chat.
> 3. **Phase 3: Grocery Supermarket Partner** (`partner_type: grocery`) — **Supermarket Mode ONLY** (Dark Store deferred). Order pick-and-pack checklist, camera barcode scanner, real-time stock adjustments, out-of-stock customer substitution alert flow.
> 4. **Phase 4: Food & Grocery Delivery Courier** (`partner_type: rider`) — Order claiming from restaurants and supermarkets, 2-stage navigation (Pickup → Drop-off), customer handoff OTP (`order_handoff_codes`), COD cash collection ledger, multi-party chat.
>
> **⚠️ PROTOTYPE RULE:** `prototype/Partner/` has been deleted and must NOT be recreated or referenced. All partner implementation strictly targets `apps/partner/android/` and `apps/partner/ios/`.
>
> **Official Reference Repositories (`reference/`):**
> - Mobility Driver: `reference/amitshekhariitbhu_ridesharing-uber-lyft-app`, `reference/anandwana001_uber-clone`, `reference/vikasrana07_luxeride`.
> - Restaurant KDS: `reference/enatega_food-delivery-multivendor/enatega-multivendor-store`.
> - Grocery Supermarket: `reference/Aakash901_BlinkitClone`.
> - Delivery Courier: `reference/kaaneneskpc_Deliverr`, `reference/enatega_food-delivery-multivendor/enatega-multivendor-rider`.
> - Chat & Calling: `reference/GetStream_whatsApp-clone-compose`, `reference/Gmarvis_whatsapp-clone`.
>
> **Key tech decisions:**
> - **Maps:** Google Maps SDK (Android: Maps Compose; iOS: GMSMapView via UIViewRepresentable)
> - **Routing/Distance:** OSRM via Supabase Edge Function `routing` — NOT Google Directions API
> - **Location:** FusedLocationProviderClient Foreground Service (Android) / CoreLocation CLLocationManager (iOS)
> - **Backend:** Supabase (PostgREST, Realtime channels, Storage for KYC, RPCs `request_ride`, `submit_review`, etc.)
> - **Architecture:** Clean MVVM + StateFlow/Combine + Hilt DI (Android) / ObservableObject (iOS)
>
> **Running the Mobile Phone Prototype** (reference only):
> Run `next dev` (and all npm commands) from `prototype/Phone/`, not the repository root.
> The `node_modules/next/` package is installed at `prototype/Phone/node_modules/next/`.

