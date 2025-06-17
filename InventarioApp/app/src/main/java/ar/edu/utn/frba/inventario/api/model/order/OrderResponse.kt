package ar.edu.utn.frba.inventario.api.model.order

import ar.edu.utn.frba.inventario.api.model.item.ItemStatus

data class OrderResponse(
    val id: Long,
    val status: ItemStatus,
    val creationDate: String,
    val scheduledDate: String,
    val lastModifiedDate: String,
    val idLocation: Long,
    val sender: String,
    val productAmount: Map<Long, Int>,
    val productNames: Map<Long, String>
)