package com.undef.gestionpedidos.ui.feature.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import kotlinx.coroutines.launch
import com.undef.gestionpedidos.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OrderDetailUiState(
    val order: Pedido? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class OrderDetailViewModel(private val orderId: Int) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            val order = ServiceLocator.orderRepository.getOrderById(orderId)
            if (order != null) {
                _uiState.value = OrderDetailUiState(order = order, isLoading = false)
            } else {
                _uiState.value = OrderDetailUiState(isLoading = false, error = "Pedido no encontrado")
            }
        }
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
