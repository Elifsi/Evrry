package com.elifsi.evrry.consumer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.VoiceAgentModel
import com.elifsi.evrry.consumer.model.VoiceAgentRegistry
import com.elifsi.evrry.consumer.ui.theme.*

/**
 * Top App Bar with In-App Back Navigation and ChatGPT-Style Model Switcher
 */
@Composable
fun EvrryTopBar(
    title: String? = null,
    subtitle: String? = null,
    canGoBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    onBackClick: () -> Unit = {},
    isHome: Boolean = false,
    activeModel: VoiceAgentModel = VoiceAgentRegistry.ellieMini,
    onSelectModel: (VoiceAgentModel) -> Unit = {},
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    cartItemCount: Int = 0,
    onCartClick: (() -> Unit)? = null
) {
    var showModelMenu by remember { mutableStateOf(false) }
    val effectiveBack = onBack ?: onBackClick
    val hasBack = canGoBack || (onBack != null)

    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LEFT: Back Button or Menu Hamburger
            if (hasBack) {
                IconButton(
                    onClick = effectiveBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, EvrrySubtleBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = EvrryDarkCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else if (isHome) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, EvrrySubtleBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = EvrryDarkCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }

            // CENTER: ChatGPT-Style Minimal Model Switcher [ Ellie ▾ ] or Title
            if (isHome) {
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
                            .clickable { showModelMenu = !showModelMenu }
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = activeModel.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = EvrryDarkCharcoal
                        )
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Switch Model",
                            tint = EvrryMutedText,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Dropdown Menu (ChatGPT style)
                    DropdownMenu(
                        expanded = showModelMenu,
                        onDismissRequest = { showModelMenu = false },
                        modifier = Modifier
                            .background(Color.White)
                            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(16.dp))
                            .width(220.dp)
                    ) {
                        Text(
                            text = "SELECT AI MODEL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryMutedText,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        VoiceAgentRegistry.allModels.forEach { model ->
                            val isSelected = model.id == activeModel.id
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = model.name,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 13.sp,
                                                color = EvrryDarkCharcoal
                                            )
                                            Surface(
                                                color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = model.badge,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) EvrryPrimaryBlue else EvrryMutedText,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = model.description,
                                            fontSize = 10.sp,
                                            color = EvrryMutedText,
                                            maxLines = 1
                                        )
                                    }
                                },
                                onClick = {
                                    onSelectModel(model)
                                    showModelMenu = false
                                },
                                trailingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = EvrryPrimaryBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            } else if (title != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = EvrryDarkCharcoal
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = EvrryMutedText
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // RIGHT: Cart Button (with Badge) & Profile Avatar Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onCartClick != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, EvrrySubtleBorder, CircleShape)
                            .clickable { onCartClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = if (cartItemCount > 0) EvrryPrimaryBlue else EvrryDarkCharcoal,
                            modifier = Modifier.size(20.dp)
                        )
                        if (cartItemCount > 0) {
                            Surface(
                                color = EvrryPrimaryBlue,
                                shape = CircleShape,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (cartItemCount > 99) "99+" else "$cartItemCount",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                if (isHome || onCartClick == null) {
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, EvrrySubtleBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = EvrryPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 5-Tab In-App Bottom Navigation Bar
 */
@Composable
fun EvrryBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    chatsUnreadCount: Int = 3
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple("home", "AI", Icons.Default.Star),
                Triple("explore", "Explore", Icons.Default.Search),
                Triple("chats", "Chats", Icons.Default.ChatBubble),
                Triple("rides", "Rides", Icons.Default.Place),
                Triple("activity", "Activity", Icons.Default.ReceiptLong),
                Triple("settings", "Profile", Icons.Default.Person)
            )

            tabs.forEach { (route, label, icon) ->
                val isSelected = currentRoute == route
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onNavigate(route) }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) EvrryPrimaryBlue else EvrryMutedText,
                            modifier = Modifier.size(22.dp)
                        )
                        if (route == "chats" && chatsUnreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .offset(x = 6.dp, y = (-4).dp)
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(EvrryPrimaryBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (chatsUnreadCount > 9) "9+" else chatsUnreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) EvrryPrimaryBlue else EvrryMutedText
                    )
                }
            }
        }
    }
}
