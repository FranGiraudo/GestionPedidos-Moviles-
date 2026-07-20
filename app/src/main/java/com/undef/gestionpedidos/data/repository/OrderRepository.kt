package com.undef.gestionpedidos.data.repository

import com.undef.gestionpedidos.data.local.dao.ClientDao
import com.undef.gestionpedidos.data.local.dao.OrderDao
import com.undef.gestionpedidos.data.local.dao.ProductDao
import com.undef.gestionpedidos.data.local.entity.OrderEntity
import com.undef.gestionpedidos.data.local.entity.OrderLineEntity
import com.undef.gestionpedidos.data.remote.ApiService
import com.undef.gestionpedidos.data.remote.OrderLineSyncDto
import com.undef.gestionpedidos.data.remote.OrderSyncDto
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.LineaPedido
import com.undef.gestionpedidos.domain.model.Pedido
import com.undef.gestionpedidos.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class OrderRepository(
    private val orderDao: OrderDao,
    private val clientDao: ClientDao,
    private val productDao: ProductDao
) {
    fun getAllOrders(): Flow<List<Pedido>> {
        return orderDao.getAllOrders().map { entities ->
            entities.mapNotNull { entity -> mapEntityToDomain(entity) }
        }
    }

    suspend fun getOrderById(id: Int): Pedido? {
        val entity = orderDao.getOrderById(id) ?: return null
        return mapEntityToDomain(entity)
    }

    private suspend fun mapEntityToDomain(entity: OrderEntity): Pedido? {
        val clientEntity = clientDao.getClientById(entity.clientId) ?: return null
        val cliente = Cliente(clientEntity.id, clientEntity.razonSocial, clientEntity.cuit, clientEntity.direccion, clientEntity.localidad, clientEntity.telefono, clientEntity.email, clientEntity.activo)
        
        val lineEntities = orderDao.getLinesForOrder(entity.id)
        val lineas = lineEntities.mapNotNull { lineEnt ->
            val pEnt = productDao.getProductById(lineEnt.productId) ?: return@mapNotNull null
            val producto = Producto(pEnt.id, pEnt.codigo, pEnt.descripcion ?: "", pEnt.unidadMedida, pEnt.precioUnitario, pEnt.stockActual, pEnt.activo, pEnt.categoryId)
            LineaPedido(lineEnt.id, lineEnt.orderId, producto, lineEnt.cantidad, producto.precioUnitario)
        }
        
        val estado = try { EstadoPedido.valueOf(entity.estado) } catch(e: Exception) { EstadoPedido.BORRADOR }
        val fechaCreacion = try { LocalDate.parse(entity.fechaCreacion) } catch(e: Exception) { LocalDate.now() }
        val fechaEntregaEstimada = try { LocalDate.parse(entity.fechaEntregaEstimada) } catch(e: Exception) { LocalDate.now().plusDays(1) }

        return Pedido(entity.id, entity.numeroPedido, cliente, estado, fechaCreacion, fechaEntregaEstimada, lineas, entity.observaciones, entity.comprobanteUri)
    }

    suspend fun saveOrder(pedido: Pedido) {
        val entity = OrderEntity(
            numeroPedido = pedido.numeroPedido,
            clientId = pedido.cliente.id,
            fechaCreacion = pedido.fechaCreacion.toString(),
            fechaEntregaEstimada = pedido.fechaEntregaEstimada.toString(),
            estado = pedido.estado.name,
            observaciones = pedido.observaciones,
            comprobanteUri = pedido.comprobanteUri
        )
        val orderId = orderDao.insertOrder(entity).toInt()
        
        pedido.lineas.forEach { linea ->
            val lineEntity = OrderLineEntity(
                orderId = orderId,
                productId = linea.producto.id,
                cantidad = linea.cantidad,
                precioUnitario = linea.precioUnitario,
                subtotal = linea.subtotal
            )
            orderDao.insertOrderLine(lineEntity)
        }
    }
    
    suspend fun updateOrder(pedido: Pedido) {
        val entity = OrderEntity(
            id = pedido.id,
            numeroPedido = pedido.numeroPedido,
            clientId = pedido.cliente.id,
            fechaCreacion = pedido.fechaCreacion.toString(),
            fechaEntregaEstimada = pedido.fechaEntregaEstimada.toString(),
            estado = pedido.estado.name,
            observaciones = pedido.observaciones,
            comprobanteUri = pedido.comprobanteUri
        )
        orderDao.updateOrder(entity)
        
        // Sincronizar líneas: borramos las anteriores y guardamos las nuevas
        orderDao.deleteOrderLines(pedido.id)
        pedido.lineas.forEach { linea ->
            val lineEntity = OrderLineEntity(
                orderId = pedido.id,
                productId = linea.producto.id,
                cantidad = linea.cantidad,
                precioUnitario = linea.precioUnitario,
                subtotal = linea.subtotal
            )
            orderDao.insertOrderLine(lineEntity)
        }
    }

    suspend fun deleteOrder(id: Int) {
        orderDao.deleteOrderLines(id)
        orderDao.deleteOrder(id)
    }
    
    suspend fun syncOrdersToCloud(): Boolean {
        return try {
            val orders = getAllOrders().first()
            val dtos = orders.map { pedido ->
                val lines = orderDao.getLinesForOrder(pedido.id).map { line: com.undef.gestionpedidos.data.local.entity.OrderLineEntity ->
                    OrderLineSyncDto(
                        productId = line.productId,
                        cantidad = line.cantidad,
                        precioUnitario = line.precioUnitario,
                        subtotal = line.subtotal
                    )
                }
                OrderSyncDto(
                    orderId = pedido.id,
                    numeroPedido = pedido.numeroPedido,
                    clientId = pedido.cliente.id,
                    fechaCreacion = pedido.fechaCreacion.toString(),
                    fechaEntregaEstimada = pedido.fechaEntregaEstimada.toString(),
                    estado = pedido.estado.name,
                    observaciones = pedido.observaciones,
                    total = pedido.total,
                    lineas = lines
                )
            }
            val response = ServiceLocator.supabaseApiService.syncOrders(
                apiKey = ServiceLocator.SUPABASE_ANON_KEY,
                authorization = "Bearer ${ServiceLocator.SUPABASE_ANON_KEY}",
                orders = dtos
            )
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
