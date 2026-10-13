package com.elifsi.evrry.consumer.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.VoucherItem
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.LoyaltyViewModel

/**
 * Dedicated Vouchers & Referrals Management Screen
 * Dedicated screen for entering promo codes, viewing active food/grocery/ride vouchers,
 * and sharing referral codes.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VouchersReferralsScreen(
    loyaltyViewModel: LoyaltyViewModel,
    onBack: () -> Unit
) {
    val vouchers by loyaltyViewModel.vouchers.collectAsState()
    val referralInfo by loyaltyViewModel.referralInfo.collectAsState()

    var promoInput by remember { mutableStateOf("") }
    var promoAppliedMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Vouchers & Referrals", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                        Text("Discounts, promotions & friend invite bonuses", fontSize = 11.sp, color = EvrryMutedText)
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF9FBFF))
            )
        },
        containerColor = Color(0xFFF9FBFF)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            // 1. REFERRAL BANNER
            item {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Invite Friends & Earn", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EvrryDarkCharcoal)
                            Surface(color = EvrryPrimaryBlue, shape = RoundedCornerShape(8.dp)) {
                                Text("Rs 200 Each", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                        Text(
                            text = "Give your friends Rs 200 off their first ride or order. You'll get Rs 200 in Evrry credits when they complete it.",
                            fontSize = 12.sp,
                            color = EvrryMutedText,
                            lineHeight = 16.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("YOUR CODE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                Text(referralInfo.referralCode, fontSize = 18.sp, fontWeight = FontWeight.Black, color = EvrryPrimaryBlue)
                            }
                            Button(
                                onClick = { /* Share intent */ },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. PROMO CODE INPUT
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("REDEEM PROMO CODE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = promoInput,
                            onValueChange = { promoInput = it.uppercase() },
                            placeholder = { Text("Enter promo code (e.g. EVRRY100)", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        Button(
                            onClick = {
                                if (promoInput.isNotBlank()) {
                                    promoAppliedMessage = "Code $promoInput redeemed successfully!"
                                    promoInput = ""
                                }
                            },
                            enabled = promoInput.isNotBlank(),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                        ) {
                            Text("Apply", fontWeight = FontWeight.Bold)
                        }
                    }
                    promoAppliedMessage?.let { msg ->
                        Text(msg, color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 3. ACTIVE VOUCHERS LIST
            item {
                Text("AVAILABLE VOUCHERS (${vouchers.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText, modifier = Modifier.padding(top = 8.dp))
            }

            items(vouchers) { voucher ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEFF6FF),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ConfirmationNumber,
                                        contentDescription = null,
                                        tint = EvrryPrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(voucher.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EvrryDarkCharcoal)
                                Text("Code: ${voucher.code} • ${voucher.description}", fontSize = 11.sp, color = EvrryMutedText)
                                Text(voucher.expiryDaysText, fontSize = 10.sp, color = EvrryMutedText, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                        Text(voucher.discountText, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF10B981))
                    }
                }
            }
        }
    }
}
