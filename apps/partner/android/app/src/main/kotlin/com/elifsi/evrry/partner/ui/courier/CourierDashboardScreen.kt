package com.elifsi.evrry.partner.ui.courier

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.elifsi.evrry.partner.model.CourierOrder
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.CourierDeliveryViewModel

/**
 * Main Delivery Courier Operational Screen
 * Implements:
 * 1. Online Toggle & Order Discovery Feed (kaaneneskpc/Deliverr)
 * 2. Two-Stage Navigation HUD (enatega-multivendor-rider)
 * 3. 4-Digit Customer Delivery OTP Handshake
 * 4. Cash-on-Delivery (COD) Cash Collection Ledger
 * 5. Multi-Party Chat & Dual Calling
 *
 * Elifsi Technologies Private Limited
 */
@Composable
fun CourierDashboardScreen(
    viewModel: CourierDeliveryViewModel,
    onRoleSwitcherClick: () -> Unit
) {
    val context = LocalContext.current
    val isOnline by viewModel.isOnline.collectAsState()
    val availableOrders by viewModel.availableOrders.collectAsState()
    val activeOrder by viewModel.activeOrder.collectAsState()
    val deliveryStage by viewModel.deliveryStage.collectAsState()
    val hasArrivedAtStore by viewModel.hasArrivedAtStore.collectAsState()
    val hasArrivedAtCustomer by viewModel.hasArrivedAtCustomer.collectAsState()

    val otpInput by viewModel.otpInput.collectAsState()
    val otpError by viewModel.otpError.collectAsState()
    val isOtpVerifying by viewModel.isOtpVerifying.collectAsState()
    val codConfirmed by viewModel.codCashCollectedConfirmed.collectAsState()

    val codCashInHandPaisa by viewModel.codCashInHandPaisa.collectAsState()
    val todayEarningsPaisa by viewModel.todayEarningsPaisa.collectAsState()
    val todayDeliveriesCount by viewModel.todayDeliveriesCount.collectAsState()
    val ledgerEntries by viewModel.ledgerEntries.collectAsState()

    val activeChatTab by viewModel.activeChatTab.collectAsState()
    val storeMessages by viewModel.storeChatMessages.collectAsState()
    val customerMessages by viewModel.customerChatMessages.collectAsState()

    val showDualCallSheet by viewModel.showDualCallSheet.collectAsState()
    val callTarget by viewModel.callTarget.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    var showOtpDialog by remember { mutableStateOf(false) }
    var showChatSheet by remember { mutableStateOf(false) }
    var showLedgerSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EvrryNavyDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Courier Top Bar with Role Switcher & Online Toggle
            Surface(
                color = EvrryNavyMedium,
                border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1-Tap Role Switcher Pill
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(EvrryNavyLight)
                                .border(1.dp, EvrryNavyBorder, RoundedCornerShape(24.dp))
                                .clickable { onRoleSwitcherClick() }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🛵", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Delivery Courier",
                                    color = EvrryTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Tap to switch role ▼",
                                    color = EvrryOrange,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Online / Offline Switch
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isOnline) EvrryGreen.copy(alpha = 0.15f) else EvrryRed.copy(alpha = 0.15f))
                                .border(
                                    1.dp,
                                    if (isOnline) EvrryGreen.copy(alpha = 0.4f) else EvrryRed.copy(alpha = 0.4f),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.toggleOnline(context) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) EvrryGreen else EvrryRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOnline) "ONLINE" else "OFFLINE",
                                color = if (isOnline) EvrryGreen else EvrryRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Stats & Cash Ledger Trigger Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Column {
                                Text("DELIVERIES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EvrryTextSecondary)
                                Text("$todayDeliveriesCount", fontSize = 14.sp, fontWeight = FontWeight.Black, color = EvrryTextPrimary)
                            }
                            Column {
                                Text("EARNINGS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EvrryTextSecondary)
                                Text("Rs ${(todayEarningsPaisa / 100.0).toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = EvrryGreen)
                            }
                            Column {
                                Text("COD CASH IN HAND", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EvrryTextSecondary)
                                Text("Rs ${(codCashInHandPaisa / 100.0).toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = EvrryOrange)
                            }
                        }

                        FilledTonalButton(
                            onClick = { showLedgerSheet = true },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = EvrryNavyLight,
                                contentColor = EvrryOrange
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ledger", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Body: Active Navigation HUD OR Order Discovery Feed
            Box(modifier = Modifier.weight(1f)) {
                if (activeOrder != null) {
                    CourierActiveDeliveryHud(
                        order = activeOrder!!,
                        stage = deliveryStage,
                        hasArrivedAtStore = hasArrivedAtStore,
                        hasArrivedAtCustomer = hasArrivedAtCustomer,
                        onArrivedAtStoreClick = { viewModel.markArrivedAtStore() },
                        onConfirmPickupClick = { viewModel.confirmOrderPickup() },
                        onArrivedAtCustomerClick = { viewModel.markArrivedAtCustomer() },
                        onOpenOtpDialogClick = { showOtpDialog = true },
                        onOpenChatClick = { showChatSheet = true },
                        onOpenCallSheet = { target -> viewModel.openDualCallSheet(target) }
                    )
                } else {
                    OrderDiscoveryFeedView(
                        isOnline = isOnline,
                        orders = availableOrders,
                        onClaimClick = { order -> viewModel.claimDelivery(order) },
                        onPassClick = { order -> viewModel.passDelivery(order) },
                        onGoOnlineClick = { viewModel.toggleOnline(context) }
                    )
                }
            }
        }

        // OTP Verification Dialog
        if (showOtpDialog && activeOrder != null) {
            CourierOtpVerificationDialog(
                order = activeOrder!!,
                otpInput = otpInput,
                otpError = otpError,
                isVerifying = isOtpVerifying,
                codConfirmed = codConfirmed,
                onDigitPress = { digit -> viewModel.enterOtpDigit(digit) },
                onDeletePress = { viewModel.deleteOtpDigit() },
                onToggleCodConfirmed = { confirmed -> viewModel.toggleCodCashCollected(confirmed) },
                onVerifyClick = {
                    viewModel.verifyOtpAndCompleteDelivery()
                },
                onDismiss = { showOtpDialog = false }
            )
        }

        // Multi-Party Chat Sheet (Store vs Customer)
        if (showChatSheet && activeOrder != null) {
            CourierMultiPartyChatSheet(
                order = activeOrder!!,
                activeTab = activeChatTab,
                storeMessages = storeMessages,
                customerMessages = customerMessages,
                onSelectTab = { tab -> viewModel.selectChatTab(tab) },
                onSendMessage = { text -> viewModel.sendChatMessage(text) },
                onDismiss = { showChatSheet = false }
            )
        }

        // Dual Calling Bottom Sheet (VoIP vs SIM)
        if (showDualCallSheet && activeOrder != null) {
            CourierDualCallSheet(
                order = activeOrder!!,
                callTarget = callTarget,
                onVoipCall = { viewModel.triggerVoipCall() },
                onSimCall = { viewModel.triggerSimCall(context) },
                onDismiss = { viewModel.closeDualCallSheet() }
            )
        }

        // COD Cash Collection Ledger Sheet
        if (showLedgerSheet) {
            CourierCodLedgerSheet(
                codCashInHandPaisa = codCashInHandPaisa,
                todayEarningsPaisa = todayEarningsPaisa,
                ledgerEntries = ledgerEntries,
                onInstantCashoutClick = { amount -> viewModel.requestInstantCashout(amount) },
                onDismiss = { showLedgerSheet = false }
            )
        }

        // Toast Feedback Notification Banner
        if (toastMessage != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = EvrryNavyLight,
                contentColor = EvrryTextPrimary,
                action = {
                    TextButton(onClick = { viewModel.clearToast() }) {
                        Text("OK", color = EvrryOrange, fontWeight = FontWeight.Bold)
                    }
                }
            ) {
                Text(text = toastMessage ?: "")
            }
        }
    }
}

