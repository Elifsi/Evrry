package com.elifsi.evrry.consumer.ui.food

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.CatalogItem
import com.elifsi.evrry.consumer.model.OrderFulfillmentType
import com.elifsi.evrry.consumer.model.Restaurant
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.OrderActivityItem

/**
 * Food Vertical Specialized UI Components & Sheets
 * Delivery / Pickup switcher, Pre-Checkout upsells, Store info, and Order tracking
 * Elifsi Technologies Private Limited
 */

/**
 * 1. Delivery vs. Pickup Mode Switcher
 */
@Composable
fun FulfillmentModeSwitcher(
    selectedType: OrderFulfillmentType,
    onTypeSelected: (OrderFulfillmentType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Delivery Pill
            val isDelivery = selectedType == OrderFulfillmentType.DELIVERY
            Surface(
                color = if (isDelivery) EvrryPrimaryBlue else Color.Transparent,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTypeSelected(OrderFulfillmentType.DELIVERY) }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛵 Delivery",
                        fontWeight = if (isDelivery) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (isDelivery) Color.White else EvrryDarkCharcoal
                    )
                }
            }

            // Pickup Pill
            val isPickup = selectedType == OrderFulfillmentType.PICKUP
            Surface(
                color = if (isPickup) EvrryPrimaryBlue else Color.Transparent,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTypeSelected(OrderFulfillmentType.PICKUP) }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛍️ Takeaway",
                        fontWeight = if (isPickup) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (isPickup) Color.White else EvrryDarkCharcoal
                    )
                }
            }
        }
    }
}

