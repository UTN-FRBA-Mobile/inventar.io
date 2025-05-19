package ar.edu.utn.frba.inventario.api.model.auth

data class LoginRequest(
    val username: String,
    val password: String,
    val latitude: Double,
    val longitude: Double,
)