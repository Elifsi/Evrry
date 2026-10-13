package com.elifsi.evrry.partner.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elifsi.evrry.partner.model.PartnerRole
import com.elifsi.evrry.partner.ui.chat.PartnerChatSheet
import com.elifsi.evrry.partner.ui.components.PartnerTopBar
import com.elifsi.evrry.partner.ui.components.RoleSwitcherModalSheet
import com.elifsi.evrry.partner.ui.courier.CourierDashboardScreen
import com.elifsi.evrry.partner.ui.grocery.GrocerySupermarketScreen
import com.elifsi.evrry.partner.ui.kds.KitchenDisplayScreen
import com.elifsi.evrry.partner.ui.kds.PrepTimeSelectorModal
import com.elifsi.evrry.partner.ui.kds.RejectReasonSheet
import com.elifsi.evrry.partner.ui.driver.DriverHudScreen
import com.elifsi.evrry.partner.ui.menu.Menu86Screen
import com.elifsi.evrry.partner.ui.onboarding.PartnerRoleSelectionScreen
import com.elifsi.evrry.partner.ui.printer.PrintReceiptDialog
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.CourierDeliveryViewModel
import com.elifsi.evrry.partner.viewmodel.DriverMobilityViewModel
import com.elifsi.evrry.partner.viewmodel.GrocerySupermarketViewModel
import com.elifsi.evrry.partner.viewmodel.PartnerOnboardingAuthState
import com.elifsi.evrry.partner.viewmodel.PartnerOnboardingViewModel
import com.elifsi.evrry.partner.viewmodel.RestaurantKitchenViewModel

/**
 * Main Navigation Host for Unified Native Partner Application
 * Platform: Android (Kotlin + Jetpack Compose)
 * Elifsi Technologies Private Limited
 */
