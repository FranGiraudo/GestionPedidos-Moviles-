package com.undef.gestionpedidos.ui.feature.newproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.domain.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewProductUiState(
    val codigo: String = "",
    val descripcion: String = "",
    val unidadMedida: String = "Unidad",
    val precioUnitario: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class NewProductViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewProductUiState())
    val uiState: StateFlow<NewProductUiState> = _uiState.asStateFlow()

    fun onCodigoChange(newCodigo: String) {
        _uiState.update { it.copy(codigo = newCodigo, error = null) }
    }

    fun onDescripcionChange(newDescripcion: String) {
        _uiState.update { it.copy(descripcion = newDescripcion, error = null) }
    }

    fun onUnidadMedidaChange(newUnidad: String) {
        _uiState.update { it.copy(unidadMedida = newUnidad, error = null) }
    }

    fun onPrecioUnitarioChange(newPrecio: String) {
        _uiState.update { it.copy(precioUnitario = newPrecio, error = null) }
    }

    fun saveProduct(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        if (state.codigo.isBlank() || state.descripcion.isBlank() || state.precioUnitario.isBlank()) {
            _uiState.update { it.copy(error = "Por favor, completa todos los campos.") }
            return
        }

        val precio = state.precioUnitario.toDoubleOrNull()
        if (precio == null || precio <= 0) {
            _uiState.update { it.copy(error = "El precio debe ser un número mayor a 0.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val producto = Producto(
                    id = 0,
                    codigo = state.codigo,
                    descripcion = state.descripcion,
                    unidadMedida = state.unidadMedida,
                    precioUnitario = precio,
                    stockActual = 0, // Stock inicial
                    activo = true
                )
                ServiceLocator.productRepository.addProduct(producto)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al guardar el producto") }
            }
        }
    }
}
