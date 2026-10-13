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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.PartnerRole
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.PartnerAuthViewModel

/**
 * Onboarding Role Selection & Retail KYC Screen
 * Displays visual role cards for Driver, Kitchen, Grocery Supermarket, and Delivery Courier.
 * Selecting Grocery Supermarket initiates retail KYC (business_registration, pan_vat_certificate)
 * and sets partner profile type = 'grocery'.
 * Elifsi Technologies Private Limited
 */
@Composable
fun RoleSelectionScreen(
    authViewModel: PartnerAuthViewModel,
    onRoleConfirmed: () -> Unit
) {
    val selectedRole by authViewModel.selectedRole.collectAsState()
    var showRetailKycDialog by remember { mutableStateOf(false) }

    var tradeName by remember { mutableStateOf("Bhatbhateni Supermarket") }
    var legalName by remember { mutableStateOf("Bhat-Bhateni Supermarket & Departmental Store Pvt. Ltd.") }
    var panNumber by remember { mutableStateOf("609823145") }
    var brnNumber by remember { mutableStateOf("BRN-2081-99812") }
    var storeAddress by remember { mutableStateOf("Pulchowk, Lalitpur") }
    var wardNo by remember { mutableStateOf("3") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome to Evrry Partner",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = EvrryDarkCharcoal
        )
        Text(
            text = "Select your operational business role. You can switch between active ventures anytime using the top bar.",
            fontSize = 13.sp,
            color = EvrryMutedText,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(PartnerRole.entries) { role ->
                val isSelected = (role == selectedRole)
                Surface(
                    color = EvrryWhite,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            authViewModel.selectRole(role)
                            if (role == PartnerRole.GROCERY) {
                                showRetailKycDialog = true
                            }
                        }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(role.badgeColor) else EvrrySubtleBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            color = Color(role.badgeColor).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = role.emoji, fontSize = 24.sp)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = role.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryDarkCharcoal
                                )
                                Surface(
                                    color = Color(role.badgeColor).copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = role.badge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(role.badgeColor),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = role.subtitle,
                                fontSize = 12.sp,
                                color = EvrryMutedText,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                authViewModel.selectRole(role)
                                if (role == PartnerRole.GROCERY) {
                                    showRetailKycDialog = true
                                }
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(role.badgeColor))
                        )
                    }
                }
            }
        }

        // Action Button
        Button(
            onClick = onRoleConfirmed,
            colors = ButtonDefaults.buttonColors(containerColor = Color(selectedRole.badgeColor)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Continue as ${selectedRole.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = EvrryWhite
            )
        }
    }

    // Retail KYC Dialog for Grocery Supermarket
    if (showRetailKycDialog) {
        AlertDialog(
            onDismissRequest = { showRetailKycDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🛒", fontSize = 20.sp)
                    Text(
                        "Retail Supermarket KYC",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = EvrryDarkCharcoal
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Please verify your retail supermarket registration details per Nepal IRD & Company Registrar requirements.",
                        fontSize = 12.sp,
                        color = EvrryMutedText
                    )

                    OutlinedTextField(
                        value = tradeName,
                        onValueChange = { tradeName = it },
                        label = { Text("Store Trade Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = legalName,
                        onValueChange = { legalName = it },
                        label = { Text("Legal Entity Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = panNumber,
                        onValueChange = { panNumber = it },
                        label = { Text("PAN / VAT Certificate No.") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = brnNumber,
                        onValueChange = { brnNumber = it },
                        label = { Text("Business Registration No. (BRN)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = storeAddress,
                            onValueChange = { storeAddress = it },
                            label = { Text("Store Address") },
                            modifier = Modifier.weight(2f)
                        )
                        OutlinedTextField(
                            value = wardNo,
                            onValueChange = { wardNo = it },
                            label = { Text("Ward") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EvrryGreenSuccess,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "PAN/VAT & BRN documents verified in local storage",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryGreenSuccess
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.switchRoleQuick(PartnerRole.GROCERY)
                        showRetailKycDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryGreenSuccess)
                ) {
                    Text("Confirm Retail KYC", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRetailKycDialog = false }) {
                    Text("Cancel", color = EvrryMutedText)
                }
            }
        )
    }
}
