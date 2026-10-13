//
//  PartnerMapHUDView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Interactive 3D Isometric Navigation Map HUD for iOS
//

import SwiftUI
import CoreLocation

public struct PartnerMapHUDView: View {
    public let pickupAddress: String
    public let dropAddress: String
    public let vehicleType: String
    public let bearingDegrees: Double
    public let speedKmh: Int
    public let etaMinutes: Int
    public let status: DriverRideStatus
    
    @State private var progress: CGFloat = 0.05
    @State private var pulseScale: CGFloat = 1.0
    
    public init(
        pickupAddress: String,
        dropAddress: String,
        vehicleType: String = "bike",
        bearingDegrees: Double = 45.0,
        speedKmh: Int = 28,
        etaMinutes: Int = 12,
        status: DriverRideStatus = .accepted
    ) {
        self.pickupAddress = pickupAddress
        self.dropAddress = dropAddress
        self.vehicleType = vehicleType
        self.bearingDegrees = bearingDegrees
        self.speedKmh = speedKmh
        self.etaMinutes = etaMinutes
        self.status = status
    }
    
    public var body: some View {
        GeometryReader { geo in
            let w = geo.size.width
            let h = geo.size.height
            
            let waypoints = [
                CGPoint(x: w * 0.18, y: h * 0.22),
                CGPoint(x: w * 0.30, y: h * 0.32),
                CGPoint(x: w * 0.42, y: h * 0.40),
                CGPoint(x: w * 0.55, y: h * 0.50),
                CGPoint(x: w * 0.68, y: h * 0.62),
                CGPoint(x: w * 0.82, y: h * 0.78)
            ]
            
            let totalSegments = max(waypoints.count - 1, 1)
            let scaled = progress * CGFloat(totalSegments)
            let segIndex = min(max(Int(scaled), 0), totalSegments - 1)
            let segProgress = scaled - CGFloat(segIndex)
            
            let p1 = waypoints[segIndex]
            let p2 = waypoints[segIndex + 1]
            let vehiclePos = CGPoint(
                x: p1.x + (p2.x - p1.x) * segProgress,
                y: p1.y + (p2.y - p1.y) * segProgress
            )
            
            ZStack(alignment: .top) {
            // MAP CANVAS
            Canvas { context, size in
                let w = size.width
                let h = size.height
                
                // Background street lines
                let roadColor = Color(red: 0.84, green: 0.89, blue: 0.96)
                var path1 = Path()
                path1.move(to: CGPoint(x: 0, y: h * 0.45))
                path1.addLine(to: CGPoint(x: w, y: h * 0.35))
                context.stroke(path1, with: .color(roadColor), lineWidth: 14)
                
                var path2 = Path()
                path2.move(to: CGPoint(x: w * 0.38, y: 0))
                path2.addLine(to: CGPoint(x: w * 0.44, y: h))
                context.stroke(path2, with: .color(roadColor), lineWidth: 12)
                
                // Polyline Route Track (Thamel -> Patan)
                var fullTrack = Path()
                fullTrack.move(to: waypoints[0])
                for pt in waypoints.dropFirst() {
                    fullTrack.addLine(to: pt)
                }
                context.stroke(fullTrack, with: .color(Color.blue.opacity(0.25)), lineWidth: 8)
                
                // Traveled Path along segments
                var traveled = Path()
                traveled.move(to: waypoints[0])
                if segIndex > 0 {
                    for i in 1...segIndex {
                        traveled.addLine(to: waypoints[i])
                    }
                }
                traveled.addLine(to: vehiclePos)
                context.stroke(traveled, with: .color(Color(red: 0.12, green: 0.45, blue: 0.94)), lineWidth: 8)
            }
            
            // Origin Pin
            Text("📍")
                .font(.system(size: 16))
                .position(waypoints[0])
            
            // Destination Pin
            Image(systemName: "mappin.circle.fill")
                .font(.system(size: 22))
                .foregroundColor(.red)
                .background(Circle().fill(Color.white).frame(width: 14, height: 14))
                .position(waypoints.last!)
            
            // Live Vehicle Marker with Radar Pulse & Heading Rotation
            ZStack {
                Circle()
                    .fill(Color(red: 0.0, green: 0.37, blue: 1.0).opacity(0.25))
                    .frame(width: 44, height: 44)
                    .scaleEffect(pulseScale)
                
                Image(systemName: (vehicleType == "cab" || vehicleType == "comfort_ev") ? "car.fill" : "bicycle")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(.white)
                    .frame(width: 34, height: 34)
                    .background(Color(red: 0.0, green: 0.37, blue: 1.0))
                    .clipShape(Circle())
                    .shadow(color: Color.black.opacity(0.25), radius: 3, x: 0, y: 2)
                    .rotationEffect(.degrees(bearingDegrees))
            }
            .position(vehiclePos)
            
            // TOP FLOATING GUIDANCE BANNER
            HStack(spacing: 8) {
                Image(systemName: "location.north.line.fill")
                    .foregroundColor(Color(red: 0.38, green: 0.65, blue: 0.98))
                    .font(.system(size: 13))
                Text(status == .accepted ? "Turn right onto Kantipath in 200m" : "Straight on Thapathali in 400m")
                    .font(.system(size: 11, weight: .semibold))
                    .foregroundColor(.white)
                Text("•")
                    .foregroundColor(.gray)
                Text("\(speedKmh) km/h")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(Color(red: 0.38, green: 0.65, blue: 0.98))
                Text("•")
                    .foregroundColor(.gray)
                Text("ETA: ~\(etaMinutes) min")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(Color(red: 0.20, green: 0.83, blue: 0.60))
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(Color(red: 0.06, green: 0.09, blue: 0.16).opacity(0.92))
            .cornerRadius(12)
            .padding(.top, 10)
            
            // BOTTOM CORRIDOR ADDRESS PILL
            VStack {
                Spacer()
                HStack {
                    Text("🛣️ \(pickupAddress) → \(dropAddress)")
                        .font(.system(size: 10, weight: .medium))
                        .foregroundColor(.primary)
                        .lineLimit(1)
                    Spacer()
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 5)
                .background(Color.white.opacity(0.9))
                .cornerRadius(8)
                .padding(10)
            }
        }
    }
    .frame(height: 230)
    .background(Color(red: 0.92, green: 0.95, blue: 0.99))
    .cornerRadius(20)
    .overlay(
        RoundedRectangle(cornerRadius: 20)
            .stroke(Color.gray.opacity(0.2), lineWidth: 1)
    )
    .onAppear {
        withAnimation(.linear(duration: 20.0).repeatForever(autoreverses: false)) {
            progress = 0.95
        }
        withAnimation(.easeInOut(duration: 1.2).repeatForever(autoreverses: true)) {
            pulseScale = 1.4
        }
    }
}
