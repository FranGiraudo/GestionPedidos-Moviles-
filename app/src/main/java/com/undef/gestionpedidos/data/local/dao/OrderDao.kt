package com.undef.gestionpedidos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.undef.gestionpedidos.data.local.entity.OrderEntity
import com.undef.gestionpedidos.data.local.entity.OrderLineEntity

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderLine(orderLine: OrderLineEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity): Int

    @Query("SELECT * FROM orders ORDER BY id DESC")
    suspend fun getAllOrders(): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Int): OrderEntity?

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrder(id: Int): Int

    @Query("DELETE FROM order_lines WHERE orderId = :orderId")
    suspend fun deleteOrderLines(orderId: Int): Int

    @Query("SELECT * FROM order_lines WHERE orderId = :orderId")
    suspend fun getLinesForOrder(orderId: Int): List<OrderLineEntity>
    
    @Query("DELETE FROM orders")
    suspend fun clearOrders(): Int
    
    @Query("DELETE FROM order_lines")
    suspend fun clearOrderLines(): Int
}
