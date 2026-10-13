//
//  VideoCallView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  WhatsApp-Grade Fullscreen Video Call View for iOS
//

import SwiftUI

public struct VideoCallView: View {
    public let session: ActiveCallSession
    @ObservedObject public var callViewModel: CallViewModel
    public var onCallEnded: () -> Void

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
            // 1. FULLSCREEN REMOTE VIDEO FEED (WebRTC video canvas)
            ZStack {
                if session.isVideoEnabled {
                    LinearGradient(
                        colors: [
                            Color(red: 0.12, green: 0.16, blue: 0.23),
                            Color(red: 0.06, green: 0.09, blue: 0.16),
                            Color(red: 0.01, green: 0.02, blue: 0.05)
                        ],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                    .ignoresSafeArea()

                    VStack(spacing: 12) {
                        Circle()
                            .fill(Color.evrryPrimaryBlue.opacity(0.2))
                            .frame(width: 100, height: 100)
                            .overlay(Circle().stroke(Color.evrryPrimaryBlue, lineWidth: 2))
                            .overlay(
                                Text(String(session.calleeName.prefix(2)).uppercased())
                                    .font(.system(size: 32, weight: .bold))
                                    .foregroundColor(.white)
                            )

                        Text(session.state == .connected ? "WebRTC Video Stream Active" : "Connecting to video...")
                            .font(.system(size: 13))
                            .foregroundColor(.white.opacity(0.7))
                    }
                } else {
                    Color.black.ignoresSafeArea()
                    VStack(spacing: 8) {
                        Image(systemName: "video.slash.fill")
                            .font(.system(size: 40))
                            .foregroundColor(.white.opacity(0.5))
                        Text("Camera is off")
                            .font(.system(size: 14))
                            .foregroundColor(.white.opacity(0.6))
                    }
                }
            }

            // 2. TOP HUD: CALLER INFO, DURATION, EXPLICIT BACK BUTTON & E2EE BADGE
            VStack {
                HStack(alignment: .center) {
                    Button(action: onCallEnded) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 18, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(width: 44, height: 44)
                            .background(Color.black.opacity(0.45))
                            .clipShape(Circle())
                    }
                    .frame(width: 44, height: 44)

                    Spacer().frame(width: 12)

                    VStack(alignment: .leading, spacing: 2) {
                        Text(session.calleeName)
                            .font(.system(size: 17, weight: .bold))
                            .foregroundColor(.white)

                        Text(statusText)
                            .font(.system(size: 12))
                            .foregroundColor(.white.opacity(0.8))
                    }

                    Spacer()

                    // E2EE BADGE
                    HStack(spacing: 4) {
                        Image(systemName: "lock.fill")
                            .font(.system(size: 10))
                            .foregroundColor(Color(red: 0.99, green: 0.88, blue: 0.28))
                        Text("E2EE")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.white)
                    }
                    .padding(.horizontal, 10)
                    .padding(.vertical, 6)
                    .background(Color.black.opacity(0.45))
                    .cornerRadius(12)
                }
                .padding(.horizontal, 16)
                .padding(.top, 16)

                Spacer()
            }

            // 3. FLOATING PICTURE-IN-PICTURE (PiP) LOCAL PREVIEW
            VStack {
                HStack {
                    Spacer()
                    VStack(spacing: 4) {
                        Text("📷")
                            .font(.system(size: 24))
                        Text(session.isFrontCamera ? "Front" : "Rear")
                            .font(.system(size: 10, weight: .bold))
                            .foregroundColor(.white.opacity(0.8))
                    }
                    .frame(width: 100, height: 145)
                    .background(Color(red: 0.12, green: 0.16, blue: 0.23))
                    .cornerRadius(16)
                    .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.white.opacity(0.3), lineWidth: 2))
                    .padding(.top, 70)
                    .padding(.trailing, 16)
                }
                Spacer()
            }

            // 4. BOTTOM FLOATING CONTROLS DOCK
            VStack {
                Spacer()
                HStack(spacing: 18) {
                    // FLIP CAMERA
                    Button(action: { callViewModel.flipCamera() }) {
                        Circle()
                            .fill(Color.white.opacity(0.15))
                            .frame(width: 48, height: 48)
                            .overlay(
                                Image(systemName: "camera.rotate.fill")
                                    .font(.system(size: 20))
                                    .foregroundColor(.white)
                            )
                    }

                    // TOGGLE VIDEO
                    Button(action: { callViewModel.toggleVideo() }) {
                        Circle()
                            .fill(!session.isVideoEnabled ? Color.white : Color.white.opacity(0.15))
                            .frame(width: 48, height: 48)
                            .overlay(
                                Image(systemName: session.isVideoEnabled ? "video.fill" : "video.slash.fill")
                                    .font(.system(size: 20))
                                    .foregroundColor(!session.isVideoEnabled ? .evrryDarkCharcoal : .white)
                            )
                    }

                    // MUTE AUDIO
                    Button(action: { callViewModel.toggleMute() }) {
                        Circle()
                            .fill(session.isMuted ? Color.white : Color.white.opacity(0.15))
                            .frame(width: 48, height: 48)
                            .overlay(
                                Image(systemName: session.isMuted ? "mic.slash.fill" : "mic.fill")
                                    .font(.system(size: 20))
                                    .foregroundColor(session.isMuted ? .evrryDarkCharcoal : .white)
                            )
                    }

                    // END CALL BUTTON
                    Button(action: {
                        callViewModel.endCall()
                        onCallEnded()
                    }) {
                        Circle()
                            .fill(Color(red: 0.94, green: 0.27, blue: 0.27))
                            .frame(width: 52, height: 52)
                            .overlay(
                                Image(systemName: "phone.down.fill")
                                    .font(.system(size: 22))
                                    .foregroundColor(.white)
                            )
                    }
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 14)
                .background(Color.black.opacity(0.65))
                .cornerRadius(28)
                .padding(.bottom, 28)
            }
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
