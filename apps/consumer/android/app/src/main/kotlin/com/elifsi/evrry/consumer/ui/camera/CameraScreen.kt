package com.elifsi.evrry.consumer.ui.camera

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.elifsi.evrry.consumer.model.CameraFilterLens
import com.elifsi.evrry.consumer.model.QrScanResult
import com.elifsi.evrry.consumer.ui.theme.EvrryNavy
import com.elifsi.evrry.consumer.viewmodel.CameraViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onBack: () -> Unit,
    onOpenMemories: () -> Unit,
    onCaptureComplete: (imageUrl: String, filterName: String) -> Unit,
    onNavigateAction: (target: String) -> Unit
) {
    val snaps by viewModel.snaps.collectAsState()
    val selectedLens by viewModel.selectedLens.collectAsState()
    val detectedQr by viewModel.detectedQr.collectAsState()

    var isFrontCamera by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isNightMode by remember { mutableStateOf(false) }
    var showGrid by remember { mutableStateOf(false) }
    var showLenses by remember { mutableStateOf(false) }
    var isQrScannerActive by remember { mutableStateOf(true) }
    var showDemoQrMenu by remember { mutableStateOf(false) }

    val latestSnap = snaps.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Mock Camera Viewfinder Surface (High-Resolution Visual Preview)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isNightMode) Color(0xFF0F172A)
                    else Color(0xFF1E293B)
                )
        ) {
            // Lens Color Tone Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        when (selectedLens.id) {
                            "gold" -> Color(0x22F59E0B)
                            "everest" -> Color(0x2238BDF8)
                            "vintage" -> Color(0x3378350F)
                            "cyber" -> Color(0x22EC4899)
                            "noir" -> Color(0x66000000)
                            else -> Color.Transparent
                        }
                    )
            )

            // Grid Alignment Overlay
            if (showGrid) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.2f)))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.2f)))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.2f)))
                    }
                    Row(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.2f)))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.2f)))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.2f)))
                    }
                    Row(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.2f)))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.2f)))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.White.copy(alpha = 0.2f)))
                    }
                }
            }

            // QR Reticle Frame in Center
            if (isQrScannerActive && detectedQr == null) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text = "Scan QR or Snap",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }

        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            // Mode Pill / Demo QR Menu Trigger
            Box {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable { showDemoQrMenu = !showDemoQrMenu }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(15.dp))
                    Text("Evrry Lens & Scan", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Demo QR Dropdown Menu
                DropdownMenu(
                    expanded = showDemoQrMenu,
                    onDismissRequest = { showDemoQrMenu = false },
                    modifier = Modifier.background(EvrryNavy)
                ) {
                    DropdownMenuItem(
                        text = { Text("☕ Fonepay (Himalayan Java)", color = Color.White, fontSize = 12.sp) },
                        onClick = {
                            viewModel.detectQr("nepalqr://fonepay?merchant=Himalayan+Java+Coffee&amount=350")
                            showDemoQrMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("👤 Evrry Friend (@aarav)", color = Color.White, fontSize = 12.sp) },
                        onClick = {
                            viewModel.detectQr("evrry://user/@aarav")
                            showDemoQrMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🍽️ Table #4 (Roadhouse Cafe)", color = Color.White, fontSize = 12.sp) },
                        onClick = {
                            viewModel.detectQr("evrry://restaurant/roadhouse-cafe?table=4")
                            showDemoQrMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🌐 Website (elifsi.com)", color = Color.White, fontSize = 12.sp) },
                        onClick = {
                            viewModel.detectQr("https://elifsi.com")
                            showDemoQrMenu = false
                        }
                    )
                }
            }

            // Flash Toggle
            IconButton(
                onClick = { isFlashOn = !isFlashOn },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFlashOn) Color(0xFFFBBF24)
                        else Color.Black.copy(alpha = 0.45f)
                    )
            ) {
                Icon(
                    if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = if (isFlashOn) Color.Black else Color.White
                )
            }
        }

        // Right-Side Snapchat-Style Vertical Tool Dock
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { isFrontCamera = !isFrontCamera },
                modifier = Modifier.size(42.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f))
            ) {
                Icon(Icons.Default.Cameraswitch, contentDescription = "Flip", tint = Color.White, modifier = Modifier.size(20.dp))
            }

            IconButton(
                onClick = { isNightMode = !isNightMode },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isNightMode) Color(0xFFFBBF24)
                        else Color.Black.copy(alpha = 0.45f)
                    )
            ) {
                Icon(
                    Icons.Default.NightlightRound,
                    contentDescription = "Night Mode",
                    tint = if (isNightMode) Color.Black else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { showGrid = !showGrid },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (showGrid) Color.White
                        else Color.Black.copy(alpha = 0.45f)
                    )
            ) {
                Icon(
                    Icons.Default.GridOn,
                    contentDescription = "Grid",
                    tint = if (showGrid) Color.Black else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { isQrScannerActive = !isQrScannerActive },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isQrScannerActive) Color(0xFFFBBF24)
                        else Color.Black.copy(alpha = 0.45f)
                    )
            ) {
                Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = "QR Scanner",
                    tint = if (isQrScannerActive) Color.Black else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { showLenses = !showLenses },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (showLenses) Color(0xFF2563EB)
                        else Color.Black.copy(alpha = 0.45f)
                    )
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = "Lenses",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Floating Detected QR Banner Card
        detectedQr?.let { qr ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 110.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.88f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                when (qr.type) {
                                    "fonepay" -> Icons.Default.CreditCard
                                    "contact" -> Icons.Default.Person
                                    "restaurant" -> Icons.Default.Restaurant
                                    else -> Icons.Default.QrCode
                                },
                                contentDescription = null,
                                tint = when (qr.type) {
                                    "fonepay" -> Color(0xFF10B981)
                                    "contact" -> Color(0xFF38BDF8)
                                    "restaurant" -> Color(0xFFFBBF24)
                                    else -> Color.White
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(qr.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(qr.subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = {
                                onNavigateAction(qr.target)
                                viewModel.clearDetectedQr()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(qr.actionLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = { viewModel.clearDetectedQr() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }

        // Bottom Controls Dock
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
                .padding(bottom = 32.dp, top = 20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Horizontal Lens Scroller
                if (showLenses) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.lenses) { lens ->
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (selectedLens.id == lens.id) Color.White
                                        else Color.Black.copy(alpha = 0.5f)
                                    )
                                    .clickable { viewModel.selectLens(lens) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    lens.name,
                                    color = if (selectedLens.id == lens.id) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Memories Shortcut Thumbnail
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onOpenMemories() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (latestSnap != null) {
                                AsyncImage(
                                    model = latestSnap.imageUrl,
                                    contentDescription = "Memories",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Memories", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Large Snapchat-Style Shutter Button
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                            .padding(6.dp)
                            .clickable {
                                // Capture high-resolution photo with active lens
                                val samplePhotoUrl = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800&auto=format&fit=crop&q=80"
                                onCaptureComplete(samplePhotoUrl, selectedLens.name)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }

                    // Lenses Shortcut
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { showLenses = !showLenses }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFBBF24))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Lenses", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
