package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.api.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _productDetail: MutableStateFlow<Product?> = MutableStateFlow(null)
    val productDetail = _productDetail.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val productId: String = savedStateHandle["productId"] ?: ""

    init {
        if (productId.isNotEmpty()) {
            loadProductDetails(productId)
        } else {
            _isLoading.value = false
            _errorMessage.value = "ID de producto no proporcionado."
        }
    }

    private fun loadProductDetails(id: String) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val detailsResult = productRepository.getProductListById(listOf(id))
                Log.e("ProductDetailViewModel", "getProductListById: $detailsResult")

                val stockLocationResult = productRepository.getStockByProductId(id)
                Log.e("ProductDetailViewModel", "getStockByProductId: $stockLocationResult")

                var loadedProduct: Product? = null

                if (detailsResult is NetworkResult.Success) {
                    val details = detailsResult.data[id]
                    loadedProduct = Product(
                        id = id,
                        name = details?.name.toString(),
                        description = details?.description.toString(),
                        ean13 = details?.ean13.toString(),
                        imageURL = details?.imageURL,
                        innerLocation = null,
                        currentStock = 0
                    )
                } else if (detailsResult is NetworkResult.Error) {
                    _errorMessage.value = "Error al cargar detalles: ${detailsResult.message}"
                    Log.e(
                        "ProductDetailViewModel",
                        "Error loading product details: ${detailsResult.message}"
                    )
                } else if (detailsResult is NetworkResult.Exception) {
                    _errorMessage.value = "Excepción al cargar detalles: ${detailsResult.e.message}"
                    Log.e(
                        "ProductDetailViewModel",
                        "Exception loading product details",
                        detailsResult.e
                    )
                }

                if (stockLocationResult is NetworkResult.Success) {
                    val stockLocation = stockLocationResult.data
                    val productCurrentStock = stockLocation.stockCount[id]
                    val productInnerLocation = stockLocation.locationDetails[id]

                    loadedProduct = loadedProduct?.copy(
                        innerLocation = productInnerLocation,
                        currentStock = productCurrentStock
                    )
                } else if (stockLocationResult is NetworkResult.Error) {
                    _errorMessage.value =
                        "Error al cargar stock/ubicación: ${stockLocationResult.message}"
                    Log.e(
                        "ProductDetailViewModel",
                        "Error loading product stock/location: ${stockLocationResult.message}"
                    )
                } else if (stockLocationResult is NetworkResult.Exception) {
                    _errorMessage.value =
                        "Excepción al cargar stock/ubicación: ${stockLocationResult.e.message}"
                    Log.e(
                        "ProductDetailViewModel",
                        "Exception loading product stock/location",
                        stockLocationResult.e
                    )
                }

                if (loadedProduct != null) {
                    _productDetail.value = loadedProduct
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("ProductDetailViewModel", "Unexpected error loading product details", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}