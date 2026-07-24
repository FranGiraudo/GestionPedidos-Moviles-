package com.undef.gestionpedidos.ui.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val pedidosRecientes: List<Pedido> = emptyList(),
    val totalVentas: String = "0",
    val dolarBlue: String = "0",
    val pedidosPendientes: Int = 0,
    val isLoading: Boolean = true
)

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // Fetch puntual del dólar blue
        viewModelScope.launch {
            val dolares = ServiceLocator.financeRepository.getDolarBlue()
            _uiState.value = _uiState.value.copy(
                dolarBlue = String.format("%.2f", dolares)
            )
        }

        // Colección reactiva de los pedidos de Room
        viewModelScope.launch {
            ServiceLocator.orderRepository.getAllOrders().collect { pedidos ->
                val pendientes = pedidos.count { it.estado == EstadoPedido.BORRADOR }
                val recientes = pedidos.take(5)
                val ventas = pedidos.filter { it.estado != EstadoPedido.CANCELADO }.sumOf { it.total }

                _uiState.value = _uiState.value.copy(
                    pedidosRecientes = recientes,
                    totalVentas = String.format("%.2f", ventas),
                    pedidosPendientes = pendientes,
                    isLoading = false
                )
            }
        }
    }
}
