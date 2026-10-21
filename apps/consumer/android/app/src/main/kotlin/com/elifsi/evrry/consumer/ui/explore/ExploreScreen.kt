package com.elifsi.evrry.consumer.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.elifsi.evrry.consumer.location.AddressType
import com.elifsi.evrry.consumer.location.SavedAddress
import com.elifsi.evrry.consumer.location.SavedAddressRepository
import com.elifsi.evrry.consumer.ui.location.LocationPickerSheet
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.ExploreViewModel

/**
 * Explore Services Hub matching prototype/Phone/app/explore/page.tsx
 * Strictly limited to the 3 live verticals:
 * 1. City Rides (Cab & Bike)
 * 2. Food Delivery
 * 3. Grocery Quick Mart
 * (Hotels & Room Rentals strictly excluded from user navigation per release scope)
 * Elifsi Technologies Private Limited
 */
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onNavigateToCart: () -> Unit,
    onNavigateToFood: () -> Unit = {},
    onNavigateToGrocery: () -> Unit = {},
    onNavigateToRides: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToCamera: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val cart by viewModel.cart.collectAsState()
    val savedAddresses by SavedAddressRepository.shared.addresses.collectAsState()
    val activeAddress = savedAddresses.find { it.isDefault } ?: savedAddresses.firstOrNull()
    var showLocationPicker by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // 1. LOCATION BAR & AI CONCIERGE CHIP
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showLocationPicker = true }
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = EvrryPrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = activeAddress?.label ?: "Kathmandu Valley",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EvrryDarkCharcoal
                    )
                    Text(
                        text = if (activeAddress != null) "(${activeAddress.address.take(16)}...)" else "(Doorstep)",
                        fontSize = 11.sp,
                        color = EvrryMutedText
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Change Location",
                        tint = EvrryMutedText,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // TOP-RIGHT CART ICON WITH BADGE (1, 2, 3...)
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

                    Surface(
                        onClick = onNavigateToHome,
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = EvrryPrimaryBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "AI Concierge",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryPrimaryBlue
                            )
                        }
                    }
                }
            }
        }

        // 2. HEADER TITLE & SUBTITLE
        item {
            Column {
                Text(
                    text = "Explore Services",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryDarkCharcoal
                )
                Text(
                    text = "Rides · Food · Groceries — and more coming soon across Nepal.",
                    fontSize = 12.sp,
                    color = EvrryMutedText,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // 3. GLOBAL SEARCH BAR
        item {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = EvrryMutedText,
                        modifier = Modifier.size(20.dp)
                    )
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Search food, groceries, rides...",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = onNavigateToCamera,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan QR",
                            tint = EvrryPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 4. Spacer before verticals
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 5. VERTICAL 1: CITY RIDES (Car Icon, Blue/Indigo Gradient)
        item {
            Surface(
                onClick = onNavigateToRides,
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF2563EB), Color(0xFF4338CA))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = "Rides",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "City Rides",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryDarkCharcoal
                                )
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "EV & Moto",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Bike, Auto & Cab across Kathmandu",
                                fontSize = 11.sp,
                                color = EvrryMutedText,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "ETA",
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "3-5 min pickup",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open",
                                tint = EvrryMutedText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 6. VERTICAL 2: FOOD DELIVERY (Restaurant Icon, Orange/Amber Gradient)
        item {
            Surface(
                onClick = onNavigateToFood,
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFFED7AA), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFF97316), Color(0xFFD97706))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = "Food Delivery",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Food Delivery",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryDarkCharcoal
                                )
                                Surface(
                                    color = Color(0xFFFFF7ED),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Popular",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFC2410C),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Local kitchens, momo joints & restaurants",
                                fontSize = 11.sp,
                                color = EvrryMutedText,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "ETA",
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "20-30 min",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open",
                                tint = EvrryMutedText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 7. VERTICAL 3: GROCERY MARTS (Shopping Cart Icon, Emerald/Teal Gradient)
        item {
            Surface(
                onClick = onNavigateToGrocery,
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF059669), Color(0xFF0D9488))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Grocery",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Grocery Marts",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryDarkCharcoal
                                )
                                Surface(
                                    color = Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Fastest",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Daily essentials & farm-fresh produce",
                                fontSize = 11.sp,
                                color = EvrryMutedText,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "ETA",
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "10-15 min dispatch",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open",
                                tint = EvrryMutedText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

    }

    if (showLocationPicker) {
        LocationPickerSheet(
            title = "Choose Delivery Location",
            savedAddressRepository = SavedAddressRepository.shared,
            onLocationSelected = { loc ->
                val newAddr = SavedAddress(
                    id = "addr_${System.currentTimeMillis()}",
                    label = loc.label.ifBlank { loc.displayTitle },
                    address = loc.address ?: loc.label,
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    type = AddressType.OTHER,
                    isDefault = true
                )
                SavedAddressRepository.shared.addAddress(newAddr)
                SavedAddressRepository.shared.setDefault(newAddr.id)
                showLocationPicker = false
            },
            onDismiss = { showLocationPicker = false }
        )
    }
}
