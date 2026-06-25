package com.undef.gestionpedidos.ui.feature.neworder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.R
import com.undef.gestionpedidos.data.repository.PedidoRepository
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.LineaPedido
import com.undef.gestionpedidos.domain.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewOrderUiState(
    val expandedClientMenu: Boolean = false,
    val selectedClient: Cliente? = null,
    val observaciones: String = "",
    val expandedProductMenu: Boolean = false,
    val selectedProduct: Producto? = null,
    val quantityText: String = "1",
    val orderLines: List<LineaPedido> = emptyList(),
    val total: Double = 0.0,
    // ── Estados de red ──────────────────────────────────────────────────────
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val pedidoEnviadoExitoso: Boolean = false
)

class NewOrderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PedidoRepository(application)

    private val _uiState = MutableStateFlow(NewOrderUiState())
    val uiState: StateFlow<NewOrderUiState> = _uiState.asStateFlow()

    // ── Selección de cliente ─────────────────────────────────────────────────

    fun updateExpandedClientMenu(expanded: Boolean) {
        _uiState.update { it.copy(expandedClientMenu = expanded) }
    }

    fun updateSelectedClient(cliente: Cliente) {
        _uiState.update { it.copy(selectedClient = cliente, expandedClientMenu = false) }
    }

    // ── Observaciones ────────────────────────────────────────────────────────

    fun updateObservaciones(obs: String) {
        _uiState.update { it.copy(observaciones = obs) }
    }

    // ── Selección de producto ────────────────────────────────────────────────

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

    // ── Líneas del pedido ────────────────────────────────────────────────────

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

            _uiState.update {
                it.copy(
                    orderLines = updatedLines,
                    total = newTotal,
                    selectedProduct = null,
                    quantityText = "1"
                )
            }
        }
    }

    fun removeProduct(linea: LineaPedido) {
        val currentState = _uiState.value
        val updatedLines = currentState.orderLines.toMutableList()
        updatedLines.remove(linea)
        val newTotal = updatedLines.sumOf { it.subtotal }
        _uiState.update { it.copy(orderLines = updatedLines, total = newTotal) }
    }

    // ── Confirmación y POST de red ───────────────────────────────────────────

    /**
     * Valida el formulario y envía el pedido al servidor mediante un POST real.
     * Actualiza [uiState] con el mensaje para el Snackbar y el flag de éxito.
     */
    fun confirmarPedido() {
        val state = _uiState.value
        val cliente = state.selectedClient
        val lineas = state.orderLines

        // Validación (sin cambiar lógica de negocio)
        if (cliente == null || lineas.isEmpty()) {
            _uiState.update { it.copy(userMessage = getApplication<Application>().getString(R.string.error_select_client_product)) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val resultado = repository.enviarPedido(
                cliente      = cliente,
                lineas       = lineas,
                observaciones = state.observaciones,
                total        = state.total
            )

            resultado.fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(
                            isLoading            = false,
                            userMessage          = "✓ Pedido #${response.id} enviado correctamente",
                            pedidoEnviadoExitoso = true
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading   = false,
                            userMessage = getApplication<Application>().getString(R.string.error_send_order)
                        )
                    }
                }
            )
        }
    }

    /** Limpia el mensaje del Snackbar luego de mostrarlo */
    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
