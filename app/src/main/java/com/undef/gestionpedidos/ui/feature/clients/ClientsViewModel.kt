package com.undef.gestionpedidos.ui.feature.clients

import androidx.lifecycle.ViewModel
import com.undef.gestionpedidos.data.mock.MockData
import com.undef.gestionpedidos.domain.model.Cliente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        _uiState.value = _uiState.value.copy(clients = MockData.clientes)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        // TODO: Implement actual filtering
    }
}
