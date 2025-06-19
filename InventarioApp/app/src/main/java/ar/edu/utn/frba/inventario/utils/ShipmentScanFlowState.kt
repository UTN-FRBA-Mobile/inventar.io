package ar.edu.utn.frba.inventario.utils

import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.api.model.shipment.Shipment

object ShipmentScanFlowState {
    var selectedShipment: Shipment? = null
    var scannedProduct: Product? = null

    fun reset() {
        selectedShipment = null
        scannedProduct = null
    }
}