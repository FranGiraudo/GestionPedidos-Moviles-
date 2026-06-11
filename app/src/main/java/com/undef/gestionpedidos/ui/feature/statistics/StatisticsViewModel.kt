package com.undef.gestionpedidos.ui.feature.statistics

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StatisticsUiState(
    val ventasTotalesMes: String = "$1,250,000",
    val nuevosClientesMes: String = "12",
    val productosMasVendidos: List<String> = listOf("Coca Cola 2.25L", "Fernet Branca 750ml", "Cerveza Quilmes 1L")
)

class StatisticsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    // TODO: Load real statistics data from repository
}
