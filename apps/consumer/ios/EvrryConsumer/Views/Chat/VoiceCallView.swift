//
//  VoiceCallView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  WhatsApp-Grade Fullscreen Audio Voice Call View for iOS
//

import SwiftUI

public struct VoiceCallView: View {
    public let session: ActiveCallSession
    @ObservedObject public var callViewModel: CallViewModel
    public var onCallEnded: () -> Void

    @State private var isPulsing: Bool = false

    public init(
        session: ActiveCallSession,
        callViewModel: CallViewModel,
        onCallEnded: @escaping () -> Void
    ) {
        self.session = session
        self.callViewModel = callViewModel
        self.onCallEnded = onCallEnded
    }

    public var body: some View {
        ZStack {
            // BACKGROUND GRADIENT
            LinearGradient(
                colors: [
                    Color(red: 0.04, green: 0.09, blue: 0.16), // #0B172A
                    Color(red: 0.12, green: 0.16, blue: 0.23),
                    Color(red: 0.06, green: 0.09, blue: 0.16)
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 0) {
                // TOP HEADER: EXPLICIT BACK BUTTON & E2EE NOTICE
                HStack {
                    Button(action: onCallEnded) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 18, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(width: 44, height: 44)
                            .background(Color.white.opacity(0.15))
                            .clipShape(Circle())
                    }
                    .frame(width: 44, height: 44)

                    Spacer()

                    // E2EE Pill
                    HStack(spacing: 6) {
                        Image(systemName: "lock.fill")
                            .font(.system(size: 11))
                            .foregroundColor(Color(red: 0.99, green: 0.88, blue: 0.28))
                        Text("End-to-End Encrypted")
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(.white.opacity(0.9))
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(Color.white.opacity(0.12))
                    .cornerRadius(12)

                    Spacer()

                    // Balance space for centering
                    Color.clear.frame(width: 44, height: 44)
                }
                .padding(.horizontal, 16)
                .padding(.top, 16)

                Spacer().frame(height: 16)

                // CALLEE NAME
                Text(session.calleeName)
                    .font(.system(size: 26, weight: .bold))
                    .foregroundColor(.white)

                // STATUS / DURATION
                Text(statusText)
                    .font(.system(size: 15))
                    .foregroundColor(.white.opacity(0.7))

                Spacer()

                // CENTER: PULSING CALL AVATAR
                ZStack {
                    if session.state == .ringing || session.state == .connected {
                        Circle()
                            .fill(Color.evrryPrimaryBlue.opacity(0.2))
                            .frame(width: 170, height: 170)
                            .scaleEffect(isPulsing ? 1.15 : 1.0)
                            .animation(
                                Animation.easeInOut(duration: 1.4)
                                    .repeatForever(autoreverses: true),
                                value: isPulsing
                            )
                    }

                    Circle()
                        .fill(Color(red: 0.12, green: 0.16, blue: 0.23))
                        .frame(width: 130, height: 130)
                        .overlay(Circle().stroke(Color.evrryPrimaryBlue, lineWidth: 2))
                        .overlay(
                            Text(String(session.calleeName.prefix(2)).uppercased())
                                .font(.system(size: 42, weight: .black))
                                .foregroundColor(.white)
                        )
                }
                .onAppear {
                    isPulsing = true
                }

                Spacer()

                // BOTTOM CONTROLS DOCK
                VStack(spacing: 24) {
                    HStack(spacing: 36) {
                        // SPEAKER BUTTON
                        CallActionBtn(
                            systemImage: "speaker.wave.3.fill",
                            label: "Speaker",
                            isActive: session.isSpeakerOn,
                            action: { callViewModel.toggleSpeaker() }
                        )

                        // SIM CALL FALLBACK BUTTON (Cellular dialer)
                        CallActionBtn(
                            systemImage: "phone.fill",
                            label: "SIM Call",
                            isActive: false,
                            action: { callViewModel.dialCellularFallback() }
                        )

                        // MUTE BUTTON
                        CallActionBtn(
                            systemImage: session.isMuted ? "mic.slash.fill" : "mic.fill",
                            label: session.isMuted ? "Unmute" : "Mute",
                            isActive: session.isMuted,
                            action: { callViewModel.toggleMute() }
                        )
                    }

                    // RED END CALL BUTTON
                    Button(action: {
                        callViewModel.endCall()
                        onCallEnded()
                    }) {
                        Circle()
                            .fill(Color(red: 0.94, green: 0.27, blue: 0.27))
                            .frame(width: 68, height: 68)
                            .overlay(
                                Image(systemName: "phone.down.fill")
                                    .font(.system(size: 28))
                                    .foregroundColor(.white)
                            )
                            .shadow(color: Color.red.opacity(0.4), radius: 8, x: 0, y: 4)
                    }
                }
                .padding(.bottom, 36)
            }
            .padding(.horizontal, 24)
        }
        .onChange(of: session.state) { newState in
            if newState == .ended {
                onCallEnded()
            }
        }
    }

    private var statusText: String {
        switch session.state {
        case .connecting: return "Connecting..."
        case .ringing: return "Ringing..."
        case .connected: return callViewModel.formatDuration(session.durationSeconds)
        case .ended: return "Call Ended"
        }
    }
}

private struct CallActionBtn: View {
    let systemImage: String
    let label: String
    let isActive: Bool
    let action: () -> Void

    var body: some View {
        VStack(spacing: 6) {
            Button(action: action) {
                Circle()
                    .fill(isActive ? Color.white : Color.white.opacity(0.15))
                    .frame(width: 54, height: 54)
                    .overlay(
                        Image(systemName: systemImage)
                            .font(.system(size: 22))
                            .foregroundColor(isActive ? .evrryDarkCharcoal : .white)
                    )
            }

            Text(label)
                .font(.system(size: 11, weight: .medium))
                .foregroundColor(.white.opacity(0.8))
        }
    }
}
