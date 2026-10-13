package com.elifsi.evrry.consumer.ui.components

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.network.EvrryMapRoutingService
import com.elifsi.evrry.consumer.ui.theme.*

/**
 * 3D Isometric Vehicle Types supported by Evrry Super App
 */
enum class VehicleType {
    SCOOTER,     // Delivery Moped with Insulated Thermal Box (Quick Mart / Food)
    MOTORCYCLE,  // Two-Wheeler Moto Ride (Pathao style)
    CAB,         // City Cab / EV Sedan (Mobility)
}

/**
 * Render an authentic 3D Isometric Vehicle Marker with drop shadow,
 * radar pulse ring, and smooth heading rotation.
 */
@Composable
fun IsometricVehicleMarker(
    vehicleType: VehicleType,
    bearingDegrees: Float,
    modifier: Modifier = Modifier,
    accentColor: Color = EvrryPrimaryBlue
) {
    // Pulse animation for real-time GPS telemetry socket
    val infiniteTransition = rememberInfiniteTransition(label = "gps_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    // Smooth heading rotation interpolation
    val animatedBearing by animateFloatAsState(
        targetValue = bearingDegrees,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "vehicle_bearing"
    )

    Box(
        modifier = modifier.size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. Live Radar GPS Pulse Ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.width * 0.22f
            drawCircle(
                color = accentColor.copy(alpha = pulseAlpha),
                radius = baseRadius * pulseScale,
                center = center
            )
        }

        // 2. Ground Contact Ambient Shadow
        Canvas(
            modifier = Modifier
                .size(46.dp, 26.dp)
                .offset(y = 12.dp)
        ) {
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x55000000), Color(0x15000000), Color.Transparent),
                    center = Offset(size.width / 2f, size.height / 2f)
                ),
                topLeft = Offset.Zero,
                size = size
            )
        }

        // 3. 3D Isometric Vehicle Canvas (Rotated by bearing)
        Canvas(
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer {
                    rotationZ = animatedBearing
                }
        ) {
            when (vehicleType) {
                VehicleType.SCOOTER -> drawIsometricDeliveryScooter(accentColor)
                VehicleType.MOTORCYCLE -> drawIsometricMotorcycle(accentColor)
                VehicleType.CAB -> drawIsometricCab(accentColor)
            }
        }
    }
}

/**
 * Draw 3D Isometric Delivery Scooter (with rear delivery cube)
 */
private fun DrawScope.drawIsometricDeliveryScooter(accent: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Under-chassis shadow
    drawOval(
        color = Color(0x33000000),
        topLeft = Offset(cx - 14f, cy - 4f),
        size = Size(28f, 16f)
    )

    // Rear Delivery Thermal Box (Elevated 3D Cube)
    // Box bottom base
    val boxBase = Path().apply {
        moveTo(cx - 9f, cy + 2f)
        lineTo(cx + 9f, cy + 2f)
        lineTo(cx + 7f, cy + 18f)
        lineTo(cx - 7f, cy + 18f)
        close()
    }
    drawPath(boxBase, color = Color(0xFF0038B3))

    // Box top facet (lit)
    val boxTop = Path().apply {
        moveTo(cx - 8f, cy - 2f)
        lineTo(cx + 8f, cy - 2f)
        lineTo(cx + 7f, cy + 10f)
        lineTo(cx - 7f, cy + 10f)
        close()
    }
    drawPath(boxTop, color = accent)

    // White "E" logo on the delivery box lid
    drawCircle(
        color = Color.White,
        radius = 3.5f,
        center = Offset(cx, cy + 4f)
    )

    // Main Scooter Chassis Body (Streamlined aero cowl)
    val bodyPath = Path().apply {
        moveTo(cx, cy - 18f)        // Front nose tip
        lineTo(cx + 6f, cy - 10f)   // Right shoulder
        lineTo(cx + 5f, cy + 4f)    // Right footboard
        lineTo(cx - 5f, cy + 4f)    // Left footboard
        lineTo(cx - 6f, cy - 10f)   // Left shoulder
        close()
    }
    drawPath(
        bodyPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF3880FF), accent, Color(0xFF002C99))
        )
    )

    // Dark Footboard & Rider Seat
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(cx - 4.5f, cy - 6f),
        size = Size(9f, 11f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Front Windshield (Glass reflection)
    val visorPath = Path().apply {
        moveTo(cx - 3.5f, cy - 15f)
        lineTo(cx + 3.5f, cy - 15f)
        lineTo(cx + 2.5f, cy - 11f)
        lineTo(cx - 2.5f, cy - 11f)
        close()
    }
    drawPath(visorPath, color = Color(0xCCBAE6FD))

    // Front Headlight Beam (Bright Xenon Gleam)
    drawCircle(
        color = Color(0xFFFEF08A),
        radius = 2.5f,
        center = Offset(cx, cy - 18.5f)
    )

    // Handlebar & Side Mirrors
    drawLine(
        color = Color(0xFF334155),
        start = Offset(cx - 9f, cy - 12f),
        end = Offset(cx + 9f, cy - 12f),
        strokeWidth = 2.2f,
        cap = StrokeCap.Round
    )
    // Left & Right Mirrors
    drawCircle(color = Color(0xFFCBD5E1), radius = 1.8f, center = Offset(cx - 8.5f, cy - 13.5f))
    drawCircle(color = Color(0xFFCBD5E1), radius = 1.8f, center = Offset(cx + 8.5f, cy - 13.5f))
}

