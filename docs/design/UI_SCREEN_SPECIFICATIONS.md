# EVRRY — Screen Specifications

Companion to [`UI_DESIGN_SYSTEM.md`](UI_DESIGN_SYSTEM.md) · [`UI_COMPONENT_CATALOG.md`](UI_COMPONENT_CATALOG.md) · [`UI_TYPOGRAPHY_AND_SPACING.md`](UI_TYPOGRAPHY_AND_SPACING.md). Reference: [`assets/App identity.jpg`](../../assets/App%20identity.jpg), "App UI Previews (Key Screens)".

**Legend** — 👁 Observed in reference · 🧭 Decision · ⚠ Provisional. All names/balances/transactions in the reference are **illustrative mock data**; nothing here implies a functioning feature.

> [!IMPORTANT]
> **Payments (reference screen 5) is excluded by product decision** (no in-app wallet; Balance, Send, Receive, Scan, Add Money, Wallet shortcut are not built). Section 5 below states what replaces it. Checkout payment choices (Fonepay QR, eSewa, Khalti, Card, COD) use the *Payment-method selector* in the component catalog.

---

## Navigation

**Primary bottom navigation (5, always visible on top-level screens)** — 👁 Home · Chat · Pay · Shop · More.
🧭 evrry mapping: **Home · Chat · Orders · Shop · More** (the *Pay* slot becomes **Orders/Activity**: orders, bookings, rides, history). ⚠ needs designer sign-off.

**Secondary service shortcuts (Home grid, 👁 10 tiles)**: Chat, Pay, Wallet, Contacts, Shop, Travel, Services, Bills, Health, More.
🧭 evrry mapping: **Chat · Food · Mart · Rides · Stays · Rooms · Rentals · Contacts · Vouchers · More** (Pay/Wallet/Bills/Health dropped or reassigned). No destination appears both as a tab *and* a tile except **Chat** and **Shop**, which are intentionally duplicated (👁 reference does the same) for one-tap reach.

| Concern | Rule |
|---|---|
| Selected tab | `brand.primary` filled icon + semibold label; re-tapping selected tab scrolls to top / pops to root |
| State | Each tab keeps its own back stack (restored on re-select) |
| Transitions | Push: slide-in 220 ms `standard`; modal/sheet: slide-up 220 ms; tab switch: cross-fade 150 ms; reduced-motion → fade only/none |
| Back | System back & top-bar chevron `<` (👁 present on Payments/Travel/Chat) pop one level; at root of a tab → go to Home; at Home → exit |
| Titles | Left-aligned `screenTitle` in top bar for root screens; inline back + title for pushed screens (👁 "< Travel") |
| Modules | Same top bar pattern, same bottom nav visibility (hidden only in full-screen flows: camera, map tracking, checkout, chat thread composer keeps nav hidden) |

---

## Screen 1 — Brand Splash Screen (1.5-Second Animated)
| | |
|---|---|
| **Purpose** | Brand entrance moment while verifying saved session or routing to onboarding |
| **Duration** | Exactly 1.5 s (1500 ms): 450 ms entrance curve, 700 ms brand hold, 350 ms exit crossfade |
| **Layout** | Vertical centered stack on soft gradient (`#FFFFFF` → `#F0F6FF`); centered blue rounded squircle tile (`92×92 dp/pt`, radius `26 dp/pt`) with bold white `e`; below it the `evrry` wordmark with `🇳🇵` Nepal flag; tagline *Nepal's Everything Super App*; bottom 3-dot micro-pulse indicator |
| **Components** | `SplashScreen` (Android) / `SplashView` (iOS), `Animatable` / `withAnimation`, `EvrryPrimaryBlue` surface badge |
| **Thermal & Battery Optimization** | Uses native hardware-accelerated Compose `Animatable` and SwiftUI `.animation` primitives with zero CPU polling loops or heavy video files to prevent device overheating and battery drain |
| **Auto-Routing State Machine** | 1. If active user session exists in local storage / keychain → Route directly to Home (`AuthScreenState.Authenticated`)<br>2. If first install / onboarding not completed → Route to Onboarding Carousel (`AuthScreenState.Onboarding`)<br>3. If onboarding previously seen but unauthenticated → Route to Phone Login (`AuthScreenState.Idle`) |
| **A11y** | Announce "Evrry, Nepal's Everything Super App" once; honor system reduced-motion (skip scale bounce) |

