package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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

    override val items: SnapshotStateList<Shipment> = mutableStateListOf<Shipment>()

    fun getShipments() {
        viewModelScope.launch(Dispatchers.Default) {
            when (val shipmentResult = shipmentRepository.getShipmentList()) {
                is NetworkResult.Success -> {
                    Log.d("UserViewModel", "Success: ${shipmentResult.data}")
                    val shipmentsParsed = shipmentResult.data.map { sr -> parseMapShipment(sr) }
                    items.apply { addAll(shipmentsParsed) }
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
            number = "S${shipmentResponse.idLocation}E${shipmentResponse.id}",
            customerName = shipmentResponse.customerName,
            status = shipmentResponse.status,
            products = shipmentResponse.productAmount.map { pa->
               Product(id = pa.key.toString(), name = "generic", quantity = pa.value,
               imageUrl = "a",
               innerLocation = "Pasillo 3, estante 2",
               currentStock = 100)
               },
            creationDate = LocalDateTime.now().minusDays(1)
        )
    }
}