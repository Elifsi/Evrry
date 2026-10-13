package com.elifsi.evrry.consumer.ui.rides

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.AssignedDriver
import com.elifsi.evrry.consumer.ui.theme.*

/**
 * Ride Rating & Review Bottom Sheet (Consumer Android)
 * Platform: Evrry Super App
 * Elifsi Technologies Private Limited
 *
 * Connected to Supabase RPC 'submit_review'.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideRatingSheet(
    driver: AssignedDriver,
    fareNpr: Double,
    onSubmitReview: (Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf<String>() }

    val quickTags = listOf(
        "Polite & Friendly",
        "Safe & Smooth Driving",
        "Clean Vehicle",
        "Optimal Route",
        "On Time"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // DRIVER AVATAR
            Surface(
                color = EvrryPrimaryBlue.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("👨‍✈️", fontSize = 32.sp)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "How was your ride with ${driver.name}?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = EvrryDarkCharcoal
                )
                Text(
                    text = "Trip Total: Rs ${fareNpr.toInt()} · ${driver.vehicleModel}",
                    fontSize = 12.sp,
                    color = EvrryMutedText
                )
            }

            // 5 STAR SELECTOR
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (star in 1..5) {
                    IconButton(
                        onClick = { rating = star },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "$star stars",
                            tint = if (star <= rating) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Text(
                text = when (rating) {
                    5 -> "Exceptional Experience! ⭐⭐⭐⭐⭐"
                    4 -> "Very Good ⭐⭐⭐⭐"
                    3 -> "Average ⭐⭐⭐"
                    2 -> "Below Expectations ⭐⭐"
                    else -> "Poor Experience ⭐"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (rating >= 4) Color(0xFFD97706) else EvrryMutedText
            )

            // QUICK FEEDBACK TAGS
            Text(
                text = "WHAT WENT WELL?",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryMutedText,
                modifier = Modifier.align(Alignment.Start)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickTags) { tag ->
                    val isSelected = selectedTags.contains(tag)
                    Surface(
                        onClick = {
                            if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                        modifier = Modifier.border(
                            1.dp,
                            if (isSelected) EvrryPrimaryBlue else EvrrySubtleBorder,
                            RoundedCornerShape(16.dp)
                        )
                    ) {
                        Text(
                            text = tag,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) EvrryPrimaryBlue else EvrryDarkCharcoal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // COMMENT FIELD
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Write optional feedback for the driver…", fontSize = 13.sp, color = EvrryMutedText) },
                shape = RoundedCornerShape(14.dp),
                minLines = 2,
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EvrryPrimaryBlue,
                    unfocusedBorderColor = EvrrySubtleBorder
                )
            )

            // SUBMIT CTA
            Button(
                onClick = {
                    val fullComment = if (selectedTags.isNotEmpty()) {
                        "${selectedTags.joinToString(", ")}. $comment".trim()
                    } else comment
                    onSubmitReview(rating, fullComment)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Submit Review",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    }
}
