//
//  RoleSelectionView.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  4-Role Selection & Driver KYC View
//

import SwiftUI

public struct RoleSelectionView: View {
    @ObservedObject var authViewModel: PartnerAuthViewModel
    public let onRoleConfirmed: () -> Void
    
    @State private var selectedRole: PartnerRole = .driver
    
    public init(authViewModel: PartnerAuthViewModel, onRoleConfirmed: @escaping () -> Void) {
        self.authViewModel = authViewModel
        self.onRoleConfirmed = onRoleConfirmed
    }
    
    public var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                if authViewModel.authState == .kycPending {
                    if authViewModel.selectedRole == .restaurant {
                        RestaurantKycContentView(
                            kycData: $authViewModel.restaurantKycData,
                            isLoading: authViewModel.isLoading,
                            onSubmit: {
                                authViewModel.submitRestaurantKyc()
                                onRoleConfirmed()
                            }
                        )
                    } else if authViewModel.selectedRole == .rider {
                        CourierKycContentView(
                            kycData: $authViewModel.courierKycData,
                            isLoading: authViewModel.isLoading,
                            onSubmit: {
                                authViewModel.submitCourierKyc()
                                onRoleConfirmed()
                            }
                        )
                    } else if authViewModel.selectedRole == .grocery {
                        GroceryKycContentView(
                            kycData: $authViewModel.groceryKycData,
                            isLoading: authViewModel.isLoading,
                            onSubmit: {
                                authViewModel.submitGroceryKyc()
                                onRoleConfirmed()
                            }
                        )
                    } else {
                        DriverKycContentView(
                            kycData: $authViewModel.kycData,
                            isLoading: authViewModel.isLoading,
                            onSubmit: {
                                authViewModel.submitDriverKyc()
                                onRoleConfirmed()
                            }
                        )
                    }
                } else {
                    ScrollView {
                        VStack(spacing: 16) {
                            ForEach(PartnerRole.allCases) { role in
                                let isSelected = role == selectedRole
                                Button {
                                    selectedRole = role
                                } label: {
                                    VStack(alignment: .leading, spacing: 10) {
                                        HStack {
                                            Text(role.emoji)
                                                .font(.system(size: 28))
                                                .frame(width: 50, height: 50)
                                                .background(role.accentColor.opacity(0.12))
                                                .cornerRadius(12)
                                            
                                            VStack(alignment: .leading, spacing: 2) {
                                                Text(role.title)
                                                    .font(.system(size: 16, weight: .bold))
                                                    .foregroundColor(.primary)
                                                Text(role.subtitle)
                                                    .font(.system(size: 12, weight: .semibold))
                                                    .foregroundColor(role.accentColor)
                                            }
                                            
                                            Spacer()
                                            
                                            if isSelected {
                                                Image(systemName: "checkmark.circle.fill")
                                                    .foregroundColor(role.accentColor)
                                                    .font(.system(size: 22))
                                            }
                                        }
                                        
                                        Text(role.description)
                                            .font(.system(size: 12))
                                            .foregroundColor(.secondary)
                                            .multilineTextAlignment(.leading)
                                    }
                                    .padding(16)
                                    .background(isSelected ? role.accentColor.opacity(0.06) : Color.white)
                                    .cornerRadius(18)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 18)
                                            .stroke(isSelected ? role.accentColor : Color.gray.opacity(0.2), lineWidth: isSelected ? 2 : 1)
                                    )
                                    .shadow(color: Color.black.opacity(isSelected ? 0.06 : 0.02), radius: 6, y: 3)
                                }
                            }
                            
                            Button {
                                authViewModel.selectRole(selectedRole)
                            } label: {
                                Text("Continue to \(selectedRole.title) KYC 🚀")
                                    .font(.system(size: 15, weight: .bold))
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 14)
                                    .background(selectedRole.accentColor)
                                    .foregroundColor(.white)
                                    .cornerRadius(14)
                            }
                            .padding(.top, 10)
                        }
                        .padding(16)
                    }
                    .background(Color(UIColor.systemGray6).opacity(0.4))
                }
            }
            .navigationTitle(authViewModel.authState == .kycPending ? (authViewModel.selectedRole == .restaurant ? "Restaurant Merchant KYC" : (authViewModel.selectedRole == .rider ? "Courier KYC Verification" : (authViewModel.selectedRole == .grocery ? "Supermarket Retail KYC" : "Driver KYC Verification"))) : "Choose Venture Role")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                if authViewModel.authState == .kycPending {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button("Back") {
                            authViewModel.authState = .roleSelection
                        }
                    }
                }
            }
        }
    }
}

