package ar.edu.utn.frba.inventario.api.interceptor

import okhttp3.logging.HttpLoggingInterceptor

object LoggingInterceptor {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun get(): HttpLoggingInterceptor {
        return loggingInterceptor
    }
}