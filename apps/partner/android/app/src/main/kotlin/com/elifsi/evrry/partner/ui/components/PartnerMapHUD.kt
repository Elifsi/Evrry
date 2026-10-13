package com.elifsi.evrry.partner.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.partner.model.DriverRideStatus
import com.elifsi.evrry.partner.ui.theme.*

/**
 * 3D Isometric Partner Vehicle HUD
 * Grounded in reference/amitshekhariitbhu_ridesharing-uber-lyft-app
 * Platform: Evrry Super App (Partner Android)
 * Elifsi Technologies Private Limited
 */

@Composable
fun PartnerMapHUD(
    pickupAddress: String,
    dropAddress: String,
    vehicleType: String = "bike",
    bearingDegrees: Float? = null,
    speedKmh: Int = 28,
    etaMinutes: Int = 12,
    status: DriverRideStatus = DriverRideStatus.ACCEPTED,
    modifier: Modifier = Modifier
) {
    // Kathmandu corridor simulation path (Thamel -> Kantipath -> Jamal -> Tripureshwor -> Patan)
    val waypoints = remember {
        listOf(
            Offset(0.18f, 0.22f), // Origin
            Offset(0.30f, 0.32f),
            Offset(0.42f, 0.40f),
            Offset(0.55f, 0.50f),
            Offset(0.68f, 0.62f),
            Offset(0.82f, 0.78f)  // Destination
        )
    }

    val infiniteProgress = rememberInfiniteTransition(label = "route_anim")
    val progress by infiniteProgress.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vehicle_progress"
    )

    // Pulse animation for GPS beacon
    val pulseScale by infiniteProgress.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gps_pulse_scale"
    )
    val pulseAlpha by infiniteProgress.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gps_pulse_alpha"
    )

    // Calculate vehicle position along route
    val (vehiclePos, calculatedBearing) = remember(progress) {
        val totalSegments = waypoints.size - 1
        val scaled = progress * totalSegments
        val segIndex = scaled.toInt().coerceIn(0, totalSegments - 1)
        val segProgress = scaled - segIndex

        val p1 = waypoints[segIndex]
        val p2 = waypoints[segIndex + 1]

        val currentX = p1.x + (p2.x - p1.x) * segProgress
        val currentY = p1.y + (p2.y - p1.y) * segProgress

        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        val angleRad = Math.atan2(dy.toDouble(), dx.toDouble())
        val deg = (Math.toDegrees(angleRad) + 90.0).toFloat()

        Pair(Offset(currentX, currentY), deg)
    }

    // Animated bearing smoothing (tracks explicit bearing if provided, else follows calculated route bearing)
    val targetBearing = bearingDegrees ?: calculatedBearing
    val animatedBearing by animateFloatAsState(
        targetValue = targetBearing,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "animated_bearing"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFEAF2FD))
            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(24.dp))
    ) {
        // 1. Interactive Road Canvas with Polyline Track
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Subtle green parks
            drawCircle(Color(0xFFDCFCE7), radius = 65f, center = Offset(w * 0.2f, h * 0.8f))
            drawCircle(Color(0xFFDCFCE7), radius = 80f, center = Offset(w * 0.85f, h * 0.2f))

            // Background City Road Grid
            val roadColor = Color(0xFFD6E4F8)
            drawLine(roadColor, Offset(0f, h * 0.45f), Offset(w, h * 0.35f), strokeWidth = 14f, cap = StrokeCap.Round)
            drawLine(roadColor, Offset(w * 0.38f, 0f), Offset(w * 0.44f, h), strokeWidth = 12f, cap = StrokeCap.Round)
            drawLine(roadColor, Offset(w * 0.72f, 0f), Offset(w * 0.76f, h), strokeWidth = 10f, cap = StrokeCap.Round)

            // Full Route Outline
            val fullPath = Path().apply {
                val p0 = waypoints.first()
                moveTo(p0.x * w, p0.y * h)
                for (i in 1 until waypoints.size) {
                    val p = waypoints[i]
                    lineTo(p.x * w, p.y * h)
                }
            }
            drawPath(
                fullPath,
                color = Color(0xFFBFDBFE),
                style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Active Driven Route (Vibrant Royal Blue)
            val traveledPath = Path().apply {
                val p0 = waypoints.first()
                moveTo(p0.x * w, p0.y * h)
                val totalSegments = waypoints.size - 1
                val scaled = progress * totalSegments
                val segIndex = scaled.toInt().coerceIn(0, totalSegments - 1)

                for (i in 1..segIndex) {
                    val p = waypoints[i]
                    lineTo(p.x * w, p.y * h)
                }
                lineTo(vehiclePos.x * w, vehiclePos.y * h)
            }
            drawPath(
                traveledPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1D4ED8), Color(0xFF2563EB))
                ),
                style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Polyline Glow
            drawPath(
                traveledPath,
                color = Color(0x332563EB),
                style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // 2. Origin Marker (Pickup or Driver Start)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 26.dp)
        ) {
            Surface(
                color = Color.White,
                shape = CircleShape,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .size(28.dp)
                    .border(2.dp, EvrryGreenSuccess, CircleShape)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("📍", fontSize = 13.sp)
                }
            }
        }

        // 3. Destination Marker (Dropoff Pin)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 28.dp, bottom = 28.dp)
        ) {
            Surface(
                color = EvrryRedDestructive,
                shape = CircleShape,
                shadowElevation = 4.dp,
                modifier = Modifier.size(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Drop Location",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 4. Live Vehicle Marker with Smooth Heading Rotation
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val posX = maxWidth * vehiclePos.x - 30.dp
            val posY = maxHeight * vehiclePos.y - 30.dp

            Box(
                modifier = Modifier
                    .offset(x = posX, y = posY)
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                // Live Radar Pulse
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = EvrryPrimaryBlue.copy(alpha = pulseAlpha),
                        radius = size.width * 0.24f * pulseScale,
                        center = Offset(size.width / 2f, size.height / 2f)
                    )
                }

                // Vehicle Body rotated by bearing
                Canvas(
                    modifier = Modifier
                        .size(44.dp)
                        .graphicsLayer {
                            rotationZ = animatedBearing
                        }
                ) {
                    when (vehicleType) {
                        "cab", "comfort_ev" -> drawCabMarker()
                        else -> drawBikeMarker()
                    }
                }
            }
        }

        // 5. Turn-by-Turn Instruction Banner
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xEE0F172A))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Maneuver",
                tint = Color(0xFF60A5FA),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = if (status == DriverRideStatus.ACCEPTED) "Turn right onto Kantipath in 200m" else "Straight on Thapathali in 400m",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text("•", color = Color(0xFF94A3B8), fontSize = 11.sp)
            Text("$speedKmh km/h", color = Color(0xFF60A5FA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("•", color = Color(0xFF94A3B8), fontSize = 11.sp)
            Text("ETA: ~$etaMinutes min", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        // 6. Bottom Corridor Info Pill
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCCFFFFFF))
                .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "🛣️ $pickupAddress → $dropAddress",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = EvrryDarkCharcoal,
                maxLines = 1
            )
        }
    }
}

