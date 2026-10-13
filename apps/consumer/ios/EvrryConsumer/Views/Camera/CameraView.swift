import SwiftUI

public struct CameraView: View {
    @ObservedObject public var viewModel: CameraViewModel
    public var onBack: () -> Void
    public var onOpenMemories: () -> Void
    public var onCaptureComplete: (String, String) -> Void
    public var onNavigateAction: (String) -> Void

    @State private var isFrontCamera: Bool = false
    @State private var isFlashOn: Bool = false
    @State private var isNightMode: Bool = false
    @State private var showGrid: Bool = false
    @State private var showLenses: Bool = false
    @State private var isQrScannerActive: Bool = true
    @State private var showDemoQrMenu: Bool = false
    @State private var isRecordingVideo: Bool = false
    @State private var recordingProgress: Double = 0.0

    private let sampleQrPresets = [
        ("Fonepay Merchant (Himalayan Java)", "00020126580014np.com.fonepay0112HIMALAYANJAVA5204541153035245406350.005802NP5921Himalayan Java Coffee6009Kathmandu"),
        ("Evrry Friend Contact (@aayush)", "evrry://user/@aayush"),
        ("Restaurant Table Menu (#4)", "evrry://restaurant/roadhouse?table=4"),
        ("Evrry Super App Web", "https://evrry.com")
    ]

    public init(
        viewModel: CameraViewModel,
        onBack: @escaping () -> Void,
        onOpenMemories: @escaping () -> Void,
        onCaptureComplete: @escaping (String, String) -> Void,
        onNavigateAction: @escaping (String) -> Void
    ) {
        self.viewModel = viewModel
        self.onBack = onBack
        self.onOpenMemories = onOpenMemories
        self.onCaptureComplete = onCaptureComplete
        self.onNavigateAction = onNavigateAction
    }

    public var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()

            // 1. Mock Camera Viewfinder Surface
            viewfinderSurface

            // 2. Rule-of-Thirds Grid Overlay
            if showGrid {
                gridOverlay
            }

            // 3. Center QR Scanning Target Reticle
            if isQrScannerActive {
                qrReticleOverlay
            }

            // 4. Floating QR Detection Glassmorphism Card
            if let detected = viewModel.detectedQr {
                VStack {
                    Spacer()
                    detectedQrCard(detected: detected)
                        .padding(.horizontal, 20)
                        .padding(.bottom, 160)
                }
                .transition(.scale.combined(with: .opacity))
            }

            // 5. Top Controls (Close, Flash Indicator, Lens Name)
            topBarOverlay

            // 6. Right Vertical Tool Dock (Flip, Flash, Night, Grid, Lenses, QR)
            rightToolDock

