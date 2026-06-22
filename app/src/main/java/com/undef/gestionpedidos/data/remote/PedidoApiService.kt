package com.undef.gestionpedidos.data.remote

import com.undef.gestionpedidos.data.remote.dto.PedidoRequestDto
import com.undef.gestionpedidos.data.remote.dto.PedidoResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PedidoApiService {
    /**
     * Envía un pedido nuevo al servidor.
     * El servidor responde con el mismo objeto más el campo "id" generado.
     */
    @POST("api/v1/orders")
    suspend fun crearPedido(@Body pedido: PedidoRequestDto): Response<PedidoResponseDto>
}
