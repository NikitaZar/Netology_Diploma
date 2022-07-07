package ru.nikitazar.netology_diploma.hiltModules.eventModules

import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.nikitazar.netology_diploma.api.*
import ru.nikitazar.netology_diploma.auth.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class EventApiServiceModule {
    @Provides
    @Singleton
    fun provideEventApiService(auth: AppAuth): EventApiService{
        return retrofit(okhttp(loggingInterceptor(), authInterceptor(auth)))
            .create(EventApiService::class.java)
    }
}