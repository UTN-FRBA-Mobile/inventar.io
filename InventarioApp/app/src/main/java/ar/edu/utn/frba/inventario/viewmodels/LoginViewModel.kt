package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.auth.LoginRequest
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.repository.AuthRepository
import ar.edu.utn.frba.inventario.api.utils.TokenManager
import ar.edu.utn.frba.inventario.events.NavigationEvent
import ar.edu.utn.frba.inventario.utils.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {
    private val _user = MutableStateFlow("")
    val user = _user.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent?>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun changeUser(newUser: String) {
        _user.value = newUser
    }

    fun changePassword(newPass: String) {
        _password.value = newPass
    }

    fun doLogin() {
        // Verifico que haya completado los campos
        val currentUser = _user.value
        val currentPassword = _password.value

        if (currentUser.isBlank() || currentPassword.isBlank()) {
            viewModelScope.launch {
                _snackbarMessage.emit("Usuario y contraseña no pueden estar vacíos")
            }
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            // Formato de contraseña: sha256({pass}{user})
            val hashedPassword = sha256(currentPassword + currentUser)
            // TODO: obtener desde la API de geolocalización
            val latitude = -34.6297674
            val longitude = -58.4521302

            Log.d("LoginViewModel", "Iniciando login con backend para usuario: $currentUser")
            val loginResult = authRepository.login(LoginRequest(currentUser, hashedPassword, latitude, longitude))
            Log.d("LoginViewModel", "Login ejecutado")

            when (loginResult) {
                is NetworkResult.Success -> {
                    Log.d("LoginViewModel", "Login exitoso")
                    _navigationEvent.emit(NavigationEvent.NavigateTo(Screen.Home.route))
                    tokenManager.saveTokens(
                        loginResult.data.accessToken,
                        loginResult.data.refreshToken
                    )
                }
                is NetworkResult.Error -> {
                    Log.d("LoginViewModel", "Error: code=${loginResult.code}, message=${loginResult.message}")

                    val message = when {
                        loginResult.code == 401 && loginResult.message == "wrong credentials" -> "Usuario o contraseña inválidos"
                        loginResult.code == 401 && loginResult.message == "no location" -> "Muy alejado de una ubicación válida"
                        else -> "Error de login: ${loginResult.message ?: "desconocido"}"
                    }

                    _snackbarMessage.emit(message)
                }
                is NetworkResult.Exception -> {
                    Log.d("LoginViewModel", "Error crítico: ${loginResult.e.message}")
                    _snackbarMessage.emit("Error crítico: ${loginResult.e.message}")
                }
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