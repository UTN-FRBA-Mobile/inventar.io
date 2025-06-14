package ar.edu.utn.frba.inventario.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.product.ProductResponse
import ar.edu.utn.frba.inventario.api.model.shipment.ShipmentResponse
import ar.edu.utn.frba.inventario.api.repository.ProductRepository
import ar.edu.utn.frba.inventario.api.repository.ShipmentRepository
import ar.edu.utn.frba.inventario.events.NavigationEvent
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
class ShipmentViewModel @Inject constructor(private val shipmentRepository: ShipmentRepository, private val productRepository: ProductRepository):ViewModel(){
    private val _shipment  = MutableStateFlow<Shipment>(Shipment(id = "0", number = "", customerName = ""))
    val shipment = _shipment.asStateFlow()

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

            Log.d("ShipmentViewModel", "Iniciando pedido a API del envio: $id")

            val result = shipmentRepository.getShipment(id.toLong())

            when(result){
                is NetworkResult.Success -> {
                    Log.d("ShipmentViewModel", "Success:${result.data.customerName}")

                    val ean13ProductList = result.data.productAmount.map { pa -> pa.key.toString() }
                    Log.d("ShipmentViewModel", "ean13 productos del envio:$ean13ProductList")

                    val shipmentProducts = productRepository.getProductList(ean13ProductList)

                    when(shipmentProducts){
                        is NetworkResult.Success -> {
                            Log.d("ShipmentViewModel", "Success products:${shipmentProducts.data}")

                            _shipment.value = parseShipment(result.data,shipmentProducts.data)
                        }
                        is NetworkResult.Error -> {
                            Log.d("ShipmentViewModel", "Error Productos: Code=${shipmentProducts.code}, message=${shipmentProducts.message}")
                        }
                        is NetworkResult.Exception -> {
                            Log.d("ShipmentViewModel", "Error Crítico Productos: ${shipmentProducts.e.message}")
                        }
                    }

                    loadProductToScanList(_shipment.value.products)
                    Log.d("ShipmentViewModel", "contenido de ProductsToScan:$productToScanList")
                }
                is NetworkResult.Error -> {
                    Log.d("ShipmentViewModel", "Error: Code=${result.code}, message=${result.message}")
                }
                is NetworkResult.Exception -> {
                    Log.d("ShipmentViewModel", "Error Crítico: ${result.e.message}")
                }
            }
        }
    }

    private fun loadProductToScanList(products:List<Product>){
        productToScanList.removeAll(productToScanList)
        products.forEach { p->productToScanList.add(ProductToScan(
            id = p.id, requiredQuantity = p.quantity,
            innerLocation = "",
            currentStock = 222
        )) }
    }

    fun getLoadedQuantityProduct(id:String):Int{
        return productToScanList.first { ps -> ps.id == id }.loadedQuantity.value
    }

    fun setLoadedQuantityProduct(id:String, newValue: Int){
        Log.d("ShipmentViewModel", "Se inicia con la actualizacion valor de loadedQuantity del producto $id")
        productToScanList.forEach { p-> if (p.id==id)
        {
            p.loadedQuantity.value = newValue
            Log.d("ShipmentViewModel", "Se actualizo el valor de loadedQuantity a ${p.loadedQuantity.value} del producto $id")
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

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun getProductById(productId: String): Product {
        return try {
            _shipment.value.products.first { it.id == productId }
        } catch (e: NoSuchElementException) {
            Log.e("ShipmentViewModel", "Producto no encontrado: $productId")
            //TODO: en lugar de mostrar la screen de producto cuando no se encuentra un producto, mostrar solo un mensaje de producto no identificado, como cuando se filtra y no hay resultados
            Product(
                id = stringResource(R.string.unknown_product_id), name = stringResource(R.string.product_not_found), quantity = 0,
                innerLocation = stringResource(R.string.no_location_assigned),
                currentStock = 0,
                imageUrl = ""
            )
        }
    }

    data class ProductToScan(
        val id: String,
        val requiredQuantity: Int,
        val loadedQuantity: MutableState<Int> = mutableStateOf(0),
        val innerLocation: String,
        val currentStock: Int

    )

    fun parseShipment(shipmentResponse:ShipmentResponse, productList: Map<Long,ProductResponse>):Shipment{

        Log.d("ShipmentViewModel", "Shipment to parse: $shipmentResponse")

        val shipmentProducts = shipmentResponse.productAmount.map { pa ->
            Product(id=pa.key.toString(), name= productList.values.first { p->p.ean13 == pa.key.toString() }.name, quantity = pa.value,
            innerLocation = "est",
            currentStock = 22,
            imageUrl = "a") }
        val shipment  = Shipment(
            id=shipmentResponse.id.toString(),
            number = "S${shipmentResponse.idLocation}E${shipmentResponse.id}",
            customerName = shipmentResponse.customerName,
            products = shipmentProducts
        )

        return shipment
    }
}

//Luego usar uno que se defina en api/model
data class Shipment(
    var id: String, // Al principio del id podría tener el código de sucursal
    var number: String,
    var customerName: String,
    //val status: ShipmentStatus,
    var products: List<Product> = listOf(
        Product("P-101", "Resma de papel A4", 1,
            innerLocation = "Pasillo 5 • Estante 3",
            currentStock = 222,
            imageUrl = "a"),
        Product("P-002", "Producto Ejemplo 2", 2,
            innerLocation = "est",
            currentStock = 22,
            imageUrl = "a")
    ),
    var creationDate: LocalDateTime = LocalDateTime.now(), // esto variaría según fecha de creación
    var responsible: String? = "Sin responsable"
)