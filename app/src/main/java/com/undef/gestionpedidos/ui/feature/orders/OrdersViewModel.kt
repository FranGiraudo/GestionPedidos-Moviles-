package com.undef.gestionpedidos.ui.feature.orders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.data.repository.PedidoRepository
import com.undef.gestionpedidos.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrdersUiState(
    val orders: List<Pedido> = emptyList(),
    val searchQuery: String = ""
)

class OrdersViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()
    
    private val repository = PedidoRepository(application)

    init {
        loadOrders()
        syncOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            repository.getRecentOrdersFlow().collect { pedidos ->
                _uiState.update { it.copy(orders = pedidos) }
            }
        }
    }

    private fun syncOrders() {
        viewModelScope.launch {
            repository.syncOrdersFromCloud()
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        // TODO: Implement actual filtering
    }
}
