//
//  CourierDashboardView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Food & Grocery Delivery Courier Main Operational View
//  Grounded in kaaneneskpc_Deliverr & enatega-multivendor-rider
//

import SwiftUI
import MapKit

public struct CourierDashboardView: View {
    @StateObject private var viewModel: CourierDeliveryViewModel
    public let onRoleSwitcherClick: () -> Void
    
    public init(
        viewModel: CourierDeliveryViewModel? = nil,
        onRoleSwitcherClick: @escaping () -> Void
    ) {
        self._viewModel = StateObject(wrappedValue: viewModel ?? CourierDeliveryViewModel())
        self.onRoleSwitcherClick = onRoleSwitcherClick
    }
    
    public var body: some View {
        ZStack {
            Color(red: 0.07, green: 0.08, blue: 0.14).ignoresSafeArea()
            
            VStack(spacing: 0) {
                // Adaptive Courier Top Bar
                CourierTopBar(
                    isOnline: viewModel.isOnline,
                    onRoleSwitcherClick: onRoleSwitcherClick,
                    onToggleOnline: { viewModel.toggleOnline() }
                )
                
                if let activeOrder = viewModel.activeOrder {
                    // Active Two-Stage Navigation HUD
                    ZStack {
                        // Map Background Simulation
                        CourierMapBackgroundView(
                            stage: viewModel.deliveryStage,
                            storeCoordinate: activeOrder.storeLocationCoordinate,
                            dropoffCoordinate: activeOrder.dropoffLocationCoordinate,
                            currentCoordinate: viewModel.locationManager.currentCoordinate
                        )
                        
                        CourierActiveHudView(viewModel: viewModel)
                    }
                } else {
                    // Idle Order Discovery Feed
                    ScrollView {
                        VStack(spacing: 16) {
                            // Earnings & Wallet Bar
                            CourierMetricsSummaryBar(
                                todayEarningsRs: Double(viewModel.todayEarningsPaisa) / 100.0,
                                cashInHandRs: Double(viewModel.codCashInHandPaisa) / 100.0,
                                deliveriesCount: viewModel.todayDeliveriesCount,
                                onOpenLedger: { viewModel.showLedgerSheet = true }
                            )
                            
                            // Online/Offline Status Banner
                            if !viewModel.isOnline {
                                HStack {
                                    Image(systemName: "moon.fill")
                                        .foregroundColor(.orange)
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text("You are currently OFFLINE")
                                            .font(.system(size: 13, weight: .bold))
                                            .foregroundColor(.white)
                                        Text("Go online to receive nearby food & grocery dispatch offers")
                                            .font(.system(size: 11))
                                            .foregroundColor(.gray)
                                    }
                                    Spacer()
                                    Button {
                                        viewModel.toggleOnline()
                                    } label: {
                                        Text("Go Online 🟢")
                                            .font(.system(size: 12, weight: .bold))
                                            .padding(.horizontal, 12)
                                            .padding(.vertical, 6)
                                            .background(Color.green)
                                            .foregroundColor(.white)
                                            .cornerRadius(10)
                                    }
                                }
                                .padding(12)
                                .background(Color.orange.opacity(0.12))
                                .cornerRadius(12)
                                .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.orange.opacity(0.3), lineWidth: 1))
                            }
                            
                            // Discovery Feed Section
                            VStack(alignment: .leading, spacing: 12) {
                                HStack {
                                    Text("NEARBY DELIVERY OFFERS (\(viewModel.availableOrders.count))")
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(.gray)
                                    Spacer()
                                    if viewModel.isOnline {
                                        HStack(spacing: 4) {
                                            Circle().fill(Color.green).frame(width: 6, height: 6)
                                            Text("Radar Active")
                                                .font(.system(size: 11, weight: .semibold))
                                                .foregroundColor(.green)
                                        }
                                    }
                                }
                                
                                if viewModel.availableOrders.isEmpty {
                                    VStack(spacing: 10) {
                                        Image(systemName: "bicycle")
                                            .font(.system(size: 40))
                                            .foregroundColor(.gray.opacity(0.6))
                                        Text("Scanning for nearby restaurant and supermarket dispatches...")
                                            .font(.system(size: 13))
                                            .foregroundColor(.gray)
                                            .multilineTextAlignment(.center)
                                    }
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 40)
                                } else {
                                    ForEach(viewModel.availableOrders) { order in
                                        CourierOrderDiscoveryCard(
                                            order: order,
                                            onClaim: { viewModel.claimOrder(order) },
                                            onPass: { viewModel.passOrder(order.id) }
                                        )
                                    }
                                }
                            }
                        }
                        .padding(16)
                    }
                }
            }
            
            // Toast Notification Banner
            if let toast = viewModel.toastMessage {
                VStack {
                    Spacer()
                    HStack {
                        Text(toast)
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(.white)
                        Spacer()
                        Button {
                            viewModel.clearToast()
                        } label: {
                            Image(systemName: "xmark")
                                .font(.system(size: 12))
                                .foregroundColor(.gray)
                        }
                    }
                    .padding(14)
                    .background(Color(red: 0.16, green: 0.18, blue: 0.28))
                    .cornerRadius(12)
                    .shadow(radius: 8)
                    .padding(.horizontal, 20)
                    .padding(.bottom, 24)
                }
                .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
        .sheet(isPresented: $viewModel.showOtpSheet) {
            CourierOtpVerificationSheet(viewModel: viewModel)
        }
        .sheet(isPresented: $viewModel.showLedgerSheet) {
            CourierCodLedgerSheet(viewModel: viewModel)
        }
        .sheet(isPresented: $viewModel.showChatSheet) {
            CourierMultiPartyChatSheet(viewModel: viewModel)
        }
        .sheet(isPresented: $viewModel.showDualCallSheet) {
            CourierDualCallSheet(viewModel: viewModel)
        }
    }
}

