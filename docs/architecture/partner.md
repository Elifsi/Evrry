# Partner Architecture

The Partner application enables merchants, restaurants, service providers, and gig operators to manage their business, catalog, orders, and finances on the **evrry** Super App platform, developed by **Elifsi Technologies Private Limited**.

---

## 1. Platform Clients

| Client | Directory | Technology | Build / Package |
|---|---|---|---|
| **Android** | `apps/partner/android/` | Kotlin, Jetpack Compose, Foreground Services | Gradle |
| **iOS** | `apps/partner/ios/` | Swift, SwiftUI, Critical Alerts | Xcode |
| **Web Portal** | `apps/web/partner/` | Next.js, React, Tailwind, Data Tables, Charts | npm |

---

## 2. Role-Based Partner Architecture

Because the Consumer experience in evrry is multi-vertical (Food, Rides, Grocery, Hotels, Services), the **Partner Application is fundamentally Role-Based and Domain-Adaptive**. 

The system operates across **two distinct role dimensions**:

### Dimension 1: Vertical / Business Domain Roles (What business is being operated?)
The Partner UI dynamically reconfigures its core views based on the partner's registered vertical:

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                          PARTNER VERTICAL ROLE MAPPING                                  │
├───────────────────┬─────────────────────────────────────────────────────────────────────┤
│ Partner Type      │ Corresponding Partner Role & Operational Interface                  │
├───────────────────┼─────────────────────────────────────────────────────────────────────┤
│ 🚗 Driver         │ MOBILITY RIDE DRIVER ROLE (`partner_type = 'driver'`):              │
│ (Passenger Rides) │ • Full-screen interactive map & turn-by-turn routing (OSRM)         │
│                   │ • InDrive-style real-time fare negotiation & counter-bidding        │
│                   │ • Live trip requests HUD (Accept, Counter-Bid, Decline)             │
│                   │ • Passenger 4-digit start OTP verification (`start_otp`)            │
│                   │ • Live GPS broadcasting to Supabase Realtime `driver_locations`     │
│                   │ • In-Trip Passenger Chat & Dual Calling (In-App VoIP + Direct SIM)  │
├───────────────────┼─────────────────────────────────────────────────────────────────────┤
│ 🍔 Restaurant     │ RESTAURANT / KITCHEN ROLE (`partner_type = 'restaurant'`):          │
│ (Food & Dining)   │ • Kitchen Display System (KDS) order queue & prep timers            │
│                   │ • High-decibel audio incoming order chimes                          │
│                   │ • Accept with estimated prep time (15m, 25m, 40m) or structured rej.│
│                   │ • Instant menu item 86'ing (marking out-of-stock in real-time)      │
│                   │ • ESC/POS thermal receipt & Kitchen Order Ticket (KOT) printing     │
│                   │ • In-app communication with assigned Delivery Courier & Customer    │
├───────────────────┼─────────────────────────────────────────────────────────────────────┤
│ 🛒 Grocery        │ GROCERY / SUPERMARKET ROLE (`partner_type = 'grocery'`):            │
│ (Retail Mart)     │ • ⚠️ SUPERMARKET MODE ONLY (Dark Store micro-fulfillment deferred)   │
│                   │ • Order pick-and-pack fulfillment checklist (item-by-item checkoff) │
│                   │ • Barcode scanner / SKU lookup for price checks & stock depletion   │
│                   │ • Real-time inventory count and customer substitution alert dispatch│
│                   │ • In-app communication with assigned Delivery Courier & Customer    │
├───────────────────┼─────────────────────────────────────────────────────────────────────┤
│ 🛵 Rider          │ FOOD & GROCERY DELIVERY COURIER ROLE (`partner_type = 'rider'`):     │
│ (Delivery Courier)│ • Claims ready orders from restaurants & supermarkets               │
│                   │ • Two-stage multi-stop turn-by-turn navigation (Pickup → Drop-off)   │
│                   │ • Secure customer handoff 4-digit delivery OTP verification         │
│                   │ • Cash-on-Delivery (COD) cash collection ledger & reconciliation    │
│                   │ • Multi-party chat (Courier ↔ Restaurant/Supermarket & Consumer)    │
└───────────────────┴─────────────────────────────────────────────────────────────────────┘
```

### Dimension 2: Staff Permission Roles (Who is using the app?)
Within any partner entity, staff accounts are restricted via PostgreSQL Row Level Security:

* **`owner`**: Complete authority. Bank account setup, payout requests, revenue reports, tax/VAT documents, and staff member management.
* **`manager`**: Operational control. Menu/catalog pricing, operating hours, active order dispatch, and customer dispute resolution.
* **`kitchen_staff` / `counter_cashier`**: Order fulfillment only. View incoming order tickets, mark items as "ready for pickup", and print receipts. **Strictly blocked from viewing bank accounts, gross revenues, or payout triggers.**
* **`driver`**: Mobility execution only. View assigned trip details, passenger phone proxy/direct dialer, and GPS navigation.
* **`rider`**: Delivery courier execution only. View assigned order pickup & delivery points, customer OTP, and COD cash collection ledger.

---

## 3. Core Functional Capabilities

### A. Partner Onboarding & Business Profile
- Submission of business details, trade licenses, VAT/PAN certificates, bank accounts.
- Multi-step verification state machine: `submitted` → `under_review` → `verified` / `rejected` → `active`.
- Store hours, operating days, holiday schedules, and temporary "busy" or "closed" toggles.

### B. Catalog, Menu & Inventory Control
- Hierarchical category and product creation.
- Support for complex product variants (sizes, colors) and modifier groups (toppings, add-ons, required vs. optional selections).
- Stock level tracking with automated out-of-stock toggling.

### C. Live Order Dispatch & Kitchen Display
- High-priority real-time order arrival via WebSocket subscriptions (Supabase Realtime) and device push notifications.
- Order review actions:
  - **Accept**: Sets estimated prep time (e.g., 15 mins).
  - **Reject**: Requires a structured reason (out of ingredients, store overloaded).
- Status advancement: `acknowledged` → `preparing` → `ready_for_pickup` → `dispatched` → `delivered`.
- Bluetooth / LAN ESC/POS thermal receipt and kitchen order ticket (KOT) printing.

### D. Financials, Earnings & Ledger
- Per-order earnings breakdown:
  - Gross Order Value
  - Platform Commission / Fee
  - Taxes / VAT withheld
  - Net Partner Earning
- Financial balance ledger and settlement history.
- Bank payout status tracking and downloadable tax invoices.

### E. Team & Staff Role-Based Access Control (RBAC)
- Staff invitation by email/phone.
- Granular permission levels:
  - **Owner**: Full access including bank details, staff management, and business settings.
  - **Manager**: Menu pricing, operating hours, order management, performance analytics.
  - **Kitchen / Cashier**: Active order view, order state transitions, receipt printing (no financial access).
  - **Driver / Courier**: Assigned order details, customer contact, and delivery completion.

---

## 4. Multi-Tenant Data Isolation

- Partner isolation is enforced via database policies using `partner_id` foreign keys.
- Authenticated requests resolve the caller's authorized partners via `partner_members`.
- Under no circumstances can Partner A read orders, customer details, or financials of Partner B.

---

## 5. Hybrid Operational Architecture: Manual Control Dashboard + AI Co-Pilot

A core platform principle is **"AI-Assisted, Human-Controlled"**:
- **Never a Black Box**: AI is an accelerator, **not a replacement for partner control**.
- **100% Full Manual Control**: Every single feature, price, order status, room availability, and setting has a robust, intuitive manual dashboard (buttons, forms, toggles, tables). Small or quick tasks can be done manually in seconds without touching AI.
- **AI as a Co-Pilot**: AI assists busy partners (e.g. scanning a 100-item paper menu with a photo, hands-free voice commands while driving or cooking). All AI actions present a preview for human confirmation before publishing.

### A. Manual Controls vs. AI Co-Pilot Feature Matrix

```
┌───────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                               HYBRID PARTNER OPERATIONAL MATRIX                                       │
├─────────────────────┬───────────────────────────────────────────┬─────────────────────────────────────┤
│ Domain / Task       │ 🛠️ Full Manual Dashboard (Always Available)│ 🤖 AI Co-Pilot Accelerator (Optional)│
├─────────────────────┼───────────────────────────────────────────┼─────────────────────────────────────┤
│ Menu & Inventory    │ • Form-based item creation & edit         │ • Photo-to-Menu OCR & Digitizer     │
│                     │ • Manual price changes & stock counters   │   (snaps physical menu, auto-fills) │
│                     │ • Image upload & tag selectors            │ • Nepali voice toggle: "Momo sakiyo"│
│                     │ • Out-of-stock toggle switches            │   (instantly toggles availability)  │
├─────────────────────┼───────────────────────────────────────────┼─────────────────────────────────────┤
│ Live Order & KDS    │ • KDS board with Accept / Reject buttons  │ • Voice order status updates        │
│                     │ • Custom prep time selector (10m, 20m)    │ • Automated print ticket triggers   │
│                     │ • Manual handoff code input               │ • Kitchen audio chime on new orders │
├─────────────────────┼───────────────────────────────────────────┼─────────────────────────────────────┤
│ Rooms & Stays       │ • Visual calendar with drag-to-book       │ • 3-bullet listing generator        │
│                     │ • Manual room rates & blackout dates      │ • AI Draft FAQ auto-responder for   │
│                     │ • Form inputs for amenities & house rules │   amenity/parking questions on chat │
├─────────────────────┼───────────────────────────────────────────┼─────────────────────────────────────┤
│ Rides & Drivers     │ • Interactive map with Accept / Bid slider│ • Hands-free voice HUD while driving│
│                     │ • Manual 4-digit start OTP keypad         │ • Audio route & pickup advisories   │
│                     │ • One-tap "Arrived" / "Completed" buttons │ • "Customer lai call gara" command  │
├─────────────────────┼───────────────────────────────────────────┼─────────────────────────────────────┤
│ Financials & COD    │ • Daily ledger balance & settlement tables│ • Plain-Nepali morning audio/text   │
│                     │ • Bank account entry & invoice downloads  │   briefing (sales, high-demand tips,│
│                     │ • In-app "Settle Cash" via eSewa/Khalti   │   customer review sentiment summary)│
└─────────────────────┴───────────────────────────────────────────┴─────────────────────────────────────┘
```

### B. Human-in-the-Loop Safeguards
1. **Catalog Previews**: When an AI scans a paper menu photo, it populates a draft form in the manual dashboard. The merchant reviews the names, prices, and tags, makes any quick edits, and clicks **"Save & Publish"**.
2. **Chat Auto-Response Approval**: AI drafts replies to guest inquiries based on verified property amenities. The host can either tap **"Send"**, edit the text, or toggle full auto-reply on/off at will.
3. **Manual Fallback**: If the device is offline or the user prefers manual operation, 100% of workflows operate independently of AI services.

---

## 6. Single Adaptive Partner App vs. Fragmented Apps & Onboarding Lifecycle

### A. Why a Single Adaptive App is Strictly Superior
Instead of maintaining fragmented apps ("EVRRY Driver", "EVRRY Restaurant", "EVRRY Grocery", "EVRRY Courier"), **evrry uses ONE unified Adaptive Partner Application** (`apps/partner/android` and `apps/partner/ios` on mobile, `apps/web/partner` on desktop/web):

1. **Zero Clutter via Dynamic HUD**: A bike driver never sees restaurant kitchen buttons; a supermarket clerk never sees bike-taxi maps. The app detects the partner's verified active role (`driver`, `restaurant`, `grocery`, `rider`) and renders **only** the relevant operational interface.
2. **Single Codebase & Shared Infra**: One app to test, publish, and maintain. Authentication (SMS OTP), KYC uploaders, Bank Account management, and Ledger Payouts are 100% shared across all four partner types.
3. **Multi-Role Switching for Nepali Business Owners**: Many individuals in Nepal operate multiple ventures (e.g. a grocery store owner who also drives a taxi in the evening, or a restaurant owner who runs local food delivery). A profile switcher in the top app bar allows instant 1-tap switching without logging in and out.
4. **The 4 Primary Mobile Operational Modes**:
   - **Mobility Ride Driver (`driver`)**: Full-screen OSRM Map HUD, InDrive bidding cards, live GPS broadcasting, passenger start OTP, in-trip Chat & Dual Calling (VoIP + SIM).
   - **Restaurant Kitchen (`restaurant`)**: KDS order board, audio arrival chimes, prep countdown timers, instant 86'ing out-of-stock items, ESC/POS receipt printing.
   - **Grocery Supermarket (`grocery`)**: **Supermarket mode only** (Dark Store deferred). Order picking checklist, camera barcode scanner, real-time inventory adjustments, customer substitution dispatch.
   - **Food & Grocery Courier (`rider`)**: Foreground location tracking, order claiming, two-stop routing (Store Pickup → Customer Drop), handoff OTP verification, COD cash ledger.

### B. End-to-End Partner Onboarding Workflow

```mermaid
sequenceDiagram
    autonumber
    actor P as Partner (Phone/Web)
    participant App as EVRRY Partner App
    participant DB as Supabase DB & Storage
    actor Admin as Elifsi Superadmin Console

    P->>App: 1. Sign in with Phone Number (SMS OTP)
    App->>DB: Resolves/creates auth profile

    P->>App: 2. Selects Business Vertical: [Restaurant | Grocery | Rider | Driver | Hotel | Landlord]

    P->>App: 3. Enters Business Profile (Trade Name, Legal Name, Palika, Ward, Address, GPS)
    App->>DB: INSERT into partner_profiles (status = 'draft', type = chosen_type)

    P->>App: 4. Uploads KYC Documents (Citizenship, License, Bluebook, PAN/VAT, or Lalpurja)
    App->>DB: Uploads to private 'kyc' bucket; INSERT partner_kyc_documents (status = 'pending')

    P->>App: 5. Inputs Bank Account for Midnight Payouts (Bank Code, Account Name & Number)
    App->>DB: INSERT partner_bank_accounts (is_verified = false)

    P->>App: 6. Clicks "Submit for Verification"
    App->>DB: UPDATE partner_profiles SET status = 'pending_verification'

    Note over Admin,DB: Instant notification in Superadmin Console (apps/web/admin)
    Admin->>DB: 7. Inspects documents in split-screen viewer & verifies IRD PAN / Bluebook stamp
    Admin->>DB: Calls admin_review_partner(p_partner, p_approve, p_reason)

    alt Approved
        DB-->>App: partner_profiles.status = 'active'
        App-->>P: Push Notification / SMS: "Welcome to EVRRY! Your store/vehicle is now LIVE!"
        Note over P,App: Dynamic HUD unlocks! Restaurant sees KDS; Rider sees Map HUD.
    else Rejected
        DB-->>App: partner_profiles.status = 'rejected' (rejection_reason = "Bluebook tax stamp unclear")
        App-->>P: Notification: "Action required: Please re-upload your Bluebook document."
    end
