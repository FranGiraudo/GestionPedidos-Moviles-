package com.undef.gestionpedidos.data.remote

import com.google.gson.annotations.SerializedName

/**
 * DTO para sincronización de pedidos contra Supabase.
 *
 * SQL para crear la tabla en Supabase (ejecutar en el SQL Editor de Supabase):
 *
 * CREATE TABLE order_syncs (
 *     id              BIGSERIAL PRIMARY KEY,
 *     order_id        INTEGER NOT NULL,
 *     numero_pedido   TEXT NOT NULL,
 *     client_id       INTEGER NOT NULL,
 *     fecha_creacion  TEXT NOT NULL,
 *     fecha_entrega   TEXT NOT NULL,
 *     estado          TEXT NOT NULL,
 *     observaciones   TEXT,
 *     total           DOUBLE PRECISION NOT NULL,
 *     lineas          JSONB,
 *     synced_at       TIMESTAMPTZ DEFAULT now()
 * );
 *
 * -- Habilitar acceso vía anon key (Row Level Security):
 * ALTER TABLE order_syncs ENABLE ROW LEVEL SECURITY;
 * CREATE POLICY "allow_insert" ON order_syncs FOR INSERT WITH CHECK (true);
 */
data class OrderSyncDto(
    @SerializedName("order_id")       val orderId: Int,
    @SerializedName("numero_pedido")  val numeroPedido: String,
    @SerializedName("client_id")      val clientId: Int,
    @SerializedName("fecha_creacion") val fechaCreacion: String,
    @SerializedName("fecha_entrega")  val fechaEntregaEstimada: String,
    @SerializedName("estado")         val estado: String,
    @SerializedName("observaciones")  val observaciones: String,
    @SerializedName("total")          val total: Double,
    @SerializedName("lineas")         val lineas: List<OrderLineSyncDto>
)

data class OrderLineSyncDto(
    @SerializedName("product_id")       val productId: Int,
    @SerializedName("cantidad")         val cantidad: Int,
    @SerializedName("precio_unitario")  val precioUnitario: Double,
    @SerializedName("subtotal")         val subtotal: Double
)
