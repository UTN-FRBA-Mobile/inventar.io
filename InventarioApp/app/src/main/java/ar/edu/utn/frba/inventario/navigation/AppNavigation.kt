package ar.edu.utn.frba.inventario.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.inventario.screens.Home
import ar.edu.utn.frba.inventario.screens.Login

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            Login(navController = navController)
        }
        composable("home") {
            Home()
        }
    }
}