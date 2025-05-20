package ar.edu.utn.frba.inventario.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.inventario.screens.Home
import ar.edu.utn.frba.inventario.screens.LoginScreen
import ar.edu.utn.frba.inventario.screens.Orders
import ar.edu.utn.frba.inventario.screens.User

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "loginScreen") {
        composable("loginScreen") {
            LoginScreen(navController = navController)
        }
        composable("home") {
            Home(navController = navController)
        }
        composable("orders") {
            Orders(navController = navController)
        }
        composable("user") {
            User(navController = navController)
        }
    }
}