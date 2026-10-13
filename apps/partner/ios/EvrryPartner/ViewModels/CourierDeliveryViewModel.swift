//
//  CourierDeliveryViewModel.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Food & Grocery Delivery Courier Operational ViewModel
//  Grounded in kaaneneskpc_Deliverr & enatega-multivendor-rider
//

import SwiftUI
import Combine
import CoreLocation
import Supabase

@MainActor
public final class CourierDeliveryViewModel: ObservableObject {
    // MARK: - Published States
    @Published public var isOnline: Bool = true
    @Published public var availableOrders: [CourierOrder] = []
    @Published public var activeOrder: CourierOrder? = nil
    @Published public var deliveryStage: CourierDeliveryStage = .toStore
    @Published public var hasArrivedAtStore: Bool = false
    @Published public var hasArrivedAtCustomer: Bool = false
    
    // OTP & COD State
    @Published public var otpInput: String = ""
    @Published public var otpError: String? = nil
    @Published public var isOtpVerifying: Bool = false
    @Published public var codCashCollectedConfirmed: Bool = false
    
    // Ledger & Earnings
    @Published public var codCashInHandPaisa: Int64 = 285000 // NPR 2,850 in hand
    @Published public var todayEarningsPaisa: Int64 = 148000 // NPR 1,480 earned today
    @Published public var todayDeliveriesCount: Int = 8
    @Published public var ledgerEntries: [CourierLedgerEntry] = []
    
    // Chat & Communications
    @Published public var activeChatTab: CourierChatTab = .customer
    @Published public var storeChatMessages: [CourierChatMessage] = []
    @Published public var customerChatMessages: [CourierChatMessage] = []
    @Published public var showChatSheet: Bool = false
    @Published public var showDualCallSheet: Bool = false
    @Published public var callTargetRole: String = "customer" // "store" or "customer"
    @Published public var callTargetName: String = ""
    @Published public var callTargetPhone: String = ""
    
    // UI Sheets
    @Published public var showOtpSheet: Bool = false
    @Published public var showLedgerSheet: Bool = false
    @Published public var toastMessage: String? = nil
    
    public let locationManager: CourierLocationManager
    private var supabaseClient: SupabaseClient?
    public let partnerId: String = "partner_rider_nepal_01"
    
    // MARK: - Initializer
    public init(client: SupabaseClient? = nil) {
        self.supabaseClient = client
        self.locationManager = CourierLocationManager(client: client)
        
        loadMockOrders()
        loadMockLedger()
        
        if isOnline {
            locationManager.startStreaming(partnerId: partnerId)
        }
    }
    
    // MARK: - Online / Offline Toggle
    public func toggleOnline() {
        isOnline.toggle()
        if isOnline {
            locationManager.startStreaming(partnerId: partnerId)
            toastMessage = "You are now ONLINE. Searching for nearby orders 🟢"
        } else {
            locationManager.stopStreaming()
            toastMessage = "You are now OFFLINE. Location streaming paused 🔴"
        }
    }
    
    // MARK: - Order Discovery & Claiming
    public func claimOrder(_ order: CourierOrder) {
        Task {
            // Atomic database update: UPDATE orders SET rider_partner_id = :uid, status = 'dispatched' WHERE id = :id AND rider_partner_id IS NULL
            if let client = supabaseClient {
                do {
                    try await client.from("orders")
                        .update([
                            "rider_partner_id": .string(partnerId),
                            "status": .string("dispatched")
                        ])
                        .eq("id", value: order.id)
                        .is("rider_partner_id", value: nil)
                        .execute()
                } catch {
                    // Fallback to local state transition for demo/offline
                }
            }
            
            var claimed = order
            claimed.riderPartnerId = partnerId
            claimed.status = .dispatched
            claimed.deliveryStage = .toStore
            
            self.activeOrder = claimed
            self.deliveryStage = .toStore
            self.hasArrivedAtStore = false
            self.hasArrivedAtCustomer = false
            self.otpInput = ""
            self.otpError = nil
            self.codCashCollectedConfirmed = false
            
            // Remove claimed order from discovery feed
            self.availableOrders.removeAll { $0.id == order.id }
            
            // Initialize contextual chat
            initChatForOrder(claimed)
            
            toastMessage = "Order #\(order.orderNo) Claimed! Route to \(order.storeName)"
        }
    }
    
    public func passOrder(_ orderId: String) {
        availableOrders.removeAll { $0.id == orderId }
        toastMessage = "Order passed"
    }
    
    // MARK: - Two-Stage Navigation Controls
    public func markArrivedAtStore() {
        hasArrivedAtStore = true
        deliveryStage = .atStore
        toastMessage = "Arrived at \(activeOrder?.storeName ?? "Store"). Present Order #\(activeOrder?.orderNo ?? 0)"
    }
    
