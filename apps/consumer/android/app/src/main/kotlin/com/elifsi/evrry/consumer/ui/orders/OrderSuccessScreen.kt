package com.elifsi.evrry.consumer.ui.orders

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Order Success Screen
 * Ported directly from Commerce OrderSuccessScreen
 * Elifsi Technologies Private Limited
 */
@Composable
fun OrderSuccessScreen(
    orderId: String,
    onTrackOrder: (String) -> Unit,
    onBackToHome: () -> Unit
) {
    var showAnimation by remember { mutableStateOf(false) }

    val scaleAnimation by animateFloatAsState(
        targetValue = if (showAnimation) 1f else 0.2f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "successScale"
    )

    LaunchedEffect(Unit) {
        delay(100)
        showAnimation = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBFF))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = EvrryGreenSuccess.copy(alpha = 0.12f),
            shape = CircleShape,
            modifier = Modifier
                .size(110.dp)
                .scale(scaleAnimation)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Success",
                    tint = EvrryGreenSuccess,
                    modifier = Modifier.size(68.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Order Placed Successfully!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = EvrryDarkCharcoal
        )

        Surface(
            color = Color(0xFFEFF6FF),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(vertical = 10.dp)
                .border(1.dp, EvrryPrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
        ) {
            Text(
                text = "Order ID: #$orderId",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryPrimaryBlue,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
            )
        }

        // 60-SECOND POST-ORDER MODIFICATION BUFFER (Express Commerce Standard)
        var remainingSeconds by remember { mutableStateOf(60) }
        LaunchedEffect(remainingSeconds) {
            if (remainingSeconds > 0) {
                delay(1000L)
                remainingSeconds -= 1
            }
        }

        if (remainingSeconds > 0) {
            Surface(
                color = Color(0xFFFFFBEB),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .border(1.dp, Color(0xFFFCD34D), RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("⏱️", fontSize = 14.sp)
                            Text(
                                text = "Forgot an item? Add in ${remainingSeconds}s",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                        Surface(
                            color = Color(0xFF92400E),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${remainingSeconds}s",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Text(
                        text = "Zero extra delivery fee! Your Jhamsikhel Hub bag is currently being prepared.",
                        fontSize = 11.sp,
                        color = Color(0xFF78350F)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onBackToHome,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF92400E)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Text(
                            text = "+ Add Forgotten Items to Open Bag",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            Text(
                text = "The kitchen / hub is preparing your order. Your Evrry delivery partner will arrive in ~15-20 minutes.",
                fontSize = 13.sp,
                color = EvrryMutedText,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ACTION BUTTONS
        Button(
            onClick = { onTrackOrder(orderId) },
            colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Track Live Order Details →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackToHome,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Back to Explore", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = EvrryDarkCharcoal)
        }
    }
}
