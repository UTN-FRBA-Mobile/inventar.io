package ar.edu.utn.frba.inventario.api.model.product

data class Product(
    val id: String,
    val name: String,
    val quantity: Int,
    val imageUrl: String?,
    val innerLocation: String?,
    val currentStock: Int?
)