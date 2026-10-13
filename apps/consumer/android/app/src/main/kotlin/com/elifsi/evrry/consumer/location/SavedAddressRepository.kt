package com.elifsi.evrry.consumer.location

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Saved Places & User Address Repository
 * Platform: Evrry Super App (Consumer Android)
 * Elifsi Technologies Private Limited
 *
 * Persists and manages user addresses (Home, Office, Custom).
 */
@Singleton
class SavedAddressRepository @Inject constructor() {

    private val _addresses = MutableStateFlow<List<SavedAddress>>(
        listOf(
            SavedAddress(
                id = "addr_home",
                label = "Home",
                address = "Sanepa Heights, Lalitpur",
                latitude = 27.6841,
                longitude = 85.3088,
                type = AddressType.HOME,
                isDefault = true
            ),
            SavedAddress(
                id = "addr_work",
                label = "Office",
                address = "Durbar Marg, Kathmandu",
                latitude = 27.7118,
                longitude = 85.3168,
                type = AddressType.WORK,
                isDefault = false
            ),
            SavedAddress(
                id = "addr_gym",
                label = "Fitness Club",
                address = "Jhamsikhel, Lalitpur",
                latitude = 27.6830,
                longitude = 85.3120,
                type = AddressType.OTHER,
                isDefault = false
            )
        )
    )
    val addresses: StateFlow<List<SavedAddress>> = _addresses.asStateFlow()

    fun getHome(): SavedAddress? = _addresses.value.find { it.type == AddressType.HOME }
    fun getWork(): SavedAddress? = _addresses.value.find { it.type == AddressType.WORK }

    fun addAddress(address: SavedAddress) {
        val current = _addresses.value.toMutableList()
        current.add(address)
        _addresses.value = current
    }

    fun removeAddress(id: String) {
        _addresses.value = _addresses.value.filter { it.id != id }
    }

    fun setDefault(id: String) {
        _addresses.value = _addresses.value.map {
            it.copy(isDefault = (it.id == id))
        }
    }
}
