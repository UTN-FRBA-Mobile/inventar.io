package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.order.Order
import ar.edu.utn.frba.inventario.api.model.order.OrderResponse
import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.api.repository.OrderRepository
import ar.edu.utn.frba.inventario.utils.toLocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle
) : BaseItemViewModel<Order>(
    savedStateHandle = savedStateHandle,
    filterKey = "orders_filter"
) {

    private val _orders = MutableStateFlow<List<OrderResponse>>(emptyList())
    val orders: StateFlow<List<OrderResponse>> = _orders.asStateFlow()

    private val _items: SnapshotStateList<Order> = mutableStateListOf()
    override val items: SnapshotStateList<Order> get() = _items


    override fun getStatus(item: Order) = item.status

    override fun getFilterDate(item: Order) = item.creationDate //TODO analizar si usamos creationDate o deberíamos ordenar por otra fecha

    fun getOrders() {
        viewModelScope.launch(Dispatchers.Default) {
            when (val ordersResult = orderRepository.getOrdersList()) {
                is NetworkResult.Success -> {
                    Log.d("OrdersViewModel", "Success: ${ordersResult.data}")
                    val ordersParsed = ordersResult.data?.map { response ->
                        parseMapOrder(response)
                    } ?: emptyList()

                    withContext(Dispatchers.Main) {
                        _items.clear()
                        _items.addAll(ordersParsed)
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
    }

    fun parseMapOrder(orderResponse: OrderResponse): Order{
        return Order(
            id = orderResponse.id.toString(),
            number = "P-0002",
            sender = orderResponse.sender,
            status = orderResponse.status,
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
            creationDate = orderResponse.creationDate.toLocalDateTime()
        )
    }



    /*
    val creationDate: String,
    val scheduledDate: String,
    val lastModifiedDate: String,

     override val confirmedReceiptDate: LocalDateTime? = null,
    override val estimatedReceiptDate: LocalDateTime? = null,
    override val cancellationDate: LocalDateTime? = null,
    override val creationDate: LocalDateTime = LocalDateTime.now()
     */


}