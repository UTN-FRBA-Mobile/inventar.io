package ar.edu.utn.frba.inventario.composables.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.removeRouteParams

@Composable
fun AppNavBar(navController: NavController, items: List<Screen>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.removeRouteParams()

    NavigationBar(
        modifier = Modifier.height(82.dp)
    ) {
        items.forEach { screen: Screen ->

            NavigationBarItem(
                icon = {
                    val iconRes = when (screen) {
                        Screen.Shipments -> R.drawable.shipments
                        Screen.Orders -> R.drawable.orders
                        Screen.User -> R.drawable.user
                        else -> throw IllegalArgumentException("Unknown screen: ${screen.route}")
                    }
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute == screen.route) return@NavigationBarItem
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Shipments.route)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