// MARK: - Top Bar with 1-Tap Role Switcher
private struct CourierTopBar: View {
    let isOnline: Bool
    let onRoleSwitcherClick: () -> Void
    let onToggleOnline: () -> Void
    
    var body: some View {
        HStack {
            // 1-Tap Role Switcher Pill
            Button(action: onRoleSwitcherClick) {
                HStack(spacing: 8) {
                    Text("🛵")
                        .font(.system(size: 20))
                    VStack(alignment: .leading, spacing: 1) {
                        Text("Delivery Courier")
                            .font(.system(size: 13, weight: .bold))
                            .foregroundColor(.white)
                        Text("Tap to switch role ▼")
                            .font(.system(size: 10, weight: .semibold))
                            .foregroundColor(.orange)
                    }
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(Color(red: 0.14, green: 0.16, blue: 0.25))
                .cornerRadius(20)
                .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.white.opacity(0.12), lineWidth: 1))
            }
            
            Spacer()
            
            // Online / Offline Switch
            Button(action: onToggleOnline) {
                HStack(spacing: 6) {
                    Circle()
                        .fill(isOnline ? Color.green : Color.red)
                        .frame(width: 8, height: 8)
                    Text(isOnline ? "ONLINE" : "OFFLINE")
                        .font(.system(size: 11, weight: .heavy))
                        .foregroundColor(isOnline ? .green : .red)
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 7)
                .background(isOnline ? Color.green.opacity(0.15) : Color.red.opacity(0.15))
                .cornerRadius(18)
                .overlay(RoundedRectangle(cornerRadius: 18).stroke(isOnline ? Color.green.opacity(0.4) : Color.red.opacity(0.4), lineWidth: 1))
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
        .background(Color(red: 0.10, green: 0.12, blue: 0.20))
        .overlay(Rectangle().frame(height: 1).foregroundColor(Color.white.opacity(0.08)), alignment: .bottom)
    }
}

// MARK: - Metrics Summary Bar
private struct CourierMetricsSummaryBar: View {
    let todayEarningsRs: Double
    let cashInHandRs: Double
    let deliveriesCount: Int
    let onOpenLedger: () -> Void
    
