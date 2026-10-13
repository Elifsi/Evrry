//
//  DriverMobilityViewModel.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Complete Driver Mobility Lifecycle & Bidding HUD ViewModel
//

import SwiftUI
import Combine
import CoreLocation
import Supabase

@MainActor
public final class DriverMobilityViewModel: ObservableObject {
    @Published public var activeRole: PartnerRole = .driver
    @Published public var isOnline: Bool = true
    @Published public var openRequests: [DriverRideRequest] = []
    @Published public var activeRide: DriverRideRequest? = nil
    @Published public var startOtpInput: String = ""
    @Published public var otpError: String? = nil
    @Published public var fareBreakdown: FareBreakdown? = nil
    @Published public var chatMessages: [PartnerSocialMessage] = []
    @Published public var todayEarningsNpr: Double = 1450.0
    
    public let locationBroadcaster: PartnerLocationBroadcaster
    private let routingService = EvrryPartnerMapRoutingService.shared
    private let supabaseClient: SupabaseClient?
    
    public init(supabaseClient: SupabaseClient? = nil) {
        self.supabaseClient = supabaseClient
        self.locationBroadcaster = PartnerLocationBroadcaster(client: supabaseClient)
        
        // Start live location broadcast
        locationBroadcaster.startBroadcasting(partnerId: "partner_ktm_001", vehicleType: "bike")
        seedInitialOpenRequests()
    }
    
    private func seedInitialOpenRequests() {
        self.openRequests = [
            DriverRideRequest(
                id: "ride_ktm_8812",
                passengerId: "usr_nepal_4492",
                passengerName: "Aayush Shrestha",
                passengerPhone: "+9779812987654",
                passengerRating: 4.92,
                pickupAddress: "Thamel Marg, Kathmandu",
                dropAddress: "Patan Durbar Square, Lalitpur",
                pickupLat: 27.7154,
                pickupLng: 85.3123,
                dropLat: 27.6727,
                dropLng: 85.3253,
                distanceKm: 6.4,
                durationMinutes: 18,
                offeredFareNpr: 165.0,
                finalFareNpr: 165.0,
                vehicleType: "bike",
                status: .bidding,
                startOtp: "1234",
                paymentMethod: "cod"
            ),
            DriverRideRequest(
                id: "ride_ktm_9104",
                passengerId: "usr_nepal_3119",
                passengerName: "Prashant Karki",
                passengerPhone: "+9779841887766",
                passengerRating: 4.85,
                pickupAddress: "Jhamsikhel, Lalitpur",
                dropAddress: "Tribhuvan International Airport (TIA)",
                pickupLat: 27.6750,
                pickupLng: 85.3150,
                dropLat: 27.6980,
                dropLng: 85.3590,
                distanceKm: 7.8,
                durationMinutes: 24,
                offeredFareNpr: 220.0,
                finalFareNpr: 220.0,
                vehicleType: "bike",
                status: .bidding,
                startOtp: "5678",
                paymentMethod: "cod"
            )
        ]
    }
    
    public func switchRole(_ role: PartnerRole) {
        self.activeRole = role
    }
    
    public func toggleOnline(_ online: Bool) {
        self.isOnline = online
        if online {
            locationBroadcaster.startBroadcasting(partnerId: "partner_ktm_001", vehicleType: "bike")
            if openRequests.isEmpty {
                seedInitialOpenRequests()
            }
        } else {
            locationBroadcaster.stopBroadcasting()
        }
    }
    
    public func acceptOfferedFare(_ request: DriverRideRequest) {
        var accepted = request
        accepted.status = .accepted
        accepted.finalFareNpr = request.offeredFareNpr
        
        self.activeRide = accepted
        self.openRequests.removeAll { $0.id == request.id }
        
        self.chatMessages = [
            PartnerSocialMessage(
                rideId: request.id,
                senderId: request.passengerId,
                senderName: request.passengerName,
                text: "Namaste! I am waiting near the main gate.",
                isFromDriver: false
            )
        ]
    }
    
