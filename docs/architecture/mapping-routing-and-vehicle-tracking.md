# Mapping, Routing & 3D Vehicle Tracking Specification
**evrry Super App Platform (Web vs. Mobile Map Division & Live Tracking)**  
*Elifsi Technologies Private Limited*

---

## 1. Executive Summary & Cost Strategy

The Evrry platform handles thousands of simultaneous food deliveries, grocery shipments, and passenger rides. Mapping and geolocation can easily become a major platform expense if not properly architected:

```
┌────────────────────────────────────────────────────────────────────────┐
│                      MAP & GEOLOCATION ARCHITECTURE                     │
├──────────────────────────────────┬─────────────────────────────────────┤
│ 📱 Native Phone Apps (Android/iOS)│ 🌐 Production Web Portal (Next.js)  │
├──────────────────────────────────┼─────────────────────────────────────┤
│ • Google Maps Mobile SDK         │ • MapLibre GL + OpenStreetMap (OSM) │
│ • 100% Free on Android           │ • 100% Free forever (Unlimited loads)│
│ • High Free Tier on iOS          │ • $0 Google JavaScript API billing  │
│ • 3D Isometric Vehicles (360°)   │ • Lightweight route polyline        │
│ • Turn-by-turn turn navigation   │ • "Open in Evrry Phone App" banner  │
├──────────────────────────────────┴─────────────────────────────────────┤
│                   COMMON BACKEND & ROUTING (All Clients)               │
├────────────────────────────────────────────────────────────────────────┤
│ • Self-Hosted OSRM on OCI (Zero Google Directions API fees)            │
│ • Supabase Edge Function: `routing` (Polyline, Distance, ETA)          │
│ • Supabase Realtime: `driver_location` & `order_tracking` channels     │
│ • Unified Pricing: Rs 10 Platform fee, Rs 50 base (3km), Rs 15/km extra│
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Division of Labor: Google Maps SDK vs. Self-Hosted OSRM

| Component | Responsibility | Why Not Google Cloud? |
|---|---|---|
| **Map Rendering (Mobile)** | Native Google Maps SDK displays the vector base map, satellite imagery, building footprints, and user location pin. | Android Google Maps SDK is **100% free with unlimited map loads**. iOS has a high free tier. |
| **Map Rendering (Web)** | **MapLibre GL + OpenStreetMap** renders vector/raster tiles in the browser canvas using WebGL. | Google Maps JS API charges **$7.00 per 1,000 loads** after the $200 credit. High web traffic would incur massive fees. |
| **Route & Polyline (All)** | **Self-Hosted OSRM** calculates the shortest drivable road route, polyline coordinates, driving distance, and estimated travel time. | Google Directions API charges **$5.00–$10.00 per 1,000 calls**. Self-hosted OSRM in Docker costs **$0** and answers in **< 5ms**. |
| **Nearest Road Snapping** | OSRM `nearest` API snaps noisy GPS coordinates to the actual road centerline before computing ETAs. | Completely free and local on the OCI instance. |

---

## 3. Web Browser Mapping & Live Tracking (`apps/web/consumer`)

### A. Zero-Cost Web Maps (MapLibre GL / OpenStreetMap)
On the web portal (`apps/web/consumer/`):
- Map viewing is powered by **MapLibre GL** (open-source fork of Mapbox GL) and OpenStreetMap tiles (via free tile providers like CartoDB Positron / OSM Standard).
- **Unlimited map loads at $0 cost**. Users can freely browse hundreds of restaurant locations, delivery radiuses, and store pick-up points without incurring Google Maps API charges.

### B. Live Order & Ride Tracking Helper Banner
When a customer views an active order or ride in their web browser:
1. The web page displays a clean, lightweight route polyline showing the merchant/pickup point, drop-off location, and the rider's approximate location.
2. A prominent, persistent helper card is displayed above the map:

```
┌────────────────────────────────────────────────────────────────────────┐
│ 🛵 LIVE TRACKING                                                       │
│ Driver is on the way! Estimated arrival in 12 mins.                    │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ 📱 For real-time 3D vehicle tracking and turn-by-turn navigation,  │ │
│ │    open this order in the Evrry Phone App!        [ Open App ↗ ]   │ │
│ └────────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

### C. Optional Google Maps JavaScript Fallback
If MapLibre GL is ever disabled or unsupported in a legacy browser environment, the web app can gracefully fall back to Google Maps JS API via the `NEXT_PUBLIC_GOOGLE_MAPS_KEY` environment variable, strictly constrained within Google's free monthly credit.

---

## 4. Mobile Phone Apps: 3D Vehicle Models & Isometric Tracking

In the native mobile applications (`apps/consumer/android/` and `apps/consumer/ios/`), tracking features high-fidelity **3D isometric vehicle models**:

