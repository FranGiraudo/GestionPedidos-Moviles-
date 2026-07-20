package com.undef.gestionpedidos.ui.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

data class RegisterUiState(
    val nombreCompleto: String = "",
    val email: String = "",
    val contrasena: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateNombreCompleto(newValue: String) {
        _uiState.update { it.copy(nombreCompleto = newValue, error = null) }
    }

    fun updateEmail(newValue: String) {
        _uiState.update { it.copy(email = newValue, error = null) }
    }

    fun updateContrasena(newValue: String) {
        _uiState.update { it.copy(contrasena = newValue, error = null) }
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.nombreCompleto.isBlank() || state.email.isBlank() || state.contrasena.isBlank()) {
            _uiState.update { it.copy(error = "Todos los campos son obligatorios") }
            return
        }
        if (!EMAIL_REGEX.matches(state.email)) {
            _uiState.update { it.copy(error = "El formato del email es invalido") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val success = ServiceLocator.userRepository.register(
                email = state.email,
                password = state.contrasena,
                fullName = state.nombreCompleto
            )
            if (success) {
                onSuccess()
            } else {
                _uiState.update { it.copy(isLoading = false, error = "El email ya está registrado") }
            }
        }
    }
}