@Composable
fun PartnerNavHost(
    onboardingViewModel: PartnerOnboardingViewModel = viewModel(),
    kitchenViewModel: RestaurantKitchenViewModel = viewModel(),
    driverViewModel: DriverMobilityViewModel = viewModel(),
    groceryViewModel: GrocerySupermarketViewModel = viewModel(),
    courierViewModel: CourierDeliveryViewModel = viewModel()
) {
    val authState by onboardingViewModel.authState.collectAsState()
    val activeRole by onboardingViewModel.activeRole.collectAsState()
    val showRoleSwitcher by onboardingViewModel.showRoleSwitcherSheet.collectAsState()

    val store by kitchenViewModel.store.collectAsState()
    val isRinging by kitchenViewModel.audioChimeService.isRinging.collectAsState()
    val selectedPrepOrder by kitchenViewModel.selectedOrderForPrep.collectAsState()
    val selectedRejectOrder by kitchenViewModel.selectedOrderForReject.collectAsState()
    val receiptPreviewText by kitchenViewModel.receiptPreviewText.collectAsState()
    val receiptPreviewTitle by kitchenViewModel.receiptPreviewTitle.collectAsState()
    val activeChatOrder by kitchenViewModel.activeChatOrder.collectAsState()
    val chatRecipientRole by kitchenViewModel.chatRecipientRole.collectAsState()
    val allChatMessages by kitchenViewModel.chatMessages.collectAsState()
    val toastMessage by kitchenViewModel.toastMessage.collectAsState()

    var showMenu86Screen by remember { mutableStateOf(false) }

    EvrryPartnerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(EvrryNavyDark)
        ) {
            when {
                authState != PartnerOnboardingAuthState.ACTIVE_DASHBOARD -> {
                    PartnerRoleSelectionScreen(
                        onboardingViewModel = onboardingViewModel,
                        onComplete = { /* completed */ }
                    )
                }

                activeRole == PartnerRole.DRIVER -> {
                    DriverHudScreen(
                        viewModel = driverViewModel,
                        onRoleSelected = { role -> onboardingViewModel.switchRole(role) }
                    )
                }

                activeRole == PartnerRole.RIDER -> {
                    CourierDashboardScreen(
                        viewModel = courierViewModel,
                        onRoleSwitcherClick = { onboardingViewModel.openRoleSwitcher() }
                    )
                }

                showMenu86Screen -> {
                    Menu86Screen(
                        viewModel = kitchenViewModel,
                        onBack = { showMenu86Screen = false }
                    )
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Adaptive Top Bar with 1-Tap Role Switcher
                        PartnerTopBar(
                            activeRole = activeRole,
                            store = store,
                            isRinging = isRinging,
                            onRoleSwitcherClick = { onboardingViewModel.openRoleSwitcher() },
                            onToggleStoreOpen = { kitchenViewModel.toggleStoreOpen() },
                            onSilenceChime = { kitchenViewModel.silenceChime() },
                            onOpenMenu86 = { showMenu86Screen = true }
                        )

                        // Role Content
                        Box(modifier = Modifier.weight(1f)) {
                            when (activeRole) {
                                PartnerRole.RESTAURANT -> {
                                    KitchenDisplayScreen(viewModel = kitchenViewModel)
                                }
                                PartnerRole.GROCERY -> {
                                    GrocerySupermarketScreen(
                                        groceryViewModel = groceryViewModel
                                    )
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }

            // Toast Floating Notification Banner
            if (toastMessage != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = EvrryNavyLight,
                    contentColor = EvrryTextPrimary,
                    action = {
                        TextButton(onClick = { kitchenViewModel.clearToast() }) {
                            Text("OK", color = EvrryOrange, fontWeight = FontWeight.Bold)
                        }
                    }
                ) {
                    Text(text = toastMessage ?: "")
                }
            }

            // Role Switcher Modal Sheet
            if (showRoleSwitcher) {
                RoleSwitcherModalSheet(
                    activeRole = activeRole,
                    onRoleSelected = { role -> onboardingViewModel.switchRole(role) },
                    onDismiss = { onboardingViewModel.closeRoleSwitcher() }
                )
            }

            // Prep Time Selector Modal
            if (selectedPrepOrder != null) {
                PrepTimeSelectorModal(
                    order = selectedPrepOrder!!,
                    onAccept = { prepMinutes -> kitchenViewModel.acceptOrder(selectedPrepOrder!!.id, prepMinutes) },
                    onAcceptAndPrint = { prepMinutes ->
                        val orderId = selectedPrepOrder!!.id
                        kitchenViewModel.acceptOrder(orderId, prepMinutes)
                        kitchenViewModel.printTicket(selectedPrepOrder!!, isKot = true)
                    },
                    onDismiss = { kitchenViewModel.closePrepTimeModal() }
                )
            }

            // Reject Reason Sheet
            if (selectedRejectOrder != null) {
                RejectReasonSheet(
                    order = selectedRejectOrder!!,
                    onReject = { reason -> kitchenViewModel.rejectOrder(selectedRejectOrder!!.id, reason) },
                    onDismiss = { kitchenViewModel.closeRejectModal() }
                )
            }

            // Thermal Receipt Preview Dialog
            if (receiptPreviewText != null) {
                PrintReceiptDialog(
                    title = receiptPreviewTitle,
                    receiptText = receiptPreviewText!!,
                    onPrint = { kitchenViewModel.printTicket(selectedPrepOrder ?: kitchenViewModel.orders.value.first(), isKot = true) },
                    onDismiss = { kitchenViewModel.closeReceiptPreview() }
                )
            }

            // Contextual In-Trip Chat Sheet
            if (activeChatOrder != null) {
                val orderMessages = allChatMessages[activeChatOrder!!.id] ?: emptyList()
                PartnerChatSheet(
                    order = activeChatOrder!!,
                    recipientRole = chatRecipientRole,
                    messages = orderMessages,
                    onSendMessage = { text -> kitchenViewModel.sendChatMessage(text) },
                    onDismiss = { kitchenViewModel.closeChat() }
                )
            }
        }
    }
}

@Composable
private fun OtherRolePlaceholder(
    role: PartnerRole,
    description: String,
    onSwitchToRestaurant: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = role.icon, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = role.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = EvrryTextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            fontSize = 13.sp,
            color = EvrryTextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSwitchToRestaurant,
            colors = ButtonDefaults.buttonColors(containerColor = EvrryOrange),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Switch to Restaurant Kitchen Mode",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
