package ar.edu.utn.frba.inventario.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.LocationViewModel
import ar.edu.utn.frba.inventario.viewmodels.UserScreenViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun WelcomeScreen(
    navController: NavController,
    userScreenViewModel: UserScreenViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel()
) {
    WelcomeBodyContent(navController, userScreenViewModel, locationViewModel)
}

@Composable
fun WelcomeBodyContent(
    navController: NavController,
    userScreenViewModel: UserScreenViewModel,
    locationViewModel: LocationViewModel

){
    val context = LocalContext.current
    val user by userScreenViewModel.user.collectAsState()


    if(locationViewModel.hasLocationPermission(context)){
        Log.d("WelcomeScreen", "user has Location Permitions")
    }else{
        Log.d("WelcomeScreen", "user doesnt have Location Permitions")
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(
            text = "Bienvenido username",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Actualmente te encuentras en la sucursal locationName",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Button(
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ) {
            Text(
                text = "Continuar",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}