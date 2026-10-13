package com.elifsi.evrry.consumer.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.CartItem
import com.elifsi.evrry.consumer.model.DefaultAddresses
import com.elifsi.evrry.consumer.model.DeliveryAddress
import com.elifsi.evrry.consumer.model.GroceryCatalogData
import com.elifsi.evrry.consumer.model.GrocerySubstitutionPreference
import com.elifsi.evrry.consumer.model.OrderFulfillmentType
import com.elifsi.evrry.consumer.model.PaymentMethod
import com.elifsi.evrry.consumer.ui.components.EvrryTopBar
import com.elifsi.evrry.consumer.ui.food.FulfillmentModeSwitcher
import com.elifsi.evrry.consumer.ui.food.PreCheckoutUpsellSection
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.ActivityViewModel
import com.elifsi.evrry.consumer.viewmodel.ExploreViewModel
import com.elifsi.evrry.consumer.viewmodel.OrderActivityItem

/**
 * Evrry Multi-Vertical Cart & Checkout Screen
 * Unified cart supporting Food Delivery and Quick Mart & Grocery coexistence
 * Connects directly to ActivityViewModel and clears cart upon placement
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
    exploreViewModel: ExploreViewModel,
    activityViewModel: ActivityViewModel,
    onBack: () -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    val cart by exploreViewModel.cart.collectAsState()
    val hasFood = remember(cart.items) { cart.foodItems.isNotEmpty() }
    val hasGrocery = remember(cart.items) { cart.groceryItems.isNotEmpty() }
    val isMultiStore = hasFood && hasGrocery
    val minOrderNpr = remember(cart.activeGroceryVendorId) {
        val activeStore = GroceryCatalogData.partnerStores.find { it.id == cart.activeGroceryVendorId }
        activeStore?.minOrderNpr ?: GroceryCatalogData.defaultHub.minOrderNpr
    }
    val grocerySubtotal = remember(cart.items) { cart.groceryItems.sumOf { it.totalNpr } }
    val isBelowMinOrder = hasGrocery && grocerySubtotal < minOrderNpr
    var selectedPayment by remember { mutableStateOf(PaymentMethod.ESEWA) }
    var specialNotes by remember { mutableStateOf(cart.specialInstructions) }
    var selectedSubstitution by remember { mutableStateOf(GrocerySubstitutionPreference.ALLOW_STORE_SUBSTITUTION) }
    var showAddressSheet by remember { mutableStateOf(false) }

    // Delivery Address Picker Sheet
    if (showAddressSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddressSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "SELECT DELIVERY ADDRESS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryMutedText
                )

                DefaultAddresses.list.forEach { addr ->
                    val isSelected = addr.id == cart.deliveryAddress.id
                    Surface(
                        color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (isSelected) EvrryPrimaryBlue else EvrrySubtleBorder, RoundedCornerShape(14.dp))
                            .clickable {
                                exploreViewModel.selectDeliveryAddress(addr)
                                showAddressSheet = false
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = EvrryPrimaryBlue)
                                Column {
                                    Text(addr.label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EvrryDarkCharcoal)
                                    Text(addr.fullAddress, fontSize = 12.sp, color = EvrryMutedText)
                                    addr.landmark?.let {
                                        Text(it, fontSize = 11.sp, color = EvrryPrimaryBlue)
                                    }
                                }
                            }
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = EvrryPrimaryBlue)
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
    ) {
        // TOP APP BAR with In-App Back Navigation
        EvrryTopBar(
            title = "My Cart & Checkout",
            canGoBack = true,
            onBackClick = onBack
        )

        if (cart.items.isEmpty()) {
            // EMPTY CART STATE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🛒", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Your Cart is Empty", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EvrryDarkCharcoal)
                Text("Browse food, groceries, and services in Explore.", fontSize = 13.sp, color = EvrryMutedText)
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Explore Catalog", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // CART CHECKOUT CONTENT
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
            ) {
                // FULFILLMENT MODE SWITCHER (Delivery vs Takeaway for Food)
                if (hasFood) {
                    item {
                        FulfillmentModeSwitcher(
                            selectedType = cart.fulfillmentType,
                            onTypeSelected = { exploreViewModel.setFulfillmentType(it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // VENDOR / STORE HEADER
                item {
                    val headerText = when {
                        isMultiStore -> "MULTI-VERTICAL BASKET (FOOD + GROCERY)"
                        hasGrocery -> "ORDER FROM ${cart.activeGroceryVendorName?.uppercase() ?: "QUICK MART"}"
                        else -> "ORDER FROM ${cart.activeFoodVendorName?.uppercase() ?: "RESTAURANT KITCHEN"}"
                    }
                    Text(
                        text = headerText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryMutedText
                    )
                }

                // DELIVERY / PICKUP ADDRESS CARD
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (hasFood && cart.fulfillmentType == OrderFulfillmentType.PICKUP) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = Color(0xFFEFF6FF),
                                        shape = CircleShape,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("🛍️", fontSize = 16.sp)
                                        }
                                    }
                                    Column {
                                        Text("Self-Pickup from Kitchen", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryPrimaryBlue)
                                        Text(cart.activeFoodVendorName ?: "Restaurant Kitchen", fontSize = 12.sp, color = EvrryDarkCharcoal)
                                        Text("Ready in ~15 mins · No delivery fee", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = EvrryPrimaryBlue.copy(alpha = 0.1f),
                                        shape = CircleShape,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Column {
                                        Text("Delivery to ${cart.deliveryAddress.label}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                                        Text(cart.deliveryAddress.fullAddress, fontSize = 12.sp, color = EvrryMutedText, maxLines = 1)
                                        if (isMultiStore) {
                                            Text("Coordinated delivery for food + grocery", fontSize = 11.sp, color = EvrryPrimaryBlue, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                                Text(
                                    text = "Change",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryPrimaryBlue,
                                    modifier = Modifier.clickable { showAddressSheet = true }
                                )
                            }
                        }
                    }
                }

                // 1. FOOD DELIVERY ITEMS SECTION
                if (hasFood) {
                    item {
                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🍽️", fontSize = 16.sp)
                                    Column {
                                        Text(
                                            text = "FOOD DELIVERY",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = EvrryPrimaryCoral
                                        )
                                        Text(
                                            text = cart.activeFoodVendorName ?: "Restaurant Kitchen",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EvrryDarkCharcoal
                                        )
                                    }
                                }
                                Surface(
                                    color = EvrryPrimaryCoral.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${cart.foodItems.sumOf { it.quantity }} items",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryPrimaryCoral,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    items(cart.foodItems) { cartItem ->
                        CartItemRow(
                            cartItem = cartItem,
                            onIncrement = { exploreViewModel.addToCart(cartItem.item) },
                            onDecrement = { exploreViewModel.removeFromCart(cartItem.item.id) }
                        )
                    }
                }

                // 2. QUICK MART & GROCERY ITEMS SECTION
                if (hasGrocery) {
                    item {
                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = if (hasFood) 8.dp else 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("⚡", fontSize = 16.sp)
                                    Column {
                                        Text(
                                            text = "QUICK MART & GROCERY",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF047857)
                                        )
                                        Text(
                                            text = cart.activeGroceryVendorName ?: "Evrry Quick Mart Hub",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EvrryDarkCharcoal
                                        )
                                    }
                                }
                                Surface(
                                    color = Color(0xFF047857).copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${cart.groceryItems.sumOf { it.quantity }} items",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    items(cart.groceryItems) { cartItem ->
                        CartItemRow(
                            cartItem = cartItem,
                            onIncrement = { exploreViewModel.addToCart(cartItem.item) },
                            onDecrement = { exploreViewModel.removeFromCart(cartItem.item.id) }
                        )
                    }
                }

                // 3. ANY OTHER CATALOG ITEMS
                val remainingItems = cart.items.filter { it.item.vertical != "food" && it.item.vertical != "grocery" }
                if (remainingItems.isNotEmpty()) {
                    item {
                        Text("OTHER SERVICES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                    }
                    items(remainingItems) { cartItem ->
                        CartItemRow(
                            cartItem = cartItem,
                            onIncrement = { exploreViewModel.addToCart(cartItem.item) },
                            onDecrement = { exploreViewModel.removeFromCart(cartItem.item.id) }
                        )
                    }
                }

                // COOKING / DELIVERY INSTRUCTIONS
                item {
                    Text("COOKING & DELIVERY INSTRUCTIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                    OutlinedTextField(
                        value = specialNotes,
                        onValueChange = {
                            specialNotes = it
                            exploreViewModel.setSpecialInstructions(it)
                        },
                        placeholder = { Text("E.g. Extra spicy, don't ring the bell, call when arriving...", fontSize = 12.sp, color = EvrryMutedText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        maxLines = 2
                    )
                }

                // FREQUENTLY ORDERED UPSELLS
                item {
                    PreCheckoutUpsellSection(
                        popularAddons = exploreViewModel.popularAddons,
                        onAddAddon = { exploreViewModel.addAddonToCart(it) }
                    )
                }

                // OUT OF STOCK PREFERENCE
                if (hasGrocery) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("OUT OF STOCK ITEM PREFERENCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    GrocerySubstitutionPreference.ALLOW_STORE_SUBSTITUTION to "🔄 Replace closest",
                                    GrocerySubstitutionPreference.CALL_CUSTOMER to "📞 Call me",
                                    GrocerySubstitutionPreference.REFUND_ITEM to "💵 Refund item"
                                ).forEach { (pref, label) ->
                                    val isSelected = selectedSubstitution == pref
                                    Surface(
                                        color = if (isSelected) Color(0xFFECFDF5) else Color.White,
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) Color(0xFF10B981) else EvrrySubtleBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedSubstitution = pref }
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color(0xFF047857) else EvrryDarkCharcoal,
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // BILL BREAKDOWN
                item {
                    Text("BILL BREAKDOWN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (isMultiStore) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Food Delivery Subtotal", fontSize = 13.sp, color = EvrryMutedText)
                                    Text("Rs ${cart.foodItems.sumOf { it.totalNpr }.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Quick Mart Subtotal", fontSize = 13.sp, color = EvrryMutedText)
                                    Text("Rs ${cart.groceryItems.sumOf { it.totalNpr }.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                                }
                            } else {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Item Subtotal", fontSize = 13.sp, color = EvrryMutedText)
                                    Text("Rs ${cart.subtotalNpr.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Delivery Partner Fee", fontSize = 13.sp, color = EvrryMutedText)
                                val feeText = when {
                                    hasFood && cart.fulfillmentType == OrderFulfillmentType.PICKUP && !hasGrocery -> "FREE (Takeaway)"
                                    cart.effectiveDeliveryFeeNpr == 0.0 -> "FREE (Order over Rs 1,000)"
                                    else -> "Rs ${cart.deliveryFeeNpr.toInt()}"
                                }
                                val feeColor = if (cart.effectiveDeliveryFeeNpr == 0.0) Color(0xFF16A34A) else EvrryDarkCharcoal
                                Text(feeText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = feeColor)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Platform Service Fee", fontSize = 13.sp, color = EvrryMutedText)
                                Text("Rs ${cart.platformFeeNpr.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                            }
                            HorizontalDivider(color = EvrrySubtleBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Amount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                Text(
                                    "Rs ${cart.totalNpr.toInt()}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (hasGrocery && !hasFood) Color(0xFF047857) else EvrryPrimaryBlue
                                )
                            }
                        }
                    }
                }

                // PAYMENT METHODS
                item {
                    Text("SELECT PAYMENT METHOD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaymentMethod.values().forEach { method ->
                            val isSelected = selectedPayment == method
                            Surface(
                                color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) EvrryPrimaryBlue else EvrrySubtleBorder,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedPayment = method }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(method.label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                                        Text(method.badge, fontSize = 11.sp, color = if (isSelected) EvrryPrimaryBlue else EvrryMutedText)
                                    }
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = EvrryPrimaryBlue, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // PLACE ORDER BUTTON (Wires into ActivityViewModel & clears cart)
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                val buttonText = if (isBelowMinOrder) {
                    "Min Grocery Rs ${minOrderNpr.toInt()} (Add Rs ${(minOrderNpr - grocerySubtotal).toInt()} more)"
                } else {
                    "Pay Rs ${cart.totalNpr.toInt()} with ${selectedPayment.label}"
                }
                val isButtonEnabled = !isBelowMinOrder && cart.items.isNotEmpty()

                Button(
                    onClick = {
                        val generatedOrderId = "ord-${System.currentTimeMillis() % 100000}"
                        val firstFood = cart.foodItems.firstOrNull()?.item?.title
                        val firstGrocery = cart.groceryItems.firstOrNull()?.item?.title
                        val summaryTitle = when {
                            isMultiStore -> "Food & Grocery Order (${cart.itemCount} items)"
                            hasGrocery -> if (cart.groceryItems.size > 1) "$firstGrocery + ${cart.groceryItems.size - 1} more" else (firstGrocery ?: "Grocery Order")
                            else -> if (cart.foodItems.size > 1) "$firstFood + ${cart.foodItems.size - 1} more" else (firstFood ?: "Food Order")
                        }

                        val lineItems = cart.items.map {
                            val optionsPart = if (it.selectedOptions.isNotEmpty()) " (${it.selectedOptions.joinToString(", ")})" else ""
                            val prefix = if (it.item.vertical == "grocery") "[Grocery] " else "[Food] "
                            "$prefix${it.item.title}$optionsPart x${it.quantity} (Rs ${it.totalNpr.toInt()})"
                        }

                        val isPickup = hasFood && !hasGrocery && cart.fulfillmentType == OrderFulfillmentType.PICKUP
                        val compiledInstructions = buildString {
                            if (specialNotes.isNotBlank()) append(specialNotes)
                            if (hasGrocery) {
                                if (isNotEmpty()) append(" • ")
                                val subText = when (selectedSubstitution) {
                                    GrocerySubstitutionPreference.ALLOW_STORE_SUBSTITUTION -> "Allow closest replacement"
                                    GrocerySubstitutionPreference.CALL_CUSTOMER -> "Call before replacement"
                                    GrocerySubstitutionPreference.REFUND_ITEM -> "Refund if out of stock"
                                }
                                append("Sub: $subText")
                            }
                        }.ifBlank { null }

                        val vendorDisplay = when {
                            isMultiStore -> "${cart.activeFoodVendorName ?: "Kitchen"} & ${cart.activeGroceryVendorName ?: "Quick Mart"}"
                            hasGrocery -> cart.activeGroceryVendorName ?: "Evrry Quick Mart Hub"
                            else -> cart.activeFoodVendorName ?: "Restaurant Kitchen"
                        }

                        val categoryDisplay = when {
                            isMultiStore -> "Food & Mart"
                            hasGrocery -> "Mart"
                            else -> "Food"
                        }

                        val newOrder = OrderActivityItem(
                            id = generatedOrderId,
                            title = summaryTitle,
                            vendorName = vendorDisplay,
                            category = categoryDisplay,
                            amountRs = cart.totalNpr.toInt(),
                            status = if (hasGrocery && !hasFood) "placed" else "kitchen",
                            paymentMethod = selectedPayment.label,
                            dateFormatted = "Just now",
                            etaMinutes = if (hasGrocery && !hasFood) 12 else (if (isPickup) 15 else 25),
                            riderName = if (isPickup) null else "Bikash Thapa",
                            riderPhone = if (isPickup) null else "+977 9841000001",
                            vehicleNumber = if (isPickup) null else "Ba 88 Pa 4321",
                            rideOtp = if (selectedPayment == PaymentMethod.COD) "4821" else null,
                            deliveryAddress = if (isPickup) "Self-Pickup from ${cart.activeFoodVendorName ?: "Kitchen"}" else cart.deliveryAddress.fullAddress,
                            specialInstructions = compiledInstructions,
                            lineItems = lineItems,
                            subtotalRs = cart.subtotalNpr.toInt(),
                            deliveryFeeRs = cart.effectiveDeliveryFeeNpr.toInt(),
                            platformFeeRs = cart.effectivePlatformFeeNpr.toInt()
                        )

                        // 1. Add order to ActivityViewModel
                        activityViewModel.addOrder(newOrder)

                        // 2. Clear cart
                        exploreViewModel.clearCart()

                        // 3. Navigate to order confirmation
                        onOrderPlaced(generatedOrderId)
                    },
                    enabled = isButtonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasGrocery && !hasFood) Color(0xFF047857) else EvrryPrimaryBlue,
                        disabledContainerColor = Color(0xFF9CA3AF)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    cartItem: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(cartItem.item.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = EvrryDarkCharcoal)
                if (cartItem.item.subtitle.isNotBlank()) {
                    Text(cartItem.item.subtitle, fontSize = 11.sp, color = EvrryMutedText)
                }
                if (cartItem.selectedOptions.isNotEmpty()) {
                    Text(cartItem.selectedOptions.joinToString(" · "), fontSize = 11.sp, color = EvrryPrimaryBlue)
                }
                Text("Rs ${cartItem.item.priceNpr.toInt()} each", fontSize = 12.sp, color = EvrryDarkCharcoal, fontWeight = FontWeight.Medium)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onDecrement() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EvrryDarkCharcoal)
                    }
                }
                Text("${cartItem.quantity}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EvrryDarkCharcoal)
                Surface(
                    color = EvrryPrimaryBlue.copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onIncrement() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EvrryPrimaryBlue)
                    }
                }
            }
        }
    }
}
