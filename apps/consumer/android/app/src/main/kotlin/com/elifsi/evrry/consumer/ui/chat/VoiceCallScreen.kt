package com.elifsi.evrry.consumer.ui.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.CallEnd
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.model.ActiveCallSession
import com.elifsi.evrry.consumer.model.CallState
import com.elifsi.evrry.consumer.ui.theme.EvrryDarkCharcoal
import com.elifsi.evrry.consumer.ui.theme.EvrryPrimaryBlue
import com.elifsi.evrry.consumer.viewmodel.CallViewModel

/**
 * WhatsApp-Grade Fullscreen Audio Voice Call Screen
 * Modeled after WhatsApp iOS / Android calling UI
 * Features: Pulsing animated avatar rings, active duration ticker,
 * hardware mute/speaker toggles, E2EE verification badge, and cellular fallback.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@Composable
fun VoiceCallScreen(
    session: ActiveCallSession,
    callViewModel: CallViewModel,
    onCallEnded: () -> Unit
) {
    val context = LocalContext.current

    // Pulsing animation for avatar when ringing or connected
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Automatically navigate back when call transitions to ENDED
    LaunchedEffect(session.state) {
        if (session.state == CallState.ENDED) {
            onCallEnded()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0B172A), // Dark Charcoal
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP HEADER: E2EE NOTICE & CALL STATUS
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // E2EE Lock Pill
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFFDE047),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "End-to-End Encrypted",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // CALLEE NAME
                Text(
                    text = session.calleeName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // CALL STATUS / DURATION
                Text(
                    text = when (session.state) {
                        CallState.CONNECTING -> "Connecting..."
                        CallState.RINGING -> "Ringing..."
                        CallState.CONNECTED -> callViewModel.formatDuration(session.durationSeconds)
                        CallState.ENDED -> "Call Ended"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // CENTER: PULSING CALL AVATAR
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Outer Glow Pulse Ring
                if (session.state == CallState.RINGING || session.state == CallState.CONNECTED) {
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(EvrryPrimaryBlue.copy(alpha = 0.2f))
                    )
                }

                // Inner Avatar Surface
                Surface(
                    modifier = Modifier.size(130.dp),
                    shape = CircleShape,
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(2.dp, EvrryPrimaryBlue)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = session.calleeName.take(2).uppercase(),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            // BOTTOM CONTROLS DOCK
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Secondary Controls Row: Speaker, Cellular Fallback, Mute
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // SPEAKER BUTTON
                    CallActionButton(
                        icon = Icons.Rounded.VolumeUp,
                        label = "Speaker",
                        isActive = session.isSpeakerOn,
                        onClick = { callViewModel.toggleSpeaker() }
                    )

                    // CELLULAR SIM CALL FALLBACK BUTTON (Nepal direct dial)
                    CallActionButton(
                        icon = Icons.Rounded.Phone,
                        label = "SIM Call",
                        isActive = false,
                        onClick = { callViewModel.dialCellularFallback(context) }
                    )

                    // MUTE BUTTON
                    CallActionButton(
                        icon = if (session.isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                        label = if (session.isMuted) "Unmute" else "Mute",
                        isActive = session.isMuted,
                        onClick = { callViewModel.toggleMute() }
                    )
                }

                // RED END CALL BUTTON
                FloatingActionButton(
                    onClick = {
                        callViewModel.endCall()
                        onCallEnded()
                    },
                    containerColor = Color(0xFFEF4444), // Crimson Red
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(68.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CallEnd,
                        contentDescription = "End Call",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CallActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick),
            shape = CircleShape,
            color = if (isActive) Color.White else Color.White.copy(alpha = 0.15f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) EvrryDarkCharcoal else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
    }
}
