package com.woovi.app.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.woovi.app.WooviApp
import com.woovi.app.core.error.toUserMessage
import com.woovi.app.core.util.Validators
import com.woovi.app.data.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        val emailError = Validators.email(email)
        val passwordError = if (password.isBlank()) "Escribe tu contraseña" else null
        if (emailError != null || passwordError != null) {
            _uiState.value = AuthUiState(emailError = emailError, passwordError = passwordError)
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                repository.signIn(email.trim(), password)
                _uiState.value = AuthUiState()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e(TAG, "Error al iniciar sesión", e)
                _uiState.value = AuthUiState(errorMessage = e.toUserMessage())
            }
        }
    }

    fun signUp(email: String, password: String, confirm: String) {
        val emailError = Validators.email(email)
        val passwordError = Validators.password(password)
        val confirmError = if (confirm != password) "Las contraseñas no coinciden" else null
        if (emailError != null || passwordError != null || confirmError != null) {
            _uiState.value = AuthUiState(
                emailError = emailError,
                passwordError = passwordError,
                confirmError = confirmError
            )
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val sessionStarted = repository.signUp(email.trim(), password)
                _uiState.value = if (sessionStarted) {
                    AuthUiState()
                } else {
                    AuthUiState(infoMessage = "Te enviamos un correo para confirmar tu cuenta. Ábrelo y luego inicia sesión.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e(TAG, "Error al registrarse", e)
                _uiState.value = AuthUiState(errorMessage = e.toUserMessage())
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                repository.signOut()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e(TAG, "Error al cerrar sesión", e)
            }
        }
    }

    companion object {
        private const val TAG = "Woovi"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WooviApp
                AuthViewModel(app.container.authRepository)
            }
        }
    }
}