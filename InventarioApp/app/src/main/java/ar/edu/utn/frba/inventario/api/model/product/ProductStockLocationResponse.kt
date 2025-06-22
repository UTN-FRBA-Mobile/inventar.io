package ar.edu.utn.frba.inventario.api.model.product

data class ProductStockLocationResponse (
    val stockCount: Map<String, Int>,
    val locationDetails: Map<String, String>
)