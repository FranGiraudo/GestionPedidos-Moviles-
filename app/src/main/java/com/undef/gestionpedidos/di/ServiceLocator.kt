package com.undef.gestionpedidos.di

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
