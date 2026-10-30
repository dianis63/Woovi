package com.woovi.app.core.util


import android.util.Patterns

object Validators {

    fun email(value: String): String? = when {
        value.isBlank() -> "Escribe tu correo"
        !Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches() -> "Ese correo no parece válido"
        else -> null
    }

    fun password(value: String): String? = when {
        value.isBlank() -> "Escribe una contraseña"
        value.length < 8 -> "Usa al menos 8 caracteres"
        else -> null
    }
}