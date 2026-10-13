# apps/partner/ — Partner Applications

The **Partner Application** is designed for businesses, service providers, and gig operators participating in the **evrry** Super App platform, developed by **Elifsi Technologies Private Limited**.

> **Important**: Partner applications are distinct products from consumer applications. They are designed around specific business roles, workflows, and operational needs rather than customer shopping flows.

---

## Role-Based & Domain-Adaptive Architecture

Because the Consumer app offers multiple verticals, the Partner application is built as **ONE Unified Adaptive App** (`apps/partner/android/` and `apps/partner/ios/`) featuring a visual Role Selection screen on signup and an instant 1-tap in-app Role Switcher in the top bar:

1. **Mobility Ride Driver Role** (`partner_type: driver`):
   - Passenger transit (Bike, Taxi, Cab, Comfort EV).
   - InDrive-style real-time fare bidding & counter-offers.
   - Interactive turn-by-turn map navigation (OSRM polyline routing & bearing angle calculation).
   - Passenger 4-digit start OTP verification (`start_otp`).
   - Contextual In-Trip Passenger Chat and Dual Calling (In-App VoIP + Direct SIM dialer).
   - Live GPS broadcasting to Supabase Realtime `driver_locations`.

2. **Restaurant / Kitchen Role** (`partner_type: restaurant`):
   - Kitchen Display System (KDS) order queue (`acknowledged` → `preparing` → `ready_for_pickup`).
   - High-decibel audio incoming order chimes.
   - Preparation timers and quick prep adjustments (15m, 25m, 40m).
   - Instant menu item 86'ing (toggling items out-of-stock in real-time).
   - Bluetooth / LAN ESC/POS thermal ticket printing (Kitchen Order Ticket / KOT & Customer Receipt).
   - In-app chat with Assigned Delivery Courier and Consumer.

3. **Grocery / Supermarket Role** (`partner_type: grocery`):
   - **Supermarket Mode ONLY** (Dark Store micro-fulfillment is explicitly deferred for future releases).
   - Order pick-and-pack fulfillment checklist (item-by-item checkbox & weight verification).
   - Barcode scanner / SKU lookup for price checks and immediate stock depletion.
   - Real-time stock counts and customer substitution alert dispatch (proposing replacement items if out-of-stock).
   - In-app chat with Delivery Courier and Consumer.

4. **Food & Grocery Delivery Courier Role** (`partner_type: rider`):
   - Claims ready orders from Restaurants and Supermarkets (`orders.rider_partner_id`).
   - Two-stage multi-stop turn-by-turn navigation (Step 1: Store Pickup → Step 2: Customer Delivery).
   - Secure customer handoff 4-digit delivery OTP verification (`order_handoff_codes`).
   - Cash-on-Delivery (COD) cash collection ledger and midnight wallet reconciliation.
   - Multi-party chat (Courier ↔ Restaurant/Supermarket and Courier ↔ Consumer).

### Staff Permission Tiers (RBAC)
Within each business partner, access is partitioned:
* `owner`: Full banking, payout disbursement, tax, and team control.
* `manager`: Catalog pricing, opening hours, active orders.
* `kitchen_staff` / `cashier`: Order fulfillment only (zero financial access).
* `driver`: Active trip navigation only.

---

## Core Architectural Capabilities

1. **Onboarding & Verification**:
   - Business registration, KYC/legal document upload, tax/VAT details, bank account verification.
   - Multi-stage onboarding status (pending review, verified, suspended, active).

2. **Catalog & Inventory Management**:
   - Product/service catalog creation, categorization, multi-variant options, and modifier groups.
   - Dynamic pricing rules, discount schedules, and real-time item availability toggling.
   - Stock level tracking and low-inventory alerts where applicable.

3. **Order Fulfillment Workflow**:
   - Real-time incoming order sound/push alerts.
   - Accept, reject (with structured reason codes), and estimated preparation time setting.
   - Order lifecycle progression: `acknowledged` → `preparing` → `ready_for_pickup` → `dispatched` → `delivered`.
   - Customer and delivery partner handoff coordination.

4. **Earnings, Settlement & Payouts**:
   - Granular gross revenue, net earnings, platform commission deductions, and adjustments ledger.
   - Scheduled bank settlement and automated payout status tracking.
   - Downloadable financial statements and tax summaries.

5. **Analytics & Operations**:
   - Real-time sales dashboard, top-performing items, peak order hours, customer ratings/feedback.

6. **Team & Staff Access Control**:
   - Role-based access control (RBAC): Owner, Store Manager, Kitchen Staff, Cashier, Driver.
   - Scoped permissions preventing unauthorized access to financial and bank payout records.

---

## Platforms

- **Android**: [`apps/partner/android/`](./android/) (Kotlin + Jetpack Compose)
- **iOS**: [`apps/partner/ios/`](./ios/) (Swift + SwiftUI)
- **Web Dashboard**: [`apps/web/partner/`](../web/partner/) (Next.js + React)
