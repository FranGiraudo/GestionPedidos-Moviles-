package com.undef.gestionpedidos.ui.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.domain.model.EstadoPedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class TopProductInfo(val name: String, val quantity: Int, val fraction: Float)
data class TopClientInfo(val name: String, val spent: Double, val fraction: Float)
data class DailySales(val day: String, val amount: Double)

data class StatisticsUiState(
    val ventasTotalesMes: String = "$ 0.00",
    val nuevosClientesMes: String = "0",
    val topProducts: List<TopProductInfo> = emptyList(),
    val topClients: List<TopClientInfo> = emptyList(),
    val dailySales: List<DailySales> = emptyList(),
    val isLoading: Boolean = true
)

class StatisticsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val nf: NumberFormat = NumberFormat.getNumberInstance(Locale("es", "AR")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    init {
        viewModelScope.launch {
            val ordersFlow = ServiceLocator.orderRepository.getAllOrders()
            val clientsFlow = ServiceLocator.clientRepository.getAllClients()

            combine(ordersFlow, clientsFlow) { pedidos, clients ->
                val validOrders = pedidos.filter { it.estado != EstadoPedido.CANCELADO }

                val ventasTotales = validOrders.sumOf { it.total }

                // Productos más vendidos
                val productQuantities = mutableMapOf<String, Int>()
                for (order in validOrders) {
                    for (line in order.lineas) {
                        productQuantities[line.producto.descripcion] = productQuantities.getOrDefault(line.producto.descripcion, 0) + line.cantidad
                    }
                }
                val sortedProducts = productQuantities.entries.sortedByDescending { it.value }.take(5)
                val maxQty = sortedProducts.maxOfOrNull { it.value }?.toFloat() ?: 1f
                val topProducts = sortedProducts.map { 
                    TopProductInfo(it.key, it.value, it.value / maxQty) 
                }

                // Mayores clientes
                val clientSpent = validOrders.groupBy { it.cliente?.razonSocial ?: "Desconocido" }
                    .mapValues { entry -> entry.value.sumOf { it.total } }
                val sortedClients = clientSpent.entries.sortedByDescending { it.value }.take(5)
                val maxSpent = sortedClients.maxOfOrNull { it.value }?.toFloat() ?: 1f
                val topClients = sortedClients.map { 
                    TopClientInfo(it.key, it.value, (it.value / maxSpent).toFloat()) 
                }

                // Gráfico últimos 7 días
                val last7Days = (0..6).map { LocalDate.now().minusDays(it.toLong()) }.reversed()
                val formatter = DateTimeFormatter.ofPattern("dd/MM")
                val dailySales = last7Days.map { date ->
                    val sum = validOrders.filter { it.fechaCreacion == date }.sumOf { it.total }
                    DailySales(date.format(formatter), sum)
                }

                StatisticsUiState(
                    ventasTotalesMes = "$ ${nf.format(ventasTotales)}",
                    nuevosClientesMes = clients.size.toString(),
                    topProducts = topProducts,
                    topClients = topClients,
                    dailySales = dailySales,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}
