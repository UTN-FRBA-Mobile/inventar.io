package ar.edu.utn.frba.inventario.api.utils

import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.repository.AuthRepository
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Provider

class TokenRefreshAuthenticator constructor(
    private val tokenManager: TokenManager,
    private val authRepository: Provider<AuthRepository>,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val currentAccessToken = tokenManager.getAccessToken()
        val requestAccessToken = response.request.header("Authorization")?.substringAfter("Bearer ")

        if ((response.code == 401 || response.code == 403) &&
            currentAccessToken != null && currentAccessToken == requestAccessToken
        ) {
            synchronized(this) {
                val newAccessTokenCheck = tokenManager.getAccessToken()
                if (currentAccessToken != newAccessTokenCheck) {
                    // El token fue refrescado por otro hilo/request, reintentar con el nuevo token
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessTokenCheck")
                        .build()
                }

                val refreshToken = tokenManager.getRefreshToken()
                    ?: return null // No hay refresh token, no se puede continuar

                when (val refreshResult = authRepository.get().refreshToken(refreshToken)) {
                    is NetworkResult.Success -> {
                        val newLoginResponse = refreshResult.data
                        tokenManager.saveSession(
                            newLoginResponse.accessToken,
                            newLoginResponse.refreshToken
                        )

                        return response.request.newBuilder()
                            .header("Authorization", "Bearer ${newLoginResponse.accessToken}")
                            .build()
                    }

                    is NetworkResult.Error, is NetworkResult.Exception -> {
                        // El refresh falló (ej. refresh token inválido o expirado)
                        tokenManager.clearSession()
                        return null
                    }
                }
            }
        }

        return null
    }
}