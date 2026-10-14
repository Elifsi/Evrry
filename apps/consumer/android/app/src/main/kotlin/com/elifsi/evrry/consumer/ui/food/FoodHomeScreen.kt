package com.elifsi.evrry.consumer.ui.food

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.elifsi.evrry.consumer.model.Restaurant
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.ExploreViewModel
import com.elifsi.evrry.consumer.viewmodel.FoodViewModel

@Composable
fun FoodHomeScreen(
    foodViewModel: FoodViewModel,
    exploreViewModel: ExploreViewModel,
    onBack: () -> Unit,
    onRestaurantClick: (String) -> Unit,
    onNavigateToCart: () -> Unit
) {
    val searchQuery by foodViewModel.searchQuery.collectAsState()
    val selectedCuisine by foodViewModel.selectedCuisine.collectAsState()
    val activeFilters by foodViewModel.activeFilters.collectAsState()
    val cart by exploreViewModel.cart.collectAsState()

    val filteredRestaurants = remember(searchQuery, selectedCuisine, activeFilters) {
        foodViewModel.getFilteredRestaurants()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (cart.itemCount > 0) 100.dp else 24.dp)
        ) {
            // TOP NAVIGATION & IN-APP BACK BUTTON (iOS parity)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
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
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("⚡", fontSize = 11.sp)
                                Text(
                                    text = "25-35 min",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC2410C)
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
            }

            // HEADER & LOCATION
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("📍", fontSize = 13.sp)
                        Text(
                            text = "Delivering to: Jhamsikhel, Lalitpur",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryDarkCharcoal
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Food Delivery",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = EvrryDarkCharcoal,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Order from Kathmandu & Lalitpur's top kitchens • Authentic local taste",
                        fontSize = 12.sp,
                        color = EvrryMutedText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Specialized Delivery vs Pickup toggle
                    FulfillmentModeSwitcher(
                        selectedType = cart.fulfillmentType,
                        onTypeSelected = { exploreViewModel.setFulfillmentType(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // PROMOTIONAL PERKS BANNER
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFF97316), Color(0xFFD97706))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "🏷️ EVRRY Food Fest",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "20% OFF on Roadhouse & Jimbu",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Use code: NEPAL20 • Free delivery on Rs 1,000+",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Text("🍕", fontSize = 38.sp)
                        }
                    }
                }
            }

            // SEARCH BAR
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = EvrryMutedText,
                            modifier = Modifier.size(18.dp)
                        )
                        TextField(
                            value = searchQuery,
                            onValueChange = { foodViewModel.setSearchQuery(it) },
                            placeholder = {
                                Text(
                                    "Search restaurants, cuisines, momo, pizza...",
                                    fontSize = 12.sp,
                                    color = EvrryMutedText
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = EvrryMutedText,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { foodViewModel.setSearchQuery("") }
                            )
                        }
                    }
                }
            }

            // CUISINE HORIZONTAL SCROLL CAROUSEL
            item {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "WHAT ARE YOU CRAVING?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = EvrryMutedText,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        foodViewModel.cuisines.forEach { c ->
                            val isSelected = selectedCuisine == c.id
                            Surface(
                                color = if (isSelected) Color(0xFFF97316) else Color.White,
                                shape = RoundedCornerShape(16.dp),
                                shadowElevation = if (isSelected) 2.dp else 0.dp,
                                modifier = Modifier
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFFF97316) else EvrrySubtleBorder,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { foodViewModel.selectCuisine(c.id) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = c.emoji, fontSize = 22.sp)
                                    Text(
                                        text = c.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.White else EvrryDarkCharcoal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // QUICK FILTER CHIPS
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    foodViewModel.quickFilters.forEach { f ->
                        val isSelected = activeFilters.contains(f.id)
                        Surface(
                            color = if (isSelected) Color(0xFFFFF7ED) else Color.White,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFFF97316) else EvrrySubtleBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { foodViewModel.toggleFilter(f.id) }
                        ) {
                            Text(
                                text = f.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color(0xFFEA580C) else EvrryDarkCharcoal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // OPEN RESTAURANTS HEADER
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OPEN RESTAURANTS (${filteredRestaurants.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = EvrryMutedText,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Sorted by Rating & Distance",
                        fontSize = 11.sp,
                        color = EvrryMutedText
                    )
                }
            }

            // RESTAURANT CARDS OR EMPTY STATE
            if (filteredRestaurants.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🍽️", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No kitchens match your filter",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryDarkCharcoal
                        )
                        Text(
                            text = "Try searching for Momo, Thakali, or reset filters.",
                            fontSize = 12.sp,
                            color = EvrryMutedText,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { foodViewModel.resetFilters() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Reset Filters", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(filteredRestaurants) { restaurant ->
                    RestaurantCardItem(
                        restaurant = restaurant,
                        onClick = { onRestaurantClick(restaurant.id) }
                    )
                }
            }
        }

        // FLOATING CART BAR
        if (cart.itemCount > 0) {
            Surface(
                color = EvrryDarkCharcoal,
                shape = RoundedCornerShape(18.dp),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .clickable { onNavigateToCart() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.White
                        )
                        Text(
                            text = "${cart.itemCount} items in Cart",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "View Cart · Rs ${cart.totalNpr.toInt()} →",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RestaurantCardItem(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder)
    ) {
        Column {
            // Gradient Header with Restaurant Accent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(restaurant.gradientFrom), Color(restaurant.gradientTo))
                        )
                    )
            ) {
                if (!restaurant.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = if (restaurant.imageUrl.startsWith("http")) restaurant.imageUrl else "https://vsdkaijsiuzommgqibse.supabase.co/storage/v1/object/public/catalog/${restaurant.imageUrl}",
                        contentDescription = restaurant.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.45f), Color.Black.copy(alpha = 0.2f))
                                )
                            )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    if (restaurant.isPureVeg) {
                        Surface(
                            color = Color(0xFF16A34A),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "PURE VEG",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    // Rating Badge
                    Surface(
                        color = Color(0xFF16A34A),
                        shape = RoundedCornerShape(8.dp)
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
                }
            }

            // Content Area
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = restaurant.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryDarkCharcoal
                )
                Text(
                    text = restaurant.cuisines.joinToString(" • "),
                    fontSize = 12.sp,
                    color = EvrryMutedText,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "🕒 ${restaurant.deliveryEtaMinutes} min",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = EvrryDarkCharcoal
                        )
                        Text(
                            text = "📍 ${restaurant.area}",
                            fontSize = 11.sp,
                            color = EvrryMutedText
                        )
                    }
                    Text(
                        text = "Rs ${restaurant.priceForTwo} for two",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EvrryDarkCharcoal
                    )
                }

                if (restaurant.offer != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFFFFF7ED),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🏷️", fontSize = 10.sp)
                            Text(
                                text = restaurant.offer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2410C)
                            )
                        }
                    }
                }
            }
        }
    }
}
