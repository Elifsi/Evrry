package com.elifsi.evrry.consumer.ui.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elifsi.evrry.consumer.ui.theme.EvrryDarkCharcoal
import com.elifsi.evrry.consumer.ui.theme.EvrryMutedText
import com.elifsi.evrry.consumer.ui.theme.EvrryPrimaryBlue
import com.elifsi.evrry.consumer.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

/**
 * Brand Animated Splash Screen (1.5 seconds)
 * Highly optimized, zero thermal throttling, native Compose animations.
 * Evrry Logo + "Nepal's Everything Super App" + Auto-Routing.
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 */
@Composable
fun SplashScreen(
    viewModel: AuthViewModel,
    hasSeenOnboarding: Boolean = false
) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.85f) }

    LaunchedEffect(Unit) {
        // Entrance animation: 450ms smooth curve
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
        )
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
        )

        // Hold for the total 1500ms duration
        delay(700)

        // Exit subtle fade: 350ms
        alphaAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )

        // Auto-route based on saved session / onboarding flag
        viewModel.finishSplash(hasSeenOnboarding = hasSeenOnboarding)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF0F6FF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            // Brandmark Icon
            Surface(
                modifier = Modifier
                    .size(92.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(26.dp),
                        spotColor = EvrryPrimaryBlue.copy(alpha = 0.35f)
                    ),
                shape = RoundedCornerShape(26.dp),
                color = EvrryPrimaryBlue
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "e",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 52.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "evrry",
                    fontWeight = FontWeight.Black,
                    fontSize = 34.sp,
                    color = EvrryDarkCharcoal,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "🇳🇵",
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Brand Tagline
            Text(
                text = "Nepal's Everything Super App",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = EvrryMutedText,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Pulsing Micro Indicator (Battery-friendly, static layout)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(EvrryPrimaryBlue)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(EvrryPrimaryBlue.copy(alpha = 0.5f))
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(EvrryPrimaryBlue.copy(alpha = 0.25f))
                )
            }
        }
    }
}
