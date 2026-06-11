package com.undef.gestionpedidos.data.remote

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body

data class DolarResponse(
    val moneda: String,
    val casa: String,
    val nombre: String,
    val compra: Double,
    val venta: Double,
    val fechaActualizacion: String
)

data class CuitResponse(
    val cuit: String,
    val razonSocial: String
)

data class SyncResponse(
    val status: String,
    val message: String
)

interface ApiService {
    @GET("https://dolarapi.com/v1/dolares/blue")
    suspend fun getDolarBlue(): DolarResponse
}