            // 7. Bottom Lens Carousel + Shutter + Memories Shortcut
            bottomControlsOverlay
        }
        .sheet(isPresented: $showDemoQrMenu) {
            demoQrPickerSheet
        }
    }

    // MARK: - Viewfinder Surface
    private var viewfinderSurface: some View {
        GeometryReader { geo in
            ZStack {
                // High-Resolution Nepal Viewfinder Backdrop
                AsyncImage(url: URL(string: viewfinderImageForLens)) { phase in
                    if let image = phase.image {
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: geo.size.width, height: geo.size.height)
                            .clipped()
                    } else {
                        Rectangle()
                            .fill(LinearGradient(
                                colors: [Color(red: 0.1, green: 0.12, blue: 0.18), Color.black],
                                startPoint: .top,
                                endPoint: .bottom
                            ))
                    }
                }
                .overlay(
                    lensColorGradingOverlay
                )

                // Top & Bottom Subtle Vignette for Snapchat Aesthetic
                VStack {
                    LinearGradient(
                        colors: [Color.black.opacity(0.65), Color.clear],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                    .frame(height: 120)

                    Spacer()

                    LinearGradient(
                        colors: [Color.clear, Color.black.opacity(0.75)],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                    .frame(height: 160)
                }
                .ignoresSafeArea()
            }
        }
    }

    private var viewfinderImageForLens: String {
        switch viewModel.selectedLens.id {
        case "gold":
            return "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=900&auto=format&fit=crop&q=80"
        case "everest":
            return "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=900&auto=format&fit=crop&q=80"
        case "cyber":
            return "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=900&auto=format&fit=crop&q=80"
        case "vintage":
            return "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=900&auto=format&fit=crop&q=80"
        default:
            return "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=900&auto=format&fit=crop&q=80"
        }
    }

    private var lensColorGradingOverlay: some View {
        Group {
            switch viewModel.selectedLens.id {
            case "gold":
                Color.yellow.opacity(0.18).blendMode(.overlay)
            case "everest":
                Color.cyan.opacity(0.2).blendMode(.color)
            case "cyber":
                Color.purple.opacity(0.25).blendMode(.overlay)
            case "vintage":
                Color.orange.opacity(0.15).blendMode(.multiply)
            case "noir":
                Color.black.opacity(0.4).saturation(0.0)
            default:
                Color.clear
            }
        }
    }

    // MARK: - Rule-of-Thirds Grid
    private var gridOverlay: some View {
        GeometryReader { geo in
            ZStack {
                // Horizontal lines
                Path { path in
                    let h1 = geo.size.height / 3
                    let h2 = geo.size.height * 2 / 3
                    path.move(to: CGPoint(x: 0, y: h1))
                    path.addLine(to: CGPoint(x: geo.size.width, y: h1))
                    path.move(to: CGPoint(x: 0, y: h2))
                    path.addLine(to: CGPoint(x: geo.size.width, y: h2))
                }
                .stroke(Color.white.opacity(0.25), lineWidth: 0.8)

                // Vertical lines
                Path { path in
                    let w1 = geo.size.width / 3
                    let w2 = geo.size.width * 2 / 3
                    path.move(to: CGPoint(x: w1, y: 0))
                    path.addLine(to: CGPoint(x: w1, y: geo.size.height))
                    path.move(to: CGPoint(x: w2, y: 0))
                    path.addLine(to: CGPoint(x: w2, y: geo.size.height))
                }
                .stroke(Color.white.opacity(0.25), lineWidth: 0.8)
            }
        }
        .allowsHitTesting(false)
    }

    // MARK: - QR Reticle Overlay
    private var qrReticleOverlay: some View {
        VStack(spacing: 12) {
            ZStack {
                RoundedRectangle(cornerRadius: 24)
                    .stroke(
                        viewModel.detectedQr != nil ? Color.green : Color.white.opacity(0.4),
                        style: StrokeStyle(lineWidth: 2.5, dash: [20, 10])
                    )
                    .frame(width: 220, height: 220)

                // Reticle corner marks
                if viewModel.detectedQr != nil {
                    Image(systemName: "checkmark.circle.fill")
                        .foregroundColor(.green)
                        .font(.system(size: 36))
                }
            }

            Text(viewModel.detectedQr != nil ? "QR Code Recognized" : "Point at Fonepay or Evrry QR")
                .font(.system(size: 12, weight: .semibold))
                .foregroundColor(.white.opacity(0.85))
                .padding(.horizontal, 14)
                .padding(.vertical, 6)
                .background(Color.black.opacity(0.5))
                .clipShape(Capsule())
        }
    }

    // MARK: - Detected QR Floating Card
    private func detectedQrCard(detected: QrScanResult) -> some View {
        HStack(spacing: 14) {
            ZStack {
                Circle()
                    .fill(Color.evrryPrimaryBlue.opacity(0.2))
                    .frame(width: 44, height: 44)
                Image(systemName: qrIconName(type: detected.type))
                    .font(.system(size: 20))
                    .foregroundColor(.evrryPrimaryBlue)
            }

            VStack(alignment: .leading, spacing: 3) {
                Text(detected.title)
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(.white)
                Text(detected.subtitle)
                    .font(.system(size: 12))
                    .foregroundColor(.white.opacity(0.7))
                    .lineLimit(1)
            }

            Spacer()

            Button(action: {
                onNavigateAction(detected.target)
                viewModel.clearDetectedQr()
            }) {
                Text(detected.actionLabel)
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(.white)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 8)
                    .background(Color.evrryPrimaryBlue)
                    .clipShape(Capsule())
            }
        }
        .padding(14)
        .background(
            RoundedRectangle(cornerRadius: 20)
                .fill(Color(red: 0.08, green: 0.1, blue: 0.16).opacity(0.92))
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(Color.white.opacity(0.2), lineWidth: 1)
                )
        )
        .shadow(color: Color.black.opacity(0.4), radius: 10, y: 4)
    }

    private func qrIconName(type: String) -> String {
        switch type {
        case "fonepay": return "creditcard.fill"
        case "contact": return "person.crop.circle.fill"
        case "restaurant": return "fork.knife"
        case "url": return "safari.fill"
        default: return "qrcode"
        }
    }

    // MARK: - Top Controls
    private var topBarOverlay: some View {
        VStack {
            HStack {
                Button(action: onBack) {
                    Image(systemName: "xmark")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.white)
                        .frame(width: 40, height: 40)
                        .background(Color.black.opacity(0.5))
                        .clipShape(Circle())
                }

                Spacer()

                // Filter Badge Indicator
                if viewModel.selectedLens.id != "normal" {
                    Text(viewModel.selectedLens.name)
                        .font(.system(size: 13, weight: .bold))
                        .foregroundColor(.white)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 6)
                        .background(Color.black.opacity(0.55))
                        .clipShape(Capsule())
                        .overlay(
                            Capsule().stroke(Color.white.opacity(0.2), lineWidth: 0.8)
                        )
                }

                Spacer()

                // Demo QR Simulation Trigger
                Button(action: { showDemoQrMenu = true }) {
                    HStack(spacing: 4) {
                        Image(systemName: "qrcode.viewfinder")
                            .font(.system(size: 13, weight: .bold))
                        Text("Demo QR")
                            .font(.system(size: 11, weight: .bold))
                    }
                    .foregroundColor(.white)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 6)
                    .background(Color.evrryPrimaryBlue.opacity(0.8))
                    .clipShape(Capsule())
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 50)

            Spacer()
        }
    }

    // MARK: - Right Vertical Tool Dock
    private var rightToolDock: some View {
        HStack {
            Spacer()
            VStack(spacing: 16) {
                dockButton(
                    icon: "arrow.triangle.2.circlepath",
                    label: "Flip",
                    isActive: isFrontCamera,
                    action: { isFrontCamera.toggle() }
                )
                dockButton(
                    icon: isFlashOn ? "bolt.fill" : "bolt.slash.fill",
                    label: "Flash",
                    isActive: isFlashOn,
                    action: { isFlashOn.toggle() }
                )
                dockButton(
                    icon: "moon.stars.fill",
                    label: "Night",
                    isActive: isNightMode,
                    action: { isNightMode.toggle() }
                )
                dockButton(
                    icon: "grid",
                    label: "Grid",
                    isActive: showGrid,
                    action: { showGrid.toggle() }
                )
                dockButton(
                    icon: "sparkles",
                    label: "Lenses",
                    isActive: showLenses,
                    action: { withAnimation { showLenses.toggle() } }
                )
                dockButton(
                    icon: "qrcode",
                    label: "Scan",
                    isActive: isQrScannerActive,
                    action: {
                        isQrScannerActive.toggle()
                        if !isQrScannerActive { viewModel.clearDetectedQr() }
                    }
                )
            }
            .padding(.vertical, 14)
            .padding(.horizontal, 8)
            .background(Color.black.opacity(0.45))
            .clipShape(Capsule())
            .overlay(
                Capsule().stroke(Color.white.opacity(0.15), lineWidth: 0.8)
            )
            .padding(.trailing, 14)
            .padding(.top, 100)

            Spacer().frame(width: 0)
        }
    }

    private func dockButton(icon: String, label: String, isActive: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            VStack(spacing: 2) {
                Image(systemName: icon)
                    .font(.system(size: 19))
                    .foregroundColor(isActive ? .yellow : .white)
                    .frame(width: 32, height: 32)
                Text(label)
                    .font(.system(size: 9, weight: .medium))
                    .foregroundColor(.white.opacity(0.85))
            }
        }
    }

    // MARK: - Bottom Controls Overlay
    private var bottomControlsOverlay: some View {
        VStack(spacing: 16) {
            Spacer()

            // Horizontal Lens Carousel (if enabled or always visible for quick swiping)
            if showLenses {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 14) {
                        ForEach(viewModel.lenses) { lens in
                            let isSelected = viewModel.selectedLens.id == lens.id
                            Button(action: {
                                withAnimation(.spring()) {
                                    viewModel.selectLens(lens)
                                }
                            }) {
                                VStack(spacing: 4) {
                                    ZStack {
                                        Circle()
                                            .fill(isSelected ? Color.white : Color.black.opacity(0.6))
                                            .frame(width: 50, height: 50)
                                            .overlay(
                                                Circle()
                                                    .stroke(isSelected ? Color.evrryPrimaryBlue : Color.white.opacity(0.3), lineWidth: isSelected ? 3 : 1)
                                            )
                                        Image(systemName: "camera.filters")
                                            .font(.system(size: 20))
                                            .foregroundColor(isSelected ? Color.evrryPrimaryBlue : .white)
                                    }
                                    Text(lens.name)
                                        .font(.system(size: 11, weight: isSelected ? .bold : .medium))
                                        .foregroundColor(isSelected ? .white : .white.opacity(0.7))
                                }
                                .padding(.vertical, 4)
                            }
                        }
                    }
                    .padding(.horizontal, 24)
                }
                .transition(.move(edge: .bottom).combined(with: .opacity))
            }

            // Bottom Shutter Row: Memories Thumbnail | Shutter Button | Filters Toggle
            HStack(alignment: .center) {
                // Memories Shortcut Button
                Button(action: onOpenMemories) {
                    VStack(spacing: 4) {
                        ZStack {
                            if let firstSnap = viewModel.snaps.first {
                                AsyncImage(url: URL(string: firstSnap.imageUrl)) { phase in
                                    if let img = phase.image {
                                        img.resizable().aspectRatio(contentMode: .fill)
                                    } else {
                                        Color.gray
                                    }
                                }
                                .frame(width: 46, height: 46)
                                .clipShape(RoundedRectangle(cornerRadius: 12))
                                .overlay(
                                    RoundedRectangle(cornerRadius: 12)
                                        .stroke(Color.white, lineWidth: 2)
                                )
                            } else {
                                RoundedRectangle(cornerRadius: 12)
                                    .fill(Color.white.opacity(0.2))
                                    .frame(width: 46, height: 46)
                                    .overlay(
                                        Image(systemName: "photo.on.rectangle.angled")
                                            .foregroundColor(.white)
                                            .font(.system(size: 18))
                                    )
                            }
                        }
                        Text("Memories")
                            .font(.system(size: 10, weight: .semibold))
                            .foregroundColor(.white.opacity(0.85))
                    }
                }
                .frame(width: 70)

                Spacer()

                // Circular Snapchat-style Shutter Button
                ZStack {
                    // Outer Ring
                    Circle()
                        .stroke(Color.white, lineWidth: 4.5)
                        .frame(width: 80, height: 80)

                    // Inner Capture Core
                    Circle()
                        .fill(Color.white)
                        .frame(width: 66, height: 66)
                        .scaleEffect(isRecordingVideo ? 0.85 : 1.0)
                }
                .onTapGesture {
                    // Instant photo capture
                    let previewUrl = viewfinderImageForLens
                    onCaptureComplete(previewUrl, viewModel.selectedLens.name)
                }
                .onLongPressGesture(minimumDuration: 0.3, pressing: { pressing in
                    isRecordingVideo = pressing
                }, perform: {
                    let previewUrl = viewfinderImageForLens
                    onCaptureComplete(previewUrl, viewModel.selectedLens.name)
                })

                Spacer()

                // Quick Lenses Switcher Button
                Button(action: {
                    withAnimation(.spring()) {
                        showLenses.toggle()
                    }
                }) {
                    VStack(spacing: 4) {
                        ZStack {
                            Circle()
                                .fill(showLenses ? Color.evrryPrimaryBlue : Color.black.opacity(0.5))
                                .frame(width: 46, height: 46)
                                .overlay(
                                    Circle().stroke(Color.white.opacity(0.6), lineWidth: 1.5)
                                )
                            Image(systemName: "face.dashed")
                                .font(.system(size: 22))
                                .foregroundColor(.white)
                        }
                        Text("Lenses")
                            .font(.system(size: 10, weight: .semibold))
                            .foregroundColor(.white.opacity(0.85))
                    }
                }
                .frame(width: 70)
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 36)
        }
    }

    // MARK: - Demo QR Simulator Sheet
    private var demoQrPickerSheet: some View {
        NavigationView {
            List {
                Section(header: Text("Simulate Real Camera QR Detections")) {
                    ForEach(sampleQrPresets, id: \.0) { preset in
                        Button(action: {
                            showDemoQrMenu = false
                            viewModel.detectQr(raw: preset.1)
                        }) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(preset.0)
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.primary)
                                Text(preset.1)
                                    .font(.system(size: 11, design: .monospaced))
                                    .foregroundColor(.secondary)
                                    .lineLimit(1)
                            }
                            .padding(.vertical, 4)
                        }
                    }
                }
            }
            .navigationTitle("Test QR Codes")
            .navigationBarItems(trailing: Button("Cancel") { showDemoQrMenu = false })
        }
    }
}
