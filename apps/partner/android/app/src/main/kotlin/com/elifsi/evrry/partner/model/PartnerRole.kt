package com.elifsi.evrry.partner.model

import kotlinx.serialization.Serializable

/**
 * Evrry Unified Partner Roles & KYC State
 * Platform: Android (Kotlin + Jetpack Compose)
 * Elifsi Technologies Private Limited
 */

@Serializable
enum class PartnerRole(
    val code: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val badge: String = ""
) {
    DRIVER(
        code = "driver",
        title = "Mobility Ride Driver",
        subtitle = "Bike, Taxi, Cab, Comfort EV transit",
        emoji = "🚗",
        badge = "Phase 1"
    ),
    RESTAURANT(
        code = "restaurant",
        title = "Restaurant Kitchen",
        subtitle = "Kitchen Display System & thermal tickets",
        emoji = "🍳",
        badge = "Phase 2"
    ),
    GROCERY(
        code = "grocery",
        title = "Grocery Supermarket",
        subtitle = "Shelf SKU lookup, pick-pack & inventory",
        emoji = "🛒",
        badge = "Supermarket Mode"
    ),
    RIDER(
        code = "rider",
        title = "Delivery Courier",
        subtitle = "Order claiming & 2-stage handoff OTP",
        emoji = "🛵",
        badge = "Phase 4"
    );

    val id: String get() = code
    val icon: String get() = emoji
    val badgeColor: Long
        get() = when (this) {
            DRIVER -> 0xFF2563EBL
            RESTAURANT -> 0xFFEA580CL
            GROCERY -> 0xFF16A34AL
            RIDER -> 0xFF9333EAL
        }

    companion object {
        fun fromCode(code: String): PartnerRole {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: DRIVER
        }
    }
}

enum class KycDocumentType(val code: String, val label: String, val description: String) {
    BUSINESS_REGISTRATION("business_registration", "Business Registration Certificate", "Office of Company Registrar (OCR) or Ward registration"),
    PAN_VAT_CERTIFICATE("pan_vat_certificate", "PAN / VAT Certificate", "Inland Revenue Department (IRD) Tax Registration"),
    FOOD_HYGIENE_PERMIT("food_hygiene_permit", "Food Hygiene & Sanitation Permit", "DFTQC Nepal Kitchen Grade Certificate"),
    CITIZENSHIP("citizenship", "Proprietor Citizenship (Nagarikta)", "Government of Nepal National Identity"),
    DRIVING_LICENSE("driving_license", "Smart Driving License", "DoTM Nepal Driver License"),
    VEHICLE_BLUEBOOK("vehicle_bluebook", "Vehicle Registration Bluebook", "Tax & ownership verified"),
    VEHICLE_PHOTO("vehicle_photo", "Vehicle Number Plate Photo", "Front and plate clear view")
}

@Serializable
data class KycDocument(
    val docType: String,
    val fileName: String,
    val fileUri: String? = null,
    val uploadedAt: Long = System.currentTimeMillis(),
    val status: String = "pending" // "pending", "approved", "rejected"
)

@Serializable
data class PartnerKycState(
    val businessRegistrationNo: String = "BRN-2081-99812",
    val panVatNumber: String = "609823145",
    val tradeName: String = "Bhatbhateni Supermarket",
    val legalName: String = "Bhat-Bhateni Supermarket & Departmental Store Pvt. Ltd.",
    val storeAddress: String = "Pulchowk, Lalitpur Ward 3",
    val wardNo: Int = 3,
    val businessRegDocUploaded: Boolean = true,
    val panVatDocUploaded: Boolean = true,
    val licenseNumber: String = "01-06-00981245",
    val licenseExpiry: String = "2029-05-14",
    val bluebookNumber: String = "BB-KTM-88129",
    val vehiclePlate: String = "BA 88 PA 4321",
    val vehicleModel: String = "Honda Dio / Pulsar 150",
    val vehicleType: String = "scooter",
    val drivingLicenseDocUploaded: Boolean = true,
    val vehicleBluebookDocUploaded: Boolean = true,
    val vehiclePhotoUploaded: Boolean = true,
    val kycStatus: String = "verified", // "draft", "pending_verification", "verified"
    val isVerified: Boolean = true
)

typealias DriverKycData = PartnerKycState

@Serializable
data class PartnerProfile(
    val id: String = "partner-profile-001",
    val ownerId: String = "usr-partner-owner",
    val type: PartnerRole = PartnerRole.DRIVER,
    val status: String = "active", // "draft", "pending_verification", "active", "rejected"
    val businessName: String = "Evrry Mobility Driver",
    val legalName: String = "Elifsi Technologies Private Limited",
    val tradeName: String = "Evrry Partner",
    val ownerName: String = "Bikash Thapa",
    val phone: String = "+977-9841000001",
    val panVatNumber: String = "301456789",
    val activeStoreId: String = "store-bhatbhateni-pulchowk",
    val kycDocuments: List<KycDocument> = emptyList()
) {
    val partnerType: String get() = type.code
}
