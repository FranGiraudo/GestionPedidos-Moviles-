package com.undef.gestionpedidos.ui.feature.neworder

import androidx.lifecycle.ViewModel
import com.undef.gestionpedidos.data.mock.MockData
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.LineaPedido
import com.undef.gestionpedidos.domain.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NewOrderUiState(
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
}
