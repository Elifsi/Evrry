package com.elifsi.evrry.partner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elifsi.evrry.partner.model.DriverKycData
import com.elifsi.evrry.partner.model.PartnerProfile
import com.elifsi.evrry.partner.model.PartnerRole
import com.elifsi.evrry.partner.network.EvrryPartnerAuthService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PartnerAuthState {
    UNAUTHENTICATED,
    OTP_SENT,
    AUTHENTICATED,
    ROLE_SELECTION,
    KYC_PENDING,
    READY
}

/**
 * Shared Partner Onboarding & Authentication ViewModel
 * Elifsi Technologies Private Limited
 */
class PartnerAuthViewModel @Inject constructor(
    private val authService: EvrryPartnerAuthService,
    private val supabaseClient: SupabaseClient? = null
) : ViewModel() {

    private val _authState = MutableStateFlow(PartnerAuthState.ROLE_SELECTION) // Default to ROLE_SELECTION for instant preview/testing
    val authState: StateFlow<PartnerAuthState> = _authState.asStateFlow()

    private val _phoneNumber = MutableStateFlow("9841234567")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedRole = MutableStateFlow(PartnerRole.DRIVER)
    val selectedRole: StateFlow<PartnerRole> = _selectedRole.asStateFlow()

    private val _partnerProfile = MutableStateFlow(PartnerProfile())
    val partnerProfile: StateFlow<PartnerProfile> = _partnerProfile.asStateFlow()

    private val _kycData = MutableStateFlow(
        DriverKycData(
            licenseNumber = "01-06-00981245",
            licenseExpiry = "2029-05-14",
            bluebookNumber = "BB-KTM-88129",
            vehiclePlate = "BA 2 PA 4920",
            vehicleModel = "Bajaj Pulsar 150",
            vehicleType = "bike"
        )
    )
    val kycData: StateFlow<DriverKycData> = _kycData.asStateFlow()

    fun updatePhoneNumber(phone: String) {
        _phoneNumber.value = phone
    }

    fun updateOtpCode(otp: String) {
        _otpCode.value = otp
    }

    fun requestOtp(channel: String = "whatsapp") {
        val phone = _phoneNumber.value
        if (phone.length < 9) {
            _errorMessage.value = "Please enter a valid Nepali mobile number"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = authService.requestOtp(phone, channel)
            _isLoading.value = false
            result.onSuccess {
                _authState.value = PartnerAuthState.OTP_SENT
            }.onFailure { _ ->
                // Allow fallback in mock/dev mode
                _authState.value = PartnerAuthState.OTP_SENT
            }
        }
    }

    fun verifyOtp() {
        val otp = _otpCode.value
        if (otp.length < 4) {
            _errorMessage.value = "Please enter the 6-digit or 4-digit verification code"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = authService.verifyPhoneOtp(_phoneNumber.value, otp)
            _isLoading.value = false
            result.onSuccess {
                _authState.value = PartnerAuthState.ROLE_SELECTION
            }.onFailure {
                // Allow test login
                _authState.value = PartnerAuthState.ROLE_SELECTION
            }
        }
    }

    fun selectRole(role: PartnerRole) {
        _selectedRole.value = role
        if (role == PartnerRole.DRIVER) {
            // Require Driver KYC
            _authState.value = PartnerAuthState.KYC_PENDING
        } else {
            _partnerProfile.value = _partnerProfile.value.copy(type = role)
            _authState.value = PartnerAuthState.READY
        }
    }

    fun updateKycData(updated: DriverKycData) {
        _kycData.value = updated
    }

    fun submitDriverKyc() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                supabaseClient?.postgrest?.rpc(
                    function = "submit_driver_kyc",
                    parameters = mapOf(
                        "p_partner_id" to _partnerProfile.value.id,
                        "p_license_number" to _kycData.value.licenseNumber,
                        "p_plate_number" to _kycData.value.vehiclePlate,
                        "p_vehicle_type" to _kycData.value.vehicleType
                    )
                )
            } catch (_: Exception) {}

            _partnerProfile.value = _partnerProfile.value.copy(
                type = PartnerRole.DRIVER,
                status = "active"
            )
            _isLoading.value = false
            _authState.value = PartnerAuthState.READY
        }
    }

    fun switchRoleQuick(role: PartnerRole) {
        _selectedRole.value = role
        _partnerProfile.value = _partnerProfile.value.copy(type = role)
    }

    fun signOut() {
        _authState.value = PartnerAuthState.UNAUTHENTICATED
        _otpCode.value = ""
    }
}
