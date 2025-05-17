package ar.edu.utn.frba.inventario.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController){
    val items = listOf("home","orders","user")

    NavigationBar {
        items.forEach{item -> NavigationBarItem(icon = {
            when(item){
                "home" -> Icon(Icons.Default.Home, contentDescription = null)
                "orders" -> Icon(Icons.Default.Star, contentDescription = null)
                "user"-> Icon(Icons.Default.AccountCircle, contentDescription = null)
            } },
            label = { Text(item) },
            selected = navController.currentDestination?.route == item,
            onClick = {
                navController.navigate(item){
                    popUpTo(navController.graph.startDestinationId){ saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            })}
    }

}