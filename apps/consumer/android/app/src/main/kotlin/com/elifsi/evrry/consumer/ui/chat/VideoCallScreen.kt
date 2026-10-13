package com.elifsi.evrry.consumer.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
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
import com.elifsi.evrry.consumer.model.ActiveCallSession
import com.elifsi.evrry.consumer.model.CallState
import com.elifsi.evrry.consumer.ui.theme.EvrryDarkCharcoal
import com.elifsi.evrry.consumer.ui.theme.EvrryPrimaryBlue
import com.elifsi.evrry.consumer.viewmodel.CallViewModel

/**
 * WhatsApp-Grade Fullscreen Video Call Screen
 * Modeled after WhatsApp WebRTC video calling experience
 * Features: Fullscreen remote stream canvas, floating Picture-in-Picture (PiP) local preview,
 * camera flip (front/back), video mute, audio mute, and duration ticker.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@Composable
fun VideoCallScreen(
    session: ActiveCallSession,
    callViewModel: CallViewModel,
    onCallEnded: () -> Unit
) {
    // Automatically navigate back when call transitions to ENDED
    LaunchedEffect(session.state) {
        if (session.state == CallState.ENDED) {
            onCallEnded()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // 1. FULLSCREEN REMOTE VIDEO FEED (Simulated WebRTC Video Track)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (session.isVideoEnabled) {
                // Gradient backdrop representing live incoming WebRTC video frames
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A),
                                    Color(0xFF020617)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = EvrryPrimaryBlue.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(2.dp, EvrryPrimaryBlue)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = session.calleeName.take(2).uppercase(),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Text(
                            text = if (session.state == CallState.CONNECTED) "WebRTC Video Stream Active" else "Waiting for video stream...",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Video Paused Placeholder
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.VideocamOff,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Camera is off",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // 2. TOP HUD: CALLER INFO, DURATION & E2EE BADGE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = session.calleeName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = when (session.state) {
                        CallState.CONNECTING -> "Connecting..."
                        CallState.RINGING -> "Ringing..."
                        CallState.CONNECTED -> callViewModel.formatDuration(session.durationSeconds)
                        CallState.ENDED -> "Call Ended"
                    },
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // E2EE Lock Badge
            Surface(
                color = Color.Black.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "E2EE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 3. FLOATING PICTURE-IN-PICTURE (PiP) LOCAL CAMERA PREVIEW (Top-Right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 60.dp, end = 16.dp)
                .size(width = 100.dp, height = 145.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E293B))
                .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("📷", fontSize = 24.sp)
                Text(
                    text = if (session.isFrontCamera) "Front" else "Rear",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // 4. BOTTOM FLOATING CONTROLS DOCK
        Surface(
            color = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 28.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // FLIP CAMERA
                IconButton(
                    onClick = { callViewModel.flipCamera() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Cameraswitch,
                        contentDescription = "Flip Camera",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // TOGGLE VIDEO
                IconButton(
                    onClick = { callViewModel.toggleVideo() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (!session.isVideoEnabled) Color.White else Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = if (session.isVideoEnabled) Icons.Rounded.Videocam else Icons.Rounded.VideocamOff,
                        contentDescription = "Toggle Video",
                        tint = if (!session.isVideoEnabled) EvrryDarkCharcoal else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // MUTE / UNMUTE AUDIO
                IconButton(
                    onClick = { callViewModel.toggleMute() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (session.isMuted) Color.White else Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = if (session.isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                        contentDescription = "Mute Audio",
                        tint = if (session.isMuted) EvrryDarkCharcoal else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // END CALL BUTTON
                FloatingActionButton(
                    onClick = {
                        callViewModel.endCall()
                        onCallEnded()
                    },
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CallEnd,
                        contentDescription = "End Call",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
