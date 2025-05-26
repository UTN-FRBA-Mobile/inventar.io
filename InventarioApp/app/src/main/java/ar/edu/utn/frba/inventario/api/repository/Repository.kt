package ar.edu.utn.frba.inventario.api.repository

import ar.edu.utn.frba.inventario.api.model.network.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

abstract class Repository {
    suspend fun <T : Any?> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            doCall(apiCall)
        }
    }

    // Sólo lo utilizamos para REFRESH - porque corre sobre un dispatchers IO - así que
    // si bloqueamos temporalmente ese thread no hay problema.
    fun <T : Any?> blockingApiCall(apiCall: () -> Call<T>): NetworkResult<T> {
        return runBlocking {
            doCall { apiCall.invoke().execute() }
        }
    }

    private suspend fun <T : Any?> doCall(apiCall: suspend () -> Response<T>): NetworkResult<T> = try {
        val response = apiCall()
        if (response.isSuccessful) {
            @Suppress("UNCHECKED_CAST")
            NetworkResult.Success(response.body() as T)
        } else {
            val errorBody = response.errorBody()?.string()
            NetworkResult.Error(response.code(), errorBody ?: response.message())
        }
    } catch (e: Exception) {
        NetworkResult.Exception(e)
    }
}

