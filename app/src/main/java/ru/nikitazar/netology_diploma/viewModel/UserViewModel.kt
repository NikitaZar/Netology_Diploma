package ru.nikitazar.netology_diploma.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.nikitazar.netology_diploma.dto.User
import ru.nikitazar.netology_diploma.repository.userRepository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {

    val data: LiveData<List<User>> = repository.data

    init {
        loadUsers()
    }

    fun loadUsers() = viewModelScope.launch {
        try {
            repository.getAll()
        } catch (e: Exception) {
            Log.e("loadUsers", e.message.toString())
        }
    }

    fun getUserById(id: Long) = viewModelScope.launch {
        try {
            repository.getById(id)
        } catch (e: Exception) {
            Log.e("getUserById", e.message.toString())
        }
    }
}