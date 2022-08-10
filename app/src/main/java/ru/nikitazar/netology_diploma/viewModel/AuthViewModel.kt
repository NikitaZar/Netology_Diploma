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
import ru.nikitazar.netology_diploma.errors.ApiError2
import ru.nikitazar.netology_diploma.model.AuthState
import ru.nikitazar.netology_diploma.model.PhotoModel
import ru.nikitazar.netology_diploma.model.AuthErrorState
import ru.nikitazar.netology_diploma.model.AuthErrorType
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

    private val _errorState = MutableLiveData(AuthErrorState(false, AuthErrorType.NO_ERROR))
    val errorState: LiveData<AuthErrorState>
        get() = _errorState

    fun updateUser(login: String, pass: String) = viewModelScope.launch {
        try {
            _errorState.value = AuthErrorState()
            val authState = repository.updateUser(login, pass)
            auth.setAuth(authState.id, authState.token, login)
        } catch (e: ApiError2) {
            val responseBody = e.responseBody?.string() ?: ""

            if (e.code == 400 && responseBody.contains("Incorrect password")) {
                _errorState.value = AuthErrorState(true, AuthErrorType.PASSWORD)
            }
        } catch (e: Exception) {
            Log.e("updateUser", e.message.toString())
        }
    }

    fun registerUser(login: String, pass: String, name: String) = viewModelScope.launch {
        try {
            _errorState.value = AuthErrorState()
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
        } catch (e: ApiError2) {
            val responseBody = e.responseBody?.string() ?: ""

            if (e.code == 400 && responseBody.contains("User already registered")) {
                _errorState.value = AuthErrorState(true, AuthErrorType.REGISTERED)
            }

        } catch (e: Exception) {
            Log.e("registerUser", e.toString())
        }
    }

    fun changeAvatar(uri: Uri?) = viewModelScope.launch {
        _avatar.value = PhotoModel(uri, uri?.toFile())
    }
}