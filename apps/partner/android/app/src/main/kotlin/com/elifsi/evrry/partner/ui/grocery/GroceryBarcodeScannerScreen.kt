package com.elifsi.evrry.partner.ui.grocery

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.elifsi.evrry.partner.model.GroceryPartnerItem
import com.elifsi.evrry.partner.ui.theme.*
import com.elifsi.evrry.partner.viewmodel.GrocerySupermarketViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

/**
 * Barcode Scanner & Shelf SKU Audit Screen
 * CameraX + Google ML Kit Barcode Scanning with explicit hardware release on screen exit.
 * Pointing camera at product barcode displays shelf price, stock count, and rapid stock/price controls.
 * Elifsi Technologies Private Limited
 */
@Composable
fun GroceryBarcodeScannerScreen(
    viewModel: GrocerySupermarketViewModel,
    onBack: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val scannedBarcode by viewModel.scannedBarcode.collectAsState()
    val scannedItem by viewModel.scannedItem.collectAsState()

    var manualCodeInput by remember { mutableStateOf("") }
    var cameraProviderRef by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var cameraHasError by remember { mutableStateOf(false) }

    // Explicit hardware cleanup when screen is disposed
    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                cameraProviderRef?.unbindAll()
            } catch (_: Exception) {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // CameraX Live Preview
        if (!cameraHasError) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraExecutor = Executors.newSingleThreadExecutor()
                    val barcodeScanner = BarcodeScanning.getClient()

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            cameraProviderRef = cameraProvider

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                @OptIn(ExperimentalGetImage::class)
                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    val inputImage = InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees
                                    )
                                    barcodeScanner.process(inputImage)
                                        .addOnSuccessListener { barcodes ->
                                            for (barcode in barcodes) {
                                                val raw = barcode.rawValue
                                                if (!raw.isNullOrBlank()) {
                                                    viewModel.onBarcodeScanned(raw)
                                                    break
                                                }
                                            }
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                } else {
                                    imageProxy.close()
                                }
                            }

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            cameraHasError = true
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Camera preview unavailable.\nUse manual SKU/Barcode entry below.",
                    color = EvrryWhite,
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Viewfinder Reticle Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 140.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(260.dp, 160.dp)
                    .border(2.dp, EvrryPrimaryBlue, RoundedCornerShape(16.dp))
                    .background(Color.Transparent)
            ) {
                Text(
                    text = "Align Barcode / SKU inside frame",
                    fontSize = 11.sp,
                    color = EvrryWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                )
            }
        }

        // Top Header with Back button and torch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = EvrryWhite)
            }

            Text(
                text = "Shelf Barcode Scanner",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = EvrryWhite
            )

            IconButton(
                onClick = { viewModel.clearScannedBarcode() },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = EvrryWhite)
            }
        }

        // Bottom Shelf Audit Sheet
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Manual SKU lookup bar
            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = manualCodeInput,
                        onValueChange = { manualCodeInput = it },
                        placeholder = { Text("Or enter Barcode/SKU...", fontSize = 12.sp, color = Color.LightGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = EvrryWhite,
                            unfocusedTextColor = EvrryWhite,
                            focusedBorderColor = EvrryPrimaryBlue,
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            if (manualCodeInput.isNotBlank()) {
                                viewModel.onBarcodeScanned(manualCodeInput.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EvrryPrimaryBlue)
                    ) {
                        Text("Audit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Scanned Product Details Card
            if (scannedItem != null) {
                val item = scannedItem!!
                Surface(
                    color = EvrryWhite,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = EvrryEmeraldBg,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = item.section.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EvrryEmerald,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = item.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EvrryNavy,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "Barcode: ${item.barcode ?: "N/A"} • SKU: ${item.sku ?: "N/A"}",
                                    fontSize = 11.sp,
                                    color = EvrryMuted
                                )
                            }

                            // Price Tag
                            Column(horizontalAlignment = Alignment.End) {
                                Text("SHELF PRICE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EvrryMuted)
                                Text(
                                    text = "NPR ${String.format("%.2f", item.priceNpr)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EvrryPrimaryBlue
                                )
                            }
                        }

                        HorizontalDivider(color = EvrryBorder)

                        // Rapid Stock Controls Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("CURRENT STOCK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EvrryMuted)
                                Text(
                                    text = "${item.stockQuantity} ${item.unit}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (item.stockQuantity > 5) EvrryDarkCharcoal else EvrryAmberWarning
                                )
                            }

                            // + / - Stock Counter
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalIconButton(
                                    onClick = { viewModel.adjustStock(item.id, -1) }
                                ) {
                                    Text("-", fontSize = 18.sp, fontWeight = FontWeight.Black)
                                }

                                Text(
                                    text = "${item.stockQuantity}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = EvrryNavy
                                )

                                FilledTonalIconButton(
                                    onClick = { viewModel.adjustStock(item.id, 1) }
                                ) {
                                    Text("+", fontSize = 18.sp, fontWeight = FontWeight.Black)
                                }
                            }

                            // Rapid Availability Toggle (86 Item)
                            Button(
                                onClick = { viewModel.toggleItemAvailability(item.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (item.isAvailable) EvrryRedDestructive else EvrryEmerald
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (item.isAvailable) "86 Out of Stock" else "Mark In Stock",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else if (scannedBarcode != null) {
                Surface(
                    color = EvrryWhite,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Barcode \"$scannedBarcode\" not found in store catalog",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EvrryRedDestructive
                        )
                        Text(
                            text = "Point camera at valid store item or check SKU spelling.",
                            fontSize = 11.sp,
                            color = EvrryMuted
                        )
                    }
                }
            }
        }
    }
}
