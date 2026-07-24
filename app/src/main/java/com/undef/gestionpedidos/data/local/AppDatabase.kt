package com.undef.gestionpedidos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.undef.gestionpedidos.data.local.dao.CategoryDao
import com.undef.gestionpedidos.data.local.dao.ClientDao
import com.undef.gestionpedidos.data.local.dao.OrderDao
import com.undef.gestionpedidos.data.local.dao.ProductDao
import com.undef.gestionpedidos.data.local.dao.UserDao
import com.undef.gestionpedidos.data.local.entity.CategoryEntity
import com.undef.gestionpedidos.data.local.entity.ClientEntity
import com.undef.gestionpedidos.data.local.entity.OrderEntity
import com.undef.gestionpedidos.data.local.entity.OrderLineEntity
import com.undef.gestionpedidos.data.local.entity.ProductEntity
import com.undef.gestionpedidos.data.local.entity.UserEntity

@Database(
    entities = [ClientEntity::class, ProductEntity::class, OrderEntity::class, OrderLineEntity::class, CategoryEntity::class, UserEntity::class],
    version = 7,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
}