/**
 * Order Discovery Feed View (kaaneneskpc/Deliverr)
 * Displays incoming orders available for nearby courier claim
 */
@Composable
private fun OrderDiscoveryFeedView(
    isOnline: Boolean,
    orders: List<CourierOrder>,
    onClaimClick: (CourierOrder) -> Unit,
    onPassClick: (CourierOrder) -> Unit,
    onGoOnlineClick: () -> Unit
) {
    if (!isOnline) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(EvrryRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOff,
                    contentDescription = null,
                    tint = EvrryRed,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You are currently Offline",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Toggle Online to start persistent GPS streaming and receive food & grocery orders in Kathmandu Valley",
                fontSize = 12.sp,
                color = EvrryTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onGoOnlineClick,
                colors = ButtonDefaults.buttonColors(containerColor = EvrryGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("GO ONLINE NOW", fontWeight = FontWeight.Bold)
            }
        }
    } else if (orders.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = EvrryOrange,
                modifier = Modifier.size(36.dp),
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Searching for Nearby Orders...",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Listening for ready orders from Restaurants and Supermarkets in your vicinity",
                fontSize = 12.sp,
                color = EvrryTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Available Orders (${orders.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryTextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(EvrryGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Live GPS Dispatch",
                            fontSize = 11.sp,
                            color = EvrryGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            items(orders) { order ->
                IncomingDeliveryCard(
                    order = order,
                    onClaim = { onClaimClick(order) },
                    onPass = { onPassClick(order) }
                )
            }
        }
    }
}

/**
 * Incoming Delivery Order Card
 */
@Composable
private fun IncomingDeliveryCard(
    order: CourierOrder,
    onClaim: () -> Unit,
    onPass: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = EvrryNavyMedium),
        border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Store Title & Category Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (order.isGrocery) EvrryGreen.copy(alpha = 0.15f)
                                else EvrryOrange.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (order.isGrocery) "🛒" else "🍳",
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = order.storeName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryTextPrimary
                        )
                        Text(
                            text = "Order #${order.orderNo} · ${order.items.size} item(s)",
                            fontSize = 11.sp,
                            color = EvrryTextSecondary
                        )
                    }
                }

                Surface(
                    color = if (order.isGrocery) EvrryGreen.copy(alpha = 0.2f) else EvrryOrange.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (order.isGrocery) "SUPERMARKET" else "RESTAURANT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (order.isGrocery) EvrryGreen else EvrryOrange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-Stop Route Details
            // Stop 1: Pickup
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = null,
                    tint = EvrryOrange,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Pickup: ${order.storeAddress}", fontSize = 12.sp, color = EvrryTextPrimary)
                    Text("${order.pickupDistanceM / 1000.0} km away from your location", fontSize = 10.sp, color = EvrryTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stop 2: Dropoff
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = EvrryGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Deliver to: ${order.deliveryAddress}", fontSize = 12.sp, color = EvrryTextPrimary)
                    Text("Customer: ${order.consumerName}", fontSize = 10.sp, color = EvrryTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Financial Summary Banner
            Surface(
                color = EvrryNavyDark,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ESTIMATED EARNING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EvrryTextSecondary)
                        Text("Rs ${order.estimatedEarningRs.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = EvrryGreen)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            color = if (order.isCod) EvrryRed.copy(alpha = 0.2f) else EvrryBlue.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (order.isCod) "COLLECT CASH: Rs ${order.totalRs.toInt()}" else "PREPAID ONLINE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (order.isCod) EvrryRed else EvrryBlue,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            text = "Total Trip: ${order.totalDistanceKm} km",
                            fontSize = 10.sp,
                            color = EvrryTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Claim vs Pass Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onPass,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EvrryTextSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EvrryNavyBorder)
                ) {
                    Text("Pass", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                Button(
                    onClick = onClaim,
                    modifier = Modifier
                        .weight(1.8f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryOrange)
                ) {
                    Text("CLAIM DELIVERY", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}
