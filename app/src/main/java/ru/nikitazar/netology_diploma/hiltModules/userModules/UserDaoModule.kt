package ru.nikitazar.netology_diploma.hiltModules.userModules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.nikitazar.netology_diploma.dao.UserDao
import ru.nikitazar.netology_diploma.db.AppDb

@InstallIn(SingletonComponent::class)
@Module
object UserDaoModule {
    @Provides
    fun provideUserDao(db: AppDb): UserDao = db.userDao()
}