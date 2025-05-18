package ar.edu.utn.frba.inventario.api.repository

import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

abstract class Repository {
    suspend fun <T : Any> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        NetworkResult.Success(body)
                    } ?: NetworkResult.Error(response.code(), "Cuerpo de respuesta vacío o nulo")
                } else {
                    val errorBody = response.errorBody()?.string()
                    NetworkResult.Error(response.code(), errorBody ?: response.message())
                }
            } catch (e: Exception) {
                NetworkResult.Exception(e)
            }
        }
    }
}