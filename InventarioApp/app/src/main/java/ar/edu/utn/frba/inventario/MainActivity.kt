package ar.edu.utn.frba.inventario

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.inventario.composables.navigation.AppNavBar
import ar.edu.utn.frba.inventario.composables.navigation.AppNavHost
import ar.edu.utn.frba.inventario.ui.theme.InventarioTheme
import ar.edu.utn.frba.inventario.utils.Screen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InventarioTheme {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStack?.destination?.route

                LaunchedEffect(Unit) {
                    addPrintBackStackListener(navController)
                }

                val navBarItems: List<Screen> = listOf(
                    Screen.Shipments,
                    Screen.Orders,
                    Screen.User
                )

                Scaffold(
                    bottomBar = {
                        if (currentRoute in navBarItems.map { it.route }) {
                            AppNavBar(navController, navBarItems)
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavHost(navController)
                    }
                }
            }
        }
    }
}

fun addPrintBackStackListener(navController: NavController) {
    navController.addOnDestinationChangedListener { controller, _, _ ->
        @SuppressLint("RestrictedApi")
        val routes =
            controller.currentBackStack.value.joinToString(", ") {
                val route = it.destination.route
                // Print all routes without query params
                route?.substringBefore("?") ?: "null"
            }

        Log.d("BackStackLog", "BackStack: $routes")

        // Print current route without query params
        val currentRoute =
            controller.currentBackStackEntry
                ?.destination
                ?.route
                ?.substringBefore("?")
        Log.d("BackStackLog", "Current Route: $currentRoute")
    }
}
