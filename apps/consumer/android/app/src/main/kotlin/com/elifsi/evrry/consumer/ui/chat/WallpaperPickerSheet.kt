package com.elifsi.evrry.consumer.ui.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * WhatsApp-style chat wallpaper picker bottom sheet.
 * Offers custom photos from gallery plus 12 gradient and solid-colour presets.
 * Selecting a wallpaper calls onPick with a URI or preset ID that the
 * ChatRoomScreen reads to render the background.
 *
 * Platform: Evrry Consumer Android
 * Elifsi Technologies Private Limited
 */

data class WallpaperPreset(
    val id: String,
    val label: String,
    val colors: List<Color>  // 1 = solid, 2 = gradient
)

val WALLPAPER_PRESETS = listOf(
    WallpaperPreset("default",    "Default",      listOf(Color(0xFFF0F4FF))),
    WallpaperPreset("dusk",       "Dusk",         listOf(Color(0xFF667EEA), Color(0xFF764BA2))),
    WallpaperPreset("sunrise",    "Sunrise",      listOf(Color(0xFFFF6B6B), Color(0xFFFFE66D))),
    WallpaperPreset("ocean",      "Ocean",        listOf(Color(0xFF00B4DB), Color(0xFF0083B0))),
    WallpaperPreset("forest",     "Forest",       listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    WallpaperPreset("rose",       "Rose",         listOf(Color(0xFFFF758C), Color(0xFFFF7EB3))),
    WallpaperPreset("midnight",   "Midnight",     listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))),
    WallpaperPreset("peach",      "Peach",        listOf(Color(0xFFFFB347), Color(0xFFFF8C94))),
    WallpaperPreset("lavender",   "Lavender",     listOf(Color(0xFFE8D5FF))),
    WallpaperPreset("mint",       "Mint",         listOf(Color(0xFFD4EDDA))),
    WallpaperPreset("sand",       "Sand",         listOf(Color(0xFFF5E6C8))),
    WallpaperPreset("slate",      "Slate",        listOf(Color(0xFFE2E8F0))),
)

@Composable
fun WallpaperPickerSheet(
    currentWallpaperId: String?,
    onPick: (presetId: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val evrryBlue = Color(0xFF2563EB)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onPick(uri.toString())
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(bottom = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chat Wallpaper",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B172A),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
            }
        }

        Text(
            text = "Choose a custom photo or preset for this chat",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 6.dp)
        )

        // WhatsApp-style "My Photos / Gallery" Option
        Surface(
            color = Color(0xFFEFF6FF),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable { galleryLauncher.launch("image/*") }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(evrryBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "My Photos",
                        tint = evrryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "My Photos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B172A)
                    )
                    Text(
                        text = "Choose from your device gallery",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Divider(color = Color(0xFFEEEEEE))
        Spacer(Modifier.height(6.dp))

        Text(
            text = "PRESETS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(WALLPAPER_PRESETS) { preset ->
                val isSelected = currentWallpaperId == preset.id
                val brush = if (preset.colors.size == 1)
                    Brush.radialGradient(listOf(preset.colors[0], preset.colors[0]))
                else
                    Brush.linearGradient(preset.colors)

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush)
                        .border(
                            width = if (isSelected) 3.dp else 0.5.dp,
                            color = if (isSelected) evrryBlue else Color(0xFFCCCCCC),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onPick(preset.id) },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text = preset.label,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}
