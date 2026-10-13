package com.elifsi.evrry.consumer.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.elifsi.evrry.consumer.model.*
import com.elifsi.evrry.consumer.ui.components.EvrryTopBar
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.AuthViewModel
import com.elifsi.evrry.consumer.viewmodel.LoyaltyViewModel

/**
 * Settings, Gamified Leagues, Loyalty Vouchers, Google Drive Backup & Privacy Screen
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    activeModel: VoiceAgentModel,
    authViewModel: AuthViewModel? = null,
    loyaltyViewModel: LoyaltyViewModel? = null,
    onSelectModel: (VoiceAgentModel) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
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

    // Loyalty & Gamification State
    val loyaltyState by (loyaltyViewModel?.loyaltyState?.collectAsState() ?: remember { mutableStateOf(UserLoyaltyState()) })
    val stampCard by (loyaltyViewModel?.stampCard?.collectAsState() ?: remember { mutableStateOf(DigitalStampCard()) })
    val vouchers by (loyaltyViewModel?.vouchers?.collectAsState() ?: remember { mutableStateOf(emptyList<VoucherItem>()) })
    val referralInfo by (loyaltyViewModel?.referralInfo?.collectAsState() ?: remember { mutableStateOf(ReferralInfo()) })

    // Backup & Security State
    val chatBackup by (authViewModel?.chatBackupState?.collectAsState() ?: remember { mutableStateOf(ChatBackupState()) })
    val appSecurity by (authViewModel?.appSecurity?.collectAsState() ?: remember { mutableStateOf(AppSecuritySettings()) })
    val supportInfo by (authViewModel?.supportInfo?.collectAsState() ?: remember { mutableStateOf(SupportContactInfo()) })
    val dataManagement by (authViewModel?.dataManagement?.collectAsState() ?: remember { mutableStateOf(DataManagementState()) })

    var selectedTab by remember { mutableIntStateOf(0) } // 0=League & Profile, 1=Vouchers & Stamps, 2=Backup & Privacy, 3=Support & More
    var showEditProfileSheet by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showAddressBook by remember { mutableStateOf(false) }
    var cacheNotice by remember { mutableStateOf<String?>(null) }

    var promoInputText by remember { mutableStateOf("") }
    var promoFeedbackMessage by remember { mutableStateOf<String?>(null) }
    var checkinMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // TOP BAR
        EvrryTopBar(
            title = "Profile & Settings",
            canGoBack = true,
            onBackClick = onBack
        )

        // SEGMENTED TAB PILL (Minimal, uncluttered)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(Color(0xFFEEF2F6), RoundedCornerShape(12.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val tabs = listOf("Profile", "Vouchers", "Backup", "Support")
            tabs.forEachIndexed { index, title ->
                Surface(
                    onClick = { selectedTab = index },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedTab == index) Color.White else Color.Transparent,
                    shadowElevation = if (selectedTab == index) 1.dp else 0.dp
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == index) EvrryPrimaryBlue else Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            // =========================================================================
            // TAB 0: PROFILE & GAMIFIED LEAGUE
            // =========================================================================
            if (selectedTab == 0) {
                // 1. USER PROFILE CARD (WeChat & WhatsApp Style)
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp)),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    Surface(
                                        color = EvrryPrimaryBlue.copy(alpha = 0.12f),
                                        shape = CircleShape,
                                        modifier = Modifier.size(62.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = userProfile.fullName.take(2).uppercase(),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EvrryPrimaryBlue
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(EvrryPrimaryBlue)
                                            .border(1.5.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Edit photo",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = userProfile.fullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            color = EvrryDarkCharcoal
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFDCFCE7),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "KYC",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EvrryGreenSuccess,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "@${userProfile.username}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EvrryPrimaryBlue
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = userProfile.phone,
                                        fontSize = 12.sp,
                                        color = EvrryMutedText
                                    )
                                }
                            }

                            // BIO STATUS
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "About: ${userProfile.bioStatus}",
                                    fontSize = 12.sp,
                                    color = EvrryDarkCharcoal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }

                            // ACTION BUTTONS
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showEditProfileSheet = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EvrryPrimaryBlue)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Edit Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { showQrDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                                ) {
                                    Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("My QR Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 2. GAMIFIED LEAGUE & TIER SYSTEM CARD
                item {
                    val currentLeague = loyaltyState.league
                    val nextLeague = loyaltyState.nextLeague

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // LEAGUE BANNER HEADER
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = Color(currentLeague.primaryColorHex).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "LEAGUE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(currentLeague.primaryColorHex),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = currentLeague.label,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = EvrryDarkCharcoal
                                        )
                                    }
                                    Text(
                                        text = "${loyaltyState.tierPoints} Tier Points (TP)",
                                        fontSize = 12.sp,
                                        color = EvrryMutedText
                                    )
                                }

                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "${loyaltyState.eCoinsBalance} E-Coins",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryPrimaryBlue,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            // PROGRESS BAR TO NEXT LEAGUE
                            Spacer(modifier = Modifier.height(14.dp))
                            if (nextLeague != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(currentLeague.label, fontSize = 11.sp, color = EvrryMutedText)
                                    Text(
                                        "${loyaltyState.pointsToNext} TP to ${nextLeague.label}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryPrimaryBlue
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { loyaltyState.progressToNext },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = EvrryPrimaryBlue,
                                    trackColor = Color(0xFFE2E8F0)
                                )
                            } else {
                                Text(
                                    text = "🏆 You are in the top Diamond League!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EvrryPrimaryBlue
                                )
                            }

                            // PERKS SUMMARY
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stars,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = currentLeague.perkDescription,
                                        fontSize = 11.sp,
                                        color = EvrryDarkCharcoal,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            // DAILY CHECK-IN STREAK
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🔥", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${loyaltyState.consecutiveCheckinDays}-Day Streak",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EvrryDarkCharcoal
                                        )
                                    }
                                    Text(
                                        text = if (loyaltyState.hasCheckedInToday) "Claimed today! Check back tomorrow."
                                        else "Claim today's daily streak bonus",
                                        fontSize = 11.sp,
                                        color = EvrryMutedText
                                    )
                                }

                                Button(
                                    onClick = {
                                        val claimed = loyaltyViewModel?.claimDailyCheckin() ?: false
                                        checkinMessage = if (claimed) "Claimed 15 E-Coins!" else "Already claimed today"
                                    },
                                    enabled = !loyaltyState.hasCheckedInToday,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF16A34A),
                                        disabledContainerColor = Color(0xFFE2E8F0)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (loyaltyState.hasCheckedInToday) "Claimed ✓" else "Claim Coins",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. SAVED PLACES & ADDRESS BOOK CARD
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                            .clickable { showAddressBook = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Place,
                                            contentDescription = null,
                                            tint = EvrryPrimaryBlue,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text("Saved Places & Address Book", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EvrryDarkCharcoal)
                                    Text("Home, Work & custom locations for fast ride booking", fontSize = 11.sp, color = EvrryMutedText)
                                }
                            }
                            Text("→", fontSize = 18.sp, color = EvrryMutedText)
                        }
                    }
                }
            }

            // =========================================================================
            // TAB 1: VOUCHERS, STAMPS & REFERRALS
            // =========================================================================
            if (selectedTab == 1) {
                // 1. REFER & EARN CARD (WhatsApp Viral Loop)
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = CircleShape,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.CardGiftcard,
                                            contentDescription = null,
                                            tint = Color(0xFFB45309),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Refer & Earn NPR 150", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EvrryDarkCharcoal)
                                    Text("Friend gets NPR 100 on signup; you get NPR 150", fontSize = 11.sp, color = EvrryMutedText)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("YOUR REFERRAL CODE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                                        Text(referralInfo.referralCode, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = EvrryDarkCharcoal)
                                    }

                                    Button(
                                        onClick = { /* Share via WhatsApp */ },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Share WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Invited: ${referralInfo.friendsInvitedCount} friends", fontSize = 11.sp, color = EvrryMutedText)
                                Text("Earned: NPR ${referralInfo.totalEarnedNpr}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryGreenSuccess)
                            }
                        }
                    }
                }

                // 2. DIGITAL STAMP CARD (Japanese Point-Card Model)
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Stars, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Digital Stamp Card", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                }
                                Text("${stampCard.currentStamps}/${stampCard.totalSlots} Stamps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "1 stamp per NPR 500 spent. Collect 10 stamps to unlock Flat NPR 500 Rebate Voucher!",
                                fontSize = 11.sp,
                                color = EvrryMutedText
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            // 10 STAMP SLOTS GRID (5 x 2)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (i in 1..5) {
                                    val isStamped = i <= stampCard.currentStamps
                                    Surface(
                                        color = if (isStamped) EvrryPrimaryBlue.copy(alpha = 0.12f) else Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(8.dp),
                                        border = if (isStamped) androidx.compose.foundation.BorderStroke(1.dp, EvrryPrimaryBlue) else null,
                                        modifier = Modifier.weight(1f).height(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = if (isStamped) "✓" else "$i",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isStamped) EvrryPrimaryBlue else EvrryMutedText
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (i in 6..10) {
                                    val isStamped = i <= stampCard.currentStamps
                                    Surface(
                                        color = if (isStamped) EvrryPrimaryBlue.copy(alpha = 0.12f) else Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(8.dp),
                                        border = if (isStamped) androidx.compose.foundation.BorderStroke(1.dp, EvrryPrimaryBlue) else null,
                                        modifier = Modifier.weight(1f).height(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = if (isStamped) "✓" else "$i",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isStamped) EvrryPrimaryBlue else EvrryMutedText
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. ENTER PROMO CODE INPUT
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(18.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("REDEEM PROMO CODE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = promoInputText,
                                    onValueChange = { promoInputText = it },
                                    placeholder = { Text("e.g. DASHAIN or EVRRY50", fontSize = 13.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Button(
                                    onClick = {
                                        val result = loyaltyViewModel?.redeemPromoCode(promoInputText)
                                        promoFeedbackMessage = result?.second
                                        if (result?.first == true) promoInputText = ""
                                    },
                                    modifier = Modifier.height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                                ) {
                                    Text("Apply", fontWeight = FontWeight.Bold)
                                }
                            }

                            if (promoFeedbackMessage != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = promoFeedbackMessage!!,
                                    fontSize = 11.sp,
                                    color = if (promoFeedbackMessage!!.contains("successfully")) EvrryGreenSuccess else EvrryRedDestructive
                                )
                            }
                        }
                    }
                }

                // 4. ACTIVE VOUCHERS LIST
                item {
                    Text("ACTIVE VOUCHERS (${vouchers.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                }

                items(vouchers, key = { it.id }) { voucher ->
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = EvrryPrimaryBlue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(22.dp))
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(voucher.discountText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp)) {
                                        Text(voucher.code, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                                Text(voucher.description, fontSize = 11.sp, color = EvrryMutedText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(voucher.expiryDaysText, fontSize = 10.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // TAB 2: BACKUP, PRIVACY & SECURITY
            // =========================================================================
            if (selectedTab == 2) {
                // 1. GOOGLE DRIVE CHAT BACKUP CARD (WhatsApp Standard)
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = CircleShape,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(22.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Chat Backup", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                    Text("Google Drive End-to-End Encrypted", fontSize = 11.sp, color = EvrryMutedText)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                "Back up your messages, photos, voice notes and documents to Google Drive. You can restore them when you reinstall Evrry.",
                                fontSize = 11.sp,
                                color = EvrryMutedText,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Last Backup:", fontSize = 11.sp, color = EvrryMutedText)
                                        Text(chatBackup.lastBackupTimestamp, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total Size:", fontSize = 11.sp, color = EvrryMutedText)
                                        Text("${chatBackup.totalSizeMb} MB", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Google Account:", fontSize = 11.sp, color = EvrryMutedText)
                                        Text(chatBackup.googleDriveAccount, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = EvrryPrimaryBlue)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            // BACK UP BUTTON WITH PROGRESS
                            if (chatBackup.isBackingUp) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Backing up to Google Drive...", fontSize = 11.sp, color = EvrryPrimaryBlue, fontWeight = FontWeight.Bold)
                                        Text("${chatBackup.backupProgressPercent}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { chatBackup.backupProgressPercent / 100f },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = EvrryPrimaryBlue
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { authViewModel?.startChatBackup() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                                ) {
                                    Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Back Up Now", fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // INCLUDE VIDEOS TOGGLE
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Include Videos", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                                    Text("${chatBackup.videosSizeMb} MB of videos included in backup", fontSize = 11.sp, color = EvrryMutedText)
                                }
                                Switch(
                                    checked = chatBackup.includeVideos,
                                    onCheckedChange = { authViewModel?.updateIncludeVideos(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EvrryPrimaryBlue)
                                )
                            }
                        }
                    }
                }

                // 2. PRIVACY & SOCIAL DISCOVERABILITY
                item {
                    Text("PRIVACY & SOCIAL DISCOVERABILITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                }

                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(18.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // FIND BY PHONE TOGGLE
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                    Text("Find Me by Phone Number", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                                    Text("When off, strangers cannot find you via phone. Only your @handle or QR code works.", fontSize = 11.sp, color = EvrryMutedText, lineHeight = 15.sp)
                                }
                                Switch(
                                    checked = userProfile.allowPhoneDiscovery,
                                    onCheckedChange = { authViewModel?.updatePrivacySettings(it, userProfile.readReceiptsEnabled, userProfile.lastSeenPrivacy) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EvrryPrimaryBlue)
                                )
                            }

                            HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)

                            // READ RECEIPTS (BLUE TICKS)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                    Text("Read Receipts (Blue Ticks)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                                    Text("Show double blue ticks when messages are read.", fontSize = 11.sp, color = EvrryMutedText)
                                }
                                Switch(
                                    checked = userProfile.readReceiptsEnabled,
                                    onCheckedChange = { authViewModel?.updatePrivacySettings(userProfile.allowPhoneDiscovery, it, userProfile.lastSeenPrivacy) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EvrryPrimaryBlue)
                                )
                            }

                            HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)

                            // BIOMETRIC APP LOCK
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                    Text("App Lock (Fingerprint / Face)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryDarkCharcoal)
                                    Text("Require biometric authentication when launching Evrry.", fontSize = 11.sp, color = EvrryMutedText)
                                }
                                Switch(
                                    checked = appSecurity.biometricLockEnabled,
                                    onCheckedChange = { authViewModel?.toggleBiometricLock(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EvrryPrimaryBlue)
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // TAB 3: HELP, CONTACT US & DANGER ZONE
            // =========================================================================
            if (selectedTab == 3) {
                // 1. CONTACT US & 24/7 SUPPORT
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = EvrryGreenSuccess, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Help & Customer Support", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                    Text(supportInfo.supportHours, fontSize = 11.sp, color = EvrryMutedText)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            // HOTLINE BUTTON
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Support Hotline", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                        Text(supportInfo.hotlinePhone, fontSize = 13.sp, color = EvrryPrimaryBlue, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { /* Call hotline */ },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Call Now", fontSize = 11.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            // EMAIL
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Email Support", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                        Text(supportInfo.supportEmail, fontSize = 12.sp, color = EvrryMutedText)
                                    }
                                    OutlinedButton(
                                        onClick = { /* Email */ },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Write", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. LEGAL POLICIES & COMPANY
                item {
                    Text("LEGAL POLICIES & ABOUT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                }

                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(18.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // TERMS OF SERVICE
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showTermsDialog = true },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Surface(color = Color(0xFFEFF6FF), shape = CircleShape, modifier = Modifier.size(36.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Info, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Terms of Service", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                        Text("User agreement, order escrow & rules", fontSize = 11.sp, color = EvrryMutedText)
                                    }
                                }
                                Text("View", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                            }

                            HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)

                            // PRIVACY POLICY
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showPrivacyDialog = true },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Surface(color = Color(0xFFDCFCE7), shape = CircleShape, modifier = Modifier.size(36.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = EvrryGreenSuccess, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Privacy Policy", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                        Text("Zero-knowledge E2EE & Nepal Privacy Act 2075", fontSize = 11.sp, color = EvrryMutedText)
                                    }
                                }
                                Text("View", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                            }

                            HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)

                            // ABOUT US
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showAboutDialog = true },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Surface(color = Color(0xFFFAF5FF), shape = CircleShape, modifier = Modifier.size(36.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF9333EA), modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("About Evrry", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                        Text("Elifsi Technologies Private Limited • v2.4.0", fontSize = 11.sp, color = EvrryMutedText)
                                    }
                                }
                                Text("Info", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EvrryPrimaryBlue)
                            }
                        }
                    }
                }

                // 3. DATA PRIVACY, STORAGE & ERASURE (DELETE DATA)
                item {
                    Text("DATA PRIVACY & STORAGE MANAGEMENT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                }

                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(18.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // LOCAL CACHE CLEANER
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text("Local Media & App Cache", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                    Text(
                                        if (dataManagement.cacheSizeMb > 0) "${String.format(java.util.Locale.US, "%.1f", dataManagement.cacheSizeMb)} MB cached temporary media" else "Cache is clean (0.0 MB)",
                                        fontSize = 11.sp,
                                        color = EvrryMutedText
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        authViewModel?.clearLocalCache()
                                        cacheNotice = "Local cache cleared successfully"
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Clear Cache", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)

                            // AI MEMORY PURGE
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text("AI Concierge Memory", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                    Text(
                                        "${dataManagement.aiMemoryFactsCount} personal preferences remembered",
                                        fontSize = 11.sp,
                                        color = EvrryMutedText
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        authViewModel?.clearAiMemoryFacts()
                                        cacheNotice = "AI memory reset"
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Clear Memory", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(color = EvrrySubtleBorder, thickness = 0.5.dp)

                            // EXPORT MY DATA (GDPR / NEPAL PRIVACY ACT)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text("Download Account Archive", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                                    Text("Export profile, orders & ledger in JSON", fontSize = 11.sp, color = EvrryMutedText)
                                }
                                Button(
                                    onClick = { authViewModel?.requestDataExport() },
                                    enabled = !dataManagement.isExportingData,
                                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        if (dataManagement.isExportingData) "Generating..." else "Export JSON",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (dataManagement.exportDownloadUrl != null) {
                                Text(
                                    "Archive ready! Link: ${dataManagement.exportDownloadUrl}",
                                    fontSize = 11.sp,
                                    color = EvrryGreenSuccess,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (cacheNotice != null) {
                                Text(
                                    cacheNotice ?: "",
                                    fontSize = 11.sp,
                                    color = EvrryGreenSuccess,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // 4. AI CONCIERGE MODEL SELECTOR
                item {
                    Text("AI CONCIERGE VOICE MODEL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                }

                items(VoiceAgentRegistry.allModels) { model ->
                    val isSelected = activeModel.id == model.id
                    Surface(
                        color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (isSelected) EvrryPrimaryBlue else EvrrySubtleBorder, RoundedCornerShape(16.dp))
                            .clickable { onSelectModel(model) }
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(model.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EvrryDarkCharcoal)
                                Text(model.description, fontSize = 11.sp, color = EvrryMutedText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // 5. DANGER ZONE: LOGOUT & DELETE ACCOUNT
                item {
                    Text("ACCOUNT ACTIONS & DANGER ZONE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
                }

                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(18.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // LOGOUT
                            OutlinedButton(
                                onClick = onLogout,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Logout / Switch Account", fontWeight = FontWeight.Bold)
                            }

                            // DELETE ACCOUNT (MANDATORY GOOGLE & APPLE POLICY - 30-DAY INDUSTRY STANDARD)
                            Button(
                                onClick = { showDeleteAccountDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EvrryRedDestructive)
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Schedule Account Deletion (30 Days)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // --- MODALS & DIALOGS ---

    // 1. EDIT PROFILE BOTTOM SHEET
    if (showEditProfileSheet) {
        var editName by remember { mutableStateOf(userProfile.fullName) }
        var editHandle by remember { mutableStateOf(userProfile.username) }
        var editBio by remember { mutableStateOf(userProfile.bioStatus) }

        ModalBottomSheet(
            onDismissRequest = { showEditProfileSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text("Edit Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = editHandle,
                    onValueChange = { editHandle = it },
                    label = { Text("Username Handle (@handle)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = editBio,
                    onValueChange = { editBio = it },
                    label = { Text("Status / About") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        authViewModel?.updateProfile(editName, editHandle, editBio)
                        showEditProfileSheet = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // 2. MY QR CODE DIALOG
    if (showQrDialog) {
        Dialog(onDismissRequest = { showQrDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("My QR Code", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                    Text("Scan to add on Evrry without sharing phone number", fontSize = 11.sp, color = EvrryMutedText, textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(190.dp).border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = EvrryDarkCharcoal, modifier = Modifier.size(150.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(userProfile.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EvrryDarkCharcoal)
                    Text("@${userProfile.username}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EvrryPrimaryBlue)

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showQrDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 3. DELETE ACCOUNT CONFIRMATION DIALOG (MANDATORY GOOGLE PLAY / APPLE STORE - 30-DAY INDUSTRY STANDARD)
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account (30-Day Cooling Period)?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Following global privacy standards (GDPR / ISO 27001 / App Store guidelines):\n\n" +
                    "• Your profile, chat presence, and @${userProfile.username} will be deactivated and hidden immediately.\n" +
                    "• 30-Day Grace Period: You can cancel this deletion anytime within 30 days simply by logging back in.\n" +
                    "• After 30 days, your account data, chat backup keys, ${loyaltyState.tierPoints} Tier Points, and ${loyaltyState.eCoinsBalance} E-Coins will be permanently purged from Evrry servers.",
                    fontSize = 12.sp,
                    color = EvrryDarkCharcoal,
                    lineHeight = 16.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        authViewModel?.deleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryRedDestructive)
                ) {
                    Text("Schedule 30-Day Deletion", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Keep Account")
                }
            }
        )
    }

    // 4. TERMS OF SERVICE DIALOG
    if (showTermsDialog) {
        Dialog(onDismissRequest = { showTermsDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Terms of Service", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                            Text("Last Updated: October 2026", fontSize = 11.sp, color = EvrryMutedText)
                        }
                        IconButton(onClick = { showTermsDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = EvrrySubtleBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("1. Agreement to Terms", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                            Text(
                                "By accessing or using Evrry Super App, operated by Elifsi Technologies Private Limited in Kathmandu, Nepal, you agree to be bound by these Terms of Service. If you do not agree, please discontinue using the application.",
                                fontSize = 11.sp, color = EvrryMutedText, lineHeight = 16.sp
                            )
                        }
                        item {
                            Text("2. Super App Services", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                            Text(
                                "Evrry provides an integrated mobile platform connecting consumers, independent merchants, and mobility riders across Nepal. Services include on-demand food delivery, grocery ordering, ride-hailing, digital vouchers, and end-to-end encrypted messaging.",
                                fontSize = 11.sp, color = EvrryMutedText, lineHeight = 16.sp
                            )
                        }
                        item {
                            Text("3. Payments & Escrow", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                            Text(
                                "Payments are securely processed via licensed payment service providers in Nepal (Fonepay, eSewa, Khalti, ConnectIPS) or Cash on Delivery (COD). Delivery fees and tips are held in escrow until successful completion of the delivery or trip.",
                                fontSize = 11.sp, color = EvrryMutedText, lineHeight = 16.sp
                            )
                        }
                        item {
                            Text("4. User Conduct & Messaging", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                            Text(
                                "You agree to use social chat and in-app VoIP calling solely for lawful communications. Harassment, unauthorized automated scraping, spamming, and fraudulent activity are strictly prohibited.",
                                fontSize = 11.sp, color = EvrryMutedText, lineHeight = 16.sp
                            )
                        }
                        item {
                            Text("5. Governing Law & Jurisdiction", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                            Text(
                                "These terms are governed by and construed in accordance with the laws of Nepal. Any legal disputes shall be subject to the exclusive jurisdiction of the competent courts of Kathmandu, Nepal.",
                                fontSize = 11.sp, color = EvrryMutedText, lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showTermsDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                    ) {
                        Text("I Understand", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 5. PRIVACY POLICY DIALOG
    if (showPrivacyDialog) {
        Dialog(onDismissRequest = { showPrivacyDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Privacy Policy", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                            Text("Nepal Individual Privacy Act 2075 & GDPR Aligned", fontSize = 11.sp, color = EvrryPrimaryBlue, fontWeight = FontWeight.SemiBold)
                        }
                        IconButton(onClick = { showPrivacyDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = EvrrySubtleBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("1. Zero-Eavesdropping Commitment", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                            Text(
                                "All 1-to-1 chats, group conversations, audio calls, and video streams utilize Signal Protocol End-to-End Encryption (E2EE). Neither Evrry nor Elifsi Technologies can access your conversation contents, photos, or voice streams.",
                                fontSize = 11.sp, color = EvrryMutedText, lineHeight = 16.sp
                            )
                        }
                        item {
                            Text("2. Information We Collect", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                            Text(
                                "We collect your verified mobile phone number (for OTP authentication), device push token, and live GPS coordinates strictly during active ride bookings or food delivery dispatch.",
                                fontSize = 11.sp, color = EvrryMutedText, lineHeight = 16.sp
                            )
                        }
                        item {
                            Text("3. WeChat-Style Phone Stealth", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                            Text(
                                "Your phone number is masked from riders, restaurants, and group chats. You can disable 'Find Me by Phone' so contacts can only discover you via your chosen @username or personal QR code.",
                                fontSize = 11.sp, color = EvrryMutedText, lineHeight = 16.sp
                            )
                        }
                        item {
                            Text("4. Google Drive & iCloud Chat Backups", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                            Text(
                                "Chat archives and media are backed up directly to your personal Google Drive or Apple iCloud account with end-to-end encryption keys. We never store raw chat backups on company servers.",
                                fontSize = 11.sp, color = EvrryMutedText, lineHeight = 16.sp
                            )
                        }
                        item {
                            Text("5. Right to Erasure & Data Portability", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                            Text(
                                "Under Section 27 of Nepal's Individual Privacy Act, 2075, you hold the unconditional right to export your account data archive, clear cached files, or permanently delete your profile and all associated data.",
                                fontSize = 11.sp, color = EvrryMutedText, lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showPrivacyDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                    ) {
                        Text("Accept Policy", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 6. ABOUT US DIALOG
    if (showAboutDialog) {
        Dialog(onDismissRequest = { showAboutDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = EvrryPrimaryBlue.copy(alpha = 0.1f),
                        shape = CircleShape,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(28.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Evrry Super App", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = EvrryDarkCharcoal)
                    Text("v2.4.0 • Build 2026.10", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EvrryPrimaryBlue)

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        "Developed with ❤️ by\nElifsi Technologies Private Limited\nKathmandu, Nepal",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = EvrryDarkCharcoal,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, EvrrySubtleBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Company Reg: REG-314982/080/081", fontSize = 11.sp, color = EvrryMutedText)
                            Text("Headquarters: Pulchowk, Lalitpur, Nepal", fontSize = 11.sp, color = EvrryMutedText)
                            Text("Website: https://evrry.com", fontSize = 11.sp, color = EvrryPrimaryBlue, fontWeight = FontWeight.SemiBold)
                            Text("Architecture: Native Jetpack Compose & SwiftUI", fontSize = 11.sp, color = EvrryMutedText)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showAboutDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showAddressBook) {
        Dialog(onDismissRequest = { showAddressBook = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                com.elifsi.evrry.consumer.ui.profile.AddressBookScreen(
                    onBack = { showAddressBook = false }
                )
            }
        }
    }
}
