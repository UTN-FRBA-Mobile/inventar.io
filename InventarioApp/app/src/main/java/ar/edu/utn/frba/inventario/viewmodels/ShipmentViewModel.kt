package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.events.NavigationEvent
import ar.edu.utn.frba.inventario.utils.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ShipmentViewModel @Inject constructor():ViewModel(){
    private val _shipment  = MutableStateFlow<Shipment>(Shipment(id = "0", number = "", customerName = ""))
    val shipment = _shipment.asStateFlow()

    val productToScanList = mutableStateListOf<ProductToScan>()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent?>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun loadShipment(id:Int){
        viewModelScope.launch {

            Log.d("ShipmentViewModel", "Iniciando pedido a API del envio: $id")
            val response = shipmentRepositoryMock(id)

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
        products.forEach { p->productToScanList.add(ProductToScan(id = p.id, requiredQuantity = p.quantity)) }
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
    }
    fun isProductCompleted(id:String):Boolean{
        val prodToScan = productToScanList.first { ps -> ps.id == id }
        val isCompletedProduct = prodToScan.requiredQuantity == prodToScan.loadedQuantity.value
        return isCompletedProduct
    }

    data class ProductToScan(
        val id: String,
        val requiredQuantity: Int,
        val loadedQuantity: MutableState<Int> = mutableStateOf(0)
    )

    //Para pruebas, hasta que este el endponit
    private fun shipmentRepositoryMock(id: Int):Shipment{

        val envios: List<Shipment> =
            listOf(
                Shipment(
                    id = "S01-9",
                    number = "ENV-0009",
                    customerName = "Este es un nombre tan largo que no debería entrar",
                    //status = ShipmentStatus.COMPLETED,
                    products = listOf(
                        Product("P-101", "AAAA", 1),
                        Product("P-102", "BBBB", 2)
                    ),
                    creationDate = LocalDateTime.now().minusDays(3)
                ),
                Shipment(
                    id = "S01-10",
                    number = "ENV-0010",
                    customerName = "Dibu Martínez",
                    //status = ShipmentStatus.BLOCKED,
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
                    //status = ShipmentStatus.PENDING,
                    products = listOf(
                        Product("P-101", "AAAA", 1)
                    ),
                    creationDate = LocalDateTime.now().minusDays(3)
                ),
                Shipment(
                    id = "S01-2",
                    number = "ENV-0002",
                    customerName = "Lionel Messi",
                    //status = ShipmentStatus.IN_PROGRESS,
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
                    //status = ShipmentStatus.PENDING,
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
                    //status = ShipmentStatus.COMPLETED,
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
                    //status = ShipmentStatus.IN_PROGRESS,
                    products = listOf(
                        Product("P-501", "adasd", 1),
                        Product("P-502", "aaaaaaa", 1)
                    ),
                    creationDate = LocalDateTime.now().minusHours(12)
                )
        )



        val result = envios.first { s -> s.id.equals("S01-$id") }

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
        Product("P-001", "Producto Ejemplo 1", 1),
        Product("P-002", "Producto Ejemplo 2", 2)
    ),
    val creationDate: LocalDateTime = LocalDateTime.now(), // esto variaría según fecha de creación
    val responsible: String? = "Sin responsable"
)
//Luego usar uno que se defina en api/model
data class Product(
    val id: String,
    val name: String,
    val quantity: Int
)


//enum class ShipmentStatus(
//    val color: Color,
//    val displayName: String,
//    val iconResourceId: Int
//) {
//    PENDING(
//        color = GreyPending,
//        displayName = "Pendiente",
//        iconResourceId = R.drawable.pending
//    ),
//    IN_PROGRESS(
//        color = YellowInProgress,
//        displayName = "En Progreso",
//        iconResourceId = R.drawable.in_progress
//    ),
//    COMPLETED(
//        color = GreenCompleted,
//        displayName = "Completado",
//        iconResourceId = R.drawable.completed
//    ),
//    BLOCKED(
//        color = RedBlocked,
//        displayName = "Bloqueado",
//        iconResourceId = R.drawable.blocked
//    )
//}