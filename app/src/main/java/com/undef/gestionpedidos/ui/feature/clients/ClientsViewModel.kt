package com.undef.gestionpedidos.ui.feature.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.domain.model.Cliente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ClientsUiState(
    val clients: List<Cliente> = emptyList(),
    val searchQuery: String = ""
)

class ClientsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ClientsUiState())
    val uiState: StateFlow<ClientsUiState> = _uiState.asStateFlow()

    init {
        loadClients()
    }

    private fun loadClients() {
        viewModelScope.launch {
            val clientes = ServiceLocator.clientRepository.getAllClients()
            _uiState.value = _uiState.value.copy(clients = clientes)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            val clientes = ServiceLocator.clientRepository.getAllClients()
            val filtrados = if(query.isBlank()) clientes else clientes.filter { it.razonSocial.contains(query, ignoreCase = true) }
            _uiState.value = _uiState.value.copy(clients = filtrados)
        }
    }
}
