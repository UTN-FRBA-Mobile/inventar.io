package ar.edu.utn.frba.inventario.api.model.shipment

import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import java.time.LocalDateTime

data class ShipmentResponse(
    val id: Long,
    val status: ItemStatus,
    val creationDate: LocalDateTime,
    val lastModifiedDate: LocalDateTime,
    val idLocation: Long,
    val customerName: String,
    val productAmount: Map<Long, Int>)
