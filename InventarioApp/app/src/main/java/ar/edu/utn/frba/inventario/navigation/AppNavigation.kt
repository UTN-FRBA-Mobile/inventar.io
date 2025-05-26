package ar.edu.utn.frba.inventario.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.inventario.api.utils.TokenManager
import ar.edu.utn.frba.inventario.screens.HomeScreen
import ar.edu.utn.frba.inventario.screens.LoginScreen
import ar.edu.utn.frba.inventario.screens.OrdersScreen
import ar.edu.utn.frba.inventario.screens.SessionCheckerScreen
import ar.edu.utn.frba.inventario.screens.UserScreen
import ar.edu.utn.frba.inventario.utils.Screen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val tokenManager = rememberTokenManager()

    NavHost(navController = navController, startDestination = Screen.SessionChecker.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Orders.route) {
            OrdersScreen(navController)
        }
        composable(Screen.User.route) {
            UserScreen(navController)
        }
        composable(Screen.SessionChecker.route) {
            SessionCheckerScreen(navController, tokenManager)
        }
    }
}

@Composable
fun rememberTokenManager(): TokenManager {
    val context = LocalContext.current
    return remember { TokenManager(context.applicationContext) }
}