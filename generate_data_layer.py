import os

base_dir = r"c:\Users\frang\Documents\GitHub\GestionPedidos-Moviles\app\src\main\java\com\undef\gestionpedidos\data"
entity_dir = os.path.join(base_dir, "local", "entity")
dao_dir = os.path.join(base_dir, "local", "dao")
prefs_dir = os.path.join(base_dir, "local", "prefs")
remote_dir = os.path.join(base_dir, "remote")
repo_dir = os.path.join(base_dir, "repository")
di_dir = r"c:\Users\frang\Documents\GitHub\GestionPedidos-Moviles\app\src\main\java\com\undef\gestionpedidos\di"

os.makedirs(entity_dir, exist_ok=True)
os.makedirs(dao_dir, exist_ok=True)
os.makedirs(prefs_dir, exist_ok=True)
os.makedirs(remote_dir, exist_ok=True)
os.makedirs(repo_dir, exist_ok=True)
os.makedirs(di_dir, exist_ok=True)

# Entities
with open(os.path.join(entity_dir, "ClientEntity.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val razonSocial: String,
    val cuit: String,
    val direccion: String,
    val telefono: String,
    val email: String
)
""")

with open(os.path.join(entity_dir, "ProductEntity.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double
)
""")

with open(os.path.join(entity_dir, "OrderEntity.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientId: Int,
    val fecha: String,
    val estado: String
)
""")

with open(os.path.join(entity_dir, "OrderLineEntity.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_lines")
data class OrderLineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int,
    val productId: Int,
    val cantidad: Int,
    val subtotal: Double
)
""")

# DAOs
with open(os.path.join(dao_dir, "ClientDao.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.undef.gestionpedidos.data.local.entity.ClientEntity

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients")
    suspend fun getAllClients(): List<ClientEntity>

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getClientById(id: Int): ClientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity)
    
    @Query("DELETE FROM clients")
    suspend fun clearAll()
}
""")

with open(os.path.join(dao_dir, "ProductDao.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.undef.gestionpedidos.data.local.entity.ProductEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)
    
    @Query("DELETE FROM products")
    suspend fun clearAll()
}
""")

with open(os.path.join(dao_dir, "OrderDao.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.undef.gestionpedidos.data.local.entity.OrderEntity
import com.undef.gestionpedidos.data.local.entity.OrderLineEntity

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderLine(orderLine: OrderLineEntity)

    @Query("SELECT * FROM orders ORDER BY id DESC")
    suspend fun getAllOrders(): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Int): OrderEntity?

    @Query("SELECT * FROM order_lines WHERE orderId = :orderId")
    suspend fun getLinesForOrder(orderId: Int): List<OrderLineEntity>
    
    @Query("DELETE FROM orders")
    suspend fun clearAllOrders()
    
    @Query("DELETE FROM order_lines")
    suspend fun clearAllLines()
}
""")

# AppDatabase
with open(os.path.join(base_dir, "local", "AppDatabase.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.data.local

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
""")

print("Entities, DAOs, and Database generated successfully.")
