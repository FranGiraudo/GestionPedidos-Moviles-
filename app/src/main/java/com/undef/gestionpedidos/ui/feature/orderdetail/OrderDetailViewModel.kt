package com.undef.gestionpedidos.ui.feature.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import kotlinx.coroutines.launch
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import com.undef.gestionpedidos.domain.model.LineaPedido
import com.undef.gestionpedidos.domain.model.Producto

data class OrderDetailUiState(
    val order: Pedido? = null,
    val editableLines: List<LineaPedido> = emptyList(),
    val availableProducts: List<Producto> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class OrderDetailViewModel(private val orderId: Int) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    init {
        loadOrder()
        loadProducts()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            val order = ServiceLocator.orderRepository.getOrderById(orderId)
            if (order != null) {
                _uiState.value = _uiState.value.copy(
                    order = order, 
                    editableLines = order.lineas,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Pedido no encontrado")
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val products = ServiceLocator.productRepository.getAllProducts()
            _uiState.value = _uiState.value.copy(availableProducts = products.filter { it.activo })
        }
    }

    fun updateLineQuantity(productoId: Int, newQuantity: Int) {
        val currentLines = _uiState.value.editableLines.toMutableList()
        val index = currentLines.indexOfFirst { it.producto.id == productoId }
        if (index != -1 && newQuantity > 0) {
            val line = currentLines[index]
            currentLines[index] = line.copy(
                cantidad = newQuantity
            )
            _uiState.value = _uiState.value.copy(editableLines = currentLines)
        }
    }

    fun removeLine(productoId: Int) {
        val currentLines = _uiState.value.editableLines.filterNot { it.producto.id == productoId }
        _uiState.value = _uiState.value.copy(editableLines = currentLines)
    }

    fun addProductLine(producto: Producto) {
        val currentLines = _uiState.value.editableLines.toMutableList()
        val exists = currentLines.any { it.producto.id == producto.id }
        if (!exists) {
            val newLine = LineaPedido(
                id = 0,
                pedidoId = orderId,
                producto = producto,
                cantidad = 1,
                precioUnitario = producto.precioUnitario
            )
            currentLines.add(newLine)
            _uiState.value = _uiState.value.copy(editableLines = currentLines)
        }
    }

    fun saveOrderChanges(newStatus: EstadoPedido) {
        val currentOrder = _uiState.value.order ?: return
        val currentLines = _uiState.value.editableLines
        
        val updatedOrder = currentOrder.copy(
            estado = newStatus,
            lineas = currentLines
        )
        
        _uiState.value = _uiState.value.copy(order = updatedOrder)
        
        viewModelScope.launch {
            ServiceLocator.orderRepository.updateOrder(updatedOrder)
        }
    }

    fun attachComprobante(uri: String) {
        val currentOrder = _uiState.value.order ?: return
        val updatedOrder = currentOrder.copy(comprobanteUri = uri)
        _uiState.value = _uiState.value.copy(order = updatedOrder)
        
        viewModelScope.launch {
            ServiceLocator.orderRepository.updateOrder(updatedOrder)
        }
    }

    fun deleteOrder() {
        viewModelScope.launch {
            ServiceLocator.orderRepository.deleteOrder(orderId)
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