---

## Screen 2 — Dynamic DoorDash-Grade Onboarding Walkthrough (5 Core Verticals)
| | |
|---|---|
| **Purpose** | Educate first-time users on Nepal's 5 core super app verticals with interactive, smooth transitions |
| **Layout** | Full-screen dynamic container with background color smoothly crossfading (`animateColorAsState` / `.animation`) to match each active vertical. Top bar: Brand wordmark (`e evrry`) + top-right "Skip" button. Center: Interactive artwork card (`240×240 dp/pt`) with drop shadow, category badge, central hero vector icon, floating live metric pill (`previewMetric`), bold headline, descriptive subtitle, and 2 highlight feature chips with checkmarks. Bottom: DoorDash-style expanding pill indicator + "Continue" / "Get Started with Evrry" CTA. |
| **Extensible Data Model** | `OnboardingPage(id, badge, title, subtitle, accentColor, backgroundColor, icon/systemImage, highlights, previewMetric, previewMetricLabel)` |
| **5 Core Slides** | 1. **Food Delivery & Dining** (`#EF4444` Coral Red): *"Hot & Fresh Meals, Delivered Fast"* · 28 min avg delivery · 450+ restaurants · Live GPS rider tracking<br>2. **Grocery Quick Mart** (`#10B981` Emerald Green): *"Groceries & Daily Essentials in 15 Mins"* · 15 min express dispatch · Farm-fresh produce · First 3 orders free delivery<br>3. **Mobility & Rides** (`#2563EB` Royal Blue): *"Fast & Safe Rides Across Nepal"* · 3 min pickup ETA · Upfront transparent fares · 24/7 In-ride SOS security<br>4. **End-to-End Encrypted Chat** (`#8B5CF6` Purple): *"Private Chat, HD Calls & Money Sharing"* · 100% Signal-grade Double Ratchet E2EE · Crystal HD calls · Split bills<br>5. **Hospitality & Rentals** (`#F59E0B` Warm Amber): *"Boutique Stays & Vehicle Rentals"* · 4.9★ rating · Verified host guarantee · Instant digital check-in |
| **Interactive Controls** | Swipeable `HorizontalPager` / `TabView(PageTabViewStyle)`, tapping expanding dots jumps to page, "Skip" routes immediately to Login, last slide CTA transforms to *"Get Started with Evrry"* with forward arrow |
| **A11y** | Content accessible via both swipe gestures and directional tap buttons; indicators announce "Page X of 5"; CTA touch target minimum 54 dp/pt |

---

## Screen 2.1 — Mobile Authentication & 1-Tap Social Sign-In
| | |
|---|---|
| **Purpose** | Low-friction Nepal mobile number verification (+977) and 1-tap social login |
| **Layout** | Scrollable container (`verticalScroll` / `ScrollView`). Top: Brand header + welcome message. Center: Phone input field with 🇳🇵 `+977` prefix, OTP channel selector pills (WhatsApp "Instant · Free" vs Domestic SMS "Backup"), and "Get Verification Code" button. Middle: `─── OR CONTINUE WITH ───` subtle divider. Bottom: Social login button group (Google, Apple, Email) and terms disclaimer. |
| **Social Sign-In Group** | 1. **Continue with Google**: White surface, official Google `G` emblem, calls `signInWithGoogle()`<br>2. **Continue with Apple**: Dark `#0F172A` surface, official `` Apple glyph, calls `signInWithApple()`<br>3. **Continue with Email**: White surface with envelope icon, triggers inline email entry dialog/sheet, calls `signInWithEmail(email)` |
| **Nepal Mobile Linking Invariant** | Users authenticating via Google, Apple, or Email receive a subsequent prompt to attach their 10-digit Nepal mobile number (+977) so food delivery riders and taxi drivers can contact them during active dispatches |

