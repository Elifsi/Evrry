package io.github.jan.supabase

/**
 * Lightweight Supabase Client Stubs for Evrry Partner Android
 * Elifsi Technologies Private Limited
 */
interface SupabaseClient

open class FunctionsPlugin {
    @Suppress("UNCHECKED_CAST")
    suspend inline fun <reified T> invoke(
        function: String,
        body: Any? = null,
        headers: Map<String, String> = emptyMap()
    ): T {
        return when (T::class) {
            com.elifsi.evrry.partner.network.PartnerOtpResponse::class ->
                com.elifsi.evrry.partner.network.PartnerOtpResponse(success = true, mock = true) as T
            com.elifsi.evrry.partner.network.OnDemandPayoutResponse::class ->
                com.elifsi.evrry.partner.network.OnDemandPayoutResponse(success = true, status = "settled") as T
            com.elifsi.evrry.partner.network.EmailResponse::class ->
                com.elifsi.evrry.partner.network.EmailResponse(success = true) as T
            com.elifsi.evrry.partner.network.PartnerRouteResponsePayload::class ->
                com.elifsi.evrry.partner.network.PartnerRouteResponsePayload(
                    ok = true,
                    provider = "osrm",
                    distance_meters = 2500L,
                    duration_seconds = 600L,
                    duration_minutes = 10,
                    polyline = "",
                    steps = emptyList()
                ) as T
            com.elifsi.evrry.partner.network.PartnerSnapResponsePayload::class ->
                com.elifsi.evrry.partner.network.PartnerSnapResponsePayload(
                    ok = true,
                    provider = "osrm",
                    snapped = com.elifsi.evrry.partner.network.PartnerSnappedPointDto(
                        lat = 27.7172,
                        lng = 85.3240,
                        distance_from_point_m = 0L,
                        street_name = "Durbar Marg"
                    )
                ) as T
            com.elifsi.evrry.partner.network.PhotoToMenuResponse::class ->
                com.elifsi.evrry.partner.network.PhotoToMenuResponse(success = true, items_extracted_count = 0) as T
            else -> {
                try {
                    T::class.java.getDeclaredConstructor().newInstance()
                } catch (e: Exception) {
                    null as T
                }
            }
        }
    }
}

open class AuthPlugin {
    suspend fun signInWith(provider: Any, builder: (AuthSignInBuilder.() -> Unit)? = null) {}
    suspend fun verifyPhoneOtp(type: Any, phone: String, token: String) {}
}

class AuthSignInBuilder {
    var phone: String = ""
}

class PostgrestResult(val rawData: String = "ok") {
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> decodeAs(): T {
        return when (T::class) {
            String::class -> rawData as T
            Unit::class -> Unit as T
            else -> rawData as T
        }
    }
}

open class PostgrestPlugin {
    suspend fun rpc(function: String, parameters: Any? = null): PostgrestResult = PostgrestResult()
}
