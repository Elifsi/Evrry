//
//  HomeViewModel.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Home AI Concierge ViewModel (ChatGPT Model Switcher & Real-time voice)
//

import SwiftUI
import Combine

public struct ChatMessage: Identifiable, Equatable {
    public let id: String
    public let isUser: Bool
    public let text: String
    public let timestamp: Date
    
    public init(id: String = UUID().uuidString, isUser: Bool, text: String, timestamp: Date = Date()) {
        self.id = id
        self.isUser = isUser
        self.text = text
        self.timestamp = timestamp
    }
}

public enum VoiceState {
    case idle
    case listening
    case thinking
    case speaking
}

@MainActor
public final class HomeViewModel: ObservableObject {
    @Published public var activeModel: VoiceAgentModel = VoiceAgentRegistry.ellie
    @Published public var messages: [ChatMessage] = []
    @Published public var voiceState: VoiceState = .idle
    @Published public var aiMode: String = "chat" // "chat" | "assistant"
    @Published public var caption: String = ""
    @Published public var isMuted: Bool = false
    @Published public var inputText: String = ""
    
    public init() {}
    
    public func selectModel(_ model: VoiceAgentModel) {
        self.activeModel = model
        if !self.messages.isEmpty {
            self.messages.append(ChatMessage(isUser: false, text: "Switched to \(model.name) (\(model.badge))."))
        }
    }

    public func setAiMode(_ mode: String) {
        self.aiMode = mode
        if mode == "assistant" {
            self.voiceState = .listening
            self.caption = ""
        } else {
            self.voiceState = .idle
        }
    }

    public func toggleMute() {
        self.isMuted.toggle()
    }

    public func clearChat() {
        self.messages = []
        self.inputText = ""
        self.caption = ""
        self.voiceState = .idle
    }
    
    public func sendMessage(_ text: String? = nil) {
        let content = (text ?? inputText).trimmingCharacters(in: .whitespacesAndNewlines)
        guard !content.isEmpty else { return }
        
        self.messages.append(ChatMessage(isUser: true, text: content))
        self.inputText = ""
        self.voiceState = .thinking
        
        Task {
            try? await Task.sleep(nanoseconds: 800_000_000)
            self.voiceState = .speaking
            let reply = generateReply(content, model: activeModel)
            self.messages.append(ChatMessage(isUser: false, text: reply))
            try? await Task.sleep(nanoseconds: 1_500_000_000)
            self.voiceState = .idle
        }
    }
    
    public func toggleVoiceOrb() {
        switch voiceState {
        case .idle:
            self.voiceState = .listening
            Task {
                try? await Task.sleep(nanoseconds: 3_000_000_000)
                sendMessage("Momo ani bike ride book gardeu na Patan samma")
            }
        case .listening, .speaking:
            self.voiceState = .idle
        case .thinking:
            break
        }
    }
    
    private func generateReply(_ query: String, model: VoiceAgentModel) -> String {
        let lower = query.lowercased()
        if lower.contains("momo") || lower.contains("khaja") {
            return "Kathmandu Momo Corner bata C-Momo (Rs 280) ready chha! Cart ma add gardiye — order confirm garum?"
        } else if lower.contains("ride") || lower.contains("patan") || lower.contains("bike") {
            return "Thamel bata Patan samma Moto rider Bikash Tamang 2 min ma aaudai chha (Rs 165). OTP: 1234."
        } else if lower.contains("grocery") || lower.contains("apple") {
            return "Jumla organic apples (Rs 240/kg) Himalayan Mart bata 15 min ma deliver hunchha!"
        } else {
            return "Namaste, I can help you find restaurants, book rides across Kathmandu, or order groceries. What would you like?"
        }
    }
}
