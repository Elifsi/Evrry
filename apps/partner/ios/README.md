# apps/partner/ios — evrry Native Partner iOS App

> **Platform**: evrry Super App Ecosystem  
> **Company**: Elifsi Technologies Private Limited  
> **Repository**: [https://github.com/Elifsi/Evrry.git](https://github.com/Elifsi/Evrry.git)  

## Status

🔮 **Planned — Implementation Blueprint Ready**

---

## 1. Technology Stack & Key Dependencies

```swift
// Swift Package Manager (Package.swift / Xcode Dependencies)

// 1. Supabase Swift Client (PostgREST, Auth, Realtime, Storage)
.package(url: "https://github.com/supabase/supabase-swift.git", from: "2.14.0")

// 2. High-Priority Push Alerts & APNs
// UserNotifications framework with Critical Alert entitlements for high-decibel order rings

// 3. Printing & Hardware
// CoreBluetooth for portable ESC/POS thermal printers & AirPrint support
```

---

## 2. Architecture & Design Patterns
 
 - **UI Framework**: Modern SwiftUI with role-adaptive views:
   - `driver`: Interactive Map HUD (`GMSMapView`), InDrive bidding card, passenger 4-digit start OTP, turn-by-turn routing, Dual Calling sheet (VoIP + SIM), and passenger Chat.
   - `restaurant`: iPad split-view navigation (`NavigationSplitView`) for kitchen display systems (KDS), preparation timers, 86'ing out-of-stock items, and ESC/POS thermal printing.
   - `grocery`: **Supermarket Mode ONLY** (Dark Store deferred). Order pick-and-pack checklist, camera barcode scanner, inventory adjustments, and substitution alerts.
   - `rider`: Delivery Courier view. Order claiming, multi-stop routing (Store Pickup → Customer Drop), handoff OTP verification, COD ledger, multi-party chat.
 - **Role Switching**: Top-bar 1-tap Role Switcher for multi-venture Nepali partners without logging out.
 - **Concurrency**: Native Swift Concurrency (`async/await`) consuming real-time order and ride streams from Supabase:
   ```swift
   for await change in client.channel("partner-feed").postgresChange(...) {
       // update incoming ride bids or kitchen/grocery order queue
   }
   ```
 - **Staff Role Scoping**: UI surfaces adapt automatically to the logged-in staff role (`owner`, `manager`, `cashier`, `driver`, `rider`) based on PostgreSQL RLS claims.
 
 ---
 
 ## 3. Reference Alignments
 
 - **Mobility Driver Navigation**: Follows [`amitshekhariitbhu/ridesharing-uber-lyft-app`](https://github.com/amitshekhariitbhu/ridesharing-uber-lyft-app) and [`anandwana001/uber-clone`](https://github.com/anandwana001/uber-clone) for driver HUD and polyline tracking.
 - **Multi-Vendor Kitchen Dashboard**: Follows [`enatega/food-delivery-multivendor`](https://github.com/enatega/food-delivery-multivendor) (`enatega-multivendor-store`) for ticket prep timers, audio chimes, and item modifier views.
 - **Grocery Supermarket Fulfillment**: Follows [`Aakash901/BlinkitClone`](https://github.com/Aakash901/BlinkitClone) for catalog models, pick-and-pack checklist, and substitutions.
 - **Delivery Courier Operations**: Follows [`enatega/food-delivery-multivendor`](https://github.com/enatega/food-delivery-multivendor) (`enatega-multivendor-rider`) and [`kaaneneskpc/Deliverr`](https://github.com/kaaneneskpc/Deliverr) for delivery order lifecycles and handoff OTPs.
 - **Chat & Calling**: Follows [`GetStream/whatsApp-clone-compose`](https://github.com/GetStream/whatsApp-clone-compose) and [`Gmarvis/whatsapp-clone`](https://github.com/Gmarvis/whatsapp-clone).
