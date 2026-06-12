package com.undef.gestionpedidos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.ui.navigation.AppNavHost
import com.undef.gestionpedidos.ui.theme.GestionPedidosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkMode by ServiceLocator.userPreferencesRepository.isDarkMode.collectAsState(initial = false)
            GestionPedidosTheme(darkTheme = isDarkMode) {
                AppNavHost()
            }
        }
    }
}
