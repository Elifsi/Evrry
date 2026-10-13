package com.elifsi.evrry.partner.ui.grocery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.elifsi.evrry.partner.data.GroceryCatalogSampleData
import com.elifsi.evrry.partner.model.GroceryPartnerItem
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.GrocerySupermarketViewModel

/**
 * Supermarket Inventory & Rapid Stock Management Screen
 * Category-filtered product list with search bar, rapid stock counters,
 * and 1-tap 86ing out-of-stock items.
 * Grounded in Aakash901_BlinkitClone AdapterProduct & FilteringProducts patterns.
 * Elifsi Technologies Private Limited
 */
@Composable
fun GroceryInventoryScreen(
    viewModel: GrocerySupermarketViewModel,
    onOpenScanner: () -> Unit
) {
    val selectedAisle by viewModel.selectedAisle.collectAsState()
    val searchQuery by viewModel.inventorySearchQuery.collectAsState()
    val catalog by viewModel.catalog.collectAsState()

    val filteredItems = remember(catalog, selectedAisle, searchQuery) {
        viewModel.getFilteredCatalog()
    }

    var itemToEditPrice by remember { mutableStateOf<GroceryPartnerItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EvrryPaper)
    ) {
        // TOP SEARCH & BARCODE SCANNER BAR
        Surface(
            color = EvrryWhite,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setInventorySearchQuery(it) },
                        placeholder = { Text("Search title, brand, SKU or barcode...", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = EvrryMuted)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setInventorySearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = EvrryMuted)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    FilledIconButton(
                        onClick = onOpenScanner,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = EvrryPrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(50.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Barcode", tint = EvrryWhite)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AISLE / DEPARTMENT FILTER CHIPS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GroceryCatalogSampleData.aisles.forEach { aisle ->
                        val isSelected = (aisle == selectedAisle)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setAisleFilter(aisle) },
                            label = {
                                Text(
                                    text = aisle,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EvrryNavy,
                                selectedLabelColor = EvrryWhite,
                                containerColor = EvrryPaper,
                                labelColor = EvrryInk
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }

        // CATALOG ITEMS LIST
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No items matched \"$searchQuery\" in $selectedAisle",
                    color = EvrryMuted,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp)
            ) {
                items(filteredItems) { item ->
                    InventoryItemCard(
                        item = item,
                        onAdjustStock = { delta -> viewModel.adjustStock(item.id, delta) },
                        onToggleAvailability = { viewModel.toggleItemAvailability(item.id) },
                        onEditPrice = { itemToEditPrice = item }
                    )
                }
            }
        }
    }

    // Quick Edit Price Dialog
    if (itemToEditPrice != null) {
        val item = itemToEditPrice!!
        var priceInput by remember { mutableStateOf(item.priceNpr.toString()) }

        AlertDialog(
            onDismissRequest = { itemToEditPrice = null },
            title = {
                Text(
                    text = "Update Shelf Price",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = EvrryNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("Price (NPR)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newPrice = priceInput.toDoubleOrNull()
                        if (newPrice != null) {
                            viewModel.updateItemPrice(item.id, newPrice)
                        }
                        itemToEditPrice = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                ) {
                    Text("Save Price", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToEditPrice = null }) {
                    Text("Cancel", color = EvrryMuted)
                }
            }
        )
    }
}

@Composable
fun InventoryItemCard(
    item: GroceryPartnerItem,
    onAdjustStock: (Int) -> Unit,
    onToggleAvailability: () -> Unit,
    onEditPrice: () -> Unit
) {
    Surface(
        color = EvrryWhite,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = item.section,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryMuted,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EvrryNavy,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "SKU: ${item.sku ?: "N/A"} • Barcode: ${item.barcode ?: "N/A"}",
                        fontSize = 11.sp,
                        color = EvrryMuted
                    )
                }

                // Price Tag with Click to Edit
                Surface(
                    color = EvrryPaper,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onEditPrice() }
                        .border(1.dp, EvrryBorder, RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "NPR ${String.format("%.2f", item.priceNpr)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = EvrryPrimaryBlue
                        )
                        Icon(Icons.Default.Edit, contentDescription = "Edit Price", modifier = Modifier.size(12.dp), tint = EvrryMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = EvrryBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Stock Counters & Rapid Availability Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock Counter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Stock:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EvrryMuted)
                    FilledTonalIconButton(
                        onClick = { onAdjustStock(-1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${item.stockQuantity} ${item.unit}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (item.stockQuantity > 5) EvrryDarkCharcoal else EvrryAmberWarning
                    )
                    FilledTonalIconButton(
                        onClick = { onAdjustStock(1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Rapid 86 / In Stock Toggle Button
                Button(
                    onClick = onToggleAvailability,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isAvailable) Color(0xFFFEE2E2) else EvrryEmeraldBg
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (item.isAvailable) "86 Out of Stock" else "In Stock ✅",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isAvailable) EvrryRedDestructive else EvrryEmerald
                    )
                }
            }
        }
    }
}
