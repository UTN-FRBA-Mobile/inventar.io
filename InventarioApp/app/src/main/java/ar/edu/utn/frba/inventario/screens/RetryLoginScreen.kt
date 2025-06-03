package ar.edu.utn.frba.inventario.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.viewmodels.UserScreenViewModel

@Composable
fun RetryLoginScreen(
    navController: NavController,
    userScreenViewModel: UserScreenViewModel = hiltViewModel()
) {
    //RetryLoginBodyContent(navController, userScreenViewModel)
}

@Composable
fun RetryLoginBodyContent(
    /* comenté esto para poder usar el composable
    navController: NavController,
    userScreenViewModel: UserScreenViewModel*/
){
    /* De la rama LocationService tengo el LocationViewModel y el UserScreenViewModelActualizado.
    val user by userScreenViewModel.user.collectAsState()
    */
    val username = "usergenerico"
    Column (
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(
            text = "Atención $username",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "No te encuentras cerca de ninguna sucursal habilitada",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)

        )
        Button(
            onClick = {},
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(
                text = "Reintentar",
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    }
}

@Preview
@Composable
fun runRetryLoginComposable(){
    RetryLoginBodyContent()
}