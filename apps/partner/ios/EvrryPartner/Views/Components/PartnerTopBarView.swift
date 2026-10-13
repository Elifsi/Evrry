//
//  PartnerTopBarView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Unified Partner Top Bar with 1-Tap Role Switcher
//

import SwiftUI

public struct PartnerTopBarView: View {
    @ObservedObject var viewModel: DriverMobilityViewModel
    @State private var showRoleSwitcherSheet: Bool = false
    @State private var beaconPulsing: Bool = false
    
    public init(viewModel: DriverMobilityViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        VStack(spacing: 0) {
            HStack(alignment: .center, spacing: 10) {
                // 1-TAP ROLE SWITCHER PILL
                Button {
                    showRoleSwitcherSheet = true
                } label: {
                    HStack(spacing: 6) {
                        Text(viewModel.activeRole.emoji)
                            .font(.system(size: 16))
                        VStack(alignment: .leading, spacing: 1) {
                            Text(viewModel.activeRole.title)
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(viewModel.activeRole.accentColor)
                            Text("1-Tap Switch")
                                .font(.system(size: 9))
                                .foregroundColor(.secondary)
                        }
                        Image(systemName: "arrow.left.arrow.right")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(viewModel.activeRole.accentColor)
                    }
                    .padding(.horizontal, 10)
                    .padding(.vertical, 6)
                    .background(viewModel.activeRole.accentColor.opacity(0.12))
                    .cornerRadius(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(viewModel.activeRole.accentColor.opacity(0.35), lineWidth: 1)
                    )
                }
                
                Spacer()
                
                // GO ONLINE / GO OFFLINE SWITCH
                Button {
                    viewModel.toggleOnline(!viewModel.isOnline)
                } label: {
                    HStack(spacing: 6) {
                        Circle()
                            .fill(viewModel.isOnline ? Color.green : Color.gray)
                            .frame(width: 8, height: 8)
                            .opacity(viewModel.isOnline && beaconPulsing ? 0.4 : 1.0)
                        Text(viewModel.isOnline ? "ONLINE" : "OFFLINE")
                            .font(.system(size: 11, weight: .black))
                            .foregroundColor(viewModel.isOnline ? Color(red: 0.08, green: 0.45, blue: 0.22) : .secondary)
                    }
                    .padding(.horizontal, 10)
                    .padding(.vertical, 6)
                    .background(viewModel.isOnline ? Color.green.opacity(0.15) : Color(UIColor.systemGray6))
                    .cornerRadius(12)
                }
                
                // RATING & DAILY EARNINGS PILL
                VStack(alignment: .trailing, spacing: 1) {
                    Text("⭐ 4.95")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundColor(.orange)
                    Text("Rs \(Int(viewModel.todayEarningsNpr))")
                        .font(.system(size: 12, weight: .black))
                        .foregroundColor(Color(red: 0.0, green: 0.30, blue: 0.91))
                }
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(Color(red: 0.94, green: 0.96, blue: 1.0))
                .cornerRadius(10)
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(Color.blue.opacity(0.2), lineWidth: 1)
                )
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
            .background(Color.white)
            
            Divider()
        }
        .onAppear {
            withAnimation(.easeInOut(duration: 0.8).repeatForever(autoreverses: true)) {
                beaconPulsing = true
            }
        }
        .sheet(isPresented: $showRoleSwitcherSheet) {
            RoleSwitcherSheet(
                currentRole: viewModel.activeRole,
                onSelect: { role in
                    viewModel.switchRole(role)
                    showRoleSwitcherSheet = false
                }
            )
            .presentationDetents([.medium])
        }
    }
}

private struct RoleSwitcherSheet: View {
    let currentRole: PartnerRole
    let onSelect: (PartnerRole) -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            Text("Select Active Business Role")
                .font(.system(size: 18, weight: .bold))
            Text("Switch between multiple venture operations in 1 tap without logging out.")
                .font(.system(size: 12))
                .foregroundColor(.secondary)
            
            ForEach(PartnerRole.allCases) { role in
                let isSelected = role == currentRole
                Button {
                    onSelect(role)
                } label: {
                    HStack(spacing: 12) {
                        Text(role.emoji)
                            .font(.system(size: 24))
                            .frame(width: 44, height: 44)
                            .background(role.accentColor.opacity(0.12))
                            .cornerRadius(12)
                        
                        VStack(alignment: .leading, spacing: 2) {
                            HStack {
                                Text(role.title)
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.primary)
                                if isSelected {
                                    Text("ACTIVE")
                                        .font(.system(size: 8, weight: .black))
                                        .padding(.horizontal, 6)
                                        .padding(.vertical, 2)
                                        .background(role.accentColor)
                                        .foregroundColor(.white)
                                        .cornerRadius(6)
                                }
                            }
                            Text(role.subtitle)
                                .font(.system(size: 11))
                                .foregroundColor(.secondary)
                        }
                        
                        Spacer()
                        
                        if isSelected {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(role.accentColor)
                        }
                    }
                    .padding(12)
                    .background(isSelected ? role.accentColor.opacity(0.06) : Color(UIColor.systemGray6).opacity(0.5))
                    .cornerRadius(14)
                    .overlay(
                        RoundedRectangle(cornerRadius: 14)
                            .stroke(isSelected ? role.accentColor : Color.gray.opacity(0.2), lineWidth: isSelected ? 2 : 1)
                    )
                }
            }
            
            Spacer()
        }
        .padding(20)
    }
}