private fun DrawScope.drawBikeMarker() {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Tires
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(cx - 2f, cy - 18f), size = Size(4f, 7f), cornerRadius = CornerRadius(2f, 2f))
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(cx - 2f, cy + 9f), size = Size(4f, 8f), cornerRadius = CornerRadius(2f, 2f))

    // Tank / Body
    val body = Path().apply {
        moveTo(cx, cy - 12f)
        lineTo(cx + 5f, cy - 6f)
        lineTo(cx + 4f, cy + 6f)
        lineTo(cx - 4f, cy + 6f)
        lineTo(cx - 5f, cy - 6f)
        close()
    }
    drawPath(body, color = Color(0xFF2563EB))

    // Seat
    drawRoundRect(color = Color(0xFF334155), topLeft = Offset(cx - 3.5f, cy - 3f), size = Size(7f, 10f), cornerRadius = CornerRadius(2f, 2f))

    // Handlebar
    drawLine(color = Color(0xFF64748B), start = Offset(cx - 9f, cy - 10f), end = Offset(cx + 9f, cy - 10f), strokeWidth = 2.4f, cap = StrokeCap.Round)
}

private fun DrawScope.drawCabMarker() {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Cab Body
    drawRoundRect(color = Color(0xFFF59E0B), topLeft = Offset(cx - 8f, cy - 16f), size = Size(16f, 32f), cornerRadius = CornerRadius(5f, 5f))
    // Windshield & Windows
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(cx - 6f, cy - 10f), size = Size(12f, 18f), cornerRadius = CornerRadius(2f, 2f))
    // Roof
    drawRoundRect(color = Color(0xFFD97706), topLeft = Offset(cx - 5f, cy - 5f), size = Size(10f, 11f), cornerRadius = CornerRadius(2f, 2f))
    // Taxi Light
    drawRoundRect(color = Color.White, topLeft = Offset(cx - 3f, cy - 3f), size = Size(6f, 2.5f), cornerRadius = CornerRadius(1f, 1f))
}
