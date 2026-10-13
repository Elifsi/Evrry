package com.elifsi.evrry.consumer.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.LoyaltyLeague
import com.elifsi.evrry.consumer.ui.theme.*
import com.elifsi.evrry.consumer.viewmodel.LoyaltyViewModel

/**
 * Dedicated Rewards & Loyalty Tier Screen
 * Shows points balance, progress to next tier, perks breakdown, and earning guides.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoyaltyRewardsScreen(
    loyaltyViewModel: LoyaltyViewModel,
    onBack: () -> Unit
) {
    val loyaltyState by loyaltyViewModel.loyaltyState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Rewards & Loyalty", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EvrryDarkCharcoal)
                        Text("Evrry Club Tier & Points perks", fontSize = 11.sp, color = EvrryMutedText)
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
            // 1. TIER HERO CARD
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color.Transparent,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1E3A8A), Color(0xFF2563EB))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("CURRENT BALANCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                                    Text("${loyaltyState.tierPoints} Points", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                                Surface(
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = loyaltyState.league.label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Tier Progress", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                    Text("${loyaltyState.pointsToNext} pts to next tier", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                LinearProgressIndicator(
                                    progress = { loyaltyState.progressToNext },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = Color.White,
                                    trackColor = Color.White.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }

            // 2. HOW TO EARN POINTS
            item {
                Text("HOW TO EARN POINTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText)
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
                        EarnRuleRow(icon = Icons.Default.DirectionsCar, title = "Mobility Rides", description = "Earn 10 points per completed ride")
                        HorizontalDivider(color = EvrrySubtleBorder)
                        EarnRuleRow(icon = Icons.Default.Fastfood, title = "Food & Grocery Orders", description = "Earn 1 point for every Rs 50 spent")
                        HorizontalDivider(color = EvrrySubtleBorder)
                        EarnRuleRow(icon = Icons.Default.RateReview, title = "Ratings & Reviews", description = "Earn 50 points per rated driver/kitchen")
                    }
                }
            }

            // 3. TIER PRIVILEGES BREAKDOWN
            item {
                Text("TIER PRIVILEGES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EvrryMutedText, modifier = Modifier.padding(top = 6.dp))
            }

            listOf(
                Triple("Bronze Explorer", "Base 1x points · Standard booking · Zero commission", Color(0xFF94A3B8)),
                Triple("Silver Voyager", "1.25x points multiplier · 10% off restaurant prep · Priority dispatch", Color(0xFF64748B)),
                Triple("Gold Executive", "1.5x points multiplier · Free delivery on orders > Rs 500 · Instant courier pairing", Color(0xFFD97706)),
                Triple("Platinum VIP", "2x points multiplier · Dedicated VIP concierge · Zero cancellation fees", Color(0xFF6366F1))
            ).forEach { (tier, perks, color) ->
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(shape = CircleShape, color = color.copy(alpha = 0.15f), modifier = Modifier.size(40.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                                }
                            }
                            Column {
                                Text(tier, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EvrryDarkCharcoal)
                                Text(perks, fontSize = 11.sp, color = EvrryMutedText, lineHeight = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EarnRuleRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = CircleShape, color = Color(0xFFEFF6FF), modifier = Modifier.size(36.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = EvrryPrimaryBlue, modifier = Modifier.size(18.dp))
            }
        }
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EvrryDarkCharcoal)
            Text(description, fontSize = 11.sp, color = EvrryMutedText)
        }
    }
}
