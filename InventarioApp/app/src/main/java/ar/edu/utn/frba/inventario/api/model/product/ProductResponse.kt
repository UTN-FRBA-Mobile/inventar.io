package ar.edu.utn.frba.inventario.api.model.product

data class ProductResponse(
    val id: String,
    val name: String,
    val description: String,
    val ean13: String,
    val imageURL: String?
)