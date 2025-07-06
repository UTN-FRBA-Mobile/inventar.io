package ar.edu.utn.frba.inventario.composables.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import ar.edu.utn.frba.inventario.api.utils.TokenManager
import ar.edu.utn.frba.inventario.screens.LoginScreen
import ar.edu.utn.frba.inventario.screens.ProductDetailScreen
import ar.edu.utn.frba.inventario.screens.UserScreen
import ar.edu.utn.frba.inventario.screens.WelcomeScreen
import ar.edu.utn.frba.inventario.screens.order.OrderDetailScreen
import ar.edu.utn.frba.inventario.screens.order.OrdersScreen
import ar.edu.utn.frba.inventario.screens.scan.ManualCodeScreen
import ar.edu.utn.frba.inventario.screens.scan.ManualOrderScreen
import ar.edu.utn.frba.inventario.screens.scan.OrderProductsScreen
import ar.edu.utn.frba.inventario.screens.scan.OrderResultScreen
import ar.edu.utn.frba.inventario.screens.scan.ProductAmountScreen
import ar.edu.utn.frba.inventario.screens.scan.ProductResultScreen
import ar.edu.utn.frba.inventario.screens.scan.ScanScreen
import ar.edu.utn.frba.inventario.screens.shipment.ShipmentDetailScreen
import ar.edu.utn.frba.inventario.screens.shipment.ShipmentsScreen
import ar.edu.utn.frba.inventario.utils.HasCode
import ar.edu.utn.frba.inventario.utils.OrderProductsListArgs
import ar.edu.utn.frba.inventario.utils.OrderResultArgs
import ar.edu.utn.frba.inventario.utils.ProductResultArgs
import ar.edu.utn.frba.inventario.utils.ScanArgs
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.removeRouteParams
import ar.edu.utn.frba.inventario.utils.withArgsDefinition
import ar.edu.utn.frba.inventario.viewmodels.ShipmentDetailViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.removeRouteParams()

    val fullScreenRoutes = listOf(Screen.Scan.route)
    val isFullScreen = currentRoute in fullScreenRoutes

    val BODY_PADDING = 15.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (!isFullScreen) Modifier.padding(BODY_PADDING)
                else Modifier
            )
    ) {
        NavHostBody(navController)
    }
}

@Composable
fun NavHostBody(navController: NavHostController) {
    val tokenManager = rememberTokenManager()
    val startDestination =
        if (tokenManager.hasSession()) Screen.Welcome.route else Screen.Login.route

    val productResultArgs = ProductResultArgs.entries.toTypedArray()
    val scanArgs = ScanArgs.entries.toTypedArray()
    val orderProductsListArgs = OrderProductsListArgs.entries.toTypedArray()
    val orderResultArgs = OrderResultArgs.entries.toTypedArray()

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
        composable(
            route = Screen.ShipmentDetail.route + "/{id}",
            arguments = listOf(
                navArgument(name = "id") {
                    type = NavType.StringType
                }
            )) { backStackEntry ->
            val idShipment = backStackEntry.arguments?.getString("id") ?: ""
            ShipmentDetailScreen(navController = navController, id = idShipment)
        }
        composable(
            route = Screen.OrderDetail.route + "/{orderId}",
            arguments = listOf(
                navArgument(name = "orderId") {
                    type = NavType.StringType
                }
            )) { backStackEntry ->
            val idOrder = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(navController = navController, id = idOrder)
        }
        composable(
            route = Screen.ProductDetail.route + "/{productId}",
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
        composable(
            route = Screen.OrderProductsList.withArgsDefinition(orderProductsListArgs),
            arguments = navArgsOf(orderProductsListArgs)
        ) { backStackEntry ->
            val orderId =
                backStackEntry.arguments?.getString(OrderProductsListArgs.OrderId.code) ?: ""
            OrderProductsScreen(
                navController = navController,
                orderId = orderId
            )
        }
        composable(
            Screen.OrderResult.withArgsDefinition(orderResultArgs),
            arguments = navArgsOf(orderResultArgs)
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(OrderResultArgs.OrderId.code)
            val initialErrorMessage =
                backStackEntry.arguments?.getString(OrderResultArgs.ErrorMessage.code)

            OrderResultScreen(
                navController = navController,
                code = orderId,
                errorMessage = initialErrorMessage,
                codeType = backStackEntry.arguments?.getString(OrderResultArgs.CodeType.code.toString())
                    ?: "",
                origin = backStackEntry.arguments?.getString(OrderResultArgs.Origin.code.toString())
                    ?: ""
            )
        }
        composable(Screen.ManualOrder.route) {
            ManualOrderScreen(navController = navController)
        }

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