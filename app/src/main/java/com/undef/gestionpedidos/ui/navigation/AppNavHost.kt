package com.undef.gestionpedidos.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.undef.gestionpedidos.ui.feature.auth.LoginScreen
import com.undef.gestionpedidos.ui.feature.auth.RegisterScreen
import com.undef.gestionpedidos.ui.feature.auth.SplashScreen
import com.undef.gestionpedidos.ui.feature.clients.ClientsScreen
import com.undef.gestionpedidos.ui.feature.dashboard.DashboardScreen
import com.undef.gestionpedidos.ui.feature.newclient.NewClientScreen
import com.undef.gestionpedidos.ui.feature.neworder.NewOrderScreen
import com.undef.gestionpedidos.ui.feature.orderdetail.OrderDetailScreen
import com.undef.gestionpedidos.ui.feature.orders.OrdersScreen
import com.undef.gestionpedidos.ui.feature.profile.ProfileScreen
import com.undef.gestionpedidos.ui.feature.settings.SettingsScreen
import com.undef.gestionpedidos.ui.feature.statistics.StatisticsScreen
import com.undef.gestionpedidos.ui.theme.BottomNavBg
import com.undef.gestionpedidos.ui.theme.BottomNavIconActive
import com.undef.gestionpedidos.ui.theme.BottomNavSelected

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
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    color = BottomNavBg,
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        bottomNavItems.forEach { (route, title, icon) ->
                            val isSelected = currentRoute == route
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            ) {
                                Surface(
                                    color = if (isSelected) BottomNavSelected else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        imageVector = icon, 
                                        contentDescription = title,
                                        tint = if (isSelected) BottomNavIconActive else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                                    )
                                }
                                Text(
                                    text = title, 
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) BottomNavIconActive else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            
            composable(AppDestination.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(AppDestination.Login.route) {
                            popUpTo(AppDestination.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppDestination.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(AppDestination.Dashboard.route) {
                            popUpTo(AppDestination.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(AppDestination.Register.route)
                    }
                )
            }

            composable(AppDestination.Register.route) {
                RegisterScreen(
                    onNavigateBack = { navController.popBackStack() }
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
