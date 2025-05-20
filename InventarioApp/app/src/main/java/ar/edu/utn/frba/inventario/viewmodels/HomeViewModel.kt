package ar.edu.utn.frba.inventario.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import ar.edu.utn.frba.inventario.api.model.shipment.Shipment
import ar.edu.utn.frba.inventario.api.model.shipment.ShipmentStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    val shipments: SnapshotStateList<Shipment> = mutableStateListOf<Shipment>().apply {
        addAll(listOf(
            Shipment("S01-1", "ENVÍO 0001", ShipmentStatus.COMPLETED),
            Shipment("S01-2", "ENVÍO 0002", ShipmentStatus.IN_PROGRESS),
            Shipment("S01-3", "ENVÍO 0003", ShipmentStatus.PENDING),
            Shipment("S01-4", "ENVÍO 0004", ShipmentStatus.COMPLETED),
            Shipment("S01-5", "ENVÍO 0005", ShipmentStatus.IN_PROGRESS),
            Shipment("S01-6", "ENVÍO 0006", ShipmentStatus.PENDING)
        ))
    }

    fun getSortedShipments(): List<Shipment> {
        return shipments.sortedBy { it.status.ordinal }
    }
}

