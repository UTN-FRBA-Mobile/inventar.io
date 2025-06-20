package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.api.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductResultViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _foundProduct = MutableStateFlow<Product?>(null)
    val foundProduct: StateFlow<Product?> = _foundProduct

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadProductByCode(code: String?, codeType: String?) {
        if (code == null || codeType != "ean-13") {
            _errorMessage.value = "Código inválido"
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            when (val result = productRepository.getProductList(listOf(code))) {
                is NetworkResult.Success -> {
                    val product: Product? = result.data.values.firstOrNull()

                    _foundProduct.value = product

                    // Print result data
                    Log.d("ProductResultViewModel", "Product data: ${result.data}")

                    _errorMessage.value = if (product == null) "No se encontró el producto" else null
                }

                is NetworkResult.Error -> {
                    _errorMessage.value = result.message ?: "Error desconocido"
                }

                is NetworkResult.Exception -> _errorMessage.value = "Error desconocido"
            }
            _isLoading.value = false
        }
    }
}
