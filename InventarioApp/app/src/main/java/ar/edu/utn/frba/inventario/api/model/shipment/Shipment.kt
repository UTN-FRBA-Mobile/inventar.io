package ar.edu.utn.frba.inventario.api.model.shipment

import androidx.compose.ui.graphics.Color
import ar.edu.utn.frba.inventario.R

data class Shipment(
    val id: String, //al principio del id podría tener el código de sucursal
    val number: String,
    val status: ShipmentStatus
)

enum class ShipmentStatus(val color: Color) {
    PENDING(Color.LightGray),
    IN_PROGRESS(Color(0xFFF7FF62)),
    COMPLETED(Color(0xFF3BF063))
}