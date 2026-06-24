package com.undef.gestionpedidos.ui.feature.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.undef.gestionpedidos.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterUiState(
    val nombreCompleto: String = "",
    val email: String = "",
    val contrasena: String = "",
    val error: String? = null
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
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

    fun register(): Boolean {
        val state = _uiState.value
        if (state.nombreCompleto.isBlank() || state.email.isBlank() || state.contrasena.isBlank()) {
            _uiState.update { it.copy(error = getApplication<Application>().getString(R.string.error_required_fields)) }
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(error = getApplication<Application>().getString(R.string.error_invalid_email)) }
            return false
        }
        return true
    }
}
