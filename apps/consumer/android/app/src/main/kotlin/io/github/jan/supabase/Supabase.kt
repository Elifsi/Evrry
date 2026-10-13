package io.github.jan.supabase

/**
 * Lightweight Supabase Client Stubs for Evrry Consumer Android
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
            com.elifsi.evrry.consumer.network.SendOtpResponse::class ->
                com.elifsi.evrry.consumer.network.SendOtpResponse(success = true, mock = true) as T
            com.elifsi.evrry.consumer.network.VoiceChatResponse::class ->
                com.elifsi.evrry.consumer.network.VoiceChatResponse(success = true, reply = "Namaste! I am Ellie.", mock = true) as T
            com.elifsi.evrry.consumer.network.SendEmailResponse::class ->
                com.elifsi.evrry.consumer.network.SendEmailResponse(success = true, mock = true) as T
            com.elifsi.evrry.consumer.network.PaymentInitiateResponse::class ->
                com.elifsi.evrry.consumer.network.PaymentInitiateResponse(success = true, provider = "mock") as T
            com.elifsi.evrry.consumer.network.PaymentVerifyResponse::class ->
                com.elifsi.evrry.consumer.network.PaymentVerifyResponse(success = true) as T
            com.elifsi.evrry.consumer.network.RouteResponsePayload::class ->
                com.elifsi.evrry.consumer.network.RouteResponsePayload(
                    ok = true,
                    provider = "osrm",
                    distance_meters = 2500L,
                    duration_seconds = 600L,
                    duration_minutes = 10,
                    polyline = "",
                    steps = emptyList()
                ) as T
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
