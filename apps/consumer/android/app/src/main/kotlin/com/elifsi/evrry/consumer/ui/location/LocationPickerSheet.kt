package com.elifsi.evrry.consumer.ui.location

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.location.LocationPoint
import com.elifsi.evrry.consumer.location.LocationService
import com.elifsi.evrry.consumer.location.SavedAddressRepository
import com.elifsi.evrry.consumer.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Reusable Location Picker Bottom Sheet (Consumer Android)
 * Platform: Evrry Super App
 * Elifsi Technologies Private Limited
 *
 * Allows users to choose locations via GPS fix, place search,
 * saved places (Home/Office), or popular Kathmandu hotspots.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerSheet(
    title: String = "Select Location",
    initialPoint: LocationPoint? = null,
    locationService: LocationService = remember { LocationService() },
    savedAddressRepository: SavedAddressRepository = remember { SavedAddressRepository.shared },
    onLocationSelected: (LocationPoint) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val savedAddresses by savedAddressRepository.addresses.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<LocationPoint>>(emptyList()) }
    var isLocating by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery) {
        searchResults = locationService.searchPlaces(searchQuery)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SHEET HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryDarkCharcoal
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Close",
                        tint = EvrryMutedText
                    )
                }
            }

            // SEARCH INPUT FIELD
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Search street, area or landmark…", fontSize = 14.sp, color = EvrryMutedText)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = EvrryPrimaryBlue
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = EvrryMutedText)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EvrryPrimaryBlue,
                    unfocusedBorderColor = EvrrySubtleBorder
                )
            )

            // "USE CURRENT LOCATION" BUTTON
            Surface(
                onClick = {
                    isLocating = true
                    coroutineScope.launch {
                        val pt = locationService.getCurrentLocation(context)
                        isLocating = false
                        onLocationSelected(pt)
                    }
                },
                color = Color(0xFFEFF6FF),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EvrryPrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EvrryPrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLocating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "GPS",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Use Current Location",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EvrryPrimaryBlue
                        )
                        Text(
                            text = "Fetch live GPS coordinates with high accuracy",
                            fontSize = 11.sp,
                            color = EvrryMutedText
                        )
                    }
                }
            }

            // SAVED PLACES SHORTCUTS (Home / Office / Other)
            if (savedAddresses.isNotEmpty()) {
                Text(
                    text = "SAVED PLACES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EvrryMutedText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    savedAddresses.forEach { addr ->
                        Surface(
                            onClick = { onLocationSelected(addr.toLocationPoint()) },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(addr.type.emoji, fontSize = 16.sp)
                                Column {
                                    Text(
                                        text = addr.label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryDarkCharcoal
                                    )
                                    Text(
                                        text = addr.address.substringBefore(","),
                                        fontSize = 10.sp,
                                        color = EvrryMutedText,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SEARCH RESULTS / POPULAR PLACES LIST
            Text(
                text = if (searchQuery.isBlank()) "POPULAR KATHMANDU DESTINATIONS" else "SEARCH RESULTS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryMutedText
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { point ->
                    Surface(
                        onClick = { onLocationSelected(point) },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Pin",
                                    tint = EvrryDarkCharcoal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = point.label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EvrryDarkCharcoal
                                )
                                Text(
                                    text = point.address,
                                    fontSize = 11.sp,
                                    color = EvrryMutedText,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
