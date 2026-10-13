# EVRRY — Component Catalog

Tokens: [`UI_COLOR_TOKENS.json`](UI_COLOR_TOKENS.json). Scale/spacing: [`UI_TYPOGRAPHY_AND_SPACING.md`](UI_TYPOGRAPHY_AND_SPACING.md). Dimensions are **Decisions** unless marked *(Observed)*. States for every interactive component: default · pressed · focused · selected · disabled · loading · success · error (where relevant).

## Buttons
| | Primary | Secondary | Text/Link |
|---|---|---|---|
| Visual | `action.primary` bg, white `button` text, radius `lg`, height 52, full-width in forms *(Observed: full-width blue "Get Started" / "Search Flights")* | `surface.primarySoft` bg, `brand.primary` text | `brand.primary`, 600, no bg *(Observed: "Sign In", "See All")* |
| Pressed | `action.primaryPressed`, scale .98 | `secondaryPressed` | underline/opacity .7 |
| Focused | 2 px `border.focus` ring, offset 2 | same | same |
| Disabled | `primaryDisabled` bg/fg, no ripple | 40 % opacity | `text.disabled` |
| Loading | label replaced by 20 px spinner, width locked, taps ignored | same | — |
| Success/Error | brief check/ ! icon then revert; error also shows inline message | — | — |

## Inputs & search
Height 52, radius `lg`, bg white, 1 px `border.strong` (3:1), 16 px text, label above (`inputLabel`), leading 20 px icon *(Observed: search field with magnifier + placeholder "Search anything…")*. Focus: 2 px `brand.primary`. Error: 1.5 px `errorText` border + icon + message below in `errorText` (never colour only). Disabled: `surface.muted` bg. **Universal search** field is tappable (opens search screen) on Home.

## Service shortcut tile *(Observed)*
56 px square tile, radius `xl`, bg white/`surface.raised`, 1 px `border.default`, outline icon 24–28 px `brand.primary`; label `navLabel` `text.primary` 8 px below, centred, max 2 lines. Pressed: `surface.primarySoft`. Selected (Services grid): 2 px `brand.primary` border + `primarySoft`. Min target = tile + label (≥ 48 px).

## Bottom navigation *(Observed: 5 items, icon + label, selected = blue)*
64 px + inset, white, 1 px top border `border.default`, `zIndex.bottomNav`. Icon 24, label 12 `navLabel`. Selected: `brand.primary` filled icon + semibold label; unselected: `text.secondary` outline. Badge: 8 px `status.error` dot / count pill (≤ 99+). No horizontal scrolling; no more than 5 items.

## Tabs & category chips *(Observed: Flights/Hotels/Trains/Bus; All/Electronics/Fashion/Home)*
Chip height 36, radius `full`, `navLabel`/`bodySmall` 500. Unselected: `surface.default` + `text.primary`; selected: `brand.primary` bg + white (or `primarySoft` + blue text). Horizontal scroll, 8 px gap, 20 px leading inset, snap not required.

## Cards & containers
White (`surface.raised`) on `background.subtle`, radius `xl`, padding 16, 1 px `border.default`, `shadow.card` optional. Tappable cards: pressed `surface.muted`, full-card hit area, `role=button/link`.

## Promotional banner *(Observed: blue gradient card "Everything you need. One place." with circular arrow button; "Up to 50% Off — Top Brands")*
`promoCard` gradient, radius `xl`, padding 20, white `sectionHeading`/`display`-lite text, circular 40 px white button with blue arrow at end. White text on the gradient's lightest stop is 3.72:1 → keep headline ≥ 18 px semibold. Decorative wave art `accentLight` at ≤ 40 % opacity behind, text never over busiest region.

## List / transaction-style row (use for Recent, Orders, Activity)
Height ≥ 64, padding 12×16; 40–44 px circular leading avatar/brand logo; title `cardTitle`, subtitle `bodySmall` `text.secondary`, trailing caption (time) `caption` and optional trailing amount `amount`. Divider 1 px `border.default` inset to text. Status = icon + word (e.g. ✔ "Delivered") — colour is additive. Amount sign always explicit.
> Visual pattern only. Do **not** implement wallet credits/debits.

## Avatar
Circular; 32 (list-compact) / 40 (rows, chat header) / 56 (profile) / 80 (profile header); initials fallback on `primarySoft` with `brand.primary` text; online dot 10 px `status.success` + 2 px white ring (also expose "online" to a11y).

