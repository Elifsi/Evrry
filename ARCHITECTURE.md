# evrry — System Architecture

This document describes the comprehensive system architecture of the **evrry** Super App platform, developed by **Elifsi Technologies Private Limited**, spanning client applications, backend services, payments, payouts, and the reference prototype.

---

## 1. High-Level Architecture Topology

```
                         EVRRY SUPER APP PLATFORM
                   (Elifsi Technologies Private Limited)
                                     │
      ┌──────────────────────────────┼──────────────────────────────┐
      │                              │                              │
      ▼                              ▼                              ▼
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│  CONSUMER APPS   │       │   PARTNER APPS   │       │ PROTOTYPE (REF)  │
│ Android (Kotlin) │       │ Android (Kotlin) │       │  Next.js + React │
│ iOS (Swift)      │       │ iOS (Swift)      │       │  Tailwind + TS   │
│ Web (Next.js)    │       │ Web (Next.js)    │       │ (prototype/Phone/) │
└─────────┬────────┘       └─────────┬────────┘       └──────────────────┘
          │                          │                  (Browser-only UX
          │                          │                  & Flow Reference)
          └─────────────┬────────────┘
                        │
                        ▼
         ┌──────────────────────────────┐
         │  CENTRALIZED SUPABASE BACKEND │
         │         (supabase/)          │
         │                              │
         │  ├── Supabase Auth & RBAC    │
         │  ├── PostgreSQL Database     │
         │  │    with Row Level Security│
         │  ├── Supabase Realtime       │
         │  ├── Supabase Storage        │
         │  └── Supabase Edge Functions │
         └──────────────┬───────────────┘
                        │
       ┌────────────────┴────────────────┐
       ▼                                 ▼
┌────────────────────────┐     ┌────────────────────────┐
│   PAYMENT SUBSYSTEM    │     │    PAYOUT SUBSYSTEM    │
│  eSewa / Khalti /      │     │  Settlement Engine /   │
│  Fonepay / Card        │     │  Bank IPS Transfer /   │
│  (Inward Collection)   │     │  Ledger Accounting     │
└────────────────────────┘     └────────────────────────┘
```

---

## 2. Core Application Segments

### A. Mobile Phone Prototype — `prototype/Phone/`
- **Status**: Current, working interactive reference.
- **Technology**: Next.js 16 (App Router), React 18, TypeScript, Tailwind CSS, Zustand, Vitest (97 tests passing), Playwright.
- **Purpose**: Implements the multi-vertical mock catalog, conversational voice AI concierge, E2EE chat, WebRTC calling, and order simulations. Form-factor is specifically designed and styled for **mobile smartphone viewports**, acting as the **Smartphone UI & UX Golden Master** for native iOS and Android apps. It is **NOT** a desktop web app or the consumer web portal.

