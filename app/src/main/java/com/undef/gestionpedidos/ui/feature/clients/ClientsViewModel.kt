package com.undef.gestionpedidos.ui.feature.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.data.repository.ClienteRepository
import com.undef.gestionpedidos.domain.model.Cliente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientsUiState(
    val clients: List<Cliente> = emptyList(),
    val searchQuery: String = ""
)

class ClientsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ClientsUiState())
    val uiState: StateFlow<ClientsUiState> = _uiState.asStateFlow()

    private val repository = ClienteRepository()

    init {
        loadClients()
    }

    private fun loadClients() {
        viewModelScope.launch {
            repository.getClientesFlow().collect { clientes ->
                _uiState.update { it.copy(clients = clientes) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        // TODO: Implement actual filtering
    }
}