## Chat bubbles *(Observed)*
Received: `surface.default`/white + 1 px border, left, radius 16 (bottom-left 4). Sent: `brand.primary` bg, white text, right, radius 16 (bottom-right 4) *(reference sent bubbles appear light-blue tint — see conflict below)*. Max width 78 %. Timestamp `caption`. Location preview card: map thumbnail 16:9, radius 12, title + address, tap → map. Composer: pill input (height 48) + emoji trigger icon + attachment icon + circular 40 px blue send button; WhatsApp-style microphone icon beside send button when input is empty.
- **Voice Notes**: Horizontal waveform visualizer (28 bars), 32 px play/pause button, duration counter `0:00`, and sent/read status indicator.
- **Borderless Floating Stickers & Animated Emojis**: Rendered as borderless, transparent floating elements (140–160 dp size) with no rectangular speech bubble border, colored background, or drop shadow. Floating translucent timestamp pill (`rgba(0,0,0,0.45)`) positioned in the bottom-right corner.
> **Reference ambiguity**: sent bubble looks light-blue (`#DBEAFE`-ish) with dark text, not solid blue. **Provisional**: use `action.secondaryPressed` (`#DBEAFE`) bg + `text.primary`; solid-blue variant is the fallback. Decide with designer.

## Bottom sheet & dialog
Sheet: white, top radius `xxl`, 4×36 drag handle, padding 20, `shadow.sheet`, scrim `overlay.scrim`, max height 90 %. Dialog: radius `xl`, width ≤ 320, title `sectionHeading`, body `body`, buttons stacked (primary then text). Focus trapped; Esc/back closes unless destructive in-flight.

## Dropdowns / menus
Radius `lg`, white, `shadow.raised`, item height 48, selected item `primarySoft` + check icon. Native pickers preferred on mobile.

## Menu row (Profile & More) *(Observed)*
Height 56, leading 20–24 px blue outline icon, label `body`, trailing chevron 16 `text.secondary`, dividers 1 px, grouped in a white card radius `xl`.

## Travel/Stays search form *(Observed: stacked fields + full-width "Search Flights")*
Stacked field rows (52) with leading icon, 12 px gap; single primary CTA. For evrry map to **Stays** (location, dates, guests) — see screen spec.

## Empty states
Centred 120 px line illustration (accentLight/blue outline), `sectionHeading` title, `bodySmall` secondary text, one primary or secondary action. Never blame the user.

## Loading
Skeleton blocks `surface.muted` (shimmer 1.2 s, disabled under reduced-motion) for lists/cards; 24 px spinner `brand.primary` for in-button/refresh. Splash progress is implicit (no bar).

## Toasts & banners
Toast: bottom, above nav, radius `lg`, `text.primary` bg `#0B172A` + white text (dark toast), 4 s auto-dismiss, optional action; icon + text for success/error. Inline banner: `primarySoft` / error-tint bg with icon, persistent until resolved. Announce via live region.

## Success & error patterns
Success: 56 px circle `status.success` fill + white check, title, subtitle, primary CTA. Error: `status.error` fill + `!`, **plain-language** message, next step, retry. Field errors inline.

## Payment-method selector (evrry-specific — not in reference)
List of radio rows (Fonepay QR · eSewa · Khalti · Card · Cash on Delivery), each = menu-row pattern with provider logo 32 px, selected = 2 px `brand.primary` border + `primarySoft`. Fonepay QR screen: white card, QR centred 240 px min, amount `amountLarge`, remark `caption`, countdown; QR blurred (overlay) when scanned. Uses only tokens above.

## Top App Bar with In-App Back `<` (`EvrryTopBar`)
Height 56–64 px, brand navy `#0E1B38` or clean surface `#F8FAFC`. Contains an explicit top-left in-app back button (`<` arrow/chevron, minimum $44 \times 44\text{ pt}$ hit target) on all pushed or modal screens to ensure seamless navigation on iOS devices which lack a hardware back button. Centered or left-aligned title (`cardTitle`), optional trailing action/profile button.

## ChatGPT-Style Minimal Model Switcher
Minimalist capsule pill button `[ Ellie ▾ ]` positioned in the top bar. Height 36 px, rounded full (`rounded-full`), subtle border, chevron down icon. Tapping opens a bottom drawer listing the 4 personas (**Ellie**, **Rony**, **Jenny**, **Suka**) with personality tags and selection checks. Zero manual language pills; engine automatically mirrors the user's spoken language.

## Voice Waveform Orb
Central circular visualizer (diameter 140–160 px) rendered on the Home Agent screen. Responds to 4 lifecycle states:
- `idle`: Gentle breathing animation (scale 1.0 $\longleftrightarrow$ 1.05) with soft emerald/blue glow.
- `listening`: Real-time audio reactive ripple rings expanding outward.
- `thinking`: Rotating radiant aura gradient indicating LLM processing.
- `speaking`: Harmonious multi-concentric pulsing waves synced with audio output.

