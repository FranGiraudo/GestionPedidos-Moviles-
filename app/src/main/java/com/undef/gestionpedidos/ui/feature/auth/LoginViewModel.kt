package com.undef.gestionpedidos.ui.feature.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val email: String = "",
    val contrasena: String = "",
    val error: String? = null
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun updateContrasena(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena, error = null) }
    }

    fun validateLogin(): Boolean {
        val email = _uiState.value.email
        val contrasena = _uiState.value.contrasena

        return if (email.isBlank() || contrasena.isBlank()) {
            _uiState.update { it.copy(error = "Todos los campos son obligatorios") }
            false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(error = "El formato del email es invalido") }
            false
        } else {
            _uiState.update { it.copy(error = null) }
            true
        }
    }
}
