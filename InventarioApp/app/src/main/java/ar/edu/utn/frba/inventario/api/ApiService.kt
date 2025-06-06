package ar.edu.utn.frba.inventario.api

import ar.edu.utn.frba.inventario.api.model.auth.LoginRequest
import ar.edu.utn.frba.inventario.api.model.auth.LoginResponse
import ar.edu.utn.frba.inventario.api.model.self.LocationResponse
import ar.edu.utn.frba.inventario.api.model.self.UserResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    /* Authentication */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/refresh")
    fun refreshToken(@Body refreshToken: String): Call<LoginResponse>

    /* Self location & user */
    @GET("/api/v1/user/self")
    suspend fun getMyUser(): Response<UserResponse>

    @GET("/api/v1/location/self")
    suspend fun getMyLocation(): Response<LocationResponse>
}