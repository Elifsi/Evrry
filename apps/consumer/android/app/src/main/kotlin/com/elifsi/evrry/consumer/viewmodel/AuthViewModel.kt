package com.elifsi.evrry.consumer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elifsi.evrry.consumer.model.*
import com.elifsi.evrry.consumer.network.EvrryAuthService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Authentication & User Profile ViewModel (Nepal Phone + WhatsApp OTP / SMS)
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
class AuthViewModel(
    private val authService: EvrryAuthService? = null
) : ViewModel() {

    private val _state = MutableStateFlow<AuthScreenState>(AuthScreenState.Splash)
    val state: StateFlow<AuthScreenState> = _state.asStateFlow()

    fun finishSplash(hasSeenOnboarding: Boolean = false) {
        _state.value = if (hasSeenOnboarding) AuthScreenState.Idle else AuthScreenState.Onboarding
    }

    fun finishOnboarding() {
        _state.value = AuthScreenState.Idle
    }

    fun signInWithGoogle(onSuccess: () -> Unit = {}) {
        _isLoading.value = true
        viewModelScope.launch {
            delay(600)
            _isLoading.value = false
            val user = _userProfile.value.copy(
                fullName = "Rahul Sharma",
                username = "rahul_google",
                email = "rahul.sharma@gmail.com",
                phone = "",
                isPhoneVerified = false
            )
            _userProfile.value = user
            _state.value = AuthScreenState.Authenticated(user)
            onSuccess()
        }
    }

    fun signInWithApple(onSuccess: () -> Unit = {}) {
        _isLoading.value = true
        viewModelScope.launch {
            delay(600)
            _isLoading.value = false
            val user = _userProfile.value.copy(
                fullName = "Rahul Sharma",
                username = "rahul_apple",
                email = "rahul.apple@privaterelay.appleid.com",
                phone = "",
                isPhoneVerified = false
            )
            _userProfile.value = user
            _state.value = AuthScreenState.Authenticated(user)
            onSuccess()
        }
    }

    fun signInWithEmail(email: String, onSuccess: () -> Unit = {}) {
        _isLoading.value = true
        viewModelScope.launch {
            delay(600)
            _isLoading.value = false
            val name = email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
            val user = _userProfile.value.copy(
                fullName = name,
                username = email.substringBefore("@"),
                email = email,
                phone = "",
                isPhoneVerified = false
            )
            _userProfile.value = user
            _state.value = AuthScreenState.Authenticated(user)
            onSuccess()
        }
    }

    fun signInWithPassword(identifier: String, password: String, onSuccess: () -> Unit = {}) {
        _isLoading.value = true
        viewModelScope.launch {
            delay(500)
            _isLoading.value = false
            val isEmail = identifier.contains("@")
            val name = if (isEmail) identifier.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() } else "Evrry User"
            val phone = if (!isEmail) {
                if (identifier.startsWith("+")) identifier else "+977 $identifier"
            } else ""
            val email = if (isEmail) identifier else ""
            val user = _userProfile.value.copy(
                fullName = name,
                username = identifier.substringBefore("@").filter { it.isLetterOrDigit() }.ifBlank { "user" },
                email = email,
                phone = phone,
                isPhoneVerified = phone.isNotBlank()
            )
            _userProfile.value = user
            _state.value = AuthScreenState.Authenticated(user)
            onSuccess()
        }
    }

    fun signUpWithPassword(fullName: String, identifier: String, password: String, onSuccess: () -> Unit = {}) {
        _isLoading.value = true
        viewModelScope.launch {
            delay(500)
            _isLoading.value = false
            val isEmail = identifier.contains("@")
            val phone = if (!isEmail) {
                if (identifier.startsWith("+")) identifier else "+977 $identifier"
            } else ""
            val email = if (isEmail) identifier else ""
            val user = _userProfile.value.copy(
                fullName = fullName.ifBlank { "New Evrry User" },
                username = identifier.substringBefore("@").filter { it.isLetterOrDigit() }.ifBlank { "user" },
                email = email,
                phone = phone,
                isPhoneVerified = phone.isNotBlank()
            )
            _userProfile.value = user
            _state.value = AuthScreenState.Authenticated(user)
            onSuccess()
        }
    }

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _selectedChannel = MutableStateFlow(OtpChannel.WHATSAPP)
    val selectedChannel: StateFlow<OtpChannel> = _selectedChannel.asStateFlow()

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            id = "usr_nepal_9812345678",
            phone = "+977 9812345678",
            fullName = "Rahul Sharma",
            username = "rahul_evrry",
            avatarUrl = null,
            bioStatus = "Available on Evrry",
            loyaltyPoints = 420,
            kycVerified = true,
            allowPhoneDiscovery = false,
            readReceiptsEnabled = true,
            lastSeenPrivacy = "Everyone"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun updatePhoneNumber(phone: String) {
        val digitsOnly = phone.filter { it.isDigit() }.take(10)
        _phoneNumber.value = digitsOnly
    }

    fun selectChannel(channel: OtpChannel) {
        _selectedChannel.value = channel
    }

    fun updateOtp(otp: String) {
        _otpCode.value = otp.filter { it.isDigit() }.take(6)
    }

    fun requestOtp() {
        val phone = _phoneNumber.value
        if (phone.length < 10) {
            _state.value = AuthScreenState.Error("Please enter a valid 10-digit Nepal mobile number (e.g. 9812345678).")
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            delay(500)
            _isLoading.value = false
            _state.value = AuthScreenState.OtpRequested(
                phone = "+977$phone",
                channel = _selectedChannel.value,
                retryAfterSeconds = 60,
                mockOtp = "123456"
            )
        }
    }

    fun requestOtpForPhone(phone: String, channel: OtpChannel = OtpChannel.WHATSAPP) {
        val digitsOnly = phone.filter { it.isDigit() }.take(10)
        _phoneNumber.value = digitsOnly
        _selectedChannel.value = channel
        requestOtp()
    }

    fun verifyOtp(code: String = _otpCode.value, onSuccess: () -> Unit = {}) {
        if (code.length != 6) {
            _state.value = AuthScreenState.Error("Please enter the complete 6-digit verification code.")
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            delay(600)
            _isLoading.value = false
            val user = _userProfile.value.copy(
                phone = "+977${_phoneNumber.value.ifEmpty { "9812345678" }}",
                isPhoneVerified = true
            )
            _userProfile.value = user
            _state.value = AuthScreenState.Authenticated(user)
            onSuccess()
        }
    }

    fun linkAndVerifyPhone(
        phone: String,
        code: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val clean = phone.filter { it.isDigit() }.takeLast(10)
        if (clean.length < 10) {
            onError("Please enter a valid 10-digit mobile number.")
            return
        }
        if (code.length != 6) {
            onError("Please enter the 6-digit verification code.")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            delay(500)
            _isLoading.value = false
            val formattedPhone = "+977 $clean"
            _userProfile.value = _userProfile.value.copy(
                phone = formattedPhone,
                isPhoneVerified = true
            )
            onSuccess()
        }
    }

    fun changePassword(
        current: String,
        new: String,
        confirm: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (current.isBlank()) {
            onResult(false, "Please enter your current password")
            return
        }
        if (new.length < 6) {
            onResult(false, "New password must be at least 6 characters")
            return
        }
        if (new != confirm) {
            onResult(false, "New password and confirmation do not match")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            delay(400)
            _isLoading.value = false
            onResult(true, "Password updated successfully!")
        }
    }

    fun requestPasswordReset(identifier: String = "", onResult: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(400)
            _isLoading.value = false
            val dest = if (identifier.isNotBlank()) {
                if (identifier.contains("@")) "email ($identifier)" else "phone (+977 ${identifier.trim().takeLast(10)})"
            } else if (_userProfile.value.email.isNotBlank()) {
                "email (${_userProfile.value.email})"
            } else if (_userProfile.value.phone.isNotBlank()) {
                "phone (${_userProfile.value.phone})"
            } else {
                "your registered contact"
            }
            onResult("Password reset verification link & OTP sent to $dest.")
        }
    }

    fun updateProfile(
        fullName: String,
        username: String,
        bioStatus: String,
        avatarUrl: String? = null
    ) {
        val sanitizedUsername = username.removePrefix("@").filter { it.isLetterOrDigit() || it == '_' || it == '.' }
        _userProfile.value = _userProfile.value.copy(
            fullName = fullName,
            username = sanitizedUsername,
            bioStatus = bioStatus,
            avatarUrl = avatarUrl ?: _userProfile.value.avatarUrl
        )
    }

    fun updatePrivacySettings(
        allowPhoneDiscovery: Boolean,
        readReceiptsEnabled: Boolean,
        lastSeenPrivacy: String
    ) {
        _userProfile.value = _userProfile.value.copy(
            allowPhoneDiscovery = allowPhoneDiscovery,
            readReceiptsEnabled = readReceiptsEnabled,
            lastSeenPrivacy = lastSeenPrivacy
        )
    }

    // --- WHATSAPP-GRADE CHAT BACKUP (GOOGLE DRIVE) ---
    private val _chatBackupState = MutableStateFlow(ChatBackupState())
    val chatBackupState: StateFlow<ChatBackupState> = _chatBackupState.asStateFlow()

    fun startChatBackup() {
        if (_chatBackupState.value.isBackingUp) return
        viewModelScope.launch {
            _chatBackupState.value = _chatBackupState.value.copy(isBackingUp = true, backupProgressPercent = 5)
            delay(400)
            _chatBackupState.value = _chatBackupState.value.copy(backupProgressPercent = 35)
            delay(500)
            _chatBackupState.value = _chatBackupState.value.copy(backupProgressPercent = 75)
            delay(400)
            _chatBackupState.value = _chatBackupState.value.copy(
                isBackingUp = false,
                backupProgressPercent = 100,
                lastBackupTimestamp = "Just now"
            )
        }
    }

    fun updateBackupFrequency(frequency: String) {
        _chatBackupState.value = _chatBackupState.value.copy(autoBackupFrequency = frequency)
    }

    fun updateBackupNetwork(network: String) {
        _chatBackupState.value = _chatBackupState.value.copy(backupNetwork = network)
    }

    fun updateIncludeVideos(include: Boolean) {
        _chatBackupState.value = _chatBackupState.value.copy(includeVideos = include)
    }

    // --- APP SECURITY & BIOMETRIC LOCK ---
    private val _appSecurity = MutableStateFlow(AppSecuritySettings())
    val appSecurity: StateFlow<AppSecuritySettings> = _appSecurity.asStateFlow()

    fun toggleBiometricLock(enabled: Boolean) {
        _appSecurity.value = _appSecurity.value.copy(biometricLockEnabled = enabled)
    }

    // --- CUSTOMER SUPPORT ---
    private val _supportInfo = MutableStateFlow(SupportContactInfo())
    val supportInfo: StateFlow<SupportContactInfo> = _supportInfo.asStateFlow()

    // --- DATA MANAGEMENT & ERASURE (DATA PRIVACY & GDPR) ---
    private val _dataManagement = MutableStateFlow(DataManagementState())
    val dataManagement: StateFlow<DataManagementState> = _dataManagement.asStateFlow()

    fun clearLocalCache() {
        _dataManagement.value = _dataManagement.value.copy(cacheSizeMb = 0.0, isCacheCleared = true)
    }

    fun clearAiMemoryFacts() {
        _dataManagement.value = _dataManagement.value.copy(aiMemoryFactsCount = 0)
    }

    fun requestDataExport() {
        viewModelScope.launch {
            _dataManagement.value = _dataManagement.value.copy(isExportingData = true)
            delay(1200)
            _dataManagement.value = _dataManagement.value.copy(
                isExportingData = false,
                exportDownloadUrl = "https://evrry.com/api/user/export-archive.json"
            )
        }
    }

    // --- ACCOUNT DELETION (GDPR / PLAY STORE / APP STORE COMPLIANCE) ---
    fun deleteAccount(): Boolean {
        _userProfile.value = UserProfile(
            id = "",
            phone = "",
            fullName = "Deleted User",
            username = "deleted",
            avatarUrl = null
        )
        _dataManagement.value = DataManagementState(cacheSizeMb = 0.0, aiMemoryFactsCount = 0, isCacheCleared = true)
        resetToPhoneInput()
        return true
    }

    fun resetToPhoneInput() {
        _otpCode.value = ""
        _state.value = AuthScreenState.Idle
    }
}
