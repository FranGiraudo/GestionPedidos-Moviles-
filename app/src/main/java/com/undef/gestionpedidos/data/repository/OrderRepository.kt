package com.undef.gestionpedidos.data.repository

import com.undef.gestionpedidos.data.local.dao.ClientDao
import com.undef.gestionpedidos.data.local.dao.OrderDao
import com.undef.gestionpedidos.data.local.dao.ProductDao
import com.undef.gestionpedidos.data.local.entity.OrderEntity
import com.undef.gestionpedidos.data.local.entity.OrderLineEntity
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.LineaPedido
import com.undef.gestionpedidos.domain.model.Pedido
import com.undef.gestionpedidos.domain.model.Producto
import kotlinx.coroutines.delay
import java.time.LocalDate

class OrderRepository(
    private val orderDao: OrderDao,
    private val clientDao: ClientDao,
    private val productDao: ProductDao
) {
    suspend fun getAllOrders(): List<Pedido> {
        val orders = orderDao.getAllOrders()
        return orders.mapNotNull { entity ->
            mapEntityToDomain(entity)
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
            val producto = Producto(pEnt.id, pEnt.codigo, pEnt.descripcion, pEnt.unidadMedida, pEnt.precioUnitario, pEnt.stockActual, pEnt.activo)
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
        delay(2000)
        return true
    }
}
