package com.woovi.app.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MediaType(val label: String) {
    @SerialName("manga") MANGA("Manga"),
    @SerialName("manhwa") MANHWA("Manhwa"),
    @SerialName("pelicula") PELICULA("Película"),
    @SerialName("serie") SERIE("Serie"),
    @SerialName("anime") ANIME("Anime")
}

@Serializable
enum class MediaStatus(val label: String) {
    @SerialName("pendiente") PENDIENTE("Pendiente"),
    @SerialName("en_progreso") EN_PROGRESO("En curso"),
    @SerialName("completado") COMPLETADO("Terminado"),
    @SerialName("abandonado") ABANDONADO("Abandonado")
}