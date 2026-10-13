package com.elifsi.evrry.partner.ui.driver

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
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
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.DriverRideStatus
import com.elifsi.evrry.partner.model.PartnerRole
import com.elifsi.evrry.partner.model.RideRequest
import com.elifsi.evrry.partner.ui.chat.PartnerChatRoomScreen
import com.elifsi.evrry.partner.ui.components.PartnerMapHUD
import com.elifsi.evrry.partner.ui.components.PartnerTopBar
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.DriverMobilityViewModel

/**
 * Complete Mobility Ride Driver HUD Screen (Partner Android)
 * Platform: Evrry Super App
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverHudScreen(
    viewModel: DriverMobilityViewModel,
    onRoleSelected: (PartnerRole) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.activeRole.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val locationState by viewModel.locationState.collectAsState()
    val openRequests by viewModel.openRequests.collectAsState()
    val activeRide by viewModel.activeRide.collectAsState()
    val startOtpInput by viewModel.startOtpInput.collectAsState()
    val otpError by viewModel.otpError.collectAsState()
    val fareBreakdown by viewModel.fareBreakdown.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val todayEarnings by viewModel.todayEarningsNpr.collectAsState()

    var showCallSheet by remember { mutableStateOf(false) }
    var showChatScreen by remember { mutableStateOf(false) }
    var selectedVoipNotice by remember { mutableStateOf<String?>(null) }

    if (showChatScreen && activeRide != null) {
        val ride = activeRide!!
        PartnerChatRoomScreen(
            passengerName = ride.passengerName,
            passengerPhone = ride.passengerPhone,
            rideId = ride.id,
            messages = chatMessages,
            onSendMessage = { viewModel.sendChatMessage(it) },
            onBack = { showChatScreen = false },
            onCallClick = { showCallSheet = true }
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
    ) {
        // 1. TOP BAR WITH 1-TAP ROLE SWITCHER & ONLINE TOGGLE
        PartnerTopBar(
            currentRole = currentRole,
            isOnline = isOnline,
            onToggleOnline = { viewModel.toggleOnline(it) },
            onRoleSelected = { role ->
                viewModel.switchRole(role)
                onRoleSelected(role)
            },
            todayEarningsNpr = todayEarnings
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. INTERACTIVE NAVIGATION MAP HUD WITH POLYLINE TRACKING
            item {
                val ride = activeRide ?: openRequests.firstOrNull()
                PartnerMapHUD(
                    pickupAddress = ride?.pickupAddress ?: "Thamel Marg, Kathmandu",
                    dropAddress = ride?.dropAddress ?: "Patan Durbar Square",
                    vehicleType = locationState.vehicleType,
                    bearingDegrees = locationState.bearing,
                    speedKmh = locationState.speedKmh,
                    etaMinutes = ride?.durationMinutes ?: 15,
                    status = activeRide?.status ?: DriverRideStatus.BIDDING
                )
            }

            // 3. OFFLINE STANDBY BANNER
            if (!isOnline) {
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                            .padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("😴", fontSize = 36.sp)
                            Text("You are Currently Offline", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EvrryDarkCharcoal)
                            Text(
                                "Go online in the top bar to broadcast your live GPS to Kathmandu riders and receive nearby trip requests.",
                                fontSize = 12.sp,
                                color = EvrryMutedText,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.toggleOnline(true) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EvrryGreenSuccess)
                            ) {
                                Text("Go Online Now ⚡", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 4. ACTIVE TRIP LIFECYCLE (ACCEPTED -> ARRIVED -> IN_PROGRESS -> COMPLETED)
            if (activeRide != null) {
                val ride = activeRide!!

                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, EvrryPrimaryBlue, RoundedCornerShape(20.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Trip Status Header & Passenger Profile
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(EvrryPrimaryBlue.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("👤", fontSize = 22.sp)
                                    }
                                    Column {
                                        Text(ride.passengerName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EvrryDarkCharcoal)
                                        Text("⭐ ${ride.passengerRating} · ${ride.passengerPhone}", fontSize = 11.sp, color = EvrryMutedText)
                                    }
                                }

                                Surface(
                                    color = Color(ride.status.badgeColor).copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = ride.status.label,
                                        color = Color(ride.status.badgeColor),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // Route Addresses Card
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EvrryGreenSuccess))
                                        Column {
                                            Text("PICKUP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                            Text(ride.pickupAddress, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                                        }
                                    }
                                    HorizontalDivider(color = EvrrySubtleBorder)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EvrryRedDestructive))
                                        Column {
                                            Text("DROP-OFF", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                            Text(ride.dropAddress, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                                        }
                                    }
                                }
                            }

                            // Action Buttons: Chat & Dual Call
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showCallSheet = true },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Rounded.Call, contentDescription = "Call", tint = EvrryPrimaryBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call", color = EvrryPrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { showChatScreen = true },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Rounded.Email, contentDescription = "Chat", tint = EvrryPrimaryBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Chat", color = EvrryPrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // PHASE 1: ACCEPTED -> "ARRIVED AT PICKUP"
                            if (ride.status == DriverRideStatus.ACCEPTED) {
                                Button(
                                    onClick = { viewModel.markArrivedAtPickup() },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Arrived", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Arrived at Pickup Location 📍", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }

                            // PHASE 2: ARRIVED -> 4-DIGIT START OTP ENTRY
                            if (ride.status == DriverRideStatus.ARRIVED) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFEFF6FF))
                                        .padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "Enter Passenger 4-Digit Trip OTP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = EvrryPrimaryBlue
                                    )
                                    Text(
                                        text = "Ask passenger for their 4-digit code (e.g. ${ride.startOtp}) to start navigation",
                                        fontSize = 11.sp,
                                        color = EvrryMutedText
                                    )

                                    OutlinedTextField(
                                        value = startOtpInput,
                                        onValueChange = { viewModel.updateStartOtpInput(it) },
                                        placeholder = { Text("• • • •", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                                        textStyle = LocalTextStyle.current.copy(
                                            textAlign = TextAlign.Center,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black
                                        ),
                                        singleLine = true,
                                        modifier = Modifier.width(180.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EvrryPrimaryBlue,
                                            unfocusedBorderColor = EvrrySubtleBorder
                                        )
                                    )

                                    if (otpError != null) {
                                        Text(otpError ?: "", color = EvrryRedDestructive, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.verifyStartOtp() },
                                        enabled = startOtpInput.length == 4,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EvrryGreenSuccess),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Verify OTP & Start Trip 🚀", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }

                            // PHASE 3: IN PROGRESS -> "COMPLETE TRIP"
                            if (ride.status == DriverRideStatus.IN_PROGRESS) {
                                Surface(
                                    color = Color(0xFFF0FDF4),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Navigating to Drop-off", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF166534))
                                            Text("Agreed Fare: Rs ${ride.finalFareNpr.toInt()}", fontSize = 11.sp, color = EvrryMutedText)
                                        }
                                        Button(
                                            onClick = { viewModel.completeTrip() },
                                            colors = ButtonDefaults.buttonColors(containerColor = EvrryGreenSuccess),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Complete Trip 🏁", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            // PHASE 4: COMPLETED -> FARE BREAKDOWN & COD LEDGER
                            if (ride.status == DriverRideStatus.COMPLETED && fareBreakdown != null) {
                                val fb = fareBreakdown!!
                                Surface(
                                    color = Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.5.dp, EvrryGreenSuccess, RoundedCornerShape(14.dp))
                                        .padding(16.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Total Trip Fare", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EvrryDarkCharcoal)
                                            Text("Rs ${fb.totalFareNpr.toInt()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = EvrryDarkCharcoal)
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Platform Fee (15%)", fontSize = 12.sp, color = EvrryMutedText)
                                            Text("- Rs ${fb.platformCommissionNpr.toInt()}", fontSize = 12.sp, color = EvrryRedDestructive)
                                        }
                                        HorizontalDivider(color = EvrrySubtleBorder)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Driver Net Payout", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF166534))
                                            Text("Rs ${fb.driverNetNpr.toInt()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF166534))
                                        }

                                        Surface(
                                            color = Color(0xFFFEF3C7),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "💵 Collect Rs ${fb.totalFareNpr.toInt()} in CASH from passenger (${fb.paymentMethod})",
                                                color = Color(0xFFB45309),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(10.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        Button(
                                            onClick = { viewModel.collectCashAndFinish() },
                                            colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Cash Collected · Back to Online ✅", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. INDRIVE-STYLE LIVE NEGOTIATION & BIDDING HUD (FLOATING INCOMING REQUESTS)
            if (isOnline && activeRide == null) {
                item {
                    Text(
                        text = "NEARBY RIDE REQUESTS (${openRequests.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryMutedText
                    )
                }

                if (openRequests.isEmpty()) {
                    item {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(color = EvrryPrimaryBlue, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                                Text("Listening for ride requests on Thamel / Patan corridor…", fontSize = 12.sp, color = EvrryMutedText)
                            }
                        }
                    }
                } else {
                    items(openRequests.size) { index ->
                        val req = openRequests[index]
                        var counterFare by remember(req.id) { mutableStateOf(req.offeredFareNpr) }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(20.dp),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, Color(0xFF3B82F6), RoundedCornerShape(20.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Passenger Info & Offered Fare Badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(EvrryPrimaryBlue.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("👤", fontSize = 18.sp)
                                        }
                                        Column {
                                            Text(req.passengerName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EvrryDarkCharcoal)
                                            Text("⭐ ${req.passengerRating} · ${req.distanceKm} km (~${req.durationMinutes}m)", fontSize = 11.sp, color = EvrryMutedText)
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("OFFERED FARE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                        Text("Rs ${req.offeredFareNpr.toInt()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF166534))
                                    }
                                }

                                // Route Summary
                                Surface(
                                    color = Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("📍 Pickup: ${req.pickupAddress}", fontSize = 12.sp, color = EvrryDarkCharcoal, maxLines = 1)
                                        Text("🏁 Drop: ${req.dropAddress}", fontSize = 12.sp, color = EvrryDarkCharcoal, maxLines = 1)
                                    }
                                }

                                if (req.myBidStatus == "pending") {
                                    // PENDING BID BANNER
                                    Surface(
                                        color = Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Counter-Bid Rs ${req.myBidFareNpr?.toInt()} Submitted", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFB45309))
                                            CircularProgressIndicator(color = Color(0xFFB45309), modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp)
                                        }
                                    }
                                } else {
                                    // COUNTER-OFFER STEPPER ([-10], [+10] and quick chips [+20, +50, +100])
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Counter-Offer Price Stepper:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedButton(
                                                onClick = { counterFare = maxOf(30.0, counterFare - 10.0) },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Text("-10", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }

                                            Surface(
                                                color = Color(0xFFEFF6FF),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                                    Text("Rs ${counterFare.toInt()}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = EvrryPrimaryBlue)
                                                }
                                            }

                                            OutlinedButton(
                                                onClick = { counterFare += 10.0 },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Text("+10", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }

                                            // Quick Chips (+20, +50, +100)
                                            listOf(20, 50, 100).forEach { chipDelta ->
                                                Surface(
                                                    color = Color(0xFFF1F5F9),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.clickable { counterFare = req.offeredFareNpr + chipDelta }
                                                ) {
                                                    Text("+$chipDelta", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal, modifier = Modifier.padding(horizontal = 7.dp, vertical = 8.dp))
                                                }
                                            }
                                        }
                                    }

                                    // ACTION BUTTONS: ACCEPT OR COUNTER OR DECLINE
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { viewModel.declineRequest(req.id) },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EvrryRedDestructive),
                                            modifier = Modifier.weight(0.8f)
                                        ) {
                                            Text("Decline", fontSize = 12.sp)
                                        }

                                        if (counterFare != req.offeredFareNpr) {
                                            Button(
                                                onClick = { viewModel.submitCounterBid(req, counterFare) },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                                modifier = Modifier.weight(1.4f)
                                            ) {
                                                Text("Counter Rs ${counterFare.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            Button(
                                                onClick = { viewModel.acceptOfferedFare(req) },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = EvrryGreenSuccess),
                                                modifier = Modifier.weight(1.4f)
                                            ) {
                                                Text("Accept Rs ${req.offeredFareNpr.toInt()} 🤝", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

    // DUAL CALL CHOOSER SHEET
    if (showCallSheet && activeRide != null) {
        val ride = activeRide!!
        DualCallChooserSheet(
            passengerName = ride.passengerName,
            passengerPhone = ride.passengerPhone,
            onDismiss = { showCallSheet = false },
            onInAppVoipCall = {
                selectedVoipNotice = "WebRTC In-App VoIP call connected with ${ride.passengerName}"
            }
        )
    }

    // VOIP NOTICE SNACKBAR
    if (selectedVoipNotice != null) {
        AlertDialog(
            onDismissRequest = { selectedVoipNotice = null },
            title = { Text("🌐 WebRTC Voice Call Active", fontWeight = FontWeight.Bold) },
            text = { Text(selectedVoipNotice ?: "") },
            confirmButton = {
                Button(onClick = { selectedVoipNotice = null }) {
                    Text("End Call")
                }
            }
        )
    }
}
