package ru.nikitazar.netology_diploma.hiltModules.postModules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.nikitazar.netology_diploma.dao.PostDao
import ru.nikitazar.netology_diploma.dao.PostRemoteKeyDao
import ru.nikitazar.netology_diploma.db.AppDb

@InstallIn(SingletonComponent::class)
@Module
object PostDaoModule {
    @Provides
    fun providePostDao(db: AppDb): PostDao = db.postDao()

    @Provides
    fun providePostRemoteKeyDao(db: AppDb): PostRemoteKeyDao = db.postRemoteKeyDao()
}