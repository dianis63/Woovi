package com.woovi.app.ui.library

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woovi.app.core.error.toUserMessage
import com.woovi.app.core.util.wooviViewModelFactory
import com.woovi.app.data.model.MediaItem
import com.woovi.app.data.repository.MediaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data object Empty : LibraryUiState
    data class Success(val items: List<MediaItem>) : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}

class LibraryViewModel(private val repository: MediaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            if (_uiState.value !is LibraryUiState.Success) {
                _uiState.value = LibraryUiState.Loading
            }
            try {
                val items = repository.getMyItems()
                _uiState.value = if (items.isEmpty()) LibraryUiState.Empty else LibraryUiState.Success(items)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("Woovi", "Error al cargar la biblioteca", e)
                _uiState.value = LibraryUiState.Error(e.toUserMessage())
            }
        }
    }

    companion object {
        val Factory = wooviViewModelFactory { LibraryViewModel(it.mediaRepository) }
    }
}