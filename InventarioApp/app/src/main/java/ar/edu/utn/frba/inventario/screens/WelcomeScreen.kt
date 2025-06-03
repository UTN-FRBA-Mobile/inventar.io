package ar.edu.utn.frba.inventario.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
    userScreenViewModel: UserScreenViewModel = hiltViewModel()
) {
    UserBodyContent(navController, userScreenViewModel)
}

@Composable
fun WelcomeBodyContent(
    /*navController: NavController,
    userScreenViewModel: UserScreenViewModel
    locationViewModel: LocationViewModel
    */
){
    /* De la rama LocationService tengo el LocationViewModel y el UserScreenViewModelActualizado.
    val user by userScreenViewModel.user.collectAsState()
    */
    val username = "usergenerico"
    val locationName = "Valentin Alsina"
    val fakeLatitude= -38.0
    val fakeLongitude = -58.0

    val posicion = remember(fakeLatitude, fakeLongitude) { LatLng(fakeLatitude, fakeLongitude) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(posicion, 15f)
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(
            text = "Bienvenido $username",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = posicion),
                title = "Tu ubicación"
            )
        }
        Text(
            text = "Actualmente te encuentras en la sucursal $locationName",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun RunScreen(){
    WelcomeBodyContent()
}