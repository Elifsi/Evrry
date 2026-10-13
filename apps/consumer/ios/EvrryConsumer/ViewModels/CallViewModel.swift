//
//  CallViewModel.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Call ViewModel managing WhatsApp-grade WebRTC VoIP voice and video calls for iOS
//

import SwiftUI
import Combine

public final class CallViewModel: ObservableObject {
    @Published public var callSession: ActiveCallSession? = nil
    @Published public var callLogs: [CallLogItem] = []

    private var timerCancellable: AnyCancellable? = nil

    public init() {
        self.callLogs = Self.getInitialCallLogs()
    }

    public func startCall(
        chatId: String,
        calleeName: String,
        calleeAvatar: String? = nil,
        calleePhone: String? = nil,
        callType: CallType = .audio
    ) {
        timerCancellable?.cancel()

        let newSession = ActiveCallSession(
            chatId: chatId,
            calleeName: calleeName,
            calleeAvatar: calleeAvatar,
            calleePhone: calleePhone,
            callType: callType,
            state: .connecting,
            durationSeconds: 0,
            isMuted: false,
            isSpeakerOn: callType == .video,
            isVideoEnabled: callType == .video,
            isFrontCamera: true
        )
        self.callSession = newSession

        // Simulate WebRTC Signaling & Ringing
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.8) { [weak self] in
            guard let self = self, self.callSession != nil else { return }
            self.callSession?.state = .ringing
        }

        // Simulate counterpart answering
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.8) { [weak self] in
            guard let self = self, self.callSession != nil, self.callSession?.state != .ended else { return }
            self.callSession?.state = .connected
            self.startTimer()
        }
    }

    private func startTimer() {
        timerCancellable?.cancel()
        timerCancellable = Timer.publish(every: 1.0, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                guard let self = self, var session = self.callSession, session.state == .connected else { return }
                session.durationSeconds += 1
                self.callSession = session
            }
    }

    public func toggleMute() {
        guard var session = callSession else { return }
        session.isMuted.toggle()
        callSession = session
    }

    public func toggleSpeaker() {
        guard var session = callSession else { return }
        session.isSpeakerOn.toggle()
        callSession = session
    }

    public func toggleVideo() {
        guard var session = callSession else { return }
        session.isVideoEnabled.toggle()
        callSession = session
    }

    public func flipCamera() {
        guard var session = callSession else { return }
        session.isFrontCamera.toggle()
        callSession = session
    }

    public func endCall() {
        timerCancellable?.cancel()
        if let current = callSession {
            let isAnswered = current.durationSeconds > 0
            let logItem = CallLogItem(
                id: UUID().uuidString,
                chatId: current.chatId,
                contactName: current.calleeName,
                contactAvatar: current.calleeAvatar,
                contactPhone: current.calleePhone,
                callType: current.callType,
                direction: isAnswered ? .outgoing : .missed,
                status: isAnswered ? .completed : .missed,
                durationSeconds: current.durationSeconds,
                timestampFormatted: "Just now",
                isDriverOrMerchant: false,
                subtitle: isAnswered ? "\(formatDuration(current.durationSeconds)) • Outgoing" : "Cancelled / Missed"
            )
            callLogs.insert(logItem, at: 0)
        }

        callSession?.state = .ended
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) { [weak self] in
            self?.callSession = nil
        }
    }

    public func deleteCallLog(id: String) {
        callLogs.removeAll { $0.id == id }
    }

    public func clearCallLogs() {
        callLogs.removeAll()
    }

    /**
     * Fallback to direct Nepal cellular GSM SIM calling if WebRTC connection is degraded.
     */
    public func dialCellularFallback() {
        let phone = callSession?.calleePhone ?? "9841000001"
        endCall()
        if let url = URL(string: "tel://\(phone)"), UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url)
        }
    }

    public func formatDuration(_ seconds: Int) -> String {
        let mins = seconds / 60
        let secs = seconds % 60
        return String(format: "%02d:%02d", mins, secs)
    }

    private static func getInitialCallLogs() -> [CallLogItem] {
        return [
            CallLogItem(
                id: "call-log-1",
                chatId: "chat-order-482",
                contactName: "Ramesh Thapa",
                contactPhone: "9841234567",
                callType: .audio,
                direction: .outgoing,
                status: .completed,
                durationSeconds: 195,
                timestampFormatted: "Today, 12:45 PM",
                isDriverOrMerchant: true,
                subtitle: "Delivery Captain • 3m 15s"
            ),
            CallLogItem(
                id: "call-log-2",
                chatId: "chat-direct-1",
                contactName: "Suman Sharma",
                contactPhone: "9801239876",
                callType: .video,
                direction: .incoming,
                status: .completed,
                durationSeconds: 745,
                timestampFormatted: "Today, 10:14 AM",
                subtitle: "Video Call • 12m 25s"
            ),
            CallLogItem(
                id: "call-log-3",
                chatId: "chat-ride-901",
                contactName: "Bikash Gurung",
                contactPhone: "9851122334",
                callType: .audio,
                direction: .missed,
                status: .missed,
                durationSeconds: 0,
                timestampFormatted: "Yesterday, 6:30 PM",
                isDriverOrMerchant: true,
                subtitle: "Ride Captain • Missed Call"
            ),
            CallLogItem(
                id: "call-log-4",
                chatId: "chat-direct-2",
                contactName: "Pooja Adhikari",
                contactPhone: "9818445566",
                callType: .audio,
                direction: .outgoing,
                status: .completed,
                durationSeconds: 312,
                timestampFormatted: "Yesterday, 2:15 PM",
                subtitle: "Voice Call • 5m 12s"
            ),
            CallLogItem(
                id: "call-log-5",
                chatId: nil,
                contactName: "Kathmandu Fresh Mart",
                contactPhone: "9802001122",
                callType: .audio,
                direction: .incoming,
                status: .completed,
                durationSeconds: 48,
                timestampFormatted: "Oct 8, 4:20 PM",
                isDriverOrMerchant: true,
                subtitle: "Store Partner • 48s"
            )
        ]
    }
}
