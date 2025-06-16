package ar.edu.utn.frba.inventario.viewmodels

import android.location.Location
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.auth.LoginRequest
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.repository.AuthRepository
import ar.edu.utn.frba.inventario.api.repository.LocationRepository
import ar.edu.utn.frba.inventario.api.utils.TokenManager
import ar.edu.utn.frba.inventario.events.NavigationEvent
import ar.edu.utn.frba.inventario.utils.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _navigationEvent = MutableSharedFlow<NavigationEvent?>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _user = MutableStateFlow("")
    val user = _user.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val location: StateFlow<Location?> = locationRepository.location

    fun changeUser(newUser: String) {
        _user.value = newUser
    }

    fun changePassword(newPass: String) {
        _password.value = newPass
    }

    fun doLogin() {
        val currentUser = _user.value
        val currentPassword = _password.value

        // Ambos campos deben tener valor
        if (currentUser.isBlank() || currentPassword.isBlank()) {
            viewModelScope.launch {
                _snackbarMessage.emit("Debe completar ambos campos")
            }
            return
        }

        _isLoading.value = true

        viewModelScope.launch(Dispatchers.Default) {
            try {
                // Formato de contraseña: sha256({pass}{user})
                val hashedPassword = sha256(currentPassword + currentUser)

                val latitude = locationRepository.getLatitude()
                val longitude = locationRepository.getLongitude()
                Log.d("LoginViewModel", "Latitud: $latitude, Longitud: $longitude")

                val loginResult = withContext(Dispatchers.Default) {
                    authRepository.login(LoginRequest(currentUser, hashedPassword, latitude, longitude))
                }

                when (loginResult) {
                    is NetworkResult.Success -> {
                        Log.d("LoginViewModel", "Login exitoso")

                        tokenManager.saveSession(
                            loginResult.data.accessToken,
                            loginResult.data.refreshToken
                        )

                        _user.value = ""
                        _password.value = ""
                        Log.d("LoginScreenViewModel", "Redireccionando a Welcome")
                        _navigationEvent.emit(NavigationEvent.NavigateTo(Screen.Welcome.route))
                        Log.d("LoginScreenViewModel", "Listo a Welcome")

                    }
                    is NetworkResult.Error -> {
                        Log.d("LoginViewModel", "Error: code=${loginResult.code}, message=${loginResult.message}")

                        val message = when {
                            loginResult.code == 401 && loginResult.message == "wrong credentials" -> "Credenciales inválidas"
                            loginResult.code == 401 && loginResult.message == "no location" -> "Muy alejado de una ubicación válida"
                            else -> "Error desconocido. Intente nuevamente más tarde"
                        }

                        _snackbarMessage.emit(message)
                    }
                    is NetworkResult.Exception -> {
                        Log.d("LoginViewModel", "Error crítico: ${loginResult.e.message}")
                        _snackbarMessage.emit("Ha ocurrido un error. Revise su conexión a internet")
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun sha256(input: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))
            .toHexString()
    }
}