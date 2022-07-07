package ru.nikitazar.netology_diploma.hiltModules.authModules

import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.nikitazar.netology_diploma.api.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AuthApiServiceModule {
    @Provides
    @Singleton
    fun provideAuthApiService(): AuthApiService{
        return retrofit(okhttp())
            .create(AuthApiService::class.java)
    }
}