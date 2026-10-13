package com.elifsi.evrry.partner.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.CatalogItem
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.RestaurantKitchenViewModel
import java.util.*

/**
 * Instant Menu Item 86'ing Screen (Out-of-Stock Toggle)
 * Platform: Android (Jetpack Compose)
 * Elifsi Technologies Private Limited
 *
 * Provides real-time stock toggling for dishes. Toggling immediately updates
 * catalog_items.is_available and updates consumer food catalog view.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu86Screen(
    viewModel: RestaurantKitchenViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.catalogItems.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedSection by remember { mutableStateOf("All") }

    val sections = remember(items) {
        listOf("All") + items.map { it.section }.distinct()
    }

    val filteredItems = remember(items, searchQuery, selectedSection) {
        items.filter { item ->
            val matchSection = selectedSection == "All" || item.section.equals(selectedSection, ignoreCase = true)
            val matchSearch = searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true)
            matchSection && matchSearch
        }
    }

    val soldOutCount = remember(items) { items.count { !it.isAvailable } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Menu Items · 86ing",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryTextPrimary
                        )
                        Text(
                            text = "$soldOutCount item(s) currently marked out of stock",
                            fontSize = 12.sp,
                            color = if (soldOutCount > 0) EvrryRed else EvrryGreen
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = EvrryTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EvrryNavyMedium
                )
            )
        },
        containerColor = EvrryNavyDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search dishes to toggle availability...", color = EvrryTextSecondary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = EvrryTextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EvrryOrange,
                    unfocusedBorderColor = EvrryNavyBorder,
                    focusedTextColor = EvrryTextPrimary,
                    unfocusedTextColor = EvrryTextPrimary,
                    focusedContainerColor = EvrryNavyMedium,
                    unfocusedContainerColor = EvrryNavyMedium
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section filter pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sections) { section ->
                    val isSelected = selectedSection == section
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) EvrryOrange else EvrryNavyLight)
                            .border(1.dp, if (isSelected) EvrryOrange else EvrryNavyBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedSection = section }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = section,
                            color = if (isSelected) Color.White else EvrryTextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // List of items with instant toggle
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                1.dp,
                                if (!item.isAvailable) EvrryRed.copy(alpha = 0.5f) else EvrryNavyBorder,
                                RoundedCornerShape(14.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = EvrryNavyMedium)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.isAvailable) EvrryTextPrimary else EvrryTextMuted,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (!item.isAvailable) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(EvrryRed.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "86'd",
                                                color = EvrryRed,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (!item.description.isNullOrBlank()) {
                                    Text(
                                        text = item.description,
                                        color = EvrryTextSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                Text(
                                    text = "NPR ${String.format(Locale.US, "%.0f", item.priceRs)} · ${item.section}",
                                    color = EvrryOrange,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            // Availability Switch
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Switch(
                                    checked = item.isAvailable,
                                    onCheckedChange = { viewModel.toggle86Item(item.id) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = EvrryGreen,
                                        uncheckedThumbColor = EvrryRed,
                                        uncheckedTrackColor = EvrryNavyLight
                                    )
                                )
                                Text(
                                    text = if (item.isAvailable) "IN STOCK" else "SOLD OUT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isAvailable) EvrryGreen else EvrryRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
