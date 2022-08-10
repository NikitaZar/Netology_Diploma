package ru.nikitazar.netology_diploma.hiltModules.eventModules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.nikitazar.netology_diploma.repository.eventRepository.EventRepository
import ru.nikitazar.netology_diploma.repository.eventRepository.EventRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EventRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPostRepository(imp: EventRepositoryImpl): EventRepository
}