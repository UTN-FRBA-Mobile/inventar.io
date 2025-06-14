package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.api.model.shipment.Shipment
import ar.edu.utn.frba.inventario.api.model.shipment.ShipmentResponse
import ar.edu.utn.frba.inventario.api.repository.ShipmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@HiltViewModel
class HomeViewModel @Inject constructor(private val shipmentRepository: ShipmentRepository,
    savedStateHandle: SavedStateHandle
) : BaseItemViewModel<Shipment>(
    savedStateHandle = savedStateHandle,
    filterKey = "shipment_filter"
) {
    private val _shipments = MutableStateFlow<List<ShipmentResponse>>(emptyList())
    val shipments: StateFlow<List<ShipmentResponse>> = _shipments.asStateFlow()

    //TODO remover cuando tengamos datos reales
    override val items: SnapshotStateList<Shipment> = mutableStateListOf<Shipment>()
//        .apply {
//        addAll(listOf(
//            Shipment(
//                id = "S01-1",
//                number = "ENV-0001",
//                customerName = "Este es un nombre tan largo que no debería entrar",
//                status = ItemStatus.COMPLETED,
//                products = listOf(
//                    Product(
//                        "P-101", "AAAA", 1,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100
//                    ),
//                    Product("P-102", "BBBB", 2,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100)
//                ),
//                creationDate = LocalDateTime.now().minusDays(3)
//            ),
//            Shipment(
//                id = "S01-1",
//                number = "ENV-0010",
//                customerName = "Dibu Martínez",
//                status = ItemStatus.BLOCKED,
//                products = listOf(
//                    Product("P-101", "AAAA", 1,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100),
//                    Product("P-102", "BBBB", 2,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100)
//                ),
//                creationDate = LocalDateTime.now().minusDays(3)
//            ),
//            Shipment(
//                id = "S01-1",
//                number = "ENV-0001",
//                customerName = "Enzo Fernández",
//                status = ItemStatus.PENDING,
//                products = listOf(
//                    Product("P-101", "AAAA", 1,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100)
//                ),
//                creationDate = LocalDateTime.now().minusDays(3)
//            ),
//            Shipment(
//                id = "S01-2",
//                number = "ENV-0002",
//                customerName = "Lionel Messi",
//                status = ItemStatus.IN_PROGRESS,
//                products = listOf(
//                    Product("P-201", "Monitor", 1,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100),
//                    Product("P-202", "Teclado", 1,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100)
//                ),
//                creationDate = LocalDateTime.now().minusDays(1)
//            ),
//            Shipment(
//                id = "S01-3",
//                number = "ENV-0003",
//                customerName = "UTN FRBA",
//                status = ItemStatus.PENDING,
//                products = listOf(
//                    Product("P-301", "ASDADS", 1,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100),
//                    Product("P-302", "ADADASD", 3,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100),
//                    Product("P-301", "ASDADS", 1,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100),
//                    Product("P-302", "ADADASD", 3,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100),
//                    Product("P-301", "ASDADS", 1,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100),
//                    Product("P-302", "ADADASD", 3,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100),
//                    Product("P-301", "ASDADS", 1,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100),
//                    Product("P-302", "ADADASD", 3,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100),
//
//                ),
//                creationDate = LocalDateTime.now().minusHours(5)
//            ),
//            Shipment(
//                id = "S01-4",
//                number = "ENV-0004",
//                customerName = "Julián Álvarez",
//                status = ItemStatus.COMPLETED,
//                products = listOf(
//                    Product("P-401", "Tablet", 2,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100),
//                    Product("P-402", "Fundas", 2,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100)
//                ),
//                creationDate = LocalDateTime.now().minusDays(2)
//            ),
//            Shipment(
//                id = "S01-5",
//                number = "ENV-0005",
//                customerName = "Juan Pérez",
//                status = ItemStatus.IN_PROGRESS,
//                products = listOf(
//                    Product("P-501", "adasd", 1,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100),
//                    Product("P-502", "aaaaaaa", 1,
//                        imageUrl = "a",
//                        innerLocation = "Pasillo 3, estante 2",
//                        currentStock = 100)
//                ),
//                creationDate = LocalDateTime.now().minusHours(12)
//            )
//
//        ))
//    }

    fun getShipments() {
        viewModelScope.launch(Dispatchers.Default) {
            when (val shipmentResult = shipmentRepository.getShipmentList()) {
                is NetworkResult.Success -> {
                    Log.d("UserViewModel", "Success: ${shipmentResult.data}")
                    val shipmentsParsed = shipmentResult.data.map { sr -> parseMapShipment(sr) }
                    items.apply { shipmentsParsed }
                }
                is NetworkResult.Error -> {
                    Log.d("UserViewModel", "Error: code=${shipmentResult.code}, message=${shipmentResult.message}")
                }
                is NetworkResult.Exception -> {
                    Log.d("UserViewModel", "Error crítico: ${shipmentResult.e.message}")
                }
            }
        }
    }

    override fun getStatus(item: Shipment) = item.status

    override fun getFilterDate(item: Shipment) = item.creationDate

    fun parseMapShipment(shipmentResponse: ShipmentResponse): Shipment{
       return Shipment(
            id = shipmentResponse.id.toString(),
            number = "ENV-0002",
            customerName = shipmentResponse.customerName,
            status = shipmentResponse.status,
            products = listOf(
                Product("P-201", "Monitor", 1,
                    imageUrl = "a",
                    innerLocation = "Pasillo 3, estante 2",
                    currentStock = 100),
                Product("P-202", "Teclado", 1,
                    imageUrl = "a",
                    innerLocation = "Pasillo 3, estante 2",
                    currentStock = 100)
            ),
            creationDate = LocalDateTime.now().minusDays(1)
        )
    }
}