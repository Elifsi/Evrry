package com.elifsi.evrry.consumer.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.elifsi.evrry.consumer.model.*
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.AuthViewModel
import com.elifsi.evrry.consumer.viewmodel.LoyaltyViewModel

/**
 * Profile Screen matching prototype/Phone/app/profile/page.tsx
 * Every section navigates to its own dedicated sub-screen:
 * 1. Account Details & Edit
 * 2. Payment Methods (eSewa, Khalti, Fonepay, Cards)
 * 3. Vouchers & Referrals
 * 4. Rewards & Loyalty Club
 * 5. Activity (Orders, Bookings & Rides)
 * 6. Address Book (Saved delivery points)
 * 7. Snap Memories & My Locker (Private PIN vault)
 * 8. AI & Voice Concierge
 * 9. App Settings & Chat Backup
 * 10. Privacy & Security (E2EE)
 * 11. Danger Zone (Sign Out & 30-Day Account Deletion)
 * Elifsi Technologies Private Limited
 */
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel? = null,
    loyaltyViewModel: LoyaltyViewModel? = null,
    onNavigateToAccount: () -> Unit = {},
    onNavigateToPaymentMethods: () -> Unit = {},
    onNavigateToVouchers: () -> Unit = {},
    onNavigateToRewards: () -> Unit = {},
    onNavigateToActivity: () -> Unit = {},
    onNavigateToAddressBook: () -> Unit = {},
    onNavigateToMemories: () -> Unit = {},
    onNavigateToAiSettings: () -> Unit = {},
    onNavigateToAppSettings: () -> Unit = {},
    onNavigateToPrivacySecurity: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val userProfile by (authViewModel?.userProfile?.collectAsState() ?: remember {
        mutableStateOf(
            UserProfile(
                id = "usr_nepal_9812345678",
                phone = "+977 9812345678",
                fullName = "Rahul Sharma",
                username = "rahul_evrry",
                avatarUrl = null,
                bioStatus = "Available on Evrry",
                loyaltyPoints = 420,
                kycVerified = true,
                allowPhoneDiscovery = false,
                readReceiptsEnabled = true,
                lastSeenPrivacy = "Everyone"
            )
        )
    })

    val loyaltyState by (loyaltyViewModel?.loyaltyState?.collectAsState() ?: remember { mutableStateOf(UserLoyaltyState()) })
    val vouchers by (loyaltyViewModel?.vouchers?.collectAsState() ?: remember { mutableStateOf(emptyList<VoucherItem>()) })
    val referralInfo by (loyaltyViewModel?.referralInfo?.collectAsState() ?: remember { mutableStateOf(ReferralInfo()) })

    var showQrModal by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp)
    ) {
        // 1. PAGE TITLE
        item {
            Column {
                Text(
                    text = "YOU",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryPrimaryBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Profile",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryDarkCharcoal
                )
            }
        }

        // 2. TIER-COLORED GRADIENT HEADER BANNER
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.Transparent,
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToAccount() }
                        ) {
                            // Avatar Circle
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = (userProfile.fullName.takeIf { it.isNotEmpty() } ?: "R").take(1).uppercase(),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Name & Username
                            Column {
                                Text(
                                    text = userProfile.fullName.ifEmpty { "Rahul Sharma" },
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "@${userProfile.username.ifEmpty { "rahul_evrry" }}",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Right: Personal QR Button + Tier Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = { showQrModal = true },
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = "Personal QR",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Surface(
                                onClick = onNavigateToRewards,
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = loyaltyState.league.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2.5 ACTION REQUIRED BANNER (Missing / Unverified Phone)
        if (userProfile.phone.isBlank() || !userProfile.isPhoneVerified) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFEF2F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAccount() }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFEE2E2),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Action Required: Complete Profile",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "Add and verify your mobile number so drivers and couriers can call you during rides & orders.",
                                fontSize = 11.sp,
                                color = Color(0xFFB91C1C),
                                lineHeight = 15.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFDC2626)
                        ) {
                            Text(
                                text = "Verify",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. DEDICATED CARDS / ROWS

        // 2. Payment Methods (eSewa, Khalti, Fonepay, Cards)
        item {
            ProfileNavRow(
                icon = Icons.Default.Payment,
                title = "Payment Methods",
                subtitle = "eSewa, Khalti, Fonepay & Bank Cards",
                badge = "Manage",
                onClick = onNavigateToPaymentMethods
            )
        }

        // 3. Vouchers & Referrals
        item {
            ProfileNavRow(
                icon = Icons.Default.ConfirmationNumber,
                title = "Vouchers & Referrals",
                subtitle = "${vouchers.size} active vouchers • Code: ${referralInfo.referralCode}",
                badge = "${vouchers.size}",
                onClick = onNavigateToVouchers
            )
        }

        // 4. Rewards & Loyalty
        item {
            ProfileNavRow(
                icon = Icons.Default.EmojiEvents,
                title = "Rewards & Loyalty",
                subtitle = "${userProfile.loyaltyPoints} points • Level: ${loyaltyState.league.label}",
                badge = "${userProfile.loyaltyPoints} pts",
                onClick = onNavigateToRewards
            )
        }

        // 5. Activity (Orders, Bookings & Rides)
        item {
            ProfileNavRow(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                title = "Activity & History",
                subtitle = "Food orders, grocery mart & rides history",
                onClick = onNavigateToActivity
            )
        }

        // 6. Address Book
        item {
            ProfileNavRow(
                icon = Icons.Default.Home,
                title = "Address Book",
                subtitle = "Saved delivery locations (Home, Office)",
                onClick = onNavigateToAddressBook
            )
        }

        // 7. Snap Memories & My Locker
        item {
            ProfileNavRow(
                icon = Icons.Default.Lock,
                title = "Snap Memories & My Locker",
                subtitle = "PIN-protected private vault 🔒 & saved stories",
                onClick = onNavigateToMemories
            )
        }

        // 8. AI & Voice Concierge
        item {
            ProfileNavRow(
                icon = Icons.Default.AutoAwesome,
                title = "AI & Voice Concierge",
                subtitle = "4 Persona Models (Ellie, Rony, Jenny, Suka)",
                onClick = onNavigateToAiSettings
            )
        }

        // 9. App Settings & Chat Backup
        item {
            ProfileNavRow(
                icon = Icons.Default.Settings,
                title = "App Settings & Backup",
                subtitle = "Google Drive chat backup, biometrics & notifications",
                onClick = onNavigateToAppSettings
            )
        }

        // 10. Privacy & Security
        item {
            ProfileNavRow(
                icon = Icons.Default.Security,
                title = "Privacy & Encryption",
                subtitle = "Signal-grade Double Ratchet E2EE",
                onClick = onNavigateToPrivacySecurity
            )
        }

        // 11. Account Actions & Danger Zone
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Sign Out Button
                Surface(
                    onClick = { showLogoutConfirm = true },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = EvrryDarkCharcoal,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign Out",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryDarkCharcoal
                        )
                    }
                }

                // Delete Account Button (30-Day Industry Standard Grace Period)
                Surface(
                    onClick = { showDeleteAccountDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Delete Account",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Schedule Account Deletion (30 Days)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    }

    // WECHAT-STYLE PERSONAL QR CODE MODAL
    if (showQrModal) {
        PersonalQrCodeModal(
            user = userProfile,
            onDismiss = { showQrModal = false }
        )
    }

    // LOGOUT CONFIRMATION DIALOG
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Sign Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to sign out of Evrry?", color = EvrryMutedText) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        authViewModel?.resetToPhoneInput()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Sign Out", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel", color = EvrryDarkCharcoal)
                }
            }
        )
    }

    // 30-DAY ACCOUNT DELETION DIALOG (APPLE APP STORE 5.1.1(v) & GDPR COMPLIANT)
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                    Text("Delete Account", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to schedule your Evrry account for deletion?",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = EvrryDarkCharcoal
                    )
                    Text(
                        text = "Industry standard 30-day grace period: Your profile, orders, and chats will be deactivated immediately. If you change your mind, simply log in within 30 days to cancel the request. After 30 days, all your data will be permanently purged.",
                        fontSize = 12.sp,
                        color = EvrryMutedText,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        authViewModel?.deleteAccount()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Schedule Deletion", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = EvrryDarkCharcoal)
                }
            }
        )
    }
}

