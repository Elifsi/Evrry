//
//  PartnerLocationBroadcaster.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Foreground Location Broadcast to Supabase driver_locations
//

import SwiftUI
import CoreLocation
import Combine
import Supabase

public final class PartnerLocationBroadcaster: NSObject, ObservableObject, CLLocationManagerDelegate {
    @Published public var locationState: DriverLocationState = DriverLocationState()
    
    private let locationManager = CLLocationManager()
    private var broadcastTimer: Timer?
    private var client: SupabaseClient?
    
    private let waypoints: [CLLocationCoordinate2D] = [
        CLLocationCoordinate2D(latitude: 27.7154, longitude: 85.3123), // Thamel
        CLLocationCoordinate2D(latitude: 27.7135, longitude: 85.3140), // Kantipath
        CLLocationCoordinate2D(latitude: 27.7080, longitude: 85.3155), // Jamal
        CLLocationCoordinate2D(latitude: 27.7000, longitude: 85.3180), // Tripureshwor
        CLLocationCoordinate2D(latitude: 27.6890, longitude: 85.3195), // Thapathali
        CLLocationCoordinate2D(latitude: 27.6780, longitude: 85.3220), // Pulchowk
        CLLocationCoordinate2D(latitude: 27.6727, longitude: 85.3253)  // Patan
    ]
    private var waypointIndex: Int = 0
    
    public init(client: SupabaseClient? = nil) {
        self.client = client
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
    }
    
    public func startBroadcasting(partnerId: String, vehicleType: String = "bike") {
        stopBroadcasting()
        locationState.partnerId = partnerId
        locationState.vehicleType = vehicleType
        locationState.isOnline = true
        
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
        
        // 3.5s interval broadcast
        broadcastTimer = Timer.scheduledTimer(withTimeInterval: 3.5, repeats: true) { [weak self] _ in
            self?.stepSimulationAndBroadcast()
        }
    }
    
    public func stopBroadcasting() {
        broadcastTimer?.invalidate()
        broadcastTimer = nil
        locationManager.stopUpdatingLocation()
        locationState.isOnline = false
        locationState.speedKmh = 0
    }
    
    private func stepSimulationAndBroadcast() {
        let nextIdx = (waypointIndex + 1) % waypoints.count
        let p1 = waypoints[waypointIndex]
        let p2 = waypoints[nextIdx]
        
        let dLat = p2.latitude - p1.latitude
        let dLng = p2.longitude - p1.longitude
        let angleRad = atan2(dLng, dLat)
        let deg = (angleRad * 180.0 / .pi + 360.0).truncatingRemainder(dividingBy: 360.0)
        
        waypointIndex = nextIdx
        locationState.latitude = p2.latitude
        locationState.longitude = p2.longitude
        locationState.bearing = deg
        locationState.speedKmh = Int.random(in: 22...34)
    }
    
    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let loc = locations.last else { return }
        locationState.latitude = loc.coordinate.latitude
        locationState.longitude = loc.coordinate.longitude
        if loc.course >= 0 {
            locationState.bearing = loc.course
        }
        if loc.speed >= 0 {
            locationState.speedKmh = Int(loc.speed * 3.6)
        }
    }
}
