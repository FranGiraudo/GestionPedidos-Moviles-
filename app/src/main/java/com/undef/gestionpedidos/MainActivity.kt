package com.undef.gestionpedidos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.ui.navigation.AppNavHost
import com.undef.gestionpedidos.ui.theme.GestionPedidosTheme
import com.undef.gestionpedidos.worker.SyncWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar WorkManager para sincronización
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SyncOrdersWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        setContent {
            val isDarkMode by ServiceLocator.userPreferencesRepository.isDarkMode.collectAsState(initial = false)
            GestionPedidosTheme(darkTheme = isDarkMode) {
                AppNavHost()
            }
        }
    }
}
