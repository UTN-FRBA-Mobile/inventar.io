package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.order.Order
import ar.edu.utn.frba.inventario.api.model.order.OrderResponse
import ar.edu.utn.frba.inventario.api.model.product.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(): ViewModel(){

    private val _order  = MutableStateFlow<Order>(Order(id = "0", number = "", sender = "", status = ItemStatus.PENDING, products = emptyList()))
    val order = _order.asStateFlow()


    fun loadOrder(id:String){
        viewModelScope.launch {

            Log.d("OrderDetailViewModel", "Iniciando pedido a API del pedido: $id")
            val response = OrderRepositoryMock(id)

            val result = 200

            when(result){
                200 -> {
                    _order.value = response
                    Log.d("ShipmentViewModel", "Exito en carga de datos del pedido: $id")
                }
                400 -> {
                    Log.d("ShipmentViewModel", "Fallo la carga de datos del pedido: $id")
                }
            }
        }
    }

    //Para pruebas, hasta que este el endponit
    private fun OrderRepositoryMock(id: String):Order{
        val orders: List<Order> =
            listOf(
                Order(
                    id = "1",
                    number = "P-9090",
                    sender = "Cliente Premium",
                    status = ItemStatus.PENDING,
                    products = listOf(
                        Product("P-101", "Producto A", 2,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100),
                        Product("P-102", "Producto B", 3,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    estimatedReceiptDate = LocalDateTime.now().plusDays(2)
                ),
                Order(
                    id = "ORD-002",
                    number = "P-9091",
                    sender = "Cliente Regular",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-201", "Producto C", 5,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().plusDays(1)
                ),
                Order(
                    id = "ORD-003",
                    number = "P-9092",
                    sender = "Cliente VIP",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-301", "Producto D", 1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100),
                        Product("P-302", "Producto E", 2,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().minusDays(1)
                ),
                Order(
                    id = "ORD-004",
                    number = "P-9095",
                    sender = "Cliente Prueba",
                    status = ItemStatus.CANCELLED,
                    products = listOf(
                        Product("P-401", "Producto F", 4,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    cancellationDate = LocalDateTime.now().minusHours(3)
                ),
                Order(
                    id = "ORD-005",
                    number = "P-9099",
                    sender = "Empresa X",
                    status = ItemStatus.CANCELLED,
                    products = List(15) { index ->
                        Product("P-50${index+1}", "Producto ${index+1}", index+1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    },
                    cancellationDate = LocalDateTime.now().plusDays(3)
                ),
                Order(
                    id = "ORD-003",
                    number = "P-9092",
                    sender = "Cliente VIP",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-301", "Producto D", 1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100),
                        Product("P-302", "Producto E", 2,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().minusDays(1)
                ),
                Order(
                    id = "ORD-003",
                    number = "P-9092",
                    sender = "Cliente VIP",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-301", "Producto D", 1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().minusDays(1)
                ),
                Order(
                    id = "ORD-003",
                    number = "P-9092",
                    sender = "Cliente VIP",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-301", "Producto D", 1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100),
                        Product("P-302", "Producto E", 2,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().minusDays(1)
                ),
                Order(
                    id = "ORD-003",
                    number = "P-9092",
                    sender = "Cliente VIP",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-301", "Producto D", 1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100),
                        Product("P-302", "Producto E", 2,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().minusDays(1)
                ),
                Order(
                    id = "ORD-003",
                    number = "P-9092",
                    sender = "Cliente VIP",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-301", "Producto D", 1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100),
                        Product("P-302", "Producto E", 2,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().minusDays(1)
                ),
                Order(
                    id = "ORD-003",
                    number = "P-9092",
                    sender = "Cliente VIP",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-301", "Producto D", 1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().minusDays(1)
                ),
                Order(
                    id = "ORD-003",
                    number = "P-9092",
                    sender = "Cliente VIP",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-301", "Producto D", 1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100),
                        Product("P-302", "Producto E", 2,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().minusDays(1)
                ),
                Order(
                    id = "ORD-003",
                    number = "P-9092",
                    sender = "Cliente VIP",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-301", "Producto D", 1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100),
                        Product("P-302", "Producto E", 2,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().minusDays(1)
                ),
                Order(
                    id = "ORD-003",
                    number = "P-9092",
                    sender = "Cliente VIP",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-301", "Producto D", 1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100),
                        Product("P-302", "Producto E", 2,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().minusDays(1)
                ),
                Order(
                    id = "ORD-003",
                    number = "P-9092",
                    sender = "Cliente VIP",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-301", "Producto D", 1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100),
                        Product("P-302", "Producto E", 2,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().minusDays(1)
                ),
                Order(
                    id = "ORD-003",
                    number = "P-9092",
                    sender = "Cliente VIP",
                    status = ItemStatus.COMPLETED,
                    products = listOf(
                        Product("P-301", "Producto D", 1,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100),
                        Product("P-302", "Producto E", 2,
                            imageUrl = "a",
                            innerLocation = "Pasillo 3, estante 2",
                            currentStock = 100)
                    ),
                    confirmedReceiptDate = LocalDateTime.now().minusDays(1)
                ),
            )


        val result = orders.first { s -> s.id.equals(id) }

        return result
    }
/*
    fun getOrderById(orderId: String): OrderResponse {
        viewModelScope.launch(Dispatchers.Default) {
            when (val ordersResult = orderRepository.getOrder(orderId)) {
                is NetworkResult.Success -> {
                    Log.d("OrdersViewModel", "Success: ${ordersResult.data}")
                    val ordersParsed = ordersResult.data?.map { response ->
                        parseMapOrder(response)
                    } ?: emptyList()

                    // Actualizar la lista en el hilo principal
                    withContext(Dispatchers.Main) {
                        _items.clear()
                        _items.addAll(ordersParsed) // Solo esta línea es necesaria
                    }

                }
                is NetworkResult.Error -> {
                    Log.d("OrdersViewModel", "Error: code=${ordersResult.code}, message=${ordersResult.message}")
                }
                is NetworkResult.Exception -> {
                    Log.d("OrdersViewModel", "Error crítico: ${ordersResult.e.message}")
                }
            }
        }
    }*/
}



