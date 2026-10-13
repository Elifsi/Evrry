package com.elifsi.evrry.consumer.ui.food

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.elifsi.evrry.consumer.model.FoodMenuItem
import com.elifsi.evrry.consumer.model.Restaurant
import com.elifsi.evrry.consumer.ui.theme.*

/**
 * Item Customization & Add-to-Cart Sheet
 * Ported directly from Commerce FoodDetailsScreen & Commerce ItemDetail
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodItemCustomizationSheet(
    item: FoodMenuItem,
    restaurant: Restaurant,
    onDismiss: () -> Unit,
    onAddToCart: (CatalogItem, List<String>, String, Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var selectedOptionId by remember { mutableStateOf(item.options.firstOrNull()?.id) }
    val selectedAddonIds = remember { mutableStateListOf<String>() }
    var specialInstructions by remember { mutableStateOf("") }

    val basePrice = item.priceNpr
    val optionExtra = item.options.find { it.id == selectedOptionId }?.extraPriceNpr ?: 0.0
    val addonExtra = item.addons.filter { selectedAddonIds.contains(it.id) }.sumOf { it.priceNpr }
    val unitPrice = basePrice + optionExtra + addonExtra
    val totalPrice = unitPrice * quantity

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
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HEADER: Title, Veg Dot, Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.White,
                            modifier = Modifier
                                .size(16.dp)
                                .border(1.5.dp, if (item.veg) Color(0xFF16A34A) else Color(0xFFDC2626), RoundedCornerShape(4.dp))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (item.veg) Color(0xFF16A34A) else Color(0xFFDC2626))
                                )
                            }
                        }
                        Text(item.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EvrryDarkCharcoal)
                    }
                    item.subtitle?.let {
                        Text(it, fontSize = 13.sp, color = EvrryMutedText, modifier = Modifier.padding(top = 4.dp))
                    }
                    Text("Rs ${item.priceNpr.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = EvrryPrimaryBlue, modifier = Modifier.padding(top = 4.dp))
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFF1F5F9))
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EvrryDarkCharcoal, modifier = Modifier.size(18.dp))
                }
            }

            HorizontalDivider(color = EvrrySubtleBorder)

            // VARIATIONS / OPTIONS (Radio options)
            if (item.options.isNotEmpty()) {
                Text("CHOOSE PREPARATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.options.forEach { opt ->
                        val isSelected = opt.id == selectedOptionId
                        Surface(
                            color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, if (isSelected) EvrryPrimaryBlue else EvrrySubtleBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedOptionId = opt.id }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(opt.name, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = EvrryDarkCharcoal)
                                Text(
                                    if (opt.extraPriceNpr > 0) "+Rs ${opt.extraPriceNpr.toInt()}" else "Free",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) EvrryPrimaryBlue else EvrryMutedText
                                )
                            }
                        }
                    }
                }
            }

            // ADD-ONS (Checkboxes)
            if (item.addons.isNotEmpty()) {
                Text("ADD-ONS & EXTRAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.addons.forEach { addon ->
                        val isChecked = selectedAddonIds.contains(addon.id)
                        Surface(
                            color = if (isChecked) Color(0xFFEFF6FF) else Color.White,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, if (isChecked) EvrryPrimaryBlue else EvrrySubtleBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    if (isChecked) selectedAddonIds.remove(addon.id) else selectedAddonIds.add(addon.id)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(addon.name, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = EvrryDarkCharcoal)
                                Text("+Rs ${addon.priceNpr.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EvrryPrimaryBlue)
                            }
                        }
                    }
                }
            }

            // SPECIAL INSTRUCTIONS TEXT
            Text("SPECIAL INSTRUCTIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            OutlinedTextField(
                value = specialInstructions,
                onValueChange = { specialInstructions = it },
                placeholder = { Text("E.g. Less spicy, extra momo achar...", fontSize = 12.sp, color = EvrryMutedText) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )

            // BOTTOM ACTION BAR: Quantity Stepper + Add Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        "-",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = if (quantity > 1) EvrryDarkCharcoal else EvrryMutedText,
                        modifier = Modifier.clickable(enabled = quantity > 1) { quantity-- }
                    )
                    Text("$quantity", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EvrryDarkCharcoal)
                    Text(
                        "+",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = EvrryPrimaryBlue,
                        modifier = Modifier.clickable { quantity++ }
                    )
                }

                // Add to Cart CTA
                Button(
                    onClick = {
                        val chosenOptions = mutableListOf<String>()
                        item.options.find { it.id == selectedOptionId }?.let { chosenOptions.add(it.name) }
                        item.addons.filter { selectedAddonIds.contains(it.id) }.forEach { chosenOptions.add(it.name) }

                        val catalogItem = CatalogItem(
                            id = item.id,
                            vertical = "food",
                            vendorId = restaurant.id,
                            vendorName = restaurant.name,
                            title = item.title,
                            subtitle = item.subtitle ?: restaurant.name,
                            priceNpr = unitPrice,
                            rating = restaurant.rating
                        )
                        onAddToCart(catalogItem, chosenOptions, specialInstructions, quantity)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Add to Cart · Rs ${totalPrice.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
