package com.woovi.app

import android.content.Context
import com.woovi.app.core.config.createWooviSupabaseClient
import com.woovi.app.data.repository.AuthRepository
import com.woovi.app.data.repository.CoverRepository
import com.woovi.app.data.repository.MediaRepository
import io.github.jan.supabase.SupabaseClient

class AppContainer(val appContext: Context) {
    val supabase: SupabaseClient by lazy { createWooviSupabaseClient() }
    val authRepository: AuthRepository by lazy { AuthRepository(supabase) }
    val coverRepository: CoverRepository by lazy { CoverRepository(supabase) }
    val mediaRepository: MediaRepository by lazy { MediaRepository(supabase, coverRepository) }
}