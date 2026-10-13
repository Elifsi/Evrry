package com.elifsi.evrry.partner.ui.grocery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.GrocerySupermarketViewModel

/**
 * Supermarket Partner Main Container Screen
 * 3 operational tabs (Pick-and-Pack Orders, Inventory Stock, Barcode / SKU Scanner).
 * Elifsi Technologies Private Limited
 */
@Composable
fun GrocerySupermarketScreen(
    groceryViewModel: GrocerySupermarketViewModel = viewModel()
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Pick & Pack, 1: Inventory, 2: Barcode Scanner

    Scaffold(
        bottomBar = {
            if (activeTab != 2) { // Hide tab bar during full-screen camera scanning
                NavigationBar(
                    containerColor = EvrryWhite,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        icon = { Icon(Icons.Default.Checklist, contentDescription = "Pick & Pack") },
                        label = { Text("Pick & Pack", fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EvrryPrimaryBlue,
                            selectedTextColor = EvrryPrimaryBlue,
                            indicatorColor = EvrryPrimaryBlue.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        icon = { Icon(Icons.Default.Inventory2, contentDescription = "Inventory") },
                        label = { Text("Inventory", fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EvrryPrimaryBlue,
                            selectedTextColor = EvrryPrimaryBlue,
                            indicatorColor = EvrryPrimaryBlue.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Barcode") },
                        label = { Text("Barcode Audit", fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EvrryPrimaryBlue,
                            selectedTextColor = EvrryPrimaryBlue,
                            indicatorColor = EvrryPrimaryBlue.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (activeTab) {
                0 -> GroceryPickPackScreen(
                    viewModel = groceryViewModel,
                    onOpenScanner = { activeTab = 2 }
                )
                1 -> GroceryInventoryScreen(
                    viewModel = groceryViewModel,
                    onOpenScanner = { activeTab = 2 }
                )
                2 -> GroceryBarcodeScannerScreen(
                    viewModel = groceryViewModel,
                    onBack = { activeTab = 0 }
                )
            }
        }
    }
}
