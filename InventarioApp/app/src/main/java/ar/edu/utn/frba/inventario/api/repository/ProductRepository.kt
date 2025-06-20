package ar.edu.utn.frba.inventario.api.repository

import ar.edu.utn.frba.inventario.api.ApiService
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.product.Product
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val apiService: ApiService
):Repository()  {
    suspend fun getProductList(ean13s : List<String>): NetworkResult<Map<Long,Product>>{
        return safeApiCall {apiService.getProductList(ean13s)}
    }
}