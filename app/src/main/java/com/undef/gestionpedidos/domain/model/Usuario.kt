package com.undef.gestionpedidos.domain.model

data class Usuario(
    val id: Int,
    val email: String,
    val fullName: String,
    val phone: String? = null,
    val role: String = "operador",
    val isActive: Boolean = true
)
