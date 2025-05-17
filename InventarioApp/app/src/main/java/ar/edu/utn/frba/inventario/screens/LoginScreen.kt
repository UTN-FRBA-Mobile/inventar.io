package ar.edu.utn.frba.inventario.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.viewmodels.LoginViewModel

@Composable
fun Login(viewModel: LoginViewModel = viewModel(), navController: NavController){

    Column(modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("User:")
        TextField(value = viewModel.user, onValueChange = {viewModel.changeUser(it)})
        Text("Password:")
        TextField(value = viewModel.password, onValueChange = {viewModel.changePassword(it)})
        Button(onClick = { viewModel.login(viewModel.user,viewModel.password, navController) }) {
            Text(text = "Login")
        }
    }
}