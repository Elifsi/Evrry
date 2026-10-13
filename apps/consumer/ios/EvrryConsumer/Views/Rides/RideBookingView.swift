//
//  RideBookingView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Ride Mobility & Dispatch View (Interactive Map, Location Pickers, 3D Vehicles, InDrive Bidding, OTP 1234 & Reviews)
//

import SwiftUI
import CoreLocation

public struct RideBookingView: View {
    @ObservedObject public var viewModel: RideViewModel
    public let onBack: () -> Void
    public let onProfileClick: () -> Void
    public let onChatClick: (String, String) -> Void
    public let onInAppCallClick: (String, String) -> Void
    
    @State private var pickerMode: String? = nil // "pickup" or "drop"
    @State private var showRatingSheet: Bool = false
    @State private var showCallChooserSheet: Bool = false
    
    public init(
        viewModel: RideViewModel,
        onBack: @escaping () -> Void = {},
        onProfileClick: @escaping () -> Void = {},
        onChatClick: @escaping (String, String) -> Void = { _, _ in },
        onInAppCallClick: @escaping (String, String) -> Void = { _, _ in }
    ) {
        self.viewModel = viewModel
        self.onBack = onBack
        self.onProfileClick = onProfileClick
        self.onChatClick = onChatClick
        self.onInAppCallClick = onInAppCallClick
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            // TOP BAR
            EvrryTopBar(
                title: "Book a Ride",
                canGoBack: true,
                onBackClick: onBack,
                onProfileClick: onProfileClick
            )
            
            ScrollView {
                VStack(spacing: 16) {
                    // 1. LIVE ROUTE MAP & 3D ISOMETRIC VEHICLE
                    EvrryLiveTrackingMapView(
                        vendorName: viewModel.activeRide.pickupAddress.components(separatedBy: ",").first ?? viewModel.activeRide.pickupAddress,
                        destinationAddress: viewModel.activeRide.dropAddress.components(separatedBy: ",").first ?? viewModel.activeRide.dropAddress,
                        vehicleType: viewModel.activeRide.rideClass == .moto ? .motorcycle : .cab,
                        riderName: viewModel.activeRide.driver?.name ?? "Bikash Tamang",
                        etaMinutes: viewModel.activeRide.durationMinutes,
                        speedKmh: viewModel.activeRide.status == .accepted ? 34 : 26
                    )
                    
                    // 2. ROUTE SELECTION CARD
                    VStack(spacing: 12) {
                        // PICKUP ROW
                        Button(action: {
                            if viewModel.activeRide.status == .idle || viewModel.activeRide.status == .searching {
                                pickerMode = "pickup"
                            }
                        }) {
                            HStack(spacing: 10) {
                                Circle()
                                    .fill(Color.evrryGreenSuccess)
                                    .frame(width: 12, height: 12)
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("PICKUP LOCATION")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(.evrryMutedText)
                                    Text(viewModel.activeRide.pickupAddress)
                                        .font(.system(size: 13, weight: .semibold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                        .lineLimit(1)
                                }
                                Spacer()
                                if viewModel.activeRide.status == .idle {
                                    Text("Change")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(.evrryPrimaryBlue)
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 4)
                                        .background(Color.blue.opacity(0.08))
                                        .clipShape(RoundedRectangle(cornerRadius: 8))
                                }
                            }
                        }
                        .buttonStyle(PlainButtonStyle())
                        
                        // DIVIDER WITH SWAP BUTTON
                        HStack {
                            Divider().background(Color.evrrySubtleBorder)
                            if viewModel.activeRide.status == .idle {
                                Button(action: { viewModel.swapPickupAndDrop() }) {
                                    Image(systemName: "arrow.up.arrow.down")
                                        .font(.system(size: 12, weight: .semibold))
                                        .foregroundColor(.evrryMutedText)
                                        .frame(width: 28, height: 28)
                                }
                                Divider().background(Color.evrrySubtleBorder)
                            }
                        }
                        
                        // DESTINATION ROW
                        Button(action: {
                            if viewModel.activeRide.status == .idle || viewModel.activeRide.status == .searching {
                                pickerMode = "drop"
                            }
                        }) {
                            HStack(spacing: 10) {
                                Circle()
                                    .fill(Color.evrryRedDestructive)
                                    .frame(width: 12, height: 12)
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("DESTINATION")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(.evrryMutedText)
                                    Text(viewModel.activeRide.dropAddress)
                                        .font(.system(size: 13, weight: .semibold))
                                        .foregroundColor(.evrryDarkCharcoal)
                                        .lineLimit(1)
                                }
                                Spacer()
                                if viewModel.activeRide.status == .idle {
                                    Text("Change")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(.evrryPrimaryBlue)
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 4)
                                        .background(Color.blue.opacity(0.08))
                                        .clipShape(RoundedRectangle(cornerRadius: 8))
                                }
                            }
                        }
                        .buttonStyle(PlainButtonStyle())
                        
                        // OSRM DISTANCE & DURATION PILL
                        HStack {
                            if viewModel.isRouting {
                                Text("Calculating road route…")
                                    .font(.system(size: 11))
                                    .foregroundColor(.evrryPrimaryBlue)
                                Spacer()
                                ProgressView().scaleEffect(0.7)
                            } else {
                                Text("🛣️ Distance: \(String(format: "%.1f", viewModel.activeRide.distanceKm)) km")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(.evrryPrimaryBlue)
                                Spacer()
                                Text("⏱️ ETA: ~\(viewModel.activeRide.durationMinutes) mins")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(.evrryPrimaryBlue)
                            }
                        }
                        .padding(.horizontal, 12)
                        .padding(.vertical, 8)
                        .background(Color.blue.opacity(0.06))
                        .clipShape(RoundedRectangle(cornerRadius: 10))
                    }
                    .padding(16)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 20))
                    .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    
                    // 3. LIFECYCLE STATES
                    switch viewModel.activeRide.status {
                    case .searching:
                        if viewModel.activeRide.driverBids.isEmpty {
                            VStack(spacing: 14) {
                                ProgressView()
                                    .scaleEffect(1.2)
                                Text(viewModel.activeRide.bookingMode == .bidding ? "Broadcasting Offer to Drivers…" : "Connecting to Nearby Drivers…")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(.evrryDarkCharcoal)
                                Text(viewModel.activeRide.bookingMode == .bidding
                                     ? "Offered fare: Rs \(Int(viewModel.activeRide.offeredFareNpr)) · Waiting for driver bids"
                                     : "Dispatching request for \(viewModel.activeRide.rideClass.title) in Kathmandu")
                                    .font(.system(size: 12))
                                    .foregroundColor(.evrryMutedText)
                                Button("Cancel Request") {
                                    viewModel.cancelRide()
                                }
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.evrryRedDestructive)
                            }
                            .padding(20)
                            .frame(maxWidth: .infinity)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 20))
                            .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.evrryPrimaryBlue.opacity(0.3), lineWidth: 1))
                        } else {
                            // DRIVER BIDS LIST (InDrive Negotiation)
                            VStack(spacing: 12) {
                                HStack {
                                    Text("INCOMING DRIVER OFFERS (\(viewModel.activeRide.driverBids.count))")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(.evrryMutedText)
                                    Spacer()
                                    Text("Your Offer: Rs \(Int(viewModel.activeRide.offeredFareNpr))")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(.evrryPrimaryBlue)
                                }
                                
                                ForEach(viewModel.activeRide.driverBids) { bid in
                                    HStack(spacing: 12) {
                                        ZStack {
                                            Circle()
                                                .fill(Color.blue.opacity(0.1))
                                                .frame(width: 44, height: 44)
                                            Text("👨‍✈️").font(.system(size: 22))
                                        }
                                        
                                        VStack(alignment: .leading, spacing: 2) {
                                            HStack(spacing: 5) {
                                                Text(bid.driver.name)
                                                    .font(.system(size: 14, weight: .bold))
                                                    .foregroundColor(.evrryDarkCharcoal)
                                                if bid.driver.isVerified {
                                                    Image(systemName: "checkmark.seal.fill")
                                                        .font(.system(size: 11))
                                                        .foregroundColor(.evrryPrimaryBlue)
                                                }
                                                if bid.isCounterOffer {
                                                    Text("Counter-offer")
                                                        .font(.system(size: 9, weight: .bold))
                                                        .foregroundColor(.white)
                                                        .padding(.horizontal, 6)
                                                        .padding(.vertical, 2)
                                                        .background(Color.orange)
                                                        .clipShape(Capsule())
                                                }
                                            }
                                            Text("\(bid.driver.vehicleModel) · \(bid.driver.vehiclePlate)")
                                                .font(.system(size: 11))
                                                .foregroundColor(.evrryMutedText)
                                            Text("⭐ \(String(format: "%.2f", bid.driver.rating)) · ~\(bid.etaMinutes) min away")
                                                .font(.system(size: 11, weight: .semibold))
                                                .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                                        }
                                        
                                        Spacer()
                                        
                                        VStack(alignment: .trailing, spacing: 6) {
                                            Text("Rs \(Int(bid.bidFareNpr))")
                                                .font(.system(size: 16, weight: .black))
                                                .foregroundColor(bid.isCounterOffer ? Color.orange : .evrryDarkCharcoal)
                                            
                                            Button(action: { viewModel.acceptDriverBid(bid) }) {
                                                Text("Accept")
                                                    .font(.system(size: 12, weight: .bold))
                                                    .foregroundColor(.white)
                                                    .padding(.horizontal, 14)
                                                    .padding(.vertical, 7)
                                                    .background(Color.evrryPrimaryBlue)
                                                    .clipShape(RoundedRectangle(cornerRadius: 10))
                                            }
                                        }
                                    }
                                    .padding(12)
                                    .background(Color.white)
                                    .clipShape(RoundedRectangle(cornerRadius: 16))
                                    .overlay(RoundedRectangle(cornerRadius: 16).stroke(bid.isCounterOffer ? Color.orange.opacity(0.4) : Color.evrrySubtleBorder, lineWidth: 1))
                                }
                                
                                Button("Cancel Request") {
                                    viewModel.cancelRide()
                                }
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.evrryRedDestructive)
                                .padding(.top, 4)
                            }
                        }
                        
                    case .accepted:
                        if let driver = viewModel.activeRide.driver {
                            VStack(spacing: 14) {
                                HStack {
                                    ZStack {
                                        Circle()
                                            .fill(Color.blue.opacity(0.1))
                                            .frame(width: 48, height: 48)
                                        Text("👨‍✈️").font(.system(size: 24))
                                    }
                                    
                                    VStack(alignment: .leading, spacing: 2) {
                                        HStack(spacing: 5) {
                                            Text(driver.name)
                                                .font(.system(size: 15, weight: .bold))
                                                .foregroundColor(.evrryDarkCharcoal)
                                            if driver.isVerified {
                                                HStack(spacing: 2) {
                                                    Image(systemName: "checkmark.seal.fill")
                                                        .font(.system(size: 10))
                                                        .foregroundColor(.evrryPrimaryBlue)
                                                    Text("Verified")
                                                        .font(.system(size: 9, weight: .bold))
                                                        .foregroundColor(.evrryPrimaryBlue)
                                                }
                                                .padding(.horizontal, 5)
                                                .padding(.vertical, 2)
                                                .background(Color.blue.opacity(0.08))
                                                .clipShape(Capsule())
                                            }
                                        }
                                        Text("\(driver.vehicleModel) · \(driver.vehiclePlate)")
                                            .font(.system(size: 12))
                                            .foregroundColor(.evrryMutedText)
                                        Text("⭐ \(String(format: "%.2f", driver.rating)) (\(driver.tripsCompleted) trips)")
                                            .font(.system(size: 11, weight: .bold))
                                            .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                                    }
                                    
                                    Spacer()
                                    
                                    // PASSENGER OTP 1234
                                    VStack(spacing: 1) {
                                        Text("TRIP OTP")
                                            .font(.system(size: 9, weight: .bold))
                                            .foregroundColor(.evrryPrimaryBlue)
                                        Text(viewModel.activeRide.otpCode)
                                            .font(.system(size: 18, weight: .black))
                                            .foregroundColor(.evrryPrimaryBlue)
                                    }
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 6)
                                    .background(Color.blue.opacity(0.08))
                                    .clipShape(RoundedRectangle(cornerRadius: 12))
                                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrryPrimaryBlue, lineWidth: 1.5))
                                }
                                
                                HStack(spacing: 8) {
                                    Button(action: {
                                        showCallChooserSheet = true
                                    }) {
                                        HStack(spacing: 4) {
                                            Image(systemName: "phone.fill")
                                            Text("Call")
                                        }
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(.evrryPrimaryBlue)
                                        .frame(maxWidth: .infinity)
                                        .padding(.vertical, 10)
                                        .background(Color.blue.opacity(0.08))
                                        .clipShape(RoundedRectangle(cornerRadius: 12))
                                    }
                                    
                                    Button(action: {
                                        onChatClick(driver.name, driver.phone)
                                    }) {
                                        HStack(spacing: 4) {
                                            Image(systemName: "message.fill")
                                            Text("Chat")
                                        }
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(.evrryPrimaryBlue)
                                        .frame(maxWidth: .infinity)
                                        .padding(.vertical, 10)
                                        .background(Color.blue.opacity(0.08))
                                        .clipShape(RoundedRectangle(cornerRadius: 12))
                                    }
                                    
                                    Button(action: { viewModel.completeRide() }) {
                                        HStack(spacing: 4) {
                                            Image(systemName: "checkmark.circle.fill")
                                            Text("Complete")
                                        }
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(Color(red: 0.09, green: 0.40, blue: 0.20))
                                        .frame(maxWidth: .infinity)
                                        .padding(.vertical, 10)
                                        .background(Color(red: 0.86, green: 0.99, blue: 0.91))
                                        .clipShape(RoundedRectangle(cornerRadius: 12))
                                    }
                                }
                            }
                            .padding(16)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 20))
                            .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.evrryGreenSuccess, lineWidth: 1.5))
                        }
                        
                    case .completed:
                        VStack(spacing: 12) {
                            ZStack {
                                Circle()
                                    .fill(Color(red: 0.86, green: 0.99, blue: 0.91))
                                    .frame(width: 48, height: 48)
                                Image(systemName: "checkmark")
                                    .foregroundColor(Color(red: 0.09, green: 0.40, blue: 0.20))
                                    .font(.system(size: 20, weight: .bold))
                            }
                            Text("Trip Completed!")
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Text("Fare: Rs \(Int(viewModel.activeRide.estimatedFareNpr)) · Distance: \(String(format: "%.1f", viewModel.activeRide.distanceKm)) km")
                                .font(.system(size: 13))
                                .foregroundColor(.evrryMutedText)
                            
                            if !viewModel.activeRide.reviewSubmitted {
                                Button(action: { showRatingSheet = true }) {
                                    Text("Rate & Review Driver ⭐")
                                        .font(.system(size: 14, weight: .bold))
                                        .foregroundColor(.white)
                                        .frame(maxWidth: .infinity)
                                        .frame(height: 46)
                                        .background(Color.evrryPrimaryBlue)
                                        .clipShape(RoundedRectangle(cornerRadius: 12))
                                }
                            } else {
                                Text("Review Submitted! Thank you.")
                                    .font(.system(size: 12, weight: .bold))
                                    .foregroundColor(Color(red: 0.09, green: 0.40, blue: 0.20))
                                    .padding(8)
                                    .background(Color(red: 0.86, green: 0.99, blue: 0.91))
                                    .clipShape(RoundedRectangle(cornerRadius: 8))
                            }
                            
                            Button(action: { viewModel.resetRide() }) {
                                Text("Book Another Ride")
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.evrryPrimaryBlue)
                                    .frame(maxWidth: .infinity)
                                    .frame(height: 46)
                                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrryPrimaryBlue, lineWidth: 1))
                            }
                        }
                        .padding(20)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: 20))
                        
                    case .cancelled:
                        VStack(spacing: 10) {
                            Text("Trip Cancelled")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.evrryRedDestructive)
                            Button("Reset") { viewModel.resetRide() }
                                .font(.system(size: 13, weight: .bold))
                        }
                        .padding(16)
                        .frame(maxWidth: .infinity)
                        .background(Color.red.opacity(0.08))
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        
                    case .idle, .arrived, .tripActive:
                        // 4. BOOKING MODE SWITCHER (FIXED FARE vs INDRIVE NEGOTIATION)
                        HStack(spacing: 8) {
                            ForEach(BookingMode.allCases) { mode in
                                let isSelected = viewModel.activeRide.bookingMode == mode
                                Button(action: { viewModel.setBookingMode(mode) }) {
                                    HStack(spacing: 6) {
                                        Text(mode.title)
                                            .font(.system(size: 13, weight: .bold))
                                    }
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 10)
                                    .background(isSelected ? Color.evrryPrimaryBlue : Color.white)
                                    .foregroundColor(isSelected ? .white : .evrryDarkCharcoal)
                                    .clipShape(RoundedRectangle(cornerRadius: 12))
                                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(isSelected ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: 1))
                                }
                            }
                        }
                        
                        // 5. VEHICLE CLASS OPTIONS
                        VStack(alignment: .leading, spacing: 8) {
                            Text("CHOOSE VEHICLE")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.evrryMutedText)
                            
                            ForEach(RideClass.allCases) { rClass in
                                let isSelected = viewModel.activeRide.rideClass == rClass
                                let fare = rClass.baseFareNpr + (rClass.perKmRateNpr * viewModel.activeRide.distanceKm)
                                
                                Button(action: { viewModel.selectClass(rClass) }) {
                                    HStack(spacing: 12) {
                                        ZStack {
                                            RoundedRectangle(cornerRadius: 12)
                                                .fill(isSelected ? Color.white : Color(red: 0.95, green: 0.96, blue: 0.98))
                                                .frame(width: 46, height: 46)
                                            Text(rClass.emoji).font(.system(size: 26))
                                        }
                                        
                                        VStack(alignment: .leading, spacing: 2) {
                                            HStack(spacing: 6) {
                                                Text(rClass.title)
                                                    .font(.system(size: 15, weight: .bold))
                                                    .foregroundColor(.evrryDarkCharcoal)
                                                if isSelected {
                                                    Image(systemName: "checkmark.circle.fill")
                                                        .foregroundColor(.evrryPrimaryBlue)
                                                        .font(.system(size: 14))
                                                }
                                            }
                                            Text("\(rClass.capacity) · ~\(viewModel.activeRide.durationMinutes) min ETA")
                                                .font(.system(size: 11))
                                                .foregroundColor(.evrryMutedText)
                                        }
                                        Spacer()
                                        VStack(alignment: .trailing, spacing: 1) {
                                            Text("Rs \(Int(fare))")
                                                .font(.system(size: 17, weight: .black))
                                                .foregroundColor(.evrryDarkCharcoal)
                                            Text("Rs \(Int(rClass.perKmRateNpr))/km")
                                                .font(.system(size: 10))
                                                .foregroundColor(.evrryMutedText)
                                        }
                                    }
                                    .padding(14)
                                    .background(isSelected ? Color.blue.opacity(0.06) : Color.white)
                                    .clipShape(RoundedRectangle(cornerRadius: 18))
                                    .overlay(RoundedRectangle(cornerRadius: 18).stroke(isSelected ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: isSelected ? 2 : 1))
                                }
                            }
                        }
                        
                        // 6. INDRIVE BIDDING STEPPER (WHEN BIDDING IS ACTIVE)
                        if viewModel.activeRide.bookingMode == .bidding {
                            VStack(spacing: 12) {
                                HStack {
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text("YOUR OFFERED FARE")
                                            .font(.system(size: 11, weight: .bold))
                                            .foregroundColor(.evrryMutedText)
                                        Text("Drivers nearby can accept or propose a counter-offer")
                                            .font(.system(size: 11))
                                            .foregroundColor(.evrryMutedText)
                                    }
                                    Spacer()
                                }
                                
                                HStack(spacing: 16) {
                                    Button(action: { viewModel.updateOfferedFare(delta: -10) }) {
                                        Text("− Rs 10")
                                            .font(.system(size: 13, weight: .bold))
                                            .foregroundColor(.evrryDarkCharcoal)
                                            .padding(.horizontal, 14)
                                            .padding(.vertical, 10)
                                            .background(Color(red: 0.95, green: 0.96, blue: 0.98))
                                            .clipShape(RoundedRectangle(cornerRadius: 10))
                                    }
                                    
                                    Spacer()
                                    
                                    VStack(spacing: 2) {
                                        Text("Rs \(Int(viewModel.activeRide.offeredFareNpr))")
                                            .font(.system(size: 26, weight: .black))
                                            .foregroundColor(.evrryPrimaryBlue)
                                        Text(fareDiffText)
                                            .font(.system(size: 10, weight: .semibold))
                                            .foregroundColor(fareDiffColor)
                                    }
                                    
                                    Spacer()
                                    
                                    Button(action: { viewModel.updateOfferedFare(delta: 10) }) {
                                        Text("+ Rs 10")
                                            .font(.system(size: 13, weight: .bold))
                                            .foregroundColor(.evrryPrimaryBlue)
                                            .padding(.horizontal, 14)
                                            .padding(.vertical, 10)
                                            .background(Color.blue.opacity(0.08))
                                            .clipShape(RoundedRectangle(cornerRadius: 10))
                                    }
                                }
                                
                                // Quick preset chips
                                HStack(spacing: 8) {
                                    Button(action: { viewModel.setCustomOfferedFare(max(30.0, viewModel.activeRide.estimatedFareNpr - 20)) }) {
                                        Text("−Rs 20 (Budget)")
                                            .font(.system(size: 11, weight: .semibold))
                                            .foregroundColor(.evrryDarkCharcoal)
                                            .padding(.horizontal, 10)
                                            .padding(.vertical, 6)
                                            .background(Color(red: 0.95, green: 0.96, blue: 0.98))
                                            .clipShape(Capsule())
                                    }
                                    
                                    Button(action: { viewModel.setCustomOfferedFare(viewModel.activeRide.estimatedFareNpr) }) {
                                        Text("Fair Rate (Rs \(Int(viewModel.activeRide.estimatedFareNpr)))")
                                            .font(.system(size: 11, weight: .bold))
                                            .foregroundColor(.evrryPrimaryBlue)
                                            .padding(.horizontal, 10)
                                            .padding(.vertical, 6)
                                            .background(Color.blue.opacity(0.08))
                                            .clipShape(Capsule())
                                    }
                                    
                                    Button(action: { viewModel.setCustomOfferedFare(viewModel.activeRide.estimatedFareNpr + 20) }) {
                                        Text("+Rs 20 (Fast)")
                                            .font(.system(size: 11, weight: .semibold))
                                            .foregroundColor(.evrryDarkCharcoal)
                                            .padding(.horizontal, 10)
                                            .padding(.vertical, 6)
                                            .background(Color(red: 0.95, green: 0.96, blue: 0.98))
                                            .clipShape(Capsule())
                                    }
                                }
                            }
                            .padding(14)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 18))
                            .overlay(RoundedRectangle(cornerRadius: 18).stroke(Color.evrryPrimaryBlue.opacity(0.3), lineWidth: 1.5))
                        }
                    }
                }
                .padding(16)
            }
            
            // 7. BOTTOM BOOK CTA
            if viewModel.activeRide.status == .idle {
                VStack {
                    let buttonTitle = viewModel.activeRide.bookingMode == .bidding
                        ? "Broadcast Offer · Rs \(Int(viewModel.activeRide.offeredFareNpr))"
                        : "Book \(viewModel.activeRide.rideClass.title) · Rs \(Int(viewModel.activeRide.estimatedFareNpr))"
                    
                    Button(action: { viewModel.requestRide() }) {
                        HStack {
                            if viewModel.isBooking {
                                ProgressView().tint(.white)
                            } else {
                                Text(buttonTitle)
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.white)
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(Color.evrryPrimaryBlue)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                    }
                    .disabled(viewModel.isBooking)
                }
                .padding(16)
                .background(Color.white)
                .shadow(color: Color.black.opacity(0.06), radius: 6, x: 0, y: -2)
            }
        }
        .background(Color.evrryBgTop.ignoresSafeArea())
        .sheet(item: Binding<IdentifiableString?>(
            get: { pickerMode.map { IdentifiableString(id: $0) } },
            set: { pickerMode = $0?.id }
        )) { mode in
            LocationPickerSheetView(
                title: mode.id == "pickup" ? "Select Pickup Location" : "Select Destination",
                onLocationSelected: { point in
                    if mode.id == "pickup" {
                        viewModel.setPickup(point)
                    } else {
                        viewModel.setDrop(point)
                    }
                    pickerMode = nil
                },
                onDismiss: { pickerMode = nil }
            )
        }
        .sheet(isPresented: $showRatingSheet) {
            if let driver = viewModel.activeRide.driver {
                RideRatingSheetView(
                    driver: driver,
                    fareNpr: viewModel.activeRide.estimatedFareNpr,
                    onSubmit: { rating, comment in
                        viewModel.submitReview(rating: rating, comment: comment)
                        showRatingSheet = false
                    },
                    onDismiss: { showRatingSheet = false }
                )
            }
        }
        .sheet(isPresented: $showCallChooserSheet) {
            if let driver = viewModel.activeRide.driver {
                CallChooserSheetView(
                    driver: driver,
                    onCallInApp: {
                        showCallChooserSheet = false
                        onInAppCallClick(driver.name, driver.phone)
                    },
                    onDirectSimCall: {
                        showCallChooserSheet = false
                        let cleanPhone = driver.phone.replacingOccurrences(of: " ", with: "")
                        if let url = URL(string: "tel://\(cleanPhone)"), UIApplication.shared.canOpenURL(url) {
                            UIApplication.shared.open(url)
                        }
                    },
                    onDismiss: { showCallChooserSheet = false }
                )
            }
        }
    }
    
    private var fareDiffText: String {
        let diff = Int(viewModel.activeRide.offeredFareNpr - viewModel.activeRide.estimatedFareNpr)
        if diff == 0 {
            return "Fair estimated rate"
        } else if diff > 0 {
            return "Rs +\(diff) above standard fare"
        } else {
            return "Rs \(diff) below standard fare"
        }
    }
    
    private var fareDiffColor: Color {
        let diff = Int(viewModel.activeRide.offeredFareNpr - viewModel.activeRide.estimatedFareNpr)
        if diff == 0 {
            return Color.evrryMutedText
        } else if diff > 0 {
            return Color(red: 0.09, green: 0.64, blue: 0.29)
        } else {
            return Color.orange
        }
    }
}