private struct DriverKycContentView: View {
    @Binding var kycData: DriverKycData
    let isLoading: Bool
    let onSubmit: () -> Void
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // VEHICLE TYPE SELECTOR
                Text("VEHICLE CATEGORY")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(.secondary)
                
                HStack(spacing: 8) {
                    ForEach([("bike", "🏍️ Moto"), ("cab", "🚖 Cab"), ("comfort_ev", "⚡ EV")], id: \.0) { vType, title in
                        let isSelected = kycData.vehicleType == vType
                        Button {
                            kycData.vehicleType = vType
                        } label: {
                            Text(title)
                                .font(.system(size: 12, weight: .bold))
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                                .background(isSelected ? Color.blue.opacity(0.12) : Color.white)
                                .foregroundColor(isSelected ? .blue : .primary)
                                .cornerRadius(10)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 10)
                                        .stroke(isSelected ? Color.blue : Color.gray.opacity(0.2), lineWidth: isSelected ? 2 : 1)
                                )
                        }
                    }
                }
                
                // DRIVING LICENSE
                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Text("🪪 Smart Driving License")
                            .font(.system(size: 14, weight: .bold))
                        Spacer()
                    }
                    TextField("License Number (e.g. 01-06-00981245)", text: $kycData.licenseNumber)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    
                    Button {
                        kycData.licensePhotoPath = "kyc/license.jpg"
                    } label: {
                        HStack {
                            Image(systemName: "camera.fill")
                            Text(kycData.licensePhotoPath != nil ? "✓ License Photo Attached" : "Attach License Photo")
                        }
                        .font(.system(size: 12, weight: .bold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(Color.blue.opacity(0.1))
                        .foregroundColor(.blue)
                        .cornerRadius(8)
                    }
                }
                .padding(14)
                .background(Color.white)
                .cornerRadius(14)
                
                // BLUEBOOK
                VStack(alignment: .leading, spacing: 8) {
                    Text("📘 Vehicle Bluebook (Yatayat)")
                        .font(.system(size: 14, weight: .bold))
                    TextField("Bluebook Registration No.", text: $kycData.bluebookNumber)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    
                    Button {
                        kycData.bluebookPhotoPath = "kyc/bluebook.jpg"
                    } label: {
                        HStack {
                            Image(systemName: "doc.fill")
                            Text(kycData.bluebookPhotoPath != nil ? "✓ Bluebook Scan Attached" : "Attach Bluebook Tax Valid Page")
                        }
                        .font(.system(size: 12, weight: .bold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(Color.blue.opacity(0.1))
                        .foregroundColor(.blue)
                        .cornerRadius(8)
                    }
                }
                .padding(14)
                .background(Color.white)
                .cornerRadius(14)
                
                // VEHICLE PLATE & PHOTO
                VStack(alignment: .leading, spacing: 8) {
                    Text("🚗 Vehicle Plate & Model")
                        .font(.system(size: 14, weight: .bold))
                    HStack {
                        TextField("BA 2 PA 4920", text: $kycData.vehiclePlate)
                            .padding(10)
                            .background(Color(UIColor.systemGray6).opacity(0.5))
                            .cornerRadius(8)
                        TextField("Bajaj Pulsar 150", text: $kycData.vehicleModel)
                            .padding(10)
                            .background(Color(UIColor.systemGray6).opacity(0.5))
                            .cornerRadius(8)
                    }
                    
                    Button {
                        kycData.vehiclePhotoPath = "kyc/vehicle.jpg"
                    } label: {
                        HStack {
                            Image(systemName: "car.fill")
                            Text(kycData.vehiclePhotoPath != nil ? "✓ Vehicle Photo Attached" : "Take Vehicle Photo")
                        }
                        .font(.system(size: 12, weight: .bold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(Color.blue.opacity(0.1))
                        .foregroundColor(.blue)
                        .cornerRadius(8)
                    }
                }
                .padding(14)
                .background(Color.white)
                .cornerRadius(14)
                
                Button(action: onSubmit) {
                    HStack {
                        if isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Text("Complete KYC & Go Online 🚀")
                                .font(.system(size: 15, weight: .bold))
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Color.green)
                    .foregroundColor(.white)
                    .cornerRadius(14)
                }
                .disabled(isLoading || kycData.licenseNumber.isEmpty || kycData.vehiclePlate.isEmpty)
            }
            .padding(16)
        }
        .background(Color(UIColor.systemGray6).opacity(0.4))
    }
}

public struct RestaurantKycContentView: View {
    @Binding var kycData: RestaurantKycData
    let isLoading: Bool
    let onSubmit: () -> Void
    
    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // RESTAURANT IDENTITY
                VStack(alignment: .leading, spacing: 8) {
                    Text("🍳 Restaurant Information")
                        .font(.system(size: 14, weight: .bold))
                    TextField("Restaurant Trade Name", text: $kycData.restaurantName)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    TextField("Owner Legal Name", text: $kycData.ownerName)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    HStack {
                        TextField("Street Address (e.g. Jhamsikhel)", text: $kycData.storeAddress)
                            .padding(10)
                            .background(Color(UIColor.systemGray6).opacity(0.5))
                            .cornerRadius(8)
                        TextField("Ward No", value: $kycData.wardNumber, formatter: NumberFormatter())
                            .frame(width: 80)
                            .padding(10)
                            .background(Color(UIColor.systemGray6).opacity(0.5))
                            .cornerRadius(8)
                    }
                }
                .padding(14)
                .background(Color.white)
                .cornerRadius(14)
                
                // BUSINESS REGISTRATION
                VStack(alignment: .leading, spacing: 8) {
                    Text("🏢 Business Registration (Company Registrar / Ward)")
                        .font(.system(size: 14, weight: .bold))
                    TextField("Registration No. (e.g. REG-2081-FD-9982)", text: $kycData.businessRegistrationNo)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    
                    Button {
                        kycData.businessRegDocUploaded = true
                    } label: {
                        HStack {
                            Image(systemName: "doc.fill")
                            Text(kycData.businessRegDocUploaded ? "✓ Business Certificate Attached" : "Attach Business Certificate")
                        }
                        .font(.system(size: 12, weight: .bold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(Color.orange.opacity(0.1))
                        .foregroundColor(.orange)
                        .cornerRadius(8)
                    }
                }
                .padding(14)
                .background(Color.white)
                .cornerRadius(14)
                
                // PAN / VAT
                VStack(alignment: .leading, spacing: 8) {
                    Text("🧾 PAN / VAT Tax Registration")
                        .font(.system(size: 14, weight: .bold))
                    TextField("9-Digit PAN / VAT (IRD Nepal)", text: $kycData.panVatNumber)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    
                    Button {
                        kycData.panVatDocUploaded = true
                    } label: {
                        HStack {
                            Image(systemName: "doc.text.fill")
                            Text(kycData.panVatDocUploaded ? "✓ PAN/VAT Certificate Attached" : "Attach PAN/VAT Scan")
                        }
                        .font(.system(size: 12, weight: .bold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(Color.orange.opacity(0.1))
                        .foregroundColor(.orange)
                        .cornerRadius(8)
                    }
                }
                .padding(14)
                .background(Color.white)
                .cornerRadius(14)
                
                // FOOD HYGIENE PERMIT
                VStack(alignment: .leading, spacing: 8) {
                    Text("🥗 Food Hygiene & Safety Permit (DFTQC)")
                        .font(.system(size: 14, weight: .bold))
                    TextField("DFTQC Hygiene Permit No.", text: $kycData.foodHygienePermitNo)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    
                    Button {
                        kycData.foodHygieneDocUploaded = true
                    } label: {
                        HStack {
                            Image(systemName: "checkmark.seal.fill")
                            Text(kycData.foodHygieneDocUploaded ? "✓ Hygiene Permit Attached" : "Attach DFTQC Permit")
                        }
                        .font(.system(size: 12, weight: .bold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(Color.orange.opacity(0.1))
                        .foregroundColor(.orange)
                        .cornerRadius(8)
                    }
                }
                .padding(14)
                .background(Color.white)
                .cornerRadius(14)
                
                // SUBMIT BUTTON
                Button(action: onSubmit) {
                    HStack {
                        if isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Text("Launch Kitchen KDS 🍳")
                                .font(.system(size: 15, weight: .bold))
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Color(red: 0.98, green: 0.45, blue: 0.09))
                    .foregroundColor(.white)
                    .cornerRadius(14)
                }
                .disabled(isLoading || kycData.businessRegistrationNo.isEmpty || kycData.panVatNumber.isEmpty)
            }
            .padding(16)
        }
        .background(Color(UIColor.systemGray6).opacity(0.4))
    }
}

public struct CourierKycContentView: View {
    @Binding var kycData: CourierKycData
    let isLoading: Bool
    let onSubmit: () -> Void
    
    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // VEHICLE TYPE SELECTOR
                Text("DELIVERY VEHICLE CATEGORY")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(.secondary)
                
                HStack(spacing: 8) {
                    ForEach([("scooter", "🛵 Scooter"), ("bike", "🏍️ Motorbike"), ("ev_scooter", "⚡ Electric")], id: \.0) { vType, title in
                        let isSelected = kycData.vehicleType == vType
                        Button {
                            kycData.vehicleType = vType
                        } label: {
                            Text(title)
                                .font(.system(size: 12, weight: .bold))
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                                .background(isSelected ? Color.purple.opacity(0.12) : Color.white)
                                .foregroundColor(isSelected ? .purple : .primary)
                                .cornerRadius(10)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 10)
                                        .stroke(isSelected ? Color.purple : Color.gray.opacity(0.2), lineWidth: isSelected ? 2 : 1)
                                )
                        }
                    }
                }
                
                // DRIVING LICENSE
                VStack(alignment: .leading, spacing: 8) {
                    Text("🪪 Smart Driving License (DoTM Nepal)")
                        .font(.system(size: 14, weight: .bold))
                    TextField("License Number (e.g. 01-06-00981245)", text: $kycData.licenseNumber)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    
                    Button {
                        kycData.licensePhotoPath = "kyc/courier_license.jpg"
                    } label: {
                        HStack {
                            Image(systemName: "camera.fill")
                            Text(kycData.licensePhotoPath != nil ? "✓ License Photo Attached" : "Attach Driver License Photo")
                        }
                        .font(.system(size: 12, weight: .bold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(Color.purple.opacity(0.1))
                        .foregroundColor(.purple)
                        .cornerRadius(8)
                    }
                }
                .padding(14)
                .background(Color.white)
                .cornerRadius(14)
                
                // BLUEBOOK
                VStack(alignment: .leading, spacing: 8) {
                    Text("📘 Vehicle Bluebook (Tax Paid Receipt)")
                        .font(.system(size: 14, weight: .bold))
                    TextField("Bluebook Registration No.", text: $kycData.bluebookNumber)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    
                    Button {
                        kycData.bluebookPhotoPath = "kyc/courier_bluebook.jpg"
                    } label: {
                        HStack {
                            Image(systemName: "doc.fill")
                            Text(kycData.bluebookPhotoPath != nil ? "✓ Bluebook Attached" : "Attach Bluebook Tax Valid Page")
                        }
                        .font(.system(size: 12, weight: .bold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(Color.purple.opacity(0.1))
                        .foregroundColor(.purple)
                        .cornerRadius(8)
                    }
                }
                .padding(14)
                .background(Color.white)
                .cornerRadius(14)
                
                // VEHICLE PLATE & PHOTO
                VStack(alignment: .leading, spacing: 8) {
                    Text("🛵 Vehicle Registration Plate & Photo")
                        .font(.system(size: 14, weight: .bold))
                    TextField("Vehicle Plate (e.g. BA 88 PA 4321)", text: $kycData.vehiclePlate)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    
                    Button {
                        kycData.vehiclePhotoPath = "kyc/courier_vehicle.jpg"
                    } label: {
                        HStack {
                            Image(systemName: "camera.fill")
                            Text(kycData.vehiclePhotoPath != nil ? "✓ Vehicle Photo Attached" : "Take Number Plate Photo")
                        }
                        .font(.system(size: 12, weight: .bold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(Color.purple.opacity(0.1))
                        .foregroundColor(.purple)
                        .cornerRadius(8)
                    }
                }
                .padding(14)
                .background(Color.white)
                .cornerRadius(14)
                
                // SUBMIT BUTTON
                Button(action: onSubmit) {
                    HStack {
                        if isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Text("Complete KYC & Start Delivering 🛵")
                                .font(.system(size: 15, weight: .bold))
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Color.purple)
                    .foregroundColor(.white)
                    .cornerRadius(14)
                }
                .disabled(isLoading || kycData.licenseNumber.isEmpty || kycData.vehiclePlate.isEmpty)
            }
            .padding(16)
        }
        .background(Color(UIColor.systemGray6).opacity(0.4))
    }
}

public struct GroceryKycContentView: View {
    @Binding var kycData: GroceryKycData
    let isLoading: Bool
    let onSubmit: () -> Void
    
    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // STORE IDENTITY
                VStack(alignment: .leading, spacing: 8) {
                    Text("🛒 Supermarket Retail Details")
                        .font(.system(size: 14, weight: .bold))
                    TextField("Store Trade Name", text: $kycData.tradeName)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    TextField("Registered Legal Entity Name", text: $kycData.legalName)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    HStack {
                        TextField("Store Location (e.g. Pulchowk)", text: $kycData.storeAddress)
                            .padding(10)
                            .background(Color(UIColor.systemGray6).opacity(0.5))
                            .cornerRadius(8)
                        TextField("Ward", text: $kycData.wardNo)
                            .frame(width: 80)
                            .padding(10)
                            .background(Color(UIColor.systemGray6).opacity(0.5))
                            .cornerRadius(8)
                    }
                }
                .padding(14)
                .background(Color.white)
                .cornerRadius(14)
                
                // STATUTORY REGISTRATION
                VStack(alignment: .leading, spacing: 8) {
                    Text("📋 Statutory Registration (DoCI & IRD Nepal)")
                        .font(.system(size: 14, weight: .bold))
                    TextField("Company / Business Reg. No (BRN)", text: $kycData.businessRegistrationNo)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                    TextField("PAN / VAT Registration No (9-digit)", text: $kycData.panVatNumber)
                        .keyboardType(.numberPad)
                        .padding(10)
                        .background(Color(UIColor.systemGray6).opacity(0.5))
                        .cornerRadius(8)
                }
                .padding(14)
                .background(Color.white)
                .cornerRadius(14)
                
                // SUBMIT BUTTON
                Button(action: onSubmit) {
                    HStack {
                        if isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Text("Complete Retail KYC & Launch Supermarket 🛒")
                                .font(.system(size: 15, weight: .bold))
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(14)
                }
                .disabled(isLoading || kycData.tradeName.isEmpty || kycData.businessRegistrationNo.isEmpty)
            }
            .padding(16)
        }
        .background(Color(UIColor.systemGray6).opacity(0.4))
    }
}

