package com.woovi.app.ui.form

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.woovi.app.ui.components.CoverImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CoverPicker(
    existingCoverPath: String?,
    pickedCover: PickedCover?,
    isProcessing: Boolean,
    errorText: String?,
    title: String,
    onPicked: (android.net.Uri) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onPicked(uri)
    }

    val previewBitmap by produceState<ImageBitmap?>(initialValue = null, pickedCover) {
        value = pickedCover?.let { cover ->
            withContext(Dispatchers.Default) {
                BitmapFactory.decodeByteArray(cover.bytes, 0, cover.bytes.size)?.asImageBitmap()
            }
        }
    }

    val hasCover = pickedCover != null || existingCoverPath != null

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Portada (opcional)", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val previewModifier = Modifier
                .width(110.dp)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium)

            Box(contentAlignment = Alignment.Center) {
                val bitmap = previewBitmap
                when {
                    bitmap != null -> Image(
                        bitmap = bitmap,
                        contentDescription = "Vista previa de la portada",
                        contentScale = ContentScale.Crop,
                        modifier = previewModifier
                    )
                    pickedCover == null && existingCoverPath != null -> CoverImage(
                        coverPath = existingCoverPath,
                        title = title.ifBlank { "?" },
                        placeholderColor = MaterialTheme.colorScheme.primaryContainer,
                        modifier = previewModifier
                    )
                    else -> Box(
                        modifier = previewModifier.background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) { Text("Sin portada", style = MaterialTheme.typography.labelMedium) }
                }
                if (isProcessing) CircularProgressIndicator()
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    enabled = !isProcessing,
                    shape = MaterialTheme.shapes.medium
                ) { Text(if (hasCover) "Cambiar portada" else "Elegir portada") }

                if (hasCover) {
                    TextButton(onClick = onRemove, enabled = !isProcessing) { Text("Quitar portada") }
                }
                Text(
                    "JPG, PNG o WebP. Se optimiza antes de subirla.",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        errorText?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
    }
}