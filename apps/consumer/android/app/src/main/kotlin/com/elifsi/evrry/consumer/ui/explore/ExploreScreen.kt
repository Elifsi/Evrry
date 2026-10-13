package com.elifsi.evrry.consumer.ui.explore

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.ServiceVertical
import com.elifsi.evrry.consumer.ui.components.EvrryTopBar
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.ExploreViewModel

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onNavigateToCart: () -> Unit,
    onNavigateToFood: () -> Unit = {},
    onNavigateToGrocery: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val selectedVertical by viewModel.selectedVertical.collectAsState()
    val items by viewModel.items.collectAsState()
    val cart by viewModel.cart.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
    ) {
        // TOP APP BAR with Top-Right Cart Icon & Badge
        EvrryTopBar(
            title = "Explore Evrry",
            canGoBack = false,
            cartItemCount = cart.itemCount,
            onCartClick = onNavigateToCart,
            onProfileClick = onProfileClick
        )

        // CATEGORY CHIPS SCROLLER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val allChipSelected = selectedVertical == "all"
            Surface(
                color = if (allChipSelected) EvrryDarkCharcoal else Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .border(1.dp, if (allChipSelected) EvrryDarkCharcoal else EvrrySubtleBorder, RoundedCornerShape(20.dp))
                    .clickable { viewModel.selectVertical("all") }
            ) {
                Text(
                    text = "✨ All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (allChipSelected) Color.White else EvrryDarkCharcoal,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }

            ServiceVertical.activeVerticals.forEach { vertical ->
                val isSelected = selectedVertical == vertical.id
                Surface(
                    color = if (isSelected) EvrryDarkCharcoal else Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .border(1.dp, if (isSelected) EvrryDarkCharcoal else EvrrySubtleBorder, RoundedCornerShape(20.dp))
                        .clickable {
                            when (vertical) {
                                ServiceVertical.FOOD -> onNavigateToFood()
                                ServiceVertical.GROCERY -> onNavigateToGrocery()
                                else -> viewModel.selectVertical(vertical.id)
                            }
                        }
                ) {
                    Text(
                        text = "${vertical.emoji} ${vertical.title}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Color.White else EvrryDarkCharcoal,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // CATALOG CARDS LIST
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
        ) {
            // DEDICATED FOOD DELIVERY BANNER
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToFood() },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(Color(0xFFF97316), Color(0xFFD97706))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("🥟 FRESH & HOT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.85f))
                                Text("Food & Restaurant Delivery", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Kathmandu & Lalitpur top kitchens • 25 min delivery", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(color = Color.White, shape = RoundedCornerShape(8.dp)) {
                                    Text("Explore Kitchens →", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2410C), modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                                }
                            }
                            Text("🍲", fontSize = 38.sp)
                        }
                    }
                }
            }

            // DEDICATED GROCERY QUICK MART BANNER
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToGrocery() },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(Color(0xFF059669), Color(0xFF0D9488))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("⚡ 10-15 MIN DISPATCH", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.85f))
                                Text("Grocery Quick Mart", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("DDC Milk, Farm Eggs, Wai Wai & Fresh Produce", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(color = Color.White, shape = RoundedCornerShape(8.dp)) {
                                    Text("Shop Quick Mart →", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857), modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                                }
                            }
                            Text("🥦", fontSize = 38.sp)
                        }
                    }
                }
            }
            items(items) { item ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = EvrryDarkCharcoal
                                )
                                Text(
                                    text = item.subtitle,
                                    fontSize = 12.sp,
                                    color = EvrryMutedText,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEF3C7))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("⭐", fontSize = 10.sp)
                                Text(
                                    text = "${item.rating}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Rs ${item.priceNpr.toInt()}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = EvrryDarkCharcoal
                                    )
                                    item.originalPriceNpr?.let { orig ->
                                        Text(
                                            text = "Rs ${orig.toInt()}",
                                            fontSize = 12.sp,
                                            color = EvrryMutedText
                                        )
                                    }
                                }
                                Text(
                                    text = "by ${item.vendorName}",
                                    fontSize = 11.sp,
                                    color = EvrryMutedText
                                )
                            }

                            Button(
                                onClick = { viewModel.addToCart(item) },
                                colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // FLOATING CART BAR (If cart has items)
        if (cart.itemCount > 0) {
            Surface(
                color = EvrryDarkCharcoal,
                shape = RoundedCornerShape(18.dp),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onNavigateToCart() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                        Text(
                            text = "${cart.itemCount} items in Cart",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "View Cart · Rs ${cart.totalNpr.toInt()} →",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
