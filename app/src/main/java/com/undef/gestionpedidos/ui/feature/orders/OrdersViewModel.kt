package com.undef.gestionpedidos.ui.feature.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrdersUiState(
    val orders: List<Pedido> = emptyList(),
    val searchQuery: String = ""
)

class OrdersViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private var allOrders: List<Pedido> = emptyList()

    init {
        viewModelScope.launch {
            ServiceLocator.orderRepository.getAllOrders().collect { pedidos ->
                allOrders = pedidos
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
        val filtrados = if (query.isBlank()) allOrders else allOrders.filter { it.numeroPedido.contains(query, ignoreCase = true) }
        _uiState.value = _uiState.value.copy(orders = filtrados)
    }
}
