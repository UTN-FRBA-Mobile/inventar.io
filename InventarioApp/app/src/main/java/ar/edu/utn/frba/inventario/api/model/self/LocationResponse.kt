package ar.edu.utn.frba.inventario.api.model.self

data class LocationResponse(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
    val name: String,
)
