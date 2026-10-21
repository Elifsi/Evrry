# apps/partner/android — evrry Native Partner Android App

> **Platform**: evrry Super App Ecosystem  
> **Company**: Elifsi Technologies Private Limited  
> **Repository**: [https://github.com/Elifsi/Evrry.git](https://github.com/Elifsi/Evrry.git)  

## Status

✅ **Fully Implemented — Complete Source Code in Repository (56 Kotlin files)**

---

## 1. Technology Stack & Key Dependencies

```kotlin
// apps/partner/android/build.gradle.kts dependencies reference

// Supabase Kotlin Multiplatform SDK
implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))
implementation("io.github.jan-tennert.supabase:postgrest-kt")
implementation("io.github.jan-tennert.supabase:auth-kt")
implementation("io.github.jan-tennert.supabase:realtime-kt")
implementation("io.github.jan-tennert.supabase:storage-kt")

// Native Maps & Hardware Location Broadcast
implementation("com.google.android.gms:play-services-maps:18.2.0")
implementation("com.google.android.gms:play-services-location:21.2.0")
implementation("com.google.maps.android:maps-compose:4.3.3")

// High-Priority Audio & Foreground Services
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-service:2.7.0")

// ESC/POS Thermal Printing (Bluetooth & USB)
// e.g. com.dantsu.escposprinter:escposprinter:3.3.0
```

---

## 2. Multi-Flavor / Dynamic Role Architecture (`kaaneneskpc/Deliverr`)

The partner application uses a single-codebase adaptive architecture:
- **`flavorDimensions += "partnerType"`** or runtime adaptive role view:
  - `driver`: Mobility passenger rides (Bike, Taxi, Cab, Comfort EV). Turn-by-turn OSRM map routing, InDrive-style fare bidding, 4-digit start OTP, driver location broadcast, In-Trip Chat & Dual Calling (VoIP + SIM).
  - `restaurant`: Dining kitchen operations. Tablet-optimized Kitchen Display System (KDS), preparation timers, 86ing items, ESC/POS thermal ticket printing, courier/consumer chat.
  - `grocery`: **Supermarket Mode ONLY** (Dark Store deferred). Order pick-and-pack checklist, barcode scanner, inventory adjustments, and substitution alerts.
  - `rider`: Food & grocery delivery courier. Foreground GPS tracking service, order claiming, 2-stage multi-stop routing (Store Pickup → Customer Drop), handoff OTP verification, COD cash collection, multi-party chat.

---

## 3. Official Architectural Reference Alignments

- **Driver Mobility & GPS Broadcast**: Follows [`amitshekhariitbhu/ridesharing-uber-lyft-app`](https://github.com/amitshekhariitbhu/ridesharing-uber-lyft-app) and [`anandwana001/uber-clone`](https://github.com/anandwana001/uber-clone) with vehicle bearing/heading calculation, polyline decode, and active HUD.
- **Kitchen KDS & Order Lifecycles**: Follows [`enatega/food-delivery-multivendor`](https://github.com/enatega/food-delivery-multivendor) (`enatega-multivendor-store`) for finite-state order transitions (`acknowledged` $\rightarrow$ `preparing` $\rightarrow$ `ready_for_pickup`), audio alarm chimes, and ESC/POS thermal printing.
- **Grocery Supermarket Fulfillment**: Follows [`Aakash901/BlinkitClone`](https://github.com/Aakash901/BlinkitClone) for item catalog models, pick-and-pack checklist, and out-of-stock substitutions.
- **Delivery Courier Operations**: Follows [`kaaneneskpc/Deliverr`](https://github.com/kaaneneskpc/Deliverr) and [`enatega/food-delivery-multivendor`](https://github.com/enatega/food-delivery-multivendor) (`enatega-multivendor-rider`) for foreground GPS service, order claiming, two-stop navigation, and delivery OTP handoff.
- **Chat & Calling**: Follows [`GetStream/whatsApp-clone-compose`](https://github.com/GetStream/whatsApp-clone-compose) and [`Gmarvis/whatsapp-clone`](https://github.com/Gmarvis/whatsapp-clone) for real-time messaging and dual calling (VoIP + SIM dialer).

---

## 4. Security & Permissions

- Partner staff permissions strictly governed by PostgreSQL Row Level Security (RLS) linked to `public.partner_members` (`owner`, `manager`, `cashier`, `driver`).
- Zero financial or bank disbursement authority on `cashier` or `driver` logins.
