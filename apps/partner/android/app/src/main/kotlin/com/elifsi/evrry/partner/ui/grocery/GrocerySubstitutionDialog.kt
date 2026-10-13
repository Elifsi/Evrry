package com.elifsi.evrry.partner.ui.grocery

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.GroceryOrderItem
import com.elifsi.evrry.partner.model.GroceryPartnerItem
import com.elifsi.evrry.partner.model.SubstitutionStatus
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.GrocerySupermarketViewModel

/**
 * Out-of-Stock Customer Substitution Flow Dialog
 * Recommends same-category replacement items, dispatches substitution proposals
 * to consumer, and resolves real-time approval/rejection.
 * Elifsi Technologies Private Limited
 */
@Composable
fun GrocerySubstitutionDialog(
    orderItem: GroceryOrderItem,
    viewModel: GrocerySupermarketViewModel,
    orderId: String,
    onDismiss: () -> Unit
) {
    val substitutes = remember(orderItem) {
        viewModel.getRecommendedSubstitutes(orderItem)
    }

    var selectedSubstitute by remember { mutableStateOf<GroceryPartnerItem?>(null) }
    var isProposalSent by remember { mutableStateOf(orderItem.substitutionStatus == SubstitutionStatus.PROPOSED) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🔄", fontSize = 18.sp)
                    Text(
                        text = "Out-of-Stock Substitution",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = EvrryNavy
                    )
                }
                Text(
                    text = "Shelf missing: ${orderItem.name}",
                    fontSize = 12.sp,
                    color = EvrryRedDestructive,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isProposalSent) {
                    Text(
                        text = "Choose an in-stock substitute from '${orderItem.section}' aisle to propose to customer:",
                        fontSize = 12.sp,
                        color = EvrryMuted
                    )

                    if (substitutes.isEmpty()) {
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No other items found in ${orderItem.section}. You can reject/refund the item directly.",
                                fontSize = 12.sp,
                                color = EvrryMuted,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                        ) {
                            items(substitutes) { subItem ->
                                val isSelected = (selectedSubstitute?.id == subItem.id)
                                val priceDiff = subItem.priceNpr - orderItem.unitPriceNpr

                                Surface(
                                    color = if (isSelected) EvrryPrimaryBlue.copy(alpha = 0.08f) else EvrryPaper,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedSubstitute = subItem }
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) EvrryPrimaryBlue else EvrryBorder,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = subItem.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = EvrryInk
                                            )
                                            Text(
                                                text = "Stock: ${subItem.stockQuantity} ${subItem.unit} • SKU: ${subItem.sku ?: "N/A"}",
                                                fontSize = 10.sp,
                                                color = EvrryMuted
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "NPR ${String.format("%.2f", subItem.priceNpr)}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.sp,
                                                color = EvrryPrimaryBlue
                                            )
                                            Text(
                                                text = if (priceDiff >= 0) "+NPR ${String.format("%.2f", priceDiff)}" else "-NPR ${String.format("%.2f", -priceDiff)}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (priceDiff > 0) EvrryAmberWarning else EvrryGreenSuccess
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Proposal sent awaiting consumer feedback
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Proposal Dispatched to Customer 📱",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = "\"${orderItem.name} is out of stock. Would you like ${selectedSubstitute?.name ?: orderItem.proposedSubstituteName} (NPR ${selectedSubstitute?.priceNpr ?: orderItem.proposedSubstitutePriceNpr}) instead?\"",
                                fontSize = 12.sp,
                                color = Color(0xFF78350F)
                            )
                            Text(
                                text = "Awaiting customer approval in real time...",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF92400E)
                            )
                        }
                    }

                    // Simulation action buttons for testing real-time approval
                    Text(
                        text = "Real-time Customer Response:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryMuted
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.resolveSubstitution(orderId, orderItem.id, approved = true)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EvrryEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = EvrryWhite)
                                Text("Customer Approves", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.resolveSubstitution(orderId, orderItem.id, approved = false)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = EvrryRedDestructive)
                                Text("Customer Rejects", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryRedDestructive)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isProposalSent) {
                Button(
                    onClick = {
                        val subItem = selectedSubstitute ?: return@Button
                        viewModel.proposeSubstitution(orderId, orderItem.id, subItem)
                        isProposalSent = true
                    },
                    enabled = selectedSubstitute != null,
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = EvrryWhite)
                        Text("Send Proposal to Customer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isProposalSent) "Close" else "Cancel", color = EvrryMuted)
            }
        }
    )
}
