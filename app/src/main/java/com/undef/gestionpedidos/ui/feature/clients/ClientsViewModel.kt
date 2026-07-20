package com.undef.gestionpedidos.ui.feature.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.domain.model.Cliente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class ClientsUiState(
    val clients: List<Cliente> = emptyList(),
    val searchQuery: String = ""
)

class ClientsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ClientsUiState())
    val uiState: StateFlow<ClientsUiState> = _uiState.asStateFlow()

    private var allClients: List<Cliente> = emptyList()

    init {
        viewModelScope.launch {
            ServiceLocator.clientRepository.getAllClients().collect { clientes ->
                allClients = clientes
                applyFilter()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilter()
    }

    private fun applyFilter() {
        val query = _uiState.value.searchQuery
        val filtrados = if (query.isBlank()) allClients else allClients.filter { it.razonSocial.contains(query, ignoreCase = true) }
        _uiState.value = _uiState.value.copy(clients = filtrados)
    }
}
