package com.undef.gestionpedidos.ui.feature.dashboard

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

data class DashboardUiState(
    val pedidosRecientes: List<Pedido> = emptyList(),
    val totalVentasDia: String = "0",
    val pedidosPendientes: Int = 0
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    private val repository = PedidoRepository(application)

    init {
        loadData()
        syncData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getRecentOrdersFlow().collect { pedidos ->
                _uiState.update { 
                    it.copy(
                        pedidosRecientes = pedidos,
                        pedidosPendientes = pedidos.count { p -> p.estado.name == "EN_PREPARACION" || p.estado.name == "CONFIRMADO" }
                    ) 
                }
            }
        }
    }

    private fun syncData() {
        viewModelScope.launch {
            repository.syncOrdersFromCloud()
        }
    }
}
