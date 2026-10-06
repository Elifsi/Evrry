# apps/web/ — Production Web Applications

This directory contains the production web applications and shared web libraries for the **Evrry** Super App platform, developed by **Elifsi Technologies Private Limited**:

```
apps/web/
├── admin/        # Superadmin Operations & KYC Platform (Internal staff console)
├── common/       # Shared TypeScript SDK (Auth, Routing, Payments, Payouts, AI)
├── consumer/     # Customer-facing production web application (Next.js + React)
└── partner/      # Merchant & Host web operations dashboard (KDS, POS, Hotel Desk)
```

---

## ⚠️ Crucial Distinction: Mobile Phone Prototype vs. Web Applications

> **DO NOT CONFUSE `prototype/Phone/` WITH PRODUCTION WEB APPLICATIONS:**
> - [`prototype/Phone/`](../../prototype/Phone/) is strictly an **Interactive Mobile Phone Form-Factor Prototype**. It simulates a smartphone screen (bottom navigation bar, mobile touch gestures, camera/microphone hardware flows) and acts as the **Smartphone UI & UX Golden Master** for the native iOS and Android apps. It is **NOT** a desktop web app or the consumer web portal.
> - [`apps/web/`](./) contains the **Production Web Applications** designed for desktop and mobile web browsers that connect to the shared Supabase backend (`supabase/`) with live authentication, PostgreSQL RLS, and payment gateways.

---

## 🗺️ Web Mapping & Live Tracking Strategy

1. **Zero-Cost Web Maps (MapLibre GL / OpenStreetMap)**:
   - On the web (`apps/web/consumer/`), map viewing uses **MapLibre GL and OpenStreetMap tiles** (100% open-source, WebGL accelerated, **$0 cost with unlimited map loads**).
   - This avoids expensive Google Maps JavaScript API fees ($7.00 per 1,000 loads) when web users browse restaurants or track deliveries.
2. **Phone App Live Tracking Banner**:
   - For order and ride tracking on the web browser, a prominent helper banner is displayed:
     > 📱 **"For real-time 3D vehicle tracking and turn-by-turn navigation, open in the Evrry Phone App"**
3. **Native Phone Apps (`apps/consumer/`)**:
   - Uses the **Google Maps SDK** (100% free with unlimited loads on Android, high free tier on iOS) rendering smooth **3D isometric vehicle icons (Scooter, Motorcycle, and Cab/Car)** with 360° bearing rotation along OSRM road routes.
4. **Fallback Option**:
   - Google Maps JavaScript API is supported as an optional toggleable fallback only if MapLibre GL is ever insufficient for a specific browser environment.

