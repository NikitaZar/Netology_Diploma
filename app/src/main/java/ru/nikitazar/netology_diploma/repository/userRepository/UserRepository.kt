package ru.nikitazar.netology_diploma.repository.userRepository

import androidx.lifecycle.LiveData
import ru.nikitazar.netology_diploma.dto.User

interface UserRepository {
    val data: LiveData<List<User>>
    suspend fun getById(id: Long): User
    suspend fun getAll()
}