package ar.edu.utn.frba.inventario.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.UserScreenViewModel

@Composable
fun UserScreen(
    navController: NavController,
    userScreenViewModel: UserScreenViewModel = hiltViewModel()
) {
    UserBodyContent(navController, userScreenViewModel)
}

@Composable
fun UserBodyContent(
    navController: NavController,
    userScreenViewModel: UserScreenViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("User")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { userScreenViewModel.getUser() }) {
            Text("Test get user")
        }
        Button(onClick = {
            userScreenViewModel.doLogout()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true } // clears entire back stack
                launchSingleTop = true
            }
        }) {
            Text("Logout")
        }
    }
}