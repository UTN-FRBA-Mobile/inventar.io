package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.product.ProductOperation
import ar.edu.utn.frba.inventario.api.model.shipment.Shipment
import ar.edu.utn.frba.inventario.api.model.shipment.ShipmentResponse
import ar.edu.utn.frba.inventario.api.repository.ShipmentRepository
import ar.edu.utn.frba.inventario.events.NavigationEvent
import ar.edu.utn.frba.inventario.utils.ShipmentProductToScanList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ShipmentDetailViewModel @Inject constructor(
    private val shipmentRepository: ShipmentRepository
):ViewModel(){
    private val _shipment  = MutableStateFlow<Shipment>(Shipment(
        id = "0", number = "",
        customerName = "",
        status = ItemStatus.PENDING,
        products = listOf(),
        creationDate = LocalDateTime.now()
    ))
    val selectedShipment = _shipment.asStateFlow()

    val productToScanList = mutableStateListOf<ProductToScan>(ProductToScan(id = "P-101",
        requiredQuantity = 1,
        innerLocation = "",
        currentStock = 222),ProductToScan(id = "P-002",
        requiredQuantity = 2,
        innerLocation = "est",
        currentStock = 22)
    )

    val isStateCompleteShipment: MutableState<Boolean> = mutableStateOf(false)

    private val _navigationEvent = MutableSharedFlow<NavigationEvent?>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun loadShipment(id:String){
        viewModelScope.launch(Dispatchers.IO) {

            Log.d("ShipmentDetailViewModel", "Iniciando pedido a API del envio: $id")

            val result = shipmentRepository.getShipment(id.toLong())

            when(result){
                is NetworkResult.Success -> {
                    Log.d("ShipmentDetailViewModel", "Success:${result.data.customerName}")

                    _shipment.value = parseShipment(result.data)

                    loadProductToScanList(_shipment.value.products)
                    Log.d("ShipmentDetailViewModel", "contenido de ProductsToScan:$productToScanList")
                }
                is NetworkResult.Error -> {
                    Log.d("ShipmentDetailViewModel", "Error: Code=${result.code}, message=${result.message}")
                }
                is NetworkResult.Exception -> {
                    Log.d("ShipmentDetailViewModel", "Error Crítico: ${result.e.message}")
                }
            }
        }
    }

    private fun loadProductToScanList(products:List<ProductOperation>){
        if(!ShipmentProductToScanList.statusShipmentProductToScanList()){
            productToScanList.removeAll(productToScanList)
            products.forEach { p->productToScanList.add(ProductToScan(
                id = p.id, requiredQuantity = p.quantity,
                innerLocation = "",
                currentStock = 222
            ))
                ShipmentProductToScanList.addProduct(productId = p.id, loadedQuantity = 0)
            }
            ShipmentProductToScanList.activeShipmentProductToScanList()
        }else{
            ShipmentProductToScanList.getLoadedProducts().forEach { mp-> setLoadedQuantityProduct(mp.key,mp.value)}
        }

    }

    fun getLoadedQuantityProduct(id:String):Int{
        return productToScanList.first { ps -> ps.id == id }.loadedQuantity.value
    }

    fun setLoadedQuantityProduct(id:String, newValue: Int){
        Log.d("ShipmentDetailViewModel", "Se inicia con la actualizacion valor de loadedQuantity del producto $id")
        productToScanList.forEach { p-> if (p.id==id)
        {
            p.loadedQuantity.value = newValue
            Log.d("ShipmentDetailViewModel", "Se actualizo el valor de loadedQuantity a ${p.loadedQuantity.value} del producto $id")
        }
        }
        isCompletedShipment()
    }
    fun getProductStatus(id:String):ItemStatus{
        val prodToScan = productToScanList.first { ps -> ps.id == id }

        var productStatus = ItemStatus.PENDING

        if(prodToScan.requiredQuantity == prodToScan.loadedQuantity.value){
            productStatus = ItemStatus.COMPLETED
        }else if (prodToScan.requiredQuantity < prodToScan.loadedQuantity.value){
            productStatus = ItemStatus.BLOCKED
        }
        return productStatus
    }

    fun isCompletedShipment(){
        isStateCompleteShipment.value = productToScanList.all { ps -> ps.requiredQuantity == ps.loadedQuantity.value}
    }

    data class ProductToScan(
        val id: String,
        val requiredQuantity: Int,
        val loadedQuantity: MutableState<Int> = mutableStateOf(0),
        val innerLocation: String,
        val currentStock: Int
    )

    fun parseShipment(shipmentResponse:ShipmentResponse):Shipment{
        Log.d("ShipmentDetailViewModel", "Shipment to parse: $shipmentResponse")

        val shipmentProducts = shipmentResponse.productAmount.map { pa ->
            ProductOperation(
                id = pa.key.toString(),
                name = shipmentResponse.productNames.get(pa.key) ?: "",
                quantity = pa.value,
            )

            // ProductByShipment
            // - Product <--
            // - Shipment <--
            // - Cantidad

        }
        val shipment  = Shipment(
            id = shipmentResponse.id.toString(),
            number = "D${shipmentResponse.idLocation}-E${shipmentResponse.id}",
            customerName = shipmentResponse.customerName,
            products = shipmentProducts,
            status = shipmentResponse.status,
            creationDate = LocalDateTime.parse(shipmentResponse.creationDate.replace("Z", ""))
        )
        return shipment
    }
}