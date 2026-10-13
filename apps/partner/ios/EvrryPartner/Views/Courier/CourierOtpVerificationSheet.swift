//
//  CourierOtpVerificationSheet.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  4-Digit Customer Delivery Handoff OTP & Anti-Fraud COD Enforcement
//  Grounded in enatega-multivendor-rider
//

import SwiftUI

public struct CourierOtpVerificationSheet: View {
    @ObservedObject var viewModel: CourierDeliveryViewModel
    @Environment(\.dismiss) private var dismiss
    
    public init(viewModel: CourierDeliveryViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        guard let order = viewModel.activeOrder else {
            return AnyView(EmptyView())
        }
        
        return AnyView(
            VStack(spacing: 20) {
                // Header
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Customer Delivery Handoff")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.white)
                        Text("Order #\(order.orderNo) • \(order.consumerName)")
                            .font(.system(size: 12))
                            .foregroundColor(.gray)
                    }
                    Spacer()
                    Button {
                        viewModel.showOtpSheet = false
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .font(.system(size: 24))
                            .foregroundColor(.gray)
                    }
                }
                
                // COD Warning Banner (CRITICAL ANTI-FRAUD REQUIREMENT)
                if order.isCod {
                    VStack(alignment: .leading, spacing: 10) {
                        HStack {
                            Image(systemName: "exclamationmark.triangle.fill")
                                .foregroundColor(.red)
                                .font(.system(size: 18))
                            Text("CASH ON DELIVERY (COD)")
                                .font(.system(size: 13, weight: .black))
                                .foregroundColor(.red)
                            Spacer()
                            Text("NPR \(Int(order.totalRs))")
                                .font(.system(size: 16, weight: .black))
                                .foregroundColor(.white)
                        }
                        
                        Text("Collect physical cash before entering customer's secret OTP code. Once verified, order is closed.")
                            .font(.system(size: 11))
                            .foregroundColor(Color.red.opacity(0.9))
                        
                        Toggle(isOn: $viewModel.codCashCollectedConfirmed) {
                            Text("I have received physical cash (NPR \(Int(order.totalRs)))")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(.white)
                        }
                        .toggleStyle(SwitchToggleStyle(tint: .green))
                        .padding(.top, 4)
                    }
                    .padding(14)
                    .background(Color.red.opacity(0.15))
                    .cornerRadius(14)
                    .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.red.opacity(0.5), lineWidth: 1.5))
                }
                
                // 4-Digit OTP Prompt
                VStack(spacing: 8) {
                    Text("Enter 4-Digit Customer OTP")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                    
                    Text("Customer reads this code from their active tracking screen")
                        .font(.system(size: 11))
                        .foregroundColor(.gray)
                        .multilineTextAlignment(.center)
                    
                    // 4 Digit Boxes
                    HStack(spacing: 12) {
                        ForEach(0..<4) { index in
                            let char = getDigit(at: index)
                            Text(char)
                                .font(.system(size: 26, weight: .heavy))
                                .foregroundColor(.white)
                                .frame(width: 55, height: 60)
                                .background(Color(red: 0.16, green: 0.18, blue: 0.28))
                                .cornerRadius(12)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 12)
                                        .stroke(
                                            index == viewModel.otpInput.count ? Color.blue : (char.isEmpty ? Color.white.opacity(0.1) : Color.green),
                                            lineWidth: index == viewModel.otpInput.count ? 2 : 1
                                        )
                                )
                        }
                    }
                    .padding(.vertical, 8)
                    
                    if let err = viewModel.otpError {
                        Text(err)
                            .font(.system(size: 12, weight: .semibold))
                            .foregroundColor(.red)
                            .padding(.top, 4)
                    }
                }
                
                // Custom Numeric Keypad
                VStack(spacing: 10) {
                    ForEach([["1", "2", "3"], ["4", "5", "6"], ["7", "8", "9"]], id: \.self) { row in
                        HStack(spacing: 12) {
                            ForEach(row, id: \.self) { digit in
                                KeypadButton(label: digit) {
                                    appendDigit(digit)
                                }
                            }
                        }
                    }
                    
                    HStack(spacing: 12) {
                        KeypadButton(label: "C") {
                            viewModel.otpInput = ""
                            viewModel.otpError = nil
                        }
                        
                        KeypadButton(label: "0") {
                            appendDigit("0")
                        }
                        
                        KeypadButton(label: "⌫") {
                            if !viewModel.otpInput.isEmpty {
                                viewModel.otpInput.removeLast()
                                viewModel.otpError = nil
                            }
                        }
                    }
                }
                .padding(.horizontal, 24)
                
                // Complete Delivery Button
                Button {
                    viewModel.verifyHandoffOtp()
                } label: {
                    HStack {
                        if viewModel.isOtpVerifying {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Image(systemName: "checkmark.seal.fill")
                            Text("Confirm & Complete Delivery 🏁")
                                .font(.system(size: 16, weight: .bold))
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 16)
                    .background(viewModel.otpInput.count == 4 && (!order.isCod || viewModel.codCashCollectedConfirmed) ? Color.green : Color.gray.opacity(0.4))
                    .foregroundColor(.white)
                    .cornerRadius(16)
                }
                .disabled(viewModel.otpInput.count < 4 || viewModel.isOtpVerifying)
                .padding(.horizontal, 16)
                
                Spacer()
            }
            .padding(20)
            .background(Color(red: 0.08, green: 0.10, blue: 0.16).ignoresSafeArea())
        )
    }
    
    private func getDigit(at index: Int) -> String {
        guard index < viewModel.otpInput.count else { return "" }
        let strIndex = viewModel.otpInput.index(viewModel.otpInput.startIndex, offsetBy: index)
        return String(viewModel.otpInput[strIndex])
    }
    
    private func appendDigit(_ digit: String) {
        guard viewModel.otpInput.count < 4 else { return }
        viewModel.otpInput.append(digit)
        viewModel.otpError = nil
        if viewModel.otpInput.count == 4 {
            viewModel.verifyHandoffOtp()
        }
    }
}

private struct KeypadButton: View {
    let label: String
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(label)
                .font(.system(size: 20, weight: .bold))
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 48)
                .background(Color(red: 0.15, green: 0.18, blue: 0.28))
                .cornerRadius(12)
        }
    }
}
