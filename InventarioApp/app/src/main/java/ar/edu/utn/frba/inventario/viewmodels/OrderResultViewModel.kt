package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.order.OrderResponse
import ar.edu.utn.frba.inventario.api.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderResultViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _foundOrder = MutableStateFlow<OrderResponse?>(null)
    var foundOrder: StateFlow<OrderResponse?> = _foundOrder.asStateFlow()

    internal val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    internal val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val orderId: String? = savedStateHandle["orderId"]


    fun loadOrderById(id: String?, codeType: String) {
        _errorMessage.value = null
        _isLoading.value = true
        _foundOrder.value = null

        if (id.isNullOrBlank()) {
            _errorMessage.value = "No se puede buscar una orden con un ID vacío."
            _isLoading.value = false
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (val result = orderRepository.getOrderById(id)) {
                    is NetworkResult.Success -> {
                        _foundOrder.value = result.data
                        _isLoading.value = false
                        Log.d("OrderResultViewModel", "Orden encontrada: ${foundOrder.value}")
                    }
                    is NetworkResult.Error -> {
                        val specificError = if (result.message?.contains("Not Found", ignoreCase = true) == true ||
                            result.message?.contains("404", ignoreCase = true) == true) {
                            "La orden con ID '$id' no fue encontrada."
                        } else {
                            "Orden no encontrada. Intente nuevamente. ${result.message ?: "Error desconocido"}"
                        }
                        _errorMessage.value = specificError // Establece el mensaje de error
                        _isLoading.value = false
                        Log.e("OrderResultViewModel", "Error al cargar orden: ${result.message}")
                    }
                    is NetworkResult.Exception -> {
                        _errorMessage.value = "Excepción al cargar la orden: ${result.e.message}. Por favor, intente de nuevo."
                        _isLoading.value = false
                        Log.e("OrderResultViewModel", "Excepción al cargar orden", result.e)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}. Contacte a soporte si el problema persiste."
                _isLoading.value = false
                Log.e("OrderResultViewModel", "Error inesperado al cargar orden", e)
            }
        }
    }
}