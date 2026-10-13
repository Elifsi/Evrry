# Product Requirements — Partner Application

The Partner Application provides business management and fulfillment tools for merchant partners on the **evrry** Super App platform, developed by **Elifsi Technologies Private Limited**.

---

## 1. Product Vision

Empower local merchants, restaurants, hotel operators, and gig service providers with intuitive tools to grow sales, fulfill incoming orders efficiently, track earnings, and receive transparent, timely payouts.

---

## 2. The 4 Core Partner Personas

| Persona | Partner Type (`public.partner_type`) | Typical Business | Primary Need |
|---|---|---|---|
| **1. Mobility Ride Driver** | `driver` | Bike, Taxi, Cab, Comfort EV operators | Real-time InDrive fare bidding, OSRM map routing, passenger start OTP (4 digits), In-Trip Chat & Dual Calling (VoIP + SIM) |
| **2. Restaurant Kitchen Partner** | `restaurant` | Quick-service & dine-in restaurants | Fast order acceptance with prep-time stepper, KDS order board, audio arrival alarms, 86'ing out-of-stock items, ESC/POS ticket printing |
| **3. Grocery Supermarket Partner** | `grocery` | Supermarkets & neighborhood marts (**Supermarket ONLY**, Dark Store deferred) | Order pick-and-pack fulfillment checklist, barcode scanning / SKU lookup, inventory adjustments, consumer substitution alerts |
| **4. Food & Grocery Delivery Courier** | `rider` | Independent two-wheeler delivery riders | Order claiming from restaurants/supermarkets, 2-stop GPS navigation (Store → Consumer), delivery OTP handoff, COD cash collection |

---

## 3. Key Feature Matrix

1. **Mobility Ride Console**:
   - InDrive-style live fare bidding & counter-offers.
   - Interactive turn-by-turn map navigation with bearing/heading calculation.
   - 4-digit start OTP keypad (`start_otp`).
   - Contextual In-Trip Passenger Chat and Dual Calling (In-App VoIP + Direct SIM dialer).
   - Live GPS broadcasting to Supabase Realtime `driver_locations`.

2. **Restaurant Kitchen KDS**:
   - High-priority audio chimes on incoming orders.
   - Accept with estimated preparation time (15m, 25m, 40m) or structured reject reason codes.
   - Live order progression: `acknowledged` → `preparing` → `ready_for_pickup`.
   - Instant menu item 86'ing (out of stock).
   - ESC/POS thermal receipt and kitchen order ticket (KOT) printing.

3. **Grocery Supermarket Fulfillment**:
   - Supermarket mode ONLY (Dark store deferred).
   - Pick-and-pack order checklist (marking items picked with weight/quantity confirmation).
   - Barcode scanning & SKU lookup for immediate inventory adjustments.
   - Out-of-stock item substitution proposal flow with customer notification.

4. **Delivery Courier Console**:
   - Triangular order claiming (notifies nearby couriers when order is preparing or ready).
   - 2-stage multi-stop turn-by-turn navigation (Pickup from Store → Drop-off at Customer).
   - Secure customer handoff 4-digit delivery OTP verification (`order_handoff_codes`).
   - Cash-on-Delivery (COD) cash collection ledger and wallet settlement.
   - Multi-party chat (Courier ↔ Merchant & Courier ↔ Consumer).

5. **Financial Insights & Settlements**:
   - Real-time gross sales vs. net earnings view.
   - Automated settlement cycle tracking and midnight bank transfer history.

6. **Staff Roles (RBAC)**:
   - Separation of cashier/kitchen views from owner financial controls (`owner`, `manager`, `kitchen_staff`/`cashier`, `driver`, `rider`).
