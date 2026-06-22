package com.undef.gestionpedidos.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LineaRequestDto(
    @SerializedName("productoId")     val productoId: Int,
    @SerializedName("productoCodigo") val productoCodigo: String,
    @SerializedName("productoNombre") val productoNombre: String,
    @SerializedName("cantidad")       val cantidad: Int,
    @SerializedName("precioUnitario") val precioUnitario: Double,
    @SerializedName("subtotal")       val subtotal: Double
)

data class PedidoRequestDto(
    @SerializedName("clienteId")      val clienteId: Int,
    @SerializedName("clienteNombre")  val clienteNombre: String,
    @SerializedName("estado")         val estado: String,
    @SerializedName("observaciones")  val observaciones: String,
    @SerializedName("total")          val total: Double,
    @SerializedName("lineas")         val lineas: List<LineaRequestDto>
)
