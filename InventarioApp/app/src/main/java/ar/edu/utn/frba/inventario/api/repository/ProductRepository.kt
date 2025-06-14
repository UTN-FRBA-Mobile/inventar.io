package ar.edu.utn.frba.inventario.api.repository

import ar.edu.utn.frba.inventario.api.ApiService
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import ar.edu.utn.frba.inventario.api.model.network.NetworkResult.*
import ar.edu.utn.frba.inventario.api.model.product.ProductResponse
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val apiService: ApiService
) : Repository() {

    suspend fun getProductByEan13(code: String): NetworkResult<ProductResponse?> {
        val result = safeApiCall {
            apiService.getProductsByEan13s(code)
        }

        return when (result) {
            is NetworkResult.Success -> {
                val product = result.data.values.firstOrNull()
                Success(product)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Exception -> result
        }
    }
}
