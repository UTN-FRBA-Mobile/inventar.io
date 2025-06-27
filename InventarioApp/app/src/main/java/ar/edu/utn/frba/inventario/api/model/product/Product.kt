package ar.edu.utn.frba.inventario.api.model.product

data class Product(
    val id: String,
    val name: String,
    val imageURL: String?,
    val innerLocation: String?,
    var currentStock: Int?,
    val ean13: String,
    val description: String,
)