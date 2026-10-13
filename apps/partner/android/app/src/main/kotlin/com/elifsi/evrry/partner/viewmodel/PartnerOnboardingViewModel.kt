package com.elifsi.evrry.partner.viewmodel

import androidx.lifecycle.ViewModel
import com.elifsi.evrry.partner.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class PartnerOnboardingAuthState {
    PHONE_INPUT,
    OTP_VERIFY,
    ROLE_SELECTION,
    KYC_UPLOAD,
    ACTIVE_DASHBOARD
}

/**
 * ViewModel for Unified Partner Onboarding, Merchant KYC & 1-Tap Role Switcher
 * Platform: Android (Kotlin + Jetpack Compose)
 * Elifsi Technologies Private Limited
 */
class PartnerOnboardingViewModel @Inject constructor() : ViewModel() {

    private val _authState = MutableStateFlow(PartnerOnboardingAuthState.ACTIVE_DASHBOARD)
    val authState: StateFlow<PartnerOnboardingAuthState> = _authState.asStateFlow()

    private val _phoneNumber = MutableStateFlow("+977-9841234567")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _activeRole = MutableStateFlow(PartnerRole.RESTAURANT)
    val activeRole: StateFlow<PartnerRole> = _activeRole.asStateFlow()

    private val _partnerProfile = MutableStateFlow(PartnerProfile())
    val partnerProfile: StateFlow<PartnerProfile> = _partnerProfile.asStateFlow()

    // KYC Upload States for Restaurant Kitchen
    private val _uploadedKycDocs = MutableStateFlow<Map<String, KycDocument>>(
        mapOf(
            KycDocumentType.BUSINESS_REGISTRATION.code to KycDocument(
                docType = KycDocumentType.BUSINESS_REGISTRATION.code,
                fileName = "OCR_Reg_Certificate_Roadhouse_Pvt_Ltd.pdf",
                status = "approved"
            ),
            KycDocumentType.PAN_VAT_CERTIFICATE.code to KycDocument(
                docType = KycDocumentType.PAN_VAT_CERTIFICATE.code,
                fileName = "IRD_PAN_VAT_301456789.pdf",
                status = "approved"
            ),
            KycDocumentType.FOOD_HYGIENE_PERMIT.code to KycDocument(
                docType = KycDocumentType.FOOD_HYGIENE_PERMIT.code,
                fileName = "DFTQC_Hygiene_Rating_GradeA.pdf",
                status = "approved"
            )
        )
    )
    val uploadedKycDocs: StateFlow<Map<String, KycDocument>> = _uploadedKycDocs.asStateFlow()

    private val _showRoleSwitcherSheet = MutableStateFlow(false)
    val showRoleSwitcherSheet: StateFlow<Boolean> = _showRoleSwitcherSheet.asStateFlow()

    fun setPhoneNumber(number: String) {
        _phoneNumber.value = number
    }

    fun setOtpCode(code: String) {
        _otpCode.value = code
    }

    fun requestOtp() {
        if (_phoneNumber.value.isNotBlank()) {
            _authState.value = PartnerOnboardingAuthState.OTP_VERIFY
        }
    }

    fun verifyOtp() {
        if (_otpCode.value.length >= 4) {
            _authState.value = PartnerOnboardingAuthState.ROLE_SELECTION
        }
    }

    /**
     * Selecting a partner role on onboarding
     * If RESTAURANT selected: initiates merchant KYC (business_reg, pan_vat, food_hygiene_permit)
     */
    fun selectRoleOnboarding(role: PartnerRole) {
        _activeRole.value = role
        if (role == PartnerRole.RESTAURANT || role == PartnerRole.GROCERY || role == PartnerRole.RIDER) {
            _authState.value = PartnerOnboardingAuthState.KYC_UPLOAD
        } else {
            _authState.value = PartnerOnboardingAuthState.ACTIVE_DASHBOARD
        }
    }

    /**
     * Upload KYC Document
     */
    fun uploadKycDocument(type: KycDocumentType, fileName: String) {
        val doc = KycDocument(
            docType = type.code,
            fileName = fileName,
            status = "approved"
        )
        val updated = _uploadedKycDocs.value.toMutableMap()
        updated[type.code] = doc
        _uploadedKycDocs.value = updated
    }

    /**
     * Submit merchant KYC and activate restaurant profile + public.stores (category = 'food')
     */
    fun submitRestaurantKyc() {
        _partnerProfile.value = _partnerProfile.value.copy(
            type = PartnerRole.RESTAURANT,
            status = "active",
            businessName = "Roadhouse Cafe & Kitchen",
            kycDocuments = _uploadedKycDocs.value.values.toList()
        )
        _authState.value = PartnerOnboardingAuthState.ACTIVE_DASHBOARD
    }

    /**
     * Submit retail KYC and activate grocery profile + public.stores (category = 'grocery')
     */
    fun submitGroceryKyc() {
        _partnerProfile.value = _partnerProfile.value.copy(
            type = PartnerRole.GROCERY,
            status = "active",
            tradeName = "Bhatbhateni Supermarket",
            legalName = "Bhat-Bhateni Supermarket & Departmental Store Pvt. Ltd.",
            businessName = "Bhatbhateni Supermarket & Departmental Store Pvt. Ltd.",
            activeStoreId = "store-bhatbhateni-pulchowk",
            kycDocuments = _uploadedKycDocs.value.values.toList()
        )
        _authState.value = PartnerOnboardingAuthState.ACTIVE_DASHBOARD
    }

    /**
     * Submit delivery courier KYC and activate rider profile
     */
    fun submitCourierKyc(
        vehiclePlate: String = "BA 88 PA 4321",
        vehicleType: String = "Scooter (Honda Dio)"
    ) {
        _partnerProfile.value = _partnerProfile.value.copy(
            type = PartnerRole.RIDER,
            status = "active",
            businessName = "Evrry Delivery Courier",
            tradeName = "Courier Rider ($vehiclePlate · $vehicleType)",
            kycDocuments = _uploadedKycDocs.value.values.toList()
        )
        _authState.value = PartnerOnboardingAuthState.ACTIVE_DASHBOARD
    }

    /**
     * 1-Tap Top Bar Role Switcher for Multi-Venture Nepali Partners
     */
    fun switchRole(role: PartnerRole) {
        _activeRole.value = role
        _showRoleSwitcherSheet.value = false
    }

    fun openRoleSwitcher() {
        _showRoleSwitcherSheet.value = true
    }

    fun closeRoleSwitcher() {
        _showRoleSwitcherSheet.value = false
    }

    fun isRestaurantKycComplete(): Boolean {
        val docs = _uploadedKycDocs.value
        return docs.containsKey(KycDocumentType.BUSINESS_REGISTRATION.code) &&
                docs.containsKey(KycDocumentType.PAN_VAT_CERTIFICATE.code) &&
                docs.containsKey(KycDocumentType.FOOD_HYGIENE_PERMIT.code)
    }

    fun isGroceryKycComplete(): Boolean {
        val docs = _uploadedKycDocs.value
        return docs.containsKey(KycDocumentType.BUSINESS_REGISTRATION.code) &&
                docs.containsKey(KycDocumentType.PAN_VAT_CERTIFICATE.code)
    }
}
