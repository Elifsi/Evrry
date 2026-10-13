//
//  KitchenDisplayView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Commercial Kitchen Display System (KDS) Tabbed View
//

import SwiftUI

public struct KitchenDisplayView: View {
    @StateObject public var viewModel = RestaurantKitchenViewModel()
    public let onRoleSwitch: (PartnerRole) -> Void
    
    @State private var showRoleSwitcher: Bool = false
    @State private var alarmPulse: Bool = false
    
    public init(onRoleSwitch: @escaping (PartnerRole) -> Void) {
        self.onRoleSwitch = onRoleSwitch
    }
    
    public var body: some View {
        ZStack {
            PartnerKitchenTheme.bgDark.ignoresSafeArea()
            
            VStack(spacing: 0) {
                // TOP BAR
                kdsTopBar
                
                // TAB SELECTOR BAR
                kdsTabBar
                
                // TAB CONTENT
                TabView(selection: $viewModel.activeTab) {
                    incomingTabContent
                        .tag(KitchenTab.incoming)
                    
                    preparingTabContent
                        .tag(KitchenTab.preparing)
                    
                    readyTabContent
                        .tag(KitchenTab.readyForPickup)
                    
                    completedTabContent
                        .tag(KitchenTab.completed)
                }
                .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
            }
        }
        // MODALS & SHEETS
        .sheet(isPresented: $viewModel.showPrepTimeModal) {
            if let order = viewModel.selectedOrder {
                PrepTimeModalView(
                    order: order,
                    onConfirm: { minutes, autoPrint in
                        viewModel.confirmAccept(order: order, prepMinutes: minutes, autoPrint: autoPrint)
                    },
                    onCancel: {
                        viewModel.showPrepTimeModal = false
                    }
                )
                .presentationDetents([.medium, .fraction(0.65)])
            }
        }
        .sheet(isPresented: $viewModel.showRejectReasonSheet) {
            if let order = viewModel.selectedOrder {
                RejectReasonSheetView(
                    order: order,
                    onConfirm: { reason in
                        viewModel.confirmReject(order: order, reason: reason)
                    },
                    onCancel: {
                        viewModel.showRejectReasonSheet = false
                    }
                )
                .presentationDetents([.medium, .fraction(0.65)])
            }
        }
        .sheet(isPresented: $viewModel.showMenu86Sheet) {
            Menu86View(viewModel: viewModel) {
                viewModel.showMenu86Sheet = false
            }
        }
        .sheet(isPresented: $viewModel.showPrintReceiptSheet) {
            if let order = viewModel.selectedOrder {
                ReceiptPrintView(
                    order: order,
                    store: viewModel.currentStore,
                    initialDocType: viewModel.printType,
                    onDismiss: {
                        viewModel.showPrintReceiptSheet = false
                    }
                )
            }
        }
        .sheet(isPresented: $viewModel.showChatSheet) {
            if let order = viewModel.selectedOrder {
                KitchenChatSheetView(
                    viewModel: viewModel,
                    order: order,
                    onDismiss: {
                        viewModel.showChatSheet = false
                    }
                )
            }
        }
        .sheet(isPresented: $showRoleSwitcher) {
            roleSwitcherSheet
                .presentationDetents([.fraction(0.45)])
        }
    }
    
    // MARK: - Top Bar
    
