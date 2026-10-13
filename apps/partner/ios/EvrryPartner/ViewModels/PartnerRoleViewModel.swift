//
//  PartnerRoleViewModel.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  Unified Partner Role Switching & Retail KYC State Machine
//

import Foundation
import Combine

public class PartnerRoleViewModel: ObservableObject {
    @Published public var currentRole: PartnerRole = .grocery
    @Published public var kycState: PartnerKycState = PartnerKycState()
    @Published public var partnerProfile: PartnerProfile = PartnerProfile()
    
    public init() {}
    
    public func switchRole(_ newRole: PartnerRole) {
        currentRole = newRole
        partnerProfile.type = newRole
    }
    
    public func submitRetailKyc(
        legalName: String,
        tradeName: String,
        panNumber: String,
        businessRegNo: String,
        storeAddress: String,
        wardNo: Int
    ) {
        kycState = PartnerKycState(
            businessRegistrationNo: businessRegNo,
            panVatNumber: panNumber,
            tradeName: tradeName,
            legalName: legalName,
            storeAddress: storeAddress,
            wardNo: wardNo,
            businessRegDocUploaded: true,
            panVatDocUploaded: true,
            kycStatus: "verified",
            isVerified: true
        )
        
        partnerProfile.legalName = legalName
        partnerProfile.tradeName = tradeName
        partnerProfile.type = .grocery
        partnerProfile.status = "active"
        
        currentRole = .grocery
    }
}
