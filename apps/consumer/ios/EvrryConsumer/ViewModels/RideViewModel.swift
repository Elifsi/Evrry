//
//  RideViewModel.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Ride Mobility & Dispatch ViewModel (Fixed Fare & InDrive Bidding Negotiation)
//

import SwiftUI
import Combine
import CoreLocation
import Supabase

@MainActor
public final class RideViewModel: ObservableObject {
    @Published public var activeRide: ActiveRide = ActiveRide()
    @Published public var isBooking: Bool = false
    @Published public var isRouting: Bool = false
    
    private let routingService = EvrryMapRoutingService.shared
    private let supabaseClient: SupabaseClient?
    
    public init(supabaseClient: SupabaseClient? = nil) {
        self.supabaseClient = supabaseClient
        recomputeRoute()
    }
    
    public func setPickup(_ point: LocationPoint) {
        self.activeRide.pickupAddress = point.address
        self.activeRide.pickupLat = point.latitude
        self.activeRide.pickupLng = point.longitude
        recomputeRoute()
    }
    
    public func setDrop(_ point: LocationPoint) {
        self.activeRide.dropAddress = point.address
        self.activeRide.dropLat = point.latitude
        self.activeRide.dropLng = point.longitude
        recomputeRoute()
    }
    
    public func swapPickupAndDrop() {
        let cur = activeRide
        self.activeRide.pickupAddress = cur.dropAddress
        self.activeRide.pickupLat = cur.dropLat
        self.activeRide.pickupLng = cur.dropLng
        self.activeRide.dropAddress = cur.pickupAddress
        self.activeRide.dropLat = cur.pickupLat
        self.activeRide.dropLng = cur.pickupLng
        recomputeRoute()
    }
    
    public func recomputeRoute() {
        self.isRouting = true
        
        Task {
            if let client = supabaseClient {
                do {
                    let res = try await routingService.getRoute(
                        client: client,
                        origin: activeRide.pickupCoordinate,
                        destination: activeRide.dropCoordinate
                    )
                    let distKm = Double(res.distance_meters) / 1000.0
                    let fare = activeRide.rideClass.baseFareNpr + (activeRide.rideClass.perKmRateNpr * distKm)
                    
                    self.activeRide.distanceKm = (distKm * 10).rounded() / 10
                    self.activeRide.durationMinutes = res.duration_minutes
                    self.activeRide.estimatedFareNpr = fare
                    if self.activeRide.status == .idle {
                        self.activeRide.offeredFareNpr = fare
                    }
                    self.activeRide.routePolyline = res.polyline
                } catch {
                    fallbackApproximation()
                }
            } else {
                fallbackApproximation()
            }
            self.isRouting = false
        }
    }
    
    private func fallbackApproximation() {
        let dLat = abs(activeRide.dropLat - activeRide.pickupLat) * 111.0
        let dLng = abs(activeRide.dropLng - activeRide.pickupLng) * 98.0
        let approxKm = max(1.5, sqrt(dLat * dLat + dLng * dLng) * 1.35)
        let approxMins = max(5, Int(approxKm * 3.2))
        let fare = activeRide.rideClass.baseFareNpr + (activeRide.rideClass.perKmRateNpr * approxKm)
        
        self.activeRide.distanceKm = (approxKm * 10).rounded() / 10
        self.activeRide.durationMinutes = approxMins
        self.activeRide.estimatedFareNpr = fare
        if self.activeRide.status == .idle {
            self.activeRide.offeredFareNpr = fare
        }
    }
    
    public func selectClass(_ rideClass: RideClass) {
        let estimated = rideClass.baseFareNpr + (rideClass.perKmRateNpr * activeRide.distanceKm)
        self.activeRide.rideClass = rideClass
        self.activeRide.estimatedFareNpr = estimated
        self.activeRide.offeredFareNpr = estimated
    }
    
    public func setBookingMode(_ mode: BookingMode) {
        self.activeRide.bookingMode = mode
    }
    
    public func updateOfferedFare(delta: Double) {
        let newFare = max(30.0, self.activeRide.offeredFareNpr + delta)
        self.activeRide.offeredFareNpr = newFare
    }
    
    public func setCustomOfferedFare(_ fare: Double) {
        self.activeRide.offeredFareNpr = max(30.0, fare)
    }
    
    public func acceptDriverBid(_ bid: DriverBid) {
        self.activeRide.status = .accepted
        self.activeRide.driver = bid.driver
        self.activeRide.estimatedFareNpr = bid.bidFareNpr
        self.activeRide.offeredFareNpr = bid.bidFareNpr
        self.activeRide.driverBids = []
        self.activeRide.otpCode = "1234"
    }
    