/**
 * 2. Store Info & Operating Hours Detail Sheet (Atlas Screen 43)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantInfoSheet(
    restaurant: Restaurant,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = restaurant.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = EvrryDarkCharcoal
                    )
                    Text(
                        text = restaurant.cuisines.joinToString(", "),
                        fontSize = 13.sp,
                        color = EvrryMutedText
                    )
                }

                Surface(
                    color = Color(0xFF16A34A),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("★", fontSize = 11.sp, color = Color.White)
                        Text(
                            text = "${restaurant.rating} (${restaurant.ratingCount}+)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            HorizontalDivider(color = EvrrySubtleBorder)

            // Operating Hours
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(18.dp))
                    }
                }
                Column {
                    Text("OPERATING HOURS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                    Text(restaurant.operatingHours, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                    Text("Open Today · Fresh kitchen orders accepted", fontSize = 12.sp, color = Color(0xFF16A34A))
                }
            }

            // Location & Address
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(18.dp))
                    }
                }
                Column {
                    Text("KITCHEN ADDRESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                    Text(restaurant.fullAddress, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                    Text("Pickup Ready in ~${restaurant.pickupEtaMinutes} mins", fontSize = 12.sp, color = EvrryPrimaryBlue)
                }
            }

            // Contact Phone
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(18.dp))
                    }
                }
                Column {
                    Text("PHONE NUMBER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                    Text(restaurant.phone, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                }
            }

            // Hygiene & Dietary Badges
            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🛡️", fontSize = 20.sp)
                    Column {
                        Text(
                            text = "Hygiene & Food Safety",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF166534)
                        )
                        Text(
                            text = restaurant.hygieneCert,
                            fontSize = 11.sp,
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Close Details", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * 3. Itemized Digital Receipt Sheet (Atlas Screen 26)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemizedReceiptSheet(
    order: OrderActivityItem,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("DIGITAL TAX INVOICE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                    Text("Order #${order.id}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = EvrryDarkCharcoal)
                }
                Surface(
                    color = Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "PAID",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF059669),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Surface(
                color = EvrrySubtleSurface,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(order.vendorName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                    Text("PAN/VAT: 601239841 · Inland Revenue Dept (IRD)", fontSize = 11.sp, color = EvrryMutedText)
                    Text("Date: ${order.dateFormatted}", fontSize = 11.sp, color = EvrryMutedText)
                    Text("Delivery To: ${order.deliveryAddress ?: "Kathmandu, Nepal"}", fontSize = 11.sp, color = EvrryMutedText)
                }
            }

            HorizontalDivider(color = EvrrySubtleBorder)

            Text("LINE ITEMS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)

            if (order.lineItems.isNotEmpty()) {
                order.lineItems.forEach { itemDesc ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(itemDesc, fontSize = 13.sp, color = EvrryDarkCharcoal)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(order.title, fontSize = 13.sp, color = EvrryDarkCharcoal)
                    Text("Rs ${order.amountRs - order.deliveryFeeRs - order.platformFeeRs}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            HorizontalDivider(color = EvrrySubtleBorder)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Item Subtotal", fontSize = 12.sp, color = EvrryMutedText)
                Text("Rs ${if (order.subtotalRs > 0) order.subtotalRs else (order.amountRs - order.deliveryFeeRs - order.platformFeeRs)}", fontSize = 12.sp, color = EvrryDarkCharcoal)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Delivery Charge", fontSize = 12.sp, color = EvrryMutedText)
                Text(if (order.deliveryFeeRs == 0) "FREE (Takeaway)" else "Rs ${order.deliveryFeeRs}", fontSize = 12.sp, color = EvrryDarkCharcoal)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Platform Service Fee", fontSize = 12.sp, color = EvrryMutedText)
                Text("Rs ${order.platformFeeRs}", fontSize = 12.sp, color = EvrryDarkCharcoal)
            }

            HorizontalDivider(color = EvrrySubtleBorder)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Paid", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                Text("Rs ${order.amountRs}", fontSize = 17.sp, fontWeight = FontWeight.Black, color = EvrryPrimaryBlue)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Payment Method", fontSize = 12.sp, color = EvrryMutedText)
                Text(order.paymentMethod, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * 4. Post-Delivery Rating & Reviews Composer (Atlas Screens 59, 60, 77, 78)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderRatingSheet(
    order: OrderActivityItem,
    onDismiss: () -> Unit,
    onSubmit: (Int, Int, List<String>, String) -> Unit
) {
    var foodRating by remember { mutableIntStateOf(5) }
    var riderRating by remember { mutableIntStateOf(5) }
    val selectedTags = remember { mutableStateListOf<String>() }
    var reviewNote by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    val foodTags = listOf("Hot & Fresh 🔥", "Spicy & Flavorful 🌶️", "Generous Portion 🍲", "Great Packaging 📦")
    val riderTags = listOf("Super Fast ⚡", "Polite & Helpful 🤝", "Careful Handling 🛡️")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isSubmitted) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🎉", fontSize = 48.sp)
                    Text("Thank You for Your Feedback!", fontWeight = FontWeight.Black, fontSize = 18.sp, color = EvrryDarkCharcoal)
                    Text("Your rating helps ${order.vendorName} and the delivery partner deliver better experiences.", fontSize = 13.sp, color = EvrryMutedText, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Text(
                    text = "RATE YOUR ORDER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryMutedText
                )

                Text(
                    text = "How was your experience from ${order.vendorName}?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryDarkCharcoal
                )

                // Food Rating Row
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Food Quality & Taste", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        for (i in 1..5) {
                            Text(
                                text = if (i <= foodRating) "★" else "☆",
                                fontSize = 32.sp,
                                color = if (i <= foodRating) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                                modifier = Modifier.clickable { foodRating = i }
                            )
                        }
                    }
                }

                // Rider Rating Row (if food delivery)
                if (order.riderName != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Delivery Partner (${order.riderName})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            for (i in 1..5) {
                                Text(
                                    text = if (i <= riderRating) "★" else "☆",
                                    fontSize = 32.sp,
                                    color = if (i <= riderRating) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                                    modifier = Modifier.clickable { riderRating = i }
                                )
                            }
                        }
                    }
                }

                // Tag Chips
                Text("WHAT WENT WELL?", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                val applicableTags = if (order.riderName != null) (foodTags + riderTags) else foodTags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    applicableTags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        Surface(
                            color = if (isSelected) EvrryPrimaryBlue.copy(alpha = 0.12f) else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(20.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EvrryPrimaryBlue) else null,
                            modifier = Modifier.clickable {
                                if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                            }
                        ) {
                            Text(
                                text = tag,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) EvrryPrimaryBlue else EvrryDarkCharcoal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Optional Review Input
                OutlinedTextField(
                    value = reviewNote,
                    onValueChange = { reviewNote = it },
                    placeholder = { Text("Leave a note for the kitchen...", fontSize = 13.sp, color = EvrryMutedText) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        onSubmit(foodRating, riderRating, selectedTags.toList(), reviewNote)
                        isSubmitted = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Submit Rating", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * 5. Pre-Checkout Frequently Ordered Upsell Drawer (Atlas Screen 89)
 */
@Composable
fun PreCheckoutUpsellSection(
    popularAddons: List<CatalogItem>,
    onAddAddon: (CatalogItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "FREQUENTLY ORDERED TOGETHER",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = EvrryMutedText
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            popularAddons.forEach { addon ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder),
                    modifier = Modifier.width(200.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(addon.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal, maxLines = 1)
                        Text(addon.subtitle, fontSize = 11.sp, color = EvrryMutedText, maxLines = 1)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Rs ${addon.priceNpr.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryPrimaryBlue)
                            Surface(
                                color = EvrryPrimaryBlue.copy(alpha = 0.1f),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { onAddAddon(addon) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", tint = EvrryPrimaryBlue, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