    var body: some View {
        HStack(spacing: 10) {
            // Today's Earning
            VStack(alignment: .leading, spacing: 2) {
                Text("TODAY'S EARNING")
                    .font(.system(size: 9, weight: .bold))
                    .foregroundColor(.gray)
                Text("NPR \(Int(todayEarningsRs))")
                    .font(.system(size: 15, weight: .black))
                    .foregroundColor(.green)
                Text("\(deliveriesCount) completed")
                    .font(.system(size: 9))
                    .foregroundColor(.gray)
            }
            .padding(10)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color(red: 0.12, green: 0.14, blue: 0.22))
            .cornerRadius(12)
            
            // Cash in Hand & Ledger Button
            Button(action: onOpenLedger) {
                VStack(alignment: .leading, spacing: 2) {
                    HStack {
                        Text("CASH IN HAND")
                            .font(.system(size: 9, weight: .bold))
                            .foregroundColor(.orange)
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.system(size: 9))
                            .foregroundColor(.orange)
                    }
                    Text("NPR \(Int(cashInHandRs))")
                        .font(.system(size: 15, weight: .black))
                        .foregroundColor(.white)
                    Text("Tap to view ledger")
                        .font(.system(size: 9))
                        .foregroundColor(.orange)
                }
                .padding(10)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(red: 0.12, green: 0.14, blue: 0.22))
                .cornerRadius(12)
                .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.orange.opacity(0.3), lineWidth: 1))
            }
        }
    }
}

// MARK: - Order Discovery Card
private struct CourierOrderDiscoveryCard: View {
    let order: CourierOrder
    let onClaim: () -> Void
    let onPass: () -> Void
    
    var body: some View {
        VStack(spacing: 12) {
            // Header with Store Category
            HStack {
                HStack(spacing: 6) {
                    Text(order.storeCategory == "food" ? "🍳" : "🛒")
                        .font(.system(size: 16))
                    Text(order.storeCategory == "food" ? "RESTAURANT" : "SUPERMARKET")
                        .font(.system(size: 10, weight: .black))
                        .foregroundColor(order.storeCategory == "food" ? .orange : .green)
                }
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(order.storeCategory == "food" ? Color.orange.opacity(0.15) : Color.green.opacity(0.15))
                .cornerRadius(8)
                
                Spacer()
                
                Text("ORDER #\(order.orderNo)")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(.gray)
            }
            
            // Route Breakdown: Store -> Customer
            VStack(spacing: 8) {
                HStack(alignment: .top, spacing: 10) {
                    Circle()
                        .fill(Color.orange)
                        .frame(width: 8, height: 8)
                        .padding(.top, 4)
                    
                    VStack(alignment: .leading, spacing: 2) {
                        Text(order.storeName)
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.white)
                        Text(order.storeAddress)
                            .font(.system(size: 11))
                            .foregroundColor(.gray)
                    }
                    
                    Spacer()
                    
                    Text("\(order.pickupDistanceMeters) m")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(.orange)
                }
                
                HStack(alignment: .top, spacing: 10) {
                    Circle()
                        .fill(Color.purple)
                        .frame(width: 8, height: 8)
                        .padding(.top, 4)
                    
                    VStack(alignment: .leading, spacing: 2) {
                        Text(order.consumerName)
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.white)
                        Text(order.deliveryAddress)
                            .font(.system(size: 11))
                            .foregroundColor(.gray)
                    }
                    
                    Spacer()
                    
                    Text("\(order.dropoffDistanceMeters) m")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(.purple)
                }
            }
            
            Divider().background(Color.white.opacity(0.08))
            
