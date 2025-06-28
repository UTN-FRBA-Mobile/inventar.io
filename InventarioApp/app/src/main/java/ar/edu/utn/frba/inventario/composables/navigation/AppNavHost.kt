package ar.edu.utn.frba.inventario.composables.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ar.edu.utn.frba.inventario.api.utils.TokenManager
import ar.edu.utn.frba.inventario.screens.LoginScreen
import ar.edu.utn.frba.inventario.screens.ProductDetailScreen
import ar.edu.utn.frba.inventario.screens.UserScreen
import ar.edu.utn.frba.inventario.screens.WelcomeScreen
import ar.edu.utn.frba.inventario.screens.order.OrderDetailScreen
import ar.edu.utn.frba.inventario.screens.order.OrdersScreen
import ar.edu.utn.frba.inventario.screens.scan.ManualCodeScreen
import ar.edu.utn.frba.inventario.screens.scan.ProductAmountScreen
import ar.edu.utn.frba.inventario.screens.scan.ProductResultScreen
import ar.edu.utn.frba.inventario.screens.scan.ScanScreen
import ar.edu.utn.frba.inventario.screens.shipment.ShipmentDetailScreen
import ar.edu.utn.frba.inventario.screens.shipment.ShipmentsScreen
import ar.edu.utn.frba.inventario.utils.HasCode
import ar.edu.utn.frba.inventario.utils.ProductResultArgs
import ar.edu.utn.frba.inventario.utils.ScanArgs
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.withArgsDefinition
import ar.edu.utn.frba.inventario.viewmodels.ShipmentDetailViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    val tokenManager = rememberTokenManager()
    val startDestination = if (tokenManager.hasSession()) Screen.Welcome.route else Screen.Login.route

    val productResultArgs = ProductResultArgs.entries.toTypedArray()
    val scanArgs = ScanArgs.entries.toTypedArray()

    printCurrentBackStack(navController)

    NavHost(navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController)
        }
        composable(Screen.Shipments.route) {
            ShipmentsScreen(navController)
        }
        composable(Screen.Orders.route) {
            OrdersScreen(navController)
        }
        composable(Screen.User.route) {
            UserScreen(navController)
        }
        composable(
            Screen.Scan.withArgsDefinition(scanArgs),
            arguments = navArgsOf(scanArgs)
        ) { backStackEntry ->
            ScanScreen(
                navController,
                backStackEntry.arguments?.getString(ScanArgs.Origin.code) ?: "shipment"
            )
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
        composable(route=Screen.ShipmentDetail.route+"/{id}",
        arguments = listOf(
            navArgument(name = "id"){
                type= NavType.StringType
            }
        )) { backStackEntry->
            val idShipment = backStackEntry.arguments?.getString("id")?:""
            ShipmentDetailScreen(navController = navController, id = idShipment)
        }
        composable(route=Screen.OrderDetail.route+"/{orderId}",
            arguments = listOf(
                navArgument(name = "orderId"){
                    type= NavType.StringType
                }
            )) { backStackEntry->
            val idOrder = backStackEntry.arguments?.getString("orderId")?:""
            OrderDetailScreen(navController = navController, id = idOrder)
        }
        composable(
            route = Screen.ProductDetail.route +  "/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val viewModel: ShipmentDetailViewModel = hiltViewModel()
            ProductDetailScreen(
                navController = navController,
                productId = productId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.ProductAmount.route) {
            ProductAmountScreen(navController)
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