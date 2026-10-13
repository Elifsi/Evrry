//
//  OrderSuccessView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Order Success Screen (SwiftUI Parity with Commerce OrderSuccessScreen)
//

import SwiftUI

public struct OrderSuccessView: View {
    public let orderId: String
    public let onTrackOrder: (String) -> Void
    public let onBackToHome: () -> Void
    
    @State private var scale: CGFloat = 0.2
    @State private var opacity: Double = 0.0
    @State private var remainingSeconds: Int = 60
    private let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    
    public init(
        orderId: String,
        onTrackOrder: @escaping (String) -> Void,
        onBackToHome: @escaping () -> Void
    ) {
        self.orderId = orderId
        self.onTrackOrder = onTrackOrder
        self.onBackToHome = onBackToHome
    }
    
    public var body: some View {
        VStack(spacing: 20) {
            Spacer()
            
            // Success Animated Badge
            ZStack {
                Circle()
                    .fill(Color.evrryGreenSuccess.opacity(0.12))
                    .frame(width: 110, height: 110)
                
                Image(systemName: "checkmark.circle.fill")
                    .font(.system(size: 68))
                    .foregroundColor(.evrryGreenSuccess)
            }
            .scaleEffect(scale)
            .opacity(opacity)
            
            Text("Order Placed Successfully!")
                .font(.system(size: 22, weight: .black))
                .foregroundColor(.evrryDarkCharcoal)
            
            Text("Order ID: #\(orderId)")
                .font(.system(size: 13, weight: .bold))
                .foregroundColor(.evrryPrimaryBlue)
                .padding(.horizontal, 12)
                .padding(.vertical, 5)
                .background(Color(red: 0.94, green: 0.96, blue: 1.0))
                .clipShape(RoundedRectangle(cornerRadius: 8))
                .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color.evrryPrimaryBlue.opacity(0.3), lineWidth: 1))
            
            Text("The kitchen has received your order and started cooking.\nYour Evrry rider will be dispatched shortly.")
                .font(.system(size: 13))
                .foregroundColor(.evrryMutedText)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 32)
            
            // 60-SECOND POST-ORDER BUFFER (Express Commerce Standard)
            if remainingSeconds > 0 {
                VStack(alignment: .leading, spacing: 6) {
                    HStack {
                        HStack(spacing: 6) {
                            Text("⏱️").font(.system(size: 14))
                            Text("Forgot an item? Add in \(remainingSeconds)s")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(Color(red: 0.57, green: 0.25, blue: 0.05))
                        }
                        Spacer()
                        Text("\(remainingSeconds)s")
                            .font(.system(size: 11, weight: .black))
                            .foregroundColor(.white)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 3)
                            .background(Color(red: 0.57, green: 0.25, blue: 0.05))
                            .clipShape(RoundedRectangle(cornerRadius: 6))
                    }
                    Text("Zero extra delivery fee! Your Jhamsikhel Hub bag is currently being prepared.")
                        .font(.system(size: 11))
                        .foregroundColor(Color(red: 0.47, green: 0.21, blue: 0.06))
                    
                    Button(action: onBackToHome) {
                        Text("+ Add Forgotten Items to Open Bag")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 36)
                            .background(Color(red: 0.57, green: 0.25, blue: 0.05))
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                    }
                    .padding(.top, 4)
                }
                .padding(14)
                .background(Color(red: 1.0, green: 0.98, blue: 0.92))
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color(red: 0.99, green: 0.83, blue: 0.30), lineWidth: 1))
                .padding(.horizontal, 24)
                .onReceive(timer) { _ in
                    if remainingSeconds > 0 {
                        remainingSeconds -= 1
                    }
                }
            } else {
                // ETA Card
                HStack(spacing: 12) {
                    Text("⏱️")
                        .font(.system(size: 24))
                    VStack(alignment: .leading, spacing: 2) {
                        Text("ESTIMATED ARRIVAL")
                            .font(.system(size: 10, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                        Text("10 - 15 Minutes")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                    }
                    Spacer()
                }
                .padding(16)
                .background(Color.white)
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                .padding(.horizontal, 24)
                .padding(.top, 10)
            }
            
            Spacer()
            
            // CTAs
            VStack(spacing: 12) {
                Button(action: { onTrackOrder(orderId) }) {
                    Text("Track Live Status →")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(Color.evrryPrimaryBlue)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                }
                
                Button(action: onBackToHome) {
                    Text("Return to Home")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(.evrryMutedText)
                        .frame(maxWidth: .infinity)
                        .frame(height: 44)
                }
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
        .background(Color(red: 0.98, green: 0.98, blue: 1.0).ignoresSafeArea())
        .onAppear {
            withAnimation(.spring(response: 0.5, dampingFraction: 0.6)) {
                scale = 1.0
                opacity = 1.0
            }
        }
    }
}
