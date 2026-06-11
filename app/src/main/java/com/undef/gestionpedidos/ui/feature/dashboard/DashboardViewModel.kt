package com.undef.gestionpedidos.ui.feature.dashboard

import androidx.lifecycle.ViewModel
import com.undef.gestionpedidos.data.mock.MockData
import com.undef.gestionpedidos.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardUiState(
    val pedidosRecientes: List<Pedido> = emptyList(),
    val totalVentasDia: String = "0",
    val pedidosPendientes: Int = 0
)

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // Todo: Replace with real repository logic
        _uiState.value = DashboardUiState(
            pedidosRecientes = MockData.pedidos,
            totalVentasDia = "125,000",
            pedidosPendientes = 3
        )
    }
}
