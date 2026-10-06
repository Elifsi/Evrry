# Platform Roadmap

This document outlines the phased development roadmap for **evrry**, developed by **Elifsi Technologies Private Limited**.

---

## Phase 0: Mobile Phone Prototype & UX Validation (Complete)
- [x] End-to-end interactive Next.js mobile phone prototype (`prototype/Phone/`)
- [x] Multi-category mock catalog (food, grocery, retail, hotels, rides, services)
- [x] Voice-first AI concierge loop with tool use & model fallback
- [x] End-to-end encrypted direct messaging and WebRTC voice/video calling
- [x] Simulated ride booking and driver auto-advance tracking
- [x] Camera filters (Snap) and custom filter creator
- [x] Monorepo restructuring with complete architectural separation
- [x] 97 unit and integration tests passing across 15 test suites

---

## Phase 1: Shared Backend & Database Foundations (Complete — 100% Implemented)
- [x] Centralized Supabase backend architecture & local Docker configuration
- [x] 20 SQL migrations with strict Row Level Security (RLS) on every table:
  - Extensions (PostGIS, pgcrypto, btree_gist) & Enums (`20261005000001`)
  - User profiles, roles, and address book (`20261005000002`)
  - Partner profiles, venues, and staff RBAC (`20261005000003`)
  - Service verticals, stores, and operating hours (`20261005000004`)
  - Unified catalog items, variants, and modifiers (`20261005000005`)
  - Orders state machine & order items (`20261005000006`)
  - Mobility rides, bids, vehicles, and real-time locations (`20261005000007`)
  - Hotel rooms, inventory, and non-overlapping booking blocks (`20261005000008`)
  - Payments, inward gateway collections, and webhooks (`20261005000009`)
  - Double-entry ledger transactions & partner earnings (`20261005000010`)
  - Midnight settlements & partner payout batches (`20261005000011`)
  - Reviews, ratings, and moderation flags (`20261005000012`)
  - Promotions, vouchers, and referral balances (`20261005000013`)
  - Encrypted chat threads, messages, and presence (`20261005000014`)
  - WebRTC signaling sessions and call metadata (`20261005000015`)
  - Social feed, stories, lenses, and filters (`20261005000016`)
  - AI concierge conversations, tools, and message memory (`20261005000017`)
  - Push notification device tokens, dispatch queue, and audit logs (`20261005000018`)
  - Security audit logs and superadmin KYC operations (`20261005000019`)
  - pg_cron automated schedules & midnight maintenance jobs (`20261005000020`)
- [x] 11 server-side Supabase Edge Functions:
  - `ai-gateway`, `send-otp`, `send-sms`, `whatsapp-webhook`, `routing` (OSRM), `payment-initiate`, `payment-verify`, `payout-execute`, `push-notify`, `send-email`, `process-outbox`
- [x] Python AI Voice Gateway microservice (`services/ai-voice-gateway/`)
- [x] 1-Click OCI self-hosting deployment suite (`deploy/oci/`) and health checker (`scripts/verify-backend-readiness.sh`)


---

## Phase 2: Production Web Applications & Superadmin Console (Current Focus)
- [ ] Scaffold `apps/web/admin/` (Superadmin Operations Console):
  - Dashboard overview & live metrics
  - Partner KYC document verification queue (Driving licenses, Blue Books, PAN/VAT certificates)
  - Platform fee (Rs 10), delivery base rate (Rs 50), and radius pricing tier controls
  - Midnight payout batch approval & manual trigger
- [ ] Implement `apps/web/common/` shared SDK bridges (Auth, OSRM Routing, Payments, Payouts, AI)
- [ ] Scaffold `apps/web/consumer/` (Responsive Consumer Web Application):
  - Catalog browsing across Food, Grocery, Stays, and Services
  - Web AI concierge chat interface
  - Zero-cost MapLibre GL / OpenStreetMap map viewing
  - Live order tracking banner (*"For real-time 3D vehicle tracking, open in Evrry Phone App"*)
- [ ] Scaffold `apps/web/partner/` (Merchant Operations Web Portal):
  - Kitchen Display System (KDS) & audio alerts
  - Bulk grocery stock management & barcode scanning
  - Hotel room booking calendar with PostgreSQL `btree_gist` exclusion constraints

---

## Phase 3: Native Consumer Mobile Applications (Android & iOS)
- [ ] Scaffold `apps/consumer/android/` (Kotlin + Jetpack Compose):
  - Native Google Maps SDK integration (100% free unlimited loads)
  - 3D isometric vehicle markers (Scooter, Motorcycle, Cab/Car) with 360° heading rotation along OSRM route
  - Supabase Auth (SMS & WhatsApp OTP) & Realtime channels
- [ ] Scaffold `apps/consumer/ios/` (Swift + SwiftUI):
  - Mandatory 44pt top-left in-app back navigation buttons and modal dismissal controls
  - Native Google Maps SDK / MapKit with 3D isometric vehicle tracking
  - Apple Human Interface Guidelines layout parity with Android
- [ ] On-device speech recognition & low-latency AI Concierge voice loop

---

## Phase 4: Native Partner Mobile Applications & Production Rollout
- [ ] Scaffold `apps/partner/android/` (KDS, POS thermal receipt printing, Rider delivery mode)
- [ ] Cross-client push notifications (FCM & APNs bridges)
- [ ] Merchant cash-in-hand reconciliation and automated connectIPS midnight payouts

