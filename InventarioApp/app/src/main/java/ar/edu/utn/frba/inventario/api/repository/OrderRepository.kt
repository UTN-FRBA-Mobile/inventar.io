package ar.edu.utn.frba.inventario.api.repository

import ar.edu.utn.frba.inventario.api.ApiService
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.order.OrderResponse
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val apiService: ApiService):Repository() {
    suspend fun getOrder(orderId: Long): NetworkResult<OrderResponse> {
        return safeApiCall {apiService.getOrder(orderId)}
    }

    suspend fun getOrdersList(): NetworkResult<List<OrderResponse>> {
        return safeApiCall {apiService.getOrdersList()}
    }
}

