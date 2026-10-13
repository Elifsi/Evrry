package com.elifsi.evrry.consumer.viewmodel

import androidx.lifecycle.ViewModel
import com.elifsi.evrry.consumer.model.CameraFilterLens
import com.elifsi.evrry.consumer.model.MemoryTab
import com.elifsi.evrry.consumer.model.QrParser
import com.elifsi.evrry.consumer.model.QrScanResult
import com.elifsi.evrry.consumer.model.SnapItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class CameraViewModel : ViewModel() {

    private val _snaps = MutableStateFlow<List<SnapItem>>(
        listOf(
            SnapItem(
                id = "snap-patan",
                imageUrl = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800&auto=format&fit=crop&q=80",
                filterName = "Kathmandu Gold",
                location = "Patan Durbar Square, Lalitpur",
                caption = "Golden sunset at Patan Krishna Mandir ✨",
                createdAt = System.currentTimeMillis() - 3600000 * 4
            ),
            SnapItem(
                id = "snap-everest",
                imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop&q=80",
                filterName = "Everest Ice",
                location = "Nagarkot Sunrise Point",
                caption = "Morning glow over the snow peaks 🏔️",
                createdAt = System.currentTimeMillis() - 3600000 * 24
            ),
            SnapItem(
                id = "snap-thamel",
                imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&auto=format&fit=crop&q=80",
                filterName = "Cyberpunk Neon",
                location = "Thamel Night Market",
                caption = "Live music and aromatic coffee ☕🎶",
                createdAt = System.currentTimeMillis() - 3600000 * 48
            )
        )
    )
    val snaps: StateFlow<List<SnapItem>> = _snaps.asStateFlow()

    private val _cameraRoll = MutableStateFlow<List<SnapItem>>(
        listOf(
            SnapItem(
                id = "roll-pokhara",
                imageUrl = "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=800&auto=format&fit=crop&q=80",
                filterName = "Camera Roll",
                location = "Phewa Lake, Pokhara",
                caption = "Boating on peaceful Phewa waters ⛵",
                createdAt = System.currentTimeMillis() - 3600000 * 72
            ),
            SnapItem(
                id = "roll-momo",
                imageUrl = "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=800&auto=format&fit=crop&q=80",
                filterName = "Camera Roll",
                location = "Jhamsikhel, Lalitpur",
                caption = "Hot steamed buff momo with spicy timur jhol 🥟🔥",
                createdAt = System.currentTimeMillis() - 3600000 * 96
            )
        )
    )
    val cameraRoll: StateFlow<List<SnapItem>> = _cameraRoll.asStateFlow()

    private val _lockerSnaps = MutableStateFlow<List<SnapItem>>(
        listOf(
            SnapItem(
                id = "locker-itinerary",
                imageUrl = "https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=800&auto=format&fit=crop&q=80",
                filterName = "Private Vault",
                location = "Personal Vault",
                caption = "Private travel receipts & flight passes 🔒",
                isLocked = true,
                createdAt = System.currentTimeMillis() - 3600000 * 120
            )
        )
    )
    val lockerSnaps: StateFlow<List<SnapItem>> = _lockerSnaps.asStateFlow()

    private val _lockerPin = MutableStateFlow("1234") // Default 4-digit PIN
    val lockerPin: StateFlow<String> = _lockerPin.asStateFlow()

    private val _isLockerUnlocked = MutableStateFlow(false)
    val isLockerUnlocked: StateFlow<Boolean> = _isLockerUnlocked.asStateFlow()

    private val _detectedQr = MutableStateFlow<QrScanResult?>(null)
    val detectedQr: StateFlow<QrScanResult?> = _detectedQr.asStateFlow()

    val lenses = listOf(
        CameraFilterLens("normal", "Normal", "Natural real colors"),
        CameraFilterLens("gold", "Kathmandu Gold", "Warm heritage sunshine"),
        CameraFilterLens("everest", "Everest Ice", "Cool mountain contrast"),
        CameraFilterLens("vintage", "Vintage 90s", "Warm nostalgic retro"),
        CameraFilterLens("cyber", "Cyberpunk Neon", "High saturation vibrant"),
        CameraFilterLens("noir", "Noir B&W", "Dramatic black and white")
    )

    private val _selectedLens = MutableStateFlow(lenses[0])
    val selectedLens: StateFlow<CameraFilterLens> = _selectedLens.asStateFlow()

    fun selectLens(lens: CameraFilterLens) {
        _selectedLens.value = lens
    }

    fun addSnap(imageUrl: String, filterName: String, location: String = "Kathmandu, Nepal", caption: String = ""): SnapItem {
        val snap = SnapItem(
            id = "snap-" + UUID.randomUUID().toString().take(8),
            imageUrl = imageUrl,
            filterName = filterName,
            location = location,
            caption = caption,
            createdAt = System.currentTimeMillis()
        )
        _snaps.value = listOf(snap) + _snaps.value
        return snap
    }

    fun deleteSnap(id: String) {
        _snaps.value = _snaps.value.filter { it.id != id }
    }

    fun addToCameraRoll(imageUrl: String, caption: String = ""): SnapItem {
        val rollItem = SnapItem(
            id = "roll-" + UUID.randomUUID().toString().take(8),
            imageUrl = imageUrl,
            filterName = "Camera Roll",
            location = "Device Storage",
            caption = caption,
            createdAt = System.currentTimeMillis()
        )
        _cameraRoll.value = listOf(rollItem) + _cameraRoll.value
        return rollItem
    }

    fun deleteCameraRoll(id: String) {
        _cameraRoll.value = _cameraRoll.value.filter { it.id != id }
    }

    fun setLockerPin(newPin: String) {
        _lockerPin.value = newPin.trim()
    }

    fun unlockLocker(pin: String): Boolean {
        if (pin.trim() == _lockerPin.value) {
            _isLockerUnlocked.value = true
            return true
        }
        return false
    }

    fun lockLocker() {
        _isLockerUnlocked.value = false
    }

    fun moveToLocker(id: String, fromTab: MemoryTab) {
        if (fromTab == MemoryTab.SNAPS) {
            val item = _snaps.value.find { it.id == id }
            if (item != null) {
                _snaps.value = _snaps.value.filter { it.id != id }
                _lockerSnaps.value = listOf(item.copy(isLocked = true)) + _lockerSnaps.value
            }
        } else {
            val item = _cameraRoll.value.find { it.id == id }
            if (item != null) {
                _cameraRoll.value = _cameraRoll.value.filter { it.id != id }
                _lockerSnaps.value = listOf(item.copy(isLocked = true)) + _lockerSnaps.value
            }
        }
    }

    fun restoreFromLocker(id: String) {
        val item = _lockerSnaps.value.find { it.id == id }
        if (item != null) {
            _lockerSnaps.value = _lockerSnaps.value.filter { it.id != id }
            _snaps.value = listOf(item.copy(isLocked = false)) + _snaps.value
        }
    }

    fun deleteLockerSnap(id: String) {
        _lockerSnaps.value = _lockerSnaps.value.filter { it.id != id }
    }

    fun detectQr(raw: String) {
        _detectedQr.value = QrParser.parse(raw)
    }

    fun clearDetectedQr() {
        _detectedQr.value = null
    }
}