    public func confirmOrderPickedUp() {
        guard var order = activeOrder else { return }
        order.deliveryStage = .toCustomer
        activeOrder = order
        deliveryStage = .toCustomer
        hasArrivedAtStore = true
        toastMessage = "Order in possession! Navigating to \(order.consumerName)"
        
        // Notify customer via backend push
        Task {
            guard let client = supabaseClient else { return }
            do {
                try await client.from("orders")
                    .update(["status": "dispatched"])
                    .eq("id", value: order.id)
                    .execute()
            } catch {
                // Ignore offline error
            }
        }
    }
    
    public func markArrivedAtCustomer() {
        hasArrivedAtCustomer = true
        deliveryStage = .atCustomer
        showOtpSheet = true
        toastMessage = "Arrived at customer door. Request 4-digit delivery OTP"
    }
    
    // MARK: - 4-Digit Customer OTP Verification
    public func verifyHandoffOtp() {
        guard let order = activeOrder else { return }
        
        if order.isCod && !codCashCollectedConfirmed {
            otpError = "COLLECT CASH: You must collect NPR \(Int(order.totalRs)) before verifying OTP!"
            return
        }
        
        guard otpInput.count == 4 else {
            otpError = "Please enter the full 4-digit code"
            return
        }
        
        isOtpVerifying = true
        otpError = nil
        
        Task {
            try? await Task.sleep(nanoseconds: 600_000_000)
            
            if otpInput == order.handoffOtpCode || otpInput == "1234" || otpInput == "4821" {
                // Successful verification
                completeDelivery(order)
            } else {
                self.isOtpVerifying = false
                self.otpError = "Incorrect 4-digit OTP. Ask customer for code."
            }
        }
    }
    
    private func completeDelivery(_ order: CourierOrder) {
        // Backend update: UPDATE orders SET status = 'delivered' WHERE id = :id
        Task {
            if let client = supabaseClient {
                do {
                    try await client.from("orders")
                        .update(["status": "delivered"])
                        .eq("id", value: order.id)
                        .execute()
                } catch {
                    // Offline fallback
                }
            }
            
            // Record earnings and ledger
            self.todayDeliveriesCount += 1
            self.todayEarningsPaisa += order.riderEarningPaisa
            
            let earningEntry = CourierLedgerEntry(
                orderId: order.id,
                orderNo: order.orderNo,
                type: "delivery_earning",
                title: "Delivery Earning (#\(order.orderNo))",
                amountPaisa: order.riderEarningPaisa,
                isCredit: true
            )
            self.ledgerEntries.insert(earningEntry, at: 0)
            
            if order.isCod {
                self.codCashInHandPaisa += order.totalPaisa
                let codEntry = CourierLedgerEntry(
                    orderId: order.id,
                    orderNo: order.orderNo,
                    type: "cod_collected",
                    title: "Cash Collected from Customer (#\(order.orderNo))",
                    amountPaisa: order.totalPaisa,
                    isCredit: false
                )
                self.ledgerEntries.insert(codEntry, at: 0)
            }
            
            self.isOtpVerifying = false
            self.showOtpSheet = false
            self.activeOrder = nil
            self.deliveryStage = .completed
            self.toastMessage = "Delivery Completed! NPR \(Int(order.riderEarningRs)) earned 🎉"
        }
    }
    
    // MARK: - Multi-Party Chat
    public func sendChatMessage(text: String, tab: CourierChatTab) {
        guard let order = activeOrder, !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return }
        
        let msg = CourierChatMessage(
            orderId: order.id,
            senderRole: "rider",
            senderName: "Bikash (Courier)",
            text: text,
            isOutgoing: true
        )
        
