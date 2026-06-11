package com.undef.gestionpedidos.ui.feature.newclient

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NewClientUiState(
    val razonSocial: String = "",
    val cuit: String = "",
    val direccion: String = "",
    val localidad: String = "",
    val telefono: String = "",
    val email: String = ""
)

class NewClientViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewClientUiState())
    val uiState: StateFlow<NewClientUiState> = _uiState.asStateFlow()

    fun updateRazonSocial(newValue: String) {
        _uiState.update { it.copy(razonSocial = newValue) }
    }

    fun updateCuit(newValue: String) {
        _uiState.update { it.copy(cuit = newValue) }
    }

    fun updateDireccion(newValue: String) {
        _uiState.update { it.copy(direccion = newValue) }
    }

    fun updateLocalidad(newValue: String) {
        _uiState.update { it.copy(localidad = newValue) }
    }

    fun updateTelefono(newValue: String) {
        _uiState.update { it.copy(telefono = newValue) }
    }

    fun updateEmail(newValue: String) {
        _uiState.update { it.copy(email = newValue) }
    }

    fun saveClient(): Boolean {
        val state = _uiState.value
        // Basic validation
        if (state.razonSocial.isBlank() || state.cuit.isBlank()) {
            return false
        }
        // TODO: Persist logic here in the future
        return true
    }
}