## Screen 3 — Home
| | |
|---|---|
| **Purpose** | Hub: search, jump to services, promos, recent activity |
| **Layout** 👁 | Top: compact wordmark (left) + profile avatar (right) → universal search field → 5×2 shortcut grid → promo card (gradient, arrow button) → **Recent** (+ "See All") list → bottom nav |
| **Components** | TopBar, Avatar 40, SearchField, ServiceTile ×10, PromoBanner, SectionHeader, ListRow ×n, BottomNav |
| **Tokens** | `background.main`/`subtle`, `surface.raised`, `border.default`, `gradient.promoCard`, `text.*` |
| **Spacing** 🧭 | Section gap 24; grid 5 cols × 12 gap (≥ 360 px); < 360 → 4 cols; promo height ≈ 128 |
| **Interactive** | Search → full-screen search with recents & voice (AI concierge entry 🧭); tile → service; promo → deep link; avatar → Profile; row → detail |
| **States** | Loading: skeleton for promo + rows; empty Recent: friendly empty state; offline: banner "You're offline" + cached data; long names ellipsis (1 line) |
| **A11y** | Tiles: `button` with label; grid order = visual order; promo text readable ≥ 4.5 (use ≥ 18 px semibold on gradient); avatar label "Profile" |
| **Notes** | Dark preview exists in reference ⚠ no spec; ship light first. Recent = orders/chats/bookings, **not** wallet transactions. |

## Screen 4 — Chat
| | |
|---|---|
| **Purpose** | 1:1 encrypted social messaging, order/ride coordination, voice notes, stickers, and media |
| **Layout** 👁 | Top bar: in-app back `<`, avatar, name + "Online", wallpaper button (`Palette` / `photo.artframe`), call button (right); dynamic wallpaper container with photo/gradient and 18–20% dark scrim; message list: received bubbles, sent bubbles, location preview cards, borderless floating stickers/animated emojis; bottom composer: emoji sheet toggle (`Mood`), pill input *Type a message…*, attachment button (`+`), circular send button, and adjacent voice note mic button |
| **Components** | `ChatRoomScreen` / `ChatRoomView`, `TopBar(chat)`, `MessageBubble`, `StickerBubble`, `VoiceNotePlayer`, `LocationCard`, `Composer`, `WallpaperContainer` |
| **Tokens** | `status.success` (online dot), `surface.default` (received bubble), `action.secondaryPressed` / `brand.primary` (sent bubble), `overlay.scrim` (20% wallpaper dark scrim) |
| **Spacing** 🧭 | Bubble gap 4 (same sender) / 12 (change); side inset 16; bubble padding 12×14; sticker max-size 150×150 |
| **Interactive** | Call → `CallScreen` (WebRTC); wallpaper button → `WallpaperPickerSheet`; emoji button → `EmojiStickerSheet`; mic button (hold/lock) → record voice note; attach: camera, gallery, location, file; sticker tap → send; long-press: reply/copy/delete |
| **States** | Sending (clock icon) · sent ✓ · delivered ✓✓ · read ✓✓ blue; typing indicator; E2EE banner "Messages are end-to-end encrypted"; wallpaper loading & cached |
| **A11y** | Each bubble labelled "You said … 10:42" / "Aarav said …"; sticker bubbles announce alt description; composer min 48; live region for incoming messages |
| **Notes** | Partner (rider/merchant) threads show verified badge; stickers render without rectangular border; custom wallpapers apply 18–20% scrim for bubble legibility. |

## Screen 4.1 — WebRTC 1:1 Voice & Video Call
| | |
|---|---|
| **Purpose** | Real-time encrypted VoIP voice and video calls between customers and contacts or drivers |
| **Layout** | Full-screen immersive call view; top: in-app back / minimize button, encryption indicator 🔒, call duration timer (`02:45`); center: peer avatar with pulsating animated ripple rings (Voice) or full-screen remote camera feed with floating draggable PIP of local camera (Video); bottom: floating horizontal glassmorphic action bar with mic mute, speaker toggle, video/camera flip, and 56 px red circular End Call CTA |
| **Components** | `CallScreen` / `CallView`, `AvatarRippleVisualizer`, `DraggablePipVideo`, `CallControlBar`, `DurationTimer` |
| **Interactive** | Mic toggle (mute/unmute), speaker toggle (earpiece/speakerphone), video toggle (enable/disable local camera), camera flip (front/back), end call (terminates WebRTC session & logs to `call_logs`) |
| **States** | `Initiating` → `Ringing` → `Connected` → `Ended` / `Declined` / `Missed`; reconnecting overlay on network drop |
| **A11y** | Call controls labeled "Mute microphone", "Toggle speakerphone", "End call"; duration announced at periodic intervals |

