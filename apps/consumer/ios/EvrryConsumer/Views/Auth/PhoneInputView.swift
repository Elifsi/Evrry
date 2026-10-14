//
//  PhoneInputView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Universal Registration & Login Screen (Nepal +977 WhatsApp OTP & Email/Password)
//

import SwiftUI

public struct PhoneInputView: View {
    @ObservedObject public var viewModel: AuthViewModel
    public let onContinue: () -> Void
    
    // 1. User arrives at the Register screen by default
    @State private var isSignUp: Bool = true
    
    // 2. Default is Phone Number input; tap "Continue with Email" switches to Email mode
    @State private var isEmailAuth: Bool = false
    
    @State private var phoneInput: String = ""
    @State private var emailInput: String = ""
    @State private var passwordInput: String = ""
    @State private var showPassword: Bool = false
    
    // Forgot Password State
    @State private var showForgotPasswordSheet: Bool = false
    @State private var forgotPasswordInput: String = ""
    @State private var forgotPasswordStatus: String? = nil
    @State private var isResetting: Bool = false
    
    public init(viewModel: AuthViewModel, onContinue: @escaping () -> Void) {
        self.viewModel = viewModel
        self.onContinue = onContinue
    }
    
    public var body: some View {
        ScrollView {
            VStack(spacing: 0) {
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
                    
                    // Title & Subtitle
                    if isSignUp {
                        Text("Create your Account")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text(isEmailAuth ? "Sign up with your email and password to get started." : "Enter your Nepal mobile number and password to register.")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)
                            .padding(.top, 6)
                    } else {
                        Text("Welcome to evrry")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text(isEmailAuth ? "Sign in with your email and password to access your account." : "Sign in with your Nepal mobile number to access your account.")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryMutedText)
                            .padding(.top, 6)
                    }
                    
                    Spacer().frame(height: 28)
                    
                    if !isEmailAuth {
                        // PHONE NUMBER INPUT
                        Text("NEPAL MOBILE NUMBER")
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
                                get: { phoneInput.isEmpty ? viewModel.phoneNumber : phoneInput },
                                set: {
                                    phoneInput = $0
                                    viewModel.updatePhoneNumber($0)
                                }
                            ))
                            .keyboardType(.numberPad)
                            .font(.system(size: 16))
                        }
                        .padding(.horizontal, 14)
                        .padding(.vertical, 14)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1.5))
                        
                        Spacer().frame(height: 16)
                        
                        // PASSWORD INPUT
                        Text("PASSWORD")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                        
                        Spacer().frame(height: 8)
                        
                        HStack(spacing: 10) {
                            Image(systemName: "lock.fill")
                                .foregroundColor(.evrryMutedText)
                                .frame(width: 20)
                            if showPassword {
                                TextField("••••••••", text: $passwordInput)
                                    .font(.system(size: 15))
                            } else {
                                SecureField("••••••••", text: $passwordInput)
                                    .font(.system(size: 15))
                            }
                            Button(action: { showPassword.toggle() }) {
                                Image(systemName: showPassword ? "eye.slash.fill" : "eye.fill")
                                    .foregroundColor(.evrryMutedText)
                            }
                        }
                        .padding(.horizontal, 14)
                        .padding(.vertical, 14)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                        .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1.2))
                        
                        if !isSignUp {
                            HStack {
                                Spacer()
                                Button(action: {
                                    forgotPasswordInput = viewModel.phoneNumber
                                    forgotPasswordStatus = nil
                                    showForgotPasswordSheet = true
                                }) {
                                    Text("Forgot Password?")
                                        .font(.system(size: 13, weight: .semibold))
                                        .foregroundColor(.evrryPrimaryBlue)
                                }
                            }
                            .padding(.top, 6)
                            Spacer().frame(height: 12)
                        } else {
                            Spacer().frame(height: 22)
                        }
                        
                        // PRIMARY CTA: Sends WhatsApp OTP on phone number
                        let isPhoneValid = viewModel.phoneNumber.count == 10 && passwordInput.count >= 6
                        Button(action: {
                            viewModel.selectChannel(.whatsapp)
                            viewModel.requestOtp()
                            onContinue()
                        }) {
                            HStack {
                                if viewModel.isLoading {
                                    ProgressView().tint(.white)
                                } else {
                                    Text(isSignUp ? "Create Account" : "Sign In")
                                        .font(.system(size: 15, weight: .bold))
                                        .foregroundColor(.white)
                                }
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .background(isPhoneValid && !viewModel.isLoading ? Color.evrryPrimaryBlue : Color.evrryPrimaryBlue.opacity(0.4))
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                        }
                        .disabled(!isPhoneValid || viewModel.isLoading)
                        
                    } else {
                        // EMAIL INPUT MODE
                        Text("EMAIL ADDRESS")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                        
                        Spacer().frame(height: 8)
                        
                        HStack(spacing: 10) {
                            Image(systemName: "envelope.fill")
                                .foregroundColor(.evrryMutedText)
                                .frame(width: 20)
                            TextField("name@example.com", text: $emailInput)
                                .keyboardType(.emailAddress)
                                .autocapitalization(.none)
                                .font(.system(size: 15))
                        }
                        .padding(.horizontal, 14)
                        .padding(.vertical, 14)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                        .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1.2))
                        
                        Spacer().frame(height: 16)
                        
                        // PASSWORD INPUT
                        Text("PASSWORD")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                        
                        Spacer().frame(height: 8)
                        
                        HStack(spacing: 10) {
                            Image(systemName: "lock.fill")
                                .foregroundColor(.evrryMutedText)
                                .frame(width: 20)
                            if showPassword {
                                TextField("••••••••", text: $passwordInput)
                                    .font(.system(size: 15))
                            } else {
                                SecureField("••••••••", text: $passwordInput)
                                    .font(.system(size: 15))
                            }
                            Button(action: { showPassword.toggle() }) {
                                Image(systemName: showPassword ? "eye.slash.fill" : "eye.fill")
                                    .foregroundColor(.evrryMutedText)
                            }
                        }
                        .padding(.horizontal, 14)
                        .padding(.vertical, 14)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                        .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1.2))
                        
                        if !isSignUp {
                            HStack {
                                Spacer()
                                Button(action: {
                                    forgotPasswordInput = emailInput
                                    forgotPasswordStatus = nil
                                    showForgotPasswordSheet = true
                                }) {
                                    Text("Forgot Password?")
                                        .font(.system(size: 13, weight: .semibold))
                                        .foregroundColor(.evrryPrimaryBlue)
                                }
                            }
                            .padding(.top, 6)
                            Spacer().frame(height: 12)
                        } else {
                            Spacer().frame(height: 22)
                        }
                        
                        let isEmailValid = emailInput.contains("@") && emailInput.contains(".") && passwordInput.count >= 6
                        Button(action: {
                            if isSignUp {
                                viewModel.signUpWithPassword(fullName: "", identifier: emailInput, password: passwordInput)
                            } else {
                                viewModel.signInWithPassword(identifier: emailInput, password: passwordInput)
                            }
                        }) {
                            HStack {
                                if viewModel.isLoading {
                                    ProgressView().tint(.white)
                                } else {
                                    Text(isSignUp ? "Create Account" : "Sign In")
                                        .font(.system(size: 15, weight: .bold))
                                        .foregroundColor(.white)
                                }
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .background(isEmailValid && !viewModel.isLoading ? Color.evrryPrimaryBlue : Color.evrryPrimaryBlue.opacity(0.4))
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                        }
                        .disabled(!isEmailValid || viewModel.isLoading)
                    }
                    
                    Spacer().frame(height: 24)
                    
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
                    
                    Spacer().frame(height: 18)
                    
                    // Social Sign-In Buttons
                    VStack(spacing: 10) {
                        if !isEmailAuth {
                            // CONTINUE WITH EMAIL BUTTON
                            Button(action: {
                                isEmailAuth = true
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
                        } else {
                            // CONTINUE WITH PHONE NUMBER BUTTON
                            Button(action: {
                                isEmailAuth = false
                            }) {
                                HStack(spacing: 10) {
                                    Image(systemName: "phone.fill")
                                        .font(.system(size: 15))
                                        .foregroundColor(.evrryPrimaryBlue)
                                    
                                    Text("Continue with Phone Number")
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
                        
                        // CONTINUE WITH GOOGLE
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
                    }
                    
                    Spacer().frame(height: 28)
                    
                    // Switch Footer: Register now <-> Login here
                    HStack(spacing: 4) {
                        Spacer()
                        Text(isSignUp ? "Already on Evrry?" : "New on Evrry?")
                            .font(.system(size: 13))
                            .foregroundColor(.evrryDarkCharcoal)
                        Button(action: {
                            isSignUp.toggle()
                        }) {
                            Text(isSignUp ? "Login here" : "Register now")
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.evrryPrimaryBlue)
                        }
                        Spacer()
                    }
                    
                    Spacer().frame(height: 12)
                    
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
        .sheet(isPresented: $showForgotPasswordSheet) {
            NavigationView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("Enter your registered mobile number or email address. We'll send an OTP verification link to reset your account password.")
                        .font(.system(size: 13))
                        .foregroundColor(.evrryMutedText)
                        .lineSpacing(3)
                    
                    TextField("98XXXXXXXX or name@example.com", text: $forgotPasswordInput)
                        .padding()
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                        .keyboardType(.emailAddress)
                        .autocapitalization(.none)
                    
                    if let status = forgotPasswordStatus {
                        HStack {
                            Text(status)
                                .font(.system(size: 12, weight: .medium))
                                .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                        }
                        .padding(12)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color(red: 0.93, green: 0.99, blue: 0.96))
                        .clipShape(RoundedRectangle(cornerRadius: 10))
                    }
                    
                    Button(action: {
                        isResetting = true
                        viewModel.requestPasswordReset(identifier: forgotPasswordInput) { _, msg in
                            isResetting = false
                            forgotPasswordStatus = msg
                        }
                    }) {
                        HStack {
                            if isResetting {
                                ProgressView().tint(.white)
                            } else {
                                Text("Send Reset Instructions")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.white)
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(forgotPasswordInput.trimmingCharacters(in: .whitespaces).isEmpty ? Color.evrryPrimaryBlue.opacity(0.4) : Color.evrryPrimaryBlue)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                    }
                    .disabled(forgotPasswordInput.trimmingCharacters(in: .whitespaces).isEmpty || isResetting)
                    
                    Spacer()
                }
                .padding(20)
                .navigationTitle("Reset Password")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button("Done") {
                            showForgotPasswordSheet = false
                            forgotPasswordStatus = nil
                        }
                        .foregroundColor(.evrryPrimaryBlue)
                    }
                }
            }
        }
    }
}
