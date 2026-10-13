//
//  CourierMultiPartyChatSheet.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Multi-Party Chat for Active Orders (Store Channel vs Customer Channel)
//  Grounded in enatega-multivendor-rider & GetStream_whatsApp-clone-compose
//

import SwiftUI

public struct CourierMultiPartyChatSheet: View {
    @ObservedObject var viewModel: CourierDeliveryViewModel
    @State private var inputText: String = ""
    
    private let storeQuickReplies = [
        "I am waiting at the pickup counter",
        "How many minutes until food is packed?",
        "Order # is ready?",
        "Please include cutlery & extra tissues"
    ]
    
    private let customerQuickReplies = [
        "I have arrived at your building gate",
        "Please provide floor or door number",
        "Traffic delay, arriving in ~5 mins",
        "Please keep exact change ready"
    ]
    
    public init(viewModel: CourierDeliveryViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        guard let order = viewModel.activeOrder else {
            return AnyView(EmptyView())
        }
        
        let messages = viewModel.activeChatTab == .store ? viewModel.storeChatMessages : viewModel.customerChatMessages
        let quickReplies = viewModel.activeChatTab == .store ? storeQuickReplies : customerQuickReplies
        
        return AnyView(
            VStack(spacing: 0) {
                // Header with Tab Switcher
                VStack(spacing: 12) {
                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Active Delivery Chat")
                                .font(.system(size: 17, weight: .bold))
                                .foregroundColor(.white)
                            Text("Order #\(order.orderNo)")
                                .font(.system(size: 12))
                                .foregroundColor(.gray)
                        }
                        
                        Spacer()
                        
                        Button {
                            viewModel.showChatSheet = false
                        } label: {
                            Image(systemName: "xmark.circle.fill")
                                .font(.system(size: 24))
                                .foregroundColor(.gray)
                        }
                    }
                    
                    // Segmented Control Tabs (Store vs Customer)
                    HStack(spacing: 8) {
                        ForEach(CourierChatTab.allCases) { tab in
                            let isSelected = viewModel.activeChatTab == tab
                            Button {
                                viewModel.activeChatTab = tab
                            } label: {
                                HStack(spacing: 6) {
                                    Image(systemName: tab.icon)
                                    Text(tab.rawValue)
                                        .font(.system(size: 13, weight: .bold))
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 8)
                                .background(isSelected ? Color.blue : Color(red: 0.16, green: 0.18, blue: 0.28))
                                .foregroundColor(isSelected ? .white : .gray)
                                .cornerRadius(10)
                            }
                        }
                    }
                }
                .padding(16)
                .background(Color(red: 0.10, green: 0.12, blue: 0.18))
                
                // Chat Messages Scroll
                ScrollViewReader { proxy in
                    ScrollView {
                        LazyVStack(spacing: 12) {
                            ForEach(messages) { msg in
                                HStack {
                                    if msg.isOutgoing {
                                        Spacer()
                                        VStack(alignment: .trailing, spacing: 2) {
                                            Text(msg.text)
                                                .font(.system(size: 14))
                                                .foregroundColor(.white)
                                                .padding(.horizontal, 14)
                                                .padding(.vertical, 10)
                                                .background(Color.blue)
                                                .cornerRadius(16)
                                            
                                            Text(msg.timestamp, style: .time)
                                                .font(.system(size: 10))
                                                .foregroundColor(.gray)
                                        }
                                    } else {
                                        VStack(alignment: .leading, spacing: 2) {
                                            Text(msg.senderName)
                                                .font(.system(size: 11, weight: .bold))
                                                .foregroundColor(.orange)
                                            
                                            Text(msg.text)
                                                .font(.system(size: 14))
                                                .foregroundColor(.white)
                                                .padding(.horizontal, 14)
                                                .padding(.vertical, 10)
                                                .background(Color(red: 0.18, green: 0.20, blue: 0.30))
                                                .cornerRadius(16)
                                            
                                            Text(msg.timestamp, style: .time)
                                                .font(.system(size: 10))
                                                .foregroundColor(.gray)
                                        }
                                        Spacer()
                                    }
                                }
                                .id(msg.id)
                            }
                        }
                        .padding(16)
                    }
                    .onChange(of: messages.count) { _ in
                        if let last = messages.last {
                            proxy.scrollTo(last.id, anchor: .bottom)
                        }
                    }
                }
                
                // Quick Reply Action Chips
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(quickReplies, id: \.self) { chip in
                            Button {
                                viewModel.sendChatMessage(text: chip, tab: viewModel.activeChatTab)
                            } label: {
                                Text(chip)
                                    .font(.system(size: 12))
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 6)
                                    .background(Color(red: 0.16, green: 0.18, blue: 0.28))
                                    .cornerRadius(12)
                                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.white.opacity(0.1), lineWidth: 1))
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 6)
                }
                .background(Color(red: 0.08, green: 0.10, blue: 0.16))
                
                // Input Bar
                HStack(spacing: 10) {
                    TextField(viewModel.activeChatTab == .store ? "Message kitchen staff..." : "Message customer...", text: $inputText)
                        .padding(10)
                        .background(Color(red: 0.16, green: 0.18, blue: 0.28))
                        .foregroundColor(.white)
                        .cornerRadius(12)
                    
                    Button {
                        viewModel.sendChatMessage(text: inputText, tab: viewModel.activeChatTab)
                        inputText = ""
                    } label: {
                        Image(systemName: "paperplane.fill")
                            .font(.system(size: 16))
                            .foregroundColor(.white)
                            .frame(width: 42, height: 42)
                            .background(Color.blue)
                            .clipShape(Circle())
                    }
                    .disabled(inputText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                }
                .padding(12)
                .background(Color(red: 0.10, green: 0.12, blue: 0.18))
            }
            .background(Color(red: 0.08, green: 0.10, blue: 0.16).ignoresSafeArea())
        )
    }
}
