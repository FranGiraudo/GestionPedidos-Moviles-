package com.undef.gestionpedidos.ui.feature.neworder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.LineaPedido
import com.undef.gestionpedidos.domain.model.Pedido
import com.undef.gestionpedidos.domain.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class NewOrderUiState(
    val availableClients: List<Cliente> = emptyList(),
    val availableProducts: List<Producto> = emptyList(),
    val expandedClientMenu: Boolean = false,
    val selectedClient: Cliente? = null,
    val observaciones: String = "",
    val expandedProductMenu: Boolean = false,
    val selectedProduct: Producto? = null,
    val quantityText: String = "1",
    val orderLines: List<LineaPedido> = emptyList(),
    val total: Double = 0.0
)

class NewOrderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewOrderUiState())
    val uiState: StateFlow<NewOrderUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val clients = ServiceLocator.clientRepository.getAllClients()
            val products = ServiceLocator.productRepository.getAllProducts()
            _uiState.update { it.copy(
                availableClients = clients.filter { c -> c.activo },
                availableProducts = products.filter { p -> p.activo }
            ) }
        }
    }

    fun updateExpandedClientMenu(expanded: Boolean) {
        _uiState.update { it.copy(expandedClientMenu = expanded) }
    }

    fun updateSelectedClient(cliente: Cliente) {
        _uiState.update { it.copy(selectedClient = cliente, expandedClientMenu = false) }
    }

    fun updateObservaciones(obs: String) {
        _uiState.update { it.copy(observaciones = obs) }
    }

    fun updateExpandedProductMenu(expanded: Boolean) {
        _uiState.update { it.copy(expandedProductMenu = expanded) }
    }

    fun updateSelectedProduct(producto: Producto) {
        _uiState.update { it.copy(selectedProduct = producto, expandedProductMenu = false) }
    }

    fun updateQuantityText(qty: String) {
        if (qty.isEmpty() || qty.all { it.isDigit() }) {
            _uiState.update { it.copy(quantityText = qty) }
        }
    }

    fun addProduct() {
        val currentState = _uiState.value
        val product = currentState.selectedProduct
        val qty = currentState.quantityText.toIntOrNull() ?: 0

        if (product != null && qty > 0) {
            val updatedLines = currentState.orderLines.toMutableList()
            val existingIndex = updatedLines.indexOfFirst { it.producto.id == product.id }
            
            if (existingIndex != -1) {
                val existingLine = updatedLines[existingIndex]
                updatedLines[existingIndex] = existingLine.copy(
                    cantidad = existingLine.cantidad + qty
                )
            } else {
                updatedLines.add(
                    LineaPedido(
                        id = updatedLines.size + 1,
                        pedidoId = 0,
                        producto = product,
                        cantidad = qty,
                        precioUnitario = product.precioUnitario
                    )
                )
            }
            
            val newTotal = updatedLines.sumOf { it.subtotal }

            _uiState.update { it.copy(
                orderLines = updatedLines,
                total = newTotal,
                selectedProduct = null,
                quantityText = "1"
            )}
        }
    }

    fun removeProduct(linea: LineaPedido) {
        val currentState = _uiState.value
        val updatedLines = currentState.orderLines.toMutableList()
        updatedLines.remove(linea)
        val newTotal = updatedLines.sumOf { it.subtotal }
        _uiState.update { it.copy(orderLines = updatedLines, total = newTotal) }
    }

    fun saveOrder(): Boolean {
        val state = _uiState.value
        if (state.selectedClient == null || state.orderLines.isEmpty()) {
            return false
        }
        
        viewModelScope.launch {
            val order = Pedido(
                id = 0,
                numeroPedido = "PED-${System.currentTimeMillis().toString().takeLast(5)}",
                cliente = state.selectedClient,
                fechaCreacion = LocalDate.now(),
                fechaEntregaEstimada = LocalDate.now().plusDays(1),
                estado = EstadoPedido.BORRADOR,
                lineas = state.orderLines,
                observaciones = state.observaciones
            )
            ServiceLocator.orderRepository.saveOrder(order)
        }
        return true
    }
}
