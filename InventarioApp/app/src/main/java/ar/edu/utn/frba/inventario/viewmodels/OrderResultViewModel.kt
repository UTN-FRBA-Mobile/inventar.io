package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.order.OrderResponse
import ar.edu.utn.frba.inventario.api.repository.OrderRepository
import ar.edu.utn.frba.inventario.utils.OrderProductsListArgs
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.withNavArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OrderResultViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _foundOrder = MutableStateFlow<OrderResponse?>(null)
    var foundOrder: StateFlow<OrderResponse?> = _foundOrder.asStateFlow()

    internal val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    internal val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    internal val _startOrderLoading = MutableStateFlow(false)
    val startOrderLoading: StateFlow<Boolean> = _startOrderLoading.asStateFlow()

    internal val _startOrderError = MutableStateFlow<String?>(null)
    val startOrderError: StateFlow<String?> = _startOrderError.asStateFlow()

    private val orderId: String? = savedStateHandle["orderId"]

    fun loadOrderById(id: String?, codeType: String) {
        _errorMessage.value = null
        _isLoading.value = true
        _foundOrder.value = null
        _startOrderError.value = null

        if (id.isNullOrBlank()) {
            _errorMessage.value = "No se puede buscar un pedido con un ID vacío."
            _isLoading.value = false
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (val result = orderRepository.getOrderById(id)) {
                    is NetworkResult.Success -> {
                        _foundOrder.value = result.data
                        _isLoading.value = false
                        Log.d("OrderResultViewModel", "Pedido encontrado: ${foundOrder.value}")
                    }

                    is NetworkResult.Error -> {
                        val specificError =
                            if (result.message?.contains("Not Found", ignoreCase = true) == true ||
                                result.message?.contains("404", ignoreCase = true) == true
                            ) {
                                "El pedido con ID '$id' no fue encontrado."
                            } else {
                                "Orden no encontrada. Intente nuevamente. ${result.message ?: "Error desconocido"}"
                            }
                        _errorMessage.value = specificError
                        _isLoading.value = false
                        Log.e("OrderResultViewModel", "Error al cargar orden: ${result.message}")
                    }

                    is NetworkResult.Exception -> {
                        _errorMessage.value =
                            "Excepción al cargar el pedido: ${result.e.message}. Por favor, intente de nuevo."
                        _isLoading.value = false
                        Log.e("OrderResultViewModel", "Excepción al cargar orden", result.e)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value =
                    "Error inesperado: ${e.message}. Contacte a soporte si el problema persiste."
                _isLoading.value = false
                Log.e("OrderResultViewModel", "Error inesperado al cargar orden", e)
            }
        }
    }

    fun handleContinueButtonClick(navController: NavController) {
        _startOrderError.value = null

        val currentOrder = _foundOrder.value
        if (currentOrder == null) {
            _startOrderError.value = "No hay orden cargada para continuar."
            return
        }

        if (currentOrder.status == ItemStatus.PENDING) {
            currentOrder.id?.let { orderIdLong ->
                viewModelScope.launch(Dispatchers.IO) {
                    _startOrderLoading.value = true
                    when (val result = orderRepository.startOrder(orderIdLong)) {
                        is NetworkResult.Success -> {
                            _foundOrder.value = result.data
                            _startOrderLoading.value = false
                            withContext(Dispatchers.Main) {
                                navigateToOrderProductsList(navController, orderIdLong.toString())
                            }
                        }

                        is NetworkResult.Error -> {
                            _startOrderError.value =
                                "Error al iniciar el pedido: ${result.message ?: "Desconocido"}"
                            _startOrderLoading.value = false
                            Log.e(
                                "OrderResultViewModel",
                                "Error al iniciar orden: ${result.message}",
                            )
                        }

                        is NetworkResult.Exception -> {
                            _startOrderError.value =
                                "Excepción al iniciar el pedido: ${result.e.message}"
                            _startOrderLoading.value = false
                            Log.e("OrderResultViewModel", "Excepción al iniciar orden", result.e)
                        }
                    }
                }
            } ?: run {
                _startOrderError.value = "ID de orden no disponible para iniciar."
            }
        } else {
            currentOrder.id?.let { orderIdLong ->
                viewModelScope.launch(Dispatchers.Main) {
                    navigateToOrderProductsList(navController, orderIdLong.toString())
                }
            } ?: run {
                _startOrderError.value = "ID de orden no disponible para continuar."
            }
        }
    }

    private fun navigateToOrderProductsList(navController: NavController, orderId: String) {
        val destination = Screen.OrderProductsList.withNavArgs(
            OrderProductsListArgs.OrderId to orderId,
        )
        navController.navigate(destination)
    }
}
