package com.undef.gestionpedidos.data.repository

import com.undef.gestionpedidos.data.mock.MockData
import com.undef.gestionpedidos.domain.model.Cliente
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ClienteRepository {
    fun getClientesFlow(): Flow<List<Cliente>> = flow {
        // En el futuro, esto consultará Room o Retrofit.
        // Por ahora exponemos la data estática como un Flow para cumplir el patrón reactivo.
        emit(MockData.clientes)
    }
}
