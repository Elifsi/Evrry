package com.elifsi.evrry.partner.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.KycDocumentType
import com.elifsi.evrry.partner.model.PartnerRole
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.PartnerOnboardingAuthState
import com.elifsi.evrry.partner.viewmodel.PartnerOnboardingViewModel

/**
 * Partner Role Selection & Merchant KYC Screen
 * Platform: Android (Jetpack Compose)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerRoleSelectionScreen(
    onboardingViewModel: PartnerOnboardingViewModel,
    onComplete: () -> Unit
) {
    val authState by onboardingViewModel.authState.collectAsState()
    val activeRole by onboardingViewModel.activeRole.collectAsState()
    val kycDocs by onboardingViewModel.uploadedKycDocs.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = EvrryNavyDark
    ) {
        when {
            authState == PartnerOnboardingAuthState.KYC_UPLOAD && activeRole == PartnerRole.RESTAURANT -> {
                // Restaurant KYC Document Upload Screen
                RestaurantKycUploadView(
                    kycDocs = kycDocs,
                    onUploadMockDoc = { type ->
                        val mockFile = when (type) {
                            KycDocumentType.BUSINESS_REGISTRATION -> "OCR_Reg_Certificate_Roadhouse_Pvt_Ltd.pdf"
                            KycDocumentType.PAN_VAT_CERTIFICATE -> "IRD_PAN_VAT_301456789.pdf"
                            KycDocumentType.FOOD_HYGIENE_PERMIT -> "DFTQC_Hygiene_Rating_GradeA.pdf"
                            KycDocumentType.CITIZENSHIP -> "Nagarikta_Chandra_Gurung.jpg"
                            else -> "Doc_${type.code}.pdf"
                        }
                        onboardingViewModel.uploadKycDocument(type, mockFile)
                    },
                    onSubmit = {
                        onboardingViewModel.submitRestaurantKyc()
                        onComplete()
                    }
                )
            }
            authState == PartnerOnboardingAuthState.KYC_UPLOAD && activeRole == PartnerRole.GROCERY -> {
                // Grocery Supermarket Retail KYC Document Upload Screen
                GroceryKycUploadView(
                    kycDocs = kycDocs,
                    onUploadMockDoc = { type ->
                        val mockFile = when (type) {
                            KycDocumentType.BUSINESS_REGISTRATION -> "OCR_Reg_Certificate_Bhatbhateni_Pvt_Ltd.pdf"
                            KycDocumentType.PAN_VAT_CERTIFICATE -> "IRD_PAN_VAT_609823145.pdf"
                            else -> "Doc_${type.code}.pdf"
                        }
                        onboardingViewModel.uploadKycDocument(type, mockFile)
                    },
                    onSubmit = {
                        onboardingViewModel.submitGroceryKyc()
                        onComplete()
                    }
                )
            }
            authState == PartnerOnboardingAuthState.KYC_UPLOAD && activeRole == PartnerRole.RIDER -> {
                // Delivery Courier KYC Document Upload Screen
                CourierKycUploadView(
                    kycDocs = kycDocs,
                    onUploadMockDoc = { type ->
                        val mockFile = when (type) {
                            KycDocumentType.DRIVING_LICENSE -> "DoTM_License_01-06-00981245.pdf"
                            KycDocumentType.VEHICLE_BLUEBOOK -> "Bluebook_Tax_Paid_BA_88_PA_4321.pdf"
                            KycDocumentType.VEHICLE_PHOTO -> "Vehicle_NumberPlate_Photo.jpg"
                            else -> "Doc_${type.code}.pdf"
                        }
                        onboardingViewModel.uploadKycDocument(type, mockFile)
                    },
                    onSubmit = { vehiclePlate, vehicleType ->
                        onboardingViewModel.submitCourierKyc(vehiclePlate, vehicleType)
                        onComplete()
                    }
                )
            }
            else -> {
                // Role Selection Cards
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Welcome to evrry Partner",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryTextPrimary
                )
                Text(
                    text = "Select your primary operational business venture to configure KYC and dashboard tooling:",
                    fontSize = 13.sp,
                    color = EvrryTextSecondary,
                    modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(PartnerRole.entries) { role ->
                        val isSelected = role == activeRole
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    1.5.dp,
                                    if (isSelected) EvrryOrange else EvrryNavyBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { onboardingViewModel.selectRoleOnboarding(role) },
                            colors = CardDefaults.cardColors(containerColor = EvrryNavyMedium)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = role.icon, fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = role.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = EvrryTextPrimary
                                    )
                                    Text(
                                        text = role.subtitle,
                                        fontSize = 12.sp,
                                        color = EvrryTextSecondary,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isSelected) EvrryOrange else EvrryNavyLight
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        onboardingViewModel.selectRoleOnboarding(activeRole)
                        if (activeRole != PartnerRole.RESTAURANT && activeRole != PartnerRole.GROCERY && activeRole != PartnerRole.RIDER) onComplete()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryOrange)
                ) {
                    Text(
                        text = when (activeRole) {
                            PartnerRole.RESTAURANT -> "Continue to Restaurant Merchant KYC"
                            PartnerRole.GROCERY -> "Continue to Grocery Retail KYC"
                            PartnerRole.RIDER -> "Continue to Courier Delivery KYC"
                            else -> "Launch Partner Dashboard"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
}

/**
 * Restaurant KYC Document Upload Screen
 */
