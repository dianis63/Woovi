package com.woovi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.woovi.app.data.model.MediaItem
import com.woovi.app.data.model.MediaStatus
import kotlin.math.abs

@Composable
fun MediaCard(
    item: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coverColor = if (abs(item.id.hashCode()) % 2 == 0) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .background(coverColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.title.take(1).uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.mediaType.label,
                style = MaterialTheme.typography.labelMedium
            )
            StatusChip(status = item.status)
        }
    }
}

@Composable
fun StatusChip(status: MediaStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        MediaStatus.PENDIENTE -> MaterialTheme.colorScheme.secondaryContainer
        MediaStatus.EN_PROGRESO -> MaterialTheme.colorScheme.primaryContainer
        MediaStatus.COMPLETADO -> Color(0xFFCFE8D5)
        MediaStatus.ABANDONADO -> Color(0xFFE6E0E3)
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color
    ) {
        Text(
            text = status.label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}