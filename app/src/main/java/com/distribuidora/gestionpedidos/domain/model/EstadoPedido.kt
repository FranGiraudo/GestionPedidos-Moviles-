package com.distribuidora.gestionpedidos.domain.model

enum class EstadoPedido(val etiqueta: String) {
    BORRADOR("Borrador"),
    CONFIRMADO("Confirmado"),
    EN_PREPARACION("En preparacion"),
    DESPACHADO("Despachado"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado")
}