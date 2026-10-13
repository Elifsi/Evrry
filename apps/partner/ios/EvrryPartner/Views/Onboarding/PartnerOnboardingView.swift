//
//  PartnerOnboardingView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Shared Partner Phone & OTP Authentication View
//

import SwiftUI

public struct PartnerOnboardingView: View {
    @ObservedObject var authViewModel: PartnerAuthViewModel
    
    public init(authViewModel: PartnerAuthViewModel) {
        self.authViewModel = authViewModel
    }
    
    public var body: some View {
        VStack(spacing: 20) {
            Spacer()
            
            // LOGO
            ZStack {
                Circle()
                    .fill(Color.blue.opacity(0.1))
                    .frame(width: 80, height: 80)
                Text("🇳🇵")
                    .font(.system(size: 38))
            }
            
            Text("evrry Partner")
                .font(.system(size: 26, weight: .black))
            
            Text("Unified Operator Portal for Nepal")
                .font(.system(size: 13))
                .foregroundColor(.secondary)
            
            if let err = authViewModel.errorMessage {
                Text(err)
                    .font(.system(size: 12, weight: .medium))
                    .foregroundColor(.red)
                    .padding(10)
                    .frame(maxWidth: .infinity)
                    .background(Color.red.opacity(0.08))
                    .cornerRadius(10)
            }
            
            if authViewModel.authState == .unauthenticated {
                // PHONE NUMBER STEP
                VStack(alignment: .leading, spacing: 10) {
                    Text("Enter Your Mobile Number")
                        .font(.system(size: 13, weight: .bold))
                    
                    HStack {
                        Text("🇳🇵 +977")
                            .font(.system(size: 14, weight: .bold))
                        Divider()
                            .frame(height: 20)
                        TextField("98XXXXXXXX", text: $authViewModel.phoneNumber)
                            .keyboardType(.numberPad)
                            .font(.system(size: 15))
                    }
                    .padding(14)
                    .background(Color(UIColor.systemGray6).opacity(0.6))
                    .cornerRadius(14)
                    .overlay(
                        RoundedRectangle(cornerRadius: 14)
                            .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                    )
                    
                    Button {
                        authViewModel.requestOtp(channel: "whatsapp")
                    } label: {
                        HStack {
                            if authViewModel.isLoading {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            } else {
                                Text("Get WhatsApp OTP 💬")
                                    .font(.system(size: 15, weight: .bold))
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 14)
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(14)
                    }
                    .disabled(authViewModel.isLoading || authViewModel.phoneNumber.isEmpty)
                    
                    Button {
                        authViewModel.requestOtp(channel: "sms")
                    } label: {
                        Text("Send via SMS instead")
                            .font(.system(size: 13))
                            .foregroundColor(.secondary)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 8)
                    }
                }
            } else {
                // OTP VERIFICATION STEP
                VStack(spacing: 14) {
                    Text("Enter Verification Code")
                        .font(.system(size: 15, weight: .bold))
                    Text("Code sent to +977 \(authViewModel.phoneNumber)")
                        .font(.system(size: 12))
                        .foregroundColor(.secondary)
                    
                    TextField("• • • • • •", text: $authViewModel.otpCode)
                        .keyboardType(.numberPad)
                        .font(.system(size: 22, weight: .bold))
                        .multilineTextAlignment(.center)
                        .padding(14)
                        .background(Color(UIColor.systemGray6).opacity(0.6))
                        .cornerRadius(14)
                    
                    Button {
                        authViewModel.verifyOtp()
                    } label: {
                        HStack {
                            if authViewModel.isLoading {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            } else {
                                Text("Verify & Continue")
                                    .font(.system(size: 15, weight: .bold))
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 14)
                        .background(Color.green)
                        .foregroundColor(.white)
                        .cornerRadius(14)
                    }
                    .disabled(authViewModel.isLoading || authViewModel.otpCode.isEmpty)
                    
                    Button {
                        authViewModel.requestOtp(channel: "whatsapp")
                    } label: {
                        Text("Resend Code")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(.blue)
                    }
                }
            }
            
            Spacer()
        }
        .padding(24)
        .background(Color(UIColor.systemBackground))
    }
}
