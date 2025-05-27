package ar.edu.utn.frba.inventario.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.api.utils.TokenManager
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.Spinner

@Composable
fun SessionCheckerScreen(
    navController: NavController,
    tokenManager: TokenManager
) {
    LaunchedEffect(Unit) {
        if (!tokenManager.hasSession()) {
            Log.d("SessionCheckerScreen", "No session found, navigating to Login")
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.SessionChecker.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Always loading, because this is a temporary screen
        Spinner(true)
    }
}