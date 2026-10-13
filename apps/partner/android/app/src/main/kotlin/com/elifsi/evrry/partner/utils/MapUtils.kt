package com.elifsi.evrry.partner.utils

import kotlin.math.*

/**
 * Map, Bearing, and Polyline Utility Functions
 * Grounded in reference/amitshekhariitbhu_ridesharing-uber-lyft-app/utils/MapUtils.kt
 * Platform: Evrry Super App (Partner Android)
 * Elifsi Technologies Private Limited
 */
object MapUtils {

    /**
     * Calculates the rotation bearing angle (0 to 360 degrees) between two coordinates
     * for realistic vehicle orientation along Kathmandu streets.
     */
    fun getRotation(startLat: Double, startLng: Double, endLat: Double, endLng: Double): Float {
        val latDifference = abs(startLat - endLat)
        val lngDifference = abs(startLng - endLng)
        if (latDifference < 0.000001 && lngDifference < 0.000001) return 0f

        var rotation = 0f
        when {
            startLat < endLat && startLng < endLng -> {
                rotation = Math.toDegrees(atan(lngDifference / latDifference)).toFloat()
            }
            startLat >= endLat && startLng < endLng -> {
                rotation = (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90).toFloat()
            }
            startLat >= endLat && startLng >= endLng -> {
                rotation = (Math.toDegrees(atan(lngDifference / latDifference)) + 180).toFloat()
            }
            startLat < endLat && startLng >= endLng -> {
                rotation = (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270).toFloat()
            }
        }
        return (rotation % 360f + 360f) % 360f
    }

    /**
     * Calculate spherical distance in meters between two lat/lng coordinates (Haversine formula).
     */
    fun computeDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    /**
     * Decodes an encoded path string into a list of LatLng pairs.
     * Compatible with OSRM and Google Maps polyline format.
     */
    fun decodePolyline(encoded: String): List<Pair<Double, Double>> {
        val poly = ArrayList<Pair<Double, Double>>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                if (index >= len) break
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat

            shift = 0
            result = 0
            do {
                if (index >= len) break
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lng += dlng

            val pLat = lat.toDouble() / 1E5
            val pLng = lng.toDouble() / 1E5
            poly.add(Pair(pLat, pLng))
        }
        return poly
    }
}
