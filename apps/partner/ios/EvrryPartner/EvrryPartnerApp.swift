//
//  EvrryPartnerApp.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Native Unified Partner iOS Application
//

import SwiftUI

@main
struct EvrryPartnerApp: App {
    @StateObject private var authViewModel = PartnerAuthViewModel()
    @StateObject private var driverViewModel = DriverMobilityViewModel()
    
    var body: some Scene {
        WindowGroup {
            PartnerMainCoordinatorView(
                authViewModel: authViewModel,
                driverViewModel: driverViewModel
            )
        }
    }
}

public struct PartnerMainCoordinatorView: View {
    @ObservedObject var authViewModel: PartnerAuthViewModel
    @ObservedObject var driverViewModel: DriverMobilityViewModel
    @StateObject private var roleViewModel = PartnerRoleViewModel()
    @State private var showRoleSwitcherSheet: Bool = false
    
    public init(
        authViewModel: PartnerAuthViewModel,
        driverViewModel: DriverMobilityViewModel
    ) {
        self.authViewModel = authViewModel
        self.driverViewModel = driverViewModel
    }
    
    public var body: some View {
        Group {
            switch authViewModel.authState {
            case .unauthenticated, .otpSent:
                PartnerOnboardingView(authViewModel: authViewModel)
            case .roleSelection, .kycPending:
                RoleSelectionView(
                    authViewModel: authViewModel,
                    onRoleConfirmed: {
                        authViewModel.switchRoleQuick(authViewModel.selectedRole)
                        driverViewModel.switchRole(authViewModel.selectedRole)
                        roleViewModel.switchRole(authViewModel.selectedRole)
                    }
                )
            case .authenticated, .ready:
                let activeRole = roleViewModel.currentRole
                if activeRole == .driver {
                    DriverHudView(
                        viewModel: driverViewModel,
                        onRoleSelected: { newRole in
                            authViewModel.switchRoleQuick(newRole)
                            driverViewModel.switchRole(newRole)
                            roleViewModel.switchRole(newRole)
                        }
                    )
                } else if activeRole == .restaurant {
                    KitchenDisplayView(
                        onRoleSwitch: { newRole in
                            authViewModel.switchRoleQuick(newRole)
                            driverViewModel.switchRole(newRole)
                            roleViewModel.switchRole(newRole)
                        }
                    )
                } else if activeRole == .grocery {
                    GrocerySupermarketHomeView(
                        roleViewModel: roleViewModel
                    )
                } else if activeRole == .rider {
                    CourierDashboardView(
                        onRoleSwitcherClick: {
                            showRoleSwitcherSheet = true
                        }
                    )
                } else {
                    OtherRoleFallbackView(
                        role: activeRole,
                        onSwitchToDriver: {
                            authViewModel.switchRoleQuick(.driver)
                            driverViewModel.switchRole(.driver)
                            roleViewModel.switchRole(.driver)
                        }
                    )
                }
            }
        }
        .sheet(isPresented: $showRoleSwitcherSheet) {
            VStack(alignment: .leading, spacing: 16) {
                HStack {
                    Text("Switch Operational Venture")
                        .font(.system(size: 18, weight: .black))
                    Spacer()
                    Button("Done") { showRoleSwitcherSheet = false }
                        .font(.system(size: 14, weight: .bold))
                }
                .padding(.horizontal, 20)
                .padding(.top, 20)
                
                VStack(spacing: 10) {
                    ForEach(PartnerRole.allCases) { role in
                        let isCurrent = (role == roleViewModel.currentRole)
                        Button {
                            showRoleSwitcherSheet = false
                            if !isCurrent {
                                authViewModel.switchRoleQuick(role)
                                driverViewModel.switchRole(role)
                                roleViewModel.switchRole(role)
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
                                        .foregroundColor(.primary)
                                    Text(role.subtitle)
                                        .font(.system(size: 11))
                                        .foregroundColor(role.accentColor)
                                }
                                
                                Spacer()
                                
                                if isCurrent {
                                    Image(systemName: "checkmark.circle.fill")
                                        .foregroundColor(role.accentColor)
                                }
                            }
                            .padding(12)
                            .background(isCurrent ? role.accentColor.opacity(0.08) : Color(UIColor.systemGray6))
                            .cornerRadius(14)
                        }
                    }
                }
                .padding(.horizontal, 20)
                Spacer()
            }
            .presentationDetents([.fraction(0.48)])
        }
    }
}

private struct OtherRoleFallbackView: View {
    let role: PartnerRole
    let onSwitchToDriver: () -> Void
    
    var body: some View {
        VStack(spacing: 16) {
            Spacer()
            Text(role.emoji)
                .font(.system(size: 48))
                .frame(width: 80, height: 80)
                .background(role.accentColor.opacity(0.15))
                .cornerRadius(20)
            
            Text(role.title)
                .font(.system(size: 22, weight: .black))
            Text(role.subtitle)
                .font(.system(size: 13, weight: .semibold))
                .foregroundColor(role.accentColor)
            
            Text(role.description)
                .font(.system(size: 12))
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 24)
            
            Button(action: onSwitchToDriver) {
                Text("Switch to Ride Driver Role 🚗")
                    .font(.system(size: 14, weight: .bold))
                    .padding(.horizontal, 20)
                    .padding(.vertical, 12)
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(12)
            }
            .padding(.top, 16)
            
            Spacer()
        }
        .padding(24)
    }
}
