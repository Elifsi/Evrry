package com.elifsi.evrry.partner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.PartnerRole
import com.elifsi.evrry.partner.model.RestaurantStore
import com.elifsi.evrry.partner.ui.theme.*

/**
 * Top Bar with 1-Tap Adaptive Role Switcher & Kitchen Controls
 * Platform: Android (Jetpack Compose)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerTopBar(
    activeRole: PartnerRole,
    store: RestaurantStore,
    isRinging: Boolean,
    onRoleSwitcherClick: () -> Unit,
    onToggleStoreOpen: () -> Unit,
    onSilenceChime: () -> Unit,
    onOpenMenu86: () -> Unit
) {
    Surface(
        color = EvrryNavyMedium,
        tonalElevation = 4.dp,
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
                    Text(
                        text = activeRole.icon,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = activeRole.title,
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

                // Action Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Audio Ringing Alert Indicator / Mute Button
                    if (isRinging) {
                        Button(
                            onClick = onSilenceChime,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EvrryRed
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Alarm Ringing",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SILENCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Quick Menu 86'ing Button
                    FilledTonalButton(
                        onClick = onOpenMenu86,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = EvrryNavyLight,
                            contentColor = EvrryTextPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "86'd Items",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Store Open / Closed Toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (store.isOpen) EvrryGreen.copy(alpha = 0.15f) else EvrryRed.copy(alpha = 0.15f))
                            .border(
                                1.dp,
                                if (store.isOpen) EvrryGreen.copy(alpha = 0.4f) else EvrryRed.copy(alpha = 0.4f),
                                RoundedCornerShape(18.dp)
                            )
                            .clickable { onToggleStoreOpen() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (store.isOpen) EvrryGreen else EvrryRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (store.isOpen) "OPEN" else "CLOSED",
                            color = if (store.isOpen) EvrryGreen else EvrryRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Sub-bar Store Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (activeRole == PartnerRole.GROCERY) "Bhatbhateni Supermarket · Pulchowk, Ward 3" else "${store.name} · ${store.addressText}",
                    color = EvrryTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Text(
                    text = if (activeRole == PartnerRole.GROCERY) "Supermarket Mode" else "Avg Prep: ${store.avgPrepTimeMinutes}m",
                    color = if (activeRole == PartnerRole.GROCERY) EvrryGreen else EvrryOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * 1-Tap Role Switcher Modal Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSwitcherModalSheet(
    activeRole: PartnerRole,
    onRoleSelected: (PartnerRole) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EvrryNavyMedium,
        scrimColor = Color.Black.copy(alpha = 0.65f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Switch Partner Operational Mode",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryTextPrimary
            )
            Text(
                text = "Select an active business venture without signing out of your account",
                fontSize = 12.sp,
                color = EvrryTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            PartnerRole.entries.forEach { role ->
                val isSelected = role == activeRole
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onRoleSelected(role) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) EvrryNavyLight else EvrryNavyDark
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isSelected) EvrryOrange else EvrryNavyBorder
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = role.icon, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = role.title,
                                color = EvrryTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = role.subtitle,
                                color = EvrryTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active",
                                tint = EvrryOrange
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Mobility Driver HUD Top Bar with Online Beacon Toggle & 1-Tap Switcher
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerTopBar(
    currentRole: PartnerRole,
    isOnline: Boolean,
    onToggleOnline: (Boolean) -> Unit,
    onRoleSelected: (PartnerRole) -> Unit,
    todayEarningsNpr: Double
) {
    var showSheet by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Surface(
        color = EvrryNavyMedium,
        tonalElevation = 4.dp,
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
                        .clickable { showSheet = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currentRole.icon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentRole.title,
                        color = EvrryTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Switch Role",
                        tint = EvrryOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Online/Offline Beacon Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isOnline) EvrryGreen else EvrryTextSecondary)
                    )
                    Text(
                        text = if (isOnline) "ONLINE" else "OFFLINE",
                        color = if (isOnline) EvrryGreen else EvrryTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = isOnline,
                        onCheckedChange = onToggleOnline,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EvrryGreen,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = EvrryNavyDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Sub-bar Earnings & Telemetry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GPS: Kathmandu Corridor · 3.5s Cadence",
                    color = EvrryTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Text(
                    text = "Today's Earnings: NPR ${String.format(java.util.Locale.US, "%.0f", todayEarningsNpr)}",
                    color = EvrryOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showSheet) {
        RoleSwitcherModalSheet(
            activeRole = currentRole,
            onRoleSelected = { role ->
                showSheet = false
                onRoleSelected(role)
            },
            onDismiss = { showSheet = false }
        )
    }
}

/**
 * Supermarket Partner Mode Top Bar
 */
@Composable
fun PartnerTopBar(
    currentRole: PartnerRole,
    onRoleSelected: (PartnerRole) -> Unit,
    storeName: String
) {
    var showSheet by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Surface(
        color = EvrryNavyMedium,
        tonalElevation = 4.dp,
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
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(EvrryNavyLight)
                        .border(1.dp, EvrryNavyBorder, RoundedCornerShape(24.dp))
                        .clickable { showSheet = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currentRole.icon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentRole.title,
                        color = EvrryTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Switch Role",
                        tint = EvrryOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = storeName,
                    color = EvrryTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showSheet) {
        RoleSwitcherModalSheet(
            activeRole = currentRole,
            onRoleSelected = { role ->
                showSheet = false
                onRoleSelected(role)
            },
            onDismiss = { showSheet = false }
        )
    }
}


