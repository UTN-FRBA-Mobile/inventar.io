package ar.edu.utn.frba.inventario.viewmodels

import Product
import Shipment
import ShipmentStatus
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.time.LocalDateTime

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    //Creo lista con productos random para poder visualizar las cards
    val shipments: SnapshotStateList<Shipment> = mutableStateListOf<Shipment>().apply {
        addAll(listOf(
            Shipment(
                id = "S01-1",
                number = "ENV-0001",
                customerName = "Este es un nombre tan largo que no debería entrar",
                status = ShipmentStatus.COMPLETED,
                products = listOf(
                    Product("P-101", "AAAA", 1),
                    Product("P-102", "BBBB", 2)
                ),
                creationDate = LocalDateTime.now().minusDays(3)
            ),
            Shipment(
                id = "S01-1",
                number = "ENV-0010",
                customerName = "Dibu Martínez",
                status = ShipmentStatus.BLOCKED,
                products = listOf(
                    Product("P-101", "AAAA", 1),
                    Product("P-102", "BBBB", 2)
                ),
                creationDate = LocalDateTime.now().minusDays(3)
            ),
            Shipment(
                id = "S01-1",
                number = "ENV-0001",
                customerName = "Enzo Fernández",
                status = ShipmentStatus.PENDING,
                products = listOf(
                    Product("P-101", "AAAA", 1)
                ),
                creationDate = LocalDateTime.now().minusDays(3)
            ),
            Shipment(
                id = "S01-2",
                number = "ENV-0002",
                customerName = "Lionel Messi",
                status = ShipmentStatus.IN_PROGRESS,
                products = listOf(
                    Product("P-201", "Monitor", 1),
                    Product("P-202", "Teclado", 1)
                ),
                creationDate = LocalDateTime.now().minusDays(1)
            ),
            Shipment(
                id = "S01-3",
                number = "ENV-0003",
                customerName = "UTN FRBA",
                status = ShipmentStatus.PENDING,
                products = listOf(
                    Product("P-301", "ASDADS", 1),
                    Product("P-302", "ADADASD", 3),
                    Product("P-301", "ASDADS", 1),
                    Product("P-302", "ADADASD", 3),
                    Product("P-301", "ASDADS", 1),
                    Product("P-302", "ADADASD", 3),
                    Product("P-301", "ASDADS", 1),
                    Product("P-302", "ADADASD", 3),
                    Product("P-301", "ASDADS", 1),
                    Product("P-302", "ADADASD", 3),
                    Product("P-301", "ASDADS", 1),
                    Product("P-302", "ADADASD", 3),
                    Product("P-301", "ASDADS", 1),
                    Product("P-302", "ADADASD", 3)
                ),
                creationDate = LocalDateTime.now().minusHours(5)
            ),
            Shipment(
                id = "S01-4",
                number = "ENV-0004",
                customerName = "Julián Álvarez",
                status = ShipmentStatus.COMPLETED,
                products = listOf(
                    Product("P-401", "Tablet", 2),
                    Product("P-402", "Fundas", 2)
                ),
                creationDate = LocalDateTime.now().minusDays(2)
            ),
            Shipment(
                id = "S01-5",
                number = "ENV-0005",
                customerName = "Juan Pérez",
                status = ShipmentStatus.IN_PROGRESS,
                products = listOf(
                    Product("P-501", "adasd", 1),
                    Product("P-502", "aaaaaaa", 1)
                ),
                creationDate = LocalDateTime.now().minusHours(12)
            )

        ))
    }

    private val _selectedStatuses = mutableStateOf<Set<ShipmentStatus>>(emptySet())
    val selectedStatuses: androidx.compose.runtime.State<Set<ShipmentStatus>>
        get() = _selectedStatuses

    fun getFilteredShipments(): List<Shipment> {
        return if (_selectedStatuses.value.isEmpty()) {
            shipments.sortedWith(
                compareBy<Shipment> { it.status.ordinal }
                    .thenBy { it.creationDate }
            )
        } else {
            getSortedShipments(shipments
                .filter { it.status in _selectedStatuses.value})
        }
    }

    fun updateSelectedStatuses(status: ShipmentStatus) {
        _selectedStatuses.value = _selectedStatuses.value.toMutableSet().apply {
            if (contains(status)) remove(status) else add(status)
        }
    }

    fun clearFilters() {
        _selectedStatuses.value = emptySet()
    }


    fun getSortedShipments(shipments: List<Shipment>): List<Shipment> {
        return shipments.sortedWith(
            compareBy<Shipment> { it.status.ordinal }
                .thenBy { it.creationDate }  // se ordena desde el más antiguo al más nuevo. Tiene sentido para los completados?
        )
    }
}

