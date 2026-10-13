package com.elifsi.evrry.consumer.ui.rides

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.elifsi.evrry.consumer.model.BookingMode
import com.elifsi.evrry.consumer.model.DriverBid
import com.elifsi.evrry.consumer.model.RideClass
import com.elifsi.evrry.consumer.model.RideStatus
import com.elifsi.evrry.consumer.ui.components.EvrryLiveTrackingMap
import com.elifsi.evrry.consumer.ui.components.EvrryTopBar
import com.elifsi.evrry.consumer.ui.components.VehicleType
import com.elifsi.evrry.consumer.ui.location.LocationPickerSheet
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.RideViewModel

/**
 * Ride Mobility & Dispatch Screen (Consumer Android)
 * Platform: Evrry Super App
 * Elifsi Technologies Private Limited
 *
 * Provides map routing, manual pickup/drop selection, vehicle tiers,
 * live dispatch tracking, OTP verification, and review modal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideBookingScreen(
    viewModel: RideViewModel,
    onBack: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onChatClick: (driverName: String, driverPhone: String) -> Unit = { _, _ -> },
    onInAppCallClick: (driverName: String, driverPhone: String) -> Unit = { _, _ -> }
) {
    val activeRide by viewModel.activeRide.collectAsState()
    val isBooking by viewModel.isBooking.collectAsState()
    val isRouting by viewModel.isRouting.collectAsState()
    val context = LocalContext.current

    var pickerMode by remember { mutableStateOf<String?>(null) } // "pickup" or "drop"
    var showRatingSheet by remember { mutableStateOf(false) }
    var showCallChooserSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
    ) {
        // TOP APP BAR
        EvrryTopBar(
            title = "Book a Ride",
            canGoBack = true,
            onBack = onBack,
            onProfileClick = onProfileClick
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. LIVE ROUTE MAP & 3D ISOMETRIC VEHICLE
            item {
                EvrryLiveTrackingMap(
                    vendorName = activeRide.pickupAddress.substringBefore(","),
                    destinationAddress = activeRide.dropAddress.substringBefore(","),
                    vehicleType = when (activeRide.rideClass) {
                        RideClass.MOTO -> VehicleType.MOTORCYCLE
                        RideClass.CAB, RideClass.COMFORT -> VehicleType.CAB
                    },
                    riderName = activeRide.driver?.name ?: "Bikash Tamang",
                    etaMinutes = activeRide.durationMinutes,
                    speedKmh = if (activeRide.status == RideStatus.ACCEPTED) 34 else 26
                )
            }

            // 2. ROUTE SELECTION CARD (Pickup, Drop & OSRM Distance)
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // PICKUP ROW
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (activeRide.status == RideStatus.IDLE || activeRide.status == RideStatus.SEARCHING) {
                                        pickerMode = "pickup"
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(EvrryGreenSuccess)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text("PICKUP LOCATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                Text(
                                    text = activeRide.pickupAddress,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EvrryDarkCharcoal,
                                    maxLines = 1
                                )
                            }
                            if (activeRide.status == RideStatus.IDLE) {
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Change",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryPrimaryBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // DIVIDER WITH SWAP BUTTON
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = EvrrySubtleBorder,
                                thickness = 1.dp
                            )
                            if (activeRide.status == RideStatus.IDLE) {
                                IconButton(
                                    onClick = { viewModel.swapPickupAndDrop() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.SwapVert,
                                        contentDescription = "Swap",
                                        tint = EvrryMutedText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = EvrrySubtleBorder,
                                    thickness = 1.dp
                                )
                            }
                        }

                        // DESTINATION ROW
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (activeRide.status == RideStatus.IDLE || activeRide.status == RideStatus.SEARCHING) {
                                        pickerMode = "drop"
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(EvrryRedDestructive)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text("DESTINATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                Text(
                                    text = activeRide.dropAddress,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EvrryDarkCharcoal,
                                    maxLines = 1
                                )
                            }
                            if (activeRide.status == RideStatus.IDLE) {
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Change",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryPrimaryBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // OSRM ROUTE DISTANCE & DURATION PILL
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEFF6FF))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isRouting) {
                                Text("Calculating road route…", fontSize = 11.sp, color = EvrryPrimaryBlue)
                                CircularProgressIndicator(color = EvrryPrimaryBlue, modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp)
                            } else {
                                Text("🛣️ Distance: ${activeRide.distanceKm} km", fontSize = 11.sp, color = EvrryPrimaryBlue, fontWeight = FontWeight.Bold)
                                Text("⏱️ ETA: ~${activeRide.durationMinutes} mins", fontSize = 11.sp, color = EvrryPrimaryBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 3. LIFECYCLE STATES
            when (activeRide.status) {
                RideStatus.SEARCHING -> {
                    item {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(20.dp),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, EvrryPrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                if (activeRide.bookingMode == BookingMode.BIDDING && activeRide.driverBids.isNotEmpty()) {
                                    // IN-DRIVE BIDDING: INCOMING DRIVER BIDS LIST
                                    Text(
                                        text = "Nearby Drivers Responded (${activeRide.driverBids.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = EvrryDarkCharcoal
                                    )
                                    Text(
                                        text = "Your Offer: Rs ${activeRide.offeredFareNpr.toInt()} · Choose your driver below",
                                        fontSize = 12.sp,
                                        color = EvrryMutedText
                                    )

                                    activeRide.driverBids.forEach { bid ->
                                        Surface(
                                            color = Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(1.dp, if (!bid.isCounterOffer) EvrryGreenSuccess else EvrryPrimaryBlue, RoundedCornerShape(14.dp))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Surface(
                                                        color = EvrryPrimaryBlue.copy(alpha = 0.1f),
                                                        shape = CircleShape,
                                                        modifier = Modifier.size(40.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text("👨‍✈️", fontSize = 20.sp)
                                                        }
                                                    }
                                                    Column {
                                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                            Text(bid.driver.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EvrryDarkCharcoal)
                                                            if (bid.driver.isVerified) {
                                                                Icon(
                                                                    imageVector = Icons.Default.CheckCircle,
                                                                    contentDescription = "Verified Driver",
                                                                    tint = EvrryPrimaryBlue,
                                                                    modifier = Modifier.size(13.dp)
                                                                )
                                                            }
                                                        }
                                                        Text("${bid.driver.vehicleModel} · ⭐ ${bid.driver.rating}", fontSize = 11.sp, color = EvrryMutedText)
                                                        Text("~${bid.etaMinutes} min away", fontSize = 10.sp, color = EvrryPrimaryBlue, fontWeight = FontWeight.SemiBold)
                                                    }
                                                }

                                                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Surface(
                                                        color = if (!bid.isCounterOffer) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                                        shape = RoundedCornerShape(6.dp)
                                                    ) {
                                                        Text(
                                                            text = if (!bid.isCounterOffer) "Accepted Rs ${bid.bidFareNpr.toInt()}" else "Counter Rs ${bid.bidFareNpr.toInt()}",
                                                            color = if (!bid.isCounterOffer) Color(0xFF166534) else Color(0xFFB45309),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }

                                                    Button(
                                                        onClick = { viewModel.acceptDriverBid(bid) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                                                        shape = RoundedCornerShape(10.dp),
                                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                                    ) {
                                                        Text("Accept", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.cancelRide() },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EvrryRedDestructive),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Cancel Request", fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    // STANDARD SEARCHING / BROADCASTING SPINNER
                                    CircularProgressIndicator(
                                        color = EvrryPrimaryBlue,
                                        modifier = Modifier.size(42.dp),
                                        strokeWidth = 3.dp
                                    )
                                    Text(
                                        text = if (activeRide.bookingMode == BookingMode.BIDDING)
                                            "Broadcasting Your Offer of Rs ${activeRide.offeredFareNpr.toInt()}…"
                                        else
                                            "Connecting to Nearby Drivers…",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = EvrryDarkCharcoal
                                    )
                                    Text(
                                        text = if (activeRide.bookingMode == BookingMode.BIDDING)
                                            "Waiting for drivers in Thamel / Patan corridor to accept or counter-bid"
                                        else
                                            "Dispatching request for ${activeRide.rideClass.title} in Thamel / Patan corridor",
                                        fontSize = 12.sp,
                                        color = EvrryMutedText
                                    )
                                    OutlinedButton(
                                        onClick = { viewModel.cancelRide() },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EvrryRedDestructive)
                                    ) {
                                        Text("Cancel Request", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                RideStatus.ACCEPTED -> {
                    item {
                        val driver = activeRide.driver ?: return@item
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(20.dp),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, EvrryGreenSuccess, RoundedCornerShape(20.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // DRIVER PROFILE & OTP BADGE
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            color = EvrryPrimaryBlue.copy(alpha = 0.1f),
                                            shape = CircleShape,
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("👨‍✈️", fontSize = 24.sp)
                                            }
                                        }
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(driver.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EvrryDarkCharcoal)
                                                if (driver.isVerified) {
                                                    Surface(
                                                        color = Color(0xFFEFF6FF),
                                                        shape = RoundedCornerShape(6.dp)
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.CheckCircle,
                                                                contentDescription = "Verified Driver",
                                                                tint = EvrryPrimaryBlue,
                                                                modifier = Modifier.size(11.dp)
                                                            )
                                                            Text("Verified", color = EvrryPrimaryBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            }
                                            Text("${driver.vehicleModel} · ${driver.vehiclePlate}", fontSize = 12.sp, color = EvrryMutedText)
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text("⭐ ${driver.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                                Text("(${driver.tripsCompleted} trips)", fontSize = 11.sp, color = EvrryMutedText)
                                            }
                                        }
                                    }

                                    // 4-DIGIT PASSENGER START OTP
                                    Surface(
                                        color = Color(0xFFEFF6FF),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.border(1.5.dp, EvrryPrimaryBlue, RoundedCornerShape(12.dp))
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("TRIP OTP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                                            Text(activeRide.otpCode, fontSize = 18.sp, fontWeight = FontWeight.Black, color = EvrryPrimaryBlue)
                                        }
                                    }
                                }

                                // DRIVER ACTION BUTTONS (CALL · CHAT · COMPLETE)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showCallChooserSheet = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Rounded.Call, contentDescription = "Call", tint = EvrryPrimaryBlue, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Call", color = EvrryPrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { onChatClick(driver.name, driver.phone) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Email, contentDescription = "Chat", tint = EvrryPrimaryBlue, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Chat", color = EvrryPrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { viewModel.completeRide() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDCFCE7)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1.2f),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = "Finish", tint = Color(0xFF166534), modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Complete", color = Color(0xFF166534), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                RideStatus.COMPLETED -> {
                    item {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(20.dp),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, EvrryGreenSuccess, RoundedCornerShape(20.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDCFCE7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Done", tint = Color(0xFF166534), modifier = Modifier.size(28.dp))
                                }
                                Text("Trip Completed!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EvrryDarkCharcoal)
                                Text("Fare: Rs ${activeRide.estimatedFareNpr.toInt()} · Distance: ${activeRide.distanceKm} km", fontSize = 13.sp, color = EvrryMutedText)

                                if (!activeRide.reviewSubmitted) {
                                    Button(
                                        onClick = { showRatingSheet = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Rate & Review Driver ⭐", fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Review Submitted! Thank you.", color = Color(0xFF166534), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                                    }
                                }

                                OutlinedButton(
                                    onClick = { viewModel.resetRide() },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Book Another Ride", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                RideStatus.CANCELLED -> {
                    item {
                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, EvrryRedDestructive.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("Trip Cancelled", color = EvrryRedDestructive, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                OutlinedButton(
                                    onClick = { viewModel.resetRide() },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Reset", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                RideStatus.IDLE, RideStatus.ARRIVED, RideStatus.TRIP_ACTIVE -> {
                    // 4. BOOKING MODE SELECTOR (Fixed Rate vs InDrive Bidding)
                    item {
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    color = if (activeRide.bookingMode == BookingMode.FIXED_FARE) Color.White else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp),
                                    shadowElevation = if (activeRide.bookingMode == BookingMode.FIXED_FARE) 2.dp else 0.dp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setBookingMode(BookingMode.FIXED_FARE) }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = "⚡ Fixed Rate",
                                            fontWeight = if (activeRide.bookingMode == BookingMode.FIXED_FARE) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = if (activeRide.bookingMode == BookingMode.FIXED_FARE) EvrryPrimaryBlue else EvrryMutedText
                                        )
                                    }
                                }

                                Surface(
                                    color = if (activeRide.bookingMode == BookingMode.BIDDING) Color.White else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp),
                                    shadowElevation = if (activeRide.bookingMode == BookingMode.BIDDING) 2.dp else 0.dp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setBookingMode(BookingMode.BIDDING) }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = "🤝 InDrive Bidding",
                                            fontWeight = if (activeRide.bookingMode == BookingMode.BIDDING) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = if (activeRide.bookingMode == BookingMode.BIDDING) EvrryPrimaryBlue else EvrryMutedText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 5. VEHICLE TIERS LIST (Moto, Cab, Comfort EV)
                    item {
                        Text(
                            text = "CHOOSE VEHICLE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryMutedText
                        )
                    }

                    items(RideClass.values().size) { index ->
                        val rClass = RideClass.values()[index]
                        val isSelected = activeRide.rideClass == rClass
                        val fare = rClass.baseFareNpr + (rClass.perKmRateNpr * activeRide.distanceKm)

                        Surface(
                            color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) EvrryPrimaryBlue else EvrrySubtleBorder,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable { viewModel.selectClass(rClass) }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Vehicle visual badge
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) Color.White else Color(0xFFF1F5F9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(rClass.emoji, fontSize = 26.sp)
                                    }

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(rClass.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EvrryDarkCharcoal)
                                            if (isSelected) {
                                                Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = "Selected", tint = EvrryPrimaryBlue, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                        Text("${rClass.capacity} · ~${activeRide.durationMinutes} min ETA", fontSize = 11.sp, color = EvrryMutedText)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Rs ${fare.toInt()}", fontWeight = FontWeight.Black, fontSize = 17.sp, color = EvrryDarkCharcoal)
                                    Text("Rs ${rClass.perKmRateNpr.toInt()}/km", fontSize = 10.sp, color = EvrryMutedText)
                                }
                            }
                        }
                    }

                    // 6. INDRIVE BIDDING FARE OFFER CARD
                    if (activeRide.bookingMode == BookingMode.BIDDING) {
                        item {
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.5.dp, Color(0xFF3B82F6), RoundedCornerShape(18.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("OFFER YOUR PRICE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                                            Text("Recommended: Rs ${activeRide.estimatedFareNpr.toInt()}", fontSize = 12.sp, color = EvrryMutedText)
                                        }
                                        Surface(
                                            color = Color(0xFFEFF6FF),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Negotiable", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }

                                    // FARE STEPPER
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                                    ) {
                                        FilledIconButton(
                                            onClick = { viewModel.updateOfferedFare(-10.0) },
                                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFEFF6FF), contentColor = EvrryPrimaryBlue),
                                            modifier = Modifier.size(46.dp)
                                        ) {
                                            Text("-10", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "Rs ${activeRide.offeredFareNpr.toInt()}",
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Black,
                                                color = EvrryDarkCharcoal
                                            )
                                            Text("Your Offer", fontSize = 11.sp, color = EvrryMutedText)
                                        }

                                        FilledIconButton(
                                            onClick = { viewModel.updateOfferedFare(10.0) },
                                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFEFF6FF), contentColor = EvrryPrimaryBlue),
                                            modifier = Modifier.size(46.dp)
                                        ) {
                                            Text("+10", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                    }

                                    // QUICK PRESET CHIPS
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            color = Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { viewModel.updateOfferedFare(-20.0) }
                                                .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(10.dp))
                                        ) {
                                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                                Text("- Rs 20", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                                            }
                                        }

                                        Surface(
                                            color = Color(0xFFEFF6FF),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { viewModel.setCustomOfferedFare(activeRide.estimatedFareNpr) }
                                                .border(1.dp, EvrryPrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                        ) {
                                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                                Text("Fair Rate", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                                            }
                                        }

                                        Surface(
                                            color = Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { viewModel.updateOfferedFare(20.0) }
                                                .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(10.dp))
                                        ) {
                                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                                Text("+ Rs 20", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
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

        // 7. BOTTOM BOOK / OFFER CTA (Visible when in IDLE)
        if (activeRide.status == RideStatus.IDLE) {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { viewModel.requestRide() },
                        enabled = !isBooking,
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (isBooking) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = if (activeRide.bookingMode == BookingMode.BIDDING)
                                    "Broadcast Offer · Rs ${activeRide.offeredFareNpr.toInt()}"
                                else
                                    "Book ${activeRide.rideClass.title} · Rs ${activeRide.estimatedFareNpr.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // MODAL BOTTOM SHEETS
    if (pickerMode != null) {
        LocationPickerSheet(
            title = if (pickerMode == "pickup") "Select Pickup Location" else "Select Destination",
            onLocationSelected = { point ->
                if (pickerMode == "pickup") {
                    viewModel.setPickup(point)
                } else {
                    viewModel.setDrop(point)
                }
                pickerMode = null
            },
            onDismiss = { pickerMode = null }
        )
    }

    if (showRatingSheet && activeRide.driver != null) {
        RideRatingSheet(
            driver = activeRide.driver!!,
            fareNpr = activeRide.estimatedFareNpr,
            onSubmitReview = { rating, comment ->
                viewModel.submitReview(rating, comment)
                showRatingSheet = false
            },
            onDismiss = { showRatingSheet = false }
        )
    }

    if (showCallChooserSheet && activeRide.driver != null) {
        val driver = activeRide.driver!!
        ModalBottomSheet(
            onDismissRequest = { showCallChooserSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = EvrryPrimaryBlue.copy(alpha = 0.1f),
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("📞", fontSize = 22.sp)
                        }
                    }
                    Column {
                        Text(
                            text = "Call ${driver.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = EvrryDarkCharcoal
                        )
                        Text(
                            text = "Choose your preferred calling method",
                            fontSize = 12.sp,
                            color = EvrryMutedText
                        )
                    }
                }

                HorizontalDivider(color = EvrrySubtleBorder)

                // OPTION 1: CALL IN APP (FREE WEBRTC VOIP CALL)
                Surface(
                    onClick = {
                        showCallChooserSheet = false
                        onInAppCallClick(driver.name, driver.phone)
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EvrryPrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            color = EvrryPrimaryBlue,
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Rounded.Call, contentDescription = "App Call", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Call in App", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EvrryDarkCharcoal)
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("FREE · VOIP", color = Color(0xFF166534), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                }
                            }
                            Text("Free internet audio call · Phone number hidden", fontSize = 12.sp, color = EvrryMutedText)
                        }
                    }
                }

                // OPTION 2: DIRECT SIM CALL (STANDARD CELLULAR CARRIER)
                Surface(
                    onClick = {
                        showCallChooserSheet = false
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${driver.phone}"))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            color = Color(0xFF64748B),
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = "SIM Call", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Direct SIM Call", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EvrryDarkCharcoal)
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("CARRIER", color = Color(0xFFB45309), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                }
                            }
                            Text("Standard cellular call · ${driver.phone}", fontSize = 12.sp, color = EvrryMutedText)
                        }
                    }
                }

                OutlinedButton(
                    onClick = { showCallChooserSheet = false },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold, color = EvrryMutedText)
                }
            }
        }
    }
}
