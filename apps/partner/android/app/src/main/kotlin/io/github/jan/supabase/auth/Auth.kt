package io.github.jan.supabase.auth

import io.github.jan.supabase.AuthPlugin
import io.github.jan.supabase.SupabaseClient

private val defaultAuth = AuthPlugin()

val SupabaseClient.auth: AuthPlugin
    get() = defaultAuth

object OtpType {
    object Phone {
        val SMS = "sms"
    }
}
