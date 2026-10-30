package com.woovi.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woovi.app.ui.components.MessageBanner
import com.woovi.app.ui.components.WooviTextField

@Composable
fun RegisterScreen(
    onGoLogin: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Volver") }
        Spacer(Modifier.height(8.dp))
        Text("Crea tu cuenta", style = MaterialTheme.typography.headlineMedium)
        Text("Así tendrás tu biblioteca guardada y solo para ti.", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))

        state.errorMessage?.let { MessageBanner(message = it, isError = true) }
        state.infoMessage?.let { MessageBanner(message = it, isError = false) }

        WooviTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo electrónico",
            modifier = Modifier.fillMaxWidth(),
            errorText = state.emailError,
            keyboardType = KeyboardType.Email
        )
        WooviTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña (mínimo 8 caracteres)",
            modifier = Modifier.fillMaxWidth(),
            errorText = state.passwordError,
            keyboardType = KeyboardType.Password,
            isPassword = true
        )
        WooviTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = "Repite la contraseña",
            modifier = Modifier.fillMaxWidth(),
            errorText = state.confirmError,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            isPassword = true,
            onDone = { viewModel.signUp(email, password, confirm) }
        )

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { viewModel.signUp(email, password, confirm) },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.large
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Crear cuenta")
            }
        }
        TextButton(
            onClick = onGoLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) { Text("¿Ya tienes cuenta? Inicia sesión") }
    }
}
