package com.undef.gestionpedidos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.undef.gestionpedidos.data.local.entity.ClientEntity

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients")
    suspend fun getAllClients(): List<ClientEntity>

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getClientById(id: Int): ClientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity): Long

    @Update
    suspend fun updateClient(client: ClientEntity): Int
    
    @Query("DELETE FROM clients")
    suspend fun clearClients(): Int
}