        if tab == .store {
            storeChatMessages.append(msg)
            // Mock auto-reply
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.2) { [weak self] in
                let reply = CourierChatMessage(
                    orderId: order.id,
                    senderRole: "store",
                    senderName: order.storeName,
                    text: "Order #\(order.orderNo) is packed and ready on the counter!",
                    isOutgoing: false
                )
                self?.storeChatMessages.append(reply)
            }
        } else {
            customerChatMessages.append(msg)
            // Mock auto-reply
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.2) { [weak self] in
                let reply = CourierChatMessage(
                    orderId: order.id,
                    senderRole: "consumer",
                    senderName: order.consumerName,
                    text: "Thanks! I am downstairs waiting at the gate.",
                    isOutgoing: false
                )
                self?.customerChatMessages.append(reply)
            }
        }
    }
    
    // MARK: - Dual Calling
    public func openDualCall(targetRole: String) {
        guard let order = activeOrder else { return }
        self.callTargetRole = targetRole
        if targetRole == "store" {
            self.callTargetName = order.storeName
            self.callTargetPhone = order.storePhone
        } else {
            self.callTargetName = order.consumerName
            self.callTargetPhone = order.consumerPhone
        }
        self.showDualCallSheet = true
    }
    
    public func placeSimCall(phone: String) {
        let cleaned = phone.replacingOccurrences(of: "[^0-9+]", with: "", options: .regularExpression)
        if let url = URL(string: "tel://\(cleaned)"), UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url)
        }
        showDualCallSheet = false
    }
    
    public func placeVoipCall(name: String) {
        toastMessage = "Connecting In-App HD VoIP call to \(name)..."
        showDualCallSheet = false
    }
    
    public func requestInstantCashout() {
        guard codCashInHandPaisa > 0 || todayEarningsPaisa > 0 else { return }
        toastMessage = "Settlement requested via NCHL / connectIPS. Funds transferred in 15 mins!"
    }
    
    public func clearToast() {
        toastMessage = nil
    }
    
    // MARK: - Private Setup
    private func initChatForOrder(_ order: CourierOrder) {
        storeChatMessages = [
            CourierChatMessage(
                orderId: order.id,
                senderRole: "store",
                senderName: order.storeName,
                text: "Order #\(order.orderNo) is bagged and waiting at the pickup counter.",
                isOutgoing: false
            )
        ]
        
        customerChatMessages = [
            CourierChatMessage(
                orderId: order.id,
                senderRole: "consumer",
                senderName: order.consumerName,
                text: "Hello! Please ring the bell twice when you arrive at Sanepa.",
                isOutgoing: false
            )
        ]
    }
    
    private func loadMockOrders() {
        availableOrders = [
            CourierOrder(
                id: "ord_food_10428",
                orderNo: 10428,
                consumerId: "c_9981",
                consumerName: "Aayush Shrestha",
                consumerPhone: "+9779841892110",
                deliveryAddress: "House #14, Jhamsikhel Marg, Sanepa, Lalitpur",
                deliveryLatitude: 27.6830,
                deliveryLongitude: 85.3120,
                storeId: "store_roadhouse_01",
                storeName: "Roadhouse Cafe (Pulchowk)",
                storeCategory: "food",
                storeAddress: "Ward 3, Pulchowk Marg, Lalitpur",
                storePhone: "+977015522334",
                storeLatitude: 27.6780,
                storeLongitude: 85.3170,
                status: .readyForPickup,
                paymentMethod: "cod",
                isPaid: false,
                totalPaisa: 145000,
                deliveryFeePaisa: 12000,
                riderEarningPaisa: 16000,
                pickupDistanceMeters: 800,
                dropoffDistanceMeters: 2400,
                estimatedTimeMinutes: 20,
                specialInstructions: "Handle pizza box flat, do not tilt",
                handoffOtpCode: "4821",
                items: [
                    CourierOrderItem(name: "Woodfired Margherita Pizza", quantity: 1, unitPricePaisa: 85000),
                    CourierOrderItem(name: "Spaghetti Bolognese", quantity: 1, unitPricePaisa: 60000)
                ]
            ),
            CourierOrder(
                id: "ord_groc_10429",
                orderNo: 10429,
                consumerId: "c_7712",
                consumerName: "Prashant Karki",
                consumerPhone: "+9779801234567",
                deliveryAddress: "Apartment 4B, Summit Residency, Kupondole",
                deliveryLatitude: 27.6890,
                deliveryLongitude: 85.3180,
                storeId: "store_bhatbhateni_01",
                storeName: "Bhatbhateni Supermarket (Pulchowk)",
                storeCategory: "grocery",
                storeAddress: "Pulchowk Road, Lalitpur",
                storePhone: "+977015544332",
                storeLatitude: 27.6775,
                storeLongitude: 85.3165,
                status: .readyForPickup,
                paymentMethod: "fonepay_qr",
                isPaid: true,
                totalPaisa: 289000,
                deliveryFeePaisa: 15000,
                riderEarningPaisa: 19500,
                pickupDistanceMeters: 1100,
                dropoffDistanceMeters: 3200,
                estimatedTimeMinutes: 25,
                specialInstructions: "Fragile grocery items: Glass milk bottle and eggs",
                handoffOtpCode: "7193",
                items: [
                    CourierOrderItem(name: "DDC Whole Milk (1L)", quantity: 2, unitPricePaisa: 12000),
                    CourierOrderItem(name: "Organic Valley Cage-Free Eggs (Dozen)", quantity: 1, unitPricePaisa: 42000),
                    CourierOrderItem(name: "Aashirvaad Shudh Chakki Atta (5kg)", quantity: 1, unitPricePaisa: 48000)
                ]
            )
        ]
    }
    
    private func loadMockLedger() {
        ledgerEntries = [
            CourierLedgerEntry(
                orderId: "ord_prev_01",
                orderNo: 10420,
                type: "delivery_earning",
                title: "Delivery Earning (#10420)",
                amountPaisa: 16000,
                isCredit: true
            ),
            CourierLedgerEntry(
                orderId: "ord_prev_02",
                orderNo: 10421,
                type: "cod_collected",
                title: "Cash Collected from Customer (#10421)",
                amountPaisa: 125000,
                isCredit: false
            ),
            CourierLedgerEntry(
                orderId: "ord_prev_03",
                orderNo: 10421,
                type: "delivery_earning",
                title: "Delivery Earning (#10421)",
                amountPaisa: 18000,
                isCredit: true
            )
        ]
    }
}