```

---

## 7. Kitchen Voice Wake-Word & Zero-Leak Audio Architecture

Streaming continuous 24/7 kitchen audio to cloud LLMs would bankrupt the platform, record private chatter, and cause hallucinations. 

evrry enforces a **3-Layer Local Audio Filter** so cloud AI is asleep 99.9% of the day:

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                   3-LAYER ZERO-LEAK KITCHEN AUDIO ARCHITECTURE                       │
├──────────────────────────────────────────────────────────────────────────────────────┤
│ 1. 100% OFFLINE LOCAL WAKE-WORD (Device CPU - Zero Cloud Cost, Zero Data Sent)       │
│    • Tiny open-source model (Porcupine / Vosk WebAssembly) runs locally in browser.  │
│    • Listens ONLY for the exact wake phrase: "Hey Evrry" or "Namaste Evrry".         │
│    • All background chatter (cricket, music, customer conversations) is 100% ignored.│
├──────────────────────────────────────────────────────────────────────────────────────┤
│ 2. VISUAL CHIME & 4-SECOND LISTENING WINDOW                                          │
│    • Screen flashes a bright green ring & plays a pleasant "Ting" chime.             │
│    • Opens a strict 4-second listening window.                                       │
│    • Local VAD (Voice Activity Detection) detects silence and closes mic in 1 sec.   │
├──────────────────────────────────────────────────────────────────────────────────────┤
│ 3. 2-SECOND AUDIO SNIPPET DISPATCH & PHYSICAL ALTERNATIVES                           │
│    • Only the 2-second command audio is sent to the cloud STT API (< NPR 0.05 cost).  │
│    • Physical Alternatives for Loud Kitchens:                                        │
│      - Big On-Screen Tap Button (wrist/elbow tap).                                    │
│      - Bluetooth Kitchen Foot Pedal (step to talk, release to send).                 │
└──────────────────────────────────────────────────────────────────────────────────────┘
```
