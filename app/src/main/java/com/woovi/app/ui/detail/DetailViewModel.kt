package com.woovi.app.ui.detail

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val item: MediaItem? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isDeleting: Boolean = false,
    val deleted: Boolean = false
)

class DetailViewModel(
    private val repository: MediaRepository,
    private val itemId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            if (_uiState.value.item == null) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }
            try {
                val item = repository.getItem(itemId)
                _uiState.update { it.copy(item = item, isLoading = false, errorMessage = null) }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("Woovi", "Error al cargar el detalle", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = e.toUserMessage()) }
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }
            try {
                repository.deleteItem(itemId)
                _uiState.update { it.copy(isDeleting = false, deleted = true) }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("Woovi", "Error al eliminar la obra", e)
                _uiState.update { it.copy(isDeleting = false, errorMessage = e.toUserMessage()) }
            }
        }
    }

    companion object {
        fun factory(itemId: String) = wooviViewModelFactory {
            DetailViewModel(it.mediaRepository, itemId)
        }
    }
}