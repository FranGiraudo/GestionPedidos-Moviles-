package com.undef.gestionpedidos.ui.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class DashboardUiState(
    val pedidosRecientes: List<Pedido> = emptyList(),
    val totalVentas: String = "0",
    val dolarBlue: String = "0",
    val pedidosPendientes: Int = 0,
    val userName: String = "",
    val isLoading: Boolean = true
)

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val nf: NumberFormat = NumberFormat.getNumberInstance(Locale("es", "AR")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    init {
        // Fetch puntual del dólar blue
        viewModelScope.launch {
            val dolares = ServiceLocator.financeRepository.getDolarBlue()
            _uiState.value = _uiState.value.copy(
                dolarBlue = nf.format(dolares)
            )
        }

        // Colección reactiva: pedidos + nombre de usuario juntos
        viewModelScope.launch {
            combine(
                ServiceLocator.orderRepository.getAllOrders(),
                ServiceLocator.userPreferencesRepository.userName
            ) { pedidos, name ->
                val pendientes = pedidos.count { it.estado == EstadoPedido.BORRADOR }
                val recientes = pedidos.take(5)
                val ventas = pedidos.filter { it.estado != EstadoPedido.CANCELADO }.sumOf { it.total }

                DashboardUiState(
                    pedidosRecientes = recientes,
                    totalVentas = nf.format(ventas),
                    dolarBlue = _uiState.value.dolarBlue,
                    pedidosPendientes = pendientes,
                    userName = name ?: "",
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}
