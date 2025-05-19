package ar.edu.utn.frba.inventario.api.model.auth

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
)