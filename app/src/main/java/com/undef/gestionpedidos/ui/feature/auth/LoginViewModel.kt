package com.undef.gestionpedidos.ui.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val LOGIN_EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

data class LoginUiState(
    val email: String = "",
    val contrasena: String = "",
    val isLoading: Boolean = false,
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

    fun validateLogin(onSuccess: () -> Unit) {
        val email = _uiState.value.email
        val contrasena = _uiState.value.contrasena

        if (email.isBlank() || contrasena.isBlank()) {
            _uiState.update { it.copy(error = "Todos los campos son obligatorios") }
            return
        }
        if (!LOGIN_EMAIL_REGEX.matches(email)) {
            _uiState.update { it.copy(error = "El formato del email es invalido") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val usuario = ServiceLocator.userRepository.login(email, contrasena)
            if (usuario != null) {
                ServiceLocator.userPreferencesRepository.saveLoginSession(email)
                onSuccess()
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Email o contraseña incorrectos") }
            }
        }
    }
}
