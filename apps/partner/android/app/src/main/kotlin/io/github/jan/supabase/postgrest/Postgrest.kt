package io.github.jan.supabase.postgrest

import io.github.jan.supabase.PostgrestPlugin
import io.github.jan.supabase.PostgrestResult
import io.github.jan.supabase.SupabaseClient

private val defaultPostgrest = PostgrestPlugin()

val SupabaseClient.postgrest: PostgrestPlugin
    get() = defaultPostgrest

suspend fun PostgrestPlugin.rpc(function: String, parameters: Any? = null): PostgrestResult =
    rpc(function, parameters)
