package ar.edu.utn.frba.inventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import ar.edu.utn.frba.inventario.navigation.AppNavigation
import ar.edu.utn.frba.inventario.ui.theme.InventarioTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InventarioTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}