## Screen 4.2 — Emoji & Sticker Sheet
| | |
|---|---|
| **Purpose** | WhatsApp-grade expressive picker for categorized Unicode emojis, Google Noto animated emojis, and Twemoji stickers |
| **Layout** | 3-tab bottom sheet modal (50–55% height); top drag handle and search/tab bar: `Emojis`, `Animated`, `Stickers`; tab 1 displays 5 category chips (`Smileys`, `People`, `Food`, `Activities`, `Objects`) with 7-col grid; tab 2 displays Google Noto animated GIFs in 4-col grid; tab 3 displays Twemoji SVG stickers in 4-col grid |
| **Components** | `EmojiStickerSheet` / `EmojiStickerSheetView`, `CategoryChipRow`, `EmojiGrid`, `AsyncImageGrid` |
| **Interactive** | Tab switch with indicator slide; category chip filter; tap emoji/sticker to send immediately into active conversation |
| **Assets** | Zero paid SDKs: Google Noto Animated Emojis (Apache 2.0 license), Twitter Twemoji (CC-BY 4.0 license), Unicode 15.0 |

## Screen 4.3 — Chat Wallpaper Customizer
| | |
|---|---|
| **Purpose** | Per-conversation chat background personalization with preset palettes and custom gallery photos |
| **Layout** | Modal bottom sheet; title "Chat Wallpaper" with reset option; "My Photos" gallery launcher card (camera/photo icon + "Choose from Gallery"); 12 preset tiles in a 3-col grid (WhatsApp Warm Emerald, Classic Doodle, Navy, Midnight Violet, Sunset Amber, Rose Quartz, Slate Glass, etc.); selected tile features white checkmark badge |
| **Components** | `WallpaperPickerSheet` / `WallpaperPickerView`, `PresetTileGrid`, `GalleryPhotoPickerTile` |
| **Interactive** | Tap preset → instant wallpaper update; tap "My Photos" → launches native device photo picker (`GetContent` / `PhotosPicker`); persist via `set_chat_wallpaper()` RPC |
| **Legibility Scrim** | Mandatory 18–20% dark tint scrim applied automatically over custom photos so white text and bubbles remain easily readable |

## Screen 5 — Payments (EXCLUDED)
The reference shows balance card, Send/Receive/Scan/Add Money, transactions. **Not implemented** (no in-app wallet, see payments architecture). Replacement 🧭: the **Orders/Activity** tab = a filterable ListRow list (Orders · Bookings · Rides) with status chips and a receipt detail page. Receipts show line items, fees, voucher discount, and *payment method used* (Fonepay/eSewa/Khalti/Card/COD) as plain text. Amount formatting & status-with-icon rules from the typography/catalog docs still apply.

## Screen 6 — Shop
| | |
|---|---|
| **Purpose** | Browse & buy goods (food/grocery/retail per vertical) |
| **Layout** 👁 | Title *Shop* + filter icon; search field *Search products…*; chip row (All, Electronics, Fashion, Home); offer banner *Up to 50% Off — Top Brands*; 2-col product grid (image, name, price) |
| **Components** | TopBar, SearchField, Chips, PromoBanner, ProductCard |
| **ProductCard** 🧭 | Image 1:1 (radius `lg`, `surface.muted` placeholder), name `cardTitle` 2 lines, price `amount` + optional strikethrough `caption`, add button / quantity stepper (Blinkit-style) bottom-right |
| **Spacing** 🧭 | Grid 2 col gap 12, side 20; card padding 12 |
| **Interactive** | Chip filter; card → detail; stepper add/remove; filter sheet; cart badge on top bar |
| **States** | Out of stock (greyed + label), loading skeleton, no results empty state, error retry, price change toast |
| **A11y** | Price announced with currency; stepper buttons labelled "Add one …"; images have alt |
| **Notes** | Cart-based checkout for Shop/Food/Mart; Hotels = direct checkout (no cart). |

