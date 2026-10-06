# apps/web/consumer — Production Consumer Web App

## Status

🔮 **Planned — Implementation Blueprint Ready**

## Technology Stack

- **Framework**: Next.js 15+ (App Router)
- **Language**: TypeScript (Strict Mode)
- **UI & Styling**: React, Tailwind CSS, Lucide Icons, Radix UI
- **Backend SDK**: `@supabase/ssr` / `@supabase/supabase-js` (Shared Supabase backend)
- **Maps & Tracking**: MapLibre GL + OpenStreetMap tiles (100% free, unlimited map loads, zero Google billing)
- **Routing Engine**: Self-hosted OSRM via `EvrryWebRoutingClient` (`apps/web/common/routing.ts`)
- **State Management**: Zustand / React Query
- **Payments**: Hosted gateway integration (eSewa, Khalti, Fonepay, Card tokenization)

## Core Responsibilities

- Fully responsive web version of the **Evrry** consumer experience (by **Elifsi Technologies Private Limited**).
- Web-based AI concierge chat interface with browser voice integration (Web Speech API).
- Search, browse, and multi-vertical checkout across food, grocery, hotels, electronics, and services.
- User account management, address book, saved cards, order tracking, and notification center.
- Direct messages and browser-based WebRTC calling.

---

## 🗺️ Web Map Viewing & Order Tracking Architecture

### 1. Cost Efficiency via MapLibre GL & OpenStreetMap
- Google Maps JavaScript API costs **$7.00 per 1,000 loads** (after initial $200 free tier). Loading Google Maps for every restaurant browsing visitor or web food order quickly causes unsustainable costs.
- `apps/web/consumer` uses **MapLibre GL and OpenStreetMap tiles**, which is **100% free and open source with unlimited map loads**.

### 2. Live Tracking Helper Banner
On the web order/ride tracking screen, a prominent card/banner informs the customer:
```
┌────────────────────────────────────────────────────────────────────────┐
│ 📱 For real-time 3D vehicle tracking and turn-by-turn navigation,     │
│    open in the Evrry Phone App!                   [ Open App ]        │
└────────────────────────────────────────────────────────────────────────┘
```
- In the native mobile phone apps (Android / iOS), full 3D isometric vehicle models (Scooter, Motorcycle, and Cab) rotate smoothly in 360° along the OSRM route.
- Web tracking displays a lightweight route polyline with store and drop-off pins, keeping web bundle size minimal.

### 3. Fallback Provision
- If MapLibre GL is ever disabled or unsupported in a specific browser environment, a toggleable Google Maps JavaScript API fallback can be activated via `NEXT_PUBLIC_GOOGLE_MAPS_KEY`, remaining within the free tier.

---

## Relationship to `prototype/Phone/`

- [`prototype/Phone/`](../../../prototype/Phone/) is strictly an **Interactive Mobile Phone Form-Factor Prototype** (simulates smartphone dimensions, bottom navigation bar, smartphone tap interactions, camera/mic flows). It is **NOT** a desktop web app.
- `apps/web/consumer` is the **production web portal** designed for desktop and mobile web browsers with responsive layouts, connecting directly to the shared Supabase backend.

> **Security Reminder**: Never expose `SUPABASE_SERVICE_ROLE_KEY` in client bundles. Use only `NEXT_PUBLIC_SUPABASE_URL` and `NEXT_PUBLIC_SUPABASE_ANON_KEY`.

