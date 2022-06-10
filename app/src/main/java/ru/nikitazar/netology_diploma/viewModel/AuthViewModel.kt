package ru.nikitazar.netology_diploma.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import ru.nikitazar.netology_diploma.auth.AppAuth
import ru.nikitazar.netology_diploma.model.AuthState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val auth: AppAuth) : ViewModel() {
    val data: LiveData<AuthState> = auth.authStateFlow.asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = auth.authStateFlow.value.id != 0L
}