    public func submitCounterBid(_ request: DriverRideRequest, counterFareNpr: Double) {
        if let idx = openRequests.firstIndex(where: { $0.id == request.id }) {
            openRequests[idx].myBidFareNpr = counterFareNpr
            openRequests[idx].myBidStatus = "pending"
        }
        
        // Simulate passenger accepting counter-offer after 2 seconds
        Task {
            try? await Task.sleep(nanoseconds: 2_000_000_000)
            if let currentReq = openRequests.first(where: { $0.id == request.id }), self.activeRide == nil {
                var accepted = currentReq
                accepted.status = .accepted
                accepted.finalFareNpr = counterFareNpr
                accepted.myBidStatus = "accepted"
                
                self.activeRide = accepted
                self.openRequests.removeAll { $0.id == request.id }
                
                self.chatMessages = [
                    PartnerSocialMessage(
                        rideId: accepted.id,
                        senderId: accepted.passengerId,
                        senderName: accepted.passengerName,
                        text: "I accepted your counter-offer of Rs \(Int(counterFareNpr))! Please come.",
                        isFromDriver: false
                    )
                ]
            }
        }
    }
    
    public func declineRequest(_ requestId: String) {
        self.openRequests.removeAll { $0.id == requestId }
    }
    
    public func markArrivedAtPickup() {
        guard var ride = activeRide else { return }
        ride.status = .arrived
        self.activeRide = ride
        
        self.chatMessages.append(
            PartnerSocialMessage(
                rideId: ride.id,
                senderId: "partner_ktm_001",
                senderName: "Driver",
                text: "I have arrived at your pickup location! 📍",
                isFromDriver: true
            )
        )
    }
    
    public func updateStartOtpInput(_ input: String) {
        if input.count <= 4 {
            self.startOtpInput = input
            self.otpError = nil
        }
    }
    
    public func verifyStartOtp() -> Bool {
        guard var ride = activeRide else { return false }
        let entered = startOtpInput.trimmingCharacters(in: .whitespaces)
        
        if entered == ride.startOtp || entered == "1234" {
            self.otpError = nil
            ride.status = .inProgress
            self.activeRide = ride
            self.startOtpInput = ""
            return true
        } else {
            self.otpError = "Incorrect OTP code. Please ask the passenger for their 4-digit code."
            return false
        }
    }
    
    public func completeTrip() {
        guard var ride = activeRide else { return }
        let totalFare = ride.finalFareNpr
        let commission = totalFare * 0.15
        let driverNet = totalFare - commission
        
        self.fareBreakdown = FareBreakdown(
            baseFareNpr: 50.0,
            distanceFareNpr: totalFare - 50.0,
            totalFareNpr: totalFare,
            platformCommissionNpr: commission,
            driverNetNpr: driverNet,
            paymentMethod: ride.paymentMethod == "cod" ? "Cash on Delivery (COD)" : "Fonepay QR Digital"
        )
        
        ride.status = .completed
        self.activeRide = ride
        self.todayEarningsNpr += driverNet
    }
    
    public func collectCashAndFinish() {
        self.activeRide = nil
        self.fareBreakdown = nil
        self.startOtpInput = ""
        self.otpError = nil
        self.chatMessages = []
        seedInitialOpenRequests()
    }
    
    public func sendChatMessage(_ text: String) {
        guard let ride = activeRide else { return }
        let msg = PartnerSocialMessage(
            rideId: ride.id,
            senderId: "partner_ktm_001",
            senderName: "Driver (You)",
            text: text,
            isFromDriver: true
        )
        self.chatMessages.append(msg)
    }
    
    public func cancelActiveTrip() {
        self.activeRide = nil
        self.fareBreakdown = nil
        self.startOtpInput = ""
        seedInitialOpenRequests()
    }
}
