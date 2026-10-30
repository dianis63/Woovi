package com.woovi.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val WooviColorScheme = lightColorScheme(
    primary = FucsiaOscuro,
    onPrimary = Blanco,
    primaryContainer = RosaPastel,
    onPrimaryContainer = Marron,
    secondaryContainer = Lavanda,
    onSecondaryContainer = Marron,
    background = Crema,
    onBackground = Marron,
    surface = Blanco,
    onSurface = Marron
)

private val WooviShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun WooviTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WooviColorScheme,
        typography = WooviTypography,
        shapes = WooviShapes,
        content = content
    )
}