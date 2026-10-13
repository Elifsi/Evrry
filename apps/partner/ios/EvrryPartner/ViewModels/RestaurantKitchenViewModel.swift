//
//  RestaurantKitchenViewModel.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Restaurant Kitchen KDS State Machine & ViewModel
//

import Foundation
import SwiftUI
import Combine

@MainActor
public final class RestaurantKitchenViewModel: ObservableObject {
    @Published public var activeTab: KitchenTab = .incoming
    @Published public var orders: [RestaurantOrder] = []
    @Published public var catalogItems: [RestaurantCatalogItem] = []
    @Published public var currentStore: RestaurantStore = RestaurantStore()
    @Published public var isStoreOpen: Bool = true
    
    // Modals & Sheets
    @Published public var selectedOrder: RestaurantOrder? = nil
    @Published public var showPrepTimeModal: Bool = false
    @Published public var showRejectReasonSheet: Bool = false
    @Published public var showPrintReceiptSheet: Bool = false
    @Published public var printType: PrintDocType = .kot
    @Published public var showMenu86Sheet: Bool = false
    @Published public var showChatSheet: Bool = false
    @Published public var chatRecipient: String = "consumer" // "consumer" or "courier"
    @Published public var chatMessages: [RestaurantChatMessage] = []
    @Published public var isAlarmActive: Bool = false
    
    // Live Clock Ticker
    @Published public var now: Date = Date()
    private var tickerTimer: AnyCancellable?
    
    public init() {
        loadMockData()
        startCountdownTicker()
        checkIncomingAlarms()
    }
    
