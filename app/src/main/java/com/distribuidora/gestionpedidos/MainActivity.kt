package com.distribuidora.gestionpedidos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.distribuidora.gestionpedidos.ui.navigation.AppNavHost
import com.distribuidora.gestionpedidos.ui.theme.GestionPedidosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestionPedidosTheme {
                AppNavHost()
            }
        }
    }
}