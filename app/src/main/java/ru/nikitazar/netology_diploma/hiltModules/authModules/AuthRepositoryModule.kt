package ru.nikitazar.netology_diploma.hiltModules.authModules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.nikitazar.netology_diploma.repository.authRepository.AuthRepository
import ru.nikitazar.netology_diploma.repository.authRepository.AuthRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(imp: AuthRepositoryImpl): AuthRepository
}