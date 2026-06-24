package com.undef.gestionpedidos.ui.navigation

sealed class AppDestination(val route: String) {
    object Splash : AppDestination("splash")
    object Login : AppDestination("login")
    object Register : AppDestination("register")
    object Dashboard : AppDestination("dashboard")
    object Orders : AppDestination("orders")
    object Clients : AppDestination("clients")
    object Statistics : AppDestination("statistics")
    object NewOrder : AppDestination("new_order")
    object NewClient : AppDestination("new_client")
    object Profile : AppDestination("profile")
    object Settings : AppDestination("settings")
    object OrderDetail : AppDestination("order_detail/{orderId}") {
        fun createRoute(orderId: Int) = "order_detail/$orderId"
    }
    object NewProduct : AppDestination("new_product")
    object EditClient : AppDestination("edit_client/{clientId}") {
        fun createRoute(clientId: Int) = "edit_client/$clientId"
    }
}
