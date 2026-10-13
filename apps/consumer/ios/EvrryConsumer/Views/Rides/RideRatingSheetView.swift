//
//  RideRatingSheetView.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Ride Rating & Driver Review Sheet (SwiftUI)
//

import SwiftUI

public struct RideRatingSheetView: View {
    public let driver: AssignedDriver
    public let fareNpr: Double
    public let onSubmit: (Int, String) -> Void
    public let onDismiss: () -> Void
    
    @State private var rating: Int = 5
    @State private var comment: String = ""
    @State private var selectedTags: Set<String> = []
    
    private let quickTags = [
        "Polite & Friendly",
        "Safe & Smooth Driving",
        "Clean Vehicle",
        "Optimal Route",
        "On Time"
    ]
    
    public init(
        driver: AssignedDriver,
        fareNpr: Double,
        onSubmit: @escaping (Int, String) -> Void,
        onDismiss: @escaping () -> Void
    ) {
        self.driver = driver
        self.fareNpr = fareNpr
        self.onSubmit = onSubmit
        self.onDismiss = onDismiss
    }
    
    public var body: some View {
        NavigationView {
            VStack(spacing: 18) {
                // DRIVER BADGE
                ZStack {
                    Circle()
                        .fill(Color.evrryPrimaryBlue.opacity(0.1))
                        .frame(width: 64, height: 64)
                    Text("👨‍✈️")
                        .font(.system(size: 32))
                }
                
                VStack(spacing: 4) {
                    Text("How was your ride with \(driver.name)?")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.evrryDarkCharcoal)
                    Text("Trip Total: Rs \(Int(fareNpr)) · \(driver.vehicleModel)")
                        .font(.system(size: 12))
                        .foregroundColor(.evrryMutedText)
                }
                
                // 5 STAR SELECTOR
                HStack(spacing: 10) {
                    ForEach(1...5, id: \.self) { star in
                        Button(action: { rating = star }) {
                            Image(systemName: star <= rating ? "star.fill" : "star")
                                .font(.system(size: 32))
                                .foregroundColor(star <= rating ? Color(red: 0.96, green: 0.62, blue: 0.04) : Color.gray.opacity(0.4))
                        }
                    }
                }
                
                Text(sentimentText)
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundColor(rating >= 4 ? Color(red: 0.85, green: 0.47, blue: 0.02) : .evrryMutedText)
                
                // QUICK FEEDBACK TAGS
                VStack(alignment: .leading, spacing: 8) {
                    Text("WHAT WENT WELL?")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                    
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(quickTags, id: \.self) { tag in
                                let isSelected = selectedTags.contains(tag)
                                Button(action: {
                                    if isSelected { selectedTags.remove(tag) } else { selectedTags.insert(tag) }
                                }) {
                                    Text(tag)
                                        .font(.system(size: 12, weight: .medium))
                                        .foregroundColor(isSelected ? .evrryPrimaryBlue : .evrryDarkCharcoal)
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 8)
                                        .background(isSelected ? Color.blue.opacity(0.08) : Color(red: 0.97, green: 0.98, blue: 0.99))
                                        .clipShape(Capsule())
                                        .overlay(Capsule().stroke(isSelected ? Color.evrryPrimaryBlue : Color.evrrySubtleBorder, lineWidth: 1))
                                }
                            }
                        }
                    }
                }
                
                // COMMENT FIELD
                VStack(alignment: .leading, spacing: 6) {
                    Text("ADDITIONAL COMMENTS")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.evrryMutedText)
                    
                    TextField("Write optional feedback for the driver…", text: $comment)
                        .font(.system(size: 13))
                        .padding(12)
                        .background(Color(red: 0.97, green: 0.98, blue: 0.99))
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.evrrySubtleBorder, lineWidth: 1))
                }
                
                Spacer()
                
                // SUBMIT BUTTON
                Button(action: {
                    let fullComment = selectedTags.isEmpty ? comment : "\(selectedTags.joined(separator: ", ")). \(comment)".trimmingCharacters(in: .whitespaces)
                    onSubmit(rating, fullComment)
                }) {
                    Text("Submit Review")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.evrryPrimaryBlue)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                }
            }
            .padding(20)
            .navigationTitle("Rate Driver")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Skip") { onDismiss() }
                }
            }
        }
    }
    
    private var sentimentText: String {
        switch rating {
        case 5: return "Exceptional Experience! ⭐⭐⭐⭐⭐"
        case 4: return "Very Good ⭐⭐⭐⭐"
        case 3: return "Average ⭐⭐⭐"
        case 2: return "Below Expectations ⭐⭐"
        default: return "Poor Experience ⭐"
        }
    }
}
