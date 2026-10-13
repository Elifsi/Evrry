package com.elifsi.evrry.partner.ui.courier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.CourierLedgerEntry
import com.elifsi.evrry.partner.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Cash-on-Delivery (COD) Cash Collection Ledger & Reconciliation Sheet
 * Platform: Android (Jetpack Compose)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierCodLedgerSheet(
    codCashInHandPaisa: Long,
    todayEarningsPaisa: Long,
    ledgerEntries: List<CourierLedgerEntry>,
    onInstantCashoutClick: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    val codRs = codCashInHandPaisa / 100.0
    val earningsRs = todayEarningsPaisa / 100.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EvrryNavyMedium,
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EvrryGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = EvrryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Courier Balance & COD Ledger",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryTextPrimary
                        )
                        Text(
                            text = "Midnight automatic settlement & bank reconciliation",
                            fontSize = 11.sp,
                            color = EvrryTextSecondary
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = EvrryTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Two Balance Cards: COD Cash in Hand vs Courier Earnings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // COD Cash in Hand Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = EvrryNavyDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "COD CASH IN HAND",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rs ${codRs.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = EvrryOrange
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Physical cash due to merchant at midnight",
                            fontSize = 9.sp,
                            color = EvrryTextSecondary
                        )
                    }
                }

                // Delivery Earnings Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = EvrryNavyDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EvrryGreen.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "TODAY'S EARNINGS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rs ${earningsRs.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = EvrryGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Available for instant payout",
                            fontSize = 9.sp,
                            color = EvrryTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Instant Cash Out Button
            Button(
                onClick = { onInstantCashoutClick(earningsRs) },
                enabled = earningsRs > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EvrryGreen)
            ) {
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "INSTANT CASHOUT (NCHL / IPS) · Rs ${earningsRs.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Recent Transactions Ledger",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Ledger Entries
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ledgerEntries) { entry ->
                    val isCredit = entry.type == "DELIVERY_FEE_EARNED"
                    val isCod = entry.type == "COD_COLLECTED"
                    val timeStr = SimpleDateFormat("h:mm a, d MMM", Locale.getDefault()).format(Date(entry.timestamp))

                    Surface(
                        color = EvrryNavyDark,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCredit) EvrryGreen.copy(alpha = 0.15f)
                                        else if (isCod) EvrryOrange.copy(alpha = 0.15f)
                                        else EvrryBlue.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCredit || isCod) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = if (isCredit) EvrryGreen else if (isCod) EvrryOrange else EvrryBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.description,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryTextPrimary
                                )
                                Text(
                                    text = timeStr,
                                    fontSize = 10.sp,
                                    color = EvrryTextSecondary
                                )
                            }
                            Text(
                                text = "${if (isCredit || isCod) "+" else "-"}Rs ${entry.amountRs.toInt()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCredit) EvrryGreen else if (isCod) EvrryOrange else EvrryTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
