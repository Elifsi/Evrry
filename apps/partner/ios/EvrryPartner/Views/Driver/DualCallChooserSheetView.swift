//
//  DualCallChooserSheetView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Dual Call Chooser Sheet (In-App VoIP + Direct Carrier SIM)
//

import SwiftUI

public struct DualCallChooserSheetView: View {
    public let passengerName: String
    public let passengerPhone: String
    public let onDismiss: () -> Void
    public let onInAppVoipCall: () -> Void
    
    public init(
        passengerName: String,
        passengerPhone: String,
        onDismiss: @escaping () -> Void,
        onInAppVoipCall: @escaping () -> Void
    ) {
        self.passengerName = passengerName
        self.passengerPhone = passengerPhone
        self.onDismiss = onDismiss
        self.onInAppVoipCall = onInAppVoipCall
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Contact Passenger")
                .font(.system(size: 18, weight: .bold))
            Text("Choose your preferred calling method to coordinate with \(passengerName) (\(passengerPhone))")
                .font(.system(size: 12))
                .foregroundColor(.secondary)
            
            // 1. IN-APP VOIP CALL
            Button {
                onDismiss()
                onInAppVoipCall()
            } label: {
                HStack(spacing: 14) {
                    Image(systemName: "phone.fill")
                        .font(.system(size: 20))
                        .foregroundColor(.white)
                        .frame(width: 44, height: 44)
                        .background(Color.blue)
                        .clipShape(Circle())
                    
                    VStack(alignment: .leading, spacing: 2) {
                        HStack {
                            Text("Call in App")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.primary)
                            Text("FREE · VOIP")
                                .font(.system(size: 9, weight: .black))
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(Color.green.opacity(0.15))
                                .foregroundColor(Color(red: 0.08, green: 0.45, blue: 0.22))
                                .cornerRadius(6)
                        }
                        Text("High-definition encrypted voice call over mobile data / Wi-Fi")
                            .font(.system(size: 11))
                            .foregroundColor(.secondary)
                    }
                    Spacer()
                }
                .padding(14)
                .background(Color.blue.opacity(0.06))
                .cornerRadius(16)
                .overlay(
                    RoundedRectangle(cornerRadius: 16)
                        .stroke(Color.blue.opacity(0.3), lineWidth: 1.5)
                )
            }
            
            // 2. DIRECT CARRIER SIM CALL
            Button {
                onDismiss()
                if let url = URL(string: "tel://\(passengerPhone)") {
                    UIApplication.shared.open(url)
                }
            } label: {
                HStack(spacing: 14) {
                    Image(systemName: "antenna.radiowaves.left.and.right")
                        .font(.system(size: 20))
                        .foregroundColor(.white)
                        .frame(width: 44, height: 44)
                        .background(Color.gray)
                        .clipShape(Circle())
                    
                    VStack(alignment: .leading, spacing: 2) {
                        HStack {
                            Text("Direct SIM Call")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.primary)
                            Text("CARRIER")
                                .font(.system(size: 9, weight: .black))
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(Color.gray.opacity(0.15))
                                .foregroundColor(.secondary)
                                .cornerRadius(6)
                        }
                        Text("Standard cellular call via your SIM (Ncell / NTC network proxy)")
                            .font(.system(size: 11))
                            .foregroundColor(.secondary)
                    }
                    Spacer()
                }
                .padding(14)
                .background(Color(UIColor.systemGray6).opacity(0.6))
                .cornerRadius(16)
                .overlay(
                    RoundedRectangle(cornerRadius: 16)
                        .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                )
            }
            
            Spacer()
        }
        .padding(24)
    }
}
