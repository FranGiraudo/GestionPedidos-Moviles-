package com.distribuidora.gestionpedidos.domain.model

data class Producto(
    val id: Int,
    val codigo: String,
    val descripcion: String,
    val unidadMedida: String,
    val precioUnitario: Double,
    val stockActual: Int,
    val activo: Boolean = true
)