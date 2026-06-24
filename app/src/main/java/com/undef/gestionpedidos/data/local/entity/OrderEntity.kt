package com.undef.gestionpedidos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val numeroPedido: String,
    val clienteId: Int,
    val clienteNombre: String,
    val estado: String,
    val fechaCreacion: Long, // Almacenado como epoch days o millis
    val total: Double
)
