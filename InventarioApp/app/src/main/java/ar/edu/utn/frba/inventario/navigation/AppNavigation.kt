package ar.edu.utn.frba.inventario.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ar.edu.utn.frba.inventario.screens.HomeScreen
import ar.edu.utn.frba.inventario.screens.LoginScreen
import ar.edu.utn.frba.inventario.screens.OrdersScreen
import ar.edu.utn.frba.inventario.screens.ScanResultScreen
import ar.edu.utn.frba.inventario.screens.ScanScreen
import ar.edu.utn.frba.inventario.screens.UserScreen
import ar.edu.utn.frba.inventario.utils.Screen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Orders.route) {
            OrdersScreen(navController = navController)
        }
        composable(Screen.User.route) {
            UserScreen(navController = navController)
        }
        composable(Screen.Scan.route) {
            ScanScreen(navController = navController)
        }
        composable(
            "scan_result?result={result}&errorMessage={errorMessage}&codeType={codeType}",
            arguments = listOf(
                navArgument("result") { nullable = true; defaultValue = null },
                navArgument("errorMessage") { nullable = true; defaultValue = null },
                navArgument("codeType") { nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            ScanResultScreen(
                navController = navController,
                result = backStackEntry.arguments?.getString("result"),
                errorMessage = backStackEntry.arguments?.getString("errorMessage"),
                codeType = backStackEntry.arguments?.getString("codeType")
            )
        }
    }
}