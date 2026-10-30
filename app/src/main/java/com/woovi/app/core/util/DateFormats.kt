package com.woovi.app.core.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateFormats {
    private val display = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    fun toDisplay(iso: String): String = try {
        LocalDate.parse(iso).format(display)
    } catch (e: Exception) {
        iso
    }
    fun toMillis(iso: String): Long? = try {
        LocalDate.parse(iso).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    } catch (e: Exception) {
        null
    }
    fun fromMillis(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString()
}