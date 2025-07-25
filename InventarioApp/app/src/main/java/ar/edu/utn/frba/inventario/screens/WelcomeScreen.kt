package ar.edu.utn.frba.inventario.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.composables.utils.AnimatedBuildingGif
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.UserScreenViewModel

@Composable
fun WelcomeScreen(
    navController: NavController,
    userScreenViewModel: UserScreenViewModel = hiltViewModel()
) {
    WelcomeBodyContent(navController, userScreenViewModel)
}

@Composable
fun WelcomeBodyContent(
    navController: NavController,
    userScreenViewModel: UserScreenViewModel
) {
    val userLocation by userScreenViewModel.branchLocationName.collectAsState()
    val user by userScreenViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        userScreenViewModel.getUser()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Text(
            text = "${stringResource(R.string.welcome_screen_title)} ${user?.username}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.padding(20.dp))

        AnimatedBuildingGif()

        Spacer(modifier = Modifier.padding(20.dp))

        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${stringResource(R.string.welcome_screen_location_indicator)} $userLocation",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .width(140.dp),
                onClick = {
                    navController.navigate(Screen.Shipments.route) {
                        popUpTo(0) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                Text(
                    text = stringResource(id = R.string._continue),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}