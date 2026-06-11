package com.undef.gestionpedidos.ui.feature.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.undef.gestionpedidos.data.mock.MockData
import com.undef.gestionpedidos.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OrderDetailUiState(
    val order: Pedido? = null,
    val isLoading: Boolean = true
)

class OrderDetailViewModel(private val orderId: Int) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        val order = MockData.pedidos.find { it.id == orderId }
        _uiState.value = OrderDetailUiState(order = order, isLoading = false)
    }
}

class OrderDetailViewModelFactory(private val orderId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderDetailViewModel(orderId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