## Screen 7 — Travel → "Stays" (evrry adaptation)
| | |
|---|---|
| **Purpose** | Reference: flight search. evrry: **Hotel/Stay search** (+ Rides entry) |
| **Layout** 👁 | Back + title; segmented tabs (Flights/Hotels/Trains/Bus, "Flights" selected); stacked form (From, To, Date, Passengers); full-width **Search** button; **Popular Destinations** cards (Goa, Bali, Dubai) |
| **evrry mapping** 🧭 | Tabs: **Stays · Rides · Rooms · Rentals**; Stays form: *Where* (palika/city), *Check-in – Check-out* (date-range picker), *Guests & rooms*; destinations: Kathmandu, Pokhara, Chitwan… |
| **Components** | TopBar, SegmentedTabs, FormField ×3, PrimaryButton, DestinationCard |
| **DestinationCard** | 3 across/horizontal scroll; image 4:5, radius `xl`, name overlay on bottom gradient scrim (white text ≥ 4.5) |
| **States** | Button disabled until required fields valid; date validation errors inline; no-results state; booking CTA loading → direct to payment (no cart) |
| **A11y** | Tabs `role=tablist`; date picker keyboard accessible; field errors announced |

## Screen 8 — Services
| | |
|---|---|
| **Purpose** | Grid of service categories |
| **Layout** 👁 | Title *Services* (+ filter/grid icon); 3-col grid of tiles with icon + label: Bills, Recharge, DTH, Electricity, Gas, Water, Insurance, Health, Education |
| **evrry mapping** 🧭 | Food, Mart, Rides, Stays, Rooms, Rentals, Vouchers, Referrals, Help (bill-pay items are **not** in scope) |
| **Dimensions** | Tile = ServiceTile (56 px icon container) in a 3-col grid, gap 12 (16 on ≥ 430), label centred `navLabel`, max 2 lines |
| **States** | Pressed `primarySoft`; selected (if filter use) 2 px blue; "Coming soon" tiles `text.disabled` + badge |
| **A11y** | Tile = button with label; grid reading order row-wise; focus ring visible on keyboard |

## Screen 9 — Profile & More
| | |
|---|---|
| **Purpose** | Account, preferences, help |
| **Layout** 👁 | Header: avatar 56, name, phone **(masked in UI per privacy rule)**; grouped menu: My Profile, Payment Methods, Security, Notifications, Help & Support, About EVRRY; chevrons |
| **evrry additions** 🧭 | `@handle` + **My QR** button, Vouchers & Referrals, Leagues/Stamp card, Privacy (find-by-phone toggle), Language; "Payment Methods" = saved cards/links only (no balance) |
| **Components** | ProfileHeader, MenuRow ×n (56 px), grouped card, Toggle (52×32) |
| **Spacing** | Header padding 20; group card radius `xl`; row padding 16 |
| **States** | Unverified badge, logged-out → Sign-in CTA, update-available badge on About |
| **A11y** | Each row a `button` with label; toggles announce state; sign-out confirm dialog with destructive text `errorText` |

## Screen 10 — Home AI Voice Concierge (Flagship Home Agent)
| | |
|---|---|
| **Purpose** | Voice-first multimodal concierge for instant everyday ordering, ride bidding, and queries |
| **Layout** | Top bar with ChatGPT-style minimal switcher `[ Ellie ▾ ]` and profile icon; central pulsating **Voice Waveform Orb** (size 140–160 px); transcript subtitle bubble; bottom quick chips ("🥟 Momo Order", "🛵 Fast Bike", "🛒 Milk & Bread", "🏨 Hotel Room"); bottom navigation |
| **Minimal Switcher** | Centered pill `[ Ellie ▾ ]` opening a sleek bottom sheet displaying the 4 models: **Ellie** (Everyday guide), **Rony** (Executive concierge), **Jenny** (Hospitable companion), **Suka** (Calm night assistant) |
| **Zero Language Pills** | Deliberately omits manual language pills. The AI engine automatically mirrors the user's spoken or typed language (**Nepali, Hindi, Maithili, English**) |
| **Voice Orb States** | `idle` (gentle breathing scale 1.0), `listening` (pulsating audio wave reactive), `thinking` (spinning aura glow), `speaking` (harmonious multi-ring wave) |
| **Components** | `HomeAgentScreen` / `HomeAgentView`, `VoiceOrb`, `ModelSwitcherSheet`, `QuickChipsRow` |
| **A11y** | Voice button `accessibilityLabel="Microphone input"`; live transcription region for deaf/hard-of-hearing users |

