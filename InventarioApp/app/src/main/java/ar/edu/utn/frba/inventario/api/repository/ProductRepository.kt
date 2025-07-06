package ar.edu.utn.frba.inventario.api.repository

import ar.edu.utn.frba.inventario.api.ApiService
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.api.model.product.ProductResponse
import ar.edu.utn.frba.inventario.api.model.product.ProductStockLocationResponse
import javax.inject.Inject

class ProductRepository @Inject constructor(private val apiService: ApiService) : Repository() {

    suspend fun getProductList(ean13s: List<String>): NetworkResult<Map<Long, Product>> =
        safeApiCall {
            apiService.getProductList(ean13s)
        }

    suspend fun getProductListById(id: List<String>): NetworkResult<Map<String, ProductResponse>> =
        safeApiCall {
            apiService.getProductListById(id)
        }

    suspend fun getStockByProductId(id: String): NetworkResult<ProductStockLocationResponse> =
        safeApiCall {
            apiService.getStockByProductId(id)
        }

    suspend fun getStockByProductIdList(
        ids: List<String>,
    ): NetworkResult<ProductStockLocationResponse> = safeApiCall {
        apiService.getStockByProductIdList(ids)
    }
}
