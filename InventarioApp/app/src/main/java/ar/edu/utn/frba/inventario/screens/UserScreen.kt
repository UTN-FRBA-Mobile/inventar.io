package ar.edu.utn.frba.inventario.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.inventario.utils.Base64Image
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.LocationViewModel
import ar.edu.utn.frba.inventario.viewmodels.UserScreenViewModel

data class LocationTest (
    val name: String,
    val latitude: Double,
    val longitude: Double
)


@Composable
fun UserScreen(
    navController: NavController,
    userScreenViewModel: UserScreenViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel()
) {
    UserBodyContent(navController, userScreenViewModel, locationViewModel)
}

@Composable
fun UserBodyContent(
    navController: NavController,
    userScreenViewModel: UserScreenViewModel,
    locationViewModel: LocationViewModel
) {
    val context = LocalContext.current
    val user by userScreenViewModel.user.collectAsState()
    val testLocations = listOf(
        LocationTest("Obelisco", -34.6037, -58.3816),
        LocationTest("Cataratas del Iguazú", -25.6953, -54.4367),
        LocationTest("Cerro Aconcagua", -32.6532, -70.0109)
    )

    LaunchedEffect(Unit) {
        userScreenViewModel.getUser()
    }

    Text(
        text = "Perfil",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(16.dp)
    )
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

        Text("parte de santi")


        if (user == null) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Base64Image(base64String = user!!.base64image, modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)                    // Hace la imagen circular
                        .border(2.dp, Color.Gray, CircleShape) // Opcional: borde gris redondeado
                        .shadow(4.dp, CircleShape) )
                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(text = user!!.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "@${user!!.username}",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                    Log.d("Show base64", "Success: ${user!!.base64image}")
                }

                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(testLocations.size) { index ->
                        var address by remember { mutableStateOf<String?>(null) }

                        LaunchedEffect(testLocations[index].latitude, testLocations[index].longitude) {
                            address = locationViewModel.getAddressFromLocation(context, testLocations[index].latitude, testLocations[index].longitude)
                        }
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)) {
                            Card (
                                modifier = Modifier.fillMaxWidth()
                            ){
                                Text(text = "Nombre: ${testLocations[index].name}", fontSize = 18.sp, modifier = Modifier.padding(10.dp))
                                Text(
                                    text = "Dirección: ${address ?: "Cargando..."}",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
