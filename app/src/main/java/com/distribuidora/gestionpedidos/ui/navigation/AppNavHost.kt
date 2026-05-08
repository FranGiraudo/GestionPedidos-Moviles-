package com.distribuidora.gestionpedidos.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.distribuidora.gestionpedidos.ui.feature.auth.LoginScreen
import com.distribuidora.gestionpedidos.ui.feature.clients.ClientsScreen
import com.distribuidora.gestionpedidos.ui.feature.dashboard.DashboardScreen
import com.distribuidora.gestionpedidos.ui.feature.newclient.NewClientScreen
import com.distribuidora.gestionpedidos.ui.feature.neworder.NewOrderScreen
import com.distribuidora.gestionpedidos.ui.feature.orderdetail.OrderDetailScreen
import com.distribuidora.gestionpedidos.ui.feature.orders.OrdersScreen
import com.distribuidora.gestionpedidos.ui.feature.profile.ProfileScreen
import com.distribuidora.gestionpedidos.ui.feature.settings.SettingsScreen
import com.distribuidora.gestionpedidos.ui.feature.statistics.StatisticsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Triple(AppDestination.Dashboard.route, "Inicio", Icons.Default.Home),
        Triple(AppDestination.Orders.route, "Pedidos", Icons.Default.List),
        Triple(AppDestination.Clients.route, "Clientes", Icons.Default.Face),
        Triple(AppDestination.Statistics.route, "Datos", Icons.Default.Star)
    )

    val showBottomBar = bottomNavItems.any { it.first == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { (route, title, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = title) },
                            label = { Text(title) },
                            selected = currentRoute == route,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(AppDestination.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(AppDestination.Dashboard.route) {
                            popUpTo(AppDestination.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppDestination.Dashboard.route) {
                DashboardScreen(
                    onNavigateToNewOrder = { navController.navigate(AppDestination.NewOrder.route) },
                    onNavigateToNewClient = { navController.navigate(AppDestination.NewClient.route) },
                    onNavigateToOrderDetail = { orderId -> navController.navigate(AppDestination.OrderDetail.createRoute(orderId)) },
                    onNavigateToProfile = { navController.navigate(AppDestination.Profile.route) }
                )
            }

            composable(AppDestination.Orders.route) {
                OrdersScreen(
                    onNavigateToNewOrder = { navController.navigate(AppDestination.NewOrder.route) },
                    onNavigateToOrderDetail = { orderId -> navController.navigate(AppDestination.OrderDetail.createRoute(orderId)) }
                )
            }

            composable(AppDestination.Clients.route) {
                ClientsScreen(
                    onNavigateToNewClient = { navController.navigate(AppDestination.NewClient.route) }
                )
            }

            composable(AppDestination.Statistics.route) {
                StatisticsScreen()
            }

            composable(AppDestination.NewOrder.route) {
                NewOrderScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(AppDestination.NewClient.route) {
                NewClientScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(AppDestination.Profile.route) {
                ProfileScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSettings = { navController.navigate(AppDestination.Settings.route) },
                    onLogout = {
                        navController.navigate(AppDestination.Login.route) {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable(AppDestination.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = AppDestination.OrderDetail.route,
                arguments = listOf(navArgument("orderId") { type = NavType.IntType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getInt("orderId") ?: return@composable
                OrderDetailScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}