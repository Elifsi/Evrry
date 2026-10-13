//
//  GroceryBarcodeScannerView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Camera Barcode Scanner & Shelf SKU Audit for Supermarket Mode
//  AVFoundation AVCaptureMetadataOutput with Clean Hardware Release
//

import SwiftUI
import AVFoundation

public struct GroceryBarcodeScannerView: View {
    @ObservedObject var viewModel: GrocerySupermarketViewModel
    let onBack: () -> Void
    
    @State private var isFlashlightOn = false
    @State private var showEditPriceModal = false
    @State private var editedPriceString = ""
    
    public init(
        viewModel: GrocerySupermarketViewModel,
        onBack: @escaping () -> Void
    ) {
        self.viewModel = viewModel
        self.onBack = onBack
    }
    
    public var body: some View {
        ZStack {
            // Full screen camera feed
            CameraPreviewView(onBarcodeDetected: { barcode in
                viewModel.scanBarcode(barcode)
                playHapticFeedback()
            }, isFlashlightOn: $isFlashlightOn)
            .edgesIgnoringSafeArea(.all)
            
            // Scanner Overlay
            VStack {
                // Top Action Bar
                HStack {
                    Button(action: onBack) {
                        Image(systemName: "chevron.left")
                            .font(.headline)
                            .foregroundColor(.white)
                            .padding(12)
                            .background(Color.black.opacity(0.6))
                            .clipShape(Circle())
                    }
                    
                    Spacer()
                    
                    Text("Shelf Barcode & SKU Audit")
                        .font(.headline)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                        .background(Color.black.opacity(0.6))
                        .cornerRadius(20)
                    
                    Spacer()
                    
                    Button(action: {
                        isFlashlightOn.toggle()
                    }) {
                        Image(systemName: isFlashlightOn ? "bolt.fill" : "bolt.slash.fill")
                            .font(.headline)
                            .foregroundColor(isFlashlightOn ? .yellow : .white)
                            .padding(12)
                            .background(Color.black.opacity(0.6))
                            .clipShape(Circle())
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 48)
                
                Spacer()
                
                // Central Viewfinder Reticle
                ZStack {
                    RoundedRectangle(cornerRadius: 16)
                        .stroke(Color(hex: "22C55E"), lineWidth: 2)
                        .frame(width: 280, height: 180)
                    
                    // Center Targeting Line
                    Rectangle()
                        .fill(Color(hex: "22C55E"))
                        .frame(width: 250, height: 2)
                }
                
                Text("Align barcode inside rectangle")
                    .font(.caption)
                    .foregroundColor(.white.opacity(0.8))
                    .padding(.top, 8)
                
                Spacer()
                
                // Bottom Scanned Item Card
                if let item = viewModel.scannedItem {
                    VStack(alignment: .leading, spacing: 12) {
                        HStack(spacing: 12) {
                            VStack(alignment: .leading, spacing: 3) {
                                Text(item.name)
                                    .font(.headline)
                                    .fontWeight(.bold)
                                    .foregroundColor(.white)
                                HStack(spacing: 8) {
                                    Text(item.brand)
                                        .font(.caption)
                                        .foregroundColor(.gray)
                                    Text("•")
                                        .font(.caption)
                                        .foregroundColor(.gray)
                                    Text("Barcode: \(item.barcode ?? "N/A")")
                                        .font(.caption)
                                        .foregroundColor(Color(hex: "F97316"))
                                }
                            }
                            
                            Spacer()
                            
                            Button(action: {
                                viewModel.toggleStockAvailability(itemId: item.id)
                            }) {
                                Text(item.isAvailable ? "In Stock" : "86'd Out")
                                    .font(.caption)
                                    .fontWeight(.bold)
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 6)
                                    .background(item.isAvailable ? Color(hex: "22C55E") : Color(hex: "EF4444"))
                                    .foregroundColor(.white)
                                    .cornerRadius(8)
                            }
                        }
                        
                        Divider().background(Color(hex: "334155"))
                        
                        HStack {
                            // Shelf Price
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Shelf Price")
                                    .font(.caption2)
                                    .foregroundColor(.gray)
                                HStack(spacing: 4) {
                                    Text("NPR \(String(format: "%.0f", item.priceNpr))")
                                        .font(.title3)
                                        .fontWeight(.bold)
                                        .foregroundColor(.white)
                                    Button(action: {
                                        editedPriceString = String(format: "%.0f", item.priceNpr)
                                        showEditPriceModal = true
                                    }) {
                                        Image(systemName: "pencil.circle")
                                            .foregroundColor(Color(hex: "F97316"))
                                    }
                                }
                            }
                            
                            Spacer()
                            
                            // Stock Counter
                            VStack(alignment: .trailing, spacing: 2) {
                                Text("Shelf Inventory")
                                    .font(.caption2)
                                    .foregroundColor(.gray)
                                HStack(spacing: 12) {
                                    Button(action: {
                                        viewModel.updateStockQuantity(itemId: item.id, delta: -1)
                                    }) {
                                        Text("-")
                                            .font(.headline)
                                            .foregroundColor(.white)
                                            .frame(width: 32, height: 32)
                                            .background(Color(hex: "334155"))
                                            .clipShape(Circle())
                                    }
                                    
                                    Text("\(item.stockQuantity) \(item.unit)")
                                        .font(.subheadline)
                                        .fontWeight(.bold)
                                        .foregroundColor(.white)
                                    
                                    Button(action: {
                                        viewModel.updateStockQuantity(itemId: item.id, delta: 1)
                                    }) {
                                        Text("+")
                                            .font(.headline)
                                            .foregroundColor(.white)
                                            .frame(width: 32, height: 32)
                                            .background(Color(hex: "F97316"))
                                            .clipShape(Circle())
                                    }
                                }
                            }
                        }
                    }
                    .padding(16)
                    .background(Color(hex: "0F172A").opacity(0.95))
                    .cornerRadius(16)
                    .padding(16)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                } else {
                    HStack(spacing: 8) {
                        Image(systemName: "barcode.viewfinder")
                            .foregroundColor(Color(hex: "F97316"))
                        Text("Ready to scan shelf barcodes...")
                            .font(.subheadline)
                            .foregroundColor(.white)
                    }
                    .padding()
                    .background(Color.black.opacity(0.7))
                    .cornerRadius(12)
                    .padding(.bottom, 24)
                }
            }
        }
        .sheet(isPresented: $showEditPriceModal) {
            if let item = viewModel.scannedItem {
                VStack(spacing: 18) {
                    Text("Edit Shelf Price")
                        .font(.headline)
                        .foregroundColor(.white)
                    Text(item.name)
                        .font(.subheadline)
                        .foregroundColor(.gray)
                    
                    HStack {
                        Text("NPR")
                            .font(.title2)
                            .foregroundColor(.white)
                        TextField("New Price", text: $editedPriceString)
                            .keyboardType(.numberPad)
                            .font(.title2)
                            .padding()
                            .background(Color(hex: "1E293B"))
                            .cornerRadius(10)
                            .foregroundColor(.white)
                    }
                    .padding(.horizontal)
                    
                    HStack(spacing: 12) {
                        Button("Cancel") {
                            showEditPriceModal = false
                        }
                        .foregroundColor(.gray)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color(hex: "1E293B"))
                        .cornerRadius(10)
                        
                        Button("Save Price") {
                            if let newPrice = Double(editedPriceString) {
                                viewModel.updatePrice(itemId: item.id, newPriceNpr: newPrice)
                            }
                            showEditPriceModal = false
                        }
                        .foregroundColor(.white)
                        .fontWeight(.bold)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color(hex: "22C55E"))
                        .cornerRadius(10)
                    }
                    .padding(.horizontal)
                }
                .padding()
                .background(Color(hex: "0F172A").edgesIgnoringSafeArea(.all))
            }
        }
    }
    
    private func playHapticFeedback() {
        #if os(iOS)
        let generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(.success)
        #endif
    }
}