## 6-Box OTP PIN Input
Row of 6 discrete square boxes (44–48 px width/height, rounded 12 px, 8 px gap). Active box features 2 px blue border highlight (`EvrryBlue`). Entering a digit advances focus to the next box; typing the 6th digit automatically triggers verification without requiring a confirm button tap. Includes a 45-second animated cooldown resend link.

## Brand Animated Splash (`SplashScreen` / `SplashView`)
Hardware-accelerated 1.5-second brand introduction. Features a centered squircle brandmark badge (92×92 dp/pt, 26 dp radius, `#2563EB`) housing the bold "e" glyph, `evrry` wordmark with `🇳🇵` Nepal flag, and the tagline *"Nepal's Everything Super App"*. Includes 450 ms entrance scaling, 700 ms brand hold, and 350 ms exit crossfade with auto-routing to onboarding or login. Designed to execute within native GPU compositing pipelines to eliminate battery drain and device heating.

## DoorDash-Grade Dynamic Onboarding Carousel (`OnboardingScreen` / `OnboardingView`)
Interactive multi-slide onboarding walkthrough dynamically driven by `OnboardingRepository.defaultPages` across Nepal's 5 core verticals (Food, Grocery, Mobility, E2EE Chat, Stays/Rentals). Features:
- Smooth container background color crossfading synced with vertical theme colors.
- Central layered artwork card (240×240 dp/pt) with drop shadow and floating live metric badges (e.g. `⚡ 15 min · Lightning Dispatch`).
- Category badge, bold title, descriptive subtitle, and checked feature highlight chips.
- Persistent top-bar "Skip" action and bottom "Continue / Get Started with Evrry" CTA.

## Expanding Pill Page Indicator (`ExpandingPillIndicator`)
Dynamic horizontal pagination indicator row. Unselected pages render as compact 8×8 dp/pt circles (`#CBD5E1`), while the active page expands to a 24×8 dp/pt rounded pill utilizing spring animations (`tween` / `.spring(response: 0.3)`). Tapping any dot animates the pager directly to that slide.

## 1-Tap Social Sign-In Button Group (`SocialSignInButtonGroup`)
Stack of branded authentication buttons positioned beneath the primary mobile number verification field:
- **Continue with Google**: Pure white surface, subtle 1 px border, official Google `G` emblem in circular container.
- **Continue with Apple**: Premium `#0F172A` deep charcoal surface with official `` Apple icon.
- **Continue with Email**: White surface with outline envelope icon, opening an inline email dialog/sheet.
- All 1-tap options link to a subsequent Nepal phone binding prompt to guarantee driver/courier dispatch capability.

## Emoji & Sticker Bottom Sheet (`EmojiStickerSheet` / `EmojiStickerSheetView`)
Interactive 3-tab modal bottom sheet for WhatsApp-grade expressions:
- **Tab 1: Categorized Unicode Emojis**: 5 horizontal filter chips (`Smileys`, `People`, `Food`, `Activities`, `Objects`) filtering a 7-column responsive Unicode grid.
- **Tab 2: Google Noto Animated Emojis**: 4-column responsive grid displaying royalty-free open-source animated GIF assets (Apache 2.0 license) rendered with GPU acceleration.
- **Tab 3: Twitter Twemoji Stickers**: 4-column grid displaying royalty-free open-source SVG sticker assets (CC-BY 4.0 license).
- Dimensions: Height 50–55% screen height, top drag handle, white card background with dark mode support, tap-to-send action.

## Chat Wallpaper Picker Sheet (`WallpaperPickerSheet` / `WallpaperPickerView`)
Interactive bottom sheet modal allowing users to customize chat background appearance on a per-conversation basis:
- **12 Curated Presets**: Solid colors and vibrant gradients including WhatsApp Classic Doodle/Warm Emerald, Deep Navy, Midnight Violet, Sunset Amber, Rose Quartz, and Slate Glass.
- **"My Photos" Custom Device Gallery Selector**: Dedicated tile with camera/photo icon triggering native system photo pickers (`ActivityResultContracts.GetContent()` on Android, `PhotosPicker` from `PhotosUI` on iOS, or file reader on Web).
- **Automatic 18–20% Dark Scrim Invariant**: Custom photos automatically receive a dark scrim overlay (`Color.Black.copy(alpha = 0.20f)` / `linear-gradient(rgba(0,0,0,0.18), rgba(0,0,0,0.18))`) to guarantee that white message text and incoming/outgoing bubbles remain crisp and legible regardless of photo brightness.
- Active wallpaper state marked with a white checkmark badge on the selected tile.

