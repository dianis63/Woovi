package com.woovi.app.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.woovi.app.WooviApp
import kotlinx.coroutines.CancellationException

@Composable
fun CoverImage(
    coverPath: String?,
    title: String,
    placeholderColor: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coverRepository = remember {
        (context.applicationContext as WooviApp).container.coverRepository
    }

    var url by remember(coverPath) { mutableStateOf<String?>(null) }
    LaunchedEffect(coverPath) {
        url = if (coverPath == null) null else try {
            coverRepository.signedUrl(coverPath)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e("Woovi", "No se pudo firmar la portada", e)
            null
        }
    }

    Box(
        modifier = modifier.background(placeholderColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title.take(1).uppercase(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        val currentUrl = url
        if (currentUrl != null && coverPath != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(currentUrl)
                    .memoryCacheKey(coverPath)
                    .diskCacheKey(coverPath)
                    .crossfade(true)
                    .build(),
                contentDescription = "Portada de $title",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}