package com.elifsi.evrry.consumer.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.SavedPaymentMethod
import com.elifsi.evrry.consumer.model.SavedPaymentMethodRepository
import com.elifsi.evrry.consumer.model.SavedPaymentType
import com.elifsi.evrry.consumer.ui.theme.*

/**
 * Dedicated Payment Methods Management Screen
 * Displays all saved payment instruments (eSewa, Khalti, Fonepay, Bank Cards).
 * Allows effortless deletion, setting default, and linking new payment gateways.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    onBack: () -> Unit
) {
    val paymentMethods by SavedPaymentMethodRepository.paymentMethods.collectAsState()

    var methodToDelete by remember { mutableStateOf<SavedPaymentMethod?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    // State for Add Dialog
    var selectedTypeToAdd by remember { mutableStateOf(SavedPaymentType.ESEWA) }
    var inputIdentifier by remember { mutableStateOf("") }
    var inputHolderName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Payment Methods",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryDarkCharcoal
                        )
                        Text(
                            text = "Manage saved wallets, cards & QR accounts",
                            fontSize = 11.sp,
                            color = EvrryMutedText
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = EvrryDarkCharcoal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF9FBFF)
                )
            )
        },
        containerColor = Color(0xFFF9FBFF)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            // 1. INFO BANNER
            item {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EvrryPrimaryBlue,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "1-Tap Express Checkout",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryDarkCharcoal
                            )
                            Text(
                                text = "Your default payment method will be used for rides, food & groceries automatically.",
                                fontSize = 11.sp,
                                color = EvrryMutedText,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // 2. SAVED PAYMENT METHODS HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SAVED PAYMENT METHODS (${paymentMethods.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryMutedText,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // 3. SAVED PAYMENT INSTRUMENTS LIST
            if (paymentMethods.isEmpty()) {
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = EvrryMutedText,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No saved payment methods",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EvrryDarkCharcoal
                            )
                            Text(
                                text = "Add an eSewa, Khalti, Fonepay account or card below.",
                                fontSize = 12.sp,
                                color = EvrryMutedText,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(paymentMethods, key = { it.id }) { method ->
                    PaymentMethodCard(
                        method = method,
                        onSetDefault = {
                            SavedPaymentMethodRepository.setDefault(method.id)
                        },
                        onDelete = {
                            methodToDelete = method
                        }
                    )
                }
            }

            // 4. ADD PAYMENT METHOD BUTTON
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EvrryPrimaryBlue
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add New Payment Method",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 5. SECURITY & REGULATION DISCLAIMER
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = EvrryMutedText,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "PCI-DSS Level 1 · NRB Regulated · 256-Bit Tokenized",
                            fontSize = 11.sp,
                            color = EvrryMutedText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "Card numbers and MPINs are never stored in plain text.",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }

    // CONFIRM DELETE DIALOG
    methodToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { methodToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "Remove Payment Method?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove ${target.title} (${target.subtitle}) from your saved payment methods?",
                    fontSize = 13.sp,
                    color = EvrryMutedText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        SavedPaymentMethodRepository.deletePaymentMethod(target.id)
                        methodToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Remove", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { methodToDelete = null }) {
                    Text("Cancel", color = EvrryDarkCharcoal)
                }
            }
        )
    }

    // ADD NEW PAYMENT METHOD DIALOG
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                inputIdentifier = ""
                inputHolderName = ""
            },
            title = {
                Text(
                    text = "Link Payment Method",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Payment Provider:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EvrryMutedText)

                    // Type Selector Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            SavedPaymentType.ESEWA to "eSewa",
                            SavedPaymentType.KHALTI to "Khalti",
                            SavedPaymentType.FONEPAY to "Fonepay",
                            SavedPaymentType.CARD to "Card"
                        ).forEach { (type, label) ->
                            val isSelected = selectedTypeToAdd == type
                            Surface(
                                onClick = { selectedTypeToAdd = type },
                                color = if (isSelected) EvrryPrimaryBlue else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else EvrryDarkCharcoal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    when (selectedTypeToAdd) {
                        SavedPaymentType.ESEWA, SavedPaymentType.KHALTI -> {
                            Text(
                                text = "Enter registered mobile number:",
                                fontSize = 11.sp,
                                color = EvrryMutedText
                            )
                            OutlinedTextField(
                                value = inputIdentifier,
                                onValueChange = { inputIdentifier = it.filter { ch -> ch.isDigit() }.take(10) },
                                placeholder = { Text("98XXXXXXXX", color = Color(0xFF94A3B8)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        SavedPaymentType.FONEPAY -> {
                            Text(
                                text = "Enter Bank Account or Mobile Number:",
                                fontSize = 11.sp,
                                color = EvrryMutedText
                            )
                            OutlinedTextField(
                                value = inputIdentifier,
                                onValueChange = { inputIdentifier = it },
                                placeholder = { Text("e.g. Nabil Bank 0102030405", color = Color(0xFF94A3B8)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        SavedPaymentType.CARD -> {
                            Text(
                                text = "Card Number (16 Digits):",
                                fontSize = 11.sp,
                                color = EvrryMutedText
                            )
                            OutlinedTextField(
                                value = inputIdentifier,
                                onValueChange = { inputIdentifier = it.filter { ch -> ch.isDigit() }.take(16) },
                                placeholder = { Text("4111 2222 3333 4444", color = Color(0xFF94A3B8)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        else -> {}
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newMethod = SavedPaymentMethod(
                            id = "pm_${System.currentTimeMillis()}",
                            type = selectedTypeToAdd,
                            title = when (selectedTypeToAdd) {
                                SavedPaymentType.ESEWA -> "eSewa Mobile Wallet"
                                SavedPaymentType.KHALTI -> "Khalti Digital Wallet"
                                SavedPaymentType.FONEPAY -> "Fonepay Direct ($inputIdentifier)"
                                SavedPaymentType.CARD -> "Visa / Mastercard Card"
                                else -> "Cash on Delivery"
                            },
                            subtitle = if (selectedTypeToAdd == SavedPaymentType.CARD) {
                                "•••• ${inputIdentifier.takeLast(4).ifBlank { "0000" }}"
                            } else {
                                "+977 ${inputIdentifier.ifBlank { "9812345678" }}"
                            },
                            isDefault = false,
                            linkedAt = "Active"
                        )
                        SavedPaymentMethodRepository.addPaymentMethod(newMethod)
                        showAddDialog = false
                        inputIdentifier = ""
                    },
                    enabled = inputIdentifier.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                ) {
                    Text("Link Method", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = EvrryDarkCharcoal)
                }
            }
        )
    }
}

@Composable
private fun PaymentMethodCard(
    method: SavedPaymentMethod,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (method.isDefault) 1.5.dp else 1.dp,
                color = if (method.isDefault) EvrryPrimaryBlue else EvrrySubtleBorder,
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Brand Badge Icon
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(method.type.badgeColorHex),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            when (method.type) {
                                SavedPaymentType.ESEWA -> {
                                    Text("e", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                                }
                                SavedPaymentType.KHALTI -> {
                                    Text("K", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                                }
                                SavedPaymentType.FONEPAY -> {
                                    Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                                SavedPaymentType.CARD -> {
                                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                                SavedPaymentType.COD -> {
                                    Text("COD", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = method.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryDarkCharcoal
                            )
                            if (method.isDefault) {
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "DEFAULT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryPrimaryBlue,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = method.subtitle,
                            fontSize = 12.sp,
                            color = EvrryMutedText,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Delete Action (Trash Icon)
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Payment Method",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Bottom Actions (Set Default)
            if (!method.isDefault) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = EvrrySubtleBorder)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSetDefault() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Set as Default →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryPrimaryBlue
                    )
                }
            }
        }
    }
}