/**
 * Draw 3D Isometric Commuter Motorcycle
 */
private fun DrawScope.drawIsometricMotorcycle(accent: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Front & Rear Tires
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(cx - 2f, cy - 20f), size = Size(4f, 8f), cornerRadius = CornerRadius(2f, 2f))
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(cx - 2.5f, cy + 10f), size = Size(5f, 9f), cornerRadius = CornerRadius(2f, 2f))

    // Long Tank & Chassis Frame
    val tankPath = Path().apply {
        moveTo(cx, cy - 14f)
        lineTo(cx + 5.5f, cy - 7f)
        lineTo(cx + 4f, cy + 6f)
        lineTo(cx - 4f, cy + 6f)
        lineTo(cx - 5.5f, cy - 7f)
        close()
    }
    drawPath(tankPath, color = accent)

    // Double Rider Cushion Seat
    drawRoundRect(
        color = Color(0xFF334155),
        topLeft = Offset(cx - 3.5f, cy - 3f),
        size = Size(7f, 12f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Handlebar
    drawLine(
        color = Color(0xFF64748B),
        start = Offset(cx - 10f, cy - 11f),
        end = Offset(cx + 10f, cy - 11f),
        strokeWidth = 2.4f,
        cap = StrokeCap.Round
    )
}

/**
 * Draw 3D Isometric City Cab / Car
 */
private fun DrawScope.drawIsometricCab(accent: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Cab Body Base
    drawRoundRect(
        color = Color(0xFFF59E0B), // Vibrant Taxi Amber
        topLeft = Offset(cx - 9f, cy - 18f),
        size = Size(18f, 36f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Windshield & Windows (Glass)
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(cx - 7f, cy - 11f),
        size = Size(14f, 20f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Roof Top
    drawRoundRect(
        color = Color(0xFFD97706),
        topLeft = Offset(cx - 6f, cy - 6f),
        size = Size(12f, 13f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Taxi Roof Light Beacon
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(cx - 3.5f, cy - 4f),
        size = Size(7f, 3f),
        cornerRadius = CornerRadius(1.5f, 1.5f)
    )

    // Headlights
    drawCircle(color = Color(0xFFFEF08A), radius = 2f, center = Offset(cx - 6f, cy - 17f))
    drawCircle(color = Color(0xFFFEF08A), radius = 2f, center = Offset(cx + 6f, cy - 17f))
}

/**
 * High-Performance Interactive Map View with Animated 3D Isometric Rider
 * along the authentic Kathmandu navigation polyline.
 */
@Composable
fun EvrryLiveTrackingMap(
    vendorName: String,
    destinationAddress: String,
    vehicleType: VehicleType = VehicleType.SCOOTER,
    riderName: String? = "Bikash Tamang",
    etaMinutes: Int = 12,
    speedKmh: Int = 26,
    modifier: Modifier = Modifier
) {
    // Realistic Kathmandu corridor waypoints (Jhamsikhel Hub -> Pulchowk -> Sanepa Heights)
    val waypoints = remember {
        listOf(
            Offset(0.18f, 0.22f), // Origin (Vendor / Hub)
            Offset(0.28f, 0.30f),
            Offset(0.38f, 0.35f),
            Offset(0.50f, 0.44f), // Midpoint junction
            Offset(0.62f, 0.52f),
            Offset(0.72f, 0.65f),
            Offset(0.82f, 0.78f)  // Destination (Customer Home)
        )
    }

    // Animation progress along the route (0.0 to 1.0)
    val infiniteProgress = rememberInfiniteTransition(label = "route_anim")
    val progress by infiniteProgress.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rider_progress"
    )

    // Calculate rider position and bearing angle based on progress
    val (riderPosition, riderBearing) = remember(progress) {
        val totalSegments = waypoints.size - 1
        val scaled = progress * totalSegments
        val segIndex = scaled.toInt().coerceIn(0, totalSegments - 1)
        val segProgress = scaled - segIndex

        val p1 = waypoints[segIndex]
        val p2 = waypoints[segIndex + 1]

        val currentX = p1.x + (p2.x - p1.x) * segProgress
        val currentY = p1.y + (p2.y - p1.y) * segProgress

        // Compute angle in degrees
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        val angleRad = Math.atan2(dy.toDouble(), dx.toDouble())
        val deg = (Math.toDegrees(angleRad) + 90.0).toFloat()

        Pair(Offset(currentX, currentY), deg)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFEBF3FC))
            .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(20.dp))
    ) {
        // 1. Kathmandu Road Canvas Background & Polylines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Green Parks & River bend
            drawCircle(
                color = Color(0xFFDCFCE7),
                radius = 70f,
                center = Offset(w * 0.25f, h * 0.75f)
            )
            drawCircle(
                color = Color(0xFFDCFCE7),
                radius = 90f,
                center = Offset(w * 0.85f, h * 0.25f)
            )

            // Background City Road Grid (Subtle street outlines)
            val roadBrush = Color(0xFFD4E2F4)
            drawLine(roadBrush, Offset(0f, h * 0.45f), Offset(w, h * 0.35f), strokeWidth = 14f, cap = StrokeCap.Round)
            drawLine(roadBrush, Offset(w * 0.40f, 0f), Offset(w * 0.45f, h), strokeWidth = 12f, cap = StrokeCap.Round)
            drawLine(roadBrush, Offset(w * 0.70f, 0f), Offset(w * 0.75f, h), strokeWidth = 10f, cap = StrokeCap.Round)
            drawLine(roadBrush, Offset(0f, h * 0.75f), Offset(w, h * 0.80f), strokeWidth = 14f, cap = StrokeCap.Round)

            // Complete Planned Route Track (Grey background track)
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

            // Traveled Route Polyline (Bold Electric Blue)
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
                lineTo(riderPosition.x * w, riderPosition.y * h)
            }
            drawPath(
                traveledPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF004CE8), Color(0xFF2563EB))
                ),
                style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Outer polyline glow
            drawPath(
                traveledPath,
                color = Color(0x33004CE8),
                style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // 2. Origin Marker (Store / Hub)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 28.dp)
        ) {
            Surface(
                color = Color.White,
                shape = CircleShape,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .size(28.dp)
                    .border(1.5.dp, Color(0xFF10B981), CircleShape)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🏪", fontSize = 13.sp)
                }
            }
        }

        // 3. Destination Marker (Customer Pin)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 28.dp, bottom = 32.dp)
        ) {
            Surface(
                color = EvrryPrimaryBlue,
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

        // 4. Live 3D Isometric Vehicle Marker (Animated along path)
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val posX = maxWidth * riderPosition.x - 32.dp
            val posY = maxHeight * riderPosition.y - 32.dp

            Box(
                modifier = Modifier
                    .offset(x = posX, y = posY)
            ) {
                IsometricVehicleMarker(
                    vehicleType = vehicleType,
                    bearingDegrees = riderBearing,
                    accentColor = EvrryPrimaryBlue
                )
            }
        }

        // 5. Top Floating HUD (Live Telemetry, Speed & ETA)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xEEFFFFFF))
                .border(1.dp, EvrrySubtleBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Live Socket Indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
            )
            Text(
                text = "Live GPS Socket",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryDarkCharcoal
            )
            Text(
                text = "•",
                fontSize = 11.sp,
                color = EvrryMutedText
            )
            Text(
                text = "$speedKmh km/h",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EvrryPrimaryBlue
            )
            Text(
                text = "•",
                fontSize = 11.sp,
                color = EvrryMutedText
            )
            Text(
                text = "ETA: ~$etaMinutes min",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF047857)
            )
        }

        // 6. Bottom Location Pill (Kathmandu Corridor)
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xDD0F172A))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Bearing",
                tint = Color.White,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = "$vendorName → $destinationAddress",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
