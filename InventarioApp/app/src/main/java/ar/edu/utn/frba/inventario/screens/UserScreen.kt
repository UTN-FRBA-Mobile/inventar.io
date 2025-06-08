package ar.edu.utn.frba.inventario.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.utils.Base64Image
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.LocationViewModel
import ar.edu.utn.frba.inventario.viewmodels.UserScreenViewModel

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

    LaunchedEffect(Unit) {
        userScreenViewModel.getUser()
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.secondaryContainer)
    ){
        if (user == null) {
            Column (
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
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
                        .border(2.dp, Color.Gray, CircleShape)
                        .shadow(4.dp, CircleShape) )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = user!!.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "@${user!!.username}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Log.d("Show base64", "Success: ${user!!.base64image}")
                }
                Text(
                    text = "Ubicaciones Autorizadas",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyColumn(modifier =
                    Modifier
                        .padding(8.dp)
                        .fillMaxHeight(0.9f)
                ) {
                    items(user!!.allowedLocations.size) { index ->
                        var address by remember { mutableStateOf<String?>(null) }

                        LaunchedEffect(user!!.allowedLocations[index].latitude, user!!.allowedLocations[index].longitude) {
                            address = locationViewModel.getAddressFromLocation(context, user!!.allowedLocations[index].latitude, user!!.allowedLocations[index].longitude)
                        }

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)) {
                            Card (
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ){
                                Text(text = user!!.allowedLocations[index].name, fontSize = 18.sp, modifier = Modifier.padding(10.dp))
                                Text(
                                    text = address ?: "Cargando...",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
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
    }
}
