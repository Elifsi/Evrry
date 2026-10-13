# Product Requirements — Consumer Application

The Consumer Application is the user-facing flagship product for the **evrry** Super App ecosystem, developed by **Elifsi Technologies Private Limited**.

---

## 1. Product Vision

A voice-first, AI-native super-app where users in Nepal and South Asia can seamlessly discover, chat, order, book, pay, and communicate across everyday commerce and service verticals. Built with parallel native codebases for Android (Kotlin + Jetpack Compose) and iOS (Swift + SwiftUI) delivering 1:1 UI parity.

---

## 2. Core User Journeys

### Journey 1: Nepal Mobile Authentication & WhatsApp OTP
1. User enters 10-digit Nepal mobile number with prefix `+977` (e.g., `9841234567`).
2. App sends a high-speed WhatsApp template OTP via Meta Cloud API / Supabase Edge Functions. If WhatsApp is unavailable, sparrow SMS provides fallback delivery.
3. 45-second countdown timer runs before allowing a resend.
4. User enters 6-digit PIN into individual input boxes; upon entering the 6th digit, verification executes automatically.
5. Session tokens are securely saved in device keychain / encrypted storage.

### Journey 2: Flagship AI Concierge & Minimal Model Switcher
1. User lands on the Home screen featuring a pulsating voice waveform orb and a minimalist ChatGPT-style model switcher pill `[ Ellie Mini ▾ ]`.
2. Tapping the switcher pill reveals the clean model selection:
   - **Ellie Mini** (`Fast · Everyday`): Lightweight, ultra-responsive model for instant food orders, rides, and daily tasks.
   - **Ellie Pro** (`Deep Reasoning`): Advanced reasoning model for multi-stop trip planning, complex inquiries, and custom itineraries.
3. **Automatic Multilingual Mirroring**: No manual language toggles or clutter. Ellie dynamically detects and responds in **Nepali, Hindi, Maithili, or English** based on the user's spoken or typed prompt.
4. LiveKit WebRTC pipeline streams low-latency bidirectional voice.

### Journey 3: Full Food Delivery Vertical (Deliverr & Enatega Architecture)
1. **Dedicated Food Delivery Home (`FoodHomeScreen` / `FoodHomeView`)**:
   - Pinned Top Navigation with in-app back (`< Explore Verticals`) and delivery ETA badge (`⚡ 25-35 min delivery`).
   - Location pill: `📍 Delivering to: Jhamsikhel, Lalitpur`.
   - Promotional festival banner: `EVRRY Food Fest` (20% OFF on Roadhouse & Jimbu Thakali, `NEPAL20`, free delivery on Rs 1,000+).
   - Real-time search bar for dishes, cuisines, or restaurants (`Momo`, `Pizza`, `Thakali`).
   - Horizontal Cravings Carousel: `🍽️ All`, `🥟 Momo & Newari`, `🍲 Thakali Thali`, `🍕 Woodfired Pizza`, `🍔 Burgers & CFC`, `🍱 Japanese & Sushi`, `🥐 Bakery & Desserts`, `🥗 Pure Veg`.
   - Quick Filter Chips: `Rating 4.5+ ★`, `Pure Veg 🟢`, `Under 30 mins ⚡`, `Special Offers 🏷️`.
   - Open Restaurants catalog cards featuring gradient banners, rating badges (★ 4.8), ETA (28 min), area (Pulchowk, Lalitpur), price for two (Rs 1,200 for two), and special offers.
2. **Dedicated Restaurant Menu Detail (`RestaurantDetailScreen` / `RestaurantDetailView`)**:
   - Hero header with restaurant gradient accent and in-app back navigation (`< Food`).
   - Restaurant metadata row: Rating badge, delivery ETA, location, price for two, promo offer badge, pure veg badge.
   - Categorized menu sections (e.g. `Woodfired Pizza`, `Starters`, `Thakali Thali`, `Momo Specials`, `Desserts`).
   - Dish row items with Veg / Non-Veg indicator dot (`🟢`/`🔴`), bestseller badge, price in NPR, and customization trigger.
3. **Enatega Multi-Restaurant Cart Replacement Alert**:
   - If user attempts to add an item from a different restaurant while items already exist in the cart, an interactive modal alert appears (`Replace cart items? Discard them and start a new order?`), with `Discard & Start` and `Cancel` options.
4. **Deliverr & Enatega Item Customization Sheet (`FoodItemCustomizationSheet`)**:
   - Bottom sheet displaying dish details, base price, preparation choice (radio options), extra add-ons (checkboxes), special cooking instructions text field, and quantity counter.
   - Updates unit and total price dynamically before adding to order.
5. **Cart & Multi-Vendor Checkout (`CartCheckoutScreen` / `CartCheckoutView`)**:
   - Delivery address card with instant switch sheet between saved locations (Home, Work).
   - Itemized cart with customized options displayed per item.
   - Special cooking instructions note for the chef.
   - Bill details: Subtotal + Delivery Partner Fee (Rs 50) + Platform Fee (Rs 10).
   - Domestic Direct Payments: eSewa, Khalti, Fonepay Dynamic QR, and Cash on Delivery (Zero in-app stored wallet per NRB compliance).
6. **Order Confirmation & Tracking Flow**:
   - Order placement routes dynamically to `OrderSuccessScreen` / `OrderSuccessView` with animated checkmark and order ID `#ord-XXXX`.
   - Tapping "Track Live Status" opens `OrderDetailsScreen` / `OrderDetailsView` featuring a 5-stage status stepper (`placed` → `confirmed` → `kitchen` → `delivering` → `delivered`), live route & dispatch card, driver HUD with direct Call action, delivery destination, and itemized receipt breakdown.

### Journey 4: Mobility & Ride Booking
1. User enters pickup and drop-off locations with visual route estimate.
2. Selects vehicle tier: Moto (Bike, fastest), Cab (Standard Car), or Comfort (Premium Air-Conditioned).
3. Live fare calculated in Nepali Rupees (NPR).
4. System confirms booking and generates a secure 4-digit ride start OTP (e.g. `1234`) displayed for the driver.

### Journey 5: Orders, Live Tracking & Receipts (Activity Tab)
1. Activity tab displays active and past orders, rides, and service requests.
2. 5-stage live order progress stepper (`Placed` → `Confirmed` → `In Kitchen` → `On the Way` → `Delivered`).
3. Driver HUD with real-time ETA, vehicle plate, driver rating, and 4-digit ride OTP (`1234`).
4. Itemized receipts with direct payment badges (Paid via eSewa, Khalti, Fonepay QR, COD — Zero in-app wallet per NRB compliance).

---

## 3. Implementation Status

| Platform | Path | Status | Notes |
| :--- | :--- | :--- | :--- |
| **Android Native** | [`apps/consumer/android/`](../../../apps/consumer/android/) | ✅ Implemented | Kotlin + Jetpack Compose, Material 3, in-app `<` navigation, Food Delivery Vertical |
| **iOS Native** | [`apps/consumer/ios/`](../../../apps/consumer/ios/) | ✅ Implemented | Swift + SwiftUI, MVVM, in-app `<` navigation, Food Delivery Vertical |
| **Mobile Prototype** | [`prototype/Phone/`](../../../prototype/Phone/) | ✅ Golden Master | Next.js 16 interactive smartphone UI reference |
| **Consumer Web** | [`apps/web/consumer/`](../../../apps/web/consumer/) | 🔮 Planned | Next.js + TypeScript responsive web portal |
