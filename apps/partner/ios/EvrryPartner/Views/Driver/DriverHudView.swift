//
//  DriverHudView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Complete Driver Mobility HUD & Lifecycle View
//

import SwiftUI

public struct DriverHudView: View {
    @ObservedObject var viewModel: DriverMobilityViewModel
    public let onRoleSelected: (PartnerRole) -> Void
    
    @State private var showCallSheet: Bool = false
    @State private var showChatScreen: Bool = false
    @State private var voipNoticeText: String? = nil
    
    public init(viewModel: DriverMobilityViewModel, onRoleSelected: @escaping (PartnerRole) -> Void) {
        self.viewModel = viewModel
        self.onRoleSelected = onRoleSelected
    }
    
    public var body: some View {
        if showChatScreen, let ride = viewModel.activeRide {
            PartnerChatRoomView(
                passengerName: ride.passengerName,
                passengerPhone: ride.passengerPhone,
                rideId: ride.id,
                messages: viewModel.chatMessages,
                onSendMessage: { viewModel.sendChatMessage($0) },
                onBack: { showChatScreen = false },
                onCallClick: { showCallSheet = true }
            )
        } else {
            VStack(spacing: 0) {
                // TOP BAR WITH 1-TAP ROLE SWITCHER
                PartnerTopBarView(viewModel: viewModel)
                
                ScrollView {
                    VStack(spacing: 16) {
                        // 1. MAP HUD WITH POLYLINE TRACKING
                        let ride = viewModel.activeRide ?? viewModel.openRequests.first
                        PartnerMapHUDView(
                            pickupAddress: ride?.pickupAddress ?? "Thamel Marg, Kathmandu",
                            dropAddress: ride?.dropAddress ?? "Patan Durbar Square",
                            vehicleType: viewModel.locationBroadcaster.locationState.vehicleType,
                            bearingDegrees: viewModel.locationBroadcaster.locationState.bearing,
                            speedKmh: viewModel.locationBroadcaster.locationState.speedKmh,
                            etaMinutes: ride?.durationMinutes ?? 15,
                            status: viewModel.activeRide?.status ?? .bidding
                        )
                        
                        // 2. OFFLINE STANDBY CARD
                        if !viewModel.isOnline {
                            VStack(spacing: 10) {
                                Text("😴")
                                    .font(.system(size: 34))
                                Text("You are Currently Offline")
                                    .font(.system(size: 16, weight: .bold))
                                Text("Go online in the top bar to broadcast your live GPS to Kathmandu riders and receive nearby trip requests.")
                                    .font(.system(size: 12))
                                    .foregroundColor(.secondary)
                                    .multilineTextAlignment(.center)
                                
                                Button {
                                    viewModel.toggleOnline(true)
                                } label: {
                                    Text("Go Online Now ⚡")
                                        .font(.system(size: 14, weight: .bold))
                                        .padding(.horizontal, 20)
                                        .padding(.vertical, 10)
                                        .background(Color.green)
                                        .foregroundColor(.white)
                                        .cornerRadius(12)
                                }
                            }
                            .padding(20)
                            .frame(maxWidth: .infinity)
                            .background(Color.white)
                            .cornerRadius(18)
                            .overlay(RoundedRectangle(cornerRadius: 18).stroke(Color.gray.opacity(0.2), lineWidth: 1))
                        }
                        
                        // 3. ACTIVE TRIP LIFECYCLE
                        if let active = viewModel.activeRide {
                            VStack(alignment: .leading, spacing: 14) {
                                // Passenger Profile & Status
                                HStack {
                                    ZStack {
                                        Circle()
                                            .fill(Color.blue.opacity(0.12))
                                            .frame(width: 44, height: 44)
                                        Text("👤")
                                            .font(.system(size: 20))
                                    }
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(active.passengerName)
                                            .font(.system(size: 15, weight: .bold))
                                        Text("⭐ \(String(format: "%.2f", active.passengerRating)) · \(active.passengerPhone)")
                                            .font(.system(size: 11))
                                            .foregroundColor(.secondary)
                                    }
                                    Spacer()
                                    Text(active.status.label)
                                        .font(.system(size: 10, weight: .black))
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 4)
                                        .background(active.status.color.opacity(0.15))
                                        .foregroundColor(active.status.color)
                                        .cornerRadius(8)
                                }
                                
                                // Addresses
                                VStack(alignment: .leading, spacing: 6) {
                                    HStack {
                                        Circle().fill(Color.green).frame(width: 8, height: 8)
                                        Text("PICKUP: \(active.pickupAddress)")
                                            .font(.system(size: 11, weight: .semibold))
                                            .lineLimit(1)
                                    }
                                    Divider()
                                    HStack {
                                        Circle().fill(Color.red).frame(width: 8, height: 8)
                                        Text("DROP: \(active.dropAddress)")
                                            .font(.system(size: 11, weight: .semibold))
                                            .lineLimit(1)
                                    }
                                }
                                .padding(10)
                                .background(Color(UIColor.systemGray6).opacity(0.5))
                                .cornerRadius(10)
                                
                                // Action buttons: Call & Chat
                                HStack(spacing: 10) {
                                    Button {
                                        showCallSheet = true
                                    } label: {
                                        HStack {
                                            Image(systemName: "phone.fill")
                                            Text("Call")
                                        }
                                        .font(.system(size: 12, weight: .bold))
                                        .frame(maxWidth: .infinity)
                                        .padding(.vertical, 8)
                                        .background(Color.blue.opacity(0.1))
                                        .foregroundColor(.blue)
                                        .cornerRadius(10)
                                    }
                                    
                                    Button {
                                        showChatScreen = true
                                    } label: {
                                        HStack {
                                            Image(systemName: "message.fill")
                                            Text("Chat")
                                        }
                                        .font(.system(size: 12, weight: .bold))
                                        .frame(maxWidth: .infinity)
                                        .padding(.vertical, 8)
                                        .background(Color.blue.opacity(0.1))
                                        .foregroundColor(.blue)
                                        .cornerRadius(10)
                                    }
                                }
                                
                                // PHASE 1: ACCEPTED -> "Arrived at Pickup"
                                if active.status == .accepted {
                                    Button {
                                        viewModel.markArrivedAtPickup()
                                    } label: {
                                        HStack {
                                            Image(systemName: "mappin.circle.fill")
                                            Text("Arrived at Pickup Location 📍")
                                        }
                                        .font(.system(size: 14, weight: .bold))
                                        .frame(maxWidth: .infinity)
                                        .padding(.vertical, 12)
                                        .background(Color.blue)
                                        .foregroundColor(.white)
                                        .cornerRadius(12)
                                    }
                                }
                                
                                // PHASE 2: ARRIVED -> 4-Digit OTP Entry
                                if active.status == .arrived {
                                    VStack(spacing: 8) {
                                        Text("Enter Passenger 4-Digit Trip OTP")
                                            .font(.system(size: 13, weight: .bold))
                                            .foregroundColor(.blue)
                                        Text("Ask passenger for their code (e.g. \(active.startOtp))")
                                            .font(.system(size: 11))
                                            .foregroundColor(.secondary)
                                        
                                        TextField("• • • •", text: $viewModel.startOtpInput)
                                            .keyboardType(.numberPad)
                                            .font(.system(size: 20, weight: .black))
                                            .multilineTextAlignment(.center)
                                            .frame(width: 140)
                                            .padding(8)
                                            .background(Color.white)
                                            .cornerRadius(10)
                                            .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.blue, lineWidth: 1.5))
                                        
                                        if let err = viewModel.otpError {
                                            Text(err)
                                                .font(.system(size: 11, weight: .bold))
                                                .foregroundColor(.red)
                                        }
                                        
                                        Button {
                                            _ = viewModel.verifyStartOtp()
                                        } label: {
                                            Text("Verify OTP & Start Trip 🚀")
                                                .font(.system(size: 13, weight: .bold))
                                                .frame(maxWidth: .infinity)
                                                .padding(.vertical, 10)
                                                .background(Color.green)
                                                .foregroundColor(.white)
                                                .cornerRadius(10)
                                        }
                                        .disabled(viewModel.startOtpInput.count < 4)
                                    }
                                    .padding(12)
                                    .background(Color.blue.opacity(0.06))
                                    .cornerRadius(12)
                                }
                                
                                // PHASE 3: IN PROGRESS -> "Complete Trip"
                                if active.status == .inProgress {
                                    Button {
                                        viewModel.completeTrip()
                                    } label: {
                                        HStack {
                                            Image(systemName: "flag.checkered")
                                            Text("Complete Trip 🏁")
                                        }
                                        .font(.system(size: 14, weight: .bold))
                                        .frame(maxWidth: .infinity)
                                        .padding(.vertical, 12)
                                        .background(Color.green)
                                        .foregroundColor(.white)
                                        .cornerRadius(12)
                                    }
                                }
                                
                                // PHASE 4: COMPLETED -> Cash on Delivery (COD) Ledger
                                if active.status == .completed, let fb = viewModel.fareBreakdown {
                                    VStack(spacing: 8) {
                                        HStack {
                                            Text("Total Fare")
                                                .font(.system(size: 13, weight: .bold))
                                            Spacer()
                                            Text("Rs \(Int(fb.totalFareNpr))")
                                                .font(.system(size: 16, weight: .black))
                                        }
                                        HStack {
                                            Text("Platform Commission (15%)")
                                                .font(.system(size: 11))
                                                .foregroundColor(.secondary)
                                            Spacer()
                                            Text("- Rs \(Int(fb.platformCommissionNpr))")
                                                .font(.system(size: 11))
                                                .foregroundColor(.red)
                                        }
                                        Divider()
                                        HStack {
                                            Text("Driver Net Payout")
                                                .font(.system(size: 13, weight: .bold))
                                                .foregroundColor(Color(red: 0.08, green: 0.45, blue: 0.22))
                                            Spacer()
                                            Text("Rs \(Int(fb.driverNetNpr))")
                                                .font(.system(size: 16, weight: .black))
                                                .foregroundColor(Color(red: 0.08, green: 0.45, blue: 0.22))
                                        }
                                        
                                        Text("💵 Collect Rs \(Int(fb.totalFareNpr)) in CASH from passenger (\(fb.paymentMethod))")
                                            .font(.system(size: 11, weight: .bold))
                                            .foregroundColor(.orange)
                                            .padding(8)
                                            .frame(maxWidth: .infinity)
                                            .background(Color.orange.opacity(0.12))
                                            .cornerRadius(8)
                                        
                                        Button {
                                            viewModel.collectCashAndFinish()
                                        } label: {
                                            Text("Cash Collected · Back to Online ✅")
                                                .font(.system(size: 14, weight: .bold))
                                                .frame(maxWidth: .infinity)
                                                .padding(.vertical, 12)
                                                .background(Color.blue)
                                                .foregroundColor(.white)
                                                .cornerRadius(12)
                                        }
                                    }
                                    .padding(12)
                                    .background(Color(UIColor.systemGray6).opacity(0.5))
                                    .cornerRadius(12)
                                }
                            }
                            .padding(16)
                            .background(Color.white)
                            .cornerRadius(18)
                            .overlay(RoundedRectangle(cornerRadius: 18).stroke(Color.blue, lineWidth: 1.5))
                        }
                        
                        // 4. INDRIVE-STYLE LIVE FARE BIDDING HUD (OPEN REQUESTS)
                        if viewModel.isOnline && viewModel.activeRide == nil {
                            HStack {
                                Text("NEARBY RIDE REQUESTS (\(viewModel.openRequests.count))")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(.secondary)
                                Spacer()
                            }
                            
                            ForEach(viewModel.openRequests) { req in
                                RideBiddingCard(
                                    request: req,
                                    onAccept: { viewModel.acceptOfferedFare(req) },
                                    onCounter: { fare in viewModel.submitCounterBid(req, counterFareNpr: fare) },
                                    onDecline: { viewModel.declineRequest(req.id) }
                                )
                            }
                        }
                    }
                    .padding(16)
                }
                .background(Color(UIColor.systemGray6).opacity(0.4))
            }
            .sheet(isPresented: $showCallSheet) {
                if let ride = viewModel.activeRide {
                    DualCallChooserSheetView(
                        passengerName: ride.passengerName,
                        passengerPhone: ride.passengerPhone,
                        onDismiss: { showCallSheet = false },
                        onInAppVoipCall: {
                            voipNoticeText = "WebRTC In-App VoIP call connected with \(ride.passengerName)"
                        }
                    )
                    .presentationDetents([.medium])
                }
            }
            .alert("🌐 WebRTC Voice Call", isPresented: Binding(
                get: { voipNoticeText != nil },
                set: { if !$0 { voipNoticeText = nil } }
            )) {
                Button("End Call", role: .cancel) { voipNoticeText = nil }
            } message: {
                Text(voipNoticeText ?? "")
            }
        }
    }
}

