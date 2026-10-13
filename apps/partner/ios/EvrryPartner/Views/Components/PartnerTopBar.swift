//
//  PartnerTopBar.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Top Bar with 1-Tap Partner Role Switcher
//

import SwiftUI

public struct PartnerTopBar: View {
    @ObservedObject public var roleViewModel: PartnerRoleViewModel
    public var storeName: String
    
    public init(
        roleViewModel: PartnerRoleViewModel,
        storeName: String = "Bhatbhateni Supermarket • Pulchowk"
    ) {
        self.roleViewModel = roleViewModel
        self.storeName = storeName
    }
    
    public var body: some View {
        VStack(spacing: 8) {
            // Identity Header
            HStack {
                HStack(spacing: 8) {
                    ZStack {
                        Circle()
                            .fill(Color(red: 0.0, green: 0.37, blue: 1.0))
                            .frame(width: 32, height: 32)
                        Text("E")
                            .font(.system(size: 16, weight: .black))
                            .foregroundColor(.white)
                    }
                    
                    VStack(alignment: .leading, spacing: 2) {
                        HStack(spacing: 6) {
                            Text("evrry")
                                .font(.system(size: 16, weight: .black))
                                .foregroundColor(Color(red: 0.05, green: 0.09, blue: 0.16))
                            
                            Text("PARTNER")
                                .font(.system(size: 9, weight: .bold))
                                .foregroundColor(Color(red: 0.02, green: 0.59, blue: 0.41))
                                .padding(.horizontal, 4)
                                .padding(.vertical, 2)
                                .background(Color(red: 0.92, green: 0.99, blue: 0.96))
                                .cornerRadius(4)
                        }
                        
                        Text(storeName)
                            .font(.system(size: 11, weight: .medium))
                            .foregroundColor(Color(red: 0.39, green: 0.45, blue: 0.55))
                    }
                }
                
                Spacer()
                
                // Live Status Badge
                HStack(spacing: 4) {
                    Circle()
                        .fill(Color(red: 0.09, green: 0.64, blue: 0.29))
                        .frame(width: 6, height: 6)
                    Text("Live Mart")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundColor(Color(red: 0.05, green: 0.09, blue: 0.16))
                }
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(Color(red: 0.95, green: 0.96, blue: 0.98))
                .cornerRadius(12)
            }
            .padding(.horizontal, 16)
            .padding(.top, 8)
            
            // 1-TAP ROLE SWITCHER ROW
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(PartnerRole.allCases) { role in
                        let isSelected = (role == roleViewModel.currentRole)
                        Button(action: {
                            roleViewModel.switchRole(role)
                        }) {
                            HStack(spacing: 6) {
                                Text(role.emoji)
                                    .font(.system(size: 13))
                                Text(role.title)
                                    .font(.system(size: 11, weight: isSelected ? .bold : .medium))
                                    .foregroundColor(isSelected ? .white : Color(red: 0.06, green: 0.09, blue: 0.16))
                            }
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                            .background(isSelected ? Color(red: 0.0, green: 0.37, blue: 1.0) : Color.white)
                            .cornerRadius(10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(isSelected ? Color.clear : Color(red: 0.89, green: 0.91, blue: 0.94), lineWidth: 1)
                            )
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 4)
            }
        }
        .background(Color.white)
        .shadow(color: Color.black.opacity(0.04), radius: 3, x: 0, y: 2)
    }
}
