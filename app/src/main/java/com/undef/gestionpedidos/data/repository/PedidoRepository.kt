package com.undef.gestionpedidos.data.repository

import android.content.Context
import com.undef.gestionpedidos.data.local.AppDatabase
import com.undef.gestionpedidos.data.local.entity.OrderEntity
import com.undef.gestionpedidos.data.remote.RetrofitClient
import com.undef.gestionpedidos.data.remote.dto.LineaRequestDto
import com.undef.gestionpedidos.data.remote.dto.PedidoRequestDto
import com.undef.gestionpedidos.data.remote.dto.PedidoResponseDto
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.LineaPedido
import com.undef.gestionpedidos.domain.model.Pedido
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class PedidoRepository(context: Context) {

    private val api = RetrofitClient.pedidoApiService
    private val dao = AppDatabase.getDatabase(context).orderDao()

    fun getRecentOrdersFlow(): Flow<List<Pedido>> {
        return dao.getRecentOrders().map { entities ->
            entities.map { entity ->
                Pedido(
                    id = entity.id,
                    numeroPedido = entity.numeroPedido,
                    cliente = Cliente(
                        id = entity.clienteId,
                        razonSocial = entity.clienteNombre,
                        cuit = "",
                        direccion = "",
                        localidad = "",
                        telefono = "",
                        email = ""
                    ),
                    estado = EstadoPedido.valueOf(entity.estado),
                    fechaCreacion = LocalDate.now(), // Simplified
                    fechaEntregaEstimada = LocalDate.now().plusDays(3),
                    observaciones = "",
                    lineas = emptyList() // Simplified
                )
            }
        }
    }

    /**
     * Convierte los datos del dominio a un DTO y realiza el POST al servidor.
     * Devuelve [Result.success] con el objeto creado o [Result.failure] con la excepción.
     */
    suspend fun enviarPedido(
        cliente: Cliente,
        lineas: List<LineaPedido>,
        observaciones: String,
        total: Double
    ): Result<PedidoResponseDto> {
        return try {
            val dto = PedidoRequestDto(
                clienteId     = cliente.id,
                clienteNombre = cliente.razonSocial,
                estado        = "CONFIRMADO",
                observaciones = observaciones,
                total         = total,
                lineas        = lineas.map { linea ->
                    LineaRequestDto(
                        productoId     = linea.producto.id,
                        productoCodigo = linea.producto.codigo,
                        productoNombre = linea.producto.descripcion,
                        cantidad       = linea.cantidad,
                        precioUnitario = linea.precioUnitario,
                        subtotal       = linea.subtotal
                    )
                }
            )

            val response = api.crearPedido(dto)

            if (response.isSuccessful && response.body() != null) {
                val resDto = response.body()!!
                // Insert into Room
                val entity = OrderEntity(
                    numeroPedido = "PED-${resDto.id}",
                    clienteId = resDto.clienteId,
                    clienteNombre = resDto.clienteNombre,
                    estado = resDto.estado,
                    fechaCreacion = System.currentTimeMillis(),
                    total = resDto.total
                )
                dao.insertOrder(entity)
                
                Result.success(resDto)
            } else {
                Result.failure(Exception("Error del servidor: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
