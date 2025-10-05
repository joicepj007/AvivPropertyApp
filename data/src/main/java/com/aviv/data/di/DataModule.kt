package com.aviv.data.di

import com.aviv.data.remote.PropertyApi
import com.aviv.data.repository.PropertyRepositoryImpl
import com.aviv.domain.repository.PropertyRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providePropertyApi(retrofit: Retrofit): PropertyApi {
        return retrofit.create(PropertyApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPropertyRepository(
        repositoryImpl: PropertyRepositoryImpl
    ): PropertyRepository
}