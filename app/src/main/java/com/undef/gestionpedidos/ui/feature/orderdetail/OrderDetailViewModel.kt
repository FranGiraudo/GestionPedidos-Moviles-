package com.undef.gestionpedidos.ui.feature.orderdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.data.repository.PedidoRepository
import com.undef.gestionpedidos.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrderDetailUiState(
    val order: Pedido? = null,
    val isLoading: Boolean = true
)

class OrderDetailViewModel(
    private val orderId: Int,
    application: Application
) : AndroidViewModel(application) {

    private val repository = PedidoRepository(application)

    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            repository.getRecentOrdersFlow().collect { pedidos ->
                val order = pedidos.find { it.id == orderId }
                _uiState.value = OrderDetailUiState(order = order, isLoading = false)
            }
        }
    }
}

class OrderDetailViewModelFactory(
    private val orderId: Int,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderDetailViewModel(orderId, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
