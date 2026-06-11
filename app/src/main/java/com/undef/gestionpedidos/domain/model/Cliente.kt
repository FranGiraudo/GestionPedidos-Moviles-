package com.undef.gestionpedidos.domain.model

data class Cliente(
    val id: Int,
    val razonSocial: String,
    val cuit: String,
    val direccion: String,
    val localidad: String,
    val telefono: String,
    val email: String,
    val activo: Boolean = true
)
