package com.undef.gestionpedidos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val numeroPedido: String,
    val clientId: Int,
    val fechaCreacion: String,
    val fechaEntregaEstimada: String,
    val estado: String,
    val observaciones: String
)
