package ru.nikitazar.netology_diploma.hiltModules.userModules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.nikitazar.netology_diploma.repository.userRepository.UserRepository
import ru.nikitazar.netology_diploma.repository.userRepository.UserRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(imp: UserRepositoryImpl): UserRepository
}