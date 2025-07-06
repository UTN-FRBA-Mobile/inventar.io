package ar.edu.utn.frba.inventario.viewmodels.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.repository.ProductRepository
import ar.edu.utn.frba.inventario.utils.ShipmentScanFlowState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProductAmountViewModel @Inject constructor(
    private val repository: ProductRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _currentProductStock = MutableStateFlow<Int?>(null)
    val currentProductStock: StateFlow<Int?> = _currentProductStock

    init {
        loadStock()
    }

    private fun loadStock() {
        val product = ShipmentScanFlowState.scannedProduct
        if (product == null) {
            _errorMessage.value = "Producto no válido"
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                when (val result = repository.getStockByProductId(product.id)) {
                    is NetworkResult.Success -> {
                        val response = result.data
                        val stock = response.stockCount[product.id] ?: 0

                        _currentProductStock.value = stock
                        ShipmentScanFlowState.scannedProductStock = stock
                    }

                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message ?: "Error al obtener stock"
                    }

                    is NetworkResult.Exception -> {
                        _errorMessage.value = "Excepción al obtener stock: ${result.e.message}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
