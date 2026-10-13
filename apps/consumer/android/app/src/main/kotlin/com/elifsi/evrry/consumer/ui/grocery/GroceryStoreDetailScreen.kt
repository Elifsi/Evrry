package com.elifsi.evrry.consumer.ui.grocery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.*
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.ExploreViewModel
import com.elifsi.evrry.consumer.viewmodel.GroceryViewModel

/**
 * Supermarket & Grocery Store Detail Screen
 * Allows shopping from physical partner supermarkets and local kirana stores
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@Composable
fun GroceryStoreDetailScreen(
    store: GroceryStore,
    groceryViewModel: GroceryViewModel,
    exploreViewModel: ExploreViewModel,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    var selectedAisle by remember { mutableStateOf("All") }
    var inStoreSearchQuery by remember { mutableStateOf("") }
    var pendingProductToAdd by remember { mutableStateOf<GroceryProduct?>(null) }
    var showConflictDialog by remember { mutableStateOf(false) }

    val cart by exploreViewModel.cart.collectAsState()

    val storeProducts = remember(store.id, selectedAisle, inStoreSearchQuery) {
        groceryViewModel.getProductsForStore(store.id, selectedAisle, inStoreSearchQuery)
    }

    // Single-store Cart Conflict Dialog
    if (showConflictDialog && pendingProductToAdd != null) {
        AlertDialog(
            onDismissRequest = {
                showConflictDialog = false
                pendingProductToAdd = null
            },
            title = {
                Text("Start shopping from ${store.name}?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Text(
                    "Your grocery basket contains items from ${cart.activeGroceryVendorName ?: cart.activeVendorName ?: "another store"}. Groceries can only be fulfilled from one store at a time. Clear conflicting items to continue?",
                    fontSize = 13.sp,
                    color = EvrryMutedText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingProductToAdd?.let { prod ->
                            val catalogItem = CatalogItem(
                                id = prod.id,
                                vertical = "grocery",
                                title = prod.title,
                                subtitle = prod.packSize,
                                priceNpr = prod.priceNpr,
                                originalPriceNpr = prod.mrpNpr,
                                vendorName = store.name,
                                vendorId = store.id,
                                rating = store.rating,
                                etaMinutes = 30
                            )
                            exploreViewModel.clearAndAdd(catalogItem)
                        }
                        showConflictDialog = false
                        pendingProductToAdd = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857))
                ) {
                    Text("Clear & Add", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showConflictDialog = false
                        pendingProductToAdd = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onBack() }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = EvrryDarkCharcoal,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "All Stores",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EvrryDarkCharcoal
                    )
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(14.dp))
                        .clickable { onNavigateToCart() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color(0xFF047857), modifier = Modifier.size(16.dp))
                        if (cart.itemCount > 0) {
                            Text("${cart.itemCount}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                        }
                    }
                }
            }

            // STORE HEADER CARD
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(store.emoji, fontSize = 28.sp)
                            Column {
                                Text(store.name, fontSize = 16.sp, fontWeight = FontWeight.Black, color = EvrryDarkCharcoal)
                                Text(store.area, fontSize = 12.sp, color = EvrryMutedText)
                            }
                        }
                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = store.badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(store.tagline, fontSize = 12.sp, color = EvrryDarkCharcoal, lineHeight = 16.sp)

                    // METRICS ROW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("⭐", fontSize = 11.sp)
                            Text("${store.rating}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                            Text("(${store.reviewCount})", fontSize = 11.sp, color = EvrryMutedText)
                        }
                        Text("•", color = EvrrySubtleBorder)
                        Text("📍 ${store.distanceKm} km", fontSize = 11.sp, color = EvrryMutedText, fontWeight = FontWeight.Medium)
                        Text("•", color = EvrrySubtleBorder)
                        Text("⏱️ ${store.etaRange}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                        Text("•", color = EvrrySubtleBorder)
                        Text("Min Rs ${store.minOrderNpr.toInt()}", fontSize = 11.sp, color = EvrryMutedText)
                    }

                    // FREE DELIVERY PROMPT
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🎉", fontSize = 12.sp)
                            Text(
                                text = "FREE Delivery on orders over Rs ${store.freeDeliveryAboveNpr.toInt()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF166534)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // IN-STORE SEARCH BAR
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = EvrryMutedText, modifier = Modifier.size(18.dp))
                    TextField(
                        value = inStoreSearchQuery,
                        onValueChange = { inStoreSearchQuery = it },
                        placeholder = { Text("Search items in ${store.name}...", fontSize = 12.sp, color = EvrryMutedText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    if (inStoreSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { inStoreSearchQuery = "" }, modifier = Modifier.size(20.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = EvrryMutedText)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AISLE FILTER TABS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val aisles = listOf("All") + store.aisles
                aisles.forEach { aisle ->
                    val isSelected = selectedAisle == aisle
                    Surface(
                        color = if (isSelected) Color(0xFF047857) else Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .border(1.dp, if (isSelected) Color(0xFF047857) else EvrrySubtleBorder, RoundedCornerShape(16.dp))
                            .clickable { selectedAisle = aisle }
                    ) {
                        Text(
                            text = aisle,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else EvrryDarkCharcoal,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // PRODUCT GRID
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(storeProducts, key = { it.id }) { product ->
                    val cartItem = cart.items.find { it.item.id == product.id }
                    val currentQty = cartItem?.quantity ?: 0

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Product Icon & Discount Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🛍️", fontSize = 22.sp)
                                }

                                if (product.discountPercent > 0) {
                                    Surface(
                                        color = Color(0xFFEFF6FF),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "${product.discountPercent}% OFF",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EvrryPrimaryBlue,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = product.brand,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryMutedText,
                                maxLines = 1
                            )

                            Text(
                                text = product.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EvrryDarkCharcoal,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 16.sp
                            )

                            Text(
                                text = product.packSize,
                                fontSize = 11.sp,
                                color = EvrryMutedText
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // Price & Stepper Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Rs ${product.priceNpr.toInt()}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = EvrryDarkCharcoal
                                    )
                                    if (product.mrpNpr > product.priceNpr) {
                                        Text(
                                            text = "Rs ${product.mrpNpr.toInt()}",
                                            fontSize = 10.sp,
                                            color = EvrryMutedText,
                                            style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                                        )
                                    }
                                }

                                val isOutOfStock = product.stockQuantity <= 0

                                if (isOutOfStock) {
                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder)
                                    ) {
                                        Text(
                                            text = "Sold Out",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EvrryMutedText,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                } else if (currentQty == 0) {
                                    Button(
                                        onClick = {
                                            if (exploreViewModel.hasConflict(store.id, "grocery")) {
                                                pendingProductToAdd = product
                                                showConflictDialog = true
                                            } else {
                                                val catalogItem = CatalogItem(
                                                    id = product.id,
                                                    vertical = "grocery",
                                                    title = product.title,
                                                    subtitle = product.packSize,
                                                    priceNpr = product.priceNpr,
                                                    originalPriceNpr = product.mrpNpr,
                                                    vendorName = store.name,
                                                    vendorId = store.id,
                                                    rating = store.rating,
                                                    etaMinutes = 30
                                                )
                                                exploreViewModel.addToCart(catalogItem)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("+ ADD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                } else {
                                    val canAddMore = currentQty < product.stockQuantity && currentQty < product.maxPerOrder
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier
                                            .border(1.dp, Color(0xFF047857), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "−",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF047857),
                                            modifier = Modifier.clickable {
                                                exploreViewModel.removeFromCart(product.id)
                                            }
                                        )
                                        Text(
                                            text = "$currentQty",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = EvrryDarkCharcoal
                                        )
                                        Text(
                                            text = "+",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (canAddMore) Color(0xFF047857) else EvrryMutedText,
                                            modifier = Modifier.clickable(enabled = canAddMore) {
                                                val catalogItem = CatalogItem(
                                                    id = product.id,
                                                    vertical = "grocery",
                                                    title = product.title,
                                                    subtitle = product.packSize,
                                                    priceNpr = product.priceNpr,
                                                    originalPriceNpr = product.mrpNpr,
                                                    vendorName = store.name,
                                                    vendorId = store.id,
                                                    rating = store.rating,
                                                    etaMinutes = 30
                                                )
                                                exploreViewModel.addToCart(catalogItem)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FLOATING BASKET BAR
        if (cart.itemCount > 0) {
            Surface(
                color = Color(0xFF047857),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onNavigateToCart() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${cart.itemCount} ${if (cart.itemCount == 1) "ITEM" else "ITEMS"} • Rs ${cart.totalNpr.toInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "From ${cart.activeGroceryVendorName ?: cart.activeVendorName ?: store.name}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("View Basket", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("→", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
