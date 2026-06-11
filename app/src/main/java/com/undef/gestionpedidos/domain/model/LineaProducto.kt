package com.undef.gestionpedidos.domain.model

data class LineaPedido(
    val id: Int,
    val pedidoId: Int,
    val producto: Producto,
    val cantidad: Int,
    val precioUnitario: Double
) {
    val subtotal: Double
        get() = cantidad * precioUnitario
}
