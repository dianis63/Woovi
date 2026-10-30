package com.woovi.app.data.repository

import com.woovi.app.data.model.MediaItem
import com.woovi.app.data.model.MediaItemInput
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class MediaRepository(private val supabase: SupabaseClient) {

    suspend fun getMyItems(): List<MediaItem> =
        supabase.from(TABLE).select {
            order(column = "created_at", order = Order.DESCENDING)
        }.decodeList<MediaItem>()

    suspend fun getItem(id: String): MediaItem =
        supabase.from(TABLE).select {
            filter { eq("id", id) }
        }.decodeSingle<MediaItem>()

    suspend fun addItem(item: MediaItemInput) {
        supabase.from(TABLE).insert(item)
    }

    suspend fun updateItem(id: String, item: MediaItemInput) {
        supabase.from(TABLE).update(item) {
            filter { eq("id", id) }
        }
    }

    suspend fun deleteItem(id: String) {
        supabase.from(TABLE).delete {
            filter { eq("id", id) }
        }
    }

    private companion object {
        const val TABLE = "media_items"
    }
}