public struct CallChooserSheetView: View {
    public let driver: AssignedDriver
    public let onCallInApp: () -> Void
    public let onDirectSimCall: () -> Void
    public let onDismiss: () -> Void
    
    public var body: some View {
        VStack(spacing: 16) {
            // HEADER
            HStack(spacing: 12) {
                ZStack {
                    Circle()
                        .fill(Color.blue.opacity(0.1))
                        .frame(width: 48, height: 48)
                    Text("📞").font(.system(size: 22))
                }
                VStack(alignment: .leading, spacing: 2) {
                    Text("Call \(driver.name)")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text("Choose your preferred calling method")
                        .font(.system(size: 12))
                        .foregroundColor(.evrryMutedText)
                }
                Spacer()
            }
            
            Divider().background(Color.evrrySubtleBorder)
            
            // OPTION 1: CALL IN APP
            Button(action: onCallInApp) {
                HStack(spacing: 14) {
                    ZStack {
                        Circle()
                            .fill(Color.evrryPrimaryBlue)
                            .frame(width: 42, height: 42)
                        Image(systemName: "phone.badge.waveform.fill")
                            .foregroundColor(.white)
                            .font(.system(size: 17))
                    }
                    VStack(alignment: .leading, spacing: 2) {
                        HStack(spacing: 6) {
                            Text("Call in App")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Text("FREE · VOIP")
                                .font(.system(size: 9, weight: .bold))
                                .foregroundColor(Color(red: 0.09, green: 0.40, blue: 0.20))
                                .padding(.horizontal, 5)
                                .padding(.vertical, 2)
                                .background(Color(red: 0.86, green: 0.99, blue: 0.91))
                                .clipShape(RoundedRectangle(cornerRadius: 6))
                        }
                        Text("Free internet audio call · Phone number hidden")
                            .font(.system(size: 12))
                            .foregroundColor(.evrryMutedText)
                    }
                    Spacer()
                }
                .padding(14)
                .background(Color.blue.opacity(0.06))
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrryPrimaryBlue.opacity(0.3), lineWidth: 1))
            }
            .buttonStyle(PlainButtonStyle())
            
