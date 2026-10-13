//
//  CourierDualCallSheet.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Dual Calling Chooser Sheet (In-App VoIP vs Direct SIM)
//  Grounded in GetStream_whatsApp-clone-compose & luxeride
//

import SwiftUI

public struct CourierDualCallSheet: View {
    @ObservedObject var viewModel: CourierDeliveryViewModel
    
    public init(viewModel: CourierDeliveryViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        VStack(spacing: 20) {
            // Header
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Contact \(viewModel.callTargetRole == "store" ? "Store Partner" : "Customer")")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.white)
                    Text("\(viewModel.callTargetName) • \(viewModel.callTargetPhone)")
                        .font(.system(size: 13))
                        .foregroundColor(.gray)
                }
                Spacer()
                Button {
                    viewModel.showDualCallSheet = false
                } label: {
                    Image(systemName: "xmark.circle.fill")
                        .font(.system(size: 24))
                        .foregroundColor(.gray)
                }
            }
            
            // Call Options
            VStack(spacing: 12) {
                // In-App VoIP Call Option
                Button {
                    viewModel.placeVoipCall(name: viewModel.callTargetName)
                } label: {
                    HStack(spacing: 14) {
                        Image(systemName: "waveform.badge.mic")
                            .font(.system(size: 22))
                            .foregroundColor(.white)
                            .frame(width: 48, height: 48)
                            .background(Color.blue)
                            .clipShape(Circle())
                        
                        VStack(alignment: .leading, spacing: 2) {
                            Text("In-App HD VoIP Audio Call")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.white)
                            Text("Free data call • Encrypted low-latency audio")
                                .font(.system(size: 11))
                                .foregroundColor(.gray)
                        }
                        
                        Spacer()
                        
                        Image(systemName: "chevron.right")
                            .foregroundColor(.gray)
                    }
                    .padding(14)
                    .background(Color(red: 0.14, green: 0.16, blue: 0.25))
                    .cornerRadius(14)
                    .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.blue.opacity(0.3), lineWidth: 1))
                }
                
                // Direct SIM Cellular Call Option
                Button {
                    viewModel.placeSimCall(phone: viewModel.callTargetPhone)
                } label: {
                    HStack(spacing: 14) {
                        Image(systemName: "phone.fill")
                            .font(.system(size: 22))
                            .foregroundColor(.white)
                            .frame(width: 48, height: 48)
                            .background(Color.green)
                            .clipShape(Circle())
                        
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Direct SIM Carrier Call")
                                .font(.system(size: 15, weight: .bold))
                            Text("Nepal Telecom (NTC) / Ncell GSM dialer")
                                .font(.system(size: 11))
                                .foregroundColor(.gray)
                        }
                        
                        Spacer()
                        
                        Image(systemName: "chevron.right")
                            .foregroundColor(.gray)
                    }
                    .padding(14)
                    .background(Color(red: 0.14, green: 0.16, blue: 0.25))
                    .cornerRadius(14)
                    .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.green.opacity(0.3), lineWidth: 1))
                }
            }
            
            Spacer()
        }
        .padding(20)
        .background(Color(red: 0.08, green: 0.10, blue: 0.16).ignoresSafeArea())
    }
}
