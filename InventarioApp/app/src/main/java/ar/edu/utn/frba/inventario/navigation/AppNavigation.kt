package ar.edu.utn.frba.inventario.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.inventario.screens.Home
import ar.edu.utn.frba.inventario.screens.Login
import ar.edu.utn.frba.inventario.screens.Orders
import ar.edu.utn.frba.inventario.screens.Scan
import ar.edu.utn.frba.inventario.screens.User
import ar.edu.utn.frba.inventario.utils.Screen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            Login(navController = navController)
        }
        composable(Screen.Home.route) {
            Home(navController = navController)
        }
        composable(Screen.Orders.route) {
            Orders(navController = navController)
        }
        composable(Screen.User.route) {
            User(navController = navController)
        }
        composable(Screen.Scan.route) {
            Scan(navController = navController)
        }
    }
}