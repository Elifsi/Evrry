//
//  ActivityViewModel.swift
//  EvrryConsumer
//
//  Elifsi Technologies Private Limited
//  Orders, Bookings, Deliveries & Rides Activity ViewModel
//

import Foundation
import Combine

public enum ActivityFilter: String, CaseIterable, Identifiable {
    case all = "All"
    case active = "Active"
    case completed = "Completed"
    
    public var id: String { rawValue }
}

public struct OrderActivityItem: Identifiable, Equatable {
    public let id: String
    public let title: String
    public let vendorName: String
    public let category: String
    public let amountRs: Int
    public var status: String
    public let paymentMethod: String
    public let dateFormatted: String
    public let etaMinutes: Int?
    public let riderName: String?
    public let riderPhone: String?
    public let rideOtp: String?
    public let vehicleNumber: String?
    public let lineItems: [String]
    public let deliveryAddress: String?
    public let specialInstructions: String?
    public let subtotalRs: Int?
    public let deliveryFeeRs: Int?
    public let platformFeeRs: Int?
    
    public init(
        id: String,
        title: String,
        vendorName: String,
        category: String,
        amountRs: Int,
        status: String,
        paymentMethod: String,
        dateFormatted: String,
        etaMinutes: Int? = nil,
        riderName: String? = nil,
        riderPhone: String? = nil,
        rideOtp: String? = nil,
        vehicleNumber: String? = nil,
        lineItems: [String] = [],
        deliveryAddress: String? = nil,
        specialInstructions: String? = nil,
        subtotalRs: Int? = nil,
        deliveryFeeRs: Int? = nil,
        platformFeeRs: Int? = nil
    ) {
        self.id = id
        self.title = title
        self.vendorName = vendorName
        self.category = category
        self.amountRs = amountRs
        self.status = status
        self.paymentMethod = paymentMethod
        self.dateFormatted = dateFormatted
        self.etaMinutes = etaMinutes
        self.riderName = riderName
        self.riderPhone = riderPhone
        self.rideOtp = rideOtp
        self.vehicleNumber = vehicleNumber
        self.lineItems = lineItems
        self.deliveryAddress = deliveryAddress
        self.specialInstructions = specialInstructions
        self.subtotalRs = subtotalRs
        self.deliveryFeeRs = deliveryFeeRs
        self.platformFeeRs = platformFeeRs
    }
}

@MainActor
public final class ActivityViewModel: ObservableObject {
    @Published public var selectedFilter: ActivityFilter = .all
    @Published public var items: [OrderActivityItem] = [
        OrderActivityItem(
            id: "ord-001",
            title: "Steamed Chicken Momo (2x) + Buff Chilly",
            vendorName: "Baneshwor Momo Hut",
            category: "Food",
            amountRs: 480,
            status: "kitchen",
            paymentMethod: "eSewa",
            dateFormatted: "Today, 1:15 PM",
            etaMinutes: 18,
            riderName: "Bikash Thapa",
            riderPhone: "9841000001",
            vehicleNumber: "Ba 88 Pa 4321",
            lineItems: ["2x Steamed Chicken Momo (Rs 360)", "1x Buff Chilly Momo (Rs 120)"],
            deliveryAddress: "Ward 3, Jhamsikhel, Lalitpur",
            subtotalRs: 420,
            deliveryFeeRs: 50,
            platformFeeRs: 10
        ),
        OrderActivityItem(
            id: "ride-002",
            title: "Moto Ride to Thamel Chowk",
            vendorName: "evrry Mobility",
            category: "Ride",
            amountRs: 165,
            status: "delivering",
            paymentMethod: "Cash on Delivery",
            dateFormatted: "Today, 12:40 PM",
            etaMinutes: 6,
            riderName: "Kiran Shrestha",
            riderPhone: "9841000002",
            rideOtp: "1234",
            vehicleNumber: "Ba 92 Pa 8765"
        ),
        OrderActivityItem(
            id: "ord-003",
            title: "Fresh Cow Milk (2L) + Organic Eggs (12 pcs)",
            vendorName: "Kalimati Fresh Mart",
            category: "Mart",
            amountRs: 390,
            status: "delivered",
            paymentMethod: "Fonepay QR",
            dateFormatted: "Yesterday, 6:30 PM",
            lineItems: ["2x Fresh Cow Milk 1L (Rs 200)", "1x Organic Eggs 12pcs (Rs 190)"],
            deliveryAddress: "Ward 3, Jhamsikhel, Lalitpur"
        )
    ]
    
    public init() {}
    
    public var filteredItems: [OrderActivityItem] {
        switch selectedFilter {
        case .all:
            return items
        case .active:
            return items.filter { ["placed", "confirmed", "kitchen", "delivering", "draft"].contains($0.status) }
        case .completed:
            return items.filter { ["delivered", "completed", "cancelled"].contains($0.status) }
        }
    }
    
    public func getOrder(id: String) -> OrderActivityItem? {
        items.first(where: { $0.id == id })
    }
    
    public func addOrder(
        vendorName: String,
        category: String,
        title: String,
        amountRs: Int,
        paymentMethod: String,
        lineItems: [String],
        deliveryAddress: String,
        specialInstructions: String,
        subtotalRs: Int,
        deliveryFeeRs: Int,
        platformFeeRs: Int,
        etaMinutes: Int = 30,
        rideOtp: String? = nil
    ) -> String {
        let suffix = String(Int(Date().timeIntervalSince1970 * 1000)).suffix(6)
        let orderId = "ord-\(suffix)"
        let newOrder = OrderActivityItem(
            id: orderId,
            title: title,
            vendorName: vendorName,
            category: category,
            amountRs: amountRs,
            status: "placed",
            paymentMethod: paymentMethod,
            dateFormatted: "Just now",
            etaMinutes: etaMinutes,
            riderName: "Sujan Magar",
            riderPhone: "9841234567",
            rideOtp: rideOtp,
            vehicleNumber: "Ba 02 Pa 9988",
            lineItems: lineItems,
            deliveryAddress: deliveryAddress,
            specialInstructions: specialInstructions.isEmpty ? nil : specialInstructions,
            subtotalRs: subtotalRs,
            deliveryFeeRs: deliveryFeeRs,
            platformFeeRs: platformFeeRs
        )
        self.items.insert(newOrder, at: 0)
        return orderId
    }
    
    public func cancelOrder(id: String) {
        if let idx = items.firstIndex(where: { $0.id == id }) {
            items[idx].status = "cancelled"
        }
    }
}
