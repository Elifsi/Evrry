package com.elifsi.evrry.partner.ui.kds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
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
 * Preparation Time Selector Modal for Kitchen KDS
 * Grounded in enatega-multivendor-store set-order-accept-time
 * Platform: Android (Jetpack Compose)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrepTimeSelectorModal(
    order: RestaurantOrder,
    onAccept: (prepMinutes: Int) -> Unit,
    onAcceptAndPrint: (prepMinutes: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMinutes by remember { mutableStateOf(order.prepTimeMinutes.coerceAtLeast(15)) }
    val presetTimes = listOf(15, 25, 40)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EvrryNavyMedium,
        scrimColor = Color.Black.copy(alpha = 0.70f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Set Estimated Preparation Time",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryTextPrimary
            )
            Text(
                text = "Order #${order.orderNo} · ${order.items.size} item(s)",
                fontSize = 13.sp,
                color = EvrryTextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
            )

            // Preset Quick Buttons (15 min, 25 min, 40 min)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                presetTimes.forEach { minutes ->
                    val isSelected = selectedMinutes == minutes
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) EvrryOrange else EvrryNavyLight)
                            .border(
                                1.dp,
                                if (isSelected) EvrryOrange else EvrryNavyBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedMinutes = minutes }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$minutes",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else EvrryTextPrimary
                            )
                            Text(
                                text = "Minutes",
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else EvrryTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Stepper for Custom Adjustments
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(EvrryNavyLight)
                    .border(1.dp, EvrryNavyBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (selectedMinutes > 5) selectedMinutes -= 5 },
                    enabled = selectedMinutes > 5
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease 5 mins",
                        tint = if (selectedMinutes > 5) EvrryTextPrimary else EvrryTextMuted
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$selectedMinutes mins",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryOrange
                    )
                    Text(
                        text = "Cooking timer target",
                        fontSize = 11.sp,
                        color = EvrryTextSecondary
                    )
                }

                IconButton(
                    onClick = { if (selectedMinutes < 120) selectedMinutes += 5 },
                    enabled = selectedMinutes < 120
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase 5 mins",
                        tint = EvrryTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Accept & Print KOT Button
            Button(
                onClick = { onAcceptAndPrint(selectedMinutes) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EvrryGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Accept & Print KOT ($selectedMinutes min)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Accept (Digital Only)
            OutlinedButton(
                onClick = { onAccept(selectedMinutes) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(EvrryOrange)
                )
            ) {
                Text(
                    text = "Accept Without Printing",
                    color = EvrryOrange,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