    private var kdsTopBar: some View {
        VStack(spacing: 0) {
            HStack(spacing: 10) {
                // Store Open / Closed Toggle Pill
                Button {
                    viewModel.toggleStoreOpen()
                } label: {
                    HStack(spacing: 6) {
                        Circle()
                            .fill(viewModel.isStoreOpen ? PartnerKitchenTheme.accentGreen : PartnerKitchenTheme.alertRed)
                            .frame(width: 8, height: 8)
                        Text(viewModel.isStoreOpen ? "KITCHEN OPEN" : "CLOSED")
                            .font(.system(size: 11, weight: .heavy))
                            .foregroundColor(viewModel.isStoreOpen ? PartnerKitchenTheme.accentGreen : PartnerKitchenTheme.alertRed)
                    }
                    .padding(.horizontal, 10)
                    .padding(.vertical, 6)
                    .background(viewModel.isStoreOpen ? PartnerKitchenTheme.accentGreen.opacity(0.15) : PartnerKitchenTheme.alertRed.opacity(0.15))
                    .cornerRadius(8)
                }
                
                // Store Name
                VStack(alignment: .leading, spacing: 1) {
                    Text(viewModel.currentStore.name)
                        .font(.system(size: 13, weight: .black))
                        .foregroundColor(PartnerKitchenTheme.textPrimary)
                        .lineLimit(1)
                    Text("KDS Active • Lalitpur")
                        .font(.system(size: 10, weight: .medium))
                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                }
                
                Spacer()
                
                // Silence Alarm button if active
                if viewModel.isAlarmActive {
                    Button {
                        viewModel.silenceAlarm()
                    } label: {
                        HStack(spacing: 4) {
                            Image(systemName: "speaker.slash.fill")
                                .font(.system(size: 11))
                            Text("SILENCE")
                                .font(.system(size: 10, weight: .heavy))
                        }
                        .padding(.horizontal, 8)
                        .padding(.vertical, 6)
                        .background(PartnerKitchenTheme.alertRed)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                        .scaleEffect(alarmPulse ? 1.05 : 0.95)
                        .animation(.easeInOut(duration: 0.5).repeatForever(autoreverses: true), value: alarmPulse)
                    }
                    .onAppear { alarmPulse = true }
                }
                
                // Menu 86'ing Button
                Button {
                    viewModel.showMenu86Sheet = true
                } label: {
                    HStack(spacing: 4) {
                        Image(systemName: "slash.circle")
                            .font(.system(size: 11, weight: .bold))
                        Text("86'd Menu")
                            .font(.system(size: 10, weight: .bold))
                    }
                    .padding(.horizontal, 8)
                    .padding(.vertical, 6)
                    .background(PartnerKitchenTheme.cardDark)
                    .foregroundColor(PartnerKitchenTheme.primaryOrange)
                    .cornerRadius(8)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
                    )
                }
                
                // 1-Tap Role Switcher Pill
                Button {
                    showRoleSwitcher = true
                } label: {
                    HStack(spacing: 4) {
                        Text(PartnerRole.restaurant.emoji)
                            .font(.system(size: 13))
                        Image(systemName: "chevron.down")
                            .font(.system(size: 9, weight: .bold))
                            .foregroundColor(PartnerKitchenTheme.textSecondary)
                    }
                    .padding(.horizontal, 8)
                    .padding(.vertical, 6)
                    .background(PartnerKitchenTheme.cardDark)
                    .cornerRadius(8)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
                    )
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
            .background(PartnerKitchenTheme.bgDark)
            
            Divider()
                .background(PartnerKitchenTheme.cardBorder)
        }
    }
    
    // MARK: - Tab Bar
    
