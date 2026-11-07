package com.woovi.app.data.repository

import com.woovi.app.data.model.MediaItem
import com.woovi.app.data.model.MediaItemInput
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class MediaRepository(
    private val supabase: SupabaseClient,
    private val covers: CoverRepository
) {

    suspend fun getMyItems(): List<MediaItem> =
        supabase.from(TABLE).select {
            order(column = "created_at", order = Order.DESCENDING)
        }.decodeList<MediaItem>()

    suspend fun getItem(id: String): MediaItem =
        supabase.from(TABLE).select {
            filter { eq("id", id) }
        }.decodeSingle<MediaItem>()

    // Crear: subir portada (si hay) -> guardar fila. Si la fila falla, se borra la portada recién subida.
    suspend fun createItem(input: MediaItemInput, newCover: ByteArray?) {
        val uploadedPath = newCover?.let { covers.upload(it) }
        try {
            supabase.from(TABLE).insert(input.copy(coverPath = uploadedPath))
        } catch (e: Exception) {
            uploadedPath?.let { covers.deleteQuietly(it) }
            throw e
        }
    }

    // Editar. input.coverPath trae la portada que tenía la obra.
    // 1) subir la nueva  2) actualizar la fila  3) SOLO si todo salió bien, borrar la vieja.
    suspend fun editItem(
        id: String,
        input: MediaItemInput,
        newCover: ByteArray?,
        removeCover: Boolean
    ) {
        val oldPath = input.coverPath
        val uploadedPath = newCover?.let { covers.upload(it) }
        val finalPath = when {
            uploadedPath != null -> uploadedPath
            removeCover -> null
            else -> oldPath
        }
        try {
            supabase.from(TABLE).update(input.copy(coverPath = finalPath)) {
                filter { eq("id", id) }
            }
        } catch (e: Exception) {
            uploadedPath?.let { covers.deleteQuietly(it) }
            throw e
        }
        if (oldPath != null && oldPath != finalPath) {
            covers.deleteQuietly(oldPath)
        }
    }

    // Eliminar: primero la fila; la portada se borra solo después de confirmar que la fila ya no existe.
    suspend fun deleteItem(id: String, coverPath: String?) {
        supabase.from(TABLE).delete {
            filter { eq("id", id) }
        }
        coverPath?.let { covers.deleteQuietly(it) }
    }

    private companion object {
        const val TABLE = "media_items"
    }
}