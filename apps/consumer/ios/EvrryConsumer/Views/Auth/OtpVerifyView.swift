//
//  OtpVerifyView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  6-Digit OTP Verification Screen (In-App Back Navigation)
//

import SwiftUI

public struct OtpVerifyView: View {
    @ObservedObject public var viewModel: AuthViewModel
    public let onBack: () -> Void
    public let onSuccess: () -> Void
    
    @FocusState private var isPinFocused: Bool
    
    public init(viewModel: AuthViewModel, onBack: @escaping () -> Void, onSuccess: @escaping () -> Void) {
        self.viewModel = viewModel
        self.onBack = onBack
        self.onSuccess = onSuccess
    }
    
    public var body: some View {
        let targetPhone: String = {
            if case .otpRequested(let phone, _, _, _) = viewModel.state {
                return phone
            }
            return "+977"
        }()
        
        let channelLabel: String = {
            if case .otpRequested(_, let channel, _, _) = viewModel.state {
                return channel.label
            }
            return "WhatsApp"
        }()
        
        VStack(spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                // TOP IN-APP BACK BUTTON (Critical for iOS navigation)
                Button(action: {
                    viewModel.resetToPhoneInput()
                    onBack()
                }) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                        .frame(width: 42, height: 42)
                        .background(Color.white)
                        .clipShape(Circle())
                        .overlay(Circle().stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        .shadow(color: Color.black.opacity(0.04), radius: 3, x: 0, y: 1)
                }
                .padding(.top, 16)
                
                Spacer().frame(height: 24)
                
                Text("Enter 6-digit Code")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(.evrryDarkCharcoal)
                
                Text("We sent a verification code to \(targetPhone) via \(channelLabel).")
                    .font(.system(size: 13))
                    .foregroundColor(.evrryMutedText)
                    .padding(.top, 6)
                
                Spacer().frame(height: 32)
                
                // 6 Numeric Input Boxes
                HStack(spacing: 8) {
                    ForEach(0..<6, id: \.self) { index in
                        let char = getDigit(at: index)
                        ZStack {
                            RoundedRectangle(cornerRadius: 14)
                                .fill(Color.white)
                                .frame(height: 56)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 14)
                                        .stroke(viewModel.otpCode.count == index ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: viewModel.otpCode.count == index ? 2 : 1)
                                )
                            Text(char)
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                        }
                    }
                }
                .contentShape(Rectangle())
                .onTapGesture {
                    isPinFocused = true
                }
                
                // Invisible numerical TextField for keyboard
                TextField("", text: Binding(
                    get: { viewModel.otpCode },
                    set: {
                        viewModel.updateOtp($0)
                        if $0.count == 6 {
                            viewModel.verifyOtp(code: $0, onSuccess: onSuccess)
                        }
                    }
                ))
                .keyboardType(.numberPad)
                .focused($isPinFocused)
                .frame(width: 0, height: 0)
                .opacity(0)
                
                Spacer().frame(height: 20)
                
                // Quick Auto-Fill Demo Mode
                Button(action: {
                    viewModel.updateOtp("123456")
                    viewModel.verifyOtp(code: "123456", onSuccess: onSuccess)
                }) {
                    HStack {
                        Text("Demo Testing: Tap to auto-fill 123456")
                            .font(.system(size: 11, weight: .medium))
                            .foregroundColor(.evrryPrimaryBlue)
                        Spacer()
                        Text("Auto-Fill →")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryPrimaryBlue)
                    }
                    .padding(.vertical, 10)
                    .padding(.horizontal, 14)
                    .background(Color.blue.opacity(0.08))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                }
                
                Spacer().frame(height: 24)
                
                HStack {
                    Spacer()
                    Text("Didn't receive code? Resend in 0:42")
                        .font(.system(size: 12))
                        .foregroundColor(.evrryMutedText)
                    Spacer()
                }
            }
            .padding(.horizontal, 24)
            
            Spacer()
            
            // Verify CTA Button
            Button(action: {
                viewModel.verifyOtp(onSuccess: onSuccess)
            }) {
                HStack {
                    if viewModel.isLoading {
                        ProgressView().tint(.white)
                    } else {
                        Text("Verify & Continue")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.white)
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 52)
                .background(viewModel.otpCode.count == 6 ? Color.evrryPrimaryBlue : Color.evrryPrimaryBlue.opacity(0.4))
                .clipShape(RoundedRectangle(cornerRadius: 16))
            }
            .disabled(viewModel.otpCode.count != 6 || viewModel.isLoading)
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
        .background(Color.evrryBgTop.ignoresSafeArea())
        .onAppear {
            isPinFocused = true
        }
    }
    
    private func getDigit(at index: Int) -> String {
        guard index < viewModel.otpCode.count else { return "" }
        let stringIndex = viewModel.otpCode.index(viewModel.otpCode.startIndex, offsetBy: index)
        return String(viewModel.otpCode[stringIndex])
    }
}
