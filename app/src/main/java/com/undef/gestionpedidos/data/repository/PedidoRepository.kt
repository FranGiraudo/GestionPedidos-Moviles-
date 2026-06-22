package com.undef.gestionpedidos.data.repository

import com.undef.gestionpedidos.data.remote.RetrofitClient
import com.undef.gestionpedidos.data.remote.dto.LineaRequestDto
import com.undef.gestionpedidos.data.remote.dto.PedidoRequestDto
import com.undef.gestionpedidos.data.remote.dto.PedidoResponseDto
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.LineaPedido

class PedidoRepository {

    private val api = RetrofitClient.pedidoApiService

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
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error del servidor: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
