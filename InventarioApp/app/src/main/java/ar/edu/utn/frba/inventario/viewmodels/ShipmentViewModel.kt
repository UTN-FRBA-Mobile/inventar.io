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
import ar.edu.utn.frba.inventario.api.repository.ShipmentRepository
import ar.edu.utn.frba.inventario.events.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ShipmentViewModel @Inject constructor(private val shipmentRepository: ShipmentRepository):ViewModel(){
    private val _shipment  = MutableStateFlow<Shipment>(Shipment(id = "0", number = "", customerName = ""))
    val shipment = _shipment.asStateFlow()

    val productToScanList = mutableStateListOf<ProductToScan>()

    val isStateCompleteShipment: MutableState<Boolean> = mutableStateOf(false)

    private val _navigationEvent = MutableSharedFlow<NavigationEvent?>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun loadShipment(id:String){
        viewModelScope.launch {

            Log.d("ShipmentViewModel", "Iniciando pedido a API del envio: $id")
            val response = shipmentRepositoryMock(id)

            val res = shipmentRepository.getShipment(7)

            val result = 200

            when(result){
                200 -> {
                    _shipment.value = response
                    loadProductToScanList(response.products)
                    Log.d("ShipmentViewModel", "Exito en carga de datos del envio: $id")
                }
                400 -> {
                    Log.d("ShipmentViewModel", "Fallo la carga de datos del envio: $id")
                }
            }
        }
    }

    private fun loadProductToScanList(products:List<Product>){
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

    //Para pruebas, hasta que este el endponit
    private fun shipmentRepositoryMock(id: String):Shipment{

        val envios: List<Shipment> =
            listOf(
                Shipment(
                    id = "S01-9",
                    number = "ENV-0009",
                    customerName = "Este es un nombre tan largo que no debería entrar",
                    //status = ShipmentStatus.COMPLETED,
                    products = listOf(
                        Product(
                            "P-101", "AAAA", 1,
                            innerLocation = "est",
                            currentStock = 22,
                            imageUrl = "a"
                        ),
                        Product(
                            "P-102", "BBBB", 2,
                            innerLocation = "est",
                            currentStock = 22,
                            imageUrl = "a"
                        )
                    ),
                    creationDate = LocalDateTime.now().minusDays(3)
                ),
                Shipment(
                    id = "S01-10",
                    number = "ENV-0010",
                    customerName = "Dibu Martínez",
                    //status = ShipmentStatus.BLOCKED,
                    products = listOf(
                        Product(
                            "P-101", "AAAA", 1,
                            innerLocation = "est",
                            currentStock = 22,
                            imageUrl = "a"
                        ),
                        Product(
                            "P-102", "BBBB", 2,
                            innerLocation = "est",
                            currentStock = 22,
                            imageUrl = "a"
                        )
                    ),
                    creationDate = LocalDateTime.now().minusDays(3)
                ),
                Shipment(
                    id = "S01-1",
                    number = "ENV-0001",
                    customerName = "Enzo Fernández",
                    //status = ShipmentStatus.PENDING,
                    products = listOf(
                        Product("P-101", "Resma de papel A4", 1,
                            innerLocation = "est",
                            currentStock = 22,
                            imageUrl = "a"
                        )
                    ),
                    creationDate = LocalDateTime.now().minusDays(3)
                ),
                Shipment(
                    id = "S01-2",
                    number = "ENV-0002",
                    customerName = "Lionel Messi",
                    //status = ShipmentStatus.IN_PROGRESS,
                    products = listOf(
                        Product("P-201", "Monitor", 1,
                            innerLocation = "est",
                            currentStock = 22,
                            imageUrl = "a")),
                    creationDate = LocalDateTime.now().minusDays(1)
                ),
                Shipment(
                    id = "S01-3",
                    number = "ENV-0003",
                    customerName = "UTN FRBA",
                    //status = ShipmentStatus.PENDING,
                    products = listOf(
                        Product("P-301", "ASDADS", 1,
                            innerLocation = "est",
                            currentStock = 22,
                            imageUrl = "a"),
                        Product("P-302", "ADADASD", 3,
                            innerLocation = "est",
                            currentStock = 22,
                            imageUrl = "a")
                    ),
                    creationDate = LocalDateTime.now().minusHours(5)
                ),
                Shipment(
                    id = "S01-4",
                    number = "ENV-0004",
                    customerName = "Julián Álvarez",
                    //status = ShipmentStatus.COMPLETED,
                    products = listOf(
                        Product("P-401", "Tablet", 2,
                            innerLocation = "est",
                            currentStock = 22,
                            imageUrl = "a"),
                        Product("P-402", "Fundas", 2,
                            innerLocation = "est",
                            currentStock = 22,
                            imageUrl = "a")
                    ),
                    creationDate = LocalDateTime.now().minusDays(2)
                ),
                Shipment(
                    id = "S01-5",
                    number = "ENV-0005",
                    customerName = "Juan Pérez",
                    //status = ShipmentStatus.IN_PROGRESS,
                    products = listOf(
                        Product(
                            "P-501", "adasd", 1,
                            innerLocation = "est",
                            currentStock = 22,
                            imageUrl = "a"
                        ),
                        Product("P-502", "aaaaaaa", 1,
                            innerLocation = "est",
                            currentStock = 22,
                            imageUrl = "a")
                    ),
                    creationDate = LocalDateTime.now().minusHours(12)
                )
        )



        val result = envios.first { s -> s.id.equals(id) }

        return result
    }
}

//Luego usar uno que se defina en api/model
data class Shipment(
    val id: String, // Al principio del id podría tener el código de sucursal
    val number: String,
    val customerName: String,
    //val status: ShipmentStatus,
    val products: List<Product> = listOf(
        Product("P-101", "Resma de papel A4", 1,
            innerLocation = "Pasillo 5 • Estante 3",
            currentStock = 222,
            imageUrl = "a"),
        Product("P-002", "Producto Ejemplo 2", 2,
            innerLocation = "est",
            currentStock = 22,
            imageUrl = "a")
    ),
    val creationDate: LocalDateTime = LocalDateTime.now(), // esto variaría según fecha de creación
    val responsible: String? = "Sin responsable"
)