package com.elifsi.evrry.consumer.ui.grocery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.*
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.ExploreViewModel
import com.elifsi.evrry.consumer.viewmodel.GroceryViewModel

/**
 * Production Express Quick Mart & Supermarkets Grocery Home Screen
 * Platform: Evrry Super App (Consumer Android)
 * Architecture: Dual Mode: 10-15 Min Express Quick Commerce + Physical Supermarket Stores
 * Elifsi Technologies Private Limited
 */
@Composable
fun GroceryHomeScreen(
    groceryViewModel: GroceryViewModel,
    exploreViewModel: ExploreViewModel,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onStoreClick: (GroceryStore) -> Unit = {}
) {
    val activeHub by groceryViewModel.activeHub.collectAsState()
    val deliveryMode by groceryViewModel.deliveryMode.collectAsState()
    val selectedCategory by groceryViewModel.selectedCategoryId.collectAsState()
    val selectedSubCategory by groceryViewModel.selectedSubCategory.collectAsState()
    val searchQuery by groceryViewModel.searchQuery.collectAsState()
    val storeSearchQuery by groceryViewModel.storeSearchQuery.collectAsState()
    val storeFilterType by groceryViewModel.storeFilterType.collectAsState()
    val cart by exploreViewModel.cart.collectAsState()

    val filteredProducts = remember(selectedCategory, selectedSubCategory, searchQuery) {
        groceryViewModel.getFilteredProducts()
    }
    val filteredStores = remember(storeSearchQuery, storeFilterType) {
        groceryViewModel.getStores()
    }

    val currentCategoryObj = remember(selectedCategory) {
        GroceryCatalogData.categories.find { it.id == selectedCategory } ?: GroceryCatalogData.categories[0]
    }

    val cartItemIds = remember(cart.items) { cart.items.map { it.item.id }.toSet() }
    val impulseItems = remember(cartItemIds) { groceryViewModel.getImpulseAdditions(cartItemIds) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. PINNED TOP BAR (In-app back with iOS parity + 10-15m ETA badge)
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
                        text = "Back",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EvrryDarkCharcoal
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("⚡", fontSize = 11.sp)
                            Text(
                                text = "${activeHub.etaMinutes} min",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                    }

                    // Top-Right Cart Icon Button with Badge
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, EvrrySubtleBorder, CircleShape)
                            .clickable { onNavigateToCart() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = if (cart.itemCount > 0) EvrryPrimaryBlue else EvrryDarkCharcoal,
                            modifier = Modifier.size(18.dp)
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

            // DUAL-MODE FULFILLMENT SELECTOR: Express Dark Store vs. Supermarket Stores
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Express 10m Mart Pill
                    Surface(
                        color = if (deliveryMode == GroceryDeliveryMode.EXPRESS_DARKSTORE) Color(0xFF047857) else Color.Transparent,
                        shape = RoundedCornerShape(9.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { groceryViewModel.setDeliveryMode(GroceryDeliveryMode.EXPRESS_DARKSTORE) }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚡", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "10m Express Mart",
                                fontSize = 12.sp,
                                fontWeight = if (deliveryMode == GroceryDeliveryMode.EXPRESS_DARKSTORE) FontWeight.Bold else FontWeight.Medium,
                                color = if (deliveryMode == GroceryDeliveryMode.EXPRESS_DARKSTORE) Color.White else EvrryMutedText
                            )
                        }
                    }

                    // Physical Supermarket Stores Pill
                    Surface(
                        color = if (deliveryMode == GroceryDeliveryMode.GROCERY_STORES) Color(0xFF047857) else Color.Transparent,
                        shape = RoundedCornerShape(9.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { groceryViewModel.setDeliveryMode(GroceryDeliveryMode.GROCERY_STORES) }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏪", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Supermarket Stores",
                                fontSize = 12.sp,
                                fontWeight = if (deliveryMode == GroceryDeliveryMode.GROCERY_STORES) FontWeight.Bold else FontWeight.Medium,
                                color = if (deliveryMode == GroceryDeliveryMode.GROCERY_STORES) Color.White else EvrryMutedText
                            )
                        }
                    }
                }
            }

            if (deliveryMode == GroceryDeliveryMode.GROCERY_STORES) {
                // SUPERMARKET STORES DIRECTORY
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Partner Supermarkets",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = EvrryDarkCharcoal,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Shop from Bhatbhateni, Big Mart & local Kathmandu grocery stores",
                        fontSize = 12.sp,
                        color = EvrryMutedText
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Search input for Stores & In-Store SKUs
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Stores",
                            tint = EvrryMutedText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (storeSearchQuery.isEmpty()) {
                                Text(
                                    text = "Search stores (Bhatbhateni, Big Mart, Kirana)...",
                                    fontSize = 12.sp,
                                    color = EvrryMutedText
                                )
                            }
                            androidx.compose.foundation.text.BasicTextField(
                                value = storeSearchQuery,
                                onValueChange = { groceryViewModel.setStoreSearchQuery(it) },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EvrryDarkCharcoal
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (storeSearchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = EvrryMutedText,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { groceryViewModel.setStoreSearchQuery("") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Store Filter Chips
                val storeFilterOptions = listOf("All", "Supermarkets", "Daily Marts", "Organic & Farm", "Local Kirana")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    storeFilterOptions.forEach { filterName ->
                        val isSelected = storeFilterType == filterName
                        Surface(
                            color = if (isSelected) Color(0xFF047857) else Color.White,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF047857) else EvrrySubtleBorder
                            ),
                            modifier = Modifier.clickable {
                                groceryViewModel.setStoreFilterType(filterName)
                            }
                        ) {
                            Text(
                                text = filterName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else EvrryDarkCharcoal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Store List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = if (cart.itemCount > 0) 90.dp else 24.dp)
                ) {
                    // Promotional Banner for Supermarket Marketplace
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF1E3A8A), Color(0xFF2563EB))
                                        )
                                    )
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            color = Color.White.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text(
                                                text = "🏪 PHYSICAL SUPERMARKET NETWORK",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Bhatbhateni & Big Mart",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Doorstep fulfillment from real retail store shelves in 25-45 mins",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                    Text("🛒", fontSize = 36.sp)
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Available Stores near you (${filteredStores.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryDarkCharcoal
                            )
                            Text(
                                text = "Delivery: 20-45 mins",
                                fontSize = 11.sp,
                                color = Color(0xFF047857),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (filteredStores.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🏪", fontSize = 40.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No partner stores match your search",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryDarkCharcoal
                                    )
                                    Text(
                                        text = "Try clearing filters or search term",
                                        fontSize = 12.sp,
                                        color = EvrryMutedText
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredStores, key = { it.id }) { store ->
                            GroceryStoreCard(
                                store = store,
                                onClick = { onStoreClick(store) }
                            )
                        }
                    }
                }
            } else {
                // 2. LOCATION & TITLE
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("📍", fontSize = 13.sp)
                        Text(
                            text = activeHub.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Groceries & Essentials",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = EvrryDarkCharcoal,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Fresh dairy, produce & daily staples delivered in minutes",
                        fontSize = 12.sp,
                        color = EvrryMutedText
                    )
                }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. SEARCH INPUT (Quick Mart SearchFragment pattern)
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = EvrryMutedText,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search milk, eggs, wai wai, atta, veggies...",
                                fontSize = 12.sp,
                                color = EvrryMutedText
                            )
                        }
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = { groceryViewModel.setSearchQuery(it) },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = EvrryDarkCharcoal
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = EvrryMutedText,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { groceryViewModel.setSearchQuery("") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. MAIN PRODUCT GRID (2-column Quick Mart grid with headers)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = if (cart.itemCount > 0) 90.dp else 24.dp)
            ) {
                // PROMOTIONAL BANNER
                item(span = { GridItemSpan(2) }) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Transparent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF059669), Color(0xFF0D9488))
                                    )
                                )
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(
                                            text = "🥛 MORNING ESSENTIALS",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Fresh DDC Milk & Bakery",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Free delivery on orders above Rs. 1,000",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                                Text("🥬", fontSize = 36.sp)
                            }
                        }
                    }
                }

                // AISLES HORIZONTAL SCROLLER
                item(span = { GridItemSpan(2) }) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "EXPLORE AISLES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = EvrryMutedText,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GroceryCatalogData.categories.forEach { cat ->
                                val isSelected = selectedCategory == cat.id
                                Surface(
                                    color = if (isSelected) Color(0xFF047857) else Color.White,
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFF047857) else EvrrySubtleBorder
                                    ),
                                    modifier = Modifier
                                        .clickable { groceryViewModel.selectCategory(cat.id) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(cat.emoji, fontSize = 14.sp)
                                        Text(
                                            text = cat.name,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) Color.White else EvrryDarkCharcoal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // SUBCATEGORY PILLS
                if (currentCategoryObj.subcategories.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            currentCategoryObj.subcategories.forEach { subCat ->
                                val isSubSelected = selectedSubCategory == subCat
                                Surface(
                                    color = if (isSubSelected) Color(0xFFECFDF5) else Color.White,
                                    shape = RoundedCornerShape(20.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSubSelected) Color(0xFF10B981) else EvrrySubtleBorder
                                    ),
                                    modifier = Modifier
                                        .clickable { groceryViewModel.selectSubCategory(subCat) }
                                ) {
                                    Text(
                                        text = subCat,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSubSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSubSelected) Color(0xFF047857) else EvrryDarkCharcoal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // PRODUCTS COUNT HEADER
                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentCategoryObj.name} (${filteredProducts.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryDarkCharcoal
                        )
                        Text(
                            text = "⚡ 12m dispatch",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857)
                        )
                    }
                }

                // NO PRODUCTS STATE
                if (filteredProducts.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🔍", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No products found",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryDarkCharcoal
                                )
                                Text(
                                    text = "Try searching for milk, eggs, or wai wai",
                                    fontSize = 12.sp,
                                    color = EvrryMutedText
                                )
                            }
                        }
                    }
                }

                // 2-COLUMN PRODUCT CARDS
                items(filteredProducts, key = { it.id }) { product ->
                    GroceryProductCard(
                        product = product,
                        activeHub = activeHub,
                        exploreViewModel = exploreViewModel,
                        groceryViewModel = groceryViewModel
                    )
                }

                // 4B. DID YOU FORGET ANYTHING? (Quick Mart Pre-Checkout Impulse Additions)
                if (impulseItems.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "DID YOU FORGET ANYTHING? ⚡",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = EvrryMutedText,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Quick Add",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857)
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    impulseItems.forEach { item ->
                                        val impulseCatalogItem = CatalogItem(
                                            id = item.id,
                                            vertical = "grocery",
                                            title = item.title,
                                            subtitle = "${item.packSize} • ${item.brand}",
                                            priceNpr = item.priceNpr,
                                            originalPriceNpr = item.mrpNpr,
                                            vendorName = activeHub.name,
                                            vendorId = activeHub.id,
                                            rating = 4.9,
                                            etaMinutes = activeHub.etaMinutes,
                                            tags = item.tags
                                        )
                                        Surface(
                                            color = Color.White,
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier
                                                .width(145.dp)
                                                .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(14.dp))
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(60.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color(0xFFF1F5F9)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = when (item.categoryId) {
                                                            "dairy" -> "🥛"
                                                            "produce" -> "🥦"
                                                            "instant" -> "🍜"
                                                            "personal" -> "🧴"
                                                            "bakery" -> "🍞"
                                                            else -> "🛍️"
                                                        },
                                                        fontSize = 28.sp
                                                    )
                                                }
                                                Text(
                                                    text = item.packSize,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EvrryMutedText
                                                )
                                                Text(
                                                    text = item.title,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EvrryDarkCharcoal,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Rs ${item.priceNpr.toInt()}",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = EvrryDarkCharcoal
                                                    )
                                                    Surface(
                                                        color = Color.White,
                                                        shape = RoundedCornerShape(6.dp),
                                                        modifier = Modifier
                                                            .clickable {
                                                                if (exploreViewModel.hasConflict(activeHub.id, "grocery")) {
                                                                    exploreViewModel.clearAndAdd(impulseCatalogItem)
                                                                } else {
                                                                    exploreViewModel.addToCart(impulseCatalogItem)
                                                                }
                                                            }
                                                            .border(1.dp, Color(0xFF047857), RoundedCornerShape(6.dp))
                                                    ) {
                                                        Text(
                                                            text = "+ ADD",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = Color(0xFF047857),
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. PERSISTENT FLOATING BASKET & FREE DELIVERY NUDGE (Express Commerce Standard)
        if (cart.itemCount > 0) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // FREE DELIVERY PROGRESS BAR
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFD1FAE5), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (cart.isFreeDeliveryEligible) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("🎉", fontSize = 12.sp)
                                    Text(
                                        text = "FREE Delivery Unlocked! (Saved Rs 50)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857)
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("🚚", fontSize = 12.sp)
                                    Text(
                                        text = "Add Rs ${cart.amountNeededForFreeDelivery.toInt()} more for FREE Delivery",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryDarkCharcoal
                                    )
                                }
                            }
                            Text(
                                text = "${(cart.freeDeliveryProgress * 100).toInt()}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                        LinearProgressIndicator(
                            progress = { cart.freeDeliveryProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF047857),
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                }

                // FLOATING BASKET ACTION BAR
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF047857),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToCart() }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Basket",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "${cart.itemCount} ${if (cart.itemCount == 1) "ITEM" else "ITEMS"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White.copy(alpha = 0.85f),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Rs ${cart.subtotalNpr.toInt()}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "View Basket",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "→",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Express Quick Mart Single-Card Grocery Product Card
 * Features inline variant pills (e.g., 500 ml / 1 Litre or 6 Pcs / Crate of 30)
 * avoiding catalog card duplication and updating price/cart mapping dynamically.
 */
@Composable
fun GroceryProductCard(
    product: GroceryProduct,
    activeHub: GroceryHub,
    exploreViewModel: ExploreViewModel,
    groceryViewModel: GroceryViewModel,
    modifier: Modifier = Modifier
) {
    var selectedVariantIndex by remember(product.id) { mutableIntStateOf(0) }

    val activeVariant = if (product.variants.isNotEmpty()) {
        product.variants.getOrNull(selectedVariantIndex) ?: product.variants.first()
    } else null

    val activeItemId = activeVariant?.id ?: product.id
    val currentPrice = activeVariant?.priceNpr ?: product.priceNpr
    val currentMrp = activeVariant?.mrpNpr ?: product.mrpNpr
    val currentPackSize = activeVariant?.packSize ?: product.packSize
    val currentStock = activeVariant?.stockQuantity ?: product.stockQuantity
    val currentDiscount = activeVariant?.discountPercent ?: product.discountPercent

    val catalogItem = remember(product, activeVariant) {
        CatalogItem(
            id = activeItemId,
            vertical = "grocery",
            title = if (activeVariant != null && product.variants.size > 1) "${product.title} (${activeVariant.packSize})" else product.title,
            subtitle = "$currentPackSize • ${product.brand}",
            priceNpr = currentPrice,
            originalPriceNpr = currentMrp,
            vendorName = activeHub.name,
            vendorId = activeHub.id,
            rating = 4.9,
            etaMinutes = activeHub.etaMinutes,
            tags = product.tags
        )
    }

    val currentQty = exploreViewModel.getItemQty(activeItemId)
    val isOutOfStock = currentStock <= 0
    val isLowStock = currentStock in 1..3

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            // Product Thumbnail Placeholder / Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (product.categoryId) {
                        "dairy" -> "🥛"
                        "produce" -> "🥦"
                        "instant" -> "🍜"
                        "staples" -> "🍚"
                        "beverages" -> "🧃"
                        "bakery" -> "🍞"
                        "cleaning" -> "🧼"
                        else -> "🛍️"
                    },
                    fontSize = 38.sp
                )

                if (currentDiscount > 0) {
                    Surface(
                        color = Color(0xFF047857),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "$currentDiscount% OFF",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                if (isLowStock) {
                    Surface(
                        color = Color(0xFFFFFBEB),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "Only $currentStock left",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pack Size Badge or Variant Selector Pills (Express Quick Mart Single-Card Variant Switcher)
            if (product.variants.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    product.variants.forEachIndexed { index, variant ->
                        val isVariantSelected = index == selectedVariantIndex
                        Surface(
                            color = if (isVariantSelected) Color(0xFFECFDF5) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isVariantSelected) Color(0xFF10B981) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.clickable { selectedVariantIndex = index }
                        ) {
                            Text(
                                text = variant.packSize,
                                fontSize = 9.sp,
                                fontWeight = if (isVariantSelected) FontWeight.Black else FontWeight.SemiBold,
                                color = if (isVariantSelected) Color(0xFF047857) else EvrryMutedText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            } else {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = currentPackSize,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryMutedText,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Title & Brand
            Text(
                text = product.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryDarkCharcoal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            if (product.titleNe != null) {
                Text(
                    text = product.titleNe,
                    fontSize = 10.sp,
                    color = EvrryMutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price Row + Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rs ${currentPrice.toInt()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = EvrryDarkCharcoal
                    )
                    if (currentMrp > currentPrice) {
                        Text(
                            text = "Rs ${currentMrp.toInt()}",
                            fontSize = 10.sp,
                            color = EvrryMutedText,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                // EXPRESS QUANTITY STEPPER (Add -> [- qty +])
                if (isOutOfStock) {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
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
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF047857)),
                        modifier = Modifier.clickable {
                            if (exploreViewModel.hasConflict(activeHub.id, "grocery")) {
                                exploreViewModel.clearAndAdd(catalogItem)
                            } else {
                                exploreViewModel.addToCart(catalogItem)
                            }
                        }
                    ) {
                        Text(
                            text = "ADD",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF047857),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    // Active Stepper
                    Surface(
                        color = Color(0xFF047857),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "−",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier
                                    .clickable { exploreViewModel.removeFromCart(activeItemId) }
                                    .padding(horizontal = 6.dp)
                            )
                            Text(
                                text = "$currentQty",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            Text(
                                text = "+",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier
                                    .clickable {
                                        val canInc = if (activeVariant != null) {
                                            groceryViewModel.canIncrementVariant(activeVariant, product.maxPerOrder, currentQty)
                                        } else {
                                            groceryViewModel.canIncrement(product, currentQty)
                                        }
                                        if (canInc) {
                                            exploreViewModel.addToCart(catalogItem)
                                        }
                                    }
                                    .padding(horizontal = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Partner Supermarket Store Card Component
 * Displays physical retail supermarkets (Bhatbhateni, Big Mart, Saleways, etc.)
 * with ratings, ETA (25-45m), minimum order limit, free delivery threshold, and aisles.
 */
@Composable
fun GroceryStoreCard(
    store: GroceryStore,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Emoji icon + Name & Tagline + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFECFDF5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = store.emoji, fontSize = 26.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = store.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryDarkCharcoal
                        )
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = store.badge,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = store.tagline,
                        fontSize = 12.sp,
                        color = EvrryMutedText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Location, Distance, Rating, ETA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("📍", fontSize = 11.sp)
                    Text(
                        text = "${store.area} (${store.distanceKm} km)",
                        fontSize = 11.sp,
                        color = EvrryMutedText,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("⭐", fontSize = 10.sp)
                            Text(
                                text = "${store.rating} (${store.reviewCount})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("⚡", fontSize = 10.sp)
                            Text(
                                text = store.etaRange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                }
            }

            // Divider
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // Economics & Policy Strip: Min Order & Free Delivery
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Min Rs ${store.minOrderNpr.toInt()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EvrryDarkCharcoal
                    )
                    Text("•", fontSize = 11.sp, color = EvrryMutedText)
                    Text(
                        text = "Free above Rs ${store.freeDeliveryAboveNpr.toInt()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF047857)
                    )
                }

                Surface(
                    color = Color(0xFF047857),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Text(
                        text = "Shop Store →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Aisles Preview Chips
            if (store.aisles.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    store.aisles.take(4).forEach { aisle ->
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, EvrrySubtleBorder)
                        ) {
                            Text(
                                text = aisle,
                                fontSize = 10.sp,
                                color = EvrryMutedText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

