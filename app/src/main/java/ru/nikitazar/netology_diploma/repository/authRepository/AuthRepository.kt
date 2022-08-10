package ru.nikitazar.netology_diploma.repository.authRepository

import ru.nikitazar.netology_diploma.dto.AuthState
import ru.nikitazar.netology_diploma.dto.Media
import ru.nikitazar.netology_diploma.dto.MediaUpload

interface AuthRepository {
    suspend fun updateUser(login: String, pass: String): AuthState
    suspend fun registerUser(login: String, pass: String, name: String): AuthState
    suspend fun registerWithPhoto(login: String, pass: String, name: String, upload: MediaUpload): AuthState
}