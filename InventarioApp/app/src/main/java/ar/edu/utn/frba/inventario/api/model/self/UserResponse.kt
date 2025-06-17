package ar.edu.utn.frba.inventario.api.model.self

data class UserResponse(
    val id: Int,
    val username: String,
    val name: String,
    val imageURL: String,
    val allowedLocations: List<LocationResponse>,
)
