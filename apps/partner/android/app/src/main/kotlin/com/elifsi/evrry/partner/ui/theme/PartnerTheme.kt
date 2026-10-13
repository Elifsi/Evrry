package com.elifsi.evrry.partner.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val EvrryNavyDark = Color(0xFF0A0F1D)
val EvrryNavyMedium = Color(0xFF131D33)
val EvrryNavyLight = Color(0xFF1C294A)
val EvrryNavyBorder = Color(0xFF283A66)

val EvrryOrange = Color(0xFFFF6B00)
val EvrryOrangeDark = Color(0xFFD95700)
val EvrryGreen = Color(0xFF10B981)
val EvrryGreenDark = Color(0xFF059669)
val EvrryRed = Color(0xFFEF4444)
val EvrryYellow = Color(0xFFF59E0B)

val EvrryTextPrimary = Color(0xFFF8FAFC)
val EvrryTextSecondary = Color(0xFF94A3B8)
val EvrryTextMuted = Color(0xFF64748B)

private val DarkColorScheme = darkColorScheme(
    primary = EvrryOrange,
    onPrimary = Color.White,
    primaryContainer = EvrryNavyLight,
    onPrimaryContainer = EvrryTextPrimary,
    secondary = EvrryGreen,
    onSecondary = Color.White,
    background = EvrryNavyDark,
    onBackground = EvrryTextPrimary,
    surface = EvrryNavyMedium,
    onSurface = EvrryTextPrimary,
    surfaceVariant = EvrryNavyLight,
    onSurfaceVariant = EvrryTextSecondary,
    outline = EvrryNavyBorder,
    error = EvrryRed,
    onError = Color.White
)

@Composable
fun EvrryPartnerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
