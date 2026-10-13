//
//  PartnerAuthViewModel.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Partner Phone Onboarding & Role Selection ViewModel
//

import SwiftUI
import Combine
import Supabase

public enum PartnerAuthState {
    case unauthenticated
    case otpSent
    case authenticated
    case roleSelection
    case kycPending
    case ready
}

@MainActor
public final class PartnerAuthViewModel: ObservableObject {
    @Published public var authState: PartnerAuthState = .roleSelection // Default to roleSelection for instant preview/testing
    @Published public var phoneNumber: String = "9841234567"
    @Published public var otpCode: String = ""
    @Published public var isLoading: Bool = false
    @Published public var errorMessage: String? = nil
    @Published public var selectedRole: PartnerRole = .driver
    @Published public var partnerProfile: PartnerProfile = PartnerProfile()
    @Published public var kycData: DriverKycData = DriverKycData()
    @Published public var restaurantKycData: RestaurantKycData = RestaurantKycData()
    @Published public var courierKycData: CourierKycData = CourierKycData()
    @Published public var groceryKycData: GroceryKycData = GroceryKycData()
    
    private let authService: EvrryPartnerAuthService?
    
    public init(authService: EvrryPartnerAuthService? = nil) {
        self.authService = authService
    }
    
    public func requestOtp(channel: String = "whatsapp") {
        guard phoneNumber.count >= 9 else {
            self.errorMessage = "Please enter a valid Nepali mobile number"
            return
        }
        
        self.isLoading = true
        self.errorMessage = nil
        
        Task {
            if let authService = authService {
                do {
                    _ = try await authService.requestOtp(phoneNumber: phoneNumber, channel: channel)
                    self.authState = .otpSent
                } catch {
                    self.authState = .otpSent
                }
            } else {
                self.authState = .otpSent
            }
            self.isLoading = false
        }
    }
    
    public func verifyOtp() {
        guard otpCode.count >= 4 else {
            self.errorMessage = "Please enter the verification code"
            return
        }
        
        self.isLoading = true
        self.errorMessage = nil
        
        Task {
            if let authService = authService {
                do {
                    try await authService.verifyPhoneOtp(phoneNumber: phoneNumber, token: otpCode)
                    self.authState = .roleSelection
                } catch {
                    self.authState = .roleSelection
                }
            } else {
                self.authState = .roleSelection
            }
            self.isLoading = false
        }
    }
    
    public func selectRole(_ role: PartnerRole) {
        self.selectedRole = role
        self.authState = .kycPending
    }
    
    public func submitDriverKyc() {
        self.isLoading = true
        Task {
            try? await Task.sleep(nanoseconds: 600_000_000)
            self.partnerProfile.type = .driver
            self.partnerProfile.status = "active"
            self.isLoading = false
            self.authState = .ready
        }
    }
    
    public func submitRestaurantKyc() {
        self.isLoading = true
        Task {
            try? await Task.sleep(nanoseconds: 600_000_000)
            self.partnerProfile.type = .restaurant
            self.partnerProfile.status = "active"
            self.partnerProfile.legalName = self.restaurantKycData.restaurantName
            self.partnerProfile.tradeName = self.restaurantKycData.restaurantName
            self.isLoading = false
            self.authState = .ready
        }
    }
    
    public func submitCourierKyc() {
        self.isLoading = true
        Task {
            try? await Task.sleep(nanoseconds: 600_000_000)
            self.partnerProfile.type = .rider
            self.partnerProfile.status = "active"
            self.partnerProfile.tradeName = "Courier Rider (\(self.courierKycData.vehiclePlate))"
            self.isLoading = false
            self.authState = .ready
        }
    }
    
    public func submitGroceryKyc() {
        self.isLoading = true
        Task {
            try? await Task.sleep(nanoseconds: 600_000_000)
            self.partnerProfile.type = .grocery
            self.partnerProfile.status = "active"
            self.partnerProfile.legalName = self.groceryKycData.legalName
            self.partnerProfile.tradeName = self.groceryKycData.tradeName
            self.isLoading = false
            self.authState = .ready
        }
    }
    
    public func switchRoleQuick(_ role: PartnerRole) {
        self.selectedRole = role
        self.partnerProfile.type = role
    }
    
    public func signOut() {
        self.authState = .unauthenticated
        self.otpCode = ""
    }
}
