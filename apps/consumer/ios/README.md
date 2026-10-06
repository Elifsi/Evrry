# apps/consumer/ios — evrry Native Consumer iOS App

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

// 2. Push Notifications via Firebase Messaging (APNs Bridge)
.package(url: "https://github.com/firebase/firebase-ios-sdk.git", from: "10.23.0")

// 3. Native Maps & Location
// GoogleMaps SPM or MapKit integration
// CoreLocation: Native iOS hardware GPS location provider with background navigation

// 4. Audio Streaming & Voice Processing
// AVFoundation: Low-level audio recording via AVAudioEngine and live voice playback node
```

---

## 2. Architecture & Design Patterns

- **Architecture**: Clean MVVM Architecture with `@Observable` (iOS 17+) or `@StateObject` ViewModels.
- **UI Framework**: Declarative SwiftUI adhering to Apple Human Interface Guidelines (HIG).
- **Concurrency**: Native Swift Concurrency (`async/await`, `Task`, `AsyncSequence`).
- **Realtime Channels**: Direct consumption of Supabase Realtime channel streams:
  ```swift
  for await change in channel.postgresChange(AnyAction.self, schema: "public") {
      // update state
  }
  ```
- **Live 3D Vehicle Tracking**: Google Maps iOS SDK / MapKit renders high-fidelity **3D isometric vehicle markers (Scooter, Motorcycle, and Cab)** with smooth 360° heading rotation along OSRM road polylines and ground drop-shadows.
- **Zero Google Directions Fees**: Free Google Maps SDK / MapKit for map viewport; polylines, ETAs, and turn-by-turn geometry fetched from self-hosted OSRM endpoints (`supabase/functions/routing`).
- **Mandatory In-App Back Navigation**: Top-left back button (`44x44 pt` minimum tap target) on every nested screen to prevent iOS navigation traps (see [`docs/architecture/unified-mobile-ui-and-ios-navigation.md`](file:///home/rahul/codes/Evrry/docs/architecture/unified-mobile-ui-and-ios-navigation.md)).

---

## 3. Reference Architecture Alignments

- **WhatsApp Chat UI**: Follows [`efxlve/whatsapp-clone`](https://github.com/efxlve/whatsapp-clone) for swipe-to-reply, thread grouping, media sheets, and contact lists.
- **Camera & Lenses**: Follows [`Snapchat/camera-kit-ios-sdk`](https://github.com/Snapchat/camera-kit-ios-sdk) for AVFoundation capture pipelines.
- **Payment Handling**: Web redirection and deep-link callback handlers for Khalti (`/api/v2/epay/initiate/`) and eSewa HMAC-SHA256 signatures.

---

## 4. Reference Prototype

Refer to the working interactive phone prototype at [`prototype/Phone/`](../../../prototype/Phone/) for mobile smartphone feature workflows, state machines, and UX specifications. Note: `prototype/Phone/` is strictly a **mobile phone UI/UX golden master**, not a desktop web app.

> ⚠️ **Security Reminder**: Never place Supabase `service_role` keys, private gateway secrets, or merchant keys into Xcode configurations, `Info.plist`, or app bundles.

