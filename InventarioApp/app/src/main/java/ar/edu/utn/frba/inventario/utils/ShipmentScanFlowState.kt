package ar.edu.utn.frba.inventario.utils

import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.api.model.shipment.Shipment

object ShipmentScanFlowState {
    // The shipment that started the scan flow
    var selectedShipment: Shipment? = null

    // The product that was scanned during the flow
    var scannedProduct: Product? = null

    // The stock of the scannedProduct
    var scannedProductStock: Int? = null

    fun clear() {
        selectedShipment = null
        scannedProduct = null
        scannedProductStock = null
    }
}
