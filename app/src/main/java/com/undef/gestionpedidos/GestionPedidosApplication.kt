package com.undef.gestionpedidos

import android.app.Application
import android.util.Log
import com.undef.gestionpedidos.di.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.Producto

class GestionPedidosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)

        CoroutineScope(Dispatchers.IO).launch {
            if (ServiceLocator.clientRepository.getAllClients().first().isEmpty()) {
                ServiceLocator.clientRepository.addClient(Cliente(1, "Supermercado A", "30-11111111-1", "Av. Siempre Viva 123", "Capital Federal", "555-1234", "a@super.com", true))
                ServiceLocator.clientRepository.addClient(Cliente(2, "Almacen B", "20-22222222-2", "Calle Falsa 456", "Gran Buenos Aires", "555-5678", "b@almacen.com", true))
            }
            if (ServiceLocator.productRepository.getAllProducts().first().isEmpty()) {
                ServiceLocator.productRepository.addProduct(Producto(1, "P001", "Coca Cola 2L", "Unidades", 2500.0, 100, true))
                ServiceLocator.productRepository.addProduct(Producto(2, "P002", "Alfajor Guaymallen", "Unidades", 500.0, 50, true))
                ServiceLocator.productRepository.addProduct(Producto(3, "P003", "Yerba Playadito 1Kg", "Kilos", 4000.0, 30, true))
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resultado = ServiceLocator.supabaseApiService.getCategories(
                    apiKey = ServiceLocator.SUPABASE_ANON_KEY,
                    authorization = "Bearer ${ServiceLocator.SUPABASE_ANON_KEY}"
                )
                Log.d("SupabaseTest", "Categories: $resultado")
            } catch (e: Exception) {
                Log.e("SupabaseTest", "Error al conectar con Supabase", e)
            }
        }
    }
}
