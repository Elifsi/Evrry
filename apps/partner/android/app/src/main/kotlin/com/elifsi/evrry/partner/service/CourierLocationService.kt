package com.elifsi.evrry.partner.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.elifsi.evrry.partner.MainActivity
import com.elifsi.evrry.partner.location.PartnerLocationBroadcaster
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Android Persistent Foreground Location Service for Food & Grocery Delivery Courier
 * Modeled after kaaneneskpc/Deliverr (data/location, BaseDeliverrActivity)
 *
 * Keeps hardware GPS tracking active even when app is backgrounded or screen is turned off.
 * Streams real-time rider coordinates to public.driver_locations table.
 *
 * Elifsi Technologies Private Limited
 */
class CourierLocationService : LifecycleService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null
    private val locationBroadcaster = PartnerLocationBroadcaster()

    companion object {
        const val CHANNEL_ID = "evrry_courier_gps_channel"
        const val NOTIFICATION_ID = 2081
        const val ACTION_START = "ACTION_START_COURIER_GPS"
        const val ACTION_STOP = "ACTION_STOP_COURIER_GPS"
        const val EXTRA_PARTNER_ID = "EXTRA_PARTNER_ID"
        const val EXTRA_VEHICLE_TYPE = "EXTRA_VEHICLE_TYPE"

        fun startService(context: Context, partnerId: String = "rider-kathmandu-01", vehicleType: String = "scooter") {
            val intent = Intent(context, CourierLocationService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_PARTNER_ID, partnerId)
                putExtra(EXTRA_VEHICLE_TYPE, vehicleType)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, CourierLocationService::class.java).apply {
                action = ACTION_STOP
            }
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_START -> {
                val partnerId = intent.getStringExtra(EXTRA_PARTNER_ID) ?: "rider-kathmandu-01"
                val vehicleType = intent.getStringExtra(EXTRA_VEHICLE_TYPE) ?: "scooter"
                startForegroundTracking(partnerId, vehicleType)
            }
            ACTION_STOP -> {
                stopForegroundTracking()
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startForegroundTracking(partnerId: String, vehicleType: String) {
        val notification = buildPersistentNotification("Streaming live GPS coordinates to Kathmandu hub")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        locationBroadcaster.startBroadcasting(partnerId, vehicleType)

        // Continuous heartbeat logging for verification
        serviceScope.launch {
            while (isActive) {
                val state = locationBroadcaster.locationState.value
                val updatedNotification = buildPersistentNotification(
                    "Speed: ${state.speedKmh} km/h · Bearing: ${state.bearing.toInt()}° · GPS Active"
                )
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, updatedNotification)
                delay(4000)
            }
        }
    }

    private fun stopForegroundTracking() {
        locationBroadcaster.stopBroadcasting()
        releaseWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "evrry:courier_location_wakelock"
        ).apply {
            setReferenceCounted(false)
            acquire(6 * 60 * 60 * 1000L) // 6 hours safety max
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Evrry Courier Live Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps courier GPS active for real-time dispatch and consumer order tracking"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildPersistentNotification(contentText: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🛵 Evrry Courier Online")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        locationBroadcaster.stopBroadcasting()
        releaseWakeLock()
    }
}
