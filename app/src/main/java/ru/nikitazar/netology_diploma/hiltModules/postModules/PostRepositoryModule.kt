package ru.nikitazar.netology_diploma.hiltModules.postModules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.nikitazar.netology_diploma.repository.*
import ru.nikitazar.netology_diploma.repository.postRepository.PostRepository
import ru.nikitazar.netology_diploma.repository.postRepository.PostRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PostRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPostRepository(imp: PostRepositoryImpl): PostRepository
}