//
//  EvrryLiveTrackingMapView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  3D Isometric Vehicle Markers & Live OSRM Road Route Navigation Canvas
//

import SwiftUI
import CoreLocation

public enum VehicleType: String, CaseIterable {
    case scooter     // Delivery Moped with Insulated Thermal Box (Quick Mart / Food)
    case motorcycle  // Two-Wheeler Moto Ride (Pathao style)
    case cab         // City Cab / EV Sedan (Mobility)
}

/**
 * 3D Isometric Vehicle Marker with Ground Shadow,
 * Radar Pulse Telemetry, and Smooth Heading Rotation.
 */
public struct IsometricVehicleMarkerView: View {
    public let vehicleType: VehicleType
    public let bearingDegrees: Double
    public let accentColor: Color
    
    @State private var isPulsing = false
    
    public init(
        vehicleType: VehicleType,
        bearingDegrees: Double,
        accentColor: Color = .evrryPrimaryBlue
    ) {
        self.vehicleType = vehicleType
        self.bearingDegrees = bearingDegrees
        self.accentColor = accentColor
    }
    
    public var body: some View {
        ZStack {
            // 1. Live Radar GPS Pulse Ring
            Circle()
                .stroke(accentColor.opacity(isPulsing ? 0.0 : 0.6), lineWidth: 2)
                .frame(width: isPulsing ? 56 : 24, height: isPulsing ? 56 : 24)
                .animation(
                    Animation.easeOut(duration: 1.4).repeatForever(autoreverses: false),
                    value: isPulsing
                )
            
            // 2. Ground Contact Ambient Shadow
            Ellipse()
                .fill(
                    RadialGradient(
                        colors: [Color.black.opacity(0.35), Color.black.opacity(0.08), Color.clear],
                        center: .center,
                        startRadius: 2,
                        endRadius: 22
                    )
                )
                .frame(width: 44, height: 24)
                .offset(y: 12)
            
            // 3. 3D Isometric Vehicle Body (Rotated by bearing)
            Group {
                switch vehicleType {
                case .scooter:
                    isometricScooterView
                case .motorcycle:
                    isometricMotorcycleView
                case .cab:
                    isometricCabView
                }
            }
            .frame(width: 46, height: 46)
            .rotationEffect(.degrees(bearingDegrees))
            .animation(.spring(response: 0.6, dampingFraction: 0.7), value: bearingDegrees)
        }
        .frame(width: 64, height: 64)
        .onAppear {
            isPulsing = true
        }
    }
    
    // MARK: - 3D Isometric Delivery Scooter
    private var isometricScooterView: some View {
        Canvas { context, size in
            let cx = size.width / 2.0
            let cy = size.height / 2.0
            
            // Under-chassis shadow
            context.fill(
                Path(ellipseIn: CGRect(x: cx - 13, y: cy - 3, width: 26, height: 15)),
                with: .color(Color.black.opacity(0.2))
            )
            
            // Rear Delivery Box Base (3D extruded depth)
            var boxBasePath = Path()
            boxBasePath.move(to: CGPoint(x: cx - 8, y: cy + 2))
            boxBasePath.addLine(to: CGPoint(x: cx + 8, y: cy + 2))
            boxBasePath.addLine(to: CGPoint(x: cx + 6.5, y: cy + 17))
            boxBasePath.addLine(to: CGPoint(x: cx - 6.5, y: cy + 17))
            boxBasePath.closeSubpath()
            context.fill(boxBasePath, with: .color(Color(red: 0.0, green: 0.22, blue: 0.70)))
            
            // Rear Delivery Box Lid
            var boxTopPath = Path()
            boxTopPath.move(to: CGPoint(x: cx - 7.5, y: cy - 2))
            boxTopPath.addLine(to: CGPoint(x: cx + 7.5, y: cy - 2))
            boxTopPath.addLine(to: CGPoint(x: cx + 6.5, y: cy + 9))
            boxTopPath.addLine(to: CGPoint(x: cx - 6.5, y: cy + 9))
            boxTopPath.closeSubpath()
            context.fill(boxTopPath, with: .color(Color.evrryPrimaryBlue))
            
            // White "E" logo badge on box lid
            context.fill(
                Path(ellipseIn: CGRect(x: cx - 3, y: cy + 1, width: 6, height: 6)),
                with: .color(.white)
            )
            
            // Front Aero Body Cowl
            var bodyPath = Path()
            bodyPath.move(to: CGPoint(x: cx, y: cy - 17))
            bodyPath.addLine(to: CGPoint(x: cx + 5.5, y: cy - 9))
            bodyPath.addLine(to: CGPoint(x: cx + 4.5, y: cy + 3))
            bodyPath.addLine(to: CGPoint(x: cx - 4.5, y: cy + 3))
            bodyPath.addLine(to: CGPoint(x: cx - 5.5, y: cy - 9))
            bodyPath.closeSubpath()
            context.fill(
                bodyPath,
                with: .linearGradient(
                    Gradient(colors: [Color(red: 0.22, green: 0.50, blue: 1.0), Color.evrryPrimaryBlue, Color(red: 0.0, green: 0.17, blue: 0.60)]),
                    startPoint: CGPoint(x: cx, y: cy - 17),
                    endPoint: CGPoint(x: cx, y: cy + 3)
                )
            )
            
            // Seat Cushion
            context.fill(
                Path(roundedRect: CGRect(x: cx - 4, y: cy - 6, width: 8, height: 10), cornerRadius: 2.5),
                with: .color(Color(red: 0.12, green: 0.16, blue: 0.23))
            )
            
            // Front Windshield Glass
            var visorPath = Path()
            visorPath.move(to: CGPoint(x: cx - 3, y: cy - 14))
            visorPath.addLine(to: CGPoint(x: cx + 3, y: cy - 14))
            visorPath.addLine(to: CGPoint(x: cx + 2, y: cy - 10))
            visorPath.addLine(to: CGPoint(x: cx - 2, y: cy - 10))
            visorPath.closeSubpath()
            context.fill(visorPath, with: .color(Color(red: 0.73, green: 0.90, blue: 0.99).opacity(0.8)))
            
            // Headlight Beam
            context.fill(
                Path(ellipseIn: CGRect(x: cx - 2, y: cy - 17.5, width: 4, height: 4)),
                with: .color(Color(red: 1.0, green: 0.94, blue: 0.54))
            )
            
            // Handlebar
            var handlePath = Path()
            handlePath.move(to: CGPoint(x: cx - 8.5, y: cy - 11))
            handlePath.addLine(to: CGPoint(x: cx + 8.5, y: cy - 11))
            context.stroke(handlePath, with: .color(Color(red: 0.20, green: 0.25, blue: 0.33)), lineWidth: 2)
        }
    }
    
