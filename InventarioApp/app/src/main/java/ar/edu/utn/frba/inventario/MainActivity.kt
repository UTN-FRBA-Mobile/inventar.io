package ar.edu.utn.frba.inventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.inventario.navigation.AppNavigation
import ar.edu.utn.frba.inventario.screens.BottomNavigationBar
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

                val navBarItems: List<Screen> = listOf(Screen.Home, Screen.Orders, Screen.User)

                Scaffold(
                    bottomBar = {
                        if (currentRoute in navBarItems.map { it.route }) {
                            BottomNavigationBar(navController, navBarItems)
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation(navController)
                    }
                }
            }
        }
    }
}