/**
 * Standard Navigation Row in Profile Screen
 */
@Composable
private fun ProfileNavRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
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
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = EvrryPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryDarkCharcoal
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = EvrryMutedText,
                        maxLines = 1
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (badge != null) {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryPrimaryBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Navigate",
                    tint = EvrryMutedText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * WeChat-Style Personal QR Code Modal
 */
@Composable
private fun PersonalQrCodeModal(
    user: UserProfile,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EvrryPrimaryBlue,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = user.fullName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }
                    }
                    Column {
                        Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EvrryDarkCharcoal)
                        Text("@${user.username}", fontSize = 12.sp, color = EvrryMutedText)
                    }
                }

                HorizontalDivider(color = EvrrySubtleBorder)

                // Simulated Stylized WeChat QR Code
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier
                        .size(200.dp)
                        .border(2.dp, EvrryPrimaryBlue, RoundedCornerShape(16.dp))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR Code",
                            tint = EvrryDarkCharcoal,
                            modifier = Modifier.size(160.dp)
                        )
                        Surface(
                            shape = CircleShape,
                            color = EvrryPrimaryBlue,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("e", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }
                    }
                }

                Text(
                    text = "Scan to add Rahul on Evrry or split food & ride bills instantly",
                    fontSize = 12.sp,
                    color = EvrryMutedText,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