### B. Consumer Applications — `apps/consumer/`
- **Android**: `apps/consumer/android/` (Kotlin 2.0 + Jetpack Compose + Material 3, in-app `<` back navigation) — ✅ **Fully Implemented**
- **iOS**: `apps/consumer/ios/` (Swift 5.10 / 6 + SwiftUI + Clean MVVM, in-app `<` back navigation) — ✅ **Fully Implemented**
- **Web**: `apps/web/consumer/` (Next.js + React + MapLibre GL / OpenStreetMap for zero-cost web mapping) — 🔮 Planned
- **Implementation Highlights**:
  - 1:1 domain models (`AuthState`, `VoiceModel`, `CatalogItem`, `FoodModels`, `CartState`, `RideBooking`), ViewModels, and services.
  - Dedicated Nepal phone auth with WhatsApp template OTP delivery, Sparrow SMS fallback, 45s resend cooldown, and 6-digit auto-verify PIN entry.
  - AI Voice Concierge featuring ChatGPT-style minimal switcher `[ Ellie Mini ▾ ]` (Ellie Mini & Ellie Pro), voice waveform orb, zero manual language pills, and automatic multilingual mirroring (Nepali, Hindi, Maithili, English).
  - Full Food Delivery Vertical architecture ported directly from reference repositories [`reference/kaaneneskpc_Deliverr`](reference/kaaneneskpc_Deliverr) and [`reference/enatega_food-delivery-multivendor`](reference/enatega_food-delivery-multivendor): Dedicated Food Home (`FoodHomeScreen` / `FoodHomeView`), Cuisines carousel, quick filters, restaurant cards, Restaurant Menu Detail (`RestaurantDetailScreen` / `RestaurantDetailView`), Enatega multi-restaurant cart replacement alert, `FoodItemCustomizationSheet` (radio variations, checkbox add-ons, notes, steppers), delivery address picker (`DefaultAddresses`), `OrderSuccessScreen` / `OrderSuccessView`, and `OrderDetailsScreen` / `OrderDetailsView` (5-stage live status stepper, route & dispatch map, driver HUD with Call action, itemized receipt breakdown).
  - Mandatory in-app back buttons (`<`) on every sub-screen in `EvrryTopBar` ($\ge 44\times 44\text{ pt/dp}$) to guarantee seamless navigation on iOS devices which lack a hardware back button.
  - Complete elimination of stored in-app wallet (`walletBalanceNpr` purged across all models) in compliance with Nepal Rastra Bank (NRB) regulations, utilizing direct domestic payments (eSewa, Khalti, Fonepay QR, COD).

### C. Partner & Operations Applications — `apps/partner/` & `apps/web/`
- **Android**: `apps/partner/android/` (Kotlin + Jetpack Compose for KDS, POS, and Rider mode)
- **iOS**: `apps/partner/ios/` (Swift + SwiftUI for iPad counter and merchant management)
- **Partner Web Dashboard**: `apps/web/partner/` (Next.js + React for desktop business operations, KDS, inventory)
- **Superadmin Web Console**: `apps/web/admin/` (Next.js for Elifsi staff: KYC queue, driver approval, midnight payouts)
- **Shared Web Client SDK**: `apps/web/common/` (Shared TypeScript bridges for Auth, Routing, Payments, Payouts, AI)
- **Purpose**: Dedicated to merchant onboarding, catalog & inventory management, order fulfillment, kitchen printing, earnings tracking, and administrative governance.

### D. Centralized Shared Backend — `supabase/`
- **Status**: ✅ **100% Implemented** (23 SQL Migrations, 12 Edge Functions, PostGIS, Storage, pg_cron).
- Centralized shared backend providing PostgreSQL, Authentication, Row Level Security, Realtime channels, and Edge Functions for all production clients.

---

## 3. Payment Processing Architecture

The platform abstracts payment processing behind a **Payment Service** with provider adapters:

```
Consumer Checkout (Android / iOS / Web)
      ↓
Payment Service (Edge Function: payment-initiate)
      ↓ Server-calculated immutable total
Payment Provider Adapter
      ├── eSewa Adapter
      ├── Khalti Adapter (Wallet + facilitated Card/Bank payments)
      ├── Fonepay Adapter (Interbank QR rails)
      └── Card Gateway Adapter (Optional / dedicated processor if required)
      ↓
Gateway Hosted Session / QR / Deep-Link
      ↓
Payment Service (Edge Function: payment-verify)
      ↓ Cryptographic HMAC signature check
PostgreSQL Payments, Transactions & Orders Update
```

### Key Architectural Directives:
- **Card Handling Nuance**: Cards are not assumed to require an entirely separate gateway. Gateways like Khalti or Fonepay may facilitate debit/credit card processing. The architecture avoids duplicating card handling where active providers already cover it, avoids assuming they cover every card use case, and remains extensible so a dedicated Card Gateway Adapter can be introduced when a specific card processor is chosen.
- **Zero Raw Card Storage**: Raw card numbers (PAN), CVV, and expiry dates are **never** received or stored in the evrry database. The platform relies on provider-hosted or tokenized flows.
- **No Client Secrets**: Client applications never possess provider secret keys or merchant API secrets.
- **Server Financial Authority**: The server alone computes all payable amounts, discounts, taxes, and fees.
- **Separation of Concepts**: Inward payments, refunds, transaction ledgers, settlements, and outward partner payouts are isolated backend entities.