            // OPTION 2: DIRECT SIM CALL
            Button(action: onDirectSimCall) {
                HStack(spacing: 14) {
                    ZStack {
                        Circle()
                            .fill(Color(red: 0.39, green: 0.45, blue: 0.55))
                            .frame(width: 42, height: 42)
                        Image(systemName: "phone.fill")
                            .foregroundColor(.white)
                            .font(.system(size: 17))
                    }
                    VStack(alignment: .leading, spacing: 2) {
                        HStack(spacing: 6) {
                            Text("Direct SIM Call")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.evrryDarkCharcoal)
                            Text("CARRIER")
                                .font(.system(size: 9, weight: .bold))
                                .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                                .padding(.horizontal, 5)
                                .padding(.vertical, 2)
                                .background(Color(red: 0.99, green: 0.95, blue: 0.78))
                                .clipShape(RoundedRectangle(cornerRadius: 6))
                        }
                        Text("Standard cellular call · \(driver.phone)")
                            .font(.system(size: 12))
                            .foregroundColor(.evrryMutedText)
                    }
                    Spacer()
                }
                .padding(14)
                .background(Color(red: 0.97, green: 0.98, blue: 0.99))
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.evrrySubtleBorder, lineWidth: 1))
            }
            .buttonStyle(PlainButtonStyle())
            
            Button(action: onDismiss) {
                Text("Cancel")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(.evrryMutedText)
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
                    .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrrySubtleBorder, lineWidth: 1))
            }
            .padding(.top, 4)
        }
        .padding(20)
    }
}

private struct IdentifiableString: Identifiable {
    let id: String
}
