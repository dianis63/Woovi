package com.woovi.app.ui.form

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woovi.app.core.error.toUserMessage
import com.woovi.app.core.util.wooviViewModelFactory
import com.woovi.app.data.model.MediaItem
import com.woovi.app.data.model.MediaItemInput
import com.woovi.app.data.model.MediaStatus
import com.woovi.app.data.model.MediaType
import com.woovi.app.data.repository.MediaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormFields(
    val title: String = "",
    val type: MediaType = MediaType.MANGA,
    val status: MediaStatus = MediaStatus.PENDIENTE,
    val description: String = "",
    val genres: String = "",
    val rating: Int? = null,
    val notes: String = "",
    val startDate: String? = null,
    val finishDate: String? = null
)

data class ItemFormUiState(
    val fields: FormFields = FormFields(),
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
    val isSaving: Boolean = false,
    val titleError: String? = null,
    val genresError: String? = null,
    val descriptionError: String? = null,
    val notesError: String? = null,
    val datesError: String? = null,
    val errorMessage: String? = null,
    val saved: Boolean = false
)

class ItemFormViewModel(
    private val repository: MediaRepository,
    private val itemId: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemFormUiState(isLoading = itemId != null))
    val uiState: StateFlow<ItemFormUiState> = _uiState.asStateFlow()

    init {
        if (itemId != null) loadItem(itemId)
    }

    fun retryLoad() {
        if (itemId != null) loadItem(itemId)
    }

    private fun loadItem(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadFailed = false, errorMessage = null) }
            try {
                val item = repository.getItem(id)
                _uiState.update { it.copy(isLoading = false, fields = item.toFields()) }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("Woovi", "Error al cargar la obra para editar", e)
                _uiState.update {
                    it.copy(isLoading = false, loadFailed = true, errorMessage = e.toUserMessage())
                }
            }
        }
    }

    fun updateFields(transform: (FormFields) -> FormFields) {
        _uiState.update { it.copy(fields = transform(it.fields)) }
    }

    fun save() {
        val f = _uiState.value.fields
        val title = f.title.trim()
        val genres = parseGenres(f.genres)

        val titleError = when {
            title.isEmpty() -> "El título es obligatorio"
            title.length > 150 -> "Máximo 150 caracteres"
            else -> null
        }
        val genresError =
            if (genres.size > 10 || genres.any { it.length > 30 }) "Máximo 10 géneros de hasta 30 letras" else null
        val descriptionError = if (f.description.length > 2000) "Máximo 2000 caracteres" else null
        val notesError = if (f.notes.length > 4000) "Máximo 4000 caracteres" else null
        val datesError = if (f.startDate != null && f.finishDate != null && f.finishDate < f.startDate)
            "La fecha de fin no puede ser anterior a la de inicio" else null

        if (listOf(titleError, genresError, descriptionError, notesError, datesError).any { it != null }) {
            _uiState.update {
                it.copy(
                    titleError = titleError,
                    genresError = genresError,
                    descriptionError = descriptionError,
                    notesError = notesError,
                    datesError = datesError,
                    errorMessage = null
                )
            }
            return
        }

        val input = MediaItemInput(
            title = title,
            mediaType = f.type,
            status = f.status,
            description = f.description.trim().ifEmpty { null },
            genres = genres,
            rating = f.rating,
            notes = f.notes.trim().ifEmpty { null },
            startDate = f.startDate,
            finishDate = f.finishDate
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true, errorMessage = null, titleError = null, genresError = null,
                    descriptionError = null, notesError = null, datesError = null
                )
            }
            try {
                if (itemId == null) {
                    repository.addItem(input)
                    _uiState.value = ItemFormUiState(saved = true)
                } else {
                    repository.updateItem(itemId, input)
                    _uiState.update { it.copy(isSaving = false, saved = true) }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("Woovi", "Error al guardar la obra", e)
                _uiState.update { it.copy(isSaving = false, errorMessage = e.toUserMessage()) }
            }
        }
    }

    fun consumeSaved() {
        _uiState.update { it.copy(saved = false) }
    }

    private fun parseGenres(text: String): List<String> =
        text.split(",").map { it.trim() }.filter { it.isNotEmpty() }.distinctBy { it.lowercase() }

    private fun MediaItem.toFields() = FormFields(
        title = title,
        type = mediaType,
        status = status,
        description = description.orEmpty(),
        genres = genres.joinToString(", "),
        rating = rating,
        notes = notes.orEmpty(),
        startDate = startDate,
        finishDate = finishDate
    )

    companion object {
        fun factory(itemId: String?) = wooviViewModelFactory {
            ItemFormViewModel(it.mediaRepository, itemId)
        }
    }
}