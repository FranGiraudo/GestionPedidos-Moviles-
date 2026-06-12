import os

base_dir = r"c:\Users\frang\Documents\GitHub\GestionPedidos-Moviles\app\src\main\java\com\undef\gestionpedidos\data"
prefs_dir = os.path.join(base_dir, "local", "prefs")
remote_dir = os.path.join(base_dir, "remote")
repo_dir = os.path.join(base_dir, "repository")
di_dir = r"c:\Users\frang\Documents\GitHub\GestionPedidos-Moviles\app\src\main\java\com\undef\gestionpedidos\di"
app_dir = r"c:\Users\frang\Documents\GitHub\GestionPedidos-Moviles\app\src\main\java\com\undef\gestionpedidos"

os.makedirs(prefs_dir, exist_ok=True)
os.makedirs(remote_dir, exist_ok=True)
os.makedirs(repo_dir, exist_ok=True)
os.makedirs(di_dir, exist_ok=True)

# UserPreferencesRepository
with open(os.path.join(prefs_dir, "UserPreferencesRepository.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.data.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN] ?: false
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_EMAIL]
    }

    suspend fun saveLoginSession(email: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[USER_EMAIL] = email
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
""")

# ApiService
with open(os.path.join(remote_dir, "ApiService.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.data.remote

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
""")

