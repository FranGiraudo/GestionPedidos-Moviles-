package com.undef.gestionpedidos.data.repository

import com.undef.gestionpedidos.data.mock.MockData
import com.undef.gestionpedidos.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductoRepository {
    fun getProductosFlow(): Flow<List<Producto>> = flow {
        // En el futuro, esto consultará Room o Retrofit.
        emit(MockData.productos)
    }
}
