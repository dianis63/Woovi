package com.woovi.app.ui.form

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woovi.app.data.model.MediaStatus
import com.woovi.app.data.model.MediaType
import com.woovi.app.ui.components.DateField
import com.woovi.app.ui.components.ErrorState
import com.woovi.app.ui.components.LoadingBox
import com.woovi.app.ui.components.MessageBanner
import com.woovi.app.ui.components.WooviTextField

@Composable
fun ItemFormScreen(
    itemId: String?,                       // null = agregar, con id = editar
    onFinished: () -> Unit,
    onBack: (() -> Unit)? = null,
    viewModel: ItemFormViewModel = viewModel(factory = ItemFormViewModel.factory(itemId))
) {
    val state by viewModel.uiState.collectAsState()
    val fields = state.fields
    val isEditing = itemId != null

    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.consumeSaved()
            onFinished()
        }
    }

    when {
        state.isLoading -> LoadingBox()
        state.loadFailed -> ErrorState(
            message = state.errorMessage ?: "No pudimos cargar la obra.",
            onRetry = { viewModel.retryLoad() }
        )
        else -> Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isEditing && onBack != null) {
                TextButton(onClick = onBack) { Text("← Cancelar") }
            }
            Text(
                text = if (isEditing) "Editar obra" else "Nueva obra",
                style = MaterialTheme.typography.headlineMedium
            )

            state.errorMessage?.let { MessageBanner(message = it, isError = true) }

            WooviTextField(
                value = fields.title,
                onValueChange = { v -> viewModel.updateFields { it.copy(title = v) } },
                label = "Título *",
                modifier = Modifier.fillMaxWidth(),
                errorText = state.titleError
            )

            Text("Tipo de contenido *", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MediaType.entries.forEach { option ->
                    FilterChip(
                        selected = fields.type == option,
                        onClick = { viewModel.updateFields { it.copy(type = option) } },
                        label = { Text(option.label) }
                    )
                }
            }

            Text("Estado *", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MediaStatus.entries.forEach { option ->
                    FilterChip(
                        selected = fields.status == option,
                        onClick = { viewModel.updateFields { it.copy(status = option) } },
                        label = { Text(option.label) }
                    )
                }
            }

            Text("Calificación (opcional)", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..10).forEach { n ->
                    FilterChip(
                        selected = fields.rating == n,
                        onClick = {
                            viewModel.updateFields { it.copy(rating = if (it.rating == n) null else n) }
                        },
                        label = { Text("$n") }
                    )
                }
            }

            WooviTextField(
                value = fields.genres,
                onValueChange = { v -> viewModel.updateFields { it.copy(genres = v) } },
                label = "Géneros (separados por coma)",
                modifier = Modifier.fillMaxWidth(),
                errorText = state.genresError
            )
            WooviTextField(
                value = fields.description,
                onValueChange = { v -> viewModel.updateFields { it.copy(description = v) } },
                label = "Descripción",
                modifier = Modifier.fillMaxWidth(),
                errorText = state.descriptionError,
                singleLine = false,
                minLines = 3,
                imeAction = ImeAction.Default
            )
            WooviTextField(
                value = fields.notes,
                onValueChange = { v -> viewModel.updateFields { it.copy(notes = v) } },
                label = "Notas personales",
                modifier = Modifier.fillMaxWidth(),
                errorText = state.notesError,
                singleLine = false,
                minLines = 3,
                imeAction = ImeAction.Default
            )

            DateField(
                label = "Inicio",
                value = fields.startDate,
                onValueChange = { v -> viewModel.updateFields { it.copy(startDate = v) } }
            )
            DateField(
                label = "Fin",
                value = fields.finishDate,
                onValueChange = { v -> viewModel.updateFields { it.copy(finishDate = v) } }
            )
            state.datesError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.save() },
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isEditing) "Guardar cambios" else "Guardar obra")
                }
            }
        }
    }
}