private struct RideBiddingCard: View {
    let request: DriverRideRequest
    let onAccept: () -> Void
    let onCounter: (Double) -> Void
    let onDecline: () -> Void
    
    @State private var counterFare: Double
    
    init(request: DriverRideRequest, onAccept: @escaping () -> Void, onCounter: @escaping (Double) -> Void, onDecline: @escaping () -> Void) {
        self.request = request
        self.onAccept = onAccept
        self.onCounter = onCounter
        self.onDecline = onDecline
        _counterFare = State(initialValue: request.offeredFareNpr)
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Header
            HStack {
                HStack(spacing: 8) {
                    ZStack {
                        Circle()
                            .fill(Color.blue.opacity(0.1))
                            .frame(width: 36, height: 36)
                        Text("👤")
                            .font(.system(size: 16))
                    }
                    VStack(alignment: .leading, spacing: 2) {
                        Text(request.passengerName)
                            .font(.system(size: 14, weight: .bold))
                        Text("⭐ \(String(format: "%.2f", request.passengerRating)) · \(request.distanceKm, specifier: "%.1f") km (~\(request.durationMinutes)m)")
                            .font(.system(size: 11))
                            .foregroundColor(.secondary)
                    }
                }
                Spacer()
                VStack(alignment: .trailing, spacing: 1) {
                    Text("OFFERED FARE")
                        .font(.system(size: 9, weight: .bold))
                        .foregroundColor(.secondary)
                    Text("Rs \(Int(request.offeredFareNpr))")
                        .font(.system(size: 18, weight: .black))
                        .foregroundColor(Color(red: 0.08, green: 0.45, blue: 0.22))
                }
            }
            
            // Route
            VStack(alignment: .leading, spacing: 4) {
                Text("📍 Pickup: \(request.pickupAddress)")
                    .font(.system(size: 11))
                    .lineLimit(1)
                Text("🏁 Drop: \(request.dropAddress)")
                    .font(.system(size: 11))
                    .lineLimit(1)
            }
            .padding(8)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color(UIColor.systemGray6).opacity(0.5))
            .cornerRadius(8)
            