            // Financials: Courier Earning & Payment Mode
            HStack {
                VStack(alignment: .leading, spacing: 1) {
                    Text("ESTIMATED EARNING")
                        .font(.system(size: 9, weight: .bold))
                        .foregroundColor(.gray)
                    Text("NPR \(Int(order.riderEarningRs))")
                        .font(.system(size: 18, weight: .black))
                        .foregroundColor(.green)
                }
                
                Spacer()
                
                if order.isCod {
                    VStack(alignment: .trailing, spacing: 1) {
                        Text("COLLECT CASH")
                            .font(.system(size: 9, weight: .bold))
                            .foregroundColor(.red)
                        Text("NPR \(Int(order.totalRs))")
                            .font(.system(size: 13, weight: .heavy))
                            .foregroundColor(.red)
                    }
                } else {
                    Text("PAID ONLINE ✓")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.green)
                }
            }
            
            // Action Buttons: Pass vs Claim
            HStack(spacing: 10) {
                Button(action: onPass) {
                    Text("Pass")
                        .font(.system(size: 13, weight: .bold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                        .background(Color.white.opacity(0.08))
                        .foregroundColor(.gray)
                        .cornerRadius(12)
                }
                
                Button(action: onClaim) {
                    HStack {
                        Image(systemName: "hand.raised.fill")
                        Text("Accept Delivery 🛵")
                            .font(.system(size: 13, weight: .bold))
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
                    .background(Color.orange)
                    .foregroundColor(.white)
                    .cornerRadius(12)
                }
            }
        }
        .padding(14)
        .background(Color(red: 0.12, green: 0.14, blue: 0.22))
        .cornerRadius(16)
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.white.opacity(0.08), lineWidth: 1))
    }
}

// MARK: - Map Background Simulation
private struct CourierMapBackgroundView: View {
    let stage: CourierDeliveryStage
    let storeCoordinate: CLLocationCoordinate2D
    let dropoffCoordinate: CLLocationCoordinate2D
    let currentCoordinate: CLLocationCoordinate2D
    
    @State private var region: MKCoordinateRegion
    
    init(stage: CourierDeliveryStage, storeCoordinate: CLLocationCoordinate2D, dropoffCoordinate: CLLocationCoordinate2D, currentCoordinate: CLLocationCoordinate2D) {
        self.stage = stage
        self.storeCoordinate = storeCoordinate
        self.dropoffCoordinate = dropoffCoordinate
        self.currentCoordinate = currentCoordinate
        self._region = State(initialValue: MKCoordinateRegion(
            center: currentCoordinate,
            span: MKCoordinateSpan(latitudeDelta: 0.02, longitudeDelta: 0.02)
        ))
    }
    
    var body: some View {
        Map(coordinateRegion: $region, annotationItems: [
            MapAnnotationItem(id: "current", coordinate: currentCoordinate, title: "Courier", icon: "bicycle"),
            MapAnnotationItem(id: "store", coordinate: storeCoordinate, title: "Store", icon: "building.2.fill"),
            MapAnnotationItem(id: "dropoff", coordinate: dropoffCoordinate, title: "Customer", icon: "house.fill")
        ]) { item in
            MapAnnotation(coordinate: item.coordinate) {
                VStack(spacing: 2) {
                    Image(systemName: item.icon)
                        .font(.system(size: 14))
                        .foregroundColor(.white)
                        .frame(width: 28, height: 28)
                        .background(item.id == "current" ? Color.blue : (item.id == "store" ? Color.orange : Color.purple))
                        .clipShape(Circle())
                        .shadow(radius: 4)
                    Text(item.title)
                        .font(.system(size: 9, weight: .bold))
                        .foregroundColor(.white)
                        .padding(.horizontal, 4)
                        .background(Color.black.opacity(0.6))
                        .cornerRadius(4)
                }
            }
        }
        .ignoresSafeArea()
        .onChange(of: currentCoordinate.latitude) { _ in
            region.center = currentCoordinate
        }
    }
}

private struct MapAnnotationItem: Identifiable {
    let id: String
    let coordinate: CLLocationCoordinate2D
    let title: String
    let icon: String
}
