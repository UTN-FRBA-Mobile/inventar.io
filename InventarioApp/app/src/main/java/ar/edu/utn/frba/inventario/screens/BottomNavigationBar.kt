package ar.edu.utn.frba.inventario.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ar.edu.utn.frba.inventario.utils.Screen

@Composable
fun BottomNavigationBar(navController: NavController, items: List<Screen> ) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.Home -> Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Screen.Orders -> Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Screen.User -> Icon(Icons.Default.AccountCircle, contentDescription = null)
                        else -> throw IllegalArgumentException("Unknown screen: ${screen.route}")
                    }
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute == screen.route)
                        return@NavigationBarItem

                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
