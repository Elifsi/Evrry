package com.elifsi.evrry.consumer.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.location.AddressType
import com.elifsi.evrry.consumer.location.SavedAddress
import com.elifsi.evrry.consumer.location.SavedAddressRepository
import com.elifsi.evrry.consumer.ui.components.EvrryTopBar
import com.elifsi.evrry.consumer.ui.location.LocationPickerSheet
import com.elifsi.evrry.consumer.ui.theme.*

/**
 * Saved Places & Address Book Screen (Consumer Android)
 * Platform: Evrry Super App
 * Elifsi Technologies Private Limited
 */
@Composable
fun AddressBookScreen(
    repository: SavedAddressRepository = remember { SavedAddressRepository() },
    onBack: () -> Unit
) {
    val addresses by repository.addresses.collectAsState()
    var showAddPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
    ) {
        EvrryTopBar(
            title = "Saved Places",
            canGoBack = true,
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ADD NEW PLACE BUTTON
            Button(
                onClick = { showAddPicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Place", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Text(
                text = "SAVED ADDRESSES (${addresses.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryMutedText
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(addresses) { address ->
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(address.type.emoji, fontSize = 20.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = address.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = EvrryDarkCharcoal
                                    )
                                    if (address.isDefault) {
                                        Surface(
                                            color = Color(0xFFDCFCE7),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "Default",
                                                color = Color(0xFF166534),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = address.address,
                                    fontSize = 12.sp,
                                    color = EvrryMutedText,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            IconButton(onClick = { repository.removeAddress(address.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = EvrryRedDestructive.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddPicker) {
        LocationPickerSheet(
            title = "Add Saved Place",
            onLocationSelected = { pt ->
                repository.addAddress(
                    SavedAddress(
                        id = "addr_${System.currentTimeMillis()}",
                        label = pt.label,
                        address = pt.address,
                        latitude = pt.latitude,
                        longitude = pt.longitude,
                        type = AddressType.OTHER
                    )
                )
                showAddPicker = false
            },
            onDismiss = { showAddPicker = false }
        )
    }
}
