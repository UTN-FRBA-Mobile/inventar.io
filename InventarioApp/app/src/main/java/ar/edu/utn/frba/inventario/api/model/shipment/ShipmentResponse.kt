package ar.edu.utn.frba.inventario.api.model.shipment

import ar.edu.utn.frba.inventario.api.model.item.ItemStatus

data class ShipmentResponse(
    val id: Long,
    val status: ItemStatus,
    val creationDate: String,
    val lastModifiedDate: String,
    val idLocation: Long,
    val customerName: String,
    val productAmount: Map<Long, Int>,
    val productNames: Map<Long, String>
)
