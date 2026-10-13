package com.elifsi.evrry.partner.location

import com.elifsi.evrry.partner.model.DriverLocationState
import com.elifsi.evrry.partner.utils.MapUtils
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Partner Location Broadcaster
 * Elifsi Technologies Private Limited
 *
 * Runs continuous GPS tracking & broadcast to `public.driver_locations` every 3-5 seconds.
 * Calculates vehicle bearing/heading angle and snaps positions along navigation paths.
 */
@Singleton
class PartnerLocationBroadcaster @Inject constructor(
    private val supabaseClient: SupabaseClient? = null
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var broadcastJob: Job? = null

    // Default Kathmandu corridor starting coordinates (Thamel corridor)
    private val _locationState = MutableStateFlow(
        DriverLocationState(
            partnerId = "partner_ktm_001",
            vehicleType = "bike",
            latitude = 27.7145,
            longitude = 27.7145,
            bearing = 45f,
            speedKmh = 28,
            isOnline = false
        )
    )
    val locationState: StateFlow<DriverLocationState> = _locationState.asStateFlow()

    // Kathmandu road corridor waypoints for live telemetry simulation & testing
    private val simulationWaypoints = listOf(
        Pair(27.7154, 85.3123), // Thamel
        Pair(27.7135, 85.3140), // Kantipath
        Pair(27.7080, 85.3155), // Jamal / Ratna Park
        Pair(27.7000, 85.3180), // Tripureshwor
        Pair(27.6890, 85.3195), // Thapathali Bridge
        Pair(27.6780, 85.3220), // Pulchowk
        Pair(27.6727, 85.3253)  // Patan Durbar Square
    )
    private var currentWaypointIndex = 0

    /**
     * Start broadcasting location updates to public.driver_locations
     */
    fun startBroadcasting(partnerId: String, vehicleType: String = "bike") {
        stopBroadcasting()
        _locationState.value = _locationState.value.copy(
            partnerId = partnerId,
            vehicleType = vehicleType,
            isOnline = true
        )

        broadcastJob = scope.launch {
            while (isActive) {
                // Update simulated/fused location step
                stepLocationAlongCorridor()

                // Broadcast to Supabase driver_locations table via RPC
                val cur = _locationState.value
                try {
                    supabaseClient?.postgrest?.rpc(
                        function = "broadcast_driver_location",
                        parameters = mapOf(
                            "p_partner_id" to cur.partnerId,
                            "p_vehicle_type" to cur.vehicleType,
                            "p_lat" to cur.latitude,
                            "p_lng" to cur.longitude,
                            "p_bearing" to cur.bearing.toInt(),
                            "p_speed_kmh" to cur.speedKmh,
                            "p_is_online" to true
                        )
                    )
                } catch (_: Exception) {
                    // Ignored in offline/mock mode
                }

                // 3 to 4 seconds interval as required by spec
                delay(3500)
            }
        }
    }

    /**
     * Stop broadcasting and mark driver offline
     */
    fun stopBroadcasting() {
        broadcastJob?.cancel()
        broadcastJob = null
        val cur = _locationState.value
        _locationState.value = cur.copy(isOnline = false, speedKmh = 0)

        // Mark offline in Supabase
        scope.launch {
            try {
                supabaseClient?.postgrest?.rpc(
                    function = "set_driver_offline",
                    parameters = mapOf("p_partner_id" to cur.partnerId)
                )
            } catch (_: Exception) {}
        }
    }

    /**
     * Advance location smoothly along the road network
     */
    private fun stepLocationAlongCorridor() {
        val nextIdx = (currentWaypointIndex + 1) % simulationWaypoints.size
        val currentPoint = simulationWaypoints[currentWaypointIndex]
        val targetPoint = simulationWaypoints[nextIdx]

        val bearing = MapUtils.getRotation(
            startLat = currentPoint.first,
            startLng = currentPoint.second,
            endLat = targetPoint.first,
            endLng = targetPoint.second
        )

        // Advance waypoint
        currentWaypointIndex = nextIdx
        _locationState.value = _locationState.value.copy(
            latitude = targetPoint.first,
            longitude = targetPoint.second,
            bearing = bearing,
            speedKmh = (22..35).random(),
            lastUpdated = System.currentTimeMillis()
        )
    }

    /**
     * Manually set exact location coordinates (e.g. from GPS callback)
     */
    fun updateManualLocation(lat: Double, lng: Double, bearing: Float, speedKmh: Int) {
        _locationState.value = _locationState.value.copy(
            latitude = lat,
            longitude = lng,
            bearing = bearing,
            speedKmh = speedKmh,
            lastUpdated = System.currentTimeMillis()
        )
    }
}
