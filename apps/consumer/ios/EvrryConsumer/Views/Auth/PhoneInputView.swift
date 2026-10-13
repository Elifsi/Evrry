//
//  PhoneInputView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Mobile Phone Login Screen (Nepal +977 WhatsApp / SMS OTP)
//  + 1-Tap Social Sign-In (Google, Apple, Email)
//

import SwiftUI

public struct PhoneInputView: View {
    @ObservedObject public var viewModel: AuthViewModel
    public let onContinue: () -> Void
    
    @State private var showEmailAlert: Bool = false
    @State private var emailText: String = ""
    
    public init(viewModel: AuthViewModel, onContinue: @escaping () -> Void) {
        self.viewModel = viewModel
        self.onContinue = onContinue
    }
    
    public var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                // Content
                VStack(alignment: .leading, spacing: 0) {
                    Spacer().frame(height: 16)
                    
                    // Brand Header
                    HStack(spacing: 8) {
                        Text("e")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(.white)
                            .frame(width: 36, height: 36)
                            .background(Color.evrryPrimaryBlue)
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                        
                        Text("evrry")
                            .font(.system(size: 24, weight: .black))
                            .foregroundColor(.evrryDarkCharcoal)
                    }
                    
                    Spacer().frame(height: 24)
                    
                    Text("Welcome to evrry")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    
                    Text("Enter your Nepal mobile number to sign in or register your account.")
                        .font(.system(size: 13))
                        .foregroundColor(.evrryMutedText)
                        .padding(.top, 6)
                    
                    Spacer().frame(height: 28)
                    
                    // Mobile Input Field with 🇳🇵 +977
                    Text("MOBILE NUMBER")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                    
                    Spacer().frame(height: 8)
                    
                    HStack(spacing: 10) {
                        Text("🇳🇵 +977")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        
                        Rectangle()
                            .frame(width: 1, height: 24)
                            .foregroundColor(.evrrySubtleBorder)
                        
                        TextField("98XXXXXXXX", text: Binding(
                            get: { viewModel.phoneNumber },
                            set: { viewModel.updatePhoneNumber($0) }
                        ))
                        .keyboardType(.numberPad)
                        .font(.system(size: 16))
                    }
                    .padding(.horizontal, 14)
                    .padding(.vertical, 14)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 16))
                    .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1.5))
                    
                    Spacer().frame(height: 20)
                    
                    // Channel Selector (WhatsApp vs Domestic SMS)
                    Text("VERIFICATION CHANNEL")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                    
                    Spacer().frame(height: 8)
                    
                    HStack(spacing: 10) {
                        ForEach(OtpChannel.allCases, id: \.self) { channel in
                            let isSelected = viewModel.selectedChannel == channel
                            Button(action: { viewModel.selectChannel(channel) }) {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(channel.label)
                                        .font(.system(size: 13, weight: .semibold))
                                        .foregroundColor(isSelected ? .evrryPrimaryBlue : .evrryDarkCharcoal)
                                    Text(channel.badge)
                                        .font(.system(size: 10))
                                        .foregroundColor(isSelected ? .evrryPrimaryBlue.opacity(0.8) : .evrryMutedText)
                                }
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .padding(12)
                                .background(isSelected ? Color.blue.opacity(0.06) : Color.white)
                                .clipShape(RoundedRectangle(cornerRadius: 14))
                                .overlay(RoundedRectangle(cornerRadius: 14).stroke(isSelected ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: isSelected ? 1.5 : 1))
                            }
                        }
                    }
                    
                    if let error = viewModel.errorMessage {
                        Text(error)
                            .font(.system(size: 12))
                            .foregroundColor(.evrryRedDestructive)
                            .padding(.top, 12)
                    }
                    
                    Spacer().frame(height: 24)
                    
                    // Primary Continue CTA
                    Button(action: {
                        viewModel.requestOtp()
                        onContinue()
                    }) {
                        HStack {
                            if viewModel.isLoading {
                                ProgressView().tint(.white)
                            } else {
                                Text("Get Verification Code")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.white)
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(viewModel.phoneNumber.count == 10 ? Color.evrryPrimaryBlue : Color.evrryPrimaryBlue.opacity(0.4))
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                    }
                    .disabled(viewModel.phoneNumber.count != 10 || viewModel.isLoading)
                    
                    Spacer().frame(height: 28)
                    
                    // OR CONTINUE WITH Divider
                    HStack {
                        Rectangle()
                            .fill(Color.evrrySubtleBorder)
                            .frame(height: 1)
                        Text("OR CONTINUE WITH")
                            .font(.system(size: 11, weight: .semibold))
                            .foregroundColor(.evrryMutedText)
                            .padding(.horizontal, 10)
                        Rectangle()
                            .fill(Color.evrrySubtleBorder)
                            .frame(height: 1)
                    }
                    
                    Spacer().frame(height: 20)
                    
                    // Social Sign-In Buttons
                    VStack(spacing: 10) {
                        // Google
                        Button(action: {
                            viewModel.signInWithGoogle()
                        }) {
                            HStack(spacing: 10) {
                                Text("G")
                                    .font(.system(size: 14, weight: .black))
                                    .foregroundColor(Color(red: 66/255, green: 133/255, blue: 244/255))
                                    .frame(width: 24, height: 24)
                                    .background(Color(red: 241/255, green: 245/255, blue: 249/255))
                                    .clipShape(Circle())
                                
                                Text("Continue with Google")
                                    .font(.system(size: 14, weight: .semibold))
                                    .foregroundColor(.evrryDarkCharcoal)
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 14))
                            .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        }
                        
                        // Apple
                        Button(action: {
                            viewModel.signInWithApple()
                        }) {
                            HStack(spacing: 8) {
                                Image(systemName: "applelogo")
                                    .font(.system(size: 16, weight: .semibold))
                                    .foregroundColor(.white)
                                
                                Text("Continue with Apple")
                                    .font(.system(size: 14, weight: .semibold))
                                    .foregroundColor(.white)
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(Color(red: 15/255, green: 23/255, blue: 42/255))
                            .clipShape(RoundedRectangle(cornerRadius: 14))
                        }
                        
                        // Email
                        Button(action: {
                            showEmailAlert = true
                        }) {
                            HStack(spacing: 10) {
                                Image(systemName: "envelope.fill")
                                    .font(.system(size: 15))
                                    .foregroundColor(.evrryMutedText)
                                
                                Text("Continue with Email")
                                    .font(.system(size: 14, weight: .semibold))
                                    .foregroundColor(.evrryDarkCharcoal)
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 14))
                            .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        }
                    }
                    
                    Spacer().frame(height: 28)
                    
                    // Terms & Privacy
                    HStack {
                        Spacer()
                        Text("By continuing, you agree to Evrry Terms & Privacy Policy.")
                            .font(.system(size: 11))
                            .foregroundColor(.evrryMutedText)
                            .multilineTextAlignment(.center)
                        Spacer()
                    }
                    .padding(.bottom, 24)
                }
                .padding(.horizontal, 24)
            }
        }
        .background(Color.evrryBgTop.ignoresSafeArea())
        .alert("Sign in with Email", isPresented: $showEmailAlert) {
            TextField("name@example.com", text: $emailText)
                .textInputAutocapitalization(.never)
            Button("Continue") {
                if emailText.contains("@") && emailText.contains(".") {
                    viewModel.signInWithEmail(emailText)
                }
            }
            Button("Cancel", role: .cancel) {}
        } message: {
            Text("Enter your email address to sign in. A Nepal phone number will be attached for dispatches.")
        }
    }
}