    public func requestRide() {
        let current = self.activeRide
        self.isBooking = true
        self.activeRide.status = .searching
        self.activeRide.driverBids = []
        
        Task {
            let finalOfferedFare = current.bookingMode == .bidding ? current.offeredFareNpr : current.estimatedFareNpr
            
            if current.bookingMode == .bidding {
                // InDrive Bidding Flow: Nearby drivers stream bids
                try? await Task.sleep(nanoseconds: 1_200_000_000)
                self.isBooking = false
                
                let primaryDriver: AssignedDriver
                let secondDriver: AssignedDriver
                
                switch current.rideClass {
                case .moto:
                    primaryDriver = AssignedDriver(
                        name: "Bikash Tamang",
                        phone: "+9779841234567",
                        vehiclePlate: "BA 2 PA 4920",
                        vehicleModel: "Bajaj Pulsar 150",
                        rating: 4.95,
                        tripsCompleted: 1840
                    )
                    secondDriver = AssignedDriver(
                        name: "Aman Maharjan",
                        phone: "+9779812345678",
                        vehiclePlate: "BA 4 PA 1198",
                        vehicleModel: "TVS Raider 125",
                        rating: 4.91,
                        tripsCompleted: 620
                    )
                case .cab:
                    primaryDriver = AssignedDriver(
                        name: "Sunil Shrestha",
                        phone: "+9779851098765",
                        vehiclePlate: "BA 1 JA 3812",
                        vehicleModel: "Suzuki Dzire Taxi",
                        rating: 4.88,
                        tripsCompleted: 2410
                    )
                    secondDriver = AssignedDriver(
                        name: "Ramesh Adhikari",
                        phone: "+9779841987654",
                        vehiclePlate: "BA 2 JA 5519",
                        vehicleModel: "Hyundai Aura Cab",
                        rating: 4.92,
                        tripsCompleted: 1420
                    )
                case .comfort:
                    primaryDriver = AssignedDriver(
                        name: "Prabesh Karki",
                        phone: "+9779801239876",
                        vehiclePlate: "BA 3 CHA 7291",
                        vehicleModel: "BYD Atto 3 EV (AC)",
                        rating: 4.98,
                        tripsCompleted: 950
                    )
                    secondDriver = AssignedDriver(
                        name: "Deepak Thapa",
                        phone: "+9779851122334",
                        vehiclePlate: "BA 3 CHA 4820",
                        vehicleModel: "MG ZS EV",
                        rating: 4.96,
                        tripsCompleted: 810
                    )
                }
                
                let bid1 = DriverBid(
                    id: "bid_1",
                    driver: primaryDriver,
                    bidFareNpr: finalOfferedFare,
                    etaMinutes: 3,
                    isCounterOffer: false
                )
                let bid2 = DriverBid(
                    id: "bid_2",
                    driver: secondDriver,
                    bidFareNpr: finalOfferedFare + 20.0,
                    etaMinutes: 2,
                    isCounterOffer: true
                )
                
                self.activeRide.driverBids = [bid1, bid2]
            } else {
                // Fixed Rate: Immediate driver dispatch
                try? await Task.sleep(nanoseconds: 1_600_000_000)
                self.isBooking = false
                
                let assigned: AssignedDriver
                switch current.rideClass {
                case .moto:
                    assigned = AssignedDriver(
                        name: "Bikash Tamang",
                        phone: "+9779841234567",
                        vehiclePlate: "BA 2 PA 4920",
                        vehicleModel: "Bajaj Pulsar 150 (Black)",
                        rating: 4.95,
                        tripsCompleted: 1840
                    )
                case .cab:
                    assigned = AssignedDriver(
                        name: "Sunil Shrestha",
                        phone: "+9779851098765",
                        vehiclePlate: "BA 1 JA 3812",
                        vehicleModel: "Suzuki Dzire Taxi",
                        rating: 4.88,
                        tripsCompleted: 2410
                    )
                case .comfort:
                    assigned = AssignedDriver(
                        name: "Prabesh Karki",
                        phone: "+9779801239876",
                        vehiclePlate: "BA 3 CHA 7291",
                        vehicleModel: "BYD Atto 3 EV (Air Conditioned)",
                        rating: 4.98,
                        tripsCompleted: 950
                    )
                }
                
                self.activeRide.status = .accepted
                self.activeRide.driver = assigned
                self.activeRide.otpCode = "1234"
            }
        }
    }
    
    public func completeRide() {
        self.activeRide.status = .completed
    }
    
    public func submitReview(rating: Int, comment: String) {
        self.activeRide.reviewSubmitted = true
    }
    
    public func cancelRide() {
        self.activeRide.status = .cancelled
        self.activeRide.driverBids = []
    }
    
    public func resetRide() {
        let current = self.activeRide
        self.activeRide = ActiveRide(
            status: .idle,
            bookingMode: current.bookingMode,
            rideClass: current.rideClass,
            pickupAddress: current.pickupAddress,
            dropAddress: current.dropAddress,
            pickupLat: current.pickupLat,
            pickupLng: current.pickupLng,
            dropLat: current.dropLat,
            dropLng: current.dropLng
        )
        recomputeRoute()
    }
}
