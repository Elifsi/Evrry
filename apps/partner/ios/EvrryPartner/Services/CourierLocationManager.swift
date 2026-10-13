//
//  CourierLocationManager.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Courier Real-Time GPS Tracking & Broadcast to Supabase driver_locations
//  Grounded in kaaneneskpc_Deliverr & enatega-multivendor-rider
//

import SwiftUI
import CoreLocation
import Combine
import Supabase

public final class CourierLocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    @Published public var currentCoordinate: CLLocationCoordinate2D = CLLocationCoordinate2D(latitude: 27.6780, longitude: 85.3170) // Pulchowk, Lalitpur
    @Published public var currentBearing: Double = 0.0
    @Published public var currentSpeedKmh: Int = 0
    @Published public var isStreaming: Bool = false
    
    private let locationManager = CLLocationManager()
    private var broadcastTimer: Timer?
    private var partnerId: String = "partner_courier_001"
    private var supabaseClient: SupabaseClient?
    
    // Smooth Kathmandu simulation waypoints for offline testing
    private let routeWaypoints: [CLLocationCoordinate2D] = [
        CLLocationCoordinate2D(latitude: 27.6780, longitude: 85.3170), // Pulchowk Store
        CLLocationCoordinate2D(latitude: 27.6795, longitude: 85.3155),
        CLLocationCoordinate2D(latitude: 27.6810, longitude: 85.3140),
        CLLocationCoordinate2D(latitude: 27.6825, longitude: 85.3128),
        CLLocationCoordinate2D(latitude: 27.6830, longitude: 85.3120)  // Jhamsikhel Customer
    ]
    private var currentWaypointIndex: Int = 0
    
    public init(client: SupabaseClient? = nil) {
        self.supabaseClient = client
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        locationManager.distanceFilter = 5.0
    }
    
    public func startStreaming(partnerId: String) {
        self.partnerId = partnerId
        self.isStreaming = true
        
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
        
        broadcastTimer?.invalidate()
        broadcastTimer = Timer.scheduledTimer(withTimeInterval: 3.0, repeats: true) { [weak self] _ in
            self?.stepSimulationOrBroadcast()
        }
    }
    
    public func stopStreaming() {
        self.isStreaming = false
        broadcastTimer?.invalidate()
        broadcastTimer = nil
        locationManager.stopUpdatingLocation()
        currentSpeedKmh = 0
        
        // Notify backend offline
        Task { [weak self] in
            guard let self = self, let client = self.supabaseClient else { return }
            do {
                try await client.from("driver_locations")
                    .update(["is_online": false])
                    .eq("partner_id", value: self.partnerId)
                    .execute()
            } catch {
                // Offline fallback ignored
            }
        }
    }
    
    private func stepSimulationOrBroadcast() {
        guard isStreaming else { return }
        let nextIndex = (currentWaypointIndex + 1) % routeWaypoints.count
        let p1 = routeWaypoints[currentWaypointIndex]
        let p2 = routeWaypoints[nextIndex]
        
        let dLat = p2.latitude - p1.latitude
        let dLng = p2.longitude - p1.longitude
        let angleRad = atan2(dLng, dLat)
        let deg = (angleRad * 180.0 / .pi + 360.0).truncatingRemainder(dividingBy: 360.0)
        
        currentWaypointIndex = nextIndex
        currentCoordinate = p2
        currentBearing = deg
        currentSpeedKmh = Int.random(in: 20...32)
        
        broadcastToSupabase()
    }
    
    private func broadcastToSupabase() {
        guard let client = supabaseClient else { return }
        Task { [weak self] in
            guard let self = self else { return }
            let payload: [String: AnyJSON] = [
                "partner_id": .string(self.partnerId),
                "latitude": .double(self.currentCoordinate.latitude),
                "longitude": .double(self.currentCoordinate.longitude),
                "bearing": .double(self.currentBearing),
                "speed_kmh": .integer(self.currentSpeedKmh),
                "is_online": .bool(self.isStreaming)
            ]
            do {
                try await client.from("driver_locations")
                    .upsert(payload)
                    .execute()
            } catch {
                // Local simulation continues uninterrupted
            }
        }
    }
    
    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let loc = locations.last else { return }
        currentCoordinate = loc.coordinate
        if loc.course >= 0 {
            currentBearing = loc.course
        }
        if loc.speed >= 0 {
            currentSpeedKmh = Int(loc.speed * 3.6)
        }
        broadcastToSupabase()
    }
}