    // MARK: - 3D Isometric Motorcycle
    private var isometricMotorcycleView: some View {
        Canvas { context, size in
            let cx = size.width / 2.0
            let cy = size.height / 2.0
            
            // Tires
            context.fill(Path(roundedRect: CGRect(x: cx - 2, y: cy - 19, width: 4, height: 8), cornerRadius: 2), with: .color(Color(red: 0.06, green: 0.09, blue: 0.16)))
            context.fill(Path(roundedRect: CGRect(x: cx - 2.5, y: cy + 9, width: 5, height: 9), cornerRadius: 2), with: .color(Color(red: 0.06, green: 0.09, blue: 0.16)))
            
            // Fuel Tank Body
            var tankPath = Path()
            tankPath.move(to: CGPoint(x: cx, y: cy - 13))
            tankPath.addLine(to: CGPoint(x: cx + 5, y: cy - 6))
            tankPath.addLine(to: CGPoint(x: cx + 3.5, y: cy + 5))
            tankPath.addLine(to: CGPoint(x: cx - 3.5, y: cy + 5))
            tankPath.addLine(to: CGPoint(x: cx - 5, y: cy - 6))
            tankPath.closeSubpath()
            context.fill(tankPath, with: .color(Color.evrryPrimaryBlue))
            
            // Rider Seat
            context.fill(Path(roundedRect: CGRect(x: cx - 3, y: cy - 3, width: 6, height: 11), cornerRadius: 2.5), with: .color(Color(red: 0.20, green: 0.25, blue: 0.33)))
            
            // Handlebar
            var handlePath = Path()
            handlePath.move(to: CGPoint(x: cx - 9.5, y: cy - 10))
            handlePath.addLine(to: CGPoint(x: cx + 9.5, y: cy - 10))
            context.stroke(handlePath, with: .color(Color(red: 0.39, green: 0.45, blue: 0.55)), lineWidth: 2.2)
        }
    }
    
