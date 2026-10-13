package com.elifsi.evrry.partner.viewmodel

import androidx.lifecycle.ViewModel
import com.elifsi.evrry.partner.model.PartnerKycState
import com.elifsi.evrry.partner.model.PartnerProfile
import com.elifsi.evrry.partner.model.PartnerRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Unified Partner Role & KYC ViewModel
 * Manages runtime role switching across Driver, Restaurant, Grocery, and Rider,
 * as well as Supermarket Retail KYC registration.
 * Elifsi Technologies Private Limited
 */
class PartnerRoleViewModel : ViewModel() {

    private val _currentRole = MutableStateFlow(PartnerRole.GROCERY)
    val currentRole: StateFlow<PartnerRole> = _currentRole.asStateFlow()

    private val _kycState = MutableStateFlow(PartnerKycState())
    val kycState: StateFlow<PartnerKycState> = _kycState.asStateFlow()

    private val _partnerProfile = MutableStateFlow(PartnerProfile())
    val partnerProfile: StateFlow<PartnerProfile> = _partnerProfile.asStateFlow()

    fun switchRole(newRole: PartnerRole) {
        _currentRole.value = newRole
        _partnerProfile.value = _partnerProfile.value.copy(type = newRole)
    }

    fun submitRetailKyc(
        legalName: String,
        tradeName: String,
        panNumber: String,
        businessRegNo: String,
        storeAddress: String,
        wardNo: Int
    ) {
        _kycState.value = _kycState.value.copy(
            legalName = legalName,
            tradeName = tradeName,
            panVatNumber = panNumber,
            businessRegistrationNo = businessRegNo,
            storeAddress = storeAddress,
            wardNo = wardNo,
            businessRegDocUploaded = true,
            panVatDocUploaded = true,
            kycStatus = "verified",
            isVerified = true
        )

        _partnerProfile.value = _partnerProfile.value.copy(
            legalName = legalName,
            tradeName = tradeName,
            type = PartnerRole.GROCERY,
            status = "active"
        )
        _currentRole.value = PartnerRole.GROCERY
    }

    fun submitCourierKyc(
        licenseNumber: String = "01-06-00981245",
        licenseExpiry: String = "2029-05-14",
        bluebookNumber: String = "BB-KTM-88129",
        vehiclePlate: String = "BA 88 PA 4321",
        vehicleType: String = "scooter",
        vehicleModel: String = "Honda Dio 110"
    ) {
        _kycState.value = _kycState.value.copy(
            licenseNumber = licenseNumber,
            licenseExpiry = licenseExpiry,
            bluebookNumber = bluebookNumber,
            vehiclePlate = vehiclePlate,
            vehicleType = vehicleType,
            vehicleModel = vehicleModel,
            drivingLicenseDocUploaded = true,
            vehicleBluebookDocUploaded = true,
            vehiclePhotoUploaded = true,
            kycStatus = "verified",
            isVerified = true
        )

        _partnerProfile.value = _partnerProfile.value.copy(
            businessName = "Evrry Express Courier",
            type = PartnerRole.RIDER,
            status = "active"
        )
        _currentRole.value = PartnerRole.RIDER
    }
}
