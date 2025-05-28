package ar.edu.utn.frba.inventario.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ar.edu.utn.frba.inventario.api.utils.TokenManager
import ar.edu.utn.frba.inventario.screens.HomeScreen
import ar.edu.utn.frba.inventario.screens.LoginScreen
import ar.edu.utn.frba.inventario.screens.OrdersScreen
import ar.edu.utn.frba.inventario.screens.UserScreen
import ar.edu.utn.frba.inventario.screens.scan.ManualCodeScreen
import ar.edu.utn.frba.inventario.screens.scan.ProductResultScreen
import ar.edu.utn.frba.inventario.screens.scan.ScanScreen
import ar.edu.utn.frba.inventario.utils.HasCode
import ar.edu.utn.frba.inventario.utils.ProductResultArgs
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.withArgsDefinition

@Composable
fun AppNavigation(navController: NavHostController) {
    val tokenManager = rememberTokenManager()
    val startDestination = if (tokenManager.hasSession()) Screen.Home.route else Screen.Login.route

    val productResultArgs = ProductResultArgs.entries.toTypedArray()

    printCurrentBackStack(navController)

    NavHost(navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Orders.route) {
            OrdersScreen(navController)
        }
        composable(Screen.User.route) {
            UserScreen(navController)
        }
        composable(Screen.Scan.route) {
            ScanScreen(navController)
        }
        composable(Screen.ManualCode.route) {
            ManualCodeScreen(navController)
        }
        composable(
            Screen.ProductResult.withArgsDefinition(productResultArgs),
            arguments = navArgsOf(productResultArgs)
        ) { backStackEntry ->
            ProductResultScreen(
                navController,
                backStackEntry.arguments?.getString(ProductResultArgs.Code.code),
                backStackEntry.arguments?.getString(ProductResultArgs.CodeType.code),
                backStackEntry.arguments?.getString(ProductResultArgs.ErrorMessage.code),
                backStackEntry.arguments?.getString(ProductResultArgs.Origin.code) ?: "scan"
            )
        }
    }
}

fun printCurrentBackStack(navController: NavController) {
    navController.addOnDestinationChangedListener { controller, _, _ ->
        @SuppressLint("RestrictedApi")
        val routes = controller.currentBackStack.value.joinToString(", ") {
            val route = it.destination.route
            // Print all routes without query params
            route?.substringBefore("?") ?: "null"
        }

        Log.d("BackStackLog", "BackStack: $routes")

        // Print current route without query params
        val currentRoute = controller.currentBackStackEntry?.destination?.route?.substringBefore("?")
        Log.d("BackStackLog", "Current Route: $currentRoute")
    }
}

fun navArgsOf(args: Array<out HasCode>): List<NamedNavArgument> {
    return args.map {
        navArgument(it.code) {
            nullable = true
            defaultValue = null
        }
    }
}

@Composable
fun rememberTokenManager(): TokenManager {
    val context = LocalContext.current
    return remember { TokenManager(context.applicationContext) }
}