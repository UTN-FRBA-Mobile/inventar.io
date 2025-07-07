package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.order.Order
import ar.edu.utn.frba.inventario.api.repository.OrderRepository
import ar.edu.utn.frba.inventario.api.repository.SelfRepository
import ar.edu.utn.frba.inventario.api.utils.PreferencesManager
import ar.edu.utn.frba.inventario.utils.OrderMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle,
    private val selfRepository: SelfRepository,
    preferencesManager: PreferencesManager
) : BaseItemViewModel<Order>(
    savedStateHandle = savedStateHandle,
    preferencesManager = preferencesManager,
    statusFilterKey = "orders"
) {

    private val _items: SnapshotStateList<Order> = mutableStateListOf()
    override val items: SnapshotStateList<Order> get() = _items

    private val _locationName = MutableStateFlow("")
    val locationName: StateFlow<String> = _locationName

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    override fun getStatus(item: Order) = item.status

    override fun getFilterDate(item: Order) =
        item.creationDate 

    fun getOrders() {
        viewModelScope.launch(Dispatchers.Default) {
            _loading.value = true
            _error.value = null
            when (val ordersResult = orderRepository.getOrdersList()) {
                is NetworkResult.Success -> {
                    Log.d("OrdersViewModel", "Success: ${ordersResult.data}")
                    val ordersParsed = ordersResult.data.map { response ->
                        OrderMapper.toOrder(response)
                    }

                    withContext(Dispatchers.Main) {
                        _items.clear()
                        _items.addAll(ordersParsed)
                    }
                }

                is NetworkResult.Error -> {
                    Log.d(
                        "OrdersViewModel",
                        "Error: code=${ordersResult.code}, message=${ordersResult.message}"
                    )
                    _error.value = ordersResult.message ?: "Error desconocido al cargar pedidos."
                }

                is NetworkResult.Exception -> {
                    Log.d("OrdersViewModel", "Error crítico: ${ordersResult.e.message}")
                    _error.value =
                        ordersResult.e.message ?: "Excepción desconocida al cargar pedidos."
                }
            }
            _loading.value = false
        }
    }
}