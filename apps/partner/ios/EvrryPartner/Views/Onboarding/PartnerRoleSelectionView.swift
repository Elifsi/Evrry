//
//  PartnerRoleSelectionView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Onboarding Role Selection & Retail KYC Screen
//

import SwiftUI

public struct PartnerRoleSelectionView: View {
    @ObservedObject public var roleViewModel: PartnerRoleViewModel
    public var onRoleConfirmed: () -> Void
    
    @State private var showRetailKycSheet: Bool = false
    @State private var tradeName: String = "Bhatbhateni Supermarket"
    @State private var legalName: String = "Bhat-Bhateni Supermarket & Departmental Store Pvt. Ltd."
    @State private var panNumber: String = "609823145"
    @State private var brnNumber: String = "BRN-2081-99812"
    @State private var storeAddress: String = "Pulchowk, Lalitpur"
    @State private var wardNo: String = "3"
    
    public init(
        roleViewModel: PartnerRoleViewModel,
        onRoleConfirmed: @escaping () -> Void
    ) {
        self.roleViewModel = roleViewModel
        self.onRoleConfirmed = onRoleConfirmed
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            VStack(alignment: .leading, spacing: 4) {
                Text("Welcome to Evrry Partner")
                    .font(.system(size: 22, weight: .black))
                    .foregroundColor(Color(red: 0.05, green: 0.11, blue: 0.22))
                
                Text("Select your operational business role. You can switch between active ventures anytime.")
                    .font(.system(size: 13))
                    .foregroundColor(Color(red: 0.39, green: 0.45, blue: 0.55))
            }
            .padding(.horizontal, 16)
            .padding(.top, 16)
            
            ScrollView {
                VStack(spacing: 12) {
                    ForEach(PartnerRole.allCases) { role in
                        let isSelected = (role == roleViewModel.currentRole)
                        Button(action: {
                            roleViewModel.switchRole(role)
                            if role == .grocery {
                                showRetailKycSheet = true
                            }
                        }) {
                            HStack(spacing: 14) {
                                ZStack {
                                    RoundedRectangle(cornerRadius: 12)
                                        .fill(isSelected ? Color(red: 0.0, green: 0.37, blue: 1.0).opacity(0.1) : Color(red: 0.97, green: 0.98, blue: 0.99))
                                        .frame(width: 48, height: 48)
                                    Text(role.emoji)
                                        .font(.system(size: 22))
                                }
                                
                                VStack(alignment: .leading, spacing: 2) {
                                    HStack(spacing: 6) {
                                        Text(role.title)
                                            .font(.system(size: 15, weight: .bold))
                                            .foregroundColor(Color(red: 0.06, green: 0.09, blue: 0.16))
                                        
                                        Text(role.badge)
                                            .font(.system(size: 9, weight: .bold))
                                            .foregroundColor(role == .grocery ? Color(red: 0.02, green: 0.59, blue: 0.41) : Color(red: 0.39, green: 0.45, blue: 0.55))
                                            .padding(.horizontal, 6)
                                            .padding(.vertical, 2)
                                            .background(role == .grocery ? Color(red: 0.92, green: 0.99, blue: 0.96) : Color(red: 0.95, green: 0.96, blue: 0.98))
                                            .cornerRadius(4)
                                    }
                                    
                                    Text(role.subtitle)
                                        .font(.system(size: 12))
                                        .foregroundColor(Color(red: 0.39, green: 0.45, blue: 0.55))
                                }
                                
                                Spacer()
                                
                                Image(systemName: isSelected ? "largecircle.fill.circle" : "circle")
                                    .foregroundColor(isSelected ? Color(red: 0.0, green: 0.37, blue: 1.0) : Color.gray)
                                    .font(.system(size: 20))
                            }
                            .padding(14)
                            .background(Color.white)
                            .cornerRadius(14)
                            .overlay(
                                RoundedRectangle(cornerRadius: 14)
                                    .stroke(isSelected ? Color(red: 0.0, green: 0.37, blue: 1.0) : Color(red: 0.89, green: 0.91, blue: 0.94), lineWidth: isSelected ? 2 : 1)
                            )
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .padding(.horizontal, 16)
            }
            
            Button(action: onRoleConfirmed) {
                Text("Continue as \(roleViewModel.currentRole.title)")
                    .font(.system(size: 15, weight: .bold))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color(red: 0.0, green: 0.37, blue: 1.0))
                    .cornerRadius(12)
            }
            .padding(16)
        }
        .background(Color(red: 0.97, green: 0.98, blue: 0.99).edgesIgnoringSafeArea(.all))
        .sheet(isPresented: $showRetailKycSheet) {
            NavigationView {
                Form {
                    Section(header: Text("Retail Mart Information")) {
                        TextField("Store Trade Name", text: $tradeName)
                        TextField("Legal Entity Name", text: $legalName)
                        TextField("PAN / VAT Certificate No.", text: $panNumber)
                        TextField("Business Registration No. (BRN)", text: $brnNumber)
                    }
                    
                    Section(header: Text("Location")) {
                        TextField("Store Address", text: $storeAddress)
                        TextField("Ward No.", text: $wardNo)
                    }
                    
                    Section {
                        HStack(spacing: 8) {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(Color(red: 0.02, green: 0.59, blue: 0.41))
                            Text("BRN & PAN Documents Verified")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(Color(red: 0.02, green: 0.59, blue: 0.41))
                        }
                    }
                }
                .navigationBarTitle("Retail Supermarket KYC", displayMode: .inline)
                .navigationBarItems(
                    leading: Button("Cancel") { showRetailKycSheet = false },
                    trailing: Button("Confirm KYC") {
                        roleViewModel.submitRetailKyc(
                            legalName: legalName,
                            tradeName: tradeName,
                            panNumber: panNumber,
                            businessRegNo: brnNumber,
                            storeAddress: storeAddress,
                            wardNo: Int(wardNo) ?? 3
                        )
                        showRetailKycSheet = false
                    }
                    .font(.system(size: 14, weight: .bold))
                )
            }
        }
    }
}
