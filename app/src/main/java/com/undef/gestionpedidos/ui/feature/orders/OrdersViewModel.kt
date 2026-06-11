package com.undef.gestionpedidos.ui.feature.orders

import androidx.lifecycle.ViewModel
import com.undef.gestionpedidos.data.mock.MockData
import com.undef.gestionpedidos.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OrdersUiState(
    val orders: List<Pedido> = emptyList(),
    val searchQuery: String = ""
)

class OrdersViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        _uiState.value = _uiState.value.copy(orders = MockData.pedidos)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        // TODO: Implement actual filtering
    }
}
