package io.github.jan.supabase.functions

import io.github.jan.supabase.FunctionsPlugin
import io.github.jan.supabase.SupabaseClient

private val defaultFunctions = FunctionsPlugin()

val SupabaseClient.functions: FunctionsPlugin
    get() = defaultFunctions
