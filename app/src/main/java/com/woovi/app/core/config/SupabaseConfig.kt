package com.woovi.app.core.config

import com.woovi.app.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
fun createWooviSupabaseClient() = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_KEY
) {
    // explicitNulls = true: si pones un campo en null, SÍ se envía y la base lo limpia.
    defaultSerializer = KotlinXSerializer(Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = true
    })
    install(Auth)
    install(Postgrest)
    install(Storage)
}