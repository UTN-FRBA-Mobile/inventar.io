package ar.edu.utn.frba.inventario.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.order.Order
import ar.edu.utn.frba.inventario.api.repository.OrderRepository
import ar.edu.utn.frba.inventario.utils.OrderMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository
): ViewModel(){

    companion object {
        val UnknownError = R.string.unknown_error
        val CriticalError = R.string.critical_error
    }

    private val _order  = MutableStateFlow<Order?>(null)
    val order = _order.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadOrder(id: String) {
        viewModelScope.launch {

            _loading.value = true
            _error.value = null

            when (val result = orderRepository.getOrderById(id)) {
                is NetworkResult.Success -> {
                    _order.value = OrderMapper.toOrder(result.data)
                }
                is NetworkResult.Error -> {
                    _error.value = (result.message ?: UnknownError).toString()
                }
                is NetworkResult.Exception -> {
                    _error.value = (result.e.message ?: CriticalError).toString()
                }
            }
            _loading.value = false
        }
    }
}