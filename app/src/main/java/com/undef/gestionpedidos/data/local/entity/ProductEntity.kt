package com.undef.gestionpedidos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String,
    val descripcion: String,
    val unidadMedida: String,
    val precioUnitario: Double,
    val stockActual: Int,
    val activo: Boolean = true
)
