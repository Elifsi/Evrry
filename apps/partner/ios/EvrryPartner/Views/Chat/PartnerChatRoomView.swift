//
//  PartnerChatRoomView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Realtime In-Trip Driver-Passenger Chat Room
//

import SwiftUI

public struct PartnerChatRoomView: View {
    public let passengerName: String
    public let passengerPhone: String
    public let rideId: String
    public let messages: [PartnerSocialMessage]
    public let onSendMessage: (String) -> Void
    public let onBack: () -> Void
    public let onCallClick: () -> Void
    
    @State private var inputText: String = ""
    
    private let cannedReplies = [
        "I'm waiting outside 📍",
        "I have arrived at pickup point ✅",
        "Heavy traffic, 2-3 mins delay 🚦",
        "Please confirm your exact gate number 🏠"
    ]
    
    public init(
        passengerName: String,
        passengerPhone: String,
        rideId: String,
        messages: [PartnerSocialMessage],
        onSendMessage: @escaping (String) -> Void,
        onBack: @escaping () -> Void,
        onCallClick: @escaping () -> Void
    ) {
        self.passengerName = passengerName
        self.passengerPhone = passengerPhone
        self.rideId = rideId
        self.messages = messages
        self.onSendMessage = onSendMessage
        self.onBack = onBack
        self.onCallClick = onCallClick
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            // TOP BAR
            HStack(spacing: 12) {
                Button(action: onBack) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(.primary)
                }
                
                ZStack {
                    Circle()
                        .fill(Color.blue.opacity(0.12))
                        .frame(width: 38, height: 38)
                    Text("👤")
                        .font(.system(size: 18))
                }
                
                VStack(alignment: .leading, spacing: 2) {
                    Text(passengerName)
                        .font(.system(size: 15, weight: .bold))
                    HStack(spacing: 4) {
                        Circle()
                            .fill(Color.green)
                            .frame(width: 6, height: 6)
                        Text("Passenger · Live Trip")
                            .font(.system(size: 10, weight: .semibold))
                            .foregroundColor(.green)
                    }
                }
                
                Spacer()
                
                Button(action: onCallClick) {
                    Image(systemName: "phone.fill")
                        .foregroundColor(.blue)
                        .font(.system(size: 16))
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
            .background(Color.white)
            
            Divider()
            
            // MESSAGES SCROLL
            ScrollViewReader { proxy in
                ScrollView {
                    LazyVStack(spacing: 10) {
                        ForEach(messages) { msg in
                            HStack {
                                if msg.isFromDriver {
                                    Spacer()
                                }
                                
                                VStack(alignment: msg.isFromDriver ? .trailing : .leading, spacing: 3) {
                                    Text(msg.text)
                                        .font(.system(size: 13))
                                        .foregroundColor(msg.isFromDriver ? .primary : .primary)
                                    
                                    HStack(spacing: 4) {
                                        Text(msg.timestamp, style: .time)
                                            .font(.system(size: 9))
                                            .foregroundColor(.secondary)
                                        if msg.isFromDriver {
                                            Text("✓✓")
                                                .font(.system(size: 9, weight: .bold))
                                                .foregroundColor(.blue)
                                        }
                                    }
                                }
                                .padding(.horizontal, 12)
                                .padding(.vertical, 8)
                                .background(msg.isFromDriver ? Color(red: 0.86, green: 0.98, blue: 0.88) : Color.white)
                                .cornerRadius(14)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 14)
                                        .stroke(Color.gray.opacity(0.15), lineWidth: 1)
                                )
                                
                                if !msg.isFromDriver {
                                    Spacer()
                                }
                            }
                            .id(msg.id)
                        }
                    }
                    .padding(16)
                }
                .background(Color(UIColor.systemGray6).opacity(0.4))
                .onChange(of: messages.count) { _ in
                    if let last = messages.last {
                        proxy.scrollTo(last.id, anchor: .bottom)
                    }
                }
            }
            
            // QUICK CANNED REPLIES
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(cannedReplies, id: \.self) { reply in
                        Button {
                            onSendMessage(reply)
                        } label: {
                            Text(reply)
                                .font(.system(size: 11, weight: .medium))
                                .foregroundColor(.primary)
                                .padding(.horizontal, 10)
                                .padding(.vertical, 6)
                                .background(Color(UIColor.systemGray6))
                                .cornerRadius(12)
                        }
                    }
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
            }
            .background(Color.white)
            
            Divider()
            
            // INPUT BAR
            HStack(spacing: 10) {
                TextField("Message passenger…", text: $inputText)
                    .textFieldStyle(.plain)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 8)
                    .background(Color(UIColor.systemGray6).opacity(0.6))
                    .cornerRadius(20)
                
                if !inputText.trimmingCharacters(in: .whitespaces).isEmpty {
                    Button {
                        onSendMessage(inputText.trimmingCharacters(in: .whitespaces))
                        inputText = ""
                    } label: {
                        Image(systemName: "arrow.up.circle.fill")
                            .font(.system(size: 28))
                            .foregroundColor(.blue)
                    }
                } else {
                    Button {
                        onSendMessage("🎤 [Voice Note: 0:04]")
                    } label: {
                        Image(systemName: "mic.fill")
                            .font(.system(size: 18))
                            .foregroundColor(.blue)
                            .frame(width: 32, height: 32)
                            .background(Color.blue.opacity(0.1))
                            .clipShape(Circle())
                    }
                }
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 8)
            .background(Color.white)
        }
    }
}
