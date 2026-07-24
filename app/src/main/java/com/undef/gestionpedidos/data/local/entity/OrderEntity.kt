package com.undef.gestionpedidos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["clientId"])
    ]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val numeroPedido: String,
    val clientId: Int,
    val fechaCreacion: String,
    val fechaEntregaEstimada: String,
    val estado: String,
    val observaciones: String,
    val comprobanteUri: String? = null
)