---

## 4. Partner Payout & Financial Architecture

Payments (customer collections) and Payouts (merchant disbursements) are distinct:

```
Consumer Payment (Collected into platform account)
      ↓
Payment System
      ↓
Immutable Transaction Ledger (Double-entry recording)
      ↓
Partner Earnings (Order subtotal - Platform fee = Net Payable)
      ↓
Settlement Engine (Scheduled cycle aggregation & adjustments)
      ↓
Partner Payout (Disbursement batch)
      ↓
Payout Provider / Bank Adapter (Direct bank transfer / IPS)
```

### Financial Data Model:
- **`payments`**: Records customer payment attempts and gateway verification tokens.
- **`orders`**: Business agreement between customer and partner.
- **`transactions`**: Immutable ledger of debits and credits across platform, consumer, and partner accounts.
- **`partner_earnings`**: Order-level earnings calculation.
- **`settlements`**: Grouping of partner earnings across an accounting period (e.g. weekly).
- **`payouts`**: Actual disbursement record linked to partner bank account.
- **`refunds`**: Reversal transactions linked to original payment and deducted from settlement.

---

## 5. Authorization & Multi-Tenant Security Model

The platform defines distinct user roles enforced via Supabase Auth and PostgreSQL Row Level Security (RLS):

| Role | Scope | Data Access Privileges |
|---|---|---|
| **Consumer** | Customer | Can read/write only their own profile, cart, orders, and chats. |
| **Partner Owner** | Business | Full access to own store profile, catalog, orders, staff, and bank payout settings. |
| **Partner Manager** | Business | Access to catalog, pricing, hours, live orders, and operational analytics. |
| **Partner Staff** | Business | View and advance active incoming orders (no access to financial payouts). |
| **Partner Driver** | Mobility | Access to assigned active delivery/ride orders and customer navigation. |
| **Platform Admin** | Platform | Oversees dispute resolution, KYC approvals, and settlement release (Edge Functions only). |

### Strict Multi-Tenant Isolation:
- Under no circumstances can Partner A inspect Partner B's catalog, sales, customer details, or bank records.
- Cross-tenant leakage is prevented at the database query level via RLS policies using `partner_id` constraints:
  ```sql
  CREATE POLICY "partner_access" ON public.orders
  FOR ALL TO authenticated
  USING (
    partner_id IN (
      SELECT partner_id FROM public.partner_members
      WHERE user_id = auth.uid()
    )
  );
  ```

---

## 6. Credential Safety Rules

> ⛔ **ABSOLUTE RULE: ZERO SERVICE-ROLE CREDENTIALS IN CLIENT APPLICATIONS**
> 
> The Supabase `service_role` key grants unrestricted administrative bypass of all Row Level Security policies.
> It must **NEVER** be placed in:
> - Android build files or assets
> - iOS plists or Swift code
> - Web client bundles or public environment variables
> - Any file committed to Git
>
> All client applications run exclusively with the public `anon` key. Privileged actions occur only in Supabase Edge Functions.

---

## 7. Prototype Internal Architecture Notes (Reference)

For developers referencing the existing prototype at `prototype/Phone/`:
- **Zustand Slices**: `useAppStore` and `useChatStore` use slice composability to prevent monolith store files while maintaining unified cross-domain state.
- **Barrel Exports**: `lib/types`, `lib/data/catalog`, `lib/store/useAppStore`, `lib/store/useChatStore` export through `index.ts`.
- **Testing**:
  - Unit tests: `*.test.ts` (Vitest)
  - Integration tests: `*.integration.test.ts` (Vitest)
  - E2E tests: `e2e/*.spec.ts` (Playwright)
