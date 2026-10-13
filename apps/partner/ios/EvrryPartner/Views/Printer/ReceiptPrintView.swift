//
//  ReceiptPrintView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  ESC/POS Thermal Ticket & Bill Monospace Preview Dialog
//

import SwiftUI

public struct ReceiptPrintView: View {
    public let order: RestaurantOrder
    public let store: RestaurantStore
    public let initialDocType: PrintDocType
    public let onDismiss: () -> Void
    
    @State private var selectedDocType: PrintDocType
    @State private var isPrinting: Bool = false
    @State private var printSuccess: Bool = false
    
    public init(
        order: RestaurantOrder,
        store: RestaurantStore,
        initialDocType: PrintDocType = .kot,
        onDismiss: @escaping () -> Void
    ) {
        self.order = order
        self.store = store
        self.initialDocType = initialDocType
        self.onDismiss = onDismiss
        _selectedDocType = State(initialValue: initialDocType)
    }
    
    private var receiptText: String {
        EscPosPrinterService.shared.generateReceiptPreviewText(order: order, store: store, docType: selectedDocType)
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            // Drag handle
            Capsule()
                .fill(PartnerKitchenTheme.cardBorder)
                .frame(width: 44, height: 5)
                .padding(.top, 10)
                .padding(.bottom, 12)
            
            // Header
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Thermal Ticket Print Preview")
                        .font(.system(size: 18, weight: .black))
                        .foregroundColor(PartnerKitchenTheme.textPrimary)
                    Text("ESC/POS 58mm / 80mm Bluetooth & LAN")
                        .font(.system(size: 11))
                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                }
                Spacer()
                Button(action: onDismiss) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.system(size: 22))
                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                }
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 12)
            
            // Doc Type Picker
            Picker("Document Type", selection: $selectedDocType) {
                ForEach(PrintDocType.allCases) { doc in
                    Text(doc.rawValue).tag(doc)
                }
            }
            .pickerStyle(SegmentedPickerStyle())
            .padding(.horizontal, 16)
            .padding(.bottom, 12)
            
            // Simulated Thermal Paper Sheet
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    // Top jagged teeth edge
                    HStack(spacing: 4) {
                        ForEach(0..<20) { _ in
                            Image(systemName: "triangle.fill")
                                .font(.system(size: 8))
                                .foregroundColor(Color(white: 0.96))
                                .rotationEffect(.degrees(180))
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .background(Color(white: 0.96))
                    .padding(.top, 4)
                    
                    Text(receiptText)
                        .font(.system(size: 11, weight: .medium, design: .monospaced))
                        .foregroundColor(Color.black)
                        .padding(16)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color(white: 0.96))
                    
                    // Bottom cut edge
                    HStack(spacing: 4) {
                        ForEach(0..<20) { _ in
                            Image(systemName: "triangle.fill")
                                .font(.system(size: 8))
                                .foregroundColor(Color(white: 0.96))
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .background(Color(white: 0.96))
                    .padding(.bottom, 4)
                }
                .cornerRadius(8)
                .shadow(color: Color.black.opacity(0.3), radius: 8, y: 4)
                .padding(.horizontal, 24)
            }
            
            // Print Button
            VStack(spacing: 8) {
                if printSuccess {
                    HStack(spacing: 6) {
                        Image(systemName: "checkmark.circle.fill")
                            .foregroundColor(PartnerKitchenTheme.accentGreen)
                        Text("Sent to thermal printer successfully!")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(PartnerKitchenTheme.accentGreen)
                    }
                }
                
                Button {
                    isPrinting = true
                    // Simulate ESC/POS byte generation & Bluetooth transmission
                    _ = EscPosPrinterService.shared.generateEscPosBytes(order: order, store: store, docType: selectedDocType)
                    
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.6) {
                        isPrinting = false
                        printSuccess = true
                    }
                } label: {
                    HStack(spacing: 8) {
                        if isPrinting {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Image(systemName: "printer.fill")
                            Text("Print \(selectedDocType == .kot ? "KOT" : "Tax Bill") Now")
                                .font(.system(size: 15, weight: .bold))
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(PartnerKitchenTheme.courierBlue)
                    .foregroundColor(.white)
                    .cornerRadius(12)
                }
                .disabled(isPrinting)
            }
            .padding(16)
        }
        .background(PartnerKitchenTheme.bgDark)
    }
}
