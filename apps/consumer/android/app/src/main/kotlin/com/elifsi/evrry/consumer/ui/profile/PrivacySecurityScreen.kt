package com.elifsi.evrry.consumer.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.AuthViewModel

/**
 * Dedicated Privacy & Security Management Screen
 * Signal-Grade Double Ratchet E2EE verification, phone discovery, and last-seen privacy.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val userProfile by authViewModel.userProfile.collectAsState()

    var allowPhoneDiscovery by remember { mutableStateOf(userProfile.allowPhoneDiscovery) }
    var readReceiptsEnabled by remember { mutableStateOf(userProfile.readReceiptsEnabled) }
    var lastSeenOption by remember { mutableStateOf(userProfile.lastSeenPrivacy) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Privacy & Security", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                        Text("Encryption, discovery & profile visibility", fontSize = 11.sp, color = EvrryMutedText)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = EvrryDarkCharcoal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF9FBFF))
            )
        },
        containerColor = Color(0xFFF9FBFF)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            // 1. E2EE STATUS BANNER
            item {
                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(18.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFF10B981), modifier = Modifier.size(40.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                        }
                        Column {
                            Text("Signal-Grade Double Ratchet E2EE", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF065F46))
                            Text("Messages, voice notes & VoIP calls are encrypted peer-to-peer. Only you and your chat partner hold the keys.", fontSize = 11.sp, color = Color(0xFF047857), lineHeight = 15.sp)
                        }
                    }
                }
            }

            // 2. DISCOVERY & VISIBILITY
            item {
                Text("DISCOVERY & CONTACTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
            }

            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, EvrrySubtleBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Find by Phone Number", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                                Text("Allow people who have your Nepal mobile number saved to find your Evrry account", fontSize = 11.sp, color = EvrryMutedText)
                            }
                            Switch(
                                checked = allowPhoneDiscovery,
                                onCheckedChange = {
                                    allowPhoneDiscovery = it
                                    authViewModel.updatePrivacySettings(it, readReceiptsEnabled, lastSeenOption)
                                }
                            )
                        }

                        HorizontalDivider(color = EvrrySubtleBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Read Receipts", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
                                Text("Show blue ticks when you have read messages", fontSize = 11.sp, color = EvrryMutedText)
                            }
                            Switch(
                                checked = readReceiptsEnabled,
                                onCheckedChange = {
                                    readReceiptsEnabled = it
                                    authViewModel.updatePrivacySettings(allowPhoneDiscovery, it, lastSeenOption)
                                }
                            )
                        }
                    }
                }
            }

            // 3. LAST SEEN VISIBILITY
            item {
                Text("WHO CAN SEE MY LAST SEEN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText, modifier = Modifier.padding(top = 4.dp))
            }

            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, EvrrySubtleBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Everyone", "My Connections", "Nobody").forEach { option ->
                            val isSelected = lastSeenOption == option
                            Surface(
                                onClick = {
                                    lastSeenOption = option
                                    authViewModel.updatePrivacySettings(allowPhoneDiscovery, readReceiptsEnabled, option)
                                },
                                color = if (isSelected) Color(0xFFEFF6FF) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) EvrryPrimaryBlue else EvrryDarkCharcoal
                                    )
                                    if (isSelected) {
                                        RadioButton(selected = true, onClick = null)
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
