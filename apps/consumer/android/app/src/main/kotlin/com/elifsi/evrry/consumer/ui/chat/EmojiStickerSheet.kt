package com.elifsi.evrry.consumer.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * WhatsApp-style combined Emoji + Sticker bottom sheet for Evrry chat.
 * - Emoji tab: curated grid of Unicode emojis grouped by category
 * - Sticker tab: grid of open-source CDN sticker assets (no paid SDK)
 * - Animated emoji tab: animated GIF emojis via Coil
 *
 * Platform: Evrry Consumer Android
 * Elifsi Technologies Private Limited
 */

// ---------------------------------------------------------------------------
// Data
// ---------------------------------------------------------------------------

private data class EmojiCategory(val label: String, val emojis: List<String>)

private val EMOJI_CATEGORIES = listOf(
    EmojiCategory("😊 Smileys", listOf("😀","😃","😄","😁","😆","😅","😂","🤣","😊","😇","🙂","🙃","😉","😌","😍","🥰","😘","😗","😙","😚","😋","😛","😝","😜","🤪","🤨","🧐","🤓","😎","🥸","🤩","🥳","😏","😒","😞","😔","😟","😕","🙁","☹️","😣","😖","😫","😩","🥺","😢","😭","😤","😠","😡","🤬","🤯","😳","🥵","🥶")),
    EmojiCategory("👋 Gestures", listOf("👍","👎","👏","🙌","🤝","🤞","✌️","🤘","👌","🫶","💪","🖐️","✋","🤙","☝️","👆","👇","👈","👉","🫡","🤜","🤛","✊","👊","🫳","🫴","🙏")),
    EmojiCategory("❤️ Hearts",   listOf("❤️","🧡","💛","💚","💙","💜","🖤","🤍","🤎","💔","💕","💞","💓","💗","💖","💘","💝","💟","❣️","💌","🫀","♥️")),
    EmojiCategory("🎉 Objects",  listOf("🎉","🎂","🎁","⭐","✨","🔥","💯","🎶","📷","💻","📱","⏰","💡","🔑","💰","🎯","🏆","🚀","🌈","☀️","🍕","🍔","☕","🍩","🍜","🍣","🍦")),
    EmojiCategory("🌸 Nature",   listOf("🌸","🌺","🌻","🌼","🌷","🍀","🌙","⭐","☁️","🌧️","❄️","🐶","🐱","🐼","🦋","🌊","🐘","🦁","🐯","🦊","🐻","🐸","🦜"))
)

// Open-source stickers via Twemoji CDN (no paid SDK, royalty-free CC BY 4.0 license)
private val STICKER_URLS = listOf(
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f600.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f602.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f970.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f525.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f389.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f499.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f44d.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f64f.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f4af.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/2764.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f308.svg",
    "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f381.svg",
)

// Animated emoji GIFs via Noto Animated Emoji (Apache 2.0 licensed)
private val ANIMATED_EMOJI_URLS = listOf(
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f600/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f602/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f525/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f389/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f499/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f44d/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/1f64f/512.gif",
    "https://fonts.gstatic.com/s/e/notoemoji/latest/2764_fe0f/512.gif",
)

// ---------------------------------------------------------------------------
// Main Composable
// ---------------------------------------------------------------------------

@Composable
fun EmojiStickerSheet(
    onEmojiPick: (String) -> Unit,
    onStickerPick: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val evrryBlue = Color(0xFF2563EB)
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedEmojiCat by remember { mutableIntStateOf(0) }
    val tabs = listOf("😊 Emoji", "🎭 Animated", "🗂️ Stickers")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.weight(1f),
                containerColor = Color.Transparent,
                contentColor = evrryBlue
            ) {
                tabs.forEachIndexed { i, label ->
                    Tab(
                        selected = selectedTab == i,
                        onClick = { selectedTab = i },
                        text = {
                            Text(
                                label,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == i) evrryBlue else Color.Gray
                            )
                        }
                    )
                }
            }
            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
            }
        }

        Divider(color = Color(0xFFEEEEEE))

        when (selectedTab) {
            // ── EMOJI TAB ──────────────────────────────────────────────────
            0 -> {
                // Category row
                LazyRow(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(EMOJI_CATEGORIES.indices.toList()) { i ->
                        val cat = EMOJI_CATEGORIES[i]
                        val selected = selectedEmojiCat == i
                        Text(
                            text = cat.label.split(" ").first(),
                            fontSize = 20.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selected) evrryBlue.copy(alpha = 0.12f) else Color.Transparent)
                                .clickable { selectedEmojiCat = i }
                                .padding(8.dp)
                        )
                    }
                }
                // Emoji grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(EMOJI_CATEGORIES[selectedEmojiCat].emojis) { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onEmojiPick(emoji) }
                                .padding(6.dp),
                        )
                    }
                }
            }

            // ── ANIMATED EMOJI TAB ─────────────────────────────────────────
            1 -> {
                val context = LocalContext.current
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ANIMATED_EMOJI_URLS) { url ->
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onStickerPick(url) }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(url)
                                    // Coil handles GIF via ImageDecoderDecoder on API 28+
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Animated emoji",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            // ── STICKERS TAB ───────────────────────────────────────────────
            2 -> {
                val context = LocalContext.current
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(STICKER_URLS) { url ->
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF5F5F5))
                                .clickable { onStickerPick(url) }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Sticker",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}
