//
//  LocationService.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Reusable Location Engineering & CoreLocation Geocoding Service
//

import Foundation
import CoreLocation
import Combine

public final class LocationService: NSObject, ObservableObject, CLLocationManagerDelegate {
    public static let shared = LocationService()
    
    // Kathmandu Valley Center default
    public static let defaultKathmanduCoordinate = CLLocationCoordinate2D(latitude: 27.7172, longitude: 85.3240)
    public static let defaultKathmanduAddress = "Thamel Marg, Kathmandu"
    
    // Popular hotspots in Kathmandu valley
    public static let popularLocations: [LocationPoint] = [
        LocationPoint(label: "Thamel Tourist Hub", address: "Thamel Marg, Kathmandu", latitude: 27.7154, longitude: 85.3123),
        LocationPoint(label: "Patan Durbar Square", address: "Mangal Bazar, Lalitpur", latitude: 27.6727, longitude: 85.3253),
        LocationPoint(label: "Jhamsikhel Restaurant Hub", address: "Jhamsikhel Road, Lalitpur", latitude: 27.6841, longitude: 85.3121),
        LocationPoint(label: "Pulchowk Engineering Campus", address: "Pulchowk, Lalitpur", latitude: 27.6800, longitude: 85.3188),
        LocationPoint(label: "Tribhuvan International Airport (TIA)", address: "Ring Road, Kathmandu", latitude: 27.6966, longitude: 85.3591),
        LocationPoint(label: "New Road Financial District", address: "Khichapokhari, Kathmandu", latitude: 27.7032, longitude: 85.3121),
        LocationPoint(label: "Boudhanath Stupa", address: "Boudha, Kathmandu", latitude: 27.7215, longitude: 85.3620),
        LocationPoint(label: "Baneshwor Chowk", address: "New Baneshwor, Kathmandu", latitude: 27.6915, longitude: 85.3420),
        LocationPoint(label: "Maharajgunj Teaching Hospital", address: "Maharajgunj, Kathmandu", latitude: 27.7360, longitude: 85.3308),
        LocationPoint(label: "Bhaktapur Durbar Square", address: "Durbar Square, Bhaktapur", latitude: 27.6710, longitude: 85.4298)
    ]
    
    private let locationManager = CLLocationManager()
    @Published public var currentLocation: CLLocation?
    @Published public var authorizationStatus: CLAuthorizationStatus = .notDetermined
    
    public override init() {
        super.init()
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        self.locationManager.distanceFilter = 10.0
    }
    
    public func requestPermission() {
        locationManager.requestWhenInUseAuthorization()
    }
    
    public func startUpdatingLocation() {
        locationManager.startUpdatingLocation()
    }
    
    public func stopUpdatingLocation() {
        locationManager.stopUpdatingLocation()
    }
    
    public func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        self.authorizationStatus = manager.authorizationStatus
        if manager.authorizationStatus == .authorizedWhenInUse || manager.authorizationStatus == .authorizedAlways {
            startUpdatingLocation()
        }
    }
    
    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let latest = locations.last else { return }
        self.currentLocation = latest
    }
    
    /**
     * Resolves human-readable street address from coordinate
     */
    public func reverseGeocode(coordinate: CLLocationCoordinate2D) async -> String {
        // Fast match against known popular hotspots
        for spot in Self.popularLocations {
            let loc1 = CLLocation(latitude: spot.latitude, longitude: spot.longitude)
            let loc2 = CLLocation(latitude: coordinate.latitude, longitude: coordinate.longitude)
            if loc1.distance(from: loc2) < 250 {
                return spot.address
            }
        }
        
        let geocoder = CLGeocoder()
        let location = CLLocation(latitude: coordinate.latitude, longitude: coordinate.longitude)
        
        do {
            let placemarks = try await geocoder.reverseGeocodeLocation(location)
            if let p = placemarks.first {
                let name = p.name ?? ""
                let street = p.thoroughfare ?? ""
                let subLocality = p.subLocality ?? p.locality ?? "Kathmandu"
                let components = [name, street, subLocality].filter { !$0.isEmpty }
                let unique = Array(NSOrderedSet(array: components)) as? [String] ?? components
                if !unique.isEmpty {
                    return unique.joined(separator: ", ")
                }
            }
        } catch {}
        
        return String(format: "Near %.4f, %.4f, Kathmandu", coordinate.latitude, coordinate.longitude)
    }
    
    /**
     * Searches places by name
     */
    public func searchPlaces(query: String) -> [LocationPoint] {
        if query.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            return Array(Self.popularLocations.prefix(6))
        }
        let q = query.lowercased()
        let matches = Self.popularLocations.filter {
            $0.label.lowercased().contains(q) || $0.address.lowercased().contains(q)
        }
        if !matches.isEmpty { return matches }
        
        return [
            LocationPoint(
                label: query,
                address: "\(query), Kathmandu Valley, Nepal",
                latitude: Self.defaultKathmanduCoordinate.latitude + Double(query.hashValue % 100) * 0.0003,
                longitude: Self.defaultKathmanduCoordinate.longitude + Double(query.hashValue % 100) * 0.0003,
                isCurrentLocation: false
            )
        ]
    }
}
