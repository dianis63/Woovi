package com.woovi.app.data.repository

import android.util.Log
import com.woovi.app.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class CoverRepository(private val supabase: SupabaseClient) {

    private val bucket get() = supabase.storage.from(BUCKET)

    private class CachedUrl(val url: String, val expiresAtMillis: Long)
    private val urlCache = ConcurrentHashMap<String, CachedUrl>()

    suspend fun upload(bytes: ByteArray): String {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("No hay sesión iniciada")
        val path = "$userId/${UUID.randomUUID()}.jpg"
        bucket.upload(path, bytes) { upsert = false }
        return path
    }

    suspend fun signedUrl(path: String): String {
        val now = System.currentTimeMillis()
        urlCache[path]?.takeIf { it.expiresAtMillis > now }?.let { return it.url }

        val raw = bucket.createSignedUrl(path, 1.hours)
        val url = if (raw.startsWith("http")) raw
        else "${BuildConfig.SUPABASE_URL}/storage/v1${if (raw.startsWith("/")) "" else "/"}$raw"

        urlCache[path] = CachedUrl(url, now + 50.minutes.inWholeMilliseconds)
        return url
    }

    suspend fun deleteQuietly(path: String) {
        withContext(NonCancellable) {
            try {
                bucket.delete(path)
                urlCache.remove(path)
            } catch (e: Exception) {
                Log.w("Woovi", "No se pudo borrar la portada $path", e)
            }
        }
    }

    private companion object {
        const val BUCKET = "covers"
    }
}