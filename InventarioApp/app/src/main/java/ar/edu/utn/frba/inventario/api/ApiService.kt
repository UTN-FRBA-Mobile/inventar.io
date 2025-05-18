package ar.edu.utn.frba.inventario.api

import ar.edu.utn.frba.inventario.api.model.auth.LoginRequest
import ar.edu.utn.frba.inventario.api.model.auth.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: String): Response<LoginResponse>
}