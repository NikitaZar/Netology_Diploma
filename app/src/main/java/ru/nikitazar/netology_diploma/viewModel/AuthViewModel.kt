package ru.nikitazar.netology_diploma.viewModel

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.nikitazar.netology_diploma.auth.AppAuth
import ru.nikitazar.netology_diploma.dto.MediaUpload
import ru.nikitazar.netology_diploma.model.AuthState
import ru.nikitazar.netology_diploma.model.PhotoModel
import ru.nikitazar.netology_diploma.repository.authRepository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AppAuth,
    private val repository: AuthRepository
    ) : ViewModel() {

    private val noPhoto = PhotoModel()
    private val _avatar = MutableLiveData(noPhoto)

    val avatar: LiveData<PhotoModel>
        get() = _avatar

    val data: LiveData<AuthState> = auth.authStateFlow.asLiveData(Dispatchers.Default)
    val authenticated: LiveData<Boolean>
        get() = MutableLiveData((data.value?.id ?: 0L) != 0L)

    fun updateUser(login: String, pass: String) = viewModelScope.launch {
        try {
            val authState = repository.updateUser(login, pass)
            auth.setAuth(authState.id, authState.token, login)
        } catch (e: Exception) {
            Log.i("updateUser", e.message.toString())
        }
    }

    fun registerUser(login: String, pass: String, name: String) = viewModelScope.launch {
        try {
            when (_avatar.value) {
                noPhoto -> {
                    val authState = repository.registerUser(login, pass, name)
                    auth.setAuth(authState.id, authState.token, login)
                }
                else -> {
                    _avatar.value?.file?.let { file ->
                        val authState = repository.registerWithPhoto(login, pass, name, MediaUpload(file))
                        auth.setAuth(authState.id, authState.token, login)
                    }
                }
            }
        } catch (e: Exception) {
            Log.i("updateUser", e.message.toString())
        }
    }

    fun changeAvatar(uri: Uri?) = viewModelScope.launch {
        _avatar.value = PhotoModel(uri, uri?.toFile())
    }
}