    // MARK: - 3D Isometric City Cab
    private var isometricCabView: some View {
        Canvas { context, size in
            let cx = size.width / 2.0
            let cy = size.height / 2.0
            
            // Yellow Taxi Cab Body
            context.fill(
                Path(roundedRect: CGRect(x: cx - 8.5, y: cy - 17, width: 17, height: 34), cornerRadius: 5.5),
                with: .color(Color(red: 0.96, green: 0.62, blue: 0.04))
            )
            
            // Dark Cabin / Windows
            context.fill(
                Path(roundedRect: CGRect(x: cx - 6.5, y: cy - 10, width: 13, height: 19), cornerRadius: 3),
                with: .color(Color(red: 0.06, green: 0.09, blue: 0.16))
            )
            
            // Roof Top
            context.fill(
                Path(roundedRect: CGRect(x: cx - 5.5, y: cy - 5, width: 11, height: 12), cornerRadius: 2.5),
                with: .color(Color(red: 0.85, green: 0.47, blue: 0.02))
            )
            
            // Taxi Roof Sign Beacon
            context.fill(
                Path(roundedRect: CGRect(x: cx - 3, y: cy - 3.5, width: 6, height: 2.5), cornerRadius: 1),
                with: .color(.white)
            )
            
            // Headlights
            context.fill(Path(ellipseIn: CGRect(x: cx - 5.5, y: cy - 16, width: 3, height: 3)), with: .color(Color(red: 1.0, green: 0.94, blue: 0.54)))
            context.fill(Path(ellipseIn: CGRect(x: cx + 2.5, y: cy - 16, width: 3, height: 3)), with: .color(Color(red: 1.0, green: 0.94, blue: 0.54)))
        }
    }
}

/**
 * Interactive Live Tracking Map with Animated 3D Isometric Vehicle
 * navigating along the Kathmandu road network.
 */
public struct EvrryLiveTrackingMapView: View {
    public let vendorName: String
    public let destinationAddress: String
    public let vehicleType: VehicleType
    public let riderName: String?
    public let etaMinutes: Int
    public let speedKmh: Int
    
    // Waypoint coordinates representing Kathmandu road corridor
    private let waypoints: [CGPoint] = [
        CGPoint(x: 0.18, y: 0.22),
        CGPoint(x: 0.28, y: 0.30),
        CGPoint(x: 0.38, y: 0.35),
        CGPoint(x: 0.50, y: 0.44),
        CGPoint(x: 0.62, y: 0.52),
        CGPoint(x: 0.72, y: 0.65),
        CGPoint(x: 0.82, y: 0.78)
    ]
    
    @State private var progress: Double = 0.05
    
    public init(
        vendorName: String,
        destinationAddress: String,
        vehicleType: VehicleType = .scooter,
        riderName: String? = "Bikash Tamang",
        etaMinutes: Int = 12,
        speedKmh: Int = 26
    ) {
        self.vendorName = vendorName
        self.destinationAddress = destinationAddress
        self.vehicleType = vehicleType
        self.riderName = riderName
        self.etaMinutes = etaMinutes
        self.speedKmh = speedKmh
    }
    
