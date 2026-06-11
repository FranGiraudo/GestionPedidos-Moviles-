package com.undef.gestionpedidos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.undef.gestionpedidos.data.local.dao.ClientDao
import com.undef.gestionpedidos.data.local.dao.OrderDao
import com.undef.gestionpedidos.data.local.dao.ProductDao
import com.undef.gestionpedidos.data.local.entity.ClientEntity
import com.undef.gestionpedidos.data.local.entity.OrderEntity
import com.undef.gestionpedidos.data.local.entity.OrderLineEntity
import com.undef.gestionpedidos.data.local.entity.ProductEntity

@Database(
    entities = [ClientEntity::class, ProductEntity::class, OrderEntity::class, OrderLineEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
}
