package ar.edu.utn.frba.inventario.api.repository

import ar.edu.utn.frba.inventario.api.ApiService
import ar.edu.utn.frba.inventario.api.model.auth.LoginRequest
import ar.edu.utn.frba.inventario.api.model.auth.LoginResponse
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
) : Repository() {
    suspend fun login(loginRequest: LoginRequest): NetworkResult<LoginResponse> {
        return safeApiCall { apiService.login(loginRequest) }
    }

    suspend fun refreshToken(refreshToken: String): NetworkResult<LoginResponse> {
        return safeApiCall { apiService.refreshToken(refreshToken) }
    }
}