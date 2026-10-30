package com.woovi.app.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woovi.app.ui.components.EmptyState
import com.woovi.app.ui.components.ErrorState
import com.woovi.app.ui.components.LoadingBox
import com.woovi.app.ui.components.MediaCard

@Composable
fun LibraryScreen(
    onAddClick: () -> Unit,
    onItemClick: (String) -> Unit,
    viewModel: LibraryViewModel = viewModel(factory = LibraryViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Mi biblioteca",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(top = 16.dp)
        )

        when (val s = state) {
            is LibraryUiState.Loading -> LoadingBox()
            is LibraryUiState.Error -> ErrorState(message = s.message, onRetry = { viewModel.load() })
            is LibraryUiState.Empty -> EmptyState(
                title = "Tu estantería está vacía",
                message = "Agrega tu primera obra y empieza a llenar tu pequeña biblioteca.",
                actionLabel = "Agregar obra",
                onAction = onAddClick
            )
            is LibraryUiState.Success -> {
                Text(
                    text = "${s.items.size} obras",
                    style = MaterialTheme.typography.bodyMedium
                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(s.items, key = { it.id }) { item ->
                        MediaCard(item = item, onClick = { onItemClick(item.id) })
                    }
                }
            }
        }
    }
}