### A. The 3 Vehicle Formats
1. 🛵 **Delivery Scooter / Moped**: Used for Food Delivery and Blinkit-style Quick-Commerce Grocery orders.
2. 🏍️ **Motorcycle / Two-Wheeler**: Used for Bike Taxi ride-hailing (fast single-passenger transport).
3. 🚗 **Cab / Sedan / Hatchback**: Used for Four-Wheeler Car ride-hailing (multi-passenger mobility).

### B. Visual & Motion Behavior
- **3D Isometric Perspective**: High-resolution rendering from a 45° overhead isometric angle, displaying depth, vehicle roof, windshield, and wheels.
- **Dynamic 360° Bearing Rotation**: The vehicle icon dynamically rotates to match the calculated bearing (heading angle `0°–360°`) along the current road polyline segment:
  $$\text{bearing} = \text{atan2}(\sin(\Delta \lambda) \cdot \cos(\phi_2), \cos(\phi_1) \cdot \sin(\phi_2) - \sin(\phi_1) \cdot \cos(\phi_2) \cdot \cos(\Delta \lambda))$$
- **Ground Drop-Shadow**: A soft, translucent elliptical shadow (`rgba(0,0,0,0.3)`) anchored beneath the chassis gives the vehicle realistic grounding against the road surface.
- **Smooth Coordinate Interpolation (Lerp)**: When the driver's phone sends GPS coordinates every 3–5 seconds over Supabase Realtime, the mobile client smoothly interpolates position and angle using linear interpolation (`lerp`) so the vehicle glides along the street without jitter.

---

## 5. What is Common Across Native Apps and Web

Both Native Mobile and Web clients share the exact same underlying services and business logic:

```
                  ┌─────────────────────────────────────────┐
                  │          SHARED DATA & LOGIC            │
                  └─────────────────────────────────────────┘
                                       │
      ┌────────────────────────────────┼────────────────────────────────┐
      ▼                                ▼                                ▼
┌──────────────────┐         ┌──────────────────┐         ┌──────────────────┐
│   SUPABASE DB    │         │   OSRM ROUTING   │         │ PRICING ENGINE   │
│  (20 Migrations) │         │ (Edge Function)  │         │ (Shared Rules)   │
├──────────────────┤         ├──────────────────┤         ├──────────────────┤
│ • `rides` table  │         │ • Polyline path  │         │ • Platform: Rs 10│
│ • `orders` table │         │ • Road distance  │         │ • Base: Rs 50 3km│
│ • PostGIS points │         │ • Real ETA mins  │         │ • Extra: Rs 15/km│
│ • Live Realtime  │         │ • Zero Google API│         │ • Free: > Rs 1000│
└──────────────────┘         └──────────────────┘         └──────────────────┘
```

1. **Shared Database & PostGIS**: Both clients query `public.stores`, `public.orders`, and `public.rides` where locations are stored as standard WGS84 coordinates and `geography(Point, 4326)` columns.
2. **Shared Routing Endpoint**: Both clients call the Supabase Edge Function `routing` (`EvrryWebRoutingClient` in `apps/web/common/routing.ts` on Web; `EvrryRoutingService` in Kotlin/Swift on Mobile).
3. **Shared Realtime GPS Channels**:
   - `driver_location:{driver_id}` broadcasts `{ latitude, longitude, bearing, speed, timestamp }`.
   - `order_tracking:{order_id}` broadcasts `{ status, current_step, eta_minutes }`.
4. **Shared Pricing Calculation**:
   - Platform fee: **Rs 10.00**
   - Free delivery threshold: **Rs 1,000.00**
   - Base delivery: **Rs 50.00** (up to 3 km)
   - Extra delivery: **Rs 15.00 / km**
   - Driver earnings: **Rs 40.00** base + **80%** of extra distance fees.

---

## 6. Delineation: Mobile Phone Prototype vs. Web Applications

> ⚠️ **CRITICAL ARCHITECTURAL DISTINCTION**:
>
> - **[`prototype/Phone/`](file:///home/rahul/codes/Evrry/prototype/Phone/)**:
>   - An **Interactive Mobile Phone Simulation** (Next.js 16).
>   - Specifically constrained and styled for a **smartphone screen form-factor** (bottom navigation bar, smartphone tap interactions, camera/mic permissions).
>   - Acts as the **Smartphone UI & UX Golden Master** for native Android (Jetpack Compose) and iOS (SwiftUI) apps.
>   - **It is NOT the desktop/laptop consumer web portal.**
>
> - **[`apps/web/consumer/`](file:///home/rahul/codes/Evrry/apps/web/consumer/)**:
>   - The **Production Responsive Web Portal** for desktop, laptop, and tablet browsers.
>   - Uses MapLibre GL for zero-cost map loads and displays the helper prompt banner guiding users to the phone app for 3D live tracking.