## WebRTC 1:1 Voice & Video Call Screen (`CallScreen` / `CallView`)
Full-screen calling experience for encrypted VoIP sessions:
- **Voice Mode**: 110 px circular contact avatar with pulsating multi-ring ripple visualizer, live status label (`Calling...`, `Ringing...`, `Connected`), and elapsed call duration timer `00:00`.
- **Video Mode**: Full-screen remote participant camera stream with draggable floating local video picture-in-picture (PIP) tile.
- **Control HUD**: Floating horizontal glassmorphism bar containing Microphone mute toggle, Speakerphone switch, Camera toggle/flip, and circular 56 px red End Call CTA.

## Camera Viewfinder & Lens Carousel (`CameraScreen` / `CameraView`)
Fullscreen camera experience inspired by Snapchat while maintaining 100% trademark compliance:
- **Vertical Tool Dock (Right Side)**: Semi-transparent pill containing Camera Flip (`arrow.triangle.2.circlepath`), Flash (`bolt.fill`), Night Mode (`moon.stars.fill`), Rule-of-Thirds Grid (`grid`), Lenses (`sparkles`), and QR Scanner toggle (`qrcode.viewfinder`).
- **Circular Shutter Button**: 80 px outer ring with a 66 px inner capture core. Tap triggers immediate photo snapshot; long press triggers animated recording state.
- **Filter & Lens Carousel**: Horizontal scrolling selector with live color grading overlays (Normal, Kathmandu Gold, Everest Ice, Vintage 90s, Cyberpunk Neon, Noir B&W).
- **Memories Shortcut Thumbnail**: 46×46 dp/pt rounded preview on the bottom-left displaying the latest snap thumbnail with direct navigation into Memories.

## Real-Time QR Scanner Reticle & Action Card (`QrScanCard`)
Camera-integrated QR recognition HUD matching modern smartphone and Snapchat scan behavior:
- **Animated Center Reticle**: 220×220 dp/pt corner brackets with dashed scanning lines and green confirmation check on detection.
- **Floating Action Card**: Glassmorphism surface `#0B132B` (92% opacity) with white border, displaying:
  - Contextual icon (`creditcard.fill` for Fonepay, `person.crop.circle.fill` for Evrry user, `fork.knife` for table service, `globe` for URLs).
  - Title and formatted subtitle (e.g., `Himalayan Java Coffee · Rs 350`, `Roadhouse Cafe · Table #4`).
  - High-contrast action CTA pill (`Pay with Fonepay`, `Open Chat`, `View Digital Menu`, `Open Link`).

## Memories 3-Tab Hub (`MemoriesScreen` / `MemoriesView`)
Central photo and memory archive organizing saved media across three distinct categories:
- **Tab 1: Snaps**: 2-column grid of camera snaps with date stamps, Nepal location tags, and filter badges. Tapping opens the fullscreen viewer modal.
- **Tab 2: Camera Roll**: Multi-column grid showcasing device photo library captures with instant photo import.
- **Tab 3: My Locker (Private Vault)**: Isolated encrypted storage tab protected by a 4-digit PIN lock screen.

## My Locker PIN Keypad (`LockerPinKeypad`)
Secure numeric entry keypad for accessing private vault media:
- **4-Dot Indicator**: Dynamic dot circles reflecting entered passcode digits (0–4).
- **Numeric Matrix**: Circular 68 px touch targets (digits 0–9, backspace, and clear).
- **Haptic & Visual Error Feedback**: Red flash and shake animation on invalid PIN entry (default testing PIN: `1234`).
- **Unlocked Session Banner**: "Private Vault Unlocked 🔒" banner with 1-tap "Lock Now" button.

## Snap Review & Friend Share Screen (`SnapReviewScreen` / `SnapReviewView`)
Post-capture review interface allowing immediate enhancement and distribution:
- **Text Caption Overlay**: Tap-to-edit translucent text pill with centered keyboard input and quick "Done" action.
- **Direct Save to My Locker**: Top-right amber lock button saving directly into the private vault with 1 tap.
- **Bottom Action Deck**: 1-tap "Save to Memories", "Post to Story" (publishing to 24-hr status row), and "Send To".
- **Send to Friend Bottom Sheet**: Instant picker listing WhatsApp-grade chats with 1-tap transmission via `sendImage`.

## Consistency rules
1. One primary button per screen section. 2. No raw hex. 3. Icons: one library, outline. 4. Radii from the scale only. 5. Status never colour-only. 6. Touch targets ≥ 48. 7. Text ≥ 12. 8. In-App back `<` button mandatory on all nested views for iOS navigation parity. 9. New components must be added here before use.


