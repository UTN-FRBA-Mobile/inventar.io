package ar.edu.utn.frba.inventario.api.repository

import ar.edu.utn.frba.inventario.api.ApiService
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.self.LocationResponse
import ar.edu.utn.frba.inventario.api.model.self.UserResponse
import javax.inject.Inject

class SelfRepository @Inject constructor(
    private val apiService: ApiService,
) : Repository() {
    suspend fun getMyUser(): NetworkResult<UserResponse> {
        return safeApiCall { apiService.getMyUser() }
    }

    suspend fun getMyLocation(): NetworkResult<LocationResponse> {
        return safeApiCall { apiService.getMyLocation() }
    }
}