package com.elifsi.evrry.consumer.ui.camera

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.elifsi.evrry.consumer.model.MemoryTab
import com.elifsi.evrry.consumer.model.SnapItem
import com.elifsi.evrry.consumer.ui.theme.EvrryNavy
import com.elifsi.evrry.consumer.viewmodel.CameraViewModel
import com.elifsi.evrry.consumer.viewmodel.ChatViewModel

@Composable
fun MemoriesScreen(
    cameraViewModel: CameraViewModel,
    chatViewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val snaps by cameraViewModel.snaps.collectAsState()
    val cameraRoll by cameraViewModel.cameraRoll.collectAsState()
    val lockerSnaps by cameraViewModel.lockerSnaps.collectAsState()
    val isLockerUnlocked by cameraViewModel.isLockerUnlocked.collectAsState()
    val chats by chatViewModel.chats.collectAsState()

    var activeTab by remember { mutableStateOf(MemoryTab.SNAPS) }
    var selectedSnap by remember { mutableStateOf<SnapItem?>(null) }
    var showSendToFriendSheet by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // PIN Keypad State
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    fun triggerToast(msg: String) {
        toastMessage = msg
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("Memories", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                if (activeTab == MemoryTab.CAMERA_ROLL) {
                    Button(
                        onClick = {
                            val newPhoto = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800&auto=format&fit=crop&q=80"
                            cameraViewModel.addToCameraRoll(newPhoto, "Device Import")
                            triggerToast("Imported photo to Camera Roll")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Import", fontSize = 12.sp, color = Color.White)
                    }
                }

                if (activeTab == MemoryTab.MY_LOCKER && isLockerUnlocked) {
                    Button(
                        onClick = {
                            cameraViewModel.lockLocker()
                            triggerToast("My Locker Locked")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E).copy(alpha = 0.2f)),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFDA4AF), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lock Vault", fontSize = 11.sp, color = Color(0xFFFDA4AF))
                    }
                }
            }

            // Tab Selector Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(4.dp)
            ) {
                // Snaps Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTab == MemoryTab.SNAPS) Color.White else Color.Transparent)
                        .clickable { activeTab = MemoryTab.SNAPS }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Snaps (${snaps.size})",
                        color = if (activeTab == MemoryTab.SNAPS) Color.Black else Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Camera Roll Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTab == MemoryTab.CAMERA_ROLL) Color.White else Color.Transparent)
                        .clickable { activeTab = MemoryTab.CAMERA_ROLL }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Camera Roll (${cameraRoll.size})",
                        color = if (activeTab == MemoryTab.CAMERA_ROLL) Color.Black else Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // My Locker Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTab == MemoryTab.MY_LOCKER) Color.White else Color.Transparent)
                        .clickable { activeTab = MemoryTab.MY_LOCKER }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            if (isLockerUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (activeTab == MemoryTab.MY_LOCKER) Color.Black else (if (isLockerUnlocked) Color(0xFFFBBF24) else Color.White.copy(alpha = 0.6f)),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            "My Locker",
                            color = if (activeTab == MemoryTab.MY_LOCKER) Color.Black else Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Content
            when (activeTab) {
                MemoryTab.SNAPS -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(snaps) { snap ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(9f / 14f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                    .clickable { selectedSnap = snap }
                            ) {
                                AsyncImage(
                                    model = snap.imageUrl,
                                    contentDescription = snap.caption,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.25f))
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                ) {
                                    Text(snap.location, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                    Text(snap.filterName, color = Color.White.copy(alpha = 0.6f), fontSize = 8.sp)
                                }
                            }
                        }
                    }
                }

                MemoryTab.CAMERA_ROLL -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(cameraRoll) { snap ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(9f / 14f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                    .clickable { selectedSnap = snap }
                            ) {
                                AsyncImage(
                                    model = snap.imageUrl,
                                    contentDescription = snap.caption,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                ) {
                                    Text(snap.location, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                }
                            }
                        }
                    }
                }

                MemoryTab.MY_LOCKER -> {
                    if (!isLockerUnlocked) {
                        // Keypad PIN Entry
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFFBBF24).copy(alpha = 0.15f))
                                    .border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(32.dp))
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text("My Locker", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Enter 4-digit passcode (Default: 1234)", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)

                            // 4 PIN Dots
                            Row(
                                modifier = Modifier.padding(vertical = 28.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                (0..3).forEach { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (index < pinInput.length) {
                                                    if (pinError) Color(0xFFF43F5E) else Color.White
                                                } else Color.Transparent
                                            )
                                            .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                    )
                                }
                            }

                            // 3x4 Keypad
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                val keys = listOf(
                                    listOf("1", "2", "3"),
                                    listOf("4", "5", "6"),
                                    listOf("7", "8", "9"),
                                    listOf("", "0", "DEL")
                                )

                                keys.forEach { row ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        row.forEach { digit ->
                                            if (digit.isEmpty()) {
                                                Spacer(modifier = Modifier.size(64.dp))
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .size(64.dp)
                                                        .clip(CircleShape)
                                                        .background(Color.White.copy(alpha = 0.1f))
                                                        .clickable {
                                                            if (digit == "DEL") {
                                                                if (pinInput.isNotEmpty()) pinInput = pinInput.dropLast(1)
                                                                pinError = false
                                                            } else if (pinInput.length < 4) {
                                                                pinInput += digit
                                                                if (pinInput.length == 4) {
                                                                    val ok = cameraViewModel.unlockLocker(pinInput)
                                                                    if (ok) {
                                                                        pinInput = ""
                                                                        triggerToast("My Locker Unlocked")
                                                                    } else {
                                                                        pinError = true
                                                                        pinInput = ""
                                                                    }
                                                                }
                                                            }
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (digit == "DEL") {
                                                        Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                                                    } else {
                                                        Text(digit, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Unlocked Vault View
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(lockerSnaps) { snap ->
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(9f / 14f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                        .clickable { selectedSnap = snap }
                                ) {
                                    AsyncImage(
                                        model = snap.imageUrl,
                                        contentDescription = snap.caption,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .align(Alignment.TopEnd)
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.6f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Fullscreen Photo Detail Dialog
        selectedSnap?.let { snap ->
            Dialog(
                onDismissRequest = { selectedSnap = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.95f))
                        .padding(16.dp)
                ) {
                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { selectedSnap = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(snap.filterName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(snap.location, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.size(40.dp))
                    }

                    // Main Photo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = snap.imageUrl,
                            contentDescription = snap.caption,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (snap.caption.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(snap.caption, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // Bottom Action Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Send Friend
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { showSendToFriendSheet = true }
                        ) {
                            Box(
                                modifier = Modifier.size(42.dp).clip(CircleShape).background(Color(0xFF2563EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Send", color = Color.White, fontSize = 10.sp)
                        }

                        // Post to 24-hr Story / Status
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                chatViewModel.postStory(snap.imageUrl, snap.caption ?: "")
                                selectedSnap = null
                                triggerToast("Posted to your Story 📖")
                            }
                        ) {
                            Box(
                                modifier = Modifier.size(42.dp).clip(CircleShape).background(Color(0xFF0284C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoStories, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Story", color = Color.White, fontSize = 10.sp)
                        }

                        // Add to Locker / Unhide
                        if (activeTab == MemoryTab.MY_LOCKER) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    cameraViewModel.restoreFromLocker(snap.id)
                                    selectedSnap = null
                                    triggerToast("Restored to Snaps")
                                }
                            ) {
                                Box(
                                    modifier = Modifier.size(42.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Unhide", color = Color.White, fontSize = 10.sp)
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    cameraViewModel.moveToLocker(snap.id, activeTab)
                                    selectedSnap = null
                                    triggerToast("Moved to My Locker 🔒")
                                }
                            ) {
                                Box(
                                    modifier = Modifier.size(42.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("To Locker", color = Color.White, fontSize = 10.sp)
                            }
                        }

                        // Delete
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                if (activeTab == MemoryTab.SNAPS) cameraViewModel.deleteSnap(snap.id)
                                else if (activeTab == MemoryTab.CAMERA_ROLL) cameraViewModel.deleteCameraRoll(snap.id)
                                else cameraViewModel.deleteLockerSnap(snap.id)
                                selectedSnap = null
                                triggerToast("Deleted")
                            }
                        ) {
                            Box(
                                modifier = Modifier.size(42.dp).clip(CircleShape).background(Color(0xFFF43F5E).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFDA4AF), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Delete", color = Color(0xFFFDA4AF), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Send to Friend Dialog Sheet
        if (showSendToFriendSheet && selectedSnap != null) {
            Dialog(onDismissRequest = { showSendToFriendSheet = false }) {
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = EvrryNavy)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Send to Friend", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { showSendToFriendSheet = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        Text("Share via E2EE encrypted social chat", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(chats) { chat ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .clickable {
                                            // Send photo snap into active chat
                                            chatViewModel.sendImage(chat.id, selectedSnap!!.imageUrl)
                                            showSendToFriendSheet = false
                                            triggerToast("Sent to ${chat.title}!")
                                        }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.size(38.dp).clip(CircleShape).background(Color(0xFF2563EB)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(chat.title.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                        Column {
                                            Text(chat.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                            Text(if (chat.isOnline) "Online" else "Last seen recently", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                        }
                                    }

                                    Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