# Repositories
with open(os.path.join(repo_dir, "Repositories.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.data.repository

import com.undef.gestionpedidos.data.local.dao.ClientDao
import com.undef.gestionpedidos.data.local.dao.ProductDao
import com.undef.gestionpedidos.data.local.entity.ClientEntity
import com.undef.gestionpedidos.data.local.entity.ProductEntity
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.Producto
import com.undef.gestionpedidos.data.remote.ApiService
import kotlinx.coroutines.delay

class ClientRepository(private val clientDao: ClientDao, private val apiService: ApiService) {
    suspend fun getAllClients(): List<Cliente> {
        return clientDao.getAllClients().map {
            Cliente(it.id, it.razonSocial, it.cuit, it.direccion, it.telefono, it.email)
        }
    }

    suspend fun getClientById(id: Int): Cliente? {
        val it = clientDao.getClientById(id) ?: return null
        return Cliente(it.id, it.razonSocial, it.cuit, it.direccion, it.telefono, it.email)
    }

    suspend fun addClient(cliente: Cliente) {
        val entity = ClientEntity(
            razonSocial = cliente.razonSocial,
            cuit = cliente.cuit,
            direccion = cliente.direccion,
            telefono = cliente.telefono,
            email = cliente.email
        )
        clientDao.insertClient(entity)
    }

    // Mock API call since AFIP public endpoints require auth
    suspend fun fetchCuitData(cuit: String): String {
        delay(1000) // simulate network delay
        if(cuit.startsWith("20") || cuit.startsWith("23") || cuit.startsWith("27")) {
            return "Consumidor Final Mock"
        } else if (cuit.startsWith("30") || cuit.startsWith("33")) {
            return "Empresa S.A. Mock"
        }
        return "Razón Social Desconocida"
    }
}

class ProductRepository(private val productDao: ProductDao) {
    suspend fun getAllProducts(): List<Producto> {
        return productDao.getAllProducts().map {
            Producto(it.id, it.codigo, it.nombre, it.descripcion, it.precio)
        }
    }

    suspend fun getProductById(id: Int): Producto? {
        val it = productDao.getProductById(id) ?: return null
        return Producto(it.id, it.codigo, it.nombre, it.descripcion, it.precio)
    }

    suspend fun addProduct(producto: Producto) {
        val entity = ProductEntity(
            codigo = producto.codigo,
            nombre = producto.nombre,
            descripcion = producto.descripcion,
            precio = producto.precio
        )
        productDao.insertProduct(entity)
    }
}

class FinanceRepository(private val apiService: ApiService) {
    suspend fun getDolarBlue(): Double {
        return try {
            val response = apiService.getDolarBlue()
            response.venta
        } catch (e: Exception) {
            1200.0 // Fallback if no internet
        }
    }
}
""")

with open(os.path.join(repo_dir, "OrderRepository.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.data.repository

import com.undef.gestionpedidos.data.local.dao.ClientDao
import com.undef.gestionpedidos.data.local.dao.OrderDao
import com.undef.gestionpedidos.data.local.dao.ProductDao
import com.undef.gestionpedidos.data.local.entity.OrderEntity
import com.undef.gestionpedidos.data.local.entity.OrderLineEntity
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.LineaProducto
import com.undef.gestionpedidos.domain.model.Pedido
import com.undef.gestionpedidos.domain.model.Producto
import kotlinx.coroutines.delay

class OrderRepository(
    private val orderDao: OrderDao,
    private val clientDao: ClientDao,
    private val productDao: ProductDao
) {
    suspend fun getAllOrders(): List<Pedido> {
        val orders = orderDao.getAllOrders()
        return orders.mapNotNull { entity ->
            mapEntityToDomain(entity)
        }
    }

    suspend fun getOrderById(id: Int): Pedido? {
        val entity = orderDao.getOrderById(id) ?: return null
        return mapEntityToDomain(entity)
    }

    private suspend fun mapEntityToDomain(entity: OrderEntity): Pedido? {
        val clientEntity = clientDao.getClientById(entity.clientId) ?: return null
        val cliente = Cliente(clientEntity.id, clientEntity.razonSocial, clientEntity.cuit, clientEntity.direccion, clientEntity.telefono, clientEntity.email)
        
        val lineEntities = orderDao.getLinesForOrder(entity.id)
        val lineas = lineEntities.mapNotNull { lineEnt ->
            val pEnt = productDao.getProductById(lineEnt.productId) ?: return@mapNotNull null
            val producto = Producto(pEnt.id, pEnt.codigo, pEnt.nombre, pEnt.descripcion, pEnt.precio)
            LineaProducto(producto, lineEnt.cantidad, lineEnt.subtotal)
        }
        
        val estado = try { EstadoPedido.valueOf(entity.estado) } catch(e: Exception) { EstadoPedido.PENDIENTE }
        return Pedido(entity.id, cliente, entity.fecha, estado, lineas)
    }

    suspend fun saveOrder(pedido: Pedido) {
        val entity = OrderEntity(
            clientId = pedido.cliente.id,
            fecha = pedido.fecha,
            estado = pedido.estado.name
        )
        val orderId = orderDao.insertOrder(entity).toInt()
        
        pedido.lineas.forEach { linea ->
            val lineEntity = OrderLineEntity(
                orderId = orderId,
                productId = linea.producto.id,
                cantidad = linea.cantidad,
                subtotal = linea.subtotal
            )
            orderDao.insertOrderLine(lineEntity)
        }
    }
    
    suspend fun syncOrdersToCloud(): Boolean {
        delay(2000) // Simulamos la carga a internet
        return true
    }
}
""")

# ServiceLocator
with open(os.path.join(di_dir, "ServiceLocator.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos.di

import android.content.Context
import androidx.room.Room
import com.undef.gestionpedidos.data.local.AppDatabase
import com.undef.gestionpedidos.data.local.prefs.UserPreferencesRepository
import com.undef.gestionpedidos.data.remote.ApiService
import com.undef.gestionpedidos.data.repository.ClientRepository
import com.undef.gestionpedidos.data.repository.FinanceRepository
import com.undef.gestionpedidos.data.repository.OrderRepository
import com.undef.gestionpedidos.data.repository.ProductRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {
    private var database: AppDatabase? = null
    private var apiService: ApiService? = null
    
    lateinit var userPreferencesRepository: UserPreferencesRepository
    lateinit var clientRepository: ClientRepository
    lateinit var productRepository: ProductRepository
    lateinit var orderRepository: OrderRepository
    lateinit var financeRepository: FinanceRepository

    fun init(context: Context) {
        // Init Room
        database = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "gestion_pedidos_db"
        ).build()

        // Init Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dolarapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // Init DataStore
        userPreferencesRepository = UserPreferencesRepository(context)

        // Init Repositories
        val db = database!!
        clientRepository = ClientRepository(db.clientDao(), apiService!!)
        productRepository = ProductRepository(db.productDao())
        orderRepository = OrderRepository(db.orderDao(), db.clientDao(), db.productDao())
        financeRepository = FinanceRepository(apiService!!)
    }
}
""")

# Application
with open(os.path.join(app_dir, "GestionPedidosApplication.kt"), "w") as f:
    f.write("""package com.undef.gestionpedidos

import android.app.Application
import com.undef.gestionpedidos.di.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.Producto

class GestionPedidosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
        
        // Carga inicial de datos mock para testing si está vacío
        CoroutineScope(Dispatchers.IO).launch {
            if (ServiceLocator.clientRepository.getAllClients().isEmpty()) {
                ServiceLocator.clientRepository.addClient(Cliente(1, "Supermercado A", "30-11111111-1", "Av. Siempre Viva 123", "555-1234", "a@super.com"))
                ServiceLocator.clientRepository.addClient(Cliente(2, "Almacén B", "20-22222222-2", "Calle Falsa 456", "555-5678", "b@almacen.com"))
            }
            if (ServiceLocator.productRepository.getAllProducts().isEmpty()) {
                ServiceLocator.productRepository.addProduct(Producto(1, "P001", "Coca Cola 2L", "Gaseosa", 2500.0))
                ServiceLocator.productRepository.addProduct(Producto(2, "P002", "Alfajor Guaymallen", "Golosina", 500.0))
                ServiceLocator.productRepository.addProduct(Producto(3, "P003", "Yerba Playadito 1Kg", "Yerba", 4000.0))
            }
        }
    }
}
""")

print("Layer 2 generated successfully.")
