package ar.edu.utn.frba.inventario.api.repository

import ar.edu.utn.frba.inventario.api.ApiService
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.product.ProductResponse
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val apiService: ApiService
):Repository()  {
    suspend fun getProductList(ean13s : List<String>): NetworkResult<Map<Long,ProductResponse>>{
        return safeApiCall {apiService.getProductList(ean13s)}
    }
}