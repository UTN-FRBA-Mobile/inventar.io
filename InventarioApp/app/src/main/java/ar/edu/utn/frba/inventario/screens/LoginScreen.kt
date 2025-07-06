package ar.edu.utn.frba.inventario.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.composables.utils.Spinner
import ar.edu.utn.frba.inventario.events.NavigationEvent
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.LocationViewModel
import ar.edu.utn.frba.inventario.viewmodels.LoginScreenViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    loginScreenViewModel: LoginScreenViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val user by loginScreenViewModel.user.collectAsStateWithLifecycle()
    val password by loginScreenViewModel.password.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val isLoading by loginScreenViewModel.isLoading.collectAsStateWithLifecycle()

    val logoResourceId = if (isSystemInDarkTheme()) {
        R.drawable.logo_dark
    } else {
        R.drawable.logo_white
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    val locationPermissionGranted by locationViewModel
        .locationPermissionGranted
        .collectAsStateWithLifecycle()
    val context = LocalContext.current
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            locationViewModel.setLocationPermissionGranted(true)
            locationViewModel.startLocationUpdates()
        } else {
            Log.d("LoginScreen", "Permiso de ubicación denegado")
        }
    }
    LaunchedEffect(Unit) {
        loginScreenViewModel.navigationEvent.collect { event ->
            if (event is NavigationEvent.NavigateTo) {
                navController.navigate(event.route) {
                    // Limpio el stack al start del grafo de navegación
                    // Asi el user no puede volver al login yendo para atrás
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        loginScreenViewModel.snackbarMessage.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                locationPermission,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(locationPermission)
        }
    }
    LaunchedEffect(Unit) {
        if (!locationPermissionGranted) {
            locationPermissionLauncher.launch(locationPermission)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(WindowInsets.ime.asPaddingValues()),
        ) {
            // si llega a cancelar permisos y quiere loguearse de vuelta, no muestra el login
            // y muestra un botón para solicitar permisos
            if (!locationPermissionGranted) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Activar permisos de ubicacion",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Button(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        onClick = {
                            locationPermissionLauncher.launch(locationPermission)
                        },
                    ) {
                        Text(
                            text = "Activar ubicacion",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            painter = painterResource(id = logoResourceId),
                            contentDescription = stringResource(R.string.logo),
                            modifier = Modifier
                                .size(screenWidth * 0.8f),
                            contentScale = ContentScale.Fit,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        OutlinedTextField(
                            value = user,
                            onValueChange = { loginScreenViewModel.changeUser(it) },
                            label = { Text("Usuario") },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                },
                            ),
                            singleLine = true,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { loginScreenViewModel.changePassword(it) },
                            label = { Text("Contraseña") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    executeLogin(
                                        loginScreenViewModel,
                                        keyboardController,
                                        focusManager,
                                    )
                                },
                            ),
                            singleLine = true,
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = {
                            executeLogin(
                                loginScreenViewModel,
                                keyboardController,
                                focusManager,
                            )
                        }) {
                            Text("Login")
                        }
                    }
                }
            }
        }

        Spinner(isLoading)
    }
}

fun executeLogin(
    loginScreenViewModel: LoginScreenViewModel,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
) {
    focusManager.clearFocus()
    keyboardController?.hide()
    loginScreenViewModel.doLogin()
}
