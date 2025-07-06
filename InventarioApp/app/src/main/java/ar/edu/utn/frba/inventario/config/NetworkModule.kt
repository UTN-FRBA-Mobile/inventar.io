package ar.edu.utn.frba.inventario.config

import android.content.Context
import ar.edu.utn.frba.inventario.BuildConfig
import ar.edu.utn.frba.inventario.api.ApiService
import ar.edu.utn.frba.inventario.api.interceptor.AuthInterceptor
import ar.edu.utn.frba.inventario.api.interceptor.LoggingInterceptor
import ar.edu.utn.frba.inventario.api.repository.AuthRepository
import ar.edu.utn.frba.inventario.api.utils.PreferencesManager
import ar.edu.utn.frba.inventario.api.utils.TokenManager
import ar.edu.utn.frba.inventario.api.utils.TokenRefreshAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    const val API_BASE_URL: String = BuildConfig.API_BASE_URL

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager = TokenManager(context)

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = LoggingInterceptor.get()

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor = AuthInterceptor(tokenManager)

    @Provides
    @Singleton
    fun provideTokenRefreshAuthenticator(
        tokenManager: TokenManager,
        authRepository: Provider<AuthRepository>,
    ): TokenRefreshAuthenticator = TokenRefreshAuthenticator(tokenManager, authRepository)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenRefreshAuthenticator: TokenRefreshAuthenticator,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .authenticator(tokenRefreshAuthenticator)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        if (API_BASE_URL.isEmpty() || API_BASE_URL == "http://default-url") {
            throw IllegalArgumentException("Please set API_BASE_URL=http://192.168.XXX.XXX:8080 in local.properties (root project folder)")
        } else {
            println("API_BASE_URL: $API_BASE_URL")
        }

        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager = PreferencesManager(context)
}