    private var kdsTabBar: some View {
        HStack(spacing: 6) {
            ForEach(KitchenTab.allCases) { tab in
                let isSelected = (viewModel.activeTab == tab)
                let count: Int = {
                    switch tab {
                    case .incoming: return viewModel.incomingOrders.count
                    case .preparing: return viewModel.preparingOrders.count
                    case .readyForPickup: return viewModel.readyOrders.count
                    case .completed: return viewModel.completedOrders.count
                    }
                }()
                
                Button {
                    withAnimation(.easeInOut(duration: 0.2)) {
                        viewModel.activeTab = tab
                    }
                } label: {
                    HStack(spacing: 5) {
                        Image(systemName: tab.iconName)
                            .font(.system(size: 11))
                        Text(tab.rawValue)
                            .font(.system(size: 12, weight: isSelected ? .bold : .medium))
                        
                        if count > 0 {
                            Text("\(count)")
                                .font(.system(size: 10, weight: .heavy))
                                .padding(.horizontal, 5)
                                .padding(.vertical, 2)
                                .background(isSelected ? Color.white.opacity(0.25) : PartnerKitchenTheme.primaryOrange.opacity(0.3))
                                .foregroundColor(isSelected ? .white : PartnerKitchenTheme.primaryOrange)
                                .cornerRadius(10)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 10)
                    .background(isSelected ? PartnerKitchenTheme.primaryOrange : PartnerKitchenTheme.cardDark)
                    .foregroundColor(isSelected ? .white : PartnerKitchenTheme.textSecondary)
                    .cornerRadius(10)
                    .overlay(
                        RoundedRectangle(cornerRadius: 10)
                            .stroke(isSelected ? PartnerKitchenTheme.primaryOrange : PartnerKitchenTheme.cardBorder, lineWidth: 1)
                    )
                }
            }
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(PartnerKitchenTheme.bgDark)
    }
    
    // MARK: - Incoming Orders Tab
    
    private var incomingTabContent: some View {
        ScrollView {
            if viewModel.incomingOrders.isEmpty {
                emptyKdsState(title: "No Incoming Orders", subtitle: "New orders will ring with a high-priority chime", icon: "bell.slash.fill")
            } else {
                LazyVStack(spacing: 14) {
                    ForEach(viewModel.incomingOrders) { order in
                        incomingOrderCard(order: order)
                    }
                }
                .padding(14)
            }
        }
    }
    
    private func incomingOrderCard(order: RestaurantOrder) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            // Header with pulsing alert badge
            HStack {
                HStack(spacing: 6) {
                    Circle()
                        .fill(PartnerKitchenTheme.alertRed)
                        .frame(width: 8, height: 8)
                    Text("NEW ORDER #\(order.orderNo)")
                        .font(.system(size: 16, weight: .black))
                        .foregroundColor(PartnerKitchenTheme.textPrimary)
                }
                
                Spacer()
                
                Text(order.paymentMethod.uppercased())
                    .font(.system(size: 10, weight: .black))
                    .padding(.horizontal, 8)
                    .padding(.vertical, 3)
                    .background(order.paymentMethod == "prepaid" ? PartnerKitchenTheme.accentGreen.opacity(0.2) : PartnerKitchenTheme.timerAmber.opacity(0.2))
                    .foregroundColor(order.paymentMethod == "prepaid" ? PartnerKitchenTheme.accentGreen : PartnerKitchenTheme.timerAmber)
                    .cornerRadius(6)
            }
            
            // Customer Info & Placed Time
            HStack {
                Text("👤 \(order.consumerName) (\(order.consumerPhone))")
                    .font(.system(size: 12, weight: .medium))
                    .foregroundColor(PartnerKitchenTheme.textSecondary)
                Spacer()
                Text("Just now")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(PartnerKitchenTheme.alertRed)
            }
            
            // Customer Note if any
            if let note = order.customerNote, !note.isEmpty {
                HStack(alignment: .top, spacing: 6) {
                    Image(systemName: "exclamationmark.bubble.fill")
                        .font(.system(size: 12))
                        .foregroundColor(PartnerKitchenTheme.timerAmber)
                    Text("NOTE: \(note)")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(PartnerKitchenTheme.timerAmber)
                }
                .padding(8)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(PartnerKitchenTheme.timerAmber.opacity(0.12))
                .cornerRadius(8)
            }
            
            Divider().background(PartnerKitchenTheme.cardBorder)
            
            // Item List
            VStack(alignment: .leading, spacing: 6) {
                ForEach(order.items) { item in
                    HStack {
                        Text("\(item.quantity)x")
                            .font(.system(size: 14, weight: .black))
                            .foregroundColor(PartnerKitchenTheme.primaryOrange)
                            .frame(width: 28, alignment: .leading)
                        
                        Text(item.name)
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(PartnerKitchenTheme.textPrimary)
                        
                        Spacer()
                        
                        Text("NPR \(String(format: "%.0f", Double(item.lineTotalPaisa)/100.0))")
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(PartnerKitchenTheme.textSecondary)
                    }
                    if let itemNote = item.customerNotes, !itemNote.isEmpty {
                        Text("   * \(itemNote)")
                            .font(.system(size: 11, weight: .semibold))
                            .foregroundColor(PartnerKitchenTheme.timerAmber)
                    }
                }
            }
            
            Divider().background(PartnerKitchenTheme.cardBorder)
            
            // Total & Action Buttons
            HStack {
                VStack(alignment: .leading, spacing: 1) {
                    Text("TOTAL AMOUNT")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                    Text("NPR \(String(format: "%.0f", order.totalNpr))")
                        .font(.system(size: 16, weight: .black))
                        .foregroundColor(PartnerKitchenTheme.textPrimary)
                }
                
                Spacer()
                
                // Reject Button
                Button {
                    viewModel.promptReject(order: order)
                } label: {
                    Text("Decline")
                        .font(.system(size: 13, weight: .bold))
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(PartnerKitchenTheme.alertRed.opacity(0.15))
                        .foregroundColor(PartnerKitchenTheme.alertRed)
                        .cornerRadius(10)
                        .overlay(
                            RoundedRectangle(cornerRadius: 10)
                                .stroke(PartnerKitchenTheme.alertRed.opacity(0.4), lineWidth: 1)
                        )
                }
                
                // Accept Button
                Button {
                    viewModel.promptAccept(order: order)
                } label: {
                    HStack(spacing: 4) {
                        Image(systemName: "checkmark")
                        Text("Accept Order")
                    }
                    .font(.system(size: 13, weight: .black))
                    .padding(.horizontal, 18)
                    .padding(.vertical, 10)
                    .background(PartnerKitchenTheme.primaryOrange)
                    .foregroundColor(.white)
                    .cornerRadius(10)
                    .shadow(color: PartnerKitchenTheme.primaryOrange.opacity(0.4), radius: 6, y: 3)
                }
            }
        }
        .padding(14)
        .background(PartnerKitchenTheme.cardDark)
        .cornerRadius(16)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(PartnerKitchenTheme.alertRed, lineWidth: 2)
        )
    }
    
    // MARK: - Preparing Orders Tab
    
    private var preparingTabContent: some View {
        ScrollView {
            if viewModel.preparingOrders.isEmpty {
                emptyKdsState(title: "Kitchen Is Idle", subtitle: "Accept incoming orders to initiate cooking timers", icon: "flame")
            } else {
                LazyVStack(spacing: 14) {
                    ForEach(viewModel.preparingOrders) { order in
                        preparingOrderCard(order: order)
                    }
                }
                .padding(14)
            }
        }
    }
    
    private func preparingOrderCard(order: RestaurantOrder) -> some View {
        let remainingSec = order.remainingPrepSeconds(now: viewModel.now)
        let isOverdue = order.isOverdue(now: viewModel.now)
        let progress = order.prepProgress(now: viewModel.now)
        let formattedTime = order.formattedRemainingTime(now: viewModel.now)
        
        let timerColor: Color = {
            if isOverdue { return PartnerKitchenTheme.alertRed }
            if remainingSec < 300 { return PartnerKitchenTheme.timerAmber }
            return PartnerKitchenTheme.accentGreen
        }()
        
        return VStack(alignment: .leading, spacing: 12) {
            // Header with circular countdown
            HStack(alignment: .center) {
                VStack(alignment: .leading, spacing: 3) {
                    HStack(spacing: 6) {
                        Text("ORDER #\(order.orderNo)")
                            .font(.system(size: 16, weight: .black))
                            .foregroundColor(PartnerKitchenTheme.textPrimary)
                        
                        if isOverdue {
                            Text("OVERDUE")
                                .font(.system(size: 9, weight: .heavy))
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(PartnerKitchenTheme.alertRed.opacity(0.2))
                                .foregroundColor(PartnerKitchenTheme.alertRed)
                                .cornerRadius(4)
                        }
                    }
                    Text("Customer: \(order.consumerName)")
                        .font(.system(size: 12))
                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                }
                
                Spacer()
                
                // CIRCULAR COUNTDOWN TIMER
                ZStack {
                    Circle()
                        .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 4)
                        .frame(width: 54, height: 54)
                    
                    Circle()
                        .trim(from: 0.0, to: CGFloat(1.0 - progress))
                        .stroke(timerColor, style: StrokeStyle(lineWidth: 4, lineCap: .round))
                        .frame(width: 54, height: 54)
                        .rotationEffect(.degrees(-90))
                    
                    VStack(spacing: 0) {
                        Text(formattedTime)
                            .font(.system(size: 12, weight: .black, design: .monospaced))
                            .foregroundColor(timerColor)
                        Text("rem")
                            .font(.system(size: 8, weight: .bold))
                            .foregroundColor(PartnerKitchenTheme.textMuted)
                    }
                }
            }
            
            // Items Cooking Checklist
            VStack(alignment: .leading, spacing: 6) {
                ForEach(order.items) { item in
                    HStack {
                        Image(systemName: "checkmark.circle")
                            .font(.system(size: 13))
                            .foregroundColor(PartnerKitchenTheme.accentGreen)
                        Text("\(item.quantity)x \(item.name)")
                            .font(.system(size: 13, weight: .bold))
                            .foregroundColor(PartnerKitchenTheme.textPrimary)
                        Spacer()
                    }
                    if let itemNote = item.customerNotes, !itemNote.isEmpty {
                        Text("     * \(itemNote)")
                            .font(.system(size: 11, weight: .semibold))
                            .foregroundColor(PartnerKitchenTheme.timerAmber)
                    }
                }
            }
            .padding(10)
            .background(PartnerKitchenTheme.bgDark)
            .cornerRadius(10)
            
            // Courier Assignment Status if known
            if let riderName = order.riderName {
                HStack(spacing: 6) {
                    Image(systemName: "figure.walk.motion")
                        .foregroundColor(PartnerKitchenTheme.courierBlue)
                    Text("Courier assigned: \(riderName) • \(String(format: "%.1f", order.riderDistanceKm ?? 0.0)) km away")
                        .font(.system(size: 11, weight: .medium))
                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                }
            }
            
            Divider().background(PartnerKitchenTheme.cardBorder)
            
            // Secondary Tools & "Mark Ready for Pickup"
            HStack(spacing: 8) {
                // Print KOT
                Button {
                    viewModel.preparePrint(order: order, type: .kot)
                } label: {
                    Image(systemName: "printer.fill")
                        .font(.system(size: 14))
                        .padding(10)
                        .background(PartnerKitchenTheme.bgDark)
                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                        .cornerRadius(10)
                        .overlay(
                            RoundedRectangle(cornerRadius: 10)
                                .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
                        )
                }
                
                // Customer Chat
                Button {
                    viewModel.openChat(order: order, recipient: "consumer")
                } label: {
                    Image(systemName: "message.fill")
                        .font(.system(size: 14))
                        .padding(10)
                        .background(PartnerKitchenTheme.bgDark)
                        .foregroundColor(PartnerKitchenTheme.primaryOrange)
                        .cornerRadius(10)
                        .overlay(
                            RoundedRectangle(cornerRadius: 10)
                                .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
                        )
                }
                
                Spacer()
                
                // Ready for Pickup Button
                Button {
                    viewModel.markReadyForPickup(order: order)
                } label: {
                    HStack(spacing: 6) {
                        Image(systemName: "checkmark.seal.fill")
                        Text("Ready for Pickup")
                    }
                    .font(.system(size: 13, weight: .black))
                    .padding(.horizontal, 16)
                    .padding(.vertical, 11)
                    .background(PartnerKitchenTheme.accentGreen)
                    .foregroundColor(.white)
                    .cornerRadius(10)
                    .shadow(color: PartnerKitchenTheme.accentGreen.opacity(0.35), radius: 6, y: 3)
                }
            }
        }
        .padding(14)
        .background(PartnerKitchenTheme.cardDark)
        .cornerRadius(16)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(timerColor.opacity(0.6), lineWidth: 1.5)
        )
    }
    
    // MARK: - Ready for Pickup Tab
    
    private var readyTabContent: some View {
        ScrollView {
            if viewModel.readyOrders.isEmpty {
                emptyKdsState(title: "No Orders Waiting", subtitle: "Orders ready for dispatch will appear here awaiting courier arrival", icon: "box.truck.fill")
            } else {
                LazyVStack(spacing: 14) {
                    ForEach(viewModel.readyOrders) { order in
                        readyOrderCard(order: order)
                    }
                }
                .padding(14)
            }
        }
    }
    
    private func readyOrderCard(order: RestaurantOrder) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                HStack(spacing: 6) {
                    Image(systemName: "checkmark.seal.fill")
                        .foregroundColor(PartnerKitchenTheme.accentGreen)
                    Text("ORDER #\(order.orderNo)")
                        .font(.system(size: 16, weight: .black))
                        .foregroundColor(PartnerKitchenTheme.textPrimary)
                }
                Spacer()
                Text("READY")
                    .font(.system(size: 10, weight: .heavy))
                    .padding(.horizontal, 8)
                    .padding(.vertical, 3)
                    .background(PartnerKitchenTheme.accentGreen.opacity(0.2))
                    .foregroundColor(PartnerKitchenTheme.accentGreen)
                    .cornerRadius(6)
            }
            
            // Assigned Courier Details Card
            HStack(spacing: 10) {
                Image(systemName: "scooter")
                    .font(.system(size: 20))
                    .foregroundColor(PartnerKitchenTheme.courierBlue)
                    .frame(width: 40, height: 40)
                    .background(PartnerKitchenTheme.courierBlue.opacity(0.12))
                    .cornerRadius(10)
                
                VStack(alignment: .leading, spacing: 2) {
                    Text(order.riderName ?? "Searching for nearby courier...")
                        .font(.system(size: 13, weight: .bold))
                        .foregroundColor(PartnerKitchenTheme.textPrimary)
                    Text("\(order.riderVehicleType ?? "Motorbike") • \(String(format: "%.1f", order.riderDistanceKm ?? 0.0)) km away")
                        .font(.system(size: 11))
                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                }
                
                Spacer()
                
                if order.riderName != nil {
                    Button {
                        viewModel.openChat(order: order, recipient: "courier")
                    } label: {
                        Image(systemName: "message.fill")
                            .font(.system(size: 14))
                            .padding(8)
                            .background(PartnerKitchenTheme.courierBlue.opacity(0.2))
                            .foregroundColor(PartnerKitchenTheme.courierBlue)
                            .cornerRadius(8)
                    }
                }
            }
            .padding(10)
            .background(PartnerKitchenTheme.bgDark)
            .cornerRadius(10)
            
            // Items summary
            HStack {
                Text("\(order.items.count) item(s) • Total NPR \(String(format: "%.0f", order.totalNpr))")
                    .font(.system(size: 12, weight: .semibold))
                    .foregroundColor(PartnerKitchenTheme.textSecondary)
                Spacer()
                Text(order.paymentMethod.uppercased())
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(PartnerKitchenTheme.timerAmber)
            }
            
            Divider().background(PartnerKitchenTheme.cardBorder)
            
            // Actions: Print Bill & Handover
            HStack(spacing: 10) {
                Button {
                    viewModel.preparePrint(order: order, type: .customerBill)
                } label: {
                    HStack(spacing: 4) {
                        Image(systemName: "doc.text.fill")
                        Text("Print Tax Bill")
                    }
                    .font(.system(size: 12, weight: .bold))
                    .padding(.horizontal, 14)
                    .padding(.vertical, 10)
                    .background(PartnerKitchenTheme.bgDark)
                    .foregroundColor(PartnerKitchenTheme.textPrimary)
                    .cornerRadius(8)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
                    )
                }
                
                Spacer()
                
                Button {
                    viewModel.markDispatched(order: order)
                } label: {
                    HStack(spacing: 6) {
                        Image(systemName: "shippingbox.fill")
                        Text("Handoff to Courier")
                    }
                    .font(.system(size: 13, weight: .black))
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
                    .background(PartnerKitchenTheme.courierBlue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
                }
            }
        }
        .padding(14)
        .background(PartnerKitchenTheme.cardDark)
        .cornerRadius(16)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
        )
    }
    
    // MARK: - Completed Tab
    
    private var completedTabContent: some View {
        ScrollView {
            VStack(spacing: 16) {
                // DAILY GROSS SALES SUMMARY BANNER
                HStack(spacing: 14) {
                    VStack(alignment: .leading, spacing: 3) {
                        Text("TODAY'S ORDERS")
                            .font(.system(size: 10, weight: .heavy))
                            .foregroundColor(PartnerKitchenTheme.textSecondary)
                        Text("\(viewModel.completedCountToday)")
                            .font(.system(size: 24, weight: .black))
                            .foregroundColor(PartnerKitchenTheme.textPrimary)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    
                    Divider().frame(height: 36).background(PartnerKitchenTheme.cardBorder)
                    
                    VStack(alignment: .leading, spacing: 3) {
                        Text("GROSS SALES")
                            .font(.system(size: 10, weight: .heavy))
                            .foregroundColor(PartnerKitchenTheme.textSecondary)
                        Text("NPR \(String(format: "%.0f", viewModel.grossSalesNprToday))")
                            .font(.system(size: 24, weight: .black))
                            .foregroundColor(PartnerKitchenTheme.accentGreen)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                }
                .padding(16)
                .background(PartnerKitchenTheme.cardDark)
                .cornerRadius(16)
                .overlay(
                    RoundedRectangle(cornerRadius: 16)
                        .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
                )
                
                // Finished Tickets List
                LazyVStack(spacing: 10) {
                    ForEach(viewModel.completedOrders) { order in
                        HStack {
                            VStack(alignment: .leading, spacing: 3) {
                                HStack(spacing: 6) {
                                    Text("#\(order.orderNo)")
                                        .font(.system(size: 14, weight: .black))
                                        .foregroundColor(PartnerKitchenTheme.textPrimary)
                                    Text(order.consumerName)
                                        .font(.system(size: 12))
                                        .foregroundColor(PartnerKitchenTheme.textSecondary)
                                }
                                Text("\(order.items.count) item(s) • \(order.paymentMethod.uppercased())")
                                    .font(.system(size: 11))
                                    .foregroundColor(PartnerKitchenTheme.textMuted)
                            }
                            
                            Spacer()
                            
                            VStack(alignment: .trailing, spacing: 3) {
                                Text("NPR \(String(format: "%.0f", order.totalNpr))")
                                    .font(.system(size: 14, weight: .black))
                                    .foregroundColor(PartnerKitchenTheme.textPrimary)
                                
                                Button {
                                    viewModel.preparePrint(order: order, type: .customerBill)
                                } label: {
                                    Text("Reprint")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(PartnerKitchenTheme.primaryOrange)
                                }
                            }
                        }
                        .padding(12)
                        .background(PartnerKitchenTheme.cardDark)
                        .cornerRadius(12)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(PartnerKitchenTheme.cardBorder, lineWidth: 1)
                        )
                    }
                }
            }
            .padding(14)
        }
    }
    
    // MARK: - Empty State Helper
    
    private func emptyKdsState(title: String, subtitle: String, icon: String) -> some View {
        VStack(spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 44))
                .foregroundColor(PartnerKitchenTheme.textMuted)
                .padding(.top, 60)
            
            Text(title)
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(PartnerKitchenTheme.textPrimary)
            
            Text(subtitle)
                .font(.system(size: 13))
                .foregroundColor(PartnerKitchenTheme.textSecondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 32)
        }
        .frame(maxWidth: .infinity)
    }
    
    // MARK: - 1-Tap Role Switcher Sheet
    
    private var roleSwitcherSheet: some View {
        VStack(spacing: 16) {
            Capsule()
                .fill(PartnerKitchenTheme.cardBorder)
                .frame(width: 44, height: 5)
                .padding(.top, 10)
            
            Text("Switch Partner Operation")
                .font(.system(size: 17, weight: .black))
                .foregroundColor(PartnerKitchenTheme.textPrimary)
            
            VStack(spacing: 8) {
                ForEach(PartnerRole.allCases) { role in
                    let isCurrent = (role == .restaurant)
                    Button {
                        showRoleSwitcher = false
                        if !isCurrent {
                            onRoleSwitch(role)
                        }
                    } label: {
                        HStack(spacing: 12) {
                            Text(role.emoji)
                                .font(.system(size: 24))
                                .frame(width: 44, height: 44)
                                .background(role.accentColor.opacity(0.12))
                                .cornerRadius(10)
                            
                            VStack(alignment: .leading, spacing: 2) {
                                Text(role.title)
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(PartnerKitchenTheme.textPrimary)
                                Text(role.subtitle)
                                    .font(.system(size: 11))
                                    .foregroundColor(PartnerKitchenTheme.textSecondary)
                            }
                            
                            Spacer()
                            
                            if isCurrent {
                                Text("ACTIVE")
                                    .font(.system(size: 10, weight: .heavy))
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 3)
                                    .background(PartnerKitchenTheme.primaryOrange.opacity(0.2))
                                    .foregroundColor(PartnerKitchenTheme.primaryOrange)
                                    .cornerRadius(6)
                            }
                        }
                        .padding(10)
                        .background(isCurrent ? PartnerKitchenTheme.primaryOrange.opacity(0.1) : PartnerKitchenTheme.cardDark)
                        .cornerRadius(12)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(isCurrent ? PartnerKitchenTheme.primaryOrange : PartnerKitchenTheme.cardBorder, lineWidth: 1)
                        )
                    }
                }
            }
            .padding(.horizontal, 16)
            
            Spacer()
        }
        .background(PartnerKitchenTheme.bgDark)
    }
}