## Screen 11 — Nepal Phone Auth & WhatsApp OTP
| | |
|---|---|
| **Purpose** | Fast, frictionless Nepal mobile sign-in with instant WhatsApp delivery & SMS fallback |
| **Layout** | Step 1: `+977` prefix, 10-digit input field, WhatsApp/SMS toggle, "Send Verification Code" CTA.<br>Step 2: Top bar with in-app back `<`; title "Verify Code"; masked phone number; 6 individual discrete digit boxes; 45s cooldown countdown; "Resend Code" link |
| **In-App Back `<`** | Mandatory top-left back button inside `EvrryTopBar` ($\ge 44 \times 44\text{ pt}$) preventing iOS navigation traps |
| **Auto-Verification** | Enters 6th digit $\rightarrow$ triggers auto-verify immediately without requiring a confirm button tap |
| **Components** | `PhoneInputScreen` / `PhoneInputView`, `OtpVerifyScreen` / `OtpVerifyView`, `EvrryTopBar` |

## Screen 12 — Snapchat-Style Camera, Lenses & Real-Time QR Scanner
| | |
|---|---|
| **Purpose** | High-performance mobile camera viewfinder with real-time QR code decoding and AR lenses (100% trademark-compliant) |
| **Layout** | Fullscreen camera viewfinder surface; right-side vertical tool dock (Flip, Flash, Night, Grid, Lenses, QR toggle); top controls (close, active lens badge, demo QR trigger); center QR reticle with corner brackets; floating `QrScanCard`; bottom circular shutter button, horizontal lens carousel, and bottom-left Memories thumbnail shortcut |
| **Legal Protection** | Zero trademarked phrases ("Snapchat", "Snapcode", "Bitmoji", "My Eyes Only"). Cleanly designated as **Evrry Camera**, **Lenses**, and **Stories** |
| **Live QR Decoding** | Seamlessly decodes: 1) Fonepay & Nepal EMVCo QRs $\rightarrow$ "Pay with Fonepay" action; 2) Evrry Friend Contact QRs $\rightarrow$ "Open Chat" action; 3) Restaurant Dining Table QRs $\rightarrow$ "View Digital Menu"; 4) Web links $\rightarrow$ "Open Link" |
| **Components** | `CameraScreen` / `CameraView`, `QrScanCard`, `LensCarousel`, `CircularShutter` |

## Screen 13 — Memories Hub & My Locker (Private Vault)
| | |
|---|---|
| **Purpose** | 3-tab media repository for preserved camera snaps, device camera roll imports, and PIN-secured private vault |
| **Layout** | Top bar with back button and title "Memories"; 3-tab segment bar (`Snaps`, `Camera Roll`, `My Locker`); multi-column photo grid; fullscreen snap detail modal with location/filter tags, delete, and friend/story distribution buttons |
| **My Locker Security** | Discrete 4-digit passcode keypad (`LockerPinKeypad`) with 4-dot indicators; shake error animation on invalid entry (default PIN: `1234`); unlocked session header banner with instant "Lock Now" button; "Unhide / Restore" action |
| **Components** | `MemoriesScreen` / `MemoriesView`, `LockerPinKeypad`, `SnapDetailModal` |

## Screen 14 — Snap Review & Direct Friend Sharing
| | |
|---|---|
| **Purpose** | Immediate post-capture inspection, text caption overlay, story posting, and WhatsApp-grade encrypted chat sharing |
| **Layout** | High-resolution image preview; top bar with dismiss `X`, text caption button, and direct amber "Save to My Locker" button; interactive caption overlay; bottom action bar with "Save" (Memories), "Story" (24-hr status), and "Send To" (opens friend bottom sheet) |
| **Chat Integration** | 1-tap friend selection sheet immediately transmits captured photo snap with caption through WhatsApp-grade E2EE chat |
| **Components** | `SnapReviewScreen` / `SnapReviewView`, `SendToFriendSheet` |

---

## Cross-screen requirements
- Contrast ≥ 4.5:1 normal text, 3:1 large text & UI boundaries; fix via text-safe tokens, not by altering brand colours.
- Targets ≥ 48; text ≥ 12; support 200 % text scaling; respect reduced-motion.
- Status never by colour alone; errors are specific and actionable.
- Focus order = reading order; custom focus ring `border.focus`.
- Mock data in the reference is illustrative; NPR + Nepal-specific names/places replace INR/India examples.
- **Mandatory iOS In-App Back Button (`<`)**: Every pushed or modal screen must feature an in-app back navigation button to accommodate iOS hardware which has no physical or system back button.
