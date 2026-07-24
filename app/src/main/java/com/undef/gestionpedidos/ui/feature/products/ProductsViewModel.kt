package com.undef.gestionpedidos.ui.feature.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.domain.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class ProductsUiState(
    val products: List<Producto> = emptyList(),
    val searchQuery: String = ""
)

class ProductsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    private var allProducts: List<Producto> = emptyList()

    init {
        viewModelScope.launch {
            ServiceLocator.productRepository.getAllProducts().collect { productos ->
                allProducts = productos
                applyFilter()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilter()
    }

    private fun applyFilter() {
        val query = _uiState.value.searchQuery
        val filtrados = if (query.isBlank()) {
            allProducts
        } else {
            allProducts.filter { 
                it.descripcion.contains(query, ignoreCase = true) || 
                it.codigo.contains(query, ignoreCase = true) 
            }
        }
        _uiState.value = _uiState.value.copy(products = filtrados)
    }

    fun deactivateProduct(id: Int) {
        viewModelScope.launch {
            ServiceLocator.productRepository.deactivateProduct(id)
        }
    }

    fun activateProduct(id: Int) {
        viewModelScope.launch {
            ServiceLocator.productRepository.activateProduct(id)
        }
    }
}
