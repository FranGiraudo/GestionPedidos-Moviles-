package com.undef.gestionpedidos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val razonSocial: String,
    val cuit: String,
    val direccion: String,
    val localidad: String,
    val telefono: String,
    val email: String,
    val activo: Boolean = true
)
