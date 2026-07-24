package com.undef.gestionpedidos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.undef.gestionpedidos.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long
    
    @androidx.room.Update
    suspend fun updateProduct(product: ProductEntity): Int

    @Query("UPDATE products SET activo = 0 WHERE id = :id")
    suspend fun deactivateProduct(id: Int): Int

    @Query("UPDATE products SET activo = 1 WHERE id = :id")
    suspend fun activateProduct(id: Int): Int
    
    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: Int): Int
    
    @Query("DELETE FROM products")
    suspend fun clearProducts(): Int
}
