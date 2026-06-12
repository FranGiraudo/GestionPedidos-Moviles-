package com.undef.gestionpedidos.domain.model

import java.time.LocalDate

data class Pedido(
    val id: Int,
    val numeroPedido: String,
    val cliente: Cliente,
    val estado: EstadoPedido,
    val fechaCreacion: LocalDate,
    val fechaEntregaEstimada: LocalDate,
    val lineas: List<LineaPedido>,
    val observaciones: String = "",
    val comprobanteUri: String? = null
) {
    val total: Double
        get() = lineas.sumOf { it.subtotal }
}
