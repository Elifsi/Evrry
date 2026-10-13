package com.elifsi.evrry.consumer.ui.food

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.*
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.ExploreViewModel
import com.elifsi.evrry.consumer.viewmodel.FoodViewModel

/**
 * Restaurant Detail & Menu Screen
 * Modeled after Commerce RestaurantDetailsScreen & Commerce Restaurant Screen
 * Elifsi Technologies Private Limited
 */
@Composable
fun RestaurantDetailScreen(
    restaurantId: String,
    foodViewModel: FoodViewModel,
    exploreViewModel: ExploreViewModel,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val restaurant = remember(restaurantId) {
        FoodCatalogData.findRestaurant(restaurantId) ?: FoodCatalogData.restaurants.first()
    }
    val menuItems = remember(restaurantId) {
        FoodCatalogData.menuByRestaurant(restaurant.id)
    }
    val sections = remember(menuItems) {
        menuItems.map { it.menuSection }.distinct()
    }

    val cart by exploreViewModel.cart.collectAsState()
    val switchBanner by foodViewModel.restaurantSwitchedBanner.collectAsState()

    var selectedItemForCustomization by remember { mutableStateOf<FoodMenuItem?>(null) }
    var showConflictDialog by remember { mutableStateOf(false) }
    var pendingCustomizedItem by remember { mutableStateOf<CartItem?>(null) }
    var showInfoSheet by remember { mutableStateOf(false) }

    if (showInfoSheet) {
        RestaurantInfoSheet(
            restaurant = restaurant,
            onDismiss = { showInfoSheet = false }
        )
    }

    // COMMERCE RULE: Interactive Conflict Alert Dialog
    if (showConflictDialog) {
        AlertDialog(
            onDismissRequest = {
                showConflictDialog = false
                pendingCustomizedItem = null
            },
            title = {
                Text("Replace items in cart?", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Text(
                    text = "Your cart already contains items from ${cart.activeVendorName ?: "another restaurant"}. Would you like to clear your current cart and start an order from ${restaurant.name}?",
                    fontSize = 13.sp,
                    color = EvrryDarkCharcoal
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingCustomizedItem?.let {
                            exploreViewModel.clearAndAdd(it.item, it.selectedOptions, it.specialNotes, it.quantity)
                            foodViewModel.notifyRestaurantSwitched(restaurant.name)
                        }
                        showConflictDialog = false
                        pendingCustomizedItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Clear & Add", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConflictDialog = false
                        pendingCustomizedItem = null
                    }
                ) {
                    Text("Cancel", color = EvrryMutedText)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    // CUSTOMIZATION SHEET
    selectedItemForCustomization?.let { item ->
        FoodItemCustomizationSheet(
            item = item,
            restaurant = restaurant,
            onDismiss = { selectedItemForCustomization = null },
            onAddToCart = { catItem, options, notes, qty ->
                if (exploreViewModel.hasConflict(restaurant.id)) {
                    pendingCustomizedItem = CartItem(item = catItem, quantity = qty, selectedOptions = options, specialNotes = notes)
                    showConflictDialog = true
                } else {
                    exploreViewModel.addToCartWithOptions(catItem, options, notes, qty)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (cart.itemCount > 0) 90.dp else 24.dp)
        ) {
            // HERO BANNER WITH IN-APP BACK BUTTON (iOS parity)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(restaurant.gradientFrom), Color(restaurant.gradientTo))
                            )
                        )
                        .padding(16.dp)
                ) {
                    // In-app back button (min 44x44 touch target)
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .align(Alignment.TopStart)
                            .clickable { onBack() },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.35f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Food",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.align(Alignment.TopEnd),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (restaurant.isPureVeg) {
                            Surface(
                                color = Color(0xFF16A34A),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "100% PURE VEG",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Top-Right Cart Icon Button with Badge
                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { onNavigateToCart() },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.35f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                if (cart.itemCount > 0) {
                                    Surface(
                                        color = EvrryPrimaryBlue,
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 2.dp, y = (-2).dp)
                                            .size(18.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = if (cart.itemCount > 99) "99+" else "${cart.itemCount}",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                lineHeight = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // RESTAURANT SWITCHED NOTIFICATION BANNER
            if (switchBanner != null) {
                item {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                                Text("ℹ️", fontSize = 14.sp)
                                Text(switchBanner ?: "", fontSize = 12.sp, color = Color(0xFF92400E), fontWeight = FontWeight.Medium)
                            }
                            Text(
                                "✕",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.clickable { foodViewModel.clearSwitchBanner() }.padding(4.dp)
                            )
                        }
                    }
                }
            }

            // RESTAURANT METADATA
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = restaurant.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = EvrryDarkCharcoal,
                            modifier = Modifier.weight(1f)
                        )

                        // Specialized Store Info button
                        Surface(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { showInfoSheet = true },
                            shape = CircleShape,
                            color = EvrrySubtleSurface
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("ℹ️", fontSize = 16.sp)
                            }
                        }
                    }
                    Text(
                        text = restaurant.cuisines.joinToString(", "),
                        fontSize = 13.sp,
                        color = EvrryMutedText,
                        modifier = Modifier.padding(top = 3.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = Color(0xFF16A34A),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text("★", fontSize = 10.sp, color = Color.White)
                                Text(
                                    text = "${restaurant.rating}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Text(
                            text = if (cart.fulfillmentType == OrderFulfillmentType.PICKUP) "🛍️ ~${restaurant.pickupEtaMinutes} min pickup" else "🕒 ${restaurant.deliveryEtaMinutes} min delivery",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = EvrryDarkCharcoal
                        )

                        Text("•", fontSize = 12.sp, color = EvrryMutedText)

                        Text(
                            text = "📍 ${restaurant.area}",
                            fontSize = 12.sp,
                            color = EvrryMutedText
                        )

                        Text("•", fontSize = 12.sp, color = EvrryMutedText)

                        Text(
                            text = "Rs ${restaurant.priceForTwo} for two",
                            fontSize = 12.sp,
                            color = EvrryDarkCharcoal
                        )
                    }

                    if (restaurant.offer != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5))
                        ) {
                            Text(
                                text = "🏷️ ${restaurant.offer}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2410C),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Specialized Delivery vs Takeaway Switcher
                    FulfillmentModeSwitcher(
                        selectedType = cart.fulfillmentType,
                        onTypeSelected = { exploreViewModel.setFulfillmentType(it) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (cart.fulfillmentType == OrderFulfillmentType.PICKUP) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("📍", fontSize = 14.sp)
                                Column {
                                    Text("Self-Pickup from Kitchen", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EvrryPrimaryBlue)
                                    Text(restaurant.fullAddress, fontSize = 11.sp, color = EvrryDarkCharcoal)
                                }
                            }
                        }
                    }
                }
            }

            // MENU SECTIONS
            sections.forEach { sectionTitle ->
                item {
                    Text(
                        text = sectionTitle.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = EvrryMutedText,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 6.dp)
                    )
                }

                val itemsInSection = menuItems.filter { it.menuSection == sectionTitle }
                items(itemsInSection) { menuItem ->
                    val catalogItem = remember(menuItem) {
                        CatalogItem(
                            id = menuItem.id,
                            vertical = "food",
                            vendorId = restaurant.id,
                            vendorName = restaurant.name,
                            title = menuItem.title,
                            subtitle = menuItem.subtitle ?: restaurant.name,
                            priceNpr = menuItem.priceNpr,
                            rating = restaurant.rating
                        )
                    }
                    val qtyInCart = exploreViewModel.getItemQty(menuItem.id)

                    MenuItemRow(
                        item = menuItem,
                        quantity = qtyInCart,
                        onRowClick = {
                            selectedItemForCustomization = menuItem
                        },
                        onAdd = {
                            if (exploreViewModel.hasConflict(restaurant.id)) {
                                pendingCustomizedItem = CartItem(item = catalogItem)
                                showConflictDialog = true
                            } else {
                                exploreViewModel.addToCart(catalogItem)
                            }
                        },
                        onRemove = {
                            exploreViewModel.removeFromCart(menuItem.id)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(if (cart.itemCount > 0) 100.dp else 24.dp))
            }
        }

        // FLOATING BOTTOM CART BAR
        if (cart.itemCount > 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onNavigateToCart() },
                color = EvrryPrimaryBlue,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${cart.itemCount} item${if (cart.itemCount > 1) "s" else ""} added",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Text(
                            text = "Rs ${cart.totalNpr.toInt()}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "View Cart",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItemRow(
    item: FoodMenuItem,
    quantity: Int,
    onRowClick: () -> Unit,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onRowClick() },
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = Color.White,
                        modifier = Modifier
                            .size(14.dp)
                            .border(1.dp, if (item.veg) Color(0xFF16A34A) else Color(0xFFDC2626), RoundedCornerShape(3.dp))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (item.veg) Color(0xFF16A34A) else Color(0xFFDC2626))
                            )
                        }
                    }

                    if (item.isBestseller) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "BESTSELLER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryDarkCharcoal
                )

                Text(
                    text = "Rs ${item.priceNpr.toInt()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = EvrryDarkCharcoal,
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (item.subtitle != null) {
                    Text(
                        text = item.subtitle,
                        fontSize = 11.sp,
                        color = EvrryMutedText,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (item.options.isNotEmpty() || item.addons.isNotEmpty()) {
                    Text(
                        text = "Customizable",
                        fontSize = 10.sp,
                        color = EvrryPrimaryBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // QUANTITY STEPPER (Quick Mart / Commerce style)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF7ED),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (item.veg) "🥗" else "🍗",
                            fontSize = 26.sp
                        )
                    }
                }

                if (quantity == 0) {
                    Surface(
                        modifier = Modifier
                            .width(68.dp)
                            .height(30.dp)
                            .clickable { onAdd() },
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF97316)),
                        shadowElevation = 1.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "ADD",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFF97316)
                            )
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .width(68.dp)
                            .height(30.dp),
                        color = Color(0xFFF97316),
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { onRemove() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "−",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Text(
                                text = "$quantity",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )

                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { onAdd() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
