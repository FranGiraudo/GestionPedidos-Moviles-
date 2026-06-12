package com.undef.gestionpedidos.ui.feature.newclient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.domain.model.Cliente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    
    private var editingClientId: Int? = null

    fun loadClient(id: Int) {
        editingClientId = id
        viewModelScope.launch {
            val client = ServiceLocator.clientRepository.getClientById(id)
            if (client != null) {
                _uiState.update { 
                    it.copy(
                        razonSocial = client.razonSocial,
                        cuit = client.cuit,
                        direccion = client.direccion,
                        localidad = client.localidad,
                        telefono = client.telefono,
                        email = client.email
                    )
                }
            }
        }
    }

    fun updateRazonSocial(newValue: String) {
        _uiState.update { it.copy(razonSocial = newValue) }
    }

    fun updateCuit(newValue: String) {
        _uiState.update { it.copy(cuit = newValue) }
        // Auto-fetch si el CUIT tiene longitud de 11 (ejemplo: 20111111112)
        val cleanCuit = newValue.replace("-", "")
        if (cleanCuit.length == 11) {
            fetchCuitInfo(cleanCuit)
        }
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

    private fun fetchCuitInfo(cuit: String) {
        viewModelScope.launch {
            val result = ServiceLocator.clientRepository.fetchCuitData(cuit)
            _uiState.update { it.copy(razonSocial = result) }
        }
    }

    fun saveClient(): Boolean {
        val state = _uiState.value
        if (state.razonSocial.isBlank() || state.cuit.isBlank()) {
            return false
        }
        viewModelScope.launch {
            val cliente = Cliente(
                id = editingClientId ?: 0,
                razonSocial = state.razonSocial, 
                cuit = state.cuit, 
                direccion = state.direccion, 
                localidad = state.localidad, 
                telefono = state.telefono, 
                email = state.email
            )
            if (editingClientId != null) {
                ServiceLocator.clientRepository.updateClient(cliente)
            } else {
                ServiceLocator.clientRepository.addClient(cliente)
            }
        }
        return true
    }
}