// MARK: - AVFoundation Camera Preview Representable
struct CameraPreviewView: UIViewRepresentable {
    let onBarcodeDetected: (String) -> Void
    @Binding var isFlashlightOn: Bool
    
    func makeUIView(context: Context) -> CameraPreviewUIView {
        let view = CameraPreviewUIView()
        view.onBarcodeDetected = onBarcodeDetected
        view.setupCamera()
        return view
    }
    
    func updateUIView(_ uiView: CameraPreviewUIView, context: Context) {
        uiView.toggleTorch(on: isFlashlightOn)
    }
    
    static func dismantleUIView(_ uiView: CameraPreviewUIView, coordinator: ()) {
        // Clean hardware release on screen exit
        uiView.stopCamera()
    }
}

class CameraPreviewUIView: UIView, AVCaptureMetadataOutputObjectsDelegate {
    var onBarcodeDetected: ((String) -> Void)?
    private var captureSession: AVCaptureSession?
    private var previewLayer: AVCaptureVideoPreviewLayer?
    private var lastScannedCode: String? = nil
    private var lastScanTime: Date = Date.distantPast
    
    func setupCamera() {
        let session = AVCaptureSession()
        session.sessionPreset = .high
        
        guard let device = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .back) else {
            return
        }
        
        do {
            let input = try AVCaptureDeviceInput(device: device)
            if session.canAddInput(input) {
                session.addInput(input)
            }
            
            let output = AVCaptureMetadataOutput()
            if session.canAddOutput(output) {
                session.addOutput(output)
                output.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
                output.metadataObjectTypes = [.ean13, .ean8, .qr, .code128]
            }
            
            let preview = AVCaptureVideoPreviewLayer(session: session)
            preview.videoGravity = .resizeAspectFill
            preview.frame = bounds
            layer.addSublayer(preview)
            self.previewLayer = preview
            self.captureSession = session
            
            DispatchQueue.global(qos: .userInitiated).async {
                session.startRunning()
            }
        } catch {
            print("Camera hardware init error: \(error)")
        }
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        previewLayer?.frame = bounds
    }
    
    func toggleTorch(on: Bool) {
        guard let device = AVCaptureDevice.default(for: .video), device.hasTorch else { return }
        do {
            try device.lockForConfiguration()
            device.torchMode = on ? .on : .off
            device.unlockForConfiguration()
        } catch {
            print("Torch error: \(error)")
        }
    }
    
    func stopCamera() {
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            self?.captureSession?.stopRunning()
            self?.captureSession = nil
        }
    }
    
    // AVCaptureMetadataOutputObjectsDelegate
    func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        guard let metadata = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
              let stringValue = metadata.stringValue else { return }
        
        // Debounce scanning to prevent repeated triggers
        let now = Date()
        if stringValue != lastScannedCode || now.timeIntervalSince(lastScanTime) > 2.0 {
            lastScannedCode = stringValue
            lastScanTime = now
            onBarcodeDetected?(stringValue)
        }
    }
}
