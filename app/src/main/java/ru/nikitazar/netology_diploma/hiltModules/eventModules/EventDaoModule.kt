package ru.nikitazar.netology_diploma.hiltModules.eventModules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.nikitazar.netology_diploma.dao.EventDao
import ru.nikitazar.netology_diploma.dao.EventRemoteKeyDao
import ru.nikitazar.netology_diploma.dao.PostRemoteKeyDao
import ru.nikitazar.netology_diploma.db.AppDb

@InstallIn(SingletonComponent::class)
@Module
object EventDaoModule {
    @Provides
    fun provideEventDao(db: AppDb): EventDao = db.eventDao()

    @Provides
    fun provideEventRemoteKeyDao(db: AppDb): EventRemoteKeyDao = db.eventRemoteKeyDao()
}