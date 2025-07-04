package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.product.ProductOperation
import ar.edu.utn.frba.inventario.api.model.shipment.Shipment
import ar.edu.utn.frba.inventario.api.model.shipment.ShipmentResponse
import ar.edu.utn.frba.inventario.api.repository.ShipmentRepository
import ar.edu.utn.frba.inventario.api.utils.PreferencesManager
import ar.edu.utn.frba.inventario.utils.ShipmentProductToScanList
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@HiltViewModel
class ShipmentsViewModel @Inject constructor(
    private val shipmentRepository: ShipmentRepository,
    savedStateHandle: SavedStateHandle,
    preferencesManager: PreferencesManager
) : BaseItemViewModel<Shipment>(
    savedStateHandle = savedStateHandle,
    preferencesManager = preferencesManager,
    statusFilterKey = "shipments"
) {
    private val _shipments = MutableStateFlow<List<ShipmentResponse>>(emptyList())
    val shipments: StateFlow<List<ShipmentResponse>> = _shipments.asStateFlow()

    private val _items: SnapshotStateList<Shipment> = mutableStateListOf()
    override val items: SnapshotStateList<Shipment> get() = _items

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun getShipments() {
        viewModelScope.launch(Dispatchers.Default) {
            _loading.value = true
            _error.value = null
            when (val shipmentResult = shipmentRepository.getShipmentList()) {
                is NetworkResult.Success -> {
                    Log.d("UserViewModel", "Success: ${shipmentResult.data}")
                    val shipmentsParsed = shipmentResult.data.map { sr -> parseMapShipment(sr) }

                    _items.clear()
                    _items.apply { addAll(shipmentsParsed) }
                    ShipmentProductToScanList.clear()
                }
                is NetworkResult.Error -> {
                    Log.d("UserViewModel", "Error: code=${shipmentResult.code}, message=${shipmentResult.message}")
                    _error.value = shipmentResult.message ?: "Error desconocido al cargar envíos."
                }
                is NetworkResult.Exception -> {
                    Log.d("UserViewModel", "Error crítico: ${shipmentResult.e.message}")
                    _error.value = shipmentResult.e.message ?: "Excepción desconocida al cargar envíos."
                }
            }
            _loading.value = false
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
               ProductOperation(
                   id = pa.key.toString(), name = "generic", quantity = pa.value,
                   )
               },
            creationDate = LocalDateTime.now().minusDays(1)
        )
    }
}