            if request.myBidStatus == "pending" {
                HStack {
                    Text("Counter-Bid Rs \(Int(request.myBidFareNpr ?? counterFare)) Submitted")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(.orange)
                    Spacer()
                    ProgressView()
                }
                .padding(8)
                .background(Color.orange.opacity(0.12))
                .cornerRadius(8)
            } else {
                // Stepper & Quick Chips
                VStack(alignment: .leading, spacing: 6) {
                    Text("Counter-Offer Price Stepper:")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundColor(.secondary)
                    
                    HStack(spacing: 6) {
                        Button {
                            counterFare = max(30.0, counterFare - 10.0)
                        } label: {
                            Text("-10")
                                .font(.system(size: 12, weight: .bold))
                                .padding(.horizontal, 10)
                                .padding(.vertical, 6)
                                .background(Color(UIColor.systemGray6))
                                .cornerRadius(8)
                        }
                        
                        Text("Rs \(Int(counterFare))")
                            .font(.system(size: 15, weight: .black))
                            .foregroundColor(.blue)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 6)
                            .background(Color.blue.opacity(0.1))
                            .cornerRadius(8)
                        
                        Button {
                            counterFare += 10.0
                        } label: {
                            Text("+10")
                                .font(.system(size: 12, weight: .bold))
                                .padding(.horizontal, 10)
                                .padding(.vertical, 6)
                                .background(Color(UIColor.systemGray6))
                                .cornerRadius(8)
                        }
                        
                        ForEach([20, 50, 100], id: \.self) { delta in
                            Button {
                                counterFare = request.offeredFareNpr + Double(delta)
                            } label: {
                                Text("+\(delta)")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(.primary)
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 6)
                                    .background(Color(UIColor.systemGray6))
                                    .cornerRadius(8)
                            }
                        }
                    }
                }
                
                // Actions
                HStack(spacing: 8) {
                    Button(action: onDecline) {
                        Text("Decline")
                            .font(.system(size: 12))
                            .foregroundColor(.red)
                            .padding(.vertical, 8)
                            .frame(maxWidth: .infinity)
                            .background(Color.red.opacity(0.08))
                            .cornerRadius(10)
                    }
                    
                    if counterFare != request.offeredFareNpr {
                        Button {
                            onCounter(counterFare)
                        } label: {
                            Text("Counter Rs \(Int(counterFare))")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(.white)
                                .padding(.vertical, 8)
                                .frame(maxWidth: .infinity)
                                .background(Color.orange)
                                .cornerRadius(10)
                        }
                    } else {
                        Button(action: onAccept) {
                            Text("Accept Rs \(Int(request.offeredFareNpr)) 🤝")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(.white)
                                .padding(.vertical, 8)
                                .frame(maxWidth: .infinity)
                                .background(Color.green)
                                .cornerRadius(10)
                        }
                    }
                }
            }
        }
        .padding(14)
        .background(Color.white)
        .cornerRadius(16)
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.blue.opacity(0.5), lineWidth: 1.5))
    }
}
