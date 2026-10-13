package com.elifsi.evrry.partner.ui.kds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.RestaurantOrder
import com.elifsi.evrry.partner.ui.theme.*

/**
 * Structured Rejection Reason Modal Sheet for Kitchen Partner
 * Platform: Android (Jetpack Compose)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RejectReasonSheet(
    order: RestaurantOrder,
    onReject: (reason: String) -> Unit,
    onDismiss: () -> Unit
) {
    val structuredReasons = listOf(
        "Out of ingredients / Essential stock finished",
        "Kitchen overloaded / Peak rush delay",
        "Store closing soon / Kitchen shut down",
        "Kitchen equipment / Oven failure",
        "Customer request cancellation via phone"
    )

    var selectedReason by remember { mutableStateOf(structuredReasons[0]) }
    var customNote by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EvrryNavyMedium,
        scrimColor = Color.Black.copy(alpha = 0.70f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reject Kitchen Order #${order.orderNo}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryRed
                    )
                    Text(
                        text = "Select a reason to notify customer and restore any reserved inventory",
                        fontSize = 12.sp,
                        color = EvrryTextSecondary
                    )
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

            // Reason Options
            structuredReasons.forEach { reason ->
                val isSelected = selectedReason == reason
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) EvrryNavyLight else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) EvrryRed.copy(alpha = 0.6f) else EvrryNavyBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedReason = reason }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isSelected) EvrryRed else EvrryTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = reason,
                        color = EvrryTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom note input
            OutlinedTextField(
                value = customNote,
                onValueChange = { customNote = it },
                label = { Text("Additional kitchen note (optional)", color = EvrryTextSecondary, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EvrryRed,
                    unfocusedBorderColor = EvrryNavyBorder,
                    focusedTextColor = EvrryTextPrimary,
                    unfocusedTextColor = EvrryTextPrimary
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val finalReason = if (customNote.isNotBlank()) "$selectedReason — $customNote" else selectedReason
                    onReject(finalReason)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EvrryRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Confirm Order Rejection",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
