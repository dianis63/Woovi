package com.woovi.app.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woovi.app.core.util.DateFormats
import com.woovi.app.data.model.MediaItem
import com.woovi.app.ui.components.ConfirmDialog
import com.woovi.app.ui.components.ErrorState
import com.woovi.app.ui.components.LoadingBox
import com.woovi.app.ui.components.MessageBanner
import com.woovi.app.ui.components.StatusChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    itemId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDeleted: () -> Unit,
    viewModel: DetailViewModel = viewModel(factory = DetailViewModel.factory(itemId))
) {
    val state by viewModel.uiState.collectAsState()
    var showConfirm by remember { mutableStateOf(false) }

    // Se recarga cada vez que entras (por ejemplo, al volver de editar).
    LaunchedEffect(Unit) { viewModel.load() }
    LaunchedEffect(state.deleted) { if (state.deleted) onDeleted() }

    val item = state.item
    when {
        item == null && state.isLoading -> LoadingBox()
        item == null -> ErrorState(
            message = state.errorMessage ?: "No pudimos cargar la obra.",
            onRetry = { viewModel.load() }
        )
        else -> DetailContent(
            item = item,
            errorMessage = state.errorMessage,
            isDeleting = state.isDeleting,
            onBack = onBack,
            onEdit = { onEdit(item.id) },
            onDeleteClick = { showConfirm = true }
        )
    }

    if (showConfirm) {
        ConfirmDialog(
            title = "¿Eliminar esta obra?",
            message = "Se borrará de tu biblioteca y no podrás recuperarla.",
            confirmLabel = "Eliminar",
            onConfirm = {
                showConfirm = false
                viewModel.delete()
            },
            onDismiss = { showConfirm = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(
    item: MediaItem,
    errorMessage: String?,
    isDeleting: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Volver") }

        errorMessage?.let { MessageBanner(message = it, isError = true) }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .aspectRatio(2f / 3f)
                .align(Alignment.CenterHorizontally)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.title.take(1).uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Text(item.title, style = MaterialTheme.typography.headlineMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(item.mediaType.label, style = MaterialTheme.typography.titleMedium)
            StatusChip(status = item.status)
        }

        item.rating?.let {
            Text("★ $it / 10", style = MaterialTheme.typography.titleMedium)
        }

        if (item.genres.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item.genres.forEach { genre ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = genre,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        item.description?.let { Section(title = "Descripción", text = it) }
        item.notes?.let { Section(title = "Mis notas", text = it) }

        if (item.startDate != null || item.finishDate != null) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                item.startDate?.let {
                    Text("Inicio: ${DateFormats.toDisplay(it)}", style = MaterialTheme.typography.bodyLarge)
                }
                item.finishDate?.let {
                    Text("Fin: ${DateFormats.toDisplay(it)}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onEdit,
            enabled = !isDeleting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.large
        ) { Text("Editar") }

        OutlinedButton(
            onClick = onDeleteClick,
            enabled = !isDeleting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) { Text(if (isDeleting) "Eliminando…" else "Eliminar") }
    }
}

@Composable
private fun Section(title: String, text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}