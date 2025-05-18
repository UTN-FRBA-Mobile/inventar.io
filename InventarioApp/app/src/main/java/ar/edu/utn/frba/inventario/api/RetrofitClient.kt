package ar.edu.utn.frba.inventario.api

import ar.edu.utn.frba.inventario.api.interceptor.AuthInterceptor
import ar.edu.utn.frba.inventario.api.interceptor.LoggingInterceptor
import ar.edu.utn.frba.inventario.api.utils.TokenManager
import ar.edu.utn.frba.inventario.api.utils.TokenRefreshAuthenticator
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://localhost:8080"

    private val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

    private val tokenManager = TokenManager()
    private val loggingInterceptor = LoggingInterceptor.get()
    private val authInterceptor = AuthInterceptor(tokenManager)
    private val tokenRefreshAuthenticator = TokenRefreshAuthenticator(tokenManager, instance)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(LoggingInterceptor.get())
        .addInterceptor(authInterceptor)
        .authenticator(tokenRefreshAuthenticator)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}