    private func startCountdownTicker() {
        tickerTimer = Timer.publish(every: 1.0, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] currentDate in
                self?.now = currentDate
            }
    }
    
    public var incomingOrders: [RestaurantOrder] {
        orders.filter { $0.status == .acknowledged }
    }
    
    public var preparingOrders: [RestaurantOrder] {
        orders.filter { $0.status == .preparing }
    }
    
    public var readyOrders: [RestaurantOrder] {
        orders.filter { $0.status == .readyForPickup }
    }
    
    public var completedOrders: [RestaurantOrder] {
        orders.filter { $0.status == .dispatched || $0.status == .delivered }
    }
    
    public var completedCountToday: Int {
        completedOrders.count
    }
    
    public var grossSalesNprToday: Double {
        completedOrders.reduce(0.0) { $0 + $1.totalNpr }
    }
    
    public func checkIncomingAlarms() {
        if incomingOrders.contains(where: { $0.status == .acknowledged }) {
            isAlarmActive = true
            KitchenAudioAlertService.shared.startIncomingOrderChime()
        } else {
            isAlarmActive = false
            KitchenAudioAlertService.shared.stopIncomingOrderChime()
        }
    }
    
    public func silenceAlarm() {
        isAlarmActive = false
        KitchenAudioAlertService.shared.stopIncomingOrderChime()
    }
    
    // MARK: - KDS State Transitions
    
    public func promptAccept(order: RestaurantOrder) {
        selectedOrder = order
        showPrepTimeModal = true
    }
    
    public func confirmAccept(order: RestaurantOrder, prepMinutes: Int, autoPrint: Bool) {
        showPrepTimeModal = false
        silenceAlarm()
        
        let currentTime = Int64(Date().timeIntervalSince1970)
        if let idx = orders.firstIndex(where: { $0.id == order.id }) {
            orders[idx].status = .preparing
            orders[idx].prepTimeMinutes = prepMinutes
            orders[idx].acceptedAtEpochSeconds = currentTime
            
            if autoPrint {
                preparePrint(order: orders[idx], type: .kot)
            }
        }
        
        activeTab = .preparing
        checkIncomingAlarms()
    }
    
    public func promptReject(order: RestaurantOrder) {
        selectedOrder = order
        showRejectReasonSheet = true
    }
    
    public func confirmReject(order: RestaurantOrder, reason: String) {
        showRejectReasonSheet = false
        silenceAlarm()
        
        if let idx = orders.firstIndex(where: { $0.id == order.id }) {
            orders[idx].status = .rejected
            orders[idx].customerNote = (orders[idx].customerNote ?? "") + " [Rejected: \(reason)]"
        }
        
        checkIncomingAlarms()
    }
    
    public func markReadyForPickup(order: RestaurantOrder) {
        if let idx = orders.firstIndex(where: { $0.id == order.id }) {
            orders[idx].status = .readyForPickup
            // Automatically assign demo courier if none assigned
            if orders[idx].riderName == nil {
                orders[idx].riderName = "Santosh Thapa"
                orders[idx].riderPhone = "+977 9813245678"
                orders[idx].riderVehicleType = "Honda Dio (BA 98 PA 3021)"
                orders[idx].riderDistanceKm = 0.8
            }
        }
        activeTab = .readyForPickup
    }
    
    public func markDispatched(order: RestaurantOrder) {
        if let idx = orders.firstIndex(where: { $0.id == order.id }) {
            orders[idx].status = .dispatched
        }
    }
    
    public func markDelivered(order: RestaurantOrder) {
        if let idx = orders.firstIndex(where: { $0.id == order.id }) {
            orders[idx].status = .delivered
        }
    }
    
    // MARK: - Instant Menu 86'ing (Out-of-Stock Toggle)
    
    public func toggle86Item(id: String) {
        if let idx = catalogItems.firstIndex(where: { $0.id == id }) {
            catalogItems[idx].isAvailable.toggle()
        }
    }
    
    public func toggleStoreOpen() {
        isStoreOpen.toggle()
        currentStore.isActive = isStoreOpen
    }
    
    // MARK: - Printing
    
    public func preparePrint(order: RestaurantOrder, type: PrintDocType) {
        selectedOrder = order
        printType = type
        showPrintReceiptSheet = true
    }
    
    // MARK: - Chat
    
    public func openChat(order: RestaurantOrder, recipient: String) {
        selectedOrder = order
        chatRecipient = recipient
        // Load initial contextual messages
        chatMessages = [
            RestaurantChatMessage(
                orderId: order.id,
                senderRole: recipient == "consumer" ? "consumer" : "courier",
                senderName: recipient == "consumer" ? order.consumerName : (order.riderName ?? "Delivery Rider"),
                message: recipient == "consumer" ? "Namaste! Please pack extra timur achaar if possible." : "Namaste dai, I have reached the restaurant parking area.",
                timestamp: Date().addingTimeInterval(-180)
            )
        ]
        showChatSheet = true
    }
    
    public func sendChatMessage(text: String) {
        guard let order = selectedOrder, !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return }
        let newMsg = RestaurantChatMessage(
            orderId: order.id,
            senderRole: "restaurant",
            senderName: currentStore.name,
            message: text,
            timestamp: Date()
        )
        chatMessages.append(newMsg)
    }
    
    // MARK: - Seed Data
    
    private func loadMockData() {
        let currentTime = Int64(Date().timeIntervalSince1970)
        
        orders = [
            // 1 Incoming order (will trigger high chime)
            RestaurantOrder(
                id: "ord_in_101",
                orderNo: "EV-9942",
                consumerId: "c_101",
                consumerName: "Aayush Shrestha",
                consumerPhone: "+977 9841987654",
                riderPartnerId: nil,
                riderName: nil,
                riderPhone: nil,
                riderDistanceKm: nil,
                riderVehicleType: nil,
                status: .acknowledged,
                totalPaisa: 98000,
                customerNote: "Make it extra spicy and provide cutlery please",
                placedAtEpochSeconds: currentTime - 45,
                prepTimeMinutes: 20,
                acceptedAtEpochSeconds: nil,
                items: [
                    RestaurantOrderItem(name: "Buff Steamed Momo (10 pcs)", quantity: 2, unitPricePaisa: 28000, lineTotalPaisa: 56000, customerNotes: "Extra spicy jhol"),
                    RestaurantOrderItem(name: "Chicken Chilly", quantity: 1, unitPricePaisa: 42000, lineTotalPaisa: 42000, customerNotes: nil)
                ],
                paymentMethod: "cod"
            ),
            
            // 2 Active Cooking Orders in Preparing
            RestaurantOrder(
                id: "ord_prep_201",
                orderNo: "EV-9938",
                consumerId: "c_102",
                consumerName: "Pooja Gurung",
                consumerPhone: "+977 9818123456",
                riderPartnerId: "rdr_01",
                riderName: "Santosh Thapa",
                riderPhone: "+977 9813245678",
                riderDistanceKm: 1.2,
                riderVehicleType: "Honda Dio",
                status: .preparing,
                totalPaisa: 125000,
                customerNote: "Less salt in dal please",
                placedAtEpochSeconds: currentTime - 600,
                prepTimeMinutes: 25,
                acceptedAtEpochSeconds: currentTime - 540, // 9 mins elapsed
                items: [
                    RestaurantOrderItem(name: "Thakali Mutton Thali Set", quantity: 1, unitPricePaisa: 85000, lineTotalPaisa: 85000, customerNotes: "Extra ghee"),
                    RestaurantOrderItem(name: "Sweet Curd (Juju Dhau)", quantity: 2, unitPricePaisa: 20000, lineTotalPaisa: 40000, customerNotes: nil)
                ],
                paymentMethod: "prepaid"
            ),
            RestaurantOrder(
                id: "ord_prep_202",
                orderNo: "EV-9940",
                consumerId: "c_103",
                consumerName: "Roshan Karki",
                consumerPhone: "+977 9803456789",
                riderPartnerId: "rdr_02",
                riderName: "Bikram Shrestha",
                riderPhone: "+977 9849012345",
                riderDistanceKm: 2.5,
                riderVehicleType: "Hero Splendor",
                status: .preparing,
                totalPaisa: 75000,
                customerNote: nil,
                placedAtEpochSeconds: currentTime - 200,
                prepTimeMinutes: 15,
                acceptedAtEpochSeconds: currentTime - 180, // 3 mins elapsed
                items: [
                    RestaurantOrderItem(name: "Fried Buff C-Momo", quantity: 1, unitPricePaisa: 38000, lineTotalPaisa: 38000, customerNotes: nil),
                    RestaurantOrderItem(name: "Fresh Lemonade Soda", quantity: 2, unitPricePaisa: 18500, lineTotalPaisa: 37000, customerNotes: "No ice")
                ],
                paymentMethod: "prepaid"
            ),
            
            // 1 Ready for Pickup order
            RestaurantOrder(
                id: "ord_ready_301",
                orderNo: "EV-9931",
                consumerId: "c_104",
                consumerName: "Dipen Maharjan",
                consumerPhone: "+977 9851098765",
                riderPartnerId: "rdr_03",
                riderName: "Raju Lama",
                riderPhone: "+977 9860123456",
                riderDistanceKm: 0.3,
                riderVehicleType: "TVS NTorq (BA 45 PA 7890)",
                status: .readyForPickup,
                totalPaisa: 110000,
                customerNote: "Bag with seal tape",
                placedAtEpochSeconds: currentTime - 1500,
                prepTimeMinutes: 20,
                acceptedAtEpochSeconds: currentTime - 1450,
                items: [
                    RestaurantOrderItem(name: "Jhol Momo Platter", quantity: 2, unitPricePaisa: 35000, lineTotalPaisa: 70000, customerNotes: "Extra sesame soup"),
                    RestaurantOrderItem(name: "Aloo Sadeko", quantity: 1, unitPricePaisa: 40000, lineTotalPaisa: 40000, customerNotes: nil)
                ],
                paymentMethod: "cod"
            ),
            
            // 3 Completed orders for daily sales tally
            RestaurantOrder(
                id: "ord_comp_401",
                orderNo: "EV-9920",
                consumerId: "c_105",
                consumerName: "Sita Sharma",
                consumerPhone: "+977 9841000001",
                riderPartnerId: "rdr_04",
                riderName: "Nabin KC",
                riderPhone: "+977 9841222333",
                riderDistanceKm: 0.0,
                riderVehicleType: "Bike",
                status: .delivered,
                totalPaisa: 145000,
                customerNote: nil,
                placedAtEpochSeconds: currentTime - 7200,
                prepTimeMinutes: 20,
                acceptedAtEpochSeconds: currentTime - 7100,
                items: [
                    RestaurantOrderItem(name: "Thakali Chicken Thali Set", quantity: 2, unitPricePaisa: 72500, lineTotalPaisa: 145000, customerNotes: nil)
                ],
                paymentMethod: "prepaid"
            ),
            RestaurantOrder(
                id: "ord_comp_402",
                orderNo: "EV-9915",
                consumerId: "c_106",
                consumerName: "Kishor Rai",
                consumerPhone: "+977 9841000002",
                riderPartnerId: "rdr_05",
                riderName: "Manoj Pariyar",
                riderPhone: "+977 9841333444",
                riderDistanceKm: 0.0,
                riderVehicleType: "Scooter",
                status: .delivered,
                totalPaisa: 89000,
                customerNote: nil,
                placedAtEpochSeconds: currentTime - 10800,
                prepTimeMinutes: 15,
                acceptedAtEpochSeconds: currentTime - 10700,
                items: [
                    RestaurantOrderItem(name: "Buff Sukuti Sadeko", quantity: 1, unitPricePaisa: 55000, lineTotalPaisa: 55000, customerNotes: nil),
                    RestaurantOrderItem(name: "Wai Wai Sadeko", quantity: 2, unitPricePaisa: 17000, lineTotalPaisa: 34000, customerNotes: nil)
                ],
                paymentMethod: "cod"
            )
        ]
        
        catalogItems = [
            RestaurantCatalogItem(id: "item_01", name: "Buff Steamed Momo (10 pcs)", category: "Momo", pricePaisa: 28000, isAvailable: true, description: "Traditional spiced buffalo meat momo with tomato chutney"),
            RestaurantCatalogItem(id: "item_02", name: "Chicken Steamed Momo (10 pcs)", category: "Momo", pricePaisa: 32000, isAvailable: true, description: "Minced juicy chicken dumplings steamed fresh"),
            RestaurantCatalogItem(id: "item_03", name: "Fried Buff C-Momo", category: "Momo", pricePaisa: 38000, isAvailable: true, description: "Wok-tossed spicy chilli momo with bell peppers"),
            RestaurantCatalogItem(id: "item_04", name: "Jhol Momo Platter", category: "Momo", pricePaisa: 35000, isAvailable: true, description: "Dipped in rich sesame, peanut and timur broth"),
            RestaurantCatalogItem(id: "item_05", name: "Thakali Mutton Thali Set", category: "Thali", pricePaisa: 85000, isAvailable: true, description: "Local Mustang mutton curry, black lentils, gundruk, rice, ghee"),
            RestaurantCatalogItem(id: "item_06", name: "Thakali Chicken Thali Set", category: "Thali", pricePaisa: 72500, isAvailable: true, description: "Free-range chicken curry, jimbu dal, seasonal saag, pickles"),
            RestaurantCatalogItem(id: "item_07", name: "Chicken Chilly", category: "Snacks", pricePaisa: 42000, isAvailable: false, description: "Boneless chicken tossed in hot garlic chilli glaze"),
            RestaurantCatalogItem(id: "item_08", name: "Buff Sukuti Sadeko", category: "Snacks", pricePaisa: 55000, isAvailable: true, description: "Smoked dried meat tossed with mustard oil, onions and ginger"),
            RestaurantCatalogItem(id: "item_09", name: "Sweet Curd (Juju Dhau)", category: "Dessert", pricePaisa: 20000, isAvailable: true, description: "King of curd from Bhaktapur in clay pot"),
            RestaurantCatalogItem(id: "item_10", name: "Fresh Lemonade Soda", category: "Beverages", pricePaisa: 18500, isAvailable: true, description: "Freshly squeezed lemon with soda and rock salt")
        ]
    }
}
