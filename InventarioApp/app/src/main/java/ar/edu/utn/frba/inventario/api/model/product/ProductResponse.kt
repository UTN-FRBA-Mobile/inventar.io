package ar.edu.utn.frba.inventario.api.model.product

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val ean13: String,
    val base64image: String)
