package com.elifsi.evrry.partner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = EvrryPrimaryBlue,
    onPrimary = EvrryWhite,
    primaryContainer = EvrryBgGradientBottom,
    onPrimaryContainer = EvrryNavy,
    secondary = EvrryEmerald,
    onSecondary = EvrryWhite,
    background = EvrryPaper,
    onBackground = EvrryInk,
    surface = EvrryWhite,
    onSurface = EvrryInk,
    surfaceVariant = EvrrySubtleSurface,
    onSurfaceVariant = EvrryMuted,
    outline = EvrryBorder
)

@Composable
fun EvrryPartnerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
