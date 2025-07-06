package ar.edu.utn.frba.inventario.api.repository

import ar.edu.utn.frba.inventario.api.ApiService
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.order.OrderResponse
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val apiService: ApiService,
) : Repository() {
    suspend fun getOrderById(orderId: String): NetworkResult<OrderResponse> = safeApiCall { apiService.getOrder(orderId) }

    suspend fun getOrdersList(): NetworkResult<List<OrderResponse>> = safeApiCall { apiService.getOrdersList() }

    suspend fun startOrder(id: Long): NetworkResult<OrderResponse> = safeApiCall { apiService.startOrder(id) }

    suspend fun finishOrder(
        id: Long,
        productQuantities: Map<String, Int>,
    ): NetworkResult<OrderResponse> = safeApiCall { apiService.finishOrder(id, productQuantities) }
}
