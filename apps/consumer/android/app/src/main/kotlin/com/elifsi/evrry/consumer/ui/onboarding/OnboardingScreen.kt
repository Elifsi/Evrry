package com.elifsi.evrry.consumer.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

import com.elifsi.evrry.consumer.location.AddressType
import com.elifsi.evrry.consumer.location.LocationPoint
import com.elifsi.evrry.consumer.location.LocationService
import com.elifsi.evrry.consumer.location.SavedAddress
import com.elifsi.evrry.consumer.location.SavedAddressRepository
import com.elifsi.evrry.consumer.ui.location.LocationPickerSheet
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

/**
 * Dynamic DoorDash-Grade Onboarding Walkthrough Screen
 * Battery-optimized, zero unnecessary recompositions, native 60fps animations.
 * Covers all 5 core verticals (Food, Grocery, Rides, E2EE Chat, Stays/Rentals)
 * Includes Doorstep Location Setup (GPS detection + Map search)
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: AuthViewModel,
    pages: List<OnboardingPage> = OnboardingRepository.defaultPages,
    onComplete: () -> Unit = { viewModel.finishOnboarding() }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showLocationSetup by remember { mutableStateOf(false) }
    var showLocationPickerSheet by remember { mutableStateOf(false) }
    val locationService = remember { LocationService() }

    var selectedHomeLocation by remember {
        mutableStateOf(
            SavedAddress(
                id = "addr_home",
                label = "Home",
                address = "Sanepa Heights, Lalitpur",
                latitude = 27.6841,
                longitude = 85.3088,
                type = AddressType.HOME,
                isDefault = true
            )
        )
    }
    var isDetectingLocation by remember { mutableStateOf(false) }

    fun finalizeOnboarding() {
        SavedAddressRepository.shared.setHomeAddress(selectedHomeLocation)
        onComplete()
    }

    if (showLocationSetup) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FBFF))
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // HEADER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = EvrryPrimaryBlue,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "e",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Text(
                            text = "evrry",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = EvrryDarkCharcoal
                        )
                    }

                    TextButton(
                        onClick = { finalizeOnboarding() },
                        colors = ButtonDefaults.textButtonColors(contentColor = EvrryMutedText)
                    ) {
                        Text(
                            text = "Skip",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // HERO ICON
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier
                        .size(80.dp)
                        .border(2.dp, Color(0xFFBFDBFE), CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Doorstep Location",
                            tint = EvrryPrimaryBlue,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Where should we deliver?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EvrryDarkCharcoal,
                    lineHeight = 30.sp
                )

                Text(
                    text = "Set your primary doorstep location for lightning-fast food, grocery delivery, and ride pickups across Nepal. You can always manage your addresses in your Address Book.",
                    fontSize = 13.sp,
                    color = EvrryMutedText,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // CURRENT SELECTED LOCATION CARD
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, EvrryPrimaryBlue, RoundedCornerShape(18.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDCFCE7),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🏠", fontSize = 22.sp)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = selectedHomeLocation.label,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryDarkCharcoal
                                )
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Primary Home",
                                        color = Color(0xFF166534),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = selectedHomeLocation.address,
                                fontSize = 12.sp,
                                color = EvrryMutedText,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ACTION BUTTON 1: AUTO-DETECT GPS LOCATION
                OutlinedButton(
                    onClick = {
                        isDetectingLocation = true
                        coroutineScope.launch {
                            val pt = locationService.getCurrentLocation(context)
                            selectedHomeLocation = SavedAddress(
                                id = "addr_home",
                                label = "Home",
                                address = pt.address.ifEmpty { pt.label },
                                latitude = pt.latitude,
                                longitude = pt.longitude,
                                type = AddressType.HOME,
                                isDefault = true
                            )
                            isDetectingLocation = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EvrryPrimaryBlue)
                ) {
                    if (isDetectingLocation) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = EvrryPrimaryBlue, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Detecting current location…", color = EvrryPrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    } else {
                        Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Use My Current Location (GPS)", color = EvrryPrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ACTION BUTTON 2: SEARCH ON MAP
                OutlinedButton(
                    onClick = { showLocationPickerSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EvrrySubtleBorder)
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = EvrryDarkCharcoal, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Search Area / Street on Map", color = EvrryDarkCharcoal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(22.dp))

                // POPULAR NEIGHBORHOOD CHIPS
                Text(
                    text = "POPULAR NEIGHBORHOODS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryMutedText
                )

                Spacer(modifier = Modifier.height(8.dp))

                val popularPlaces = listOf(
                    Triple("Sanepa Heights", "Sanepa, Lalitpur", LocationPoint("Sanepa", "Sanepa Heights, Lalitpur", 27.6841, 85.3088)),
                    Triple("Jhamsikhel", "Jhamsikhel, Lalitpur", LocationPoint("Jhamsikhel", "Jhamsikhel, Lalitpur", 27.6830, 85.3120)),
                    Triple("Durbar Marg", "Durbar Marg, Kathmandu", LocationPoint("Durbar Marg", "Durbar Marg, Kathmandu", 27.7118, 85.3168)),
                    Triple("Thamel", "Thamel, Kathmandu", LocationPoint("Thamel", "Thamel, Kathmandu", 27.7154, 85.3123)),
                    Triple("New Baneshwor", "New Baneshwor, Kathmandu", LocationPoint("Baneshwor", "New Baneshwor, Kathmandu", 27.6915, 85.3420)),
                    Triple("Pokhara Lakeside", "Lakeside, Pokhara", LocationPoint("Lakeside", "Lakeside, Pokhara", 28.2096, 83.9595))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    popularPlaces.take(3).forEach { (label, addr, pt) ->
                        val isSelected = selectedHomeLocation.address.contains(label)
                        Surface(
                            color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, if (isSelected) EvrryPrimaryBlue else EvrrySubtleBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedHomeLocation = SavedAddress(
                                        id = "addr_home",
                                        label = "Home",
                                        address = addr,
                                        latitude = pt.latitude,
                                        longitude = pt.longitude,
                                        type = AddressType.HOME,
                                        isDefault = true
                                    )
                                }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) EvrryPrimaryBlue else EvrryDarkCharcoal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // BOTTOM CONFIRM BUTTON
            Button(
                onClick = { finalizeOnboarding() },
                colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Confirm Home Location & Continue",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }
            }

            if (showLocationPickerSheet) {
                LocationPickerSheet(
                    title = "Select Home Location",
                    onLocationSelected = { pt ->
                        selectedHomeLocation = SavedAddress(
                            id = "addr_home",
                            label = "Home",
                            address = pt.address.ifEmpty { pt.label },
                            latitude = pt.latitude,
                            longitude = pt.longitude,
                            type = AddressType.HOME,
                            isDefault = true
                        )
                        showLocationPickerSheet = false
                    },
                    onDismiss = { showLocationPickerSheet = false }
                )
            }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val currentPage = pages[pagerState.currentPage]

    val backgroundColor by animateColorAsState(
        targetValue = currentPage.backgroundColor,
        animationSpec = tween(durationMillis = 350),
        label = "bgAnim"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP HEADER: Brand + Skip Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = EvrryPrimaryBlue,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "e",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
                Text(
                    text = "evrry",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = EvrryDarkCharcoal
                )
            }

            TextButton(
                onClick = { showLocationSetup = true },
                colors = ButtonDefaults.textButtonColors(contentColor = EvrryMutedText)
            ) {
                Text(
                    text = "Skip",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }

        // CENTER CAROUSEL (HorizontalPager)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Interactive Card Artwork
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(32.dp), spotColor = page.accentColor.copy(alpha = 0.25f))
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color.White, page.backgroundColor)
                            )
                        )
                        .border(1.5.dp, page.accentColor.copy(alpha = 0.2f), RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Central Icon Hero
                    Surface(
                        modifier = Modifier.size(96.dp),
                        shape = RoundedCornerShape(26.dp),
                        color = page.accentColor.copy(alpha = 0.12f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = page.icon,
                                contentDescription = page.title,
                                modifier = Modifier.size(54.dp),
                                tint = page.accentColor
                            )
                        }
                    }

                    // Floating Live Metric Badge
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-16).dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(page.accentColor)
                            )
                            Text(
                                text = "${page.previewMetric} · ${page.previewMetricLabel}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EvrryDarkCharcoal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Badge Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = page.accentColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = page.badge,
                        color = page.accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                Text(
                    text = page.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EvrryDarkCharcoal,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                Text(
                    text = page.subtitle,
                    fontSize = 13.sp,
                    color = EvrryMutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Highlight Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    page.highlights.take(2).forEach { highlight ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, page.accentColor.copy(alpha = 0.2f)),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp),
                                    tint = page.accentColor
                                )
                                Text(
                                    text = highlight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EvrryDarkCharcoal
                                )
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM CONTROLS: Dot Indicator + Next CTA
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // DoorDash-style Expanding Dots Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.indices.forEach { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = tween(durationMillis = 250),
                        label = "dotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isSelected) currentPage.accentColor else Color(0xFFCBD5E1)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }

            // Action Button
            val isLastPage = pagerState.currentPage == pages.lastIndex
            Button(
                onClick = {
                    if (isLastPage) {
                        showLocationSetup = true
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLastPage) EvrryPrimaryBlue else currentPage.accentColor
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isLastPage) "Get Started with Evrry" else "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
