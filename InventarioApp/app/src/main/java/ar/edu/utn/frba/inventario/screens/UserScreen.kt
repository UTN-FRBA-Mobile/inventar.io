package ar.edu.utn.frba.inventario.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.api.utils.TokenManager
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.UserScreenViewModel

@Composable
fun UserScreen(
    navController: NavController,
    userScreenViewModel: UserScreenViewModel = hiltViewModel()
) {
    Scaffold(bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        UserBodyContent(innerPadding, navController, userScreenViewModel)
    }
}

@Composable
fun UserBodyContent(
    innerPadding: PaddingValues,
    navController: NavController,
    userScreenViewModel: UserScreenViewModel
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
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