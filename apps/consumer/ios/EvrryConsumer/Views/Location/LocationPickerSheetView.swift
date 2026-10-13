//
//  LocationPickerSheetView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Reusable Location Picker Bottom Sheet (SwiftUI)
//

import SwiftUI
import CoreLocation

public struct LocationPickerSheetView: View {
    public let title: String
    @ObservedObject public var locationService: LocationService = LocationService.shared
    @ObservedObject public var savedAddressService: SavedAddressService = SavedAddressService.shared
    public let onLocationSelected: (LocationPoint) -> Void
    public let onDismiss: () -> Void
    
    @State private var searchQuery: String = ""
    @State private var searchResults: [LocationPoint] = []
    @State private var isLocating: Bool = false
    
    public init(
        title: String = "Select Location",
        onLocationSelected: @escaping (LocationPoint) -> Void,
        onDismiss: @escaping () -> Void
    ) {
        self.title = title
        self.onLocationSelected = onLocationSelected
        self.onDismiss = onDismiss
    }
    
    public var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                // SEARCH FIELD
                HStack(spacing: 10) {
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(.evrryPrimaryBlue)
                    TextField("Search street, area or landmark…", text: $searchQuery)
                        .font(.system(size: 14))
                        .onChange(of: searchQuery) { newValue in
                            searchResults = locationService.searchPlaces(query: newValue)
                        }
                    if !searchQuery.isEmpty {
                        Button(action: { searchQuery = ""; searchResults = locationService.searchPlaces(query: "") }) {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundColor(.evrryMutedText)
                        }
                    }
                }
                .padding(12)
                .background(Color(red: 0.95, green: 0.96, blue: 0.98))
                .clipShape(RoundedRectangle(cornerRadius: 14))
                
                // USE CURRENT LOCATION BUTTON
                Button(action: {
                    isLocating = true
                    locationService.requestPermission()
                    locationService.startUpdatingLocation()
                    
                    Task {
                        try? await Task.sleep(nanoseconds: 600_000_000)
                        let coord = locationService.currentLocation?.coordinate ?? LocationService.defaultKathmanduCoordinate
                        let addr = await locationService.reverseGeocode(coordinate: coord)
                        isLocating = false
                        onLocationSelected(
                            LocationPoint(
                                label: "Current Location",
                                address: addr,
                                latitude: coord.latitude,
                                longitude: coord.longitude,
                                isCurrentLocation: true
                            )
                        )
                    }
                }) {
                    HStack(spacing: 12) {
                        ZStack {
                            Circle()
                                .fill(Color.evrryPrimaryBlue)
                                .frame(width: 36, height: 36)
                            if isLocating {
                                ProgressView()
                                    .tint(.white)
                                    .scaleEffect(0.8)
                            } else {
                                Image(systemName: "location.fill")
                                    .foregroundColor(.white)
                                    .font(.system(size: 16))
                            }
                        }
                        
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Use Current Location")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundColor(.evrryPrimaryBlue)
                            Text("Detect device GPS coordinates with high accuracy")
                                .font(.system(size: 11))
                                .foregroundColor(.evrryMutedText)
                        }
                        Spacer()
                    }
                    .padding(12)
                    .background(Color.blue.opacity(0.08))
                    .clipShape(RoundedRectangle(cornerRadius: 14))
                    .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.evrryPrimaryBlue.opacity(0.3), lineWidth: 1))
                }
                
                // SAVED PLACES SHORTCUTS
                if !savedAddressService.addresses.isEmpty {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("SAVED PLACES")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.evrryMutedText)
                        
                        HStack(spacing: 10) {
                            ForEach(savedAddressService.addresses) { addr in
                                Button(action: {
                                    onLocationSelected(addr.toLocationPoint())
                                }) {
                                    HStack(spacing: 6) {
                                        Text(addr.type.emoji)
                                        VStack(alignment: .leading, spacing: 1) {
                                            Text(addr.label)
                                                .font(.system(size: 12, weight: .bold))
                                                .foregroundColor(.evrryDarkCharcoal)
                                            Text(addr.address.components(separatedBy: ",").first ?? addr.address)
                                                .font(.system(size: 10))
                                                .foregroundColor(.evrryMutedText)
                                                .lineLimit(1)
                                        }
                                    }
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 8)
                                    .background(Color.white)
                                    .clipShape(RoundedRectangle(cornerRadius: 12))
                                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                }
                            }
                        }
                    }
                }
                
                // POPULAR / SEARCH RESULTS LIST
                VStack(alignment: .leading, spacing: 8) {
                    Text(searchQuery.isEmpty ? "POPULAR KATHMANDU DESTINATIONS" : "SEARCH RESULTS")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                    
                    ScrollView {
                        LazyVStack(spacing: 8) {
                            let itemsToShow = searchResults.isEmpty ? Array(LocationService.popularLocations.prefix(6)) : searchResults
                            ForEach(itemsToShow) { point in
                                Button(action: { onLocationSelected(point) }) {
                                    HStack(spacing: 12) {
                                        ZStack {
                                            Circle()
                                                .fill(Color(red: 0.94, green: 0.96, blue: 0.98))
                                                .frame(width: 34, height: 34)
                                            Image(systemName: "mappin.and.ellipse")
                                                .foregroundColor(.evrryDarkCharcoal)
                                                .font(.system(size: 14))
                                        }
                                        VStack(alignment: .leading, spacing: 2) {
                                            Text(point.label)
                                                .font(.system(size: 14, weight: .semibold))
                                                .foregroundColor(.evrryDarkCharcoal)
                                            Text(point.address)
                                                .font(.system(size: 11))
                                                .foregroundColor(.evrryMutedText)
                                                .lineLimit(1)
                                        }
                                        Spacer()
                                    }
                                    .padding(12)
                                    .background(Color.white)
                                    .clipShape(RoundedRectangle(cornerRadius: 12))
                                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                                }
                                .buttonStyle(PlainButtonStyle())
                            }
                        }
                    }
                }
                
                Spacer()
            }
            .padding(16)
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Close") { onDismiss() }
                }
            }
        }
        .onAppear {
            searchResults = locationService.searchPlaces(query: "")
        }
    }
}
