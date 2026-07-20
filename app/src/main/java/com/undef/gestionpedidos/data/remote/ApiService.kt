package com.undef.gestionpedidos.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
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

data class CategoryDto(
    val id: Long,
    val nombre: String
)

interface ApiService {
    @GET("https://dolarapi.com/v1/dolares/blue")
    suspend fun getDolarBlue(): DolarResponse

    @GET("rest/v1/categories?select=*")
    suspend fun getCategories(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String
    ): List<CategoryDto>

    /**
     * POST a Supabase: sincroniza pedidos locales a la tabla order_syncs.
     * Requiere header Prefer: return=minimal para que Supabase devuelva 204 sin body.
     */
    @POST("rest/v1/order_syncs")
    suspend fun syncOrders(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Header("Prefer") prefer: String = "return=minimal",
        @Body orders: List<OrderSyncDto>
    ): Response<Unit>
}
