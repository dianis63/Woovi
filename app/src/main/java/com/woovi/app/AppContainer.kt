package com.woovi.app

import com.woovi.app.core.config.createWooviSupabaseClient
import com.woovi.app.data.repository.AuthRepository
import com.woovi.app.data.repository.MediaRepository
import io.github.jan.supabase.SupabaseClient

class AppContainer {
    val supabase: SupabaseClient by lazy { createWooviSupabaseClient() }
    val authRepository: AuthRepository by lazy { AuthRepository(supabase) }
    val mediaRepository: MediaRepository by lazy { MediaRepository(supabase) }
}