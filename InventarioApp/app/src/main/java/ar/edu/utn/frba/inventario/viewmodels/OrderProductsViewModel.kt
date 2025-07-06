package ar.edu.utn.frba.inventario.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.order.Order
import ar.edu.utn.frba.inventario.api.model.order.OrderResponse
import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.api.model.product.ProductResponse
import ar.edu.utn.frba.inventario.api.repository.OrderRepository
import ar.edu.utn.frba.inventario.api.repository.ProductRepository
import ar.edu.utn.frba.inventario.utils.OrderMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class OrderProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _orderProducts = MutableStateFlow<List<Product>>(emptyList())
    val orderProducts: StateFlow<List<Product>> = _orderProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isFinishingOrder = MutableStateFlow(false)
    val isFinishingOrder: StateFlow<Boolean> = _isFinishingOrder.asStateFlow()

    private val _finishOrderError = MutableStateFlow<String?>(null)
    val finishOrderError: StateFlow<String?> = _finishOrderError.asStateFlow()

    private val _orderFinishedSuccessfully = MutableStateFlow<Boolean?>(null)
    val orderFinishedSuccessfully: StateFlow<Boolean?> = _orderFinishedSuccessfully.asStateFlow()

    private val orderId: String = savedStateHandle["orderId"] ?: ""

    private val _showCompleteOrderConfirmationDialog = MutableStateFlow(false)
    val showCompleteOrderConfirmationDialog = _showCompleteOrderConfirmationDialog.asStateFlow()

    init {
        if (orderId.isNotEmpty()) {
            Log.d("OrderProductsViewModel", "Initializing with orderId: $orderId")
            loadOrderProducts(orderId)
        } else {
            handleMissingOrderId()
        }
    }

    private fun handleMissingOrderId() {
        _isLoading.value = false
        _errorMessage.value = "ID de pedido no proporcionado."
        _orderProducts.value = emptyList()
        Log.e("OrderProductsViewModel", "Order ID not provided in navigation arguments.")
    }

    private fun loadOrderProducts(id: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _orderProducts.value = emptyList()
        _finishOrderError.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (val orderResult = orderRepository.getOrderById(id)) {
                    is NetworkResult.Success -> handleOrderSuccess(orderResult.data)
                    is NetworkResult.Error -> handleOrderError(id, orderResult.message)
                    is NetworkResult.Exception -> handleOrderException(orderResult.e as Exception)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}. "
                _orderProducts.value = emptyList()
                Log.e("OrderProductsViewModel", "Error inesperado", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun handleOrderSuccess(orderResponse: OrderResponse) {
        val order = OrderMapper.toOrder(orderResponse)
        Log.d("OrderProductsViewModel", "Order mapped successfully: $order")

        val productIds = order.productsInOrder.map { it.id }

        if (productIds.isEmpty()) {
            _orderProducts.value = emptyList()
            _errorMessage.value = "El pedido ${order.id} no contiene productos registrados."
            Log.d(
                "OrderProductsViewModel",
                "El pedido ${order.id} no contiene productos registrados."
            )
            return
        }

        when (val productsResult = productRepository.getProductListById(productIds)) {
            is NetworkResult.Success -> handleProductsSuccess(order, productsResult.data)
            is NetworkResult.Error -> {
                _errorMessage.value =
                    "Error al cargar detalles de productos: ${productsResult.message}. "
                _orderProducts.value = emptyList()
                Log.e(
                    "OrderProductsViewModel",
                    "Error al cargar detalles de productos: ${productsResult.message}"
                )
            }

            is NetworkResult.Exception -> {
                _errorMessage.value =
                    "Excepción al cargar detalles de productos: ${productsResult.e.message}. "
                _orderProducts.value = emptyList()
                Log.e(
                    "OrderProductsViewModel",
                    "Excepción al cargar detalles de productos",
                    productsResult.e
                )
            }
        }
    }

    private fun handleProductsSuccess(order: Order, productsMap: Map<String, ProductResponse>) {
        val productsForDisplay = order.productsInOrder.mapNotNull { productOp ->
            productsMap[productOp.id]?.let { response ->
                Product(
                    id = response.id,
                    name = response.name,
                    description = response.description,
                    ean13 = response.ean13,
                    imageURL = response.imageURL,
                    innerLocation = null,
                    currentStock = productOp.quantity
                )
            }
        }

        _orderProducts.value = productsForDisplay

        if (productsForDisplay.isEmpty()) {
            _errorMessage.value = "No se encontraron productos para esta orden."
            Log.w(
                "OrderProductsViewModel",
                "No se encontraron productos para esta orden: ${order.id}"
            )
        } else {
            _errorMessage.value = null
        }
    }

    private fun handleOrderError(id: String, message: String?) {
        val isNotFound = message?.contains("Not Found", ignoreCase = true) == true ||
                message?.contains("404", ignoreCase = true) == true

        _errorMessage.value = if (isNotFound) {
            "El pedido con ID '$id' no fue encontrado."
        } else {
            "Error al cargar el pedido: ${message ?: "Error desconocido"}. "
        }

        _orderProducts.value = emptyList()
        Log.e("OrderProductsViewModel", "Error loading order: $message")
    }

    private fun handleOrderException(e: Exception) {
        _errorMessage.value = "Excepción al cargar el pedido: ${e.message}. "
        _orderProducts.value = emptyList()
        Log.e("OrderProductsViewModel", "Exception loading order", e)
    }

    fun updateProductQuantity(productId: String, newQuantity: Int) {
        val currentList = _orderProducts.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == productId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(currentStock = newQuantity)
            _orderProducts.value = currentList
            Log.d("OrderProductsViewModel", "Cantidad actualizada para $productId a $newQuantity")
        }
    }

    fun finishOrder() {
        _isFinishingOrder.value = true
        _finishOrderError.value = null
        _orderFinishedSuccessfully.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val orderIdToFinish = orderId.toLongOrNull()
                if (orderIdToFinish == null) {
                    _finishOrderError.value = "ID de orden inválido para finalizar."
                    _isFinishingOrder.value = false
                    return@launch
                }

                val productQuantities = _orderProducts.value.associate { product ->
                    product.id to (product.currentStock ?: 0)
                }

                when (val result =
                    orderRepository.finishOrder(orderIdToFinish, productQuantities)) {
                    is NetworkResult.Success -> {
                        _isFinishingOrder.value = false
                        _orderFinishedSuccessfully.value = true
                        Log.d("OrderProductsViewModel", "Orden ${orderId} finalizada con éxito.")
                    }

                    is NetworkResult.Error -> {
                        _finishOrderError.value =
                            "Error al finalizar el pedido: ${result.message ?: "Desconocido"}"
                        _isFinishingOrder.value = false
                        _orderFinishedSuccessfully.value = false
                        Log.e(
                            "OrderProductsViewModel",
                            "Error al finalizar el pedido: ${result.message}"
                        )
                    }

                    is NetworkResult.Exception -> {
                        _finishOrderError.value =
                            "Excepción al finalizar el pedido: ${result.e.message}"
                        _isFinishingOrder.value = false
                        _orderFinishedSuccessfully.value = false
                        Log.e(
                            "OrderProductsViewModel",
                            "Excepción al finalizar el pedido",
                            result.e
                        )
                    }
                }
            } catch (e: Exception) {
                _finishOrderError.value = "Error inesperado al finalizar la el pedido: ${e.message}"
                _isFinishingOrder.value = false
                _orderFinishedSuccessfully.value = false
                Log.e("OrderProductsViewModel", "Error inesperado al finalizar el pedido", e)
            }
        }
    }

    fun showCompleteOrderConfirmation() {
        _showCompleteOrderConfirmationDialog.value = true
    }
    fun dismissCompleteOrderConfirmation() {
        _showCompleteOrderConfirmationDialog.value = false
    }
}
