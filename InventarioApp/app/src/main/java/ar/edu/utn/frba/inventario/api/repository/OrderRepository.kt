package ar.edu.utn.frba.inventario.api.repository

import ar.edu.utn.frba.inventario.api.ApiService
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.order.OrderResponse
import ar.edu.utn.frba.inventario.api.model.shipment.ShipmentResponse
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val apiService: ApiService):Repository() {
    suspend fun getOrderById(orderId: String): NetworkResult<OrderResponse> {
        return safeApiCall {apiService.getOrder(orderId)}
    }

    suspend fun getOrdersList(): NetworkResult<List<OrderResponse>> {
        return safeApiCall {apiService.getOrdersList()}
    }

    suspend fun startOrder(id: Long): NetworkResult<OrderResponse> {
        return safeApiCall {apiService.startOrder(id)}
    }

    suspend fun finishOrder(id: Long, productQuantities: Map<String, Int>): NetworkResult<OrderResponse> {
        return safeApiCall { apiService.finishOrder(id, productQuantities) }
    }
}

