package com.undef.gestionpedidos.data.repository

import com.undef.gestionpedidos.data.local.dao.ClientDao
import com.undef.gestionpedidos.data.local.dao.ProductDao
import com.undef.gestionpedidos.data.local.entity.ClientEntity
import com.undef.gestionpedidos.data.local.entity.ProductEntity
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.Producto
import com.undef.gestionpedidos.data.remote.ApiService
import kotlinx.coroutines.delay

class ClientRepository(private val clientDao: ClientDao, private val apiService: ApiService) {
    suspend fun getAllClients(): List<Cliente> {
        return clientDao.getAllClients().map {
            Cliente(it.id, it.razonSocial, it.cuit, it.direccion, it.localidad, it.telefono, it.email, it.activo)
        }
    }

    suspend fun getClientById(id: Int): Cliente? {
        val it = clientDao.getClientById(id) ?: return null
        return Cliente(it.id, it.razonSocial, it.cuit, it.direccion, it.localidad, it.telefono, it.email, it.activo)
    }

    suspend fun addClient(cliente: Cliente) {
        val entity = ClientEntity(
            razonSocial = cliente.razonSocial,
            cuit = cliente.cuit,
            direccion = cliente.direccion,
            localidad = cliente.localidad,
            telefono = cliente.telefono,
            email = cliente.email,
            activo = cliente.activo
        )
        clientDao.insertClient(entity)
    }

    suspend fun fetchCuitData(cuit: String): String {
        delay(1000)
        if(cuit.startsWith("20") || cuit.startsWith("23") || cuit.startsWith("27")) {
            return "Consumidor Final Mock"
        } else if (cuit.startsWith("30") || cuit.startsWith("33")) {
            return "Empresa S.A. Mock"
        }
        return "Razon Social Desconocida"
    }
}

class ProductRepository(private val productDao: ProductDao) {
    suspend fun getAllProducts(): List<Producto> {
        return productDao.getAllProducts().map {
            Producto(it.id, it.codigo, it.descripcion, it.unidadMedida, it.precioUnitario, it.stockActual, it.activo)
        }
    }

    suspend fun getProductById(id: Int): Producto? {
        val it = productDao.getProductById(id) ?: return null
        return Producto(it.id, it.codigo, it.descripcion, it.unidadMedida, it.precioUnitario, it.stockActual, it.activo)
    }

    suspend fun addProduct(producto: Producto) {
        val entity = ProductEntity(
            codigo = producto.codigo,
            descripcion = producto.descripcion,
            unidadMedida = producto.unidadMedida,
            precioUnitario = producto.precioUnitario,
            stockActual = producto.stockActual,
            activo = producto.activo
        )
        productDao.insertProduct(entity)
    }
}

class FinanceRepository(private val apiService: ApiService) {
    suspend fun getDolarBlue(): Double {
        return try {
            val response = apiService.getDolarBlue()
            response.venta
        } catch (e: Exception) {
            1200.0
        }
    }
}
