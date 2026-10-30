package com.woovi.app.core.error

fun Throwable.toUserMessage(): String {
    val m = message.orEmpty().lowercase()
    return when {
        "invalid login credentials" in m || "invalid_credentials" in m ->
            "Correo o contraseña incorrectos."
        "already registered" in m || "already been registered" in m || "user_already_exists" in m ->
            "Ese correo ya tiene una cuenta. Prueba iniciando sesión."
        "email not confirmed" in m || "email_not_confirmed" in m ->
            "Primero confirma tu correo para poder entrar."
        "rate limit" in m || "too many" in m ->
            "Demasiados intentos. Espera un momento y vuelve a probar."
        "unable to resolve host" in m || "timeout" in m || "failed to connect" in m || "network" in m ->
            "No hay conexión. Revisa tu internet e inténtalo de nuevo."
        "row-level security" in m || "permission denied" in m ->
            "No tienes permiso para hacer esto."
        "check constraint" in m || "violates" in m ->
            "Alguno de los datos no es válido. Revísalos e inténtalo de nuevo."
        else -> "Algo salió mal. Inténtalo de nuevo."
    }
}