package com.undef.gestionpedidos.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PedidoResponseDto(
    @SerializedName("id")             val id: String,
    @SerializedName("clienteId")      val clienteId: Int,
    @SerializedName("clienteNombre")  val clienteNombre: String,
    @SerializedName("estado")         val estado: String,
    @SerializedName("observaciones")  val observaciones: String,
    @SerializedName("total")          val total: Double,
    @SerializedName("lineas")         val lineas: List<LineaRequestDto> = emptyList()
)