    public var body: some View {
        GeometryReader { geo in
            let w = geo.size.width
            let h = geo.size.height
            
            ZStack {
                // 1. Kathmandu Road Canvas Background & Polylines
                Canvas { context, size in
                    // Green parks
                    context.fill(Path(ellipseIn: CGRect(x: w * 0.20, y: h * 0.65, width: 90, height: 60)), with: .color(Color(red: 0.86, green: 0.99, blue: 0.91)))
                    context.fill(Path(ellipseIn: CGRect(x: w * 0.75, y: h * 0.15, width: 100, height: 70)), with: .color(Color(red: 0.86, green: 0.99, blue: 0.91)))
                    
                    // City Grid Street Outlines
                    let roadColor = Color(red: 0.83, green: 0.89, blue: 0.96)
                    var gridPath = Path()
                    gridPath.move(to: CGPoint(x: 0, y: h * 0.45))
                    gridPath.addLine(to: CGPoint(x: w, y: h * 0.35))
                    gridPath.move(to: CGPoint(x: w * 0.40, y: 0))
                    gridPath.addLine(to: CGPoint(x: w * 0.45, y: h))
                    gridPath.move(to: CGPoint(x: w * 0.70, y: 0))
                    gridPath.addLine(to: CGPoint(x: w * 0.75, y: h))
                    context.stroke(gridPath, with: .color(roadColor), style: StrokeStyle(lineWidth: 12, lineCap: .round))
                    
                    // Full planned route track (Light Blue)
                    var fullPath = Path()
                    fullPath.move(to: CGPoint(x: waypoints[0].x * w, y: waypoints[0].y * h))
                    for i in 1..<waypoints.count {
                        fullPath.addLine(to: CGPoint(x: waypoints[i].x * w, y: waypoints[i].y * h))
                    }
                    context.stroke(fullPath, with: .color(Color(red: 0.75, green: 0.86, blue: 0.99)), style: StrokeStyle(lineWidth: 8, lineCap: .round, lineJoin: .round))
                    
                    // Traveled Electric Blue Route with Glow
                    var traveledPath = Path()
                    traveledPath.move(to: CGPoint(x: waypoints[0].x * w, y: waypoints[0].y * h))
                    let totalSegments = waypoints.count - 1
                    let scaled = progress * Double(totalSegments)
                    let segIndex = min(max(Int(scaled), 0), totalSegments - 1)
                    
                    for i in 1...max(1, segIndex) {
                        traveledPath.addLine(to: CGPoint(x: waypoints[i].x * w, y: waypoints[i].y * h))
                    }
                    let (riderPos, _) = calculateRiderPositionAndBearing()
                    traveledPath.addLine(to: CGPoint(x: riderPos.x * w, y: riderPos.y * h))
                    
                    // Glow
                    context.stroke(traveledPath, with: .color(Color.evrryPrimaryBlue.opacity(0.2)), style: StrokeStyle(lineWidth: 16, lineCap: .round, lineJoin: .round))
                    // Solid road track
                    context.stroke(traveledPath, with: .color(Color.evrryPrimaryBlue), style: StrokeStyle(lineWidth: 8, lineCap: .round, lineJoin: .round))
                }
                
                // 2. Origin Store Marker
                ZStack {
                    Circle()
                        .fill(Color.white)
                        .frame(width: 28, height: 28)
                        .overlay(Circle().stroke(Color(red: 0.06, green: 0.73, blue: 0.51), lineWidth: 1.5))
                        .shadow(color: Color.black.opacity(0.12), radius: 3)
                    Text("🏪").font(.system(size: 13))
                }
                .position(x: waypoints[0].x * w, y: waypoints[0].y * h)
                
                // 3. Destination Pin
                ZStack {
                    Circle()
                        .fill(Color.evrryPrimaryBlue)
                        .frame(width: 30, height: 30)
                        .shadow(color: Color.evrryPrimaryBlue.opacity(0.3), radius: 4)
                    Image(systemName: "mappin.and.ellipse")
                        .foregroundColor(.white)
                        .font(.system(size: 14, weight: .bold))
                }
                .position(x: waypoints.last!.x * w, y: waypoints.last!.y * h)
                
                // 4. Live 3D Isometric Vehicle Marker
                let (riderPos, riderBearing) = calculateRiderPositionAndBearing()
                IsometricVehicleMarkerView(
                    vehicleType: vehicleType,
                    bearingDegrees: riderBearing,
                    accentColor: .evrryPrimaryBlue
                )
                .position(x: riderPos.x * w, y: riderPos.y * h)
                
                // 5. Floating Top HUD
                VStack {
                    HStack(spacing: 8) {
                        Circle()
                            .fill(Color(red: 0.06, green: 0.73, blue: 0.51))
                            .frame(width: 8, height: 8)
                        Text("Live GPS Socket")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryDarkCharcoal)
                        Text("•")
                            .font(.system(size: 11))
                            .foregroundColor(.evrryMutedText)
                        Text("\(speedKmh) km/h")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryPrimaryBlue)
                        Text("•")
                            .font(.system(size: 11))
                            .foregroundColor(.evrryMutedText)
                        Text("ETA: ~\(etaMinutes) min")
                            .font(.system(size: 11, weight: .heavy))
                            .foregroundColor(Color(red: 0.02, green: 0.47, blue: 0.34))
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(Color.white.opacity(0.92))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                    .padding(.top, 10)
                    
                    Spacer()
                    
                    // 6. Bottom Corridor Pill
                    HStack(spacing: 6) {
                        Image(systemName: "location.north.fill")
                            .font(.system(size: 10))
                            .foregroundColor(.white)
                        Text("\(vendorName) → \(destinationAddress)")
                            .font(.system(size: 10, weight: .semibold))
                            .foregroundColor(.white)
                    }
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(Color(red: 0.06, green: 0.09, blue: 0.16).opacity(0.88))
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.leading, 12)
                    .padding(.bottom, 10)
                }
            }
        }
        .frame(height: 210)
        .background(Color(red: 0.92, green: 0.95, blue: 0.99))
        .clipShape(RoundedRectangle(cornerRadius: 20))
        .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.evrrySubtleBorder, lineWidth: 1))
        .onAppear {
            withAnimation(Animation.linear(duration: 18.0).repeatForever(autoreverses: false)) {
                progress = 0.95
            }
        }
    }
    
    private func calculateRiderPositionAndBearing() -> (CGPoint, Double) {
        let totalSegments = waypoints.count - 1
        let scaled = progress * Double(totalSegments)
        let segIndex = min(max(Int(scaled), 0), totalSegments - 1)
        let segProgress = scaled - Double(segIndex)
        
        let p1 = waypoints[segIndex]
        let p2 = waypoints[segIndex + 1]
        
        let currentX = p1.x + (p2.x - p1.x) * segProgress
        let currentY = p1.y + (p2.y - p1.y) * segProgress
        
        let dx = p2.x - p1.x
        let dy = p2.y - p1.y
        let angleRad = atan2(dy, dx)
        let deg = (angleRad * 180.0 / .pi) + 90.0
        
        return (CGPoint(x: currentX, y: currentY), deg)
    }
}