@Composable
private fun RestaurantKycUploadView(
    kycDocs: Map<String, com.elifsi.evrry.partner.model.KycDocument>,
    onUploadMockDoc: (KycDocumentType) -> Unit,
    onSubmit: () -> Unit
) {
    val requiredDocTypes = listOf(
        KycDocumentType.BUSINESS_REGISTRATION,
        KycDocumentType.PAN_VAT_CERTIFICATE,
        KycDocumentType.FOOD_HYGIENE_PERMIT
    )

    val allUploaded = requiredDocTypes.all { kycDocs.containsKey(it.code) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Restaurant Merchant KYC",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = EvrryTextPrimary
        )
        Text(
            text = "To activate your restaurant on evrry Food and link public.stores (category = 'food'), submit legal certificates below:",
            fontSize = 13.sp,
            color = EvrryTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(requiredDocTypes) { type ->
                val uploaded = kycDocs[type.code]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            if (uploaded != null) EvrryGreen else EvrryNavyBorder,
                            RoundedCornerShape(14.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = EvrryNavyMedium)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = type.label,
                                fontWeight = FontWeight.Bold,
                                color = EvrryTextPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = type.description,
                                color = EvrryTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            if (uploaded != null) {
                                Text(
                                    text = "✓ ${uploaded.fileName}",
                                    color = EvrryGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Button(
                            onClick = { onUploadMockDoc(type) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uploaded != null) EvrryNavyLight else EvrryOrange
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = if (uploaded != null) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = if (uploaded != null) EvrryGreen else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (uploaded != null) "Uploaded" else "Upload",
                                fontSize = 12.sp,
                                color = if (uploaded != null) EvrryGreen else Color.White
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onSubmit,
            enabled = allUploaded,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EvrryGreen)
        ) {
            Text(
                text = "Submit KYC & Open Kitchen KDS",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
        }
    }
}

/**
 * Grocery Supermarket Retail KYC Document Upload Screen
 */
@Composable
private fun GroceryKycUploadView(
    kycDocs: Map<String, com.elifsi.evrry.partner.model.KycDocument>,
    onUploadMockDoc: (KycDocumentType) -> Unit,
    onSubmit: () -> Unit
) {
    val requiredDocTypes = listOf(
        KycDocumentType.BUSINESS_REGISTRATION,
        KycDocumentType.PAN_VAT_CERTIFICATE
    )

    val allUploaded = requiredDocTypes.all { kycDocs.containsKey(it.code) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "🛒 Supermarket Retail KYC",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = EvrryTextPrimary
        )
        Text(
            text = "To activate your mart on evrry Grocery and link public.stores (category = 'grocery'), submit legal business registration and IRD PAN/VAT certificates below:",
            fontSize = 13.sp,
            color = EvrryTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(requiredDocTypes) { type ->
                val uploaded = kycDocs[type.code]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            if (uploaded != null) EvrryGreen else EvrryNavyBorder,
                            RoundedCornerShape(14.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = EvrryNavyMedium)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = type.label,
                                fontWeight = FontWeight.Bold,
                                color = EvrryTextPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = type.description,
                                color = EvrryTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            if (uploaded != null) {
                                Text(
                                    text = "✓ ${uploaded.fileName}",
                                    color = EvrryGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Button(
                            onClick = { onUploadMockDoc(type) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uploaded != null) EvrryNavyLight else EvrryOrange
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = if (uploaded != null) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = if (uploaded != null) EvrryGreen else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (uploaded != null) "Uploaded" else "Upload",
                                fontSize = 12.sp,
                                color = if (uploaded != null) EvrryGreen else Color.White
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onSubmit,
            enabled = allUploaded,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EvrryGreen)
        ) {
            Text(
                text = "Submit KYC & Open Supermarket Mode",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
        }
    }
}

/**
 * Delivery Courier KYC Document Upload Screen
 */
@Composable
private fun CourierKycUploadView(
    kycDocs: Map<String, com.elifsi.evrry.partner.model.KycDocument>,
    onUploadMockDoc: (KycDocumentType) -> Unit,
    onSubmit: (plate: String, vehicleType: String) -> Unit
) {
    var vehiclePlate by remember { mutableStateOf("BA 88 PA 4321") }
    var vehicleType by remember { mutableStateOf("Scooter (Honda Dio 110)") }

    val requiredDocTypes = listOf(
        KycDocumentType.DRIVING_LICENSE,
        KycDocumentType.VEHICLE_BLUEBOOK,
        KycDocumentType.VEHICLE_PHOTO
    )

    val allUploaded = requiredDocTypes.all { kycDocs.containsKey(it.code) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "🛵 Delivery Courier KYC",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = EvrryTextPrimary
        )
        Text(
            text = "To activate your courier profile and claim food & grocery delivery dispatches, provide your vehicle information and official transport verification documents:",
            fontSize = 13.sp,
            color = EvrryTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Vehicle Info Form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EvrryNavyBorder, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = EvrryNavyMedium)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Vehicle Information",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = EvrryOrange
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = vehiclePlate,
                    onValueChange = { vehiclePlate = it },
                    label = { Text("Vehicle Registration Plate (Nepal)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EvrryOrange,
                        unfocusedBorderColor = EvrryNavyBorder,
                        focusedTextColor = EvrryTextPrimary,
                        unfocusedTextColor = EvrryTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = vehicleType,
                    onValueChange = { vehicleType = it },
                    label = { Text("Vehicle Model & Category") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EvrryOrange,
                        unfocusedBorderColor = EvrryNavyBorder,
                        focusedTextColor = EvrryTextPrimary,
                        unfocusedTextColor = EvrryTextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(requiredDocTypes) { type ->
                val uploaded = kycDocs[type.code]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            if (uploaded != null) EvrryGreen else EvrryNavyBorder,
                            RoundedCornerShape(14.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = EvrryNavyMedium)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = type.label,
                                fontWeight = FontWeight.Bold,
                                color = EvrryTextPrimary,
                                fontSize = 13.sp
                            )
                            Text(
                                text = type.description,
                                color = EvrryTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            if (uploaded != null) {
                                Text(
                                    text = "✓ ${uploaded.fileName}",
                                    color = EvrryGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 3.dp)
                                )
                            }
                        }

                        Button(
                            onClick = { onUploadMockDoc(type) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uploaded != null) EvrryNavyLight else EvrryOrange
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = if (uploaded != null) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = if (uploaded != null) EvrryGreen else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (uploaded != null) "Uploaded" else "Upload",
                                fontSize = 12.sp,
                                color = if (uploaded != null) EvrryGreen else Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { onSubmit(vehiclePlate, vehicleType) },
            enabled = allUploaded && vehiclePlate.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EvrryGreen)
        ) {
            Text(
                text = "Submit KYC & Launch Courier Mode",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
        }
    }
}

