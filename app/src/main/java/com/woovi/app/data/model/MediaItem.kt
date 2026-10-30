package com.woovi.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class MediaItem(
    val id: String,
    @SerialName("user_id") val userId: String,
    val title: String,
    @SerialName("media_type") val mediaType: MediaType,
    val description: String? = null,
    val genres: List<String> = emptyList(),
    val status: MediaStatus,
    val rating: Int? = null,
    @SerialName("cover_path") val coverPath: String? = null,
    val notes: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("finish_date") val finishDate: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class MediaItemInput(
    val title: String,
    @SerialName("media_type") val mediaType: MediaType,
    val status: MediaStatus,
    val description: String?,
    val genres: List<String>,
    val rating: Int?,
    val notes: String?,
    @SerialName("start_date") val startDate: String?,
    @SerialName("finish_date") val finishDate: String?
)