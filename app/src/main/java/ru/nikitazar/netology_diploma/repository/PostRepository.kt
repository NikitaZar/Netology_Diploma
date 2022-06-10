package ru.nikitazar.netology_diploma.repository

import ru.nikitazar.netology_diploma.dto.AuthState
import ru.nikitazar.netology_diploma.dto.MediaUpload
import ru.nikitazar.netology_diploma.dto.Post

interface PostRepository {
    suspend fun getPostsAll()
    suspend fun getPostById(id: Long): Post
    suspend fun updateUser(login: String, pass: String): AuthState
    suspend fun registerUser(login: String, pass: String, name: String): AuthState
    suspend fun registerWithPhoto(login: String, pass: String, name: String, upload: MediaUpload): AuthState
}