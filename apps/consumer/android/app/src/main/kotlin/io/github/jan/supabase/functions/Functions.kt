package io.github.jan.supabase.functions

import io.github.jan.supabase.FunctionsPlugin
import io.github.jan.supabase.SupabaseClient

val SupabaseClient.functions: FunctionsPlugin
    get() = FunctionsPlugin(supabaseUrl, supabaseKey)
