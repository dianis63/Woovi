package com.woovi.app.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow

class AuthRepository(private val supabase: SupabaseClient) {

    val sessionStatus: StateFlow<SessionStatus> = supabase.auth.sessionStatus

    suspend fun signUp(email: String, password: String): Boolean {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        return supabase.auth.currentSessionOrNull() != null
    }

    suspend fun signIn(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    fun currentEmail(): String? = supabase.auth.currentUserOrNull()?.email
}