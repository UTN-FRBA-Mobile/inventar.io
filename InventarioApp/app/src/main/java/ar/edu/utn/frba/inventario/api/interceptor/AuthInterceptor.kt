package ar.edu.utn.frba.inventario.api.interceptor

import ar.edu.utn.frba.inventario.api.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor (private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = tokenManager.getAccessToken()

        if (originalRequest.url.encodedPath.contains("/auth/login") ||
            originalRequest.url.encodedPath.contains("/auth/refresh-token") ||
            accessToken == null) {
            return chain.proceed(originalRequest)
        }

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(newRequest)
    }
}