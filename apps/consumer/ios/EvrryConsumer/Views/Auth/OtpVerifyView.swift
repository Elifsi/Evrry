//
//  OtpVerifyView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  6-Digit WhatsApp OTP Verification Screen (Nepal +977)
//

import SwiftUI

public struct OtpVerifyView: View {
    @ObservedObject public var viewModel: AuthViewModel
    public let onBack: () -> Void
    public let onSuccess: () -> Void
    
    @FocusState private var isPinFocused: Bool
    
    // 1-minute countdown timer
    @State private var secondsRemaining: Int = 60
    @State private var timerActive: Bool = true
    @State private var resendToastMessage: String? = nil
    
    let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    
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
            return "+977\(viewModel.phoneNumber.isEmpty ? "98XXXXXXXX" : viewModel.phoneNumber)"
        }()
        
        let channelLabel: String = {
            if case .otpRequested(_, let channel, _, _) = viewModel.state {
                return channel.label
            }
            return "WhatsApp"
        }()
        
        VStack(spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                // TOP IN-APP BACK BUTTON
                Button(action: {
                    isPinFocused = false
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
                        let isCurrent = viewModel.otpCode.count == index
                        ZStack {
                            RoundedRectangle(cornerRadius: 14)
                                .fill(Color.white)
                                .frame(height: 56)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 14)
                                        .stroke(isCurrent ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: isCurrent ? 2 : 1)
                                )
                            Text(char)
                                .font(.system(size: 22, weight: .bold))
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
                        let filtered = $0.filter { $0.isNumber }
                        let digits = String(filtered.prefix(6))
                        viewModel.updateOtp(digits)
                        if digits.count == 6 {
                            isPinFocused = false
                            viewModel.verifyOtp(code: digits, onSuccess: onSuccess)
                        }
                    }
                ))
                .keyboardType(.numberPad)
                .focused($isPinFocused)
                .frame(width: 0, height: 0)
                .opacity(0)
                
                Spacer().frame(height: 14)
                
                // RESEND BUTTON JUST BELOW THE OTP INPUT PLACE
                HStack(spacing: 8) {
                    Button(action: {
                        viewModel.requestOtp()
                        secondsRemaining = 60
                        timerActive = true
                        resendToastMessage = "Verification code resent to WhatsApp"
                    }) {
                        HStack(spacing: 6) {
                            Image(systemName: "arrow.clockwise")
                                .font(.system(size: 12, weight: .bold))
                            Text("Resend Code")
                                .font(.system(size: 13, weight: .semibold))
                        }
                        .foregroundColor(.evrryPrimaryBlue)
                    }
                    
                    if let message = resendToastMessage {
                        Text("• \(message)")
                            .font(.system(size: 12))
                            .foregroundColor(Color(red: 16/255, green: 185/255, blue: 129/255))
                    }
                }
                .padding(.leading, 2)
                
                Spacer().frame(height: 18)
                
                // Quick Auto-Fill Demo Mode
                Button(action: {
                    viewModel.updateOtp("123456")
                    isPinFocused = false
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
            }
            .padding(.horizontal, 24)
            
            Spacer()
            
            // BOTTOM SECTION: 1-MIN COUNTDOWN TIMER & VERIFY CTA
            VStack(spacing: 16) {
                // 1 MINUTE COUNTDOWN TIMER (Resend on number in 00:xx)
                if secondsRemaining > 0 {
                    let formatted = String(format: "%02d", secondsRemaining)
                    Text("Resend on number in 00:\(formatted)")
                        .font(.system(size: 13))
                        .foregroundColor(.evrryMutedText)
                } else {
                    Button(action: {
                        viewModel.requestOtp()
                        secondsRemaining = 60
                        timerActive = true
                        resendToastMessage = "Verification code resent to \(targetPhone)"
                    }) {
                        Text("Resend on number (\(targetPhone))")
                            .font(.system(size: 13, weight: .bold))
                            .foregroundColor(.evrryPrimaryBlue)
                    }
                }
                
                // Verify CTA Button
                Button(action: {
                    isPinFocused = false
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
                    .background(viewModel.otpCode.count == 6 && !viewModel.isLoading ? Color.evrryPrimaryBlue : Color.evrryPrimaryBlue.opacity(0.4))
                    .clipShape(RoundedRectangle(cornerRadius: 16))
                }
                .disabled(viewModel.otpCode.count != 6 || viewModel.isLoading)
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
        .background(Color.evrryBgTop.ignoresSafeArea())
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                isPinFocused = true
            }
        }
        .onReceive(timer) { _ in
            if timerActive && secondsRemaining > 0 {
                secondsRemaining -= 1
            } else if secondsRemaining == 0 {
                timerActive = false
            }
        }
    }
    
    private func getDigit(at index: Int) -> String {
        guard index < viewModel.otpCode.count else { return "" }
        let stringIndex = viewModel.otpCode.index(viewModel.otpCode.startIndex, offsetBy: index)
        return String(viewModel.otpCode[stringIndex])
    }
}
