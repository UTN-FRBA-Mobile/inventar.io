package ar.edu.utn.frba.inventario.api.repository

import ar.edu.utn.frba.inventario.api.ApiService
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.shipment.ShipmentResponse
import javax.inject.Inject

class ShipmentRepository @Inject constructor(
    private val apiService: ApiService):Repository() {
    suspend fun getShipment(idShipment: Long): NetworkResult<ShipmentResponse> {
        return safeApiCall {apiService.getShipment(idShipment)}
    }

    suspend fun getShipmentList(): NetworkResult<List<ShipmentResponse>> {
        return safeApiCall {apiService.getShipmentList()}
    }

    suspend fun startShipment(idShipment: Long): NetworkResult<ShipmentResponse> {
        return safeApiCall {apiService.startShipment(idShipment)}
    }

    suspend fun finishShipment(idShipment: Long): NetworkResult<ShipmentResponse> {
        return safeApiCall {apiService.finishShipment(idShipment)}
    }

    suspend fun blockShipment(idShipment: Long): NetworkResult<ShipmentResponse> {
        return safeApiCall {apiService.blockShipment(idShipment)}
    }
}