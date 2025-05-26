package ar.edu.utn.frba.inventario.config

import ar.edu.utn.frba.inventario.api.ApiService
import ar.edu.utn.frba.inventario.api.repository.AuthRepository
import ar.edu.utn.frba.inventario.api.repository.SelfRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService): AuthRepository {
        return AuthRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideSelfRepository(apiService: ApiService): SelfRepository {
        return SelfRepository(apiService)
    }

}